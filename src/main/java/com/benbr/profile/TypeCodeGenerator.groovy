package main.java.com.benbr.profile

import groovy.text.SimpleTemplateEngine
import main.java.com.benbr.profile.types.EnumerationType

class TypeCodeGenerator {

    public String generateCode(HashMap<String, EnumerationType> types) {
        def sb = new StringBuilder()

        types.eachWithIndex{ key, value, idx ->
            if (idx != 0) {
                sb.append(",")
            }

            sb.append("$key : ${value.getInitializationCode()}\n")
        }

        def typesCode = sb.toString()
        def template = new File("src/main/resources/Type.groovy.template")
        def res = new SimpleTemplateEngine().createTemplate(template).make([types: typesCode])
        return res.toString()
    }

}
