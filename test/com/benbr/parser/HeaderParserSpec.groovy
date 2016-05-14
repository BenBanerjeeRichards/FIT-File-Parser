package com.benbr.parser

import spock.lang.Specification

class HeaderParserSpec extends Specification {

    def "Given a valid header, it is parsed correctly"() {
        given:
        def bytes = [0x0E, 0x10, 0x7D, 0x06, 0x0B, 0x98, 0x00, 0x00, 0x2E, 0x46, 0x49, 0x54, 0x00, 0x00] as byte[]
        def headerByteStream = new DataInputStream(new ByteArrayInputStream(bytes))
        def header = new FileHeaderParser().parseHeader(headerByteStream)

        expect:
        header.size == 14
        header.protocolVersion == 16
        header.profileVersion == 1661
        header.dataSize == 38923
        header.crc == 0x00
        header.dataType[0] == (char)0x2E
        header.dataType[1] == (char)0x46
        header.dataType[2] == (char)0x49
        header.dataType[3] == (char)0x54
        header.isValid()
    }

    def "Given an invalid header, is it marked as invalid"() {
        expect:
        new FileHeaderParser().parseHeader(new DataInputStream(new ByteArrayInputStream(headerBytes))).isValid() == res

        where:
        headerBytes                                                                                               | res
        [0x0F, 0x10, 0x7D, 0x06, 0x0B, 0x98, 0x00, 0x00, 0x2E, 0x46, 0x49, 0x54, 0x00, 0x00] as byte[]            | false
        [0x0E, 0x10, 0x7D, 0x06, 0x0B, 0x98, 0x00, 0x00, 0x2F, 0x46, 0x49, 0x54, 0x00, 0x00] as byte[]            | false
        [0x0E, 0x10, 0x7D, 0x06, 0x0B, 0x98, 0x00, 0x00, 0x2E, 0x47, 0x49, 0x54, 0x00, 0x00] as byte[]            | false
        [0x0E, 0x10, 0x7D, 0x06, 0x0B, 0x98, 0x00, 0x00, 0x2E, 0x46, 0xAA, 0x54, 0x00, 0x00] as byte[]            | false
        [0x0E, 0x10, 0x7D, 0x06, 0x0B, 0x98, 0x00, 0x00, 0x2E, 0x46, 0x49, 0x50, 0x00, 0x00] as byte[]            | false
    }

}
