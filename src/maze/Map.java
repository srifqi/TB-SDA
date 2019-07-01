package maze;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Map {
	public static final int DS_SIZE = 5;
	public static final int ROOM_SIZE = 16;

	public static final char[] OBSTACLES = {200};
	public static final char[] WALKABLE = {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 46, 197};

	public int width;
	public int height;
	public char[][] data;
	public ArrayList <Object> objects;

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

	public static Map createMap(Random rand) {
		int[] DS = new int[DS_SIZE * DS_SIZE];
		for (int i = 0; i < DS.length; i ++)
			DS[i] = -1;
		Integer[][] edges = new Integer[2 * DS_SIZE * (DS_SIZE - 1)][3];
		int edgeIndex = 0;
		for (int y = 0; y < DS_SIZE; y ++) {
		for (int x = 0; x < DS_SIZE; x ++) {
			if (x > 0) {
				edges[edgeIndex][0] = y * DS_SIZE + x - 1;
				edges[edgeIndex][1] = y * DS_SIZE + x;
				edges[edgeIndex][2] = rand.nextInt(Integer.MAX_VALUE);
				edgeIndex ++;
			}
			if (y > 0) {
				edges[edgeIndex][0] = (y - 1) * DS_SIZE + x;
				edges[edgeIndex][1] = y * DS_SIZE + x;
				edges[edgeIndex][2] = rand.nextInt(Integer.MAX_VALUE);
				edgeIndex ++;
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
		for (int i = 0; i < newMap.width; i ++)
		for (int j = 0; j < newMap.height; j ++)
			newMap.data[j][i] = OBSTACLES[rand.nextInt(OBSTACLES.length)];
		for (int i = 0; i < edges.length; i ++) {
			boolean unionSuccess = DSUnion(DS, edges[i][0], edges[i][1]);
			if (unionSuccess) {
				int staX = edges[i][0] % DS_SIZE;
				int staY = edges[i][0] / DS_SIZE;
				int endX = edges[i][1] % DS_SIZE;
				int endY = edges[i][1] / DS_SIZE;

				Monster phip = new Monster(rand);
				phip.pos.x = staX * ROOM_SIZE + 3;
				phip.pos.y = staY * ROOM_SIZE + 3;
				phip.HP = rand.nextInt();
				phip.maxHP = rand.nextInt();
				newMap.objects.add(phip);

				Item k1 = new Item(rand);
				k1.name = "HP +10";
				k1.pos.x = staX * ROOM_SIZE + 4;
				k1.pos.y = staY * ROOM_SIZE + 4;
				newMap.objects.add(k1);

				for (int y = staY * ROOM_SIZE + 3; y < (staY + 1) * ROOM_SIZE - 3; y ++)
				for (int x = staX * ROOM_SIZE + 3; x < (staX + 1) * ROOM_SIZE - 3; x ++)
					newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];

				for (int y = endY * ROOM_SIZE + 3; y < (endY + 1) * ROOM_SIZE - 3; y ++)
				for (int x = endX * ROOM_SIZE + 3; x < (endX + 1) * ROOM_SIZE - 3; x ++)
					newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];

				if (staX == endX) {
					for (int y = (staY + 1) * ROOM_SIZE - 3; y < endY * ROOM_SIZE + 3; y ++)
					for (int x = staX * ROOM_SIZE + ROOM_SIZE / 2 - 1; x < staX * ROOM_SIZE + ROOM_SIZE / 2 + 1; x ++)
						newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];
				}

				if (staY == endY) {
					for (int y = staY * ROOM_SIZE + ROOM_SIZE / 2 - 1; y < staY * ROOM_SIZE + ROOM_SIZE / 2 + 1; y ++)
					for (int x = (staX + 1) * ROOM_SIZE - 3; x < endX * ROOM_SIZE + 3; x ++)
						newMap.data[y][x] = WALKABLE[rand.nextInt(WALKABLE.length)];
				}
			}
		}
		return newMap;
	}
}
