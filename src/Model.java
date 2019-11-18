import java.util.ArrayList;

import javafx.scene.control.RadioButton;
import javafx.scene.paint.*;
import javafx.application.Platform;

// The model represents all the actual content and functionality of the app
// For Breakout, it manages all the game objects that the View needs
// (the bat, ball, bricks, and the score), provides methods to allow the Controller
// to move the bat (and a couple of other fucntions - change the speed or stop 
// the game), and runs a background process (a 'thread') that moves the ball 
// every 20 milliseconds and checks for collisions 
public class Model 
{
    // First,a collection of useful values for calculating sizes and layouts etc.

    public int B              = 6;      // Border round the edge of the panel
    public int M              = 40;     // Height of menu bar space at the top
    
    public int PUpChance;
    public String difficulty;

    public int BALL_SIZE      = 30;     // Ball side
    public int EXTRA_BALL_SIZE = 15;
    private int POWER_UP_SIZE = 20;		// Power up side
    public int BRICK_WIDTH    = 50;     // Brick size
    public int BRICK_HEIGHT   = 30;		// Brick size

    public int BAT_MOVE;      			// Distance to move bat on each keypress

    public int HIT_BRICK      = 50;     // Score for hitting a brick
    public int HIT_BOTTOM     = -200;   // Score (penalty) for hitting the bottom of the screen

    // The other parts of the model-view-controller setup
    View view;
    Controller controller;

    // The game 'model' - these represent the state of the game
    // and are used by the View to display it
    public GameObj ball;                // The ball
    public ArrayList<GameObj> balls;    // Extra balls
    public ArrayList<GameObj> bricks;   // The bricks
    public ArrayList<PowerUp> PUps;		// Power Ups
    public GameObj bat;                 // The bat
    private int score = 0;               // The score

    // variables that control the game 
    public boolean gameRunning = true;  // Set false to stop the game
    public boolean gameFinished = false;
    public boolean fast = false;        // Set true to make the ball go faster
    public ArrayList<Integer> bricksHit;

    // initialisation parameters for the model
    public int width;                   // Width of game
    public int height;                  // Height of game

    // CONSTRUCTOR - needs to know how big the window will be
    public Model( int w, int h )
    {
        Debug.trace("Model::<constructor>");  
        width = w; 
        height = h;
    }

    // Initialise the game - reset the score and create the game objects 
    public void initialiseGame()
    {       
        score = 0;
        ball   = new GameObj(width/2 - BALL_SIZE / 2, height - BRICK_HEIGHT*3/2 - BALL_SIZE - BRICK_HEIGHT/4, BALL_SIZE, BALL_SIZE, Color.web("#4169E1") );
        bat    = new GameObj(width/2 - BRICK_WIDTH * 3 / 2, height - BRICK_HEIGHT*3/2, BRICK_WIDTH*3, 
            BRICK_HEIGHT/4, Color.web("#CCCCCC"));
        BAT_MOVE = (int) view.sBatSpeed.getValue();
        PUpChance = (int) view.sPUp.getValue();
        RadioButton selectedDiff = (RadioButton) view.diffGroup.getSelectedToggle();
        difficulty = selectedDiff.getText();
        System.out.println(BAT_MOVE);
        System.out.println(PUpChance);
        System.out.println(difficulty);
        balls = new ArrayList<>();
        PUps = new ArrayList<>();
        bricks = new ArrayList<>();
        bricksHit = new ArrayList<>();
        // *[1]******************************************************[1]*
        // * Fill in code to add the bricks to the arrayList            *
        // **************************************************************
        for (int j = 0; j < 11; j++) {
            for (int i = 0; i < 11; i++) {
                bricks.add(new GameObj(BRICK_WIDTH * i + 4 * (i + 1), 50 + j * (BRICK_HEIGHT + 4), BRICK_WIDTH, BRICK_HEIGHT, Color.web("#32CD32"))); // #FF0000 #FF8C00
            }
        }
    }

    // Animating the game
    // The game is animated by using a 'thread'. Threads allow the program to do 
    // two (or more) things at the same time. In this case the main program is
    // doing the usual thing (View waits for input, sends it to Controller,
    // Controller sends to Model, Model updates), but a second thread runs in 
    // a loop, updating the position of the ball, checking if it hits anything
    // (and changing direction if it does) and then telling the View the Model 
    // changed.
    
    // When we use more than one thread, we have to take care that they don't
    // interfere with each other (for example, one thread changing the value of 
    // a variable at the same time the other is reading it). We do this by 
    // SYNCHRONIZING methods. For any object, only one synchronized method can
    // be running at a time - if another thread tries to run the same or another
    // synchronized method on the same object, it will stop and wait for the
    // first one to finish.
    
    // Start the animation thread
    public void startGame()
    {
        
        Thread t = new Thread( this::runGame );     // create a thread runnng the runGame method
        t.setDaemon(true);                          // Tell system this thread can die when it finishes
        t.start();                                  // Start the thread running
    }   
    
    // The main animation loop
    
    public void runGame()
    {
        try
        {
            // set gameRunning true - game will stop if it is set false (eg from main thread)
            setGameRunning(true);
            setGameFinished(false);
            initialiseGame();
            while (!getGameFinished()) {
	            while (getGameRunning() && !getGameFinished())
	            {
	                updateGame();                        // update the game state
	                modelChanged();                      // Model changed - refresh screen
	                Thread.sleep( getFast() ? 10 : 20 ); // wait a few milliseconds
	            }
	            if (!getGameRunning()) { Thread.sleep( getFast() ? 10 : 20 ); }
            }
            
        } catch (Exception e) 
        { 
            Debug.error("Model::runAsSeparateThread error: " + e.getMessage() );
        }
    }
  
    // updating the game - this happens about 50 times a second to give the impression of movement
    public synchronized void updateGame()
    {
        if (bricks.size() == 0) {
        	setGameFinished(true);
        	Platform.runLater(new Runnable(){
				public void run() {
					view.endGame();					
				}
        	});
        }
        
    	moveBat();
        // move the ball one step (the ball knows which direction it is moving in)
        ball.moveX((int) Math.round(ball.speedX));                      
        ball.moveY((int) Math.round(ball.speedY));
        for (int i = 0; i < balls.size(); i++) {
        	GameObj thisBall = balls.get(i);
        	thisBall.moveX((int) Math.round(thisBall.speedX));                      
            thisBall.moveY((int) Math.round(thisBall.speedY));
        }
        
        for (int i = 0; i < PUps.size(); i++) {
        	PowerUp thisPUp = PUps.get(i);
            thisPUp.moveY(thisPUp.speed);
        }
                
        //if (ball.edgeHit(B, M, width, height)); {
        	//make another while loop in the gameloop, pause when lose life, and then run again after a second. isFinished -> lost life -> isRunning?
        	//loseLife();
        //}
        ball.edgeHit(B, M, width, height);
        
        for (int i = 0; i < balls.size(); i++) {
        	if (balls.get(i).edgeHit(B, M, width, height)) {
        		balls.remove(i);
        	}
        }

       // check whether ball has hit a (visible) brick

        // *[3]******************************************************[3]*
        // * Fill in code to check if a visible brick has been hit      *
        // * The ball has no effect on an invisible brick               *
        // * If a brick has been hit, change its 'visible' setting to   *
        // * false so that it will 'disappear'                          * 
        // **************************************************************
        ArrayList<GameObj> ballsToCheck = new ArrayList<GameObj>();
        ballsToCheck.add(ball);
        for (int i = 0; i < balls.size(); i++) {
        	ballsToCheck.add(balls.get(i));
        }
        boolean brickCheckDone = false;
        while (!brickCheckDone) {
        	brickCheckDone = checkBrickHit(ballsToCheck);
        }
        
        // check whether ball has hit the bat
        if (ball.hitBy(bat)) {
        	ball.hitByBat(bat);
        }
        
        for (int i = 0; i < balls.size(); i++) {
        	if (balls.get(i).hitBy(bat)) {
        		balls.get(i).hitByBat(bat);
        	}
        }
        
        for (int i = 0; i < PUps.size(); i++) {
        	if (PUps.get(i).hitBy(bat)) {
        		balls.add(new GameObj(PUps.get(i).topX + PUps.get(i).width / 2, height - BRICK_HEIGHT*3/2 - EXTRA_BALL_SIZE - BRICK_HEIGHT/4, EXTRA_BALL_SIZE, EXTRA_BALL_SIZE, Color.web("#4169E1")));
        		PUps.remove(i);
        	}
        }
    }
    
    public boolean checkBrickHit(ArrayList<GameObj> ballsToCheck) {
    	for (int i = 0; i < bricks.size(); i++) {
            for (int j = 0; j < ballsToCheck.size(); j++) {
            	if (ballsToCheck.get(j).hitBy(bricks.get(i))) {
            		hitBrick(ballsToCheck.get(j), i);
            		ballsToCheck.remove(j);
            		return false;
            	}
            }
        }
    	return true;
    }
    
    public void hitBrick(GameObj thisBall, int i) {
    	thisBall.changeDirectionY();
    	GameObj theBrick = bricks.get(i);
        int drop = (int) Math.ceil((Math.random() * 100));
        if (drop <= PUpChance) {
        	PUps.add(new PowerUp(theBrick.topX + theBrick.width / 2, theBrick.topY + theBrick.height / 2, POWER_UP_SIZE, POWER_UP_SIZE, Color.RED));
        }
        bricks.remove(i);
        addToScore(HIT_BRICK);
    }

    // This is how the Model talks to the View
    // Whenever the Model changes, this method calls the update method in
    // the View. It needs to run in the JavaFX event thread, and Platform.runLater 
    // is a utility that makes sure this happens even if called from the
    // runGame thread
    public synchronized void modelChanged() {
        Platform.runLater(view::update);
    }
    
    
    // Methods for accessing and updating values
    // these are all synchronized so that the can be called by the main thread 
    // or the animation thread safely
    
    // Change game running state - set to false to stop the game
    public synchronized void setGameRunning(Boolean value)
    {  
        gameRunning = value;
    }
    
    // Return game running state
    public synchronized Boolean getGameRunning()
    {  
        return gameRunning;
    }
    
    public synchronized void setGameFinished(Boolean value)
    {  
        gameFinished = value;
    }
    
 // Return game running state
    public synchronized Boolean getGameFinished()
    {  
        return gameFinished;
    }

    // Change game speed - false is normal speed, true is fast
    public synchronized void setFast(Boolean value)
    {  
        fast = value;
    }
    
    // Return game speed - false is normal speed, true is fast
    public synchronized Boolean getFast()
    {  
        return(fast);
    }

    // Return bat object
    public synchronized GameObj getBat()
    {
        return(bat);
    }
    
    // return ball object
    public synchronized GameObj getBall()
    {
        return(ball);
    }
    
    public synchronized ArrayList<GameObj> getBalls()
    {
        return(balls);
    }
    
    // return bricks
    public synchronized ArrayList<GameObj> getBricks()
    {
        return(bricks);
    }
    
    public synchronized ArrayList<PowerUp> getPUps()
    {
        return(PUps);
    }
    
    // return score
    public synchronized int getScore()
    {
        return(score);
    }
    
     // update the score
    public synchronized void addToScore(int n)    
    {
        score += n;        
    }
    
    public synchronized void terminateGame() {
    		bricks.clear();
    }
    
    // move the bat one step - -1 is left, +1 is right
    public synchronized void moveBat()
    {        
        int dist = controller.getDirection() * BAT_MOVE;    									// Actual distance to move
        if (bat.topX + dist < 0) { dist = bat.topX; }											// Stop bat on left edge of screen
        else if (bat.topX + bat.width + dist > width) { dist = width - bat.topX - bat.width; }	// Stop bat on right edge of screen
        Debug.trace( "Model::moveBat: Move bat = " + dist );
        bat.moveX(dist);
    }
}   
    