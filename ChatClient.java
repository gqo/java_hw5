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
            ArrayList<Connection> connections = new ArrayList<Connection>(20);
            Connection conn = null;
            while(true) {
                System.out.println("Waiting for a connection on port: "+port);
                Socket client = server.accept();
                System.out.println("User "+(++uid)+" @ "
                    +client.getInetAddress().toString()+" connected.");
                conn = new Connection(client,uid,connections);
                connections.add(conn);
                conn.start();
            }
        } catch (IOException ex) {
            System.out.println("Error occured in socket creation.");
        }
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
            // PrintStream sout = new PrintStream(client.getOutputStream());
            sout.println("Welcome! Please say your desired username. Type \"/quit\" to disconnect.");
            String line = "";
            String message = "";
            boolean set_name = false;
            while(!line.equals("/quit")) {
                line = sin.nextLine();
                if(!set_name) { 
                    u_name = line;
                    set_name = true;
                    System.out.println("User "+uid+" set name to: "+line);
                }
                else {
                    message =  u_name+": "+line;
                    this.send(message);
                    for(int i = 0; i < connections.size(); i++) {
                        if(i != uid-1) { connections.get(i).send(message); }
                    }
                    System.out.println("User "+uid+" ("+u_name+")"+" said: "+line);
                }
            }
            client.close();
            System.out.println("User "+uid+" @ "+client.getInetAddress().toString()+" disconnected.");
        } catch (IOException ex) {
            System.out.println("Error occured in scanner creation.");
        }
    }

    public void send(String message) { sout.println(message); }
}