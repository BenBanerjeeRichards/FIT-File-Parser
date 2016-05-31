package main.java.com.benbr.parser.types

class FileHeader {
    FileHeader(int size, int protocolVersion, int profileVersion, int dataSize, char[] dataType, Integer crc) {
        this.size = size
        this.protocolVersion = protocolVersion
        this.profileVersion = profileVersion
        this.dataSize = dataSize
        this.dataType = dataType
        this.crc = crc
    }

    boolean isValid() {
        if (size != 12 && size != 14) return false
        if (dataType[0] != (char)0x2E) return false
        if (dataType[1] != (char)0x46) return false
        if (dataType[2] != (char)0x49) return false
        if (dataType[3] != (char)0x54) return false
        return true
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

    // uint16, nullable (12 byte headers)
    private Integer crc;


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

    Integer getCrc() {
        return crc
    }
}
