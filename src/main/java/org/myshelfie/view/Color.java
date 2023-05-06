package org.myshelfie.view;

/**
 * Colors
 */
public enum Color {

    BLACK("\033[30m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    LIGHT_GRAY("\033[37m"),
    MAGENTA("\033[35m"),
    CYAN("\033[36m"),

    BG_RED("\033[41m"),
    BG_GREEN("\033[42m"),
    BG_YELLOW("\033[43m"),
    BG_BLUE("\033[44m"),
    BG_MAGENTA("\033[45m"),
    BG_CYAN("\033[46m"),
    BG_BRIGHT_RED("\033[101m"),
    BG_BRIGHT_GREEN("\033[102m"),
    BG_BRIGHT_YELLOW("\033[103m"),
    BG_LIGHT_BLUE("\033[104m"),
    BG_GRAY1("\033[48;2;140;140;140m"),
    BG_GRAY2("\033[48;2;175;175;175m"),
    BG_GRAY3("\033[48;2;220;220;220m"),
    BG_BRIGHT_BLUE("\033[48;2;40;69;186m"),
    BG_GRASS("\033[48;2;93;181;105m"),
    BG_GRASS2("\033[48;2;93;140;105m"),

    BG_DARK_RED("\033[48;2;49;03;07m"),

    BG_WORKER_RED("\033[48;2;214;40;40m"),
    BG_WORKER_BLUE("\033[48;2;50;98;123m"),
    BG_WORKER_ORANGE("\033[48;2;248;144;11m"),

    RESET("\033[0m");

    private final String s;

    Color(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
