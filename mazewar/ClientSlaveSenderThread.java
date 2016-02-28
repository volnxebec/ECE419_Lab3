import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InvalidObjectException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Hashtable;

public class ClientSlaveSenderThread implements Runnable {

  private String name = "";
  private MSocket mSocket  =  null;
  private Hashtable<String, Client> clientTable = null;
  private BlockingQueue eventQueue = null;
  private BlockingQueue tokenRecQueue = null;
  private BlockingQueue tokenPasQueue = null;

  public ClientSlaveSenderThread(String name, MSocket mSocket, 
                                 Hashtable<String, Client> clientTable,
                                 BlockingQueue eventQueue,
                                 BlockingQueue tokenRecQueue,
                                 BlockingQueue tokenPasQueue) {
    this.name = name;
    this.mSocket = mSocket;
    this.clientTable = clientTable;
    this.eventQueue = eventQueue;
    this.tokenRecQueue = tokenRecQueue;
    this.tokenPasQueue = tokenPasQueue;
  }

  public void run() {

    MPacket ack = null;
    MPacket token = null;
    if(Debug.debug) System.out.println("Starting ClientSlaveSenderThread");
    while (true) {

      try {

        //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos1");

        //Recieved the token... YEA!
        if (!tokenRecQueue.isEmpty()) {
          //Grab the token
          token = (MPacket)tokenRecQueue.take();  

          if (token.type != MPacket.TOKEN) {
            throw new InvalidObjectException("Expecting TOKEN Packet");
          }
          if (token.event != MPacket.TOKEN_PASS) {
            throw new InvalidObjectException("Expecting TOKEN_PASS Packet");
          }

          //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos2");

          //Send back an Ack...
          ack = new MPacket(name, MPacket.TOKEN, MPacket.TOKEN_ACK);
          mSocket.writeObject(ack);

          //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos3");

          //Remove top my moves...
          boolean keepRemove = true;
          while (keepRemove) {
            keepRemove = token.removeMoveIfOwner(name);
          }

          //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos3");

          BlockingQueue<MPacket> uploadEvent = new LinkedBlockingQueue<MPacket>(eventQueue);

          //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos4");

          //Remove uploaded moves from eventQueue.... and add them to the token queue
          while (!uploadEvent.isEmpty()) {
            MPacket oldEvent = uploadEvent.take();
            MPacket tempEvent = (MPacket)eventQueue.peek();

            //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos5");

            //Sanity check before removing
            if (oldEvent.type == tempEvent.type && 
                oldEvent.event == tempEvent.event) {

              //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos6");

              eventQueue.take();
            }
            else {

              //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos7");

              throw new InvalidObjectException("Removed Wrong Packet from EventQueue");
            }

            //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos8");

            //Add to token...
            token.addPlayerMoves(oldEvent.event, name);
          }

          //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos9");

          //Execute all moves in queue in order...
          for (PlayerMove validMove : token.clientMoves) {
            //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos10");
            Client client = clientTable.get(validMove.get_owner());
            //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos11");
            if (validMove.get_move() == MPacket.UP) {
              client.forward();
            } else if (validMove.get_move() == MPacket.DOWN) {
              client.backup();
            } else if (validMove.get_move() == MPacket.LEFT) {
              client.turnLeft();
            } else if (validMove.get_move() == MPacket.RIGHT) {
              client.turnRight();
            } else if (validMove.get_move() == MPacket.FIRE) {
              client.fire();
            } else {
              throw new UnsupportedOperationException();
            }
          }

          //if(Debug.debug) System.out.println("ClientSlaveSenderThread pos12");

          //Now pass token to next client
          tokenPasQueue.put(token);
          if(Debug.debug) System.out.println("Put Token to tokenPasQueue");
        }

      } catch (InterruptedException e){
        System.out.println("Throwing Interrupt");
        Thread.currentThread().interrupt();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }

    }

  }

}









