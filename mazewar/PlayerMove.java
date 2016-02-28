import java.io.Serializable;

class PlayerMove implements Serializable {
  private int move;
  private String owner;
  private int prjID;

  public PlayerMove(int event, String name, int prjID) {
    this.move = event;
    this.owner = name;
    this.prjID = prjID;
  }

  public int get_move() {
    return move;
  }

  public String get_owner() {
    return owner;
  }

  public int get_prjID() {
    return prjID;
  }
}
