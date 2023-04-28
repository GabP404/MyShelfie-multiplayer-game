package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEventType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class GameController {
    private Game game;
    private List<Client> clients;

    private int numPlayerGame;

    private int rulesGame;

    public GameController() {}

    public void createGame(List<String> nicknames, int numCommonGoalCard) throws IOException, URISyntaxException {
        int numPlayer = nicknames.size();
        CommonGoalDeck commonGoalDeck = CommonGoalDeck.getInstance();
        PersonalGoalDeck personalGoalDeck = PersonalGoalDeck.getInstance();
        List<PersonalGoalCard> personalGoalCardsGame = personalGoalDeck.draw(numPlayer);
        List<CommonGoalCard> commonGoalCards = commonGoalDeck.drawCommonGoalCard(numCommonGoalCard);
        List<Player> players = new ArrayList<>();
        for (String nickname :
                nicknames) {
            players.add(new Player(nickname,personalGoalCardsGame.remove(0)));
        }
        HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
        for (CommonGoalCard x : commonGoalCards) {
            commonGoal.put(x, (List<ScoringToken>) createTokensCommonGoalCard(x.getId(),numPlayer));
        }
        TileBag tileBag = new TileBag();
        this.game = new Game(players, new Board(numPlayer),commonGoal,tileBag,ModelState.CREATED_GAME);
    }

    private LinkedList<ScoringToken> createTokensCommonGoalCard(String id, int numPlayer) {
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

    public GameController(Game game, List<Client> clients) {
        this.game = game;
        this.clients = clients;
    }


    private boolean checkEndGame() {
        int numPlayersOnline = (int) this.game.getPlayers().stream().filter(x -> x.isOnline()).count();
        if(numPlayersOnline == 0){
            this.game.setModelState(ModelState.END_GAME);
            return true;
        }
        if(numPlayersOnline == 1){
            this.game.setModelState(ModelState.END_GAME);
            this.game.setWinner(this.game.getPlayers().stream().filter(x -> x.isOnline()).collect(Collectors.toList()).get(0));
            return true;
        }
        if(this.game.getPlayers().stream().filter(x -> x.getBookshelf().isFull()).count() > 0) {
            this.game.setModelState(ModelState.END_GAME);
            Player p = this.game.getPlayers().stream().reduce( (a, b) -> a.getTotalPoints() > b.getTotalPoints() ? a:b).orElse(null);
            this.game.setWinner(p);
        }
        return false;
    }

    public void getConnectionMessage() {
        //setting player offline
    }

    public void setOffinePlayer(String nickname) {
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).collect(Collectors.toList()).get(0).setOnline(false);
    }

    public void setOninePlayer(String nickname) {
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).collect(Collectors.toList()).get(0).setOnline(true);
    }


    //change firm of the method based
    public void executeCommand(String command, UserInputEventType t) {
            Command c = null;
            switch (t) {
                case SELECTED_BOOKSHELF_COLUMN -> c = new PickTilesCommand(game.getBoard(),game.getCurrPlayer() ,command,this.game.getModelState());
                case SELECTED_TILES -> c = new SelectTileFromHandCommand(game.getCurrPlayer(), command,this.game.getModelState());
                case SELECTED_HAND_TILE -> c = new SelectColumnCommand(game.getCurrPlayer(), command,this.game.getModelState());
            }
            try {
                c.execute();
            }catch (WrongTurnException e) {

            }catch (InvalidCommand e) {

            }catch (WrongArgumentException e){

            }
            nextState();
    }

    private void checkState(UserInputEventType t) throws InvalidCommand{
        ModelState currentGameState = game.getModelState();
        if(currentGameState == ModelState.CREATED_GAME) throw new InvalidCommand("Game is not started");
        if(currentGameState == ModelState.WAITING_SELECTION_TILE && t != UserInputEventType.SELECTED_TILES) throw new InvalidCommand("Waiting for Tile Selection ");
        if(currentGameState == ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN && t != UserInputEventType.SELECTED_BOOKSHELF_COLUMN) throw new InvalidCommand("Waiting for Column Selection ");
        if(currentGameState == ModelState.END_GAME) throw new InvalidCommand("Game ended");
        if(currentGameState == ModelState.WAITING_3_SELECTION_TILE_FROM_HAND && t != UserInputEventType.SELECTED_HAND_TILE) throw new InvalidCommand("Waiting for Tile Selection Hand ");
        if(currentGameState == ModelState.WAITING_2_SELECTION_TILE_FROM_HAND && t != UserInputEventType.SELECTED_HAND_TILE) throw new InvalidCommand("Waiting for Tile Selection Hand ");
        if(currentGameState == ModelState.WAITING_1_SELECTION_TILE_FROM_HAND && t != UserInputEventType.SELECTED_HAND_TILE) throw new InvalidCommand("Waiting for Tile Selection Hand ");
    }


    private void nextState() {
        ModelState currentGameState = game.getModelState();
        ModelState nextState = null;
        switch (currentGameState) {
            case CREATED_GAME:
                nextState = ModelState.WAITING_SELECTION_TILE;
                break;
            case WAITING_SELECTION_TILE:
                nextState = ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN;
                break;
            case WAITING_3_SELECTION_TILE_FROM_HAND:
                nextState = ModelState.WAITING_2_SELECTION_TILE_FROM_HAND;
                break;
            case WAITING_2_SELECTION_TILE_FROM_HAND:
                nextState = ModelState.WAITING_1_SELECTION_TILE_FROM_HAND;
                break;
            case WAITING_1_SELECTION_TILE_FROM_HAND:
                if(checkEndGame()) {
                    nextState = ModelState.END_GAME;
                }else {
                    game.setCurrPlayer(game.getNextPlayer());
                    while(!game.getCurrPlayer().isOnline()) {
                        game.setCurrPlayer(game.getNextPlayer());
                    }
                    nextState = ModelState.WAITING_SELECTION_TILE;
                }
                break;
            case WAITING_SELECTION_BOOKSHELF_COLUMN:
                switch (this.game.getCurrPlayer().getTilesPicked().size()){
                    case 3:
                        nextState = ModelState.WAITING_3_SELECTION_TILE_FROM_HAND;
                        break;
                    case 2:
                        nextState = ModelState.WAITING_2_SELECTION_TILE_FROM_HAND;
                        break;
                    case 1:
                        nextState = ModelState.WAITING_1_SELECTION_TILE_FROM_HAND;
                        break;
                }
                break;
            case END_GAME:
                nextState = ModelState.END_GAME;
                break;
        }
        game.setModelState(nextState);
    }

}
