import com.benbr.Util
import com.benbr.profile.CSVProfileParser
import com.benbr.profile.CSVTypeParser
import spock.lang.Specification

class UtilSpec extends Specification {

    def "Test that given a base type, the correct size (in bytes) is returned"() {
        expect:
        def profile = new CSVTypeParser(new File("types.csv")).parse()
        Util.typeNameToNumBytes(profile, typeName) == typeSize

        where:
        typeName              |           typeSize
        "enum"                |               1
        "sint8"               |               1
        "uint8"               |               1
        "sint16"              |               2
        "uint16"              |               2
        "sint32"              |               4
        "uint32"              |               4
        "string"              |               -1
        "float32"             |               4
        "float64"             |               8
        "uint8z"              |               1
        "uint16z"             |               2
        "uint32z"             |               4
        "byte"                |               1
    }

    def "Test that given a non base type, the correct size (in bytes) is returned"() {
        expect:
        def profile = new CSVTypeParser(new File("types.csv")).parse()
        Util.typeNameToNumBytes(profile, typeName) == typeSize

        where:
        typeName                        |           typeSize
        "file"                          |               1
        "manufacturer"                  |               2
        "antplus_device_type"           |               1
        "supported_exd_screen_layouts"  |               4
    }


}
