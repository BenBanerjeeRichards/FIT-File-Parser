package main.java.com.benbr.converter

class ConversionPolicy {

    private HashMap<String, String> fieldPolicy;
    private HashMap<String, String> unitPolicy;

    ConversionPolicy(HashMap<String, String> fieldPolicy, HashMap<String, String> unitPolicy) {
        this.fieldPolicy = fieldPolicy
        this.unitPolicy = unitPolicy
    }

    ConversionPolicy(HashMap<String, String> fieldPolicy) {
        this.fieldPolicy = fieldPolicy
        this.unitPolicy = [:]
    }

    HashMap<String, String> getFieldPolicy() {
        return fieldPolicy
    }

    HashMap<String, String> getUnitPolicy() {
        return unitPolicy
    }
}
