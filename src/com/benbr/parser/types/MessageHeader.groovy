package com.benbr.parser.types

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

    private MessageType messageType;
    private MessageHeaderType headerType;
    private int localMessageType;

    private int reserved;
    private int messageSpecific;

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
}
