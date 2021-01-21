package com.ever.webSpam.sample;

import java.util.Random;

public class RandomExample {

	public static void main(String[] args)  {

        Random rd = new Random();//랜덤 객체 생성
        int limint = 150;
        for(int i=0;i<50;i++) {
            System.out.println(i + "["+(rd.nextInt(limint)+1)+"]"); //로또번호 출력
            System.out.println(i + "["+(rd.nextInt(limint)+1)+"]"); //로또번호 출력
        }
    }

}
