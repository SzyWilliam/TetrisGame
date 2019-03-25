import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainStage extends Application {
    public static Stage mainStage = new Stage();
    public static Stage gameStage = new Stage();
    private Stage rankStage = new Stage();
    private Stage instructionStage = new Stage();
    private Stage gameOption = new Stage();
    private final int BT_WIDTH = 114;  // the width for the 4 buttons
    private final int BT_HEIGHT = 40;  // the height for the 4 buttons
    private AudioClip mouseDragged;
    private AudioClip mouseClicked;


    private MediaPlayer bgm;

    private Button newGame, resume, ranking, introduction;
    private Button backFromRank, backFromInstru;
    private Button ok,back;
    private Pane pane = new Pane(); //this is for main stage
    private Pane rankPane = new Pane(); // this is for rank pane
    private TetrisPane pane_game = new TetrisPane();// this is for game pane
    private GridPane rankingPane = new GridPane();
    private Pane instructPane = new Pane();

    static public AudioClip  decend;
    static public AudioClip  move;



    private Scene scene ;

    static RankInfo rankInfo = new RankInfo("rank.txt");


    @Override
    public void start(Stage primaryStage) {
        initialize();
        setMainStage();
        setGameStage();
        setRankStage();
        setInstructionStage();
        setGameOptionStage();
        mainStage.show();
        bindStage();
    }

    /**
     * this function adds the UI_sounds and Background music
     * it also reads the ranking list information
     */
    private void initialize(){
        java.io.File file = new java.io.File("src/switchMouse.mp3");
        String url = file.toURI().toString();
        java.io.File file2 = new java.io.File("src/mouseClickV2.mp3");
        String url2 = file2.toURI().toString();
        mouseDragged = new AudioClip(url);
        mouseClicked = new AudioClip(url2);
        mouseClicked.setVolume(0.3);

        java.io.File bgmFile = new java.io.File("src/Flower Dance - Musie.mp3");
        //bgmFile = new java.io.File("src/decend.mp3");
        String url3 = bgmFile.toURI().toString();
        bgm = new MediaPlayer(new Media(url3));
        bgm.setVolume(0.4);
        bgm.setCycleCount(MediaPlayer.INDEFINITE);
        bgm.play();




        rankInfo.readRankList();

    }

    /**
     * carefully arrange  all the buttons and bg_image
     * into the main stage(The entrance)
     */
    private void setMainStage(){
        ImageView imageView = new ImageView("bg13v0.jpg");
        imageView.setFitWidth(500);
        imageView.setFitHeight(700);
        imageView.setOpacity(0.7);
        Label bgImage = new Label("", imageView);
        bgImage.setPrefSize(100,100);
        pane.getChildren().add(bgImage);

        ImageView gameFont = new ImageView("gameFontv3.png");
        gameFont.setFitWidth(250);
        gameFont.setFitHeight(60);



        Label tetris = new Label("",gameFont);
        tetris.setPrefSize(200,50);
        tetris.setLayoutX((500-250)/2.0);
        tetris.setLayoutY(70);
        Font font = Font.font("PingFang SC", FontWeight.BOLD,35);
        tetris.setFont(font);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(6.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        tetris.setEffect(ds);
        tetris.setCache(true);
        pane.getChildren().add(tetris);


        newGame = setButtonn((500-BT_WIDTH)/2.0, 160.0, "Game" );
        pane.getChildren().add(newGame);

        resume = setButtonn((500-BT_WIDTH)/2.0, 230, "Resume");
        pane.getChildren().add(resume);

        ranking = setButtonn((500-BT_WIDTH)/2.0, 300.0, "Ranking" );
        pane.getChildren().add(ranking);

        introduction = setButtonn((500-BT_WIDTH)/2.0, 370, "Reference");
        pane.getChildren().add(introduction);


        scene = new Scene(pane, 500,500);
        mainStage.setFullScreen(false);
        mainStage.setScene(scene);
        mainStage.setTitle("test stage");
    }

    /**
     * set the buttons in the main stage with
     * thier common behavior
     * @param button which_button
     */
    private void setButtonAnimation(Button button){


        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e->{
            mouseDragged.play();
            button.setContentDisplay(ContentDisplay.LEFT);
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            button.setContentDisplay(ContentDisplay.TEXT_ONLY);
        });

        button.setOnMouseClicked(e->{
            mouseClicked.play();
        });
    }

    /**
     * set the button's size and its display place
     * @param width button.width
     * @param height button.height
     * @param content button.text
     * @return the BUTTON
     */
    private Button setButtonn(double width, double height, String content){

        ImageView tick = new ImageView("tickV1.png");
        tick.setFitHeight(10);
        tick.setFitWidth(6);
        Button button = new Button(content,tick);
        button.setPrefSize(BT_WIDTH,BT_HEIGHT);
        button.setLayoutX((503.0-BT_WIDTH)/2);
        button.setLayoutY(height);
        button.setContentDisplay(ContentDisplay.TEXT_ONLY);
        button.setFont(Font.font("Bookman Old Style",16));

        setButtonAnimation(button);
        return button;
    }

    private void setGameStage(){
        java.io.File file_move = new java.io.File("src/X.mp3");
        String url_move = file_move.toURI().toString();
        move = new AudioClip(url_move);
        decend = mouseClicked;
        move.setVolume(100);
        decend.setVolume(1);
        //pane_game.getChildren().add(move);


        pane_game.initialize();
        gameStage.setScene(new Scene(pane_game));
        gameStage.setWidth(600);

    }

    /**
     * the ranking stage
     * show the rank list with UI
     */
    private void setRankStage(){
        rankingPane.setLayoutX(20);
        rankingPane.setLayoutY(120);
        rankingPane.setHgap(10);
        rankingPane.setVgap(5);
        RankInfo.Rank top = rankInfo.rankList.get(1);

        for(int i = 0; i < rankInfo.rankList.size(); i++){
            RankInfo.Rank rank = rankInfo.rankList.get(i);
            Label lblName = new Label(rank.getName());
            rankingPane.add(lblName, 1, i+1);
            int recHeight = 20;
            int recWidth = 180;
            Rectangle rankingLength = new Rectangle((double)rank.getScore() / top.getScore() * recWidth, recHeight);
            rankingLength.setFill(Color.SKYBLUE);
            rankingLength.setOpacity((double)rank.getScore() / top.getScore()+0.3);
            Label lblScore = new Label(Integer.toString(rank.getScore()), rankingLength);
            lblScore.setContentDisplay(ContentDisplay.LEFT);

            rankingPane.add(lblScore,2,i+1);
        }

        Label lblTitle = new Label("Ranking");
        lblTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        lblTitle.setLayoutX(200);
        lblTitle.setLayoutY(40);
        //rankPane.setPrefSize(500,500);


        backFromRank = new Button("Back");
        backFromRank.setPrefSize(70,20);
        backFromRank.setOnMouseClicked(e->{
            rankStage.close();
            mainStage.show();
        });
        backFromRank.setLayoutX(400);
        backFromRank.setLayoutY(400);
        rankPane.getChildren().add(backFromRank);
        rankPane.getChildren().add(lblTitle);
        rankPane.getChildren().add(rankingPane);

        rankStage.setScene(new Scene(rankPane,500,500));

    }

    /**
     * the user's reference stage
     * show the help information and instructions
     */
    private void setInstructionStage(){
        String intru = "The primary goal of this game is to manipulate\n" +
                " all the blocks and put them in appropriate places\n " +
                "in order to eliminate more lines and hence achieve higher scores." +
                "\n For each block, there are four valid commands:" +
                " \na(move left)\nd(move right)\n" +
                "w(rotate clock wise)\n" +
                "x(directly settle the current block)";
        TextArea textField = new TextArea(intru);
        textField.setLayoutX(40);
        textField.setLayoutY(100);
        textField.setPrefRowCount(10);
        textField.setPrefColumnCount(30);
        textField.setEditable(false);

        

        instructPane.getChildren().add(textField);


        Label title = new Label("Help");
        title.setFont(Font.font("Times New  Roman", FontWeight.BOLD, 30));
        title.setTextFill(Color.SKYBLUE);
        title.setLayoutX(210);
        title.setLayoutY(30);
        instructPane.getChildren().add(title);

        backFromInstru = new Button("Back");
        backFromInstru.setPrefSize(70,20);
        backFromInstru.setLayoutX(300);
        backFromInstru.setLayoutY(350);
        backFromInstru.setOnMousePressed(e->{
            instructionStage.close();
            mainStage.show();
        });
        instructPane.getChildren().add(backFromInstru);

        instructionStage.setScene(new Scene(instructPane,500,500));
    }


    private void setGameOptionStage(){
        Pane GameStagePane = new Pane();
        Pane optionPane = new VBox();
        optionPane.setLayoutX(150);
        optionPane.setLayoutY(150);
        ((VBox) optionPane).setSpacing(10);
        ImageView imageView = new ImageView(new Image("bg15v0.jpg"));
        imageView.setFitHeight(500);
        imageView.setFitWidth(500);
        imageView.setOpacity(0.7);
        GameStagePane.getChildren().addAll(imageView);

        Label instru = new Label("Please choose a mode:");
        instru.setFont(Font.font(27));
        instru.setLayoutX(70);
        instru.setLayoutY(70);
        GameStagePane.getChildren().addAll(instru);


        RadioButton classic = new RadioButton("Classic");
        classic.setFont(Font.font(25));
        RadioButton randomBlock = new RadioButton("Random Block");
        randomBlock.setFont(Font.font(25));
        RadioButton Accelerando = new RadioButton("Accelerando");
        Accelerando.setFont(Font.font(25));
        RadioButton beginFromSave = new RadioButton("Begin from save");
        beginFromSave.setFont(Font.font(25));
        ToggleGroup group = new ToggleGroup();
        classic.setToggleGroup(group);
        classic.setSelected(true);
        randomBlock.setToggleGroup(group);
        Accelerando.setToggleGroup(group);
        beginFromSave.setToggleGroup(group);
        optionPane.getChildren().addAll(classic,randomBlock,Accelerando,beginFromSave);

        ok = new Button("OK");
        ok.setFont(Font.font(18));
        ok.setOnMouseClicked(e->{
            pane_game.game = new CLI_Game();

            if(classic.isSelected()){
                pane_game.gameMode = 0;
                pane_game.beginningSetting(false);
            }else if(randomBlock.isSelected()){
                pane_game.gameMode = 1;
                pane_game.beginningSetting(false);
            }else if(Accelerando.isSelected()){
                pane_game.gameMode = 2;
                pane_game.beginningSetting(false);
            }else{
                File file;
                do{
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose the save file");
                    file = fileChooser.showOpenDialog(mainStage);
                    if(file.exists() && file.getAbsolutePath().endsWith(".txt")){
                        pane_game.game.save = file;
                        pane_game.beginningSetting(true);
                    }
                }while(!file.exists());

            }



            pane_game.run();
            gameOption.close();
            showGameStage();
            pane_game.requestFocus();
        });
        ok.setLayoutX(250);
        ok.setLayoutY(360);
        ok.setPrefSize(70,20);

        back = new Button("Back");
        back.setFont(Font.font(18));
        back.setLayoutX(340);
        back.setLayoutY(360);
        back.setPrefSize(70,20);

        GameStagePane.getChildren().add(optionPane);
        GameStagePane.getChildren().addAll(ok, back);


        Scene scene = new Scene(GameStagePane,500,500);
        scene.setFill(Color.color(244./255,244./255,244./255));


        gameOption.setScene(scene);


    }


    /**
     * set all the stage shifting button
     * binding their clicks with stage transformation
     */
    private void bindStage(){

        newGame.setOnMouseClicked(e->{
            mouseClicked.play();
            mainStage.close();
            gameOption.show();
        });

        resume.setOnMouseClicked(e->{
            mouseClicked.play();
            //mainStage.close();

        });

        ranking.setOnMouseClicked(e-> {
            mouseClicked.play();
            mainStage.close();
            rankStage.show();
        });

        pane_game.backFromGame.setOnMouseClicked(e->{
            pane_game.pause();
            Alert alert = generateConfirmationAlert("是否保存这次的游戏？");
            Optional<ButtonType> b = alert.showAndWait();
            if(b.isPresent() && b.get() == ButtonType.YES){
                try{
                    pane_game.exportSave();
                }catch (IOException ex){
                    //do nothing stupid user
                }
            }
            gameStage.close();
            mainStage.show();
        });
        back.setOnMouseClicked(e->{
            gameOption.close();
            mainStage.show();
        });
        pane_game.restart.setOnMouseClicked(e->{
            pane_game.pause();
            Alert alert = generateConfirmationAlert("重新开始一局游戏？");
            Optional<ButtonType> b = alert.showAndWait();

            if(b.isPresent() && b.get() == ButtonType.YES){
                pane_game.game = new CLI_Game();
                pane_game.second = 0;
                pane_game.game.initialize();
                pane_game.run();
            }else {
                pane_game.resume();
            }
        });

        introduction.setOnMousePressed(e->{
            mainStage.close();
            instructionStage.show();
        });


    }

    Alert generateConfirmationAlert(String prompt){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(prompt);
        ButtonType btYes = ButtonType.YES, btNo = ButtonType.NO;
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(btNo, btYes);
        return alert;
    }

    public void showGameStage(){
        if(pane_game.pause.getText().charAt(0) == 'R'){
            pane_game.pause.setText("Pause");
        }
        pane_game.second = 0;
        gameStage.show();
    }

}
