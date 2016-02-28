import java.util.*;
import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

public class MPacket implements Serializable {

    /*The following are the type of events*/
    public static final int HELLO = 100;
    public static final int ACTION = 200;
    public static final int NAMING = 300;
    public static final int TOKEN = 400;

    /*The following are the specific action 
    for each type*/
    /*Initial Hello*/
    public static final int HELLO_INIT = 101;
    /*Response to Hello*/
    public static final int HELLO_RESP = 102;

    /*Action*/
    public static final int UP = 201;
    public static final int DOWN = 202;
    public static final int LEFT = 203;
    public static final int RIGHT = 204;
    public static final int FIRE = 205;
    public static final int PROJECTILE_MOVE = 206;

    /*Naming*/
    public static final int NAMING_REGISTER = 301;
    public static final int NAMING_REQUEST = 302;
    public static final int NAMING_REPLY = 303;
    
    /*Token*/
    public static final int TOKEN_PASS = 401;
    public static final int TOKEN_ACK = 402;

    //These fields characterize the event  
    public int type;
    public int event; 

    //The name determines the client that initiated the event
    public String name;
    
    //The sequence number of the event
    public int sequenceNumber;

    //Whose projectile is this?
    public int projectileMoveID;

    //These are used to initialize the board
    public int mazeSeed;
    public int mazeHeight;
    public int mazeWidth; 
    public Player[] players;

    //These are used to register client locations
    public String clientAddr;
    public int clientPort;
    public List<PlayerLoc> clientLocation;

    //These are used to current Queued up moves...
    public Queue<PlayerMove> clientMoves;

    public MPacket(int type, int event){
        this.type = type;
        this.event = event;
    }
    
    public MPacket(String name, int type, int event){
        this.name = name;
        this.type = type;
        this.event = event;
    }

//////////////////////
    public void initPlayerMoves() {
      if (this.clientMoves == null) {
        this.clientMoves = new LinkedList<PlayerMove>();
      }
    }

    public void addPlayerMoves(int move, String owner, int prjID) {
      PlayerMove newMove = new PlayerMove(move, owner, prjID);
      clientMoves.add(newMove);
    }

    public boolean removeMoveIfOwner(String owner) {
      boolean removed = false;
      if (clientMoves != null && !clientMoves.isEmpty()) {
        PlayerMove oldMove = clientMoves.peek();
        if (oldMove.get_owner().equals(owner)) {
          clientMoves.remove();
          removed = true;
        }
      }
      return removed;
    }
///////////////////////    
    public String toString(){
        String typeStr;
        String eventStr;
        
        switch(type){
            case 100:
                typeStr = "HELLO";
                break;
            case 200:
                typeStr = "ACTION";
                break;
            case 300:
                typeStr = "NAMING";
                break;
            case 400:
                typeStr = "TOKEN";
                break;
            default:
                typeStr = "ERROR";
                break;        
        }
        switch(event){
            case 101:
                eventStr = "HELLO_INIT";
                break;
            case 102:
                eventStr = "HELLO_RESP";
                break;
            case 201:
                eventStr = "UP";
                break;
            case 202:
                eventStr = "DOWN";
                break;
            case 203:
                eventStr = "LEFT";
                break;
            case 204:
                eventStr = "RIGHT";
                break;
            case 205:
                eventStr = "FIRE";
                break;
            case 206:
                eventStr = "PROJECTILE_MOVE";
                break;
            case 301:
                eventStr = "NAMING_REGISTER";
                break;
            case 302:
                eventStr = "NAMING_REQUEST";
                break;
            case 303:
                eventStr = "NAMING_REPLY";
                break;
            case 401:
                eventStr = "TOKEN_PASS";
                break;
            case 402:
                eventStr = "TOKEN_ACK";
                break;
            default:
                eventStr = "ERROR";
                break;        
        }
        //MPACKET(NAME: name, <typestr: eventStr>, SEQNUM: sequenceNumber)
        String retString = String.format("MPACKET(NAME: %s, <%s: %s>, SEQNUM: %s)", name, 
            typeStr, eventStr, sequenceNumber);
        return retString;
    }

}

