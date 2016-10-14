import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

class Solution {

  static Map<Character, String> morseCodes = new HashMap<>();
  static String morseCode;
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

  static List<String> dictionnaryAsMorse = new ArrayList<>();
  static Map<Integer, Long> indexesToRemainingWordsCache = new HashMap<>();
  static int finalIndex;
  
  public static void main(String args[]) {
    indexesToRemainingWordsCache.clear();
    
    Scanner in = new Scanner(System.in);
    morseCode = in.next();
    finalIndex = morseCode.length();
    
    int N = in.nextInt();
    for (int i = 0; i < N; i++) {
      String W = in.next();
      acceptWord(W);
    }

    System.out.println(""+countWordFromIndex(0));
  }

  static long countWordFromIndex(int index) {
    System.err.println("Counting from "+index);
    long count = 0;
    String substringToCheck = morseCode.substring(index);
    
    Long cacheCount = indexesToRemainingWordsCache.get(Integer.valueOf(index));
    if (cacheCount != null) {
      System.err.println("Use cache for word at index "+index+" instead of recalculating");
      return cacheCount.longValue();
    }

    // Damn, we need to recalculate
    for (String morseWord : dictionnaryAsMorse) {
      if (substringToCheck.startsWith(morseWord)) {
        int newIndex = index+morseWord.length();
        if (newIndex == finalIndex) {
          count += 1;
        } else {
          long countFromIndex = countWordFromIndex(newIndex);
          count+=countFromIndex;
        }
      }
    }
    
    System.err.println("Putting index "+index+" with cache "+count);
    indexesToRemainingWordsCache.put(index, count);
    return count;
  }

  private static String wordToMorse(String w) {
    String asMorse = "";
    for (int i=0;i<w.length();i++) {
      asMorse+=morseCodes.get(w.charAt(i));
    }
    return asMorse;
  }

  public static void acceptWord(String word) {
    dictionnaryAsMorse.add(wordToMorse(word));
  }
}