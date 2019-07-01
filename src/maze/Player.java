package maze;

import java.util.Random;

public class Player {
	public String name;
	public v2 pos;

	public Player(Random rand) {
		name = NameRandomizer.next(rand, 10);
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		pos = new v2();
	}
}
