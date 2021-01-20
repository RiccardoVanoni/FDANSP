package main;

import java.io.File;

public class Main {
	
	public final static String PATH = (new File("")).getAbsolutePath();

	public static void main(String[] args) {
		
		Engine game = new Engine();
		//MapEditor game = new MapEditor();
		Thread gameThread = new Thread(game);
		gameThread.start();
	}
}
