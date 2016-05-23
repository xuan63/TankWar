package hsx.TankWar;
import java.awt.*;
import java.util.Random;


public class Blood {
	int x, y, w, h;
	TankClient tc = null;
	private Random r = new Random(); //随机数产生器
	private boolean live = true ;
	
	public Blood(int x, int y, TankClient tc) {
			this.x = x;
			this.y = y;
			w = h = 15;
			this.tc = tc;
		}
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public void draw(Graphics g){
		if( live ) {
			Color c = g.getColor();
			g.setColor(Color.MAGENTA);
			g.fillRect(x, y, w, h);
			g.setColor(c);
		} else {
			if(!live && r.nextInt(100)<2) {
				reProduce(); //再生产
				while ( this.getRect().intersects(tc.w1.getRect()) && this.getRect().intersects(tc.w2.getRect())){
					reProduce();
				}
			}
		}
	}
	
	public void reProduce(){
		x = r.nextInt(TankClient.GAME_WIDTH-50);
		y = r.nextInt(TankClient.GAME_HEIGHT-50);
		live = true ;
	}
	
	public Rectangle getRect(){
		return new Rectangle(x, y, w, h);
	}
	
	
}
