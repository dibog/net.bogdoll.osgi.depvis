package net.bogdoll.osgi.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

public class IterUtil {
	public static <E> Iterable<E> toIterable(final Enumeration<E> aEnum) {
		if(aEnum==null) return Collections.emptyList();
		
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new Iterator<E>() {

					@Override
					public boolean hasNext() {
						return aEnum.hasMoreElements();
					}

					@Override
					public E next() {
						return aEnum.nextElement();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
					
				};
			}
		};
	}
}
