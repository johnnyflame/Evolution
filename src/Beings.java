
import java.util.Random;
import java.awt.*;



/**
 * Parent class for Creatures and Monsters 
 * @author Johnny Flame Lee
 */
public abstract class Beings {
    
    protected int speed;
    protected int locationX,locationY,width,height;
    protected enum Directions{NORTH,SOUTH,EAST,WEST};
    Random random = new Random();
    
//******************************************************************************
//    Graphical elements
//******************************************************************************
    
    protected Color colour;
    protected int moveX = 10;
    protected int moveY = 10;
    
    
    
   public int randomRange (int lo, int hi){
    Random r = new Random();
    return r.nextInt(hi - lo + 1) + lo;
  } 
    
    
    
  
   
   public Beings(){
       this.width = 10;
       this.height = this.width;
       locationX = random.nextInt(World.MAP_WIDTH);
       locationY = random.nextInt(World.MAP_HEIGHT);
         this.colour = Color.BLUE;
   }
    
    
    public int getLocationX(){
        return this.locationX;
    }
    
    public int getLocationY(){
        return this.locationY;
    }
    
    
    /**
     * Search neighborhood squares in the order of [right/east, left/west,down/south,
     * and up/north]. Only 4 location is returned because the creature can only travel
     * to adjacent squares. (may expand to diagonal later if possible).
     *
     * @param target the target to search for.
     * Target will be one of:
     * Monster,Food,Poison and Creature. Their location information will be stored in a
     * 2D array of type int.
     *
     * @return the direction of the nearest target.
     * If there are multiple targets in the neighborhood, return the direction with
     * higher number of targets.
     */
    public Directions nearest(int [][] target){
        Directions direction = null;
        int quantity = 0;
        //DEBUG 2D array and XY coordinates.
        //NOTE: May yield off by 1 error, come back and -1 off MAP_WIDTH if it happens.
        //NOTE2: Check if X Y coordinates are in correct order once frontend is completed.
        //while(notFound){
        //int width = 1;
        //for(int i = this.locationX-width; i <= this.locationX+width; i++){
        //   for(int j = this.locationX-width; j<= this.locationX+width; j++)
        // width++;
        if (this.locationX < World.MAP_WIDTH-1 && target[this.locationY][this.locationX+1] > quantity){
            direction = Directions.EAST;
        }
        if (this.locationX > 0 && target[this.locationY][this.locationX-1] > quantity){
            direction = Directions.WEST;
            quantity = target[this.locationY][this.locationX-1];
        }
        if (this.locationY > 0 && target[this.locationY-1][this.locationX] > quantity){
            direction = Directions.NORTH;
            quantity = target[this.locationY-1][this.locationX];
        }
        if (this.locationY < World.MAP_HEIGHT-1 && target[this.locationY+1][this.locationX] > quantity){
            direction = Directions.SOUTH;
        }
        
        if (quantity == 0){
            if (this.locationX < World.MAP_WIDTH-2 && target[this.locationY][this.locationX+2] > quantity){
            direction = Directions.EAST;
        }
        if (this.locationX > 1 && target[this.locationY][this.locationX-2] > quantity){
            direction = Directions.WEST;
            quantity = target[this.locationY][this.locationX-2];
        }
        if (this.locationY > 1 && target[this.locationY-2][this.locationX] > quantity){
            direction = Directions.NORTH;
            quantity = target[this.locationY-2][this.locationX];
        }
        if (this.locationY < World.MAP_HEIGHT-2 && target[this.locationY+2][this.locationX] > quantity){
            direction = Directions.SOUTH;
        }
         
        }
        
        return direction;
    }
    
    /**
     * 
     * Responsible for the movement of creatures and monsters.
     * @param d the direction to move in.
     * @mapInfo updating the map of the object that moved.
     */
    public void move (int [][] map, Directions d){
        if (this.locationY > 0 && d == Directions.NORTH){
            map[this.locationY][this.locationX]--;
            map[this.locationY-1][this.locationX]++;
            this.locationY--;
  //          System.out.println("Up");
            
        }
        else if (this.locationY == 0 && d == Directions.NORTH){
            map[this.locationY][this.locationX]--;
            map[World.MAP_HEIGHT-1][this.locationX]++;
            this.locationY = World.MAP_HEIGHT-1;
  //          System.out.println("Up");
            
        }
        else if (this.locationX > 0 && d == Directions.WEST){
            map[this.locationY][this.locationX]--;
            map[this.locationY][this.locationX-1]++;
            this.locationX--;
    //        System.out.println("Left");
        }
        
        else if (this.locationX == 0 && d == Directions.WEST){
            map[this.locationY][this.locationX]--;
            map[this.locationY][World.MAP_WIDTH-1]++;
            this.locationX = World.MAP_WIDTH-1;
    //        System.out.println("Left");
        }
        
        else if (this.locationY < World.MAP_HEIGHT - 1 && d == Directions.SOUTH){
            map[this.locationY][this.locationX]--;
            map[this.locationY+1][this.locationX]++;
            this.locationY++;
    //        System.out.println("Down");
        }
        else if (this.locationY ==  World.MAP_HEIGHT - 1 && d == Directions.SOUTH){
            map[this.locationY][this.locationX]--;
            map[World.MAP_HEIGHT - (World.MAP_HEIGHT -1)][this.locationX]++;
            this.locationY = World.MAP_HEIGHT - (World.MAP_HEIGHT -1);
    //        System.out.println("Down");
        }
        else if (this.locationX < World.MAP_WIDTH -1 && d == Directions.EAST){
            map[this.locationY][this.locationX]--;
            map[this.locationY][this.locationX+1]++;
            this.locationX ++;
    //        System.out.println("Right");
        }
        else if (this.locationX == World.MAP_WIDTH -1 && d == Directions.EAST){
            map[this.locationY][this.locationX]--;
            map[this.locationY][World.MAP_WIDTH- (World.MAP_WIDTH -1)]++;
            this.locationX = World.MAP_WIDTH- (World.MAP_WIDTH -1);
    //        System.out.println("Right");
        }
    }
    
    public void moveAway (int [][] map,Directions d){
        if (this.locationY < World.MAP_HEIGHT - 1  && d == Directions.NORTH){
             map[this.locationY][this.locationX]--;
             map[this.locationY+1][this.locationX]++; 
            this.locationY++;
    //        System.out.println("Down");
        }
        if (this.locationY == World.MAP_HEIGHT - 1  && d == Directions.NORTH){
             map[this.locationY][this.locationX]--;
             map[World.MAP_HEIGHT - (World.MAP_HEIGHT -1)][this.locationX]++; 
            this.locationY = World.MAP_HEIGHT - (World.MAP_HEIGHT -1);
    //        System.out.println("Down");
        }
        else if (this.locationX < World.MAP_WIDTH -1  && d == Directions.WEST){
             map[this.locationY][this.locationX]--;
             map[this.locationY][this.locationX+1]++;
            this.locationX++;
   //         System.out.println("Right");
        }
        
        else if (this.locationX == World.MAP_WIDTH -1  && d == Directions.WEST){
             map[this.locationY][this.locationX]--;
             map[this.locationY][World.MAP_WIDTH - (World.MAP_WIDTH -1)]++;
            this.locationX = World.MAP_WIDTH - (World.MAP_WIDTH -1);
   //         System.out.println("Right");
        }
        else if (this.locationY > 0 && d == Directions.SOUTH){
             map[this.locationY][this.locationX]--;
             map[this.locationY-1][this.locationX]++;
            this.locationY--;
   //         System.out.println("Up");
        }
        else if (this.locationY == 0 && d == Directions.SOUTH){
             map[this.locationY][this.locationX]--;
             map[World.MAP_HEIGHT - 1][this.locationX]++;
            this.locationY = World.MAP_HEIGHT -1;
   //         System.out.println("Up");
        }
        else if (this.locationX  > 0 && d == Directions.EAST){
            map[this.locationY][this.locationX]--;
            map[this.locationY][this.locationX-1]++;
            this.locationX --;
    //        System.out.println("Left");
        }
        else if (this.locationX  == 0 && d == Directions.EAST){
            map[this.locationY][this.locationX]--;
            map[this.locationY][World.MAP_WIDTH - (World.MAP_WIDTH -1)]++;
            this.locationX = World.MAP_WIDTH - (World.MAP_WIDTH -1);
    //        System.out.println("Left");
        }
    }
    
    protected Directions randomWalk(){
        int r = random.nextInt(4);
        switch (r) {
            case 0:
                return Directions.NORTH;
            case 1:
                return Directions.SOUTH;
            case 2:
                return Directions.EAST;
            default:
                return Directions.WEST;
        }
    }
    
    public abstract void display (Graphics g);
    
}





    
    
    
    
    
    
    
 