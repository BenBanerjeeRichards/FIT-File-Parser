package main.java.com.benbr.log

class Log {

    /**
     * Whether the log entries should be written to some kind of output or not.
     *
     * Allows for all IO to be disabled where logging is not needed, improving performance.
     */
    boolean enabled;
    private Writer output

    Log(Writer output) {
        enabled = true
        this.output = output
    }

    void log(LogLevel level, String message) {
        if (enabled) {
            output.write("[${level.toString()}] $message\n")
            output.flush()  // Maybe not
        }
    }

    void warn(String message) {
        log(LogLevel.WARN, message)
    }

    void error(String message) {
        log(LogLevel.ERROR, message)
    }


}
