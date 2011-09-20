package net.bogdoll.osgi.depvis.ui;

import net.bogdoll.osgi.depvis.core.DependencyToDot;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {
	private ServiceTracker mServiceTracker;
	private BundleContext mContext;
	
	@Override
	public void start(BundleContext context) throws Exception {
		mContext = context;
		mServiceTracker = new ServiceTracker(context, DependencyToDot.class.getName(), this);		
		mServiceTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mServiceTracker.close();
		mContext = null;
		mServiceTracker = null;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		DependencyToDot service = (DependencyToDot)mContext.getService(reference);
		MyBundleTracker tracker = new MyBundleTracker(
				mContext, 
				service);
		tracker.open();
		
		return tracker;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		removedService(reference, service);
		addingService(reference);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		BundleTracker tracker = (BundleTracker)service;
		tracker.close();
	
		mContext.ungetService(reference);
	}
}
