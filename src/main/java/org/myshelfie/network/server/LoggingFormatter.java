package org.myshelfie.network.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for the server logger
 */
public class LoggingFormatter extends Formatter {
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        String dateStr = sdf.format(new Date(record.getMillis()));
        return "(" + dateStr + ") " + record.getLevel() + ": " + record.getMessage() + "\n";
    }
}
