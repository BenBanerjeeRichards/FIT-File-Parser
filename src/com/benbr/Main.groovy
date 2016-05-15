package com.benbr

import com.benbr.parser.FitParser
import com.benbr.profile.CSVProfileParser
import com.benbr.profile.Field
import com.benbr.profile.XLSXProfileParser


class Main {

    public static void main(String[] args) {
        //new FitParser(new File("fit.fit"))
        def profile = new CSVProfileParser(new File("profile.csv")).getFields()
        profile.eachWithIndex{ Map.Entry<String, List<Field>> entry, int i ->
            def prev;
            boolean mentionedSection = false;

            entry.value.each {
                if (it.definitionNumber == null && !mentionedSection && it.name.trim().size() > 0) {
                    mentionedSection = true
                    println "$entry.key: $prev.name"
                }

                if (prev != null) {
                    if (prev.definitionNumber == null && it.definitionNumber != null) {
                        mentionedSection = false;
                    }
                }

                prev = it
            }
        }
    }

}
