//SPAGHETTI INTENSIFIES
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.geom.AffineTransform;
import javax.sound.sampled.*;
import java.awt.Toolkit;

public class Main
{
	static JFrame j; //this is definitely not going to cause a lot of problems
	static boolean end;
	
	public static void main(String...args)
	{
		Sprite.init();
		Level.init();
		Sound.play();

		j = new JFrame();  //JFrame is the window; window is a depricated class
		MyPanel4 m = new MyPanel4();
		j.setSize(m.getSize());
		j.add(m); //adds the panel to the frame so that the picture will be drawn
			      //use setContentPane() sometimes works better then just add b/c of greater efficiency.

		j.setVisible(true); //allows the frame to be shown.
		j.setResizable(false);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the dialog box exit when you click the "x" button.
		j.setLocationRelativeTo(null);
	}

}

class MyPanel4 extends JPanel implements ActionListener,KeyListener, MouseListener  // extends JPanel to support the swing framework
{
	private Timer time;
	
	MyPanel4()
	{
		time = new Timer(10, this); //sets delay to 10 millis and calls the actionPerformed of this class.
		setSize(1200,600);
		setVisible(true); //it's like calling the repaint method.
		time.start();

		addMouseListener(this);
		setFocusable(true);
		addKeyListener(this);
	}


	
	Player player = new Player();
	
	public void paintComponent(Graphics g) //render everything
	{
		g.setColor(Level.levels.get(Level.level).color);
		g.fillRect(0,0,1200,600);
		
		Level current = Level.levels.get(Level.level);
		for (Prop i : current.props) {
			switch (i.type) { //kill me
				case ("oak"): i.oak(g); break;
				case ("spruce"): i.spruce(g); break;
				case ("darkOak"): i.darkOak(g); break;
			}
		}
		
		for (int i=0; i<Projectile.projectiles.size(); i++) {
			Projectile p = Projectile.projectiles.get(i);
			p.update();
			p.render(g);
			if (Math.hypot(p.pos[0]-600,p.pos[1]-300) > 1000 || p.life < -100) {
				Projectile.projectiles.remove(i);
				i--;
			}
			if ((p.type.equals("magic") || p.type.equals("laser") || p.type.equals("sword")) && player.rect.contains(new Point((int)p.pos[0],(int)p.pos[1]))) {
				Projectile.projectiles.remove(i);
				i--;
				player.health--;
				player.hurtTimer = 10;
			}
		}
		
		//g.fillRect((int)player.rect.getX(),(int)player.rect.getY(),(int)player.rect.getWidth(),(int)player.rect.getHeight());
		//g.fillRect((int)player.swordRect.getX(),(int)player.swordRect.getY(),(int)player.swordRect.getWidth(),(int)player.swordRect.getHeight());
		
		player.update();
		player.bar.percent = (int)(player.health*5);
		player.render(g);
		
		for (int i=0; i<current.jimmies.size(); i++) {
			Jimmy jimmy = current.jimmies.get(i);
			jimmy.update(player);
			jimmy.render(g);
			jimmy.checkHurt(player);
			if (jimmy.health <= 0) {
				current.jimmies.remove(i);
				i--;
			}
			jimmy.bump(current.jimmies); //it has to go after the other methods i dont know why
		}
		
		player.bar.render(g);
		g.drawImage(Sprite.heart,60,500-8,null);
		g.setColor(Color.RED);
		g.drawString("Level " + (Level.level+1) + "/20",10,550);
		if (Level.level == 0) {
			g.drawString("WASD to move - Q and E or spacebar to attack - Left mouse to shoot",100,20);
		}
		if (Level.level == 1) {
			g.drawString("Kill the Jimmy",100,20);
			g.drawString("Move to the right side of the screen once all jimmies are dead to advance",700,500);
			g.drawString("R also instantly heals", 600, 400);
		}
		if (Level.level == 18 && Level.levels.get(Level.level).jimmies.size() == 0) {
			g.setColor(Color.WHITE);
			g.drawString("Good luck",600,300);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyCode() == KeyEvent.VK_R) {
			player.health = 20;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_W) {
			player.wasd[0] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			player.wasd[1] = true;
			player.direction = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			player.wasd[2] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			player.wasd[3] = true;
			player.direction = true;
		}
		
		//attacks
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (player.attackTimer == 0) {
				player.attacking = true;
				player.attackTimer = 1;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			if (player.attackTimer == 0) {
				player.attacking = true;
				player.attackTimer = 1;
				player.direction = false;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			if (player.attackTimer == 0) {
				player.attacking = true;
				player.attackTimer = 1;
				player.direction = true;
			}
		}
		
		//correction for when the player turns to attack with q/e
		if (!player.attacking) {
			if (player.wasd[1]) {
				player.direction = false;
			}
			if (player.wasd[3]) {
				player.direction = true;
			}
		}
		

	}
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			player.wasd[0] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			player.wasd[1] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			player.wasd[2] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			player.wasd[3] = false;
		}
	}
	
	public void keyTyped(KeyEvent e){}
	
	public void mousePressed(MouseEvent e) {
		if (player.shootTimer == 0) {
			player.shoot();
		}
	}
	
	
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
}


class Player
{
	int x, y;
	int speed = 5;
	double health = 20;
	boolean direction = true;
	int attackTimer;
	int shootTimer;
	int hurtTimer;
	boolean attacking = false;
	Rectangle rect = new Rectangle(x,y,20,60);
	Rectangle swordRect = new Rectangle(x,y,40,20);
	
	HealthBar bar = new HealthBar(100,500,200,5,100,1);
	
	boolean[] wasd = new boolean[4];
	int[][] vectors = new int[][]{{0,-speed},{-speed,0},{0,speed},{speed,0}};
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//look at how much code is for the stick figure
		
		//draw body
		if (hurtTimer != 0) {
			g2.setColor(new Color(100,0,0));
		}
		else if (Level.levels.get(Level.level).color.equals(new Color(0,0,0))) {
			g2.setColor(Color.WHITE);
		}
		else {
			g2.setColor(Color.BLACK);
		}
		g2.fillOval(x,y,20,20);
		g2.setStroke(new BasicStroke(3));
		g2.drawLine(x+10,y+10,x+10,y+45);
		g2.drawLine(x+10,y+45,x,y+60);
		g2.drawLine(x+10,y+45,x+20,y+60);
		
		//draw arms
		int handx;
		if (direction) {
			handx = x+25;
			g2.drawLine(x+10,y+25,x+25,y+40);
			g2.drawLine(x+10,y+35,x+25,y+40);
		}
		else {
			handx = x-5;
			g2.drawLine(x+10,y+25,x-5,y+40);
			g2.drawLine(x+10,y+35,x-5,y+40);
		}
		
		//draw sword
		//i died doing this
		g2.setColor(new Color(150,100,50));
		if (direction && !attacking) {
			g2.drawLine(handx, y+40, handx+20, y+10);
		}
		if (direction && attacking) {
			g2.drawLine(handx, y+40, handx+35, y+31);
		}
		if (!direction && !attacking) {
			g2.drawLine(handx, y+40, handx-20, y+10);
		}
		if (!direction && attacking) {
			g2.drawLine(handx, y+40, handx-35, y+31);
		}
	}
	
	public void update() {
		
		//advance levels
		if (Level.levels.get(Level.level).jimmies.size() == 0) {
			if (x>1200-35) {
				Level.level++;
				if (Level.levels.size() <= Level.level) {
					System.exit(0);
					System.out.println("congratulations you beat the game");
				}
				x = 0;
				health = 20;
				Projectile.projectiles = new ArrayList<Projectile>();
			}
		}
		
		if (health <= 0) {
			System.exit(0);
			System.out.println("thou hast perished");
		}
		
		for (int i=0; i<4; i++) {
			if (wasd[i]) {
				x += vectors[i][0];
				y += vectors[i][1];
				if (x < 0 || x > 1200-25 || y < 0 || y > 600-90) {
					//reverse movement to prevent going out of bounds
					x -= vectors[i][0];
					y -= vectors[i][1];
				}
			}
		}
		
		//correction for when the player turns to attack with q/e
		if (!attacking) {
			if (wasd[1]) {
				direction = false;
			}
			if (wasd[3]) {
				direction = true;
			}
		}
		
		rect.setLocation(x,y);
		if (direction) {
			swordRect.setLocation(x+20,y+20);
		}
		else {
			swordRect.setLocation(x-40,y+20);
		}
		
		//attack cycle (in frames): 0 = resting, 1-20 = attacking, 21-30 = on cooldown
		if (attackTimer > 0) {
			attackTimer++;
			if (attackTimer > 20) {
				attacking = false;
			}
			if (attackTimer > 30) {
				attackTimer = 0;
			}
		}
		
		if (shootTimer > 0) {
			shootTimer++;
			if (shootTimer > 40) {
				shootTimer = 0;
			}
		}
		
		//hurt animation
		if (hurtTimer > 0) {
			hurtTimer--;
		}
	}
	
	public void shoot() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		Point w = Main.j.getLocationOnScreen(); //i hate jpanel
		double[] pos = new double[]{rect.getCenterX(), rect.getCenterY()};
		
		double angle = Math.random()*0.1-0.05+Math.atan2(p.getX()-w.getX()-pos[0],p.getY()-w.getY()-pos[1]-25);
		double[] vel = new double[]{(Math.sin(angle)*10),(Math.cos(angle)*10)};
		Projectile a = new Projectile(pos,vel,new double[]{0,0},"arrow");
		Projectile.projectiles.add(a);
		shootTimer = 1;
	}
}



class Prop
{
	String type;
	int x;
	int y;
	
	public Prop(int a, int b, String t) {
		x = a;
		y = b;
		type = t;
	}
	
	public void oak(Graphics g) {
		g.setColor(new Color(150,100,50));
		g.fillRect(x,y,10,60);
		g.setColor(new Color(75,150,75));
		g.fillOval(x-30,y-30,70,50);
	}
	
	public void darkOak(Graphics g) {
		g.setColor(new Color(75,25,0));
		g.fillRect(x,y,10,60);
		g.setColor(new Color(0,100,0));
		g.fillOval(x-30,y-30,70,50);
	}
	
	public void spruce(Graphics g) {
		g.setColor(new Color(150,50,50));
		g.fillRect(x,y,10,80);
		g.setColor(new Color(0,75,25));
		g.fillPolygon(new int[]{x-15,x+25,x+5},new int[]{y+50,y+50,y-20},3);
	}
}



class Jimmy
{
	int health, maxHealth;
	double x, y;
	int cooldown;
	double angle;
	int hurtTimer;
	double knockback;
	
	Image image, normalImage, hurtImage;
	Rectangle rect;
	HealthBar bar;
	
	//no way an actual use for polymorphism
	public Jimmy() {
	}
	public void update(Player p) {
	}
	public void render(Graphics g) {
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/maxHealth*100);
		bar.render(g);
	}
	public void checkHurt(Player p) {
		boolean hurt = false;
		
		if (hurtTimer > 0) { //damage ticking: after the jimmy is hurt it cant be hurt again for 20 frames to prevent weird stuff
			hurtTimer--;
			if (hurtTimer <= 0) {
				image = normalImage;
			}
		}
		for (Projectile i : Projectile.projectiles) {
			if (i.type.equals("arrow") && i.life > 0 && rect.contains(new Point((int)i.pos[0],(int)i.pos[1]))) {
				
				if (hurtTimer == 0) {
					hurt = true;
					x += 4*i.vel[0]*knockback;
					y += 4*i.vel[1]*knockback;
					rect.setLocation((int)x,(int)y);
				}
				//only apply damage and knockback if the jimmy can be hurt, but the arrow needs to be deleted always
				//it looks weird to have the arrow go through
				
				i.pos = new double[]{9999,9999}; //this just teleports the arrow really far away which is easier than deleting it
				//the arrow will just delete itself after
			}
		}
		
		if (hurtTimer == 0 && p.attacking && rect.intersects(p.swordRect)) {
			if (p.direction) {
				x += 30*knockback;
			}
			else {
				x -= 30*knockback;
			}
			hurt = true;
		}
		
		if (hurt) {
			health--;
			hurtTimer = 20; //20 frames is also the length of the melee attack animation
			image = hurtImage;
			Sound.play();
		}
	}
	public void bump(ArrayList<Jimmy> jimmies) { //this stops the jimmies from going into each other and becoming indistinct
		for (Jimmy i : jimmies) {
			if (rect.intersects(i.rect) && x != i.x && y != i.y) { //the second and third conditions are to make it not check for itself
				double angle = Math.atan2(rect.getCenterX()-i.rect.getCenterX(),rect.getCenterY()-i.rect.getCenterY());
				x += Math.sin(angle)*knockback;
				y += Math.cos(angle)*knockback;
				i.x -= Math.sin(angle)*i.knockback;
				i.y -= Math.cos(angle)*i.knockback;
			}
		}
	}
	
}


class MeleeJimmy extends Jimmy
{
	public MeleeJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth = 4;
		knockback = 1;
		normalImage = Sprite.jimmyNormal;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65); //45 and 65 are the dimensions of the image
		bar = new HealthBar((int)x,(int)y-10,45,3,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		if (dist > 25 && dist < 500) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += 2*Math.sin(angle);
			y += 2*Math.cos(angle);
		}
		
			rect.setLocation((int)x,(int)y);
			bar.x = (int)x;
			bar.y = (int)y-10;
		
		if (rect.intersects(p.rect)) {
			cooldown++;
			if (cooldown >= 50) { //after being in contact with the player for 50 frames the melee jimmy will attack
				p.hurtTimer = 10;
				p.health--;
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
	}
}


class BigMeleeJimmy extends MeleeJimmy
{
	public BigMeleeJimmy(double a, double b) {
		super(a,b);
		health = maxHealth = 15;
		knockback = 0.5;
		normalImage = Sprite.jimmyBig;
		hurtImage = Sprite.jimmyBigHurt;
		image = normalImage; //82 x 121
		rect = new Rectangle((int)x,(int)y,82,121);
		bar = new HealthBar((int)x,(int)y-10,82,4,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		if (dist > 40 && dist < 800) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += 3*Math.sin(angle);
			y += 3*Math.cos(angle);
		}
		
			rect.setLocation((int)x,(int)y);
			bar.x = (int)x;
			bar.y = (int)y-10;
		
		if (rect.intersects(p.rect)) {
			cooldown++;
			if (cooldown >= 30) {
				p.hurtTimer = 10;
				p.health -= 2;
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
	}
}


class LightningJimmy extends Jimmy
{
	ArrayList<Point> lightningPoints = new ArrayList<Point>();
	
	public LightningJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth= 4;
		knockback = 1;
		normalImage = Sprite.jimmyGreen;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-10,45,3,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		
		if (dist > 300 && cooldown < 100) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += Math.sin(angle);
			y += Math.cos(angle);
		}
			rect.setLocation((int)x,(int)y);
			bar.x = (int)x;
			bar.y = (int)y-10;
		
		if (dist < 400) {
			cooldown++;
			if (cooldown == 100) {
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (cooldown > 100) {
				//code for lightning attack
				lightningPoints = new ArrayList<Point>();
				int tX = (int) rect.getCenterX();
				int tY = (int) rect.getCenterY();
				for (int i=0; i<10; i++) {
					lightningPoints.add(new Point(tX,tY));
					tX += (int)(40*Math.sin(angle));
					tY += (int)(40*Math.cos(angle));
				}
				for (int i=0; i<lightningPoints.size(); i++) {
					lightningPoints.get(i).translate((int)(Math.random()*30-30),(int)(Math.random()*30-30));
					if (p.rect.contains(lightningPoints.get(i))) {
						p.hurtTimer = 10;
						p.health -= 0.03;
					}
				}
			}
			if (cooldown >= 200) {
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
		if (cooldown == 0) {
			lightningPoints = new ArrayList<Point>();
		}
	}
	
	public void render(Graphics g) {
		//render lightning
		for (int i=0; i<lightningPoints.size()-1; i++) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,255,0));
			g2.setStroke(new BasicStroke(2));
			Point p1 = lightningPoints.get(i);
			Point p2 = lightningPoints.get(i+1);
			g2.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
		}
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/4*100);
		bar.render(g);
	}
}


class LaserJimmy extends Jimmy
{
	public LaserJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth = 4;
		knockback = 1;
		normalImage = Sprite.jimmyBlue;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-10,45,3,100,1);
	}
	
	public void update(Player p) {
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		
		if (dist > 300) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += Math.sin(angle);
			y += Math.cos(angle);
		}
		rect.setLocation((int)x,(int)y);
		bar.x = (int)x;
		bar.y = (int)y-10;
		
		if (dist < 800) {
			cooldown++;
			if (cooldown > 100) {
				shoot(p);
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
	}
	
	public void shoot(Player p) {
		angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		Projectile laser = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*20,Math.cos(angle)*20},
			new double[]{0,0},
			"laser"
		);
		Projectile.projectiles.add(laser);
	}
}


class TreeJimmy extends Jimmy
{
	public TreeJimmy(double a, double b) {
		cooldown = 500;
		x = a;
		y = b;
		health = maxHealth = 25;
		knockback = 0;
		normalImage = Sprite.jimmyTree;
		hurtImage = Sprite.jimmyTreeHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,150,150);
		bar = new HealthBar((int)x,(int)y-10,300,5,100,1);
	}
	public void update(Player p) { //still needs player parameter to override even though it is unused
		cooldown++;
		if (cooldown >= 1000) {
			Level.levels.get(Level.level).jimmies.add(new LightningJimmy(Math.random()*1000+100,Math.random()*500+50));
			for (int i : new int[3]) {
				Level.levels.get(Level.level).jimmies.add(new MeleeJimmy(Math.random()*1000+100,Math.random()*500+50));
			}
			cooldown = 0;
		}
		rect.setLocation((int)(x+75),(int)(y+150));
	}
}


class LaserBossJimmy extends Jimmy
{
	Image[][] rings;
	int[] ringRot;
	int[] dimensions = {40,80,120};
	
	public LaserBossJimmy(double a, double b) {
		x = a;
		y = b;
		health = maxHealth = 15;
		knockback = 0.5;
		normalImage = Sprite.jimmyBlue;
		hurtImage = Sprite.jimmyHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-50,100,4,100,1);
		rings = Sprite.rings;
		ringRot = new int[3];
	}
	public void update(Player p) {
		ringRot[0] += 7;
		ringRot[1] += 5;
		ringRot[2] += 3;
		
		double dist = Math.hypot(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		
		if (dist > 300) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			x += Math.sin(angle);
			y += Math.cos(angle);
		}
		rect.setLocation((int)x,(int)y);
		bar.x = (int)x-27;
		bar.y = (int)y-10;
		
		if (dist < 800) {
			cooldown++;
			if (cooldown > 35) {
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
				Projectile laser = new Projectile(
					new double[]{rect.getCenterX(),rect.getCenterY()},
					new double[]{Math.sin(angle)*20,Math.cos(angle)*20},
					new double[]{0,0},
					"laser"
				);
				Projectile.projectiles.add(laser);
				cooldown = 0;
			}
		}
		else {
			cooldown = 0;
		}
		
		if (dist < 100) {
			p.health -= 0.05;
			p.hurtTimer = 10;
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (int i=0; i<3; i++) {
			AffineTransform a = new AffineTransform();
			a.rotate(Math.toRadians(ringRot[i]),(int)(rect.getCenterX()),(int)(rect.getCenterY()));
			g2.setTransform(a);
			g.drawImage(rings[0][i],(int)(rect.getCenterX()-dimensions[i]),(int)(rect.getCenterY()-dimensions[i]),null);
			g2.setTransform(new AffineTransform());
		}
		
		g.drawImage(image,(int)x,(int)y,null);
		
		for (int i=0; i<3; i++) {
			AffineTransform a = new AffineTransform();
			a.rotate(Math.toRadians(ringRot[i]),(int)(rect.getCenterX()),(int)(rect.getCenterY()));
			g2.setTransform(a);
			g.drawImage(rings[1][i],(int)(rect.getCenterX()-dimensions[i]),(int)(rect.getCenterY()-dimensions[i]),null);
			g2.setTransform(new AffineTransform());
		}
		
		bar.percent = (int)((double)health/maxHealth*100);
		bar.render(g);
	}
}


class MagicJimmy extends LaserJimmy //MagicJimmy is basically the laser jimmy but shoots a different projectile
{
	public MagicJimmy(double a, double b) {
		super(a,b);
		normalImage = Sprite.jimmyWhite;
		image = normalImage;
	}
	
	public void shoot(Player p) {
		angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
		Projectile magic = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*5,Math.cos(angle)*5},
			new double[]{0,0},
			"magic"
		);
		Projectile.projectiles.add(magic);
	}
}


class Boss extends Jimmy
{
	int currentAttack;
	int previousAttack;
	ArrayList<Point> lightningPoints = new ArrayList<Point>();
	ArrayList<Point> swords = new ArrayList<Point>();
	Point[] swordPoints = new Point[5];
	int wings;
	
	public Boss(double a, double b) {
		x = a;
		y = b;
		health = maxHealth = 35;
		knockback = 0;
		normalImage = Sprite.boss;
		hurtImage = Sprite.bossHurt;
		image = normalImage;
		rect = new Rectangle((int)x,(int)y,45,65);
		bar = new HealthBar((int)x,(int)y-50,100,4,100,1);
	}
	
	public void teleport() {
		x = Math.random()*1000+100;
		y = Math.random()*450+50;
		rect.setLocation((int)x,(int)y);
	}
	
	public void update(Player p) {
		if (cooldown == -150) { //cooldown
			currentAttack = (int)(Math.random()*4);
			if (currentAttack == previousAttack) {
				currentAttack = (int)(Math.random()*4); //smaller chance of using the same attack twice
			}
			previousAttack = currentAttack;
			
			teleport();
			if (currentAttack == 0) {
				cooldown = 200;
			}
			if (currentAttack == 1) {
				cooldown = 100;
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (currentAttack == 2) {
				cooldown = 200;
				int tX = (int) rect.getCenterX();
				int tY = (int) rect.getCenterY();
				swordPoints = new Point[]{
					new Point(tX-50,tY-50),new Point(tX-25,tY-60),new Point(tX,tY-65),new Point(tX+25,tY-60),new Point(tX+50,tY-50)
				};
			}
			if (currentAttack == 3) {
				cooldown = 400;
			}
		}
		if (cooldown == 0) {
			currentAttack = -1; //resting
			lightningPoints = new ArrayList<Point>();
			swords = new ArrayList<Point>();
		}
		if (currentAttack == 0 && cooldown % 5 == 0) {
			angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY()) + Math.random() - 0.5;
			Projectile magic = new Projectile(
			new double[]{rect.getCenterX(),rect.getCenterY()},
			new double[]{Math.sin(angle)*5,Math.cos(angle)*5},
			new double[]{0,0},
			"magic"
			);
			Projectile.projectiles.add(magic);
		}
		if (currentAttack == 1) {
			lightningPoints = new ArrayList<Point>();
			int tX = (int) rect.getCenterX();
			int tY = (int) rect.getCenterY();
			for (int i=0; i<30; i++) {
				lightningPoints.add(new Point(tX,tY));
				tX += (int)(40*Math.sin(angle));
				tY += (int)(40*Math.cos(angle));
			}
			for (int i=0; i<lightningPoints.size(); i++) {
				lightningPoints.get(i).translate((int)(Math.random()*30-30),(int)(Math.random()*30-30));
				if (p.rect.contains(lightningPoints.get(i))) {
					p.hurtTimer = 10;
					p.health -= 0.03;
				}
			}
		}
		if (currentAttack == 2) {
			if (cooldown % 25 == 0 && cooldown > 75) {
				swords.add(swordPoints[(200-cooldown)/25]);
			}
			if (cooldown <= 75 && cooldown % 15 == 0) {
				Point sword = swords.get(0);
				angle = Math.atan2(p.rect.getCenterX()-sword.getX(),p.rect.getCenterY()-sword.getY());
				Projectile newSword = new Projectile(
					new double[]{sword.getX(),sword.getY()},
					new double[]{Math.sin(angle)*25,Math.cos(angle)*25},
					new double[]{0,0},
					"sword"
				);
				Projectile.projectiles.add(newSword);
				swords.remove(0);
			}
		}
		if (currentAttack == 3) {
			x += Math.sin(angle)*15;
			y += Math.cos(angle)*15;
			if (x < -150 || x > 1350 || y < -150 || y > 750) {
				teleport();
				angle = Math.atan2(p.rect.getCenterX()-rect.getCenterX(),p.rect.getCenterY()-rect.getCenterY());
			}
			if (rect.intersects(p.rect)) {
				p.health -= 0.1;
				p.hurtTimer = 10;
			}
		}
		
		cooldown--;
		bar.x = (int)x-27;
		bar.y = (int)y-20;
		rect.setLocation((int)x,(int)y);
		
		wings++;
		if (wings > 60) {
			wings = 0;
		}
	}
	
	public void render(Graphics g) {
		//render lightning
		for (int i=0; i<lightningPoints.size()-1; i++) {
			Graphics2D g2 = (Graphics2D) g;
			g.setColor(new Color(255,255,255));
			g2.setStroke(new BasicStroke(2));
			Point p1 = lightningPoints.get(i);
			Point p2 = lightningPoints.get(i+1);
			g2.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
		}
		
		//render wings
		g.setColor(new Color(255,255,255));
		int tX = (int)rect.getCenterX();
		int tY = (int)rect.getCenterY()-10;
		if (wings > 30) {
			g.fillPolygon(new int[]{tX,tX-70,tX-140},new int[]{tY,tY-10,tY-100},3);
			g.fillPolygon(new int[]{tX,tX+70,tX+140},new int[]{tY,tY-10,tY-100},3);
		
			g.fillPolygon(new int[]{tX,tX-75,tX-150},new int[]{tY,tY+30,tY+10},3);
			g.fillPolygon(new int[]{tX,tX+75,tX+150},new int[]{tY,tY+30,tY+10},3);
		}
		else {
			g.fillPolygon(new int[]{tX,tX-80,tX-160},new int[]{tY,tY+40,tY+25},3);
			g.fillPolygon(new int[]{tX,tX+80,tX+160},new int[]{tY,tY+40,tY+25},3);
		
			g.fillPolygon(new int[]{tX,tX-50,tX-140},new int[]{tY,tY+70,tY+100},3);
			g.fillPolygon(new int[]{tX,tX+50,tX+140},new int[]{tY,tY+70,tY+100},3);
		}
		
		for (Point i : swords) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g.drawLine((int)i.getX(),(int)i.getY(),(int)i.getX(),(int)i.getY()-55);
			g.drawLine((int)i.getX()-10,(int)i.getY()-15,(int)i.getX()+10,(int)i.getY()-15);
			g2.setStroke(new BasicStroke(5));
			g.drawLine((int)i.getX(),(int)i.getY()-15,(int)i.getX(),(int)i.getY()-50);
		}
		g.drawImage(image,(int)x,(int)y,null);
		bar.percent = (int)((double)health/maxHealth*100);
		bar.render(g);
	}
}



class Projectile
{
	static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	double[] pos;
	double[] vel;
	double[] acc;
	String type;
	int life;
	
	public Projectile(double[] p, double[] v, double[] a, String s) {
		pos = p;
		vel = v;
		acc = a;
		type = s;
		
		if (type.equals("arrow")) {
			life = 50+(int)(Math.random()*10);
		}
		if (type.equals("laser") || type.equals("magic") || type.equals("sword")) {
			life = 99999;
		}
	}
	
	public void update() {
		life--;
		if (life > 0) {
			pos[0] += vel[0];
			pos[1] += vel[1];
		}
	}
	public void render(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		if (type.equals("arrow")) {
			g.setColor(new Color(150,50,50));
			g.setStroke(new BasicStroke(2));
			g.drawLine((int)pos[0],(int)pos[1],(int)(pos[0]-vel[0]*2),(int)(pos[1]-vel[1]*2));
			g.setColor(new Color(100,100,255));
			g.setStroke(new BasicStroke(4));
			g.drawLine((int)(pos[0]-vel[0]*1.5),(int)(pos[1]-vel[1]*1.5),(int)(pos[0]-vel[0]*2),(int)(pos[1]-vel[1]*2));
			g.setStroke(new BasicStroke(5));
			g.setColor(new Color(100,100,100));
			g.drawLine((int)pos[0],(int)pos[1],(int)pos[0],(int)pos[1]);
		}
		if (type.equals("laser")) {
			g.setColor(new Color(100,150,255));
			g.setStroke(new BasicStroke(3));
			g.drawLine((int)pos[0],(int)pos[1],(int)(pos[0]-vel[0]*2),(int)(pos[1]-vel[1]*2));
		}
		if (type.equals("magic")) {
			g.setColor(new Color(255,255,255));
			g.fillOval((int)pos[0]-4,(int)pos[1]-4,8,8);
			g.setStroke(new BasicStroke(1));
			//sparkles
			int[] tPos = new int[]{(int)pos[0],(int)pos[1]};
			for (int i=0; i<10; i++) {
				int tx = (int)(tPos[0]+Math.random()*20-10);
				int ty = (int)(tPos[1]+Math.random()*20-10);
				g.drawLine(tx-3,ty,tx+3,ty);
				g.drawLine(tx,ty-3,tx,ty+3);
				tPos[0] -= vel[0];
				tPos[1] -= vel[1];
			}
		}
		if (type.equals("sword")) {
			int tX = (int) pos[0];
			int tY = (int) pos[1];
			g.setColor(new Color(255,255,255));
			g.setStroke(new BasicStroke(3));
			g.drawLine(tX,tY,(int)(tX+vel[0]*2.4),(int)(tY+vel[1]*2.4));
			
			int[] tPos = new int[]{(int)(tX+vel[0]),(int)(tY+vel[1])};
			double newAngle = Math.atan2(vel[0],vel[1])+Math.toRadians(90);
			
			g.drawLine((int)(tPos[0]+Math.sin(newAngle)*10),(int)(tPos[1]+Math.cos(newAngle)*10),(int)(tPos[0]-Math.sin(newAngle)*10),(int)(tPos[1]-Math.cos(newAngle)*10));
			
			g.setStroke(new BasicStroke(5));
			g.drawLine((int)(tX+vel[0]),(int)(tY+vel[1]),(int)(tX+vel[0]*2),(int)(tY+vel[1]*2));
		}
	}
}



class Level
{
	ArrayList<Jimmy> jimmies = new ArrayList<Jimmy>();
	ArrayList<Prop> props = new ArrayList<Prop>();
	Color color;
	
	public static ArrayList<Level> levels = new ArrayList<Level>();
	public static int level;
	
	public static void init() {
		
		//do not do this wtf
		try {
			Scanner p = new Scanner(new File("files\\props.txt"));
			Scanner e = new Scanner(new File("files\\enemies.txt"));
			Scanner c = new Scanner(new File("files\\colors.txt"));
			
			while (p.hasNextLine()) { // the files must have the same number of lines to not error
				Level level = new Level();

				String propLine = p.nextLine();
				String jimmyLine = e.nextLine();
				String[] propArray = propLine.split(" ");
				String[] jimmyArray = jimmyLine.split(" ");
				
				//System.out.println(Arrays.toString(propArray));
				for (int i=0; i<propArray.length; i+=3) {
					level.props.add(new Prop(Integer.parseInt(propArray[i]),Integer.parseInt(propArray[i+1]),propArray[i+2]));
				}
				
				Jimmy jimmy = null;
				for (int i=0; i<jimmyArray.length; i+=3) {
					switch(jimmyArray[i+2]) { //help
						case("melee"): jimmy = new MeleeJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("big") : jimmy = new BigMeleeJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("lightning") : jimmy = new LightningJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("laser") : jimmy = new LaserJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("tree") : jimmy = new TreeJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("laserBoss") : jimmy = new LaserBossJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("magic") : jimmy = new MagicJimmy(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
						case("boss") : jimmy = new Boss(Integer.parseInt(jimmyArray[i]),Integer.parseInt(jimmyArray[i+1])); break;
					}
					if (jimmy != null) {
						level.jimmies.add(jimmy);
					}
				}
				
				String colorLine = c.nextLine();
				String[] colorArray = colorLine.split(" ");
				level.color = new Color(Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2]));
				levels.add(level);
			}
		}
		catch (FileNotFoundException e) {System.out.println("level error");}
	}
}



class Sprite
{
	static Image jimmyNormal, jimmyHurt, jimmyBig, jimmyBigHurt, jimmyGreen, jimmyBlue, jimmyWhite, jimmyTree, jimmyTreeHurt, boss, bossHurt, heart, ringFront, ringBack;
	static Image[][] rings = new Image[2][3];
	public static void init() {
		try {
			jimmyNormal = ImageIO.read(new File("files\\jimmy.png"));
			jimmyHurt = ImageIO.read(new File("files\\jimmy-hurt.png"));
			jimmyBig = ImageIO.read(new File("files\\jimmy-big.png"));
			jimmyBigHurt = ImageIO.read(new File("files\\jimmy-big-hurt.png"));
			jimmyGreen = ImageIO.read(new File("files\\jimmy-green.png"));
			jimmyBlue = ImageIO.read(new File("files\\jimmy-blue.png"));
			jimmyWhite = ImageIO.read(new File("files\\jimmy-white.png"));
			jimmyTree = ImageIO.read(new File("files\\tree.png"));
			jimmyTreeHurt = ImageIO.read(new File("files\\tree-hurt.png"));
			boss = ImageIO.read(new File("files\\boss.png"));
			bossHurt = ImageIO.read(new File("files\\boss-hurt.png"));
			heart = ImageIO.read(new File("files\\heart.png"));
			ringFront = ImageIO.read(new File("files\\ring-front.png"));
			ringBack = ImageIO.read(new File("files\\ring-back.png"));
			
			rings[0][0] = ringFront;
			rings[0][1] = ringFront.getScaledInstance(160,160,Image.SCALE_FAST);
			rings[0][2] = ringFront.getScaledInstance(240,240,Image.SCALE_FAST);
			rings[1][0] = ringBack;
			rings[1][1] = ringBack.getScaledInstance(160,160,Image.SCALE_FAST);
			rings[1][2] = ringBack.getScaledInstance(240,240,Image.SCALE_FAST);
			
			
		}
		catch (Exception e) {
			System.out.println("sprite error");
		}
	}
}



class HealthBar
{
	int x, y, length, height, percent, border;
	public HealthBar(int a, int b, int l, int h, int p, int i) {
		x = a;
		y = b;
		length = l;
		height = h;
		percent = p;
		border = i;
	}
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x-border,y-border,length+2*border,height+2*border);
		g.setColor(Color.GRAY);
		g.fillRect(x,y,length,height);
		g.setColor(Color.RED);
		g.fillRect(x,y,length*percent/100,height);
	}
}



class Sound
{
	static File owFile;
	static AudioInputStream owStream;
	static Clip ow;
	
	static void play() {
		try {
			owFile = new File("files\\ow.wav");
			owStream = AudioSystem.getAudioInputStream(owFile);
			ow = AudioSystem.getClip();
			ow.open(owStream);
			ow.start();
		}
		catch (Exception e) {System.out.println("help");};
	}
}