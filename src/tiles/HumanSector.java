package tiles;

import players.Player;
import util.Assets;

public class HumanSector extends Sector{

	public HumanSector(int i, int j) {
		super(i, j, Assets.getInstance().getSprite("human_sector"));
	}

	@Override
	public byte getClassId() {
		return 3;
	}

	@Override
	public boolean canPlayerWalk(Player p) {
		return false;
	}

}
