package net.bogdoll.osgi.depvis.core.impl;

import net.bogdoll.osgi.depvis.core.Dependency;
import net.bogdoll.osgi.depvis.core.DependencyToDot;
import net.bogdoll.osgi.depvis.core.Graph;

import org.osgi.framework.Bundle;

public class DependencyToDotImpl extends DependencyToGraphImpl implements DependencyToDot
{
	@Override
	public String toDot(Bundle[] aBundles) {
		Graph graph = toGraph(aBundles);
		return createDotRepresentation(graph);
	}
	
	private String createDotRepresentation(Graph aGraph) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph osgi {\n");
		sb.append("\tnode [shape=record];\n");
		for(net.bogdoll.osgi.depvis.core.Bundle b : aGraph.bundles()) {
			sb.append(String.format("\t\"%s_%s\" [shape=record,label=\"{%s|ver=%s}\"];\n", 
						b.getSymbolicName(),
						b.getVersion(),
						b.getSymbolicName(),
						b.getVersion()));
		}
		
		for(Dependency d : aGraph.dependencies()) {
			StringBuilder sb2 = new StringBuilder();
			for(String p : d.getPackages()) {
				sb2.append("\\n").append(p);
			}
			String p = sb2.length()<2 ? "" : sb2.substring(2);
			sb.append(String.format("\t\"%s_%s\" -> \"%s_%s\" [label=\"%s\"];\n", 
					d.getFromBundle().getSymbolicName(), 
					d.getFromBundle().getVersion(),
					d.getToBundle().getSymbolicName(), 
					d.getToBundle().getVersion(),
					p)); 
		}
		sb.append("}\n");
		
		return sb.toString();
	}
}
