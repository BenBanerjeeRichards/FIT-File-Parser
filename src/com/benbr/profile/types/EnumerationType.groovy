package com.benbr.profile.types

class EnumerationType {

    // Hash map uses as the enum index mostly, BUT not not always, increments from one linearly.

    private HashMap<Integer, String> enumeration;
    private int[] manufacturerRange;
    private int baseType;           // Contants.baseTypes

    EnumerationType() {
        enumeration = [:]
        manufacturerRange = [0xFF, 0xFF]
    }

    public void addType(String name, int value) {
        if (name.equals("mfg_range_min")) {
            manufacturerRange[0] = value
        } else if (name.equals("mfg_range_max")) {
            manufacturerRange[1] = value
        } else {
            enumeration[value] = name
        }
    }

    HashMap<Integer, String> getEnumeration() {
        return enumeration
    }

    int[] getManufacturerRange() {
        return manufacturerRange
    }

    int getBaseType() {
        return baseType
    }

    void setBaseType(int baseType) {
        this.baseType = baseType
    }
}
