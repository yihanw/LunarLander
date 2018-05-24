import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class MessageView extends JPanel implements Observer {

	private GameModel model;
	
    // status messages for game
    JLabel fuel = new JLabel("fuel: 50.0");
    JLabel speed = new JLabel("speed: 0.00");
    JLabel message = new JLabel("(paused)");
    
    public MessageView(GameModel model) {
    	this.model = model;
    	model.addObserver(this);
    	
        // want the background to be black
        setBackground(Color.BLACK);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        add(fuel);
        add(speed);
        add(message);

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
        speed.setForeground(Color.GREEN);
    }


    @Override
    public void update(Observable o, Object arg) {
    	// fuel
    	if (model.ship.getFuel() <= 10){
    		fuel.setForeground(Color.RED);
    	} else {
    		fuel.setForeground(Color.WHITE);
    	}
    	fuel.setText("fuel: " + (int)model.ship.getFuel());
    	
    	// speed
    	DecimalFormat df = new DecimalFormat("#.##");
    	if (model.ship.getSpeed() <= model.ship.getSafeLandingSpeed()){
    		speed.setForeground(Color.GREEN);
    	} else {
    		speed.setForeground(Color.WHITE);
    	}
    	speed.setText("speed: " + df.format(model.ship.getSpeed()));
    	
    	// msg
    	//System.out.println(model.ship.isCrashed);
    	if (model.ship.isCrashed){
    		message.setText("CRASH");
    		speed.setText("speed: 0.00");
    	} else if (model.ship.isLanded){
    		message.setText("LANDED!");
    	} else if (model.ship.isPaused()){
    		message.setText("(paused)");
    	} else {
    		message.setText("");
    	}
    	
    }
}