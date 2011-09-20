package net.bogdoll.osgi.depvis.filebased;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.bogdoll.osgi.depvis.core.DependencyToDot;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

public class BundleTrackerCustomizer implements org.osgi.util.tracker.BundleTrackerCustomizer {

	private final DependencyToDot mService;
	private final File mDotFile;

	public BundleTrackerCustomizer(DependencyToDot aService, File aDotFile) {
		mService = aService;
		mDotFile = aDotFile;
	}
	
	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		BundleContext bc = bundle.getBundleContext();
		if(bc!=null)
			dumpDependency(bc.getBundles());
		return bundle;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		BundleContext bc = bundle.getBundleContext();
		if(bc!=null)
			dumpDependency(bc.getBundles());
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		BundleContext bc = bundle.getBundleContext();
		if(bc!=null)
			dumpDependency(bc.getBundles());
	}
	
	private void dumpDependency(Bundle[] aBundles) {
		String dot = mService.toDot(aBundles);
		try {
			PrintWriter out = new PrintWriter(mDotFile, "UTF8");
			out.println(dot);
			out.close();
		} 
		catch(FileNotFoundException e) {
			Logger.getLogger(BundleTrackerCustomizer.class.getName()).log(Level.SEVERE, "Can't dump dependency into '"+mDotFile.getAbsolutePath()+"'", e);
		} 
		catch (UnsupportedEncodingException e) {
			Logger.getLogger(BundleTrackerCustomizer.class.getName()).log(Level.SEVERE, "Can't use UTF8 as encoding", e);
		}
	}
}
