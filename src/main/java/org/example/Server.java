package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private static int uniqueId;
	public ServerSocket serverSocket;
	private static List<Handler> users;
	private int port;
	public Server(int port) {
		this.port = port;
		users = new ArrayList<>();
	}
	public void startServer() {
		boolean Continue = true;
		try
		{
			serverSocket = new ServerSocket(port);
			display("Server waiting for Clients on port " + port + ".");
			while(Continue)
			{
				Socket socket = serverSocket.accept();
				if(!Continue) {
					break; }
				Handler h = new Handler(socket);
				users.add(h);
				Thread t = new Thread(h);
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < users.size(); ++i) {
					Handler h = users.get(i);
					try {
						h.in.close();
						h.out.close();
						h.socket.close();
					}
					catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
			String msg = ( " Exception on new ServerSocket: " + e + "\n");
			display(msg);
		}
	}
	private static void display(String msg) {
		System.out.println(msg);
	}
	private static synchronized void broadcast(String message) {
		String[] arr = message.split(" ",3);
		boolean isPrivate =  false;
		if(arr[1].charAt(0)=='@')
			isPrivate = true;
		String sender = arr[0].replace(":","");
		if(isPrivate)
		{
			String receiver=arr[1].substring(1);
			message=arr[0]+ " (private) " + arr[2];
			String message1 = message;
			int y=users.size();
			while (--y>= 0) {
				Handler h1=users.get(y);
				String check=h1.getUsername();
				if(check.equals(receiver))
				{
					if(!h1.writeMsg(message1)) {
						users.remove(y);
						display("Disconnected Client " + h1.username);
					}
					break;
				}
			}
		}
		else
		{
			display(message);
			int i = users.size();
			while (--i >= 0) {
				Handler h1 = users.get(i);
				if(!sender.equals(h1.username)){
					if(!h1.writeMsg(message)) {
						users.remove(i);
						display("Disconnected Client " + h1.username);
					}
				}
			}
		}
	}
	synchronized void remove(int id) {
		String disconnectedClient = "";
		for(int i = 0; i < users.size(); ++i) {
			Handler ct = users.get(i);
			if(ct.id == id) {
				disconnectedClient = ct.getUsername();
				users.remove(i);
				break;
			}
		}
		broadcast(disconnectedClient + " has left the chat room." );
	}
	public static class A extends Thread{
		@Override
		public void run() {
			broadcast("Closing the server complete all process and logout");
		}
	}
	public static void main(String[] args) {
		int portNumber = 5555;
		Runtime.getRuntime().addShutdownHook(new A());
		Server server = new Server(portNumber);
		server.startServer();
	}
	class Handler implements Runnable {
		private Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private int id;
		private String username;
		private ChatMessage cm;
		Handler(Socket socket) {
			id = ++uniqueId;
			this.socket = socket;
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				username = in.readObject().toString();
				for (int i = 0; i < users.size(); ++i) {
					Handler ct = users.get(i);
					if (ct.username.equals(username)) {
						username += "1";
					}
				}
				broadcast(username + " has joined the chat room.");
			} catch (IOException | ClassNotFoundException e) {
				display("Exception: " + e);
			}
		}
		public String getUsername() {
			return username;
		}
		public void run() {
			while (true) {
				try {
					cm = (ChatMessage) in.readObject();
				} catch (IOException | ClassNotFoundException e) {
					break;
				}
				String message = cm.getMessage();
				if (cm.getType() == 1) {
					broadcast(username + ": " + message);
				}
				if (cm.getType() == 2) {
					display( username + " disconnected.");
					break;
				}
				if (cm.getType() == 0) {
					writeMsg("List of the users connected ");
					for (Handler ct : users) {
						writeMsg(ct.username);
					}
				}
			}
			remove(id);
			close();
		}
		private void close() {
			try {
				out.close();
				in.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("Exception occurred : " + e);
			}
		}
		private boolean writeMsg(String msg) {
			if (!socket.isConnected()) {
				close();
				return false;
			}
			try {
				out.writeObject(msg);
			} catch (IOException e) {
				display("Exception " + e);
			}
			return true;
		}
	}
}