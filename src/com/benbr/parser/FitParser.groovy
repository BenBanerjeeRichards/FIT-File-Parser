package com.benbr.parser

import com.benbr.parser.types.MessageType
import com.benbr.profile.CSVProfileParser

class FitParser {

    private DataInputStream fitStream;

    public FitParser(File fitFile) {
        fitStream = new DataInputStream(new FileInputStream(fitFile))
        new FileHeaderParser().parseHeader(fitStream)
        def defHeader = new MessageHeaderParser().parse(fitStream.read())
        def defMessage = new DefinitionMessageParser().parse(fitStream, defHeader)
        def profile = new CSVProfileParser(new File("profile.csv")).getFields()

        DefinitionMessageParser.associateFieldDefinitionWithGlobalProfile(profile, defMessage, defHeader.localMessageType)

        defMessage.globalFields.each {
            println it.getName()
        }

    }

}
