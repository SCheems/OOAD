import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Part in MVC design pattern: View, Controller
 * Controller's design pattern: Adapter
 * Part in Adapter design pattern: Client
 * 
 * View's design pattern: Prototype
 * Part in Prototype design pattern: Prototype registry
 * 
 * Purpose: allow player to view the board and use the Controller class in MVC
 * design pattern
 */
public class BoardView {
    private BoardController controller = new BoardAdapter();
    private List<TilePrototype> tiles = new LinkedList<TilePrototype>();
    private int moveState = 0;
    private JLabel turn = new JLabel("Turn: 0");
    private JLabel winner = new JLabel("Winner: 0");
    private JLabel playerInCheck = new JLabel("Player in Check: 0");
    private JLabel player = new JLabel("Current Player: 1");

    /**
     * create a view with buttons and menu for player
     *
     * @author Lim Jun Jie
     */
    public BoardView() {
        // initialize window and screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Border line = BorderFactory.createLineBorder(Color.black);
        Border padding = new EmptyBorder(10, 10, 10, 10);

        JFrame master = new JFrame("Talabia Chess");
        master.setLayout(new BorderLayout());
        master.setSize((int) screenSize.getWidth() * 2 / 3, (int) screenSize.getHeight() * 2 / 3);
        master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        master.setResizable(true);

        JPanel boardScreen = new JPanel();
        boardScreen.setSize((int) master.getWidth() * 2 / 3, (int) master.getHeight());
        boardScreen.setPreferredSize(new Dimension((int) master.getWidth() * 2 / 3, (int) master.getHeight()));
        boardScreen.setLayout(new GridLayout(6, 7, 10, 5));
        boardScreen.setBorder(new CompoundBorder(line, padding));
        boardScreen.setVisible(true);
        master.add(boardScreen, BorderLayout.CENTER);

        JPanel menuScreen = new JPanel();
        menuScreen.setSize((int) master.getWidth() / 3, (int) master.getHeight());
        menuScreen.setPreferredSize(new Dimension((int) master.getWidth() / 3, (int) master.getHeight()));
        menuScreen.setLayout(new BoxLayout(menuScreen, BoxLayout.PAGE_AXIS));
        menuScreen.setBorder(new CompoundBorder(line, padding));
        menuScreen.setVisible(true);
        master.add(menuScreen, BorderLayout.LINE_END);

        // create menu
        turn.setFont(new Font("Arial", Font.BOLD, 24));
        turn.setAlignmentX(Component.LEFT_ALIGNMENT);
        turn.setBorder(padding);
        menuScreen.add(turn);

        player.setFont(new Font("Arial", Font.BOLD, 24));
        player.setAlignmentX(Component.LEFT_ALIGNMENT);
        player.setBorder(padding);
        menuScreen.add(player);

        playerInCheck.setFont(new Font("Arial", Font.BOLD, 24));
        playerInCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        playerInCheck.setBorder(padding);
        menuScreen.add(playerInCheck);

        winner.setFont(new Font("Arial", Font.BOLD, 24));
        winner.setAlignmentX(Component.LEFT_ALIGNMENT);
        winner.setBorder(padding);
        menuScreen.add(winner);

        JButton reset = new JButton("Reset Game");
        reset.setPreferredSize(new Dimension(1, 1));
        reset.setFont(new Font("Callibri", Font.BOLD, 24));
        reset.setBorder(new CompoundBorder(line, padding));
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        reset.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuScreen.add(reset);

        JButton save = new JButton("Save Game");
        save.setFont(new Font("Callibri", Font.BOLD, 24));
        save.setBorder(new CompoundBorder(line, padding));
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
            }
        });
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuScreen.add(save);

        JButton load = new JButton("Load Game");
        load.setFont(new Font("Callibri", Font.BOLD, 24));
        load.setBorder(new CompoundBorder(line, padding));
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGame();
            }
        });
        load.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuScreen.add(load);

        // create board (grid of buttons)
        ActionListener tileListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get JButton that has tile obj in its client property
                JButton b = (JButton) e.getSource();

                // get tile object, need its posX and posY
                TilePrototype t = (TilePrototype) b.getClientProperty("tile");
                
                if (controller.getWinner() == 0) {
                    t.setColor(Color.GREEN);
                    update(t.getX(), t.getY());
                }
            }
        };

        TilePrototype tileCreator = new Tile(tileListener);

        for (int y = 5; y > -1; y--) {
            for (int x = 0; x < 7; x++) {
                TilePrototype t = tileCreator.clone(x, y);
                tiles.add(t);
            }
        }

        for (TilePrototype t : tiles) {
            boardScreen.add(t.getButton());
        }

        List<Piece> pieces = controller.getBoard();
        setTilesImage(pieces);

        // set window visible after all components are added
        master.setVisible(true);
    }

    /**
     * update board display
     *
     * @author Lim Jun Jie
     */
    public void update(int posX, int posY) {
        if (moveState == 0) { // select piece
            List<Integer[]> moves = controller.getMove(posX, posY);
            if (moves.size() > 0) {
                highlightMoves(moves);
            }
            moveState = 1;

        } else { // move piece
            controller.move(posX, posY);
            List<Piece> pieces = controller.getBoard();
            setTilesImage(pieces);
            highlightCheck(pieces);

            if (controller.getWinner() != 0) {
                setWinner();

            } else {
                setPlayer();
            }

            turn.setText("Turn: " + controller.getTotalTurn());
            moveState = 0;
        }
    }

    /**
     * highlight Sun that is checked
     *
     * @author Lim Jun Jie
     */
    private void highlightCheck(List<Piece> pieces) {
        int checkPlayer = controller.getPlayerInCheck();
        playerInCheck.setText("Player in Check: " + checkPlayer);

        Piece sun = controller.getPiece("Sun", checkPlayer);
        if (sun != null) {
            for (TilePrototype t : tiles) {
                if (controller.getPiecePosX(sun) == t.getX() && controller.getPiecePosY(sun) == t.getY()) {
                    t.setColor(Color.RED);
                    break;
                }
            }
        }
    }

    /**
     * highlight legal moves of piece on board
     *
     * @author Lim Jun Jie
     */
    private void highlightMoves(List<Integer[]> moves) {
        for (Integer[] m : moves) {
            for (TilePrototype t : tiles) {
                if (m[0] == t.getX() && m[1] == t.getY()) {
                    t.setColor(Color.GREEN);
                    break;
                }
            }
        }
    }

    /**
     * set all tile's images according to the pieces position
     *
     * @author Lim Jun Jie
     */
    private void setTilesImage(List<Piece> pieces) {
        for (TilePrototype t : tiles) { // clear tile images
            t.setImage(null);
        }

        for (Piece p : pieces) { // put tile images
            for (TilePrototype t : tiles) {
                t.setColor(null); // un-colour button

                if (t.getX() == controller.getPiecePosX(p) && t.getY() == controller.getPiecePosY(p)) {
                    int player = controller.getPiecePlayer(p);
                    String name = controller.getPieceName(p);

                    if (name.equals("Point")) {
                        t.setImage("Images_p" + player + "\\" + "Point_" + controller.getPieceDirection(p) + ".png");
                    } else {
                        t.setImage("Images_p" + player + "\\" + name + ".png");
                    }
                    break;

                }
            }
        }
    }

    /**
     * resets the game
     *
     * @author Ling Chee Xiang
     * @author Lim Jun Jie
     */
    public void resetGame() {
        controller.resetGame();
        List<Piece> pieces = controller.getBoard();
        setTilesImage(pieces);
        playerInCheck.setText("Player in Check: 0");
        player.setText("Current Player: 1");
        winner.setText("Winner: 0");
        turn.setText("Turn: 0");
    }

    /**
     * saves the game
     *
     * @author Ling Chee Xiang
     */
    public void saveGame() {
        controller.saveGame();
    }

    /**
     * loads the game
     *
     * @author Ling Chee Xiang
     * @author Lim Jun Jie
     */
    public void loadGame() {
        controller.loadGame();
        List<Piece> pieces = controller.getBoard();
        setTilesImage(pieces);
        highlightCheck(pieces);

        playerInCheck.setText("Player in Check: " + controller.getPlayerInCheck());
        player.setText("Current Player: " + controller.getPlayer());
        winner.setText("Winner: " + controller.getWinner());
        turn.setText("Turn: " + controller.getTotalTurn());
    }

    /**
     * set player to display current player
     *
     * @author Lim Jun Jie
     */
    public void setPlayer() {
        player.setText("Current Player: " + controller.getPlayer());
    }

    /**
     * set winner to display winner of the game
     *
     * @author Lim Jun Jie
     */
    public void setWinner() {
        int gameWinner = controller.getWinner();
        if (gameWinner == 0) { // game ongoing
            player.setText("Winner: 0");
        } else if (gameWinner == -1) { // stalemate (draw)
            player.setText("Winner: None (Stalemate)");
        } else {
            player.setText("Winner: " + gameWinner);
        }
    }
}
