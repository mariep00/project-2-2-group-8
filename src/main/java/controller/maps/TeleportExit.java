package controller.maps;

public class TeleportExit extends SpecialFeature {
    public final TeleportEntrance entrance;
    public TeleportExit(TeleportEntrance entrance) {
        this.entrance = entrance;
    }
}
