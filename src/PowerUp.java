import javafx.scene.paint.Color;

public class PowerUp extends GameObj {
	
	public PowerUp(int x, int y, int w, int h, Color c) {
		super(x, y, w, h, c);
		speed = 4;
	}
}
