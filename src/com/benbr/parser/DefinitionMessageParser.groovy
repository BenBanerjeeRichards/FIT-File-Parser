package com.benbr.parser

import com.benbr.FITDecodeException
import com.benbr.Util
import com.benbr.parser.types.ArchitectureType
import com.benbr.parser.types.DefinitionMessage
import com.benbr.parser.types.FieldDefinition
import com.benbr.parser.types.MessageHeader
import com.benbr.profile.Constants
import com.benbr.profile.Field
import com.thoughtworks.qdox.parser.structs.FieldDef

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
            devFields = null;
        }

        return new DefinitionMessage(reserved, architecture, globalMessageNum, fields, devFields)
    }

    private static FieldDefinition parseField(DataInputStream inputStream) {
        int[] bytes = Util.readUnsignedValues(inputStream, 3)
        if (bytes.size() != 3) {
            throw new FITDecodeException("Unexpected EOF. Expected 3 bytes for field, recieved ${bytes.size()}")
        }
        return new FieldDefinition(bytes[0], bytes[1], bytes[2])
    }

    // Note that this mutates the fields
    public List<FieldDefinition> associateFieldWithName(HashMap<String, List<Field>> profile, int globalDefinitionNumber, List<FieldDefinition> fields) {
        // Look up global definition number
        String globalName = Constants.messageIdToName[globalDefinitionNumber]
        boolean globalKnown = profile.containsKey(globalName)

        fields.each {field ->
            if (!globalKnown) {
                field.setName("UNKNOWN")
                return
            }

            List<Field> profileFields = profile[globalName]
            def profileField = profileFields.find{
                it.getDefinitionNumber() == field.getDefinitionNumber()
            }

            if (profileField == null) {
                field.setName("UNKNOWN")
            } else {
                field.setName(profileField.getName())
            }

        }

        return fields;
    }



}




















