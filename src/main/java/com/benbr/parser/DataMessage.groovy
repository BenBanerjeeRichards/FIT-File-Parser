package main.java.com.benbr.parser

/**
 * Stores information about a data message in the FIT file.
 *
 * <p>A data message contains a number of fields, each of which contains some information. Each field has a name - this
 * is a string which describes the value of the field. Examples are 'heartrate' and 'speed'. In the FIT file, the field
 * name is just a number. The string is found out by searching the global profile (provided and maintained by
 * <a href="https://www.thisisant.com/resources/fit">Dynastream</a></p>
 *
 */
class DataMessage {
    String type;
    /**
     * <p>The field name is the key of the {@code DataMessage#fields} HashMap. The value of this HashMap could either be
     * another map of fields (these are known as sub fields or components) or a value.</p>
     *
     * <p>Sub fields are a map of fields with values that exist as the value of a high level field -
     * {@code HashMap<String, HashMap<String, Object>}. If {@code DataMessage#fields[field]} is a sub field, then
     * {@code DataMessage.hasComponents[field]} is set to true.</p>
     *
     * <p>Values are either a single type (some kind of integer or a string) or an array of elements (each with the
     * same type). If it is an array, then {@code fields[field] instanceof Object[] == true}. </p>
     * **/

    HashMap<String, Object> fields

    /**
     * The unit, as a string (as specified in the FIT global profile), of the corresponding value. For example, time
     * would be `s` and heartrate would be `bpm`.
     *
     * <p> This map is accessed in exactly the same way as {@link DataMessage#fields}. For a  normal field,
     * {@code unitSymbols[field]} and for a sub field {@code unitSymbols[field][subfield]}.</p>
     */
    HashMap<String, Object> unitSymbols;

    /**
     * Stores a boolean value for each entry in {@link DataMessage#fields}, denoting whether the value are components
     * (sub fields) or not. If this value is false, then the value of {@link DataMessage#fields} is just a type or list
     * of elements.
     */
    HashMap<String, Boolean> hasComponents;

    DataMessage() {
        fields = [:]
        unitSymbols = [:]
        hasComponents = [:]
    }

    def propertyMissing(String name, String value) {
        fields[name] = value
    }

    def propertyMissing(String name) {
        return fields[name]
    }


}
