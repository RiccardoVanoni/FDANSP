package util;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import rendering.RenderHelper;
import tiles.Sector;

public abstract class Button {
	public int i;
	public int j;
	
	private Image sprite;
	private Color color;
	protected String text;
	
	ArrayList<String> tags;
	
	public Button(int i, int j, Image sprite, Color color,String text, String... tags) {
		this.i = i;
		this.j = j;
		
		this.sprite = sprite;
		this.color = color;
		this.text = text;
		
		this.tags = new ArrayList<String>();
		for(String s : tags)
			this.tags.add(s);
	}
	
	public Button(int i, int j, Image sprite, Color color) {
		this(i,j,sprite,color, "");
	}
	
	public void render(RenderHelper renderer) {
		renderer.strokeWeight(2);
		renderer.drawHex(Sector.getScreenX(i), Sector.getScreenY(i, j), 
				Sector.s, Sector.h, (int)Sector.r, color);
		renderer.strokeWeight(1);
		
		renderer.drawSprite(sprite, Sector.getScreenX(i) + Sector.h, Sector.getScreenY(i, j) + ((int)Sector.r / 2), Sector.s, (int)Sector.r);
		
		if(text != "") {
			renderer.drawCenteredString(text, Sector.getScreenX(i) + Sector.s, Sector.getScreenY(i, j) - 10, 10, color);
		}
	}
	
	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}
	
	public abstract void buttonPressed();
}
