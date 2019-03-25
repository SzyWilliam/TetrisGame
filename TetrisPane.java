import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


/**
 * VISION 2.0
 */

public class TetrisPane extends BorderPane {

    CLI_Game game = new CLI_Game();
    Pane centerPane = new Pane();
    private Polyline frame;
    private final int gap = 20;
    Rectangle[][] GUI_map = new Rectangle[game.WIDTH][game.HEIGHT];


    PathTransition pt; // it is for the whole pane settling down
    Timeline autoDroppingAnimation;
    Timeline timeCount;

    int second = 0;
    private Label timeDisplay = new Label("Time");
    private Label scoreDisplay = new Label("Score");
    public Button backFromGame;
    Button pause;
    Button restart;

    private Pane nextBlock = new Pane();
    private Label nextBlockDisplay = new Label("Next Block", nextBlock);

    int gameMode = 0; //0 for classic, 1 for random, 2 for accelerating
    private int acc = 0;
    private int vel = 500;
    boolean first = true;



    protected enum displayState{
        DROPPING,
        DROPPED,
        EXPECTED,
        INVISIBLE,
    }
    public void initialize(){



        Line path = new Line(0,0,0,10);
        pt = new PathTransition(Duration.millis(50), path ,centerPane);
        pt.setAutoReverse(true);
        pt.setCycleCount(2);

        for(int i = 0; i < game.map.length; i++) {
            for (int j = 0; j < game.map[0].length; j++) {
                Rectangle rectangle = new Rectangle(i * gap, j * gap, gap, gap);
                drawRectangle(rectangle,displayState.INVISIBLE);
                GUI_map[i][j] = rectangle;
                centerPane.getChildren().add(rectangle);
            }
        }

        setBackground(new Background(new BackgroundFill(Color.GHOSTWHITE,null,null)));

        setPrefSize(900,500);


        timeDisplay.setLayoutX(440);
        timeDisplay.setLayoutY(200);
        timeDisplay.setTextFill(Color.BLACK);
        timeDisplay.setFont(Font.font("Times New Roman", 20));
        centerPane.getChildren().add(timeDisplay);


        scoreDisplay.setLayoutX(440);
        scoreDisplay.setLayoutY(100);
        scoreDisplay.setTextFill(Color.BLACK);
        scoreDisplay.setFont(Font.font("Times New Roman", 20));
        centerPane.getChildren().add(scoreDisplay);


        nextBlockDisplay.setLayoutX(435);
        nextBlockDisplay.setLayoutY(0);
        nextBlockDisplay.setFont(Font.font("Times New Roman", 19));
        nextBlockDisplay.setContentDisplay(ContentDisplay.BOTTOM);
        centerPane.getChildren().add(nextBlockDisplay);

        backFromGame = new Button("Back");
        backFromGame.setPrefSize(70,20);
        backFromGame.setLayoutX(440);
        backFromGame.setLayoutY(300);
        centerPane.getChildren().add(backFromGame);

        pause = new Button("Pause");
        pause.setPrefSize(70,20);
        pause.setLayoutX(440);
        pause.setLayoutY(270);
        pause.setOnMouseClicked(e->{
            if(pause.getText().charAt(0) == 'P'){
                pause();
                timeCount.pause();
                pause.setText("Resume");
            }else {
                resume();
                timeCount.play();
                pause.setText("Pause");
            }

        });
        centerPane.getChildren().add(pause);

        restart = new Button("Restart");
        restart.setPrefSize(70,20);
        restart.setLayoutX(440);
        restart.setLayoutY(330);
        centerPane.getChildren().add(restart);


        getChildren().add(centerPane);
        drawFrame();

    }

    public void beginningSetting(boolean beginFromPrevious){

        switch (gameMode){
            case 0: case 1:
                //System.out.println();
                game.gameMode = gameMode;
                break;
            case 2:
                acc = 10;
                game.gameMode = 0;
                break;

        }
        game.initialize();
        if(beginFromPrevious){
            game.beginFromPrevious();
        }

    }


    void run(){
        if(first){
            setTimeDisplay();
            first=false;
        }
        setNextBlockDisplay();
        autoDroppingAnimation= setDroppingAnimation();
        autoDroppingAnimation.play();
        setOnKeyPressed(e->listenToKey(e));
    }

    void pause(){
        autoDroppingAnimation.pause();
        setOnKeyPressed(e->{});
    }

    public void resume(){
        autoDroppingAnimation.play();
        setOnKeyPressed(e->listenToKey(e));
    }

    void drawFrame(){
        int strokeWidth = 2;
        double marginLeft = 2.0;
        double marginHeight = 4.0;
        centerPane.setLayoutX(marginLeft * gap);
        centerPane.setLayoutY(marginHeight * gap);
        frame = new Polyline();
        frame.setStrokeWidth(strokeWidth);
        frame.setStroke(Color.LIGHTSLATEGRAY);
        ObservableList<Double> points = frame.getPoints();
        points.addAll((marginLeft+1) * gap-strokeWidth,marginHeight * gap);
        points.addAll((marginLeft+1) * gap-strokeWidth,(game.HEIGHT-1.0+marginHeight) * gap + strokeWidth/3.0);
        points.addAll((marginLeft+1) * gap-strokeWidth + (game.WIDTH-2.0) * gap + strokeWidth,(game.HEIGHT-1.0+marginHeight) * gap + strokeWidth/3.0);
        points.addAll((marginLeft+1) * gap-strokeWidth + (game.WIDTH-2.0) * gap + strokeWidth,marginHeight * gap);

        getChildren().add(frame);

    }

    //先在command line里面move，然后 GUI界面根据command line的结果updat
    //game.currentBlock是cmd，currentBlock是GUI
    private synchronized void currentBlockMove(KeyCode keyCode){
        switch (keyCode){
            case S:
                game.currentBlock.move('s',true);break;
            case A: case LEFT:
                game.currentBlock.move('a',true);MainStage.move.play();break;
            case D: case RIGHT:
                game.currentBlock.move('d',true);MainStage.move.play();break;
            case X: case DOWN:
                game.currentBlock.move('x', true);
                MainStage.decend.play();
                pt.play();
                break;
            case W: case UP:
                game.currentBlock.move('w',true);MainStage.move.play();break;
            case Q:
                autoDroppingAnimation.stop();
                setOnKeyPressed(e->{
                });
        }

    }

    /**
     * this function is used to update the GUI interface according
     * to teh current game state in CLI
     */
    private synchronized void currentBlockUpdatePos(){
        if(game.currentBlock.canSettle()){
            game.settle();
            setNextBlockDisplay();
        }
        game.eliminate();
        drawTheCurrentMap();
        /*for(int i = 0; i < game.map.length; i++){
            for(int j = 0; j < game.map[i].length; j++){
                System.out.print(game.map[j][i] + " ");
            }
            System.out.println();
        }*/
        if(game.isGameOver()){
            currentBlockMove(KeyCode.X);
            currentBlockMove(KeyCode.X);
            autoDroppingAnimation.stop();
            Alert over = new Alert(Alert.AlertType.INFORMATION);
            timeCount.stop();
            over.setTitle("Tetris");
            over.setHeaderText("         Uh-Oh, Game Over!");
            ButtonType button = new ButtonType("all right", ButtonBar.ButtonData.CANCEL_CLOSE);
            over.getButtonTypes().clear();
            over.getButtonTypes().add(button);
            over.setOnHidden(e->{
                if(game.score > MainStage.rankInfo.rankList.get(9).getScore()){
                    TextInputDialog dialog = new TextInputDialog("William");
                    dialog.getDialogPane().getButtonTypes().clear();
                    ButtonType buttonName = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonName);
                    dialog.setTitle("Congratulations!");
                    dialog.setHeaderText("You break the record!");
                    dialog.setContentText("Please enter your name:");
                    Optional<String> result = dialog.showAndWait();
                    if(result.isPresent()){
                        MainStage.rankInfo.addNewRanker(MainStage.rankInfo.new Rank(result.get(), game.score));
                    }

                }
                MainStage.gameStage.close();
                MainStage.mainStage.show();


            });
            over.show();
        }

        scoreDisplay.setText(String.format("Score:\n   %d",game.score));

    }

    /**
     * this function acts as the auto dropping function
     * @return TimeLine dropping animation
     */
    private Timeline setDroppingAnimation(){
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(vel), e->{
                    currentBlockMove(KeyCode.S);
                    currentBlockUpdatePos();
                })
        );
        animation.setCycleCount(Timeline.INDEFINITE);
        return animation;
    }

    /**
     * determines each rectangle's display state
     * according to the command line version
     * if the character == ' ', then invisible
     * if the character == 'flag', then expected
     * if the character == 'block', then dropped
     * then refer to the current block information to make it dropping
     */
    private void drawTheCurrentMap(){
        //centerPane.getChildren().clear();
        for(int i = 0; i < game.map.length; i++){
            for(int j = 0; j < game.map[0].length; j++){
                Rectangle rectangle =GUI_map[i][j];
                if(game.map[i][j] == '\u2588'){
                    drawRectangle(rectangle, displayState.DROPPED);
                }
                else if(game.map[i][j] == '\u2691'){
                    drawRectangle(rectangle, displayState.EXPECTED);
                }
                else{
                    drawRectangle(rectangle, displayState.INVISIBLE);
                }


                for(int p = 0;p< game.currentBlock.block[game.currentBlock.state].length; p++){
                    for(int q = 0; q < game.currentBlock.block[game.currentBlock.state][0].length; q++){
                        if(game.currentBlock.currentPos.y+q == j && game.currentBlock.currentPos.x+p == i &&
                           game.currentBlock.block[game.currentBlock.state][p][q] == '\u2588'){
                            drawRectangle(rectangle, displayState.DROPPING);
                        }
                    }
                }
            }
        }
    }


    private void drawRectangle(Rectangle rec, displayState state){
        switch (state){
            case DROPPED://MEDIUMAQUAMARINE 102 204 189
                Color elabratedGreen = Color.color(99.0/255,207.0/255,183.0/255);
                setRectangleStyle(rec,Color.LIGHTSLATEGRAY,elabratedGreen,1.0,1.0);break;
            case DROPPING:
                setRectangleStyle(rec,Color.GREY,Color.SKYBLUE,1.0,1.0);break;
            case EXPECTED:
                setRectangleStyle(rec,Color.GREY,Color.LAVENDER,0.4,1.0);break;
            case INVISIBLE:
                setRectangleStyle(rec,Color.GREY,Color.TRANSPARENT,1.0,0.0);break;
        }
        rec.setArcWidth(gap/4.0);
        rec.setArcHeight(gap/4.0);
    }

    private void setRectangleStyle(Rectangle rec, Color strokeColor, Color fillColor, double opacity, double strokeWidth){
        rec.setStroke(strokeColor);
        rec.setFill(fillColor);
        rec.setOpacity(opacity);
        rec.setStrokeWidth(strokeWidth);
    }




    private void timeIncrease(){
        second++;
        int min = second / 60;
        int s = second % 60;
        String Time = String.format("Time\n %d:%d",min,s);
        timeDisplay.setText(Time);
    }
    private void setTimeDisplay(){


        timeCount = new Timeline(
                new KeyFrame(Duration.millis(1000), e->{
                    timeIncrease();
                    if(vel < 1500) vel += acc * 5;
                    autoDroppingAnimation.setRate(vel/500.);
                })
        );
        timeCount.setCycleCount(Timeline.INDEFINITE);
        timeCount.play();

    }

    private void setNextBlockDisplay(){
        nextBlock.getChildren().clear();
        for(int i = 0; i < game.nextBlock.block[game.nextBlock.state].length;i++){
            for(int j = 0; j < game.nextBlock.block[game.nextBlock.state][0].length;j++){
                if(game.nextBlock.block[game.nextBlock.state][i][j] == '\u2588'){
                    Rectangle rec = new Rectangle(i*gap, j * gap, gap, gap);
                    drawRectangle(rec, displayState.EXPECTED);
                    nextBlock.getChildren().add(rec);
                }
            }
        }

    }

    private void listenToKey(KeyEvent e){
        currentBlockMove(e.getCode());
        currentBlockUpdatePos();
    }


    public void exportSave() throws IOException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("save@0.txt");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().addAll(extensionFilter);
        Stage s = new Stage();
        File file = fileChooser.showSaveDialog(s);
        if(file == null) throw new IOException();
        game.save(file);
    }


}


/**VISION 1.0
 *
 *
 * import javafx.animation.Animation;
 * import javafx.animation.KeyFrame;
 * import javafx.animation.Timeline;
 * import javafx.collections.ObservableList;
 * import javafx.geometry.Point3D;
 * import javafx.scene.input.KeyCode;
 * import javafx.scene.layout.BorderPane;
 * import javafx.scene.layout.Pane;
 * import javafx.scene.paint.Color;
 * import javafx.scene.shape.Polyline;
 * import javafx.scene.shape.Rectangle;
 * import javafx.scene.transform.Rotate;
 * import javafx.util.Duration;
 * import javax.sound.sampled.Line;
 * import java.io.File;
 * import java.io.FileNotFoundException;
 * import java.io.PrintWriter;
 *
 *
 * public class TetrisPane extends BorderPane {
 *
 *     ShapeBlock currentBlock;
 *     CLI_Game game = new CLI_Game();
 *     Pane centerPane = new Pane();
 *     Rectangle[][] GUI_map = new Rectangle[game.WIDTH][game.HEIGHT];
 *     Timeline autoDroppingAnimation;
 *
 *     void run(){
 *         getChildren().add(centerPane);
 *         //setCenter(centerPane);
 *
 *         game.initialize();
 * //        currentBlock = map(game.currentBlock.getType());
 * //        currentBlockUpdatePos();
 * //        //centerPane.getChildren().add(currentBlock.getBlock());
 * //        currentBlock.animation = setDroppingAnimation();
 * //        currentBlock.animation.play();
 *         autoDroppingAnimation = setDroppingAnimation();
 *         autoDroppingAnimation.play();
 *
 *
 *         setOnKeyPressed(e->{
 *             currentBlockMove(e.getCode());
 *             currentBlockUpdatePos();
 *         });
 *     }
 *
 *     void drawFrame(){
 *         int strokeWidth = 2;
 *         double marginLeft = 2.0;
 *         double marginHeight = 4.0;
 *         centerPane.setLayoutX(marginLeft * ShapeBlock.gap);
 *         centerPane.setLayoutY(marginHeight * ShapeBlock.gap);
 *         Polyline frame = new Polyline();
 *         frame.setStrokeWidth(strokeWidth);
 *         frame.setStroke(Color.RED);
 *         ObservableList<Double> points = frame.getPoints();
 *         points.addAll((marginLeft+1) * ShapeBlock.gap-strokeWidth,marginHeight * ShapeBlock.gap);
 *         points.addAll((marginLeft+1) * ShapeBlock.gap-strokeWidth,(game.HEIGHT-1.0+marginHeight) * ShapeBlock.gap + strokeWidth);
 *         points.addAll((marginLeft+1) * ShapeBlock.gap-strokeWidth + (game.WIDTH-2.0) * ShapeBlock.gap + strokeWidth,(game.HEIGHT-1.0+marginHeight) * ShapeBlock.gap + strokeWidth);
 *         points.addAll((marginLeft+1) * ShapeBlock.gap-strokeWidth + (game.WIDTH-2.0) * ShapeBlock.gap + strokeWidth,marginHeight * ShapeBlock.gap);
 *
 *         getChildren().add(frame);
 *
 *     }
 *
 *     //先在command line里面move，然后 GUI界面根据command line的结果updat
 *     //game.currentBlock是cmd，currentBlock是GUI
 *     public synchronized void currentBlockMove(KeyCode keyCode){
 *         //if(game.currentBlock.canSettle())return;
 *
 * //        for(int i = 0; i < game.currentBlock.block[0].length; i ++){
 * //            for(int j = 0; j < game.currentBlock.block[0][i].length; j++){
 * //                if(game.currentBlock.currentPos.y + j>=0 && game.currentBlock.block[game.currentBlock.state][i][j] == '\u2588'){
 * //                    GUI_map[game.currentBlock.currentPos.x + i][game.currentBlock.currentPos.y + j] = null;
 * //                }
 * //            }
 * //        }
 *
 *         switch (keyCode){
 *             case S:
 *                 game.currentBlock.move('s',true);break;
 *             case A:
 *                 game.currentBlock.move('a',true);break;
 *             case D:
 *                 game.currentBlock.move('d',true);break;
 *             case X:
 *                 game.currentBlock.move('x', true);break;
 *             case W:
 *                 game.currentBlock.move('w',true);
 *                 //centerPane.getChildren().remove(currentBlock.getBlock());
 *                 //currentBlock.setDisplayState(ShapeBlock.displayState.DROPPING,game.currentBlock.state);
 *                 //centerPane.getChildren().add(currentBlock.getBlock());
 *                 break;
 *         }
 *
 *     }
 *
 *     public synchronized void currentBlockUpdatePos(){
 *         //currentBlock is  a class representing GUI Block, while .getblock() returns a Label
 *         //game.currentBlock is a char[][] representing the block in the command line
 *         //currentBlock.getBlock().setLayoutX(game.currentBlock.currentPos.x * ShapeBlock.gap);
 *         //currentBlock.getBlock().setLayoutY(game.currentBlock.currentPos.y * ShapeBlock.gap);
 *
 * //        for(int i = 0; i < game.currentBlock.block[0].length; i ++){
 * //            for(int j = 0; j < game.currentBlock.block[0][i].length; j++){
 * //                if(game.currentBlock.currentPos.y + j >= 0 && game.currentBlock.block[game.currentBlock.state][i][j] == '\u2588'){
 * //                    GUI_map[game.currentBlock.currentPos.x + i][game.currentBlock.currentPos.y + j] = currentBlock.blockForElimination[game.currentBlock.state][i][j];
 * //                }
 * //            }
 * //        }
 *
 *
 *         if(game.currentBlock.canSettle()){
 *             //currentBlock.animation.stop();
 *             //currentBlock.setDisplayState(ShapeBlock.displayState.DROPPED, game.currentBlock.state);
 *             game.settle();
 * //            currentBlock = map(game.currentBlock.getType());//变成新方块
 * //            currentBlockUpdatePos();
 * //            currentBlock.animation = setDroppingAnimation();
 * //
 * //            centerPane.getChildren().add(currentBlock.getBlock());
 * //            currentBlock.animation.play();
 *             //centerPane.setLayoutY(centerPane.getLayoutY() - ShapeBlock.gap);
 *         }
 *
 *         game.eliminate();
 *
 *
 *         for(int i = 0; i < game.map.length; i++){
 *             for(int j = 0; j < game.map[i].length; j++){
 *                 System.out.print(game.map[j][i] + " ");
 *             }
 *             System.out.println();
 *         }
 *         drawTheCurrentMap();
 *
 *     }
 *
 *     Timeline setDroppingAnimation(){
 *         Timeline animation = new Timeline(
 *                 new KeyFrame(Duration.millis(500), e->{
 *                     currentBlockMove(KeyCode.S);
 *                     currentBlockUpdatePos();
 *                 })
 *         );
 *         animation.setCycleCount(Timeline.INDEFINITE);
 *         return animation;
 *     }
 *
 *     ShapeBlock map(String type){
 *         ShapeBlock temp = new ShapeBlock();
 *         switch(type){
 *             case "S":temp = new ShapeBlockS();break;
 *             case "O":temp = new ShapeBlockO();break;
 *             case "J":temp = new ShapeBlockJ();break;
 *             case "L":temp = new ShapeBlockL();break;
 *             case "Z":temp = new ShapeBlockZ();break;
 *             case "T":temp = new ShapeBlockT();break;
 *             case "I":temp = new ShapeBlockI();break;
 *         }
 *         return temp;
 *     }
 *
 *     void drawTheCurrentMap(){
 *         centerPane.getChildren().clear();
 *         for(int i = 0; i < game.map.length; i++){
 *             for(int j = 0; j < game.map[0].length; j++){
 *                 Rectangle rectangle = new Rectangle(i * ShapeBlock.gap, j * ShapeBlock.gap,ShapeBlock.gap,ShapeBlock.gap);
 *                 if(game.map[i][j] == '\u2588'){
 *                     rectangle.setFill(Color.BLUE);
 *                 }
 *                 else {
 *                     rectangle.setFill(Color.WHITE);
 *                 }
 *                 centerPane.getChildren().add(rectangle);
 *             }
 *         }
 *         drawFrame();
 *     }
 * }
 */