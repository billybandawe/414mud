package gamelogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gameentities.*;

public class Command {
	
	public String input;
	protected GamePlayer sender;
		
	public Command(String command, GamePlayer player){
		this.input = input;
		this.sender = player;
	}
	
	public void ParseCommand() {
		List<String> splitInput = Arrays.asList(input.split("\\s"));
		String command = splitInput.remove(0);
		
		switch (command) {
			case "say":
				String dialogue = GameActions.Say(splitInput);
				PassMessage(sender, this.sender.GetCurrentRoom().getPlayersInRoom(), dialogue);
				break;
			case "attack":
				ProcessAttack(splitInput.get(0));
				break;
			default:
				MovePlayer(command);
		}
	}
	
	public void ProcessAttack(String playerToAttack) {
		List<GamePlayer> playersInRoom = this.sender.GetCurrentRoom().getPlayersInRoom();
		
		GamePlayer playerBeingAttacked = null;
		
		for (GamePlayer player : playersInRoom) {
			if (player.getConnection().getName().compareToIgnoreCase(playerToAttack) == 0) {
				playerBeingAttacked = player;
			}
		}
		
		if (playerBeingAttacked != null) {
			int attackerLevel = this.sender.GetLevel();
			int attackeeLevel = playerBeingAttacked.GetLevel();
			double random = Math.random();
			double levelDifference = attackerLevel - attackeeLevel;
			double probabilityOffset = (attackerLevel - attackeeLevel)/10;
			
			//Attacker wins
			if (levelDifference > 5) {
				
				this.sender.UpdateLevel(playerBeingAttacked.getConnection().getName());
				playerBeingAttacked.KillPlayer(this.sender.getConnection().getName());
				
			} else if (levelDifference < -5) {
				
				playerBeingAttacked.UpdateLevel(this.sender.getConnection().getName());
				this.sender.KillPlayer(playerBeingAttacked.getConnection().getName());
				
			} else {
				
				if (random > 0.5 - probabilityOffset) {
					this.sender.UpdateLevel(playerBeingAttacked.getConnection().getName());
					playerBeingAttacked.KillPlayer(this.sender.getConnection().getName());
				} else {
					playerBeingAttacked.UpdateLevel(this.sender.getConnection().getName());
					this.sender.KillPlayer(playerBeingAttacked.getConnection().getName());
				}
				
			}
		}
	}
	
	public void PassMessage(GamePlayer sender, List<GamePlayer> receivers, String message) {
		for(GamePlayer receiver : receivers) {
			receiver.ReceiveMessage(sender.getConnection().getName() + "\\ssays: " + message);
		}
	}
	
	public void MovePlayer(String command) {
		GameRoom[] roomsNearby = this.sender.GetCurrentRoom().RoomsNearby();
		
		switch (command) {
			case "north":
				this.sender.SetRoom(roomsNearby[0]);
			case "south":
				this.sender.SetRoom(roomsNearby[1]);
			case "east":
				this.sender.SetRoom(roomsNearby[2]);
			case "west":
				this.sender.SetRoom(roomsNearby[3]);
			default:
				this.sender.ReceiveMessage("Invalid command. Please try again...");
		}
	}
	
	

}
