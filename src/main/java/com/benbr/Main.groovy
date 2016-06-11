package main.java.com.benbr

import main.java.com.benbr.converter.ConversionPolicy
import main.java.com.benbr.converter.MessageConverter
import main.java.com.benbr.parser.FitParser
import main.java.com.benbr.profile.CSVProfileParser
import main.java.com.benbr.profile.CSVTypeParser
import main.java.com.benbr.profile.ProfileCodeGenerator
import main.java.com.benbr.profile.TypeCodeGenerator

class Main {
    private static void generateProfileFiles() {
        String code = new ProfileCodeGenerator().generateCode(new CSVProfileParser(new FileReader("profile.csv")).getFields())
        new File("src\\main\\java\\com\\benbr\\Profile.groovy").write(code)
        code = new TypeCodeGenerator().generateCode(new CSVTypeParser(new FileReader("types.csv")).parse())
        new File("src\\main\\java\\com\\benbr\\Type.groovy").write(code)
    }

    public static void main(String[] args) {
        generateProfileFiles()
        HashMap<String, String> unitPolicy = [
                semicircle: "degree",
        ]

        HashMap<String, String> fieldPolicy = [
                altitude: "feet",
                distance: "mile"
        ]

        def converter = new MessageConverter(new ConversionPolicy(fieldPolicy, unitPolicy))

        new FitParser().parse(new File("fit/compressed-speed-distance.fit")).each { message ->
            println "${message.type}"

            message.fields.each { field ->
                if (message.fieldIsArray[field.getKey()]) {
                    println "\t${field.getKey()}"
                    (Map) field.getValue().each { Map.Entry subField ->
                        def unit = message.unitSymbols[field.getKey()]?.get(subField.getKey())
                        println "\t\t${subField.getKey()} : ${subField.getValue()} ${unit}"
                    }
                } else {
                    println "\t${field.getKey()} : ${field.getValue()} (${message.unitSymbols[field.getKey()]})"
                }

            }
            println ""
        }

    }

}

