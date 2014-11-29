package gameentities;

import main.Connection;

public class GamePlayer extends GameStuff{
	
	protected int hp;
	protected int level;
	protected Money money;
	private Connection connection;
	protected String mood;
	protected GameObject[] possessions;
	
	public GamePlayer(Connection connection) {
		this.hp = 100;
		this.level = 1;
		this.money = new Money(500);
		this.connection = connection;
		this.mood = "Neutral";
	}
	
	public void ReceiveMessage(String message) {
		
		//TODO
		//Need to figure out how this will tie into the actual connection as this will affect implementation
		
	}
	
	public Connection getConnection() {
		return this.connection;
	}

}
