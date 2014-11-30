package gameentities;

import java.util.List;
import java.util.LinkedList;

public class Room extends Stuff {

	public String description;
	public Room n, e, s, w, u, d;

	public Room() {
		super();
		description = "This is an entrely bland room.";
		line        = "a room";
		name        = "room";
	}

	/*public void put(Stuff s) {
		contents.add(s);
	}*/

	/*void setDescription(String description) {
		this.description = description;
	}*/

	@Override
	public String lookDetailed() {
		return "(" + name + ")" + line + "\n" + description;
	}

}
