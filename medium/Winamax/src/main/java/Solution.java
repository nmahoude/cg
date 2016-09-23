import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {
    static class Card {
        int value;
        int color;
        
        Card(int value, int color) {
            this.value = value;
            this.color = color;
        }
        static Card fromString(String fullCard) {
            int value = 1;
            int color = 0;
            String colorCard = fullCard.substring(fullCard.length()-1);
            String card = fullCard.substring(0, fullCard.length()-1);
            switch(colorCard) {
                case "D": color = 1; break;
                case "H" : color = 2; break;
                case "C" : color = 3; break;
                case "S" : color = 4; break;
            }
            switch(card) {
                case "2": value = 2; break;
                case "3": value = 3; break;
                case "4": value = 4; break;
                case "5": value = 5; break;
                case "6": value = 6; break;
                case "7": value = 7; break;
                case "8": value = 8; break;
                case "9": value = 9; break;
                case "10": value = 10; break;
                case "J": value = 11; break;
                case "Q": value = 12; break;
                case "K": value = 13; break;
                case "A": value = 14; break;
            }
            return new Card(value, color);
        }
    }
    
    static Deque<Card> deckP1 = new ArrayDeque<>();
    static Deque<Card> deckP2 = new ArrayDeque<>();
    
    static class CardComparator implements Comparator<Card> {
        public int compare(Card c1, Card c2) {
            return -Integer.compare(c1.value, c2.value);
        }
    }
    
    static void reclaimPot(Deque<Card> deck, List<Card> pot1, List<Card> pot2) {
        //Collections.sort(pot1, new CardComparator());
        //Collections.sort(pot2, new CardComparator());
        for (Card card : pot1) {
            deck.addLast(card);
        }
        for (Card card : pot2) {
            deck.addLast(card);
        }
        pot1.clear();
        pot2.clear();
    }
    static void printCards(Deque<Card> deckP1, Deque<Card> deckP2) {
        System.err.println("Deck size : "+deckP1.size() + "/"+deckP2.size());

        for(Iterator<Card> itr = deckP1.iterator();itr.hasNext();)  {
            System.err.print(" "+itr.next().value);
        }
        System.err.println("");
        for(Iterator<Card> itr = deckP2.iterator();itr.hasNext();)  {
            System.err.print(" "+itr.next().value);
        }
        System.err.println("");
        
    }
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of cards for player 1
        for (int i = 0; i < n; i++) {
            String cardp1 = in.next(); // the n cards of player 1
            deckP1.addLast(Card.fromString(cardp1));
        }
        int m = in.nextInt(); // the number of cards for player 2
        for (int i = 0; i < m; i++) {
            String cardp2 = in.next(); // the m cards of player 2
            deckP2.addLast(Card.fromString(cardp2));
        }

        printCards(deckP1, deckP2);

        List<Card> pot1 = new ArrayList<>();
        List<Card> pot2 = new ArrayList<>();
        int rounds = 0;
        boolean pat = false;
        while (!deckP1.isEmpty() && !deckP2.isEmpty() && !pat) {
            //one turn
            rounds++;
            Card cardP1 = deckP1.removeFirst();
            Card cardP2 = deckP2.removeFirst();
            
            pot1.add(cardP1);
            pot2.add(cardP2);
            boolean winner = false;
            while (!winner && !pat) {
                if (cardP1.value > cardP2.value) {
                    boolean wasBataille = pot1.size() > 1;
                    reclaimPot(deckP1, pot1, pot2);
                    winner = true;
                    if (wasBataille){
                        printCards(deckP1, deckP2);
                        System.err.println("P1 win"+deckP1.size() + "/"+deckP2.size());
                    }
                } else if (cardP2.value > cardP1.value) {
                    boolean wasBataille = pot1.size() > 1;
                    reclaimPot(deckP2, pot1, pot2);
                    winner = true;
                    if (wasBataille){
                        printCards(deckP1, deckP2);
                        System.err.println("P2 win"+deckP1.size() + "/"+deckP2.size());
                    }
                } else {
                    // bataille
                    if (deckP1.size() < 4) {
                        pat = true;
                        break;
                    } else if (deckP2.size() < 4) {
                        pat = true;
                        break;
                    } else {
                        for (int i =0;i<3;i++) {
                            pot1.add(deckP1.removeFirst());
                            pot2.add(deckP2.removeFirst());
                        }
                        cardP1 = deckP1.removeFirst();
                        cardP2 = deckP2.removeFirst();
                        pot1.add(cardP1);
                        pot2.add(cardP2);
                        System.err.println("BATAILLE : "+deckP1.size() + "/"+deckP2.size()+" pot="+(pot1.size()+pot2.size()));
                    }
                }
            }
        }
        
        if (deckP1.isEmpty()) {
            System.out.println("2 "+rounds);
        } else if (deckP2.isEmpty()) {
            System.out.println("1 "+rounds);
        } else {
            System.out.println("PAT");
        }
    }
}