package gameentities;

import java.util.List;
import java.util.LinkedList;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectStreamException;

import main.Connection;

/* definately should have Enum, Serializ is for binary file mostly, I think */
public class Stuff /*implements Enumerate*/ /*implements Serializable*/ {

	//private static int vnumCounter = 0;

	//public int vnum;
	/*public List<String> name = new LinkedList<String>(); <- only one name is fine */
	public String name;
	public String line;

	private List<Stuff> contents = new LinkedList<Stuff>();
	private Stuff in;

	Stuff() {
		//vnum = ++vnumCounter;
		//name.add("stuff");
		name = "stuff";
		line = "some stuff is here";
	}

	public void placeIn(Stuff container) {

		/* it's already in something */
		if(in != null) {
			in.contents.remove(this);
			in = null;
			sendToRoom(this + " dissaperates!");
		}

		/* appear somewhere else */
		in = container;
		container.contents.add(this);
		sendToRoom(this + " reaperates dramatically!");

		this.sendTo("You dissaperate and instantly travel to " + container + ".");

		//System.err.print(this + " in " + container + ".\n");
	}

	public Stuff getIn() {
		return in;
	}

	public void sendTo(final String message) {
	}

	public void sendToRoom(final String message) {
		if(this.in == null) return;
		this.in.sendToContentsExcept(this, message);
	}

	private void sendToContentsExcept(final Stuff except, final String message) {
		for(Stuff s : this.contents) {
			if(s == except) continue;
			s.sendTo(message);
		}
	}

	public void sendToContents(final String message) {
		for(Stuff s : this.contents) s.sendTo(message);
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

	/** Prints all the data so it will be serialisable (but in text, not binary.) */
	public String saveString() {
		////////////////////////////////////////
		return ""; //<------
	}
}
