import java.util.*;

// -1 < posX < 7, -1 < posY < 6

/**
 * Part in MVC design pattern: Model
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Implementation
 * Purpose: an interface of board pieces to Board class(Abstraction)
 */
public interface Piece {
    public String getName();

    public int getPlayer();

    public int getX();

    public int getY();

    public String getDirection();

    public void setX(int posX);

    public void setY(int posY);

    public void setDirection(String direction);

    public List<Integer[]> getLegalMove(Board board);
}

/**
 * Part in MVC design pattern: Model
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Concrete implementation
 * Purpose: allow Board class(Abstraction) to get moves and info of Point piece
 * and inform Board class if opponent's Sun piece is in check
 */
class Point implements Piece {
    private String name = "Point";
    private int player;
    private int posX;
    private int posY;
    private String direction;

    public Point(int player, int posX, int posY, String direction) {
        this.player = player;
        this.posX = posX;
        this.posY = posY;
        this.direction = direction; // player 1 = up, player 2 = down
    }

    public Point() {
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void setX(int posX) {
        this.posX = posX;
    }

    public void setY(int posY) {
        this.posY = posY;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * get legal move of Point Piece
     *
     * @author Lim Jun Jie
     */
    public List<Integer[]> getLegalMove(Board board) {
        List<Integer[]> legalMoves = new LinkedList<Integer[]>();

        for (int i = 1; i <= 2; i++) {
            Integer[] arr = { posX, posY }; // prevent unecessary ref by java

            if (direction == "up" && (posY + i) < 6) {
                arr[1] = posY + i;
                legalMoves.add(arr);
            } else if (direction == "down" && (posY - i) > -1) {
                arr[1] = posY - i;
                legalMoves.add(arr);
            }
        }

        legalMoves = filterMove(legalMoves, board);

        return legalMoves;
    }

    /**
     * filter out the invalid moves where Point piece
     * can't attack own allies
     * or stop after attacking enemy piece
     * 
     * @author Lim Jun Jie
     */
    private List<Integer[]> filterMove(List<Integer[]> moves, Board board) {
        List<Piece> pieces = new LinkedList<Piece>();
        for (Integer[] m : moves) {
            if (board.getPiece(m[0], m[1]) != null) {
                pieces.add(board.getPiece(m[0], m[1]));
            }
        }
        List<Integer[]> filteredMove = new LinkedList<Integer[]>(moves);

        for (Piece p : pieces) {
            for (Integer[] m : moves) {
                if (p.getPlayer() == player) { // own piece
                    if (p.getY() > posY && m[1] >= p.getY()) { // up
                        filteredMove.remove(m);
                    } else if (p.getY() < posY && m[1] <= p.getY()) { // down
                        filteredMove.remove(m);
                    }
                } else { // enemy piece
                    if (p.getY() > posY && m[1] > p.getY()) { // up
                        filteredMove.remove(m);
                    } else if (p.getY() < posY && m[1] < p.getY()) { // down
                        filteredMove.remove(m);
                    }
                }
            }
        }
        isCheckSun(filteredMove, board);

        return filteredMove;
    }

    /**
     * flag opponent's sun as check in Board class
     * 
     * @author Lim Jun Jie
     */
    private void isCheckSun(List<Integer[]> moves, Board board) {
        for (Integer[] m : moves) {
            Piece p = board.getPiece(m[0], m[1]);
            if (p != null) {
                if (p.getName().equals("Sun")) {
                    board.setPlayerInCheck(p.getPlayer());
                }
            }
        }
    }
}

/**
 * Part in MVC design pattern: Model
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Concrete implementation
 * Purpose: allow Board class(Abstraction) to get moves and info of Hourglass
 * piece and inform Board class if opponent's Sun piece is in check
 */
class Hourglass implements Piece {
    private String name = "Hourglass";
    private int player;
    private int posX;
    private int posY;

    public Hourglass(int player, int posX, int posY) {
        this.player = player;
        this.posX = posX;
        this.posY = posY;
    }

    public Hourglass() {
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void setX(int posX) {
        this.posX = posX;
    }

    public void setY(int posY) {
        this.posY = posY;
    }

    public String getDirection() {
        return "";
    }

    public void setDirection(String direction) {
    }

    /**
     * get legal move of Hourglass Piece
     *
     * @author Lim Jun Jie
     */
    public List<Integer[]> getLegalMove(Board board) {
        List<Integer[]> legalMoves = new LinkedList<Integer[]>();
        Integer[][] arr = { { posX + 2, posY + 1 }, { posX + 2, posY - 1 },
                { posX - 2, posY + 1 }, { posX - 2, posY - 1 },
                { posX + 1, posY + 2 }, { posX - 1, posY + 2 },
                { posX + 1, posY - 2 }, { posX - 1, posY - 2 }
        };

        for (int i = 0; i < arr.length; i++) {
            if (arr[i][0] < 7 && arr[i][0] > -1 &&
                    arr[i][1] < 6 && arr[i][1] > -1) {

                legalMoves.add(arr[i]);
            }
        }

        legalMoves = filterMove(legalMoves, board);

        return legalMoves;
    }

    /**
     * filter out the invalid moves the Hourglass piece
     * can't attack own allies
     * 
     * @author Lim Jun Jie
     */
    private List<Integer[]> filterMove(List<Integer[]> moves, Board board) {
        List<Piece> pieces = new LinkedList<Piece>();
        List<Integer[]> filteredMove = new LinkedList<Integer[]>(moves);

        for (Integer[] m : moves) {
            if (board.getPiece(m[0], m[1]) != null) {
                pieces.add(board.getPiece(m[0], m[1]));
            }
        }

        for (Piece p : pieces) { // remove illegal moves
            for (Integer[] m : moves) {
                if (p.getPlayer() == player &&
                        m[0] == p.getX() && m[1] == p.getY()) { // own piece
                    filteredMove.remove(m);
                }
            }
        }
        isCheckSun(filteredMove, board);

        return filteredMove;
    }

    /**
     * flag opponent's sun as check in Board class
     * 
     * @author Lim Jun Jie
     */
    private void isCheckSun(List<Integer[]> moves, Board board) {
        for (Integer[] m : moves) {
            Piece p = board.getPiece(m[0], m[1]);
            if (p != null) {
                if (p.getName().equals("Sun")) {
                    board.setPlayerInCheck(p.getPlayer());
                }
            }
        }
    }
}

/**
 * Part in MVC design pattern: Model
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Concrete implementation
 * Purpose: allow Board class(Abstraction) to get moves and info of Time piece
 * and inform Board class if opponent's Sun piece is in check
 */
class Time implements Piece {
    private String name = "Time";
    private int player;
    private int posX;
    private int posY;

    public Time(int player, int posX, int posY) {
        this.player = player;
        this.posX = posX;
        this.posY = posY;
    }

    public Time() {
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void setX(int posX) {
        this.posX = posX;
    }

    public void setY(int posY) {
        this.posY = posY;
    }

    public String getDirection() {
        return "";
    }

    public void setDirection(String direction) {
    }

    /**
     * get legal move of Time Piece
     * without any reagrds to when Time Piece should stop
     * 
     * @author Lim Jun Jie
     */
    public List<Integer[]> getLegalMove(Board board) {
        List<Integer[]> legalMoves = new LinkedList<Integer[]>();
        // top right
        int y = 1;
        for (int x = 1; x < 7; x++) {
            Integer[] arr = { posX, posY };
            arr[0] = posX + x;
            arr[1] = posY + y;
            if (arr[0] < 7 && arr[1] < 6) {
                legalMoves.add(arr);
            }
            y++;
        }
        // bottom left
        y = 1;
        for (int x = 1; x < 7; x++) {
            Integer[] arr = { posX, posY };
            arr[0] = posX - x;
            arr[1] = posY - y;
            if (arr[0] > -1 && arr[1] > -1) {
                legalMoves.add(arr);
            }
            y++;
        }

        y = 1;
        for (int x = 1; x < 7; x++) {
            Integer[] arr = { posX, posY };
            // top left
            arr[0] = posX - x;
            arr[1] = posY + y;
            if (arr[0] > -1 && arr[1] < 6) {
                legalMoves.add(arr);
            }
            y++;
        }
        // bottom right
        y = 1;
        for (int x = 1; x < 7; x++) {
            Integer[] arr = { posX, posY };
            arr[0] = posX + x;
            arr[1] = posY - y;
            if (arr[0] < 7 && arr[1] > -1) {
                legalMoves.add(arr);
            }
            y++;
        }

        legalMoves = filterMoves(legalMoves, board);

        return legalMoves;
    }

    /**
     * filter out the invalid moves where the piece
     * can't attack own allies
     * or stop after attacking enemy piece
     * 
     * @author Lim Jun Jie
     */
    private List<Integer[]> filterMoves(List<Integer[]> moves, Board board) {
        List<Piece> pieces = new LinkedList<Piece>();
        for (Integer[] m : moves) {
            if (board.getPiece(m[0], m[1]) != null) {
                pieces.add(board.getPiece(m[0], m[1]));
            }
        }
        List<Integer[]> filteredMove = new LinkedList<Integer[]>(moves);

        for (Piece p : pieces) {
            for (Integer[] m : moves) {
                if (p.getPlayer() == player) { // own piece
                    if (p.getX() > posX && p.getY() > posY &&
                            m[0] >= p.getX() && m[1] >= p.getY()) { // up right
                        filteredMove.remove(m);
                    } else if (p.getX() > posX && p.getY() < posY &&
                            m[0] >= p.getX() && m[1] <= p.getY()) { // down right
                        filteredMove.remove(m);
                    } else if (p.getX() < posX && p.getY() > posY &&
                            m[0] <= p.getX() && m[1] >= p.getY()) { // top left
                        filteredMove.remove(m);
                    } else if (p.getX() < posX && p.getY() < posY &&
                            m[0] <= p.getX() && m[1] <= p.getY()) { // down left
                        filteredMove.remove(m);
                    }
                } else { // enemy piece
                    if (p.getX() > posX && p.getY() > posY &&
                            m[0] > p.getX() && m[1] > p.getY()) { // up right
                        filteredMove.remove(m);
                    } else if (p.getX() > posX && p.getY() < posY &&
                            m[0] > p.getX() && m[1] < p.getY()) { // down right
                        filteredMove.remove(m);
                    } else if (p.getX() < posX && p.getY() > posY &&
                            m[0] < p.getX() && m[1] > p.getY()) { // top left
                        filteredMove.remove(m);
                    } else if (p.getX() < posX && p.getY() < posY &&
                            m[0] < p.getX() && m[1] < p.getY()) { // down left
                        filteredMove.remove(m);
                    }
                }
            }
        }
        isCheckSun(filteredMove, board);

        return filteredMove;
    }

    /**
     * flag opponent's sun as check in Board class
     * 
     * @author Lim Jun Jie
     */
    private void isCheckSun(List<Integer[]> moves, Board board) {
        for (Integer[] m : moves) {
            Piece p = board.getPiece(m[0], m[1]);
            if (p != null) {
                if (p.getName().equals("Sun")) {
                    board.setPlayerInCheck(p.getPlayer());
                }
            }
        }
    }
}

/**
 * Part in MVC design pattern: Model
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Concrete implementation
 * Purpose: allow Board class(Abstraction) to get moves and info of Plus piece
 * and inform Board class if opponent's Sun piece is in check
 */
class Plus implements Piece {
    private String name = "Plus";
    private int player;
    private int posX;
    private int posY;

    public Plus(int player, int posX, int posY) {
        this.player = player;
        this.posX = posX;
        this.posY = posY;
    }

    public Plus() {
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void setX(int posX) {
        this.posX = posX;
    }

    public void setY(int posY) {
        this.posY = posY;
    }

    public String getDirection() {
        return "";
    }

    public void setDirection(String direction) {
    }

    /**
     * get legal move of Plus Piece
     * 
     * @author Lim Jun Jie
     */
    public List<Integer[]> getLegalMove(Board board) {
        List<Integer[]> legalMoves = new LinkedList<Integer[]>();
        // up
        int n = 1;
        while (posY + n < 6) {
            Integer[] arr = { posX, posY + n };
            legalMoves.add(arr);
            n++;
        }
        // down
        n = 1;
        while (posY - n > -1) {
            Integer[] arr = { posX, posY - n };
            legalMoves.add(arr);
            n++;
        }
        // right
        n = 1;
        while (posX + n < 7) {
            Integer[] arr = { posX + n, posY };
            legalMoves.add(arr);
            n++;
        }
        // left
        n = 1;
        while (posX - n > -1) {
            Integer[] arr = { posX - n, posY };
            legalMoves.add(arr);
            n++;
        }

        legalMoves = filterMove(legalMoves, board);

        return legalMoves;
    }

    /**
     * filter out the invalid moves where the piece
     * can't attack own allies
     * or stop after attacking enemy piece
     * 
     * @author Lim Jun Jie
     */
    private List<Integer[]> filterMove(List<Integer[]> moves, Board board) {
        List<Piece> pieces = new LinkedList<Piece>();
        for (Integer[] m : moves) {
            if (board.getPiece(m[0], m[1]) != null) {
                pieces.add(board.getPiece(m[0], m[1]));
            }
        }

        List<Integer[]> filteredMove = new LinkedList<Integer[]>(moves);

        for (Piece p : pieces) {
            for (Integer[] m : moves) {
                if (p.getPlayer() == player) { // own piece
                    if (p.getY() > posY && m[1] >= p.getY()) { // up
                        filteredMove.remove(m);
                    } else if (p.getY() < posY && m[1] <= p.getY()) { // down
                        filteredMove.remove(m);
                    } else if (p.getX() < posX && m[0] <= p.getX()) { // left
                        filteredMove.remove(m);
                    } else if (p.getX() > posX && m[0] >= p.getX()) { // right
                        filteredMove.remove(m);
                    }
                } else { // enemy piece
                    if (p.getY() > posY && m[1] > p.getY()) { // up
                        filteredMove.remove(m);
                    } else if (p.getY() < posY && m[1] < p.getY()) { // down
                        filteredMove.remove(m);
                    } else if (p.getX() < posX && m[0] < p.getX()) { // left
                        filteredMove.remove(m);
                    } else if (p.getX() > posX && m[0] > p.getX()) { // right
                        filteredMove.remove(m);
                    }
                }
            }
        }
        isCheckSun(filteredMove, board);

        return filteredMove;
    }

    /**
     * flag opponent's sun as check in Board class
     * 
     * @author Lim Jun Jie
     */
    private void isCheckSun(List<Integer[]> moves, Board board) {
        for (Integer[] m : moves) {
            Piece p = board.getPiece(m[0], m[1]);
            if (p != null) {
                if (p.getName().equals("Sun")) {
                    board.setPlayerInCheck(p.getPlayer());
                }
            }
        }
    }
}

/**
 * Part in MVC design pattern: Model
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Concrete implementation
 * Purpose: allow Board class(Abstraction) to get moves and info of Sun piece
 */
class Sun implements Piece {
    private String name = "Sun";
    private int player;
    private int posX;
    private int posY;

    public Sun(int player, int posX, int posY) {
        this.player = player;
        this.posX = posX;
        this.posY = posY;
    }

    public Sun() {
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void setX(int posX) {
        this.posX = posX;
    }

    public void setY(int posY) {
        this.posY = posY;
    }

    public String getDirection() {
        return "";
    }

    public void setDirection(String direction) {
    }

    /**
     * get unfiltered move of opponent's Sun piece
     * 
     * @author Lim Jun Jie
     */
    private List<Integer[]> getOpponentSunMove(Board board) {
        Piece opponentSun = null;
        List<Integer[]> legalMoves = new LinkedList<Integer[]>();
        for (Piece p : board.getBoard()) {
            if (p.getName().equals("Sun") && p.getPlayer() != board.getPlayer()) {
                opponentSun = p;
            }
        }

        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                Integer[] arr = { opponentSun.getX() + x, opponentSun.getY() + y };
                if (arr[0] > -1 && arr[0] < 7 &&
                        arr[1] > -1 && arr[1] < 6 &&
                        (arr[0] != opponentSun.getX() || arr[1] != opponentSun.getY())) {
                    legalMoves.add(arr);
                }
            }
        }

        return legalMoves;
    }

    /**
     * get legal move of Sun Piece
     * without any reagrds if Sun Piece entering a check after its move
     * 
     * @author Lim Jun Jie
     */
    public List<Integer[]> getLegalMove(Board board) {
        List<Integer[]> legalMoves = new LinkedList<Integer[]>();
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                Integer[] arr = { posX + x, posY + y };
                if (arr[0] > -1 && arr[0] < 7 &&
                        arr[1] > -1 && arr[1] < 6 &&
                        (arr[0] != posX || arr[1] != posY)) {
                    legalMoves.add(arr);
                }
            }
        }

        legalMoves = filterMove(legalMoves, board);

        return legalMoves;
    }

    /**
     * filter out the invalid moves the Sun piece
     * can't attack own allies
     * 
     * @author Lim Jun Jie
     */
    private List<Integer[]> filterMove(List<Integer[]> moves, Board board) {
        List<Piece> pieces = new LinkedList<Piece>();
        List<Integer[]> enemyMoves = new LinkedList<Integer[]>();
        List<Integer[]> filteredMove = new LinkedList<Integer[]>(moves);
        List<Integer[]> temp;

        for (Integer[] m : moves) {
            if (board.getPiece(m[0], m[1]) != null) {
                pieces.add(board.getPiece(m[0], m[1]));
            }
        }

        for (Piece p : pieces) { // remove illegal moves
            for (Integer[] m : moves) {
                if (p.getPlayer() == player &&
                        m[0] == p.getX() && m[1] == p.getY()) { // own piece
                    filteredMove.remove(m);
                }
            }
        }

        for (Piece p : board.getBoard()) { // remove moves that get Sun into check
            if (p.getPlayer() != board.getPlayer()) {
                if (p.getName().equals("Sun")) {
                    enemyMoves = getOpponentSunMove(board);
                } else {
                    enemyMoves = p.getLegalMove(board);
                }

                temp = new LinkedList<Integer[]>(filteredMove); // prevent concurrent access to filteredMove

                for (Integer[] m : enemyMoves) {
                    for (Integer[] n : temp) {
                        if (n[0] == m[0] && n[1] == m[1]) {
                            filteredMove.remove(n);
                        }
                    }
                }

            }
        }
        return filteredMove;
    }
}
