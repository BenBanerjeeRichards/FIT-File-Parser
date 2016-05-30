package com.benbr.convert

import com.benbr.converter.Conversion
import com.benbr.converter.Converter
import com.benbr.converter.Unit
import spock.lang.Specification

class ConverterSpec extends Specification {

    def "Given some converters, conversion works correctly in both directions"() {
        when:
        def c= new Conversion().to(Unit.FAHRENHEIT).from(Unit.CELSIUS).constants(1.8, 32)
        Converter.addConversion(c)

        then:
        Converter.convert(5, Unit.CELSIUS, Unit.FAHRENHEIT) == 41d
        Converter.convert(41, Unit.FAHRENHEIT, Unit.CELSIUS) == 5d
    }

}
