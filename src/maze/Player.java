package maze;

import java.util.ArrayList;
import java.util.Random;

public class Player extends Object {
	public int HP;
	public int maxHP;
	public int mana;
	public int maxMana;
	public ArrayList<Integer> bag;

	public Player(Random rand) {
		super();
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		type = ObjectType.PLAYER;
		icon = Tile.YOLO;
		color = 3;
		HP = 0;
		maxHP = 1;
		mana = 0;
		maxMana = 1;
		bag = new ArrayList<Integer>();
	}

	public void useItem(int itemIndex) {
		bag.remove(itemIndex);
	}

	public void attackPick(Map map, int objectIndex) {
		Object o = map.objects.get(objectIndex);
		if (o.type == ObjectType.ITEM) {
			bag.add(((Item) o).ID);
			map.objects.remove(objectIndex);
		}
	}
}
