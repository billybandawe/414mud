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

class FourOneFourMud implements Iterable<Connection> {

	private static final int fibonacci20    = 6765;
	private static final int maxConnections = 256;
	private static final int sShutdownTime  = 20;
	private static final String area        = "troter.area"; /* fixme */
	private static final String name        = "414Mud";

	private static String password;

	/** Starts up the mud and listens for connections.
	 @param args
		for future use */
	public static void main(String args[]) {

		/* sets the optional password to become an Immortal */
		password = "";
		if(args.length >= 1) password = args[0];
		System.err.print("Set the secret password for becoming an Immortal: <" + password + ">.\n");

		try {

			FourOneFourMud mud = new FourOneFourMud(fibonacci20, maxConnections);

			mud.run();

			mud.shutdown();

		} catch (IOException e) {
			/* deal-breaker */
			System.err.print("Connection wouldn't complete: " + e + ".\n");
		}
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
		how many simultaneous connections should we allow */
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

	/** Closes a connection. */
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

	private Room load(String area) {
		//////////////////////////////////////////////
		/* fixme: Map<String, Stuff> load() */ //<---
		Room center = new Room();
		center.setName("centre");
		center.setTitle("The Center Room");
		center.setDescription("The Center room.");
		Room north  = new Room();
		north.setName("north");
		north.setTitle("The North Room");
		north.setDescription("The Center room.");
		center.connectDirection(Room.Direction.N, north);
		Object object = new Object();
		object.transportTo(center);
		return loadBasement();
	}
	
	/* hack: load from file; this is how Burnside is laid out in my imagination */
	private Room loadBasement() {

		Room a1 = new Room("stairs", "North-West stairs.", "There is construction and the stairs to the outside are blocked.");
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
		Room c3a = new Room("north", "North hallway.", "There are lockers.");
		Room sbn = new Room("stair1B-N", "North stairs.", "The emergency exit stairs at the Basement.");
		Room c3b = new Room("north", "North hallway.", "A closed and intercommed door to the North that goes to the server room.");
		Room bam = new Room("washroom", "Mens washroom.", "There are many urinals.");
		Room c4 = new Room("north-east", "North-East hallway.", "There is a booth that used to have a free phone.");
		Room c5 = new Room("exit", "Exit", "The sign on the exit says Emergency.");
		Object trap = new Object("trap", "A rat-trap says 'do not remove.'", true, true);
		trap.transportTo(c5);

		Room d1 = new Room("ctf", "Ctf.", "The Computer Task-Force of the Science Undergradute Society.");
		Object stapler = new Object("stapler", "A stapler.", true, true);
		stapler.transportTo(d1);
		Room d2 = new Room("west", "West hallway.", "The busiest part of the basement. The elevators to the East are all broken.");
		Room d4 = new Room("east", "East hallway.", "No one hardly comes here. There are poster-boards with all kinds of advertising.");
		Room d5 = new Room("1b50", "A classroom.", "A dimly-lit classroom.");

		Room e1 = new Room("1b25", "Ctf computer room.", "Computers everywhere.");
		Room e2 = new Room("south-west", "South-West hallway.", "A couple of uncomfortable couches.");
		Room e3a = new Room("south", "South hallway.", "There are drinking fountains.");
		Room sbs = new Room("stair1B-S", "South stairs.", "The emergency exit stairs at the Basement.");
		Room e3b = new Room("south", "South hallway.", "There are flyers posted on corkboards.");
		Room baw = new Room("washroom", "Womans washroom.", "There are many stalls.");
		Room e4 = new Room("south-east", "South-East hallway.", "The classrooms are locked to the South and East.");

		Room f1 = new Room("stairs", "South-West stairs.", "Stairs lead up to the suface and the doors are locked.");
		Room f2 = new Room("south", "Hallway.", "Hallway at the South end.");
		Room f3 = new Room("tunnel", "Tunnel to Ottomass.", "The tunnel is locked. There is a sign from the 70s showing the McGill tunnel system.");
		Room clas = new Room("1b45", "1B45.", "There are no windows.");

		a1.connectDirection(Room.Direction.E, a2);
		a2.connectDirection(Room.Direction.E, a3);
		a2.connectDirection(Room.Direction.S, b2);

		b1.connectDirection(Room.Direction.E, b2);
		b2.connectDirection(Room.Direction.S, c2);

		c1.connectDirection(Room.Direction.E, c2);
		c2.connectDirection(Room.Direction.E, c3a);
		c3a.connectDirection(Room.Direction.E, c3b);
		c3b.connectDirection(Room.Direction.E, c4);
		c4.connectDirection(Room.Direction.E, c5);
		c2.connectDirection(Room.Direction.S, d2);
		c3a.connectDirection(Room.Direction.S, sbn);
		c3b.connectDirection(Room.Direction.S, bam);
		c4.connectDirection(Room.Direction.S, d4);

		d1.connectDirection(Room.Direction.E, d2);
		d4.connectDirection(Room.Direction.E, d5);
		d2.connectDirection(Room.Direction.S, e2);
		d4.connectDirection(Room.Direction.S, e4);

		e1.connectDirection(Room.Direction.E, e2);
		e2.connectDirection(Room.Direction.E, e3a);
		e3a.connectDirection(Room.Direction.E, e3b);
		e3b.connectDirection(Room.Direction.E, e4);
		e2.connectDirection(Room.Direction.S, f2);
		e3a.connectDirection(Room.Direction.N, sbs);
		e3b.connectDirection(Room.Direction.N, baw);
		e3a.connectDirection(Room.Direction.S, clas);
		e3b.setDirection(Room.Direction.S, clas); /* we have two exits in 1B45(?) */

		f1.connectDirection(Room.Direction.E, f2);
		f2.connectDirection(Room.Direction.E, f3);

		return clas;
	}

	public Room getUniverse() {
		return this.centerOfUniverse;
	}

	/** @param p
		The test password.
	 @return True is the password matches the one when the mud started up. */
	public boolean comparePassword(final String p) {
		return p.compareTo(password) == 0;
	}

	public List<Connection> getClients() {
		return clients;
	}

	public String getName() {
		return name;
	}
	
	public Iterator<Connection> iterator() {
		return clients.iterator();
	}

}
