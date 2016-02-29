import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.InvalidObjectException;

public class ClientSlaveListenerThread implements Runnable {

  private MSocket mSocket =  null;
  private BlockingQueue tokenRecQueue = null;
  private boolean firstToken = false;
  private int tokenID = 0;

  public ClientSlaveListenerThread(MSocket mSocket, boolean firstToken,
                                   BlockingQueue tokenRecQueue) {
    this.mSocket = mSocket;
    this.tokenRecQueue = tokenRecQueue;
    this.firstToken = firstToken;
  }
  
  public void run() {
    MPacket received = null;
    MPacket ack = null;
    if(Debug.debug) System.out.println("Starting a Client Slave listener");
    while (true) {
      try {
        if (firstToken) {
          firstToken = false;
          received = new MPacket(MPacket.TOKEN, MPacket.TOKEN_PASS);
          received.sequenceNumber = 1;
          received.initPlayerMoves();
          tokenRecQueue.put(received);
          if(Debug.debug) System.out.println("Put First Token to tokenRecQueue");
        } else {

          received = (MPacket) mSocket.readObject();

          //Sanity check... I should only recieve token_pass in this listener
          if (received.type != MPacket.TOKEN) {
            throw new InvalidObjectException("Expecting TOKEN Packet");
          }
          if (received.event != MPacket.TOKEN_PASS) {
            throw new InvalidObjectException("Expecting TOKEN_PASS Packet");
          }

          if(Debug.debug) System.out.println("Received: " + received);

          //Check for tokenID number
          if (tokenID == received.sequenceNumber) {
            ack.sequenceNumber = received.sequenceNumber;
            //Recieved same token....
          }
          else if (tokenID < received.sequenceNumber) {
            //Set current tokenID
            tokenID = received.sequenceNumber;
            //Create the ack packet...
            ack = new MPacket(MPacket.TOKEN, MPacket.TOKEN_ACK);
            ack.sequenceNumber = received.sequenceNumber;
            //Increment the actual token number
            received.sequenceNumber++;
            tokenRecQueue.put(received);
            if(Debug.debug) System.out.println("Put Token to tokenRecQueue");
          }
          else {//This should never happen
            ack.sequenceNumber = received.sequenceNumber;
            System.out.println("recieved tokenID smaller than current tokenID");
            //System.exit(1);
          }

          if(Debug.debug) System.out.println
                    ("TokenID exp: "+tokenID+", act: "+received.sequenceNumber);


          //Send back an ack
          mSocket.writeObject(ack);
          if(Debug.debug) System.out.println("Sending back Token Ack");
          if(Debug.debug) System.out.println(ack);

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
