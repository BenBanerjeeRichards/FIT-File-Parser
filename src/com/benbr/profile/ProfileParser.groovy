package com.benbr.profile

import com.benbr.profile.types.Field


interface ProfileParser {

    List<Field> getFields(File input);

}