import java.util.*;
import java.io.*;
import java.math.*;

class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int N = in.nextInt();
        int giftPrice = in.nextInt();

        int maxMoney = 0;
        int wallet[] = new int[N];
        for (int i = 0; i < N; i++) {
            int money = in.nextInt();
            wallet[i] = money;
            maxMoney+=money;
        }
        
        if (maxMoney < giftPrice) {
          System.out.println("IMPOSSIBLE");
          return;
        }
        Arrays.sort(wallet);
        int totalAmount = 0;
        int amount[] = new int[N];
        while (totalAmount < giftPrice) {
          for (int i=N;--i>=0 && totalAmount < giftPrice;) {
            if (wallet[i] > 0) {
              wallet[i]-=1;
              amount[i]+=1;
              totalAmount+=1;
            }
          }
        }
        for (int i=0;i<N;i++) {
          System.out.println(amount[i]);
        }
    }
}