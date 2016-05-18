package com.benbr.parser

import com.benbr.FITDecodeException
import com.benbr.Util
import com.benbr.parser.types.ArchitectureType
import com.benbr.parser.types.DefinitionMessage
import com.benbr.parser.types.MessageHeaderType
import com.benbr.parser.types.MessageHeader
import com.benbr.profile.types.EnumerationType
import com.benbr.profile.types.ProfileField
import sun.reflect.generics.reflectiveObjects.NotImplementedException


class DataMessageParser {

    private HashMap<String, List<ProfileField>> globalProfile;
    private HashMap<String, EnumerationType> types;

    DataMessageParser(HashMap<String, List<ProfileField>> globalProfile, HashMap<String, EnumerationType> types) {
        this.globalProfile = globalProfile
        this.types = types
    }

    public DataMessage parse(DataInputStream inputStream, MessageHeader header, HashMap<Integer, DefinitionMessage> localDefinitions) {
        def localDefinitionMatches = localDefinitions.find {
            it.key == header.localMessageType
        }

        if (localDefinitionMatches == null) {
            throw new FITDecodeException("Data message type ${header.localMessageType} not defined in local scope")
        }

        DataMessage message = new DataMessage()

        def localDefinition = localDefinitionMatches.value
        localDefinition.getFieldDefinitions().eachWithIndex { fieldDefinition, idx ->
            def globalField = localDefinition.getGlobalFields()[idx]
            int[] bytes = Util.readUnsignedValues(inputStream, fieldDefinition.getSize())

            if (localDefinition.getArchitectureType() == ArchitectureType.LITTLE_ENDIAN) {
                bytes = Util.littleToBigEndian(bytes.toList())
            }

            // TODO support strings
            long value = Util.combineBigEndian(bytes.toList())

            if (globalField == null) {
                // TODO log warning
                message.fields[generateUniqueUnknownKey(message)] = value
            } else {
                message.fields[globalField.getName()] = value
                if (globalField.isArray) {
                    println "FOUND ARRAY ${globalField.getName()}"
                }
            }
        }

        resolveDynamicFields(message, localDefinition)

        return message;
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
