package com.benbr.parser

import com.benbr.FITDecodeException
import com.benbr.Util
import com.benbr.parser.types.ArchitectureType
import com.benbr.parser.types.DefinitionMessage
import com.benbr.parser.types.FieldDefinition
import com.benbr.parser.types.MessageHeader
import com.benbr.profile.Constants
import com.benbr.profile.types.Field

class DefinitionMessageParser {

    public DefinitionMessage parse(DataInputStream inputStream, MessageHeader header) {
        int[] headerWithoutFields = Util.readUnsignedValues(inputStream, 5)
        if (headerWithoutFields.size() != 5) {
            throw new FITDecodeException("Unexpected EOF. Expected at least five bytes in message header, recieved ${headerWithoutFields.size()}")
        }

        int reserved = headerWithoutFields[0]
        ArchitectureType architecture = (headerWithoutFields[1] == 0) ? ArchitectureType.LITTLE_ENDIAN : ArchitectureType.BIG_ENDIAN
        int[] globMessageBytes = headerWithoutFields[2..3];
        if (architecture == ArchitectureType.LITTLE_ENDIAN) {
            globMessageBytes = Util.littleToBigEndian(globMessageBytes.toList())
        }

        int globalMessageNum = globMessageBytes[0] | globMessageBytes[1] << 8;
        int numFields = headerWithoutFields[4]

        List<FieldDefinition> fields = [];
        List<FieldDefinition> devFields = [];

        (1..numFields).each {
            fields << parseField(inputStream)
        }

        if (header.getReserved() == 1) {
            int numDevFields = inputStream.read()

            (1..numDevFields).each {
                devFields << parseField(inputStream)
            }
        } else {
            devFields = null
        }

        return new DefinitionMessage(globalMessageNum, architecture, reserved, fields, devFields);
    }

    private static FieldDefinition parseField(DataInputStream inputStream) {
        int[] bytes = Util.readUnsignedValues(inputStream, 3)
        if (bytes.size() != 3) {
            throw new FITDecodeException("Unexpected EOF. Expected 3 bytes for field, recieved ${bytes.size()}")
        }
        return new FieldDefinition(bytes[0], bytes[1], bytes[2])
    }

    public static void associateFieldDefinitionWithGlobalProfile(HashMap<String, List<Field>> profile, DefinitionMessage message, int globalDefinitionID) {
        String globalName = Constants.messageIdToName[globalDefinitionID]
        if (!profile.containsKey(globalName)) return;

        List<Field> globalFields = profile[globalName]

        message.getFieldDefinitions().eachWithIndex{ FieldDefinition entry, int idx ->
            def globalField = globalFields.find{it.definitionNumber == entry.definitionNumber}
            if (globalField != null) {
                message.addFieldAssociation(idx, globalField)
            }
        }

    }



}




















