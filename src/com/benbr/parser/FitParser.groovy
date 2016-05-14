package com.benbr.parser

class FitParser {

    private DataInputStream fitStream;

    public FitParser(File fitFile) {
        fitStream = new DataInputStream(new FileInputStream(fitFile))
        new FileHeaderParser().parseHeader(fitStream)
    }

}
