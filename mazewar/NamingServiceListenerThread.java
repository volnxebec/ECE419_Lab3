import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class NamingServiceListenerThread implements Runnable {

  private MSocketNoDrop mSocket =  null;
  private BlockingQueue eventQueue = null;
  private boolean nameRegistered = false;

  public NamingServiceListenerThread(MSocketNoDrop mSocket, BlockingQueue eventQueue) {
    this.mSocket = mSocket;
    this.eventQueue = eventQueue;
  }

  public void run() {
    MPacket received = null;
    MPacket ack = null;
    while(true){
      try {

        received = (MPacket) mSocket.readObject();
        if(Debug.debug) System.out.println("Received: " + received);
        if (!nameRegistered) {
          eventQueue.put(received);
        }
        ack = new MPacket(MPacket.NAMING, MPacket.NAMING_ACK);
        mSocket.writeObject(ack);

      } catch (InterruptedException e){
        e.printStackTrace();
      } catch (IOException e){
        e.printStackTrace();
      } catch (ClassNotFoundException e){
        e.printStackTrace(); 
      }
    }
  }

}
