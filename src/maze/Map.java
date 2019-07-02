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
	public char[] name;

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
		Item tanggaUp = new Item(rand);
		tanggaUp.name = "HP +10";
		tanggaUp.icon = 'H';
		tanggaUp.color = 3;
		tanggaUp.pos.x = ROOM_SIZE * DS_SIZE - 4;
		tanggaUp.pos.y = 3;
		newMap.objects.add(tanggaUp);

		Item tanggaDown = new Item(rand);
		tanggaDown.name = "HP +10";
		tanggaDown.icon = 'N';
		tanggaDown.color = 3;
		tanggaDown.pos.x = 3;
		tanggaDown.pos.y = ROOM_SIZE * DS_SIZE - 4;
		newMap.objects.add(tanggaDown);
		
		Item CheckPoint = new Item(rand);
		CheckPoint.icon = 'C';
		CheckPoint.color = 1;
		CheckPoint.pos.x = 3;
		CheckPoint.pos.y = 3;
		newMap.objects.add(CheckPoint);
		for (int i = 0; i < edges.length; i++) {
			boolean unionSuccess = DSUnion(DS, edges[i][0], edges[i][1]);
			if (unionSuccess) {
				int staX = edges[i][0] % DS_SIZE;
				int staY = edges[i][0] / DS_SIZE;
				int endX = edges[i][1] % DS_SIZE;
				int endY = edges[i][1] / DS_SIZE;

				Monster blackEye = new Monster(rand);
				blackEye.name = "HP +10";
				blackEye.icon = 'B';
				blackEye.color = 6;
				blackEye.pos.x = staX * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE - 6));
				blackEye.pos.y = staY * ROOM_SIZE + 3 + (rand.nextInt(ROOM_SIZE - 6));
				blackEye.HP = rand.nextInt(101);
				blackEye.maxHP = rand.nextInt(101);
				newMap.objects.add(blackEye);

				Item k1 = new Item(rand);
				k1.name = "HP +10";
				k1.pos.x = staX * ROOM_SIZE + 4 + rand.nextInt(ROOM_SIZE - 8);
				k1.pos.y = staY * ROOM_SIZE + 4 + rand.nextInt(ROOM_SIZE - 8);
				newMap.objects.add(k1);
				
				Item Diamond = new Item(rand);
				Diamond.icon = 'D';
				Diamond.color = 7;
				Diamond.pos.x = staX * ROOM_SIZE + 5 + rand.nextInt(ROOM_SIZE - 10);
				Diamond.pos.y = staY * ROOM_SIZE + 5 + rand.nextInt(ROOM_SIZE - 10);
				newMap.objects.add(Diamond);
				
				Item potion = new Item(rand);
				potion.icon = 'P';
				potion.color = 4;
				potion.pos.x = staX * ROOM_SIZE + 6 + rand.nextInt(ROOM_SIZE - 12);
				potion.pos.y = staY * ROOM_SIZE + 6 + rand.nextInt(ROOM_SIZE - 12);
				newMap.objects.add(potion);
				
				Item Volatile = new Item(rand);
				Volatile.icon = 'V';
				Volatile.color = 5;
				Volatile.pos.x = staX * ROOM_SIZE + 3 + rand.nextInt(ROOM_SIZE - 6);
				Volatile.pos.y = staY * ROOM_SIZE + 3 + rand.nextInt(ROOM_SIZE - 6);
				newMap.objects.add(Volatile);

				Item Energy = new Item(rand);
				Energy.icon = 'E';
				Energy.color = 9;
				Energy.pos.x = staX * ROOM_SIZE + 3 + rand.nextInt(ROOM_SIZE - 6);
				Energy.pos.y = staY * ROOM_SIZE + 3 + rand.nextInt(ROOM_SIZE - 6);
				newMap.objects.add(Energy);

				for (int y = staY * ROOM_SIZE + 3; y < (staY + 1) * ROOM_SIZE - 3; y++)
					for (int x = staX * ROOM_SIZE + 3; x < (staX + 1) * ROOM_SIZE - 3; x++)
						newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];

				for (int y = endY * ROOM_SIZE + 3; y < (endY + 1) * ROOM_SIZE - 3; y++)
					for (int x = endX * ROOM_SIZE + 3; x < (endX + 1) * ROOM_SIZE - 3; x++)
						newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];

				if (staX == endX) {
					for (int y = (staY + 1) * ROOM_SIZE - 3; y < endY * ROOM_SIZE + 3; y++)
						for (int x = staX * ROOM_SIZE + ROOM_SIZE / 2 - 1; x < staX * ROOM_SIZE + ROOM_SIZE / 2
								+ 1; x++)
							newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];
					Item Gate1 = new Item(rand);
					Gate1.icon = 'G';
					Gate1.color = 8;
					Gate1.pos.x = staX * ROOM_SIZE + ROOM_SIZE / 2 - 1;
					Gate1.pos.y = (staX + 1) * ROOM_SIZE - 4;
					newMap.objects.add(Gate1);
					
					Item Gate2 = new Item(rand);
					Gate2.icon = 'G';
					Gate2.color = 8;
					Gate2.pos.x = staX * ROOM_SIZE + ROOM_SIZE / 2;
					Gate2.pos.y = (staX + 1) * ROOM_SIZE - 4;
					newMap.objects.add(Gate2);
				}

				if (staY == endY) {
					for (int y = staY * ROOM_SIZE + ROOM_SIZE / 2 - 1; y < staY * ROOM_SIZE + ROOM_SIZE / 2 + 1; y++)
						for (int x = (staX + 1) * ROOM_SIZE - 3; x < endX * ROOM_SIZE + 3; x++)
							newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];
					Item Gate1 = new Item(rand);
					Gate1.icon = 'G';
					Gate1.color = 8;
					Gate1.pos.x = (staY + 1) * ROOM_SIZE - 4;
					Gate1.pos.y = staY * ROOM_SIZE + ROOM_SIZE / 2 - 1;
					newMap.objects.add(Gate1);
					
					Item Gate2 = new Item(rand);
					Gate2.icon = 'G';
					Gate2.color = 8;
					Gate2.pos.x = (staY + 1) * ROOM_SIZE - 4;
					Gate2.pos.y = staY * ROOM_SIZE + ROOM_SIZE / 2;
					newMap.objects.add(Gate2);
				}
			}
		}
		return newMap;
	}
}
