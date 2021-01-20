package main;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import maps.Map;
import rendering.RenderHelper;
import tiles.AlienSector;
import tiles.DangerousSector;
import tiles.EscapeHatchSector;
import tiles.HumanSector;
import tiles.SafeSector;
import tiles.Sector;
import tiles.WallSector;

public class MapEditor implements Module {

	private Engine parent;

	private Map map;
	private ArrayList<Sector> sectors = new ArrayList<Sector>();
	private Class<? extends Sector> brush = SafeSector.class;
	private int mapW = 23;
	private int mapH = 14;

	public MapEditor(Engine parent) {
		this.parent = parent;

		map = new Map(mapW, mapH, "");

		sectors.add(new SafeSector(26, 0));
		sectors.add(new DangerousSector(26, 1));
		sectors.add(new HumanSector(26, 2));
		sectors.add(new AlienSector(26, 3));
		sectors.add(new EscapeHatchSector(26, 4));
	}

	@Override
	public void render(RenderHelper renderer) {
		map.render(renderer);

		for (Sector s : sectors)
			s.render(renderer);

		renderer.drawHex(Sector.getScreenX(parent.mouseI), Sector.getScreenY(parent.mouseI, parent.mouseJ), Sector.s,
				Sector.h, (int) Sector.r, Color.red);

		renderer.drawString("Save", 25 * (int) (Sector.h + Sector.s) + 14, (int) ((25 & 1) * Sector.r) + 30, 14);

	}

	@Override
	public void update(double elapsedTime) {
		if (parent.mouseI >= 0 && parent.mouseI < mapW && parent.mouseJ >= 0
				&& parent.mouseJ < mapH) {

			if (parent.leftMouseClicked) {
				map.tiles.set(parent.mouseI + parent.mouseJ * mapW, new WallSector(parent.mouseI, parent.mouseJ));
			}

			if (parent.wheelClicked) {
				map.getSector(parent.mouseI, parent.mouseJ).editAction();
			}

			if (parent.rightMouseClicked) {
				try {
					map.tiles.set(parent.mouseI + parent.mouseJ * mapW,
							brush.getConstructor(int.class, int.class).newInstance(parent.mouseI, parent.mouseJ));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}

		if (parent.leftMouseClicked)
			for (Sector s : sectors)
				if (s.i == parent.mouseI && s.j == parent.mouseJ)
					brush = s.getClass();

		if (parent.leftMouseClicked && parent.mouseI == 25 && parent.mouseJ == 0)
			map.save("newMap.lvl");
	}

}
