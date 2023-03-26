package org.myshelfie.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.myshelfie.model.util.Pair;
import org.json.*;

public class PersonalGoalDeck {
    private List<PersonalGoalCard> cards;

    public PersonalGoalDeck() {
        //this.cards = OpenJson.getPersonalGoalCards();
    }

    public List<PersonalGoalCard> draw(int x) {
        List<PersonalGoalCard> drawnCards = new ArrayList<PersonalGoalCard>();
        List<Integer> positions= new Random().ints(0, cards.size())
                .distinct()
                .limit(x)
                .boxed()
                .collect(Collectors.toList());
        for(Integer i: positions) {
            drawnCards.add(cards.get(i));
        }
        return drawnCards;
    }


}
