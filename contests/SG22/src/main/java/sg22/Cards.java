package sg22;

import fast.read.FastReader;

public class Cards {
  public static final int TRAINING = 0;
  public static final int CODING = 1;
  public static final int DAILY_ROUTINE = 2;
  public static final int TASK_PRIORIZATION = 3;
  public static final int ARCHITECTURE_STUDY = 4;
  public static final int CI = 5;
  public static final int CODE_REVIEW = 6;
  public static final int REFACTORING = 7;
  public static final int BONUS = 8;
  public static final int DEBT = 9;
  public static final int UNKNOWN = 10;
  
  public int[] count = new int[10];
  int baclee = 0;
  

  public void copyFrom(Cards model) {
    this.baclee = model.baclee;
    System.arraycopy(model.count, 0, this.count, 0, 10);
  }

  
  public void read(FastReader in) {
    baclee = 0;
    
    for (int i=0;i<10;i++) {
      count[i] = in.nextInt();
      
      if (i < BONUS ) baclee += 2*count[i];
      if (i == BONUS) baclee += 1*count[i];
    }
  }


  public static Cards forDesks() {
    Cards cards = new Cards();
    for (int i=0;i<8;i++) {
      cards.count[i] = 5;
    }
    return cards;
  }
  
  @Override
  public String toString() {
    return String.format("[ %d %d %d %d %d %d %d %d %d %d]", count[0], 
        count[1], 
        count[2], 
        count[3], 
        count[4], 
        count[5], 
        count[6], 
        count[7], 
        count[8], 
        count[9]);
  }


  public boolean hasTaskPriorization() {
    return count[TASK_PRIORIZATION] > 0;
  }


  public void put(int index) {
    put(index, 1);
  }
  
  public void put(int index, int count) {
    this.count[index]+=count;

    if (index == BONUS) baclee += count;
    else if (index == DEBT) baclee += 0;
    else baclee += 2*count;
  }

  public void remove(int index) {
    remove(index, 1);
  }

  public void remove(int index, int count) {
    this.count[index]-=count;
    
    if (index == BONUS) baclee -= count;
    else if (index == DEBT) baclee -= 0;
    else baclee -= 2*count;
  }


  public static String toName(int loc) {
    switch (loc) {
    case TRAINING: return "TRAINING";
    case CODING: return "CODING";
    case DAILY_ROUTINE : return "DAILY_ROUTINE";
    case TASK_PRIORIZATION : return "TASK_PRIO";
    case ARCHITECTURE_STUDY: return "ARCHI";
    case CI : return "CI";
    case CODE_REVIEW : return "CODE REVIEW";
    case REFACTORING : return "REFACTO";
    case BONUS  : return "BONUS";
    case DEBT : return "DEBT";
    case UNKNOWN : return "?";
    }
    return "CARD ??? ";
  }


  public void removeDebt(int debtToRemove) {
    count[Cards.DEBT] = Math.max(0, count[Cards.DEBT]- debtToRemove);
  }


  public void addBonus(int toAdd) {
    this.count[Cards.BONUS]+=toAdd;
  }


  public void addDebt(int debtToAdd) {
    count[Cards.DEBT] += debtToAdd;
  }
  
  public void clear() {
    for (int i=0;i<10;i++) {
      count[i] = 0;
    }
    baclee = 0;
  }
  
  
  public void debugCards(String location) {
    System.err.println(String.format("^ %-15s    %d %d %d %d %d %d %d %d %d %d", location, 
        count[0], 
        count[1], 
        count[2], 
        count[3], 
        count[4], 
        count[5], 
        count[6], 
        count[7], 
        count[8], 
        count[9] 
        ));
  }


  public int totalCount() {
    return         
        count[0]+ 
        count[1]+ 
        count[2]+ 
        count[3]+ 
        count[4]+ 
        count[5]+ 
        count[6]+ 
        count[7]+ 
        count[8]+
        count[9]
            ;
  }



}
