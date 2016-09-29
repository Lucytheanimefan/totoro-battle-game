
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * author: Lucy Zhang
 */
public class main extends Application {
    public static final int SIZE = 400;
    
    private Game myGame;


    /**
     * Set things up at the beginning.
     */
    @Override
    public void start (Stage s) {
        // create your own game here
        myGame = new Game();
        s.setTitle(myGame.getTitle());
        myGame.setPrevStage(s);

        // attach game to the stage and display it
        Scene scene = myGame.init(960, 540);
        s.setScene(scene);
        s.show();

       
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}