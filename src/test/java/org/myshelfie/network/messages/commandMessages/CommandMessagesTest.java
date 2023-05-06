package org.myshelfie.network.messages.commandMessages;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Unit tests for the CommandMessages classes
 */
public class CommandMessagesTest {
    static UUID uuid = UUID.randomUUID();

    @Test
    public void testPickedTilesCommandMessage() {
        List<LocatedTile> tiles = new ArrayList<>();
        tiles.add(new LocatedTile(ItemType.BOOK, 0, 0));
        PickedTilesCommandMessage m = new PickedTilesCommandMessage("nickname", uuid, tiles);
        assertInstanceOf(CommandMessage.class, m);
        assertInstanceOf(PickedTilesCommandMessage.class, m);
        //Test the getter methods
        assertInstanceOf(List.class, m.getTiles());
        assertInstanceOf(Pair.class, m.getTiles().get(0));
    }

    @Test
    public void testSelectedColumnMessage() {
        SelectedColumnMessage m = new SelectedColumnMessage("nickname", uuid, 0);
        assertInstanceOf(CommandMessage.class, m);
        assertInstanceOf(SelectedColumnMessage.class, m);
        //Test the getter methods
        assertInstanceOf(Integer.class, m.getSelectedColumn());
    }

    @Test
    public void testSelectedTileFromHandCommandMessage() {
        SelectedTileFromHandCommandMessage m = new SelectedTileFromHandCommandMessage("nickname", uuid, 0, ItemType.BOOK);
        assertInstanceOf(CommandMessage.class, m);
        assertInstanceOf(SelectedTileFromHandCommandMessage.class, m);
        //Test the getter methods
        assertInstanceOf(Integer.class, m.getIndex());
        assertInstanceOf(ItemType.class, m.getTileType());
    }

    @Test
    public void testCommandMessage() {
        SelectedColumnMessage m = new SelectedColumnMessage("nickname", uuid, 0);
        assertInstanceOf(CommandMessage.class, m);
        assertEquals("nickname", m.getNickname());
    }

    @Test
    public void testCommandMessageWrapper() {
        SelectedColumnMessage m = new SelectedColumnMessage("nickname", uuid, 0);
        CommandMessageWrapper wrapper = new CommandMessageWrapper(m, UserInputEvent.SELECTED_BOOKSHELF_COLUMN);
        assertInstanceOf(CommandMessageWrapper.class, wrapper);
        //Test the getter methods
        assertInstanceOf(UserInputEvent.class, wrapper.getType());
        assertInstanceOf(CommandMessage.class, wrapper.getMessage());
    }
}
