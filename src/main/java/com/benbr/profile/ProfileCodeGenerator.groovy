package main.java.com.benbr.profile

import groovy.text.SimpleTemplateEngine
import main.java.com.benbr.profile.types.ProfileField

class ProfileCodeGenerator {

    private final int PROFILES_PER_BUILDER_FUNC = 32

    public String generateCode(HashMap<String, List<ProfileField>> profile) {
        int numBuilderFunctions = 0
        def functionProfiles;
        def builderFunctions = new StringBuilder();

        profile.eachWithIndex {entry, idx ->
            if (idx % (PROFILES_PER_BUILDER_FUNC - 1) == 0) {
                numBuilderFunctions += 1;
                if (idx > 0) {
                    builderFunctions.append(generateBuilderFunction(functionProfiles.toString(), numBuilderFunctions))
                }

                functionProfiles = new StringBuilder()
            }
            functionProfiles.append("profile << [${generateMessageProfileCode(entry)}]\n")
        }

        File template = new File("Profile.groovy.template");
        def res = new SimpleTemplateEngine().createTemplate(template).make([constructors: generateConstructor(numBuilderFunctions),
                                                                            builders : builderFunctions.toString()])

        return res.toString()
    }

    private static String generateBuilderFunction(String profile, int id) {
        return "\nprivate static _buildSub$id() {\n$profile}\n"
    }

    private static String generateConstructor(int numBuilderFunctions) {
        def sb = new StringBuilder("public Profile() {\n")

        (1..numBuilderFunctions).each {i ->
            sb.append("_buildSub$i()\n")
        }

        sb.append("}")
        return sb.toString()
    }

    private static generateMessageProfileCode(Map.Entry<String, List<ProfileField>> message) {
        StringBuilder sb = new StringBuilder("${message.getKey()} : [")

        message.getValue().eachWithIndex {field, idx ->
            sb.append(field.getInitializationCode())
            if (idx != message.getValue().size() - 1) {
                sb.append(",")
            }
        }

        sb.append("],")
        return sb.toString()
    }


}
