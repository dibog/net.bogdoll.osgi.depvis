package net.bogdoll.osgi.depvis.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Pair<F,S> {
	public final F first;
	public final S second;
	
	private Pair(F aFirst, S aSecond) {
		first = checkNotNull(aFirst);
		second = checkNotNull(aSecond);
	}
	
	public static <F,S> Pair<F,S> create(F aFirst, S aSecond) {
		return new Pair<F,S>(aFirst,aSecond);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + first.hashCode();
		result = prime * result + second.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			@SuppressWarnings("rawtypes")
			Pair other = (Pair) obj;
			return first.equals(other.first) && second.equals(other.second);
		} else {
			return false;
		}
	}
	

}
