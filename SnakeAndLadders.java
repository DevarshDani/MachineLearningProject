package learn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SnakeAndLadders {
    static char[][] snakeNladders = new char[6][6];
    static HashMap<Integer, Position> numberToPositionMap = new HashMap<Integer, Position>();
    static HashMap<Integer, Integer> snakesMap = new HashMap<Integer, Integer>();
    static HashMap<Integer, Integer> laddersMap = new HashMap<Integer, Integer>();
    static double[][][] qTable = new double[6][6][8];
    static double[][][] qTable1 = new double[6][6][8];
    static int[][] rewards = new int[6][6];
    static int[][] board = new int[6][6];
    static double leaningRate = 0.3;
    static double discountRate = 0.3;
    static Random rand = new Random();
    static int xPosition = 5;
    static int yPosition = 0;
    static int numberOfStepsToReachFinalState;
    static List<Integer> movesAlongTheBoard = new ArrayList<Integer>();
    public void initializeWorld(){
        setRewards();
        setNumbersALongBoard();
        setLaddersAndSnakes();
        rand.setSeed(115);
    }

    private void addSnake(int number, int reward){
        snakesMap.put(number, number+reward);
        Position pos = numberToPositionMap.get(number);
        rewards[pos.Xcoordinate][pos.Ycoordinate] = reward;
    }

    private void setLaddersAndSnakes(){
        laddersMap.put(2, 15); // key < value  for ladders
        laddersMap.put(5, 7);
        laddersMap.put(9, 27);
        laddersMap.put(25, 35);
        laddersMap.put(18, 29);

        snakesMap.put(17, 4); // key > value  for snakes
        snakesMap.put(20, 6);
        snakesMap.put(24, 16);
        snakesMap.put(34, 12);
        snakesMap.put(32, 30);

    }

    public void setRewards(){
        for (int i = 0; i < rewards.length; i++) {
            for (int j = 0; j < rewards.length; j++) {

                rewards[i][j] = -1; // reward of -1 indicates that there is no snake and ladder at that point.
            }
        }
        rewards[5][1] = 13; // positive rewards indicate a ladder
        rewards[5][4] = 2;
        rewards[4][3] = 18;
        rewards[1][0] = 10;
        rewards[3][5] = 11;

        rewards[3][4] = -13; // negative rewards indicate a snake
        rewards[2][4] = -14;
        rewards[2][0] = -8;
        rewards[0][2] = -22;
        rewards[0][4] = -2;
    }

    public int getReward(int xpos, int yPos){
        return rewards[xpos][yPos];
    }

    public void setNumbersALongBoard(){
        for (int i = 0; i < 6; i++){
            int maxNumberOfRow = 6*(6-i);
            for (int j = 0; j < 6; j++){
                if (i %2 == 0){
                    Position pos = new Position(i, j);
                    numberToPositionMap.put(maxNumberOfRow, pos);
                    board[i][j] = maxNumberOfRow--;
                }
                else{
                    Position pos = new Position(i, 5-j);
                    numberToPositionMap.put(maxNumberOfRow, pos);
                    board[i][5-j] = maxNumberOfRow--;
                }
            }
        }
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 6; j++){
                //System.out.print(board[i][j]+" ");
            }
        //System.out.println(" ");
             }

        //for (int i = 1; i <= 36; i++)
            //System.out.println(numberToPositionMap.get(i).Xcoordinate+" "+numberToPositionMap.get(i).Ycoordinate);
    }

    public void makeMove(int diceRoll){
        int currentNumber = board[xPosition][yPosition];
        int numberAfterRollingDice = currentNumber+diceRoll;
        if (numberAfterRollingDice <= 36){
        Position position = numberToPositionMap.get(numberAfterRollingDice);
        //System.out.println("current number = "+currentNumber+" number after rolling dice = "+numberAfterRollingDice);
        xPosition = position.Xcoordinate;
        yPosition = position.Ycoordinate;
        }
    }

    public Position getNextState(int diceRoll){
        //System.out.println("X = "+xPosition+" Y = "+yPosition);
        int currentNumber = board[xPosition][yPosition];
        //System.out.println("Current number = "+currentNumber);
        int numberAfterRollingDice = currentNumber+diceRoll;
        //System.out.println("Number after rolling dice = "+numberAfterRollingDice);
        if (numberAfterRollingDice <= 36){
        Position position = numberToPositionMap.get(numberAfterRollingDice);
        //System.out.println("Position = "+position);
        return position;}
        return new Position(xPosition, yPosition);
    }

    public int getNextStateForGreedy(){
        double maxValue = Integer.MIN_VALUE;
        double secondMax = Integer.MIN_VALUE;
        int diceRoll = 0;
        int secondDiceRoll = 0;
        for (int i = 0; i < 6; i++){
            if (qTable[xPosition][yPosition][i] >= maxValue){
                secondMax = maxValue;
                secondDiceRoll = diceRoll;
            maxValue = qTable[xPosition][yPosition][i];
            diceRoll = i;
            }
        }

        if (secondMax == maxValue){
            int index = rand.nextInt(2);
            if (index == 0){
                diceRoll = secondDiceRoll;
            }
        }

        int currentNumber = board[xPosition][yPosition];
        int numberAfterRollingDice = currentNumber+diceRoll+1;
        if (numberAfterRollingDice <= 36){
            return diceRoll;
        }
        return 36-currentNumber-1; // return -1 if your move is exceeding the board
    }

    public int  getImprovedNextStateForGreedy() {
        double maxValue = Integer.MIN_VALUE;
        double secondMax = Integer.MIN_VALUE;
        int diceRoll = 0;
        int secondDiceRoll = 0;
        for (int i = 0; i < 6; i++){
            if (qTable1[xPosition][yPosition][i] >= maxValue){
                secondMax = maxValue;
                secondDiceRoll = diceRoll;
                maxValue = qTable1[xPosition][yPosition][i];
                diceRoll = i;
            }
        }

        if (secondMax == maxValue){
            int index = rand.nextInt(2);
            if (index == 0){
                diceRoll = secondDiceRoll;
            }
        }

        int currentNumber = board[xPosition][yPosition];
        int numberAfterRollingDice = currentNumber+diceRoll+1;
        if (numberAfterRollingDice <= 36){
            return diceRoll;
        }
        return 36-currentNumber-1; // return -1 if your move is exceeding the board

    }


    public int getImprovedReward(int xPos, int yPos){
        int number = board[xPos][yPos];
        int reward = 0;
        for (int i = 1; i <= 4; i++){
            number++;
            if (number <= 36){
                Position posi = numberToPositionMap.get(number);
                int ir= rewards[posi.Xcoordinate][posi.Ycoordinate];
                if (ir > 0)
                    reward += ir*0.25;
                else if (ir == -1)
                    reward += ir*0.15;
                else
                    reward += ir*0.6;
            }

        }
        return reward;
    }

    public Boolean isFinalState(){
        if (xPosition == 0 && yPosition == 0)
            return true;
        else
            return false;
    }

    public void moveRandomlyAndLearnTheEnvironmentWithModifiedAlgorithm(int numOfIterations){
        for (int i = 0; i < numOfIterations; i++){
            double maxQvalue = 0;
            int reward = getImprovedReward(xPosition, yPosition);
            int numberAtCurrentPosition = board[xPosition][yPosition];
            if (isFinalState()){
                System.out.println("Total number of steps to reach final state = "+numberOfStepsToReachFinalState);
                restartTheGame();
            }
            else if (laddersMap.containsKey(numberAtCurrentPosition)){
                qTable[xPosition][yPosition][6] = (1-leaningRate)*qTable[xPosition][yPosition][6] + leaningRate*(reward + discountRate*maxQvalue);
                climbLadder(numberAtCurrentPosition);
            }
            else if (snakesMap.containsKey(numberAtCurrentPosition)){
                qTable[xPosition][yPosition][7] = (1-leaningRate)*qTable[xPosition][yPosition][7] + leaningRate*(reward + discountRate*maxQvalue);
                followSnake(numberAtCurrentPosition);
            }
            else {
                int diceRoll = rand.nextInt(6);
                Position position = getNextState(diceRoll);
                //System.out.println("Position = "+position);
                maxQvalue = getDiscountedReward(position);
                qTable[xPosition][yPosition][diceRoll] = (1 - leaningRate) * qTable[xPosition][yPosition][diceRoll] + leaningRate * (reward + discountRate * maxQvalue);
                makeMove(diceRoll+1);
                numberOfStepsToReachFinalState++;
            }
        }
    }

    public void moveRandomlyAndLearnTheEnvironment(int numOfIterations){
        for (int i = 0; i < numOfIterations; i++){
            double maxQvalue = 0;
            int reward = 0;
            int numberAtCurrentPosition = board[xPosition][yPosition];
            if (isFinalState()){
                System.out.println("Total number of steps to reach final state = "+numberOfStepsToReachFinalState);
                restartTheGame();
            }
            else if (laddersMap.containsKey(numberAtCurrentPosition)){
                int destination = laddersMap.get(numberAtCurrentPosition);
                Position pos = numberToPositionMap.get(destination);
                reward = rewards[pos.Xcoordinate][pos.Ycoordinate];
                qTable1[xPosition][yPosition][6] = (1-leaningRate)*qTable1[xPosition][yPosition][6] + leaningRate*(reward + discountRate*maxQvalue);
                climbLadder(numberAtCurrentPosition);
            }
            else if (snakesMap.containsKey(numberAtCurrentPosition)){
                int destination = snakesMap.get(numberAtCurrentPosition);
                Position pos = numberToPositionMap.get(destination);
                reward = rewards[pos.Xcoordinate][pos.Ycoordinate];
                qTable1[xPosition][yPosition][7] = (1-leaningRate)*qTable1[xPosition][yPosition][7] + leaningRate*(reward + discountRate*maxQvalue);
                followSnake(numberAtCurrentPosition);
            }
            else {
                int diceRoll = rand.nextInt(6);
                Position position = getNextState(diceRoll);
                //System.out.println("Position = "+position);
                reward = rewards[position.Xcoordinate][position.Ycoordinate];
                maxQvalue = getDiscountedReward(position);
                qTable1[xPosition][yPosition][diceRoll] = (1 - leaningRate) * qTable1[xPosition][yPosition][diceRoll] + leaningRate * (reward + discountRate * maxQvalue);
                makeMove(diceRoll+1);
                numberOfStepsToReachFinalState++;
            }
        }
    }

    private static double getDiscountedReward(Position position) {
        double maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < 6; i++){
            maxValue = Math.max(maxValue, qTable[position.Xcoordinate][position.Ycoordinate][i]);
        }
        return maxValue;
    }


    private void followSnake(int numberAtCurrentPosition) {
        int destination = snakesMap.get(numberAtCurrentPosition);
        Position pos = numberToPositionMap.get(destination);
        xPosition = pos.Xcoordinate;
        yPosition = pos.Ycoordinate;
    }

    public void climbLadder(int numberAtCurrentPosition) {
        int destination = laddersMap.get(numberAtCurrentPosition);
        Position pos = numberToPositionMap.get(destination);
        xPosition = pos.Xcoordinate;
        yPosition = pos.Ycoordinate;
    }

    private void restartTheGame() {
        xPosition = 5;
        yPosition = 0;
        numberOfStepsToReachFinalState = 0;
        movesAlongTheBoard.clear();
    }

    public void moveGreedilyAndLearnTheEnvironmentWithModifiedAlgorithm(int numOfIterations) throws IOException{
        for (int i = 0; i < numOfIterations; i++){
            double maxQvalue = 0;
            int reward = getImprovedReward(xPosition, yPosition);
            int numberAtCurrentPosition = board[xPosition][yPosition];
            if (isFinalState()){
                //System.out.println("1) "+"X position = "+xPosition+" Y position = "+yPosition);
                System.out.println("Total number of steps to reach final state = "+numberOfStepsToReachFinalState);
                //for (int j = 0; j < movesAlongTheBoard.size(); j++){
                    //System.out.print(movesAlongTheBoard.get(j)+" -> ");
                //}
                restartTheGame();
            }
            else if (laddersMap.containsKey(numberAtCurrentPosition)){
                //System.out.println("2) "+"X position = "+xPosition+" Y position = "+yPosition+" Number = "+numberAtCurrentPosition+" Reward = "+reward);
                qTable[xPosition][yPosition][6] = (1-leaningRate)*qTable[xPosition][yPosition][6] + leaningRate*(reward + discountRate*maxQvalue);
                climbLadder(numberAtCurrentPosition);
            }
            else if (snakesMap.containsKey(numberAtCurrentPosition)){
                //System.out.println("3) "+"X position = "+xPosition+" Y position = "+yPosition);
                qTable[xPosition][yPosition][7] = (1-leaningRate)*qTable[xPosition][yPosition][7] + leaningRate*(reward + discountRate*maxQvalue);
                followSnake(numberAtCurrentPosition);
            }
            else {
                int bestDiceRoll = getNextStateForGreedy();
                //System.out.println("4) "+"X position = "+xPosition+" Y position = "+yPosition);
                System.out.println("Best dice roll = "+(bestDiceRoll+1));
                int currentStateNumber = board[xPosition][yPosition];
                Position nextState = numberToPositionMap.get(bestDiceRoll+currentStateNumber+1);
                maxQvalue = getDiscountedReward(nextState);
                qTable[xPosition][yPosition][bestDiceRoll] = (1 - leaningRate) * qTable[xPosition][yPosition][bestDiceRoll] + leaningRate * (reward + discountRate * maxQvalue);
                makeMove(bestDiceRoll+1);
                numberOfStepsToReachFinalState++;

                movesAlongTheBoard.add(currentStateNumber+bestDiceRoll+1);
            }
        }
        writeQTableToFile("QTableModifiedAlgorithm.csv", 1);
    }

    public void moveGreedilyAndLearnTheEnvironment(int numOfIterations) throws IOException{
        for (int i = 0; i < numOfIterations; i++){
            double maxQvalue = 0;
            int reward = 0;
            int numberAtCurrentPosition = board[xPosition][yPosition];
            if (isFinalState()){
                //System.out.println("1) "+"X position = "+xPosition+" Y position = "+yPosition);
                System.out.println("Total number of steps to reach final state = "+numberOfStepsToReachFinalState);
                //for (int j = 0; j < movesAlongTheBoard.size(); j++){
                //System.out.print(movesAlongTheBoard.get(j)+" -> ");
                //}
                restartTheGame();
            }
            else if (laddersMap.containsKey(numberAtCurrentPosition)){
                int destination = laddersMap.get(numberAtCurrentPosition);
                Position pos = numberToPositionMap.get(destination);
                reward = rewards[pos.Xcoordinate][pos.Ycoordinate];
                //System.out.println("2) "+"X position = "+xPosition+" Y position = "+yPosition+" Number = "+numberAtCurrentPosition+" Reward = "+reward);
                qTable1[xPosition][yPosition][6] = (1-leaningRate)*qTable1[xPosition][yPosition][6] + leaningRate*(reward + discountRate*maxQvalue);
                climbLadder(numberAtCurrentPosition);
            }
            else if (snakesMap.containsKey(numberAtCurrentPosition)){
                int destination = snakesMap.get(numberAtCurrentPosition);
                Position pos = numberToPositionMap.get(destination);
                reward = rewards[pos.Xcoordinate][pos.Ycoordinate];
                //System.out.println("3) "+"X position = "+xPosition+" Y position = "+yPosition);
                qTable1[xPosition][yPosition][7] = (1-leaningRate)*qTable1[xPosition][yPosition][7] + leaningRate*(reward + discountRate*maxQvalue);
                followSnake(numberAtCurrentPosition);
            }
            else {
                int bestDiceRoll = getImprovedNextStateForGreedy();
                //System.out.println("4) "+"X position = "+xPosition+" Y position = "+yPosition);
                System.out.println("Best dice roll = "+(bestDiceRoll+1));
                int currentStateNumber = board[xPosition][yPosition];
                Position nextState = numberToPositionMap.get(bestDiceRoll+currentStateNumber+1);

                reward = rewards[nextState.Xcoordinate][nextState.Ycoordinate];

                maxQvalue = getDiscountedReward(nextState);
                qTable1[xPosition][yPosition][bestDiceRoll] = (1 - leaningRate) * qTable1[xPosition][yPosition][bestDiceRoll] + leaningRate * (reward + discountRate * maxQvalue);
                makeMove(bestDiceRoll+1);
                numberOfStepsToReachFinalState++;

                movesAlongTheBoard.add(currentStateNumber+bestDiceRoll+1);
            }
        }
        writeQTableToFile("QTableExistingAlgorithm.csv", 2);
    }

    private void writeQTableToFile(String fileName, int algorithmNumber) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(fileName));
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 6; j++){
                for (int k = 0; k < 8; k++){
                    if (algorithmNumber == 1)
                        fileWriter.write(Double.toString(qTable[i][j][k]));
                    else
                        fileWriter.write(Double.toString(qTable1[i][j][k]));
                        fileWriter.write(",");
                    }
                    fileWriter.write("\n");
                }
        }
        fileWriter.flush();
    }

    public void learnSnakeAndLadders() throws IOException{

        System.out.println("EXISTING ALGORITHM");
        initializeWorld();
        System.out.println("Move Agent Randomly With Existing Algorithm");
        moveRandomlyAndLearnTheEnvironment(100);
        System.out.println("############################################");
        System.out.println("Move Agent Greedily with Existing Algorithm");
        restartTheGame();
        moveGreedilyAndLearnTheEnvironment(200);


        System.out.println("********************************************************************************************************");

        System.out.println("MODIFIED ALGORITHM");
        initializeWorld();
        System.out.println("Move Agent Randomly With Modified Algorithm");
        moveRandomlyAndLearnTheEnvironmentWithModifiedAlgorithm(100);
        System.out.println("###############################################");
        System.out.println("Move Agent Greedily With Modified Algorithm");
        restartTheGame();
        moveGreedilyAndLearnTheEnvironmentWithModifiedAlgorithm(200);

    }

    public static void main(String args[]){
        try {
            SnakeAndLadders sl = new SnakeAndLadders();
            sl.learnSnakeAndLadders();
        }
        catch (Exception e){

        }
    }

}
