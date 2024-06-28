package perf;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeArray {

  public static void main(String[] args) throws Exception {
    Field f = Unsafe.class.getDeclaredField("theUnsafe");
    f.setAccessible(true);
    Unsafe unsafe = (Unsafe) f.get(null);

    System.out.println("PAGE SIZE: " + unsafe.pageSize());


    int memoryBlock = 50 * 1024 * 1024;
    long address = unsafe.allocateMemory(memoryBlock);

    System.out.println("ALLOCATED");

    long currentAddress = address;
    while (currentAddress < (address + memoryBlock)) {
      unsafe.putInt(currentAddress, (int) 0);
      unsafe.getInt(currentAddress, (int) 0);
    }

    System.out.println("MEMORY TOUCHED");
    /*
     * STAGE 4 Frees the allocated memory.
     */

    unsafe.freeMemory(address);

    System.out.println("DE-ALLOCATED");
  }
}
