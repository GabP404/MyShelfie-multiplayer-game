package org.myshelfie;

import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientImpl;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.server.ServerImpl;
import org.myshelfie.view.CommandLineInterface;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class App {

    public static void main( String[] args ) {

        TestCLI textUi = new TestCLI();
        textUi.run();
    }
}
