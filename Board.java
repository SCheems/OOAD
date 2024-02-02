import java.io.*;
import java.nio.file.*;
import java.util.*;
// -1 < posX < 7, -1 < posY < 6

/**
 * Part in MVC design pattern: Model, Controller
 * Controller's design pattern: Adapter
 * Part in Adapter design pattern: Service
 * 
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Abstraction
 * 
 * Purpose: allow Controller class in MVC design pattern to manipulate the
 * pieces in
 * the game and get info of the game such as player and winner
 */
public class Board {
    private LinkedList<Piece> board = new LinkedList<Piece>();
    private Piece selectedPiece = null; // pointer
    private int player = 1;
    private int playerInCheck = 0;
    private int winner = 0;
    private int turn = 0;
    private int sumTurn = 0;

    /**
     * setup the board
     * 
     * @author Lim Jun Jie
     */
    Board() {
        setBoard();
    }

    public LinkedList<Piece> getBoard() {
        return board;
    }

    public int getSumTurn() {
        return sumTurn;
    }

    /**
     * setup/reset the board
     * 
     * @author Lim Jun Jie
     * @author Lau Jun Xing
     */
    public void setBoard() {
        board.clear();
        selectedPiece = null; // pointer
        player = 1;
        playerInCheck = 0;
        winner = 0;
        turn = 0;
        sumTurn = 0;

        // set Point piece for p1 and p2
        for (int i = 0; i < 7; i++) {
            board.add(new Point(1, i, 1, "up"));
            board.add(new Point(2, i, 4, "down"));
        }

        // set Plus piece for p1 and p2
        board.add(new Plus(1, 0, 0));
        board.add(new Plus(2, 0, 5));
        board.add(new Plus(1, 6, 0));
        board.add(new Plus(2, 6, 5));

        // set Hourglass piece for p1 and p2
        board.add(new Hourglass(1, 1, 0));
        board.add(new Hourglass(2, 1, 5));
        board.add(new Hourglass(1, 5, 0));
        board.add(new Hourglass(2, 5, 5));

        // set Time piece for p1 and p2
        board.add(new Time(1, 2, 0));
        board.add(new Time(2, 2, 5));
        board.add(new Time(1, 4, 0));
        board.add(new Time(2, 4, 5));

        // set Sun for p1 and p2
        board.add(new Sun(1, 3, 0));
        board.add(new Sun(2, 3, 5));
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    /**
     * set selected piece on board using x and y position
     * 
     * @author Lim Jun Jie
     */
    public void setSelectedPiece(int posX, int posY) {
        for (Piece p : board) {
            if (p.getX() == posX && p.getY() == posY) {
                selectedPiece = p;
                break;
            }
        }
    }

    /**
     * get piece on board based on x and y position
     * if piece not on board, return null
     * 
     * @author Lim Jun Jie
     */
    public Piece getPiece(int posX, int posY) {
        for (Piece p : board) {
            if (p.getX() == posX && p.getY() == posY) {
                return p;
            }
        }
        return null;
    }

    /**
     * move selected piece piece on board based on x and y position.
     * piece in the x and y position is replaced with
     * the selected piece
     * 
     * @author Lim Jun Jie
     * @author Lau Jun Xing
     */
    public void movePiece(int posX, int posY) {
        List<Integer[]> validMoves = getValidMove();

        for (Integer[] m : validMoves) { // move piece if valid move, piece selected and sun not in check
            if (posX == m[0] && posY == m[1] && selectedPiece != null && winner == 0) {

                // check if current player in check
                if (playerInCheck == player) {
                    refreshPlayerInCheck(posX, posY);

                    if (playerInCheck == player) { // if current player still in check, break loop
                        break;
                    }
                }

                Piece attackedPiece = null;
                attackedPiece = getPiece(posX, posY);
                if (attackedPiece != null) { // remove piece if got attacked by selectedPiece
                    board.remove(attackedPiece);
                }

                selectedPiece.setX(posX);
                selectedPiece.setY(posY);

                if (selectedPiece.getName().equals("Point")) {
                    // if move point and point at last square of board, set point direction
                    if (selectedPiece.getY() == 5) {
                        selectedPiece.setDirection("down");
                    } else if (selectedPiece.getY() == 0) {
                        selectedPiece.setDirection("up");
                    }
                    selectedPiece.getLegalMove(this); // check if point check sun after turn around
                }

                // flip board after each player move 2 times
                turn++;
                sumTurn++;

                if (turn == 4) {
                    TimePlusSwitch();
                    turn = 0;
                }

                // check for win. If game not end, switch to next player and flip board
                checkWin();

                if (winner == 0) {
                    flipBoard();
                    togglePlayer();
                }
                break;
            }
        }
        selectedPiece = null;
    }

    public int getPlayer() {
        return player;
    }

    /**
     * toggles betwwen p1 and p2
     * 
     * @author Lim Jun Jie
     */
    public void togglePlayer() {
        if (player == 1) {
            player += 1;
        } else {
            player -= 1;
        }
    }

    public int getWinner() {
        return winner;
    }

    /**
     * get valid move of selected piece
     * inner arr(element) of moves format:
     * [0] = posX
     * [1] = posY
     * 
     * @author Lim Jun Jie
     * @author Lau Jun Xing
     */
    public List<Integer[]> getValidMove() {
        List<Integer[]> moves = new LinkedList<Integer[]>();
        if (selectedPiece != null) {
            if (selectedPiece.getPlayer() == player) {
                moves = selectedPiece.getLegalMove(this);
            }
        }

        return moves;
    }

    public int getPlayerInCheck() {
        return playerInCheck;
    }

    public void setPlayerInCheck(int playerInCheck) {
        this.playerInCheck = playerInCheck;
    }

    /**
     * refresh playerInCheck
     * if current player still in check, playerInCheck = current player
     * else, playerInCheck = 0
     * 
     * @author Lim Jun Jie
     */

    private void refreshPlayerInCheck(int newPosX, int newPosY) {
        int x = selectedPiece.getX();
        int y = selectedPiece.getY();
        selectedPiece.setX(newPosX);
        selectedPiece.setY(newPosY);

        List<Piece> copyBoard = new LinkedList<Piece>(board);
        if (turn + 1 == 4) {
            TimePlusSwitch();
        }

        Piece attackedPiece = null;
        attackedPiece = getPiece(newPosX, newPosY);
        if (attackedPiece != null) { // remove piece if got attacked by selectedPiece
            board.remove(attackedPiece);
        }

        playerInCheck = 0;
        for (Piece p : board) {
            if (p.getPlayer() != player) {
                p.getLegalMove(this);
            }
        }

        board = new LinkedList<Piece>(copyBoard);
        selectedPiece.setX(x);
        selectedPiece.setY(y);
    }

    /**
     * rotate the board 180 degrees
     * using formula for each piece:
     * new y = (5 - y)
     * new x = (5 - x)
     * 
     * Point's direction are switched when
     * rotate board
     * 
     * @author Lim Jun Jie
     */
    public void flipBoard() {
        for (Piece p : board) {
            p.setY(5 - p.getY());
            p.setX(6 - p.getX());

            if (p.getName().equals("Point")) {
                if (p.getDirection().equals("up")) {
                    p.setDirection("down");
                } else {
                    p.setDirection("up");
                }
            }
        }
    }

    /**
     * turn Time piece to Plus piece
     * and Plus piece to Time piece
     * 
     * @author Lim Jun Jie
     */
    private void TimePlusSwitch() {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getName().equals("Time")) {
                board.set(i, new Plus(board.get(i).getPlayer(), board.get(i).getX(), board.get(i).getY()));
            } else if (board.get(i).getName().equals("Plus")) {
                board.set(i, new Time(board.get(i).getPlayer(), board.get(i).getX(), board.get(i).getY()));
            }
        }
    }

    /**
     * check for checkmate.
     * this is done by checking if opponent's sun is checkmated
     * 
     * winner = 0, game not end
     * winner = 1 or 2, player 1 or 2 wins
     * winner = -1, stalemate (draw)
     * 
     * @author Lim Jun Jie
     */
    private void checkWin() {
        Piece enemySun = null;
        for (Piece p : board) { // get opponent's sun
            if (p.getName().equals("Sun") && p.getPlayer() != player) {
                enemySun = p;
            }
        }

        togglePlayer(); // switch to opponent to get opponent sun's and opponent's moves
        List<Integer[]> enemySunValidMoves = enemySun.getLegalMove(this);
        List<Integer[]> enemyValidMoves = new LinkedList<Integer[]>();
        for (Piece p : board) {
            if (p.getPlayer() == player) {
                enemyValidMoves = p.getLegalMove(this);
                if (enemyValidMoves.size() > 1) {
                    break;
                }
            }
        }
        togglePlayer(); // switch back to player

        if (enemySunValidMoves.size() < 1) {

            togglePlayer(); // switch to opponent to see if opponent in check

            if (playerInCheck == player) { // checkmate, set winner to winning player
                togglePlayer(); // switch back to player
                winner = player;
            } else if (enemyValidMoves.size() < 1 || board.size() < 3) { // stalemate
                togglePlayer(); // switch back to player
                winner = -1;
            } else {
                togglePlayer(); // switch back to player
            }
        }
    }

    /**
     * saves board, player, winner, turn
     * 
     * @author Lim Jun Jie
     * @author Hong Yoong Shem
     */
    public void save() {
        try {
            FileWriter fileWriter = new FileWriter("TalabiaSave.txt");
            fileWriter.write("PieceName:");
            for (Piece p : board) {
                fileWriter.write(p.getName() + ",");
            }
            fileWriter.write("\n");

            fileWriter.write("PiecePlayer:");
            for (Piece p : board) {
                fileWriter.write(p.getPlayer() + ",");
            }
            fileWriter.write("\n");

            fileWriter.write("PiecePosX:");
            for (Piece p : board) {
                fileWriter.write(p.getX() + ",");
            }
            fileWriter.write("\n");

            fileWriter.write("PiecePosY:");
            for (Piece p : board) {
                fileWriter.write(p.getY() + ",");
            }
            fileWriter.write("\n");

            fileWriter.write("PieceDirection:");
            for (Piece p : board) {
                if (p.getDirection().length() > 0) {
                    fileWriter.write(p.getDirection() + ",");
                }
            }
            fileWriter.write("\n");

            fileWriter.write("CurrentPlayer:" + player + "\n");
            fileWriter.write("Winner:" + winner + "\n");
            fileWriter.write("Turn:" + turn + "\n");
            fileWriter.write("PlayerInCheck:" + playerInCheck + "\n");
            fileWriter.write("SumTurn:" + sumTurn);

            fileWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * loads board, player, winner, turn
     * 
     * @author Lim Jun Jie
     * @author Hong Yoong Shem
     */
    public void load() {
        try {
            int line = 0;
            LinkedList<String> pieceName = new LinkedList<String>();
            LinkedList<Integer> piecePlayer = new LinkedList<Integer>();
            LinkedList<Integer> piecePosX = new LinkedList<Integer>();
            LinkedList<Integer> piecePosY = new LinkedList<Integer>();
            LinkedList<String> pieceDirection = new LinkedList<String>();

            String data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("PieceName:", "");
            String filteredData = "";
            for (int i = 0; i < data.length(); i++) {
                filteredData = filteredData + data.charAt(i);
                if (data.charAt(i) == ',') {
                    filteredData = filteredData.replace(",", "");
                    pieceName.add(filteredData);
                    filteredData = "";
                }
            }
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("PiecePlayer:", "");
            filteredData = "";
            for (int i = 0; i < data.length(); i++) {
                filteredData = filteredData + data.charAt(i);
                if (data.charAt(i) == ',') {
                    filteredData = filteredData.replace(",", "");
                    piecePlayer.add(Integer.valueOf(filteredData));
                    filteredData = "";
                }
            }
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("PiecePosX:", "");
            filteredData = "";
            for (int i = 0; i < data.length(); i++) {
                filteredData = filteredData + data.charAt(i);
                if (data.charAt(i) == ',') {
                    filteredData = filteredData.replace(",", "");
                    piecePosX.add(Integer.valueOf(filteredData));
                    filteredData = "";
                }
            }
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("PiecePosY:", "");
            filteredData = "";
            for (int i = 0; i < data.length(); i++) {
                filteredData = filteredData + data.charAt(i);
                if (data.charAt(i) == ',') {
                    filteredData = filteredData.replace(",", "");
                    piecePosY.add(Integer.valueOf(filteredData));
                    filteredData = "";
                }
            }
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("PieceDirection:", "");
            filteredData = "";
            for (int i = 0; i < data.length(); i++) {
                filteredData = filteredData + data.charAt(i);
                if (data.charAt(i) == ',') {
                    filteredData = filteredData.replace(",", "");
                    pieceDirection.add(filteredData);
                    filteredData = "";
                }
            }
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("CurrentPlayer:", "");
            player = Integer.parseInt(data);
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("Winner:", "");
            winner = Integer.parseInt(data);
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("Turn:", "");
            turn = Integer.parseInt(data);
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("PlayerInCheck:", "");
            playerInCheck = Integer.parseInt(data);
            line++;

            data = Files.readAllLines(Paths.get("TalabiaSave.txt")).get(line);
            data = data.replace("SumTurn:", "");
            sumTurn = Integer.parseInt(data);

            // loading board according to save data
            board.clear();
            int directionIndex = 0;
            for (int i = 0; i < pieceName.size(); i++) {
                switch (pieceName.get(i)) {
                    case "Point":
                        board.add(new Point(piecePlayer.get(i), piecePosX.get(i), piecePosY.get(i),
                                pieceDirection.get(directionIndex)));
                        directionIndex++;
                        break;

                    case "Hourglass":
                        board.add(new Hourglass(piecePlayer.get(i), piecePosX.get(i), piecePosY.get(i)));
                        break;

                    case "Time":
                        board.add(new Time(piecePlayer.get(i), piecePosX.get(i), piecePosY.get(i)));
                        break;

                    case "Plus":
                        board.add(new Plus(piecePlayer.get(i), piecePosX.get(i), piecePosY.get(i)));
                        break;

                    case "Sun":
                        board.add(new Sun(piecePlayer.get(i), piecePosX.get(i), piecePosY.get(i)));
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // REMOVE ALL METHODS BELOW, TESTING ONLY
    public void addPiece(Piece p) {
        board.add(p);
    }

    public void clearBoard() {
        board.clear();
    }
}