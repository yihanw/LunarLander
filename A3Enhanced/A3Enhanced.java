/*
 * CS349 A3Enhanced
 * 
 * Enhancement: add asteroids that need to avoid, use 10 dark gray rectangles that 
 * 				are randomly positioned at the top half of the game world to
 * 				to represent asteroids. The ship should crash if it intersects with 
 * 				any of the asteroids, press SPACE will restart the game.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class A3Enhanced extends JPanel {

    A3Enhanced() {
        // create the model
        GameModel model = new GameModel(60, 700, 200, 20);

        JPanel playView = new PlayView(model);
        JPanel editView = new EditView(model);
        editView.setPreferredSize(new Dimension(700, 200));

        // layout the views
        setLayout(new BorderLayout());

        add(new MessageView(model), BorderLayout.NORTH);

        // nested Border layout for edit view
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        editPanel.add(new ToolBarView(model), BorderLayout.NORTH);
        editPanel.add(editView, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);

        // main playable view will be resizable
        add(playView, BorderLayout.CENTER);

        // for getting key events into PlayView
        playView.requestFocusInWindow();
       // playView.setAlignmentY(JPanel.TOP_ALIGNMENT);
    }

    public static void main(String[] args) {
        // create the window
        JFrame f = new JFrame("A3Enhanced"); // jframe is the app window
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700, 600); // window size
        f.setContentPane(new A3Enhanced()); // add main panel to jframe
        f.setVisible(true); // show the window
    }
}
