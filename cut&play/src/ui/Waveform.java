package ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.Vector;

/**
 * The Waveform class draws a waveform from 2 byte audio data in little endian format.
 * @author Daniel Binder
 */
public class Waveform extends JPanel {

	private static final long serialVersionUID = 1L;
	private Vector<Double> lines = new Vector<>();

	public Waveform() {
		setBackground(new Color(20, 20, 20));
		setPreferredSize(new Dimension(500,100));
	}

    /**
     * This method creates the waveform from the audio data
     * @param audioBytes audio data to create waveform from (needs to be 2 bytes and in little endian format)
     */
	public void createWaveForm(byte[] audioBytes) {
	    lines = new Vector<>();

		short[] wave = new short[audioBytes.length / 2];

		double maxVal = 0;
		for(int i = 0; i < audioBytes.length / 2; i++){
			wave[i] = (short) (audioBytes[(i * 2) + 1] << 8 | (255 & audioBytes[i * 2]));

			if(wave[i] > maxVal) {     //biggest value for scaling height
				maxVal = wave[i];
			}
		}

		double xFactor = (getSize().width * 1.0) / wave.length;    //scale wave length to panel size
		double yFactor = (getSize().height / maxVal) / 2.0;  //scale wave height to panel size
		double offset = getSize().height / 2.0;            //offset in middle

		double pointY;
		double lastY = 0;
		for(int i = 0; i < wave.length; i++) {
			pointY = wave[i] * yFactor;

			lines.add(new Line2D.Double(i * xFactor, pointY + offset,
                    (i - 1) * xFactor, lastY + offset));

			lastY = pointY;
		}

		repaint();
	}

    /**
     * This method draws the data
     * @param g Graphics object
     */
	@Override
	public void paint(Graphics g) {
		Dimension d = getSize();
		int w = d.width;
		int h = d.height;

		Graphics2D g2 = (Graphics2D) g;
		g2.setBackground(getBackground());
		g2.clearRect(0, 0, w, h);
		g2.setColor(Color.BLUE);

		for(Line2D l : lines) {
			g2.draw(l);
		}
	}
} 
