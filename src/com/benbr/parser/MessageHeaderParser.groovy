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
    private int compressedTimestampMask = 0x0F;

    public MessageHeader parse(int header) {
        MessageHeaderType headerType = ((header & headerTypeMask) == headerTypeMask) ? MessageHeaderType.COMPRESSED_TIMESTAMP : MessageHeaderType.NORMAL
        if (headerType == MessageHeaderType.NORMAL) {
            return parseNormalHeader(header)
        } else {
            return parseCompressedTimestampHeader(header)
        }
    }

    MessageHeader parseNormalHeader(int header) {
        MessageType messageType = ((header & messageTypeMask) == messageTypeMask) ? MessageType.DEFINITION : MessageType.DATA
        int messageTypeSpecific = ((header & messageSpecificMask) == messageSpecificMask) ? 1 : 0;
        int reserved = ((header & reservedMask) == reservedMask) ? 1 : 0;
        int localMessageType = (header & localMessageTypeMask)

        return new MessageHeader(messageType, MessageHeaderType.NORMAL, localMessageType, reserved, messageTypeSpecific)
    }

    MessageHeader parseCompressedTimestampHeader(int header) {
        localMessageTypeMask = 0x60
        int localMessageType = (header & localMessageTypeMask) >> 5
        int compressedTimeStamp = header & compressedTimestampMask

        return new MessageHeader(MessageType.DATA, MessageHeaderType.COMPRESSED_TIMESTAMP, localMessageType, compressedTimeStamp)
    }


}
