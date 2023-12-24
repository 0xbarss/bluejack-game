public class Card {
    private static final String PLUS = "+";
    private static final String MINUS = "-";

    private int number;
    private String color;
    private String sign;

    // Regular card
    public Card(int number, String color) {
        this.number = number;
        this.color = color;
        this.sign = PLUS;
    }

    // Random card
    public Card(int number, String color, String sign) {
        this.number = number;
        this.color = color;
        this.sign = sign;
    }

    // Special card
    public Card(String sign) {
        this.number = 0;
        this.color = "";
        this.sign = sign;
    }

    public int getNumber() {
        if (sign.equals(PLUS)) return number;
        if (sign.equals(MINUS)) return -number;
        return 0;
    }
    public String getColor() {return color;}
    public String getSign() {return sign;}
    public void setNumber(int num) {number = num;}
    public void setSign(String sgn) {sign = sgn;}

    public String toString() {
        if (number == 0) return sign;
        return sign+""+number+" "+color;
    }
}
