package net.bogdoll.osgi.depvis.ui;

import net.bogdoll.osgi.depvis.core.DependencyToDot;
import net.bogdoll.osgi.depvis.ui.impl.DotUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

public class BundleTrackerCustomizer implements org.osgi.util.tracker.BundleTrackerCustomizer {

	private final DependencyToDot mService;
	private final SwingUI mUI;

	public BundleTrackerCustomizer(DependencyToDot aService, SwingUI aUI) {
		mService = aService;
		mUI = aUI;
	}
	
	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		try { 
			BundleContext bc = bundle.getBundleContext();
			if(bc!=null)
				dumpDependency(bc.getBundles());
			return bundle;
		} catch(NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		dumpDependency(bundle.getBundleContext().getBundles());
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		dumpDependency(bundle.getBundleContext().getBundles());
	}
	
	private void dumpDependency(Bundle[] aBundles) {
		String dot = mService.toDot(aBundles);
		mUI.setImage(DotUtil.toImage(dot));
	}
}
