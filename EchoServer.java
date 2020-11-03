// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;
	private ChatIF serverUI;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port, ChatIF serverUI) {
		super(port);
		this.serverUI = serverUI;
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg.toString().toCharArray()[0] == '#') {

			String[] commandArr = msg.toString().split(" ");
			System.out.println("Message received: #login " + commandArr[1] + " from null.");
			client.setInfo("loginId", commandArr[1]);
			System.out.println(commandArr[1] + " has logged on");
			this.sendToAllClients(commandArr[1] + " has logged on");

		}

		else {
			if (msg.toString().split(" ")[2].equals("disconnected")) {
				System.out.println(client.getInfo("loginId") + " has disconnected.");
				this.sendToAllClients(client.getInfo("loginId") + " has disconnected.");
			} else {
				System.out.println("Message received: " + msg + " from " + client.getInfo("loginId"));
				this.sendToAllClients(client.getInfo("loginId") + "> " + msg);
			}
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
		this.sendToAllClients("Warning - The server has stopped listening for connections.");
	}

	protected void clientConnected(ConnectionToClient client) {
		try {
			System.out.println("A new client is attempting to connect.");
			client.sendToClient("Welcome to the server!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Hook method called each time a client disconnects. The default implementation
	 * does nothing. The method may be overridden by subclasses but should remains
	 * synchronized.
	 *
	 * @param client the connection with the client.
	 */
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		System.out.println(client.getInfo("loginId") + " has disconnected.");
	}

	// Class methods ***************************************************

	public void handleMessageFromServerUI(String message) {
		try {
			if (message.toCharArray()[0] == '#') {
				String[] commandArr = message.split(" ");
				switch (commandArr[0].substring(1, commandArr[0].length())) {
				case ("quit"):
					System.exit(0);
					break;
				case ("stop"):
					stopListening();
					break;
				case ("close"):
					this.sendToAllClients(
							"SERVER SHUTTING DOWN! DISCONNECTING!\n" + "Abnormal termination of connection.");
					super.close();
					break;
				case ("setport"):
					if (!super.isListening()) {
						setPort(Integer.parseInt(commandArr[1]));
						System.out.println("Port set to: " + Integer.parseInt(commandArr[1]));
					} else {
						serverUI.display("Already connected!");
					}
					break;
				case ("start"):
					if (!super.isListening()) {
						super.listen();
					} else {
						serverUI.display("Already connected!");
					}
					break;
				case ("getport"):
					serverUI.display(Integer.toString(super.getPort()));
					break;

				}
			} else {
				super.sendToAllClients("SERVER MSG> " + message);
			}
		} catch (IOException e) {
			serverUI.display("Could not send message to server.  Terminating server.");
			// quit();

		}

	}
}
//End of EchoServer class
