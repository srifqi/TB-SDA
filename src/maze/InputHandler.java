package maze;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputHandler implements KeyListener {
	Game parentGame;

	public InputHandler(Game _parent) {
		parentGame = _parent;
		parentGame.the_gui.addKeyListener(this);
	}

	public void keyTyped(KeyEvent ev) {
		parentGame.onKeyTyped(ev);
	}

	public void keyPressed(KeyEvent ev) {
		parentGame.onKeyPressed(ev);
	}

	public void keyReleased(KeyEvent ev) {
		parentGame.onKeyReleased(ev);
	}
}
