package net.bogdoll.osgi.util;

import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class DotUtil {
	public static Image toImage(String aDotString) {
		try {
			ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/dot","-Tpng");
			Process p = pb.start();
			PrintStream out = new PrintStream(p.getOutputStream(), false);
			out.println(aDotString);
			out.close();
			BufferedImage img = ImageIO.read(p.getInputStream());
			p.waitFor();
			return img;
		} catch(Exception e) {
			return null;
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		String dot = "digraph osgi {\n"+
	"node [shape=record];\n"+
	"0 [shape=record,label=\"{org.apache.felix.framework|ver=3.2.2}\"];\n"+
	"1 [shape=record,label=\"{org.apache.felix.shell|ver=1.4.2}\"];\n"+
	"2 [shape=record,label=\"{org.apache.felix.shell.tui|ver=1.4.1}\"];\n"+
	"3 [shape=record,label=\"{TestBundles.bundle.A|ver=0.0.0}\"];\n"+
	"4 [shape=record,label=\"{TestBundles.bundle.B|ver=0.0.0}\"];\n"+
	"5 [shape=record,label=\"{TestBundles.bundle.C|ver=0.0.0}\"];\n"+
	"6 [shape=record,label=\"{guava|ver=0.9.0}\"];\n"+
	"7 [shape=record,label=\"{net.bogdoll.osgi.depvis|ver=0.0.0}\"];\n"+
	"5 -> 4 [label=\"bundleb.pub [1.0]\"];\n"+
	"5 -> 3 [label=\"bundlea.pub [1.0]\"];\n"+
	"4 -> 3 [label=\"bundlea.pub [1.0]\"];\n"+
	"1 -> 1 [label=\"org.osgi.service.log [1.3]\"];\n"+
	"1 -> 1 [label=\"org.ungoverned.osgi.service.shell [1.0.0]\"];\n"+
	"1 -> 1 [label=\"org.apache.felix.shell [1.0.0]\"];\n"+
	"2 -> 1 [label=\"org.apache.felix.shell [1.0.0]\"];\n"+
	"7 -> 6 [label=\"com.google.common.collect [0.9]\"];\n}";

		final JFrame frame = new JFrame();
		final Image img = toImage(dot);
		final int width = img.getWidth(null);
		final ImageIcon ii = new ImageIcon(img);
		
		JLabel label = new JLabel(ii);
		final JScrollPane pane = new JScrollPane(label);
		pane.setAutoscrolls(true);
		pane.addMouseWheelListener(new MouseWheelListener() {
			double factor = 1.0;
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int amount = e.getWheelRotation();
				System.out.println("amount: "+amount);
				if(amount>0)
					factor *= 1.1;
				else
					factor /= 1.1;
				
				System.out.println("factor: "+factor);
				
				ii.setImage(img.getScaledInstance((int)(width*factor), -1, Image.SCALE_SMOOTH));
				frame.getContentPane().invalidate();
				frame.validate();
			}
		});
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.pack();
		frame.setVisible(true);
	}
}
