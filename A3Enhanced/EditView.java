import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
	
	private GameModel model;
	
	private boolean isDraggingLandingPad = false;
	private boolean isDraggingTerrain = false;
	int xOffset = 0;
	int yOffset = 0;
	int index = 0;
	
    public EditView(GameModel model) {
    	
    	setBackground(Color.BLACK);
    	this.model = model;
    	model.addObserver(this);
    	
    	addMouseListener(new MouseAdapter() { 
    		public void mouseClicked(MouseEvent e) { 
    			isDraggingLandingPad = false;
    			isDraggingTerrain = false;
    			if (e.getClickCount() == 2){
    				model.setOldValue(model.getLandingPad().x, model.getLandingPad().y);
    				model.setNewValue(e.getX() - 20, e.getY() - 5);
    				model.setLandingPad(e.getX() - 20, e.getY() - 5);
    				model.landingPadUndoRedo();
    				model.setChangedAndNotify();
    			}
			} 

    		public void mousePressed(MouseEvent e) {
    			index = model.getCircleHittedIndex(e.getX(), e.getY()); 
    				
    			// landing pad
    			if (model.isHitted(e.getX(), e.getY())){
    				isDraggingLandingPad = true;
    				isDraggingTerrain = false;
    				xOffset = (int) (e.getX() - model.getLandingPad().x);
    				yOffset = (int) (e.getY() - model.getLandingPad().y);
    				model.setOldValue(model.getLandingPad().x, model.getLandingPad().y);
    			} else  
    			
    			// terrain
    			if (index != -1){
    				isDraggingLandingPad = false;
    				isDraggingTerrain = true;
    				yOffset = e.getY() - (int)model.getTerrainCircle(index).y;
    				model.setOldValue(model.getTerrainCircle(index).x, model.getTerrainCircle(index).y);
    			}
    					
    		}
    		
    		public void mouseReleased(MouseEvent e){
    			// landding pad
    			if (isDraggingLandingPad){
    				model.setNewValue(e.getX() - xOffset, e.getY() - yOffset);
    				model.landingPadUndoRedo();
    			} else
    				
    			// terrain
    			if (isDraggingTerrain){
    				model.setNewValue((int)model.getTerrainCircle(index).x, e.getY()-yOffset);
    				model.terrainUndoRedo(index);
    			}
    			isDraggingLandingPad = false;
    			isDraggingTerrain = false;
    		}
		});
    	
    	addMouseMotionListener(new MouseAdapter() {
    		public void mouseDragged(MouseEvent e){
    			if (isDraggingTerrain){
    				model.setTerrainY(index, e.getY() - yOffset);
    				//repaint();
    				model.setChangedAndNotify();
    			} else 
    				
    			if (isDraggingLandingPad){
    				model.setLandingPad(e.getX() - xOffset, e.getY() - yOffset);
    				model.setChangedAndNotify();
    			}
    		}
    	});

    }
    
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // draw worldbound
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(model.worldBounds);

        // draw terrain
        g2.setColor(Color.DARK_GRAY);
       	g2.fillPolygon(model.xpoints, model.ypoints, 22);
       	
       	// draw terrain circles
       	g2.setColor(Color.GRAY);
       	for(int i=0; i<20; i++){
       		g2.drawOval(model.circleX[i], model.circleY[i], 30, 30);
       	}
       	if(isDraggingTerrain){
       		g2.setColor(Color.WHITE);
       		g2.setStroke(new BasicStroke(3));
       		g2.drawOval(model.circleX[index], model.circleY[index], 30, 30);
       	}
       	
       	// asteroids
       	g2.setColor(Color.DARK_GRAY);
       	for(int i=0; i<10; i++){
       		g2.fill(model.getAsteroid(i));
       	}
       	
        // draw landing pad
        g2.setColor(Color.RED);
        g2.fill(model.getLandingPad());
        if (isDraggingLandingPad){
        	g2.setColor(Color.WHITE);
       		g2.setStroke(new BasicStroke(3));
       		g2.drawRect((int)model.getLandingPad().x, (int)model.getLandingPad().y, 40, 10);
        }
        
    }

    @Override
    public void update(Observable o, Object arg) {
    	revalidate();
    	repaint();
    }

}
