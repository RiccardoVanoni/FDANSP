package tiles;

import java.awt.Color;

import main.Game;
import main.Game.GAMESTATE;
import players.Player;
import rendering.RenderHelper;
import util.Assets;

public class EscapeHatchSector extends Sector {
	private int idx = 1;

	public EscapeHatchSector(int i, int j) {
		super(i, j, Assets.getInstance().getSprite("escape_hatch"));
	}

	public void setIndex(int idx) {
		this.idx = idx;
	}

	@Override
	public void render(RenderHelper renderer) {
		super.render(renderer);

		renderer.drawCenteredString(String.valueOf(idx), getScreenX(i) + s, getScreenY(i, j) + (int) r, s, Color.white);
	}
	
	@Override
	public GAMESTATE getMovementAction() {
		return Game.GAMESTATE.HUMANWIN;
	}

	@Override
	public void editAction() {
		idx++;
	}

	@Override
	public byte getClassId() {
		return 5;
	}

	@Override
	public boolean hasAdditionalInformation() {
		return true;
	}

	@Override
	public byte getAdditionalInfo() {
		return (byte) idx;
	}

	@Override
	public void parseAdditionalInformation(byte info) {
		this.idx = info;
	}

	@Override
	public boolean canPlayerWalk(Player p) {
		return p.getType() == Player.TYPE.HUMAN;
	}
}
