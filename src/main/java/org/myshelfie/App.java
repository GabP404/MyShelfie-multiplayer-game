package org.myshelfie;

import org.myshelfie.view.CommandLineInterface;

public class App {
    public static void main( String[] args ) {

        CommandLineInterface textUi = new CommandLineInterface("loremIpsum");
        textUi.run();
    }
}
