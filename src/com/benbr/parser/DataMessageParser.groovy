package com.benbr.parser

import com.benbr.FITDecodeException
import com.benbr.Util
import com.benbr.parser.types.ArchitectureType
import com.benbr.parser.types.DefinitionMessage
import com.benbr.parser.types.MessageHeader
import com.benbr.parser.types.MessageHeaderType
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

    // TODO check bound global profile fields to ensure that they are correct
    public DataMessage parse(DataInputStream inputStream, MessageHeader header, HashMap<Integer, DefinitionMessage> localDefinitions) {
        if (header.headerType == MessageHeaderType.COMPRESSED_TIMESTAMP) {
            throw new NotImplementedException()     // Coming soon
        }

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
            globalField = getFieldDefinition(message, localDefinition, globalField)

            int[] bytes = Util.readUnsignedValues(inputStream, fieldDefinition.getSize())

            if (localDefinition.getArchitectureType() == ArchitectureType.LITTLE_ENDIAN) {
                bytes = Util.littleToBigEndian(bytes.toList())
            }

            // TODO support strings
            long value = Util.combineBigEndian(bytes.toList())
            println "${globalField.getName()} : ${value} : ${fieldDefinition.getSize()}"
            message.fields[globalField.getName()] = value

        }

        return null;
    }

    private boolean referenceFieldContainsValue(DataMessage message, String referenceName, String referenceValue) {
        // Look up reference field
        def referenceField = message.fields.find { it.key == referenceName }
        if (referenceField == null) {
            throw new FITDecodeException("No reference field with name $referenceName found in DataMessage")
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

    private ProfileField getFieldDefinition(DataMessage message, DefinitionMessage localDefinition, ProfileField globalField) {
        if (globalField == null) {
            println ""
        }

        if (!globalField.isDynamicField()) return globalField;

        for (def subfield : globalField.getSubFields()) {

            // TODO figure out why .eachWithIndex does not work here
            int referenceIndex = 0;
            for (def referenceName : subfield.getReferenceFieldName()) {
                def referenceValue = subfield.getReferenceFieldValue()[referenceIndex]

                if (referenceFieldContainsValue(message, referenceName, referenceValue)) {
                    return subfield;
                }

                referenceIndex++;
            }
        }

        return globalField
    }


}
