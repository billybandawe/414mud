package gamelogic;

import java.util.List;

public class GameActions {
	
	public static String Say(List<String> words) {
		StringBuilder sb = new StringBuilder();
		
		for (String word : words) {
			sb.append(word);
			sb.append("\\s");
		}
		
		return sb.toString();
	}

}
