package org.myshelfie.network.messages.commandMessages;

/**
 * This kind of command message is used to create a game.
 */
public class CreateGameMessage extends CommandMessage {
    private final String gameName;
    private final int numPlayers;
    private final boolean simplifiedRules;

    public CreateGameMessage(String nickname, String gameName, int numPlayers, boolean simplifiedRules) {
        super(nickname);
        this.gameName = gameName;
        this.numPlayers = numPlayers;
        this.simplifiedRules = simplifiedRules;
    }

    /**
     * @return The number of players of the game to be created
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * @return True if the game must be created using simpliified rules or not.
     */
    public boolean isSimplifiedRules() {
        return simplifiedRules;
    }

    /**
     * @return The name of the game to be created.
     */
    public String getGameName() {
        return gameName;
    }
}
