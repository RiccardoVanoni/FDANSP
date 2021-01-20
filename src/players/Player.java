package players;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Stack;

import main.Game;
import pings.Ping;
import rendering.RenderHelper;
import tiles.Sector;
import util.Assets;
import util.Button;

public class Player {
	public int i;
	public int j;
	
	private TYPE type;
	
	private boolean alive;
	public int playerNum;
	private String name;
	
	private Image sprite;
	private int spriteW;
	
	public Stack<String> notifications = new Stack<String>();
	public ArrayList<Ping> pings = new ArrayList<Ping>();
	
	public Player(int i, int j, TYPE type, int playerNum) {
		this.i = i;
		this.j = j;
		this.type = type;
		
		this.playerNum = playerNum % 4;
		this.sprite = Assets.getInstance().getSprite(type.name().toLowerCase());
		
		spriteW = (int) (sprite.getWidth(null) / 4.0f);
		
		name = type.name() + (playerNum + 1);
		
		alive = true;
	}
	
	public Player setName(String name) {
		this.name = name;
		
		return this;
	}
	
	public String getName() {
		return name;
	};
	
	public boolean isAlive() {
		return alive;
	}
	
	public boolean canAttack() {
		return type == TYPE.ALIEN;
	}
	
	public Player setPos(int x, int y) {
		this.i = x;
		this.j = y;
		
		return this;
	}
	
	public void kill() {
		alive =  false;
	}
	
	public void render(RenderHelper renderer) {
		renderer.drawPartialSprite(sprite, Sector.getScreenX(i) + (int)(Sector.s *0.25), Sector.getScreenY(i, j) + (int)(Sector.r * 0.25), 
				(int)(Sector.s * 1.5), (int)(Sector.r * 1.5), spriteW*playerNum , 0, spriteW, sprite.getHeight(null));
	}
	
	public Player updatePings() {
		for(Ping p : pings) p.update();
		
		return this;
	}
	
	public void loadPingsFor(Game game, ArrayList<Button> buttons, int pI, int pJ) {
		ArrayList<Ping> pingsInSector = new ArrayList<Ping>();
		for(Ping p : pings)
			if(p.getI() == pI && p.getJ() == pJ)
				pingsInSector.add(p);
		
		if(pingsInSector.isEmpty()) return;
		
		for(Ping p : pingsInSector) p.update();
		
		pingsInSector.sort((a,b) -> -Integer.valueOf(a.getTurn()).compareTo(Integer.valueOf(b.getTurn())));
		
		ArrayList<String> tooltipBody = new ArrayList<String>();
		
		int k;
		for(k = 0; k < pingsInSector.size(); k++) {
			if(k >= 3) {
				tooltipBody.add(String.format(Assets.getInstance().I18N("otherPings"),pingsInSector.size() - 3));
				break;
			}
			
			tooltipBody.addAll(pingsInSector.get(k).getDescription());
			tooltipBody.add("");
		}
		
		String tooltipTitle = String.format(Assets.getInstance().I18N("pingsTitle"), Sector.formatCoords(pI, pJ));
		game.addTooltip(tooltipTitle, tooltipBody);
		
		for(int z = 0; z < k; z++) {
			final int index = pings.indexOf(pingsInSector.get(z));
			
			buttons.add(new Button(26,6 + z*2, 
					Assets.getInstance().getSprite("cross"), new Color(0xed7a71), "", "tooltipButtons") {

				@Override
				public void buttonPressed() {
					pings.remove(index);
					game.reloadPlayerPings(pI, pJ);
					
				}
			});
		}
	}
	
	public TYPE getType() {
		return type;
	}
	
	public int getNumMovements() {
		if(type == TYPE.HUMAN)
			return 1;
		else
			return 2;
	}
	
	public enum TYPE{ HUMAN, ALIEN }
}
