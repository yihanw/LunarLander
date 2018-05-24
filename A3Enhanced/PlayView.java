import javax.swing.*;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

// the actual game view
public class PlayView extends JPanel implements Observer {

	private GameModel model;
	
    public PlayView(GameModel model) {
        // needs to be focusable for keylistener
        setFocusable(true);
        setBackground(Color.BLACK);
        
        this.model = model;
    	model.addObserver(this);
    	
    	addKeyListener(new KeyAdapter() { 
    		public void keyPressed(KeyEvent e) { 
    			switch(e.getKeyCode()){
    			case 32: // space
    				if (model.ship.isCrashed || model.ship.isLanded){
    					model.ship.reset(new Point2d(350, 50));
    					model.ship.isCrashed = false;
    				} else {
    					model.ship.setPaused(!model.ship.isPaused());
    				}
    				break;
    			case 87: //w
    				model.ship.thrustUp();
    				break;
    			case 65: //a
    				model.ship.thrustLeft();
    				break;
    			case 83: //s
    				model.ship.thrustDown();
    				break;
    			case 68: //d
    				model.ship.thrustRight();
    				break;
    			}
    			model.setChangedAndNotify();
    		
    		}
    	});
    }
    
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        AffineTransform M = g2.getTransform();

        double xoffset = model.ship.getPosition().x;//+5;
        double yoffset = model.ship.getPosition().y;//+5;
        g2.translate(700/2, this.getHeight()/2);
        g2.scale(3, 3);
        g2.translate(-xoffset, -yoffset);
        
        // draw worldBounds
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(model.worldBounds);
        
        // draw terrain
        g2.setColor(Color.DARK_GRAY);
       	g2.fillPolygon(model.xpoints, model.ypoints, 22);
       	
       	// asteroids
       	for(int i=0; i<10; i++){
       		g2.fill(model.getAsteroid(i));
       	}
       	
        // draw landing pad
        g2.setColor(Color.RED);
        g2.fill(model.getLandingPad());
        
        // draw ship
        g2.setColor(Color.BLUE);
        g2.fill(new Rectangle2D.Double(model.ship.getPosition().x, model.ship.getPosition().y, 10, 10));
        g2.setTransform(M);  
    }

    @Override
    public void update(Observable o, Object arg) {
    	if (!model.ship.isPaused()){
			model.checkResult();
		}
    	
    	revalidate();
    	repaint();
    }
}
