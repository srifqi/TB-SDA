package maze;

import java.util.Random;

public class Item extends Object {
	public static final char ASGARD = 0;
	public static final char CHECKPOINT = 1;
	public static final char DIAMOND = 2;
	public static final char ENERGY = 3;
	public static final char POTION = 4;
	public static final char LEFT_GATE = 5;
	public static final char RIGHT_GATE = 6;
	public static final char BOTTOM_GATE = 7;
	public static final char TOP_GATE = 8;
	public static final char UP_LADDER = 9;
	public static final char DOWN_LADDER = 10;

	public char ID;

	public Item(char _ID) {
		super();
		type = ObjectType.ITEM;
		icon = (char) (Tile.ASGARD + _ID); // type-casting becacuse of the operator
		color = 6;
		ID = _ID;
		name = Text.ITEMS[ID];
	}

	public TextObject getTextObject() {
		TextObject itemStat = new TextObject();
		itemStat.text = name.toCharArray();
		itemStat.width = name.length() + 4;
		itemStat.color = 1;
		itemStat.height = 3;
		return itemStat;
	}
}
