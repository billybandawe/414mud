package main;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.io.OutputStreamWriter;

/** Connections are the people connected to our mud; later we will build a
 character around them and put them in the game.
 @author Neil */

class Connection implements Runnable {

	private static final int bufferSize = 10;

	private final Socket socket;
	private final String name = Orcish.get();
	private final FourOneFourMud mud;
	private PrintWriter   out;
	private BufferedReader in;
	private char buffer[];
	/* fixme: ip */

	/** Initalize the connection.
	 @param socket
		the client socket */
	Connection(final Socket socket, final FourOneFourMud mud) {
		System.err.print(this + " initialising.\n");
		this.socket = socket;
		this.mud    = mud;
		this.buffer = new char[bufferSize];
	}

	/** The server-side handler for connections. */
	public void run() {
		System.err.print(this + " up and running, waiting for character creation.\n");
		try(
			PrintWriter   out = new PrintWriter(socket.getOutputStream(), true /* autoflush (doesn't work) */);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		) {
			String input;

			/* make these class variables so others can talk to us using
			 sentTo(), getFrom() */
			this.out = out;
			this.in  = in;

			this.sendTo("You are " + this + ".\n");

			while((input = this.getFrom()) != null) {

				if(input.length() == 0) break; /* <- they will be loged out */

				this.sendTo(this + " sent \"" + input + ".\"\n");

				/* migrate to Commands */
				if(input.compareToIgnoreCase("shutdown") == 0) {
					mud.shutdown();
					break;
				}
			}

			this.sendTo("Closing " + this + ".\n");

		} catch(UnsupportedEncodingException e) {
			System.err.print(this + " doesn't like UTF-8 " + e + ".\n");
		} catch(IOException e) {
			System.err.print(this + " " + e + ".\n");
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
		out.print(message);
		out.flush();
		System.err.print("Sending " + this + ": " + message);
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
			//System.err.print("Skipping characters.\n");
			//if(in.ready()) in.readLine(); /* <- fixme: still allocating mem :[ */
			while(in.ready()) in.skip(1);   /* <- fixme: O(n); hack! */
		}
		String input = new String(buffer, 0, no).trim();
		System.err.print(this + ".getFrom: " + input + "\n");

		return input;
	}

	public String toString() {
		return "Connection " + name;
	}

}
