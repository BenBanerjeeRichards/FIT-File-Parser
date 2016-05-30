# FIT File Parser

 A groovy library for handling FIT files (Work in Progress)

## Features
- [x] Parse FIT files
- [x] Link to global profile, including resolution of dynamic fields
- [ ] Type conversion and special value handling (including timestamps)
- [ ] Data exports - text, XML, json, CSV
- [ ] Process records - find abnormal data
- [ ] Writing data to FIT files

## Introduction

FIT is a binary file format used by various fitness devices to store a range of information, including activity, settings and user goals. 

The most interesting type of FIT file is an activity file. As the name suggests, these store information about a fitness activity, for example a cycle ride or a walk. This includes sensor readings such as speed, distance, and cadence as well as other important pieces of information such the time, device type and average values for the entire activity.


## Messages

The FIT file contains a series of messages. Each message contains several fields, inside of which the actual data is stored. As an example, consider a `file_id` message. Every activity file must have one of these messages at the beginning of the file. It's purpose is to provide some meta information about the FIT file. The example below is from a Garmin Edge 510 activity file.

    file_id
         serial_number : 3905354281
         time_created : 831899179
         manufacturer : 1 (Garmin)
         type : 4 (activity file)
         garmin_product : 1561 (Edge 510)

This message has 5 fields - serial number, time created, manufacturer, type and the product identifier. In the entire file there is only one `file_id` message - the bulk of the file is made up of `record` messages, an example of which is shown below (note that the longitude and latitude values have been redacted).

    record
         timestamp : 831901303 s
         position_lat : <REDACTED> semicircles
         position_long : <REDACTED> semicircles
         distance : 14023.57 m
         altitude : 12.5 m
         speed : 7.211 m/s
         cadence : 70 rpm
         temperature : 14 C

Note that the values are mostly using [_Système International (SI)_](https://en.wikipedia.org/wiki/International_System_of_Units) units. A unit conversion system is currently in development. It will allow the API consumer to build conversion profiles which can then be applied to the fields.

## Usage
**IMPORTANT: This project is a work in progress. As a result, the API shown below is not stable and very liable to change**

The following code example prints all of the messages to standard output in the format show in the above examples.

```groovy
new FitParser().parse(new File("fit/fit.fit")).each { message ->
    println message.getType()
    message.fields.each{field->
        println "\t ${field.getKey()} : ${field.getValue()} ${message.fieldDefinitions[field.getKey()]?.getUnit()}"
    }

    println ""
}
```
