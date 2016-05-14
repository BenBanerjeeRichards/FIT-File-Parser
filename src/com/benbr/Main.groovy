package com.benbr

import com.benbr.parser.FitParser
import com.benbr.profile.CSVProfileParser
import com.benbr.profile.XLSXProfileParser


class Main {

    public static void main(String[] args) {
        //def parser = new CSVProfileParser(new File("profile.csv")).getFields()
        new FitParser(new File("fit.fit"))
    }

}
