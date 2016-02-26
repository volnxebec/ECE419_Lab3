import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.LinkedList;

public class NamingService {

  private int clientTotal; //Total number of clients
  private int clientCount; //Current client count

  private List<PlayerLoc> playerNames; //Store location of players

  //private MServerSocket mServerSocket = null;
  //private MSocket[] mSocketList = null; //A list of MSockets
  //private BlockingQueue eventQueue = null; //A list of events
  
  public NamingService(int maxClient, int port) {
    this.clientTotal = maxClient;
    this.clientCount = 0;
    playerNames = new LinkedList<PlayerLoc>();
    //mServerSocket = new MServerSocket(port);
    //mSocketList = new MSocket[MAX_CLIENTS];
    //eventQueue = new LinkedBlockingQueue<MPacket>();
  }

  public void addClient(String host, int port, String name) {
    PlayerLoc clientLoc = new PlayerLoc(host, port, name);
    playerNames.add(clientLoc);
  }

  public List<PlayerLoc> getPlayerLocation() {
    return playerNames;
  }
}
