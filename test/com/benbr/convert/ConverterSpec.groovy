package com.benbr.convert

import com.benbr.converter.Conversion
import com.benbr.converter.Converter
import com.benbr.converter.Unit
import spock.lang.Specification

class ConverterSpec extends Specification {

    def "Given some converters, conversion works correctly in both directions"() {
        when:
        def c= new Conversion().to(Unit.FARENHEIGHT).from(Unit.CELCIUS).constants(1.8, 32)
        Converter.addConversion(c)

        then:
        Converter.convert(5, Unit.CELCIUS, Unit.FARENHEIGHT) == 41d
        Converter.convert(41, Unit.FARENHEIGHT, Unit.CELCIUS) == 5d
    }

}
