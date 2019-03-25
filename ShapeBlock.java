
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * VISION 1.0
 *
 * public class ShapeBlock{
 *     protected CLI_Game blockGenetator = new CLI_Game();
 *     protected CLI_Game.Block rawBlock;
 *     protected static int gap = 20;
 *     protected Pane shapeBlock;
 *     protected char[][][] blockData;
 *     protected Rectangle[][][] blockForElimination;
 *     protected Timeline animation;
 *     protected int rotateState = 0;
 *     //protected int displayState; // which states(dropping / dropped / expected pos
 *     protected enum displayState{
 *         DROPPING,
 *         DROPPED,
 *         EXPECTED,
 *     }
 *
 *     protected ShapeBlock(){
 *     }
 *
 *     protected void initialize(){
 *         shapeBlock = new Pane();
 *         blockData = rawBlock.block;
 *         shapeBlock.setPrefSize(gap * blockData.length, gap * blockData[0].length);
 *         blockForElimination = new Rectangle[blockData.length][blockData[0].length][blockData[0][0].length];
 *         setDisplayState(displayState.DROPPING,0);
 *     }
 *
 *     protected void move(char command){
 *         switch (command){
 *             case 'a':
 *             case 'd':
 *             case 'w':
 *             case 'x':
 *             case 's':
 *         }
 *     }
 *
 *     public Pane getBlock(){
 *         return shapeBlock;
 *     }
 *
 *     protected void setDisplayState(displayState state, int currentState){
 *         shapeBlock.getChildren().clear();
 *         for(int i = 0; i < blockData[0].length; i++){
 *             for(int j = 0; j < blockData[0][0].length; j++){
 *                 if(blockData[currentState][i][j] == '\u2588'){
 *                     Rectangle rec = new Rectangle(gap * i, gap * j, gap,gap);
 *                     switch (state){
 *                         case DROPPED:
 *                             rec.setStroke(Color.GREY);
 *                             rec.setFill(Color.BLUE);
 *                             break;
 *                         case DROPPING:
 *                             rec.setStroke(Color.GREY);
 *                             rec.setFill(Color.BLACK);
 *                             break;
 *                         case EXPECTED:
 *                             rec.setStroke(Color.GREY);
 *                             rec.setFill(Color.GREEN);
 *                             break;
 *                     }
 *                     rec.setArcWidth(gap/3);
 *                     rec.setArcHeight(gap/3);
 *                     blockForElimination[currentState][i][j] = rec;
 *                     shapeBlock.getChildren().add(rec);
 *                     }
 *                 }
 *         }
 *     }
 *
 *
 *
 * }
 *
 * class ShapeBlockS extends ShapeBlock{
 *     ShapeBlockS(){
 *         rawBlock =  blockGenetator.new BlockS();
 *         initialize();
 *     }
 * }
 *
 * class ShapeBlockZ extends ShapeBlock{
 *     ShapeBlockZ(){
 *         rawBlock = blockGenetator.new BlockZ();
 *         initialize();
 *     }
 * }
 *
 * class ShapeBlockI extends ShapeBlock{
 *     ShapeBlockI(){
 *         rawBlock = blockGenetator.new BlockI();
 *         initialize();
 *     }
 * }
 *
 * class ShapeBlockO extends ShapeBlock{
 *     ShapeBlockO(){
 *         rawBlock = blockGenetator.new BlockO();
 *         initialize();
 *     }
 * }
 *
 * class ShapeBlockL extends ShapeBlock{
 *     ShapeBlockL(){
 *         rawBlock = blockGenetator.new BlockL();
 *         initialize();
 *     }
 * }
 *
 * class ShapeBlockJ extends ShapeBlock{
 *     ShapeBlockJ(){
 *         rawBlock = blockGenetator.new BlockJ();
 *         initialize();
 *     }
 * }
 *
 * class ShapeBlockT extends ShapeBlock{
 *     ShapeBlockT(){
 *         rawBlock = blockGenetator.new BlockT();
 *         initialize();
 *     }
 * }
 */
