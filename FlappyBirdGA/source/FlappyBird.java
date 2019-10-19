import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Collections; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FlappyBird extends PApplet {



Obstacle o;
int N = 150;
ArrayList<Bird> population = new ArrayList();
ArrayList<Obstacle> obPopulation = new ArrayList();

int obstacleVelocity = 3; //how fast the obstacles move

double mutationRate = 0.1f; //probability of mutating a weight
float mutationAmount = 0.05f; //max amount to mutate by (if mutating)

Boolean speedTraining = true; //make training go faster based upon how many birds are still alive

Boolean startWithSetWeights = false; //start with previously discovered 'good' weights
double[] startWeights = {-0.9464109390974045f, 0.523438572883606f, -0.072207719087860071f, -0.5028201788663864f}; //these are the weights described above

double overallBestFitness = 0; //best fitness found yet
double[] bestWeights = new double[4]; //best weights found so far in this session

int generation = 0; //how many generations have been evolved

int newObstacleTimer = 110; //timer for adding new obstacles
int newObstacleTimerMax = 120; //once newObstacleTimer > this, create a new obstacle

boolean passedFirstOb = false; //for figuring out if the first obstacle was passed

boolean addTrainedBird = false; //used in input box for adding trained bird

public void setup(){
  
  
  o = new Obstacle();
  o.x = (2*width) / 3;
  obPopulation.add(o);
  
  if (startWithSetWeights)
    setupBestBird();
  
  populate();
}

public void draw() {
  imageMode(CENTER);
  float rand = random(50);

  drawBackground();
  
  //iterate over all obstacles and perform some basic task
  for (int i = 0; i < obPopulation.size(); i++){
    obPopulation.get(i).display();
    obPopulation.get(i).move(obstacleVelocity);
    
    //if obstacle is offscreen, remove it.
    if (obPopulation.get(i).x < -60){
      obPopulation.remove(i);
      passedFirstOb = true;
      
      awardFitness(); //reward all surviving birds with +1 fitness
    }
  }
  
  //get # of birds that are still alive
  int alivePops = 0;  
  for (int i = 0; i < population.size(); i++)
    if (!population.get(i).dead) 
      alivePops++;
  
  //Speed Training Mode: adjust framerate based upon alive pops
  if (speedTraining){
    if (alivePops > 50) frameRate(60);
    else if (alivePops > 15) frameRate(180);
    else if (alivePops > 5) frameRate(360);
    else if (alivePops > 2) frameRate(720);
    else frameRate(5000);
  }
  
  //text info
  displayInfo(alivePops);
  
  //iterate over population (all birds) and perform basic some tasks
  for (int i = 0; i < population.size(); i++){
    if (!population.get(i).dead){
      
      population.get(i).update(); //basic physics and display update
      
      //if this birds fitness is the best yet, make it the new best fitness.
      if (population.get(i).fitness > overallBestFitness){
            bestWeights[0] = population.get(i).brain.input_Weights[0];
            bestWeights[1] = population.get(i).brain.input_Weights[1];
            bestWeights[2] = population.get(i).brain.input_Weights[2];
            bestWeights[3] = population.get(i).brain.input_Weights[3];
            overallBestFitness = population.get(i).fitness;
            }
      
      //plug inputs through neural net to make a decision on whether or not to jump
      if (population.get(i).brain.doJump(population.get(i).position, obPopulation.get(0).x, obPopulation.get(0).spaceAt))
        population.get(i).jump();
    
      //If out of bounds
      if (population.get(i).position.y > height-100 || population.get(i).position.y < -20)
        population.get(i).dead = true;
      
      //Check for collision 
      for (int j = 0; j < obPopulation.size(); j++)
        if (obPopulation.get(j).collosion(population.get(i).position.x, population.get(i).position.y) && !population.get(i).dead) 
            population.get(i).dead = true;
      
    }
  }
  
  if (alivePops < 1) //RESET & GENETIC ALGORITHM
    performGeneticAlgorithm();
  
  //timer and timer task for obstacles
  if (newObstacleTimer > newObstacleTimerMax){
    obPopulation.add(new Obstacle());
    newObstacleTimer = -1;
  }
  newObstacleTimer++;
}

public void drawBackground(){
  background(100,100,255);
  fill(0,255,0);
  rect(0, height-25, width, height); //Ground
  fill(255,255,0);
  ellipse(width-100, 100, 100, 100);
  fill(255,0,255);
}

public void displayInfo(int alivePops){
  fill (0,0,0);
  text("Generation: " + generation + " | alivePops: " + alivePops, 10, 10);
  text("Best Fitness: " + (float)overallBestFitness + "\nBest Weights: \n" + bestWeights[0] + "\n" + bestWeights[1] 
                 + "\n" + bestWeights[2] + "\n" + bestWeights[3], width - 150, 10);
  
  for (int i = 0; i < population.size(); i++)
    if (!population.get(i).dead){
      text("Current Fitness: "+ (float)population.get(i).fitness, 250, 10);
      break;
    }
    
  if (!addTrainedBird)
    fill(255,0,0);
  else
    fill(255,0,255);
  rect(width-250,0,75,50);
  fill(0,0,0);
  text("Add 'trained' \n     bird",width-248,25);
}

public void setupBestBird(){
  //initialize a bird with predetermined weights
  Bird b = new Bird();
  b.brain.input_Weights[0] = startWeights[0];
  b.brain.input_Weights[1] = startWeights[1];
  b.brain.input_Weights[2] = startWeights[2];
  b.brain.input_Weights[3] = startWeights[3];
  b.isBest = true;
  population.add(b);
}

public void populate(){
  //populate the population of birds
  for (int i = 0; i < N; i++){
    Bird b = new Bird();
    population.add(b);
  } 
}

public void awardFitness(){
  //gives fitness to all birds that are alive
  for(Bird b : population){
    if (!b.dead)
      b.fitness++;
  }
}

public void mouseClicked(){
  if (mouseX > width-250 && mouseX < width-175
  && mouseY > 0 && mouseY < 50)
    addTrainedBird = true;
}

public void performGeneticAlgorithm(){
  //selects from population based upon fitness, 
  //and then crosses over and mutates their weights to make
  //next population
  //*gets a lil messy here*
  
  //clear obstacles and make a new one
  obPopulation.clear();
  Obstacle o = new Obstacle();
  o.x = (2*width) / 3;
  obPopulation.add(o);
  newObstacleTimer = 70;
  passedFirstOb = false;
  
  generation++;
  
   ArrayList<Bird> newPopulation = new ArrayList();
  
  //elitism (always add best found fitness to next gen)
  Bird bestBird = new Bird();
  if (!addTrainedBird){
    bestBird.brain.input_Weights[0] = bestWeights[0];
    bestBird.brain.input_Weights[1] = bestWeights[1];
    bestBird.brain.input_Weights[2] = bestWeights[2];
    bestBird.brain.input_Weights[3] = bestWeights[3];
  }
  else{
    bestBird.brain.input_Weights[0] = startWeights[0];
    bestBird.brain.input_Weights[1] = startWeights[1];
    bestBird.brain.input_Weights[2] = startWeights[2];
    bestBird.brain.input_Weights[3] = startWeights[3];
    
    addTrainedBird = false;
  }
  
   bestBird.fitness = 0;
   bestBird.isBest = true;
  
  newPopulation.add(bestBird);
  
  //Cube fitness for all and find sum of all fitness scores
  double fitnessSum = 0;
  for (int k = 0; k < population.size(); k++) {
    population.get(k).fitness *= population.get(k).fitness * population.get(k).fitness;
    fitnessSum += population.get(k).fitness;
  }
  
  if (overallBestFitness > 1){
    println("evolving...");
    RandomItemChooser rc = new RandomItemChooser();
    
    //reproduce 'best' weights based parents fitness
    for (int k = 1; k < population.size(); k++){
      Bird parent1 = rc.chooseOnWeight(population);
      Bird parent2 = rc.chooseOnWeight(population);
      
      Bird newBird = new Bird();
      
      //Crossover
      for (int g = 0; g <= 3; g++){
        int intRand = (int)(1+random(2));
        if (intRand == 1){
          newBird.brain.input_Weights[g] = parent1.brain.input_Weights[g];
        }
        else {
          newBird.brain.input_Weights[g] = parent2.brain.input_Weights[g];
        }
      }
      
      //Mutate
      float rand = 0; //initialize rand float var
      for (int g = 0; g <= 3; g++){
        rand = random(1);
        if (rand < mutationRate){
          int intRand = (int)(1+random(2)); //Add or subtract decision
          if (intRand == 1){
            newBird.brain.input_Weights[g] += random(mutationAmount);
          }
          else{
            newBird.brain.input_Weights[g] -= random(mutationAmount);
          }
        }
      }
      
      //add to new population
      newPopulation.add(newBird);
    }
  }
  
  else { //they havent passed the first ob even once
    println("resetting");
    for (int i = 0; i < N; i++){
        Bird b = new Bird();
        newPopulation.add(b);
    }
  }
  
  //Collections.swap(newPopulation, 0, newPopulation.size()-1); //set best bird on top to make easier to see //this somehow breaks the genetic algorithm? :/
  population = newPopulation; //replace old population with new population
}

class RandomItemChooser {
  //modified weighted random item chooser I found on stackoverflow.
    public Bird chooseOnWeight(ArrayList<Bird> items) {
        double completeWeight = 0.0f;
        for (Bird item : items)
            completeWeight += item.relativeFitness;
        double r = Math.random() * completeWeight;
        double countWeight = 0.0f;
        for (Bird item : items) {
            countWeight += item.relativeFitness;
            if (countWeight >= r)
                return item;
        }
        throw new RuntimeException("Should never be shown.");
    }
}
class Bird {
  PVector position = new PVector(70, height/2);
  PVector velocity = new PVector(0,0);
  PVector acceleration = new PVector(0,0.4f);
  NeuralNet brain = new NeuralNet(random(2)-1, random(2)-1, random(2)-1, random(2)-1);
  
  Boolean dead = false;
  Boolean isBest = false;
  
  double fitness = 0;
  double relativeFitness = 0;

  public Bird() {
    //all instance variables start the same :/
  }

  public void jump(){
    velocity.y = -10;
    //print("REEEE");
  }

  public void calculateNewPosition(){
    position.add(velocity);
  }

  public void calculateNewVelocity(){
    velocity.add(acceleration);
  }
  
  public void display(){
    if (isBest)
      fill(255,0,255); //color purple if best
    else
      fill(0,0,0); //color black otherwise
      
    ellipse(position.x,position.y,10,10); 
  }

  public void update(){
    if (!dead){
      calculateNewVelocity();
      calculateNewPosition();
    
      display();
      
      fitness += 0.0001f;
    }
  }
}
//Not really a good neural net class, does not support hidden nodes.
//First one I ever made

class NeuralNet{
  double[] input_Weights = new double[4];
  double[] hidden_Weights = new double[2];
  boolean output;
  
  public NeuralNet(double weight1, double weight2, double weight3, double weight4){
    this.input_Weights[0] = weight1;
    this.input_Weights[1] = weight2;
    this.input_Weights[2] = weight3;
    this.input_Weights[3] = weight4;
  }
  
  public boolean doJump(PVector position, int nearestObPosition, int nearestObHole){
    //println(sigmoid(((position.x * input_Weights[0] + position.y * input_Weights[1] + nearestObPosition * input_Weights[2] + nearestObHole * input_Weights[3]))));
    if (sigmoid((position.x * input_Weights[0] 
                + position.y * input_Weights[1] 
                + nearestObPosition * input_Weights[2] 
                + nearestObHole * input_Weights[3])) > 0.5f)
    {
      return true;
    }
    return false;
  }
  
  public float sigmoid(double x){
    //x /= 500;
    //println(x + " & " +  1/(1+exp(-(float)x)));
    return (1/(1+exp(-(float)x)));
  }
}
class Obstacle{
  int x;
  int oWidth = 50;
  int spaceAt;
  
  public Obstacle(){
    this.spaceAt = 50 + PApplet.parseInt(random(height-400));
    this.x = width + 20;
  }
  
  public void move(int amount){
    this.x -= amount;
  }
  
  public void display(){
    rect(x,0,oWidth,spaceAt);
    rect(x,spaceAt+150,oWidth, height);
  }
  
  public boolean collosion(float x, float y){
    if (this.x < x && x < this.x+this.oWidth && (this.spaceAt > y || y > this.spaceAt+150)){
      return true;
    }
    return false;
  }
}
  public void settings() {  size(1200,700); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlappyBird" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
