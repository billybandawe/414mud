package gameentities;

import java.util.List;
import java.util.LinkedList;

public class Stuff /*implements Enumerate*/ {

	private static int vnumCounter = 0;

	public int vnum;
	public List<String> name = new LinkedList<String>();
	public String line;

	private List<Stuff> contents = new LinkedList<Stuff>();
	private Stuff in;

	Stuff() {
		vnum = ++vnumCounter;
		name.add("stuff");
		line = "Some stuff is here.";
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
}
