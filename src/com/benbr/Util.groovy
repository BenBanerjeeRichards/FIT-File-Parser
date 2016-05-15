package com.benbr

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream

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

    static long combineBigEndian(List<Integer> input) {
        long output = 0L;
        input.eachWithIndex { it, idx ->
            long shift = (8L * (input.size().toLong() - idx.toLong() - 1L)).toLong()
            output |= (it.toLong() << shift).toLong()
        }

        return output;
    }

}
