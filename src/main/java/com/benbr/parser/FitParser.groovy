package main.java.com.benbr.parser

import main.java.com.benbr.Profile
import main.java.com.benbr.Util
import main.java.com.benbr.converter.Converter
import main.java.com.benbr.parser.types.DefinitionMessage
import main.java.com.benbr.parser.types.MessageType

import java.util.concurrent.LinkedBlockingQueue

class FitParser {

    /**
     * Handles the parsing of FIT files.
     *
     * <p>The constructor ensures that everything is loaded correctly before the parsing starts.</p>
     */
    public FitParser() {
        Converter.loadConversions()

        if (Profile.profile.size() == 0) {
            Profile.invokeMethod("_build", null)
        }

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
                DefinitionMessageParser.associateFieldDefinitionWithGlobalProfile(message, message.getGlobalMessageNumber())
            } else {
                DataMessage message = new DataMessageParser(locals, accumulatedFields, timestampReference).parse(fitStream, header)

                if (!header.isCompressedTimestamp() && message.fields["timestamp"] != null) {
                    timestampReference = message.timestamp
                }

                messageQueue.add(message)
            }
        }
    }

    /**
     * Parses a FIT file.
     *
     * The method returns a list of  {@link DataMessage DataMessage} instances, each of which refers to a single data
     * message that occurs in the FIT file. The messages are in the same order that that they appeared in the file.
     *
     * <p> The data messages are the containers that store the information about an activity (or any other kind of file,
     * such as settings and route plans). This includes sensor information (heart rate, speed, position) as well as
     * time and some others.</p>
     *
     * <p> As an example, in an activity the vast majority of the file will be made up of record messages - these contain
     * information at a single point in time (typically denoted by the `timestamp` field). The start of the file will
     * contain information about the device (serial number, manufacturer...) and the very end will contain totals and
     * notable records (max heart rate, distance covered, elevation, etc...). </p>
     *
     * @param fitFile - the FIT file to parse
     * @return a list containing all of the DataMessages found in the FIT file (in sequential order).
     */
    public List<DataMessage> parse(File fitFile) {
        Queue<DataMessage> messageQueue = new LinkedBlockingQueue<>()
        parseFile(fitFile, messageQueue)
        return messageQueue.toList()
    }

    /**
     * This method does exactly the same thing as {@link #parse(java.io.File)}, but it carries out the parsing in a
     * separate thread.
     *
     * <p>The method immediately returns a {@link Queue<DataMessage>}, which initially will contain no items. As soon as
     * the FIT file parser decodes a data message, it will put the {@link DataMessage DataMessage} onto the queue before
     * it starts working on the next data message. </p>
     *
     * <p>The parser is not reliant on the messages it pushes onto the queue, so the consumer can safely remove them. </p>
     *
     * <p><strong>Important: This method is not finished yet. There is currently not way of notifying the consumer
     * when the parser has finished parsing the file.</strong> As a workaround, use the
     * {@link java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)} method.</p>
     *
     * @see #parse(java.io.File)
     * @param fitFile
     * @return
     */

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
