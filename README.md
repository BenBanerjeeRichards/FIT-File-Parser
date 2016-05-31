# FIT File Parser

 A groovy library for handling FIT files (Work in Progress)

## Features
- [x] Parse FIT files
- [x] Link to global profile, including resolution of dynamic fields
- [X] Type conversion and special value handling (including timestamps)
- [ ] Data exports - text, XML, json, CSV
- [ ] Process records - find abnormal data
- [ ] Writing data to FIT files

## Introduction

FIT is a binary file format used by various fitness devices to store a range of information, including activity, settings and user goals. 

The most interesting type of FIT file is an activity file. As the name suggests, they store information about a fitness activity, for example a cycle ride or a walk. This includes sensor readings such as speed, distance, and cadence as well as other important pieces of information such the time, device type and average values for the entire activity.


## Messages

The FIT file contains a series of messages. Each message contains several fields, inside of which the actual data is stored. As an example, consider a `file_id` message. Every activity file must have one of these messages at the beginning of the file. Its purpose is to provide some meta information about the FIT file. The example below is from a Garmin Edge 510 activity file.

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

Note that the parser includes a build in unit conversion system which allows rules to be applied to each message.

## Usage
**IMPORTANT: This project is a work in progress. As a result, the API shown below is not stable and very liable to change**

Below, some examples are given demonstrating what the library is capable of doing.

### Iterating over data messages and their fields

```groovy
def parser = new FitParser()
List<DataMessage> messages = parser.parse(new File("fit/fit.fit"))

// Iterate over each of the data messages in the FIT file
messages.each {message ->
    // Print the data message name (for example, 'record' or 'file_id')
    println message.getType()

    // Iterate over the fields
    message.fields.each { field ->
        // Print the name and value of each field in the data message `message`
        println "\t ${field.getKey()} : ${field.getValue()}"
    }
}
```

1. A new FitParser instance is created. Being the scenes, this processes the entire FIT profile.
2. The parse method is called to parse a fit file. It returns a list of data messages (see readme section on Messages).
3. The data messages are iterated over. The message name (as defined in the FIT profile) is printed to standard output (for example, 'record' or 'file_id').
4. The fields in the data message, which are simply a key/value association stored in a HashMap, are iterated over and printed out to the console.

The output of the above code when run on a test FIT file is as follows.

```text
file_id
	serial_number : 390765281
	time_created : 831899179
	manufacturer : 1
	type : 4
	garmin_product : 1561
file_creator
	software_version : 510
	hardware_version : 255
event
	timestamp : 831899178
	event : 0
	event_type : 0
	event_group : 0
	timer_trigger : 0
            ...
(several thousant more lines)
```


### Printing out the unit symbol

If you take a closer look at the [DataMessage class](https://github.com/BenBanerjeeRichards/FIT-File-Parser/blob/master/src/main/java/com/benbr/parser/DataMessage.groovy), then you will notice that there are two HashMaps: `fields` that store all of the field names and values, and `unitSymbols` which store the field names and symbols.

The key for both of the HashMaps is the field name (for example, `heartrate` or `serial_number`). The above example can be modified to print out the unit symbol after every field.

``` groovy
new FitParser().parse("fit/fit.fit").each {message ->
    message.fields.each { field ->
        // Look up the unit symbol in the hashmap
        String unitSymbol = message.unitSymbols[field.getKey()]
        println "\t ${field.getKey()} : ${field.getValue()} (${unitSymbol})"
    }
}
```

Now, the output includes the symbol for each unit. The `file_id`, `file_creater` and `event` messages shown above do not contain fields that have units, but later on in the file there are a number of `record` messages which contain sensor information.

```text
record
	timestamp : 831901185 (s)
	position_lat : <REDACTED> (semicircles)
	position_long : <REDACTED> (semicircles)
	distance : 13060.15 (m)
	altitude : 67.6 (m)
	speed : 8.007 (m/s)
	cadence : 89 (rpm)
	temperature : 15 (C)
```

### Unit Conversion

The previous example demonstrated that some of the default units are not ideal, so it makes sense to process them to more useful forms. This library contains tools to transform the data, the most useful of which is _Unit Conversion_.

To apply a unit conversion to the data messages, you must first define some rules to lay out exactly what you want to happen - for example, what units to convert to which. This library calls these rules a _Conversion Policy_

There are two different types of rules in the conversion policy:
 
 1. Unit Conversion
 2. Field Conversion
 
A unit conversion converts every field with one unit to a different unit. This allows you to, for example, convert all fields with the unit degrees celsius to degrees fahrenheit.

A field conversion allows you to convert every field with a specific name to another type. For example, you could convert all altitudes to feet and at the same time leave distance in metres. **The field conversion policy always has precedence over a unit conversion policy**.

The policies are stored in HashMaps as demonstrated below.

 ```groovy
 HashMap<Unit, Unit> unitPolicy = [
         (Unit.SEMICIRCLE) : Unit.DEGREE,
         (Unit.METRE) : Unit.MILE,
 ]

 HashMap<String, Unit> fieldPolicy = [
         altitude: Unit.FEET
 ]
 ```

This creates a unit policy which will convert all semicircle units to degrees and metres to miles. It also specifies a field policy that converts fields named 'altitude' to feet (the converter will automatically detect the original unit of the altitude field).

The following code demonstrates the conversion in action.


```groovy
def converter = new MessageConverter(new ConversionPolicy(fieldPolicy, unitPolicy))

new FitParser().parse(new File("fit/fit.fit")).each { message ->
    println message.getType()
    DataMessage converted = converter.convertMessage(message)

    converted.fields.each{ field ->
        println "\t ${field.getKey()}: ${field.getValue()} (${converted.unitSymbols[field.getKey()]})"
    }
}
```

1. The policies defined in the previous code listing are used to create a new `MessageConverter` object - the type responsible for transforming the data.
2. A _new_ message type is created and given the name `converted`. It is identical to `message` other than the fields that have been transformed.
3. The fields are printed out. Note that the `unitSymbols` HashMap has been updated with the new units as appropriate.

Note that any fields that have not been converted remain exactly the same as before. Also, the `convertMessage` method does not mutate the original message in any way. If you want to, you can print out both the original and transformed `DataMessgae` objects to compare them.

The above code has the following output.

```text
record
	 timestamp: 831901185 (s)
	 position_lat: <REDACTED> (deg)
	 position_long: <REDACTED> (deg)
	 distance: 8.1151854055 (miles)
	 altitude: 221.7847840000001 (feet)
	 speed: 8.007 (m/s)
	 cadence: 89 (rpm)
	 temperature: 15 (C)
```

Notice that the field conversion policy had precedence over the unit conversion policy, as altitude has the unit feet instead of miles.

