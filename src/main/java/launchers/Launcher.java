package launchers;

import gamelogic.maps.MapBuilder;
import gamelogic.controller.Controller;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
import gamelogic.maps.ScenarioMap;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;


public class Launcher {

    /**
     * Launcher without GUI
     */
    public static void main(String[] args) throws URISyntaxException {
        String fileName = "testmap.txt"; // ONLY change this string to the name of the map file you want to use (make sure the txt file is located in: resources/maps/)

        URL url = Launcher.class.getClassLoader().getResource("maps/"+fileName);
        if (url != null) {
            ScenarioMap scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
            Controller controller = new ControllerExploration(scenarioMap, new EndingExploration(scenarioMap));
            controller.init();
            controller.engine();
        }
        else System.out.println("******** Map named " + fileName + " not found ********");
    }
}