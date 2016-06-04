package main.java.com.benbr.converter

class Conversion {

    private Double constantMultipleBy;
    private Double constantAddTo;
    private String unitFrom;
    private String unitTo;

    Double getConstantMultipleBy() {
        return constantMultipleBy
    }

    Double getConstantAddTo() {
        return constantAddTo
    }

    String getUnitFrom() {
        return unitFrom
    }

    String getUnitTo() {
        return unitTo
    }

    // Builder pattern
    public Conversion to(String unitTo) {
        this.unitTo = unitTo
        return this
    }

    public Conversion from(String unitFrom) {
        this.unitFrom = unitFrom
        return this
    }

    public Conversion constants(Double constantMultiplyBy, Double constantAddTo) {
        if (constantMultiplyBy == 0d) {
            throw new ConvertException("Invalid constant (multiplyBy) - must be a non-zero value")
        }

        this.constantAddTo = constantAddTo
        this.constantMultipleBy = constantMultiplyBy
        return this;
    }

}
