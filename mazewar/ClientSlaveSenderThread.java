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

    while (true) {

      try {

        //Recieved the token... YEA!
        if (!tokenRecQueue.isEmpty()) {
          //Grab the token
          token = (MPacket)tokenRecQueue.take();  

          //Remove top my moves...
          boolean keepRemove = true;
          while (keepRemove) {
            keepRemove = token.removeMoveIfOwner(name);
          }

          BlockingQueue<MPacket> uploadEvent = new LinkedBlockingQueue<MPacket>(eventQueue);

          //Remove uploaded moves from eventQueue.... and add them to the token queue
          while (!uploadEvent.isEmpty()) {
            MPacket oldEvent = uploadEvent.take();
            MPacket tempEvent = (MPacket)eventQueue.peek();
            //Sanity check before removing
            if (oldEvent.type == tempEvent.type && 
                oldEvent.event == tempEvent.event) {
              eventQueue.take();
            }
            else {
              throw new InvalidObjectException("Removed Wrong Packet from EventQueue");
            }

            //Add to token...
            token.addPlayerMoves(oldEvent.event, name);
          }

          //Execute all moves in queue in order...
          for (PlayerMove validMove : token.clientMoves) {
            Client client = clientTable.get(validMove.get_owner());
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

          //Now pass token to next client
          tokenPasQueue.put(token);
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









