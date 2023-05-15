package org.myshelfie;

import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.server.Server;

import java.rmi.RemoteException;
import java.util.*;

import static org.myshelfie.model.ModelState.WAITING_SELECTION_TILE;

public class TestCLI implements Runnable {

    private Game game;
    private static Server server;

    private static Thread serverThread;
    private Client client;

    @Override
    public void run() {
        ModelState modelState;
        modelState = WAITING_SELECTION_TILE;
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

        game = new Game();
        game.setupGame(players, new Board(numPlayer), commonGoal, tb, modelState, "testGame");

        Object lock = new Object();
        serverThread = new Thread(() -> {
            try {
                server = new Server();
                server.startServer(lock);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        synchronized (lock) {
            try {
                lock.wait(1000);
            } catch (InterruptedException e) {
                System.out.println("Server thread interrupted");
                throw new RuntimeException(e);
            }
        }


        try {
            client = new Client(true, false);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        client.run();

        /*
        try {
            game.getBoard().refillBoard(2, new TileBag());
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            game.getPlayers().get(0).getBookshelf().insertTile(new Tile(ItemType.CAT), 1);
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
        */
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
