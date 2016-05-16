package com.benbr

import com.benbr.parser.FitParser
import com.benbr.profile.CSVTypeParser

class Main {

    public static void main(String[] args) {
        def parser = new FitParser(new File("fit.fit"))
    }

}
