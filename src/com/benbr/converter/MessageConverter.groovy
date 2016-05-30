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

}
