package org.myshelfie.controller;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the GameControllerSaver class, by checking that what is saved
 * is the same as what is loaded.
 */
public class GameControllerSaverTest {

    @Test
    public void testSavingAndLoadingFromDisk() {
        HashMap<String, GameController> gameControllers = new HashMap<>();
        HashMap<String, GameController> gameControllersLoaded;
        gameControllers.put("testGame", new GameController("test", 2, 1));
        assertDoesNotThrow(() -> {
            GameControllerSaver.save(gameControllers);
        });
        gameControllersLoaded = assertDoesNotThrow(GameControllerSaver::load);
        assertEquals(gameControllers.keySet(), gameControllersLoaded.keySet());
    }
}
