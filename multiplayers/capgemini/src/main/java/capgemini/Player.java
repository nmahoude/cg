package capgemini;

import java.util.BitSet;
import java.util.Scanner;


class TestSolution {
    public static void main(String a[]) {
        Scanner in=new Scanner(System.in);
        String M = in.nextLine();
        int L=M.length()*8;
        System.err.println("Size of string is "+M.length()+" length of bits is "+L);
        
        BitSet bits = new BitSet().valueOf(M.getBytes());
       
       
        for (int chars=0;chars<M.length();chars++) {
            
        }
        int c = 6;
        boolean currentValue = bits.get(6);
        System.err.println("First value @"+(6)+" is "+currentValue);
        String v=" 0";
        String result="";
        while(true) {
            c--;
            System.err.println("bit @"+c+" is "+bits.get(c));
            if ( bits.get(c) != currentValue) {
                System.err.println("Different, switchin");
                //System.out.print((currentValue ? "00" : "0")+v+(c!=0?" ":""));
                result= result + (currentValue ? "0" : "00")+v+" ";
                currentValue = !currentValue;
                v=" 0";
                System.err.println("result is "+result);
            } else {
                v+="0";
                System.err.println("Same, appending v="+v);
            }
            if (c==L-8) break;
            if (c%8==0)c+=15; // next char
        }
        System.out.println(result+(currentValue ? "0" : "00")+v);
    }
}
