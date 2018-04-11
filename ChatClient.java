import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {

    static int port = 5190;
    public static void main(String[] args) {
        ServerSocket server = null;
        int uid = 0; // User ID
        try {
            server = new ServerSocket(port);
            Connection[] connections = new Connection[20]; // Only handles 20 connections currently
            while(true) {
                System.out.println("Waiting for a connection on port: " + port);
                Socket client = server.accept();
                System.out.println("User " + (++uid) + " @ " +
                    client.getInetAddress().toString() + " connected.");
                new Connection(client,uid).start();
            }
        } catch (IOException ex) {
            System.out.println("Error occured in socket creation.");
        }
    }
}

class Connection extends Thread {
    Socket client;
    int uid;
    Connection(Socket new_client, int new_uid) {
        client = new_client;
        uid = new_uid;
    }
    public void run() {
        try {
            Scanner sin = new Scanner(client.getInputStream());
            PrintStream sout = new PrintStream(client.getOutputStream());
            sout.println("Connection recognized. Welcome!");
            String line = "";
            while(!line.equals("/quit")) {
                line = sin.nextLine();
                System.out.println("User " + uid + " said: " + line);
                sout.println("User " + uid + " said: " + line);
            }
            client.close();
            System.out.println("User " + uid + " @ " + 
                client.getInetAddress().toString() + " disconnected.");
        } catch (IOException ex) {
            System.out.println("Error occured in scanner creation.");
        }
    }
}