package net.bogdoll.osgi.depvis.core;

import java.io.Serializable;

public class Bundle implements Serializable {
	private static final long serialVersionUID = 1L;

	private final long mBundleId;
	private final String mSymbolicName;
	private final String mVersion;

	public Bundle(long aBundleId, String aSymbolicName, String aVersion) {
		mBundleId = aBundleId;
		mSymbolicName = aSymbolicName;
		mVersion = aVersion;
	}

	public long getBundleId() {
		return mBundleId;
	}

	public String getSymbolicName() {
		return mSymbolicName;
	}

	public String getVersion() {
		return mVersion;
	}
	
	@Override
	public String toString() {
		return getSymbolicName()+"_"+getVersion();
	}
}
