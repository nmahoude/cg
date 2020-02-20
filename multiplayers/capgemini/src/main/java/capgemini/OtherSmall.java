package capgemini;

class Solution {
  public static void main(String a[]) {
    char[]M=new java.util.Scanner(System.in).nextLine().toCharArray();
  
    boolean k=(M[0]&64)!=0,v;
    String r=k?"0 ":"00 ";
    
    for (int B:M)
        for (int m=64;m!=0;m=m/2)
        {
            r+=((v=(B&m)!=0)==k)?"0":v?" 0 0":" 00 0";
            k = v;
        }
    System.out.print(r);
  }
}
