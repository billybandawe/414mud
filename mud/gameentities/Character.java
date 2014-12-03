package gameentities;

public class Character extends Stuff {

	/* fixme: have alignment, aggresive, plug into formula */
	public int totalhp;
	public int hp;
	public int level; /* fixme: not used */
	public int money;

	public Character() {
		super();
		totalhp = hp = 50;
		level = 1;
		money = 50;
		line  = "someone is chilling";
		//name.add("someone");
		name = "someone";
	}

	/* fixme; put in Stuff.class */
	public void kill(Stuff murderer) {
		murderer.sendTo("You have slain " + this + "!");
		murderer.sendToRoom(this + " has been attacked and killed by " + murderer + ".");
	}

}
