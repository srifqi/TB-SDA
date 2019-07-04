package maze;

import java.util.Random;

public class Monster extends Object {
	public static final char COCKROACHES = 0;
	public static final char CREAPER = 1;
	public static final char DETROIT = 2;
	public static final char KABUTO = 3;
	public static final char LOLLIPOP = 4;
	public static final char MUST_ROOM = 5;
	public static final char PEA_SHOOT = 6;
	public static final char POCO_POCO = 7;
	public static final char RED_SNACK = 8;
	public static final char SLIME = 9;

	public static final int[][] BASESTATS = {
		{35, 50, 200}, {45, 20, 175}, {25, 70, 50},
		{50, 40, 300}, {20, 20, 70}, {15, 10, 60},
		{20, 25, 190}, {10, 30, 125}, {100, 150, 700},
		{15, 40, 230}
	};
	public static final int[] ULTISTATS = {
		50, 75, 90, 50, 25, 20, 80, 40, 300, 45
	};

	public char species;
	public int level;
	public GenderType gender;
	public int maxHP;
	public int HP;
	public int ATK;
	public int DEF;
	public int[] IV;

	public Monster(Random rand, char _species, int _level) {
		super();
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		type = ObjectType.MONSTER;
		species = _species;
		icon = (char) (Tile.COCKROACHES + species);
		level = _level;
		gender = rand.nextInt(2) < 1 ? GenderType.MALE : GenderType.FEMALE;
		IV = new int[3];
		IV[0] = rand.nextInt(32);
		IV[1] = rand.nextInt(32);
		IV[2] = rand.nextInt(32);
		ATK = (((2 * BASESTATS[species][0] + IV[0] * level) / 100) + 5) * 1;
		DEF = (((2 * BASESTATS[species][1] + IV[1] * level) / 100) + 5) * 1;
		maxHP = ((2 * BASESTATS[species][2] + IV[2] * level) / 100) + level + 10;
		HP = maxHP;
	}

	public void moveTo(Map map, v2 target) {
		v2 nextPos = Map.nextMovement(Map.AStar(map, pos, target), pos, target);
		if (!map.isThereObject(nextPos))
			pos = nextPos;
	}

	public void attack(Player foe) {
		int attackPower = ULTISTATS[species];
		int damage = (((2 * level / 5) + 2) * attackPower * ATK / foe.DEF / 50) + 2;
		foe.HP -= damage;
		System.out.println(name + " attacks with " + damage + " HP. " + foe.name + " has " + foe.HP + " HP now.");
	}

	public StateObject getStateObject() {
		StateObject mstrStat = new StateObject();
		mstrStat.name = name.toCharArray();
		mstrStat.entityType = Text.SPECIES[species].toCharArray();
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
