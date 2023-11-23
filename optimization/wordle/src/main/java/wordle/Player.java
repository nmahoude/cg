package wordle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Player {
  static Random random = new Random(System.currentTimeMillis());
  
  static boolean letters[] = new boolean[26];

  static boolean lettersCannotAppears[] = new boolean[26];
  static boolean lettersCanBe[][] = new boolean[26][5];

  static List<String> words = new ArrayList<>();

  public static void main(String args[]) {
      Scanner in = new Scanner(System.in);
      int wordCount = in.nextInt(); // Number of words in the word set
      System.err.println("Wordcount : "+wordCount);
      for (int i = 0; i < wordCount; i++) {
          String word = in.next(); // Word in the word set
          words.add(word);
      }

      Set<String> possibleLetters = new HashSet<>();
      
      String firstWord = "AEIOUN";
      String previousWord = null;
      // game loop
      int turn = 0;
      while (true) {
          for (int i = 0; i < 6; i++) {
              int state = in.nextInt(); // State of the letter of the corresponding position of previous guess
              if (previousWord == null) continue;

              char letter = previousWord.charAt(i);
              int letterPos = letter - 'A';
              if (state == 1) {
                  words.removeIf(w -> w.contains(""+letter));
              } 
              if (state == 2) {
                  words.removeIf(w -> !w.contains(""+letter));
                  int pos = i;
                  words.removeIf(w -> w.charAt(pos) == letter);
                  possibleLetters.add(""+letter);
              }
              if (state == 3) {
                  int pos = i;
                  words.removeIf(w -> w.charAt(pos) != letter);
                  possibleLetters.add(""+letter);
              }
          }

          String lettersInOrderAsString = "AEIOUNSCPRTBDLYGFHJKMQVWXZ";
          int[] lettersCount = new int[26];
          for (String w : words) {
            for (int i=0;i<6;i++) {
              lettersCount[w.charAt(i)-'A']++;
            }
          }
          
          String word;
          if (turn == 0) {
            word = "RAOIES";
          } else if (turn == 1) {
            word = "CUNMLD";
          } else if (turn <= 1) {
//            int index = 6;
//            int iter=0;
//            word ="";
//            while(iter <6) {
//              if (lettersCount[lettersInOrderAsString.charAt(index)-'A'] > 0) {
//                word += lettersInOrderAsString.charAt(index);
//                index++;
//                iter++;
//              } else {
//                index++;
//              }
//            }
            word = "SCPRTD";
          } else {
              words.remove(previousWord);
              System.err.println("Words in dictionnay : "+words.size());
              if (words.size() < 10) {
                  System.err.println(words);
              }

              System.err.println("Letter counts :");
              for (int i=0;i<26;i++) {
                if (lettersCount[i] > 0) System.err.println(""+(char)('A'+i)+" => "+lettersCount[i]);
              }

              String bestWord = null;
              double bestScore = Double.NEGATIVE_INFINITY;
              for (String w : words) {
                Set<String> placedLetters = new HashSet<>();
                double score = 0.0;
                for (int i=0;i<6;i++) {
                  score += lettersCount[w.charAt(i)-'A'];
                  if (placedLetters.contains(""+w.charAt(i))) score -=100; // malus to place the same letter multiple times
                  placedLetters.add(""+w.charAt(i));
                }
                
//                if (words.size() < 45) {
//                  System.err.println("Word : "+w+" score = "+score);
//                }
                if (score > bestScore) {
                  bestScore=  score;
                  bestWord = w;
                }
              }

              System.err.println("Best is "+bestWord+" with score "+bestScore);
              word = words.get(random.nextInt(words.size()));
              //word = bestWord;
          }


          // Write an action using System.out.println()
          // To debug: System.err.println("Debug messages...");

          System.out.println(word);
          previousWord = word;
          turn++;
      }
  }
}
