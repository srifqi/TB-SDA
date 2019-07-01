package maze;

import java.util.Random;

import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Random rand = new Random();
				Game the_game = new Game(rand);
				if (!the_game.start()) {
					System.out.println("Permainan gagal dijalankan.");
				}
			}
		});
	}
}
