package main;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/** Commands. eg, a connection has limited options (Newbie,) but once you have
 a body, you can do much more (Common) but some players be able to shutdown the
 mud (Immortal.) Very primitive.

 @author Neil */

public class Commandset {

	public enum Level { NEWBIE, COMMON, IMMORTAL }

	private final Level level;
	private final Map<String, Method> commands = new HashMap<String, Method>();

	/** Gets a Commandset appropriate to level.
	 @param level
		The level, Commandset.Level.{ NEWBIE, COMMON, IMMORTAL }. */
	public Commandset(Level level) {
		this.level = level;
		Class<?> c = this.getClass();
		try {
			/* these are all levels */
			switch(level) {
				case IMMORTAL:
				case COMMON:
					break;
				case NEWBIE:
					commands.put("say", c.getDeclaredMethod("say", Connection.class, String.class));
					break;
			}
		} catch (NoSuchMethodException e) {
			System.err.print(this + ": " + e + "!\n");
		}
	}

	/** This parses the string and runs it.
	 @param cmd
		A command to parse. */
	public void interpret(final Connection c, final String command) {
		String cmd, arg;

		//System.err.print(c + " running Command::interpret: " + command + ".\n");

		/* break the string up */
		int space = command.indexOf(' ');
		if(space != -1) {
			cmd = command.substring(0, space);
			arg = command.substring(space + 1);
		} else {
			cmd = command;
			arg = "";
		}

		/* parse */
		Method run = commands.get(cmd);

		/* run */
		if(run == null) {
			System.out.print("Huh? " + cmd + "\n");
		} else {
			try {
				/* null: static method; fixme: extend Connection */
				run.invoke(null, c, arg);
			} catch(Exception e) {
				c.sendTo("Can't do that.\n");
				System.err.print(c + " input '" + command + "' which: " + e + ".\n");
			}
		}
	}

	/** @return Synecdoche. */
	public String toString() {
		return "CommandSet " + level;
	}

	/** these are all the commands! */

	static void say(final Connection c, final String line) {
		System.out.print(c + ": " + line + "\n");
	}

}
