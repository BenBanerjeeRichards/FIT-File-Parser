package com.benbr.profile


enum ArrayType {
    SIZE_N, SIZE_INTEGER
}
class Field {

    // Init using map constructor

    private int definitionNumber
    private String name
    private String type

    private Boolean isArray
    private ArrayType arrayType
    private Integer size       // only set when arrayType = SIZE_INTEGER

    private List<Double> scale
    private String unit
    private Double offset
    private String referenceFieldName
    private String referenceFieldValue
    List<String> components
    List<Integer> componentBits



    // There must be a better way to do immutability.
    Field(int definitionNumber, String name, String type) {
        this.definitionNumber = definitionNumber
        this.name = name
        this.type = type

        isArray = false;
        scale = 1
        offset = 0
    }

    Field(Integer definitionNumber, String name, String type, Boolean isArray, ArrayType arrayType, Integer size, List<Double> scale, String unit, Double offset, String referenceFieldName, String referenceFieldValue, List<String> components, List<Integer> componentBits) {
        this.definitionNumber = definitionNumber
        this.name = name
        this.type = type
        this.isArray = isArray
        this.arrayType = arrayType
        this.size = size
        this.scale = scale
        this.unit = unit
        this.offset = offset
        this.referenceFieldName = referenceFieldName
        this.referenceFieldValue = referenceFieldValue
        this.components = components
        this.componentBits = componentBits
    }

    List<Integer> getComponentBits() {
        return componentBits
    }

    boolean getIsArray() {
        return isArray
    }

    ArrayType getArrayType() {
        return arrayType
    }

    int getSize() {
        return size
    }

    List<Double> getScale() {
        return scale
    }

    String getUnit() {
        return unit
    }

    double getOffset() {
        return offset
    }

    int getAccumulate() {
        return accumulate
    }

    String getReferenceFieldName() {
        return referenceFieldName
    }

    String getReferenceFieldValue() {
        return referenceFieldValue
    }

    List<String> getComponents() {
        return components
    }

    int getDefinitionNumber() {
        return definitionNumber
    }

    String getName() {
        return name
    }

    String getType() {
        return type
    }

    @Override
    public String toString() {
        return "Field{" +
                "definitionNumber=" + definitionNumber +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isArray=" + isArray +
                ", arrayType=" + arrayType +
                ", size=" + size +
                ", scale=" + scale +
                ", unit='" + unit + '\'' +
                ", offset=" + offset +
                ", referenceFieldName='" + referenceFieldName + '\'' +
                ", referenceFieldValue='" + referenceFieldValue + '\'' +
                ", components=" + components +
                ", componentBits=" + componentBits +
                '}';
    }
}
