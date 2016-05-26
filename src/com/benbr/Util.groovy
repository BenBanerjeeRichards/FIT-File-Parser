package com.benbr

import com.benbr.profile.Constants
import com.benbr.profile.types.EnumerationType

class Util {

    static int[] readUnsignedValues(DataInputStream input, int bytesToRead) {
        int[] unsignedValues = new int[bytesToRead]

        (0..bytesToRead - 1).eachWithIndex{ int entry, int idx ->
            if (input.available() == 0) return;
            unsignedValues[idx] = input.readUnsignedByte()
        }

        return unsignedValues
    }

    static int[] littleToBigEndian(List<Integer> input) {
        int[] out = new int[input.size()]
        input.eachWithIndex{ int it, int idx ->
            out[input.size() - idx - 1] = it
        }

        return out
    }

    static long combine4ByteBigEndian(int[] input) {
        if (input.size() != 4) return 0;
        return input[0] << 24L | input[1] << 16L | input[2] << 8L | input[3]

    }


    static int typeNameToNumBytes(HashMap<String, EnumerationType> profile, String typeName) {
        // Is it a base type
        boolean isBaseType = Constants.baseTypes.find {it.value == typeName} != null

        if (isBaseType) {
            return baseTypenameToNumBytes(typeName)
        } else {
            return profileTypeNameToNumBytes(profile, typeName)
        }

    }

    static int profileTypeNameToNumBytes(HashMap<String, EnumerationType> profile, String typeName) {
        def type = profile.find {it.key == typeName}
        if (type == null) {
            throw new FITDecodeException("Could not find type named ${typeName} in profile")
        }

        return baseTypenameToNumBytes((String)Constants.baseTypes[type.value.baseType]);
    }

    static int baseTypenameToNumBytes(String typeName) {
        if (typeName.contains("64")) return 8;
        if (typeName.contains("32")) return 4;
        if (typeName.contains("16")) return 2;
        if (typeName.contains("8")) return 1;
        if (typeName.contains("enum")) return 1;
        if (typeName.contains("byte")) return 1;
        if (typeName.contains("string")) return -1;         // See definition message
        return 0;
    }

}
