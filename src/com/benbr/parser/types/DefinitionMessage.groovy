package com.benbr.parser.types

class DefinitionMessage {

    DefinitionMessage(ArchitectureType architectureType, int globalMessageNumber, int fieldCount, List<FieldDefinition> fieldDefinitions) {
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldCount = fieldCount
        this.fieldDefinitions = fieldDefinitions
    }

    DefinitionMessage(int reserverd, ArchitectureType architectureType, int globalMessageNumber, int fieldCount, List<FieldDefinition> fieldDefinitions) {
        this.reserverd = reserverd
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldCount = fieldCount
        this.fieldDefinitions = fieldDefinitions
    }

    private int reserverd
    private ArchitectureType architectureType
    private int globalMessageNumber
    private int fieldCount
    private List<FieldDefinition> fieldDefinitions

    int getReserverd() {
        return reserverd
    }

    ArchitectureType getArchitectureType() {
        return architectureType
    }

    int getGlobalMessageNumber() {
        return globalMessageNumber
    }

    int getFieldCount() {
        return fieldCount
    }

    List<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions
    }
}
