package util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import main.Main;
import maps.Map;

public class Assets {
	private static Assets instance = null;

	private HashMap<String, Image> sprites = new HashMap<String, Image>();
	private HashMap<String, Map> maps = new HashMap<String, Map>();
	private ResourceBundle langs;

	private Assets() {
	}

	public static Assets getInstance() {
		if (instance == null)
			instance = new Assets();

		return instance;
	}

	public Image getSprite(String name) {
		return sprites.get(name);
	}

	public Map getMap(String name) {
		return maps.get(name);
	}

	public String I18N(String s) {
		try {
			return langs.getString(s);
		} catch (MissingResourceException e) {
			return s;
		}
	}
	
	public ArrayList<Map> getMapsArray() {
		ArrayList<Map> result = new ArrayList<Map>();
		result.addAll(maps.values());
		
		return result;
	}
	
	public String getCountry() {
		return langs.getLocale().getCountry();
	}

	public void loadSprites() {
		loadSprite("dangerous_sector", "dangerousSector.png");
		loadSprite("safe_sector", "safeSector.png");
		loadSprite("human_sector", "humanSector.png");
		loadSprite("alien_sector", "alienSector.png");
		loadSprite("escape_hatch", "escapeHatchSector.png");

		loadSprite("knife", "knife.png");
		loadSprite("right_arrow", "rightArrow.png");
		loadSprite("left_arrow", "leftArrow.png");
		loadSprite("icon", "icon.png");
		loadSprite("cross", "close.png");
		loadSprite("pencil", "pencil.png");
		loadSprite("book", "book.png");

		loadSprite("human", "human.png");
		loadSprite("alien", "alien.png");
		
		loadSprite("title", "titleImage.png");
		loadSprite("end_screen", "alienWin.png");
	}

	public void loadMaps() {
		loadMap("galilei", "galilei.lvl");
		loadMap("galvani", "galvani.lvl");
		loadMap("fermi", "fermi.lvl");
	}

	public void loadLang(String lan) {
		loadLang(lan, "");
	}

	public void loadLang(String lan, String country) {
		Locale loc;
		if (country == "")
			loc = new Locale(lan);
		else
			loc = new Locale(lan, country);

		File file = new File(Main.PATH + "/resources/langs");
		try {
			URL[] urls = { file.toURI().toURL() };
			langs = ResourceBundle.getBundle("lang", loc, new URLClassLoader(urls));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
	}

	private void loadMap(String name, String filename) {
		maps.put(name, new Map("resources/maps/" + filename, name));
	}

	private void loadSprite(String name, String filename) {
		Image result = null;
		File file = new File(Main.PATH + "/resources/sprites/" + filename);

		try {
			BufferedImage loadedImage = ImageIO.read(file);
			if (loadedImage.getType() == BufferedImage.TYPE_INT_ARGB)
				result = loadedImage;
			else {
				BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(),
						BufferedImage.TYPE_INT_ARGB);

				Graphics g = formattedImage.getGraphics();
				g.drawImage(loadedImage, 0, 0, null);
				g.dispose();

				result = formattedImage;
			}
		} catch (IOException exception) {
			if (file.exists())
				exception.printStackTrace();
			else
				System.out.println("can't find the file in: " + file);
			result = null;
		}

		if (result != null) {
			sprites.put(name, result);
		}
	}
}
