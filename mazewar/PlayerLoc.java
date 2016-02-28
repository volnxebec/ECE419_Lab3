import java.io.Serializable;

class PlayerLoc implements Serializable {

  private boolean alive;
  private String nameID;
  private String clientHost;
  private int clientPort;
  private Player clientInit;

  public PlayerLoc(String host, int port, String name) {
    this.alive = true;
    this.nameID = name;
    this.clientHost = host;
    this.clientPort = port;
  }

  public void set_alive(boolean alive) {
    this.alive = alive;
  }

  public boolean get_alive() {
    return alive;
  }

  public String get_ID() {
    return nameID;
  }

  public String get_host() {
    return clientHost;
  }

  public int get_port() {
    return clientPort;
  }

  public void init_player(Point point, int direction) {
    clientInit = new Player(nameID, point, direction);
  }

  public Player get_player() {
    return clientInit;
  }

  public String toString() {
    return " HOST: " + clientHost + " PORT: " + clientPort + " ID: " + nameID;
  }

}

