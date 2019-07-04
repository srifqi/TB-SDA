package maze;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Tile {
	public static final char ICON_HP = 28;
	public static final char ICON_MANA = 29;
	public static final char ICON_FEMALE = 30;
	public static final char ICON_MALE = 31;

	public static final char BOX_F4 = 248;
	public static final char BOX_F3 = 247;
	public static final char BOX_F2 = 246;
	public static final char BOX_F1 = 245;
	public static final char BOX_F0 = 244;

	public static final char ARROW_LT = 240;
	public static final char ARROW_RT = 241;
	public static final char ARROW_UP = 242;
	public static final char ARROW_DN = 243;

	public static final char HORIZONTAL_BAR = 255;

	public static final char[] BOX9 = {
			251, 249, 252,
			250, ' ', 250,
			253, 249, 254
	};

	public static final char[] LEVELTEXT = {'L', 27};

	public static final char YOLO = 128;

	public static final char COCKROACHES = 129;
	public static final char CREAPER = 130;
	public static final char DETROIT = 131;
	public static final char KABUTO = 132;
	public static final char LOLLIPOP = 133;
	public static final char MUST_ROOM = 134;
	public static final char PEA_SHOOT = 135;
	public static final char POCO_POCO = 136;
	public static final char RED_SNACK = 137;
	public static final char SLIME = 138;
 
	public static final char ASGARD = 144;
	public static final char CHECKPOINT = 145;
	public static final char DIAMOND = 146;
	public static final char ENERGY = 147;
	public static final char POTION = 148;
	public static final char LEFT_GATE = 149;
	public static final char RIGHT_GATE = 150;
	public static final char BOTTOM_GATE = 151;
	public static final char TOP_GATE = 152;
	public static final char UP_LADDER = 153;
	public static final char DOWN_LADDER = 154;
}

public class Map {
	public static final int DS_SIZE = 5;
	public static final int ROOM_SIZE = 16;

	public static final char[] OBSTACLES = {Tile.BOX_F4};
	public static final char[] WALKABLE = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '.', Tile.BOX_F1};

	public int width;
	public int height;
	public char[][] data;
	public ArrayList<Object> objects;

	public Map() {
		objects = new ArrayList<Object>();
	}

	public Map(int _width, int _height) {
		this();
		width = _width;
		height = _height;
		data = new char[_height][_width];
	}

	public static boolean isWalkable(char tile) {
		for (char i : WALKABLE)
			if (tile == i)
				return true;
		return false;
	}

	public boolean isThereObject(v2 coord) {
		for (Object i : objects)
			if (i.pos.equals(coord))
				return true;
		return false;
	}

	private static int DSFind(int[] DS, int i) {
		while (DS[i] != -1)
			i = DS[i];
		return i;
	}

	private static boolean DSUnion(int[] DS, int a, int b) {
		int fA = DSFind(DS, a);
		int fB = DSFind(DS, b);
		if (fA != fB) {
			DS[fA] = fB;
			return true;
		}
		return false;
	}

	public static Map createMap(Random rand, int floor) {
		int[] DS = new int[DS_SIZE * DS_SIZE];
		boolean[] itemPlaced = new boolean[DS_SIZE * DS_SIZE];
		for (int i = 0; i < DS.length; i++)
			DS[i] = -1;
		Integer[][] edges = new Integer[2 * DS_SIZE * (DS_SIZE - 1)][3];
		int edgeIndex = 0;
		for (int y = 0; y < DS_SIZE; y++) {
			for (int x = 0; x < DS_SIZE; x++) {
				if (x > 0) {
					edges[edgeIndex][0] = y * DS_SIZE + x - 1;
					edges[edgeIndex][1] = y * DS_SIZE + x;
					edges[edgeIndex][2] = rand.nextInt(Integer.MAX_VALUE);
					edgeIndex++;
				}
				if (y > 0) {
					edges[edgeIndex][0] = (y - 1) * DS_SIZE + x;
					edges[edgeIndex][1] = y * DS_SIZE + x;
					edges[edgeIndex][2] = rand.nextInt(Integer.MAX_VALUE);
					edgeIndex++;
				}
			}
		}
		Comparator<Integer[]> CustomEdgeComparator = new Comparator<Integer[]>() {
			public int compare(Integer[] A, Integer[] B) {
				return A[2] - B[2];
			}
		};
		Arrays.sort(edges, CustomEdgeComparator);
		Map newMap = new Map(DS_SIZE * ROOM_SIZE, DS_SIZE * ROOM_SIZE);
		for (int i = 0; i < newMap.width; i++)
		for (int j = 0; j < newMap.height; j++)
			newMap.data[j][i] = OBSTACLES[rand.nextInt(OBSTACLES.length)];

		if (floor > 0) {
			Item tanggaKA = new Item(floor % 2 == 1 ? Item.UP_LADDER : Item.DOWN_LADDER);
			tanggaKA.color = 3;
			tanggaKA.pos.x = 3;
			tanggaKA.pos.y = 4;
			newMap.objects.add(tanggaKA);
		}

		if (floor < Game.FLOORAMOUNT - 1) {
			Item tanggaKB = new Item(floor % 2 == 0 ? Item.UP_LADDER : Item.DOWN_LADDER);
			tanggaKB.color = 3;
			tanggaKB.pos.x = ROOM_SIZE * DS_SIZE - 5;
			tanggaKB.pos.y = ROOM_SIZE * DS_SIZE - 5;
			newMap.objects.add(tanggaKB);
		}
		
		Item CheckPoint = new Item(Item.CHECKPOINT);
		CheckPoint.color = 1;
		CheckPoint.pos.x = ROOM_SIZE / 2 * DS_SIZE - 4;
		CheckPoint.pos.y = ROOM_SIZE / 2 * DS_SIZE - 4;
		newMap.objects.add(CheckPoint);

		for (int i = 0; i < edges.length; i++) {
			boolean unionSuccess = DSUnion(DS, edges[i][0], edges[i][1]);
			if (unionSuccess) {
				int staX = edges[i][0] % DS_SIZE;
				int staY = edges[i][0] / DS_SIZE;
				int endX = edges[i][1] % DS_SIZE;
				int endY = edges[i][1] / DS_SIZE;

				for (int y = staY * ROOM_SIZE + 3; y < (staY + 1) * ROOM_SIZE - 3; y++)
				for (int x = staX * ROOM_SIZE + 3; x < (staX + 1) * ROOM_SIZE - 3; x++)
					newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];

				for (int y = endY * ROOM_SIZE + 3; y < (endY + 1) * ROOM_SIZE - 3; y++)
				for (int x = endX * ROOM_SIZE + 3; x < (endX + 1) * ROOM_SIZE - 3; x++)
					newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];

				if (staX == endX) {
					for (int y = (staY + 1) * ROOM_SIZE - 3; y < endY * ROOM_SIZE + 3; y++)
					for (int x = staX * ROOM_SIZE + ROOM_SIZE / 2 - 1; x < staX * ROOM_SIZE + ROOM_SIZE / 2 + 1; x++)
						newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];
				}

				if (staY == endY) {
					for (int y = staY * ROOM_SIZE + ROOM_SIZE / 2 - 1; y < staY * ROOM_SIZE + ROOM_SIZE / 2 + 1; y++)
					for (int x = (staX + 1) * ROOM_SIZE - 3; x < endX * ROOM_SIZE + 3; x++)
						newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];
				}

				if (itemPlaced[edges[i][0]] && itemPlaced[edges[i][1]])
					continue;

				int chsX = itemPlaced[edges[i][0]] ? endX : staX;
				int chsY = itemPlaced[edges[i][0]] ? endY : staY;
				itemPlaced[edges[i][0]] = true;

				Monster Lolipop = new Monster(rand, Monster.LOLLIPOP, rand.nextInt(5) + 5);
				Lolipop.color = 9;
				Lolipop.pos.x = chsX * ROOM_SIZE + 5;
				Lolipop.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Mushroom = new Monster(rand, Monster.MUST_ROOM, rand.nextInt(5) + 5);
				Mushroom.color = 8;
				Mushroom.pos.x = chsX * ROOM_SIZE + 7;
				Mushroom.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Poco = new Monster(rand, Monster.POCO_POCO, rand.nextInt(5) + 5);
				Poco.color = 7;
				Poco.pos.x = chsX * ROOM_SIZE + 9;
				Poco.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Slime = new Monster(rand, Monster.SLIME, rand.nextInt(10) + 5);
				Slime.color = 3;
				Slime.pos.x = chsX * ROOM_SIZE + 5;
				Slime.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Detroit = new Monster(rand, Monster.DETROIT, rand.nextInt(10) + 5);
				Detroit.color = 9;
				Detroit.pos.x = chsX * ROOM_SIZE + 7;
				Detroit.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Peashooter = new Monster(rand, Monster.PEA_SHOOT, rand.nextInt(10) + 5);
				Peashooter.color = 6;
				Peashooter.pos.x = chsX * ROOM_SIZE + 9;
				Peashooter.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Creaper = new Monster(rand, Monster.CREAPER, rand.nextInt(15) + 5);
				Creaper.color = 8;
				Creaper.pos.x = chsX * ROOM_SIZE + 5;
				Creaper.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Kabuto = new Monster(rand, Monster.KABUTO, rand.nextInt(15) + 5);
				Kabuto.color = 6;
				Kabuto.pos.x = chsX * ROOM_SIZE + 7;
				Kabuto.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster Cockroacker = new Monster(rand, Monster.COCKROACHES, rand.nextInt(15) + 5);
				Cockroacker.color = 5;
				Cockroacker.pos.x = chsX * ROOM_SIZE + 9;
				Cockroacker.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				Monster RedSnack = new Monster(rand, Monster.RED_SNACK, rand.nextInt(30) + 5);
				RedSnack.color = 2;
				RedSnack.pos.x = chsX * ROOM_SIZE + 7;
				RedSnack.pos.y = chsY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE / 2 - 3));

				if (floor == 0) {
					newMap.objects.add(Lolipop);
					newMap.objects.add(Mushroom);
					newMap.objects.add(Poco);
				} else if (floor == 1) {
					newMap.objects.add(Slime);
					newMap.objects.add(Peashooter);
					newMap.objects.add(Detroit);
				} else if (floor == 2) {
					newMap.objects.add(Creaper);
					newMap.objects.add(Kabuto);
					newMap.objects.add(Cockroacker);
				} else if (floor == 3) {
					newMap.objects.add(RedSnack);
				}

				Item Diamond = new Item(Item.DIAMOND);
				Diamond.color = 7;
				Diamond.pos.x = chsX * ROOM_SIZE + 5;
				Diamond.pos.y = chsY * ROOM_SIZE + rand.nextInt(ROOM_SIZE / 2 - 4) + ROOM_SIZE / 2;
				newMap.objects.add(Diamond);

				Item potion = new Item(Item.POTION);
				potion.color = 4;
				potion.pos.x = chsX * ROOM_SIZE + 7;
				potion.pos.y = chsY * ROOM_SIZE + rand.nextInt(ROOM_SIZE / 2 - 4) + ROOM_SIZE / 2;
				newMap.objects.add(potion);

				Item Volatile = new Item(Item.ASGARD);
				Volatile.color = 5;
				Volatile.pos.x = chsX * ROOM_SIZE + 9;
				Volatile.pos.y = chsY * ROOM_SIZE + rand.nextInt(ROOM_SIZE / 2 - 4) + ROOM_SIZE / 2;
				newMap.objects.add(Volatile);

				Item Energy = new Item(Item.ENERGY);
				Energy.color = 9;
				Energy.pos.x = chsX * ROOM_SIZE + 11;
				Energy.pos.y = chsY * ROOM_SIZE + rand.nextInt(ROOM_SIZE / 2 - 4) + ROOM_SIZE / 2;
				newMap.objects.add(Energy);

				if (staX == endX) {
					Item Gate1 = new Item(Item.LEFT_GATE);
					Gate1.color = 8;
					Gate1.pos.x = chsX * ROOM_SIZE + ROOM_SIZE / 2 - 1;
					Gate1.pos.y = (chsY + 1) * ROOM_SIZE - 4;
					newMap.objects.add(Gate1);
					
					Item Gate2 = new Item(Item.RIGHT_GATE);
					Gate2.color = 8;
					Gate2.pos.x = chsX * ROOM_SIZE + ROOM_SIZE / 2;
					Gate2.pos.y = (chsY + 1) * ROOM_SIZE - 4;
					newMap.objects.add(Gate2);
				}

				if (staY == endY) {
					Item Gate1 = new Item(Item.TOP_GATE);
					Gate1.color = 8;
					Gate1.pos.x = (chsX + 1) * ROOM_SIZE - 4;
					Gate1.pos.y = chsY * ROOM_SIZE + ROOM_SIZE / 2 - 1;
					newMap.objects.add(Gate1);
					
					Item Gate2 = new Item(Item.BOTTOM_GATE);
					Gate2.color = 8;
					Gate2.pos.x = (chsX + 1) * ROOM_SIZE - 4;
					Gate2.pos.y = chsY * ROOM_SIZE + ROOM_SIZE / 2;
					newMap.objects.add(Gate2);
				}
			}
		}
		return newMap;
	}

	public static v2[][] AStar(Map _map, v2 S, v2 E) {
		ArrayList<v2> done = new ArrayList<v2>();
		ArrayList<v2> open = new ArrayList<v2>();
		v2[][] prev = new v2[_map.height][_map.width];
		int[][] fScore = new int[_map.height][_map.width];
		int[][] gScore = new int[_map.height][_map.width];
		for (int i = 0; i < _map.width * _map.height; i ++) {
			prev[i / _map.width][i % _map.width] = new v2(-1, -1);
			fScore[i / _map.width][i % _map.width] = Integer.MAX_VALUE;
			gScore[i / _map.width][i % _map.width] = Integer.MAX_VALUE;
		}
		fScore[S.y][S.x] = S.distToSq(E);
		gScore[S.y][S.x] = 0;
		open.add(S);
		while (!open.isEmpty()) {
			int curIndex = -1;
			int fScoreMin = Integer.MAX_VALUE;
			for (int i = 0; i < open.size(); i ++) {
				v2 oi = open.get(i);
				if (fScore[oi.y][oi.x] < fScoreMin) {
					fScoreMin = fScore[oi.y][oi.x];
					curIndex = i;
				}
			}
			if (curIndex < 0)
				break;
			v2 cur = open.get(curIndex);
			if (cur.equals(E))
				return prev;
			open.remove(curIndex);
			done.add(cur);
			for (int j = -1; j <= 1; j ++) {
			for (int i = -1; i <= 1; i ++) {
				if (i - j != 1 && j - i != 1)
					continue;
				v2 ngbr = new v2(cur.x + i, cur.y + j);
				int gScoreAlt = gScore[ngbr.y][ngbr.x] + cur.distToSq(ngbr);
				boolean found = false;
				for (int k = 0; k < done.size(); k ++) {
					if (done.get(k).equals(ngbr)) {
						found = true;
						break;
					}
				}
				if (!found && Map.isWalkable(_map.data[ngbr.y][ngbr.x]) &&
						ngbr.distToSq(E) <= Game.SEARCHRADIUSSQ)
					open.add(ngbr);
				else if (gScoreAlt >= gScore[ngbr.y][ngbr.x])
					continue;
				prev[ngbr.y][ngbr.x] = cur;
				gScore[ngbr.y][ngbr.x] = gScoreAlt;
				fScore[ngbr.y][ngbr.x] = gScoreAlt + ngbr.distToSq(E);
			}
			}
		}
		return new v2[0][0];
	}

	public static v2 nextMovement(v2[][] prev, v2 start, v2 end) {
		v2 cur = end;
		if (prev.length <= 0)
			return cur;
		while (!prev[cur.y][cur.x].equals(start) && prev[cur.y][cur.x].x >= 0 && prev[cur.y][cur.x].y >= 0)
			cur = prev[cur.y][cur.x];
		return cur;
	}
}
