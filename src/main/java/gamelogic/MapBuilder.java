package gamelogic;

import gamelogic.maps.ScenarioMap;
import gamelogic.maps.Tile;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MapBuilder {
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    private ScenarioMap map = new ScenarioMap();
    private final File file;

    private int width;
    private int height;
    private float scaling;

    public MapBuilder(File file) {
        this.file = file;
        readMap();
    }

    public MapBuilder (String pathString) {
        this(new File(pathString));
    }

    public ScenarioMap getMap () {
        return map;
    }

    private void readMap() {
        try (Scanner scanner =  new Scanner(file, ENCODING.name())){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(line.contains("//")) {
                    int index = line.indexOf("//");
                    line = line.substring(0, index);
                    //System.out.println(line);
                    parseLine(line);
                }
                else {
                    //System.out.println(line);
                    parseLine(line);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void parseLine (String line) {
         //use a second Scanner to parse the content of each line
         try(Scanner scanner = new Scanner(line)){
            scanner.useDelimiter("=");
            if (scanner.hasNext()){
                // read id value pair
                String id = scanner.next();
                String value = scanner.next();
                // trim excess spaces
                value=value.trim();
                id=id.trim();
                // in case multiple parameters
                String[] items=value.split(" ");
                //System.out.println(id);
                //System.out.println(value);;

                switch(id)
                {
                    case "name":
                        map.setName(value);
                        break;
                    case "gameFile":
                        break;
                    case "gameMode":
                        map.setGameMode(Integer.parseInt(value)); // 0 is exploration, 1 evasion pursuit game
                        break;
                    case "scaling":
                        scaling = Float.parseFloat(value);
                        break;
                    case "height":
                        height = Integer.parseInt(value);
                        break;
                    case "width":
                        width = Integer.parseInt(value);
                        map.createMap(width, height);
                        break;
                    case "numGuards":
                        map.setNumGuards(Integer.parseInt(value));
                        break;
                    case "numIntruders":
                        map.setNumIntruders(Integer.parseInt(value));
                        break;
                    case "baseSpeedIntruder":
                        map.setBaseSpeedIntruder(Double.parseDouble(value));
                        break;
                    case "sprintSpeedIntruder":
                        map.setSprintSpeedIntruder(Double.parseDouble(value));
                        break;
                    case "baseSpeedGuard":
                        map.setBaseSpeedGuard(Double.parseDouble(value));
                        break;
                    case "targetArea":
                        map.insertElement(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Tile.Type.TARGET_AREA);
                        break;
                    case "spawnAreaIntruders":
                        map.insertSpawnAreaIntruder(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaGuards":
                        map.insertSpawnAreaGuard(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        break;
                    case "wall":
                        map.insertElement(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Tile.Type.WALL);
                        break;
                    case "teleport":
                        map.setTeleport(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Integer.parseInt(items[4]), Integer.parseInt(items[5]), Double.parseDouble(items[6]));
                        break;
                    case "shaded":
                        map.setShaded(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                        break;
                    case "texture":
                        break;
                        // still to do. First the coordinates, then an int with texture type and then a double with orientation
                    case "timeStep":
                        map.setTimeStep(Double.parseDouble(value));
                        break;
                    case "guardViewAngle":
                        map.setGuardViewAngle(Double.parseDouble(value));
                        break;
                    case "intruderViewAngle":
                        map.setIntruderViewAngle(Double.parseDouble(value));
                        break;
                    case "guardViewRange":
                        map.setGuardViewRange(Double.parseDouble(value));
                        break;
                    case "intruderViewRange":
                        map.setIntruderViewRange(Double.parseDouble(value));
                        break;
                    case "numberMarkers":
                        map.setNumberMarkers(Integer.parseInt(value));
                        break;
                    case "smellingDistance":
                        map.setSmellingDistance(Integer.parseInt(value));
                        break;
                }
            }
        }
    }  
    
}
