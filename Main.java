/**import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    long time = 0;
    void timeIncrease(){
        time++;
        System.out.println(time);
    }


    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        TetrisPane pane = new TetrisPane();
        pane.initialize();
        pane.run();



        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1000),e->timeIncrease())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();




        //TODO make the mainStage return a Pane, use remove(pane)+getchildren.add(pane) to make it smooth

        primaryStage.setX(200.0);
        primaryStage.setY(200.0);

        Scene scene = new Scene(borderPane, 600, 500);

        primaryStage.setTitle("Tetris Game");
        primaryStage.setScene(scene);



        borderPane.setCenter(pane);
        primaryStage.show();
        pane.requestFocus();
    }
}
*/
