import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.InvalidObjectException;

public class ClientSlaveListenerThread implements Runnable {

  private MSocket mSocket =  null;
  private BlockingQueue tokenRecQueue = null;
  private boolean firstToken = false;

  public ClientSlaveListenerThread(MSocket mSocket, boolean firstToken,
                                   BlockingQueue tokenRecQueue) {
    this.mSocket = mSocket;
    this.tokenRecQueue = tokenRecQueue;
    this.firstToken = firstToken;
  }
  
  public void run() {
    MPacket received = null;
    if(Debug.debug) System.out.println("Starting a Client Slave listener");
    while (true) {
      try {
        if (firstToken) {
          firstToken = false;
          received = new MPacket(MPacket.TOKEN, MPacket.TOKEN_PASS);
          received.initPlayerMoves();
        } else {
          received = (MPacket) mSocket.readObject();
        }
        //Sanity check... I should only recieve token_pass in this listener
        if (received.type != MPacket.TOKEN) {
          throw new InvalidObjectException("Expecting TOKEN Packet");
        }
        if (received.event != MPacket.TOKEN_PASS) {
          throw new InvalidObjectException("Expecting TOKEN_PASS Packet");
        }
        if(Debug.debug) System.out.println("Received: " + received);
        tokenRecQueue.put(received);
        if(Debug.debug) System.out.println("Put Token to tokenRecQueue");
      }catch(InterruptedException e){
        e.printStackTrace();
      }catch(IOException e){
        e.printStackTrace();
      }catch(ClassNotFoundException e){
        e.printStackTrace();
      }
    }
  }

}
