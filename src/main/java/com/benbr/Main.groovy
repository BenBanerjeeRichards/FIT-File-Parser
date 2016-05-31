package main.java.com.benbr

import main.java.com.benbr.converter.ConversionPolicy
import main.java.com.benbr.converter.Converter
import main.java.com.benbr.converter.MessageConverter
import main.java.com.benbr.converter.Unit
import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.parser.DataMessageParser
import main.java.com.benbr.parser.FitParser
import main.java.com.benbr.profile.Constants

class Main {

    public static void main(String[] args) {

        HashMap<Unit, Unit> globalConversionPolicy = [
                (Unit.SEMICIRCLE) : Unit.DEGREE,
                (Unit.METRE) : Unit.MILE,
        ]

        HashMap<String, Unit> fieldPolicy = [
                altitude: Unit.FEET
        ]


        def converter = new MessageConverter(new ConversionPolicy(fieldPolicy, globalConversionPolicy))

        new FitParser().parse(new File("fit/fit.fit")).each { message ->
            println message.getType()
            DataMessage converted = converter.convertMessage(message)

            converted.fields.each{ field ->
                println "\t ${field.getKey()}: ${field.getValue()} (${converted.unitSymbols[field.getKey()]})"
            }
        }


//        def parser = new FitParser()
//        List<DataMessage> messages = parser.parse(new File("fit/fit.fit"))
//
//        // Iterate over each of the data messages in the FIT file
//        messages.each {message ->
//            // Print the data message name (for example, 'record' or 'file_id')
//            println message.getType()
//
//            // Iterate over the fields
//            message.fields.each { field ->
//                // Look up the unit symbol in the hashmap
//                String unitSymbol = message.unitSymbols[field.getKey()]
//                println "\t${field.getKey()} : ${field.getValue()} (${unitSymbol})"
//            }
//        }
    }

}
