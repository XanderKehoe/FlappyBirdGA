class Bird {
  PVector position = new PVector(70, height/2);
  PVector velocity = new PVector(0,0);
  PVector acceleration = new PVector(0,0.4);
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
      
      fitness += 0.0001;
    }
  }
}
