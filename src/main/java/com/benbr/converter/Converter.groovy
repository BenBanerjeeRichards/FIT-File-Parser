package main.java.com.benbr.converter

import main.java.com.benbr.BiMap

final class Converter {

    private static HashMap<String, Double[]> conversions = [:]
    private static BiMap units = new BiMap()

    private Converter() {}

    static void loadConversions() {
        addConversion(new Conversion().from("celsius").to("fahrenheit").constants(1.8, 32))
        addConversion(new Conversion().from("metre").to("mile").constants(0.00062137, 0))
        addConversion(new Conversion().from("kilometre").to("metre").constants(1000, 0))
        addConversion(new Conversion().from("metre").to("feet").constants(3.28084, 0))
        addConversion(new Conversion().from("semicircle").to("degree").constants((180d) / (double) (Math.pow(2, 31)), 0))

        addUnit("semicircle", "semicircles")
        addUnit("celsius", "C")
        addUnit("kilometre", "km")
        addUnit("metre", "m")
        addUnit("degree", "deg")
        addUnit("feet", "ft")
        addUnit("mile", "mile")
    }

    static void addConversion(Conversion conversion) {
        addToMap(conversion.getUnitFrom(), conversion.getUnitTo(), conversion.getConstantMultipleBy(), conversion.getConstantAddTo())

        // Other direction
        Double multiplyBy = 1 / conversion.getConstantMultipleBy()
        Double addTo = -1 * conversion.getConstantAddTo() / conversion.getConstantMultipleBy()
        addToMap(conversion.getUnitTo(), conversion.getUnitFrom(), multiplyBy, addTo)
    }

    static void addUnit(String name, String symbol) {
        if (name.contains("->")) {
            throw new ConvertException("Unit name `${name}` is invalid: it must not contain the sequenct `->`")
        }
        units[name] = symbol
    }

    static double convert(double value, String unitFrom, String unitTo) {
        Double[] constants = conversions[getKey(unitFrom, unitTo)]
        if (constants == null) {
            throw new ConvertException("No conversion found for units specified (${unitFrom} -> ${unitTo})")
        }

        return value * constants[0] + constants[1]
    }

    private static void addToMap(String unitFrom, String unitTo, Double constantMultiply, Double constantAdd) {
        Double[] value = new Double[2]
        value = [constantMultiply, constantAdd]

        conversions[getKey(unitFrom, unitTo)] = value
    }

    private static String getKey(String unitFrom, String unitTo) {
        return "${unitFrom}->${unitTo}"
    }

    static String getUnitSymbol(String name) {
        return units.getForwards(name)
    }

    static String getUnitName(String symbol) {
        return units.getBackwards(symbol)
    }

}
