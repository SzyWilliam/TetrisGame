
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
/**
 * This is TetrisGame.java for PJ1
 * copyright by William Song
 * created in Oct.2nd, 2018
 *
 * Words failed me when the game is running properly and expectedly
 * how excited the programming is! :) :) :)
 *
 *
 * this java source file defines the main game
 * Ive divided the game into various parts
 * (e.g, mainEntrance() -- Game() -- ending()
 * Game(): GetCommand() -- Move() -- updateMap() -- printState())
 *
 *
 *
 */
public class TetrisGame {

    /**
     * the main entrance of the whole Tetris Game
     * @param args for command use
     */
    public static void main(String[] args){
        readRankList();
        printMainEntrance();
        getNextCommand("Please enter:\ns to start\nq to quit\nr to show rank list\n>>> ");

        while(NextCommand() != 'q'){
            switch (NextCommand()){
                case 's':
                    Game();
                    break;
                case 'r':
                    showRankList();
                    break;
                default:
                    System.out.println("Invalid input.Please enter s/q/r\n>>> ");
            }
            getNextCommand("Please enter:\ns to start\nq to quit\nr to show rank list\n>>> ");
        }
        printEndingPage();
    }


    //lala
    private static char nextCommand;
    private static int score = 0;
    private static Scanner input = new Scanner(System.in);
    private static boolean gameOver = false;
    private static int gameMode = 0;
    public static final int WIDTH = 21;
    public static final int HEIGHT = 21;
    public static char[][] map = new char[WIDTH][HEIGHT];
    private static Block currentBlock;
    private static Block nextBlock;
    private static ArrayList<Rank> rankList = new ArrayList<Rank>();
    private static File rankFile;

    /**
     * this function is used to read the ranking information
     * from the Rank.txt, and store them into the rankList
     * it is called before the main entrance.
     */
    public static void readRankList()
    {
        /*
        read the ranklist information
         */
        File currentDict = new File("");
        rankFile = new File("src/rank.txt");

        try{
            Scanner input = new Scanner(rankFile);
            while(input.hasNext()){
                String line = input.nextLine();
                Rank temp = new Rank(line.split(" ")[0],
                        (int)Integer.parseInt(line.split(" ")[1]));
                rankList.add(temp);
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("ah-oh, we cannot find the rankList.txt");
            System.out.println("Please assure the file exist. and make system path consistent");
            System.exit(1);
        }

        rankList.sort(new SortRank());
    }


    /**
     * this is the main function containing the control flow
     * all the logic and flows of the outer game is defined
     */
    public static void Game() {
        gameOver = false;
        Propoty propoty = new Propoty();
        if(beginFromPrevious("save.txt") &&
                Character.toLowerCase(getNextCommand("Do you what to begin from the previous game? Y/N\n>>> ")) == 'y'){
            currentBlock.removePrevious(map,currentBlock.currentPos,currentBlock.state);
            System.out.println(currentBlock);
        }else {
            char mode;
            do{
                mode = getNextCommand("Please choose game mode:\n1 basic mode\n2 random block mode\n>>>");
            }while(mode != '1' && mode != '2');
            gameMode = mode-'0';
            initialize();
        }
        try {
            currentBlock.update(map);
            printCurrentState();
            save();
            while (getNextCommand("PLease enter:\na for left, d for right\nw for rotation, x for decentingDown\n" +
                    "e for elimination, t for transformation\n>>>") != 'q'
                    && !GameOver()) {
                switch (NextCommand()){
                    case 'a': case'd': case'x': case'w': case 's'://intend to fall through
                    currentBlock.move(nextCommand, true);break;
                    case 'e':
                        int lineNum = 0;
                        //lineNum = Integer.parseInt(input.next());

                        do{
                            System.out.println("Please enter a number between 1-20 ");
                            try{
                                lineNum = Integer.parseInt(input.next());
                            }catch (NumberFormatException ex){}
                        }while(lineNum < 1 || lineNum > 20);
                        input.nextLine();
                        propoty.eliminateALine(HEIGHT-1-lineNum);
                        eliminate(propoty,false);
                        break;
                    case 't':
                        Block temp = nextBlock;
                        try{
                            nextBlock = propoty.ChangeNextBlock();
                        }
                        catch (IndexOutOfBoundsException ex){
                            nextBlock = temp;
                        }

                        break;

                }

                if (currentBlock.canSettle()) {
                    settle();
                    eliminate(propoty, true);

                    //settle();
                    currentBlock.update(map);
                }
                //currentBlock.update(map);
                printCurrentState();
                if(currentBlock.canSettle()) throw new ArrayIndexOutOfBoundsException("end");
                save();
            }
            System.out.println();
        } catch (ArrayIndexOutOfBoundsException ex) {
            /*System.out.println(ex.getMessage());
            for(StackTraceElement i : ex.getStackTrace()){
                System.out.println(i);
            }*/
            gameOver = true;
            endGame();
            save();
            return;
        }
    }


    /**
     * this function initialize the map, draw its boarder and its inner space
     * randomly created the first Block and the next Block
     */
    public static void initialize(){
        for(int i = 0; i < HEIGHT-1; i++){ //side border
            map[0][i] = '|';
            map[WIDTH-1][i] = '|';
        }
        for(int i = 0; i < WIDTH; i++)     //down board
            map[i][HEIGHT-1] = '-';
        for(int i = 1; i < WIDTH-1; i++)   //inner space
            for (int j = 0; j < HEIGHT-1; j++)
                map[i][j] = ' ';

        score = 0;

        currentBlock =randomGenerateBlock();
        nextBlock    =randomGenerateBlock();
    }


    /**
     * below shows the main entrance interface of the game
     */
    public static void printMainEntrance(){
        String page="-----------------------------------------------------\n"+
                    "|                       Tetris                      |\n"+
                    "|         Welcome to the fun world of blocks!       |\n"+
                    "|                 made by William Song              |\n"+
                    "| - - - - - - - - - - - - - - - - - - - - - - - - - |\n"+
                    "|                        input                      |\n"+
                    "|                    s: Start Game                  |\n"+
                    "|                    q: Quit Game                   |\n"+
                    "|                    r: Rank List                   |\n"+
                    "-----------------------------------------------------\n";

        try{
            for(int i = 0; i < page.length(); i++){
                System.out.print(page.charAt(i));
                Thread.sleep(15);
            }
        }
        catch (InterruptedException ex){
            System.exit(777);
        }

        //System.out.print(page);

    }


    /**
     * below shows the end page of the game
     */
    public static void printEndingPage(){
        System.out.println("Goodbye! Hope you've enjoyed your game!");
    }


    /**
     * if the game is over or terminated, this function is called
     * it firstly print the game over information to the user
     * then it compares wether the score beat the ranking
     *      if it is, it'll ask for the name and update the ranking list
     */
    public static void endGame(){
        System.out.println("-----------------------------------------------");
        System.out.println("Uh-oh, Game Over!");
        System.out.println("-----------------------------------------------");
        //System.out.println(rankList.get(1));
        if(rankList.size()< 10 || score > rankList.get(rankList.size()-1).getScore()) {
            System.out.println("Congradulations! you are now in the Rank List");
            System.out.println("Please enter your name: ");
            String name = input.next();
            input.nextLine(); // eat the remaining content
            rankList.add(new Rank(name, score));
            rankList.sort(new SortRank());
            if(rankList.size() > 10){
                rankList.remove(rankList.size() - 1);
            }
            showRankList();
        }
        updateRankList();
    }


    /**
     * print the current map state
     */
    public static void printCurrentState(){
        System.out.println("Next Block would be ");
        nextBlock.output();
        System.out.println("your score is " + score);


        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                System.out.print(map[j][i]);
                System.out.print(' ');
            }
            System.out.println();

        }
    }


    /**
     * this function generated a block for the game
     * if the game mode == 1, it randomly returns one of the seven basic types of blocks
     * if the game mode == 2, it returns BlockRand()
     * @return a block type according the game mode
     */
    public static Block randomGenerateBlock(){
        int num = (int)(Math.random() * 7);
        Block newBlock;
        switch(num){
            case 0: newBlock = new BlockI(); break;
            case 1: newBlock = new BlockJ(); break;
            case 2: newBlock = new BlockL(); break;
            case 3: newBlock = new BlockO(); break;
            case 4: newBlock = new BlockS(); break;
            case 5: newBlock = new BlockT(); break;
            case 6: newBlock = new BlockZ(); break;
            default:newBlock = new BlockO(); break;//THIS WILL NEVER BE EXECUTED I PROMISE
        }
        if(gameMode==1) return newBlock;
        return new BlockRand();
    }


    /**
     * this function is used to read the next command from the console line
     * notice for robuness, it reads one character and eat all the remaining line
     * @param prompt the prompt to instruct the user
     * @return A Single Character
     */
    public static char getNextCommand(String prompt){
        System.out.print(prompt);
        nextCommand = input.next().charAt(0);
        return nextCommand;
    }


    /**
     * @return the next command the user entered
     */
    public static char NextCommand(){
        return nextCommand;
    }


    /**
     * if the current block the bottom, then this function is called
     * it settles the current block, then generate new blocks
     */
    public static void settle(){
        currentBlock = nextBlock;
        currentBlock.calcExpectedPos();
        nextBlock = randomGenerateBlock();
        //currentBlock.update(map);
        //currentBlock.move('s', true);
        //nextBlock.update(map,nextBlock.currentPos,0);
    }


    /**
     * this function is used to display the rank list
     * notice: it must be called after read the rank file
     * otherwise it'll throw a exception
     */
    public static void showRankList(){
        int num = 1;
        for(Rank i: rankList){
            System.out.print("|RANK "+ num++ + "| ");
            System.out.println(i);
        }
    }


    /**
     * this function is used to detect whether there is a full line can
     * be eliminated in the game map
     * if it has, then eliminate the line and update the map and the score
     */
    public static void eliminate(Propoty propoty, boolean awardUse){
        int scoreP = score;
        currentBlock.removePrevious(TetrisGame.map,currentBlock.currentPos,currentBlock.state);
        boolean[] line = new boolean[HEIGHT-1];
        for(int i = 0; i < HEIGHT-1; i++) line[i] = true;

        //find all the line that is full
        //line[i] == true means this line i is full
        for(int i = HEIGHT-2; i >= 0 ; i--){
            for(int j = 1; j < WIDTH-1; j++){
                if(map[j][i] != '\u2588')
                    line[i] = false;
            }
        }

        for(int i = 0;i < line.length; i++){
            if(line[i])
            System.out.println("LINE " + i  + "IS FULL");
        }

        //calculate the score in this elimination
        int time = 1;int layer = 1;
        for(int i = HEIGHT-2; i >=0 ; i--){
            if(line[i] == true){
                while(line[--i]) {time *=2; layer++;}
                score += time * 10 *  layer;
            }
            time = layer = 1;
        }

        //moves the layer upper into the line that is eliminated
        boolean allElim = true;
        while(allElim){
            for(int i = HEIGHT-2; i >= 0 ; i--){
                if(line[i] == true) {
                    for (int p = i; p > 0; p--) {
                        for (int j = 1; j < WIDTH - 1; j++)
                            map[j][p] = map[j][p - 1];
                    }
                    line[i] = false;
                    for(int j = i; j >=1; j--){
                        line[j] = line[j-1];
                    }
                }

            }
            allElim = false;
            for(int q = 0; q < line.length; q++) if(line[q]) allElim = true;

            //currentBlock.update(TetrisGame.map);
        }
        if(awardUse)propoty.update((score-scoreP)/10);

    }

    /**
     * @return gameOver?true/false
     */
    public static boolean GameOver(){
        return gameOver;
    }


    /**
     * this function is used to write the current ranking
     * namely, the most updated ranking info back into the file
     */
    public static void updateRankList(){
        try {
            PrintWriter output = new PrintWriter(rankFile);
            for(Rank i: rankList){
                output.println(i);
            }
            output.close();
        }
        catch (FileNotFoundException ex){
            System.out.println("ERROR: can not open the rankList.txt");
            System.exit(3);
        }
    }

    /**
     * this function is used to save the current state data into save.txt
     * it is called automatically after each update of the movements
     * and run in may computer, it is quite smooth and doesn't slow the game down
     */
    public static void save(){
        File saveFile = new File("save.txt");
        try {
            PrintWriter output = new PrintWriter(saveFile);
            if(gameOver){
                output.println("Game Over");
                output.close();
                return;
            }
            output.println("SCORE:" + score);
            output.println("MODE:" + gameMode);
            output.println("CURRENT_BLOCK:");
            output.println(currentBlock);
            output.println("NEXT_BLOCK:");
            output.println(nextBlock);
            output.println("MAP:");
            for(int i = 0; i < map.length; i++){
                for(int j = 0; j < map[i].length; j++){
                    if(map[i][j] == '\u2691')
                        output.print(' ');
                    else
                        output.print(map[i][j]);
                }
                output.println();
            }
            output.close();
        }
        catch (FileNotFoundException ex){
            System.out.println("uh-oh, we can not read save.txt");
            System.exit(2);
        }
    }


    /**
     * this function is for inner use
     * @param source the source string
     * @param spliter the token that is used to split the string
     * @param index which do you want
     * @return an int number
     */
    private static int analyzeSave(String source, String spliter, int index){
        return Integer.parseInt(source.split(spliter)[index]);
    }


    /**
     * this function is for inner use
     * it read the block information from the save file
     * @param saveReader the scanner
     * @return a block()
     */
    private static Block readBlock(Scanner saveReader){
        saveReader.nextLine();
        //1 read the state
        int state1 = analyzeSave(saveReader.nextLine(), ":",1);
        //2 read the position information
        String pos = saveReader.nextLine();
        int posx,posy;
        posx = analyzeSave(pos,":",1);
        posy = analyzeSave(pos,":",2);
        String posE = saveReader.nextLine();
        int posEx,posEy;
        posEx = analyzeSave(posE,":",1);
        posEy = analyzeSave(posE,":",2);
        //5 read the block type
        String blockType;
        blockType = saveReader.nextLine().split(":")[1];
        switch (blockType.charAt(5)){//'SZLJIOT'
            case 'S': return new BlockS(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'Z': return new BlockZ(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'J': return new BlockJ(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'I': return new BlockI(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'O': return new BlockO(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'T': return new BlockT(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'L': return new BlockL(state1, new Pos(posx,posy), new Pos(posEx, posEy));
            case 'M':{ // this is a random block
                int height = analyzeSave(saveReader.nextLine(), ":",1);
                int len    = analyzeSave(saveReader.nextLine(), ":",1);
                char[][] blockTemp = new char[5][5];
                for(int i = 0; i < 5; i++){
                    String line = saveReader.nextLine();
                    for(int j = 0; j < 5; j++) {
                        blockTemp[i][j] = line.charAt(j);
                    }
                }
                //saveReader.nextLine();//eat a line
                return new BlockRand(state1, new Pos(posx,posy), new Pos(posEx, posEy),len, height, blockTemp);
            }
            default:return new BlockI();//I PROMISE IT NEVER EXECUTES
        }
    }


    /**
     * this function is called when the user want to continue the previous unfinished game
     * it read all the information from the save file and initialize the game
     * @param saveFile the name of the save file
     * @return true if previous game is not over
     */
    public static boolean beginFromPrevious(String saveFile){
        File save = new File(saveFile);
        try{
            Scanner saveReader = new Scanner(save);
            String firstLine = saveReader.nextLine();
            if(firstLine.charAt(0) == 'G'){
                return false;
            }
            //1 read the score first
            score = analyzeSave(firstLine, ":", 1);
            //2 read the mode
            gameMode =analyzeSave(saveReader.nextLine(), ":", 1);


                //3 read the current block
                currentBlock = readBlock(saveReader);
                //eat a line
                saveReader.next();//read a line with nothing
                //4 read the next block
                nextBlock = readBlock(saveReader);


            //eat a line
            saveReader.nextLine();//read a line with nothing
            //5 read the map information
            saveReader.nextLine();
            for(int i = 0; i < map.length; i++){
                String line = saveReader.nextLine();
                for(int j = 0; j < map[i].length; j++){
                    map[i][j] = line.charAt(j);
                }
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println("We can not open the save file");
            System.out.println("The game is terminated abnormally");
            System.exit(789);
        }
        return true;
    }
}
