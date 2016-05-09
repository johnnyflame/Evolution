
import java.awt.*;



/**
 *
 * @author Johnny Flame Lee
 */
public class Monster extends Beings{
    
    
    
    
    
    @Override
    public void display (Graphics g){
        g.setColor(this.colour);
        g.fillRect(this.locationX * World.RATIO,this.locationY* World.RATIO,this.width * 2,this.height *2);
    }
    
    
    public void nextAction(){
        
        if (nearest(World.creature_location) == null){
            move(World.monster_location,randomWalk());
            
        }else{
            move(World.monster_location,nearest(World.creature_location)); //update map information
        }
        
    }
}
