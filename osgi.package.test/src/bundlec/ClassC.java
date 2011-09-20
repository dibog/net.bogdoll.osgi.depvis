package bundlec;

import bundlea.ClassA;
import bundleb.ClassB;


public class ClassC 
{
	public ClassC() {
		System.out.println(ClassC.class);
		new ClassA();
		new ClassB();
	}
}
