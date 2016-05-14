package com.benbr.parser.types

class DefinitionMessage {

    DefinitionMessage(ArchitectureType architectureType, int globalMessageNumber, int fieldCount, List<FieldDefinition> fieldDefinitions) {
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldDefinitions = fieldDefinitions
    }

    DefinitionMessage(int reserverd, ArchitectureType architectureType, int globalMessageNumber, int fieldCount, List<FieldDefinition> fieldDefinitions, List<FieldDefinition> developerFieldDefinitions) {
        this.reserverd = reserverd
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldDefinitions = fieldDefinitions
        this.developerFieldDefinitions = developerFieldDefinitions
    }

    private int reserverd
    private ArchitectureType architectureType
    private int globalMessageNumber
    private List<FieldDefinition> fieldDefinitions
    
    // Usually not set (depends of reserved bit in header)
    private List<FieldDefinition> developerFieldDefinitions;

    int getReserverd() {
        return reserverd
    }

    ArchitectureType getArchitectureType() {
        return architectureType
    }

    int getGlobalMessageNumber() {
        return globalMessageNumber
    }

    List<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions
    }
}
