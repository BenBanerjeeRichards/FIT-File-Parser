package com.benbr.parser

import com.benbr.parser.types.FieldDefinition
import com.benbr.profile.types.ProfileField
import com.thoughtworks.qdox.parser.structs.FieldDef

class DataMessage {
    String type;
    HashMap<String, Object> fields
    HashMap<String, String> unitSymbols;

    DataMessage() {
        fields = [:]
        unitSymbols = [:]
    }

    def propertyMissing(String name, String value) {
        fields[name] = value
    }

    def propertyMissing(String name) {
        return fields[name]
    }


}
