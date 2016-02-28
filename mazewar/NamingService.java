import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.LinkedList;

public class NamingService {

  private int clientTotal; //Total number of clients
  private int clientCount; //Current client count

  private List<PlayerLoc> playerNames; //Store location of players

  private MServerSocketNoDrop namingServerSocket = null;
  private MSocketNoDrop[] namingSocketList = null; //A list of MSocketNoDrops
  private BlockingQueue namingEventQueue = null; //A list of events

  //private MServerSocketNoDrop mServerSocket = null;
  //private MSocketNoDrop[] mSocketList = null; //A list of MSocketNoDrops
  //private BlockingQueue eventQueue = null; //A list of events
  
  public NamingService(int maxClient, int port) throws IOException{
    this.clientTotal = maxClient;
    this.clientCount = 0;
    playerNames = new LinkedList<PlayerLoc>();
    if(Debug.debug) System.out.println("Listening on port: " + port);
    namingServerSocket = new MServerSocketNoDrop(port);
    namingSocketList = new MSocketNoDrop[clientTotal];
    namingEventQueue = new LinkedBlockingQueue<MPacket>();
  }

  public void startThreads() throws IOException {
    while (clientCount < clientTotal) {
      MSocketNoDrop namingSocket = namingServerSocket.accept();
      new Thread(new NamingServiceListenerThread(namingSocket,
                                    namingEventQueue)).start();
      namingSocketList[clientCount] = namingSocket;
      clientCount++;
    }
    new Thread(new NamingServiceSenderThread(namingSocketList,
                                             namingEventQueue, this)).start();
  }

  public void addClient(String host, int port, String name, Point point, int direction) {
    PlayerLoc clientLoc = new PlayerLoc(host, port, name);
    clientLoc.init_player(point, direction);
    playerNames.add(clientLoc);
  }

  public List<PlayerLoc> getPlayerLocation() {
    return playerNames;
  }

  public static void main(String args[]) throws IOException {
    if(Debug.debug) System.out.println("Starting the naming service");
    int port = Integer.parseInt(args[0]);
    int totalPlayers = Integer.parseInt(args[1]); 
    NamingService nameServer = new NamingService(totalPlayers, port);
    nameServer.startThreads();
  }
}
