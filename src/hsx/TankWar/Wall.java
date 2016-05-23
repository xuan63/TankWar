package hsx.TankWar;
import java.awt.*;

public class Wall {
	int x, y ,h, w;
	TankClient tc = null;
	
	public Wall(int x, int y, int w, int h, TankClient tc) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.tc = tc;
	}
	
	public void draw (Graphics g) {
		g.fillRect(x, y, w, h);
	}
	
	public Rectangle getRect(){
		return new Rectangle(x, y, w, h);
	}
	
}
