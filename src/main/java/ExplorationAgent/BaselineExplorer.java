package ExplorationAgent;

public class BaselineExplorer {


    //the agent probably has to take in the
    public BaselineExplorer(){

    }

    /*
    1. Check coordinates of spawning point (save as x0 and y0)
    2. Walk in any direction until you find a wall
    3. Once facing a wall, rotate 90º clockwise
    4. While not at x0, y0:
       - Go one space forward
       - Rotate 90º anti clock wise
       - Check if in front of wall
            -if wall, then, rotate 90º clockwise and check wall
            -if not wall, move forward, rotate anticlockwise 90º and check wall again
        *** All outer wall has been checked ***
     5. if at x0,y0; find a space that has not been explored and has explored spaces to the right
        - save x and y coordinates
        While not at x, y:
       - Go one space forward
       - Rotate 90º anti clock wise
       - Check if in front of wall
            -if wall, then, rotate 90º clockwise and check wall
            -if not wall, move forward, rotate anticlockwise 90º and check wall again
        Once at x,y find a new that has not been explored and has explored spaces to the right
     6. Repeat step 5 until map has been explored

     */
}

