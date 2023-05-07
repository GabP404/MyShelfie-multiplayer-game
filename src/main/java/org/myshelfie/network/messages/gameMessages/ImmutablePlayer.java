package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.Player;
import org.myshelfie.model.ScoringToken;
import org.myshelfie.model.Tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ImmutablePlayer  implements Serializable {
    private final String nickname;
    private final List<ScoringToken> commonGoalTokens;
    private final Boolean hasFinalToken;
    private final PersonalGoalCard personalGoal;
    private final ImmutableBookshelf bookshelf;
    private final List<Tile> tilesPicked;
    private static int DIM_TILESPICKED = 3;

    public ImmutablePlayer(Player p) {
        this.nickname = p.getNickname();
        this.commonGoalTokens = new ArrayList<>(p.getCommonGoalTokens());
        this.hasFinalToken = p.getHasFinalToken();
        this.personalGoal = p.getPersonalGoal();
        this.bookshelf = new ImmutableBookshelf(p.getBookshelf());
        this.tilesPicked = new ArrayList<>(p.getTilesPicked());
    }

    public String getNickname() {
        return nickname;
    }

    public List<ScoringToken> getCommonGoalTokens() {
        return new ArrayList<>(commonGoalTokens);
    }

    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    public PersonalGoalCard getPersonalGoal() {
        return personalGoal;
    }

    public ImmutableBookshelf getBookshelf() {
        return bookshelf;
    }

    public List<Tile> getTilesPicked() {
        return new ArrayList<>(tilesPicked);
    }


    /**
     * @return number of points earnt from ScoringTokens
     */
    public int getPointsScoringTokens() {
        int x = 0;
        for (ScoringToken s :
                this.commonGoalTokens) {
            x+= s.getPoints();
        }
        return x;
    }
}
