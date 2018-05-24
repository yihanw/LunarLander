import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;

public class GameModel extends Observable {
	
	Rectangle2D.Double worldBounds;
    public Ship ship;
    private Rectangle2D.Double landingPad;
    
    //terrrain
    int[] xpoints = new int[22];
    int[] ypoints = new int[22];
	int[] circleX = new int[20];
	int[] circleY = new int[20];
	
	//undo redo
	private UndoManager undoManager;
	int oldValueX = 330;
	int oldValueY = 100;
	int newValueX = 330;
	int newValueY = 100;
	int lastOldValueX, lastOldValueY;
	
	//asteroids
	Rectangle2D.Double[] asteroids = new Rectangle2D.Double[10];

    public GameModel(int fps, int width, int height, int peaks) {
    	undoManager = new UndoManager();
        ship = new Ship(60, width/2, 50);
        worldBounds = new Rectangle2D.Double(0, 0, width, height);
        landingPad = new Rectangle2D.Double(330, 100, 40, 10);
        generateTerrain();
        generateAsteroids();
        
        // anonymous class to monitor ship updates
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                setChangedAndNotify();
            }
        });
    }
    
    //landing pad
    public Rectangle2D.Double getLandingPad(){
    	return landingPad;
    }
    
    public void setLandingPad(int x, int y){
    	if (x<0){
    		x = 0;
    	} else if (x>(700-40)){
    		x = 700-40;
    	}
    	if (y<0){
    		y = 0;
    	} else if (y>(200-10)){
    		y = 200-10;
    	}
    	landingPad.x = x;
    	landingPad.y = y;
    }
       
    public void setOldValue(double x, double y){
    	oldValueX = (int) x;
    	oldValueY = (int) y;
    }
    
    public void setNewValue(int x, int y){
    	if (x<0){
    		x = 0;
    	} else if (x>(700-40)){
    		x = 700-40;
    	}
    	if (y<0){
    		y = 0;
    	} else if (y>(200-10)){
    		y = 200-10;
    	}
    	newValueX = x;
    	newValueY = y;
    }
    
    public void landingPadUndoRedo(){
    	final int oldX = oldValueX;
    	final int oldY = oldValueY;
    	final int newX = newValueX;
    	final int newY = newValueY;
    	
    	UndoableEdit undoableEdit = new AbstractUndoableEdit() {
	    	
	    	public void redo() throws CannotRedoException {
	    		super.redo();
	    		landingPad.x = newX;
	    		landingPad.y = newY;
	    		setChangedAndNotify();
	    	}
	    	
	    	public void undo() throws CannotUndoException {
	    		super.undo();
	    		landingPad.x = oldX;
	    		landingPad.y = oldY;
	    		setChangedAndNotify();
	    	}
    	};
    	
    	undoManager.addEdit(undoableEdit);
    	setChangedAndNotify();
    }

    public boolean isHitted(int x, int y){
    	if ((x >= landingPad.x && x <= landingPad.x+40) &&
    			(y >= landingPad.y && y <= landingPad.y+10)){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    // World
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }
    
    // generate terrain
    public void generateTerrain(){
    	for(int i=0; i<20; i++){
    		xpoints[i] = (int)(i * (700.0/19.0));
    		ypoints[i] = (int)(Math.random() * (200-15-100+1) + 100);
    		circleX[i] = (int)(xpoints[i]-15);
    		circleY[i] = (int)(ypoints[i]-15);
    	}
    	xpoints[20] = 700;
    	ypoints[20] = 200;
    	xpoints[21] = 0;
    	ypoints[21] = 200;
    }
    
    // return -1 if no circle hitted, or return hitted circle index
    public int getCircleHittedIndex(int x, int y){
    	for(int i=0; i<20; i++){
    		double distance = Math.sqrt((Math.pow(Math.abs(x-(circleX[i]+15)), 2) + Math.pow(Math.abs(y-(circleY[i]+15)), 2)));
    		if (distance <= 15) {
    			return i;
    		}
    	}
    	return -1;
    }

    public Point2d getTerrainCircle(int i){
    	return (new Point2d(circleX[i], circleY[i]));
    }
    
    public void setTerrainY(int i, int y){
    	circleY[i] = y;
    	ypoints[i] = y+15;
    	if (y<0){
    		circleY[i] = 0;
    		ypoints[i] = 15;
    	} else if (y>(200-30)){
    		circleY[i] = 200-30;
    		ypoints[i] = 200-15;
    	}
    }
   
    public void terrainUndoRedo(int index){
    	final int oldY = oldValueY;
    	final int newY = newValueY;
    	
    	UndoableEdit undoableEdit = new AbstractUndoableEdit() {    	
	    	public void redo() throws CannotRedoException {
	    		super.redo();
	    		ypoints[index] = newY + 15;
	    		circleY[index] = newY;
	    		setChangedAndNotify();
	    	}
	    	
	    	public void undo() throws CannotUndoException {
	    		super.undo();
	    		ypoints[index] = oldY + 15;
	    		circleY[index] = oldY;
	    		setChangedAndNotify();
	    	}
    	};
    	
    	undoManager.addEdit(undoableEdit);
    	setChangedAndNotify();
    }
    
    // undo redo
    public void undo(){
    	if (canUndo()){
    		undoManager.undo();
    	}
    }
    
    public void redo() {
		if (canRedo())
			undoManager.redo();
	}
    
	public boolean canUndo() {
		return undoManager.canUndo();
	}
	
	public boolean canRedo() {
		return undoManager.canRedo();
	}
	
	
    // Ship
	// check if crashed or landed
	public void checkResult(){
		Rectangle2D.Double curShip = ship.getShape();
		Polygon terrain = new Polygon(xpoints, ypoints, 22);
		if (!worldBounds.contains(curShip) || terrain.intersects(curShip) ||
				(landingPad.intersects(curShip)) && (ship.getSpeed() >= ship.getSafeLandingSpeed())){
			ship.isCrashed = true;
			ship.stop();
		}
		for(int i=0; i<5; i++){
			if (asteroids[i].intersects(curShip)){
				ship.isCrashed = true;
				ship.stop();
			}
		}
		
		if ((landingPad.intersects(curShip)) && (ship.getSpeed() < ship.getSafeLandingSpeed())){
			ship.isLanded = true;
			ship.stop();
		}
	}
	
	// enhancement
	public void generateAsteroids(){
		for(int i=0; i<10; i++){
			double x = Math.random() * (700-0+1) + 0;
			double y = Math.random() * (100-0+1) + 0;
			Rectangle2D.Double asteroid = new Rectangle2D.Double(x, y, 10, 10);
			while (asteroid.intersects(landingPad)){
				x = Math.random() * (700-0+1) + 0;
				y = Math.random() * (100-0+1) + 0;
			}
			asteroids[i] = asteroid;
		}
	}
	
	public Rectangle2D.Double getAsteroid(int i){
		return asteroids[i];
	}
	
    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

}