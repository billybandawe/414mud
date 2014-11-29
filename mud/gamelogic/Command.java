package gamelogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gameentities.*;

public class Command {
	
	public String input;
	protected GamePlayer sender;
	protected GamePlayer[] receiver;
		
	public Command(String command, GamePlayer player, GamePlayer[] args){
		this.input = input;
		this.sender = sender;
		this.receiver = args;
	}
	
	public void ParseCommand() {
		List<String> splitInput = Arrays.asList(input.split("\\s"));
		String command = splitInput.remove(0);
		
		switch (command) {
			case "say":
				String dialogue = GameActions.Say(splitInput);
				PassMessage(sender, receiver, dialogue);
				break;
				
			case "sayto":
				
		
		}
		
		
		
		
		
		
	}
	
	public void PassMessage(GamePlayer sender, GamePlayer[] receivers, String message) {
		for(GamePlayer receiver : receivers) {
			receiver.ReceiveMessage(sender.getConnection().getName() + "\\ssays " + message);
		}
		
	}
	
	

}
