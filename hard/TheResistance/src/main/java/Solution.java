import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

class Solution {

  static Map<String, String> morseCodes = new HashMap<>();
  static {
    morseCodes.put(".-", "A");
    morseCodes.put("-...", "B");
    morseCodes.put("-.-.", "C");
    morseCodes.put("-..", "D");
    morseCodes.put(".", "E");
    morseCodes.put("..-.", "F");
    morseCodes.put("--.", "G");
    morseCodes.put("....", "H");
    morseCodes.put("..", "I");
    morseCodes.put(".---", "J");
    morseCodes.put("-.-", "K");
    morseCodes.put(".-..", "L");
    morseCodes.put("--", "M");
    morseCodes.put("-.", "N");
    morseCodes.put("---", "O");
    morseCodes.put(".--.", "P");
    morseCodes.put("--.-", "Q");
    morseCodes.put(".-.", "R");
    morseCodes.put("...", "S");
    morseCodes.put("-", "T");
    morseCodes.put("..-", "U");
    morseCodes.put("...-", "V");
    morseCodes.put(".--", "W");
    morseCodes.put("-..-", "X");
    morseCodes.put("-.--", "Y");
    morseCodes.put("--..", "Z");
  }
  static List<String> dictionnary = new ArrayList<>();
  
  static class Node {
    String wordToHere = "";
    String remainder ="";
    String phrase = "";
    Map<String, Node> childs = new HashMap<>();
    private boolean exactWordFound;

    
    public Node(String wordToHere, String remainder) {
      this.wordToHere = wordToHere;
      this.remainder = remainder;
    }

    public void findChilds() {
      if (remainder.isEmpty()) {
        System.err.println("When empty : "+wordToHere);
        return;
      }
      for (Entry<String, String> morseKey : morseCodes.entrySet()) {
        String morse = morseKey.getKey();
        if (!remainder.startsWith(morse)) {
          continue;
        }
        String letter = morseKey.getValue();

        System.err.println("found morse : "+morse +" -> "+letter);
        String testedWord = wordToHere+letter;
        String remainder2 = remainder.substring(morse.length());
        boolean foundOneWord = false;
        for (String s : dictionnary) {
          if (s.startsWith(testedWord)) {
            if (s.equals(testedWord)) {
              System.err.println("*** Exact word found !*** : "+s+"/"+testedWord);
              phrase+=testedWord;
              testedWord="";
              if (remainder2.isEmpty()) {
                exactWordFound=true;
              }
            }
            foundOneWord = true;
          }
        }
        if (foundOneWord) {
          System.err.println("Found at least one word which start by: "+testedWord+" remainder="+remainder2);
          Node child = new Node(testedWord, remainder2);
          child.findChilds();
          childs.put(testedWord, child);
        } else {
          System.err.println("No word start with "+testedWord);
        }

      }
    }

    public void print() {
      if (childs.isEmpty()) {
        System.out.println(wordToHere);
      }
    }

    public int count() {
      int count = exactWordFound ? 1 : 0;
      for (Node child : childs.values()) {
        count+=child.count();
      }
      return count;
    }
  }

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    String L = in.next();

    int N = in.nextInt();
    for (int i = 0; i < N; i++) {
      String W = in.next();
      dictionnary.add(W);
    }

    System.err.println("L: "+L);
    System.err.println("Dictonnary: ");
    for (String d : dictionnary) {
      System.err.println(d);
    }
    Node root = new Node("", L);
    root.findChilds();
    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");
   
    System.out.println(""+root.count());
  }
}