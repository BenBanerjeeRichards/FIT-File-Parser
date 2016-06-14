package main.java.com.benbr.parser

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

import javax.xml.crypto.Data

/**
 * Parses CSV files as produced by the SDK. This is used for testing purposes only.
 */

class CSVFitFileParser {

    Reader file;

    // Finite state machine information for record parsing (the spreadsheet columns).
    private static enum RecordParseState {INITIAL, MESSAGE_TYPE, MESSAGE_NAME, FIELD_NAME, FIELD_VALUE, FIELD_UNIT, LOCAL_NUM}
    private static Map recordFSMTransitions = [
            (RecordParseState.INITIAL) : RecordParseState.MESSAGE_TYPE,
            (RecordParseState.MESSAGE_TYPE) : RecordParseState.LOCAL_NUM,
            (RecordParseState.LOCAL_NUM) : RecordParseState.MESSAGE_NAME,
            (RecordParseState.MESSAGE_NAME) : RecordParseState.FIELD_NAME,
            (RecordParseState.FIELD_NAME) : RecordParseState.FIELD_VALUE,
            (RecordParseState.FIELD_VALUE) : RecordParseState.FIELD_UNIT,
            (RecordParseState.FIELD_UNIT) : RecordParseState.FIELD_NAME
    ]


    CSVFitFileParser(Reader file) {
        this.file = file
    }

    List<DataMessage> parse() {
        List<CSVRecord> records = CSVFormat.EXCEL.parse(file).getRecords()
        List<DataMessage> messages = []

        records.eachWithIndex {CSVRecord record, int rowIdx ->
            if (rowIdx == 0) return;
            def message = parseRow(record)
            if (message != null) {
                messages << message
            }
        }

        return messages
    }

    /**
     * Parses a single row in the CSV file
     * @param record    The CSVRecord row
     * @return A data message. If the record is not a data message, returns null
     */
    private static DataMessage parseRow(CSVRecord record) {
        String currentFieldName = null;
        DataMessage message = new DataMessage()
        def state = RecordParseState.INITIAL

        for (String value : record) {
            // Transition to next FSM state
            state = recordFSMTransitions[state]
            value = nullifyIfNeeded(value)

            switch (state) {
                case RecordParseState.MESSAGE_TYPE:
                    if (value != "Data") return null;
                    break
                case RecordParseState.MESSAGE_NAME:
                    message.type = value
                    break
                case RecordParseState.FIELD_NAME:
                    currentFieldName = value
                    break
                case RecordParseState.FIELD_VALUE:
                    // This field can be ignored. The SDK CSV file contains both the high level field (this)
                    // as well as the processed sub fields.
                    // Possible bug: Field value is a string that contains the pipe symbol. Could check in profile
                    if (value.contains("|")) break;

                    // Ignore excessive fields at the end of the spreadsheet
                    if (value?.size() == 0) break;

                    if (currentFieldName == null) continue;

                    message.fields[currentFieldName] = value
                    break
                case RecordParseState.FIELD_UNIT:
                    if (currentFieldName in (Object)message.fields) {
                        message.unitSymbols[currentFieldName] = value
                    }
                    break
                default:
                    continue
            }

        }

        return message;
    }

    private static nullifyIfNeeded(String value) {
        return value == "unknown" ? null : value;
    }


}
