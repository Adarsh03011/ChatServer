package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static BufferedReader br;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	public static Socket socket;
	private String host;
	private String username;
	private final int port;
	Client(String server, int port, String username) {
		this.host = server;
		this.port = port;
		this.username = username;
	}
	private void display(String msg) {
		System.out.println(msg);
	}
	public boolean checkSocket(){
		return socket.isClosed();
	}
	public boolean start() {
		try {
			socket = new Socket(host, port);
		} catch(Exception e) {
			display("Error connecting to server:" + e);
			return false;
		}
		try
		{
			br = new BufferedReader(new InputStreamReader(System.in));
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e) {
			display("Exception: " + e);
			return false;
		}
		Listener listen = new Listener();
		Thread l = new Thread(listen);
		l.start();
		try
		{
			out.writeObject(username);
		}
		catch (IOException e) {
			display("Exception " + e);
			disconnect();
			return false;
		}
		return true;
	}
	void sendMessage(ChatMessage msg) {
		try {
			out.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception: " + e);
		}
	}
	private void disconnect() {
		try {
			br.close();
			in.close();
			out.close();
			socket.close();
		}catch(Exception e) {
			display("Exception: " + e);
		}
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		int portNumber = 5555;
		String host = "localhost";
		String userName;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the username: ");
		userName = sc.nextLine();
		Client client = new Client(host, portNumber, userName);
		if(!client.start())
			return;
		System.out.println(userName + " have successfully joined the chatroom!.");
		System.out.println("Type '@username yourMessage' to send a private message");
		System.out.println("Type 'Active' to see list of active clients");
		System.out.println("Type 'Logout' to logoff from server");
		while(!client.checkSocket()) {
			String msg = br.ready() ? br.readLine() : "";
			if(msg.isEmpty()){
				continue;
			}
			if(!client.checkSocket()) {
				if (msg.equals("Logout")) {
					client.sendMessage(new ChatMessage(ChatMessage.Logout, ""));
					break;
				} else if (msg.equals("Active")) {
					client.sendMessage(new ChatMessage(ChatMessage.Active, ""));
				} else {
					client.sendMessage(new ChatMessage(ChatMessage.Message, msg));
				}
			}
			else{
				break;
			}
		}
		sc.close();
		client.disconnect();
	}
	class Listener implements Runnable {
		public void run() {
			while(true) {
				try {
					String msg = in.readObject().toString();
					System.out.println(msg);
					if(msg.equals("Closing the server complete all process and logout")) {
						socket.close();
						break;
					}
				}
				catch(IOException | ClassNotFoundException | NullPointerException e) {
					display( "Server has closed the connection: " + e);
					break;
				}
			}
		}
	}
}