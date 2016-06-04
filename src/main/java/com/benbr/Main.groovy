package main.java.com.benbr

import main.java.com.benbr.format.HTMLFITFormatter
import main.java.com.benbr.format.JsonFITFormatter
import main.java.com.benbr.format.TextFITFormatter
import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.parser.FitParser

class Main {

    public static void main(String[] args) {

        def parser = new FitParser()
        List<DataMessage> messages = parser.parse(new File("fit/fit.fit"))

        messages.each {message ->
            println new JsonFITFormatter().formatDataMessage(message)
        }
    }

}
