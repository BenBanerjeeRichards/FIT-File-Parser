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
        Constants.populateUnitToSymbol()
        Converter.loadConversions()

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
