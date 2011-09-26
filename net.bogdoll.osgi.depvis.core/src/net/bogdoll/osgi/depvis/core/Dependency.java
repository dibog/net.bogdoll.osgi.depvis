package net.bogdoll.osgi.depvis.core;

import java.io.Serializable;

public class Dependency implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Bundle mFrom;
	private Bundle mTo;
	private String[] mPackages;

	public Dependency(Bundle aFrom, Bundle aTo, String[] aPackages) {
		mFrom = aFrom;
		mTo = aTo;
		mPackages = aPackages;
	}

	public String[] getPackages() {
		return mPackages;
	}

	public Bundle getFromBundle() {
		return mFrom;
	}

	public Bundle getToBundle() {
		return mTo;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String p : getPackages()) {
			sb.append(", ").append(p);
		}
		String p = sb.length()<2 ? " " : " "+sb.substring(2); 
 		return getFromBundle()+" -> "+getToBundle()+p;
	}
}
