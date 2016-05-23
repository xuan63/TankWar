package hsx.TankWar;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类是游戏的主窗口
 * @author Huang Shixuan
 *
 */

public class TankClient extends Frame {	
	public static final int GAME_WIDTH=800;
	public static final int GAME_HEIGHT=600;
	
	Tank myTank = new Tank(700, 500, true, Tank.Direction.STOP, this);
	Wall w1 = new Wall(300, 200, 20, 150, this), w2 = new Wall(500, 100, 150, 20, this);
	Blood blood = new Blood(100, 100, this);
	
	List<Tank> tanks = new ArrayList<Tank>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();
	
	Image offScreenImage = null; 
	
	@Override
	public void paint(Graphics g){
		g.drawString("missiles count: " + missiles.size(), 10, 35); //还留有几发炮弹在游戏框中
		g.drawString("explodes count: " + explodes.size(), 10, 50);
		g.drawString("tanks    count: " + tanks.size(), 10, 65);
		g.drawString("myTank    life: " + myTank.getLife(), 10, 80);
		
		if(tanks.size() == 0){
			for(int i=0; i<10; i++){
				tanks.add(new Tank( 50+40*(i+1), 50, false, Tank.Direction.D, this));
			}
		}
		
		for (int i=0; i<missiles.size(); i++ ) {
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(myTank);
			m.hitWall(w1);
			m.hitWall(w2);
			//if( !m.isLive() ) missiles.remove(m); //若这个子弹没活着
			m.draw(g); //画子弹
		}
		
		for (int i=0; i<explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.collidesWithWall(w1);
			t.collidesWithWall(w2);
			t.collidesWithTanks(tanks);
			t.draw(g);
		}
	
		myTank.draw(g);
		myTank.collidesWithWall(w1);
		myTank.collidesWithWall(w2);
		myTank.collidesWithTanks(tanks);
		myTank.eatBlood(blood);
		w1.draw(g);
		w2.draw(g);
		blood.draw(g);
	}
	
	@Override
	public void update(Graphics g) {
		if( offScreenImage == null) {
			offScreenImage = this.createImage( GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT); //用gOffScreen这支画笔画出一个实心的rectangle
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);	//用g这支画笔把offScreenImage画出来
	}
	
	/**
	 * 本方法显示游戏窗口
	 */
	
	public void lauchFrame(){
		
		for(int i=0; i<10; i++){
			tanks.add(new Tank( 50+40*(i+1), 50, false, Tank.Direction.D, this));
		}
		
		this.setTitle("TankWar");
		this.setBackground(new Color(0,168,0)); //绿色
		this.setSize(GAME_WIDTH,GAME_HEIGHT);
		this.setLocation(400,300);
		this.setVisible(true);
		this.setResizable(false);
		//匿名类,监听退出事件
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		this.addKeyListener(new KeyMonitor());
		
		new Thread( new PaintThread() ).start();
	}
	
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}
	
	private class PaintThread implements Runnable{
		@Override
		public void run(){
			while(true){
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
	}

}