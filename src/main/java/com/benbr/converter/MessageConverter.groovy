package main.java.com.benbr.converter

import main.java.com.benbr.parser.DataMessage
import main.java.com.benbr.profile.Constants

class MessageConverter {

    private ConversionPolicy conversionPolicy

    MessageConverter(ConversionPolicy conversionPolicy) {
        this.conversionPolicy = conversionPolicy
    }

    Double convertField(String fieldName, String fieldUnit, double value) {
        if (fieldUnit == "") return null
        String currentUnit = Converter.getUnitName(fieldUnit)

        if (fieldUnit == "m") {
            println "Hello"
        }

        String unitTo = Converter.getUnitName(fieldUnitPostConversion(fieldName, fieldUnit));
        if (unitTo == currentUnit || currentUnit == null) return null;

        return Converter.convert(value, currentUnit, unitTo)
    }

    public String fieldUnitPostConversion(String fieldName, String fieldUnit) {
        String unitTo;

        if (conversionPolicy.getFieldPolicy()[fieldName] != null) {
            unitTo = Converter.getUnitSymbol(conversionPolicy.getFieldPolicy()[fieldName])
        } else if (conversionPolicy.getUnitPolicy()[Converter.getUnitName(fieldUnit)]) {
            unitTo = Converter.getUnitSymbol(conversionPolicy.getUnitPolicy()[Converter.getUnitName(fieldUnit)])
        }

        return (unitTo == null) ? fieldUnit : unitTo;
    }

    public DataMessage convertMessage(DataMessage message) {
        DataMessage converted = new DataMessage();

        message.fields.each{field->
            String fieldUnit = message.unitSymbols[(String)field.getKey()]
            Double convertedValue = null

            // TODO think of something other than instanceof. Using localfield.getType type does not always seem to work
            if (field.getValue() instanceof Number) {
                convertedValue = convertField((String)field.getKey(), fieldUnit, (double)field.getValue())
            }
            if (convertedValue != null) {
                String unit = fieldUnitPostConversion((String)field.getKey(), fieldUnit)
                converted.fields[(String)field.getKey()] = convertedValue
                converted.unitSymbols[(String)field.getKey()] = unit
            } else {
                converted.fields[(String)field.getKey()] = field.getValue()
                converted.unitSymbols[(String)field.getKey()] = fieldUnit
            }
        }

        converted.type = message.type;
        return converted;
    }

}

