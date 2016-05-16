package com.benbr.parser

import com.benbr.parser.types.DefinitionMessage
import com.benbr.parser.types.FieldDefinition
import com.benbr.parser.types.MessageType
import com.benbr.profile.CSVProfileParser
import com.benbr.profile.CSVTypeParser
import com.benbr.profile.Constants

class FitParser {

    private DataInputStream fitStream;

    public FitParser(File fitFile) {
        fitStream = new DataInputStream(new FileInputStream(fitFile))
        def locals = new HashMap<Integer, DefinitionMessage>()
        def profile = new CSVProfileParser(new File("profile.csv")).getFields()
        def types = new CSVTypeParser(new File("types.csv")).parse()
        new FileHeaderParser().parseHeader(fitStream)

        while (fitStream.available() > 0) {
            def header = new MessageHeaderParser().parse(fitStream.read())
            println header

            if (header.messageType == MessageType.DEFINITION) {
                DefinitionMessage message = new DefinitionMessageParser().parse(fitStream, header)
                locals.put(header.getLocalMessageType(), message)
                DefinitionMessageParser.associateFieldDefinitionWithGlobalProfile(profile, message, message.getGlobalMessageNumber())
            } else {
                DataMessage message = new DataMessageParser(profile, types).parse(fitStream, header, locals)
            }
        }

    }

}
