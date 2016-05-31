package main.java.com.benbr

import main.java.com.benbr.profile.types.EnumerationType
import main.java.com.benbr.profile.Constants

class Util {

    private static int bytesRead = 0;

    static int read(DataInputStream input) {
        int data =  input.read()
        bytesRead += 1;
        return data
    }

    static int[] readUnsignedValues(DataInputStream input, int bytesToRead) {
        int[] unsignedValues = new int[bytesToRead]

        (0..bytesToRead - 1).eachWithIndex{ int entry, int idx ->
            if (input.available() == 0) return;
            unsignedValues[idx] = input.readUnsignedByte()
        }

        bytesRead += unsignedValues.size()
        return unsignedValues
    }

    static void resetBytesRead() {
        bytesRead = 0
    }

    static int getBytesRead() {
        return bytesRead
    }


    static int[] littleToBigEndian(List<Integer> input) {
        int[] out = new int[input.size()]
        input.eachWithIndex{ int it, int idx ->
            out[input.size() - idx - 1] = it
        }

        return out
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
        if (typeName == null) return -2
        if (typeName.contains("64")) return 8;
        if (typeName.contains("32")) return 4;
        if (typeName.contains("16")) return 2;
        if (typeName.contains("8")) return 1;
        if (typeName.contains("enum")) return 1;
        if (typeName.contains("byte")) return 1;
        if (typeName.contains("string")) return -1;         // See definition message
        return 0;
    }

        static boolean typeIsNumber(String typename) {
        int size = baseTypenameToNumBytes(typename)
        return (size > 0) && typename != "enum" && typename != "byte"
    }

    static Integer getBit(List<Integer> bytes, int bitIndex) {
        int byteIdx = bitIndex / 8
        int bitIdx = bitIndex - byteIdx * 8

        def val = bytes[byteIdx]
        if (val == null) {
            return null
        }
        return (val >> (7-bitIdx)) & 1
    }

    static Long readBits(List<Integer> bytes, int offset, int numBits) {
        if (numBits > 32) {
            return null
        }

        long value = 0

        for (int i = 0; i < numBits; i++) {
            int bit = getBit(bytes, offset + i)

            if (bit == 1) {
                value |= 1 << (numBits - i - 1)
            } else {
                value &= ~(1 << (numBits - i - 1))
            }
        }

        return value
    }

}
