package pings;

import java.util.ArrayList;

import players.Player;
import rendering.RenderHelper;

public abstract class Ping {
	protected int i,j;
	protected int turn;
	protected Player source;
	
	protected boolean seen = false;
	
	Ping(int i, int j, int turn, Player source){
		this.i = i;
		this.j = j;
		this.source = source;
		this.turn = turn;
	}
	
	public int getI() {
		return i;
	}
	
	public int getJ() {
		return j;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public void update() {
		seen = true;
	}
	
	public abstract void render(RenderHelper renderer);
	public abstract ArrayList<String> getDescription();
}
