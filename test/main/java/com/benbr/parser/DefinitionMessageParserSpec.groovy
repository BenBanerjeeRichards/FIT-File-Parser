package main.java.com.benbr.parser

import main.java.com.benbr.parser.types.*
import spock.lang.Specification

class DefinitionMessageParserSpec extends Specification {

    def "Given a valid definition record, it is parsed correctly"() {
        when:
        def header = new MessageHeader(MessageType.DEFINITION, MessageHeaderType.NORMAL, 0)
        byte[] bytes = [0x00, 0x00, 0x00, 0x00, 0x06, 0x03, 0x04, 0x8C, 0x04, 0x04, 0x86, 0x01, 0x02, 0x84, 0x02, 0x02, 0x84, 0x05, 0x02, 0x84, 0x00, 0x01, 0x00] as byte[]
        def input = new DataInputStream(new ByteArrayInputStream(bytes))
        List<FieldDefinition> fields = [
                new FieldDefinition(3, 4, 140),
                new FieldDefinition(4, 4, 134),
                new FieldDefinition(1, 2, 132),
                new FieldDefinition(2, 2, 132),
                new FieldDefinition(5, 2, 132),
                new FieldDefinition(0, 1, 0)
        ]

        def message = new DefinitionMessage(0, ArchitectureType.LITTLE_ENDIAN, 0, fields, null)
        def _message = new DefinitionMessageParser().parse(input, header)

        then:
        // There has to be a better way
        // Probably involves writing eqauls methods
        _message.architectureType == message.architectureType
        _message.globalMessageNumber == message.globalMessageNumber
        _message.reserved == message.reserved

        fields.eachWithIndex { it, idx ->
            it.equals(_message.fieldDefinitions[idx])
        }
    }

    // TODO figure out how to paramatarise these tests
    def "Given a different valid record, it is parsed correctly"() {
        when:
        def header = new MessageHeader(MessageType.DEFINITION, MessageHeaderType.NORMAL, 0)
        byte[] bytes = [0xFF, 0x00, 0x00, 0x84, 0x02, 0x00, 0x02, 132, 9, 4, 134] as byte[]
        def input = new DataInputStream(new ByteArrayInputStream(bytes))
        List<FieldDefinition> fields = [
                new FieldDefinition(0, 2, 132),
                new FieldDefinition(9, 4, 134),
        ]

        def message = new DefinitionMessage(132, ArchitectureType.LITTLE_ENDIAN, 0xFF, fields, null)
        def _message = new DefinitionMessageParser().parse(input, header)

        then:
        _message.architectureType == message.architectureType
        _message.globalMessageNumber == message.globalMessageNumber
        _message.reserved == message.reserved

        fields.eachWithIndex { it, idx ->
            it.equals(_message.fieldDefinitions[idx])
        }
    }

}
