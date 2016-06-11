package main.java.com.benbr.parser

import main.java.com.benbr.FITDecodeException
import main.java.com.benbr.Type
import main.java.com.benbr.Util
import main.java.com.benbr.parser.types.ArchitectureType
import main.java.com.benbr.parser.types.DefinitionMessage
import main.java.com.benbr.parser.types.FieldDefinition
import main.java.com.benbr.parser.types.MessageHeader
import main.java.com.benbr.profile.Constants
import main.java.com.benbr.profile.types.EnumerationType
import main.java.com.benbr.profile.types.ProfileField

class DataMessageParser {

    private HashMap<String, EnumerationType> types;

    DataMessageParser() {
    }

    public DataMessage parse(DataInputStream inputStream, MessageHeader header, HashMap<Integer, DefinitionMessage> localDefinitions, HashMap<String, Object> accumulatedFields, long referenceTimestamp) {
        def localDefinition = localDefinitions[header.getLocalMessageType()]
        if (!localDefinition) {
            throw new FITDecodeException("Data message type ${header.getLocalMessageType()} not defined in local scope")
        }
        DataMessage message = new DataMessage()
        message.type = Constants.messageIdToName[localDefinition.getGlobalMessageNumber()]
        localDefinition.getFieldDefinitions().eachWithIndex { fieldDefinition, idx ->
            def globalField = localDefinition.getGlobalFields()[idx]
            int[] unsignedBytes = Util.readUnsignedValues(inputStream, fieldDefinition.getSize())
            List<Integer> bytes = resolveEndiness(unsignedBytes.toList(), localDefinition)

            def fieldName = globalField?.getName()
            if (globalField?.isArray()) {
                // TODO finish this and move to another function
                message.fields[fieldName] = getComponents(bytes, globalField, accumulatedFields)
                message.unitSymbols[fieldName] = new HashMap<String, String>()
                message.fieldIsArray[fieldName] = true

                ((Map<String, Object>) message.fields[fieldName]).each { subField ->
                    def globalSubField = globalField.getComponents().find { it == subField.getKey() }
                }

            } else {
                fieldName = (fieldName == null) ? generateUniqueUnknownKey(message) : globalField.getName()
                message.fields[fieldName] = getFieldValue(bytes.toList(), localDefinition, fieldDefinition, globalField)
            }

            message.unitSymbols[fieldName] = globalField?.getUnit()
        }

        if (header.isCompressedTimestamp()) {
            message.fields["timestamp"] = decompressTimestamp(header.getTimestampOffset(), referenceTimestamp)
        }

        resolveDynamicFields(message, localDefinition)

        return message;
    }

    private long decompressTimestamp(long timestampOffset, long previousTimestamp) {
        if (timestampOffset >= (previousTimestamp & 0x0000001F)) {
            return (previousTimestamp & 0xFFFFFFE0) + timestampOffset
        }
        return (previousTimestamp & 0xFFFFFFE0) + timestampOffset + 0x20
    }

    private Map<String, Object> getComponents(List<Integer> bytes, ProfileField globalField, Map<String, Object> accumulatedFields) {
        def components = [:]
        int currentBitPosition = 0

        globalField.getComponents().reverse().eachWithIndex { component, idx ->
            int index = globalField.getComponents().size() - idx - 1
            int bits = globalField.getComponentBits()[index]
            double value = (Double) TypeEncoder.applyScaleAndOffset(Util.readBits(bytes, currentBitPosition, bits), globalField, index)
            currentBitPosition += bits

            if (globalField.getAccumulate()[index]) {
                components[component] = getAccumulatedFieldValue(value, accumulatedFields, globalField, component, bits, index)
            } else {
                components[component] = value
            }
        }

        return components

    }

    private double getAccumulatedFieldValue(double value, Map<String, Object> accumulatedFields, ProfileField globalField, String component, int bits, int globalFieldIndex) {
        Double[] prev = accumulatedFields?.get(globalField.getName())?.get(component)
        double fieldValue = 0

        if (prev != null) {
            double difference = value - prev[0]
            if (difference >= 0) {
                fieldValue = prev[1] + difference
            } else if (difference < 0) {
                double rolloverIncrement = (Double) TypeEncoder.applyScaleAndOffset(Math.pow(2, bits), globalField, globalFieldIndex)
                int rolloverMultiplier = 1 + (prev[1] / rolloverIncrement)
                fieldValue = value + rolloverIncrement * rolloverMultiplier
            }
        }

        if (accumulatedFields[globalField.getName()] == null) {
            accumulatedFields[globalField.getName()] = new HashMap()
        }
        accumulatedFields[globalField.getName()][component] = [value, (fieldValue == null) ? 0 : fieldValue]
        return fieldValue
    }

    private Object getFieldValue(List<Integer> valueBytes, DefinitionMessage definitionMessage, FieldDefinition fieldDefinition, ProfileField globalDefinition) {
        Object value = TypeEncoder.encode(valueBytes.toList(), fieldDefinition.getType())

        if (globalDefinition?.getType() != "string") {
            value = TypeEncoder.applyScaleAndOffset(value, globalDefinition)
        }

        return value
    }

    private List<Integer> resolveEndiness(List<Integer> valueBytes, DefinitionMessage definitionMessage) {
        if (definitionMessage.getArchitectureType() == ArchitectureType.LITTLE_ENDIAN) {
            valueBytes = Util.littleToBigEndian(valueBytes.toList())
        }

        return valueBytes
    }

    /**
     * Generates unknown field names in the form unknown_N, where N is an integer.
     * For example, unknown_1, unknown_2...
     * @param message
     */
    private static String generateUniqueUnknownKey(DataMessage message) {
        // TODO store the largest value of N instead of recalculating it every time.
        List postfixes = []

        message.fields.each { name, value ->
            name = (String) name;
            def parts = name.split("unknown")

            if (parts.size() == 2) {
                postfixes << parts[1].replace("_", "").toInteger()
            }
        }

        int postfix = (postfixes.max() == null) ? 1 : postfixes.max() + 1;

        return "unknown_${postfix}"
    }

    private void resolveDynamicFields(DataMessage message, DefinitionMessage localDefinition) {
        localDefinition.getFieldDefinitions().eachWithIndex { fieldDefinition, idx ->
            ProfileField globalField = localDefinition.getGlobalFields()[idx]

            if (globalField == null) {
                // TODO log warning

                // Can not resolve dynamic field as the field can not be found in profile
                return
            }

            if (!globalField.isDynamicField()) return;

            ProfileField newDynamicDefinition = getFieldDefinition(message, globalField)
            String previousValue = message.fields.remove(globalField.getName())
            message.fields[newDynamicDefinition.getName()] = previousValue
        }
    }

    private ProfileField getFieldDefinition(DataMessage message, ProfileField globalField) {
        if (!globalField.isDynamicField()) return globalField;

        for (def subfield : globalField.getSubFields()) {
            // TODO figure out why .eachWithIndex does not work here
            for (int i = 0; i < subfield.getReferenceFieldName().size(); i++) {
                def referenceName = subfield.getReferenceFieldName()[i]
                def referenceValue = subfield.getReferenceFieldValue()[i]

                if (referenceFieldContainsValue(message, referenceName, referenceValue)) {
                    return subfield;
                }
            }
        }

        return globalField
    }

    private boolean referenceFieldContainsValue(DataMessage message, String referenceName, String referenceValue) {
        // Look up reference field
        def referenceField = message.fields.find { it.key == referenceName }
        if (referenceField == null) {
            // TODO log warning
            return false;
        }

        String referenceFieldType = referenceField.key
        int parentFieldValue = (int) referenceField.value

        def referenceFieldTypeMatches = Type.types.find { it.key == referenceFieldType }
        if (referenceFieldTypeMatches == null) {
            throw new FITDecodeException("Reference field type $referenceFieldType not found in profile types")
        }

        HashMap<Integer, String> referenceTypeEnum = referenceFieldTypeMatches.value.enumeration

        // Look up value in types
        def enumType = referenceTypeEnum.find { it.value == referenceValue }
        if (enumType == null) return false;
        return (enumType.key == parentFieldValue)
    }


}
