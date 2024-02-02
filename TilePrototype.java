import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Part in MVC design pattern: View
 * View's design pattern: Prototype
 * Part in View design pattern: Prototype
 * Purpose: an interface to create tiles for BoardView(Prototype registry)
 */
public interface TilePrototype {
    public TilePrototype clone(int posX, int posY);

    public int getX();

    public int getY();

    public JButton getButton();

    public void setX(int posX);

    public void setY(int posY);

    public void setColor(Color colour);

    public void setImage(String pieceImagePath);

    public ActionListener getActionListener();
}

/**
 * Part in MVC design pattern: View
 * View's design pattern: Prototype
 * Part in View design pattern: Button
 * Purpose: Button that allows player to use controller to show piece's legal
 * move and move piece when pressed. Also act as indication of piece's legal
 * move and piece's position.
 */
class Tile implements TilePrototype {
    private JButton button = new JButton();
    int posX;
    int posY;
    private ActionListener action;

    /**
     * this constructor functions as a tile creator
     * 
     * @author Lim Jun Jie
     */
    public Tile(ActionListener action) {
        this.action = action;
    }

    /**
     * this constructor functions as a tile
     * only ActionListener from the tile creator is passed to copy
     * 
     * @author Lim Jun Jie
     */
    public Tile(TilePrototype tile, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        button.addActionListener(tile.getActionListener());
        button.putClientProperty("tile", this);
    }

    public JButton getButton() {
        return button;
    }

    public ActionListener getActionListener() {
        return action;
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

    /**
     * set background colour of button
     *
     * @author Lim Jun Jie
     */
    public void setColor(Color colour) {
        button.setBackground(colour);
    }

    /**
     * set image of button
     *
     * @author Lim Jun Jie
     */
    public void setImage(String pieceImagePath) {
        button.setIcon(new ImageIcon(pieceImagePath)); // e.g. "Images_p1\\Point_up.png"
    }

    /**
     * returns shallow copy the button.
     * only actionListener is passed to copy as coordinates vary
     * 
     * @author Lim Jun Jie
     */
    public TilePrototype clone(int posX, int posY) {
        return new Tile(this, posX, posY);
    }
}
