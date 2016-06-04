package main.java.com.benbr.format

import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.parser.types.DefinitionMessage

class TextFITFormatter implements FITFormatter{
    String formatDataMessage(DataMessage message) {
        StringBuilder builder = new StringBuilder("${message.getType()}\n")

        message.fields.each {field->
            builder.append("\t${field.getKey()} : ${field.getValue()} (${message.unitSymbols[field.getKey()]})\n")
        }
        return builder.toString()
    }
}