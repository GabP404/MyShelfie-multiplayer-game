package org.myshelfie.view.CLI;

import org.myshelfie.controller.GameController;
import org.myshelfie.model.Board;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.ModelState;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.View;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.myshelfie.view.PrinterCLI.*;

/**
 * This class is the command-line interface of the game.
 */
public class ViewCLI implements View{
    public static final int inputOffsetX = 0;
    public static final int inputOffsetY = 29;
    private Client client;
    private List<LocatedTile> selectedTiles;    // tiles selected from the board
    private int selectedColumn;
    private int selectedHandIndex;
    private String nickname;
    private GameView game;
    private boolean reconnecting = false;
    private boolean showingHelp = false;
    private final Scanner scanner = new Scanner(System.in);
    private List<GameController.GameDefinition> availableGames;

    /**
     * Thread responsible for the login screen
     */
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

    /**
     * Thread responsible for the lobby screen:
     * <ul>
     *     <li> choose whether to join an existing game or create a new one. </li>
     *     <li> insert the parameters and create a game, or select a game within
     *          the list of avaiable ones. </li>
     * </ul>
     */
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
                        print("Insert a Game name, player number and \"simple\" at the end for simplified version   |   --back to go back", 0, 20, false);
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
                                        if(validateString(parts[0]) && parts[0].length() <= 20) {
                                            if(parts.length > 2 && parts[2].equalsIgnoreCase("simple"))
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
                                            print("game name is not valid, it cannot contain symbols and the maximum length is 20 ", 0, 25, false);
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
                            if(this.availableGames.get(i).getNicknames().size() < this.availableGames.get(i).getMaxPlayers())
                                print("-> " + this.availableGames.get(i).getGameName() + " " + this.availableGames.get(i).getNicknames().size() + "/" + this.availableGames.get(i).getMaxPlayers(), 90, 22+i, false);
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

    /**
     * Check if the game name is in the list of available ones
     */
    private boolean isInLobbyList(String gameName) {
        for (GameController.GameDefinition availableGame : this.availableGames) {
            if (availableGame.getGameName().equalsIgnoreCase(gameName) && availableGame.getNicknames().size() < availableGame.getMaxPlayers())
                return true;
        }
        return false;
    }

    /**
     * @param input String to be validated
     * @return True if the string is valid, false otherwise
     */
    private boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * Create the ViewCLI and start it.
     * @param args Contains information about the connection type and the server address.
     */
    public static void main(String[] args) {
        boolean isRMI = Boolean.parseBoolean(args[0]);
        String serverAddress = args[1];
        ViewCLI view = new ViewCLI(isRMI, serverAddress);
        view.run();
    }

    /**
     * Create a new ViewCLI, instantiating the client and connecting to the server.
     * @param isRMI True if the connection type is RMI, false if it is Socket
     * @param serverAddress Address of the server
     */
    public ViewCLI(boolean isRMI, String serverAddress) {
        selectedColumn = -1;
        selectedHandIndex = -1;
        selectedTiles = new ArrayList<>();
        availableGames = new ArrayList<>();
        try {
            this.client = new Client(false, isRMI, serverAddress);
            client.connect();
            client.initializeView(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main method in the {@link View} interface, used to update the view with the new model.
     * After checking the game is still ongoing, it prints all the items of the view using information
     * in the updated model.
     * @param msg The GameView that represents the immutable version of the updated model
     * @param ev Event that caused the model's change
     */
    @Override
    public void update(GameView msg, GameEvent ev) {
        // End the threads to create/join a game, in case the gameView was received after a reconnection
        this.endLobbyPhase();
        game = msg;

        //if the game state is END_GAME print the ranking
        if(game.getModelState().equals(ModelState.END_GAME))
        {
            clear();
            printEndGameScreen(game, nickname);
            setCursor(inputOffsetX, inputOffsetY);
            client.stopHeartbeatThread();
        }
        else    //else print the new gameView
        {
            if(!showingHelp)//if a player is watching the help box, don't print the gameView yet
            {
                switch (ev)
                {
                    case ERROR:
                        if(game.getErrorState(nickname) != null)
                        {
                            clear();
                            printAll(game, selectedTiles, nickname);
                            printError(game.getErrorState(nickname));
                        }
                        break;
                    case BOARD_UPDATE:
                        selectedTiles.clear();
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                    default:
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                }
                setCursor(inputOffsetX, inputOffsetY);
            }
        }
    }

    /**
     * Main execution method, overrides the one in {@link Runnable}.
     * <ul>
     *     <li>Starts the thread that asks the user for the nickname (login phase)</li>
     *     <li>Start the thread that allows the user to join or create a new game</li>
     *     <li>Start the thread that handles the user input for the entire game</li>
     * </ul>
     */
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

    /**
     * Method called by {@link org.myshelfie.network.client.UserInputListener UserInputListener} when the
     * {@link org.myshelfie.network.messages.commandMessages.NicknameMessage NicknameMessage} receives a positive response
     * from the Server. This allows the transition to the choiceThread.
     */
    @Override
    public void endLoginPhase()
    {
        if(threadNick.isAlive())
            threadNick.interrupt();
    }

    /**
     * Method called by {@link org.myshelfie.network.client.UserInputListener UserInputListener} when the
     * {@link org.myshelfie.network.messages.commandMessages.CreateGameMessage create} or
     * {@link org.myshelfie.controller.JoinGameCommand join} message receive a positive response
     * from the Server. This allows the transition to the execution of the main game..
     */
    @Override
    public void endLobbyPhase()
    {
        if(threadChoice.isAlive())
            threadChoice.interrupt();
    }


    /**
        Method used to parse the input string and call the correct method to handle the chosen command.
     * @param s The input string
     */
    public void parseInput(String s) {
        String[] parts = s.split(" ");
        //if there are no arguments return
        if (parts.length < 1) {
            printError("NOT ENOUGH ARGUMENTS");
            return;
        }

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
                return;
            }
            case "help", "h" -> {
                //print possible commands
                showingHelp = true;
                printHelp();
                scanner.nextLine();
                printAll(game, selectedTiles,nickname);
                showingHelp = false;
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
        //if the game is paused, prevent further actions
        if (game.getModelState().equals(ModelState.PAUSE)) {
            printError("GAME IS PAUSED DUE TO OTHER PLAYERS' DISCONNECTION");
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
                        if(!game.getModelState().equals(ModelState.WAITING_SELECTION_TILE))
                        {
                            printError("YOU CAN'T SELECT A TILE NOW");
                            return;
                        }
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
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                    case "column", "c":
                        if(!game.getModelState().equals(ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN))
                        {
                            printError("YOU CAN'T SELECT A COLUMN NOW");
                            return;
                        }
                        if (parts.length != 3) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        try {
                            int c = Integer.parseInt(parts[2]);
                            if (!selectColumn(c))
                                return;
                        } catch (NumberFormatException nfe) {
                            printError("COLUMN NUMBER IS NOT CORRECT");
                            return;
                        }
                        break;
                    default:
                        printError("COMMAND DOES NOT EXIST");
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
                        if(!game.getModelState().equals(ModelState.WAITING_SELECTION_TILE))
                        {
                            printError("YOU CAN'T DESELECT A TILE NOW");
                            return;
                        }
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
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                    default:
                        printError("COMMAND DOES NOT EXIST");
                        break;
                }
                break;
            case "pick", "p":
                if(!game.getModelState().equals(ModelState.WAITING_1_SELECTION_TILE_FROM_HAND) &&
                        !game.getModelState().equals(ModelState.WAITING_2_SELECTION_TILE_FROM_HAND) &&
                        !game.getModelState().equals(ModelState.WAITING_3_SELECTION_TILE_FROM_HAND)) {
                    printError("YOU CAN'T PICK A TILE FROM THE HAND NOW");
                    return;
                }
                if (parts.length != 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                try {
                    int c = Integer.parseInt(parts[1]);
                    if (!pickTileFromHand(c))
                        return;
                } catch (NumberFormatException nfe) {
                    printError("HAND INDEX NUMBER IS NOT CORRECT");
                    return;
                }
                break;
            case "confirm", "c":
                if(selectedTiles.isEmpty())
                {
                    printError("NO TILES SELECTED");
                    return;
                }
                if(!game.getModelState().equals(ModelState.WAITING_SELECTION_TILE))
                {
                    printError("YOU CAN'T CONFIRM THE SELECTION NOW");
                    return;
                }
                confirmSelection();
                break;
            default:
                printError("COMMAND DOES NOT EXIST, TYPE \"help\" TO SEE ALL COMMANDS");
                return;
        }
    }

    /**
     * Method that handles the selection of a tile in the board, after checking that the single tile is actually selectable
     * as well as all the group of tiles selected so far.
     * @param r The row of the tile
     * @param c The column of the tile
     * @return true if the tile has been selected, false otherwise
     */
    private boolean selectTile(int r, int c) {
        if(!isTileExisting(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                printError("TILE ALREADY SELECTED");
                return false;
            }
        }

        if (game.getCurrPlayer().getBookshelf().getMinHeight() + selectedTiles.size() + 1 > Bookshelf.NUMROWS) {
            printError("SELECTION PREVENTED: U CAN'T FIT THE SELECTED TILES IN THE BOOKSHELF");
            return false;
        }
        LocatedTile t = new LocatedTile(null, r, c);
        selectedTiles.add(t);
        if (!isTilesGroupSelectable(selectedTiles)) {
            selectedTiles.remove(t);
            printError("SELECTION PREVENTED: TILE IS NOT SELECTABLE, CHECK THE RULES");
            return false;
        }
        return true;
    }

    /**
     * Method that allows to deselect a tile from the h
     * @param r The row of the tile to deselect
     * @param c The column of the tile to deselect
     * @return true if the tile has been deselected, false otherwise
     */
    private boolean deselectTile(int r, int c) {
        if(!isTileExisting(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                selectedTiles.remove(t);
                if(isTilesGroupSelectable(selectedTiles)) {
                    return true;
                } else {
                    selectedTiles.add(t);
                    printError("DESELECTION PREVENTED: YOU CANNOT DESELECT THAT TILE GIVEN THE CURRENT SELECTED TILES");
                    return false;
                }
            }
        }
        printError("TILE IS NOT SELECTED");
        return false;
    }

    /**
     * Checks if a tile exists in the board
     * @param r The row of the tile
     * @param c The column of the tile
     * @return true if the tile exists, false otherwise
     */
    private boolean isTileExisting(int r, int c) {
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

    /**
     * Calls the {@link org.myshelfie.network.EventManager#notify} method to send the selected tiles to the server.
     */
    private void confirmSelection() {
        this.client.eventManager.notify(UserInputEvent.SELECTED_TILES, selectedTiles);
    }

    /**
     * Checks the validity of the selected column and if correct calls the {@link org.myshelfie.network.EventManager#notify notify}
     * method to send the selected column to the server.
     * @param c Column index
     * @return true if the column is valid (and this information sent to the server), false otherwise
     */
    private boolean selectColumn(int c) {
        if(c < 0 || c>= Bookshelf.NUMCOLUMNS) {
            printError("COLUMN NUMBER IS NOT VALID");
            return false;
        }
        //if the column is too full for the selected tiles return
        if (game.getCurrPlayer().getBookshelf().getHeight(c) + game.getCurrPlayer().getTilesPicked().size() > Bookshelf.NUMROWS) {
            printError("SELECTION PREVENTED: U CAN'T FIT THE SELECTED TILES IN THAT COLUMN");
            return false;
        }
        selectedColumn = c;
        this.client.eventManager.notify(UserInputEvent.SELECTED_BOOKSHELF_COLUMN, selectedColumn);
        return true;
    }

    /**
     * Allows to pick a tile from the hand to be inserted in the bookshelf. If the index is not valid, an error is printed,
     * otherwise the index is saved and sent to the server, by calling the {@link org.myshelfie.network.EventManager#notify notify}.
     * @param index
     * @return
     */
    private boolean pickTileFromHand(int index)
    {
        if(index<0 || index >= game.getCurrPlayer().getTilesPicked().size())
        {
            printError("INDEX OF CARD IN HAND IS NOT VALID");
            return false;
        }
        selectedHandIndex = index;
        this.client.eventManager.notify(UserInputEvent.SELECTED_HAND_TILE, selectedHandIndex);
        return true;

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

    /**
     * @return The nickname of the player associated to this CLI
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Utility method used to check wheter a group of tiles is sleectable or not.
     * @param chosen The list of tiles to check. Note that {@link org.myshelfie.model.LocatedTile LocatedTiles} are used
     *               since they contain the position of the tile in the board.
     * @return true if the group of tiles is selectable, false otherwise
     */
    public boolean isTilesGroupSelectable(List<LocatedTile> chosen) {
        // Add the check that you cannot select more than 3 tiles
        if (chosen.size() > 3)
            return false;
        //Check that all the selected tiles are indeed selectable on their own (i.e. at least one free border)
        for (LocatedTile t : chosen) {
            if (!game.getBoard().hasOneOrMoreFreeBorders(t.getRow(), t.getCol()))
                return false;
        }
        // Skip the check if there is only one tile in the selection
        if (chosen.size() < 2) {
            // If so, return true since a single tile or no tiles are always in a line
            return true;
        }
        // The tiles are horizontal / vertical if all the rows / cols are the same
        boolean isHorizontal = chosen.stream().map(LocatedTile::getRow).distinct().count() == 1;
        boolean isVertical = chosen.stream().map(LocatedTile::getCol).distinct().count() == 1;

        if (!isHorizontal && !isVertical)
            return false;
        // Check that the chosen tile are "sequential" i.e., adjacent to each other
        SortedSet<Integer> sortedIndexes = new TreeSet<>();
        if (isHorizontal)
            sortedIndexes.addAll(chosen.stream().map(LocatedTile::getCol).collect(Collectors.toSet()));
        if (isVertical)
            sortedIndexes.addAll(chosen.stream().map(LocatedTile::getRow).collect(Collectors.toSet()));

        return sortedIndexes.last() - sortedIndexes.first() == sortedIndexes.size() - 1;
    }

    /**
     * Print an error message if the nickname provided by the during login phase is already used by another player.
     * This is used in {@link org.myshelfie.network.client.UserInputListener UserInputListener} after the
     * server has responded to the login request.
     */
    @Override
    public void nicknameAlreadyUsed() {
        printError("NICKNAME ALREADY USED");
    }

    /**
     * Allows to set the nickname of the player associated to this CLI. This is used
     * in {@link org.myshelfie.network.client.UserInputListener UserInputListener}
     * if the server response to the login request is positive.
     * @param nickname The nickname of the player
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Set the list of available games to show in the lobby. This is used in
     * {@link org.myshelfie.network.client.UserInputListener UserInputListener}
     * to save the list of available games received from the server.
     * @param availableGames The list of available games
     */
    @Override
    public void setAvailableGames(List<GameController.GameDefinition> availableGames) {
        this.availableGames = availableGames;
    }

    /**
     * Set the reconnecting status of the player corresponding to this CLI. This is used in
     * {@link org.myshelfie.network.client.UserInputListener UserInputListener} after the
     * server has responded to the login request.
     * @param reconnecting true if the player is reconnecting, false otherwise
     */
    @Override
    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }

    /**
     * @return The name of the game
     */
    @Override
    public String getGameName() {
        if (game == null)
            return null;
        return game.getGameName();
    }

    /**
     * @return The last {@link GameView} received from the server.
     */
    @Override
    public GameView getGameView() {
        return game;
    }

}
