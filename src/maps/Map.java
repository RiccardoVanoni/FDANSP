package maps;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import main.Main;
import players.Player;
import rendering.RenderHelper;
import tiles.AlienSector;
import tiles.DangerousSector;
import tiles.EscapeHatchSector;
import tiles.HumanSector;
import tiles.SafeSector;
import tiles.Sector;
import tiles.WallSector;

public class Map {
	public ArrayList<Sector> tiles =  null;
	private final int w,h;
	private final String name;
	
	public Map(int w, int h, String name) {
		this.w = w;
		this.h = h;
		this.name = name;
		
		tiles = new ArrayList<Sector>();
		for(int j = 0; j < h; j++)
			for(int i = 0; i < w; i++)
				tiles.add(new SafeSector(i, j));
	}
	
	public String getName() {
		return name;
	}
	
	public Image getImage(int s) {
		int h = s / 2;
		int r = (int)(s * Math.sqrt(3) / 2);
		
		int imageW = (w*3 + 1)*h;
		int imageH = (2*this.h + 1)*r;
		
		Image result = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		
		for(Sector sec : tiles)
			sec.simplifiedRender(g, s, h, r);
		
		g.dispose();
		return result;
	}
	
	public Map(String filename, String name) {
		int _w = 10, _h = 10;
		this.name = name;
		
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(new File(Main.PATH + "/" + filename)));
			
			_w = in.readByte();
			_h = in.readByte();
			
			tiles = new ArrayList<Sector>();
			for(int z = 0; z < _w*_h; z++) {
				int id = in.readByte();
				int i = in.readByte();
				int j = in.readByte();
				Sector s;
				
				switch(id) {
				case 1:
					s = new SafeSector(i, j);
					break;
				case 2:
					s = new DangerousSector(i, j);
					break;
				case 3:
					s = new HumanSector(i, j);
					break;
				case 4:
					s = new AlienSector(i, j);
					break;
				case 5:
					s = new EscapeHatchSector(i,j);
					break;
				case 0:
				default:
					s = new WallSector(i, j);
					break;
				}
				
				if(s.hasAdditionalInformation()) {
					s.parseAdditionalInformation(in.readByte());
				}
				
				tiles.add(s);
			}
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.w = _w;
		this.h = _h;
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	private void checkSector(ArrayList<Sector> a, Player p, int i, int j) {
		Sector s = getSector(i, j);
		if(s != null )
			if(s.canPlayerWalk(p))
				a.add(s);
	}
	
	public ArrayList<Sector> getMovementsFrom(Player p, int i, int j){
		ArrayList<Sector> result = new ArrayList<Sector>();
		
		if((i & 1) == 0) {
			checkSector(result, p, i + 1, j - 1);
			checkSector(result, p, i - 1, j - 1);
			checkSector(result, p, i    , j - 1);
			checkSector(result, p, i + 1, j);
			checkSector(result, p, i    , j + 1);
			checkSector(result, p, i - 1, j);
		}else {
			checkSector(result, p, i + 1, j);
			checkSector(result, p, i - 1, j);
			checkSector(result, p, i    , j - 1);
			checkSector(result, p, i + 1, j + 1);
			checkSector(result, p, i    , j + 1);
			checkSector(result, p, i - 1, j + 1);
		}
		
		return result;
	}
	
	public Sector getSector(int i, int j) {
		if(i < 0 || i >= w || j < 0 || j >= h)
			return null;
		
		return tiles.get(i + j*w);
	}
	
	public Sector getSector(Player p) {
		return getSector(p.i, p.j);
	}
	
	public void render(RenderHelper renderer) {
		for(Sector s : tiles)
			s.render(renderer);
	}
	
	public void save(String filename) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(Main.PATH + "/" +  filename)));
			
			out.writeByte(w);
			out.writeByte(h);
			
			for(int z = 0; z < w*h; z++) {
				Sector s = tiles.get(z);
				out.writeByte(s.getClassId());
				out.writeByte(s.i);
				out.writeByte(s.j);
				if(s.hasAdditionalInformation())
					out.writeByte(s.getAdditionalInfo());
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Point screenToIndicies(Point p) {
		int secX = p.x / (Sector.h + Sector.s);
		int secY = p.y / (int)(2*Sector.r);
		int secPxlX = p.x % (Sector.h + Sector.s);
		int secPxlY = p.y % (int)(2*Sector.r);
		float m = Sector.h / (float)Sector.r;
		int mouseI = secX, mouseJ = secY;
		
		if((secX & 1) == 0) {
			mouseI = secX;
			mouseJ = secY;
			if( secPxlX < (Sector.h - secPxlY * m)) {
				mouseI = secX - 1;
				mouseJ = secY - 1;
			} else if(secPxlX < (- Sector.h + secPxlY * m)) {
				mouseI = secX - 1;
				mouseJ = secY;
			}
		}else {
			if(secPxlY >= Sector.r) {
				if(secPxlX < 2*Sector.h - secPxlY*m) {
					mouseI = secX - 1;
					mouseJ = secY;
				}else {
					mouseI = secX;
					mouseJ = secY;
				}
			}else {
				if(secPxlX < secPxlY * m) {
					mouseI = secX - 1;
					mouseJ = secY;
				}else{
					mouseI = secX;
					mouseJ = secY - 1;
				}
			}
		}
		
		return new Point(mouseI, mouseJ);
	}
}
