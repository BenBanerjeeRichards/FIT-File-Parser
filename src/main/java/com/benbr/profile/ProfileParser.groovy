package main.java.com.benbr.profile

import main.java.com.benbr.profile.types.ProfileField


interface ProfileParser {

    List<ProfileField> getFields(File input);

}