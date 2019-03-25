import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class CLI_Game {
    public  final int WIDTH = 21;
    public  final int HEIGHT = 21;
    public  char[][] map = new char[WIDTH][HEIGHT];
    public int gameMode = 0;
    public Block currentBlock;
    public Block nextBlock;
    public int score = 0;

    public boolean gameOver = false;
    public File save;




    class Pos{
        public int x;
        public int y;

        Pos(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "currentPos (" + x + "," + y + ")";
        }
    }


    /**
     * this is the base class representing a block
     * it contains the basic function for move, rotate, draw and eliminate, etc.
     */
    public class Block
    {


        protected char[][][] block;  //[state][xPos][yPos]
        protected int state;         //all four types for rotation
        protected Pos currentPos;    // the [state][0][0] point's Position in the TetrisMAP
        protected Pos expectedPos;   // expected dropping place

        /**
         *
         This function valid is use to judge whether or not next move is valid;

         targetPosX: next move the left_up block.x
         targetPosY: next move the left_up block.y
         targetState: if it is a rotation, then the targetState is the expected next state

         Algorithm: it tests wether it is out of boarder
         then it determines wether the map has already has a asterisk on the expected place

         */
        protected boolean valid(    int targetPosX,
                                    int targetPosY,
                                    int targetState){

            boolean  isValid = true;
            //System.out.println("handling"+targetPosX+","+targetPosY);
            for(int i = 0; i < this.block[targetState].length; i++){
                for(int j = 0; j < this.block[targetState][i].length; j++){
                    if(block[targetState][i][j] == '\u2588'){
                        if(targetPosX + i <= 0 || targetPosX +i >= WIDTH-1){ // the x pos out of boarder
                            //System.out.println("INVALID_1");
                            return  false;
                        }
                        if(targetPosY + j >= HEIGHT-1 /*|| targetPosY + j <= 0*/){ //the y  pos out of boarder
                            //System.out.println("INVALID_2");
                            return  false;
                        }
                        if(targetPosY + j >= 0&&map[targetPosX + i][targetPosY + j] == '\u2588'){
                            isValid = false;
                            //System.out.println("INVALID_3");
                        }

                        //else if(targetPosY + i == 0)            //if it exceeded, then oops the game is over
                        //setGameOver();
                        //if(targetPosX == expectedPos.x && targetPosY == expectedPos.y)
                        //settle();       // settle it down and turn to the new block!!yeah!
                    }
                }
            }
            return isValid;
        }

        Block(){
            state = 0;
            currentPos = new Pos(8,0);
            expectedPos = new Pos(8,0);
        }

        protected void setInitial(int state, Pos currentPos, Pos expectedPos) {
            this.state = state;
            this.currentPos = currentPos;
            this.expectedPos = expectedPos;
        }
        @Override
        public String toString() {
            StringBuilder info = new StringBuilder();
            info.append("STATE:");
            info.append(state);
            info.append("\nCURRENT_POS:");
            info.append(currentPos.x).append(":").append(currentPos.y);
            info.append("\nEXPECTED_POS:");
            info.append(expectedPos.x).append(":").append(expectedPos.y).append("\n");
            return info.toString();
        }

        /**this function is used to draw the block into the map
         * it eliminates the previous state, then draw the current state
         * finally it draws the expected dropping place
         *
         * @param map   this is the game map
         * @param previous the Previous Position used to eliminate
         * @param previousState the Previous State used to eliminate
         */
        void removePrevious(char[][] map, Pos previous, int previousState){
            //eliminate the previous state
            for(int i = 0;i< block[previousState].length; i++){
                for(int j = 0; j < block[previousState][i].length; j++){
                    if(previous.y+j >=0 && block[previousState][i][j] == '\u2588'){
                        map[previous.x + i][previous.y + j] = ' ';
                    }
                }
            }

            //eliminate the previous dropping place
            for(int i = 0; i < block[previousState].length; i++){
                for(int j = 0; j < block[previousState][i].length; j++){
                    if(expectedPos.y + j < HEIGHT &&
                            expectedPos.y + j > 0  && block[previousState][i][j] == '\u2588'){
                        map[expectedPos.x + i][expectedPos.y + j] = ' ';
                    }
                }
            }
        }

        /**
         * this function is used to draw the current block and its expected dropping
         * place into the map
         * @param map the tetrisGame's main map
         */
        void update(char[][] map){


            calcExpectedPos();
            //draw the expected dropping place
            for(int i = 0; i < block[state].length; i++){
                for(int j = 0; j < block[state][i].length; j++){
                    if(expectedPos.y+j >= 0 && block[state][i][j] == '\u2588'){
                        map[expectedPos.x + i][expectedPos.y + j] = '\u2691';//a flag
                    }
                }
            }
            //draw the current state
            for(int i = 0;i< block[state].length; i++){
                for(int j = 0; j < block[state][i].length; j++){
                    if(currentPos.y+j >= 0 && block[state][i][j] == '\u2588'){
                        map[currentPos.x + i][currentPos.y + j] = '\u2588';
                    }
                }
            }


        }

        /**
         * this function is used to handle the block's move
         * it first calls the func(valid) to determine wether next move is valid
         * then it adjust the state|posX|posY according to the command
         * last it calls func(update) to draw the current state to the map
         * @param command : 'wsadx'
         */
        void move(char command, boolean remove){
            //System.out.println("moving");
            Pos previous = new Pos(currentPos.x, currentPos.y);
            if(remove)removePrevious(map, previous, state);
            switch(command){
                case 'w':{
                    if(valid(currentPos.x, currentPos.y, (state+1)%4)){
                        //System.out.println("valid in base");
                        rotateClockWise();
                        //currentPos.y += 1;
                    }
                    //currentPos.y+=1;
                    break;
                }
                case 'a':{
                    if(valid(currentPos.x-1, currentPos.y,state)){
                        currentPos.x -= 1;
                        //currentPos.y += 1;
                    }
                    break;

                }
                case 'd':{
                    if(valid(currentPos.x+1, currentPos.y, state)){
                        currentPos.x += 1;
                        //currentPos.y += 1;
                    }
                    break;
                }
                case 's':{
                    if(valid(currentPos.x, currentPos.y+1,state)){
                        currentPos.y += 1;
                    }
                    break;
                }
                case 'x':{
                    currentPos.x = expectedPos.x;
                    currentPos.y = expectedPos.y;
                    break;
                }
                default:{
                    //how could default happen? in main I've already assured command would be valid;
                    System.out.println("an error occurred.");
                }
            }
            update(map);

        }

        /**literally
         * it just shift the currentState to next state
         */
        void rotateClockWise(){
            int previousState = state;
            state  = (state + 1) % 4;
        }


        /**
         * it calculates the expected dropping place of the current block
         * which is shous in '+' in the map
         */
        void calcExpectedPos(){

            int Space = HEIGHT;
            for(int j = 0; j < block[state].length; j++){
                for(int i = 0; i < block[state][j].length; i++){
                    if(block[state][i][j] == '\u2588'){
                        if(j+1 < block[state].length && block[state][i][j+1] == '\u2588')continue;
                        int space = 0;
                        while(currentPos.y+j+space <= 0 ||map[currentPos.x+i][currentPos.y+j+space] == ' ')
                            space++;
                        if(space < Space) Space = space;

                    }
                }
            }
            expectedPos.x = currentPos.x;
            expectedPos.y = currentPos.y + Space-1;
        }

        /**
         * it detects wether a block can be settled in current pos
         * @return true if can settle
         */
        boolean canSettle(){
            return currentPos.x == expectedPos.x && currentPos.y >= expectedPos.y;
        }

        void output(){
            for(int i = 0; i < block[state].length; i++){
                for(int j = 0; j < block[state][i].length; j++){
                    //if(block[state][j][i] == '\u2588')
                    System.out.print(block[state][j][i]);
                    System.out.print(' ');
                }
                System.out.println();
            }
        }

        public String getType(){
            return null;
        }

    }

    /**
     * the 7 Block_ classes below defines each specific types
     * they are all inherited from the base class
     */
    public class BlockS extends Block{
        BlockS(){
            super();

            block = new char[4][3][3];
            for(int i = 0; i < 3; i++)
                for(int j = 0; j < 3; j++)
                    for(int k = 0; k < 3; k++)
                        block[i][j][k] = ' ';

            block[0][0][1] = block[0][1][1] = block[0][1][0] = block[0][2][0] = '\u2588';
            block[1][1][0] = block[1][1][1] = block[1][2][1] = block[1][2][2] = '\u2588';
            block[2][0][2] = block[2][1][2] = block[2][1][1] = block[2][2][1] = '\u2588';
            block[3][0][0] = block[3][0][1] = block[3][1][1] = block[3][1][2] = '\u2588';
            currentPos = new Pos(8,-1);

            calcExpectedPos();

        }

        BlockS(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }

        @Override
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKS\n";
        }

        @Override
        public String getType() {
            return "S";
        }
    }

    public class BlockZ extends Block{
        BlockZ(){
            super();

            block = new char[4][3][3];
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 3; j++)
                    for(int k = 0; k < 3; k++)
                        block[i][j][k] = ' ';

            block[0][0][0] = block[0][1][0] = block[0][1][1] = block[0][2][1] = '\u2588';
            block[1][1][1] = block[1][1][2] = block[1][2][0] = block[1][2][1] = '\u2588';
            block[2][0][1] = block[2][1][1] = block[2][1][2] = block[2][2][2] = '\u2588';
            block[3][0][1] = block[3][0][2] = block[3][1][0] = block[3][1][1] = '\u2588';
            currentPos = new Pos(8,-1);
            calcExpectedPos();

        }
        BlockZ(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKZ\n";
        }

        @Override
        public String getType() {
            return "Z";
        }
    }

    public class BlockT extends Block{
        BlockT(){
            super();

            block = new char[4][3][3];
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 3; j++)
                    for(int k = 0; k < 3; k++)
                        block[i][j][k] = ' ';

            block[0][0][1] = block[0][1][1] = block[0][2][1] = block[0][1][0] = '\u2588';
            block[1][1][0] = block[1][1][1] = block[1][1][2] = block[1][2][1] = '\u2588';
            block[2][0][1] = block[2][1][1] = block[2][2][1] = block[2][1][2] = '\u2588';
            block[3][1][0] = block[3][1][1] = block[3][1][2] = block[3][0][1] = '\u2588';
            currentPos = new Pos(8,-1);
            calcExpectedPos();

        }
        BlockT(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }

        @Override
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKT\n";
        }
        @Override
        public String getType() {
            return "T";
        }
    }

    public class BlockO extends Block{
        BlockO(){
            super();

            block = new char[4][2][2];
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 2; j++)
                    for(int k = 0; k < 2; k++)
                        block[i][j][k] = ' ';

            block[0][0][0] = block[0][0][1] = block[0][1][0] = block[0][1][1] = '\u2588';
            block[1][0][0] = block[1][0][1] = block[1][1][0] = block[1][1][1] = '\u2588';
            block[2][0][0] = block[2][0][1] = block[2][1][0] = block[2][1][1] = '\u2588';
            block[3][0][0] = block[3][0][1] = block[3][1][0] = block[3][1][1] = '\u2588';
            currentPos = new Pos(8,-1);

            calcExpectedPos();

        }
        BlockO(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }

        @Override
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKO\n";
        }
        @Override
        public String getType() {
            return "O";
        }
    }

    public class BlockL extends Block{
        BlockL(){
            super();

            block = new char[4][3][3];
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 3; j++)
                    for(int k = 0; k < 3; k++)
                        block[i][j][k] = ' ';

            block[0][0][0] = block[0][0][1] = block[0][1][1] = block[0][2][1] = '\u2588';
            block[1][2][0] = block[1][1][0] = block[1][1][1] = block[1][1][2] = '\u2588';
            block[2][0][1] = block[2][1][1] = block[2][2][1] = block[2][2][2] = '\u2588';
            block[3][0][2] = block[3][1][0] = block[3][1][1] = block[3][1][2] = '\u2588';
            currentPos = new Pos(8,-1);
            calcExpectedPos();

        }
        BlockL(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }

        @Override
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKL\n";
        }
        @Override
        public String getType() {
            return "L";
        }
    }

    public class BlockJ extends Block{
        BlockJ(){
            super();

            block = new char[4][3][3];
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 3; j++)
                    for(int k = 0; k < 3; k++)
                        block[i][j][k] = ' ';

            block[0][0][1] = block[0][1][1] = block[0][2][1] = block[0][2][0] = '\u2588';
            block[1][1][0] = block[1][1][1] = block[1][1][2] = block[1][2][2] = '\u2588';
            block[2][0][2] = block[2][0][1] = block[2][1][1] = block[2][2][1] = '\u2588';
            block[3][1][0] = block[3][1][1] = block[3][1][2] = block[3][0][0] = '\u2588';
            currentPos = new Pos(8,-1);
            calcExpectedPos();

        }
        BlockJ(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }

        @Override
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKJ\n";
        }
        @Override
        public String getType() {
            return "J";
        }
    }

    public class BlockI extends Block{
        BlockI(){
            super();

            block = new char[4][4][4];
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 4; j++)
                    for(int k = 0; k < 4; k++)
                        block[i][j][k] = ' ';

            block[0][0][1] = block[0][1][1] = block[0][2][1] = block[0][3][1] = '\u2588';
            block[1][3][0] = block[1][3][1] = block[1][3][2] = block[1][3][3] = '\u2588';
            block[2][0][2] = block[2][1][2] = block[2][2][2] = block[2][3][2] = '\u2588';
            block[3][1][0] = block[3][1][1] = block[3][1][2] = block[3][1][3] = '\u2588';
            currentPos = new Pos(8,-1);
            calcExpectedPos();

        }
        BlockI(int state, Pos currentPos, Pos expectedPos){
            this();
            setInitial(state, currentPos, expectedPos);
        }

        @Override
        public String toString() {
            return super.toString() + "BLOCKTYPE:BLOCKI\n";
        }
        @Override
        public String getType() {
            return "I";
        }
    }

    /**
     * this class is used to represent a randomly generated block
     */
    public class BlockRand extends Block{
        /**
         * this func is for inner use
         * it generates a random number between [begin, end]
         * @param begin the begin scope
         * @param end the end scope
         * @return a random number
         */
        private int randRange(int begin, int end){
            return (int)(Math.random() * (end - begin)) + begin;
        }
        private void printArray(char[][] array){
            System.out.println("Store");
            for(int i = 0; i < array.length; i++){
                for(int j = 0; j < array.length; j++){
                    if(array[j][i] == '\u2588')
                        System.out.print(array[j][i] + " ");
                    else
                        System.out.print("  ");
                }
                System.out.println();
            }
        }


        private Pos center = new Pos(0,0);
        private final int LEN = 5;
        private int height;
        private int len;

        private char[][] blockTemp;
        private final int tempLen = 20;
        private double[][] ThisToNextOffset = new double[4][2];

        /**
         * the random blocks are all by default stored in a matrix 5 by 5
         * and then we uses the Width-First-Search algorithm(implemented in the randomWalkCreate) to
         * create the random block
         */
        BlockRand(){
            super();

            blockTemp = new char[tempLen][tempLen];
            for(int i = 0; i < tempLen; i++)
                Arrays.fill(blockTemp[i],' ');


            block = new char[4][LEN][LEN];

            //initialize the block all into whitespace
            for(int s = 0; s < 4; s++)
                for(int i = 0; i < LEN; i++)
                    for(int j = 0; j < LEN; j++)
                        block[s][i][j] = ' ';


            randomWalkCreate();
            currentPos = new Pos(8,1-height);
        }

        BlockRand(int state, Pos curPos, Pos expPos, int len, int height, char[][] blockLayer1){
            block = new char[4][LEN][LEN];
            //initialize the block all into whitespace
            for(int s = 0; s < 4; s++)
                for(int i = 0; i < LEN; i++)
                    for(int j = 0; j < LEN; j++)
                        block[s][i][j] = ' ';
            blockTemp = new char[tempLen][tempLen];
            for(int i = 0; i < tempLen; i++)
                Arrays.fill(blockTemp[i],' ');
            //--------this initialization is superfluous, but i don't want to refactor


            this.state = state;
            this.currentPos = curPos;
            this.expectedPos = expPos;
            this.len = len;
            this.height = height;
            for(int i = 0; i < blockLayer1.length; i++){
                for(int j = 0; j < blockLayer1[0].length; j++){
                    block[0][i][j] = blockLayer1[i][j];
                }
            }

            rotateCreate();
        }

        /**
         * this function is only used when random walk to fill the block
         * if a point is '*', then this function will extend this point's surroundings
         * @param array
         * @param x
         * @param y
         */
        private void extend(boolean[][] array, int x, int y){
            if(y-1 >= 0 && array[x][y-1] == false && block[0][x][y-1] == ' ')
                array[x][y-1] = true;
            if(y+1 < height && array[x][y+1] == false && block[0][x][y+1] == ' ')
                array[x][y+1] = true;
            if(x-1 >= 0 && array[x-1][y] == false && block[0][x-1][y] == ' ')
                array[x-1][y] = true;
            if(x+1 < len && array[x+1][y] == false && block[0][x+1][y] == ' ')
                array[x+1][y] = true;
            //array[x][y] = false;
        }

        /**
         * this function is the main method to create a random block
         * it randomly walks around, test if vaild.
         */
        private void randomWalkCreate(){
            int centerX = randRange(0, LEN);
            int centerY = randRange(0, LEN);

            char[][] array = new char[LEN][LEN];
            for(int i = 0; i < LEN; i++)
                Arrays.fill(array[i],' ');

            array[centerX][centerY] = '\u2588';

            int remainBlock = randRange(1,15);

            // random Walk body
            boolean[][]canExtent = new boolean[LEN][LEN];

            for(int i = 0; i < LEN; i++)
                Arrays.fill(canExtent[i], false);

            canExtent[centerX][centerY] = true;
            extend(canExtent, centerX, centerY);

            //the random walk body
            while(remainBlock > 0){
                int newX, newY;
                do{
                    newX = randRange(0, LEN);
                    newY = randRange(0, LEN);
                }while(! canExtent[newX][newY]);
                array[newX][newY] = '\u2588';
                remainBlock--;
                extend(canExtent, newX, newY);
            }
            moveToLeftTop(array, LEN);

            for(int i = 0; i < LEN; i++){
                for(int j = 0; j < LEN; j++){
                    block[0][i][j] = array[i][j];
                }
            }
        /*
        after the random walk, count the new random block's height and width
         */
            len = height = -1;
            int lb, le, hb, he;
            hb=lb=LEN;le=he=-1;
            for(int i = 0; i < LEN; i++){
                int b = 0, e = LEN-1;
                while(b<LEN && array[i][b] == ' ') b++;
                while(e>=0  && array[i][e] == ' ') e--;
                if(b < hb) hb = b;
                if(e > he) he = e;

                b = 0; e = LEN-1;
                while(b<LEN && array[b][i] == ' ') b++;
                while(e>=0  && array[e][i] == ' ') e--;
                if(b < lb) lb = b;
                if(e > le) le = e;
            }
            len = le-lb+1; height = he-hb+1;

            //after create the block[0][][], it then rotate and create the remaining
            //3 states, and stored them also into the block[ 1 to 3 ]
            //by now I can abandon the Hard Code, but!!
            //since it is inherited from the base class, to maintain the consistency
            //i still have to do that stupid thing
            rotateCreate();
            calcExpectedPos();
        }

        /**
         * this function is use only through rotateCreate
         * see the notes in rotetaCreate to learn more
         * @param point
         */
        private void StateCreate(double[][] point){
            for(int i = 0; i < 3; i++){
                rotateCenteringPoint(point[i][0], point[0][1], i);
                ThisToNextOffset[i][0] = point[i+1][0] - point[i][0];
                ThisToNextOffset[i][1] = point[i+1][1] - point[i][1];
            }
            ThisToNextOffset[3][0] = point[0][0] - point[3][0];
            ThisToNextOffset[3][1] = point[0][1] - point[3][1];
        }

        /**
         * this function uses formula in Linear Algebra to rotate the  remaining states
         * and it then calls StateCreate to store the data and the offsets
         */
        private void rotateCreate(){
            if(len % 2 != 0 && height % 2 != 0){

                center.x = (len-1)/2;
                center.y = (height-1)/2;
                StateCreate(new double[][]{{center.x,center.y},{center.y,center.x},{center.x,center.y},{center.y,center.x}});
                debugInfo();
            }


            if(len % 2 != 0 && height % 2 == 0){
                center.x = (len-1)/2;
                center.y = (height)/2-1;
                StateCreate(new double[][]{{center.x,center.y}, {center.y,center.x},{center.x,center.y},{center.y,center.x}});
                debugInfo();
            }
            if(len % 2 == 0 && height % 2 != 0){
                center.x = (len)/2-1;
                center.y = (height-1)/2;
                StateCreate(new double[][]{{center.x,center.y}, {center.y,center.x},{center.x,center.y},{center.y,center.x}});
                debugInfo();
            }

            if(len % 2 == 0 && height % 2 == 0){
                double cx, cy;
                cx = len/2-1+0.5;
                cy = height/2-1+0.5;
                StateCreate(new double[][]{{cx,cy}, {cy,cx},{cx,cy},{cy,cx}});
                debugInfo();
            }

        }

        /**
         * this funtion is use to move the block to the most left and most top place
         * it is very convenient for the programmers to complement in the following funcs
         * based on this assumption, algorithms are simplified
         */
        private void moveToLeftTop(char[][] map,int maxBoundary){
            int up = maxBoundary+1;
            int left = maxBoundary+1;
            for(int i = 0; i < maxBoundary; i++){
                //move up
                int j = 0;
                while(j < maxBoundary && map[i][j] == ' ') j++;
                if(j < up) up = j;

                //move left
                int t = 0;
                while(t < maxBoundary && map[t][i] == ' ') t++;
                if(t < left) left = t;
            }
            //center.adjust(-left, -up);

            char[][] temp = new char[maxBoundary][maxBoundary];
            for(int i = 0; i < maxBoundary; i++){
                for(int j = 0; j < maxBoundary; j++){
                    temp[i][j] = ' ';
                }
            }
            for(int i = 0; i < maxBoundary-left; i++){
                for(int j = 0; j < maxBoundary-up; j++){
                    temp[i][j] = map[i+left][j+up];
                }
            }
            for(int i = 0; i < maxBoundary; i++){
                for(int j = 0; j < maxBoundary; j++){
                    map[i][j] = ' ';
                }
            }
            for(int i = 0; i < maxBoundary; i++){
                for(int j = 0; j < maxBoundary; j++){
                    map[i][j] = temp[i][j];
                }
            }
        }

        /**
         * all types of random block can use this function to rotate
         * literally, all types rotation can be boiled down to this ultimately -- cantering point
         * the former pos(x,y) will be shifted to pos(centerX-h+centerY, centerY + l - centerX)
         * the math can prove after shifting all points are rotated 90 degrees clock wisely
         * @param centerX the center for rotation
         * @param centerY
         * @param targetLayer the result show be stored in block[targetLayer][][]
         */
        private void rotateCenteringPoint(double centerX, double  centerY, int targetLayer){
            char[][] temp = new char[LEN*4][LEN*4];
            for(int i = 0; i < 4*LEN; i++)
                Arrays.fill(temp[i], ' ');

            for(int l = 0; l < LEN; l++){
                for(int h = 0; h < LEN; h++){
                    double addX = h - centerY; //it can be negative, but all the same
                    double addY = l - centerX;
                    if(block[targetLayer][l][h] == '\u2588'){
                        int x1 = (int)Math.round(centerX-addX+LEN);
                        int x2 = (int)Math.round(centerY+addY+LEN);

                        temp[x1][x2] = '\u2588';
                    }
                }
            }
            moveToLeftTop(temp, 4 * LEN);

            for(int x = 0; x < LEN; x++){
                for(int y = 0; y < LEN; y++)
                    block[targetLayer+1][x][y] = temp[x][y];
            }
        }


        @Override
        /**
         * this move function is override from the Base Class Block
         * since the program uses 4 arrays to record the different states
         * when rotate, there is a offset between CurrentPos
         *
         * so when the command == 'w', we handle it separately
         */
        void move(char command, boolean remove){
            if(command == 'w'){
                //System.out.println("before move the POS = " + currentPos.toString());

                Pos previous = new Pos(currentPos.x, currentPos.y);
                removePrevious(map, previous, state);
                //System.out.println("shifting " +(int)(currentPos.x-ThisToNextOffset[state][0]) + ", " + (int)(Math.round(currentPos.y-ThisToNextOffset[state][1])));

                if(valid((int)Math.round(currentPos.x-ThisToNextOffset[state][0]),
                        (int)Math.round(currentPos.y-ThisToNextOffset[state][1]),
                        (state+1)%4)){
                    //update(TetrisGame.map, previous, state);
                    //System.out.println("valid in blockRand");
                    currentPos.x = (int)Math.round(currentPos.x-ThisToNextOffset[state][0]);
                    currentPos.y = (int)Math.round(currentPos.y-ThisToNextOffset[state][1]);

                }
                super.move('w',false);

                //System.out.println("after move the POS =  " + currentPos.toString());
                //System.out.println((ThisToNextOffset[state][0]+","+
                //        ThisToNextOffset[state][1]));

            }
            else{
                super.move(command, true);
            }
            //System.out.println(currentPos);

        }
        public void debugInfo(){
        }

        @Override
        public String toString() {
            StringBuffer info = new StringBuffer();
            info.append("BLOCKTYPE:RANDOM\n");
            info.append("HEIGHT:"+height);
            info.append("\nLEN:" + len);
            info.append("\n");

            for(int j = 0; j < block[0].length; j++){
                for(int k = 0; k < block[0][j].length; k++){
                    info.append(block[0][j][k]);
                }
                info.append("\n");
            }
            return super.toString() + info.toString();
        }
        @Override
        public String getType() {
            return "R";
        }
    }

    /**
     * this function initialize the map, draw its boarder and its inner space
     * randomly created the first Block and the next Block
     */
    public void initialize(){
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
     * this function generated a block for the game
     * if the game mode == 1, it randomly returns one of the seven basic types of blocks
     * if the game mode == 2, it returns BlockRand()
     * @return a block type according the game mode
     */
    public Block randomGenerateBlock(){
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
        if(gameMode==0) return newBlock;
        return new BlockRand();
    }

    /**
     * if the current block the bottom, then this function is called
     * it settles the current block, then generate new blocks
     */
    public void settle(){
        currentBlock = nextBlock;
        currentBlock.calcExpectedPos();
        nextBlock = randomGenerateBlock();
    }

    /**
     * this function is used to detect whether there is a full line can
     * be eliminated in the game map
     * if it has, then eliminate the line and update the map and the score
     */
    public void eliminate(){
        int scoreP = score;
        currentBlock.removePrevious(map,currentBlock.currentPos,currentBlock.state);
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
        currentBlock.update(map);

    }





    /**
     * this function is used to save the current state data into save.txt
     * it is called automatically after each update of the movements
     * and run in may computer, it is quite smooth and doesn't slow the game down
     */
    public  void save(File saveFile){
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
    private int analyzeSave(String source, String spliter, int index){
        return Integer.parseInt(source.split(spliter)[index]);
    }


    /**
     * this function is for inner use
     * it read the block information from the save file
     * @param saveReader the scanner
     * @return a block()
     */
    private  Block readBlock(Scanner saveReader){
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
     * @return true if previous game is not over
     */
    public  boolean beginFromPrevious(){

        try{
            Scanner saveReader = new Scanner(save);
            String firstLine = saveReader.nextLine();
            if(firstLine.charAt(0) == 'G'){
                return false;
            }
            //1 read the score first
            score = analyzeSave(firstLine, ":", 1);
            //2 read the mode
            gameMode =analyzeSave(saveReader.nextLine(), ":", 1)-1;


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

    public boolean isGameOver() {
        if(currentBlock.expectedPos.y <= 0) gameOver=true;
        return gameOver;
    }

}

/**
 * This class is implemented to store the rank list information
 * each rank consists of two data fields
 * 1. name : the user's name
 * 2. score: the score achieved while playing the game
 */

class RankInfo{

    public RankInfo(String filePath){
        rankFile = new File(filePath);
    }

    public ArrayList<Rank> rankList = new ArrayList<Rank>();
    private File rankFile;

    public void addNewRanker(Rank newRanker){
        rankList.add(newRanker);
        rankList.sort(new SortRank());
        updateRankList();
    }

    /**
     * this function is used to read the ranking information
     * from the Rank.txt, and store them into the rankList
     * it is called before the main entrance.
     */
    public void readRankList()
    {
        /*
        read the ranklist information
         */
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
            System.out.println("ah-oh, we cannot find the rank.txt");
            System.out.println("Please assure the file exist. and make system path consistent");
            System.exit(1);
        }

        rankList.sort(new SortRank());
    }


    /**
     * this function is used to write the current ranking
     * namely, the most updated ranking info back into the file
     */
    public void updateRankList(){
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
     * this function is used to display the rank list
     * notice: it must be called after read the rank file
     * otherwise it'll throw a exception
     */
    public void showRankList(){
        int num = 1;
        for(Rank i: rankList){
            System.out.print("|RANK "+ num++ + "| ");
            System.out.println(i);
        }
    }



class Rank implements Comparable<Rank>{

    private int score;
    private String name;


    public Rank(String name, int score){
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return this.name + " " + this.score;
    }

    @Override
    /**
     * when compare two Rank, actually compares their scores
     */
    public int compareTo(Rank o) {
        if(this.score < o.score)
            return -1;
        if(this.score == o.score)
            return 0;
        else
            return 1;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }
}


    /**
     * this sort class is used when calling the function of
     * Standard Sort and pass as a argument
     */
    class SortRank implements Comparator<Rank> {
        @Override
        public int compare(Rank o1, Rank o2) {
            return -o1.compareTo(o2);
        }
    }
}

