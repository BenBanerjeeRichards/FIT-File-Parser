package com.benbr.converter

import com.benbr.parser.DataMessage

final class Converter {

    private static HashMap<String, Double[]> conversions = new HashMap<>();

    private Converter() {}

    static void loadConversions() {
        addConversion(new Conversion().from(Unit.CELSIUS).to(Unit.FAHRENHEIT).constants(1.8, 32))
        addConversion(new Conversion().from(Unit.METRE).to(Unit.MILE).constants(0.00062137, 0))
        addConversion(new Conversion().from(Unit.KILOMETRE).to(Unit.METRE).constants(1000, 0))
        addConversion(new Conversion().from(Unit.METRE).to(Unit.FEET).constants(3.28084, 0))
        addConversion(new Conversion().from(Unit.SEMICIRCLE).to(Unit.DEGREE).constants((180d) / (double)(Math.pow(2, 31)), 0))
    }

    static void addConversion(Conversion conversion) {
        addToMap(conversion.getUnitFrom(), conversion.getUnitTo(), conversion.getConstantMultipleBy(), conversion.getConstantAddTo())

        // Other direction
        Double multiplyBy = 1 / conversion.getConstantMultipleBy()
        Double addTo =  -1 * conversion.getConstantAddTo() / conversion.getConstantMultipleBy()
        addToMap(conversion.getUnitTo(), conversion.getUnitFrom(), multiplyBy, addTo)
    }

    static double convert(double value, Unit unitFrom, Unit unitTo) {
        Double[] constants = conversions[getKey(unitFrom, unitTo)]
        if(constants == null) {
            throw new ConvertException("No conversion found for units specified")
        }

        return value * constants[0] + constants[1]
    }

    private static void addToMap(Unit unitFrom, Unit unitTo, Double constantMultiply, Double constantAdd) {
        Double[] value = new Double[2]
        value = [constantMultiply, constantAdd]

        conversions[getKey(unitFrom, unitTo)] = value
    }

    private static String getKey(Unit unitFrom, Unit unitTo) {
        return "${unitFrom.toString().toLowerCase()}_${unitTo.toString().toLowerCase()}"
    }


}
