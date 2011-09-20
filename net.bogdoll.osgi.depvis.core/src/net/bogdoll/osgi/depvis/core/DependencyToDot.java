package net.bogdoll.osgi.depvis.core;

import org.osgi.framework.Bundle;

public interface DependencyToDot {
	String toDot(Bundle[] aBundles);
}
