package net.bogdoll.osgi.depvis.ui;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class SwingUI extends JFrame {
	private static final long serialVersionUID = 0L;
	
	private int mWidth;
	private Image mImage;
	private ImageIcon mImageIcon;
	private float mFactor;
	
	public SwingUI() {
		super("OSGI Dependency Visualiser");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setContentPane(buildUI());
		pack();
		setVisible(true);
	}
	
	public void setImage(Image aImage) {
		mImage = aImage;
		mWidth = aImage.getWidth(null);
		SwingUtilities.invokeLater(new Runnable(){
			@Override public void run() {
				mFactor = 1.0f;
				mImageIcon.setImage(mImage);
				pack();
				setVisible(true);
			}});
	}
	
	public void disposeIt() {
		final JFrame that = this;
		SwingUtilities.invokeLater(new Runnable(){
			@Override public void run() {
				setVisible(false);
				that.dispose();
			}});
	}
	
	private Container buildUI() {
		mImageIcon = new ImageIcon();
		
		JLabel label = new JLabel(mImageIcon);
		final JScrollPane pane = new JScrollPane(label);
		pane.setAutoscrolls(true);
		pane.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int amount = e.getWheelRotation();
				System.out.println("amount: "+amount);
				if(amount>0)
					mFactor *= 1.1;
				else
					mFactor /= 1.1;
				
				System.out.println("factor: "+mFactor);
				
				mImageIcon.setImage(mImage.getScaledInstance((int)(mWidth*mFactor), -1, Image.SCALE_SMOOTH));
				getContentPane().invalidate();
				validate();
			}
		});

		return pane;
	}
}
