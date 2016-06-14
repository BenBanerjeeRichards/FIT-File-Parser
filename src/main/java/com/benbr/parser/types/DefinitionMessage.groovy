package main.java.com.benbr.parser.types

import main.java.com.benbr.profile.types.ProfileField

class DefinitionMessage {

    DefinitionMessage(int globalMessageNumber, ArchitectureType architectureType, int reserved, List<FieldDefinition> fieldDefinitions, List<FieldDefinition> developerFieldDefinitions) {
        this.reserved = reserved
        this.architectureType = architectureType
        this.globalMessageNumber = globalMessageNumber
        this.fieldDefinitions = fieldDefinitions
        this.developerFieldDefinitions = developerFieldDefinitions
        globalFields = []

        // Vanilla loop prevents concurrent access exceptions
        for (int i = 0; i < fieldDefinitions.size(); i++) {
            this.globalFields.add(i, null)
        }

    }

    private int reserved
    private ArchitectureType architectureType
    private int globalMessageNumber
    private List<FieldDefinition> fieldDefinitions;
    private List<ProfileField> globalFields;

    public void addFieldAssociation(int fieldDefinitionIndex, ProfileField globalField) {
        this.globalFields.add(fieldDefinitionIndex, globalField)
    }

    List<ProfileField> getGlobalFields() {
        return globalFields
    }

    List<FieldDefinition> getDeveloperFieldDefinitions() {
        return developerFieldDefinitions
    }
    // Usually not set (depends of reserved bit in header)
    private List<FieldDefinition> developerFieldDefinitions;

    int getReserved() {
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

    void setDeveloperFieldDefinitions(List<FieldDefinition> developerFieldDefinitions) {
        this.developerFieldDefinitions = developerFieldDefinitions
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DefinitionMessage that = (DefinitionMessage) o

        if (globalMessageNumber != that.globalMessageNumber) return false
        if (reserved != that.reserved) return false
        if (architectureType != that.architectureType) return false
        if (developerFieldDefinitions != that.developerFieldDefinitions) return false
        if (fieldDefinitions != that.fieldDefinitions) return false
        if (globalFields != that.globalFields) return false

        return true
    }

}
