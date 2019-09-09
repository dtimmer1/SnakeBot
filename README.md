# SnakeBot

SnakeBot is a Java program that uses a genetic algorithm to optimize a feed-forward neural network to play the classic game Snake, without the use of pre-written machine learning libraries.

## Background

Genetic algorithms are a set of optimization algorithms that simulate the real-life genetic processes of cross-over, mutation, and natural selection to generate an optimal solution according to some fitness function. In the context of the game Snake, the fitness function is a function primarily of the number of points scored over the course of the game. By selecting for this, the weights of a feed-forward neural network can be optimized by the genetic algorithm to create an autonomous program that (occasionally) plays Snake well.

![SnakeBot screenshot](https://raw.githubusercontent.com/dtimmerman998/SnakeBot/master/readme_pic.PNG)


### Functionality

SnakeBot, due to not itself being an executable, is compiled like any normal Java program. The class GUI contains the main method.
From there, there are three options for functionality. One is to simply play Snake if you want. Another, labeled 'Evolve', starts running generations of the genetic algorithm to generate good neural networks, storing the best from each generation. Finally, 'Load' allows you to load a saved `.snake` file that contains the weights for the provided neural network infrastructure and allows it to autonomously play Snake.
