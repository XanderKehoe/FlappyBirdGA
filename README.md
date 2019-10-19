# FlappyBirdGA
A loose recreation of Flappy Bird with a self-learning AI that teaches itself how to play the game.

Created with: https://processing.org/

This program uses a genetic algorithm and a simple neural network to teach itself how to play. The 'Birds' are initalized with a bunch of random weights in their network, they gain 'fitness' by staying alive and going through the pipes. Once all the Birds die, the genetic algorithm randomly (weighted) picks two of them based upon fitness. Their weights are combined to make a new 'child', and this child's weights have a small chance of mutating slightly. This process is repeated until the new population is full, and then the new population replaces the old one, and the whole cycle repeats. The "Best Bird" (described below) is always added to each new population without any modification to its weights.

The neural net has no hidden nodes, and therefore consist simply of just an input layer and a single output node. The input layer consists of four inputs (position in x, position in y, position of closest obstacle, y coordinate of the 'hole' in the closest obstacle), and the output node determines if the Bird should jump.

Training may takes just a couple mintues, or could take a couple of hours. 

If you wish to skip ahead and see what hours of training looks like, just press the red "Add 'trained' bird" button.

UI Info:
  - Generation: Tells you how many times the genetic algorithm has been applied.
  - alivePops: How many Birds are currently alive.
  - Current Fitness: The current fitness of all alive birds.
  - Best Fitness: The best recorded fitness recorded in this session, the Bird that achieved this is known as the 'Best Bird'.
  - Best Weights: The weights of the neural net the 'Best Bird' used.
