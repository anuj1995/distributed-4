import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Insets;
import java.io.*; 
import java.util.*; 

public class NameServer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id;
	int serverPort;
	String serverIp;
	int predessorID;
	String predessorIp;
	int predessorPort;
	int successorID;
	String successorIp;
	int successorPort;
	String bootStrapIp;
	int bootStrapPort;
	String slash;
	String OS;
	ArrayList<Integer> trail = new ArrayList<>();
	HashMap<Integer,String> data = new HashMap<Integer,String>();
	
	public NameServer(String fileName) throws UnknownHostException, IOException{
		// TODO Auto-generated constructor stub
		
		OS = System.getProperty("os.name").toLowerCase();
		if(OS.indexOf("win") >= 0)
			slash ="\\";
		else if((OS.indexOf("mac") >= 0 || OS.indexOf("nux") >= 0))
			slash="/";
		File file = new File(System.getProperty("user.dir") +slash+  fileName);
		
		try {
			Scanner sc = new Scanner(file);
			this.id  = Integer.parseInt(sc.nextLine());
			this.serverPort = Integer.parseInt(sc.nextLine());
		    while (sc.hasNextLine()) {
				String[] line = sc.nextLine().split(" "); 
				bootStrapIp = line[0];
				bootStrapPort = Integer.parseInt(line[1]);
		    }
		    sc.close();
		    InetAddress inetAddress = InetAddress.getLocalHost();
		    this.serverIp = inetAddress.getHostAddress();
		    this.successorID = 0 ;
		    this.successorIp = null;
		    this.successorPort  = -1;
		    this.predessorID = 0;
		    this.predessorPort = -1;
		    this.predessorIp = null;
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	  	}
	public void Enter (String bootStrapIp, int bootStrapPort, NameServer ns ) throws ClassNotFoundException {
		try {
			Socket s = new Socket(bootStrapIp, bootStrapPort);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF("Name server has been connected with a id:" + this.id);
			dos.writeUTF("Enter");
			int situation  = dis.readInt();
			if(situation == 0) {
				//sending the request id to bootstrap server
				dos.writeInt(ns.id);
				dos.writeUTF(ns.serverIp); 
				dos.writeInt(ns.serverPort);
				//recieving the responses from bootstrap server
				ns.successorID = dis.readInt();
				ns.successorIp = dis.readUTF();
				ns.successorPort = dis.readInt();
				ns.predessorID = successorID;
				ns.predessorIp = successorIp;
				ns.predessorPort = successorPort;
				//revieveing the data from bootstrap server
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
    			ObjectInputStream is = new ObjectInputStream(s.getInputStream());
    			data = (HashMap<Integer,String>) is.readObject();
    			trail.add(predessorID);
    			System.out.println("SID: "+ ns.successorID);
    			System.out.println("PID: " + ns.predessorID);
    			s.close();
			}
			else {
				dos.writeInt(ns.id);
				dos.writeUTF(ns.serverIp); 
				dos.writeInt(ns.serverPort);
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
				os.writeObject(ns.trail);
				
			}
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		
	}
	
	public void Exit ( NameServer ns ) throws ClassNotFoundException {
		// Update sucessor.
		Socket s;
		try {
			s = new Socket(ns.successorIp, ns.successorPort);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Name server has forwarded a Exit request id: " + ns.id);
			dos.writeUTF("Update sucessor request");
			dos.writeInt(ns.predessorID);
	        dos.writeUTF(ns.predessorIp);
	        dos.writeInt(ns.predessorPort);
	        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	        os.writeObject(ns.data);
	        s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//update predecessor
		try {
			s = new Socket(ns.predessorIp, ns.predessorPort);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Name server has forwarded a Exit request id: " + ns.id);
			dos.writeUTF("Update predessor request");
			dos.writeInt(ns.successorID);
	        dos.writeUTF(ns.successorIp);
	        dos.writeInt(ns.successorPort);
	        s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void forwardDeleteRequest(int key, int successorID, String successorIp ,
			int successorPort, ArrayList<Integer> lookupTrail) {
		Socket s;
		try {
			s = new Socket(successorIp, successorPort);
			System.out.println("delete request has been forwarded to ID: " + successorID);
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Boostrap server has forwarded delete request");
	        dos.writeUTF("Forwarding delete request");
	        dos.writeInt(key);
	        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	        os.writeObject(lookupTrail);
	        s.close();
	        }catch (Exception e) {
				// TODO: handle exception
	        	e.printStackTrace();
			}
	}
	public void forwardLookupRequest(int key, int successorID, String successorIp ,
			int successorPort, ArrayList<Integer> lookupTrail) {
		Socket s;
		try {
			s = new Socket(successorIp, successorPort);
			System.out.println(" look up request has been forwarded to ID: " + successorID);
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Boostrap server has forwarded lookup request");
	        dos.writeUTF("Forwarding lookup request");
	        dos.writeInt(key);
	        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	        os.writeObject(lookupTrail);
	        s.close();
	        }catch (Exception e) {
				// TODO: handle exception
	        	e.printStackTrace();
			}
	}
	private void forwardEntryRequest(int requestID,String requestIp, int requestPort ,int successorID, String successorIp ,
			int successorPort, ArrayList<Integer> trail)
	{
		Socket s;
		try {
			s = new Socket(successorIp, successorPort);
			System.out.println("request has been forwarded to" + successorID);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Name server has forwarded a request id:" + requestID);
	        dos.writeUTF("Forwarding entry request");
	        dos.writeInt(requestID);
	        dos.writeUTF(requestIp);
	        dos.writeInt(requestPort);
	        trail.add(id);
	        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	        os.writeObject(trail);
	        s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void forwardInsertRequest(int key, String Value, int successorID, String successorIp ,
			int successorPort, ArrayList<Integer> lookupTrail) {
		Socket s;
		try {
			s = new Socket(successorIp, successorPort);
			System.out.println(" insert request has been forwarded to ID: " + successorID);
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        dos.writeUTF("Boostrap server has forwarded insert request");
	        dos.writeUTF("Forwarding insert request");
	        dos.writeInt(key);
	        dos.writeUTF(Value);
	        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	        os.writeObject(lookupTrail);
	        s.close();
	        }catch (Exception e) {
				// TODO: handle exception
	        	e.printStackTrace();
			}
	}
	
	public static void main(String Args[])throws IOException  {
		// server is listening on port 6000 
		NameServer ns = new NameServer("nsConfigFile.txt");
        ServerSocket ss = new ServerSocket(ns.serverPort); 
        
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null;
            try { 
	            // socket object to receive incoming client requests 
	            s = ss.accept(); 
	            // obtaining input and out streams 
	            DataInputStream dis = new DataInputStream(s.getInputStream()); 
	            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	            String recieved = dis.readUTF();
	            System.out.println(recieved);
	            if(recieved.contains("client")) {
		            // create a new thread object 
	            	System.out.println("Assigning new thread for this client");
	            	 // Invoking the start() method 
		            Thread c = new NameServerRequestHandler(s, dis, dos, ns);
		            c.start();
	            }
	            else {
	            	String message = dis.readUTF();
	            	System.out.println(message);
	            	switch(message)
	            	{
	            	case"Forwarding entry request":
	            		
	            		int requestID = dis.readInt();
	            		String requestIp = dis.readUTF();
	            		int requestPort = dis.readInt();
	            		ObjectInputStream is = new ObjectInputStream(s.getInputStream());
	            		ArrayList<Integer> t =  (ArrayList<Integer>) is.readObject();
	            		if((requestID > ns.predessorID && requestID < ns.id) ){
	            			//successor found
	            			//update keys
	            			int newPredessorID = ns.predessorID;
	            			String newPredessorIp = ns.predessorIp;
	            			int newPredessorPort = ns.predessorPort;
	            			t.add(ns.id);
	            			HashMap<Integer,String> subMap = new HashMap<Integer,String>();
	            			ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
	            			for(Map.Entry<Integer, String> entry : ns.data.entrySet())
	            			{
	            				int currentID = entry.getKey();
	            				String currentValue = entry.getValue();
	            				if(currentID > ns.predessorID && currentID <= requestID)
	            				{
	            					subMap.put(currentID,currentValue);
	            				}
	            			}
	            			for(Map.Entry<Integer, String> entry : subMap.entrySet())
	            			{
	            				int currentID = entry.getKey();
	            				String currentValue = entry.getValue();
	            				if(ns.data.containsKey(currentID))
	            				{
	            					ns.data.remove(currentID,currentValue);
	            				}
	            			}
	            			// update sucessor and predessor
	            			ns.predessorID = requestID;
	            			ns.predessorIp = requestIp;
	            			ns.predessorPort = requestPort;
	            			System.out.println("SID: "+ns.successorID);
	            			System.out.println("PID: " + ns.predessorID);
	            			// sending data to the request server 
	            			Socket a = new Socket(requestIp, requestPort);
	            			dis = new DataInputStream(a.getInputStream()); 
	            	        dos = new DataOutputStream(a.getOutputStream());
	            	        dos.writeUTF("Name server has arrived a response for id:" + requestID);
	            	        dos.writeUTF("Accepting Forwarding Request");
	            	        dos.writeInt(ns.id);
	        				dos.writeUTF(ns.serverIp); 
	        				dos.writeInt(ns.serverPort);
	        				dos.writeInt(newPredessorID);
	        				dos.writeUTF(newPredessorIp); 
	        				dos.writeInt(newPredessorPort);
	        				os = new ObjectOutputStream(a.getOutputStream());
	            	        os.writeObject(subMap);
	            	        os.writeObject(t);
	            			a.close();
	            		}
	            		else{
		            		ns.forwardEntryRequest(requestID, requestIp, requestPort, ns.successorID, ns.successorIp, ns.successorPort, t);
		            		if((requestID >ns.id && requestID < ns.successorID) || (requestID > ns.id && ns.successorID == 0) ){
		            			ns.successorID = requestID;
		            			ns.successorIp = requestIp;
		            			ns.successorPort = requestPort;
		            			System.out.println("SID: "+ns.successorID);
		            			System.out.println("PID: " + ns.predessorID);
		            		}
	            		}
	            		break;
	            	case "Accepting Forwarding Request":
	            		ns.successorID = dis.readInt();
	            		ns.successorIp = dis.readUTF();
	            		ns.successorPort = dis.readInt();
	            		ns.predessorID = dis.readInt();
	            		ns.predessorIp = dis.readUTF();
	            		ns.predessorPort = dis.readInt();
	            		ObjectInputStream is1 = new ObjectInputStream(s.getInputStream());
	            		ns.data =  (HashMap<Integer, String>) is1.readObject();
	            		ns.trail = (ArrayList<Integer>) is1.readObject();
	            		break;
	            	
	            	case "Update predessor request":
	            		dis = new DataInputStream(s.getInputStream());
	            		ns.successorID = dis.readInt();
	            		ns.successorIp = dis.readUTF();
	            		ns.successorPort = dis.readInt();
	            		break;
	            		
	            	case "Update sucessor request":
	            		dis = new DataInputStream(s.getInputStream());
	            		ns.predessorID = dis.readInt();
	            		ns.predessorIp = dis.readUTF();
	            		ns.predessorPort = dis.readInt();
	            		is = new ObjectInputStream(s.getInputStream());
	            		HashMap<Integer, String> temp =  (HashMap<Integer, String>) is.readObject();
	            		for(Map.Entry<Integer, String> entry : temp.entrySet())
	            		{
	            			ns.data.put(entry.getKey(), entry.getValue());
	            		}
	            		break;
	            	case "Forwarding lookup request":
	            		dis = new DataInputStream(s.getInputStream());
	            		int lookupKey = dis.readInt();
	            		is = new ObjectInputStream(s.getInputStream());
	            		ArrayList<Integer> lookupTrail = (ArrayList<Integer>) is.readObject();
	            		lookupTrail.add(ns.id);
	            		// check if the key belongs here
	            		if( lookupKey <=  ns.id && lookupKey > ns.predessorID ) {
	            			String response = ns.data.get(lookupKey);
	            			// update the response to bootstarp server
	            			Socket s1 = new Socket(ns.bootStrapIp, ns.bootStrapPort); 
	                        dos = new DataOutputStream(s1.getOutputStream());
	            			dos.writeUTF("Name server has forwarded a lookup update");
	            			dos.writeUTF("Update lookup response");
	            			if(response == null) {
	            				response = "Key not found";
	            			}
	            			dos.writeUTF(response);
	            			ObjectOutputStream os = new ObjectOutputStream(s1.getOutputStream());
	            			os.writeObject(lookupTrail);
	            			s1.close();
	            		}
	            		else {
	            			ns.forwardLookupRequest(lookupKey, ns.successorID, ns.successorIp, ns.successorPort, lookupTrail);
	            		}
	            		break;
	            	case"Forwarding insert request":
	            		dis = new DataInputStream(s.getInputStream());
	            		int insertKey = dis.readInt();
	            		String value = dis.readUTF(); 
	            		is = new ObjectInputStream(s.getInputStream());
	            		ArrayList<Integer> insertTrail = (ArrayList<Integer>) is.readObject();
	            		insertTrail.add(ns.id);
	            		// check if the key is to tobe inseted here
	            		if(insertKey > ns.predessorID && insertKey <= ns.id) {
	            			ns.data.put(insertKey, value);
	            			Socket s1 = new Socket(ns.bootStrapIp, ns.bootStrapPort); 
	                        dos = new DataOutputStream(s1.getOutputStream());
	            			dos.writeUTF("Name server has forwarded a insert update");
	            			dos.writeUTF("Update insert response");
	            			ObjectOutputStream os = new ObjectOutputStream(s1.getOutputStream());
	            			os.writeObject(insertTrail);
	            			s1.close();
	            		}
	            		else {
	            			ns.forwardInsertRequest(insertKey, value, ns.successorID, ns.successorIp, ns.successorPort, insertTrail);
	            		}
	            		break;
	            	case"Forwarding delete request":
	            		dis = new DataInputStream(s.getInputStream());
	            		int deleteKey = dis.readInt();
	            		is = new ObjectInputStream(s.getInputStream());
	            		ArrayList<Integer> deleteTrail = (ArrayList<Integer>) is.readObject();
	            		deleteTrail.add(ns.id);
	            		// check if the key is to to be deleted here
	            		if(deleteKey > ns.predessorID && deleteKey <= ns.id) {
	            			ns.data.remove(deleteKey);
	            			Socket s1 = new Socket(ns.bootStrapIp, ns.bootStrapPort); 
	                        dos = new DataOutputStream(s1.getOutputStream());
	            			dos.writeUTF("Name server has forwarded a lookup update");
	            			dos.writeUTF("Update delete response");
	            			ObjectOutputStream os = new ObjectOutputStream(s1.getOutputStream());
	            			os.writeObject(deleteTrail);
	            			s1.close();
	            		}
	            		else{
	            			ns.forwardDeleteRequest(deleteKey, ns.successorID, ns.successorIp, ns.successorPort, deleteTrail);
	            		}
	            		break;
	            	}
	            }
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
	}
}