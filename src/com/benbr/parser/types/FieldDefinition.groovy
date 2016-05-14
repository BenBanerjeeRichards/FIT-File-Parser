package com.benbr.parser.types

class FieldDefinition {

    FieldDefinition(int definitionNumber, int size, int type) {
        this.definitionNumber = definitionNumber
        this.size = size
        this.type = type
    }

    private int definitionNumber;
    private int size;
    private int type;

    int getDefinitionNumber() {
        return definitionNumber
    }

    int getSize() {
        return size
    }

    int getType() {
        return type
    }
}
