package com.benbr.converter

class Conversion {

    private Double constantMultipleBy;
    private Double constantAddTo;
    private Unit unitFrom;
    private Unit unitTo;

    Double getConstantMultipleBy() {
        return constantMultipleBy
    }

    Double getConstantAddTo() {
        return constantAddTo
    }

    Unit getUnitFrom() {
        return unitFrom
    }

    Unit getUnitTo() {
        return unitTo
    }

    // Builder pattern
    public Conversion to(Unit unitTo) {
        this.unitTo = unitTo
        return this
    }

    public Conversion from(Unit unitFrom) {
        this.unitFrom = unitFrom
        return this
    }

    public Conversion constants(Double constantMultiplyBy, Double constantAddTo) {
        if (constantMultiplyBy == 0d) {
            throw new ConvertException("Invalid constant (multiplyBy) - must be a non zero value")
        }

        this.constantAddTo = constantAddTo
        this.constantMultipleBy = constantMultiplyBy
        return this;
    }

}
