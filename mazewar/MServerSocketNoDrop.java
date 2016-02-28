import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class MServerSocketNoDrop{
    /*
    * This is the serverSocket equivalent to 
    * MSocketNoDrop
    */
 
    private ServerSocket serverSocket = null;
    
    /*
     *This creates a server socket
     */    
    public MServerSocketNoDrop(int port) throws IOException{
        serverSocket = new ServerSocket(port);
    }
    
    public MSocketNoDrop accept() throws IOException{
        Socket socket = serverSocket.accept(); 
        MSocketNoDrop mSocket = new MSocketNoDrop(socket);
        return mSocket;
    }

}
