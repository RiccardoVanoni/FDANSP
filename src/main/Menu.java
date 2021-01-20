package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import maps.Map;
import rendering.RenderHelper;
import tiles.Sector;
import util.Assets;
import util.Button;
import util.UtilFunc;

public class Menu implements Module {

	private Engine parent;

	private MENUSTATE currentState;
	private MENUSTATE nextState;

	private ArrayList<Button> buttons = new ArrayList<Button>();
	private boolean clearButtons = false;

	private int currentmapIdx;
	private int numMaps;
	private ArrayList<Map> mapsArray;
	private Image currentMapImage;
	
	private int numPlayers = 6;
	
	private ArrayList<String> playersName = new ArrayList<String>();
	private StringBuffer currentName = new StringBuffer("");
	private int nameIdx = 0;

	public Menu(Engine parent) {
		this.parent = parent;

		mapsArray = Assets.getInstance().getMapsArray();

		currentmapIdx = 0;
		numMaps = mapsArray.size();
		currentMapImage = mapsArray.get(currentmapIdx).getImage(20);

		currentState = MENUSTATE.WELCOMESCREEN;
		nextState = MENUSTATE.WELCOMESCREEN;
	}

	@Override
	public void update(double elapsedTime) {

		if (parent.leftMouseClicked)
			for (Button b : buttons)
				if (parent.mouseI == b.i && parent.mouseJ == b.j) {
					b.buttonPressed();
					parent.leftMouseClicked = false;
				}

		switch (currentState) {
		case WELCOMESCREEN:
			if (parent.keyPressed != '\0' || parent.leftMouseClicked || parent.rightMouseClicked) {
				nextState = MENUSTATE.CHOOSEMAP;
			}

			break;

		case CHOOSEMAP:
			if (buttons.isEmpty()) {
				buttons.add(new Button(15, 6, Assets.getInstance().getSprite("left_arrow"), new Color(0x84DE02)) {

					@Override
					public void buttonPressed() {
						currentmapIdx = (currentmapIdx - 1 + numMaps) % numMaps;
						currentMapImage = mapsArray.get(currentmapIdx).getImage(20);
					}
				});
				
				buttons.add(new Button(25, 6, Assets.getInstance().getSprite("right_arrow"), new Color(0x84DE02)) {

					@Override
					public void buttonPressed() {
						currentmapIdx = (currentmapIdx + 1 + numMaps) % numMaps;
						currentMapImage = mapsArray.get(currentmapIdx).getImage(20);
					}
				});
				
				
				buttons.add(new Button(20, 12, Assets.getInstance().getSprite("right_arrow"), new Color(0x84DE02), Assets.getInstance().I18N("continue")) {

					@Override
					public void buttonPressed() {
						clearButtons = true;
						nextState = MENUSTATE.SELECTNUMPLAYERS;
					}
				});
				
				buttons.add(new Button(28, 1, Assets.getInstance().getSprite("pencil"), Color.white, Assets.getInstance().I18N("mapEditor")) {

					@Override
					public void buttonPressed() {
						parent.switchModule(new MapEditor(parent));
					}
				});
			}

			break;
			
		case SELECTNUMPLAYERS:
			if(buttons.isEmpty()) {
				
				buttons.add(new Button(17, 6, Assets.getInstance().getSprite("left_arrow"), new Color(0x84DE02)) {
					
					@Override
					public void render(RenderHelper renderer) {
						if(numPlayers >4)
							super.render(renderer);
					}
					
					@Override
					public void buttonPressed() {
						if(numPlayers > 4)
							numPlayers--;
					}
				});
				
				buttons.add(new Button(23, 6, Assets.getInstance().getSprite("right_arrow"), new Color(0x84DE02)) {
					
					@Override
					public void render(RenderHelper renderer) {
						if(numPlayers < 8)
							super.render(renderer);
					}
					
					@Override
					public void buttonPressed() {
						if(numPlayers < 8)
							numPlayers++;
					}
				});
				
				buttons.add(new Button(20, 9, Assets.getInstance().getSprite("right_arrow"), new Color(0x84DE02), Assets.getInstance().I18N("confirm")) {

					@Override
					public void buttonPressed() {
						
						for(int i = 0; i < numPlayers; i++) {
							playersName.add(Assets.getInstance().I18N("player") + (i+1));
						}
						
						clearButtons = true;
						nextState = MENUSTATE.SELECTPLAYERSNAME;
					}
				});
				
			}
			break;
			
		case SELECTPLAYERSNAME:{
			if(parent.keyPressed != '\0') {
				if(Character.isLetterOrDigit(parent.keyPressed)) {
					currentName.append(parent.keyPressed);
				}else if(parent.keyPressed == KeyEvent.VK_ENTER) {
					
					if(nameIdx >= numPlayers) {
						parent.switchModule(new Game(parent, mapsArray.get(currentmapIdx), numPlayers, playersName));
						nameIdx--;
					}
					
					if(currentName.toString().trim().length() > 0)
						playersName.set(nameIdx, currentName.toString().trim());
					currentName = new StringBuffer("");
					
					nameIdx++;
				}else if(parent.keyPressed == KeyEvent.VK_BACK_SPACE) {
					if(currentName.length() > 0) {
						currentName.deleteCharAt(currentName.length() - 1);
					}
				}
			}
		}
		break;
		
		default:
			break;
		}
		
		if(clearButtons) {
			buttons.clear();
			clearButtons = false;
		}
		
		currentState = nextState;
	}

	@Override
	public void render(RenderHelper renderer) {
		renderer.background(Color.black);
		
		renderer.drawSprite(Assets.getInstance().getSprite("title"), -70, 0);

		switch (currentState) {
		case WELCOMESCREEN: {

			Font font = new Font("OCR A Extended", Font.BOLD, 40);
			ArrayList<String> title = UtilFunc.warpText(Assets.getInstance().I18N("title"), 15);

			int titleX = parent.getWidth() * 2 / 3;
			int titleY = parent.getHeight() / 2;
			int titleStartY = titleY - (int) ((title.size() / 2.0f) * (font.getSize() + 5.0f));

			for (int i = 0; i < title.size(); i++) {
				renderer.drawCenteredString(title.get(i), titleX, titleStartY + i * (font.getSize() + 5), Color.white,
						font);
			}

			renderer.drawLine(titleX - 200, titleStartY - 20, titleX + 200, titleStartY - 20, Color.white);
			renderer.drawLine(titleX - 200, titleStartY + (title.size() - 1) * (font.getSize() + 5) + 30, titleX + 200,
					titleStartY + (title.size() - 1) * (font.getSize() + 5) + 30, Color.white);
		}
			break;

		case CHOOSEMAP: {

			int centerX = (Sector.getScreenX(15) + Sector.getScreenX(25) + 2 * Sector.s) / 2;
			int centerY = parent.getHeight() / 2;
			
			renderer.fillRect(centerX - 150, centerY - 100, 300, 200, new Color(0x505050));
			
			renderer.drawSprite(currentMapImage,
					centerX - 150,
					centerY - 100, 300, 200);
			
			renderer.strokeWeight(2);
			renderer.drawRect(centerX - 150, centerY - 100, 300, 200, Color.white);
			renderer.strokeWeight(1);
			
			renderer.drawCenteredString(Assets.getInstance().I18N(mapsArray.get(currentmapIdx).getName()), centerX, centerY + 120, Color.white, new Font("OCR A Extended", Font.PLAIN, 20));
			
			renderer.drawCenteredString(Assets.getInstance().I18N("chooseMap"), centerX, parent.getHeight() / 4, Color.white, new Font("OCR A Extended", Font.BOLD, 35));
		}
			break;
		
		case SELECTNUMPLAYERS:{
			int centerX = (Sector.getScreenX(15) + Sector.getScreenX(25) + 2 * Sector.s) / 2;
			int centerY = parent.getHeight() / 2 - 80;
			
			renderer.drawCenteredString(Assets.getInstance().I18N("numPlayers"), centerX, centerY, Color.white, new Font("OCR A Extended", Font.PLAIN, 25));
			renderer.drawCenteredString(String.valueOf(numPlayers), centerX, centerY + 67, Color.white, new Font("OCR A Extended", Font.PLAIN, 25));

		}
		break;
		
		case SELECTPLAYERSNAME:{
			int centerX = (Sector.getScreenX(15) + Sector.getScreenX(25) + 2 * Sector.s) / 2;
			int centerY = parent.getHeight() / 2 - 80;
			
			renderer.drawCenteredString(Assets.getInstance().I18N("insertNames"), centerX, parent.getHeight() / 4, Color.white, new Font("OCR A Extended", Font.BOLD, 35));
			
			//renderer.drawCenteredString(currentName.toString(), centerX, centerY, 20, Color.white);
			
			for(int i = 0; i < playersName.size(); i++) {
				if(i == nameIdx)
					renderer.drawCenteredString((i + 1) + "_ " + currentName.toString(), centerX, centerY + i*20, Color.red, new Font("OCR A Extended", Font.BOLD, 20));
				else
					renderer.drawCenteredString((i + 1) + "_ " + playersName.get(i), centerX, centerY + i*20, Color.white, new Font("OCR A Extended", Font.BOLD, 20));
			}
		}
		break;
		
		default:
			break;
		}

		for (Button b : buttons)
			b.render(renderer);
	}

	private enum MENUSTATE {
		WELCOMESCREEN, CHOOSEMAP, SELECTNUMPLAYERS, SELECTPLAYERSNAME;
	}
}
