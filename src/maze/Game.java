package maze;

import java.util.Random;

import java.awt.event.KeyEvent;

public class Game {
	public static final char[][][] GUIDE_MAIN = {
		{{192, 193, 194, 195}, {90}, {65}},
		{
			"Gerak".toCharArray(),
			"Serang".toCharArray(),
			"Menu".toCharArray()
		}
	};
	public static final char[][][] GUIDE_MENU = {
		{{194, 195}, {90}, {88}},
		{
			"Ganti Pilihan".toCharArray(),
			"Pilih".toCharArray(),
			"Kembali".toCharArray()
		}
	};

	public Random the_rand;
	public Player the_player;
	public GUI the_gui;
	public InputHandler the_handler;
	public Map the_map;

	public Game(Random rand) {
		the_rand = rand;
		the_player = new Player(the_rand);
		the_gui = new GUI();
		the_handler = new InputHandler(this);
	}

	public boolean start() {
		the_map = Map.createMap(the_rand);
		return resumeLevel();
	}

	public boolean resumeLevel() {
		the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
		the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
		the_gui.setBackground(the_map);
		the_gui.limitBackgroundView(10);
		the_gui.setGuide(GUIDE_MAIN);
		the_gui.cleanObject();
		CharacterObject playerObject = new CharacterObject();
		playerObject.pos.x = GUI.MAP_WIDTH / 2;
		playerObject.pos.y = GUI.MAP_HEIGHT / 2;
		playerObject.character = 'X';
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
		playerStatusHP.value = 80;
		playerStatusHP.min = 0;
		playerStatusHP.max = 100;
		ProgressBar playerStatusMana = new ProgressBar();
		playerStatusMana.icon = 29;
		playerStatusMana.color = 3;
		playerStatusMana.value = 70;
		playerStatusMana.min = 0;
		playerStatusMana.max = 100;
		playerStatus.bars = new ProgressBar[2];
		playerStatus.bars[0] = playerStatusHP;
		playerStatus.bars[1] = playerStatusMana;
		playerStatus.pos.x = GUI.MAP_WIDTH - 16;
		playerStatus.pos.y = GUI.MAP_HEIGHT - 2 - 6;
		the_gui.addObject(playerStatus);
		//MenuObject 
		the_gui.draw();
		return true;
	}

	public void onKeyTyped(KeyEvent ev) {}
	public void onKeyReleased(KeyEvent ev) {}

	public void onKeyPressed(KeyEvent ev) {
		int keyCode = ev.getKeyCode();
		//System.out.println("key pressed: " + keyCode);
		if (keyCode == 37) {
			the_player.pos.x --;
			the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
			the_gui.draw();
		} else if (keyCode == 38) {
			the_player.pos.y --;
			the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
			the_gui.draw();
		} else if (keyCode == 39) {
			the_player.pos.x ++;
			the_gui.backgroundMapTranslation.x = GUI.MAP_WIDTH / 2 - the_player.pos.x;
			the_gui.draw();
		} else if (keyCode == 40) {
			the_player.pos.y ++;
			the_gui.backgroundMapTranslation.y = GUI.MAP_HEIGHT / 2 - the_player.pos.y;
			the_gui.draw();
		}
	}
}