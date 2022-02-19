import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MapBuilder {
    
    private final Path filePath;
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    private ScenarioMap map = new ScenarioMap();

    private int width;
    private int height;
    private float scaling;

    public MapBuilder (String mapFile) {
        filePath = Paths.get(mapFile);
    
        System.out.println(filePath.toString());
        readMapFile();
    }

    public ScenarioMap getMap () {
        return map;
    }

    private void readMapFile() {
        try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                System.out.println(line);
                parseLine(line);
            }
        }
        catch(Exception e)
        {
            System.out.println("uff");
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
                System.out.println(id);
                System.out.println(value);;
    
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
                        //map.createMap(width, height);
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
                        map.insertElement(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), new Tile(Tile.Type.TARGETAREA));
                        break;
                    case "spawnAreaIntruders":
                        map.insertElement(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), new Tile(Tile.Type.SPAWNAREAINTRUDERS));
                        break;
                    case "spawnAreaGuards":
                        map.insertElement(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), new Tile(Tile.Type.SPAWNAREAGUARDS));
                        break;
                    case "wall":
                        map.insertElement(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), new Tile(Tile.Type.WALL));
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
                }
            }
        }
    }  
    
}
