package main;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import gameentities.Player;

/** Connections are the people connected to our mud; later we will build a
 character around them and put them in the game.
 @author Neil */

public class Connection implements Runnable {

	private static final int bufferSize = 80;
	private static final Commandset newbie   = new Commandset(Commandset.Level.NEWBIE);
	private static final Commandset common   = new Commandset(Commandset.Level.COMMON);
	private static final Commandset immortal = new Commandset(Commandset.Level.IMMORTAL);

	private final Socket socket;
	private final String name = Orcish.get();
	private final FourOneFourMud mud;
	private Commandset commands;
	private PrintWriter     out;
	private BufferedReader   in;
	private char buffer[];
	private Player  player = null;
	private boolean isExit = false;
	/* fixme: ip */

	/** Initalize the connection.
	 @param socket
		the client socket */
	Connection(final Socket socket, final FourOneFourMud mud) {
		System.err.print(this + " has connected to " + mud + ".\n");
		this.commands = newbie;
		this.socket   = socket;
		this.mud      = mud;
		this.buffer   = new char[bufferSize];
	}

	/** The server-side handler for connections. */
	public void run() {
		//System.err.print(this + " up and running, waiting for character creation.\n");
		try(
			PrintWriter   out = new PrintWriter(socket.getOutputStream(), true /* autoflush (doesn't work) */);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		) {
			String input;

			/* make these class variables so others can talk to us using
			 sentTo(), getFrom() */
			this.out = out;
			this.in  = in;

			this.sendTo("You are " + this + ".");

			while(!isExit && (input = this.getFrom()) != null) {

				if(input.length() == 0) continue;

				//this.sendTo(this + " sent \"" + input + ".\"");
				commands.interpret(this, input);

			}

			this.sendTo("Closing " + this + ".");
			socket.close();
			mud.deleteClient(this);

		} catch(UnsupportedEncodingException e) {
			System.err.print(this + " doesn't like UTF-8: " + e + ".\n");
		} catch(IOException e) {
			System.err.print(this + ": " + e + ".\n");
		} finally {
			this.out = null;
			this.in  = null;
		}

	}

	/** Send a message to the connection.
	 @param message
		The message. */
	public void sendTo(final String message) {
		/* "telnet newline sequence \r\n" <- or this? */
		if(out == null) return;
		out.print(message + "\n");
		out.flush();
		//System.err.print("Sending " + this + ": " + message + "\n");
	}

	/** Wait for a message from the connection. Ignores characters beyond
	 bufferSize.
	 @return The message. */
	public String getFrom() throws IOException {
		if(in == null) return null;
		/* no way that's safe: return in.readLine();*/
		int no = in.read(buffer, 0, bufferSize);
		if(no == -1) return null; /* steam closed */
		if(no >= bufferSize) {
			/* it will never be > bufferSize, I'm just being paranoid */
			no = bufferSize;
			//System.err.print("Skipping characters.\n");
			//if(in.ready()) in.readLine(); /* <- fixme: still allocating mem :[ */
			while(in.ready()) in.skip(1);   /* <- fixme: O(n); hack! */
		}
		String input = new String(buffer, 0, no).trim();
		//System.err.print(this + ".getFrom: " + input + "\n");

		return input;
	}

	public FourOneFourMud getMud() {
		return mud;
	}

	public String toString() {
		return "Connection " + name;
	}

	public String getName() {
		return this.name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setImmortal() {
		commands = immortal;
	}

	public Commandset getCommandset() {
		return commands;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player p) {
		player = p;
		commands = common;
	}

	public void setExit() {
		isExit = true;
	}

	/* not used */
	public void sendToRoom(final String s) {
		if(player == null) return;
		player.sendToRoom(s);
	}

}
