package blackjack.logic;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.util.ArrayList;
import java.util.Random;

import blackjack.engine.scene.EntityLoader;
import blackjack.engine.scene.Scene;
import blackjack.engine.Window;

public class BlackJackLogic {
    private static BlackJackLogic instance = new BlackJackLogic();

    public static BlackJackLogic getInstance() {
        return instance;
    }

    public enum GameState {
        NONE,       // Not started
        ROUND_START,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_OVER
    }

    private static GameState state = GameState.NONE;

    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() { return type + "_" + value; }

        public int getValue() {
            if (value.equals("1")) return 11;
            else if (value.equals("11") || value.equals("12") || value.equals("13")) return 10;
            else return Integer.parseInt(value);
        }

        public boolean isAce() { return value.equals("1"); }

        public String getPath() {
            return toString();
        }
    }

    ArrayList<Card> deck;
    Random random = new Random();

    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;
    
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    private BlackJackLogic() {
        // startGame(scene);
    }

    // GAME LOGIC
    public void startGame(Scene scene) {
        System.out.println("========== NEW GAME STARTS ==========");
        clearCards(scene);
        // Getting deck ready
        buildDeck();
        shuffleDeck();

        // Set game state
        state = GameState.PLAYER_TURN;

        // Variables 
        String cardPath;

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;
        cardPath = hiddenCard.getPath();
        EntityLoader.loadCard(cardPath, scene, EntityLoader.CardType.HIDDEN);

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        cardPath = card.getPath();
        EntityLoader.loadCard(cardPath, scene, EntityLoader.CardType.DEALER);

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
            cardPath = card.getPath();
            EntityLoader.loadCard(cardPath, scene, EntityLoader.CardType.PLAYER);
        }
    }

    public void hit(Scene scene) {
        if (state != GameState.PLAYER_TURN) return;

        Card card = deck.remove(deck.size() - 1);
        playerHand.add(card);

        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        reducePlayerAce();

        EntityLoader.loadCard(card.getPath(), scene, EntityLoader.CardType.PLAYER);

        if (playerSum > 21) {
            state = GameState.ROUND_OVER;
            //System.out.println("Player busts!");
            revealHiddenCard(scene);
            finishDealer(scene);
        }
        }

    public void stand(Scene scene) {
        if (state != GameState.PLAYER_TURN) return;

        state = GameState.DEALER_TURN;

        revealHiddenCard(scene);
        finishDealer(scene);
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"1","2","3","4","5","6","7","8","9","10","11","12","13"};
        String[] types  = {"clubs","diamonds","hearts","spades"};

        for (String t : types) {
            for (String v : values) {
                deck.add(new Card(v, t));
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    public static void betChips(String chipValue) {
        if (state != GameState.PLAYER_TURN) return;
        System.out.println("Arrived " + chipValue);
    }

    public void revealHiddenCard(Scene scene) {
        EntityLoader.removeHiddedCard(hiddenCard.getPath(), scene);
    }

    public void finishDealer(Scene scene) {
        reduceDealerAce();
        while (dealerSum < 17) {
            Card card = deck.remove(deck.size() - 1);
            dealerHand.add(card);

            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            reduceDealerAce();

            EntityLoader.loadCard(card.getPath(), scene, EntityLoader.CardType.DEALER);
        } 
        state = GameState.ROUND_OVER;
        decideWinner();
    }

    public void decideWinner() {
        if (state != GameState.ROUND_OVER) return;

        System.out.println("PLAYER POINTS: " + playerSum);
        System.out.println("DEALER POINTS: " + dealerSum);
        // --- 1. Player busts ---
        if (playerSum > 21) {
            System.out.println("GAME RESULT: PLAYER LOST (BUST)");
            return;
        }

        // --- 2. Dealer busts ---
        if (dealerSum > 21) {
            System.out.println("GAME RESULT: PLAYER WINS (DEALER BUST)");
            return;
        }

        // --- 3. Both not bust → compare values ---

        // Exact tie
        if (playerSum == dealerSum) {
            System.out.println("GAME RESULT: DRAW");
            return;
        }

        // Player wins with higher total
        if (playerSum > dealerSum) {
            System.out.println("GAME RESULT: PLAYER WINS");
            return;
        }

        // Dealer wins with higher total
        if (dealerSum > playerSum) {
            System.out.println("GAME RESULT: DEALER WINS");
            return;
        }
    }


    public void clearCards(Scene scene) {
        scene.clearCardEntities(EntityLoader.getCardModels());
        EntityLoader.resetOffsets();
    }
    
    public static void keyCallBack(Window window, Scene scene) {

        glfwSetKeyCallback(window.getWindowHandle(), (handle, key, scancode, action, mods) -> {

            window.keyCallBack(key, action);

            BlackJackLogic logic = BlackJackLogic.getInstance();

            // Start game
            if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
                logic.startGame(scene);
            }

            // Hit
            if (key == GLFW_KEY_H && action == GLFW_RELEASE) {
                logic.hit(scene);
            }

            // Stand
            if (key == GLFW_KEY_J && action == GLFW_RELEASE) {
                logic.stand(scene);
            }
        });
    }
}
