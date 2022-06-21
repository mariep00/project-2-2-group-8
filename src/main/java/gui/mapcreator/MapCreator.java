package gui.mapcreator;

import datastructures.Vector2D;
import gamelogic.maps.ScenarioMap;
import gui.ScenarioMenu;
import gui.util.ImageContainer;
import gui.gamescreen.Tile;
import gui.util.HelperGUI;
import gui.util.MainGUI;
import gui.util.TransitionInterface;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MapCreator extends Application implements TransitionInterface {
    private Stage stage;
    private final ArrayList<Transition> transitions = new ArrayList<>();
    public static ListItem selectedListItem;
    private final ImageContainer imageContainer = ImageContainer.getInstance();

    private int nrOfTilesWidth = 80;
    private int nrOfTilesHeight = 80;
    private TileMapCreator[][] tiles;
    private GridPane gridPane;

    private TextField widthField;
    private TextField heightField;
    private TextField numGuards;
    private TextField numIntruders;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        ListView<ListItem> listView = getListViewPopulated();
        listView.setPrefWidth(175);

        VBox vbox = new VBox();
        widthField = new TextField(String.valueOf(nrOfTilesWidth));
        widthField.setPromptText("Width");
        heightField = new TextField(String.valueOf(nrOfTilesHeight));
        heightField.setPromptText("Height");
        Button updateSize = new Button("Update size");

        numGuards = new TextField();
        numGuards.setPromptText("Number of guards");
        numIntruders = new TextField();
        numIntruders.setPromptText("Number of intruders");
        VBox vboxTextFields = new VBox(3, widthField, heightField, updateSize, numGuards, numIntruders);

        vbox.getChildren().addAll(vboxTextFields, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        selectedListItem = listView.getItems().get(0);
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        tiles = new TileMapCreator[nrOfTilesHeight][nrOfTilesWidth];
        for (int x = 0; x < nrOfTilesWidth; x++) {
            for (int y = 0; y < nrOfTilesHeight; y++) {
                TileMapCreator tile = new TileMapCreator(new TileImageMapCreator(imageContainer.getFloor(), TileType.FLOOR));
                gridPane.add(tile, x, y);
                tiles[y][x] = tile;
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        Button buttonReset = new Button("Reset map");
        Button buttonLoad = new Button("Load map");
        Button buttonExport = new Button("Export map");
        Button buttonPlayGame = new Button("Play game with map");
        HBox hbox = new HBox(15, buttonReset, buttonLoad, buttonExport, buttonPlayGame);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane borderPane = new BorderPane(scrollPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        borderPane.setLeft(vbox);
        borderPane.setTop(hbox);
        BorderPane.setMargin(hbox, new Insets(5, 5, 5, 5));

        scrollPane.setStyle("-fx-focus-color: transparent;");
        listView.setStyle("-fx-focus-color: transparent;");

        listView.setOpacity(0);
        scrollPane.setOpacity(0);
        hbox.setOpacity(0);

        Scene scene = new Scene(borderPane);
        MainGUI.setupScene(this, scene, stage);
        stage.setScene(scene);
        loadSceneTransition(listView, scrollPane, hbox);

        widthField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                this.nrOfTilesWidth = Integer.parseInt(widthField.getText());
                resizeGrid();
            }
        });
        heightField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                this.nrOfTilesHeight = Integer.parseInt(heightField.getText());
                resizeGrid();
            }
        });
        updateSize.setOnAction(e -> {
            this.nrOfTilesWidth = Integer.parseInt(widthField.getText());
            this.nrOfTilesHeight = Integer.parseInt(heightField.getText());
            resizeGrid();
        });

        listView.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedListItem = listView.getSelectionModel().getSelectedItem();
        });

        buttonReset.setOnAction(e -> {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Tile) {
                    ((TileMapCreator) node).resetTile();
                }
            }
        });
        gridPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            Bounds boundsGridPaneInScene = gridPane.localToScene(gridPane.getBoundsInLocal());
            Bounds topLeftCellBound = gridPane.getCellBounds(0, 0); // Used to take into account centering of the map while clicking / dragging

            double posX = e.getSceneX() - boundsGridPaneInScene.getMinX() - topLeftCellBound.getMinX();
            double posY = e.getSceneY() - boundsGridPaneInScene.getMinY() - topLeftCellBound.getMinY();

            int indexX = (int) Math.floor((posX / Tile.tileSize));
            int indexY = (int) Math.floor((posY / Tile.tileSize));

            if (indexX < tiles[0].length && indexY < tiles.length && indexX >= 0 && indexY >= 0) {
                if (e.isPrimaryButtonDown()) {
                    tiles[indexY][indexX].setImageToSelected();
                }
                else if (e.isSecondaryButtonDown()) {
                    tiles[indexY][indexX].resetTile();
                }
            }
        });
        buttonLoad.setOnAction(e -> {
            ScenarioMap scenarioMap = HelperGUI.loadMapWithFileChooser(stage);

            if (scenarioMap != null) {
                gridPane.getChildren().clear();

                nrOfTilesWidth = scenarioMap.getWidth();
                nrOfTilesHeight = scenarioMap.getHeight();
                widthField.setText(String.valueOf(nrOfTilesWidth));
                heightField.setText(String.valueOf(nrOfTilesHeight));

                tiles = new TileMapCreator[nrOfTilesHeight][nrOfTilesWidth];
                gamelogic.maps.Tile[][] tilesScenarioMap = scenarioMap.getMapGrid();

                for (int x = 0; x < scenarioMap.getWidth(); x++) {
                    for (int y = 0; y < scenarioMap.getHeight(); y++) {
                        TileMapCreator tile;
                        if (tilesScenarioMap[y][x].getType() == gamelogic.maps.Tile.Type.WALL) {
                            // It's a wall. Create a tile with the right wall image
                            tile = new TileMapCreator(new TileImageMapCreator(imageContainer.getWall(HelperGUI.getBitSetSurroundingWalls(tilesScenarioMap, x, y)), TileType.WALL));
                        } else if (tilesScenarioMap[y][x].getType() == gamelogic.maps.Tile.Type.TELEPORT_ENTRANCE) {
                            tile = new TileMapCreator(new TileImageMapCreator(imageContainer.getTeleport(), TileType.TELEPORT));
                        }
                        // If it's none of above, it's always a floor
                        else {
                            tile = new TileMapCreator(new TileImageMapCreator(imageContainer.getFloor(), TileType.FLOOR));
                        }

                        if (tilesScenarioMap[y][x].isShaded()) {
                            tile.setShaded(imageContainer.getShaded(), TileType.SHADED);
                        }
                        if (tilesScenarioMap[y][x].getType() == gamelogic.maps.Tile.Type.TARGET_AREA) {
                            tile.setTargetArea(imageContainer.getTargetArea(), TileType.TARGET_AREA);
                        }

                        gridPane.add(tile, x, y);
                        tiles[y][x] = tile;
                    }
                }
                for (Vector2D pos : scenarioMap.getSpawnAreaGuards()) {
                    tiles[pos.y][pos.x].setSpawnArea(imageContainer.getSpawnAreaGuards(), TileType.SPAWN_AREA_GUARDS);
                }
                for (Vector2D pos : scenarioMap.getSpawnAreaIntruders()) {
                    tiles[pos.y][pos.x].setSpawnArea(imageContainer.getSpawnAreaIntruders(), TileType.SPAWN_AREA_INTRUDERS);
                }
            }
        });
        buttonExport.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) exportMap(selectedFile);
        });
        buttonPlayGame.setOnAction(e -> {
            int[] mapBuildIndices = mapBuildIndices();
            ScenarioMap scenarioMap = new ScenarioMap();
            scenarioMap.createMap(nrOfTilesWidth-mapBuildIndices[0]-(nrOfTilesWidth-mapBuildIndices[1]), nrOfTilesHeight-mapBuildIndices[2]-(nrOfTilesHeight-mapBuildIndices[3]));
            for (int x = mapBuildIndices[0]; x < mapBuildIndices[1]; x++) {
                for (int y = mapBuildIndices[2]; y < mapBuildIndices[3]; y++) {
                    if (tiles[y][x].isWall()) scenarioMap.insertElement((x-mapBuildIndices[0]), (y-mapBuildIndices[2]), gamelogic.maps.Tile.Type.WALL);
                    else if (tiles[y][x].isSpawnAreaGuards()) scenarioMap.insertSpawnAreaGuard((x-mapBuildIndices[0]), (y-mapBuildIndices[2]));
                    else if (tiles[y][x].isSpawnAreaIntruders()) scenarioMap.insertSpawnAreaIntruder((x-mapBuildIndices[0]), (y-mapBuildIndices[2]));
                    else if (tiles[y][x].isTargetArea()) {
                        scenarioMap.insertElement((x-mapBuildIndices[0]), (y-mapBuildIndices[2]), gamelogic.maps.Tile.Type.TARGET_AREA);
                        scenarioMap.insertTargetArea(x-mapBuildIndices[0], y-mapBuildIndices[2]);
                    }
                    else if (tiles[y][x].isTeleport()) scenarioMap.setTeleport((x-mapBuildIndices[0]), (y-mapBuildIndices[2]), (x-mapBuildIndices[0]), (y-mapBuildIndices[2]), (x-mapBuildIndices[0])+1, (y-mapBuildIndices[2])+1, 0);
                    if (tiles[y][x].isShaded()) scenarioMap.setShaded((x-mapBuildIndices[0]), (y-mapBuildIndices[2]));
                }
            }
            scenarioMap.setNumGuards(isStringNumeric(numGuards.getText()) ? Integer.parseInt(numGuards.getText()) : 0);
            scenarioMap.setNumIntruders(isStringNumeric(numIntruders.getText()) ? Integer.parseInt(numIntruders.getText()) : 0);
            scenarioMap.setMapName("Map created in the map creator");
            quitSceneTransition(() -> new ScenarioMenu(scenarioMap).start(stage), vbox, scrollPane, hbox);
        });
    }

    private void resizeGrid() {
        gridPane.getChildren().remove(0, gridPane.getChildren().size());
        TileMapCreator[][] tmp = new TileMapCreator[nrOfTilesHeight][nrOfTilesWidth];
        for (int x = 0; x < tmp[0].length; x++) {
            for (int y = 0; y < tmp.length; y++) {
                if (x < tiles[0].length && y < tiles.length) {
                    tmp[y][x] = tiles[y][x];
                }
                else {
                    tmp[y][x] = new TileMapCreator(new TileImageMapCreator(imageContainer.getFloor(), TileType.FLOOR));
                }
                gridPane.add(tmp[y][x], x, y);
            }
        }
        tiles = tmp;
    }

    private ListView<ListItem> getListViewPopulated() {
        ListView<ListItem> listItemsView = new ListView();
        listItemsView.getItems().add(new ListItem("Floor", TileType.FLOOR,imageContainer.getFloor()));
        listItemsView.getItems().add(new ListItem("Wall front", TileType.WALL,imageContainer.getWallFront()));
        listItemsView.getItems().add(new ListItem("Wall center", TileType.WALL,imageContainer.getWallCenter()));
        listItemsView.getItems().add(new ListItem("Wall left", TileType.WALL,imageContainer.getWallLeft()));
        listItemsView.getItems().add(new ListItem("Wall right", TileType.WALL,imageContainer.getWallRight()));
        listItemsView.getItems().add(new ListItem("Wall sides", TileType.WALL,imageContainer.getWallSides()));
        listItemsView.getItems().add(new ListItem("Wall top", TileType.WALL,imageContainer.getWallTop()));
        listItemsView.getItems().add(new ListItem("Wall top corner left", TileType.WALL,imageContainer.getWallTopCornerLeft()));
        listItemsView.getItems().add(new ListItem("Wall top corner right", TileType.WALL,imageContainer.getWallTopCornerRight()));
        listItemsView.getItems().add(new ListItem("Wall top cornered", TileType.WALL,imageContainer.getWallTopCornered()));
        listItemsView.getItems().add(new ListItem("Teleport", TileType.TELEPORT,imageContainer.getTeleport()));
        listItemsView.getItems().add(new ListItem("Shaded", TileType.SHADED,imageContainer.getShaded()));
        listItemsView.getItems().add(new ListItem("Spawn area guards", TileType.SPAWN_AREA_GUARDS, imageContainer.getSpawnAreaGuards()));
        listItemsView.getItems().add(new ListItem("Spawn area intruders", TileType.SPAWN_AREA_INTRUDERS, imageContainer.getSpawnAreaIntruders()));
        listItemsView.getItems().add(new ListItem("Target area", TileType.TARGET_AREA, imageContainer.getTargetArea()));

        listItemsView.setCellFactory(listView -> new ListCell<>() {
            @Override
            public void updateItem(ListItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setText(item.name);
                    ImageView imageView = new ImageView(item.image);
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    setGraphic(imageView);

                }
            }
        });

        return listItemsView;
    }

    private void exportMap(File selectedFile) {
        try(PrintWriter writer = new PrintWriter(selectedFile)) {
            int[] mapBuildIndices = mapBuildIndices();

            writer.println("name = test scenario");
            writer.println("gameMode = 0");
            writer.println("height = " + (nrOfTilesHeight-mapBuildIndices[2]-(nrOfTilesHeight-mapBuildIndices[3])));
            writer.println("width = " + (nrOfTilesWidth-mapBuildIndices[0]-(nrOfTilesWidth-mapBuildIndices[1])));
            writer.println("numGuards = " + (isStringNumeric(numGuards.getText()) ? numGuards.getText() : 0));
            writer.println("numIntruders = " + (isStringNumeric(numIntruders.getText()) ? numIntruders.getText() : 0));
            writer.println("baseSpeedIntruder = 14.0");
            writer.println("sprintSpeedIntruder = 20.0");
            writer.println("baseSpeedGuard = 14.0");
            writer.println("timeStep = 0.1");

            for (int x = mapBuildIndices[0]; x < mapBuildIndices[1]; x++) {
                for (int y = mapBuildIndices[2]; y < mapBuildIndices[3]; y++) {
                    TileMapCreator tile = tiles[y][x];
                    if (tile.isWall()) writer.println("wall = " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]));
                    else if (tile.isSpawnAreaGuards()) writer.println("spawnAreaGuards = " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]));
                    else if (tile.isSpawnAreaIntruders()) writer.println("spawnAreaIntruders = " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]));
                    else if (tile.isTargetArea()) writer.println("targetArea = " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]));
                    else if (tile.isTeleport()) writer.println("teleport = " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + ((x-mapBuildIndices[0])+1) + " " + ((y-mapBuildIndices[2])+1) + " " + 0);
                    if (tile.isShaded()) writer.println("shaded = " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]) + " " + (x-mapBuildIndices[0]) + " " + (y-mapBuildIndices[2]));
                }
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private int[] mapBuildIndices() {
        int[] indices = {1, tiles[0].length-1, 1, tiles.length-1}; // first column, last column, first row, last row
        for (int y = 0; y < tiles.length; y++) {
            if (!tiles[y][0].isWall()) indices[0] = 0;
            if (!tiles[y][tiles[0].length-1].isWall()) indices[1] = tiles[0].length;
        }
        for (int x = 0; x < tiles[0].length; x++) {
            if (!tiles[0][x].isWall()) indices[2] = 0;
            if (!tiles[tiles.length-1][x].isWall()) indices[3] = tiles.length;
        }
        return indices;
    }

    private boolean isStringNumeric(String string) {
        if (string == null) { return false; }
        try {
            double tmp = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }
}

class ListItem {
    String name;
    TileType tileType;
    Image image;

    public ListItem(String name, TileType tileType, Image image) {
        this.name = name;
        this.tileType = tileType;
        this.image = image;
    }

    public String toString() { return name; }
}