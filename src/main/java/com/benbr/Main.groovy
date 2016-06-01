package main.java.com.benbr

import main.java.com.benbr.format.HTMLFITFormatter
import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.parser.FitParser

class Main {

    public static void main(String[] args) {

        def parser = new FitParser()
        List<DataMessage> messages;
            messages  = parser.parse(new File("fit/fit.fit"))

        File f = new File("out.html")

        // Iterate over each of the data messages in the FIT file
        messages.each {message ->
            // Print the data message name (for example, 'record' or 'file_id')
            String html = new HTMLFITFormatter().formatDataMessage(message)
            f.append(html)
//            // Iterate over the fields
//            message.fields.each { field ->
//                // Look up the unit symbol in the hashmap
//                String unitSymbol = message.unitSymbols[field.getKey()]
//                println "\t${field.getKey()} : ${field.getValue()} (${unitSymbol})"
//            }
        }
    }

}
