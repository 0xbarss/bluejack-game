import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class Bluejack {
    private static final int TARGET_SET = 3;
    private static final int TARGET_SCORE = 20;
    private static final int GAME_DECK_SIZE = 40;

    private static final int PLAYER_WON_SET = 1;
    private static final int PLAYER_WON_GAME = 100;
    private static final int COMPUTER_WON_SET = -1;
    private static final int COMPUTER_WON_GAME = -100;
    private static final int CONTINUE = 2;
    private static final int TIE = 0;

    private static final int FILE_CAPACITY = 10;
    private static final String FILE_NAME = "game_history.txt";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = sc.next();
        Player player = new Player(name);
        Player computer = new Player("Computer");
        Deck gameDeck = new Deck(GAME_DECK_SIZE);

        int status = CONTINUE;
        
        gameDeck.createGameDeck();
        gameDeck.shuffleDeck();
        gameDeck.dealFiveCards(player, computer);
        player.createPlayerDeck();
        computer.createPlayerDeck();
        
        while (true) {
            status = checkGameStatus(player, computer, gameDeck);
            winConditions(status, player, computer);
            if (status == PLAYER_WON_GAME || status == COMPUTER_WON_GAME) break;
            player.play(sc, computer, gameDeck, true);

            status = checkGameStatus(player, computer, gameDeck);
            winConditions(status, player, computer);
            if (status == PLAYER_WON_GAME || status == COMPUTER_WON_GAME) break;
            computer.playAI(player.getBoard(), gameDeck, true);
        }
        sc.close();
        saveHistory(player, computer);
    }

    public static void reset(Player player, Player computer) {
        System.out.println();
        computer.clearBoard();
        player.clearBoard();
        player.setStand(false);
        computer.setStand(false);
    }

    public static int checkGameStatus(Player player, Player computer, Deck gameDeck) {
        int sumPlayersCards = player.sumCardsOnBoard();
        int sumComputersCards = computer.sumCardsOnBoard();
        // All cards are blue and the sum of them 20; End the game, win game
        if (sumPlayersCards == TARGET_SCORE && player.isHandBlue()) return PLAYER_WON_GAME;
        if (sumComputersCards == TARGET_SCORE && computer.isHandBlue()) return COMPUTER_WON_GAME;
        // The player has reached to target set; win game
        if (player.getScore() == TARGET_SET) return PLAYER_WON_GAME;
        if (computer.getScore() == TARGET_SET) return COMPUTER_WON_GAME;
        // The sum of the cards is 20; End the set, tour win  or  End the set, win game
        if (sumPlayersCards == TARGET_SCORE && player.getScore() == TARGET_SET-1) return PLAYER_WON_GAME;
        if (sumComputersCards == TARGET_SCORE && computer.getScore() == TARGET_SET-1) return COMPUTER_WON_GAME;
        // The sum of the cards is greater than 20; End the set, opponent win
        if (sumPlayersCards > TARGET_SCORE) return COMPUTER_WON_SET;
        if (sumComputersCards > TARGET_SCORE) return PLAYER_WON_SET;
        // The sum of the cards of players are equal; End the set, TIE
        if (sumPlayersCards == sumComputersCards && sumPlayersCards == TARGET_SCORE) return TIE;
        // The sum of the cards is less than 20 and closer to 20 than opponent's
        if (player.getStand() && computer.getStand()) {
            if (sumPlayersCards > sumComputersCards) return PLAYER_WON_SET;
            else if (sumComputersCards > sumPlayersCards) return COMPUTER_WON_SET;
            return TIE;
        }
        return CONTINUE;
    }

    public static void winConditions(int status, Player player, Player computer) {
        if (status == PLAYER_WON_SET) {
            player.setScore(player.getScore()+1);
            System.out.println(player.getName() + " won the set");
            reset(player, computer);
        }
        else if (status == COMPUTER_WON_SET) {
            computer.setScore(computer.getScore()+1);
            System.out.println(computer.getName() + " won the set");
            reset(player, computer);
        }
        else if (status == TIE) {
            player.setScore(player.getScore()+1);
            computer.setScore(computer.getScore()+1);
            System.out.println("TIE");
            reset(player, computer);
        }
        
        if (status == PLAYER_WON_GAME) {
            player.setScore(3);
            System.out.println(player.getName() + " won the game!!!");
        }
        else if (status == COMPUTER_WON_GAME) {
            computer.setScore(3);
            System.out.println(computer.getName() + " won the game!!!");    
        }
    } 

    public static String readFile(String path) {
        Scanner scanner;
        String lines = "";
        try {
            scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                lines += scanner.nextLine() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            //System.out.println("The file cannot be found");
        }
        return lines;
    }

    public static void saveHistory(Player player, Player computer) {
        FileWriter writer = null;
        String scores = readFile(FILE_NAME);
        try {
            writer = new FileWriter(FILE_NAME);
            scores += player.getInfo() + " - " + computer.getInfo() + ", " + java.time.LocalDate.now() + "\n";
            String[] scoresArray = scores.split("\n");
            int i = scoresArray.length > FILE_CAPACITY ? 1: 0;
            for (;i<scoresArray.length; i++) {
                writer.write(scoresArray[i] + "\n");
            }
            writer.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
