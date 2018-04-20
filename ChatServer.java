// Graeme Ferguson | N19023160 | 04/11/18

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    static int port = 5190;
    public static void main(String[] args) {
        ServerSocket server = null;
        int uid = 0; // User ID
        try {
            server = new ServerSocket(port);
            // Handles only n-set connections, consider HashMap?
            ArrayList<Connection> connections = new ArrayList<Connection>(20);
            Connection conn = null;
            while(true) {
                System.out.println("Waiting for a connection on port: "+port);
                Socket client = server.accept();
                System.out.println("User "+(++uid)+" @ "
                    +client.getInetAddress().toString()+" connected.");
                conn = new Connection(client,uid,connections);
                if(isPrune(conn,connections)) {
                    uid--;
                    System.out.println("Already connected user attempted to reconnect.");
                }
                else {
                    connections.add(conn);
                    conn.start();
                }
            }
        } catch (IOException ex) {
            System.out.println("Error occured in socket creation.");
        }
    }

    public static boolean isPrune(Connection conn, ArrayList<Connection> connections) {
        String ip = conn.getInetAddress();
        for(int i = 0; i < connections.size(); i++) {
            if(ip == connections.get(i).getInetAddress()) {
                return true;
            }
        }
        return false;
    }
}

class Connection extends Thread {
    Socket client;
    int uid;
    ArrayList<Connection> connections = new ArrayList<Connection>(20);
    PrintStream sout;
    String u_name = "";

    Connection(Socket new_client, int new_uid, ArrayList<Connection> new_conns) {
        client = new_client;
        uid = new_uid;
        connections = new_conns;
        try {
            sout = new PrintStream(client.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error occured in PrintStream creation.");
        }
    }
    
    public void run() {
        try {
            Scanner sin = new Scanner(client.getInputStream());
            String line = "";
            String message = "";
            boolean set_name = false;

            while(!line.equals("/quit")) { // User can quit by sending "/quit" on the server
                line = sin.nextLine();
                if(!set_name) { 
                    u_name = line;
                    set_name = true;
                    System.out.println("User "+uid+" set name to: "+line);
                }
                else {
                    message =  u_name+": "+line;
                    this.send(message);
                    for(int i = 0; i < connections.size(); i++) { // Sends messag eto all users
                        if(i != uid-1) { connections.get(i).send(message); }
                    }
                    System.out.println("User "+uid+" ("+u_name+")"+" said: "+line);
                }
            }
            System.out.println("User "+uid+" @ "+client.getInetAddress().toString()+" disconnected.");
            sin.close();
            client.close();
        } catch (IOException ex) {
            System.out.println("Error occured in scanner creation.");
        }
    }

    public void send(String message) { sout.println(message); }
    public String getInetAddress() { return client.getInetAddress().toString(); }
}