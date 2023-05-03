package org.myshelfie.network.messages.commandMessages;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.LocatedTile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Unit tests for the CommandMessages classes
 */
public class CommandMessagesTest {
    @Test
    public void testPickedTilesCommandMessage() {
        List<LocatedTile> tiles = new ArrayList<>();
        tiles.add(new LocatedTile(ItemType.BOOK, 0, 0));
        PickedTilesCommandMessage m = new PickedTilesCommandMessage("nickname", tiles);
        assertInstanceOf(CommandMessage.class, m);
        assertInstanceOf(PickedTilesCommandMessage.class, m);
    }

    @Test
    public void testSelectedColumnMessage() {
        SelectedColumnMessage m = new SelectedColumnMessage("nickname", 0);
        assertInstanceOf(CommandMessage.class, m);
        assertInstanceOf(SelectedColumnMessage.class, m);
    }

    @Test
    public void testSelectedTileFromHandCommandMessage() {
        SelectedTileFromHandCommandMessage m = new SelectedTileFromHandCommandMessage("nickname", 0, ItemType.BOOK);
        assertInstanceOf(CommandMessage.class, m);
        assertInstanceOf(SelectedTileFromHandCommandMessage.class, m);
    }

    @Test
    public void testCommandMessageWrapper() {
        SelectedColumnMessage m = new SelectedColumnMessage("nickname", 0);
        CommandMessageWrapper wrapper = new CommandMessageWrapper(m, UserInputEvent.SELECTED_BOOKSHELF_COLUMN);
        assertInstanceOf(CommandMessageWrapper.class, wrapper);
    }
}
