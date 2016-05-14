package com.benbr.parser

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream

class FitParser {

    private DataInputStream fitStream;

    public FitParser(File fitFile) {
        fitStream = new DataInputStream(new FileInputStream(fitFile))
    }

}
