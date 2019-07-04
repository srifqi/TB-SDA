package maze;

import java.util.ArrayList;
import java.util.Random;

public class Player extends Object {
	public int level;
	public GenderType gender;
	public int maxHP;
	public int HP;
	public int ATK;
	public int DEF;
	public int[] IV;
	public ArrayList<Character> bag;
	public int EXP;

	public Player(Random rand, int _level) {
		super();
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		type = ObjectType.PLAYER;
		icon = Tile.YOLO;
		color = 3;
		gender = rand.nextInt(2) < 1 ? GenderType.MALE : GenderType.FEMALE;
		IV = new int[3];
		IV[0] = rand.nextInt(32);
		IV[1] = rand.nextInt(32);
		IV[2] = rand.nextInt(32);
		updateLevel(_level);
		EXP = getEXPAtLevel(level);
		HP = maxHP;
		bag = new ArrayList<Character>();
	}

	public void updateLevel(int _level) {
		level = _level;
		ATK = (((2 * 25 + IV[0] * level) / 100) + 5) * 1;
		DEF = (((2 * 30 + IV[1] * level) / 100) + 5) * 1;
		maxHP = ((2 * 75 + IV[2] * level) / 100) + level + 10;
	}

	public static int getEXPAtLevel(int n) {
		return n * n * n * 4 / 5;
	}

	public static int getLevelAtEXP(int e) {
		return (int) Math.cbrt(5 * e / 4.0f);
	}

	public void useItem(int itemIndex) {
		switch (bag.get(itemIndex)) {
			case Item.ASGARD:
				updateLevel(level + 1);
				HP = maxHP;
				EXP = getEXPAtLevel(level);
				break;
			case Item.DIAMOND:
				EXP += 100;
				updateLevel(getLevelAtEXP(EXP));
				break;
			case Item.ENERGY:
				HP += maxHP / 2;
				break;
			case Item.POTION:
				HP += maxHP / 4;
				break;
		}
		bag.remove(itemIndex);
	}

	public void pickItem(Map map, int objectIndex) {
		char itemID = ((Item) map.objects.get(objectIndex)).ID;
		switch (itemID) {
			case Item.ASGARD:
			case Item.DIAMOND:
			case Item.ENERGY:
			case Item.POTION:
				bag.add(itemID);
			case Item.LEFT_GATE:
			case Item.RIGHT_GATE:
			case Item.BOTTOM_GATE:
			case Item.TOP_GATE:
				map.objects.remove(objectIndex);
				break;
		}
	}

	public int attack(Monster foe) {
		int damage = (((2 * level / 5) + 2) * 40 * ATK / foe.DEF / 50) + 2;
		foe.HP -= damage;
		return damage;
	}

	public StateObject getStateObject() {
		StateObject playerStat = new StateObject();
		playerStat.name = name.toCharArray();
		playerStat.entityType = new char[0];
		playerStat.level = level;
		playerStat.gender = gender;
		ProgressBar plyrStatHP = new ProgressBar();
		plyrStatHP.icon = 28;
		plyrStatHP.color = 2;
		plyrStatHP.value = HP;
		plyrStatHP.min = 0;
		plyrStatHP.max = maxHP;
		ProgressBar plyrStatEXP = new ProgressBar();
		plyrStatEXP.icon = 'E';
		plyrStatEXP.color = 3;
		plyrStatEXP.value = EXP;
		plyrStatEXP.min = getEXPAtLevel(level);
		plyrStatEXP.max = getEXPAtLevel(level + 1);
		playerStat.bars = new ProgressBar[2];
		playerStat.bars[0] = plyrStatHP;
		playerStat.bars[1] = plyrStatEXP;
		return playerStat;
	}
}
