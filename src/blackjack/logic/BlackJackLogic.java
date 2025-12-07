package blackjack.logic;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
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
        ROUND_OVER,
        PLAYER_WON,
        PLAYER_LOSS,
        DRAW,
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

    public static int PlayerCapital = 1000;
    public static int PlayerBet = 0;

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
        PlayerBet = 0;
        // Getting deck ready
        buildDeck();
        shuffleDeck();

        // Set game state
        state = GameState.ROUND_START;
        System.out.println("ROUND STARTED");
        System.out.println("Player's Capital: " + PlayerCapital);
        System.out.println("Player's Current Bet " + PlayerBet);

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

    public void changeGameStatetoPlayerTurn() {
        state = GameState.PLAYER_TURN; 
    }

    public void changeGameStatetoDealerTurn() {
        state = GameState.DEALER_TURN;
    }

    public boolean checkBet() {
        if (PlayerBet == 0) {
            System.out.println("Must set a bet!");
            return false;
        }

        else {
            return true;
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
            revealHiddenCard(scene);
            finishDealer(scene);


        }
        }

    public void stand(Scene scene) {
        if (state != GameState.DEALER_TURN) return;

        revealHiddenCard(scene);

        boolean needsMore = dealerDrawOneCard(scene);

        if (!needsMore) {
            state = GameState.ROUND_OVER;
            decideWinner(scene);
        }
    }

    public boolean dealerDrawOneCard(Scene scene) {
        if (dealerSum >= 17) {
            return false; // No more cards to draw
        }

        Card card = deck.remove(deck.size() - 1);
        dealerHand.add(card);

        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        reduceDealerAce();

        EntityLoader.loadCard(card.getPath(), scene, EntityLoader.CardType.DEALER);

        state = GameState.PLAYER_TURN;
        return dealerSum < 17; 
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

    public static void betChips(String chipValue, Scene scene, float z) {
        if (z != 1.55f) return;
        if (state != GameState.ROUND_START) return;
        int correctedValue = Integer.parseInt(chipValue) / 10;
        int checkBetLimit = PlayerBet + correctedValue;

        if (checkBetLimit > PlayerCapital) {
            System.out.println("Not enough money ");
        }

        else {
            PlayerBet = PlayerBet + correctedValue;
            System.out.println("Player's Current Bet " + PlayerBet);
            EntityLoader.moveBetChips(scene, chipValue);

        }
        
    }

    public static void undoBetChips(String chipValue, Scene scene, float z) {
        if (z != 1.45f) return;
        
        if (state != GameState.ROUND_START) return;
        int correctedValue = Integer.parseInt(chipValue) / 10;
        int checkNotLessthan0 = PlayerBet - correctedValue;
        
        if (checkNotLessthan0 < 0) {
            PlayerBet = 0;
            System.out.println("Player's Current Bet " + PlayerBet);
        }

        else if(checkNotLessthan0 == 0) {
            PlayerBet = 0;
            System.out.println("Player's Current Bet " + PlayerBet);
            EntityLoader.removeBetChips(scene, chipValue);
        }

        else {
            PlayerBet = PlayerBet - correctedValue;
            System.out.println("Player's Current Bet " + PlayerBet);
            EntityLoader.removeBetChips(scene, chipValue);
        }
        
    }

    public void revealHiddenCard(Scene scene) {
        EntityLoader.replaceHiddedCard(hiddenCard.getPath(), scene);
        EntityLoader.removeHiddenCard(scene);
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
        decideWinner(scene);
    }

    public void decideWinner(Scene scene) {
        if (state != GameState.ROUND_OVER) return;

        System.out.println("PLAYER POINTS: " + playerSum);
        System.out.println("DEALER POINTS: " + dealerSum);
       // --- 1. Player busts ---
        if (playerSum > 21) {
            System.out.println("GAME RESULT: PLAYER LOST (BUST)");
            PlayerCapital -= PlayerBet;   
            PlayerBet = 0;   
            if(PlayerCapital == 0) {
                System.out.println("GAME OVER");
                clearCards(scene);
                scene.getCamera().setNormalView();
            }
            return;
        }

        // --- 2. Dealer busts ---
        if (dealerSum > 21) {
            System.out.println("GAME RESULT: PLAYER WINS (DEALER BUST)");
            PlayerCapital += PlayerBet;      
            PlayerBet = 0;
            return;
        }

        // --- 3. Both not bust compare values ---

        // Exact tie
        if (playerSum == dealerSum) {
            System.out.println("GAME RESULT: DRAW");
            PlayerBet = 0;
            return;
        }

        // Player wins with higher total
        if (playerSum > dealerSum) {
            System.out.println("GAME RESULT: PLAYER WINS");
            PlayerCapital += PlayerBet;  
            PlayerBet = 0;    
            return;
        }

        // Dealer wins with higher total
        if (dealerSum > playerSum) {
            System.out.println("GAME RESULT: DEALER WINS");
            PlayerCapital -= PlayerBet;
            PlayerBet = 0;     
            if(PlayerCapital == 0) {
                System.out.println("GAME OVER");
                clearCards(scene);
                scene.getCamera().setNormalView();
            }
            return;
        }
    }

    public void clearCards(Scene scene) {
        scene.clearCardEntities(EntityLoader.getCardModels());
        EntityLoader.clearChips(scene); 
                for (int i = 0; i < EntityLoader.CHIP_VALUES.length; i++) {
                    EntityLoader.loadChips(i, EntityLoader.CHIP_VALUES[i], scene);
                }
        EntityLoader.removeHiddenCard(scene);
        EntityLoader.resetOffsets();
    }
    
    public static void keyCallBack(Window window, Scene scene) {

        glfwSetKeyCallback(window.getWindowHandle(), (handle, key, scancode, action, mods) -> {

            window.keyCallBack(key, action);

            BlackJackLogic logic = BlackJackLogic.getInstance();

            // Start game
            if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
                logic.startGame(scene);
                scene.getCamera().setTopDownView(); 
            }

            // Hit
            if (key == GLFW_KEY_H && action == GLFW_RELEASE) {
                if (logic.checkBet())
                {
                    logic.changeGameStatetoPlayerTurn();
                    logic.hit(scene);
                }   
                
            }

            // Stand
            if (key == GLFW_KEY_J && action == GLFW_RELEASE) {
                if (logic.checkBet())
                {
                    logic.changeGameStatetoDealerTurn();
                    logic.stand(scene);
                }   
                
            }

            // End Game
            if (key == GLFW_KEY_Q && action == GLFW_RELEASE) {
                scene.getCamera().setNormalView();
                scene.clearCardEntities(EntityLoader.getCardModels());
                EntityLoader.removeHiddenCard(scene);
                EntityLoader.resetOffsets();
                EntityLoader.clearChips(scene); 
                for (int i = 0; i < EntityLoader.CHIP_VALUES.length; i++) {
                    EntityLoader.loadChips(i, EntityLoader.CHIP_VALUES[i], scene);
                }
            }
        });
    }
}
