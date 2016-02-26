import java.io.Serializable;


class PlayerLoc implements Serializable {

  private boolean alive;
  private String nameID;
  private String clientHost;
  private int clientPort;

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

  public String toString() {
    return " HOST: " + clientHost + " PORT: " + clientPort + " ID: " + nameID;
  }
}

