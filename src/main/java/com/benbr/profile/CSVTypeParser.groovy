package main.java.com.benbr.profile

import main.java.com.benbr.profile.types.EnumerationType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

class CSVTypeParser {
    private Reader file;

    CSVTypeParser(Reader file) {
        this.file = file
    }

    HashMap<String, EnumerationType> parse() {
        List<CSVRecord> records = CSVFormat.EXCEL.parse(file).getRecords()
        boolean reachedTitleRow = false;
        HashMap<String, EnumerationType> types = [:];     // The final parsed form

        // Parse state. current* refers to state
        EnumerationType currentType = new EnumerationType()
        String currentTypeName = ""

        records.each { record ->
            if (!reachedTitleRow) {
                reachedTitleRow = true;
                return
            }

            if (lineIsBlank(record)) return

            if (record.getAt(2).size() == 0) {
                // If this is blank, then value name has no value => must be a new record
                // Easier than keeping track of blank lines

                // Only bother writing the next type of it has been filled out.
                // This will only be false the first time, as there has been no previous enumeration values
                if (currentType.getEnumeration().size() > 0) {
                    types << ["$currentTypeName": currentType]
                }

                // Update new state
                currentType = new EnumerationType()
                currentTypeName = record.getAt(0)

                String type = record.getAt(1)
                // Mistake in file, happens at the very end
                if (type.equals("unit8")) {
                    type = "uint8"
                }

                currentType.setBaseType(Constants.baseTypes.find { it.value.equals(type) }.key)
            } else {
                currentType.addType(record.getAt(2), toInteger(record.getAt(3)))
            }
        }

        return types;
    }

    private static boolean lineIsBlank(CSVRecord record) {
        for (int i = 0; i < record.size(); i++) {
            if (record.getAt(i).size() != 0) return false;
        }

        return true;
    }

    private int toInteger(String text) {
        if (text.startsWith("0x")) {
            // Hex
            return Integer.parseInt(text.split("x")[1], 16);
        } else {
            return text.toInteger()
        }
    }

}
