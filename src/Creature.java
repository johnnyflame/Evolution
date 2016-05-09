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
    protected double fitness;
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
    
 
    public Creature(Creature parent1,Creature parent2){ //this will be the breeding constructor.
        energy_level = 100;
        alive = true;
        //setting location by random.
        locationX = r.nextInt(World.MAP_WIDTH);
        locationY = r.nextInt(World.MAP_HEIGHT);
        //graphical elements
        
        if(r.nextBoolean()){
            this.colour = parent1.colour;
        }
        else{
            this.colour = parent2.colour;
        }
        
        
        
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
        else if (parent2.fitnessNormalised > parent1.fitnessNormalised){
             this.chromosome[6] = parent2.chromosome[6];
        }
        else {
            boolean coinToss = r.nextBoolean();
            if (coinToss == true){
                this.chromosome[6] = parent1.chromosome[6];
            }
            else{
                this.chromosome[6] = parent2.chromosome[6];
            }
        }
      
        //if creature is mutated, the default action is not inherited, but randomly generated instead.
        int mutation = r.nextInt(100);
        if (mutation == 5){
            isMutated = true;
            this.colour = Color.MAGENTA;
            
            
            if (r.nextInt(10) < 4){ // 40% chance mutation occurs in one of the action chromosomes.
                
                int mutateActionIndex = r.nextInt(6);
                       
                                if (mutateActionIndex < 2){
                                    this.chromosome[mutateActionIndex] = r.nextInt(2); //for position 0 or 1
                                }
                                else{
                                    this.chromosome[mutateActionIndex] = r.nextInt(4);
                                }
               
            }
            else{ //mutate weights;
                 int weightsIndex = r.nextInt(6);//mutate weights
                 
                 for (int i = 7;i < this.chromosome.length;i++){
                     if (i == (weightsIndex + 7)){
                         if (r.nextBoolean()){
                             this.chromosome[i] += 200;
                         }
                         else{
                             this.chromosome[i] -= 200;
                         }
                     }
                 }
            }
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
   //         System.out.println("Ate a mushroom...dead as fried chicken.");
         
        }
       else if (item == World.strawberries_location){
           energy_level += 40; //reward 
           World.strawberries_location[this.locationY][this.locationX]--;
   //        System.out.println ("Health bonus!"); 
           
       }
    }
    
    
    /**
     * This is the heavy weight method responsible for generating the creature's
     * next action.
     * 
     * @return the index for the next action.
     */
   private int actionIndex(){
        
        Integer [] actions = new Integer [6];
       
        if (foodPresent()){
            actions[1] = chromosome[1];
        }else actions[1] = null;
        
        if (mushPresent()) {
            actions[0] = chromosome[0];
        }else actions[0] = null;
     
        if (nearest(World.mushrooms_location) != null){
            actions[2] = chromosome [2];
        }else actions[2]= null;
        
        if (nearest(World.strawberries_location) != null){
            actions[3] = chromosome [3];
        }else actions[3]= null;
        
        if (nearest(World.creature_location) != null){
            actions[4] = chromosome [4];
        }else actions[4]= null;
        
        if (nearest(World.monster_location) != null){
            actions[5] = chromosome [5];
        }else actions[5]= null;
        
       //count the action list
      boolean hasAction = false;
       for (int i = 0; i < actions.length;i++){
           if (actions[i] != null){
               hasAction = true;
           }
       }
       //if actionlist is empty go to default
       if (hasAction == false){
   //       System.out.println("returned default (action 7)");
           return 6;
       }
       //otherwise decide with weights.
       else{          
           int weightIndex = 7; //start searching at position 7.
           int maxWeight = chromosome[weightIndex]; //weight at position 7
           for (int i = weightIndex; i < chromosome.length;i++){  //from 7-12
               if (actions[i-7] != null && chromosome[i] > maxWeight){
                   maxWeight = chromosome[i];
                   weightIndex = i; //weights section minus the function squares.
                   //weightIndex should be from index 7-12.
//                   System.out.println("possible index:" + weightIndex);
               }
           }
           //weights section minus the function squares.
    //         System.out.println("returned value: "+ (weightIndex -7));
           return weightIndex -7;
        }
      
   }
   

   
   public void nextAction(int index){
       
       //checking for bugs here.
   //    System.out.println("Action index is:" + actionIndex());
   
   if (index ==6){
       System.out.println("Default: index 6");
       if(chromosome[index] == 0){
            System.out.println("Default: index 6, 0");
           move(World.creature_location,randomWalk());
       }
       else if (chromosome[index] == 1){
             System.out.println("Default: index 6, 1");
           move(World.creature_location,Directions.NORTH);
       }
       else if (chromosome[index] == 2){
             System.out.println("Default: index 6, 2");
           move(World.creature_location,Directions.SOUTH);
       }
       else if (chromosome[index] == 3){
             System.out.println("Default: index 6, 3");
           move(World.creature_location,Directions.EAST);
       }
       else if (chromosome[index] == 4){
             System.out.println("Default: index 6, 4");
           move(World.creature_location,Directions.WEST);
       }
   }
   
   //action 0: if mush is present
   else if (index ==0){
         System.out.println("Default: index 0");
       if (chromosome[index] == 1 && mushPresent()){
             System.out.println("Default: index 0, 1");
             eat(World.mushrooms_location);
       }
       else{
           System.out.println("Default: index 0, 0");
           move(World.creature_location,randomWalk());
           //     System.out.println("Didn't eat mushroom.");
       }
      
   }
   //action 1: if strawb is present
   else if (index==1){
          
       if (chromosome[index] ==1 && foodPresent()){
           System.out.println("Default: index 1, 1");
           eat(World.strawberries_location);
           //  System.out.println("Delicious.");
       }
       else{        
           System.out.println("Default: index 1, 0");
           move(World.creature_location,randomWalk());
           //             System.out.println("eatFood?" + chromosome[actionIndex()] );
           //              System.out.println("Didn't eat food.");
//               fitnessValue -=5;
           }
       }
       
       //if action 2 is selected: action on nearest_mushroom
       else if(index==2){
           //going towards nearest mushroom.
           switch (chromosome[index]) {
               case 1:
                   System.out.println(" index+ " + index + ",1");
                   move(World.creature_location,nearest(World.mushrooms_location));
                   energy_level-=10;
                   break;
               case 2:
                   //move away from mushroom.
                   System.out.println("index+ " + index + ",2");
                   moveAway(World.creature_location,nearest(World.mushrooms_location));
//                   energy_level += 2; //"encouragement for evading poison"
                   break;
               case 3:
                   System.out.println(" index+ " + index + ",3");
                   if(r.nextBoolean()){
                       move(World.creature_location,nearest(World.mushrooms_location));
                   }
                   else{
                       moveAway(World.creature_location,nearest(World.mushrooms_location));
                   }
                   break;
               default:
                   move(World.creature_location,randomWalk());
                   break;
           }
       }
       //if action 3: act on nearest_food is selected.
       else if (index==3){
           switch (chromosome[index]) {
               case 1:
                   move(World.creature_location,nearest(World.strawberries_location));
//                    energy_level += 2;
//                   fitnessValue+=5;
                   break;
               case 2:
                   //move away from food.
                   moveAway(World.creature_location,nearest(World.strawberries_location));
//                   fitnessValue-=5;
                   break;
               case 3:
                   if(r.nextBoolean()){
                       move(World.creature_location,nearest(World.strawberries_location));
                   }
                   else{
                       moveAway(World.creature_location,nearest(World.strawberries_location));
                   }
                   break;
               default:
                    move(World.creature_location,randomWalk());
                   break;
           }
       }
       
       //action on other creatures.
        else if (index==4){
           switch (chromosome[index]) {
               case 1:
                   move(World.creature_location,nearest(World.creature_location));
                   break;
               case 2:
                   //move away from creature.
                   moveAway(World.creature_location,nearest(World.creature_location));          
                   break;
               case 3:
                  if(r.nextBoolean()){
                       move(World.creature_location,nearest(World.creature_location));
                   }
                   else{
                       moveAway(World.creature_location,nearest(World.creature_location));
                   }
                   break;
               default:
                    move(World.creature_location,randomWalk());
                   break;
           }
       }
       
       //on monsters
        else if (index==4){
           switch (chromosome[actionIndex()]) {
               case 1:
                   move(World.creature_location,nearest(World.monster_location));
//                   fitnessValue-=5;
                   break;
               case 2:
                   //move away from creature.
                   moveAway(World.creature_location,nearest(World.monster_location));
                      energy_level+=10;
                   break;
               case 3:
                   if(r.nextBoolean()){
                       move(World.creature_location,nearest(World.monster_location));
                   }
                   else{
                       moveAway(World.creature_location,nearest(World.monster_location));
                   }
                   break;
               default:
                   move(World.creature_location,randomWalk());
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
       nextAction(actionIndex());
       decrementHealth();
       
   }

   
   
   public void display (Graphics g){  
    g.setColor(this.colour);
    g.fillRect(this.locationX * World.RATIO,this.locationY* World.RATIO,this.width,this.height);

}
  
}
     
 
   
   
    



    

    
    
   
    
    
    
   