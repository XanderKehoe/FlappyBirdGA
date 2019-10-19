class Obstacle{
  int x;
  int oWidth = 50;
  int spaceAt;
  
  public Obstacle(){
    this.spaceAt = 50 + int(random(height-400));
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
