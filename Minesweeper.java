import java.util.*;

public class Minesweeper {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the Difficulty Level" +
                "\nPress 0 for BEGINNERS (9 * 9 Cells and 10 Mines)" +
                "\nPress 1 for INTERMEDIATE (16 * 16 Cells and 40 Mines)" +
                "\nPress 2 for ADVANCED (24 * 24 Cells and 99 Mines)");

        int difficultyLevel = Integer.parseInt(scanner.nextLine());

        int mines = 0;
        switch (difficultyLevel) {
            case 0:
                difficultyLevel = 9;
                mines = 10;
                break;
            case 1:
                difficultyLevel = 16;
                mines = 40;
                break;
            case 2:
                difficultyLevel = 24;
                mines = 99;
                break;
            default:
                break;
        }

        String[][] hiddenMatrix = new String[difficultyLevel][difficultyLevel];
        String[][] revealedMatrix = new String[difficultyLevel][difficultyLevel];
        ArrayDeque<Integer> minesPositions = new ArrayDeque<>();
        fillRevealedMatrixWithDashes(revealedMatrix);
        int numberOfNonMinesCells = hiddenMatrix.length * hiddenMatrix.length - mines;
        System.out.print("Enter your move, (row, column)\n-> ");
        int[] inputCoordinates = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        int x = inputCoordinates[0];
        int y = inputCoordinates[1];

        Random random = new Random();

        while (mines != 0) {
            int a = random.nextInt(difficultyLevel - 1);
            int b = random.nextInt(difficultyLevel - 1);

            if (!"*".equals(hiddenMatrix[a][b]) && a != x && b != y) {
                hiddenMatrix[a][b] = "*";
                minesPositions.offer(a);
                minesPositions.offer(b);
                mines--;
            }
        } // Random filling with mines
        int numOfBombs = 0;
        for (int row = 0; row < hiddenMatrix.length; row++) {
            for (int col = 0; col < hiddenMatrix.length; col++) {
                if (coordinateInBounds(hiddenMatrix.length, row - 1, col - 1) && "*".equals(hiddenMatrix[row - 1][col - 1]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row - 1, col) && "*".equals(hiddenMatrix[row - 1][col]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row - 1, col + 1) && "*".equals(hiddenMatrix[row - 1][col + 1]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row, col - 1) && "*".equals(hiddenMatrix[row][col - 1]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row, col + 1) && "*".equals(hiddenMatrix[row][col + 1]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row + 1, col - 1) && "*".equals(hiddenMatrix[row + 1][col - 1]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row + 1, col) && "*".equals(hiddenMatrix[row + 1][col]))
                    numOfBombs++;
                if (coordinateInBounds(hiddenMatrix.length, row + 1, col + 1) && "*".equals(hiddenMatrix[row + 1][col + 1]))
                    numOfBombs++;

                if (numOfBombs != 0) {
                    if (!"*".equals(hiddenMatrix[row][col]))
                        hiddenMatrix[row][col] = "" + numOfBombs;
                    numOfBombs = 0;

                } else {
                    if (!"*".equals(hiddenMatrix[row][col]))
                        hiddenMatrix[row][col] = " ";
                }
            }
        } // Filling the matrix with numbers

        boolean showStopper = false;
        while (!showStopper) {
            switch (hiddenMatrix[x][y]) {
                case " ":
                    revealedEmptySpaces(hiddenMatrix, revealedMatrix, x, y);
                    break;
                case "*":
                    revealedMatrix[x][y] = "X";
                    fillRevealedMatrixWithMines(revealedMatrix, minesPositions, x, y);
                    printMatrix(revealedMatrix);
                    System.out.println("You lose!");
                    return;
                case "X":
                    break;
                default:
                    if (!hiddenMatrix[x][y].equals(revealedMatrix[x][y]))
                        revealedMatrix[x][y] = hiddenMatrix[x][y];
                    break;
            }

            printMatrix(revealedMatrix);
            if (numberOfNonMinesCells != safeCells(revealedMatrix)){
                System.out.print("Enter your move, (row, column)\n-> ");
                inputCoordinates = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                x = inputCoordinates[0];
                y = inputCoordinates[1];
            }
            else
                showStopper = true;
        }
        printMatrix(revealedMatrix);
        System.out.println("You win!");
    }

    public static void fillRevealedMatrixWithDashes(String[][] matrixRevealed) {
        for (int row = 0; row < matrixRevealed.length; row++) {
            for (int col = 0; col < matrixRevealed.length; col++) {
                matrixRevealed[row][col] = "-";
            }
        }
    }

    public static void fillRevealedMatrixWithMines(String[][] matrixReviled, ArrayDeque<Integer> minesPositions, int x, int y) {
        while (!minesPositions.isEmpty()) {
            int a = minesPositions.poll();
            int b = minesPositions.poll();

            if (x != a && y != b)
                matrixReviled[a][b] = "*";
        }
    }

    public static boolean coordinateInBounds(int matrixLength, int row, int col) {
        return row < matrixLength && row >= 0 && col >= 0 && col < matrixLength;
    }

    public static void printMatrix(String[][] matrix) {
        System.out.print("Current Status of Board :\n     ");
        for (int row = 0; row < matrix.length; row++) {
            System.out.printf("%-3d",row);
        }
        System.out.println();
        for (int row = 0; row < matrix.length; row++) {
            System.out.printf("%-3d",row);
            for (int col = 0; col < matrix.length; col++) {
                System.out.print("  "+matrix[row][col]);
            }
            System.out.println();
        }
    }

    public static void revealedEmptySpaces(String[][] matrix, String[][] matrixReviled, int a, int b) {

        ArrayDeque<Integer> onA = new ArrayDeque<>();
        ArrayDeque<Integer> onB = new ArrayDeque<>();
        matrixReviled[a][b] = " ";
        matrix[a][b] = "X";
        onA.offer(a);
        onB.offer(b);

        while (!onA.isEmpty()) {
            a = onA.poll();
            b = onB.poll();

            if (coordinateInBounds(matrix.length, a - 1, b - 1))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a - 1, b - 1);
            if (coordinateInBounds(matrix.length, a - 1, b))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a - 1, b);
            if (coordinateInBounds(matrix.length, a - 1, b + 1))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a - 1, b + 1);
            if (coordinateInBounds(matrix.length, a, b - 1))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a, b - 1);
            if (coordinateInBounds(matrix.length, a, b + 1))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a, b + 1);
            if (coordinateInBounds(matrix.length, a + 1, b - 1))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a + 1, b - 1);
            if (coordinateInBounds(matrix.length, a + 1, b))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a + 1, b);
            if (coordinateInBounds(matrix.length, a + 1, b + 1))
                fillEmptySpaces(matrix, onA, onB, matrixReviled, a + 1, b + 1);

        }
    }

    public static void fillEmptySpaces(String[][] matrix, ArrayDeque<Integer> onA, ArrayDeque<Integer> onB, String[][] matrixReviled, int a, int b) {
        if (" ".equals(matrix[a][b])) {
            matrixReviled[a][b] = " ";
            matrix[a][b] = "X";
            onA.offer(a);
            onB.offer(b);

        } else if (!matrix[a][b].equals("X")) {
            matrixReviled[a][b] = matrix[a][b];

        }
    }

    public static int safeCells(String[][] matrix){
        int counter = 0;
        for (String[] strings : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                if (!strings[col].equals("-"))
                    counter++;
            }
        }
        return counter;
    }

}
