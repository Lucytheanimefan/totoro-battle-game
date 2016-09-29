### Design Goals  

My goal was to create a battle game that allowed the player to play certain attacks to cause its opponent to lose life points. Meanwhile, the opponent would respond to these attacks with its own as well continuously deploy other attacks. The winner would be determined by who reaches 0 life points first.   

### Adding new features  

#### To add a new move:  
After you write the function to animate the character however you'd like, you can call the  method ```updateLifePointsGUI``` inside that method to update the life points based on how much that move/feature you've added is worth. Then, in the ```Animate``` class, you will have to create another ```KeyFrame`` object and add that into the method: 

```java 
public void handleKeyAnimation(Timeline animation, Group root, ImageView myCharacter, ImageView opponent,
			boolean round2)
```

For example 
```java
KeyFrame frame_x = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
				e -> doWhateverNewMove());
```  
You will then have to add this frame to the array of frames in the penultimate line in the ```handleKeyAnimation``` method. And voila.  

#### To add a new round: 
In the function ```checkForWinner``` in the ```BattleRound``` class, replace the content inside the ```if(round2)``` with the ```proceedToNextRound``` function. This will cause the game to proceed to a next round instead of end on the second round which is the current final round. This function will also set up a new scene and initialize a new round. In the ```init``` function, you may customize the villain image, the throwing weapon image, and the background image to whatever you want for the next level.  

### Assumptions/Design Choices  
One assumption I made was that I would only implement two rounds with distinct levels so I hard coded certain things that would have been better to have passed as arguments if I created more rounds. For example, there are many hard coded "magic numbers" for the positioning of graphics, as decision that is feasible if only 2 rounds are implemented but would grow to be difficult to work with if the game were to be expanded. I also assumed my two levels to be algorithmically different because of the way the enemy was programmed to move and attack. In round 1, the enemy only moves up and down, while the enemy in round 2 moves in a "spazz attack" all over the screen. Should the enemy in round 2 touch the player, they would die. However, the round 2 enemy can be forced to move up and down if you press the correct cheat code.   

My design itself consists of 4 classes responsible for specific functionality. 

| Game       | BattleRound   | Animate  | Graphics |
| ------------- |:-------------:| :-----:|--------:|
| * responsible for splash screen| * responsible for creating a round | * responsible for animations| * responsible for creating general graphics|
| * responsible for starting the game graphic/window   | * responsible for user interactivity and enemy attacks (along with the accompanying graphics) | * responsible for most character attack moves as many are animated | |
