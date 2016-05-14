package com.benbr.parser.types

class FieldDefinition {

    FieldDefinition(int definitionNumber, int size, int type) {
        this.definitionNumber = definitionNumber
        this.size = size
        this.type = type
    }

    FieldDefinition(int definitionNumber, int size, int type, String name) {
        this.definitionNumber = definitionNumber
        this.size = size
        this.type = type
        this.name = name
    }
    private int definitionNumber;
    private int size;
    private int type;
    String name;        // From Profiles.xlxs.

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

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
