package org.myshelfie;

import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientImpl;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.server.ServerImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static org.myshelfie.view.CommandLineInterface.*;

public class TestCLI implements Runnable {

    private Game game;
    private Server server;
    private ClientImpl client;

    @Override
    public void run() {
        int standard_game = 2;
        int numPlayer = 2;
        CommonGoalDeck cgd = CommonGoalDeck.getInstance();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player(("test" + i), null));
        }
        List<CommonGoalCard> cgc = cgd.drawCommonGoalCard(standard_game);
        HashMap<CommonGoalCard, List<ScoringToken>> commonGoal = new HashMap<>();
        for (CommonGoalCard x : cgc) {
            commonGoal.put(x, (List<ScoringToken>) createTokensPersonalGoalCard(x.getId(), numPlayer));
        }
        TileBag tb = new TileBag();

        game = new Game(players, new Board(numPlayer), commonGoal, tb);
        server = new ServerImpl(game);
        client = new ClientImpl(server, "test1");

        Thread t = new Thread(() -> {
            try {

                Scanner scanner = new Scanner(System.in);
                while (true) {
                    //if (System.in.available()>0) {
                        clearRow(inputOffsetX, inputOffsetY);
                        setCursor(inputOffsetX, inputOffsetY);
                        String userCommand = scanner.nextLine();
                        client.parseInput(userCommand);
                    //}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();

        game.getBoard().refillBoard(2, new TileBag());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            game.getPlayers().get(0).getBookshelf().insertTile(new Tile(ItemType.CAT), 1);
        } catch (TileInsertionException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("PASSSAAAAAA");
    }


    private static LinkedList<ScoringToken> createTokensPersonalGoalCard(String id, int numPlayer) {
        LinkedList<ScoringToken> tokens = new LinkedList<>();
        switch (numPlayer) {
            case 2:
                tokens.add(new ScoringToken(8,id));
                tokens.add(new ScoringToken(4,id));
                break;

            case 3:
                tokens.add(new ScoringToken(8,id));
                tokens.add(new ScoringToken(6,id));
                tokens.add(new ScoringToken(4,id));
                break;

            case 4:
                tokens.add(new ScoringToken(8,id));
                tokens.add(new ScoringToken(6,id));
                tokens.add(new ScoringToken(4,id));
                tokens.add(new ScoringToken(2,id));
                break;
        }
        return tokens;
    }
}
