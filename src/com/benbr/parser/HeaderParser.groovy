package com.benbr.parser

class HeaderParser {

    public Header parseHeader(List<Integer> bytes) {
        int headerSize = bytes[0]
        int protocolVersion = bytes[1]
        int profileVersion = (bytes[3] << 8 | bytes[2])
        int dataSize = bytes[7] << 24 | bytes[6] << 16 | bytes[5] << 8 | bytes[4]
        char[] dataType = new char[4]
        dataType[0] = bytes[8]
        dataType[1] = bytes[9]
        dataType[2] = bytes[10]
        dataType[3] = bytes[11]
        int crc = bytes[13] << 8 | bytes[12]

        return new Header(headerSize, protocolVersion, profileVersion, dataSize, dataType, crc)
    }

}
