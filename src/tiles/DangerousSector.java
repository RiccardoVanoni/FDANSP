package tiles;

import main.Game.GAMESTATE;
import players.Player;
import rendering.RenderHelper;
import util.Assets;

public class DangerousSector extends Sector {

	public DangerousSector(int i, int j) {
		super(i, j, Assets.getInstance().getSprite("dangerous_sector"));
	}
	
	@Override
	public void render(RenderHelper renderer) {
		super.render(renderer);
		
		renderer.drawCenteredString(getCoords(), getScreenX(i) + s, getScreenY(i,j) + (int)r, h);
	}

	@Override
	public byte getClassId() {
		return 2;
	}

	@Override
	public boolean canPlayerWalk(Player p) {
		return true;
	}
	
	@Override
	public GAMESTATE getMovementAction() {
		return GAMESTATE.CHOOSESECTORCARD;
	}
}
