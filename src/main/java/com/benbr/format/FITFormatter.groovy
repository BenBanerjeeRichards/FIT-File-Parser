package main.java.com.benbr.format

import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.parser.types.DefinitionMessage

interface FITFormatter {
    String formatDataMessage(DataMessage message);
    String formatDefinitionMessage(DefinitionMessage message)
}