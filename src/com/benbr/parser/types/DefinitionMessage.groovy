package com.benbr.parser.types

class DefinitionMessage {

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DefinitionMessage that = (DefinitionMessage) o

        if (globalMessageNumber != that.globalMessageNumber) return false
        if (reserved != that.reserved) return false
        if (architectureType != that.architectureType) return false
        if (developerFieldDefinitions != that.developerFieldDefinitions) return false
        if (fieldDefinitions != that.fieldDefinitions) return false

        return true
    }

    int hashCode() {
        int result
        result = reserved
        result = 31 * result + architectureType.hashCode()
        result = 31 * result + globalMessageNumber
        result = 31 * result + (fieldDefinitions != null ? fieldDefinitions.hashCode() : 0)
        result = 31 * result + (developerFieldDefinitions != null ? developerFieldDefinitions.hashCode() : 0)
        return result
    }

    DefinitionMessage(ArchitectureType architectureType, int globalMessageNumber, List<FieldDefinition> fieldDefinitions) {
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldDefinitions = fieldDefinitions
    }

    DefinitionMessage(int reserverd, ArchitectureType architectureType, int globalMessageNumber, List<FieldDefinition> fieldDefinitions, List<FieldDefinition> developerFieldDefinitions) {
        this.reserved = reserverd
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldDefinitions = fieldDefinitions
        this.developerFieldDefinitions = developerFieldDefinitions
    }

    private int reserved
    private ArchitectureType architectureType
    private int globalMessageNumber
    private List<FieldDefinition> fieldDefinitions

    // Usually not set (depends of reserved bit in header)
    private List<FieldDefinition> developerFieldDefinitions;

    int getReserverd() {
        return reserved
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
