package main.java.com.benbr.parser

import main.java.com.benbr.profile.Constants
import main.java.com.benbr.profile.types.ProfileField

class TypeEncoder<T> {

    static T encode(List<Integer> input, int type) {
        String typeString = Constants.baseTypes[type]

        switch (typeString) {
            case "sint8":
                return toS8(input)
            case "uint8" || "uint8z" || "byte":
                return toU8(input)
            case "sint16":
                return toS16(input)
            case "uint16" || "uint16z":
                return toU16(input)
            case "sint32":
                return toS32(input)
            case "uint32" || "uint32z" || "enum":
                return toU32(input)
            case "float32":
                return toF32(input)
            case "float64":
                return toF64(input)
            case "string":
                return toString(input)
            default:
                // TODO log error
                return toU32(input)
        }
    }


    static T applyScaleAndOffset(Number value, ProfileField definition, int componentIndex = 0) {
        if (definition == null) {
            return value
        }

        float scale = definition.getScale()[componentIndex] == null ? 1 : definition.getScale()[componentIndex]
        if (scale == 1f) {
            return value - (int) definition.getOffset()
        }
        return (value / scale) - definition.getOffset()
    }

    /*
     * These functions make use of implicit type casting to convert the types.
     * It would be nice to have unsigned types here.
     */

    static byte toS8(List<Integer> bigEndian) {
        if (bigEndian.size() != 1) {
            // TODO handle error
        }

        return bigEndian[0]
    }

    static int toU8(List<Integer> bigEndian) {
        return bigEndian[0]
    }

    static short toS16(List<Integer> bigEndian) {
        return combineBigEndian(bigEndian)
    }

    static int toU16(List<Integer> bigEndian) {
        return combineBigEndian(bigEndian)
    }


    static long toU32(List<Integer> bigEndian) {
        return combineBigEndian(bigEndian)
    }

    static int toS32(List<Integer> bigEndian) {
        return combineBigEndian(bigEndian)
    }

    static float toF32(List<Integer> bigEndian) {
        long integerRepresentation = combineBigEndian(bigEndian)
        return integerRepresentation.toFloat()
    }

    static double toF64(List<Integer> bigEndian) {
        long integerRepresentation = combineBigEndian(bigEndian)
        return integerRepresentation.toFloat()
    }

    static String toString(List<Integer> bigEndian) {
        StringBuilder sb = new StringBuilder(bigEndian.size())
        bigEndian.reverse().each {
            sb.appendCodePoint(it)
        }

        return sb.toString().trim()
    }

    static long combineBigEndian(List<Integer> input) {
        long output = 0L;
        input.eachWithIndex { it, idx ->
            long shift = (8L * (input.size().toLong() - idx.toLong() - 1L)).toLong()
            output |= (it.toLong() << shift).toLong()
        }

        return output;
    }
}
