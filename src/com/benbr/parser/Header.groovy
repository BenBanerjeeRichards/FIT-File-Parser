package com.benbr.parser

class Header {
    Header(int size, int protocolVersion, int profileVersion, int dataSize, char[] dataType, int crc) {
        this.size = size
        this.protocolVersion = protocolVersion
        this.profileVersion = profileVersion
        this.dataSize = dataSize
        this.dataType = dataType
        this.crc = crc
    }

    // uint8. Either 12 or 14 bytes
    private int size;

    // uint8
    private int protocolVersion;

    // uint16
    private int profileVersion;

    // uint32. Length of all the data records excluding headers and CRCs.
    private int dataSize;

    // 4 byte string. Should be '.FIT'.
    private char[] dataType;

    // uint16.
    private int crc;


    int getSize() {
        return size
    }

    int getProtocolVersion() {
        return protocolVersion
    }

    int getProfileVersion() {
        return profileVersion
    }

    int getDataSize() {
        return dataSize
    }

    char[] getDataType() {
        return dataType
    }

    int getCrc() {
        return crc
    }
}
