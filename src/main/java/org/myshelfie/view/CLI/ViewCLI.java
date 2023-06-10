package org.myshelfie.view.CLI;

import org.myshelfie.controller.GameController;
import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.messages.gameMessages.ImmutableBoard;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;
import org.myshelfie.view.View;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.myshelfie.view.CLI.Color.*;
import static org.myshelfie.view.CLI.Color.BLUE;

public class ViewCLI implements View {
    private static final int frameOffsetX = 10;
    private static final int frameOffsetY = 4;
    private static final int titleOffsetX = 20;
    private static final int titleOffsetY = 8;
    private static final int helpOffsetX = 15;
    private static final int helpOffsetY = 10;
    private static final int boardOffsetX = 10;
    private static final int boardOffsetY = 15;
    private static final int bookshelfOffsetX = 38;
    private static final int bookshelfOffsetY = 15;
    private static final int commonGoalOffsetX = 5;
    private static final int commonGoalOffsetY = 2;
    private static final int personalGoalOffsetX = 110;
    private static final int personalGoalOffsetY = 15;
    private static final int bookshelvesDistance = 18;
    public static final int inputOffsetX = 0;
    public static final int inputOffsetY = 29;

    public static final int rankingOffsetX = 10;
    public static final int rankingOffsetY = 10;

    private static final int errorOffsetX = 3;
    private static final int errorOffsetY = 31;
    private Client client = null;

    private List<LocatedTile> selectedTiles;    // tiles selected from the board
    private int selectedColumn;
    private int selectedHandIndex;
    private String nickname;

    private GameView game;

    private boolean reconnecting = false;
    private boolean showingHelp = false;

    private Scanner scanner = new Scanner(System.in);

    private List<GameController.GameDefinition> availableGames;

    //Thread used to ask the nickname
    Thread threadNick = new Thread(() -> {
        clear();
        try {
            while (true) {
                printTitle();
                print("Insert a Nickname ", 0, 20, false);
                setCursor(0,22);
                nickname = scanner.nextLine();
                if(validateString(nickname) && nickname.length() < 15)
                {
                    print("CONNECTING TO SERVER WITH NICKNAME "+ nickname,0,25,false);
                    this.client.eventManager.notify(UserInputEvent.NICKNAME, nickname);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10000);
                    } catch ( InterruptedException e) {
                        Thread.currentThread().interrupt(); // restore interrupted status
                        break;
                    }
                    //send information to server
                    clear();
                    print("Try again ", 0, 25, false);
                }
                else{
                    clear();
                    print("the nickname cannot contain a symbols, spaces, and must be shorter than 15 characters ", 0, 25, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    //game creation/lobby selection
    Thread threadChoice = new Thread(() -> {
        clear();
        String choice;
        String gameName;
        do{
            choice = null;
            gameName = null;
            do {
                clear();
                printTitle();
                print("Do you want to create or join a game? [create/join]", 0, 20, false);
                setCursor(0, 22);
                choice = scanner.nextLine();
            }while(!choice.equalsIgnoreCase("create") && !choice.equalsIgnoreCase("join"));

            if(choice.equalsIgnoreCase("create"))
            {
                try {
                    clear();
                    do {
                        printTitle();
                        print("Insert a Game name, player number and --simple at the end for simplified version   |   --back to go back", 0, 20, false);
                        setCursor(0,22);
                        gameName = scanner.nextLine();
                        if (!gameName.equalsIgnoreCase("--back")) {

                            String[] parts = gameName.split(" ");
                            boolean hasSimpleRules = false;
                            if(parts.length >= 2)
                            {
                                try{
                                    int playerNum = Integer.parseInt(parts[1]);
                                    if(playerNum >= 2 && playerNum <= 4)
                                    {
                                        if(validateString(parts[0]))
                                        {
                                            if(parts.length > 2 && parts[2].equalsIgnoreCase("--simple"))
                                                hasSimpleRules = true;
                                            print("Creating game: "+ parts[0],0,25,true);
                                            this.client.eventManager.notify(UserInputEvent.CREATE_GAME, parts[0], playerNum, hasSimpleRules);
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(10000);
                                            } catch ( InterruptedException e) {
                                                Thread.currentThread().interrupt(); // restore interrupted status
                                                break;
                                            }
                                            //send information to server
                                            clear();
                                            print("Try again ", 0, 25, false);
                                        }
                                        else{
                                            clear();
                                            print("game name is not valid, it cannot contain symbols ", 0, 25, false);
                                        }
                                    }
                                    else {
                                        clear();
                                        print("number of players must be between 2 and 4 ", 0, 25, false);
                                    }
                                }
                                catch(NumberFormatException nfe) {
                                    clear();
                                    print("number of players typed is not a number ", 0, 25, false);
                                }
                            }
                            else{
                                clear();
                                print("Not enough arguments ", 0, 25, false);
                            }

                        }
                    }while (!gameName.equalsIgnoreCase("--back"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(choice.equals("join"))
            {
                try {
                    clear();
                     do{
                        printTitle();
                        print("Insert a Game name  |  --back to go back  |  --refresh to refresh lobbies", 0, 20, false);
                        print("Available games: ", 90, 20, false);
                        for (int i=0; i<this.availableGames.size(); i++) {
                            print(" -> " + this.availableGames.get(i).getGameName() + " " + this.availableGames.get(i).getNicknames().size() + "/" + this.availableGames.get(i).getMaxPlayers(), 90, 22+i, false);
                        }
                        setCursor(0,22);
                        gameName = scanner.nextLine();
                        if (!gameName.equalsIgnoreCase("--back")) {

                            //String[] parts = gameName.split(" ");
                            if (gameName.equalsIgnoreCase("--refresh")) {
                                this.client.eventManager.notify(UserInputEvent.REFRESH_AVAILABLE_GAMES);
                                Thread.sleep(250);
                                clear();
                            } else {
                                if(isInLobbyList(gameName))
                                {
                                    print("joining game: "+ gameName,0,25,    true);
                                    this.client.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(10000);
                                    } catch ( InterruptedException e) {
                                        Thread.currentThread().interrupt(); // restore interrupted status
                                        break;
                                    }
                                    clear();
                                    print("Try again ", 0, 25, false);
                                }
                                else
                                {
                                    clear();
                                    print("Game not found, please pick a game from the list",0,25,false);
                                }
                            }

                        }
                    }while (!gameName.equalsIgnoreCase("--back"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Wrong choice");
            }

        }while(Objects.requireNonNull(gameName).equalsIgnoreCase("--back"));
    });

    //checks if the game name is in the list of all lobbies
    private boolean isInLobbyList(String gameName)
    {
        for (int i=0; i<this.availableGames.size(); i++) {
            if (this.availableGames.get(i).getGameName().equalsIgnoreCase(gameName))
                return true;
        }
        return false;
    }

    private boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public ViewCLI(Client client) {
        selectedColumn = -1;
        selectedHandIndex = -1;
        selectedTiles = new ArrayList<>();
        availableGames = new ArrayList<>();
        this.client = client;
    }

    //handle messages from server
    @Override
    public void update(GameView msg, GameEvent ev) {
        // End the threads to create/join a game, in case the gameView was received after a reconnection
        this.endLobbyPhase();

        game = msg;

        //if the game state is END_GAME print the ranking
        if(game.getModelState().equals(ModelState.END_GAME))
        {
            clear();
            printEndGameScreen();
            setCursor(inputOffsetX, inputOffsetY);
        }
        else    //else print the new gameView
        {
            if(!showingHelp)//if a player is watching the help box, don't print the gameView yet
            {
                switch (ev)
                {
                    case ERROR:
                        if(game.getErrorState(nickname) != null)
                            printError(game.getErrorState(nickname));
                        break;
                    case BOARD_UPDATE:
                        selectedTiles.clear();
                        break;
                }
                clear();
                printAll();
                setCursor(inputOffsetX, inputOffsetY);
            }
        }

    }

    @Override
    public void run() {
        //start the thread that asks the user for the nickname
        threadNick.start();
        try {
            threadNick.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //start the thread that asks the user if he wants to create or join a game
        if (!reconnecting) {
            threadChoice.start();
            try {
                threadChoice.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //start the thread that handles the user input for the entire game
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    clearRow(inputOffsetX, inputOffsetY);
                    setCursor(inputOffsetX, inputOffsetY);
                    String userCommand = scanner.nextLine();
                    if(this.game != null)
                        parseInput(userCommand);
                    else
                        printError("GAME HAS NOT STARTED YET, WAIT FOR OTHER PLAYERS");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    @Override
    public void endLoginPhase()
    {
        if(threadNick.isAlive())
            threadNick.interrupt();
    }

    @Override
    public void endLobbyPhase()
    {
        if(threadChoice.isAlive())
            threadChoice.interrupt();
    }


    @Override
    public String getGameName() {
        if (game == null)
            return null;
        return game.getGameName();
    }

    @Override
    public GameView getGameView() {
        return game;
    }


    public static synchronized void print(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    public synchronized static void print(String string, int x, int y, boolean toClean) {
        if (toClean) {
            clearRow(x, y);
        }

        print(RESET);

        setCursor(x, y);

        print(string + RESET);

        resetCursor();
    }

    public static void clearRow(int x, int y) {
        setCursor(x, y);

        print("\033[K");
    }

    public static void setCursor(int x, int y) {
        print(String.format("\033[%d;%dH", y, x));
    }

    public static void resetCursor() {
        setCursor(0, 0);
    }

    /**
     * Used to clear console before using it
     */
    public static void firstClear() {
        print("\033[H\033[3J\033[2J");
    }

    /**
     * Used to clear console while game is running
     */
    public static void clear() {
        for (int i = 0; i < 150; i++) {
            clearRow(0, i);
        }
    }

    public static void printTitle()
    {
        String exteriorLine = BG_LIGHT_BROWN + "                                                                                                "+ RESET;
        String exteriorLine2 = " " + BG_TITLE_FRAME + "                                                                                              " + RESET;
        String middleLine = "  " + BG_TITLE_FRAME + " "+ BG_TITLE_FILL +"                                                                                         "+ BG_TITLE_FRAME +" " + RESET;
        print(exteriorLine, frameOffsetX, frameOffsetY, false);
        print(exteriorLine2, frameOffsetX, frameOffsetY + 1, false);
        for(int i = 0; i < 10; i++){
            print(middleLine, frameOffsetX, frameOffsetY + 2 + i, false);
        }
        print(exteriorLine2, frameOffsetX, frameOffsetY + 12, false);
        print(exteriorLine, frameOffsetX, frameOffsetY + 13, false);

        print(BG_TITLE_FILL + YELLOW.toString() +"███╗   ███╗██╗   ██╗    ███████╗██╗  ██╗███████╗██╗     ███████╗██╗███████╗", titleOffsetX, titleOffsetY, false);
        print(BG_TITLE_FILL + YELLOW.toString() +"████╗ ████║╚██╗ ██╔╝    ██╔════╝██║  ██║██╔════╝██║     ██╔════╝██║██╔════╝", titleOffsetX, titleOffsetY + 1, false);
        print(BG_TITLE_FILL + YELLOW.toString() +"██╔████╔██║ ╚████╔╝     ███████╗███████║█████╗  ██║     █████╗  ██║█████╗  ", titleOffsetX, titleOffsetY + 2, false);
        print(BG_TITLE_FILL + YELLOW.toString() +"██║╚██╔╝██║  ╚██╔╝      ╚════██║██╔══██║██╔══╝  ██║     ██╔══╝  ██║██╔══╝  ", titleOffsetX, titleOffsetY + 3, false);
        print(BG_TITLE_FILL + YELLOW.toString() +"██║ ╚═╝ ██║   ██║       ███████║██║  ██║███████╗███████╗██║     ██║███████╗", titleOffsetX, titleOffsetY + 4, false);
        print(BG_TITLE_FILL + YELLOW.toString() +"╚═╝     ╚═╝   ╚═╝       ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚═╝╚══════╝", titleOffsetX, titleOffsetY + 5, false);

    }

    public static void printWin() {
        String c = BG_BRIGHT_BLUE.toString() + BLUE.toString();

        print(c + "                                                           ", 6, 9, false);
        print(c + "  ██╗   ██╗██╗ ██████╗████████╗ ██████╗ ██████╗ ██╗   ██╗  ", 6, 10, false);
        print(c + "  ██║   ██║██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗╚██╗ ██╔╝  ", 6, 11, false);
        print(c + "  ██║   ██║██║██║        ██║   ██║   ██║██████╔╝ ╚████╔╝   ", 6, 12, false);
        print(c + "  ╚██╗ ██╔╝██║██║        ██║   ██║   ██║██╔══██╗  ╚██╔╝    ", 6, 13, false);
        print(c + "   ╚████╔╝ ██║╚██████╗   ██║   ╚██████╔╝██║  ██║   ██║     ", 6, 14, false);
        print(c + "    ╚═══╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝     ", 6, 15, false);
        print(c + "                                                           ", 6, 16, false);
    }

    public void parseInput(String s) {

        String[] parts = s.split(" ");
        //if there are no arguments return
        if (parts.length < 1) {
            printError("NOT ENOUGH ARGUMENTS");
            return;
        }

        //
        switch (parts[0]) {
            case "exit" -> {
                System.exit(0);
                return;
            }
            case "play" -> {
                //TODO: TEST THIS OPTION
                if(game.getModelState().equals(ModelState.END_GAME))
                {
                    clear();
                    threadChoice.run();
                }
                //threadChoice.run();
                return;
            }
            case "help", "h" -> {
                //print possible commands
                printHelp();
                return;
            }
            default -> {
            }
        }

        //if it's not the player turn return
        if(!game.getCurrPlayer().getNickname().equals(nickname))
        {
            printError("IT'S NOT YOUR TURN");
            return;
        }



        switch (parts[0]) {
            case "select", "s":
                if (parts.length < 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                switch (parts[1]) {
                    case "tile", "t":
                        if (parts.length != 4) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        try {
                            int r = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);
                            if (!selectTile(r, c))
                                return;
                        } catch (NumberFormatException nfe) {
                            printError("ROW OR COLUMN NUMBERS ARE NOT CORRECT");
                            return;
                        }
                        printBoard();
                        break;
                    case "column", "c":
                        if (parts.length != 3) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        int c = Integer.parseInt(parts[2]);
                        if (!selectColumn(c))
                            return;
                        break;
                }
                break;
            case "deselect", "d":
                if (parts.length < 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                switch (parts[1]) {
                    case "tile", "t":
                        if (parts.length != 4) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        try {
                            int r = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);
                            if (!deselectTile(r, c))
                                return;
                        } catch (NumberFormatException nfe) {
                            printError("ROW OR COLUMN NUMBERS ARE NOT CORRECT");
                            return;
                        }
                        printBoard();
                        break;
                    case "column", "c":
                        //not sure if player should deselect column..
                        break;
                }
                break;
            case "pick", "p":
                if (parts.length != 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                int c = Integer.parseInt(parts[1]);
                if (!pickTileFromHand(c))
                    return;
                break;
            case "confirm", "c":
                if(selectedTiles.isEmpty())
                {
                    printError("NO TILES SELECTED");
                    return;
                }
                confirmSelection();
                break;
            default:
                printError("COMMAND DOES NOT EXIST");
                return;
        }
        //printAll();
        //clearRow(0, errorOffsetY);
    }

    private boolean selectTile(int r, int c) {
        if(!tileExists(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                printError("TILE ALREADY SELECTED");
                return false;
            }
        }
        //TODO: think on what really is the necessary to save
        selectedTiles.add(new LocatedTile(null, r, c));
        return true;
    }

    private boolean deselectTile(int r, int c) {
        if(!tileExists(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                selectedTiles.remove(t);
                return true;
            }
        }
        printError("TILE IS NOT SELECTED");
        return false;
    }

    private boolean tileExists(int r, int c)
    {
        if (r < 0 || r >= Board.DIMBOARD || c < 0 || c >= Board.DIMBOARD) {
            printError("ROW OR COLUMN ARE NOT VALID NUMBERS");
            return false;
        }
        if (game.getBoard().getTile(r, c) == null) {
            printError("THERE IS NO TILE IN POSITION: " + r + " " + c);
            return false;
        }
        return true;
    }

    private void confirmSelection()
    {
        this.client.eventManager.notify(UserInputEvent.SELECTED_TILES, selectedTiles);
    }

    private boolean selectColumn(int c)
    {
        if(c < 0 || c>= Bookshelf.NUMCOLUMNS)
        {
            printError("COLUMN NUMBER IS NOT VALID");
            return false;
        }
        selectedColumn = c;
        this.client.eventManager.notify(UserInputEvent.SELECTED_BOOKSHELF_COLUMN, selectedColumn);
        return true;
    }

    private boolean pickTileFromHand(int index)
    {
        if(index<0 || index >= game.getPlayers().get(myPlayerIndex()).getTilesPicked().size())
        {
            printError("INDEX OF CARD IN HAND IS NOT VALID");
            return false;
        }
        selectedHandIndex = index;
        this.client.eventManager.notify(UserInputEvent.SELECTED_HAND_TILE, selectedHandIndex);
        return true;

    }

    //prints the end game score of all players
    public void printEndGameScreen()
    {
        print("               CommonGoal          PersonalGoal          Bookshelf          End game          Total ", rankingOffsetX, rankingOffsetY - 1, false);
        print("Nickname         points               points               points             token           points", rankingOffsetX, rankingOffsetY, false);

        int playerNum = 1;
        String playerRowPoints = "";

        //sorts the players by points with the highest first
        game.getPlayers().sort((p1, p2) -> {
            try {
                return p2.getTotalPoints() - p1.getTotalPoints();
            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            }
        });

        //cycles through all players and prints their nickname and points
        for(ImmutablePlayer p: game.getPlayers())
        {
            //if the player is the current player, print it in cyan
            playerRowPoints = "";
            if(p.getNickname().equals(nickname))
                playerRowPoints = CYAN.toString();

            try {
                playerRowPoints += p.getNickname() + RESET ;
                for(int i = 0; i < 15 - p.getNickname().length(); i++)
                    playerRowPoints += " ";
                playerRowPoints += "    " + p.getPointsScoringTokens();
                playerRowPoints += "                    " + p.getPersonalGoalPoints();
                playerRowPoints += "                    " + p.getBookshelfPoints();
                if(p.getHasFinalToken())
                    playerRowPoints += GREEN.toString() + "               ■";
                else
                    playerRowPoints += "                  ■";
                playerRowPoints += "               " + p.getTotalPoints();


            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            }

            print(playerRowPoints, rankingOffsetX, rankingOffsetY + 1 + (playerNum * 3), false);
            try {
                if(p.getTotalPoints() == game.getPlayers().get(0).getTotalPoints())
                {
                    print(YELLOW + "|\\/\\/|\n" ,rankingOffsetX + 105, rankingOffsetY + (playerNum * 3), false);
                    print(YELLOW + "|____|", rankingOffsetX + 105, rankingOffsetY + 1 + (playerNum * 3), false);
                }
            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            }

            playerNum++;
        }
        print("Type [exit/play] to continue", 0, 1, false);

    }

    public void printHelp()
    {
        showingHelp = true;
        clearRow(0,inputOffsetY);

        print("╔═════════════════════════════════════════════════════════════════════════════════╗", helpOffsetX, helpOffsetY, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 1, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 2, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 3, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 4, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 5, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 6, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 7, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 8, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 9, false);
        print("║                                                                                 ║", helpOffsetX, helpOffsetY + 10, false);
        print("╚═════════════════════════════════════════════════════════════════════════════════╝", helpOffsetX, helpOffsetY + 11, false);


        print("COMMANDS:                                                                       ", helpOffsetX + 1, helpOffsetY + 1, false);
        print("select tile [row] [column]   | s t [row] [column] - select tile from board      ", helpOffsetX + 1, helpOffsetY + 2, false);
        print("deselect tile [row] [column] | d s [row] [column] - deselect tile from board    ", helpOffsetX + 1, helpOffsetY + 3, false);//76
        print("confirm                      | c                  - confirm selection           ", helpOffsetX + 1, helpOffsetY + 4, false);
        print("select column [column]       | s c [column]       - select column from bookshelf", helpOffsetX + 1, helpOffsetY + 5, false);
        print("pick [index]                 | p [index]          - pick tile from hand         ", helpOffsetX + 1, helpOffsetY + 6, false);
        print("help                         | h                  - print this help             ", helpOffsetX + 1, helpOffsetY + 7, false);
        print("exit                                              - exit game                   ", helpOffsetX + 1, helpOffsetY + 8, false);
        print("                                                                                ", helpOffsetX + 1, helpOffsetY + 9, false);
        print("PRESS ENTER TO CONTINUE                                                         ", helpOffsetX + 1, helpOffsetY + 10, false);

        setCursor(inputOffsetX,inputOffsetY);
        scanner.nextLine();

        clear();
        printAll();
        showingHelp = false;
    }

    public void printAll()
    {
        for(int i = 0; i < errorOffsetY-2; i++)
            clearRow(0,i);
        printCommonGoals();
        printBoard();
        printAllBookshelves();
        printPoints();
        printPersonalGoal();
        if(game.getCurrPlayer().getNickname().equals(nickname))
            print(MAGENTA + "È IL TUO TURNO!",boardOffsetX, boardOffsetY-4, false);
    }

    public void printBoard()
    {
        print("BOARD: ",boardOffsetX, boardOffsetY-2,false);
        print(BG_LIGHT_CYAN + "                      ",boardOffsetX - 2, boardOffsetY-1,false);
        for(int i = 0; i<Board.DIMBOARD; i++)
        {
            for(int j = 0; j<Board.DIMBOARD; j++)
            {
                if(j == 0)
                    print(BG_LIGHT_CYAN.toString() + i + " ", boardOffsetX+j-2,boardOffsetY+i,false);

                if(ImmutableBoard.getMaskItem(i,j) > game.getPlayers().size())
                    print(BG_LIGHT_CYAN + "  ", boardOffsetX+(j*2),boardOffsetY+i,false);
                else
                {
                    if(game.getBoard().getTile(i, j) != null)
                    {
                        boolean selected = false;
                        for(LocatedTile t : selectedTiles)
                        {
                            if(t.getRow() == i && t.getCol() == j)
                            {
                                selected = true;
                            }
                        }

                        String c;
                        c = BG_DARK_GRAY + getColorFromTile(game.getBoard().getTile(i, j));

                        if(selected)
                            c = c  + "█ ";
                        else
                            c = c + "■ ";

                        print(c, boardOffsetX+(j*2),boardOffsetY+i,false);
                    }
                    else
                    {
                        print(BG_DARK_GRAY + "  ", boardOffsetX+(j*2), boardOffsetY+i, false);
                    }
                }
            }
            print(BG_LIGHT_CYAN + "  ", boardOffsetX+(Board.DIMBOARD*2), boardOffsetY+i, false);
        }
        print(BG_LIGHT_CYAN + "  0 1 2 3 4 5 6 7 8   ", boardOffsetX - 2, bookshelfOffsetY+9, false);
    }

    private String getBGColorFromTile(Tile t)
    {
        switch (t.getItemType())
        {
            case CAT: return BG_GREEN.toString();
            case BOOK: return BG_GRAY1.toString();
            case PLANT: return BG_MAGENTA.toString();
            case GAME: return BG_YELLOW.toString();
            case FRAME: return BG_BLUE.toString();
            case TROPHY: return BG_LIGHT_BLUE.toString();
        }
        return "";
    }

    private String getColorFromTile(Tile t)
    {
        switch (t.getItemType())
        {
            case CAT: return GREEN.toString();
            case BOOK: return LIGHT_GRAY.toString();
            case PLANT: return MAGENTA.toString();
            case GAME: return YELLOW.toString();
            case FRAME: return BLUE.toString();
            case TROPHY: return CYAN.toString();
        }
        return "";
    }

    private void printError(String s)
    {
        clearRow(errorOffsetX, errorOffsetY);
        String c = RED.toString();
        print(c + s, errorOffsetX, errorOffsetY, false);
    }

    private void printBookshelf(int numPlayer)
    {
        String p = "";
        if(game.getCurrPlayer().getNickname().equals(game.getPlayers().get(numPlayer).getNickname()))
            p = GREEN.toString();

        print(p + game.getPlayers().get(numPlayer).getNickname() + RESET, bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY-3, false);
        print(BG_LIGHT_BROWN + "              ", bookshelfOffsetX + (numPlayer*bookshelvesDistance) - 2, bookshelfOffsetY-1, false);
        for(int i = 0; i<Bookshelf.NUMROWS; i++)
        {
            for(int j = 0; j<Bookshelf.NUMCOLUMNS; j++)
            {
                if(j == 0)
                    print(BG_LIGHT_BROWN + String.valueOf(i) + " ", bookshelfOffsetX + (numPlayer*bookshelvesDistance)-2,bookshelfOffsetY+i,false);
                try {
                    if(game.getPlayers().get(numPlayer).getBookshelf().getTile(i, j) != null)
                    {
                        String c = BG_DARK_BROWN.toString() + getColorFromTile(game.getPlayers().get(numPlayer).getBookshelf().getTile(i, j)) + "■ ";
                        print(c, bookshelfOffsetX+(j*2) + (numPlayer*bookshelvesDistance),bookshelfOffsetY+i,false);
                    }
                    else
                    {
                        print(BG_DARK_BROWN + "  ", bookshelfOffsetX+(j*2) + (numPlayer*bookshelvesDistance), bookshelfOffsetY+i, false);
                    }
                } catch (WrongArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
            print(BG_LIGHT_BROWN + "  ", bookshelfOffsetX+(Bookshelf.NUMCOLUMNS*2) + (numPlayer*bookshelvesDistance), bookshelfOffsetY+i, false);
        }
        setCursor(bookshelfOffsetX + (numPlayer*bookshelvesDistance) - 2, bookshelfOffsetY + 6);
        print(BG_LIGHT_BROWN + "  ");
        for(int i = 0; i<Bookshelf.NUMCOLUMNS; i++)
        {
            if(numPlayer == myPlayerIndex() && game.getCurrPlayer().getSelectedColumn() == i && game.getCurrPlayer().getNickname().equals(nickname))
            {
                print(BG_YELLOW.toString() + i + " " + RESET);
            }
            else
                print(BG_LIGHT_BROWN.toString() + i + " ");
        }
        print(BG_LIGHT_BROWN + "  ");
    }

    private void printAllBookshelves()
    {
        for(int i = 0; i < game.getPlayers().size(); i++)
        {
            printBookshelf(i);
            printHandBox(i);
            printHand(i);

        }
    }

    private void printCommonGoals()
    {
        String c = YELLOW.toString();
        print(c + "COMMON GOALS:", commonGoalOffsetX, commonGoalOffsetY, false);
        printCommonGoalBoxes();
        int offset = 0;
        for(CommonGoalCard card : game.getCommonGoals())
        {
            printCommonGoalDesc(Integer.parseInt(card.getId()), offset);
            offset++;
        }

    }

    private void printCommonGoalDesc(int id, int offset)
    {
        int cordX = commonGoalOffsetX+2 + (offset*60);
        int cordY = commonGoalOffsetY + 2;

        switch (id)
        {
            case 1:
                print("Six groups each containing at least", cordX, cordY, false);
                print("2 tiles of the same type.", cordX, cordY + 1, false);
                print("The tiles of one group can be different", cordX, cordY + 2, false);
                print("from those of another group.", cordX, cordY + 3, false);
                break;
            case 2:
                print("Four groups each containing at least", cordX, cordY, false);
                print("4 tiles of the same type.", cordX, cordY + 1, false);
                print("The tiles of one group can be different", cordX, cordY + 2, false);
                print("from those of another group.", cordX, cordY + 3, false);
                break;
            case 3:
                print("Four tiles of the same type in the four", cordX, cordY, false);
                print("corners of the bookshelf.", cordX, cordY + 1, false);
                break;
            case 4:
                print("Two groups each containing 4 tiles of", cordX, cordY, false);
                print("the same type in a 2x2 square. The tiles", cordX, cordY + 1, false);
                print("of one square can be different from", cordX, cordY + 2, false);
                print("those of the other square.", cordX, cordY + 3, false);
                break;
            case 5:
                print("Three columns each formed by 6 tiles", cordX, cordY, false);
                print("of maximum three different types. One", cordX, cordY + 1, false);
                print("column can show the same or a different", cordX, cordY + 2, false);
                print("combination of another column.", cordX, cordY + 3, false);
                break;
            case 6:
                print("Eight tiles of the same type. There’s no", cordX, cordY, false);
                print("restriction about the position of these", cordX, cordY + 1, false);
                print("tiles.", cordX, cordY + 2, false);
                break;
            case 7:
                print("Five tiles of the same type forming a", cordX, cordY, false);
                print("diagonal.", cordX, cordY + 1, false);
                break;
            case 8:
                print("Four lines each formed by 5 tiles of", cordX, cordY, false);
                print("maximum three different types. One", cordX, cordY + 1, false);
                print("line can show the same or a different", cordX, cordY + 2, false);
                print("combination of another line.", cordX, cordY + 3, false);
                break;
            case 9:
                print("Two columns each formed by 6", cordX, cordY, false);
                print("different types of tiles.", cordX, cordY + 1, false);
                break;
            case 10:
                print("Two lines each formed by 5 different", cordX, cordY, false);
                print("types of tiles. One line can show the", cordX, cordY + 1, false);
                print("same or a different combination of the", cordX, cordY + 2, false);
                print("other line.", cordX, cordY + 3, false);
                break;
            case 11:
                print("Five tiles of the same type", cordX, cordY, false);
                print("forming an X.", cordX, cordY + 1, false);
                break;
            case 12:
                print("Five columns of increasing or decreasing", cordX, cordY, false);
                print("height.", cordX, cordY + 1, false);
                print("Tiles can be of any type.", cordX, cordY + 2, false);
                break;
            default:
                print("NOT YET IMPLEMENTED", commonGoalOffsetX+2 + (offset*60), commonGoalOffsetY + 2, false);
        }
        //print the value of the top scoring token of the common goal card associed to the id
        //TODO: get this to work
        print(game.getCommonGoalTokens(String.valueOf(id)).get(0).getPoints().toString(), cordX + 44, cordY + 4, false);
        //print(game.getCommonGoalsMap().get(game.getCommonGoals().get(id)).get(0).getPoints().toString(), cordX + 35, cordY + 4, false);
        //print("8", cordX + 44, cordY + 4, false);
    }

    private void  printCommonGoalBoxes()
    {
        String c = YELLOW.toString();

        for(int i = 0; i<game.getCommonGoals().size(); i++)
        {
            print(c + "╔══════════════════════════════════════════════╗", commonGoalOffsetX + (i*60), commonGoalOffsetY + 1, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 2, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 3, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 4, false);
            print(c + "║                                           ╔═══╗", commonGoalOffsetX + (i*60), commonGoalOffsetY + 5, false);
            print(c + "╚═══════════════════════════════════════════║   ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 6, false);
            print(c + "                                            ╚═══╝", commonGoalOffsetX + (i*60), commonGoalOffsetY + 7, false);
        }
    }

    private void printHand(int numPlayer)
    {
        //erasing old hand on screen
        setCursor(bookshelfOffsetX + (numPlayer*bookshelvesDistance) + 2, bookshelfOffsetY + 9);
        print("      ");
        setCursor(bookshelfOffsetX + (numPlayer*bookshelvesDistance) + 2, bookshelfOffsetY + 9);
        for(Tile t : game.getPlayers().get(numPlayer).getTilesPicked())
        {
            print(getColorFromTile(t) + "■ " + RESET);
            //print(String.valueOf(t.getItemType().name().charAt(0)+ " "));
        }
        //print("T T T", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 9, false);
    }
    private void printHandBox(int numPlayer)
    {
        print("╔ hand ═╗", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 8, false);
        print("║       ║", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 9, false);
        print("╚═══════╝", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 10, false);
    }

    private void printPoints()
    {
        for(int i = 0; i < game.getPlayers().size(); i++)
        {
            print("points: " + game.getPlayers().get(i).getPublicPoints(), bookshelfOffsetX + (i*bookshelvesDistance), bookshelfOffsetY + 12, false);
        }
    }

    private void printPersonalGoal()
    {
        print(YELLOW.toString() + "PERSONAL GOAL", personalGoalOffsetX-3, personalGoalOffsetY-2, false);

        for(int i = 0; i<Bookshelf.NUMROWS; i++)
        {
            for(int j = 0; j<Bookshelf.NUMCOLUMNS; j++)
            {
                if(j == 0)
                    print(String.valueOf(i),personalGoalOffsetX-1,personalGoalOffsetY+i, false);
                //String c = BG_BRIGHT_BLUE.toString() + BLUE.toString();
                //print(c + " ", personalGoalOffsetX+j, personalGoalOffsetY+i, false);
            }
        }
        print("0 1 2 3 4", personalGoalOffsetX, personalGoalOffsetY+6, false);

        List<Pair<Pair<Integer, Integer>, Tile>> constraints = game.getPlayers().get(myPlayerIndex()).getPersonalGoal().getConstraints();
        for (Pair<Pair<Integer, Integer>, Tile> c: constraints) {
            int col = c.getLeft().getLeft();
            int row = c.getLeft().getRight();
            print(getColorFromTile(c.getRight()) + "■ " + RESET,personalGoalOffsetX + (col*2), personalGoalOffsetY + row, false);
        }
        //print("DIMENSIONE CONSTRAINTS PERSONALGOAL CARD: " + constraints.size(), 10, 10, false);



        /*for (Pair<Pair<Integer, Integer>, Tile> c: game.getPlayers().get(myPlayerIndex()).getPersonalGoal().getConstraints()) {
            int col = c.getLeft().getLeft();
            int row = c.getLeft().getRight();
            print(BG_YELLOW + String.valueOf(c.getRight().getItemType().name().charAt(0)), personalGoalOffsetX+col,bookshelfOffsetY+row,false);

        }*/
    }

    private int myPlayerIndex()
    {
        int count = 0;
        for(ImmutablePlayer p : game.getPlayers())
        {
            if(p.getNickname().equals(nickname))
                return count;
            count++;
        }
        return -1;
    }

    public List<LocatedTile> getSelectedTiles() {
        return selectedTiles;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public int getSelectedHandIndex() {
        return selectedHandIndex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setAvailableGames(List<GameController.GameDefinition> availableGames) {
        this.availableGames = availableGames;
    }

    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }
}
