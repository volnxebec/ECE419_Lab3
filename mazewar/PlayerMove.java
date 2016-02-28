import java.io.Serializable;

class PlayerMove implements Serializable {
  private int move;
  private String owner;

  public PlayerMove(int event, String name) {
    this.move = event;
    this.owner = name;
  }

  public int get_move() {
    return move;
  }

  public String get_owner() {
    return owner;
  }
}
