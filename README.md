# FIT File Parser

 A groovy library for handling FIT files (Work in Progress)

## Features
- [x] Parse FIT files
- [x] Link to global profile, including resolution of dynamic fields
- [X] Type conversion and special value handling (including timestamps)
- [X] Data exports - text, json, CSV
- [ ] Process records - find abnormal data
- [ ] Writing data to FIT files
- [ ] Command line interface


## Usage
**IMPORTANT: This project is a work in progress. As a result, the API shown below is not stable and very liable to change**

The following example iterates over each data message in the FIT file, and then prints out each field in the messages.

```groovy
new FitParser().parse(new File("fit/fit.fit"))each {message ->
    println message.getType()

    message.fields.each { field ->
        println "\t ${field.getKey()} : ${field.getValue()}"
    }
}
```

For more examples, refer to the [quickstart guide](http://benbanerjeerichards.github.io/FIT-File-Parser/quickstart).
