import javafx.scene.input.KeyEvent;

// The breakout controller receives KeyPress events from the GUI (via
// the KeyEventHandler). It maps the keys onto methods in the model and
// calls them appropriately
public class Controller
{
	public Model model;
	public View  view;
	
	private int leftHeld = 0;
	private int rightHeld = 0;
  
	// we don't really need a constructor method, but include one to print a 
	// debugging message if required
	public Controller() {
		Debug.trace("Controller::<constructor>");
	}
	
	// This is how the View talks to the Controller
	// AND how the Controller talks to the Model
	// This method is called by the View to respond to key presses in the GUI
	// The controller's job is to decide what to do. In this case it converts
	// the keypresses into commands which are run in the model
	public void userKeyInteraction(KeyEvent event ) {
	    Debug.trace("Controller::userKeyInteraction: keyCode = " + event.getCode() );
	      
	    switch (event.getCode()) {
	    	case LEFT:                       // Left Arrow
	    		if (leftHeld == 0 && event.getEventType() == event.KEY_PRESSED) {
	    			leftHeld = 1;
	    		}
	    		else if (event.getEventType() == event.KEY_RELEASED) {
	    			leftHeld = 0;
	    		}
		        break;
		    case RIGHT:                      // Right arrow
		    	if (rightHeld == 0 && event.getEventType() == event.KEY_PRESSED) {
	    			rightHeld = 1;
	    		}
	    		else if (event.getEventType() == event.KEY_RELEASED) {
	    			rightHeld = 0;
	    		}
		        break;
		    case F :
		        // Very fast ball movement
		        model.setFast(true);
		        break;
		    case N :
		        // Normal speed ball movement
		        model.setFast(false);
		        break;
		    case S :
		        // stop the game
		        model.setGameRunning(!model.getGameRunning());
		        break;
		    case ESCAPE :
		    	model.terminateGame();
		    	break;
		    default :
		    	break;
	    }
	}
	
	public int getDirection() {
		
		return rightHeld - leftHeld;
		
	}
}
