package edu.thu.keg.GB.http0702;

import java.util.Map;
import java.util.TreeMap;

class A {
	public void cI(int x) {
		x++;
	}

	public void cC(char c) {
		c = 'w';

	}

	public void cD(double d) {
		d *= 1.0;
	}

	public void cB(boolean b) {
		b = !b;
	}
}

public class Main extends A {

	static void proc(int sel) throws ArithmeticException,
			ArrayIndexOutOfBoundsException {
		System.out.println("in Situation" + sel);
		if (sel == 0) {
			System.out.println("no exception");

		} else if (sel == 1) {
			int i[] = new int[4];
			i[3] = 3;
		}
	}

	public static void main(String arg[]) {
		int i=2;
		char c='c';
		double d=5.3f;
		boolean b=true;
		System.out.println("a="+i+",b="+d+", c="+c+",d="+b);
		A a=new A();
		a.cI(i);
		a.cC(c);
		a.cD(d);
		a.cB(b);
		System.out.println("a="+i+",b="+d+", c="+c+",d="+b);
	}
}
