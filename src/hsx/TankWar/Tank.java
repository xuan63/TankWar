package hsx.TankWar;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Tank {
	private int x, y;
	private int oldX, oldY;
	private static final int XSPEED = 5; //移动速度,不能被改变
	private static final int YSPEED = 5;
	
	public static final int WIDTH = 30;  //坦克的大小，宽、高
	public static final int HEIGHT = 30;

	private boolean bL=false, bU=false, bR=false, bD=false;
	enum Direction { L, LU, U, RU, R, RD, D, LD, STOP };
	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.D; //炮筒的方向,初始方向朝下
	private Random r = new Random(); //随机数产生器
	int step =  r.nextInt(20) + 3; //初始化，每个方向随机移动几步,3-14步之间
	
	TankClient tc = null ;
	private boolean good = true; //区分敌我,默认是好人
	private int life = 100;  //生命值
	boolean live=true;
	private BloodBar bb = new BloodBar();
	
	public boolean isGood() {
		return good;
	}
	
	public boolean isLive() {
		return live;
	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, Direction dir,TankClient tc) {
		this(x, y, good); 	//调用上一个构造方法
		this.dir = dir;
		this.tc = tc;
	}
	
	public void draw(Graphics g){
		if(!live){
			if(!good)
				tc.tanks.remove(this);
			return;
		}
 		Color c= g.getColor(); 		//先得到画笔颜色
		if(good ) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT ); 
		g.setColor(c); 			// 颜色画好了就把画笔颜色给人家还回去
		
		if(good) bb.draw(g);
		
		//改变炮筒的朝向
		switch(ptDir) {
		case L:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT/2);
			break;
		case LU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y);
			break;
		case U:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y);
			break;
		case RU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT/2);
			break;
		case RD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y+Tank.HEIGHT);
			break;
		case LD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT);
			break;
		}
		
		move();
	}
	
	void move() {
		
		this.oldX = x;
		this.oldY = y;
		
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
		
		if(this.dir != Direction.STOP) {
			this.ptDir = this.dir ;
		}
		
		//让坦克走不出游戏框
		if(x<0) x=0;
		if(y<30) y=30;
		if(x+Tank.WIDTH > TankClient.GAME_WIDTH)
			x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if(y+Tank.HEIGHT > TankClient.GAME_HEIGHT)
			y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
		
		//敌人坦克随机改变运动方向
		if(!good){
			Direction[] dirs = Direction.values();
			if(step == 0){			//如果剩余步数为0，就随机选个方向，随机选步数再移动
				step = r.nextInt(12)+3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}
			step--;
			if(r.nextInt(40)>36) this.fire();
		}
	}
	
	private void stay(){
		this.x = oldX;
		this.y = oldY;
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_F2:
			if(!this.live) {
				this.life = 100;
				this.live = true;
			}
			break;	
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		}
		locateDirection();
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_CONTROL:
			fire(); 
			break;
		case KeyEvent.VK_A:
			superFire();
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		}
		locateDirection();
	}
	
	void locateDirection(){
		if(bL && !bU && !bR && !bD) dir = Direction.L;
		else if(bL && bU && !bR && !bD) dir = Direction.LU;
		else if(!bL && bU && !bR && !bD) dir = Direction.U;
		else if(!bL && bU && bR && !bD) dir = Direction.RU;
		else if(!bL && !bU && bR && !bD) dir = Direction.R;
		else if(!bL && !bU && bR && bD) dir = Direction.RD;
		else if(!bL && !bU && !bR && bD) dir = Direction.D;
		else if(bL && !bU && !bR && bD) dir = Direction.LD;
		else if(!bL && !bU && !bR && !bD) dir = Direction.STOP;
	}

	private Missile fire() {
		if( !live ) return null;
		int x = this.x+(Tank.WIDTH-Missile.WIDTH)/2 ;
		int y = this.y+(Tank.HEIGHT-Missile.HEIGHT)/2 ;
		Missile m = new Missile( x, y, good, ptDir, this.tc);  //根据炮筒朝向new 子弹，可以解决根据dir朝向new子弹时，子弹不动的问题（因为dir=stop时，子弹的dir也是stop，而ptDir没有stop）
		tc.missiles.add(m);
		return m ;
	}
	
	private Missile fire(Direction dir) {
		int x = this.x+(Tank.WIDTH-Missile.WIDTH)/2 ;
		int y = this.y+(Tank.HEIGHT-Missile.HEIGHT)/2 ;
		Missile m = new Missile( x, y, good, dir, this.tc);  //根据炮筒朝向new 子弹，可以解决根据dir朝向new子弹时，子弹不动的问题（因为dir=stop时，子弹的dir也是stop，而ptDir没有stop）
		tc.missiles.add(m);
		return m ;
	}
	
	private void superFire() {
		if( !live ) return;
		Direction[] dirs = Direction.values();
		for (int i=0; i<8; i++) {
			fire(dirs[i]) ;
		}
	}
	
	public Rectangle getRect(){
		return new Rectangle(x, y, WIDTH,HEIGHT);
	}
	
	/**
	 * 坦克撞墙
	 * @param w 被撞的墙
	 * @return 装上了返回true，否则false
	 */
	public boolean collidesWithWall (Wall w) {
 		if(this.live && this.getRect().intersects(w.getRect())){
 			this.stay();
			return true;
		}
		return false;
	}
	
	public boolean collidesWithTanks (java.util.List<Tank> tanks) {
		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			if( this!=t ){
				if(this.live && t.isLive() && this.getRect().intersects(t.getRect()) ) {
					this.stay();
					t.stay();
					return true;
				}
			}
		}
		return false;
	}
	
	//内部类，血条
	private class BloodBar {
		void draw( Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(x, y-10, WIDTH, 10);
			int w = (WIDTH * life) / 100;
			g.fillRect(x, y-10, w, 10);
			g.setColor(c);
		}
	}
	
	public boolean eatBlood(Blood blood) {
		if(this.live && blood.isLive() && this.getRect().intersects(blood.getRect()) ){
			if(this.life < 100) this.life += 20;
			blood.setLive(false);
		}
		return false;
	}
	
}
