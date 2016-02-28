import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

public class ClientMasterSenderThread implements Runnable {
  
  private MSocket mSocket = null;
  private BlockingQueue<MPacket> eventQueue = null;

  public ClientMasterSenderThread(MSocket mSocket, 
                                  BlockingQueue eventQueue) {
    this.mSocket = mSocket;
    this.eventQueue = eventQueue;
  }

  public void run() {
    MPacket toSlave = null;
    if(Debug.debug) System.out.println("Starting ClientMasterSenderThread");
    while (true) {
      try {
        toSlave = (MPacket)eventQueue.take();
      } catch () {

      }
    }
  }
}
