package c4l.molecule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import c4l.GameState;
import c4l.entities.MoleculeType;
import c4l.entities.Sample;

public class MoleculeInfo {
  public int moleculesNeeded[] = new int [GameState.MOLECULE_TYPE];
  public int sampleId;
  public int points; // how much points will be scored*/
  
  @Override
  public String toString() {
    return "sample="+sampleId+" "+Arrays.toString(moleculesNeeded)+" points : "+points+"\n\r";
  }
  /**
   * return the list of needed molcules, without how much we need
   * @return
   */
  public List<MoleculeType> getNeededMolecules() {
    List<MoleculeType> types = new ArrayList<>();
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (moleculesNeeded[i]>0) {
        types.add(MoleculeType.values()[i]);
      }
    }
    return types;
  }
}
