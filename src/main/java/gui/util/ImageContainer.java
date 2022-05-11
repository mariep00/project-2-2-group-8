package gui.util;

import gui.gamescreen.AgentType;
import javafx.scene.image.Image;

import java.util.BitSet;

/**
 * Singleton class which contains all images
 */
public final class ImageContainer {
    private static ImageContainer imageContainer = null;
    private final Image[] wallImages;
    private final Image floor;
    private final Image shaded;
    private final Image targetArea;
    private final Image spawnAreaGuards;
    private final Image spawnAreaIntruders;
    private final Image undiscovered;
    private final Image vision;
    private final Image teleport;
    private final Image[] guardNorth;
    private final Image[] guardEast;
    private final Image[] guardSouth;
    private final Image[] guardWest;
    private final Image intruderNorth;
    private final Image intruderEast;
    private final Image intruderSouth;
    private final Image intruderWest;

    private final Image step;
    private final Image play;
    private final Image stop;
    private final Image showVision;
    private final Image hideVision;

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
        undiscovered = new Image(this.getClass().getResource("/tiles/overlay/undiscovered.png").toString());
        vision = new Image(this.getClass().getResource("/tiles/overlay/vision.png").toString());
        teleport =  new Image(this.getClass().getResource("/tiles/base/teleport.png").toString());

        guardNorth = new Image[4];
        for (int i = 0; i < guardNorth.length; i++) {
            guardNorth[i] = new Image(this.getClass().getResource("/tiles/character/guard/north/guard_north_"+(i+1)+".png").toString());
        }
        guardEast = new Image[4];
        for (int i = 0; i < guardEast.length; i++) {
            guardEast[i] = new Image(this.getClass().getResource("/tiles/character/guard/east/guard_east_"+(i+1)+".png").toString());
        }
        guardSouth = new Image[4];
        for (int i = 0; i < guardSouth.length; i++) {
            guardSouth[i] = new Image(this.getClass().getResource("/tiles/character/guard/south/guard_south_"+(i+1)+".png").toString());
        }
        guardWest = new Image[4];
        for (int i = 0; i < guardWest.length; i++) {
            guardWest[i] = new Image(this.getClass().getResource("/tiles/character/guard/west/guard_west_"+(i+1)+".png").toString());
        }

        intruderNorth = new Image(this.getClass().getResource("/tiles/character/intruder/intruder_north_1.png").toString());
        intruderEast = new Image(this.getClass().getResource("/tiles/character/intruder/intruder_east_1.png").toString());
        intruderSouth = new Image(this.getClass().getResource("/tiles/character/intruder/intruder_south_1.png").toString());
        intruderWest = new Image(this.getClass().getResource("/tiles/character/intruder/intruder_west_1.png").toString());

        step = new Image(this.getClass().getResource("/ui/step.png").toString());
        play = new Image(this.getClass().getResource("/ui/play.png").toString());
        stop = new Image(this.getClass().getResource("/ui/stop.png").toString());
        showVision = new Image(this.getClass().getResource("/ui/vision_show.png").toString());
        hideVision = new Image(this.getClass().getResource("/ui/vision_hide.png").toString());
    }

    /**
     * Singleton instance method
     * @return      the singleton imagecontainer instance
     */
    public static ImageContainer getInstance() {
        if (imageContainer == null) imageContainer = new ImageContainer();
        return imageContainer;
    }

    /**
     * Method to get the right wall image, corresponding to the given bitset
     * @param bitSet    bitset which represents if the surrounding tiles are also walls or not (see comments for more info)
     * @return          image of a wall
     */
    public Image getWall(BitSet bitSet) {
        /*
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
        if (!bitSet.get(6)) return getWallFront(); // No wall below
        else if (!bitSet.get(1)) { // No wall above
            if (!bitSet.get(3) && !bitSet.get(4)) return getWallTopCornered(); // No wall above nor left nor right
            else if (!bitSet.get(3)) { // No wall above nor left
                if (!bitSet.get(7)) return getWallTopCornered(); // No wall above nor left, but wall on right is a front wall
                else return getWallTopCornerLeft();
            }
            else if (!bitSet.get(4)) { // No wall above nor right
                if (!bitSet.get(5)) return getWallTopCornered(); // No wall above nor right, but wall on left is a front wall
                else return getWallTopCornerRight();
            }
            else if (!bitSet.get(7) && !bitSet.get(5)) return getWallTopCornered(); // No wall above, but wall on right and left are both front walls
            else if (!bitSet.get(7)) return getWallTopCornerRight(); // No wall above, but wall on right is a front wall
            else if (!bitSet.get(5)) return getWallTopCornerLeft(); // No wall above, but wall on left is a front wall
            else return getWallTop();
        }
        else if (!bitSet.get(3) && !bitSet.get(4)) return getWallSides(); // No wall on left nor right
        else if (!bitSet.get(3)) { // No wall on left
            if (!bitSet.get(7)) return getWallSides(); // But wall on right is a front wall
            else return getWallLeft();
        }
        else if (!bitSet.get(4)) { // No wall on right
            if (!bitSet.get(5)) return getWallSides(); // But wall on left is a front wall
            return getWallRight();
        }
        else if (bitSet.get(3) && bitSet.get(4)) { // Wall on left and right
            if (!bitSet.get(5) && !bitSet.get(7)) return getWallSides(); // But both wall on left and right are a front wall
            else if (!bitSet.get(5)) return getWallLeft(); // But the wall on the left is a front wall
            else if (!bitSet.get(7)) return getWallRight(); // But the wall on the right is a front wall
            else return getWallCenter();
        }
        else if (bitSet.get(4) && !bitSet.get(7)) return getWallRight(); // Wall on right which is a front wall
        else if (bitSet.get(3) && !bitSet.get(5)) return getWallLeft(); // Wall on left which is a front wall
        else return getWallCenter();
    }

    /**
     * Method to the the image of an agent, corresponding to the given agenttype
     * @param agentType     the agenttype of the image that needs to be returned
     * @return              image of the agent
     */
    public Image getAgent(AgentType agentType, double direction) {
        if (agentType == AgentType.GUARD) {
            if (direction == 0) return guardEast[0];
            else if (direction == 90) return guardSouth[0];
            else if (direction == 180) return guardWest[0];
            else if (direction == 270) return guardNorth[0];
        }
        else if (agentType == AgentType.INTRUDER) {
            if (direction == 0) return intruderNorth;
            else if (direction == 90) return intruderEast;
            else if (direction == 180) return intruderSouth;
            else if (direction == 270) return intruderWest;
        }
        return null;
    }

    public Image getFloor() { return floor; }
    public Image getShaded() { return shaded; }
    public Image getTargetArea() { return targetArea; }
    public Image getSpawnAreaGuards() { return spawnAreaGuards; }
    public Image getSpawnAreaIntruders() { return spawnAreaIntruders; }
    public Image getUndiscovered() { return undiscovered; }
    public Image getVision() { return vision; }
    public Image getTeleport() { return teleport; }
    public Image getGuard() { return guardSouth[0]; }
    public Image getWallCenter() { return wallImages[0]; }
    public Image getWallFront() { return wallImages[1]; }
    public Image getWallLeft() { return wallImages[2]; }
    public Image getWallRight() { return wallImages[3]; }
    public Image getWallSides() { return wallImages[4]; }
    public Image getWallTop() { return wallImages[5]; }
    public Image getWallTopCornerLeft() { return wallImages[6]; }
    public Image getWallTopCornerRight() { return wallImages[7]; }
    public Image getWallTopCornered() { return wallImages[8]; }

    public Image getIntruderNorth() { return intruderNorth; }
    public Image getIntruderEast() { return intruderEast; }
    public Image getIntruderSouth() { return intruderSouth; }
    public Image getIntruderWest() { return intruderWest; }

    public Image getStep() { return step; }
    public Image getPlay() { return play; }
    public Image getStop() { return stop; }
    public Image getShowVision() { return showVision; }
    public Image getHideVision() { return hideVision; }
}
