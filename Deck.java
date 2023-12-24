import java.util.Random;

public class Deck {
    private static final String BLUE = "BLUE";
    private static final String YELLOW = "YELLOW";
    private static final String RED = "RED";
    private static final String GREEN = "GREEN";
    private static final String[] COLORS = new String[]{BLUE, YELLOW, RED, GREEN};

    private Card[] deck;
    private int size;

    public Deck(int numOfCards) {
        deck = new Card[numOfCards];
        size = 0;
    }

    public Card[] getDeck() {return deck;}
    public int getSize() {return size;}
    public void setSize(int sz) {size = sz;}
    
    public void createGameDeck() {
        // Regular Cards
        int number;
        String color;
        int numOfCardsInEachDeck = (deck.length/COLORS.length);
        for (int i=0; i<deck.length; i++) {
            number = i%numOfCardsInEachDeck+1;
            color = COLORS[i/numOfCardsInEachDeck];
            deck[i] = new Card(number, color);
        }
        size = deck.length;
    }

    public void shuffleDeck() {
        // Fisher-Yates algorithm
        int index;
        Card tempCard;
        Random rand = new Random();
        for (int i=size-1; i>0; i--) {
            index = rand.nextInt(i+1);
            tempCard = deck[i];
            deck[i] = deck[index];
            deck[index] = tempCard;
        }
    }

    public void dealFiveCards(Player player, Player computer) {
        for (int i=0; i<5; i++) {
            player.addCardToDeck(drawBottomCard());
            computer.addCardToDeck(drawTopCard());
        }
    }

    public Card drawCard(int index) {
        Card card = deck[index];
        Card[] newDeck = new Card[size-1];
        System.arraycopy(deck, 0, newDeck, 0, index);
        System.arraycopy(deck, index+1, newDeck, index, size-1-index);
        deck = newDeck;
        size--;
        return card;
    }

    public Card drawTopCard() {
        return drawCard(size-1);
    }
    
    public Card drawBottomCard() {
        return drawCard(0);
    }

    public void printDeck(String name) {
        String txt = name+": ";
        for (Card card: deck) {
            if (card == null) continue;
            txt += card.toString() + " | ";
        }
        if (size == 0) System.out.println(txt + "Empty");
        else System.out.println(txt);
    }
}
