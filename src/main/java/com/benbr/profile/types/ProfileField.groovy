package main.java.com.benbr.profile.types


enum ArrayType {
    SIZE_N, SIZE_INTEGER
}

class ProfileField {

    private Integer definitionNumber
    private String name
    private String type

    private Boolean isArray
    private ArrayType arrayType
    private Integer size       // only set when arrayType = SIZE_INTEGER

    private List<Double> scale
    private String unit
    private Double offset
    private List<String> referenceFieldNames
    private List<String> referenceFieldValue
    private List<String> components
    private List<Integer> componentBits
    private List<Boolean> accumulate;
    private List<ProfileField> subFields;
    List<String> subFieldUnits;


    ProfileField(Integer definitionNumber, String name, String type) {
        this.definitionNumber = definitionNumber
        this.name = name
        this.type = type

        isArray = false;
        scale = [1]
        offset = 0
        this.subFields = []
    }

    // There must be a better way to do immutability.
    ProfileField(Integer definitionNumber, String name, String type, Boolean isArray, ArrayType arrayType, Integer size, List<Double> scale, List<String> unit, Double offset, List<String> referenceFieldName, List<String> referenceFieldValue, List<String> components, List<Integer> componentBits, List<Boolean> accumulate) {
        this.definitionNumber = definitionNumber
        this.definitionNumber = definitionNumber
        this.name = name
        this.type = type
        this.isArray = isArray
        this.arrayType = arrayType
        this.size = size
        this.scale = scale
        this.offset = offset
        this.referenceFieldNames = referenceFieldName
        this.referenceFieldValue = referenceFieldValue
        this.components = components
        this.componentBits = componentBits
        this.subFields = []
        this.accumulate = accumulate
        this.subFieldUnits = []

        if (unit.size() == 1) {
            this.unit = unit[0]
            this.subFieldUnits = null
        } else {
            // If there are subfields, the parent field has no unit
            this.unit = null
            this.subFieldUnits = unit;
        }
    }

    boolean isDynamicField() {
        return subFields.size() > 0
    }


    List<Integer> getComponentBits() {
        return componentBits
    }

    boolean isArray() {
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

    List<String> getReferenceFieldName() {
        return referenceFieldNames
    }

    List<String> getReferenceFieldValue() {
        return referenceFieldValue
    }

    List<String> getComponents() {
        return components
    }

    Integer getDefinitionNumber() {
        return definitionNumber
    }

    String getName() {
        return name
    }

    String getType() {
        return type
    }

    List<ProfileField> getSubFields() {
        return subFields
    }

    public setUnit(String unit) {
        this.unit = unit
    }

    ProfileField addSubField(ProfileField field) {
        if (field.getUnit() == null || field.getUnit().size() == 0 && this.subFieldUnits != null) {
            if (subFields.size() < this.subFieldUnits.size()) {
                field.setUnit(this.subFieldUnits?.get(subFields.size()))
            }
        }

        this.subFields << field
        return this
    }

    List<Boolean> getAccumulate() {
        return accumulate
    }


    public String getInitializationCode() {
        String arrayTypeString = (arrayType == null) ? (null) : ("ArrayType.${arrayType}")
        def unitCode = subFieldUnits ?: [unit];
        StringBuilder code = new StringBuilder("new ProfileField($definitionNumber, \"$name\", \"$type\", ${isArray}, $arrayTypeString, $size, $scale, ${stringifyList(unitCode)}, $offset, ${stringifyList(referenceFieldNames)}, ${stringifyList(referenceFieldValue)}, ${stringifyList(components)}, $componentBits, $accumulate)")
        subFields.each { sf ->
            code.append(".addSubField(${sf.getInitializationCode()})")
        }

        return code.toString()
    }

    private static String stringifyList(List<String> list) {
        StringBuilder sb = new StringBuilder("[")
        list.eachWithIndex { it, idx ->
            String comma = (idx == 0) ? "" : ",";
            sb.append("$comma \"$it\"")
        }

        return sb.append("]").toString()
    }

    public boolean isComponent() {
        return components?.size() > 0 && isArray
    }

    public boolean isList() {
        return (components == null || components?.size() == 0) && isArray
    }


    @Override
    public String toString() {
        return "ProfileField{" +
                "definitionNumber=" + definitionNumber +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isArray=" + isArray +
                ", arrayType=" + arrayType +
                ", size=" + size +
                ", scale=" + scale +
                ", unit='" + unit + '\'' +
                ", offset=" + offset +
                ", referenceFieldNames='" + referenceFieldNames + '\'' +
                ", referenceFieldValue='" + referenceFieldValue + '\'' +
                ", components=" + components +
                ", componentBits=" + componentBits +
                '}';
    }
}
