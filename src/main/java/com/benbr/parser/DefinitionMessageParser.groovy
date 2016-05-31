package main.java.com.benbr.parser

import main.java.com.benbr.FITDecodeException
import main.java.com.benbr.parser.types.ArchitectureType
import main.java.com.benbr.Util
import main.java.com.benbr.parser.types.DefinitionMessage
import main.java.com.benbr.parser.types.FieldDefinition
import main.java.com.benbr.parser.types.MessageHeader
import main.java.com.benbr.profile.Constants
import main.java.com.benbr.profile.types.ProfileField

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

        int globalMessageNum = globMessageBytes[1] | globMessageBytes[0] << 8;
        int numFields = headerWithoutFields[4]

        List<FieldDefinition> fields = [];
        List<FieldDefinition> devFields = [];

        if (numFields != 0) {
            (1..numFields).each {
                fields << parseField(inputStream)
            }
        }

        if (header.getReserved() == 1) {
            int numDevFields = inputStream.read()

            if(numDevFields != 0) {
                (1..numDevFields).each {
                    devFields << parseField(inputStream)
                }
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

    static void associateFieldDefinitionWithGlobalProfile(HashMap<String, List<ProfileField>> profile, DefinitionMessage message, int globalDefinitionID) {
        String globalName = Constants.messageIdToName[globalDefinitionID]
        if (!profile.containsKey(globalName)) return;

        List<ProfileField> globalFields = profile[globalName]

        message.getFieldDefinitions().eachWithIndex { FieldDefinition entry, int idx ->
            def globalField = globalFields.find { it.definitionNumber == entry.definitionNumber }
            if (globalField == null) {
                def nullField = new ProfileField(null, "Unknown", null)
                message.addFieldAssociation(idx, nullField)
            } else {
                message.addFieldAssociation(idx, globalField)
            }


        }

    }

}