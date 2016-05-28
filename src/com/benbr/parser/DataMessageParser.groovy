package com.benbr.parser

import com.benbr.FITDecodeException
import com.benbr.Util
import com.benbr.parser.types.ArchitectureType
import com.benbr.parser.types.DefinitionMessage
import com.benbr.parser.types.FieldDefinition
import com.benbr.parser.types.MessageHeader
import com.benbr.profile.types.EnumerationType
import com.benbr.profile.types.ProfileField

class DataMessageParser {

    private HashMap<String, List<ProfileField>> globalProfile;
    private HashMap<String, EnumerationType> types;

    DataMessageParser(HashMap<String, List<ProfileField>> globalProfile, HashMap<String, EnumerationType> types) {
        this.globalProfile = globalProfile
        this.types = types
    }

    public DataMessage parse(DataInputStream inputStream, MessageHeader header, HashMap<Integer, DefinitionMessage> localDefinitions) {
        def localDefinition = localDefinitions[header.getLocalMessageType()]
        if (!localDefinition) {
            throw new FITDecodeException("Data message type ${header.getLocalMessageType()} not defined in local scope")

        }
        DataMessage message = new DataMessage()

        localDefinition.getFieldDefinitions().eachWithIndex { fieldDefinition, idx ->
            def globalField = localDefinition.getGlobalFields()[idx]
            int[] unsignedBytes = Util.readUnsignedValues(inputStream, fieldDefinition.getSize())
            List<Integer> bytes = resolveEndiness(unsignedBytes.toList(), localDefinition)

            if (globalField?.isArray()) {
                message.fields[globalField.getName()] = getComponents(bytes, globalField)
            } else {
                String fieldName = (globalField == null) ? generateUniqueUnknownKey(message) : globalField.getName()
                message.fields[fieldName] = getFieldValue(bytes.toList(), localDefinition, fieldDefinition, globalField)
            }
        }

        resolveDynamicFields(message, localDefinition)

        return message;
    }

    private Map getComponents(List<Integer> bytes, ProfileField globalField) {
        def components = [:]
        int currentBitPosition = 0

        globalField.getComponents().reverse().eachWithIndex{ component, index ->
            int bits = globalField.getComponentBits()[index]
            long value = Util.readBits(bytes, currentBitPosition, bits)
            currentBitPosition += bits

            components[component] = TypeEncoder.applyScaleAndOffset(value, globalField, globalField.getComponents().size() - index - 1)
        }

        return components

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
        List postfixes = []

        message.fields.each {name, value ->
            name = (String)name;
            def parts = name.split("unknown")

            if (parts.size() == 2) {
                postfixes << parts[1].replace("_", "").toInteger()
            }
        }

        int postfix = (postfixes.max() == null) ? 1 : postfixes.max() + 1;

        return "unknown_${postfix}"
    }

    private void resolveDynamicFields(DataMessage message, DefinitionMessage localDefinition) {
        localDefinition.getFieldDefinitions().eachWithIndex {fieldDefinition, idx ->
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

        def referenceFieldTypeMatches = types.find { it.key == referenceFieldType }
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
