package com.benbr.profile

import com.benbr.profile.types.ProfileField


interface ProfileParser {

    List<ProfileField> getFields(File input);

}