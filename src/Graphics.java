import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;

public class Graphics {
	public static final Color RED_COLOR= javafx.scene.paint.Color.RED;

	/**
	 * Creates an image object for an image file name
	 * 
	 * @param imageLocation the name of the image file 
	 * @return Image This returns and image object
	 */
	public Image createImage(String imageLocation) {
		Image image = new Image(getClass().getClassLoader().getResourceAsStream(imageLocation));
		return image;
	}

	/**
	 * Creates an ImageView object given an Image
	 * 
	 * @param image the Image object
	 * @return ImageView Returns the ImageView
	 */
	public ImageView createImageView(Image image) {
		return new ImageView(image);
	}

	/**
	 * Sets the background image of the screen
	 * 
	 * @param root the Group to add the image to
	 * @param imageLocation the image file name
	 */
	public void setBackgroundImage(Group root, String imageLocation) {
		Image image = createImage(imageLocation);
		ImageView background = createImageView(image);
		root.getChildren().add(background);
		setImageViewParams(background, 0, 0, Screen.getPrimary().getVisualBounds().getWidth(),
				Screen.getPrimary().getVisualBounds().getHeight());
	}

	/**
	 * Sets the background image with a custom width and height
	 * 
	 * @param root The Group to add the image to
	 * @param imageLocation The image file name
	 * @param width The desired width of the background image
	 * @param height The desired height of the background image
	 */
	public void setBackgroundImage(Group root, String imageLocation, double width, double height) {
		Image image = createImage(imageLocation);
		ImageView background = createImageView(image);
		root.getChildren().add(background);
		setImageViewParams(background, 0, 0, width, height);
	}

	/**
	 * Sets the parameters of an ImageView
	 * 
	 * @param image the ImageView
	 * @param x the x-coordinate of the ImageView
	 * @param y the y-coordinate of the ImageView
	 * @param width the width of the ImageView
	 * @param height the height of the ImageView
	 */
	public void setImageViewParams(ImageView image, double x, double y, double width, double height) {
		image.setX(x);
		image.setY(y);
		image.setFitHeight(height);
		image.setFitWidth(width);
	}

	/**
	 * Sets parameters of an ImageView
	 * 
	 * @param image the ImageView
	 * @param x the x-coordinate of the ImageView
	 * @param y the y-coordinate of the ImageView
	 */
	public void setImageViewParams(ImageView image, double x, double y) {
		image.setX(x);
		image.setY(y);
	}

	/**
	 * Creates and button object
	 * 
	 * @param label The label for the button
	 * @param x The x-coordinate of the button
	 * @param y The y-coordinate of the button
	 * @param root The Group to add the button to
	 * @return Button the button object
	 */
	public Button createButton(String label, int x, int y, Group root) {
		Button button = new Button(label);
		button.setLayoutX(x);
		button.setLayoutY(y);
		root.getChildren().add(button);
		return button;
	}

	/**
	 * @param label
	 * @param x
	 * @param y
	 * @return Button the button object
	 */
	public Button createButton(String label, int x, int y) {
		Button button = new Button(label);
		button.setLayoutX(x);
		button.setLayoutY(y);
		return button;
	}

	/**
	 * Creates and append a text object to a Group
	 * 
	 * @param root the Group to append the text to
	 * @param x the x-coordinate of the text
	 * @param y the y-coordinate of the text
	 * @param text The string value
	 * @return Text the text object
	 */
	public Text createText(Group root, double x, double y, String text) {
		Text t = new Text(x, y, text);
		root.getChildren().add(t);
		return t;
	}

	/**
	 * Sets text attributes
	 * 
	 * @param text The Text object
	 * @param color The color of the text
	 * @param font The text font
	 * @param id The text identifier
	 */
	public void setTextAttributes(Text text, Color color, Font font, String id) {
		text.setFill(color);
		text.setFont(font);
		text.setId(id);
	}

	/**
	 * Creates an important text object, which entails a larger font size and red font color
	 * 
	 * @param root
	 * @param x
	 * @param y
	 * @param astring
	 * @param id
	 */
	public void importantTextAlert(Group root, double x, double y, String astring, String id) {
		Text message = createText(root, x, y, astring);
		setTextAttributes(message, javafx.scene.paint.Color.RED, Font.font("Verdana", FontWeight.BOLD, 70), id);
	}

	/**
	 * Removes a node from a Group after a delay
	 * 
	 * @param root The Group that contains the Node to be removed
	 * @param toRemove The identifier/id of the Node to be removed
	 * @param delay The delay in seconds
	 */
	public void removeAfterDelay(Group root, String toRemove, int delay) {
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						removeNode(root, toRemove);
					}
				});
			}
		}, delay);
	}
	
	/**
	 * Removes a node from a Group
	 * 
	 * @param root
	 * @param toRemove
	 */
	public void removeNode(Group root, String toRemove){
		root.getChildren().remove(root.lookup(toRemove));
	}
}
