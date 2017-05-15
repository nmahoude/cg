package c4l.molecule;

import java.util.ArrayList;
import java.util.List;

  /** in order, which molecules needed for which sample*/
public class MoleculeComboInfo {
  public List<MoleculeInfo> infos = new ArrayList<>();
  public double score;
  
  @Override
  public String toString() {
    String output = "s("+score+") \n\r";
    for (MoleculeInfo info : infos) {
      output += info.toString();
    }
    return output;
  }
}
