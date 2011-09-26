package net.bogdoll.osgi.depvis.core;

import org.osgi.framework.Bundle;

public interface DependencyToGraph {
	Graph toGraph(Bundle[] aBundles);
}
