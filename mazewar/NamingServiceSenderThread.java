import java.io.InvalidObjectException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.Random;

public class NamingServiceSenderThread implements Runnable {
  private MSocket[] mSocketList = null;
  private BlockingQueue eventQueue = null;
  private NamingService namingServer = null;

  public NamingServiceSenderThread(MSocket[] mSocketList, 
                  BlockingQueue eventQueue, NamingService naming){
    this.mSocketList = mSocketList;
    this.eventQueue = eventQueue;
    this.namingServer = naming;
  }                            

  public void handleNamingRegister() {

    int playerCount = mSocketList.length;
    MPacket namingRegister = null;

    Random randomGen = null;
    

    try {
      for (int i=0; i<playerCount; i++) {
        namingRegister = (MPacket) this.eventQueue.take();
        if (namingRegister.type != MPacket.NAMING) {
          throw new InvalidObjectException("Expecting NAMING Packet");
        }
        if (namingRegister.event != MPacket.NAMING_REGISTER) {
          throw new InvalidObjectException("Expecting NAMING_REGISTER Packet");
        }

        if (randomGen == null) {
          randomGen = new Random(namingRegister.mazeSeed);
        }
        Point point = new Point(randomGen.nextInt(namingRegister.mazeWidth),
                                randomGen.nextInt(namingRegister.mazeHeight));
        namingServer.addClient(namingRegister.clientAddr, 
                               namingRegister.clientPort, namingRegister.name,
                               point, Player.North);
      }
      //After all clients registered, broadcast location to everyone....
      namingRegister.event = MPacket.NAMING_REPLY;
      namingRegister.clientLocation = namingServer.getPlayerLocation();
      if(Debug.debug) System.out.println("Sending " + namingRegister);
      for(MSocket mSocket: mSocketList){
        mSocket.writeObject(namingRegister);
      }
    } catch (InterruptedException e){
      e.printStackTrace();
      Thread.currentThread().interrupt();
    } catch (IOException e){
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  public void run() {
    MPacket toBroadcast = null;

    handleNamingRegister(); 
  }
}
