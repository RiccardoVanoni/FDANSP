package rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;

public class RenderHelper {
	private Graphics2D graphics = null;
	private int w,h;
	
	public RenderHelper(int width, int height) {
		this.w = width;
		this.h = height;
	}
	
	public void startRendering(Graphics2D g) {
		this.graphics = g;
	}
	
	public void background(Color c) {
		if(graphics != null) {
			graphics.setBackground(c);
			graphics.clearRect(0, 0, w, h);
		}
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		drawLine(x1, y1, x2, y2, Color.BLACK);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2, Color color) {
		if(graphics != null) {
			graphics.setColor(color);
			graphics.drawLine(x1, y1, x2, y2);
		}
	}
	
	public void drawRect(int x, int y, int width, int height, Color c) {
		if(graphics != null) {
			graphics.setColor(c);
			graphics.drawRect(x, y, width, height);
		}
	}
	
	public void fillRect(int x, int y, int width, int height, Color c) {
		if(graphics != null) {
			graphics.setColor(c);
			graphics.fillRect(x, y, width, height);
		}
	}
	
	public void drawHex(int x, int y, int s, int h, int r, Color color) {
		if(graphics != null) {
			Polygon p = new Polygon(new int[]{x + h, x + 0, x + h       , x + h + s   , x + (s << 1), x + h + s}, 
									new int[]{y + 0, y + r, y + (r << 1), y + (r << 1), y + r       , y + 0}, 
									6);
			graphics.setColor(color);
			graphics.drawPolygon(p);
		}
	}
	
	public void fillHex(int x, int y, int s, int h, int r, Color color) {
		if(graphics != null) {
			Polygon p = new Polygon(new int[]{x + h, x + 0, x + h       , x + h + s   , x + (s << 1), x + h + s}, 
									new int[]{y + 0, y + r, y + (r << 1), y + (r << 1), y + r       , y + 0}, 
									6);
			graphics.setColor(color);
			graphics.fillPolygon(p);
		}
	}
	
	public void drawSprite(Image sprite, int px, int py) {
		if(sprite != null)
			drawPartialSprite(sprite, px, py, sprite.getWidth(null), sprite.getHeight(null), 0, 0, sprite.getWidth(null), sprite.getHeight(null));
	}

	public void drawSprite(Image sprite, int px, int py, int width, int height ) {
		if(sprite != null)
			drawPartialSprite(sprite, px, py, width, height, 0, 0, sprite.getWidth(null), sprite.getHeight(null));
	}
	
	public void drawPartialSprite(Image sprite, int px, int py, int ox, int oy, int w, int h) {
		if(sprite != null)
			drawPartialSprite(sprite, px, py, w, h, ox, oy, w, h);
	}
	
	public void drawPartialSprite(Image sprite, int px, int py, int destW, int destH, int ox, int oy, int originW, int originH) {
		if(graphics != null && sprite != null) {
			graphics.drawImage(sprite, px, py, px + destW, py + destH, ox, oy, ox + originW, oy + originH, null);
		}
	}
	
	public void drawString(String s, int x, int y, int dim) {
        drawString(s, x, y, dim, Color.BLACK);
	}
	
	public void drawString(String s, int x, int y, int dim, Color color) {
        graphics.setFont(new Font("consolas", Font.PLAIN, dim));
        graphics.setColor(color);
        
        graphics.drawString(s, x, y);
	}
	public void drawCenteredString(String s, int centerX, int centerY, int dim) {
		drawCenteredString(s, centerX, centerY, dim, Color.black);
	}
	
	public void drawCenteredString(String s, int centerX, int centerY, int dim, Color color) {
		Font font = new Font("consolas", Font.PLAIN, dim);
		
		drawCenteredString(s, centerX, centerY, color, font);
	}
	
	public void drawCenteredString(String s, int centerX, int centerY, Color color, Font font) {
		graphics.setFont(font);
        graphics.setColor(color);
        
		int x = centerX - (graphics.getFontMetrics(font).stringWidth(s) / 2);
		int y = centerY - (int)(graphics.getFontMetrics().getStringBounds(s, graphics).getHeight() / 2) + graphics.getFontMetrics().getAscent();

		graphics.drawString(s, x, y);
	}
	
	public void strokeWeight(int n) {
		graphics.setStroke(new BasicStroke(n));
	}
}
