package maze;

import java.util.Random;

public class Monster extends Object {
	int HP, maxHP;

	public Monster(Random rand) {
		super();
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		HP = 0;
		maxHP = 0;
		type = ObjectType.MONSTER;
	}

	public void moveTo(Map map, v2 target) {
		pos = Map.nextMovement(Map.AStar(map, pos, target), pos, target);
	}
}
