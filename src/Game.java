import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.control.*;

import java.util.HashMap;

import javafx.event.*;

public class Game {
	public static final String TITLE = "Totoro save the world";
	public static final int SIZE = 400;
	public static final int BATTLE_SCREEN_WIDTH=1000;
	public static final int BATTLE_SCREEN_HEIGHT=700;
	public static HashMap<String, Double> abilitiesScores;
	public static Scene currentScene;
	private Scene myScene;
	public static String characterName;
	public static Stage prevStage;

	Graphics graphic;

	/**
	 * Returns name of the game.
	 */
	public String getTitle() {
		return TITLE;
	}

	/**
	 * @return the character's name
	 */
	public String getCharacterName() {
		return characterName;
	}


	/**
	 * @return the current scene
	 */
	public Scene getCurrentScene() {
		return currentScene;
	}

	/**
	 * @return a hashmap containing the possible moves and their corresponding lifepoint/score worth 
	 */
	public HashMap<String, Double> getAbilitiesScores() {
		return abilitiesScores;
	}

	/**
	 * Sets the previous stage
	 * @param stage
	 */
	public void setPrevStage(Stage stage) {
		prevStage = stage;
	}

	/**
	 * Initializes the game and its graphics
	 * @param width
	 * @param height
	 * @return the scene
	 */
	public Scene init(int width, int height) {
		// create a scene graph to organize the scene
		Group root = new Group();
		// create a place to see the shapes
		myScene = new Scene(root, width, height, Color.WHITE);
		graphic = new Graphics();
		// set background and totoro
		graphic.setBackgroundImage(root, "background.png");
		createPlayButton(root, 700, 500);
		Text instructions = graphic.createText(root, 25, 50, "ANIME BATTLE GAME INSTRUCTIONS: \n\n"
				+ "Anime characters have taken over the world. Your goal is to defeat a number of anime characters  \n"
				+ "with your clever strategizing and otaku knowledge. Both you and your opponent have 50 life points. \n"
				+ "Whoever reaches 0 first loses. To defeat your opponent, you can throw weapons at them, jump on \n"
				+ "them, or leverage your special abilities detailed later. Meanwhile the opponent can also throw \n"
				+ "weapons at you. Furthermore, if you get too close to an opponent, they will go berserk and cause \n"
				+ "you to lose life points. (Your opponents are not the most stable of beings. Please proceed to see \n"
				+ "the keyboard shortcuts to these moves. You may also use the arrow keys to move around in the game. \n\n"
				+ "To sum up: \n" + "1. There are keyboard shortcuts that you use to execute attacks and move around.\n"
				+ "2. If you get too close to the enemy, you will likely die. \n" + "3. The enemy will attack you.");
		graphic.setTextAttributes(instructions, javafx.scene.paint.Color.WHITE,
				Font.font("Verdana", FontWeight.BOLD, 16), "instructions");
		return myScene;
	}

	private void startBattle() {
		Group root = setUpNewScene("Round 1", SIZE + 100, SIZE);
		ImageView character = graphic.createImageView(graphic.createImage("totoro.png"));
		graphic.setImageViewParams(character, SIZE / 3.5, 0, 220, 200);
		root.getChildren().add(character);
		graphic.createText(root, 50, 200, "You are Totoro on a mission to save the world");
		populateAbilitiesScoresDict();
		displayTotoroAbilities(root);

		Button start = graphic.createButton("Start game", 300, 370, root);
		start.setOnAction((ActionEvent e) -> {
			Group round1Root = setUpNewScene("Round 1", BATTLE_SCREEN_WIDTH, BATTLE_SCREEN_HEIGHT);
			BattleRound round1 = new BattleRound();
			round1.init(round1Root);
		});
	}

	private void displayTotoroAbilities(Group root) {
		graphic.createText(root, 10, 240,
				"You have the following abilities: \n"
						+ "Spirit Summoner (Shift key) - Can summon forest spirits, worth 7 life points \n"
						+ "Green thumb (G key) - Can grow any plant \n"
						+ "Throw mini totoro(T key) - 10 total throws, worth 5 or so life points \n"
						+ "Jump (J key) - You must get close enough to the enemy to jump on it, worth 5 life points \n"
						+ "Friendly Neighbor (C key) - Moral righteousness/ ability to show compassion.\n"
						+ "4 total spirit summons, each worth 7 life points.");
	}

	private void populateAbilitiesScoresDict() {
		abilitiesScores = new HashMap<String, Double>();
		abilitiesScores.put("Jump", 5.0);
		abilitiesScores.put("Spirit Summoner", 7.0);
		abilitiesScores.put("Green Thumb", 50.0);
		abilitiesScores.put("Intimidation", 1.0);
		abilitiesScores.put("Hit", 1.0);

	}


	/**
	 * Creates a new scene
	 * @param title 
	 * 			Title of the new scene
	 * @param width 
	 * 			Width of the new scene
	 * @param Height 
	 * 			height of the new scene
	 * @return the Group
	 */
	public Group setUpNewScene(String title, int width, int height) {
		Stage stage = new Stage();
		stage.setTitle(title);
		Group root = new Group();
		Scene scene = new Scene(root, width, height, Color.WHITE);
		currentScene = scene;
		stage.setScene(scene);
		prevStage.close();
		stage.show();
		prevStage = stage; // reassign prevStage
		return root;
	}

	private Button createPlayButton(Group root, int x, int y) {
		Button button = graphic.createButton("Play game", x, y, root);
		button.setOnAction((ActionEvent e) -> {
			startBattle();
		});
		return button;
	}

	

}
