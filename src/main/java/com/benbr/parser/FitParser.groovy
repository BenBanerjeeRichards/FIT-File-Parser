package main.java.com.benbr.parser

import main.java.com.benbr.converter.Converter
import main.java.com.benbr.parser.types.MessageType
import main.java.com.benbr.profile.CSVTypeParser
import main.java.com.benbr.profile.Constants
import main.java.com.benbr.profile.types.ProfileField
import main.java.com.benbr.Util
import main.java.com.benbr.parser.types.DefinitionMessage
import main.java.com.benbr.profile.CSVProfileParser
import main.java.com.benbr.profile.types.EnumerationType

import java.util.concurrent.LinkedBlockingQueue

class FitParser {

    private HashMap<String, List<ProfileField>> profile;
    private HashMap<String, EnumerationType> types;

    public FitParser() {
        ClassLoader loader = getClass().getClassLoader()
        InputStreamReader profileFile = new InputStreamReader(loader.getResourceAsStream("profile.csv"));
        profile = new CSVProfileParser(profileFile).getFields()
        types = new CSVTypeParser(new InputStreamReader(loader.getResourceAsStream("types.csv"))).parse()

        Converter.loadConversions()
    }

    private void parseFile(File fitFile, Queue<DataMessage> messageQueue) {
        DataInputStream fitStream = new DataInputStream(new DataInputStream(new FileInputStream(fitFile)))
        def locals = new HashMap<Integer, DefinitionMessage>()
        def fileHeader = new FileHeaderParser().parseHeader(fitStream)
        Map<String, Object> accumulatedFields = new HashMap<>()
        long timestampReference = 0;

        while (fitStream.available() > 0) {
            if (Util.getBytesRead() - fileHeader.getSize() >= fileHeader.getDataSize()) {
                return
            }

            def headerByte = Util.read(fitStream)
            def header = new MessageHeaderParser().parse(headerByte)

            if (header.messageType == MessageType.DEFINITION) {
                DefinitionMessage message = new DefinitionMessageParser().parse(fitStream, header)
                locals.put(header.getLocalMessageType(), message)
                DefinitionMessageParser.associateFieldDefinitionWithGlobalProfile(profile, message, message.getGlobalMessageNumber())
            } else {
                DataMessage message = new DataMessageParser(profile, types).parse(fitStream, header, locals, accumulatedFields, timestampReference)

                if (!header.isCompressedTimestamp() && message.fields["timestamp"] != null) {
                    timestampReference = message.timestamp
                }

                messageQueue.add(message)
            }
        }
    }

    public List<DataMessage> parse(File fitFile) {
        Queue<DataMessage> messageQueue = new LinkedBlockingQueue<>()
        parseFile(fitFile, messageQueue)
        return messageQueue.toList()
    }

    public Queue<DataMessage> parseAsync(File fitFile) {
        Queue<DataMessage> messageQueue = new LinkedBlockingQueue<>()
        new Thread(new Runnable() {
            @Override
            void run() {
                parseFile(fitFile, messageQueue)
            }
        }).start()

        return messageQueue
    }

}
