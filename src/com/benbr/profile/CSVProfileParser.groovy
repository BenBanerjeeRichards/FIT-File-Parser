package com.benbr.profile

import com.benbr.profile.types.ArrayType
import com.benbr.profile.types.ProfileField
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

class CSVProfileParser{

    private File file;

    CSVProfileParser(File file) {
        this.file = file;
    }

    HashMap<String, List<ProfileField>> getFields() {
        List<CSVRecord> records = CSVFormat.EXCEL.parse(new FileReader(file)).getRecords()

        HashMap<String, List<ProfileField>> fields = [:]

        List<ProfileField> currentFieldList = [];
        String currentMessageName = null;
        Boolean titleReached = false;

        for (def record: records) {
            if (!titleReached) {
                titleReached = true;
                continue
            }

            def msgType =  record.get(0)
            if (msgType.size() != 0) {
                if (currentFieldList.size() != 0 && currentMessageName != null) {
                    fields[currentMessageName] = currentFieldList
                }

                currentMessageName = msgType;
                currentFieldList = []
                continue;
            }


            if (isBlank(record.asList()[0..11])) {
                continue;
            }

            ProfileField field =  parseField(record)
            if (!isValidField(field)) continue;
            if (field.getDefinitionNumber() == null) {
                currentFieldList.last().addSubField(field)
            } else {
                currentFieldList << field
            }
        }

        return fields;
    }

    static boolean isValidField(ProfileField field) {
        return !(isBlankField(field) || isTitle(field))
    }

    static boolean isBlankField(ProfileField field) {
        return  (field.getDefinitionNumber() == null && field.getName().equals("")
                && field.getType().equals(""))
    }

    static boolean isTitle(ProfileField field){
        return (field.getDefinitionNumber() == null && field.getName().equals("") && field.getType().size() > 0)
    }

    static ProfileField parseField(CSVRecord record) {
        Integer definitionNum = parseDefinitionNumber(record.get(1))
        String name = record.get(2)
        String type = record.get(3)
        Integer arraySize = parseArrayField(record.get(4))
        ArrayType arrayType = null;
        if (arraySize == -1) {
            arrayType = ArrayType.SIZE_N
        } else if (arraySize != null) {
            arrayType = ArrayType.SIZE_INTEGER
        }

        List<String> components = parseList(record.get(5))
        List<Double> scale = stringListToDoubleList(parseList(record.get(6)))
        double offset = parseDouble(record.get(7))
        def units = record.get(8)
        def bits = stringListToIntList(parseList(record.get(9)))
        def refFieldName = record.get(11)
        def refFieldValue = record.get(12)

        return new ProfileField(definitionNum, name, type, arraySize != null, arrayType, arraySize, scale, units, offset, refFieldName, refFieldValue, components, bits)
    }


    static Integer parseDefinitionNumber(String input) {
        input = input.trim()
        if (input.size() == 0) return null;
        return parseInt(input)
    }

    static Integer parseInt(String input) {
        input = input.trim()
        if (input.size() == 0) return 0;
        return input.toInteger()
    }

    static double parseDouble(String input) {
        input = input.trim()
        if (input.size() == 0) return 0;
        return input.toDouble();
    }

    static List<Integer> stringListToIntList(List<String> list) {
        List<Integer> intList = [];
        for (def item : list) {
            intList << parseInt(item)
        }
        return intList;
    }

    static List<Double> stringListToDoubleList(List<String> list) {
        List<Double> doubleList = [];
        for (def item : list) {
            doubleList << parseDouble(item)
        }
        return doubleList;
    }


    static double parseScale(String text) {
        if (text.trim().size() == 0) return 1f;
        return text.toDouble()
    }


    static List<String> parseList(String text) {
        List<String> components = text.split(",")
        if (components.last().trim().size() == 0) {
            components.remove(components.size() - 1)
        }

        return components;
    }


    static Integer parseArrayField(String text) {
        if (text == null) return null;
        if (text.size() < 3) return null;
        def size = text[1..(text.size() - 2)]
        if (size.equals("N")) {
            return -1;
        } else {
            return parseInt(size)
        }
    }

    static boolean isBlank(List<String> list) {
        for (def item : list) {
            if (item.trim().size() != 0) {
                return false;
            }
        }

        return true;
    }

}
