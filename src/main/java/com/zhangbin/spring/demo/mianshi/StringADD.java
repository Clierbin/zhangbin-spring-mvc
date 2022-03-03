package com.zhangbin.spring.demo.mianshi;

public class StringADD {
    public static void main(String[] args) {
        String solve = solve("456", "77");
        System.out.println(solve);
    }

    public static  String solve (String s, String t) {
        if(s.length()==0){
            return t;
        }else if(t.length()==0){
            return s;
        }
        char[] sarr=s.toCharArray();
        char[] tarr=t.toCharArray();
        String sb="";
        // write code here
        int foe=s.length()>t.length()?s.length():t.length();
        int x=s.length()-1;
        int y=t.length()-1;
        int tmp=0;
        for(int i =0;i<foe;i++){
            int a=x>=0?sarr[x]-48:0;
            int b=y>=0?tarr[y]-48:0;
            x--;
            y--;
            int jia=a+b+tmp;
            if(jia>=10){
                tmp=1;
                sb=(jia-10)+sb;
            }else{
                tmp=0;
                sb=jia+sb;
            }
        }
        if(tmp>0){
            sb=1+sb;
        }
        return sb;

    }
}
