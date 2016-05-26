package com.benbr

import com.benbr.parser.FitParser

class Main {

    public static void main(String[] args) {
        def parser = new FitParser(new File("fit.fit"))
    }

}
