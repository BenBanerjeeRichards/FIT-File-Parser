package main.java.com.benbr.format

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import main.java.com.benbr.parser.DataMessage

class JsonFITFormatter implements FITFormatter{
    String formatDataMessage(DataMessage message) {
        // It is much easier and more efficient to use the less elegant string substitution
        // than it is to rebuild a new object to serialize into JSON.
        StringBuilder json = new StringBuilder();
        String head = /{"message_type":"${message.getType()}","fields":[/
        json.append(head)

        message.fields.eachWithIndex {field, int idx->
            String comma = idx == message.fields.size() - 1 ? "" : ",";
            json.append(/{"name":"${field.getKey()}", "value":"${field.getValue()}","unit":"${message.unitSymbols[field.getKey()]}"}${comma}/)
        }
        json.append("]}")
        return json.toString()
    }
}
