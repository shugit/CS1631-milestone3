import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.*;

public class Createcomp1
{
 static OutputStream outstream;
 static MsgEncoder mEncoder;
 static Socket universal;
 public static ArrayList <String> voters;
 public static ArrayList <String> candList;
 public static HashMap <String,Integer> tally;
 public static boolean initialized = false;
 public static String password;
 
 public static void main(String[] args) throws Exception
 {
   
  universal = new Socket("127.0.0.1", 7999);

  mEncoder = new MsgEncoder();
  final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
  
  KeyValueList msg23 = new KeyValueList();
  msg23.addPair("MsgID","23");
  msg23.addPair("Description", "Connect to SISServer");
  msg23.addPair("Name","Component");
  mEncoder.sendMsg(msg23, universal.getOutputStream());
  
  KeyValueList kvList;
  outstream = universal.getOutputStream();
  
  while(true)
  { 
   kvList = mDecoder.getMsg();
   ProcessMsg(kvList);
  } 
 }
 
 static void ProcessMsg(KeyValueList kvList) throws Exception
 {
  int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
  //System.out.println(MsgID);
  switch(MsgID)
  {
  /****************************************************
  Below are the main part of the component program. All received msgs are encoded as a KeyValueList kvList.
  kvList is a vector of <String key, String value> pairs. The 5 main methods of KeyValueList are 
   int size()                                    to get the size of KeyValueList
   String getValue(String key)                   to get value given key
   void addPair(String key, String value)        to add <Key, Value> pair to KeyValueList
   void setValue(String key, String value)       to set value to specific key
   String toString()                             System.out.print(KeyValueList) could work
  The following code can be used to new and send a KeyValueList msg to SISServer
   KeyValueList msg = new KeyValueList();
   msg.addPair("MsgID","23");
   msg.addPair("Description","Connect to SISServer");
   msg.addPair("Attribute","Value");
   ... ...
   mEncoder.sendMsg(msg, universal.getOutputStream()); //This line sends the msg
  NOTE: Always check whether all the attributes of a msg are in the KVList before sending it.
  Don't forget to send a msg after processing an incoming msg if necessary.
  All msgs must have the following 2 attributes: MsgID and Description.
  Below are the sending messages' attributes list:
   MsgName: msg711 MsgID: 711 Attrs: Status
   MsgName: msg712 MsgID: 712 Attrs: RankedReport
  For more information about KeyValueList, read comments in Util.java.
  ****************************************************/
  case 701:
    
    if(initialized){
    
   System.out.println("Message MsgName:msg701 MsgID:701 received, start processing.");
   /*************************************************
   Add code below to process Message MsgName:msg701 MsgID:701
   This message has following attributes: VoterPhoneNo; CandidateID, use KeyValueList.getValue(String key) to get the values.
   If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
   *************************************************/

/*    KeyValueList msg701 = new KeyValueList();
    msg701.addPair("MsgID","711");
    msg701.addPair("Description","Cast Vote");
    msg701.addPair("Attribute","VoterPhoneNo");
    msg701.addPair("Attribute","CandidateID");
    mEncoder.sendMsg(msg701, universal.getOutputStream());*/
    KeyValueList msg711 = new KeyValueList(); 
   msg711.addPair("MsgID","711"); 
   msg711.addPair("Description","Acknowledge Vote"); 
   
   String id = kvList.getValue("CandidateID");
   String phone = kvList.getValue("VoterPhoneNo");
   
 //  System.out.println(id);    //troubleshotting
  // System.out.println(phone); //troubleshooting
   
   if(tally.containsKey(id)){
     
    // System.out.println("valid id"); //troubleshooting
     
     if(!voters.contains(phone)){
    // System.out.println("new phone number"); //troubleshooting
       
       
     tally.put(id,tally.get(id)+1);
     //System.out.println(tally.get(id));  //troubleshooting
     voters.add(phone);
    msg711.addPair("Status","3");   
     }else{
      msg711.addPair("Status","1");  
     }
        }else{
         
          msg711.addPair("Status","2"); 
        }
   
   mEncoder.sendMsg(msg711, universal.getOutputStream());
  }else{
   
    System.out.println("Tally table must be initialized");
    
  }
   break;
  case 702:
   System.out.println("Message MsgName:msg702 MsgID:702 received, start processing.");
   /*************************************************
   Add code below to process Message MsgName:msg702 MsgID:702
   This message has following attributes: Passcode; N, use KeyValueList.getValue(String key) to get the values.
   If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
   *************************************************/
   String answer = "";
   
   String passcode = kvList.getValue("Passcode");
   int n = Integer.parseInt(kvList.getValue("N"));
  // if(passcode.equals(password)){
   
   ArrayList <String> aux = new ArrayList<String>();
   
   for(int x =0;x<candList.size();x++){
    
     aux.add(candList.get(x));
     
   }
   
   for(int y = 0;y<n;y++){
     int maxV = -1;
     String maxId = "";
     for(int x = 0;x<aux.size();x++){
   
       if(tally.get(aux.get(x))>maxV){
         maxV = tally.get(aux.get(x));
         maxId = aux.get(x);
       }
   
     
     
     }
     aux.remove(maxId);
     answer = answer + maxId + "," + maxV;
     
     if(y+1!=n){
       answer = answer + ";";
     }
	 else{
       
       boolean loop = true;
        while(loop){
		boolean t = false;
		for(int x = 0;x<aux.size();x++){

       if(tally.get(aux.get(x))==maxV){

         answer = answer + ";" + aux.get(x) + "," + maxV;
         aux.remove(x);
         t = true;
     }
       
       }
         if(!t){
          loop = false; 
         }
     }
     
   }
   

   }
   
 //  } //end password if
    KeyValueList msg712 = new KeyValueList();
    msg712.addPair("MsgID","712");
    msg712.addPair("Description","Acknowledge Request Report");
    msg712.addPair("RankedReport",answer);
    mEncoder.sendMsg(msg712, universal.getOutputStream());


   break;
  case 703:
   System.out.println("Message MsgName:msg703 MsgID:703 received, start processing.");
   /*************************************************
   Add code below to process Message MsgName:msg703 MsgID:703
   This message has following attributes: Passcode; CandidateList, use KeyValueList.getValue(String key) to get the values.
   If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
   *************************************************/

   voters = new ArrayList<String>();
   candList = new ArrayList<String>();
   initialized = true;
   tally = new HashMap<String,Integer>();
   
  
  // if(kvList.getValue("Passcode").equals(password){ 
   
    String m = kvList.getValue("CandidateList");
    StringTokenizer tok = new StringTokenizer(m,";");
   
   
    while(tok.hasMoreTokens()){  
   
      String z = tok.nextToken();
      candList.add(z);
   //   System.out.println(z);  //troubleshooting
      
      tally.put(z,0);
    
    } //initialize tally table
    
 //  } end if password if statement
   
   
    /*KeyValueList msg703 = new KeyValueList();
   
    msg703.addPair("Description","Initialize Tally Table");
    msg703.addPair("Attribute","Passcode");
    msg703.addPair("Attribute","CandidateList");
    mEncoder.sendMsg(msg703, universal.getOutputStream());*/


   break;
  /*************************************************
  Below are system messages. No modification required.
  *************************************************/
  case 26:
   System.out.println("Connect to SISServer successful.");
   break;
  case 22:
   System.exit(0);
   break;
  case 24:
   System.out.println("Algorithm Activated");
   break;
  case 25:
   System.out.println("Algorithm Deactivated");
   break;
  default:
   break;
  }
 }
}


