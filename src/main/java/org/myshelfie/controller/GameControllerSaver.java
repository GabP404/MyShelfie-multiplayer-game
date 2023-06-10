package org.myshelfie.controller;

import java.io.*;
import java.util.HashMap;
import org.myshelfie.controller.GameController;

/**
 * This class provides functionality to save and load gameControllers to/from a file.
 */
public class GameControllerSaver {
    /**
     * Saves the gameControllers map to a default file.
     *
     * @param gameControllers The map of game controllers to be saved.
     */
    public static void save(HashMap<String, GameController> gameControllers) throws IOException {
        saveToFile(gameControllers, Configuration.getServerBackupFileName());
    }

    /**
     * Loads the gameControllers map from a default file.
     *
     * @return The map of game controllers loaded from the file.
     */
    public static HashMap<String, GameController> load() throws IOException, ClassNotFoundException {
        return loadFromFile(Configuration.getServerBackupFileName());
    }

    /**
     * Saves the gameControllers map to a specified file.
     * The method is synchronized to avoid concurrent access to the file.
     *
     * @param gameControllers The map of game controllers to be saved.
     * @param filePath The path of the file where the game controllers will be saved.
     */
    public synchronized static void saveToFile(HashMap<String, GameController> gameControllers, String filePath) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
        out.writeObject(gameControllers);
    }

    /**
     * Loads the gameControllers map from a specified file.
     *
     * @param filePath The path of the file from where the game controllers will be loaded.
     * @return The map of game controllers loaded from the file.
     */
    public static HashMap<String, GameController> loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        HashMap<String, GameController> gameControllers = null;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
        gameControllers = (HashMap<String, GameController>) in.readObject();
        return gameControllers;
    }
}
