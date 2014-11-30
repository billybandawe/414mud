package gameentities;

import java.util.List;
import java.util.LinkedList;

public class Room extends Stuff {

	public String description;
	public Room n, e, s, w, u, d;

	public Room() {
		super();
		description = "This is an entrely bland room.";
		line        = "A room.";
	}

	/*public void put(Stuff s) {
		contents.add(s);
	}*/

	/*void setDescription(String description) {
		this.description = description;
	}*/

}
