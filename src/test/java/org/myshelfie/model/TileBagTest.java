//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.myshelfie.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TileBagTest {

    @Test
    public void testTileBagCreation() {
        TileBag tileBag = new TileBag();
        Assertions.assertNotNull(tileBag);
    }

    @Test
    public void testDrawItemTile() {
        TileBag tileBag = new TileBag();
        Tile drawnTile = tileBag.drawItemTile();
        Assertions.assertNotNull(drawnTile);
    }

    @Test
    public void testTileBagSize() {
        TileBag tileBag = new TileBag();
        int expectedSize = ItemType.values().length * 22;
        Assertions.assertEquals(expectedSize, tileBag.getSize());

        for(int i = 0; i < expectedSize; ++i) {
            tileBag.drawItemTile();
        }

        Assertions.assertEquals(0, tileBag.getSize());
    }
}
