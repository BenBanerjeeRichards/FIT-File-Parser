package com.benbr.profile

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

class CSVProfileParser{

    private File file;

    CSVProfileParser(File file) {
        this.file = file;
    }

    List<Field> getFields() {
        List<CSVRecord> records = CSVFormat.EXCEL.parse(new FileReader(file)).getRecords()
        List<List<Field>> fieldsList = [];
        List<Field> currentFieldList = [];
        List<String> fieldNames = []
        String currentMessageName;
        Boolean titleReached = false;

        // TODO goovy each method doesn't work due to cast to List<Field> being required. Why?

        for (def record: records) {
            if (!titleReached) {
                titleReached = true;
                continue
            }

            def msgType =  record.get(0)
            if (msgType.size() != 0) {
                fieldNames << currentMessageName
                currentMessageName = msgType;
                if (currentFieldList.size() != 0) {
                    fieldsList << currentFieldList;
                }
                currentFieldList = []
                continue;
            }


            if (isBlank(record.asList()[0..11])) {
                continue;
            }

            currentFieldList <<  parseField(record)
        }

    }

    static Field parseField(CSVRecord record) {
        int definitionNum = parseInt(record.get(1))
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
        record
        return new Field(definitionNum, name, type, arraySize != null, arrayType, arraySize, scale, units, offset, refFieldName, refFieldValue, components, bits)
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
