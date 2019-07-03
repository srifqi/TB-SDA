package maze;

import java.util.Random;

public class Monster extends Object {
	public int HP;
	public int maxHP;
	public int level;
	public GenderType gender;

	public Monster(Random rand) {
		super();
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		type = ObjectType.MONSTER;
		level = rand.nextInt(10) + 5;
		HP = 0;
		maxHP = 1;
		gender = rand.nextInt(2) < 1 ? GenderType.MALE : GenderType.FEMALE;
	}

	public void moveTo(Map map, v2 target) {
		v2 nextPos = Map.nextMovement(Map.AStar(map, pos, target), pos, target);
		if (!map.isThereObject(nextPos))
			pos = nextPos;
	}

	public StateObject getStateObject() {
		StateObject mstrStat = new StateObject();
		mstrStat.name = name.toCharArray();
		mstrStat.entityType = "Monster".toCharArray();
		mstrStat.level = level;
		mstrStat.gender = gender;
		ProgressBar mstrStatHP = new ProgressBar();
		mstrStatHP.icon = 28;
		mstrStatHP.color = 2;
		mstrStatHP.value = HP;
		mstrStatHP.min = 0;
		mstrStatHP.max = maxHP;
		mstrStat.bars = new ProgressBar[1];
		mstrStat.bars[0] = mstrStatHP;
		return mstrStat;
	}
}
