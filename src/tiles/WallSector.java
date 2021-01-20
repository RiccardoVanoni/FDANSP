package tiles;

import java.awt.Graphics2D;

import players.Player;
import rendering.RenderHelper;

public class WallSector extends Sector {

	public WallSector(int i, int j) {
		super(i, j, null);
	}

	@Override
	public byte getClassId() {
		return 0;
	}
	
	@Override
	public void render(RenderHelper renderer) {}

	@Override
	public boolean canPlayerWalk(Player p) {
		return false;
	}
	
	@Override
	public void simplifiedRender(Graphics2D g, int s, int h, int r) {}

}
