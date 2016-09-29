import java.text.DecimalFormat;
import java.util.HashMap;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.scene.text.*;

public class BattleRound {
	public static final int CHAR_SIZE = 200;
	public static final int POINT_FONT = 70;
	public static final int KEY_INPUT_SPEED = 15;
	public static final int LIFE_POINTS=50;
	
	// instance variables
	private Game myGame;
	private HashMap<String, Double> abilitiesScores;
	private double myLifePoints;
	private double oppLifePoints;
	private int numSpiritSummons;
	private int numHits;
	private Graphics graphic;

	private Timeline animation;
	private Animate anime;

	private boolean round2 = false;

	public Timeline getTimelineAnimation() {
		return animation;
	}

	public double getMyLifePoints() {
		return myLifePoints;
	}

	public double getOppLifePoints() {
		return oppLifePoints;
	}

	public boolean isRound2() {
		return round2;
	}

	public void init(Group root) {
		animation = new Timeline();
		myGame = new Game();
		graphic = new Graphics();
		anime = new Animate(graphic, myGame, this);
		myLifePoints = LIFE_POINTS;
		oppLifePoints = LIFE_POINTS;
		numSpiritSummons = 4;
		numHits = 10;
		abilitiesScores = myGame.getAbilitiesScores();
		if (round2) {
			createInitialGraphics(root, "round1Background.png", "totoro.png", "centipede.png");
		} else {
			createInitialGraphics(root, "round1Background.png", "totoro.png", "penguin_vil.png");
		}
	}

	/**
	 * This method creates all of the initial graphics in the game round. That
	 * is the villain, player, background, and life point levels.
	 * 
	 * @param root
	 * @param backgroundImage
	 *            This is the filename of the background image.
	 * @param myCharacter
	 *            This is the filename of the character that the player is.
	 * @param villain
	 *            This is the filename of the opponent image.
	 */
	private void createInitialGraphics(Group root, String backgroundImage, String myCharacter, String villain) {
		graphic.setBackgroundImage(root, backgroundImage);
		ImageView myPlayer = graphic.createImageView(graphic.createImage(myCharacter));
		ImageView opponent = graphic.createImageView(graphic.createImage(villain));
		root.getChildren().add(opponent);
		root.getChildren().add(myPlayer);
		graphic.setImageViewParams(myPlayer, 100, 200, CHAR_SIZE, CHAR_SIZE * 1.15);// player
		graphic.setImageViewParams(opponent, 600, 150); // opponent
		anime.handleKeyAnimation(animation, root, myPlayer, opponent, round2);
		Scene currentScene = myGame.getCurrentScene();
		currentScene.setOnKeyPressed(e -> handleKeyInput(e.getCode(), myPlayer, opponent, root,
				anime.updateIntersectionHit(myPlayer, opponent)));
		createInitialLifePoints(root, Animate.LIFEPOINT_X, Animate.LIFEPOINT_Y, 20);
	}

	/**
	 * Creates the life points label and the number of life points at the
	 * beginning of the game
	 * 
	 * @param root
	 * @param x
	 *            x-coordinate of the life points text
	 * @param y
	 *            y-coordinate of the life points text
	 * @param fontSize
	 */
	private void createInitialLifePoints(Group root, int x, int y, int fontSize) {
		Text lifePoints = graphic.createText(root, x, y, "Your Life Points:");
		lifePoints.setFill(Graphics.RED_COLOR);
		lifePoints.setFont(Font.font("Verdana", FontWeight.BOLD, fontSize));
		// villain's life points
		Text vilLifePoints = graphic.createText(root, x + 400, y, "Opponent's Life Points:");
		vilLifePoints.setFill(Graphics.RED_COLOR);
		vilLifePoints.setFont(Font.font("Verdana", FontWeight.BOLD, fontSize));
		updateLifePointsGUI(root, x + 70, y + 70, POINT_FONT, 0, 0);
	}

	/**
	 * This function updates the life points GUI. It removes the old number of
	 * lifepoints and rewrites the points decrementing the corresponding value
	 * by the number of life points loss.
	 * 
	 * @param root
	 * @param x
	 *            x-coordinate of the player's point value
	 * @param y
	 *            y-coordinate of the player's point value
	 * @param fontSize
	 * @param myPointDiff
	 *            The number of life points that the player has lost
	 * @param oppPointDiff
	 *            The number of life points that the opponent has lost
	 */
	public void updateLifePointsGUI(Group root, int x, int y, int fontSize, double myPointDiff, double oppPointDiff) {
		graphic.removeNode(root, "#myLifePoints");
		graphic.removeNode(root, "#vilLifePoints");
		incrementAndSetLifePoints(root, myPointDiff, "myLifePoints", x, y);
		incrementAndSetLifePoints(root, oppPointDiff, "vilLifePoints", x+400, y);
	}
	
	private void incrementAndSetLifePoints(Group root, double pointDiff, String whosePoints, int x, int y){
		Text numPoints;
		if(whosePoints.equals("myLifePoints")){
			myLifePoints=myLifePoints+pointDiff;
			numPoints = graphic.createText(root, x, y, new DecimalFormat("##.##").format(myLifePoints));
		}else{
			oppLifePoints=oppLifePoints+pointDiff;
			numPoints = graphic.createText(root, x, y, new DecimalFormat("##.##").format(oppLifePoints));
		}
		graphic.setTextAttributes(numPoints, Graphics.RED_COLOR, Font.font("Verdana", FontWeight.BOLD, POINT_FONT),
				whosePoints);
	}

	/**
	 * Handles keyboard input
	 * 
	 * @param code
	 * @param myBlock
	 *            the ImageView of my player graphic
	 * @param opponent
	 *            the ImageView of the opponent graphic
	 * @param root
	 * @param intersected
	 *            whether or not the opponent and player are intersecting each
	 *            other
	 */
	private void handleKeyInput(KeyCode code, ImageView myBlock, ImageView opponent, Group root, boolean intersected) {
		switch (code) {
		case RIGHT:
			if (checkScreenBounds(myBlock, "right")) {
				myBlock.setX(myBlock.getX() + KEY_INPUT_SPEED);
			}
			break;

		case LEFT:
			if (checkScreenBounds(myBlock, "left")) {
				myBlock.setX(myBlock.getX() - KEY_INPUT_SPEED);
			}
			break;

		case UP:
			if (checkScreenBounds(myBlock, "up")) {
				myBlock.setY(myBlock.getY() - KEY_INPUT_SPEED);
			}
			break;

		case DOWN:
			if (checkScreenBounds(myBlock, "down")) {
				myBlock.setY(myBlock.getY() + KEY_INPUT_SPEED);
			}
			break;

		case SHIFT: // spirit summoner
			if (numSpiritSummons > 0) {
				updateLifePointsGUI(root, Animate.LIFEPOINT_X + 70, Animate.LIFEPOINT_Y + 70, POINT_FONT, 0,
						-1 * abilitiesScores.get("Spirit Summoner"));
			}
			numSpiritSummons--;
			break;
		case J: // jump
			anime.animateJump(myBlock);
			if (anime.justIntersected(myBlock, opponent)) {
				updateLifePointsGUI(root, Animate.LIFEPOINT_X + 70, Animate.LIFEPOINT_Y + 70, POINT_FONT, 0,
						-1 * abilitiesScores.get("Jump"));
			}
			break;
		case G: // green thumb
			if (!round2) {
				setGreenThumbText(root, "GREEN THUMB: Mawaru Penguin just ate everything you \n"
						+ "grew with your special ability and you killed it while\n" + "it was distracted. Congrats.");
				updateLifePointsGUI(root, Animate.LIFEPOINT_X + 70, Animate.LIFEPOINT_Y + 70, POINT_FONT, 0,
						-1 * abilitiesScores.get("Green Thumb"));
			} else {
				setGreenThumbText(root, "GREEN THUMB: Kaneki is a ghoul. He eats humans. \n"
						+ "Vegetables and produce are for wimps. \n" + "You lose.");
				updateLifePointsGUI(root, Animate.LIFEPOINT_X + 70, Animate.LIFEPOINT_Y + 70, POINT_FONT,
						-1 * myLifePoints, 0);
			}
			break;
		case T:
			if (numHits > 0) {
				ImageView attack = graphic.createImageView(graphic.createImage("miniTotoro_small.png"));
				anime.handleThrowAnimation(animation, root, attack, myBlock, opponent);
			}
			numHits--;
			break;
		case C: // Cuteness/friendly neighbor
			if (round2) {
				anime.setParalyzed();
				setKanekiParalyzeText(root);
			} else {
				setPenguinParalyzeText(root);
			}
			break;
		default:
			// do nothing
		}
	}

	private void setGreenThumbText(Group root, String text) {
		Text greenThumbText = graphic.createText(root, Animate.SUMMARY_X, Animate.SUMMARY_Y + 100, text);
		graphic.setTextAttributes(greenThumbText, Graphics.RED_COLOR, Font.font("Verdana", FontWeight.BOLD, 25),
				"greenThumb");
	}

	private void setKanekiParalyzeText(Group root) {
		Text paralyzeText = graphic.createText(root, Animate.SUMMARY_X, 1.5 * Animate.SUMMARY_Y,
				"Totoro succesfully paralyzed Kaneki with its \n"
						+ "miraculous display of morality and self perserverance. \n"
						+ "Kaneki is now facing an internal existential crisis about \n"
						+ "his humanity and thus cannot divert a sufficient amount of \n"
						+ "brain power towards moving erratically to throw his opponent\n" + "off. So up and down it is.");
		graphic.setTextAttributes(paralyzeText, Graphics.RED_COLOR, Font.font("Verdana", FontWeight.NORMAL, 30),
				"paralyze");
		graphic.removeAfterDelay(root, "#paralyze", 2000);
	}

	private void setPenguinParalyzeText(Group root) {
		Text paralyzeText = graphic.createText(root, Animate.SUMMARY_X, 1.5 * Animate.SUMMARY_Y,
				"Mawaru Penguin doesn't care about how \nfriendly a neighbor you are. \n");
		graphic.setTextAttributes(paralyzeText, Graphics.RED_COLOR, Font.font("Verdana", FontWeight.NORMAL, 40),
				"Paralyze");
		graphic.removeAfterDelay(root, "#Paralyze", 1000);
	}

	/**
	 * @param image
	 *            the object/image that is moving
	 * @param direction
	 *            The direction that the image is was just moving in
	 * @return Returns true if in the screen's bounds, false if otherwise
	 */
	private boolean checkScreenBounds(ImageView image, String direction) {

		// System.out.println(maxX);
		if (direction.equals("right")) {
			return (image.getX() < Animate.MAX_X);
		} else if (direction.equals("left")) {
			return (image.getX() > -10);
		} else if (direction.equals("down")) {
			return (image.getY() < Animate.MAX_Y);
		} else if (direction.equals("up")) {
			return (image.getY() > -10);
		}
		return true;
	}

	/**
	 * Creates the game over scene for round 1 (when the player loses against
	 * Mawaru Penguin)
	 * 
	 * @param root
	 * @param x
	 *            x-coordinate of lose text
	 * @param y
	 *            y- coordinate of lose text
	 */
	public void gameOver(Group root, double x, double y) {
		animation.stop();
		updateLifePointsGUI(root, Animate.LIFEPOINT_X + 70, Animate.LIFEPOINT_Y + 70, POINT_FONT, -1 * myLifePoints, 0);

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						Group round1Loss = myGame.setUpNewScene("Game over", 370, 374);
						graphic.setBackgroundImage(round1Loss, "intimidation.png", 370, 374);
						Text loseText = graphic.createText(round1Loss, x, y, "Game over");
						graphic.setTextAttributes(loseText, Graphics.RED_COLOR,
								Font.font("Verdana", FontWeight.BOLD, 20), "lose");
						Text endMessage = graphic.createText(round1Loss, .2 * x, y + 20,
								"Mawaru Penguin managed to intimidate you \n"
										+ "into giving up. Ah well, no world saving for you.");
						graphic.setTextAttributes(endMessage, Graphics.RED_COLOR,
								Font.font("Verdana", FontWeight.BOLD, 13), "endMessage");
					}
				});
			}
		}, 2000);

	}

	/**
	 * Called when player wins a round
	 * 
	 * @param root
	 * @param x
	 *            x-coordinate of win text
	 * @param y
	 *            y-coordinate of win text
	 */
	private void proceedToNextRound(Group root, double x, double y) {
		animation.stop();
		round2 = true;
		Text winText = graphic.createText(root, x, y, "YOU WIN THIS ROUND");
		graphic.setTextAttributes(winText, javafx.scene.paint.Color.GREEN, Font.font("Verdana", FontWeight.BOLD, 70),
				"win");

		Button start = graphic.createButton("Start round 2", 470, 350, root);
		start.setOnAction((ActionEvent e) -> {
			Group round2Root = myGame.setUpNewScene("Round 2", Game.BATTLE_SCREEN_WIDTH, Game.BATTLE_SCREEN_HEIGHT);
			init(round2Root);
		});
	}

	/**
	 * Checks for the final winner at the end of the game. If not on the last round, then proceed to next round
	 * 
	 * @param root
	 * @param x
	 * @param y
	 * @return name of the winner if there is one, returns "None" if there is no final winner
	 */
	public String checkForWinner(Group root, double x, double y) {
		if (myLifePoints <= 0 && oppLifePoints > 0) {
			if (round2) {
				return "Kaneki";
			} else {
				gameOver(root, 1.5 * x, 1.5 * y);
				return "None";
			}
		} else if (oppLifePoints <= 0) {
			// next round
			if (round2) {
				return "Me";
			} else {
				proceedToNextRound(root, 1.5 * x, 1.5 * y);
				return "None";
			}
		}
		return "None";
	}

}
