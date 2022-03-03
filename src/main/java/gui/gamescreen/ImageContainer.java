package gui.gamescreen;

import controller.HashMap;
import javafx.scene.image.Image;

import java.util.BitSet;

public final class ImageContainer {
    private static ImageContainer imageContainer = null;

    private final HashMap<BitSet, Image> hashMap = new HashMap<>(100);
    private final Image[] wallImages;
    private final Image floor;
    private final Image shaded;
    private final Image targetArea;
    private final Image spawnAreaGuards;
    private final Image spawnAreaIntruders;

    private final Image guard;

    private ImageContainer() {
        wallImages = new Image[9];
        wallImages[0] = new Image(this.getClass().getResource("/tiles/base/wall_center.jpg").toString());
        wallImages[1] = new Image(this.getClass().getResource("/tiles/base/wall_front.jpg").toString());
        wallImages[2] = new Image(this.getClass().getResource("/tiles/base/wall_left.jpg").toString());
        wallImages[3] = new Image(this.getClass().getResource("/tiles/base/wall_right.jpg").toString());
        wallImages[4] = new Image(this.getClass().getResource("/tiles/base/wall_sides.jpg").toString());
        wallImages[5] = new Image(this.getClass().getResource("/tiles/base/wall_top.jpg").toString());
        wallImages[6] = new Image(this.getClass().getResource("/tiles/base/wall_top_corner_left.jpg").toString());
        wallImages[7] = new Image(this.getClass().getResource("/tiles/base/wall_top_corner_right.jpg").toString());
        wallImages[8] = new Image(this.getClass().getResource("/tiles/base/wall_top_cornered.jpg").toString());

        floor = new Image(this.getClass().getResource("/tiles/base/floor.jpg").toString());
        shaded = new Image(this.getClass().getResource("/tiles/overlay/shaded.png").toString());
        targetArea = new Image(this.getClass().getResource("/tiles/overlay/target_area.png").toString());
        spawnAreaGuards = new Image(this.getClass().getResource("/tiles/overlay/spawn_area_guards.png").toString());
        spawnAreaIntruders = new Image(this.getClass().getResource("/tiles/overlay/spawn_area_intruders.png").toString());

        guard = new Image(this.getClass().getResource("/tiles/character/guard/south/guard_south_1.png").toString());

        initializeWallHashMap();
    }
    public static ImageContainer getInstance() {
        if (imageContainer == null) imageContainer = new ImageContainer();
        return imageContainer;
    }

    public Image getWall(BitSet bitSet) {
        return hashMap.get(bitSet);
    }

    public HashMap<BitSet, Image> getHashMap() { return hashMap; }

    public Image getFloor() { return floor; }
    public Image getShaded() { return shaded; }
    public Image getTargetArea() { return targetArea; }
    public Image getSpawnAreaGuards() { return spawnAreaGuards; }
    public Image getSpawnAreaIntruders() { return spawnAreaIntruders; }

    public Image getGuard() { return guard; }

    public Image getWallCenter() { return wallImages[0]; }
    public Image getWallFront() { return wallImages[1]; }
    public Image getWallLeft() { return wallImages[2]; }
    public Image getWallRight() { return wallImages[3]; }
    public Image getWallSides() { return wallImages[4]; }
    public Image getWallTop() { return wallImages[5]; }
    public Image getWallTopCornerLeft() { return wallImages[6]; }
    public Image getWallTopCornerRight() { return wallImages[7]; }
    public Image getWallTopCornered() { return wallImages[8]; }

    private void initializeWallHashMap() {
        /*
        Populate the map, which is used to determine which wall image should be shown when constructing the game screen.
        A bitset is used to represent the surrounding walls of the tile to examine.
        A 1 means that there is a wall, a 0 means that it is something else than a wall. This can also be a tile which is outside of the map size.
        x is the tile to examine. Example:
        0   1   1
        0   x   1   --> This means that x should be a wall with a left side
        0   1   1

        The order of the bitset (indices) is:
        0   1   2
        3   x   4
        5   6   7

        Note: A bitset has to be read/written from right to left, so the indices above correspond to the indices in the bitset as:
        7 6 5 4 3 2 1 0
        */
        hashMap.add(stringToByteSet("00011111"), getWallFront());
        hashMap.add(stringToByteSet("00011000"), getWallFront());
        hashMap.add(stringToByteSet("11111000"), getWallTop());
        hashMap.add(stringToByteSet("11111111"), getWallCenter());
        hashMap.add(stringToByteSet("01111111"), getWallRight());
        hashMap.add(stringToByteSet("11011111"), getWallLeft());
        hashMap.add(stringToByteSet("11111110"), getWallCenter());
        hashMap.add(stringToByteSet("11111011"), getWallCenter());
        hashMap.add(stringToByteSet("11010000"), getWallTopCornerLeft());
        hashMap.add(stringToByteSet("01101000"), getWallTopCornerRight());
        hashMap.add(stringToByteSet("00010110"), getWallFront());
        hashMap.add(stringToByteSet("01101011"), getWallRight());
        hashMap.add(stringToByteSet("11010110"), getWallLeft());
        hashMap.add(stringToByteSet("00111111"), getWallFront());
        hashMap.add(stringToByteSet("00001011"), getWallFront());
        hashMap.add(stringToByteSet("11110110"), getWallLeft());
        hashMap.add(stringToByteSet("11111100"), getWallTop());
        hashMap.add(stringToByteSet("11111001"), getWallTop());
        hashMap.add(stringToByteSet("11101011"), getWallRight());
        hashMap.add(stringToByteSet("01101111"), getWallRight());
        hashMap.add(stringToByteSet("10011111"), getWallFront());
        hashMap.add(stringToByteSet("11010111"), getWallLeft());
        hashMap.add(stringToByteSet("01101111"), getWallRight());
        hashMap.add(stringToByteSet("01000000"), getWallTopCornered());
        hashMap.add(stringToByteSet("01000010"), getWallSides());
        hashMap.add(stringToByteSet("00000010"), getWallFront());
        hashMap.add(stringToByteSet("00001000"), getWallFront());
        hashMap.add(stringToByteSet("00010000"), getWallFront());
        hashMap.add(stringToByteSet("00001111"), getWallFront());
        hashMap.add(stringToByteSet("10010110"), getWallFront());
        hashMap.add(stringToByteSet("10010111"), getWallFront());
        hashMap.add(stringToByteSet("00101111"), getWallFront());
        hashMap.add(stringToByteSet("10111111"), getWallFront());
        hashMap.add(stringToByteSet("00010111"), getWallFront());
        hashMap.add(stringToByteSet("01100000"), getWallTopCornered());
        hashMap.add(stringToByteSet("11000000"), getWallTopCornered());
        hashMap.add(stringToByteSet("01101010"), getWallRight());
        hashMap.add(stringToByteSet("11011011"), getWallLeft());
        hashMap.add(stringToByteSet("11110000"), getWallTopCornerLeft());
        hashMap.add(stringToByteSet("11111101"), getWallTop());
        hashMap.add(stringToByteSet("11110010"), getWallLeft());
        hashMap.add(stringToByteSet("11011000"), getWallTopCornerLeft());
        hashMap.add(stringToByteSet("10010000"), getWallFront());
        hashMap.add(stringToByteSet("00000000"), getWallFront());
        hashMap.add(stringToByteSet("11000010"), getWallSides());
        hashMap.add(stringToByteSet("11010010"), getWallLeft());
        hashMap.add(stringToByteSet("01000110"), getWallSides());
        hashMap.add(stringToByteSet("01010110"), getWallSides());
        hashMap.add(stringToByteSet("01111110"), getWallRight());
        hashMap.add(stringToByteSet("11010100"), getWallTopCornerLeft());
        hashMap.add(stringToByteSet("11101000"), getWallTopCornerRight());
        hashMap.add(stringToByteSet("00101011"), getWallFront());
        hashMap.add(stringToByteSet("01010111"), getWallSides());
        hashMap.add(stringToByteSet("00000110"), getWallFront());
        hashMap.add(stringToByteSet("01101001"), getWallTopCornerRight());
        hashMap.add(stringToByteSet("11101001"), getWallTopCornerRight());
        hashMap.add(stringToByteSet("11100010"), getWallSides());
        hashMap.add(stringToByteSet("11111010"), getWallCenter());
        hashMap.add(stringToByteSet("01011111"), getWallSides());
        hashMap.add(stringToByteSet("01000111"), getWallSides());
        hashMap.add(stringToByteSet("00000111"), getWallFront());
        hashMap.add(stringToByteSet("11101111"), getWallRight());
    }

    private BitSet stringToByteSet(String string) {
        return BitSet.valueOf(new long[] { Long.parseLong(string, 2) });
    }

}
