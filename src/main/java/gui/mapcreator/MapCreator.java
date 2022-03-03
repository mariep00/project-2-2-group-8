package gui.mapcreator;

import controller.ScenarioMap;
import gui.MainGUI;
import gui.TransitionInterface;
import gui.gamescreen.GameScreen;
import gui.gamescreen.ImageContainer;
import gui.gamescreen.Tile;
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

        tiles = new TileMapCreator[nrOfTilesWidth][nrOfTilesHeight];
        for (int i = 0; i < nrOfTilesWidth; i++) {
            for (int j = 0; j < nrOfTilesHeight; j++) {
                TileMapCreator tile = new TileMapCreator(new TileImageMapCreator(imageContainer.getFloor(), TileType.FLOOR));
                gridPane.add(tile, i, j);
                tiles[i][j] = tile;
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        //scrollPane.setPannable(true); // Causes issues when "painting" while dragging
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        Button buttonReset = new Button("Reset map");
        Button buttonExport = new Button("Export map");
        Button buttonPlayGame = new Button("Play game with map");
        HBox hbox = new HBox(15, buttonReset, buttonExport, buttonPlayGame);
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

            if (indexX < tiles.length && indexY < tiles[0].length && indexX >= 0 && indexY >= 0) {
                if (e.isPrimaryButtonDown()) {
                    tiles[indexX][indexY].setImageToSelected();
                }
                else if (e.isSecondaryButtonDown()) {
                    tiles[indexX][indexY].resetTile();
                }
            }
        });
        buttonExport.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showSaveDialog(stage);
            exportMap(selectedFile);
        });
        buttonPlayGame.setOnAction(e -> {
            ScenarioMap scenarioMap = new ScenarioMap();
            scenarioMap.createMap(nrOfTilesWidth, nrOfTilesHeight, 1);
            for (int x = 0; x < tiles[0].length; x++) {
                for (int y = 0; y < tiles.length; y++) {
                    if (tiles[x][y].isWall()) scenarioMap.insertElement(x, y, controller.Tile.Type.WALL);
                    else if (tiles[x][y].isSpawnAreaGuards()) scenarioMap.insertSpawnAreaGuard(x, y);
                    else if (tiles[x][y].isSpawnAreaIntruders()) scenarioMap.insertSpawnAreaIntruder(x, y);
                    else if (tiles[x][y].isTargetArea()) scenarioMap.insertElement(x, y, controller.Tile.Type.TARGET_AREA);

                    if (tiles[x][y].isShaded()) scenarioMap.setShaded(x, y);
                }
            }
            quitSceneTransition(() -> new GameScreen(scenarioMap).start(stage), vbox, scrollPane, hbox);
        });
    }

    private void resizeGrid() {
        gridPane.getChildren().remove(0, gridPane.getChildren().size());
        TileMapCreator[][] tmp = new TileMapCreator[nrOfTilesHeight][nrOfTilesWidth];
        for (int x = 0; x < tmp.length; x++) {
            for (int y = 0; y < tmp[x].length; y++) {
                if (x < tiles.length && y < tiles[x].length) {
                    tmp[x][y] = tiles[x][y];
                }
                else {
                    tmp[x][y] = new TileMapCreator(new TileImageMapCreator(imageContainer.getFloor(), TileType.FLOOR));
                }
                gridPane.add(tmp[x][y], x, y);
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
        listItemsView.getItems().add(new ListItem("Guard south", TileType.GUARD, imageContainer.getGuard()));
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
            writer.println("name = test scenario");
            writer.println("gameMode = 0");
            writer.println("height = " + nrOfTilesHeight);
            writer.println("width = " + nrOfTilesWidth);
            writer.println("numGuards = " + (isStringNumeric(numGuards.getText()) ? numGuards.getText() : 0));
            writer.println("numIntruders = " + (isStringNumeric(numIntruders.getText()) ? numIntruders.getText() : 0));
            writer.println("baseSpeedIntruder = 14.0");
            writer.println("sprintSpeedIntruder = 20.0");
            writer.println("baseSpeedGuard = 14.0");
            writer.println("timeStep = 0.1");

            for (int x = 0; x < tiles[0].length; x++) {
                for (int y = 0; y < tiles.length; y++) {
                    TileMapCreator tile = tiles[x][y];
                    if (tile.isWall()) writer.println("wall = " + x + " " + y + " " + x + " " + y);
                    else if (tile.isSpawnAreaGuards()) writer.println("spawnAreaGuards = " + x + " " + y + " " + x + " " + y);
                    else if (tile.isSpawnAreaIntruders()) writer.println("spawnAreaIntruders = " + x + " " + y + " " + x + " " + y);
                    else if (tile.isTargetArea()) writer.println("targetArea = " + x + " " + y + " " + x + " " + y);

                    if (tile.isShaded()) writer.println("shaded = " + x + " " + y + " " + x + " " + y);
                }
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
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