package main.java.com.benbr.parser.types

class MessageHeader {

    MessageHeader(MessageType messageType, MessageHeaderType headerType, int localMessageType) {
        this.messageType = messageType
        this.headerType = headerType
        this.localMessageType = localMessageType
    }

    MessageHeader(MessageType messageType, MessageHeaderType headerType, int localMessageType, int reserved, int messageSpecific) {
        this.messageType = messageType
        this.headerType = headerType
        this.localMessageType = localMessageType
        this.reserved = reserved
        this.messageSpecific = messageSpecific
    }

    MessageHeader(MessageType messageType, MessageHeaderType headerType, int localMessageType, int timestampOffset) {
        this.messageType = messageType
        this.headerType = headerType
        this.localMessageType = localMessageType
        this.timestampOffset = timestampOffset
    }


    private MessageType messageType;
    private MessageHeaderType headerType;
    private int localMessageType;
    private int timestampOffset;
    private int reserved;
    private int messageSpecific;

    boolean isCompressedTimestamp() {
        return headerType == MessageHeaderType.COMPRESSED_TIMESTAMP
    }


    int getTimestampOffset() {
        return timestampOffset
    }

    MessageType getMessageType() {
        return messageType
    }

    MessageHeaderType getHeaderType() {
        return headerType
    }

    int getLocalMessageType() {
        return localMessageType
    }

    int getReserved() {
        return reserved
    }

    int getMessageSpecific() {
        return messageSpecific
    }


    @Override
    public String toString() {
        return "MessageHeader{" +
                "type=" + messageType +
                ", headerType=" + headerType +
                ", localMessageType=" + localMessageType +
                '}';
    }
}
