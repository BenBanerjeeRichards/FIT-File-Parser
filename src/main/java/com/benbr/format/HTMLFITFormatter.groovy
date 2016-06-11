package main.java.com.benbr.format

import groovy.xml.MarkupBuilder
import main.java.com.benbr.parser.DataMessage

class HTMLFITFormatter implements FITFormatter {
    String formatDataMessage(DataMessage message) {

        // This looks very messy.
        // Example:
        //
        //<div class="fit-record">
        //  <span class="fit-record-name">event</span>
        //  <table class="fit-record-fields">
        //      <tr>
        //          <td class="fit-field-name">alitude</td>
        //          <td class="fit-field-value">14.5</td>
        //		    <td class="fit-field-unit">feet</td>
        //   </tr>
        //</table>
        //</div>

        def writer = new StringWriter()
        new MarkupBuilder(writer).div(class: 'fit-record') {
            span(message.getType(), class: 'fit-record-name')

            table(class: 'fit-record-fields') {
                message.fields.each { field ->
                    tr {
                        td(field.getKey(), class: 'fit-field-name')
                        td(field.getValue(), class: 'fit-field-value')
                        td(message.unitSymbols[field.getKey()], class: 'fit-field-unit')
                    }
                }
            }
        }

        return writer.getBuffer().toString()
    }
}
