import java.awt.*;
import java.util.Random;
/**
 *
 * @author Johnny Flame Lee 
 */


public class Creature extends Beings{
    
    //states of the creatures
    private int energy_level;
    protected int [] chromosome = new int [13];
    //  private Behaviour [] listOfActions = new Behaviour [6];
    private Random r = new Random();
    private boolean alive;
    protected double fitnessNormalised;
    boolean isMutated = false;
    

    
   

    /**
     *
     * default constructor of a new creature, initializing it's chromosome by
     * giving the weights (array position 5 to 12) a random real number.
     * 
     */
    
    public Creature(){
        energy_level = 100; 
    
        alive = true;
        //setting location by random.
        locationX = r.nextInt(World.MAP_WIDTH);
        locationY = r.nextInt(World.MAP_HEIGHT);
        
        //initialising chromosomes
        chromosome[0] = r.nextInt(2);
        chromosome[1] = r.nextInt(2);
      
        
        for (int i = 2; i < 5;i++){
            chromosome[i] = r.nextInt(4);
        }
        chromosome[6] = r.nextInt(5); //default action
        
        //weights
        for (int i = 7; i < chromosome.length;i++){
            chromosome[i] = r.nextInt(100000);
        }
        
        //graphical elements
        this.colour = Color.YELLOW;
        
              
    }  //default Creature constructor
    
    public Creature(int health){
        energy_level = health; 
        alive = true;
        //setting location by random.
        locationX = r.nextInt(World.MAP_WIDTH);
        locationY = r.nextInt(World.MAP_HEIGHT);
        
        //initialising chromosomes
        chromosome[0] = r.nextInt(2);
        chromosome[1] = r.nextInt(2);
      
        
        for (int i = 2; i < 5;i++){
            chromosome[i] = r.nextInt(4);
        }
        chromosome[6] = r.nextInt(5); //default action
        
        //weights
        for (int i = 7; i < chromosome.length;i++){
            chromosome[i] = r.nextInt(100000);
        }
        
        //graphical elements
        this.colour = Color.YELLOW;
        
              
    }//constructor with health passed as parameter
    
    public Creature(Creature parent1,Creature parent2){ //this will be the breeding constructor.
        energy_level = 100;
        alive = true;
        //setting location by random.
        locationX = r.nextInt(World.MAP_WIDTH);
        locationY = r.nextInt(World.MAP_HEIGHT);
        //graphical elements
        this.colour = parent1.colour;
        
        
        
        int schemaLine = r.nextInt(6);
        
        for (int i = 0; i < schemaLine;i++){
            this.chromosome[i] = parent1.chromosome[i];
            this.chromosome[i+7] = parent1.chromosome[i+7];
        }
        
        for (int i = schemaLine; i < 6; i++){
            this.chromosome[i] = parent2.chromosome[i];
            this.chromosome[i+7] = parent2.chromosome[i+7];
        }
                
        if (parent1.fitnessNormalised > parent2.fitnessNormalised){
            this.chromosome[6] = parent1.chromosome[6];
        }
        else{
             this.chromosome[6] = parent2.chromosome[6];
        }
      
        //if creature is mutated, the default action is not inherited, but randomly generated instead.
        int mutation = r.nextInt(500);
        if (mutation ==5){
            isMutated = true;
            this.colour = Color.MAGENTA;
            this.chromosome[6] = 0; //may change mutation trait later.          
        }
    }
        

    
    //House keeping methods
    public int getHealth(){
        return energy_level;
    }
    
    public double getFitness(){
        return energy_level;
    }
    
    
    
            
    public boolean isAlive(){
        return alive;
    }
    
    /**
     * Checks current square for mushroom
     * @return true if mushroom is found
     */
    public boolean mushPresent(){
        return World.mushrooms_location[this.locationY][this.locationX]>0;
    }
    
    public boolean foodPresent(){
        
        return World.strawberries_location[this.locationY][this.locationX]>0;
    }
    
    public void eat(int [][] item){
        if (item == World.mushrooms_location){
            World.mushrooms_location[this.locationY][this.locationX]--;
            energy_level = 0;
            alive = false;
            System.out.println("Ate a mushroom...dead as fried chicken.");
         
        }
       else if (item == World.strawberries_location){
           energy_level += 10;
           World.strawberries_location[this.locationY][this.locationX]--;
           System.out.println ("Health bonus!"); 
           
       }
    }
    
    
    /**
     * This is the heavy weight method responsible for generating the creature's
     * next action.
     * 
     * @return the index for the next action.
     */
   private int actionIndex(){
        
        int [] actions = new int [6];
       
        if (foodPresent()){
            actions[1] = chromosome[1];
        }else actions[1] = 0;
        
        if (mushPresent()) {
            actions[0] = chromosome[0];
        }else actions[0] = 0;
     
        if (nearest(World.mushrooms_location) != null){
            actions[2] = chromosome [2];
        }else actions[2]= 0;
        
        if (nearest(World.strawberries_location) != null){
            actions[3] = chromosome [3];
        }else actions[3]= 0;
        
        if (nearest(World.creature_location) != null){
            actions[4] = chromosome [4];
        }else actions[4]= 0;
        
        if (nearest(World.monster_location) != null){
            actions[5] = chromosome [5];
        }else actions[5]= 0;
        
       //count the action list
       int counter = 0;
       for (int i = 0; i < actions.length;i++){
           counter += actions[i];
       }
       //if actionlist is empty go to default
       if (counter == 0){
           return 6;
       }
       
       //otherwise decide with weights.
       else{
           
           int weightIndex = 7; //start searching at position 7.
           int maxWeight = chromosome[weightIndex]; //weight at position 7
           for (int i = weightIndex; i < chromosome.length;i++){  //from 7-12
               if (actions[i-7] != 0 && chromosome[i] > maxWeight){
                   maxWeight = chromosome[i];
                   weightIndex = i; //weights section minus the function squares.
                   //weightIndex should be from index 7-12.
//                   System.out.println("possible index:" + weightIndex);
               }
           }
           //weights section minus the function squares.
           return weightIndex -7;
        }
   }
   

   
   public void nextAction(){
       
       //checking for bugs here.
   //    System.out.println("Action index is:" + actionIndex());
   
   if (actionIndex() ==6){
       if(chromosome[actionIndex()] == 0){
           move(World.creature_location,randomWalk());
       }
       else if (chromosome[actionIndex()] == 1){
           move(World.creature_location,Directions.NORTH);
       }
       else if (chromosome[actionIndex()] == 2){
           move(World.creature_location,Directions.SOUTH);
       }
       else if (chromosome[actionIndex()] == 3){
           move(World.creature_location,Directions.EAST);
       }
       else if (chromosome[actionIndex()] == 4){
           move(World.creature_location,Directions.WEST);
       }
   }
   
   //action 0: if mush is present
   else if (actionIndex() ==0){
       if (chromosome[actionIndex()] == 1 && mushPresent()){
           eat(World.mushrooms_location);         
       }
       else if (chromosome[actionIndex()] == 0){
           
       System.out.println("Didn't eat mushroom.");
       }
   }
   //action 1: if strawb is present
   else if (actionIndex()==1){
          
           if (chromosome[actionIndex()] ==1 && foodPresent()){
               eat(World.strawberries_location);    
               //  System.out.println("Delicious.");
           }
           else if (chromosome[actionIndex()] == 0){
               System.out.println("eatFood?" + chromosome[actionIndex()] );
               System.out.println("Didn't eat food.");
//               fitnessValue -=5;
           }
       }
       
       //if action 2 is selected: action on nearest_mushroom
       else if(actionIndex()==2){
           //going towards nearest mushroom.
           switch (chromosome[actionIndex()]) {
               case 1:
                   move(World.creature_location,nearest(World.mushrooms_location));
                   break;
               case 2:
                   //move away from mushroom.
                   moveAway(World.creature_location,nearest(World.mushrooms_location));
                   break;
               case 3:
                   move(World.creature_location,randomWalk());
                   break;
               default:
                   break;
           }
       }
       //if action 3: act on nearest_food is selected.
       else if (actionIndex()==3){
           switch (chromosome[actionIndex()]) {
               case 1:
                   move(World.creature_location,nearest(World.strawberries_location));
//                   fitnessValue+=5;
                   break;
               case 2:
                   //move away from food.
                   moveAway(World.creature_location,nearest(World.strawberries_location));
//                   fitnessValue-=5;
                   break;
               case 3:
                   move(World.creature_location,randomWalk());
//                   fitnessValue-=2;
                   break;
               default:
                   break;
           }
       }
       
       //action on other creatures.
        else if (actionIndex()==4){
           switch (chromosome[actionIndex()]) {
               case 1:
                   move(World.creature_location,nearest(World.creature_location));
                   break;
               case 2:
                   //move away from creature.
                   moveAway(World.creature_location,nearest(World.creature_location));
                   break;
               case 3:
                   move(World.creature_location,randomWalk());
                   break;
               default:
                   break;
           }
       }
       
       //on monsters
        else if (actionIndex()==4){
           switch (chromosome[actionIndex()]) {
               case 1:
                   move(World.creature_location,nearest(World.monster_location));
//                   fitnessValue-=5;
                   break;
               case 2:
                   //move away from creature.
                   moveAway(World.creature_location,nearest(World.monster_location));
//                   fitnessValue+=5;
                   break;
               case 3:
                   move(World.creature_location,randomWalk());
                   break;
               default:
                   break;
           }
       }
       
       
       
       
   }
   
   
   public void decrementHealth(){
       this.energy_level--;
   }
   
   public void statusCheck(){
       if (energy_level == 0) {
           alive = false;
           World.creature_location[this.locationY][this.locationX]--;
       }
   }
   
   public void monsterCheck(){
       if (World.monster_location[this.locationY][this.locationX] > 0){
           this.energy_level = 0;
           alive = false;
           World.creature_location[this.locationY][this.locationX]--;
       }
   }
   
   public void nextTurn(){
       nextAction();
       decrementHealth();
       
   }

   
   
   public void display (Graphics g){  
    g.setColor(this.colour);
    g.fillRect(this.locationX * World.RATIO,this.locationY* World.RATIO,this.width,this.height);

}
  
}
     
 
   
   
    



    

    
    
   
    
    
    
   