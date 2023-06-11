package org.myshelfie.network.server;

import java.util.logging.LogRecord;

public class LoggingFormatterWithColor extends LoggingFormatter {
    // Define ANSI escape codes
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_GRAY = "\u001B[90m";

    @Override
    public String format(LogRecord record) {
        String level = record.getLevel().toString();
        String color = switch (level) {
            case "SEVERE" -> ANSI_RED;
            case "WARNING" -> ANSI_YELLOW;
            case "FINE" -> ANSI_GRAY;
            default -> ANSI_RESET;
        };

        return color + super.format(record) + ANSI_RESET;
    }
}
