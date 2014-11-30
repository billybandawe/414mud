package gameentities;

import main.Connection;

public class Player extends Character {

	public Connection connection;

	public Player(Connection connection, String name) {
		super();
		this.connection = connection;
		this.line = name + " is neutral.";
		//this.name.add(name);
		this.name = name;
	}

/*	public void kill(Stuff murderer) {
		ReceiveMessage("You have been attacked and killed by " + murderer + "\n");
		
		//TODO close connection
		
	}
	
	public void UpdateLevel(String playerYouJustKilled) {
		ReceiveMessage("You have killed " + playerYouJustKilled + "\n");
		
		//TODO
		this.level++;
		this.money.AddMoney(50);
		
		ReceiveMessage("Your stats are now: " + "Level " + Integer.toString(this.level) + ", Money " + Integer.toString(this.money.GetAmount()) + "\n");
		
		
	}*/
	
	@Override
	protected Connection getConnection() {
		return connection;
	}

}
