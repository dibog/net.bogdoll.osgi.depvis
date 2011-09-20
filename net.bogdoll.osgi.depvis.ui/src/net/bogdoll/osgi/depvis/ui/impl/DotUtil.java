package net.bogdoll.osgi.depvis.ui.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.PrintStream;

import javax.imageio.ImageIO;

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
}
