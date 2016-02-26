import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class NamingServiceListenerThread implements Runnable {

  private MSocket mSocket =  null;
  private BlockingQueue eventQueue = null;

  public NamingServiceListenerThread(MSocket mSocket, BlockingQueue eventQueue) {
    this.mSocket = mSocket;
    this.eventQueue = eventQueue;
  }

  public void run() {
    MPacket received = null;
    while(true){
      try {

        received = (MPacket) mSocket.readObject();
        if(Debug.debug) System.out.println("Received: " + received);
        eventQueue.put(received);

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
