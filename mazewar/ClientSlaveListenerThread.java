import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.InvalidObjectException;

public class ClientSlaveListenerThread implements Runnable {

  private MSocket mSocket =  null;
  private BlockingQueue tokenRecQueue = null;

  public ClientSlaveListenerThread(MSocket mSocket, BlockingQueue tokenRecQueue) {
    this.mSocket = mSocket;
    this.tokenRecQueue = tokenRecQueue;
  }
  
  public void run() {
    MPacket received = null;
    if(Debug.debug) System.out.println("Starting a Client Slave listener");
    while (true) {
      try {
        received = (MPacket) mSocket.readObject();
        //Sanity check... I should only recieve token_pass in this listener
        if (received.type != MPacket.TOKEN) {
          throw new InvalidObjectException("Expecting TOKEN Packet");
        }
        if (received.event != MPacket.TOKEN_PASS) {
          throw new InvalidObjectException("Expecting TOKEN_PASS Packet");
        }
        if(Debug.debug) System.out.println("Received: " + received);
        tokenRecQueue.put(received);
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
