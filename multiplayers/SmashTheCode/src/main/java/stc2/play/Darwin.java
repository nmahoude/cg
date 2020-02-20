package stc2.play;

import stc2.play.Round.Candidate;

public class Darwin {

  public static void mutate(Candidate[] candidates) {
    Candidate newCandidates[] = new Candidate[candidates.length];
    // 3 best go unchanged
    newCandidates[0] = candidates[0];
    newCandidates[1] = candidates[2];
    newCandidates[2] = candidates[3];
    
    newCandidates[3] = createCandidateFrom(candidates[0], candidates[1]);
    newCandidates[4] = createCandidateFrom(candidates[0], candidates[1]);
    newCandidates[5] = createCandidateFrom(candidates[0], candidates[2]);
    newCandidates[6] = createCandidateFrom(candidates[1], candidates[2]);
    
    newCandidates[7] = createRandom();
    newCandidates[8] = createRandom();
    newCandidates[9] = createRandom();
    
  }

  private static Candidate createRandom() {
    Candidate c1 = new Candidate();
    c1.adjustementFactor = AdjustementFactors.random();
    return c1;
  }

  private static Candidate createCandidateFrom(Candidate c1, Candidate c2) {
    Candidate c = new Candidate();
    c.adjustementFactor.mutate(c1.adjustementFactor, c2.adjustementFactor);
    return c;
  }

}
