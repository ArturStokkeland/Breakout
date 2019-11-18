import java.util.ArrayList;

import javafx.scene.paint.*;

// An object in the game, represented as a rectangle, with a position,
// a colour, and a direction of movement.
public class GameObj
{
    // state variables for a game object
    public int topX   = 0;              // Position - top left corner X
    public int topY   = 0;              // position - top left corner Y
    public int width  = 0;              // Width of object
    public int height = 0;              // Height of object
    public Color colour;                // Colour of object
    public int 	  speed  = 5;										// Speed of ball, used to calculate directional speed
    public int    initialAngle = (int) Math.floor((Math.random() * 160) - 80); // Makes the ball go a random direction on startup for variation
    public double speedX = speed * Math.sin(Math.toRadians(initialAngle));    // Units to move the ball on each step on the X axis
    public double speedY = speed * Math.cos(Math.toRadians(initialAngle)) * -1;    // Units to move the ball on each step on the Y axis

    public GameObj( int x, int y, int w, int h, Color c )
    {
        topX   = x;       
        topY = y;
        width  = w; 
        height = h; 
        colour = c;
    }   
    
 // move in x axis
    public void moveX( int units )
    {
        topX += units;
    }

    // move in y axis
    public void moveY( int units )
    {
        topY += units;
    }
    
    // change direction of movement in x axis (-1, 0 or +1)
    public void changeDirectionX()
    {
        speedX = -speedX;
    }

    // change direction of movement in y axis (-1, 0 or +1)
    public void changeDirectionY()
    {
        speedY = -speedY;
    }

    // Detect collision between this object and the argument object
    // It's easiest to work out if they do NOT overlap, and then
    // return the negative (with the ! at the beginning)
    public boolean hitBy( GameObj obj )
    {
        return ! ( topX >= obj.topX+obj.width     ||
            topX+width <= obj.topX         ||
            topY >= obj.topY+obj.height    ||
            topY+height <= obj.topY );

    }
    
    public void hitByBat(GameObj bat) {
    	double hitPosition = (topX + width / 2) - bat.topX - (bat.width / 2); // calculates which part of the bat the ball hit
    	if (hitPosition > 80) { hitPosition = 80; }
    	else if (hitPosition < -80) { hitPosition = -80; }
    	speedX = speed * Math.sin(Math.toRadians(hitPosition));
    	speedY = speed * Math.cos(Math.toRadians(hitPosition));
    	changeDirectionY();
    }
    
    public boolean edgeHit(int B, int M, int borderWidth, int borderHeight) {
    	// get the current ball possition (top left corner)
    	int x = topX;  
        int y = topY;
        // Deal with possible edge of board hit
        if (x >= borderWidth - B - width && speedX > 0)  changeDirectionX();
        if (x <= 0 + B && speedX < 0)  changeDirectionX();
        if (y >= borderHeight - B - width && speedY > 0)  // Bottom
        { 
        	changeDirectionY();
            return true; 
            //model.addToScore( HIT_BOTTOM );     // score penalty for hitting the bottom of the screen
        }
        if (y <= 0 + M && speedY < 0)  changeDirectionY();
        
        return false;
    }

}
