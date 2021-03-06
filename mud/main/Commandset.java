package main;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.List;
import java.io.IOException;

import gameentities.Stuff;
import gameentities.Player;
import gameentities.Room;

/** Commands. eg, a connection has limited options (Newbie,) but once you have
 a body, you can do much more (Common) but some players be able to shutdown the
 mud, create things, etc (Immortal.)

 @author Neil */

public class Commandset {

	private static final int minName  = 3;
	private static final int maxName  = 8;
	private static final int maxUpper = 2;

	/* these are the commands! */
	
	private static void help(final Connection c, final String arg) {
		Commandset set = c.getCommandset();
		c.sendTo("These are the commands which you are authorised to use right now:");
		for (Map.Entry<String, Method> entry : set.commands.entrySet()) {
			c.sendTo(entry.getKey()/* + ":" + entry.getValue()*/);
		}
	}

	private static void exit(final Connection c, final String arg) {
		//System.err.print(c + " has exited.\n");
		Player p = c.getPlayer();
		if(p != null) p.sendToRoom(p + " has suddenly vashished.");
		c.sendTo("Goodbye.");
		c.setExit();
	}

	private static void say(final Connection c, final String arg) {
		Player p = c.getPlayer();
		if(p == null) return;
		//System.out.print(c + ": " + arg + "\n");
		c.sendTo("You say, \"" + arg + "\"");
		p.sendToRoom(p + " says \"" + arg + "\"");
	}

	private static void chat(final Connection c, final String arg) {
		Player p = c.getPlayer();
		if(p == null) return;
		String s = "[chat] " + p + ": " + arg;
		for(Connection everyone : c.getMud()) {
			//if(c == everyone) continue; <- echo to ones self is useful
			everyone.sendTo(s);
		}
	}

	private static void cant(final Connection c, final String arg) {
		c.sendTo("You can't do that, yet. Use create <name> to create your character.");
	}

	private static void create(final Connection c, final String arg) {
		/* this is where int, wis, are calculated; not that we have them */

		int len = arg.length();
		if(len < minName) {
			c.sendTo("Your name must be at least " + minName + " characters.");
			return;
		}
		if(len > maxName) {
			c.sendTo("Your name must be bounded by " + maxName + " characters.");
			return;
		}

		boolean isUpper, isLastUpper = false;
		boolean isFirst     = true;
		int     upper       = 0;

		for(char ch : arg.toCharArray()) {
			/*Character.isAlphabetic(), isTitleCase; <- sorry the rest of the world */
			if(!Character.isLetter(ch)) {
				c.sendTo("Your name can only be letters.");
				return;
			}
			isUpper = !Character.isLowerCase(ch);
			if(isUpper) upper++;
			if(isFirst && !isUpper
			   || isLastUpper && isUpper
			   || upper > maxUpper) {
				c.sendTo("Appropriate capitalisation please.");
				return;
			}
			isFirst = false;
			isLastUpper = isUpper;
		}
		/* fixme: compare file of bad names (like ***k and such) */
		/* fixme: compare with other players! */

		/* passed the grammar police */
		Player p = new Player(c, arg);
		c.setPlayer(p);
		System.err.print(c + " has created " + arg + ".\n");
		c.sendTo("You create a character named " + arg + "!");

		Room r = c.getMud().getUniverse();
		p.transportTo(r);
		p.lookAtStuff();
	}
	
	private static void look(final Connection c, final String arg) {
		Player p = c.getPlayer();
		if(p == null) {
			c.sendTo("You don't have eyes yet.");
			return;
		}

		/* look at the room (Stuff in) */
		Stuff surround = p.getIn();
		if(surround == null) {
			c.sendTo("You are floating in space.");
			return;
		}
		if(arg.length() > 0) {
			int count = 0;
			/* look at things */
			for(Stuff stuff : surround) {
				if(arg.equals(stuff.getName())) {
					c.sendTo(stuff.look());
					count++;
				}
			}
			/* fixme: look at exits */
			if(count == 0) c.sendTo("There is no '" + arg + "' here.");
		} else {
			c.sendTo(surround.lookDetailed());
			/* look at the Stuff */
			p.lookAtStuff();
		}
	}

	private static void shutdown(final Connection c, final String arg) {

		if(arg.length() != 0) {
			c.sendTo("Command takes no arguments.");
			return;
		}

		Player p = c.getPlayer();
		if(p == null) return;

		System.out.print(c + " initated shutdown.\n");

		String s = p + " initiated shutdown!";
		for(Connection everyone : c.getMud()) {
			everyone.sendTo(s);
			everyone.setExit(); /* doesn't work -- Connection stuck waiting */
			try {
				everyone.getSocket().close();
			} catch(IOException e) {
				System.err.print(everyone + " just wouldn't close: " + e + ".\n");
			}
		}

		c.setExit();
		c.getMud().shutdown();
	}

	private static void ascend(final Connection c, final String arg) {
		Player p = c.getPlayer();
		if(p == null) {
			c.sendTo("You must have a body.");
			return;
		}
		if(!c.getMud().comparePassword(arg)) {
			c.sendTo("That's not the password.");
			return;
		}
		p.sendToRoom("A glorious light surronds " + p + " as they ascend.");
		c.setImmortal();
		System.err.print(c + " has ascended.\n");
		c.sendTo("You are now an immortal; type 'help' for new commands.");
	}

	private static void north(final Connection c, final String arg) {
		Stuff in;
		Player p = c.getPlayer();
		if(p == null) return;
		p.go(Room.Direction.N);
		//c.sendTo(p.look());
		if((in = p.getIn()) == null) return;
		c.sendTo(in.look());
		p.lookAtStuff();
	}

	private static void east(final Connection c, final String arg) {
		Stuff in;
		Player p = c.getPlayer();
		if(p == null) return;
		p.go(Room.Direction.E);
		if((in = p.getIn()) == null) return;
		c.sendTo(in.look());
		p.lookAtStuff();
	}

	private static void south(final Connection c, final String arg) {
		Stuff in;
		Player p = c.getPlayer();
		if(p == null) return;
		p.go(Room.Direction.S);
		if((in = p.getIn()) == null) return;
		c.sendTo(in.look());
		p.lookAtStuff();
	}

	private static void west(final Connection c, final String arg) {
		Stuff in;
		Player p = c.getPlayer();
		if(p == null) return;
		p.go(Room.Direction.W);
		if((in = p.getIn()) == null) return;
		c.sendTo(in.look());
		p.lookAtStuff();
	}

	private static void up(final Connection c, final String arg) {
		Stuff in;
		Player p = c.getPlayer();
		if(p == null) return;
		p.go(Room.Direction.U);
		if((in = p.getIn()) == null) return;
		c.sendTo(in.look());
		p.lookAtStuff();
	}

	private static void down(final Connection c, final String arg) {
		Stuff in;
		Player p = c.getPlayer();
		if(p == null) return;
		p.go(Room.Direction.D);
		if((in = p.getIn()) == null) return;
		c.sendTo(in.look());
		p.lookAtStuff();
	}

	private static void who(final Connection c, final String arg) {
		Player p;
		c.sendTo("Active connections:");
		for(Connection who : c.getMud()) {
			p = who.getPlayer();
			c.sendTo(who + " (" + (p != null ? p.getName() : "not in game") + ")");
		}
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
		add("who", "who");

		/* these are level-specific */
		switch(level) {
			case IMMORTAL:
				add("shutdown", "shutdown");
			case COMMON:
				add("look", "look");
				add("l",    "look");
				add("say",  "say");
				add("'",    "say");
				add("chat", "chat");
				add(".",    "chat");
				if(level != Level.IMMORTAL) add("ascend", "ascend");
				add("n",    "north");
				add("e",    "east");
				add("s",    "south");
				add("w",    "west");
				add("u",    "up");
				add("d",    "down");
				add("north","north");
				add("east", "east");
				add("south","south");
				add("west", "west");
				add("up",   "up");
				add("down", "down");
				break;
			case NEWBIE:
				add("create", "create");
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
	 @param c
		The connection that's attributed the command.
	 @param command
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
			c.sendTo("Huh? " + cmd + " (use help for a list)");
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
