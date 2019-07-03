package maze;

import java.util.ArrayList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

enum DrawType {
	MENU, DATAINT, CHARACTER, TEXT, STATUS
}

class DrawObject {
	public DrawType type;
	public v2 pos;

	DrawObject() {
		pos = new v2();
	}
}

class MenuObject extends DrawObject {
	public char[][] options;
	public int selected;
	public char color;
	public char color2;

	public MenuObject() {
		super();
		type = DrawType.MENU;
	}
}

class DataIntObject extends MenuObject {
	public int[] dataInt;

	public DataIntObject() {
		super();
		type = DrawType.DATAINT;
	}
}

class CharacterObject extends DrawObject {
	public char character;
	public char color;

	public CharacterObject() {
		super();
		type = DrawType.CHARACTER;
	}
}

class TextObject extends DrawObject {
	public int width;
	public int height;
	public char[] text;
	public char color;
	public boolean wrapped;

	public TextObject() {
		super();
		type = DrawType.TEXT;
		wrapped = false;
	}
}

class ProgressBar {
	public char icon;
	public char color;
	public int value;
	public int min;
	public int max;
}

enum GenderType {
	GENDERLESS, MALE, FEMALE
}

class StateObject extends DrawObject {
	public char[] name;
	public char[] entityType;
	public int level;
	public GenderType gender;
	public ProgressBar[] bars;

	public StateObject() {
		super();
		type = DrawType.STATUS;
	}
}

public class GUI extends JFrame {
	public static final int MAP_WIDTH = 60;
	public static final int MAP_HEIGHT = 25;
	public static final int TILE_WIDTH = 12;
	public static final int TILE_HEIGHT = 18;

	private RenderCanvas canvas;
	private Map backgroundMap;
	public v2 backgroundMapTranslation;
	private int backgroundMapLimit = 0;
	private char[][][] guideList;
	public ArrayList<DrawObject> drawList;

	public GUI() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image tileMap = tk.getImage("maze/charmap2.png");

		canvas = new RenderCanvas(tileMap,
				new Dimension(TILE_WIDTH, TILE_HEIGHT),
				new Dimension(MAP_WIDTH, MAP_HEIGHT));
		//             0            1            2          3             4            5           6              7
		Color[] plt = {Color.BLACK, Color.WHITE, Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.LIGHT_GRAY,
		//			8			9
				Color.ORANGE, Color.PINK};
		canvas.setPalette(plt);

		backgroundMapTranslation = new v2();

		drawList = new ArrayList<DrawObject>();

		setContentPane(canvas);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setTitle("Maze");
		setVisible(true);
		setFocusable(true);
		requestFocusInWindow();
	}

	public void cleanObject() {
		drawList.removeAll(drawList);
	}

	public void setBackground(Map _map) {
		backgroundMap = _map;
	}

	public void limitBackgroundView(int amount) {
		backgroundMapLimit = amount;
	}

	public void setGuide(char[][][] _guide) {
		guideList = _guide;
	}

	public int addObject(DrawObject _entry) {
		drawList.add(_entry);
		return drawList.size() - 1;
	}

	public void closeObject() {
		drawList.remove(drawList.size() - 1);
	}

	public void closeObject(int index) {
		drawList.remove(index);
	}

	public void draw() {
		canvas.draw(backgroundMap, backgroundMapTranslation, backgroundMapLimit);
		drawButtonGuide(guideList);
		for (int i = 0; i < drawList.size(); i ++) {
			DrawObject obj = drawList.get(i);
			switch (obj.type) {
				case MENU:
					drawBoxMenu((MenuObject) obj);
					break;
				case CHARACTER:
					drawChar((CharacterObject) obj);
					break;
				case TEXT:
					drawBoxText((TextObject) obj);
					break;
				case STATUS:
					drawBoxStatus((StateObject) obj);
					break;
			}
		}
		canvas.repaint();
	}

	public void drawBoxStatus(StateObject state) {
		int x = state.pos.x;
		int y = state.pos.y;
		int height = 4 + state.bars.length;
		if (state.entityType.length > 0)
			height ++;
		canvas.drawBox(x, y ++, 16, height, Tile.BOX9, (char) 1);
		canvas.drawText(x + 2, y ++, 12, 1, state.name, (char) 3);
		if (state.entityType.length > 0)
			canvas.drawText(x + 2, y ++, 12, 1, state.entityType, (char) 2);
		if (state.level >= 0) {
			canvas.drawText(x + 2, y, 2, 1, Tile.LEVELTEXT, (char) 1);
			canvas.drawText(x + 5, y, 3, 1, String.format("%-3d", state.level).toCharArray(), (char) 1);
		}
		if (state.gender != GenderType.GENDERLESS)
			canvas.drawChar(x + 13, y, state.gender == GenderType.FEMALE ? Tile.ICON_FEMALE : Tile.ICON_MALE, (char) (state.gender == GenderType.FEMALE ? 6 : 5));
		if (state.level >= 0 || state.gender != GenderType.GENDERLESS)
			y ++;
		for (int i = 0; i < state.bars.length; i ++) {
			canvas.drawChar(x + 2, y + i, state.bars[i].icon, state.bars[i].color);
			int range = state.bars[i].max - state.bars[i].min;
			int boxes = (state.bars[i].value - state.bars[i].min + range / 20) * 10 / range;
			for (int j = 0; j < 10; j ++)
				canvas.drawChar(x + 4 + j, y + i, j < boxes ? Tile.BOX_F4 : Tile.BOX_F2, (char) (j < boxes ? 4 : 7));
		}
	}

	public void drawChar(CharacterObject o) {
		canvas.drawChar(o.pos.x, o.pos.y, o.character, o.color);
	}

	public void drawBoxText(TextObject o) {
		canvas.drawBox(o.pos.x, o.pos.y, o.width, o.height, Tile.BOX9, o.color);
		if (o.wrapped)
			canvas.drawWrapText(o.pos.x + 2, o.pos.y + 1, o.width - 4, o.height - 2, o.text, o.color);
		else
			canvas.drawText(o.pos.x + 2, o.pos.y + 1, o.width - 4, o.height - 2, o.text, o.color);
	}

	public void drawBoxMenu(MenuObject o) {
		int maxLength = 0;
		for (int i = 0; i < o.options.length; i ++) {
			if (o.options[i].length > maxLength)
				maxLength = o.options[i].length;
		}
		canvas.drawBox(o.pos.x, o.pos.y, maxLength + 8, o.options.length + 2, Tile.BOX9, o.color);
		for (int i = 0; i < o.options.length; i ++) {
			if (o.options[i].length == 0) {
				for (int j = 0; j < maxLength + 6; j ++)
					canvas.drawChar(o.pos.x + 1 + j, o.pos.y + 1 + i, Tile.HORIZONTAL_BAR, o.color);
			} else {
				if (i == o.selected)
					canvas.drawChar(o.pos.x + 2, o.pos.y + 1 + i, Tile.ARROW_RT, o.color2);
				canvas.drawText(o.pos.x + 4, o.pos.y + 1 + i, maxLength, 1, o.options[i], i == o.selected ? o.color2 : o.color);
			}
		}
	}

	public void drawButtonGuide(char[][][] guide) {
		for (int i = 0; i < MAP_WIDTH; i ++) {
			canvas.drawChar(i, MAP_HEIGHT - 2, Tile.HORIZONTAL_BAR, (char) 1);
			canvas.drawChar(i, MAP_HEIGHT - 1, ' ', (char) 1);
		}
		int x = 1;
		for (int i = 0; i < guide[1].length; i ++) {
			canvas.drawText(x, MAP_HEIGHT - 1, guide[0][i].length, 1, guide[0][i], (char) 4);
			x += guide[0][i].length + 1;
			canvas.drawText(x, MAP_HEIGHT - 1, guide[1][i].length, 1, guide[1][i], (char) 1);
			x += guide[1][i].length + 2;
		}
	}
}
