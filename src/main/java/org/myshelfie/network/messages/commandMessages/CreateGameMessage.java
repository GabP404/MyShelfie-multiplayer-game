package org.myshelfie.network.messages.commandMessages;

public class CreateGameMessage extends CommandMessage {
    private final String gameName;
    private int numPlayers;
    private boolean simplifiedRules;

    public CreateGameMessage(String nickname, String gameName, int numPlayers, boolean simplifiedRules) {
        super(nickname);
        this.gameName = gameName;
        this.numPlayers = numPlayers;
        this.simplifiedRules = simplifiedRules;
    }

    public String getGameName() {
        return gameName;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public boolean isSimplifiedRules() {
        return simplifiedRules;
    }
}
