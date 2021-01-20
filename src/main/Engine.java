package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import maps.Map;
import rendering.RenderHelper;
import util.Assets;

public class Engine extends Canvas implements Runnable, MouseListener {

	private static final long serialVersionUID = -4036206489224105214L;

	private JFrame container;
	private BufferStrategy strategy;
	private RenderHelper renderer;

	boolean leftMouseClicked = false;
	boolean rightMouseClicked = false;
	boolean wheelClicked = false;
	char keyPressed = '\0';
	int mouseI, mouseJ;
	
	private Module currentModule;
	private Module nextModule = null;
	private boolean switchModule = false;

	public Engine() {
		container = new JFrame();

		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(1170, 638));
		panel.setLayout(null);

		setBounds(0, 0, 1170, 638);
		panel.add(this);

		setIgnoreRepaint(true);
		addMouseListener(this);
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				keyPressed = e.getKeyChar();
			}
		});

		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setLocationRelativeTo(null);

		requestFocus();

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		renderer = new RenderHelper(getWidth(), getHeight());

		Assets.getInstance().loadSprites();
		Assets.getInstance().loadMaps();
		Assets.getInstance().loadLang("it");

		container.setIconImage(Assets.getInstance().getSprite("icon"));
		
		currentModule = new Menu(this);
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double nanoSecondConversion = 1.0E9 / 60.0d; // 60 fps
		double changeInSeconds = 0;
		double fps = 0;

		while (true) {
			long now = System.nanoTime();

			changeInSeconds += (now - lastTime) / nanoSecondConversion;
			fps = 60.0d / changeInSeconds;

			while (changeInSeconds >= 1) {
				
				updateMousePos();
				currentModule.update(changeInSeconds * 60.0d);
				clearFlags();
				
				if(switchModule)
					if(nextModule != null)
						currentModule = nextModule;
				
				changeInSeconds--;
			}
			
			updateMousePos();
			render();
			
			container.setTitle("fuga dagli alieni - fps: " + fps);
			lastTime = now;
		}
	}
	
	private void render() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

		renderer.startRendering(g);
		renderer.background(new Color(0xE0E0E0));
		
		currentModule.render(renderer);
		
		g.dispose();
		strategy.show();
	}
	
	private void clearFlags() {
		leftMouseClicked = false;
		rightMouseClicked = false;
		wheelClicked =  false;
		keyPressed = '\0';
	}
	
	private void updateMousePos() {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouse, this);

		mouse = Map.screenToIndicies(mouse);
		
		mouseI = mouse.x;
		mouseJ = mouse.y;
	}
	
	public void switchModule(Module nextModule) {
		switchModule = true;
		this.nextModule = nextModule;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
			leftMouseClicked = true;
		else if(arg0.getButton() == MouseEvent.BUTTON2)
			wheelClicked = true;
		else if(arg0.getButton() == MouseEvent.BUTTON3)
			rightMouseClicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
