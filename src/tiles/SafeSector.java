package tiles;

import players.Player;
import rendering.RenderHelper;
import util.Assets;

public class SafeSector extends Sector {

	public SafeSector(int i, int j) {
		super(i, j, Assets.getInstance().getSprite("safe_sector"));
	}
	
	@Override
	public void render(RenderHelper renderer) {
		super.render(renderer);
		
		renderer.drawCenteredString(String.format("%c%02d", (char)(i + 65), j+1), getScreenX(i) + s, getScreenY(i,j) + (int)r, h);	}
	
	@Override
	public byte getClassId() {
		return 1;
	}

	@Override
	public boolean canPlayerWalk(Player p) {
		return true;
	}
}
