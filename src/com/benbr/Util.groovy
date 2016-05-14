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

}
