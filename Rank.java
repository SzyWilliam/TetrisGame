import java.util.Comparator;

/**
 * This class is implemented to store the rank list information
 * each rank consists of two data fields
 * 1. name : the user's name
 * 2. score: the score achieved while playing the game
 */
public class Rank implements Comparable<Rank>{
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
}

/**
 * this sort class is used when calling the function of
 * Standard Sort and pass as a argument
 */
class SortRank implements Comparator<Rank>{
    @Override
    public int compare(Rank o1, Rank o2) {
        return -o1.compareTo(o2);
    }
}

/**
 * this class contains two different properties
 * line eliminator and block transfer
 * it is randomly awarded after each successful elimination
 */
class Propoty{
    private int numberOfLineEliminater = 1;
    private int numberOfBlockTransfer = 1;

    /**
     * after elimination, this function is automatically called
     * to award the player using this random algorithm
     * @param num the line successfully eliminated
     */
    public void update(int num){
        if(num * Math.random() > 1.2){
            System.out.println("Congratulations: you get a Line Eliminator");
            numberOfLineEliminater++;
            System.out.println(this);
        }
        else if(num * Math.random() > 1 ){
            System.out.println("Congratulations: you get a block Transfer");
            numberOfBlockTransfer++;
            System.out.println(this);
        }

    }

    /**
     * to display to the user how many properties are available
     * @return
     */
    @Override
    public String toString() {
        return "\n\neliminator:"+numberOfLineEliminater + " |transfer : "+ numberOfBlockTransfer;
    }

    /**
     * use a line eliminator to eliminate a line
     * @param lineNum the specified line waiting to be eliminated
     */
    public void eliminateALine(int lineNum){
        if(numberOfLineEliminater == 0){
            System.out.println("\nSorry, you have no eliminators right now\n");
            return;
        }
        for(int i = 1; i < TetrisGame.map.length-1; i++){
            TetrisGame.map[i][lineNum] = '\u2588';
        }
        numberOfLineEliminater--;
    }


    /**
     * use a block transfer to change the nextBlock's type
     */
    public Block ChangeNextBlock() throws IndexOutOfBoundsException{
        if(numberOfBlockTransfer == 0){
            System.out.println("\nSorry, you have no block transfers right now\n");
            throw new IndexOutOfBoundsException("Block");
        }
        numberOfBlockTransfer--;
        return TetrisGame.randomGenerateBlock();

    }
}

