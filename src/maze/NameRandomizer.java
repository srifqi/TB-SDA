package maze;

import java.util.Random;

public class NameRandomizer {
	public static final char[] CONSONANTS = "bcdfghjklmnpqrstvwxyz".toCharArray();
	public static final char[] VOWELS = "aeiou".toCharArray();
	public static final char[] POSTCONSONANTS = "ry".toCharArray();

	public static String next(Random r, int maxLength) {
		maxLength = r.nextInt(maxLength - 4) + 4;
		String name = "";
		String add = "";
		int lastType = -1;
		while (name.length() + add.length() < maxLength) {
			name += add;
			lastType = lastType == 0 ? 1 : r.nextInt(3);
			char V = VOWELS[r.nextInt(VOWELS.length)];
			char C1 = CONSONANTS[r.nextInt(CONSONANTS.length)];
			char C2 = POSTCONSONANTS[r.nextInt(POSTCONSONANTS.length)];
			if (lastType == 0) // V
				add = "" + V;
			else if (lastType == 1) // CV
				add = "" + C1 + V;
			else if (lastType == 2) // CCV
				add = "" + C1 + C2 + V;
		}
		return name;
	}
}
