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
        return;
      }
      String testedWord;
      String remainder2;
      for (Entry<String, String> morseKey : morseCodes.entrySet()) {
        String morse = morseKey.getKey();
        if (!remainder.startsWith(morse)) {
          continue;
        }
        String letter = morseKey.getValue();

        testedWord = wordToHere+letter;
        remainder2 = remainder.substring(morse.length());
        boolean foundOneWord = false;
        foundOneWord = isInDictionnary(testedWord, remainder2);
        if (foundOneWord && !remainder2.isEmpty()) {
          Node child = new Node(testedWord, remainder2);
          child.findChilds();
          childs.put(testedWord, child);
        } else {
        }

      }
    }

    private boolean isInDictionnary(String testedWord, String remainder2) {
      boolean foundOneWord = false;
      for (String s : dictionnary) {
        if (s.startsWith(testedWord)) {
          if (s.equals(testedWord)) {
            Node node = new Node("", remainder2);
            node.phrase +=testedWord;
            childs.put("EXACT", node);
            node.findChilds();
            if (remainder2.isEmpty()) {
              exactWordFound=true;
            }
          }
          foundOneWord = true;
        }
      }
      return foundOneWord;
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

    Node root = new Node("", L);
    root.findChilds();
    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");
   
    System.out.println(""+root.count());
  }
}