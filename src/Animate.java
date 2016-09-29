import java.util.HashMap;
import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.text.*;

public class Animate {

	public static final int LIFEPOINT_X = 180;
	public static final int LIFEPOINT_Y = 600;
	public static final int POINT_FONT = 70;
	public static final int FRAMES_PER_SECOND = 60;
	public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	public static final int SUMMARY_X = 50;
	public static final int SUMMARY_Y = 100;
	public static final int INTERSECTION_X_DIFFERENCE = 20;
	private static final int THROW_SPEED = 20;
	private static final int END_SCENE_DELAY = 1500;
	// screen bounds
	public static final double MAX_X = 685;
	public static final double MAX_Y = 350;
	
	private Graphics graphic;
	private Game myGame;
	private BattleRound battle;
	private int changeDirection;

	private boolean paralyzed = false;

	/**
	 * @param graphic
	 * @param game
	 * @param round
	 */
	public Animate(Graphics graphic, Game game, BattleRound round) {
		this.graphic = graphic;
		myGame = game;
		battle = round;
		changeDirection = 1;
	}

	/**
	 * Sets boolean paralyzed to true
	 */
	public void setParalyzed() {
		paralyzed = true;
	}

	private void animateOpponent(Group root, ImageView myCharacter, ImageView opponent, ImageView object,
			double myLifePoints, double oppLifePoints, boolean isKaneki, boolean paralyzed) {
		Timeline timeline = battle.getTimelineAnimation();
		String winner = battle.checkForWinner(root, SUMMARY_X, SUMMARY_Y);
		boolean intersected = updateIntersectionHit(myCharacter, opponent);
		Image oldImage = opponent.getImage();
		if (!isKaneki) { //round 1
			handleRound1VillainMoves(intersected, root, myCharacter,opponent,
					object, oldImage);
		}
		if (isKaneki) { //round 2 villain is Kaneki
			if (!winner.equals("None")) {
				setFinalOutcome(winner, root, opponent, timeline);
			}
			handleRound2VillainMoves(paralyzed, intersected, root, opponent, object);
		}
	}

	private void handleRound2VillainMoves(boolean paralyzed, boolean intersected, Group root, ImageView opponent,
			ImageView object) {
		if (paralyzed) { // fail safe against
			double y = moveObjectUpAndDown(opponent, 5);
			throwObject(object, THROW_SPEED, y);
		} else {
			executeSpazzAttack(root, opponent, object);
		}
		if (intersected) {
			battle.updateLifePointsGUI(root, LIFEPOINT_X + 70, LIFEPOINT_Y + 70, POINT_FONT, -40, 0);
		}
	}
	
	private void handleRound1VillainMoves(boolean intersected, Group root, ImageView myCharacter,ImageView opponent,
			ImageView object, Image oldImage){
		if (intersected) {
			showIntimidationMove(root, myCharacter, opponent, oldImage);
		} else {
			opponent.setImage(graphic.createImage("penguin_vil.png"));
			double y = moveObjectUpAndDown(opponent, 5);
			throwObject(object, THROW_SPEED, y);
		}
	}

	private void showIntimidationMove(Group root, ImageView myCharacter, ImageView opponent, Image oldImage) {
		opponent.setImage(graphic.createImage("intimidation.png"));
		opponent.toFront();
		graphic.setImageViewParams(opponent, myCharacter.getX(), myCharacter.getY() - 20, 200, 200);
		battle.updateLifePointsGUI(root, LIFEPOINT_X + 70, LIFEPOINT_Y + 70, POINT_FONT,
				-1 * myGame.getAbilitiesScores().get("Intimidation"), 0);
		Text intimidationText = graphic.createText(root, myCharacter.getX(), myCharacter.getY() - 50,
				"Intimidation: You got too close \n" + "to Mawaru Penguin so it decided \n"
						+ "to deploy its intimidation move");
		graphic.setTextAttributes(intimidationText, Graphics.RED_COLOR,
				Font.font("Verdana", FontWeight.NORMAL, 15), "intimidation");
		graphic.removeAfterDelay(root, "#intimidation", 250);
	}

	private void executeSpazzAttack(Group root, ImageView opponent, ImageView object) {
		graphic.importantTextAlert(root, 200, 400, "SPAZZ ATTACK", "spazz");
		double y = spazzAttack(opponent);
		graphic.removeAfterDelay(root, "#spazz", 1000);
		throwObject(object, THROW_SPEED, y);
	}

	private void setFinalOutcome(String winner, Group root, ImageView opponent, Timeline timeline) {
		if (winner.equals("Kaneki")) {
			timeline.stop();
			new java.util.Timer().schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					Platform.runLater(new Runnable() {
						public void run() {
							endWithKanekiCentipedeScene(root, opponent);
						}
					});
				}
			}, END_SCENE_DELAY);
		} else if (winner.equals("Me")) {
			timeline.stop();
			endWithHappyWinScene(root, opponent);
		}
	}

	private double moveObjectUpAndDown(ImageView image, double speed) {
		if (image.getY() >= MAX_Y) {
			changeDirection = -1;

		} else if (image.getY() <= 0) {
			changeDirection = 1;
		}
		image.setY(image.getY() + changeDirection * speed);

		return image.getY();
	}

	private void launchObject(Group root, ImageView object, ImageView myCharacter) {
		root.getChildren().add(object);
		object.setX(myCharacter.getX());
		object.setY(myCharacter.getY());
	}

	private void throwObject(ImageView object, int speed) {
		object.setX(object.getX() + speed);
	}

	private void throwObject(ImageView object, int speed, double y) {
		if (object.getX() < 0) {
			object.setX(MAX_X);
		} else {
			object.setX(object.getX() - speed);
			object.setY(y);
		}
	}

	private void checkForTargetHit(Group root, ImageView myObject, ImageView otherObject, String whosHit) {
		HashMap<String, Double> abilitiesScores = myGame.getAbilitiesScores();
		boolean intersected = updateIntersectionHit(myObject, otherObject);
		if (intersected && whosHit.equals("villain")) {
			battle.updateLifePointsGUI(root, LIFEPOINT_X + 70, LIFEPOINT_Y + 70, POINT_FONT, 0,
					-1 * abilitiesScores.get("Hit"));
		} else if (intersected && whosHit.equals("me")) {
			battle.updateLifePointsGUI(root, LIFEPOINT_X + 70, LIFEPOINT_Y + 70, POINT_FONT,
					-5 * abilitiesScores.get("Hit"), 0);
		}
	}

	private double spazzAttack(ImageView image) {
		int rangeMin = -50;
		int rangeMax = 50;
		Random random = new Random();
		double rndX = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
		double rndY = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
		double newX = image.getX() + rndX;
		double newY = image.getY() + rndY;
		if (newX > 0 && newX < MAX_X && newY > 0 && newY < MAX_Y) {
			image.setX(image.getX() + rndX);
			image.setY(image.getY() + rndY);
		}
		return image.getY();
	}

	public boolean updateIntersectionHit(ImageView object1, ImageView object2) {
		return (justIntersected(object1, object2)
				&& Math.abs(object1.getX() - object2.getX()) < INTERSECTION_X_DIFFERENCE);
	}

	public boolean justIntersected(ImageView object1, ImageView object2) {
		return (object1.getBoundsInParent().intersects(object2.getBoundsInParent()));
	}

	/**
	 * Handles the main default animation in the program
	 * @param animation 
	 * 			The main animation timeline
	 * @param root 
	 * 			The node that contains all the children on the screen
	 * @param myCharacter 
	 * 			My character graphic
	 * @param opponent 
	 * 			My opponent graphic
	 * @param round2 
	 * 			If the player is on round2 or not
	 */
	public void handleKeyAnimation(Timeline animation, Group root, ImageView myCharacter, ImageView opponent,
			boolean round2) {
		ImageView launchObject;
		if (battle.isRound2()) {
			launchObject = graphic.createImageView(graphic.createImage("Ea.png"));
		} else {
			launchObject = graphic.createImageView(graphic.createImage("penguin_head.png"));
		}
		launchObject(root, launchObject, opponent);
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> animateOpponent(root, myCharacter,
				opponent, launchObject, battle.getMyLifePoints(), battle.getOppLifePoints(), round2, paralyzed));
		KeyFrame frame1 = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
				e -> checkForTargetHit(root, launchObject, myCharacter, "me"));
		KeyFrame[] frames = {frame, frame1};
		initAnimation(animation,frames);
	}

	/**
	 * Handles the animation for when the players throws object
	 * @param animation The main animation Timeline
	 * @param root 
	 * @param object The object that is being thrown
	 * @param myCharacter My character graphic
	 * @param opponent The opponent graphic
	 */
	public void handleThrowAnimation(Timeline animation, Group root, ImageView object, ImageView myCharacter,
			ImageView opponent) {
		launchObject(root, object, myCharacter);
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> throwObject(object, THROW_SPEED));
		KeyFrame frame1 = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
				e -> checkForTargetHit(root, object, opponent, "villain"));
		KeyFrame[] frames = {frame, frame1};
		initAnimation(animation,frames);
	}
	
	private void initAnimation(Timeline animation, KeyFrame[] frames){
		animation.setCycleCount(Timeline.INDEFINITE);
		for (KeyFrame frame:frames){
			animation.getKeyFrames().add(frame);
		}
		animation.play();
	}

	/**
	 * Animates the object to jump up and down
	 * @param object The object that will jump up and down
	 */
	public void animateJump(Node object) {
		TranslateTransition translation = new TranslateTransition(Duration.millis(50), object);
		translation.interpolatorProperty().set(Interpolator.SPLINE(.1, .1, .7, .7));
		translation.setByY(-50);
		translation.setAutoReverse(true);
		translation.setCycleCount(2);
		translation.play();
	}

	private void endWithKanekiCentipedeScene(Group root, ImageView opponent) {
		Group endRoot = myGame.setUpNewScene("Game over", 500, 281);
		graphic.setBackgroundImage(endRoot, "KenCentipede.gif", 500, 281);
		Text centipede = graphic.createText(endRoot, 10, 150,
				"GAME OVER \n \"I am not the one who is wrong. The world is what\n"
						+ "is wrong,\" Kaneki says as he pulls a centipede out of his\n"
						+ "ear from the previous night's torture. This jaded, cynical\n"
						+ "fellow seems a bit bothered by your disturbing him \n"
						+ "and has decided not to spare you.");
		graphic.setTextAttributes(centipede, Graphics.RED_COLOR, Font.font("Verdana", FontWeight.NORMAL, 17),
				"centipede");

	}

	private void endWithHappyWinScene(Group root, ImageView opponent) {
		Group endRoot = myGame.setUpNewScene("You win", 500, 281);
		graphic.setBackgroundImage(endRoot, "hyouka.gif", 500, 281);
		Text hyouka = graphic.createText(endRoot, 10, 150,
				"YOU WIN and have saved the anime world! But now you \n"
						+ "have to return to the miserable reality of college \n"
						+ "life. Gah why can't we just live in our well crafted \n"
						+ "fantasies full of romantic ideas? But...I suppose \n "
						+ "there's the on redeeming thing: you won this amazing\ngame.");
		graphic.setTextAttributes(hyouka, Graphics.RED_COLOR, Font.font("Verdana", FontWeight.BOLD, 15),
				"hyouka");

	}

}
