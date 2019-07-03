package maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import java.awt.event.KeyEvent;

enum GameAction {
	NOACTION, RESUMELEVEL, MENU, STATUSSCREEN, ATTACKPICKSCREEN, ITEMSCREEN, BAGSCREEN
}

public class Game {
	public static final char[][][] GUIDE_MAIN = {
		{{Tile.ARROW_LT, Tile.ARROW_RT, Tile.ARROW_UP, Tile.ARROW_DN}, {'Z'}, {'A'}},
		{
			Text.MOVE.toCharArray(),
			Text.ATTACKPICK.toCharArray(),
			Text.MENU.toCharArray()
		}
	};
	public static final char[][][] GUIDE_ATTACKPICK = {
		{{Tile.ARROW_LT, Tile.ARROW_RT, Tile.ARROW_UP, Tile.ARROW_DN}, {'X'}},
		{
			Text.CHOOSE.toCharArray(),
			Text.BACK.toCharArray()
		}
	};
	public static final char[][][] GUIDE_MENU = {
		{{Tile.ARROW_UP, Tile.ARROW_DN}, {'Z'}, {'X'}},
		{
			Text.SELECT.toCharArray(),
			Text.CHOOSE.toCharArray(),
			Text.BACK.toCharArray()
		}
	};
	public static final char[][][] GUIDE_ITEMUSE = {
		{{'Z'}, {'X'}},
		{
			Text.USEITEM.toCharArray(),
			Text.BACK.toCharArray()
		}
	};

	public static final int FLOORAMOUNT = 3;
	public static final int SEARCHRADIUSSQ = Map.ROOM_SIZE * Map.ROOM_SIZE;

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
		the_player.maxHP = rand.nextInt(100) + 1;
		the_player.HP = rand.nextInt(the_player.maxHP + 1);
		the_player.maxMana = rand.nextInt(100) + 1;
		the_player.mana = rand.nextInt(the_player.maxMana + 1);
		the_player.bag.add(0);
		the_player.bag.add(1);
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
		the_map.objects.add(the_player);
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
		playerStatusMana.max = the_player.maxMana;
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

	public boolean moveMonsters() {
		for (Object o : the_map.objects) {
			if (o.type != ObjectType.MONSTER)
				continue;
			if (o.pos.distToSq(the_player.pos) <= SEARCHRADIUSSQ)
				((Monster) o).moveTo(the_map, the_player.pos);
		}
		return true;
	}

	public boolean openAttackPick() {
		DataIntObject attackPickMenu = new DataIntObject();
		attackPickMenu.dataInt = new int[4];
		openedMenu.add(attackPickMenu);
		int i = 0;
		for (int k = 0; k < the_map.objects.size(); k ++) {
			Object o = the_map.objects.get(k);
			if (o.type == ObjectType.MONSTER) {
				StateObject mstrStat = ((Monster) o).getStateObject();
				if (o.pos.x == the_player.pos.x && o.pos.y == the_player.pos.y - 1) {
					mstrStat.pos.x = GUI.MAP_WIDTH / 2 - 7;
					mstrStat.pos.y = GUI.MAP_HEIGHT / 2 - 8;
					attackPickMenu.dataInt[1] = k;
					the_gui.addObject(mstrStat);
					i ++;
				} else if (o.pos.x == the_player.pos.x && o.pos.y == the_player.pos.y + 1) {
					mstrStat.pos.x = GUI.MAP_WIDTH / 2 - 8;
					mstrStat.pos.y = GUI.MAP_HEIGHT / 2 + 2;
					attackPickMenu.dataInt[3] = k;
					the_gui.addObject(mstrStat);
					i ++;
				} else if (o.pos.x == the_player.pos.x - 1 && o.pos.y == the_player.pos.y) {
					mstrStat.pos.x = GUI.MAP_WIDTH / 2 - 18;
					mstrStat.pos.y = GUI.MAP_HEIGHT / 2 - 3;
					attackPickMenu.dataInt[0] = k;
					the_gui.addObject(mstrStat);
					i ++;
				} else if (o.pos.x == the_player.pos.x + 1 && o.pos.y == the_player.pos.y) {
					mstrStat.pos.x = GUI.MAP_WIDTH / 2 + 2;
					mstrStat.pos.y = GUI.MAP_HEIGHT / 2 - 3;
					attackPickMenu.dataInt[2] = k;
					the_gui.addObject(mstrStat);
					i ++;
				}
			} else if (o.type == ObjectType.ITEM) {
				TextObject itemStat = ((Item) o).getTextObject();
				if (o.pos.x == the_player.pos.x && o.pos.y == the_player.pos.y - 1) {
					itemStat.pos.x = (GUI.MAP_WIDTH - itemStat.width) / 2 + 1;
					itemStat.pos.y = GUI.MAP_HEIGHT / 2 - 5;
					attackPickMenu.dataInt[1] = k;
					the_gui.addObject(itemStat);
					i ++;
				} else if (o.pos.x == the_player.pos.x && o.pos.y == the_player.pos.y + 1) {
					itemStat.pos.x = (GUI.MAP_WIDTH - itemStat.width) / 2 + 1;
					itemStat.pos.y = GUI.MAP_HEIGHT / 2 + 3;
					attackPickMenu.dataInt[3] = k;
					the_gui.addObject(itemStat);
					i ++;
				} else if (o.pos.x == the_player.pos.x - 1 && o.pos.y == the_player.pos.y) {
					itemStat.pos.x = GUI.MAP_WIDTH / 2 - itemStat.width - 2;
					itemStat.pos.y = GUI.MAP_HEIGHT / 2 - 1;
					attackPickMenu.dataInt[0] = k;
					the_gui.addObject(itemStat);
					i ++;
				} else if (o.pos.x == the_player.pos.x + 1 && o.pos.y == the_player.pos.y) {
					itemStat.pos.x = GUI.MAP_WIDTH / 2 + 2;
					itemStat.pos.y = GUI.MAP_HEIGHT / 2 - 1;
					attackPickMenu.dataInt[2] = k;
					the_gui.addObject(itemStat);
					i ++;
				}
			}
		}
		for (; i < 4; i ++)
			the_gui.addObject(attackPickMenu);
		focusMenu.add(GameAction.ATTACKPICKSCREEN);
		guideMenu.add(GUIDE_ATTACKPICK);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean closeAttackPick() {
		for (int i = 0; i < 4; i ++)
			the_gui.closeObject();
		focusMenu.remove(focusMenu.size() - 1);
		openedMenu.remove(openedMenu.size() - 1);
		guideMenu.remove(guideMenu.size() - 1);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean openLevelMenu() {
		MenuObject playerMenu = new MenuObject();
		playerMenu.pos.x = 0;
		playerMenu.pos.y = GUI.MAP_HEIGHT - 2 - 7;
		playerMenu.options = new char[5][];
		playerMenu.options[0] = Text.MENU.toCharArray();
		playerMenu.options[1] = new char[0];
		playerMenu.options[2] = Text.JUTSU.toCharArray();
		playerMenu.options[3] = Text.OPENBAG.toCharArray();
		playerMenu.options[4] = Text.BACK.toCharArray();
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
		bagScreen.options = new char[2 + the_player.bag.size()][];
		bagScreen.options[0] = Text.BAG.toCharArray();
		bagScreen.options[1] = new char[0];
		for (int i = 0; i < the_player.bag.size(); i ++)
			bagScreen.options[2 + i] = Text.ITEMS[the_player.bag.get(i)].toCharArray();
		bagScreen.selected = 2;
		bagScreen.color = 1;
		bagScreen.color2 = 3;
		the_gui.addObject(bagScreen);
		TextObject itemInfo = new TextObject();
		itemInfo.pos.x = GUI.MAP_WIDTH / 2 + 1;
		itemInfo.pos.y = 3;
		itemInfo.width = GUI.MAP_WIDTH / 2 - 1;
		itemInfo.height = GUI.MAP_HEIGHT - 11;
		itemInfo.color = 1;
		itemInfo.wrapped = true;
		if (the_player.bag.isEmpty())
			itemInfo.text = Text.BAGEMPTY.toCharArray();
		else
			itemInfo.text = Text.ITEMDESCS[the_player.bag.get(0)].toCharArray();
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

	public boolean openItemScreen(int y) {
		MenuObject itemScreen = new MenuObject();
		itemScreen.pos.x = 2;
		itemScreen.pos.y = y;
		itemScreen.options = new char[1][];
		itemScreen.options[0] = Text.USEITEM.toCharArray();
		itemScreen.selected = 0;
		itemScreen.color = 1;
		itemScreen.color2 = 3;
		the_gui.addObject(itemScreen);
		openedMenu.add(itemScreen);
		focusMenu.add(GameAction.ITEMSCREEN);
		guideMenu.add(GUIDE_ITEMUSE);
		the_gui.setGuide(guideMenu.get(guideMenu.size() - 1));
		the_gui.draw();
		return true;
	}

	public boolean closeItemScreen() {
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
			} else if (keyCode == 90) {
				openAttackPick();
			} else if (keyCode == 37) {
				if (Map.isWalkable(the_map.data[the_player.pos.y][the_player.pos.x - 1]) &&
						!the_map.isThereObject(new v2(the_player.pos.x - 1, the_player.pos.y))) {
					the_player.pos.x --;
					the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
					moveMonsters();
					the_gui.draw();
				}
			} else if (keyCode == 38) {
				if (Map.isWalkable(the_map.data[the_player.pos.y - 1][the_player.pos.x]) &&
						!the_map.isThereObject(new v2(the_player.pos.x, the_player.pos.y - 1))) {
					the_player.pos.y --;
					the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
					moveMonsters();
					the_gui.draw();
				}
			} else if (keyCode == 39) {
				if (Map.isWalkable(the_map.data[the_player.pos.y][the_player.pos.x + 1]) &&
						!the_map.isThereObject(new v2(the_player.pos.x + 1, the_player.pos.y))) {
					the_player.pos.x ++;
					the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
					moveMonsters();
					the_gui.draw();
				}
			} else if (keyCode == 40) {
				if (Map.isWalkable(the_map.data[the_player.pos.y + 1][the_player.pos.x]) &&
						!the_map.isThereObject(new v2(the_player.pos.x, the_player.pos.y + 1))) {
					the_player.pos.y ++;
					the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
					moveMonsters();
					the_gui.draw();
				}
			}
		} else if (focus == GameAction.MENU) {
			if (keyCode == 88) {
				closeLevelMenu();
			} else if (keyCode == 90) {
				if (currentMenu.selected == currentMenu.options.length - 1) {
					closeLevelMenu();
				} else if (currentMenu.selected == 3) {
					openBagScreen();
				}
			} else if (keyCode == 38) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 5) % (currentMenu.options.length - 2)) + 2;
				the_gui.draw();
			} else if (keyCode == 40) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 3) % (currentMenu.options.length - 2)) + 2;
				the_gui.draw();
			}
		} else if (focus == GameAction.ATTACKPICKSCREEN) {
			if (keyCode == 88) {
				closeAttackPick();
			} else if (keyCode >= 37 && keyCode <= 40) {
				the_player.attackPick(the_map, ((DataIntObject) currentMenu).dataInt[keyCode - 37]);
				closeAttackPick();
				the_gui.draw();
			}
		} else if (focus == GameAction.BAGSCREEN) {
			if (keyCode == 88) {
				closeBagScreen();
			} else if (keyCode == 90) {
				openItemScreen(currentMenu.selected + 2);
			} else if (keyCode == 38) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 5) % (currentMenu.options.length - 2)) + 2;
				if (!the_player.bag.isEmpty())
					((TextObject) the_gui.drawList.get(the_gui.drawList.size() - 1)).text =
						Text.ITEMDESCS[the_player.bag.get(currentMenu.selected - 2)].toCharArray();
				the_gui.draw();
			} else if (keyCode == 40) {
				currentMenu.selected = ((currentMenu.options.length + currentMenu.selected - 3) % (currentMenu.options.length - 2)) + 2;
				if (!the_player.bag.isEmpty())
					((TextObject) the_gui.drawList.get(the_gui.drawList.size() - 1)).text =
						Text.ITEMDESCS[the_player.bag.get(currentMenu.selected - 2)].toCharArray();
				the_gui.draw();
			}
		} else if (focus == GameAction.ITEMSCREEN) {
			if (keyCode == 88) {
				closeItemScreen();
			} else if (keyCode == 90) {
				// ketika item dipilih
				the_player.useItem(openedMenu.get(openedMenu.size() - 2).selected - 2);
				closeItemScreen();
				closeBagScreen();
				openBagScreen();
			}
		}
	}
}
