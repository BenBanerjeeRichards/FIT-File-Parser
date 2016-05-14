package com.benbr.parser

import com.benbr.Util

class HeaderParser {

    public Header parseHeader(DataInputStream byteStream) {
        int headerSize = byteStream.read()
        int[] bytes = Util.readUnsignedValues(byteStream, headerSize - 1)

        int protocolVersion = bytes[0]
        int profileVersion = (bytes[2] << 8 | bytes[1])
        int dataSize = bytes[6] << 24 | bytes[5] << 16 | bytes[4] << 8 | bytes[3]
        char[] dataType = new char[4]
        dataType[0] = bytes[7]
        dataType[1] = bytes[8]
        dataType[2] = bytes[9]
        dataType[3] = bytes[10]
        int crc = bytes[12] << 8 | bytes[11]

        return new Header(headerSize, protocolVersion, profileVersion, dataSize, dataType, crc)
    }

}
