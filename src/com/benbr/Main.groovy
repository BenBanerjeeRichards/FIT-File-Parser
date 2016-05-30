package com.benbr

import com.benbr.parser.FitParser

class Main {

    public static void main(String[] args) {

        new FitParser().parse(new File("fit/fit.fit")).each { message ->
            println message.getType()
            message.fields.each{field->
                println "\t ${field.getKey()} : ${field.getValue()} ${message.fieldDefinitions[field.getKey()]?.getUnit()}"
            }

            println ""
        }
    }

}
