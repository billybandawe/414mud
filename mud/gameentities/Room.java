package gameentities;

import java.util.List;
import java.util.LinkedList;

public class Room extends Stuff {

	private String description;
	private Room n, e, s, w, u, d;

	public Room() {
		super();
		description = "This is an entrely bland room.";
		line        = "a room";
		name        = "room";
	}

	public void SetN(Room r) { n = r; }
	public void SetE(Room r) { e = r; }
	public void SetS(Room r) { s = r; }
	public void SetW(Room r) { w = r; }
	public void SetU(Room r) { u = r; }
	public void SetD(Room r) { d = r; }
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String lookDetailed() {
		return line + "\n" + description;
	}

}
