import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.io.InvalidObjectException;

public class ClientMasterSenderThread implements Runnable {
  
  private MSocket mSocket = null;
  private BlockingQueue tokenPasQueue = null;
  private int tokenID = 0;
  private boolean gotRightAck = false;

  public ClientMasterSenderThread(MSocket mSocket, 
                                  BlockingQueue tokenPasQueue) {
    this.mSocket = mSocket;
    this.tokenPasQueue = tokenPasQueue;
  }

  public void run() {
    MPacket toSlave = null;
    if(Debug.debug) System.out.println("Starting ClientMasterSenderThread");
    while (true) {
      try {
        gotRightAck = false;     
        toSlave = (MPacket)tokenPasQueue.take();
        tokenID = toSlave.sequenceNumber;
        //if(Debug.debug) System.out.println(toSlave);

        if (toSlave.type != MPacket.TOKEN) {
          throw new InvalidObjectException("Expecting TOKEN Packet");
        }
        if (toSlave.event != MPacket.TOKEN_PASS) {
          throw new InvalidObjectException("Expecting TOKEN_PASS Packet");
        }

        //Resend token if is lost
        int expBackOff = 1;
        while (true) {
          try {
            //Pass token to next client
            mSocket.writeObject(toSlave);
            if(Debug.debug) System.out.println("Sending Token Packet...");
            if(Debug.debug) System.out.println(toSlave);

            Thread thread = new Thread(new myRunnable(tokenID));

            //If don't receive ack in 1 second, resend token....
            long endTimeMillis = System.currentTimeMillis() + (expBackOff)*200;
            if(Debug.debug) System.out.println("Time end: "+endTimeMillis);
            boolean resend = false;
            thread.start();
            while (thread.isAlive()) {
              if (System.currentTimeMillis() > endTimeMillis) {
                if(Debug.debug) System.out.println("Time current: "+System.currentTimeMillis());
                if(Debug.debug) System.out.println("Token Packet dropped... need to resend");
                resend = true;
                try {
                  thread.interrupt();
                } catch (Exception e) {
                  if(Debug.debug) System.out.println("close last ack thread");
                }
                break;
              }
            }

            expBackOff++;

            if(Debug.debug) System.out.println("gotRightAck -> "+gotRightAck);

            if (resend) {
              if(Debug.debug) System.out.println("BILL_DBG 1 -> RESEND");
              continue;
            }
            else if (!gotRightAck) {
              if(Debug.debug) System.out.println("BILL_DBG 2 -> DID NOT GET RIGHT ACK");
              continue;
            }
            else {
              if(Debug.debug) System.out.println("BILL_DBG 3 -> GOOD");
              break;
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      }catch(InterruptedException e){
        e.printStackTrace();
      }catch(IOException e){
        e.printStackTrace();
      }

    }
  }

  class myRunnable implements Runnable {

    public int tokenID = 0;

    public myRunnable(int tokenID) {
      this.tokenID = tokenID;
    }

    public void run() {
      try {
        //Wait for ack to come back
        MPacket ack = (MPacket)mSocket.readObject();
        if (ack.type != MPacket.TOKEN) {
          throw new InvalidObjectException("Expecting TOKEN Packet");
        }
        if (ack.event != MPacket.TOKEN_ACK) {
          throw new InvalidObjectException("Expecting TOKEN_ACK Packet");
        }
        if (tokenID == ack.sequenceNumber) {
          gotRightAck = true;
          if(Debug.debug) System.out.println("got correct TOKEN_ACK");
          if(Debug.debug) System.out.println(ack);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

