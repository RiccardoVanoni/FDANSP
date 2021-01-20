package main;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import maps.Map;
import pings.Attack;
import players.Player;
import rendering.RenderHelper;
import tiles.AlienSector;
import tiles.HumanSector;
import tiles.Sector;
import tiles.WallSector;
import util.Assets;
import util.Button;
import util.UtilFunc;

public class Game implements Module {

	private final Engine parent;

	private int numPlayer = 8;
	private int numHuman = numPlayer / 2;
	private int numAlien = numPlayer - numHuman;
	private ArrayList<String> playersName;

	private ArrayList<Player> players;
	private ArrayList<Player> deadPlayers;
	private int currentPlayer;
	private Set<Sector> movements;
	private int numTurn;

	private Map currentMap;

	private GAMESTATE currentState;
	private GAMESTATE nextState;

	private String tooltipTitle = "";
	private ArrayList<String> tooltipBody = new ArrayList<String>();
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private boolean clearButtons = false;
	private boolean reloadPlayerPings = false;
	private int reloadPingsI = 0;
	private int reloadPingsJ = 0;

	public Game(Engine parent, Map map, int numPlayers, ArrayList<String> playersName) {
		this.parent = parent;

		numPlayer = numPlayers;
		numHuman = numPlayer / 2;
		numAlien = numPlayer - numHuman;

		currentMap = map;

		this.playersName = playersName;

		currentState = GAMESTATE.GAMESTART;
		nextState = GAMESTATE.GAMESTART;
	}

	@Override
	public void update(double elapsedTime) {
		if (parent.leftMouseClicked)
			for (Button b : buttons)
				if (parent.mouseI == b.i && parent.mouseJ == b.j) {
					b.buttonPressed();
					parent.leftMouseClicked = false;
				}

		if (reloadPlayerPings) {
			loadPingsFromPlayer(reloadPingsI, reloadPingsJ);
			reloadPlayerPings = false;
		}

		if (parent.rightMouseClicked)
			loadPingsFromPlayer(parent.mouseI, parent.mouseJ);

		switch (currentState) {
		case GAMESTART: {
			players = new ArrayList<Player>();
			deadPlayers = new ArrayList<Player>();

			Sector s = currentMap.tiles.stream().filter(a -> a instanceof HumanSector).collect(Collectors.toList())
					.get(0);
			for (int z = 0; z < numHuman; z++)
				players.add(new Player(s.i, s.j, Player.TYPE.HUMAN, z));

			s = currentMap.tiles.stream().filter(a -> a instanceof AlienSector).collect(Collectors.toList()).get(0);
			for (int z = 0; z < numAlien; z++)
				players.add(new Player(s.i, s.j, Player.TYPE.ALIEN, z));

			java.util.Collections.shuffle(players);

			for (int i = 0; i < players.size(); i++) {
				players.get(i).setName(playersName.get(i));
			}

			numTurn = 1;

			currentPlayer = 0;
			nextState = GAMESTATE.CHANGINGTURN;
			break;
		}
		case BEGINTURN: {
			Player p = players.get(currentPlayer);
			movements = new HashSet<Sector>();
			movements.addAll(currentMap.getMovementsFrom(p, p.i, p.j));

			for (int i = 0; i < p.getNumMovements() - 1; i++) {
				HashSet<Sector> newMovements = new HashSet<Sector>();
				for (Sector s : movements) {
					newMovements.addAll(currentMap.getMovementsFrom(p, s.i, s.j));
				}

				movements.addAll(newMovements);
			}

			movements.remove(currentMap.getSector(p.i, p.j));

			nextState = GAMESTATE.MOVE;

			if (numTurn > 40) {
				nextState = GAMESTATE.ALIENWIN;
				break;
			}

			if (!p.notifications.isEmpty())
				nextState = GAMESTATE.DISPLAYNOTIFICATIONS;

		}
			break;

		case DISPLAYNOTIFICATIONS:
			if (parent.keyPressed == ' ') {
				players.get(currentPlayer).notifications.pop();
				if (players.get(currentPlayer).notifications.isEmpty()) {
					if (players.get(currentPlayer).isAlive())
						nextState = GAMESTATE.MOVE;
					else {
						nextPlayer();
						nextState = GAMESTATE.CHANGINGTURN;
					}
				}

			}
			break;

		case MOVE:
			if (movements.contains(currentMap.getSector(parent.mouseI, parent.mouseJ)) && parent.leftMouseClicked) {
				players.get(currentPlayer).setPos(parent.mouseI, parent.mouseJ);

				if (players.get(currentPlayer).canAttack())
					nextState = GAMESTATE.CHOOSEACTION;
				else
					nextState = currentMap.getSector(players.get(currentPlayer).i, players.get(currentPlayer).j)
							.getMovementAction();
			}
			break;
		case CHOOSEACTION: {
			if (!GAMESTATE.CHOOSEACTION.isSetupped()) {
				buttons.add(new Button(27, 3, Assets.getInstance().getSprite("right_arrow"), new Color(0x84DE02),
						Assets.getInstance().I18N("continue")) {

					@Override
					public void buttonPressed() {
						Player p = players.get(currentPlayer);
						nextState = currentMap.getSector(p.i, p.j).getMovementAction();
						clearButtons = true;
					}

				});

				buttons.add(new Button(25, 3, Assets.getInstance().getSprite("knife"), new Color(0xB30000),
						Assets.getInstance().I18N("attack")) {

					@Override
					public void buttonPressed() {
						Player killer = players.get(currentPlayer);
						ArrayList<Player> killedPlayers = new ArrayList<Player>();

						for (Player p : players)
							if (p != killer && p.isAlive()) {
								if (p.i == killer.i && p.j == killer.j) {
									p.notifications.add(String.format(Assets.getInstance().I18N("deathNotification"),
											killer.getName()));
									p.kill();

									killedPlayers.add(p);
								} else {
									p.notifications.add(String.format(Assets.getInstance().I18N("attackNotification"),
											killer.getName(), currentMap.getSector(killer).getCoords()));

									p.pings.add(new Attack(killer.i, killer.j, numTurn, killer));
								}
							}

						String tooltip = String.format(Assets.getInstance().I18N("attackTooltipBody"),
								killedPlayers.size());
						addTooltip(Assets.getInstance().I18N("attackTooltipTitle"), UtilFunc.warpText(tooltip, 15));

						nextState = GAMESTATE.ENDTURN;
						clearButtons = true;

					}

				});

				GAMESTATE.CHOOSEACTION.setupFinished();
			}

		}
			break;
		case CHANGINGTURN:
			buttons.clear();
			clearTooltip();
			if (parent.keyPressed == ' ') {
				nextState = GAMESTATE.BEGINTURN;
			}
			break;
		case CHOOSESECTORCARD:
			switch ((new Random()).nextInt(3)) {
			case 0:// do nothing
				nextState = GAMESTATE.ENDTURN;
				break;
			case 1:// noise in your sector
				addTooltip(Assets.getInstance().I18N("noise1Title").toUpperCase(),
						UtilFunc.warpText(Assets.getInstance().I18N("noise1Body"), 15));

				addNoise(currentPlayer, players.get(currentPlayer).i, players.get(currentPlayer).j);

				nextState = GAMESTATE.ENDTURN;
				break;
			case 2:// noise in any sector
				addTooltip(Assets.getInstance().I18N("noise2Title").toUpperCase(),
						UtilFunc.warpText(Assets.getInstance().I18N("noise2Body"), 15));

				nextState = GAMESTATE.NOISEINSECTOR;
				break;
			}
		case NOISEINSECTOR:
			if (parent.leftMouseClicked) {
				Sector s = currentMap.getSector(parent.mouseI, parent.mouseJ);

				if (s instanceof WallSector) {
					parent.leftMouseClicked = false;
					break;
				}

				addNoise(currentPlayer, s.i, s.j);

				String title = String.format(Assets.getInstance().I18N("noise3Title"), s.getCoords()).toUpperCase();
				addTooltip(title, UtilFunc.warpText(Assets.getInstance().I18N("noise3Body"), 15));

				nextState = GAMESTATE.ENDTURN;
				parent.leftMouseClicked = false;
			}
			break;

		case ENDTURN:
			if (!GAMESTATE.ENDTURN.isSetupped()) {
				buttons.add(new Button(26, 3, Assets.getInstance().getSprite("right_arrow"), new Color(0x84DE02),
						Assets.getInstance().I18N("endTurn")) {

					@Override
					public void buttonPressed() {
						if (nextPlayer()) {
							nextState = GAMESTATE.ALIENWIN;
							clearButtons = true;
							clearTooltip();
						} else {
							nextState = GAMESTATE.CHANGINGTURN;
							clearButtons = true;
						}
					}

				});

				GAMESTATE.ENDTURN.setupFinished();
			}
			break;

		case HUMANWIN: {
			if (!GAMESTATE.HUMANWIN.isSetupped()) {
				clearTooltip();
				clearButtons = true;

				GAMESTATE.HUMANWIN.setupFinished();
			}
		}
			break;

		default:
			break;
		}

		if (clearButtons) {
			buttons.clear();
			clearButtons = false;
		}

		if (currentState != nextState) {
			currentState.reset();
			currentState = nextState;
		}
	}

	@Override
	public void render(RenderHelper renderer) {
		if (currentState == GAMESTATE.GAMESTART)
			return;

		int mapPixelW = (23 * 3 + 1) * Sector.h;

		renderer.fillRect(mapPixelW, 0, parent.getWidth() - mapPixelW, parent.getHeight(), Color.white);
		renderer.drawLine(mapPixelW, 0, mapPixelW, parent.getWidth());

		renderer.strokeWeight(3);
		renderer.drawHex(Sector.getScreenX(26), Sector.getScreenY(26, 1), Sector.s, Sector.h, (int) Sector.r,
				new Color(0x606060));
		renderer.strokeWeight(1);
		renderer.drawCenteredString(String.valueOf(numTurn), Sector.getScreenX(26) + Sector.s,
				Sector.getScreenY(26, 1) + (int) Sector.r, Sector.s, new Color(0x606060));
		renderer.drawCenteredString(Assets.getInstance().I18N("turn").toUpperCase(), Sector.getScreenX(26) + Sector.s,
				Sector.getScreenY(26, 0) + (int) Sector.r, Sector.s, new Color(0x606060));

		for (Button b : buttons)
			b.render(renderer);

		renderTooltip(renderer, mapPixelW, parent.getHeight() / 3 + 20, parent.getWidth() - mapPixelW);

		currentMap.render(renderer);

		for (int z = players.get(currentPlayer).pings.size() - 1; z >= 0; z--)
			players.get(currentPlayer).pings.get(z).render(renderer);

		switch (currentState) {
		case BEGINTURN:
			break;
		case GAMESTART:
			break;
		case MOVE:
			for (Sector s : movements) {
				renderer.drawHex(Sector.getScreenX(s.i), Sector.getScreenY(s.i, s.j), Sector.s, Sector.h,
						(int) Sector.r, Color.green);
			}
		case CHOOSEACTION:
			players.get(currentPlayer).render(renderer);
			break;

		case ENDTURN:
			players.get(currentPlayer).render(renderer);
			break;

		case CHANGINGTURN:

			renderer.fillRect(0, 0, mapPixelW, parent.getHeight(), new Color(0xE0E0E0));
			renderer.drawCenteredString(
					Assets.getInstance().I18N("passTurn") + " " + players.get(currentPlayer).getName() + " ("
							+ Assets.getInstance().I18N("pressSpace") + ")",
					mapPixelW / 2, parent.getHeight() / 2, 40, Color.black);

			break;

		case DISPLAYNOTIFICATIONS:

			renderer.fillRect(0, 0, mapPixelW, parent.getHeight(), new Color(0xE0E0E0));
			ArrayList<String> note = UtilFunc.warpText(players.get(currentPlayer).notifications.peek(), 25);
			note.add("(" + Assets.getInstance().I18N("pressSpace") + ")");

			for (int i = 0; i < note.size(); i++) {
				renderer.drawCenteredString(note.get(i), mapPixelW / 2,
						parent.getHeight() / 2 + (i - note.size() / 2) * 40, 40, Color.red);
			}

			break;

		case NOISEINSECTOR:
			renderer.drawHex(Sector.getScreenX(parent.mouseI) + 2, Sector.getScreenY(parent.mouseI, parent.mouseJ) + 2,
					Sector.s - 2, Sector.h - 1, (int) Sector.r - 2, Color.YELLOW);
			players.get(currentPlayer).render(renderer);
			break;

		case HUMANWIN: {
			renderer.background(new Color(0));
			renderer.drawSprite(Assets.getInstance().getSprite("title"), -70, 0);

			Font font = new Font("OCR A Extended", Font.BOLD, 40);
			String winMessage = String.format(Assets.getInstance().I18N("humanWinMessage"),
					players.get(currentPlayer).getName());
			ArrayList<String> aMessage = UtilFunc.warpText(winMessage, 15);

			int titleX = parent.getWidth() * 2 / 3;
			int titleY = parent.getHeight() / 2;
			int titleStartY = titleY - (int) ((aMessage.size() / 2.0f) * (font.getSize() + 5.0f));

			for (int i = 0; i < aMessage.size(); i++) {
				renderer.drawCenteredString(aMessage.get(i), titleX, titleStartY + i * (font.getSize() + 5),
						Color.white, font);
			}

			renderer.drawLine(titleX - 200, titleStartY - 20, titleX + 200, titleStartY - 20, Color.white);
			renderer.drawLine(titleX - 200, titleStartY + (aMessage.size() - 1) * (font.getSize() + 5) + 30,
					titleX + 200, titleStartY + (aMessage.size() - 1) * (font.getSize() + 5) + 30, Color.white);
		}
			break;

		case ALIENWIN: {
			renderer.background(new Color(0));
			renderer.drawSprite(Assets.getInstance().getSprite("end_screen"), -70, 0);

			Font font = new Font("OCR A Extended", Font.BOLD, 40);
			String winMessage = String.format(Assets.getInstance().I18N("alienWinMessage"));
			ArrayList<String> aMessage = UtilFunc.warpText(winMessage, 15);

			int titleX = parent.getWidth() * 2 / 3;
			int titleY = parent.getHeight() / 2;
			int titleStartY = titleY - (int) ((aMessage.size() / 2.0f) * (font.getSize() + 5.0f));

			for (int i = 0; i < aMessage.size(); i++) {
				renderer.drawCenteredString(aMessage.get(i), titleX, titleStartY + i * (font.getSize() + 5),
						Color.white, font);
			}

			renderer.drawLine(titleX - 200, titleStartY - 20, titleX + 200, titleStartY - 20, Color.white);
			renderer.drawLine(titleX - 200, titleStartY + (aMessage.size() - 1) * (font.getSize() + 5) + 30,
					titleX + 200, titleStartY + (aMessage.size() - 1) * (font.getSize() + 5) + 30, Color.white);
		}
			break;
		default:
			break;

		}
		if (currentState != GAMESTATE.HUMANWIN)
			renderer.drawHex(Sector.getScreenX(parent.mouseI), Sector.getScreenY(parent.mouseI, parent.mouseJ),
					Sector.s, Sector.h, (int) Sector.r, Color.blue);
	}

	private void addNoise(int surcePlayerId, int i, int j) {
		for (int z = 0; z < players.size(); z++)
			if (z != surcePlayerId)
				players.get(z).pings.add(new pings.Noise(i, j, numTurn, players.get(surcePlayerId)));
	}

	private void renderTooltip(RenderHelper renderer, int startX, int startY, int width) {
		if (tooltipTitle != "")
			renderer.drawCenteredString(tooltipTitle, startX + width / 2, startY, 20);
		if (!tooltipBody.isEmpty())
			for (int i = 0; i < tooltipBody.size(); i++)
				renderer.drawCenteredString(tooltipBody.get(i), startX + width / 2, startY + 40 + i * 30, 20);
	}

	public void addTooltip(String title, ArrayList<String> body) {
		tooltipTitle = title;
		tooltipBody = body;

		buttons.removeIf(o -> o.hasTag("tooltipButtons"));
	}

	public void clearTooltip() {
		tooltipTitle = "";
		tooltipBody.clear();
		buttons.removeIf(o -> o.hasTag("tooltipButtons"));
	}

	private void loadPingsFromPlayer(int i, int j) {
		clearTooltip();
		players.get(currentPlayer).loadPingsFor(this, buttons, i, j);
	}

	public void reloadPlayerPings(int i, int j) {
		reloadPlayerPings = true;
		reloadPingsI = i;
		reloadPingsJ = j;
	}

	/**
	 * cycles to the next player
	 * 
	 * @return returns true if there are no humans left, otherwise returns false
	 */
	private boolean nextPlayer() {
		int humansAliveCount = 0;
		for (Player p : players)
			if (p.isAlive() && p.getType() == Player.TYPE.HUMAN)
				humansAliveCount++;

		if (humansAliveCount == 0)
			return true;

		players.get(currentPlayer).updatePings();

		if (!players.get(currentPlayer).isAlive()) {
			Player p = players.get(currentPlayer);
			players.remove(p);
			deadPlayers.add(p);
		}

		currentPlayer++;
		if (currentPlayer >= players.size()) {
			numTurn++;
			currentPlayer = 0;
		}

		return false;
	}

	public static enum GAMESTATE {
		GAMESTART, BEGINTURN, MOVE, CHOOSEACTION, CHANGINGTURN, CHOOSESECTORCARD, NOISEINSECTOR, ENDTURN,
		DISPLAYNOTIFICATIONS, HUMANWIN, ALIENWIN;

		private boolean setupped = false;

		public boolean isSetupped() {
			return setupped;
		}

		public void setupFinished() {
			this.setupped = true;
		}

		public void reset() {
			setupped = false;
		}
	}

}
