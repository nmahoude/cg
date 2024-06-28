package perf;

import java.lang.reflect.Field;

import fast.read.FastReader;
import spring2022.State;
import spring2022.ag.LightState;
import sun.misc.Unsafe;

public class ImplemAsArray {

  
  public static void main(String[] args) throws Exception {
    State state = new State();
    state.readGlobal(FastReader.fromString("0 0 3 "));
    state.read(FastReader.fromString("""
        ^3 304 3 536
        ^14
        ^0 1 6053 4062 0 0 -1 -1 -1 -1 -1
        ^1 1 2828 5652 0 0 -1 -1 -1 -1 -1
        ^2 1 13000 4975 0 0 -1 -1 -1 -1 -1
        ^3 2 11722 4773 0 1 -1 -1 -1 -1 -1
        ^4 2 12087 5502 0 0 -1 -1 -1 -1 -1
        ^95 0 6251 4427 0 0 5 -321 -237 0 1
        ^102 0 11135 5495 0 0 2 240 319 0 2
        ^103 0 6637 3202 0 0 12 -240 -319 0 1
        ^106 0 4821 6444 0 0 2 -274 291 0 0
        ^109 0 5560 3501 0 0 8 -163 -365 0 1
        ^111 0 2569 6235 0 0 19 -347 -198 0 1
        ^113 0 7425 3103 0 0 23 145 -372 0 0
        ^114 0 11155 3829 0 0 23 180 356 0 2
        ^115 0 6475 5171 0 0 23 -180 -356 0 1
        """.replace("^", "") ));

    LightState s =  new LightState();
    s.createFrom(state);
    LightState copy =  new LightState();

    
    LightState2 s2 = new LightState2();
    LightState2 copy2 = new LightState2();
    
    Unit2 u1 = new Unit2(copy2, 3);
    Unit2 u2 = new Unit2(copy2, 7);
    
    
    long start, end;
    int COPY_SIZE = 100_000;
    int AFFECTATION_SIZE = COPY_SIZE * 1000;

    start = System.currentTimeMillis();
    for (int i=0;i<COPY_SIZE;i++) {
      copy.copyFrom(s);

    }
    end = System.currentTimeMillis();
    System.out.println("Time : "+(end-start)+" in ms");

    start = System.currentTimeMillis();
    for (int j=0;j<AFFECTATION_SIZE;j++) {
      copy.units[3].health = copy.units[13].health + 5;
      copy.units[7].pos.x= copy.units[13].pos.y + 5;
    }
    end = System.currentTimeMillis();
    System.out.println("Time : "+(end-start)+" in ms");

    
    
    start = System.currentTimeMillis();
    for (int i=0;i<COPY_SIZE;i++) {
      copy2.copyFrom(s2);
    }
    end = System.currentTimeMillis();
    System.out.println("Time : "+(end-start)+" in ms");

    start = System.currentTimeMillis();
    for (int j=0;j<AFFECTATION_SIZE;j++) {
      copy2.v[3 * LightState2.DECAL + LightState2.HEALTH] = copy2.v[13 * LightState2.DECAL + LightState2.HEALTH] + 5;
      copy2.v[7 * LightState2.DECAL + LightState2.X] = copy2.v[13 * LightState2.DECAL + LightState2.Y] + 5;
    }
    end = System.currentTimeMillis();
    System.out.println("Time : "+(end-start)+" in ms");

    
    start = System.currentTimeMillis();
    for (int j=0;j<AFFECTATION_SIZE;j++) {
      u1.setHealth(u2.health() + 5);
      u1.setPosy(u2.posx() + 5);
    }
    end = System.currentTimeMillis();
    System.out.println("Time : "+(end-start)+" in ms");

    
    
    Field f = Unsafe.class.getDeclaredField("theUnsafe");
    f.setAccessible(true);
    Unsafe unsafe = (Unsafe) f.get(null);

    System.out.println("PAGE SIZE: " + unsafe.pageSize());


    int memoryBlock = 50 * 1024 * 1024;
    long address = unsafe.allocateMemory(memoryBlock);

    System.out.println("ALLOCATED");

    long currentAddress = address;
    for (int j=0;j<AFFECTATION_SIZE;j++) {
      unsafe.putInt(currentAddress, unsafe.getInt(currentAddress, (int) 17));
      unsafe.putInt(currentAddress+128, unsafe.getInt(currentAddress, (int) 256));
    }
    end = System.currentTimeMillis();
    System.out.println("Time : "+(end-start)+" in ms");
    
    System.out.println("MEMORY TOUCHED");
    /*
     * STAGE 4 Frees the allocated memory.
     */

    unsafe.freeMemory(address);

    System.out.println("DE-ALLOCATED");
  
  }
}
