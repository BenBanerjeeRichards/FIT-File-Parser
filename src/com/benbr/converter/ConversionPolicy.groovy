package com.benbr.converter

class ConversionPolicy {

    private HashMap<String, Unit> fieldPolicy;
    private HashMap<Unit, Unit> unitPolicy;

    ConversionPolicy(HashMap<String, Unit> fieldPolicy, HashMap<Unit, Unit> unitPolicy) {
        this.fieldPolicy = fieldPolicy
        this.unitPolicy = unitPolicy
    }

    ConversionPolicy(HashMap<String, Unit> fieldPolicy) {
        this.fieldPolicy = fieldPolicy
        this.unitPolicy = [:]
    }

    HashMap<String, Unit> getFieldPolicy() {
        return fieldPolicy
    }

    HashMap<Unit, Unit> getUnitPolicy() {
        return unitPolicy
    }
}
