package maze;

import java.util.Random;

public class Item extends Object {
	public int ID;

	public Item(Random rand) {
		super();
		type = ObjectType.ITEM;
		icon = 'I';
		color = 6;
		ID = rand.nextInt(Text.ITEMS.length);
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
