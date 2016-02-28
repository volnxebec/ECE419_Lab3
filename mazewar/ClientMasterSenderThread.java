import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.io.InvalidObjectException;

public class ClientMasterSenderThread implements Runnable {
  
  private MSocket mSocket = null;
  private BlockingQueue tokenPasQueue = null;

  public ClientMasterSenderThread(MSocket mSocket, 
                                  BlockingQueue tokenPasQueue) {
    this.mSocket = mSocket;
    this.tokenPasQueue = tokenPasQueue;
  }

  public void run() {
    MPacket toSlave = null;
    MPacket ack = null;
    if(Debug.debug) System.out.println("Starting ClientMasterSenderThread");
    while (true) {
      try {
        
        toSlave = (MPacket)tokenPasQueue.take();
        //if(Debug.debug) System.out.println(toSlave);

        if (toSlave.type != MPacket.TOKEN) {
          throw new InvalidObjectException("Expecting TOKEN Packet");
        }
        if (toSlave.event != MPacket.TOKEN_PASS) {
          throw new InvalidObjectException("Expecting TOKEN_PASS Packet");
        }

        //Pass token to next client
        mSocket.writeObject(toSlave);
        
        //Wait for ack to come back
        ack = (MPacket)mSocket.readObject();
        if (ack.type != MPacket.TOKEN) {
          throw new InvalidObjectException("Expecting TOKEN Packet");
        }
        if (ack.event != MPacket.TOKEN_ACK) {
          throw new InvalidObjectException("Expecting TOKEN_ACK Packet");
        }
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
