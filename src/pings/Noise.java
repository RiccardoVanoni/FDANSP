package pings;

import java.awt.Color;
import java.util.ArrayList;

import players.Player;
import rendering.RenderHelper;
import tiles.Sector;
import util.Assets;
import util.UtilFunc;

public class Noise extends Ping {

	public Noise(int i, int j, int turn, Player source) {
		super(i, j, turn, source);
	}

	@Override
	public void render(RenderHelper renderer) {
		Color color = seen ? new Color(0x44506E) : new Color(0x9000DD);
		
		renderer.strokeWeight(2);
		renderer.drawHex(Sector.getScreenX(i) + 2, Sector.getScreenY(i, j) + 2, Sector.s - 2,
				Sector.h - 1, (int) Sector.r - 2, color);
		renderer.strokeWeight(1);
	}

	@Override
	public ArrayList<String> getDescription() {
		ArrayList<String> result = UtilFunc.warpText(
				String.format(Assets.getInstance().I18N("noiseDesc"), turn, source.getName())
				, 20);
		
		return result;
	}

}
