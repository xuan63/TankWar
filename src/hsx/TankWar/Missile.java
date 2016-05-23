package hsx.TankWar;
import java.awt.*;
import java.util.List;

public class Missile {
	public static final int XSPEED=10;
	public static final int YSPEED=10;
	
	public static final int WIDTH = 10; //�ӵ��Ĵ�С,����
	public static final int HEIGHT = 10;
	
	int x, y;
	Tank.Direction dir;
	TankClient tc = null;
	private boolean live = true; //�ӵ��Ƿ����
	private boolean good = true; //���˵��ӵ������ǻ��˵�
	
	public Missile(int x, int y, Tank.Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public Missile(int x, int y, boolean good, Tank.Direction dir, TankClient tc){
		this(x, y , dir);
		this.good = good;
		this.tc = tc;
	}
	
	public void draw(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, 10, 10);
		g.setColor(c);
		
		move();
	}
	
	void move() {
		if( !live ){
			tc.missiles.remove(this);
			return;
		}
		switch(dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += XSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		case STOP:
			break;
		}	
		
		if( x<0 || y<0 || x>TankClient.GAME_WIDTH || y> TankClient.GAME_HEIGHT ){
			live = false;
		}
	}

	public boolean isLive() {
		return live;
	}
	
	public Rectangle getRect(){
		return new Rectangle(x, y, WIDTH,HEIGHT);
	}
	
	public boolean hitTank (Tank t) {
 		if(this.live && this.getRect().intersects(t.getRect()) && t.live && this.good!=t.isGood()){
 			if(t.isGood()){
 				t.setLife(t.getLife() - 20);
 				if(t.getLife() <= 0) t.setLive(false);
 			} else {
 				t.setLive(false);
 			}
			this.live=false;
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}
	
	public boolean hitTanks (List<Tank> tanks) {
		for( int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			if( hitTank(t) ){
				return true;
			}
		}
		return false;
	}
	
	public boolean hitWall (Wall w) {
 		if(this.live && this.getRect().intersects(w.getRect())){
 			this.live = false;
			return true;
		}
		return false;
	}
	
}
