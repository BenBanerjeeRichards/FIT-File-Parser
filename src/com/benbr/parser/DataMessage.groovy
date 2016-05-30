package com.benbr.parser

import com.benbr.parser.types.FieldDefinition
import com.benbr.profile.types.ProfileField
import com.thoughtworks.qdox.parser.structs.FieldDef

class DataMessage {
    String type;

    def fields = [:]
    HashMap<String, Tuple> fieldDefinitions;

    def propertyMissing(String name, String value) {
        fields[name] = value
    }

    def propertyMissing(String name) {
        return fields[name]
    }


}
