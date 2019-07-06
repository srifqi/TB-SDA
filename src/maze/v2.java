package maze;

public class v2 {
	public int x;
	public int y;

	public v2() {
		x = 0;
		y = 0;
	}

	public v2(int _x, int _y) {
		x = _x;
		y = _y;
	}

	public v2 clone() {
		v2 cloned = new v2();
		cloned.x = x;
		cloned.y = y;
		return cloned;
	}

	public boolean equals(v2 target) {
		return (target.x == x) && (target.y == y);
	}

	public int distToSq(v2 target) {
		return (target.x - x) * (target.x - x) +
				(target.y - y) * (target.y - y);
	}
}
