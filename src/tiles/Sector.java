package tiles;

import java.awt.Graphics2D;
import java.awt.Image;
import main.Game.GAMESTATE;
import players.Player;
import rendering.RenderHelper;

public abstract class Sector {
	public final int i,j;
	protected final Image sprite;
	
	public static final int s = 26;
	public static final int h = s / 2;
	public static final float r = (int) (s * Math.sqrt(3) / 2);
	
	public Sector(int i, int j, Image sprite){
		this.i = i;
		this.j = j;
		this.sprite = sprite;
	}
	
	public void render(RenderHelper renderer) {
		int pX = getScreenX(i);
		int pY = getScreenY(i,j);
		renderer.drawSprite(sprite, pX, pY,2*s,(int)(2*r));
	}
	
	public abstract boolean canPlayerWalk(Player p);
	
	public String getCoords() {
		return formatCoords(i, j);
	}
	
	public static String formatCoords(int i, int j) {
		return String.format("%c%02d", (char)(i + 65), j+1);
	}
	
	/**
	 * Returns in what state the game should be if the movement of a player ended on this sector
	 */
	public GAMESTATE getMovementAction(){
		return GAMESTATE.ENDTURN;
	}
	
	/**
	 * used to create the miniature of the maps
	 * 
	 * @param g {@link java.awt.Graphics2D Graphics2D} object used to draw
	 * @param s side of the hexagon
	 * @param h s / 2
	 * @param r s * sqrt(3)/2
	 */
	public void simplifiedRender(Graphics2D g, int s, int h, int r) {
		int pX = i*(int)(h + s);
		int pY = (int)(j*2*r + (i & 1)*r);

		g.drawImage(sprite, pX, pY, pX + 2*s, pY + 2*r, 0, 0, sprite.getWidth(null), sprite.getHeight(null), null);
	}
	
	/**
	 * Action to be performed when the cell is clicked with the mouse wheel in the map editor
	 */
	public void editAction() {
	}
	
	/**
	 * Returns a byte that contains additional infos about the sector
	 * that will be stored in the file
	 * 
	 */
	public byte getAdditionalInfo() {
		return 0;
	}
	
	/**
	 * Returns if the sector has additional info that needs to be stored 
	 */
	public boolean hasAdditionalInformation() {
		return false;
	}
	
	public void parseAdditionalInformation(byte info) {
	}
	
	/**
	 * ID that uniquely identifies the class
	 */
	public abstract byte getClassId();
	
	public static int getScreenX(int i) { return i*(int)(h + s); }
	
	public static int getScreenY(int i, int j) { return (int)(j*2*r + (i & 1)*r); }
}
