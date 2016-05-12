
import java.util.Random;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 *
 * @author theFlame
 */
public class World extends JPanel{
    
    //CONSTANT parameters
    private final int DELAY = 100; //speed of the simluation
    final static int MAP_WIDTH = 100;
    final static int MAP_HEIGHT = 100;
    final static int CREATURE_NUMBER = 400;
    final static int MONSTER_NUMBER = 80;
    final static int RATIO = 10;
    final static int MUSHROOM_NUMBER = 100;
    final static int FOOD_NUMBER = 200;
    final static int setTime = 90;
    final static int SET_HEALTH = 100;
    
    
    static int time;
    
    
    
    static int creaturesLeft;
    static int generation;
    static int mushroomCount;
    static int foodCount;
    static int averageFitness;
    static String fitnessData;
    
    static Random r = new Random();
    static Monster monsters[] = new Monster[MONSTER_NUMBER];
    static Creature [] creatures = new Creature [CREATURE_NUMBER];
   
    //integer array holding the location information of food,poison
    //monster and creatures.
    
    static int [][] creature_location = new int [MAP_HEIGHT][MAP_WIDTH];
    static int [][] monster_location = new int [MAP_HEIGHT][MAP_WIDTH];
    static int [][] mushrooms_location = new int [MAP_HEIGHT][MAP_WIDTH];
    static int [][] strawberries_location = new int [MAP_HEIGHT][MAP_WIDTH];
    
    
    
//Graphical elements
    
    
    
    
    private DrawingPanel drawPanel = new DrawingPanel();
    
    private JLabel genLabel = new JLabel ("Generation: ");
    private JLabel timeLabel = new JLabel ("Time: "); 
    private JLabel creatureLabel = new JLabel ("Creatures left: "  );
    private JLabel maxMushroom = new JLabel ("Starting mushrooms " + MUSHROOM_NUMBER );
    private JLabel maxFood = new JLabel("Starting food: " + FOOD_NUMBER);
    private JLabel fitnessLabel = new JLabel("Average fitness: ");
    private JTextField fitnessDisplay = new JTextField ("----");
    
   
    
    
    private String[] buttonLabels =  {"Initialize", "NextGen", "Pause","Resume"};
    private JButton [] controlButton = new JButton[buttonLabels.length];
    
    Timer timer; 
    

    public World(){
        JPanel controlPanel = new JPanel();
        ButtonListener listener = new ButtonListener();
        timer = new Timer(DELAY, listener);
        timer.start();
        
        controlPanel.setPreferredSize (new Dimension(150, 400));
        controlPanel.add (genLabel);
        controlPanel.add(maxMushroom);
        controlPanel.add(maxFood);
        controlPanel.add (timeLabel);
        controlPanel.add(creatureLabel);
        controlPanel.add(fitnessLabel);
        controlPanel.add(fitnessDisplay);
        
        
      
        add (controlPanel);
        add (drawPanel);
        
    
    /**Using a for loop to initialise the buttons to the labels */ 
    for (int i = 0; i < buttonLabels.length; i++){  
      controlButton[i] = new JButton(buttonLabels[i]);
    }
    
    for (JButton b : controlButton){    
      b.addActionListener(listener);
      controlPanel.add(b);
    }
  }

    
    
    
    
  
  private class ButtonListener implements ActionListener{
    
    public void actionPerformed (ActionEvent aE){
        
        if (aE.getSource()== timer){
            
            genLabel.setText ("Generation: \n" + Integer.toString (generation));
            timeLabel.setText ("Time: " + Integer.toString (time));
            int count = 0;
            for(int i = 0;i<creatures.length;i++){
                if (creatures[i].isAlive()){
                    count++;
                }
            }
            creaturesLeft = count;
            
            creatureLabel.setText("Creatures left: " + (Integer.toString (creaturesLeft)) + "/"+ CREATURE_NUMBER);
           
            
            mushroomCount = countArray(mushrooms_location);
            foodCount = countArray(strawberries_location);
            maxMushroom.setText ("Mushrooms left " + mushroomCount + "/" + MUSHROOM_NUMBER );
            maxFood.setText("Food left: " + foodCount + "/" + FOOD_NUMBER);
            
            
            for (int k = 0;k<creatures.length;k++){
              
              creatures[k].statusCheck();
              creatures[k].monsterCheck();
              if (creatures[k].isAlive()){
                 creatures [k].incrementFitness();
                  creatures[k].nextTurn();
              }
          }
            
            if(time <= 0 ||creaturesLeft == 0 ){
                timer.stop();
                DecimalFormat df = new DecimalFormat("#.00");
                fitnessData += ("Generation:" + generation + ": \t" + df.format(aveFitness()) + "\n");
                fitnessDisplay.setText( df.format(aveFitness()) + "%");
                
                
                double totalFitness =0;
                
                for (int i=0;i< creatures.length;i++) {
                    totalFitness += creatures[i].getFitness();
                    System.out.println (creatures[i].getFitness());
                }
                
                
                for (Creature parent : creatures) {
                    parent.fitnessNormalised = (parent.getFitness() / totalFitness);
                    System.out.println(parent.fitnessNormalised);
                }
               
                

             
                nextGen(); //nuke the world
                timer.start();




            }
          
          else if (time%3==0){
              for (int j =0;j < monsters.length;j++){
                  monsters[j].nextAction();
              }
          }
          
              
          
          time--;
      }
      
      else{
        JButton button = (JButton) aE.getSource();
       
        if (button.getText().equals ("Initialize")){
         World.initialiseWorld();
        }
        
        

        else if (button.getText().equals ("NextGen")){
            

            nextGen(); //nuke the world
            timer.start();           
        }
        else  if (button.getText().equals ("Pause")){
            timer.stop();
            System.out.println(fitnessData);
        }
        else  if (button.getText().equals ("Resume")){
            timer.start();
        } 
      }
      drawPanel.repaint ();
      
    } 
  }
  
    //reset the environment
    public static void nextGen(){
        //nuke everything off the map apart from monsters.
        reSetArray(creature_location);
        reSetArray(mushrooms_location);
        reSetArray(strawberries_location);
        
        
        time = setTime;
        creaturesLeft = 0;
        generation++;
        
        int elites = 1;
        
        
        //Elitism, a portion of previous generation is kept on the world.
        
        for (int i = 0; i < elites; i++){ 
        creatures[i] = fittestCreature(creatures); //make a copy of the elite
        creaturesLeft++;
        }
        
        // 10 percent of next generation will be born from the fittest creature and another creature chosen by roulette
        // The king and his many wives.
        int elitebreed = CREATURE_NUMBER / 10; 
        
        for (int i = elites; i < elitebreed; i++){
            creatures[i] = new Creature (fittestCreature(creatures),pickParent(creatures));
            creaturesLeft++;
        }
        
        
        
        
        for (int i = elitebreed; i < creatures.length;i++){
             creatures[i] = new Creature(pickParent(creatures),pickParent(creatures));
             creature_location[creatures[i].getLocationY()][creatures[i].getLocationX()]++;
             creaturesLeft++;
        }
        
        
        
        
        
    
         int mushrooms = MUSHROOM_NUMBER;
        
        while (mushrooms > 0){
            int selectedRow = r.nextInt(MAP_WIDTH);
            int selectedCol = r.nextInt(MAP_HEIGHT);
            
            for (int i = 0; i < mushrooms_location.length ;i++) {
                if (i == selectedCol){
                    for (int j = 0; j < mushrooms_location[i].length; j++){
                        if (j == selectedRow && mushrooms != 0 && mushrooms_location[i][j] == 0 ) {
                            mushrooms_location[i][j] = r.nextInt(2); //0 or 1 is placed.
                            mushrooms -= mushrooms_location[i][j];
                            
                        }
                    }
                }
            }
        }
        



int food = FOOD_NUMBER;
while (food > 0){
    
    int selectedRow = r.nextInt(MAP_WIDTH);
    int selectedCol = r.nextInt(MAP_HEIGHT);
    
    for (int i = 0; i < strawberries_location.length ;i++){
        if (i == selectedCol){
            for (int j = 0; j < strawberries_location[i].length; j++){
                if (j == selectedRow && food != 0 && mushrooms_location[i][j] == 0 && strawberries_location[i][j] ==0){
                    //food and poison will not overlap.
                    strawberries_location[i][j] = r.nextInt(2);
                    food -=  strawberries_location[i][j];
                }
            }
        }
    }
}
     
    }


    public static Creature pickParent(Creature[] parentlist){
        double threashold = r.nextDouble();
        double sum = 0;
        int i = 0;
        Creature selected = parentlist[0];
         for (i = 0; i < creatures.length;i++){
             sum += parentlist[i].fitnessNormalised;
             if(sum > threashold){
                 selected = parentlist[i];
             }
         }
            return selected;
    }
    
    
    
    
    //setting up the world
    public static void initialiseWorld(){
        
       generation++;
       time = setTime;

        //initialize creatures and monsters with random locations, update location map
        for (int i = 0; i < creatures.length;i++){
             creatures[i] = new Creature();
             creature_location[creatures[i].getLocationY()][creatures[i].getLocationX()]++;
             creaturesLeft++;
        }
        
        
        
//     //   System.out.println("Creatures map:");
//         for (int i = 0; i < creature_location.length ;i++) {
//            for (int j = 0; j < creature_location[i].length; j++){
//                System.out.print(creature_location[i][j]);
//            }
//            System.out.println();
//        }
        
        
        for (int i = 0; i < monsters.length;i++){
            monsters[i] = new Monster();
            monster_location[monsters[i].getLocationY()][monsters[i].getLocationX()]++;
        }
        
        
        //initialize mushrooms
        int mushrooms = MUSHROOM_NUMBER;
        
        while (mushrooms > 0){
            int selectedRow = r.nextInt(MAP_WIDTH);
            int selectedCol = r.nextInt(MAP_HEIGHT);
            
            for (int i = 0; i < mushrooms_location.length ;i++) {
                if (i == selectedCol){
                    for (int j = 0; j < mushrooms_location[i].length; j++){
                        if (j == selectedRow && mushrooms != 0 && mushrooms_location[i][j] == 0 ) {
                            mushrooms_location[i][j] = r.nextInt(2); //0 or 1 is placed.
                            mushrooms -= mushrooms_location[i][j];
                            
                        }
                    }
                }
            }
        }
        



int food = FOOD_NUMBER;
while (food > 0){
    
    int selectedRow = r.nextInt(MAP_WIDTH);
    int selectedCol = r.nextInt(MAP_HEIGHT);
    
    for (int i = 0; i < strawberries_location.length ;i++){
        if (i == selectedCol){
            for (int j = 0; j < strawberries_location[i].length; j++){
                if (j == selectedRow && food != 0 && mushrooms_location[i][j] == 0 && strawberries_location[i][j] ==0){
                    //food and poison will not overlap.
                    strawberries_location[i][j] = r.nextInt(2);
                    food -=  strawberries_location[i][j];
                }
            }
        }
    }
}


    }
    
    
    /**Displaying the panel and filling it with components */
    private class DrawingPanel extends JPanel{
        
        public DrawingPanel(){
            setPreferredSize (new Dimension(MAP_WIDTH * RATIO, MAP_HEIGHT*RATIO));
            setBackground (Color.pink);
        }
        
        public void paintComponent (Graphics g){
            super.paintComponent(g);
        
            for (int i = 0; i < mushrooms_location.length ;i++) {
                for (int j = 0; j < mushrooms_location[i].length; j++){
                    if (mushrooms_location[i][j] > 0){
                        g.setColor(Color.black);
                        g.fillRect(i * World.RATIO,j * World.RATIO,20,20);
                    }
                }
            }
            
            
            for (int i = 0; i < strawberries_location.length ;i++) {
                for (int j = 0; j < strawberries_location[i].length; j++){
                    if (strawberries_location[i][j] > 0){
                        g.setColor(Color.RED);
                        g.fillRect(i * World.RATIO,j * World.RATIO,20,20);
                    }
                }
            }
            
            for(int i = 0; i < monsters.length;i++){
                if(monsters[i] != null){
                monsters[i].display(g);
            }
            }
            
            for (int i = 0; i < creatures.length; i++){
                if(creatures[i].isAlive()){
                    creatures[i].display(g);
                }
            }
        }
    }
    
  
  
  public static void main (String [] args){
      
 initialiseWorld();



JFrame frame = new JFrame ("Creature World");
frame.getContentPane().add (new World());
frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
frame.pack();
frame.setVisible(true);

//Creature c = new Creature();
//
//for (int i = 0; i < 10;i++){
//    c.nextTurn();
//}





    }
    
    

  
  
  
  
  
  
  
  
  
  
  
  
  /**
   * Test method: printArray
   * @param input is one of the location arrays.
   */
  private static void printArray(int[][] input){
       for (int i = 0; i < input.length ;i++) {
            for (int j = 0; j < input[i].length; j++){
                System.out.print(input[i][j]);
            }
            System.out.println();
        }  
  }
  
  private static void reSetArray(int [][] input){
      for (int i = 0; i < input.length ;i++) {
            for (int j = 0; j < input[i].length; j++){
               input[i][j] = 0;
            }
  }
  }
  
  private static int countArray(int[][] input){
      int count = 0;
      for (int i = 0; i < input.length ;i++) {
          for (int j = 0; j < input[i].length; j++){
              if(input[i][j] > 0){
                  count++;
              }
          }
      }
      return count;
  }
  
  
  private static Creature fittestCreature(Creature[] candidates){
      
      Creature fittest = candidates [0];
      
      for (int i = 0; i < candidates.length; i++){
          if (candidates[i].getFitness() > fittest.getFitness()){
              fittest = candidates[i];
          }
      }
      return fittest; 
  }
  
  
  private static double aveFitness(){
      double totalFitness =0;
      
      for (int i=0; i < creatures.length;i++){
              totalFitness+= creatures[i].getFitness();
      }
      return (totalFitness/(setTime * CREATURE_NUMBER) * 100);
      
  }
}




        
   
    

