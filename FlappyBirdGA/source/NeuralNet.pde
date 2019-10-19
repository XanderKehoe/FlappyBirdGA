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
                + nearestObHole * input_Weights[3])) > 0.5)
    {
      return true;
    }
    return false;
  }
  
  float sigmoid(double x){
    //x /= 500;
    //println(x + " & " +  1/(1+exp(-(float)x)));
    return (1/(1+exp(-(float)x)));
  }
}
