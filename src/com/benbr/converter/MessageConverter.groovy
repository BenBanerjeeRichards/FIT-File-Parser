package com.benbr.converter

import com.benbr.parser.DataMessage
import com.benbr.profile.Constants
import com.benbr.profile.types.ProfileField

class MessageConverter {

    private ConversionPolicy conversionPolicy

    MessageConverter(ConversionPolicy conversionPolicy) {
        this.conversionPolicy = conversionPolicy
    }

    Double convertField(String fieldName, String fieldUnit, double value) {
        if (fieldUnit == null) return null
        Unit currentUnit = (Unit)Constants.unitToSymbol.getBackwards(fieldUnit)

        Unit unitTo = fieldUnitPostConversion(fieldName, fieldUnit);
        if (unitTo == currentUnit) return null;
        return Converter.convert(value, currentUnit, unitTo)
    }

    public Unit fieldUnitPostConversion(String fieldname, String fieldUnit) {
        Unit currentUnit = (Unit)Constants.unitToSymbol.getBackwards(fieldUnit)
        Unit unitTo;

        if (conversionPolicy.getFieldPolicy()[fieldname] != null) {
            unitTo = conversionPolicy.getFieldPolicy()[fieldname]
        } else if (conversionPolicy.getUnitPolicy()[currentUnit]) {
            unitTo = conversionPolicy.getUnitPolicy()[currentUnit]
        }

        return (unitTo == null) ? currentUnit : unitTo;
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
                String unit = Constants.unitToSymbol.getForwards(fieldUnitPostConversion((String)field.getKey(), fieldUnit))
                converted.fields[(String)field.getKey()] = convertedValue
                converted.unitSymbols[(String)field.getKey()] = unit
            } else {
                converted.fields[(String)field.getKey()] = field.getValue()
                converted.unitSymbols[(String)field.getKey()] = fieldUnit
            }
        }

        return converted;
    }

}

