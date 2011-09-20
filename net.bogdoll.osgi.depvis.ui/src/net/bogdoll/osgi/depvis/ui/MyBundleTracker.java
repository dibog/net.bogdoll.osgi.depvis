package net.bogdoll.osgi.depvis.ui;

import net.bogdoll.osgi.depvis.core.DependencyToDot;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;

public class MyBundleTracker extends BundleTracker {

	private final SwingUI mUI;

	public MyBundleTracker(BundleContext context, DependencyToDot aService) {
		this(context, aService, new SwingUI());
	}
	
	private MyBundleTracker(BundleContext context, DependencyToDot aService, SwingUI aSwingUI) {
		super(context, Bundle.ACTIVE|Bundle.RESOLVED, new BundleTrackerCustomizer(aService, aSwingUI));
		mUI = aSwingUI;
	}

	public void close() {
		mUI.disposeIt();
	}
}
