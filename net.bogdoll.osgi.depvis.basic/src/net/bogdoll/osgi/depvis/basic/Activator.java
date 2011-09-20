package net.bogdoll.osgi.depvis.basic;

import java.io.File;
import java.io.PrintStream;

import net.bogdoll.osgi.depvis.core.DependencyToDot;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator{
	@Override
	public void start(BundleContext context) throws Exception {
		File dotFile = new File(System.getProperty("dep.dot.file","dependency.dot"));
		ServiceReference dotReference = context.getServiceReference(DependencyToDot.class.getName());
		try {
			Bundle[] bundles = context.getBundles();
			if(bundles!=null && bundles.length>0) {
				DependencyToDot	dot = (DependencyToDot) context.getService(dotReference);
				PrintStream out = new PrintStream(dotFile);
				out.println(dot.toDot(bundles));
			}
		}
		finally {
			context.ungetService(dotReference);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}
}
