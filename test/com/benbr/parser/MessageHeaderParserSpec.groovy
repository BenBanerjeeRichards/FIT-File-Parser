package com.benbr.parser

import com.benbr.parser.types.MessageHeaderType
import com.benbr.parser.types.MessageType
import spock.lang.Specification


class MessageHeaderParserSpec extends Specification {

    def "Given some headers, they are parsed correctly"() {
        expect:
        def messageHeader = new MessageHeaderParser().parse(header)
        messageHeader.messageType == messageType
        messageHeader.reserved == reserved
        messageHeader.headerType == headerType
        messageHeader.localMessageType == localMessageType
        messageHeader.messageSpecific == messageSpecific

        where:
        header  | reserved   | localMessageType | messageSpecific   | messageType           | headerType
        0x00    |   0        |  0               |   0               | MessageType.DATA      | MessageHeaderType.NORMAL
        0xFF    |   1        |  15              |   1               | MessageType.DEFINITION| MessageHeaderType.COMPRESSED_TIMESTAMP
        0x53    |   1        |  3               |   0               | MessageType.DEFINITION| MessageHeaderType.NORMAL
        0xAB    |   0        |  11              |   1               | MessageType.DATA      | MessageHeaderType.COMPRESSED_TIMESTAMP
    }

}
