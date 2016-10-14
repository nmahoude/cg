import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

class Solution {

  static Map<Character, String> morseCodes = new HashMap<>();
  static {
    morseCodes.put('A', ".-");
    morseCodes.put('B', "-...");
    morseCodes.put('C', "-.-.");
    morseCodes.put('D', "-..");
    morseCodes.put('E', ".");
    morseCodes.put('F', "..-.");
    morseCodes.put('G', "--.");
    morseCodes.put('H', "....");
    morseCodes.put('I', ".." );
    morseCodes.put('J', ".---");
    morseCodes.put('K', "-.-");
    morseCodes.put('L', ".-..");
    morseCodes.put('M', "--");
    morseCodes.put('N', "-.");
    morseCodes.put('O', "---");
    morseCodes.put('P', ".--.");
    morseCodes.put('Q', "--.-");
    morseCodes.put('R', ".-.");
    morseCodes.put('S', "...");
    morseCodes.put('T', "-");
    morseCodes.put('U', "..-");
    morseCodes.put('V', "...-");
    morseCodes.put('W', ".--");
    morseCodes.put('X', "-..-");
    morseCodes.put('Y', "-.--");
    morseCodes.put('Z', "--..");
  }
  static Node root = new Node();
  static String morseCode;
  static int finalIndex;
  static Map<Integer, Long> indexToWordCountCache = new HashMap<>();
  
  static class Node {
    String morseLetter;
    int morseLetterLength;
    Map<String, Node> childs = new HashMap<>();
    private boolean exactWordFound;
    private String letter;
    private Node parent;
    private boolean finalWord;

    
    public Node() {
    }

    @Override
    public String toString() {
      return ""+letter+" ("+morseCode+")";
    }
    
    public final long acceptMorse(int index) {
      if (morseLetter ==null) {
        // start of word, check index
        Long indextoWordCount = indexToWordCountCache.get(index);
        if (indextoWordCount != null) {
          System.err.println("Using cache : "+index+" index to "+indextoWordCount+" word count");
          return indextoWordCount.longValue();
        }
      } else {
        for (int i=0;i<morseLetterLength;i++) {
          if ((i+index >= finalIndex)
           || (morseCode.charAt(index+i) != morseLetter.charAt(i))) {
            return 0;
          }
        }
        index = index+morseLetterLength;
      }
      
      long count = 0;
      if (finalWord) {
        if (index >= finalIndex) {
          count+= 1;
        } else {
          long futureWord=root.acceptMorse(index);
          if (morseLetter != null) {
            System.err.println("Puting in cache : "+index+" index to "+futureWord+" word count");
            indexToWordCountCache.put(Integer.valueOf(index), Long.valueOf(futureWord));
            count+=futureWord;
          }
        }
      }
      if (!childs.isEmpty()) {
        for (Node child : childs.values()) {
          count+=child.acceptMorse(index);
        }
      }
      return count;
    }

    private String getFinalWorld() {
      Node current = this;
      String value ="";
      while (current!=null) {
        value = value+letter;
        current = current.parent;
      }
      return value;
    }
    
    public final void acceptWord(String dictionnaryWord) {
      if (dictionnaryWord.isEmpty()) {
        finalWord = true;
        return;
      }
      String letter = ""+dictionnaryWord.charAt(0);
      String morseLetter = morseCodes.get(letter.charAt(0));
      String reminder = dictionnaryWord.substring(1);
      Node child = childs.get(letter);
      if (child == null) {
        child = new Node();
        child.parent = this;
        child.letter = letter;
        child.morseLetter = morseLetter;
        child.morseLetterLength =morseLetter.length();
        childs.put(letter, child);
      }
      child.acceptWord(reminder);
    }
  }

  public static void main(String args[]) {
    indexToWordCountCache.clear();
    
    Scanner in = new Scanner(System.in);
    morseCode = in.next();
    finalIndex = morseCode.length();
    
    int N = in.nextInt();
    for (int i = 0; i < N; i++) {
      String W = in.next();
      root.acceptWord(W);
    }

    System.out.println(""+root.acceptMorse(0));
  }
}