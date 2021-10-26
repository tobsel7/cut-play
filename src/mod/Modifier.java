package mod;

import wave.Wave;
import wave.WaveEffect;

import javax.sound.sampled.AudioFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Contains multiple static functions for audio data manipulation.
 * All functions return a new Track object with the modified flag set to true.
 * Many functions simply delegate the task the WaveEffect class by calling the corresponding functions.
 * The reason for this is creating a simpler and uniform programming interface for the UI.
 * @author Tobias Haider
 * @author Daniel Binder
 * @author Philipp Götzenberger
 */
public class Modifier {
	
	/**
	 * Cuts away parts of the byte array representing the audio data.
	 * @param t Track used for modification
	 * @param from Start in seconds
	 * @param to End in seconds
	 * @return New modified Track object
	 */
	public static Track cut(Track t, float from, float to) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(Arrays.copyOfRange(t.getData(), 0, Modifier.calcPosition(t, from)));
			out.write(Arrays.copyOfRange(t.getData(), Modifier.calcPosition(t, to), t.getData().length));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Track(t, out.toByteArray());
	}
	
	/**
	 * Calls fadeIn function from the WaveEffect class.
	 * @param t Track used for modification
	 * @param to End in seconds
	 * @return New modified Track object
	 */
	public static Track fadeIn(Track t, float to) {
		return WaveEffect.fadeIn(Wave.createWave(t), to).toTrack(t.getName(), t.getFormat());
	}
	
	/**
	 * Calls fadeOut function from the WaveEffect class.
	 * @param t Track used for modification
	 * @param from Start in seconds
	 * @return New modified Track object
	 */
	public static Track fadeOut(Track t, float from) {
		return WaveEffect.fadeOut(Wave.createWave(t), from).toTrack(t.getName(), t.getFormat());
	}
	
	/**
	 * Add bytes with the value 0 at a specific position. 
	 * @param t Track used for modification
	 * @param pos Position in seconds
	 * @param seconds Length of the "silent" byte array in seconds
	 * @return New modified Track object
	 */
	public static Track addSil(Track t, float pos, float seconds) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int arrPos = Modifier.calcPosition(t, pos);
		byte[] silBytes = new byte[Modifier.calcPosition(t, seconds)];
		try {
			out.write(Arrays.copyOfRange(t.getData(), 0, arrPos));
			out.write(silBytes);
			out.write(Arrays.copyOfRange(t.getData(), arrPos, t.getData().length));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Track(t, out.toByteArray());
	}

	/**
	 * Call amplify function from the WaveEffect class.
	 * @param t Track used for modification
	 * @param percentage Scaling factor in percent
	 * @return New modified Track object
	 */
	public static Track volume(Track t, int percentage) {
		return WaveEffect.amplify(Wave.createWave(t), percentage).toTrack(t.getName(), t.getFormat());
	}
	
	/**
	 * Call autoCut function from the WaveEffect class.
	 * @param t Track used for modification
	 * @param threshold Threshold value in percent
	 * @param minCutDuration Minimal duration for part to be cut out
	 * @return New modified Track object
	 */
	public static Track autoCut(Track t, int threshold, float minCutDuration) {
		return WaveEffect.autoCut(Wave.createWave(t), minCutDuration, threshold)
				.toTrack(t.getName(), t.getFormat());
	}

	/**
	 * Concatenate the data arrays from different Track objects.
	 * @param l List of Tracks in correct order
	 * @return New modified Track object
	 */
	public static Track concat(List<Track> l) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		for(Track t : l) {
			try {
				bStream.write(t.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new Track(l.get(0), bStream.toByteArray());
	}

	/**
	 * Add sample values from a list of Tracks using the add function from the WaveEffect class.
	 * @param l List of Tracks (order does not matter)
	 * @return New modified Track object
	 */
	public static Track add(List<Track> l) {
	    Iterator<Track> it = l.iterator();

        Track first = it.next();
        String name = first.getName();
        AudioFormat format = first.getFormat();
        Wave wave = Wave.createWave(first);
        Track t;

        while(it.hasNext()) {
            t = it.next();
            wave = WaveEffect.add(wave, Wave.createWave(t));
        }

		return wave.toTrack(name, format);
	}

	/**
	 * Subtract sample values of the Second list element from the first list element using the subtract function from the WaveEffect class.
	 * @param l List of Tracks (order does not matter)
	 * @return New modified Track object
	 */
	public static Track subtract(List<Track> l) {
        Iterator<Track> it = l.iterator();

        Track first = it.next();
        String name = first.getName();
        AudioFormat format = first.getFormat();
        Wave wave = Wave.createWave(first);
        Track t;

        while(it.hasNext()) {
            t = it.next();
            wave = WaveEffect.subtract(wave, Wave.createWave(t));
        }

        return wave.toTrack(name, format);
	}

	/**
	 * Assisting function calculating an absolute position in a byte array.
	 * @param t	Track object 
	 * @param seconds Position in the Track in seconds
	 * @return Absolute array position
	 */
	public static int calcPosition(Track t, float seconds) {
		if(t == null) {
			return 0;
		}   //Conversion from seconds to array position
		int sampleRate = (int)t.getFormat().getSampleRate();
		int sampleSizeInBits = t.getFormat().getSampleSizeInBits();
		int channels = t.getFormat().getChannels();
		int bytesPerSecond = (sampleRate * sampleSizeInBits * channels) / 8;
		return (int)(bytesPerSecond * seconds);
	}
}
