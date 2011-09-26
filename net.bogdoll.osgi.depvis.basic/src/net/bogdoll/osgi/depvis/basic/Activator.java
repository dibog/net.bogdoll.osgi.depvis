package net.bogdoll.osgi.depvis.basic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import net.bogdoll.osgi.depvis.core.DependencyToDot;
import net.bogdoll.osgi.depvis.core.DependencyToGraph;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator{
	private ServiceTracker mDotTracker;
	private ServiceTracker mGraphTracker;
	
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			File dotFile = new File(System.getProperty("dep.dot.file","dependency.dot"));
			mDotTracker = new ServiceTracker(context, DependencyToDot.class.getName(), new DotServiceTrackerCustomizer(context,dotFile));
			mDotTracker.open();
			
			File graphFile = new File(System.getProperty("dep.ser.file","dependency.ser"));
			mGraphTracker = new ServiceTracker(context, DependencyToGraph.class.getName(), new GraphServiceTrackerCustomizer(context,graphFile));
			mGraphTracker.open();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mDotTracker.close();
		mDotTracker = null;
		
		mGraphTracker.close();
		mGraphTracker = null;
	}
}

class DotServiceTrackerCustomizer implements ServiceTrackerCustomizer {

	private final BundleContext mContext;
	private final File mDotFile;
	private DependencyToDot mService;

	public DotServiceTrackerCustomizer(BundleContext aContext, File aDotFile) {
		mContext = aContext;
		mDotFile = aDotFile;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		mService = (DependencyToDot) mContext.getService(reference);
		try {
			PrintStream out = new PrintStream(mDotFile);
			out.print(mService.toDot(mContext.getBundles()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return mService;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		mContext.ungetService(reference);
	}
	
}

class GraphServiceTrackerCustomizer implements ServiceTrackerCustomizer {

	private final BundleContext mContext;
	private final File mGraphFile;
	private DependencyToGraph mService;

	public GraphServiceTrackerCustomizer(BundleContext aContext, File aGraphFile) {
		mContext = aContext;
		mGraphFile = aGraphFile;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		mService = (DependencyToGraph) mContext.getService(reference);
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mGraphFile));
			try {
				out.writeObject(mService.toGraph(mContext.getBundles()));
			} 
			finally {
				out.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return mService;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		mContext.ungetService(reference);
	}
	
}
