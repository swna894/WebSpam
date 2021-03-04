package com.ever.webSpam.example;

public class round {

	
	public static double roundf(double d) {
		return (d + 4) / 0.5 * 0.5;
	}

	public static void main(String[] args) {
//		double x =2.2;
//		double y =2.7;
//		double z = 2.1;
////		System.err.println((x*10)%10);
////		System.err.println((y*10)%10);
////		System.err.println((z*10)%10);
////		
////		if((x*10)%10 > 5) {
////			System.err.println(Math.floor(x));
////		}
////		System.err.println(Math.floor(x));
////		System.err.println(Math.floor(y));
////		System.err.println(Math.floor(z));
//
//		double per1 = 3.5694949828;
////		double per = Double.parseDouble(String.format("%.1f",per1));
////		System.out.println(per);
//		
//		
//		double per2 = 3.1494949828;
////		 per = Double.parseDouble(String.format("%.1f",per2));
////		System.out.println(per);
//
//		double xx = (per1*100)%100;
//		System.out.println(per1);
//		double yy = (per2*100)%100;
//		System.out.println(per2);
		
		//소수 둘째 자리 가져오기
		double org = 3.5694949828;
		double dou = org * 10;
		double inx = dou - (int) dou;
		System.err.println(inx);
		
		String string = String.valueOf(3.569*10);
		Double doublex = Double.valueOf(string) * 100;
		System.err.println(doublex);
		
		//System.err.println((int)(doublex/10));
		
		if(inx > 0.5) {		
			
		} else {
			System.err.println();
		}
	}


}
