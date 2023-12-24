import java.util.Random;
import java.util.Scanner;

public class Player {
    private static final String BLUE = "BLUE";
    private static final String YELLOW = "YELLOW";
    private static final String RED = "RED";
    private static final String GREEN = "GREEN";
    private static final String[] COLORS = new String[]{BLUE, YELLOW, RED, GREEN};
    
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String FLIP = "+/-";
    private static final String DOUBLE = "x2";

    private static final int MAX_PLAYER_CARDS = 10;
    private static final int MAX_BOARD_CARDS = 9;
    private static final int MAX_HAND_CARDS = 4;

    private static final int MAX_RAND_CARDS = 3;
    private static final int MAX_SPECIAL_CARDS = 2;
    private static final int RAND_CARDS_VALUE_RANGE = 6;

    private static final int MIN_SUM_FOR_STAND = 14; // AI
    private static final int MIN_SUM_FOR_STAND_2 = 18; // AI
    private static final int MAX_SUM_FOR_STAND = 20; // AI

    private String name;
    private Deck playerDeck;
    private Deck hand;
    private Deck board;
    private int score;
    private Boolean stand;

    public Player(String nm) {
        name = nm;
        playerDeck = new Deck(MAX_PLAYER_CARDS);
        hand = new Deck(MAX_HAND_CARDS);
        board = new Deck(MAX_BOARD_CARDS);
        score = 0;
        stand = false;
    }

    public String getName() {return name;}
    public Deck getPlayerDeck() {return playerDeck;}
    public Deck getHand() {return hand;}
    public Deck getBoard() {return board;}
    public int getScore() {return score;}
    public Boolean getStand() {return stand;}
    public String getInfo() {return getName() + ": " + getScore();}
    public void setScore(int scr) {score = scr;}
    public void setStand(Boolean std) {stand = std;}

    public void displayGame(Player computer) {
        System.out.println();
        computer.getBoard().printDeck(computer.getName() + "'s Board");
        board.printDeck(name + "'s Board");
        hand.printDeck(name + "'s Hand");
        System.out.println();
    }

    public void addCardToDeck(Card card) {
        int size = playerDeck.getSize();
        Card[] deck = playerDeck.getDeck();
        deck[size] = card;
        playerDeck.setSize(size+1);
    }

    public void addCardToHand(Card card) {
        int size = hand.getSize();
        Card[] handDeck = hand.getDeck();
        handDeck[size] = card;
        hand.setSize(size+1);
    }

    public void addCardToBoard(Card card) {
        int size = board.getSize();
        Card[] boardDeck = board.getDeck();
        boardDeck[size] = card;
        board.setSize(size+1);
    }

    public void createPlayerDeck() {
        // Create Random Cards
        int number;
        String color;
        String sign;
        Random rand = new Random();
        for (int i=0; i<MAX_RAND_CARDS; i++) {
            number = rand.nextInt(RAND_CARDS_VALUE_RANGE)+1;
            color = COLORS[rand.nextInt(COLORS.length)];
            sign = rand.nextDouble() < 0.5 ? PLUS: MINUS;
            addCardToDeck(new Card(number, color, sign));
        }
        // Create Special Cards
        for (int i=0; i<MAX_SPECIAL_CARDS; i++) {
            if (rand.nextDouble() < 0.8) {
                number = rand.nextInt(RAND_CARDS_VALUE_RANGE)+1;
                color = COLORS[rand.nextInt(COLORS.length)];
                sign = rand.nextDouble() < 0.5 ? PLUS: MINUS;
                addCardToDeck(new Card(number, color, sign));
            }
            else {
                sign = rand.nextDouble() < 0.5 ? FLIP: DOUBLE;
                addCardToDeck(new Card(sign));
            }
        }
        // Add Cards to Hand
        int index;
        for (int i=0; i<MAX_HAND_CARDS; i++) {
            index = rand.nextInt(playerDeck.getSize());
            addCardToHand(playerDeck.drawCard(index));
        }
    }

    public Boolean isHandBlue() {
        Boolean condition = true;
        for (Card card: board.getDeck()) {
            if (!card.getColor().equals(BLUE)) {
                condition = false;
                break;
            }
        }
        return condition;
    }

    public void clearBoard() {
        board = new Deck(MAX_BOARD_CARDS);
    }

    public int sumCardsOnBoard() {
        int sum = 0;
        int number;
        if (board.getSize() > 0) {
            for (Card card: board.getDeck()) {
                if (card == null) continue;
                number = card.getNumber();
                if (number == 0) continue;
                sum += number;
            } 
        }
        return sum;
    }

    public Card applySpecialSign(Card card, Card specialCard) {
        Card newCard = new Card(card.getNumber(), card.getColor(), card.getSign());
        if (specialCard.getSign().equals(FLIP)) {
            newCard.setSign(newCard.getSign().equals(PLUS) ? MINUS: PLUS);
            return newCard;
        }
        else if (specialCard.getSign().equals(DOUBLE)) {
            newCard.setNumber(newCard.getNumber()*2);
            return newCard;
        }
        return newCard;
    }

    public int getInput(Scanner sc) {
        int number = 0;
        while (true) {
            try {
                System.out.print("Enter a number: ");
                number = Integer.parseInt(sc.next());
                System.out.println();
                break;
            } 
            catch (Exception e) {
                System.out.println("Wrong input, try again!");
            }
        }
        return number;
    }

    public Deck play(Scanner sc, Player computer, Deck gameDeck, Boolean letDrawCard) {
        displayGame(computer);
        if (stand) return gameDeck;
        System.out.println("1-Draw a Card\n2-End Tour\n3-Play a Card from Hand\n4-Stand\n");
        int choice = getInput(sc);
        while (letDrawCard ? (choice<1 || choice>4) && choice != 2: (choice<2 || choice>4)) {
            choice = getInput(sc);
        }
        switch (choice) {
            case 1:
                if (letDrawCard) {
                    Card drawnCard = gameDeck.drawTopCard();
                    addCardToBoard(drawnCard);
                    play(sc, computer, gameDeck, false);
                }
                break;
            case 2:
                break;
            case 3:
                choice = getInput(sc);
                while (choice<1 || choice>hand.getSize()) {    
                    choice = getInput(sc);
                }
                Card chosenCard = hand.drawCard(choice-1);
                // Check the card if it is a special card
                if (chosenCard.getNumber() == 0) {
                    Card lastCardOnBoard = board.getDeck()[board.getSize()-1];
                    board.getDeck()[board.getSize()-1] = applySpecialSign(lastCardOnBoard, chosenCard);
                    addCardToBoard(chosenCard);
                }
                else {
                    addCardToBoard(chosenCard);
                }
                break;
            case 4:
                System.out.println(name + " will stand");
                stand = true;
                break;
            }
            return gameDeck;
        }
        
    public Deck playAI(Deck playerBoard, Deck gameDeck, Boolean letDrawCard) {
        if (stand) return gameDeck;
        int sumCards = sumCardsOnBoard();
        if (board.getSize() > 0) {
            Card lastCardOnBoard = board.getDeck()[board.getSize()-1];
            if (sumCards >= MIN_SUM_FOR_STAND) {
                if (sumCards >= MIN_SUM_FOR_STAND_2 && sumCards <= MAX_SUM_FOR_STAND) {
                    stand = true;
                    System.out.println(name + " will stand");
                    return gameDeck;
                }
                Card card;
                int cardWillBePlayed = -1;
                int maxSum = sumCards;
                Card[] handDeck = hand.getDeck();
                for (int i=0; i<handDeck.length; i++) {
                    card = handDeck[i];
                    if (card.getNumber() == 0) {
                        int sumCardsExceptLastCard = sumCards-lastCardOnBoard.getNumber();
                        Card newCard = applySpecialSign(lastCardOnBoard, card);
                        int newSum = sumCardsExceptLastCard+newCard.getNumber();
                        if (newSum > maxSum && newSum <= MAX_SUM_FOR_STAND) {
                            cardWillBePlayed = i;
                            maxSum = newSum;
                        }
                    }
                    else if (sumCards+card.getNumber() > maxSum && sumCards+card.getNumber() <= MAX_SUM_FOR_STAND) {
                        cardWillBePlayed = i;
                        maxSum = sumCards+card.getNumber();
                    }
                }
                if (cardWillBePlayed != -1) {
                    Card chosenCard = hand.drawCard(cardWillBePlayed);
                    System.out.println(name + " played a card from hand!");
                    addCardToBoard(chosenCard);
                    return gameDeck;
                }
            }
            else {
                if (letDrawCard) {
                    System.out.println(name + " drew a card");
                    Card drawnCard = gameDeck.drawTopCard();
                    addCardToBoard(drawnCard);
                    return playAI(playerBoard, gameDeck, false);
                }
            }
        }
        if (letDrawCard) {
            System.out.println(name + " drew a card");
            Card drawnCard = gameDeck.drawTopCard();
            addCardToBoard(drawnCard);
            return playAI(playerBoard, gameDeck, false);
        }
        return gameDeck;
    }
}