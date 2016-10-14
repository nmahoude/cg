
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionTest {

  @Test
  public void simple() {

    Solution.dictionnaryAsMorse.clear();
    Solution.indexesToRemainingWordsCache.clear();
    
    Solution.morseCode = "-.-";
    Solution.finalIndex = Solution.morseCode.length();

    Solution.acceptWord("A");
    Solution.acceptWord("B");
    Solution.acceptWord("C");
    Solution.acceptWord("HELLO");
    Solution.acceptWord("K");
    Solution.acceptWord("WORLD");
    
    long count = Solution.countWordFromIndex(0);
    
    assertThat(count, is(1L));
  }
  
  @Test
  public void second() throws Exception {
    Solution.dictionnaryAsMorse.clear();
    Solution.indexesToRemainingWordsCache.clear();
    
    Solution.morseCode = "--.-------..";
    Solution.finalIndex = Solution.morseCode.length();

    Solution.acceptWord("GOD");
    Solution.acceptWord("GOOD");
    Solution.acceptWord("MORNING");
    Solution.acceptWord("G");
    Solution.acceptWord("HELLO");
    
    long count = Solution.countWordFromIndex(0);
    
    assertThat(count, is(1L));
  }
  
  @Test
  public void message() throws Exception {
    Solution.dictionnaryAsMorse.clear();
    Solution.indexesToRemainingWordsCache.clear();
    
    Solution.morseCode = "......-...-..---.-----.-..-..-..";
    Solution.finalIndex = Solution.morseCode.length();

    Solution.acceptWord("HELL");
    Solution.acceptWord("HELLO");
    Solution.acceptWord("OWORLD");
    Solution.acceptWord("WORLD");
    Solution.acceptWord("TEST");
    
    long count = Solution.countWordFromIndex(0);
    
    assertThat(count, is(2L));
  }
}
