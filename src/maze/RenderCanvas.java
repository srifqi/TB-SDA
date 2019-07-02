package maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class RenderCanvas extends JPanel {
	private Dimension tileSize;
	private Image tileMap;
	private Map map;
	private Map colorMap;
	private Color[] palette;

	public RenderCanvas(Image _tileMap, Dimension _tileSize, Dimension _mapSize) {
		tileMap = _tileMap;
		tileSize = _tileSize;
		map = new Map(_mapSize.width, _mapSize.height);
		colorMap = new Map(_mapSize.width, _mapSize.height);
		setPreferredSize(new Dimension(
			_mapSize.width * _tileSize.width,
			_mapSize.height * _tileSize.height
		));
	}

	public void setMap(Map _map) {
		map = _map;
		setPreferredSize(new Dimension(
			map.width * tileSize.width,
			map.height * tileSize.height
		));
	}

	public void setColorMap(Map _map) {
		colorMap = _map;
	}

	public void setPalette(Color[] _plt) {
		palette = _plt;
	}

	public void draw(Map _map, v2 translation, int limit) {
		for (int i = 0; i < colorMap.width; i ++)
		for (int j = 0; j < colorMap.height; j ++)
			colorMap.data[j][i] = 1;
		v2 pos = new v2();
		v2 screenPos = new v2();
		v2 mid = new v2(map.width / 2, map.height / 2 - 1);
		int limitSq = limit * limit;
		for (int i = 0; i < map.width; i ++) {
		for (int j = 0; j < map.height; j ++) {
			screenPos.x = i;
			screenPos.y = j;
			pos.x = i - translation.x;
			pos.y = j - translation.y;
			if (pos.x >= 0 && pos.x < _map.width && pos.y >= 0 && pos.y < _map.height &&
					limit > 0 && screenPos.distToSq(mid) <= limitSq) {
				map.data[j][i] = _map.data[pos.y][pos.x];
			} else {
				map.data[j][i] = 0;
			}
		}
		}
		for (Object o : _map.objects) {
			pos.x = o.pos.x + translation.x;
			pos.y = o.pos.y + translation.y;
			if (pos.x >= 0 && pos.x < map.width && pos.y >= 0 && pos.y < map.height &&
					limit > 0 && pos.distToSq(mid) <= limitSq) {
				map.data[pos.y][pos.x] = o.icon;
				colorMap.data[pos.y][pos.x] = o.color;
			}
		}
	}

	/*
	 * 0 1 2
	 * 3 4 5
	 * 6 7 8
	 */
	public void drawBox(int x, int y, int w, int h, char[] box9, char color) {
		int sX = x < 0 ? 0 : x;
		int sY = y < 0 ? 0 : y;
		int eX = x + w > map.width ? map.width : x + w;
		int eY = y + h > map.height ? map.height : y + h;
		for (int j = sY; j < eY; j ++) {
		for (int i = sX; i < eX; i ++) {
			colorMap.data[j][i] = color;
			if (j == y) {
				if (i == x)
					map.data[j][i] = box9[0];
				else if (i == x + w - 1)
					map.data[j][i] = box9[2];
				else
					map.data[j][i] = box9[1];
			} else if (j == y + h - 1) {
				if (i == x)
					map.data[j][i] = box9[6];
				else if (i == x + w - 1)
					map.data[j][i] = box9[8];
				else
					map.data[j][i] = box9[7];
			} else {
				if (i == x)
					map.data[j][i] = box9[3];
				else if (i == x + w - 1)
					map.data[j][i] = box9[5];
				else
					map.data[j][i] = box9[4];
			}
		}
		}
	}

	public void drawChar(int x, int y, char _char, char color) {
		if (x >= 0 && y >= 0 && x < map.width && y < map.height) {
			colorMap.data[y][x] = color;
			map.data[y][x] = _char;
		}
	}

	public void drawText(int x, int y, int w, int h, char[] text, char color) {
		int sX = x < 0 ? 0 : x;
		int sY = y < 0 ? 0 : y;
		int eX = x + w > map.width ? map.width : x + w;
		int eY = y + h > map.height ? map.height : y + h;
		for (int j = sY; j < eY; j ++) {
		for (int i = sX; i < eX; i ++) {
			colorMap.data[j][i] = color;
			int idx = (j - y) * w + i - x;
			if (idx < text.length)
				map.data[j][i] = text[idx];
		}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.BLACK);

		for (int y = 0; y < map.height; y ++)
		for (int x = 0; x < map.width; x ++) {
			v2 p = new v2(map.data[y][x] % 16, map.data[y][x] / 16);
			g.drawImage(tileMap,
					x * tileSize.width, y * tileSize.height,
					(x + 1) * tileSize.width,
					(y + 1) * tileSize.height,
					p.x * tileSize.width, p.y * tileSize.height,
					(p.x + 1) * tileSize.width,
					(p.y + 1) * tileSize.height,
					palette[colorMap.data[y][x]], this);
		}
	}	
}
