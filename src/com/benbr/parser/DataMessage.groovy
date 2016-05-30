package com.benbr.parser

import com.benbr.profile.types.ProfileField

class DataMessage {
    String type;

    def fields = [:]
    HashMap<String, ProfileField> fieldDefinitions;

    def propertyMissing(String name, String value) {
        fields[name] = value
    }

    def propertyMissing(String name) {
        return fields[name]
    }


}
