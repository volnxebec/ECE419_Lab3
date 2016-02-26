import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.LinkedList;

public class NamingService {

  private int clientTotal; //Total number of clients
  private int clientCount; //Current client count

  private List<PlayerLoc> playerNames; //Store location of players

  
  public NamingService(int maxClient) {
    this.clientTotal = maxClient;
    this.clientCount = 0;
    playerNames = new LinkedList<PlayerLoc>();
  }

  public void addClient(String host, int port, String name) {
    PlayerLoc clientLoc = new PlayerLoc(host, port, name);
    playerNames.add(clientLoc);
  }

}
