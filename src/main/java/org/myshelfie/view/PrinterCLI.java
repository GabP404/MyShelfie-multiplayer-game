package org.myshelfie.view;

import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.messages.gameMessages.ImmutableBoard;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.myshelfie.view.CLI.Color.*;
import static org.myshelfie.view.CLI.Color.BLUE;

public class PrinterCLI {
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

    //prints the end game score of all players
    public static void printEndGameScreen(GameView game, String nickname)
    {
            List<ImmutablePlayer> playersRanking = game.getPlayers().stream().sorted(Comparator.comparingInt(ImmutablePlayer::getTotalPoints).reversed()).collect(Collectors.toList());
        print(YELLOW + "╔═════════════════════════════════════════════════════════════════════════════════╗", rankingOffsetX, rankingOffsetY - 1, false);
        print(YELLOW + "║                                                                                 ║", rankingOffsetX, rankingOffsetY, false);
        print("LEADERBOARD", rankingOffsetX + 35, rankingOffsetY, false);
        print(YELLOW + "╠══════════════════╦════════════╦══════════════╦═══════════╦══════════╦═══════════╣", rankingOffsetX, rankingOffsetY + 1, false);
        print(YELLOW + "║                  ║            ║              ║           ║          ║           ║", rankingOffsetX, rankingOffsetY + 2, false);
        print("CommonGoal", rankingOffsetX + 21, rankingOffsetY + 2, false);
        print("PersonalGoal", rankingOffsetX + 34, rankingOffsetY + 2, false);
        print("Bookshelf", rankingOffsetX + 49, rankingOffsetY + 2, false);
        print("EndToken", rankingOffsetX + 62, rankingOffsetY + 2, false);

        print(CYAN + "PLAYER" + RESET, rankingOffsetX + 7, rankingOffsetY + 2, false);
        print(CYAN + "TOTAL" + RESET, rankingOffsetX + 73, rankingOffsetY + 2, false);

        print(YELLOW + "╠══════════════════╬════════════╬══════════════╬═══════════╬══════════╬═══════════╣", rankingOffsetX, rankingOffsetY + 3, false);


        for (int i=0; i<playersRanking.size(); i++) {
            print(YELLOW + "║                  ║            ║              ║           ║          ║           ║", rankingOffsetX, rankingOffsetY + 4 + i, false);
            String nameFormat = "";
            String pointsFormat = "";
            ImmutablePlayer player = playersRanking.get(i);
            if (!player.isOnline()) {
                // If the player is offline
                nameFormat = ULight_gray.toString();
                pointsFormat = ULight_gray.toString();
            }
            else if (player.isWinner()) {
                nameFormat = GREEN.toString();
                pointsFormat = GREEN.toString();
            }
            print(nameFormat + player.getNickname() + RESET, rankingOffsetX + 8 - (player.getNickname().length() / 2), rankingOffsetY + 4 + i, false);
            print(pointsFormat + player.getCommonGoalPoints(), rankingOffsetX + 25, rankingOffsetY + 4 + i, false);
            print(pointsFormat + player.getPersonalGoalPoints(), rankingOffsetX + 39, rankingOffsetY + 4 + i, false);
            print(pointsFormat + player.getBookshelfPoints(), rankingOffsetX + 52, rankingOffsetY + 4 + i, false);
            print(pointsFormat + (player.getHasFinalToken() ? "1" : "0"), rankingOffsetX + 64, rankingOffsetY + 4 + i, false);
            print(pointsFormat + player.getTotalPoints()+ RESET, rankingOffsetX + 75, rankingOffsetY + 4 + i, false);

        }

        print(YELLOW + "╠══════════════════╩════════════╩══════════════╩═══════════╩══════════╩═══════════╣", rankingOffsetX, rankingOffsetY + 4 + playersRanking.size(), false);
        print(YELLOW + "║                                                                                 ║", rankingOffsetX, rankingOffsetY + 5 + playersRanking.size(), false);
        print("WINNER", rankingOffsetX + 37, rankingOffsetY + 5 + playersRanking.size(), false);
        print(YELLOW + "╠═════════════════════════════════════════════════════════════════════════════════╣", rankingOffsetX, rankingOffsetY + 6 + playersRanking.size(), false);

        int k=0;
        for (int i=0; i < playersRanking.size(); i++) {
            if (playersRanking.get(i).isWinner()) {
                print(YELLOW + "║                                                                                 ║", rankingOffsetX, rankingOffsetY + 7 + k + playersRanking.size(), false);
                String nameFormat = "";
                ImmutablePlayer player = playersRanking.get(i);
                if (player.getNickname().equals(nickname))
                    nameFormat = GREEN.toString();
                int nameOffset = rankingOffsetX + 40 - ( (int) player.getNickname().length() / 2);
                print(nameFormat + player.getNickname(), nameOffset, rankingOffsetY + 7 + k + playersRanking.size(), false);
                k++;
            }
        }
        print(YELLOW + "╚═════════════════════════════════════════════════════════════════════════════════╝", rankingOffsetX, rankingOffsetY + 7 + playersRanking.size() + k, false);

        print("Type exit to leave", 0, 1, false);

    }

    public static void printHelp()
    {
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

    }

    public static void printAll(GameView game,List<LocatedTile> selectedTiles, String nickname) {
        for(int i = 0; i < errorOffsetY-2; i++)
            clearRow(0,i);
        printGameName(game);
        printCommonGoals(game);
        printBoard(game, selectedTiles);
        printAllBookshelves(game, nickname);
        printPoints(game);
        printPersonalGoal(game, nickname);
        if(game.getCurrPlayer().getNickname().equals(nickname))
        {
            print(MAGENTA + "IT'S YOUR TURN!",boardOffsetX-2, boardOffsetY-5, false);
            switch(game.getModelState())
            {
                case WAITING_SELECTION_TILE -> {
                    print("Select tiles from the board", boardOffsetX-2, boardOffsetY-4, false);
                }
                case WAITING_SELECTION_BOOKSHELF_COLUMN -> {
                    print("Select a column from the bookshelf", boardOffsetX-2, boardOffsetY-4, false);
                }
                case WAITING_1_SELECTION_TILE_FROM_HAND, WAITING_2_SELECTION_TILE_FROM_HAND, WAITING_3_SELECTION_TILE_FROM_HAND -> {
                    print("Pick a tile from your hand", boardOffsetX-2, boardOffsetY-4, false);
                }
                case PAUSE -> {
                    print("The game is paused because at the moment you are the only online player.", boardOffsetX-2, boardOffsetY-4, false);
                }
            }
        }

    }

    private static void printGameName(GameView game) {
        print(YELLOW + game.getGameName(), 58 - (game.getGameName().length() / 2), 0, false);
    }

    private static void printBoard(GameView game, List<LocatedTile> selectedTiles) {
        print("BOARD: ",boardOffsetX-2, boardOffsetY-2,false);
        print(BG_LIGHT_CYAN + "                      ",boardOffsetX - 2, boardOffsetY-1,false);
        for(int i = 0; i< Board.DIMBOARD; i++)
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
                            if (t.getRow() == i && t.getCol() == j) {
                                selected = true;
                                break;
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

    private static String getBGColorFromTile(Tile t)
    {
        return switch (t.getItemType()) {
            case CAT -> BG_GREEN.toString();
            case BOOK -> BG_GRAY1.toString();
            case PLANT -> BG_MAGENTA.toString();
            case GAME -> BG_YELLOW.toString();
            case FRAME -> BG_BLUE.toString();
            case TROPHY -> BG_LIGHT_BLUE.toString();
        };
    }

    private static String getColorFromTile(Tile t)
    {
        return switch (t.getItemType()) {
            case CAT -> GREEN.toString();
            case BOOK -> LIGHT_GRAY.toString();
            case PLANT -> MAGENTA.toString();
            case GAME -> YELLOW.toString();
            case FRAME -> BLUE.toString();
            case TROPHY -> CYAN.toString();
        };
    }

    public static void printError(String s)
    {
        clearRow(errorOffsetX, errorOffsetY);
        String c = RED.toString();
        print(c + s, errorOffsetX, errorOffsetY, false);
    }

    private static void printBookshelf(GameView game, int numPlayer, String nickname)
    {
        String p = "";
        if(game.getCurrPlayer().getNickname().equals(game.getPlayers().get(numPlayer).getNickname()))
            p = GREEN.toString();
        if(!game.getPlayers().get(numPlayer).isOnline())
            p = ULight_gray.toString();

        print(p + game.getPlayers().get(numPlayer).getNickname() + RESET, bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY-3, false);
        print(BG_LIGHT_BROWN + "              ", bookshelfOffsetX + (numPlayer*bookshelvesDistance) - 2, bookshelfOffsetY-1, false);
        for(int i = 0; i< Bookshelf.NUMROWS; i++)
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
            if(numPlayer == myPlayerIndex(game, nickname) && game.getCurrPlayer().getSelectedColumn() == i && game.getCurrPlayer().getNickname().equals(nickname))
            {
                print(BG_YELLOW.toString() + i + " " + RESET);
            }
            else
                print(BG_LIGHT_BROWN.toString() + i + " ");
        }
        print(BG_LIGHT_BROWN + "  ");
    }

    private static void printAllBookshelves(GameView game, String nickname)
    {
        for(int i = 0; i < game.getPlayers().size(); i++)
        {
            printBookshelf(game, i, nickname);
            printHandBox(i);
            printHand(game, i);

        }
    }

    private static void printCommonGoals(GameView game)
    {
        String c = YELLOW.toString();
        print(c + "COMMON GOALS:", commonGoalOffsetX, commonGoalOffsetY, false);
        printCommonGoalBoxes(game);
        int offset = 0;
        for(CommonGoalCard card : game.getCommonGoals())
        {
            printCommonGoalDesc(game, Integer.parseInt(card.getId()), offset);
            offset++;
        }

    }

    private static void printCommonGoalDesc(GameView game, int id, int offset)
    {
        int cordX = commonGoalOffsetX+2 + (offset*60);
        int cordY = commonGoalOffsetY + 2;

        switch (id) {
            case 1 -> {
                print("Six groups each containing at least", cordX, cordY, false);
                print("2 tiles of the same type.", cordX, cordY + 1, false);
                print("The tiles of one group can be different", cordX, cordY + 2, false);
                print("from those of another group.", cordX, cordY + 3, false);
            }
            case 2 -> {
                print("Four groups each containing at least", cordX, cordY, false);
                print("4 tiles of the same type.", cordX, cordY + 1, false);
                print("The tiles of one group can be different", cordX, cordY + 2, false);
                print("from those of another group.", cordX, cordY + 3, false);
            }
            case 3 -> {
                print("Four tiles of the same type in the four", cordX, cordY, false);
                print("corners of the bookshelf.", cordX, cordY + 1, false);
            }
            case 4 -> {
                print("Two groups each containing 4 tiles of", cordX, cordY, false);
                print("the same type in a 2x2 square. The tiles", cordX, cordY + 1, false);
                print("of one square can be different from", cordX, cordY + 2, false);
                print("those of the other square.", cordX, cordY + 3, false);
            }
            case 5 -> {
                print("Three columns each formed by 6 tiles", cordX, cordY, false);
                print("of maximum three different types. One", cordX, cordY + 1, false);
                print("column can show the same or a different", cordX, cordY + 2, false);
                print("combination of another column.", cordX, cordY + 3, false);
            }
            case 6 -> {
                print("Eight tiles of the same type. There’s no", cordX, cordY, false);
                print("restriction about the position of these", cordX, cordY + 1, false);
                print("tiles.", cordX, cordY + 2, false);
            }
            case 7 -> {
                print("Five tiles of the same type forming a", cordX, cordY, false);
                print("diagonal.", cordX, cordY + 1, false);
            }
            case 8 -> {
                print("Four lines each formed by 5 tiles of", cordX, cordY, false);
                print("maximum three different types. One", cordX, cordY + 1, false);
                print("line can show the same or a different", cordX, cordY + 2, false);
                print("combination of another line.", cordX, cordY + 3, false);
            }
            case 9 -> {
                print("Two columns each formed by 6", cordX, cordY, false);
                print("different types of tiles.", cordX, cordY + 1, false);
            }
            case 10 -> {
                print("Two lines each formed by 5 different", cordX, cordY, false);
                print("types of tiles. One line can show the", cordX, cordY + 1, false);
                print("same or a different combination of the", cordX, cordY + 2, false);
                print("other line.", cordX, cordY + 3, false);
            }
            case 11 -> {
                print("Five tiles of the same type", cordX, cordY, false);
                print("forming an X.", cordX, cordY + 1, false);
            }
            case 12 -> {
                print("Five columns of increasing or decreasing", cordX, cordY, false);
                print("height.", cordX, cordY + 1, false);
                print("Tiles can be of any type.", cordX, cordY + 2, false);
            }
            default ->
                    print("NOT YET IMPLEMENTED", commonGoalOffsetX + 2 + (offset * 60), commonGoalOffsetY + 2, false);
        }

        // Print the points of the top token
        if (game.getCommonGoalTokens(String.valueOf(id)).size() > 0)
            print(game.getCommonGoalTokens(String.valueOf(id)).get(0).getPoints().toString(), cordX + 44, cordY + 4, false);
        else
            print("-", cordX + 44, cordY + 4, false);
    }

    private static void  printCommonGoalBoxes(GameView game)
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

    private static void printHand(GameView game, int numPlayer)
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
    private static void printHandBox(int numPlayer)
    {
        print("╔ hand ═╗", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 8, false);
        print("║       ║", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 9, false);
        print("╚═══════╝", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 10, false);
    }

    private static void printPoints(GameView game)
    {
        for(int i = 0; i < game.getPlayers().size(); i++)
        {
            print("points: " + game.getPlayers().get(i).getPublicPoints(), bookshelfOffsetX + (i*bookshelvesDistance), bookshelfOffsetY + 12, false);
        }
    }

    private static void printPersonalGoal(GameView game, String nickname)
    {
        print(YELLOW.toString() + "PERSONAL GOAL", personalGoalOffsetX-3, personalGoalOffsetY-2, false);

        for(int i = 0; i<Bookshelf.NUMROWS; i++)
        {
            for(int j = 0; j<Bookshelf.NUMCOLUMNS; j++)
            {
                if(j == 0)
                    print(String.valueOf(i),personalGoalOffsetX-2,personalGoalOffsetY+i, false);
            }
        }
        print("0 1 2 3 4", personalGoalOffsetX, personalGoalOffsetY+6, false);

        List<Pair<Pair<Integer, Integer>, Tile>> constraints = game.getPlayers().get(myPlayerIndex(game, nickname)).getPersonalGoal().getConstraints();
        for (Pair<Pair<Integer, Integer>, Tile> c: constraints) {
            int col = c.getLeft().getLeft();
            int row = c.getLeft().getRight();
            print(getColorFromTile(c.getRight()) + "■ " + RESET,personalGoalOffsetX + (col*2), personalGoalOffsetY + row, false);
        }
    }

    private static int myPlayerIndex(GameView game, String nickname)
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
}
