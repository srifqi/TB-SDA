package maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import java.awt.event.KeyEvent;

enum GameAction {
	NOACTION, RESUMELEVEL, MENU, STATUSSCREEN, ATTACKSCREEN, BAGSCREEN
}

public class Game {
	public static final char[][][] GUIDE_MAIN = {
		{{Tile.ARROW_LT, Tile.ARROW_RT, Tile.ARROW_UP, Tile.ARROW_DN}, {'Z'}, {'A'}},
		{
			"Gerak".toCharArray(),
			"Serang/Ambil".toCharArray(),
			"Menu".toCharArray()
		}
	};
	public static final char[][][] GUIDE_MENU = {
		{{Tile.ARROW_UP, Tile.ARROW_DN}, {'Z'}, {'X'}},
		{
			"Ganti Pilihan".toCharArray(),
			"Pilih".toCharArray(),
			"Kembali".toCharArray()
		}
	};

	public static final int FLOORAMOUNT = 3;

	public Random the_rand;
	public Player the_player;
	public GUI the_gui;
	public InputHandler the_handler;
	public ArrayList<Map> the_world;
	public Map the_map;
	public ArrayList<MenuObject> openedMenu;
	public ArrayList<GameAction> focusMenu;
	public ArrayList<char[][][]> guideMenu;

	public Game(Random rand) {
		the_rand = rand;
		the_player = new Player(the_rand);
		the_player.maxHP = rand.nextInt(101);
		the_player.HP = rand.nextInt(the_player.maxHP + 1);
		the_player.maxmana = rand.nextInt(101);
		the_player.mana = rand.nextInt(the_player.maxmana + 1);
		the_gui = new GUI();
		the_handler = new InputHandler(this);
		the_world = new ArrayList<Map>();
		openedMenu = new ArrayList<MenuObject>();
		focusMenu = new ArrayList<GameAction>();
		guideMenu = new ArrayList<char[][][]>();
	}

	public boolean start() {
		for (int i = 0; i < FLOORAMOUNT; i ++)
			the_world.add(Map.createMap(the_rand, i));
		the_map = the_world.get(0);
		the_player.pos.x = 3;
		the_player.pos.y = 3;
		openedMenu.removeAll(openedMenu);
		focusMenu.removeAll(focusMenu);
		guideMenu.removeAll(guideMenu);
		focusMenu.add(GameAction.NOACTION);
		guideMenu.add(GUIDE_MAIN);
		return resumeLevel();
	}

	public boolean resumeLevel() {
		the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
		the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
		the_gui.setBackground(the_map);
		the_gui.limitBackgroundView(10);
		the_gui.cleanObject();
		CharacterObject playerObject = new CharacterObject();
		playerObject.pos.x = GUI.MAP_WIDTH / 2;
		playerObject.pos.y = GUI.MAP_HEIGHT / 2;
		playerObject.character = Tile.YOLO;
		playerObject.color = 3;
		the_gui.addObject(playerObject);
		String levelName = NameRandomizer.next(the_rand, 10) + " L" + (1 + the_rand.nextInt(9));
		levelName = levelName.substring(0, 1).toUpperCase() + levelName.substring(1);
		TextObject levelNameObject = new TextObject();
		levelNameObject.pos.x = GUI.MAP_WIDTH - levelName.length() - 4;
		levelNameObject.pos.y = 0;
		levelNameObject.width = levelName.length() + 4;
		levelNameObject.height = 3;
		levelNameObject.text = levelName.toCharArray();
		levelNameObject.color = 1;
		the_gui.addObject(levelNameObject);
		StateObject playerStatus = new StateObject();
		playerStatus.name = the_player.name.toCharArray();
		playerStatus.entityType = new char[0];
		playerStatus.level = the_rand.nextInt(10) + 5;
		playerStatus.gender = GenderType.MALE;
		ProgressBar playerStatusHP = new ProgressBar();
		playerStatusHP.icon = 28;
		playerStatusHP.color = 2;
		playerStatusHP.value = the_player.HP;
		playerStatusHP.min = 0;
		playerStatusHP.max = the_player.maxHP;
		ProgressBar playerStatusMana = new ProgressBar();
		playerStatusMana.icon = 29;
		playerStatusMana.color = 3;
		playerStatusMana.value = the_player.mana;
		playerStatusMana.min = 0;
		playerStatusMana.max = the_player.maxmana;
		playerStatus.bars = new ProgressBar[2];
		playerStatus.bars[0] = playerStatusHP;
		playerStatus.bars[1] = playerStatusMana;
		playerStatus.pos.x = GUI.MAP_WIDTH - 16;
		playerStatus.pos.y = GUI.MAP_HEIGHT - 2 - 6;
		the_gui.addObject(playerStatus);
		focusMenu.add(GameAction.RESUMELEVEL);
		guideMenu.add(GUIDE_MAIN);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean openLevelMenu() {
		MenuObject playerMenu = new MenuObject();
		playerMenu.pos.x = 0;
		playerMenu.pos.y = GUI.MAP_HEIGHT - 2 - 8;
		playerMenu.options = new char[6][];
		playerMenu.options[0] = "Menu".toCharArray();
		playerMenu.options[1] = new char[0];
		playerMenu.options[2] = "Status".toCharArray();
		playerMenu.options[3] = "Jurus".toCharArray();
		playerMenu.options[4] = "Buka Tas".toCharArray();
		playerMenu.options[5] = "Kembali".toCharArray();
		playerMenu.selected = 2;
		playerMenu.color = 1;
		playerMenu.color2 = 3;
		the_gui.addObject(playerMenu);
		openedMenu.add(playerMenu);
		focusMenu.add(GameAction.MENU);
		guideMenu.add(GUIDE_MENU);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean closeLevelMenu() {
		the_gui.closeObject();
		focusMenu.remove(focusMenu.size() - 1);
		openedMenu.remove(openedMenu.size() - 1);
		guideMenu.remove(guideMenu.size() - 1);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean openBagScreen() {
		MenuObject bagScreen = new MenuObject();
		bagScreen.pos.x = 0;
		bagScreen.pos.y = 0;
		bagScreen.options = new char[4][];
		bagScreen.options[0] = "Tas".toCharArray();
		bagScreen.options[1] = new char[0];
		bagScreen.options[2] = "HP +10".toCharArray();
		bagScreen.options[3] = "Mana +10".toCharArray();
		bagScreen.selected = 2;
		bagScreen.color = 1;
		bagScreen.color2 = 3;
		the_gui.addObject(bagScreen);
		TextObject itemInfo = new TextObject();
		itemInfo.pos.x = GUI.MAP_WIDTH / 2 + 1;
		itemInfo.pos.y = 3;
		itemInfo.width = GUI.MAP_WIDTH / 2 - 1;
		itemInfo.height = GUI.MAP_HEIGHT - 11;
		itemInfo.text = "Item yang menyembuhkan sebesar 10 HP.".toCharArray();
		itemInfo.color = 1;
		the_gui.addObject(itemInfo);
		openedMenu.add(bagScreen);
		focusMenu.add(GameAction.BAGSCREEN);
		guideMenu.add(GUIDE_MENU);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean closeBagScreen() {
		the_gui.closeObject();
		the_gui.closeObject();
		focusMenu.remove(focusMenu.size() - 1);
		openedMenu.remove(openedMenu.size() - 1);
		guideMenu.remove(guideMenu.size() - 1);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public void onKeyTyped(KeyEvent ev) {}
	public void onKeyReleased(KeyEvent ev) {}

	public void onKeyPressed(KeyEvent ev) {
		int keyCode = ev.getKeyCode();
		GameAction focus = focusMenu.isEmpty() ? null : focusMenu.get(focusMenu.size() - 1);
		MenuObject currentMenu = openedMenu.isEmpty() ? null : openedMenu.get(openedMenu.size() - 1);
		if (focus == GameAction.RESUMELEVEL) {
			if (keyCode == 65) {
				openLevelMenu();
			} else if (keyCode == 37) {
				if (Map.isWalkable(the_map.data[the_player.pos.y][the_player.pos.x - 1])) {
					the_player.pos.x --;
					the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
					the_gui.draw();
				}
			} else if (keyCode == 38) {
				if (Map.isWalkable(the_map.data[the_player.pos.y - 1][the_player.pos.x])) {
					the_player.pos.y --;
					the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
					the_gui.draw();
				}
			} else if (keyCode == 39) {
				if (Map.isWalkable(the_map.data[the_player.pos.y][the_player.pos.x + 1])) {
					the_player.pos.x ++;
					the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
					the_gui.draw();
				}
			} else if (keyCode == 40) {
				if (Map.isWalkable(the_map.data[the_player.pos.y + 1][the_player.pos.x])) {
					the_player.pos.y ++;
					the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
					the_gui.draw();
				}
			}
		} else if (focus == GameAction.MENU) {
			if (keyCode == 88) {
				closeLevelMenu();
			} else if (keyCode == 90) {
				if (currentMenu.selected == currentMenu.options.length - 1) {
					closeLevelMenu();
				} else if (currentMenu.selected == 4) {
					openBagScreen();
				}
			} else if (keyCode == 38) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 5) % (currentMenu.options.length - 2)) + 2;
				the_gui.draw();
			} else if (keyCode == 40) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 3) % (currentMenu.options.length - 2)) + 2;
				the_gui.draw();
			}
		} else if (focus == GameAction.BAGSCREEN) {
			if (keyCode == 88) {
				closeBagScreen();
			} else if (keyCode == 90) {
				// ketika item dipilih
			} else if (keyCode == 38) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 5) % (currentMenu.options.length - 2)) + 2;
				the_gui.draw();
			} else if (keyCode == 40) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 3) % (currentMenu.options.length - 2)) + 2;
				the_gui.draw();
			}
		}
	}
}
