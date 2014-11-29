package gameentities;

import java.util.List;

import main.Connection;

public class GamePlayer extends GameStuff{
	
	protected int level;
	protected Money money;
	private Connection connection;
	protected String mood;
	protected GameRoom currentRoom;
	
	public GamePlayer(Connection connection) {
		this.level = 1;
		this.money = new Money(0);
		this.connection = connection;
		this.mood = "Neutral";
	}
	
	public void ReceiveMessage(String message) {
		this.connection.sendTo(message);
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public void SetRoom(GameRoom newRoom) {
		this.currentRoom.RemovePlayer(this);
		
		this.currentRoom = newRoom;
		this.currentRoom.AddPlayer(this);
		GameRoom[] roomsNearby = this.currentRoom.RoomsNearby();
		List<GamePlayer> playersInRoom = this.currentRoom.getPlayersInRoom();
		
		StringBuilder sb = new StringBuilder();
		sb.append("You are now in room " + this.currentRoom.GetName() + "\n");
		sb.append("North: " + roomsNearby[0].GetName() + "\n");
		sb.append("South: " + roomsNearby[1].GetName() + "\n");
		sb.append("East: " + roomsNearby[2].GetName() + "\n");
		sb.append("West: " + roomsNearby[3].GetName() + "\n");
		ReceiveMessage(sb.toString());
		
		sb = new StringBuilder();
		sb.append("Players in room are: ");
		for (GamePlayer player : playersInRoom) {
			sb.append(player.getConnection().getName() + ", ");
		}
		ReceiveMessage(sb.toString());
		
	}
	
	public int GetLevel() {
		return this.level;
	}
	
	public int SetLevel() {
		return this.level;
	}
	
	public GameRoom GetCurrentRoom() {
		return this.currentRoom;
	}
	
	public void KillPlayer(String murderer) {
		ReceiveMessage("You have been attacked and killed by " + murderer + "\n");
		
		//TODO close connection
		
	}
	
	public void UpdateLevel(String playerYouJustKilled) {
		ReceiveMessage("You have killed " + playerYouJustKilled + "\n");
		
		//TODO
		this.level++;
		this.money.AddMoney(50);
		
		ReceiveMessage("Your stats are now: " + "Level " + Integer.toString(this.level) + ", Money " + Integer.toString(this.money.GetAmount()) + "\n");
		
		
	}

}
