package main.java.com.benbr.parser

import javax.xml.crypto.Data

class ParseTest {

    public static boolean test(File fitFile, File csvFile) {
        List<DataMessage> fitFileMessages = []
        new FitParser().parse(fitFile).each {
            fitFileMessages << DataMessageParser.flattenComponents(it)
        }

        List<DataMessage> csvFileMessages = new CSVFitFileParser(new FileReader(csvFile)).parse()
        boolean success = true;

        fitFileMessages.eachWithIndex{ DataMessage fitMessage, int idx ->
            def csvMessage = csvFileMessages[idx]

            if (fitMessage.getType() != csvMessage.getType()) {
                println "Fit message and CSV message have different types. Fit : ${fitMessage.getType()}, CSV: ${csvMessage.getType()}"
                success = false;
                return
            }

            if (csvMessage == null) {
                println "CSV message index $idx is null. Num fit messages: ${fitFileMessages.size()}. " +
                        "Num csv messages : ${csvFileMessages.size()}"
                success = false;
                return
            }

            csvMessage.fields.each {csvField ->
                if (csvField.getKey() == "unknown") return

                if (!fitMessage.fields.containsKey((String)csvField.getKey())) {
                    // TODO Look into why the SDK creates enhanced_* fields. These do not exist in the file itself.
                    if (!csvField.getKey().startsWith("enhanced_")) {
                        println "Field ${csvMessage.getType()}->${csvField.getKey()} exists in CSV message but not FIT message"
                        success = false
                    }

                    return
                }

                def csvUnit = csvMessage.unitSymbols[csvField.getKey()] ?: ""
                def fitUnit = fitMessage.unitSymbols[csvField.getKey()] ?: ""
                if (csvUnit != fitUnit) {
                    println "Different units for field ${fitMessage.getType()}->${csvField.getKey()}: csv: ${csvUnit}, fit : ${fitUnit}"
                    success= false
                }
            }

            return success
        }
    }

}
