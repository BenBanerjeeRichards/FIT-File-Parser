package main.java.com.benbr.parser

class DataMessage {
    String type;
    HashMap<String, Object> fields
    HashMap<String, Object> unitSymbols;
    HashMap<String, Boolean> fieldIsArray;

    DataMessage() {
        fields = [:]
        unitSymbols = [:]
        fieldIsArray = [:]
    }

    def propertyMissing(String name, String value) {
        fields[name] = value
    }

    def propertyMissing(String name) {
        return fields[name]
    }


}
