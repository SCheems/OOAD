import java.util.*;

/**
 * Part in MVC design pattern: Controller
 * Controller's design pattern: Adapter
 * 
 * Model's design pattern: Bridge
 * Part in Bridge design pattern: Client
 * 
 * Part in Adapter design pattern: Client interface
 * Purpose: an interface to BoardAdapter class(Adapter)
 */
public interface BoardController {
    public int getTotalTurn();

    public void move(int posX, int posY);

    public List<Piece> getBoard();

    public List<Integer[]> getMove(int posX, int posY);

    public int getWinner();

    public void resetGame();

    public void saveGame();

    public void loadGame();

    public int getPlayer();

    public int getPlayerInCheck();

    public Piece getPiece(String name, int player);

    public int getPiecePlayer(Piece p);

    public String getPieceName(Piece p);

    public String getPieceDirection(Piece p);

    public int getPiecePosX(Piece p);

    public int getPiecePosY(Piece p);
}

/**
 * Part in MVC design pattern: Controller
 * Controller's design pattern: Adapter
 * Part in Adapter design pattern: Adapter
 * Purpose: act as an interface to the player to manipulate Model class in MVC.
 * Allow info transfer from Model class in MVC to View class in MVC, and allow
 * player to use methods Model class in MVC
 */
class BoardAdapter implements BoardController {
    Board board = new Board();

    BoardAdapter() {
    }

    public int getTotalTurn(){
        return board.getSumTurn();
    }

    /**
     * move piece selected by player
     * 
     * @author Lim Jun Jie
     */
    public void move(int posX, int posY) {
        board.movePiece(posX, posY);
    }

    /**
     * get the board
     * 
     * @author Lim Jun Jie
     */
    public List<Piece> getBoard() {
        return board.getBoard();
    }

    /**
     * get legal moves of a selected piece
     * 
     * @author Lim Jun Jie
     */
    public List<Integer[]> getMove(int posX, int posY) {
        board.setSelectedPiece(posX, posY);
        List<Integer[]> moves = board.getValidMove();
        return moves;
    }

    /**
     * get winner of the game
     * 
     * winner = 0, game not end
     * winner = 1 or 2, player 1 or 2 wins
     * winner = -1, stalemate (draw)
     * 
     * @author Lim Jun Jie
     */
    public int getWinner() {
        return board.getWinner();
    }

    /**
     * reset the board
     * 
     * @author Lim Jun Jie
     */
    public void resetGame() {
        board.setBoard();
    }

    /**
     * save the game
     * 
     * @author Lim Jun Jie
     */
    public void saveGame() {
        board.save();
    }

    /**
     * load the game
     * 
     * @author Lim Jun Jie
     */
    public void loadGame() {
        board.load();
    }

    /**
     * get the current player
     * 
     * @author Lim Jun Jie
     */
    public int getPlayer() {
        return board.getPlayer();
    }

    /**
     * get the player under check
     * 
     * @author Lim Jun Jie
     */
    public int getPlayerInCheck() {
        return board.getPlayerInCheck();
    }

    /**
     * get piece's player
     * 
     * @author Lim Jun Jie
     */
    public int getPiecePlayer(Piece p) {
        return p.getPlayer();
    }

    /**
     * get piece from board by name and player
     * names available:
     * Point, Hourglass, Time, Plus, Sun
     * 
     * @author Lim Jun Jie
     */
    public Piece getPiece(String name, int player) {
        List<Piece> pieces = board.getBoard();

        for (Piece p : pieces) {
            if (p.getName().equals(name) && p.getPlayer() == player) {
                return p;
            }
        }
        return null;
    }

    /**
     * get piece's player
     * 
     * @author Lim Jun Jie
     */
    public String getPieceName(Piece p) {
        return p.getName();
    }

    public String getPieceDirection(Piece p) {
        return p.getDirection();
    }

    /**
     * get piece's posX
     * 
     * @author Lim Jun Jie
     */
    public int getPiecePosX(Piece p) {
        return p.getX();
    }

    /**
     * get piece's posY
     * 
     * @author Lim Jun Jie
     */
    public int getPiecePosY(Piece p) {
        return p.getY();
    }

}
