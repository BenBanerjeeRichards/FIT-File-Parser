package com.benbr.parser

import com.benbr.parser.types.MessageHeader
import com.benbr.parser.types.MessageHeaderType
import com.benbr.parser.types.MessageType

class MessageHeaderParser {

    private int headerTypeMask = 0x80
    private int messageTypeMask = 0x40
    private int messageSpecificMask = 0x20
    private int reservedMask = 0x10
    private int localMessageTypeMask = 0x000F

    public MessageHeader parse(int header) {
        MessageHeaderType headerType = ((header & headerTypeMask) == headerTypeMask) ? MessageHeaderType.COMPRESSED_TIMESTAMP : MessageHeaderType.NORMAL
        MessageType messageType = ((header & messageTypeMask) == messageTypeMask) ? MessageType.DEFINITION : MessageType.DATA
        int messageTypeSpecific = ((header & messageSpecificMask) == messageSpecificMask) ? 1 : 0;
        int reserved = ((header & reservedMask) == reservedMask) ? 1 : 0;
        int localMessageType = (header & localMessageTypeMask)

        return new MessageHeader(messageType, headerType, localMessageType, reserved, messageTypeSpecific)
    }

}
