package maze;

import java.util.Random;

public class Player extends Object {
	int HP, maxHP, mana, maxmana;

	public Player(Random rand) {
		super();
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		HP = 0;
		maxHP = 0;
		mana = 0;
		maxmana = 0;
		type = ObjectType.PLAYER;
	}
}
