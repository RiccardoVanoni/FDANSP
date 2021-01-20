package tiles;

import players.Player;
import util.Assets;

public class AlienSector extends Sector {

	public AlienSector(int i, int j) {
		super(i, j, Assets.getInstance().getSprite("alien_sector"));
	}

	@Override
	public byte getClassId() {
		return 4;
	}

	@Override
	public boolean canPlayerWalk(Player p) {
		return false;
	}

}
