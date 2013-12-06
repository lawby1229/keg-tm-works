package edu.thu.keg.adsl;

public class StringFunc {

	public static int getLCSlenth(String a, String b) {
		int substringLength1 = a.length();
		int substringLength2 = b.length();
		// 随机生成字符串
		String x = a;
		String y = b;
		Long startTime = System.nanoTime();
		// 构造二维数组记录子问题x[i]和y[i]的LCS的长度
		int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];
		// 动态规划计算所有子问题
		for (int i = substringLength1 - 1; i >= 0; i--) {
			for (int j = substringLength2 - 1; j >= 0; j--) {
				if (x.charAt(i) == y.charAt(j))
					opt[i][j] = opt[i + 1][j + 1] + 1;
				else
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
			}
		}
		System.out.println("substring1:" + x);
		System.out.println("substring2:" + y);
		System.out.print("LCS:");
		int i = 0, j = 0;
		while (i < substringLength1 && j < substringLength2) {
			if (x.charAt(i) == y.charAt(j)) {
				System.out.print(x.charAt(i));
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1])
				i++;
			else
				j++;
		}
		Long endTime = System.nanoTime();
		System.out.println(" Totle time is " + (endTime - startTime) + " ns");
		return opt[0][0];
	}

	public static void main(String arg[]) {
		System.out.println(StringFunc.getLCSlenth("sgh-x507", "sgh-d508"));
	}
}
