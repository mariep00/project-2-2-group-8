package gui.mapcreator;

public enum TileType {
    FLOOR(ImageType.BASE),
    WALL(ImageType.BASE),
    TELEPORT(ImageType.BASE),
    GUARD(ImageType.CHARACTER),
    SHADED(ImageType.SHADED),
    TARGET_AREA(ImageType.AREA),
    SPAWN_AREA_GUARDS(ImageType.AREA),
    SPAWN_AREA_INTRUDERS(ImageType.AREA);

    ImageType imageType;
    TileType(ImageType imageType) {
        this.imageType = imageType;
    }

    enum ImageType {
        BASE,
        CHARACTER,
        OTHER,
        SHADED,
        AREA;
    }
}
