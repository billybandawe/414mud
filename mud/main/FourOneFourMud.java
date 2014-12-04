package main;

import java.net.ServerSocket;
import java.net.SocketException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.LinkedList;

import gameentities.Room;
//import gameentities.Stuff;

/** This is the entry-point for starting the mud and listening for connections.
 @author Neil */

class FourOneFourMud {

	private static final int fibonacci20    = 6765;
	private static final int maxConnections = 256;
	private static final int sShutdownTime  = 20;
	private static final String area        = "troter.area";
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
		Room north  = new Room();
		north.setName("north");
		north.setTitle("The North Room");
		center.connectDirection(Room.Direction.N, north);
		return center;
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
}
