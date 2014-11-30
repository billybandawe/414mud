package main;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/** Commands. eg, a connection has limited options (Newbie,) but once you have
 a body, you can do much more (Common) but some players be able to shutdown the
 mud (Immortal.)

 @author Neil */

public class Commandset {

	//public enum Level 
	private final String name;
	private final Map<String, Method> commands = new HashMap<String, Method>();

	public Commandset(String name) {
		Class<?> c = this.getClass();
		this.name = name;
		try {
			//Method m = c.getMethod("say", String.class);
			Method m = c.getDeclaredMethod("say", String.class);
			commands.put("say", m);
			//commands.put("say", Commandset.class.getMethod("say", Connection.class, String.class));
			//commands.put("say", Commands.class.getMethod("say", Connection.class, String.class));
		} catch (NoSuchMethodException e) {
			System.err.print(this + ": " + e + "!\n");
		}
	}

	/** This parses the string and runs it.
	 @param cmd
	 A command to parse.
	 @return A method if the string was an actual command. */
	public void interpret(final Connection c, final String cmd) {
		System.err.print("Command::interpret: " + cmd + ".\n");
		Method run = commands.get(cmd);
		if(run == null) {
			System.out.print("Huh? " + cmd + "\n");
		} else {
			try {
				run.invoke(c, cmd);
			} catch(Exception e) {
				System.err.print("Can't do that: " + e + ".\n");
			}
		}
	}

	public String toString() {
		return "CommandSet " + name;
	}

	static void say(String l) {
		System.out.print("Worked " + l + ".\n");
	}
/*	static void say(final Connection c, final String line) {
		System.out.print(c + ": " + line + "\n");
	}*/

//	private static final Map<String, Method> commands = new HashMap<String, Method>();

/*	static {
		try {
			commands.put("say", Commands.class.getMethod("say", Connection.class, String.class));
		} catch (NoSuchMethodException e) {
			System.err.print("Building command table: " + e + "!\n");
		}
	}

	static void say(final Connection c, final String line) {
		System.out.print(c + ": " + line + "\n");
	}*/

	/** This parses the string and runs it.
	 @param cmd
		A command to parse.
	 @return A method if the string was an actual command. */
/*	public static void interpret(final Connection c, final String cmd) {
		System.err.print("Command::interpret: " + cmd + ".\n");
		Method run = commands.get(cmd);
		if(run == null) {
			System.out.print("Huh? " + cmd + "\n");
		} else {
			try {
				run.invoke(c, cmd);
			} catch(Exception e) {
				System.err.print("Can't do that: " + e + ".\n");
			}
		}
	}*/

}
