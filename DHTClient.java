import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry; 
  
// Client class 
public class DHTClient  
{ 
    public static void main(String[] args) throws IOException  
    { 
        try
        { 
            Scanner scn = new Scanner(System.in); 
              
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost"); 
      
            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 7432); 													
      
            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
            
            dos.writeUTF("client has made a connection with the server");
            // the following loop performs the exchange of 
            // information between client and client handler 
            while (true)  
            { 	
            	// obtaining input and out streams 
                dis = new DataInputStream(s.getInputStream()); 
                dos = new DataOutputStream(s.getOutputStream()); 
                System.out.println("DHT>"); 
                String tosend = scn.nextLine(); 
                dos.writeUTF(tosend); 
                  
                // If client sends exit,close this connection  
                // and then break from the while loop 
                if(tosend.equals("Exit")) 
                { 
                    System.out.println("Closing this connection : " + s); 
                    s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                }
                else if(tosend.equals("enter"))
                {
                	System.out.println(dis.readUTF());
                	System.out.println(dis.readUTF());
                	System.out.println(dis.readUTF());
                	ObjectInputStream is = new ObjectInputStream(s.getInputStream());
                	System.out.println("Server contacted are:");
                	@SuppressWarnings("unchecked")
					ArrayList<Integer> trail =  (ArrayList<Integer>) is.readObject();
                	for(int i = 0; i < trail.size(); i++) {   
                	    System.out.println(trail.get(i));
                	}
                }
                 
            } 
              
            // closing resources 
            scn.close(); 
            dis.close(); 
            dos.close(); 
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 
} 