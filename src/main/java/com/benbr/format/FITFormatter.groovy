package main.java.com.benbr.format

import main.java.com.benbr.parser.DataMessage

interface FITFormatter {
    String formatDataMessage(DataMessage message);
}