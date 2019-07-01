package maze;

enum ObjectType {
	PLAYER, MONSTER, ITEM
}

public class Object {
	public String name;
	public v2 pos;
	public ObjectType type;
	
	public Object() {
		pos = new v2();
	}
}
