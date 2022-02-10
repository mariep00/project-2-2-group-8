import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;

public class Map {
    private int map_height;
    private int map_width;
    private Area spawn_area_intruders;
    private Area spawn_area_guards;
    private Area target_area;
    private ArrayList<Area> walls;
    private ArrayList<TelePortal> teleports;
    private ArrayList<Area> shaded;

    private final static Charset ENCODING = StandardCharsets.UTF_8;

    public Map(String map_name) {

        // initialize variables
        walls = new ArrayList<>(); // create list of walls
        shaded = new ArrayList<>(); // create list of low-visability areas
        teleports = new ArrayList<>(); // create list of teleports e.g. stairs

        // read scenario
        Path filePath = Paths.get(map_name); // get path
        System.out.println(filePath);
        readMap(filePath);
    }

    public void readMap(Path filePath){
        try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
            while (scanner.hasNextLine()){
                parseLine(scanner.nextLine());
            }
        }
        catch(Exception e)
        {
        }
    }

    protected void parseLine(String line){
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
                Area tmp;
                switch(id)
                {
                    case "height":
                        this.map_height = Integer.parseInt(value);
                        break;
                    case "width":
                        this.map_width = Integer.parseInt(value);
                        break;
                    case "targetArea":
                        this.target_area = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaIntruders":
                        this.spawn_area_intruders = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaGuards":
                        this.spawn_area_guards = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        break;
                    case "wall":
                        tmp = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        this.walls.add(tmp);
                        break;
                    case "shaded":
                        tmp = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        this.shaded.add(tmp);
                        break;
                    case "teleport":
                        TelePortal teletmp = new TelePortal(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]),Integer.parseInt(items[4]),Integer.parseInt(items[5]),Double.parseDouble(items[6]));
                        this.teleports.add(teletmp);
                        break;
                    /*case "texture":
                        // still to do. First the coordinates, then an int with texture type and then a double with orientation
                     */
                }
            }
        }
    }



}
