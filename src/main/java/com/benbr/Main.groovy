package main.java.com.benbr

import main.java.com.benbr.converter.ConversionPolicy
import main.java.com.benbr.converter.Converter
import main.java.com.benbr.converter.MessageConverter
import main.java.com.benbr.converter.Unit
import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.parser.FitParser
import main.java.com.benbr.profile.Constants

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
            DataMessage converted = converter.convertMessage(message)

            converted.fields.each{ field ->
                println "\t${field.getKey()}: ${field.getValue()} (${converted.unitSymbols[field.getKey()]})"
            }

            println ""
        }
    }

}
