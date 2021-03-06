package main;

import java.net.ServerSocket;
import java.net.SocketException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import gameentities.Room;
import gameentities.Object;
import gameentities.NPC;
import main.Connection;

/** This is the entry-point for starting the mud and listening for connections.
 @author Neil */

public class FourOneFourMud implements Iterable<Connection> {

	private static final int fibonacci20    = 6765; /* fixme: should be variable */
	private static final int maxConnections = 256;
	private static final int sShutdownTime  = 10;

	private static final String area        = "troter.area"; /* fixme: not used */

	private static String name     = "414Mud";
	private static String password = "";
	private static String motd     = "Hello.";

	/** Starts up the mud and listens for connections.
	 @param args
		for future use */
	public static void main(String args[]) {

		if(args.length <= 0) System.err.print("java main.FourOneFourMud [<mud name> [<set password> [<motd>]]]");
		if(args.length >= 1) name     = args[0];
		if(args.length >= 2) password = args[1];
		if(args.length >= 3) motd     = args[2];
		System.err.print("Set MUD name: <" + name + ">.\n");
		System.err.print("Set the secret password for becoming an Immortal: <" + password + ">.\n");
		System.err.print("Set MOTD: <" + motd + ">.\n");

		FourOneFourMud mud;

		try {
			mud = new FourOneFourMud(fibonacci20, maxConnections);
		} catch (IOException e) {
			System.err.print("Connection wouldn't complete: " + e + ".\n");
			/* deal-breaker */
			return;
		}

		mud.run();

		mud.shutdown();

	}

	private final ServerSocket    serverSocket;
	private final ExecutorService pool;

	private List<Connection> clients = new LinkedList<Connection>();

	private Room centerOfUniverse;

	/* fixme: whenStarted, name-done, connected-done, players-done, etc . . . */

	/** The entire mud constructor.
	 @param port
		the mud port
	 @param poolSize
		how many simultaneous connections should we allow
	 @throws IOException
		Passes the IOException from the underlyieng sockets. */
	public FourOneFourMud(int port, int poolSize) throws IOException {
		System.err.print("414Mud starting up on port " + port
						 + "; FixedThreadPool size " + poolSize + ".\n");
		serverSocket = new ServerSocket(port);
		pool         = Executors.newFixedThreadPool(poolSize);
		centerOfUniverse = load(area);
	}

	/** Run the mud. */
	private void run() {
		/* fixme: how to get try-with-resorces to work? */
		try {
			for( ; ; ) {
				/* fixme! immortal -> newbie (makes testing difficult) */
				Connection client = new Connection(serverSocket.accept(), this);
				clients.add(client);
				pool.execute(client);
			}
		} catch(SocketException e) {
			/* this occurs if the serverSocket is closed; yes, this is how we
			 shut it down :[ */
			System.err.print(this + " shutting down.\n");
		} catch(IOException e) {
			System.err.print("Shutting down: " + e + ".\n");
		} finally {
			/* reject incoming tasks */
			pool.shutdown();
			try {
				System.err.print("Waiting " + sShutdownTime + "s for clients to terminate.\n");
				if(!pool.awaitTermination(sShutdownTime, TimeUnit.SECONDS)) {
					System.err.print("Terminating clients " + sShutdownTime + "s.\n");
					pool.shutdownNow();
					if(!pool.awaitTermination(sShutdownTime, TimeUnit.SECONDS)) {
						System.err.print("A clients did not terminate.\n");
					}
				}
				System.err.print("Server socket closing.\n");
				serverSocket.close(); // fixme: autoclosable, will already be closed in most sit
			} catch(InterruptedException ie) {
				// (Re-)Cancel if current thread also interrupted
				pool.shutdownNow();
				// Preserve interrupt status
				Thread.currentThread().interrupt();
			} catch(IOException e) {
				System.err.print("Server socket error. " + e + ".\n");
			}
		}

	}

	/** Closes a connection.
	 @param c The connection to close. */
	public void deleteClient(Connection c) {
		System.err.print(c + " is closed: " + c.getSocket().isClosed() + "; removing from " + name + ".\n");
		clients.remove(c);
	}

	/** closes the server; it will detect this, and shutdown */
	public void shutdown() {
		try {
			serverSocket.close();
		} catch(IOException e) {
			System.err.print("414Mud::shutdown: badness. " + e + ".\n");
		}
	}

	/** prints out the mud info */
	public String toString() {
		return name;
	}

	/* hack: load from file; this is how Burnside is laid out in my imagination */
	private Room load(String area) {
		//////////////////////////////////////////////
		/* fixme: Map<String, Stuff> load() */ //<---

		/*Room center = new Room();
		center.setName("centre");
		center.setTitle("The Center Room");
		center.setDescription("The Center room.");
		Room north  = new Room();
		north.setName("north");
		north.setTitle("The North Room");
		north.setDescription("The Center room.");
		center.connectDirection(Room.Direction.N, north);
		Object object = new Object();
		object.transportTo(center);*/

		/* draw a map on paper */

		Room a1 = new Room("stairs", "North-West stairs.", "The base of the stairs.");
		Room a1u = new Room("stairs", "North-West stairs.", "There is construction and the stairs to the outside are blocked.");
		Room a2 = new Room("snack", "Snack counter.", "There is a snack counter here.");
		NPC woman = new NPC("woman", "A smiling woman waits here to take your order.", /*friendly*/true, /*xeno*/false);
		woman.transportTo(a2);
		Room a3 = new Room("tunnel", "Tunnel to MacDonald-Stewart", "The tunnel is locked for the night.");
		Object vending = new Object("vending", "A vending-machine is here.", /*break*/true, /*trans*/false);
		vending.transportTo(a3);
		
		Room b1 = new Room("1b21", "A classroom.", "There are no windows and the smell of chalk permeates the air.");
		Object desk = new Object("desk", "A old-school desk is here.", true, true);
		desk.transportTo(b1);
		Room b2 = new Room("hall", "A concrete hall.", "Southwards is the main area. East is a class, west is an access door that's locked and a door to parking.");

		Room c1 = new Room("sums", "Sums.", "This is the Society of Undergradate Math Students.");
		Room c2 = new Room("north-west", "North-West hallway.", "There are desks on the concrete walls.");
		Room c3 = new Room("north", "North hallway.", "There are lockers.");
		Room c3s = new Room("stair1B-N", "North stairs.", "The emergency exit stairs at the Basement.");
		Room c3u = new Room("stair0-N", "North stairs.", "Constuction on the emergency exit stairs at the main level prevents you from going further.");
		Room c4 = new Room("north", "North hallway.", "A closed and intercommed door to the North that goes to the server room.");
		Room c4s = new Room("washroom", "Mens washroom.", "There are many urinals.");
		Room c5 = new Room("north-east", "North-East hallway.", "There is a booth that used to have a free phone.");
		Room c6 = new Room("exit", "Exit", "The sign on the exit says Emergency.");
		Object trap = new Object("trap", "A rat-trap says 'do not remove.'", true, true);
		trap.transportTo(c6);

		Room d1 = new Room("ctf", "Ctf.", "The Computer Task-Force of the Science Undergradute Society.");
		Object stapler = new Object("stapler", "A stapler.", true, true);
		stapler.transportTo(d1);
		Room d2 = new Room("west", "West hallway.", "The busiest part of the basement. The elevators to the East are all broken.");
		Room d5 = new Room("east", "East hallway.", "No one hardly comes here. There are poster-boards with all kinds of advertising.");
		Room d6 = new Room("1b50", "A classroom.", "A dimly-lit classroom.");

		Room e1 = new Room("1b25", "Ctf computer room.", "Computers everywhere.");
		Room e2 = new Room("south-west", "South-West hallway.", "A couple of uncomfortable couches.");
		Room e3 = new Room("south", "South hallway.", "There are drinking fountains.");
		Room e3n = new Room("stair1B-S", "South stairs.", "The emergency exit stairs at the Basement.");
		Room e3u = new Room("stair0-S", "South stairs.", "Constuction on the emergency exit stairs at the main level prevents you from going further.");
		Room e3s = new Room("1b45", "1B45.", "There are no windows.");
		Room e4 = new Room("south", "South hallway.", "There are flyers posted on corkboards.");
		Room e4n = new Room("washroom", "Womans washroom.", "There are many stalls.");
		Room e5 = new Room("south-east", "South-East hallway.", "The classrooms are locked to the South and East.");

		Room f1 = new Room("stairs", "South-West stairs.", "Stairs lead up to the suface.");
		Room f1u = new Room("stairs", "North-West stairs.", "The door outside is locked.");
		Room f2 = new Room("south", "Hallway.", "Hallway at the South end.");
		Room f3 = new Room("tunnel", "Tunnel to Ottomass.", "The tunnel is locked. There is a sign from the 70s showing the McGill tunnel system.");

		a1.connectDirection(Room.Direction.E, a2);
		a2.connectDirection(Room.Direction.E, a3);
		a2.connectDirection(Room.Direction.S, b2);
		a1.connectDirection(Room.Direction.U, a1u);

		b1.connectDirection(Room.Direction.E, b2);
		b2.connectDirection(Room.Direction.S, c2);

		c1.connectDirection(Room.Direction.E, c2);
		c2.connectDirection(Room.Direction.E, c3);
		c3.connectDirection(Room.Direction.E, c4);
		c4.connectDirection(Room.Direction.E, c5);
		c5.connectDirection(Room.Direction.E, c6);
		c2.connectDirection(Room.Direction.S, d2);
		c3.connectDirection(Room.Direction.S, c3s);
		c4.connectDirection(Room.Direction.S, c4s);
		c5.connectDirection(Room.Direction.S, d5);
		c3s.connectDirection(Room.Direction.U, c3u);

		d1.connectDirection(Room.Direction.E, d2);
		d5.connectDirection(Room.Direction.E, d6);
		d2.connectDirection(Room.Direction.S, e2);
		d5.connectDirection(Room.Direction.S, e5);

		e1.connectDirection(Room.Direction.E, e2);
		e2.connectDirection(Room.Direction.E, e3);
		e3.connectDirection(Room.Direction.E, e4);
		e4.connectDirection(Room.Direction.E, e5);
		e2.connectDirection(Room.Direction.S, f2);
		e3.connectDirection(Room.Direction.N, e3n);
		e3.connectDirection(Room.Direction.S, e3s);
		e4.connectDirection(Room.Direction.N, e4n);
		e4.setDirection(Room.Direction.S, e3s); /* we have two exits in 1B45 */
		e3n.connectDirection(Room.Direction.U, e3u);

		f1.connectDirection(Room.Direction.E, f2);
		f2.connectDirection(Room.Direction.E, f3);
		f1.connectDirection(Room.Direction.U, f1u);

		return e3s; /* 1B45 where our class is held; 304-414 2014-Fall */
	}

	/** @return The place you start. */
	public Room getUniverse() {
		return this.centerOfUniverse;
	}

	/** @param p
		The test password.
	 @return True is the password matches the one when the mud started up. */
	public boolean comparePassword(final String p) {
		return p.compareTo(password) == 0;
	}

	/** A list of commands that the Connection
	 @@deprecated @see{#iterator}
	public List<Connection> getClients() {
		return clients;
	}*/

	/** @return The name set when the mud was started. */
	public String getName() {
		return name;
	}

	/** @return Gets the iterator of all the connections. */
	public Iterator<Connection> iterator() {
		return clients.iterator();
	}

	/** @return Gets the Message of the Day. */
	public String getMotd() {
		return motd;
	}

}
