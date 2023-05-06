package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private Game game;
    private List<String> nicknames;

    private int numPlayerGame;

    private int numGoalCards;
    public GameController() {
        this.nicknames = new ArrayList<>();
    }

    private void setupGame() throws IOException, URISyntaxException {
        CommonGoalDeck commonGoalDeck = CommonGoalDeck.getInstance();
        PersonalGoalDeck personalGoalDeck = PersonalGoalDeck.getInstance();
        List<PersonalGoalCard> personalGoalCardsGame = personalGoalDeck.draw(numPlayerGame);
        List<CommonGoalCard> commonGoalCards = commonGoalDeck.drawCommonGoalCard(numGoalCards);
        List<Player> players = new ArrayList<>();
        for (String nickname :
                nicknames) {
            players.add(new Player(nickname,personalGoalCardsGame.remove(0)));
        }
        HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
        for (CommonGoalCard x : commonGoalCards) {
            commonGoal.put(x, (List<ScoringToken>) createTokensCommonGoalCard(x.getId(),numPlayerGame));
        }
        TileBag tileBag = new TileBag();
        this.game = new Game(players, new Board(numPlayerGame),commonGoal,tileBag,ModelState.WAITING_SELECTION_TILE);
    }

    public void createGame(int numPlayerGame, int numGoalCards, String nickname) {
        this.numPlayerGame = numPlayerGame;
        this.numGoalCards = numGoalCards;
        addPlayer(nickname);
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

    private boolean checkEndGame() {
        int numPlayersOnline = (int) this.game.getPlayers().stream().filter(x -> x.isOnline()).count();
        if(numPlayersOnline == 0){
            this.game.setModelState(ModelState.END_GAME);
            return true;
        }
        if(numPlayersOnline == 1){
            //TODO start timer instead of end game
            this.game.setModelState(ModelState.END_GAME);
            try {
                this.game.setWinner(this.game.getPlayers().stream().filter(x -> x.isOnline()).collect(Collectors.toList()).get(0));
            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        if(this.game.getPlayers().stream().filter(x -> x.getBookshelf().isFull()).count() > 0) {
            this.game.setModelState(ModelState.END_GAME);
            Player p = this.game.getPlayers().stream().reduce( (a, b) -> {
                try {
                    return a.getTotalPoints() > b.getTotalPoints() ? a:b;
                } catch (WrongArgumentException e) {
                    throw new RuntimeException(e);
                }
            }).orElse(null);
            try {
                this.game.setWinner(p);
            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public void setOfflinePlayer(String nickname) {
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).collect(Collectors.toList()).get(0).setOnline(false);
    }

    public void setOnlinePlayer(String nickname) {
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).collect(Collectors.toList()).get(0).setOnline(true);
    }


    //change firm of the method based
    public void executeCommand(CommandMessage command, UserInputEvent t) {
        Command c = null;
        try {
            switch (t) {
                case SELECTED_TILES -> c = new PickTilesCommand(game.getBoard(), game.getCurrPlayer(), (PickedTilesCommandMessage) command, this.game.getModelState());
                case SELECTED_HAND_TILE -> c = new SelectTileFromHandCommand(game.getCurrPlayer(), (SelectedTileFromHandCommandMessage) command, this.game.getModelState());
                case SELECTED_BOOKSHELF_COLUMN -> c = new SelectColumnCommand(game.getCurrPlayer(), (SelectedColumnMessage) command, this.game.getModelState());
                default -> throw new InvalidCommand();
            }
            c.execute();
            game.resetErrorState();
            nextState();
        } catch (WrongTurnException e) {
            game.setErrorState(command.getNickname(), "Wait for your turn to perform this action. " + e.getMessage());
        } catch (InvalidCommand e) {
            game.setErrorState(command.getNickname(), "You tried to perform the wrong action: " + e.getMessage());
        } catch (WrongArgumentException e){
            game.setErrorState(command.getNickname(), "Your request has an invalid argument: " + e.getMessage());
        }
    }

    private void checkState(UserInputEvent t) throws InvalidCommand{
        ModelState currentGameState = game.getModelState();
        if(currentGameState == ModelState.WAITING_SELECTION_TILE && t != UserInputEvent.SELECTED_TILES) throw new InvalidCommand("waiting for Tile Selection ");
        if(currentGameState == ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN && t != UserInputEvent.SELECTED_BOOKSHELF_COLUMN) throw new InvalidCommand("waiting for Column Selection ");
        if(currentGameState == ModelState.END_GAME) throw new InvalidCommand("game ended");
        if(currentGameState == ModelState.WAITING_3_SELECTION_TILE_FROM_HAND && t != UserInputEvent.SELECTED_HAND_TILE) throw new InvalidCommand("waiting for Tile Selection Hand ");
        if(currentGameState == ModelState.WAITING_2_SELECTION_TILE_FROM_HAND && t != UserInputEvent.SELECTED_HAND_TILE) throw new InvalidCommand("waiting for Tile Selection Hand ");
        if(currentGameState == ModelState.WAITING_1_SELECTION_TILE_FROM_HAND && t != UserInputEvent.SELECTED_HAND_TILE) throw new InvalidCommand("waiting for Tile Selection Hand ");
    }

    private void nextState() {
        ModelState currentGameState = game.getModelState();
        ModelState nextState = null;
        switch (currentGameState) {
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
                    try {
                        game.setCurrPlayer(game.getNextPlayer());
                    } catch (WrongArgumentException e) {
                        throw new RuntimeException(e);
                    }
                    while(!game.getCurrPlayer().isOnline()) {
                        try {
                            game.setCurrPlayer(game.getNextPlayer());
                        } catch (WrongArgumentException e) {
                            throw new RuntimeException(e);
                        }
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

    public void addPlayer(String nickname) {
        if (nicknames.contains(nickname)) return;
        this.nicknames.add(nickname);
        if(nicknames.size() == numPlayerGame) {
            try {
                setupGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removePlayer(String nickname) {
        if(this.game == null)
            this.nicknames.remove(nickname);
    }

    public void removeAllPlayer() {
        this.nicknames = new ArrayList<>();
    }

    public void deleteGame() {
        this.game = null;
        this.numPlayerGame = 0;
        this.numGoalCards = 0;
    }

    public Game getGame() {
        return game;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public int getNumPlayerGame() {
        return numPlayerGame;
    }

    public int getNumGoalCards() {
        return numGoalCards;
    }
}
