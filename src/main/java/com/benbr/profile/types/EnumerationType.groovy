package main.java.com.benbr.profile.types

class EnumerationType {

    // Hash map uses as the enum index mostly, BUT not not always, increments from one linearly.

    HashMap<Integer, String> enumeration;
    int[] manufacturerRange;
    int baseType;           // Contants.baseTypes

    EnumerationType() {
        enumeration = [:]
        manufacturerRange = [0xFF, 0xFF]
    }

    public EnumerationType addType(String name, int value) {
        if (name.equals("mfg_range_min")) {
            manufacturerRange[0] = value
        } else if (name.equals("mfg_range_max")) {
            manufacturerRange[1] = value
        } else {
            enumeration[value] = name
        }

        return this
    }

    HashMap<Integer, String> getEnumeration() {
        return enumeration
    }

    EnumerationType setBaseType(int baseType) {
        this.baseType = baseType
        return this
    }

    String getInitializationCode() {
        def sb = new StringBuilder("new EnumerationType().setBaseType($baseType)")

        enumeration.each {value, name ->
            sb.append(".addType(\"$name\", $value)")
        }

        return sb.toString()
    }

}
