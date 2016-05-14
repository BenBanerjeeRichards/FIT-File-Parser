package com.benbr

import com.benbr.parser.FitParser
import com.benbr.profile.CSVProfileParser
import com.benbr.profile.XLSXProfileParser


class Main {

    public static void main(String[] args) {
        new FitParser(new File("fit.fit"))
    }

}
