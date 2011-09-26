package net.bogdoll.osgi.depvis.core.impl;

import net.bogdoll.osgi.depvis.core.DependencyToDot;
import net.bogdoll.osgi.depvis.core.DependencyToGraph;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	private ServiceRegistration mService;

	@Override
	public void start(BundleContext context) throws Exception {
		mService = context.registerService(
				new String[]{
						DependencyToGraph.class.getName(),
						DependencyToDot.class.getName(),
				}, new DependencyToDotImpl(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mService.unregister();
	}
}
