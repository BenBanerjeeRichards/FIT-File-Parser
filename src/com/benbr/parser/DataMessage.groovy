package com.benbr.parser

class DataMessage {

    def fields = [:]

    def propertyMissing(String name, String value) {
        fields[name] = value
    }

    def propertyMissing(String name) {
        return fields[name]
    }


}
