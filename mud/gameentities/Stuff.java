package gameentities;

import java.util.List;
import java.util.LinkedList;

import main.Connection;

public class Stuff /*implements Enumerate*/ {

	private static int vnumCounter = 0;

	public int vnum;
	/*public List<String> name = new LinkedList<String>(); <- only one name is fine */
	public String name;
	public String line;

	private List<Stuff> contents = new LinkedList<Stuff>();
	private Stuff in;

	Stuff() {
		vnum = ++vnumCounter;
		//name.add("stuff");
		name = "stuff";
		line = "some stuff is here";
	}

	public void placeIn(Stuff container) {
		/* it's already in something */
		if(in != null) {
			System.err.print("Fixme: extract.\n");
			/* fixme: enum and get it out */
		}
		in = container;
		container.contents.add(this);
		System.err.print(this + " in " + container + ".\n");
	}

	public Stuff getIn() {
		return in;
	}

	public void sendToRoom(final String yo) {
		if(this.in == null) return;
		this.in.sendToContents(yo);
	}

	public void sendToContents(final String yo) {
		Connection c;
		for(Stuff s : this.contents) {
			if((c = s.getConnection()) == null) continue;
			c.sendTo(yo);
		}
	}

	/** @return The connection, if there is one, otherwise null. */
	protected Connection getConnection() {
		return null;
	}

	public String toString() {
		return name;
	}

	/** gives more info */
	public String look() {
		return "(" + name + ") " + line;
	}

	/** gives more info */
	public String lookDetailed() {
		return look();
	}

	public List<Stuff> getContents() {
		return contents;
	}

}
