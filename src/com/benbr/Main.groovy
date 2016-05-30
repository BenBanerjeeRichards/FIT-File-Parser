package com.benbr

import com.benbr.converter.Conversion
import com.benbr.converter.ConversionPolicy
import com.benbr.converter.Converter
import com.benbr.converter.MessageConverter
import com.benbr.converter.Unit
import com.benbr.parser.FitParser
import com.benbr.parser.types.FieldDefinition
import com.benbr.profile.Constants
import com.benbr.profile.types.ProfileField

class Main {

    public static void main(String[] args) {

        Converter.addConversion(new Conversion().from(Unit.CELSIUS).to(Unit.FAHRENHEIT).constants(1.8, 32))
        Converter.addConversion(new Conversion().from(Unit.METRE).to(Unit.MILE).constants(0.00062137, 0))
        Converter.addConversion(new Conversion().from(Unit.KILOMETRE).to(Unit.METRE).constants(1000, 0))
        Converter.addConversion(new Conversion().from(Unit.METRE).to(Unit.FEET).constants(3.28084, 0))
        Converter.addConversion(new Conversion().from(Unit.SEMICIRCLE).to(Unit.DEGREE).constants((180d) / (double)(Math.pow(2, 31)), 0))

        Map<Unit, String> m = new BiMap()
        m.put(Unit.METRE, "m")
        m.put(Unit.SEMICIRCLE, "semicircles")
        m.put(Unit.REV_PER_MIN, "rpm")
        m.put(Unit.CELSIUS, "C")
        m.put(Unit.FAHRENHEIT, "C")
        m.put(Unit.MILE, "miles")
        m.put(Unit.DEGREE, "deg")
        m.put(Unit.KILOMETRE, "km")
        m.put(Unit.FEET, "feet")
        Constants.unitToSymbol = m

        HashMap<Unit, Unit> conversionPolicy = [
                (Unit.SEMICIRCLE) : Unit.DEGREE,
                (Unit.METRE) : Unit.MILE,
        ]

        HashMap<String, Unit> fieldPolicy = [
                altitude: Unit.FEET
        ]

        def converter = new MessageConverter(new ConversionPolicy(fieldPolicy, conversionPolicy))

        new FitParser().parse(new File("fit/fit.fit")).each { message ->
            println message.getType()
            message.fields.each{field->
                String fieldUnit = message.unitSymbols[(String)field.getKey()]
                Double convertedValue = null

                // TODO think of something other than instanceof. Using localfield.getType type does not always seem to work
                if (field.getValue() instanceof Number) {
                    convertedValue = converter.convertField((String)field.getKey(), fieldUnit, (double)field.getValue())
                }

                if (convertedValue != null) {
                    println "\t ${field.getKey()} : ${convertedValue} ${Constants.unitToSymbol.getForwards(converter.fieldUnitPostConversion((String)field.getKey(), fieldUnit))}"
                } else {
                    println "\t ${field.getKey()} : ${field.getValue()} ${fieldUnit}"
                }
            }

            println ""
        }
    }

}
