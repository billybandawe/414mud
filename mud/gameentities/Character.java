package gameentities;

import java.util.List;
import java.util.LinkedList;

public class Character extends Stuff {

	/* fixme: have alignment, aggresive, plug into formula */
	public int totalhp;
	public int hp;
	public int level;
	public int money;
	public List<Stuff> carrying = new LinkedList<Stuff>();

	public Character() {
		super();
		totalhp = hp = 50;
		level = 1;
		money = 50;
		line  = "someone is chilling";
		//name.add("someone");
		name = "someone";
	}

	/*public kill() {
		//("You have been attacked and killed by " + murderer + "\n");
	}*/
}
