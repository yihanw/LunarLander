import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the edit toolbar
public class ToolBarView extends JPanel implements Observer {

	private GameModel model;
    JButton undo = new JButton("Undo");
    JButton redo = new JButton("Redo");

    public ToolBarView(GameModel model) {

    	this.model = model;
    	model.addObserver(this);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // prevent buttons from stealing focus
        undo.setFocusable(false);
        redo.setFocusable(false);

        undo.setEnabled(false);
        redo.setEnabled(false);
        
        add(undo);
        add(redo);
        
        undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.undo();
			}
		});
        
        redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				model.redo();
			}
		});
    }

    @Override
    public void update(Observable o, Object arg) {
 
    	try{
    		model.canUndo();
    	} catch(CannotUndoException e){
    		System.out.println("catch");
    		undo.setEnabled(false);
    	}
    	try{
    		model.canRedo();
    	} catch(CannotRedoException e){
    		redo.setEnabled(false);
    	}
    	undo.setEnabled(model.canUndo());
    	redo.setEnabled(model.canRedo());
    
    }
}
