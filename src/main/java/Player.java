public class Player {
    private char color;
    private int number;

    public Player(char color, int number) {
        this.color = color;
        this.number = number;
    }

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
