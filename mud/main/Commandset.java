package main;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

//import java.net.Socket; /* for Connection */
import gameentities.Stuff;
import gameentities.Player;
import gameentities.Room;

/** Commands. eg, a connection has limited options (Newbie,) but once you have
 a body, you can do much more (Common) but some players be able to shutdown the
 mud, create things, etc (Immortal.)

 @author Neil */

public class Commandset {

	/* these are the commands! */
	
	private static void help(final Connection c, final String arg) {
		Commandset set = c.getCommandset();
		c.sendTo("These are the commands which you are authorised to use right now:");
		for (Map.Entry<String, Method> entry : set.commands.entrySet()) {
			c.sendTo(entry.getKey()/* + ":" + entry.getValue()*/);
		}
	}

	private static void exit(final Connection c, final String arg) {
		System.err.print(c + " has exited.\n");
		c.setExit();
	}

	private static void say(final Connection c, final String arg) {
		System.out.print(c + ": " + arg + "\n");
		/* fixme: say to room! */
		c.sendTo(c + ": " + arg);
	}

	private static void cant(final Connection c, final String arg) {
		c.sendTo("You can't do that, yet. Use create <name> to create your character.");
	}

	private static void create(final Connection c, final String arg) {
		Player p = new Player(c, arg);
		c.sendTo("You create a character named " + arg + "!");
		c.setPlayer(p);
		Room r = c.getMud().getUniverse();
		p.placeIn(r);
	}

	private static void look(final Connection c, final String arg) {
		Player p = c.getPlayer();
		if(p == null) {
			c.sendTo("You don't have eyes yet.");
			return;
		}
		Stuff surround = p.getIn();
		if(surround == null) {
			c.sendTo("You are floating in space.");
			return;
		}
		c.sendTo(surround.line);
	}

	private static void shutdown(final Connection c, final String arg) {
		if(arg.length() != 0) {
			c.sendTo("Command takes no arguments.");
			return;
		}
		System.out.print(c + " initated shutdown.\n");
		c.setExit();
		c.getMud().shutdown();
	}

	private static void ascend(final Connection c, final String arg) {
		System.err.print(c + " is ascending. (Not! that's not implemented.)\n");
		c.sendTo("Not implemented.");
	}

	/* this is the setup for dealing with them */

	public enum Level { NEWBIE, COMMON, IMMORTAL }

	private final Level level;
	private final Map<String, Method> commands = new HashMap<String, Method>();

	/** Gets a Commandset appropriate to level.
	 @param level
		The level, Commandset.Level.{ NEWBIE, COMMON, IMMORTAL }. */
	public Commandset(Level level) {
		this.level = level;

		add("exit", "exit");
		add("quit", "exit");
		add("help", "help");
		add("?", "help");
		switch(level) {
			case IMMORTAL:
				add("shutdown", "shutdown");
			case COMMON:
				add("look", "look");
				add("say", "say");
				add("chat", "cant"/*fixme*/);
				add("ascend", "ascend");
				break;
			case NEWBIE:
				add("say",    "cant");
				add("chat",   "cant");
				add("create", "create");
				/* debug */
				add("shutdown", "shutdown");
				/* fixme */
				add("look", "look");
				break;
		}
	}

	private void add(final String command, final String method) {
		try {
			commands.put(command, this.getClass().getDeclaredMethod(method, Connection.class, String.class));
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

		/* break the string up (fixme: I suppose would could be pedantic and
		 make it any white space; more difficult) */
		int space = command.indexOf(' ');
		if(space != -1) {
			cmd = command.substring(0, space);
			arg = command.substring(space).trim();
		} else {
			cmd = command;
			arg = "";
		}

		/* parse */
		Method run = commands.get(cmd);

		/* run */
		if(run == null) {
			c.sendTo("Huh? " + cmd);
		} else {
			try {
				/* null: static method; extend Connection? nice try,
				 "object is not an instance of declaring class" whatev */
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

}
