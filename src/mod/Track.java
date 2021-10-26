package mod;

import javax.sound.sampled.AudioFormat;

/**
 * Custom wrapper class representing an audio file
 * Is used to store all the necessary data from a .wav or .mp3 file
 * @author Tobias Haider
 */
public class Track {
	
	private static int index = 1;	//Static index counter
	private final int id;	//Index of Track object
	private final String name;	//Name of a Track
	private final byte[] data;	//Audio data
	private final AudioFormat format;	//Audio format
	private boolean modified;	//Modification tag used in toString()
	private double length;		//length in seconds
	
	/**
	 * Main contructor
	 * @param name Name of the Track
	 * @param data Audio data in byte array
	 * @param format Audio format
	 * @param modified Modified flag
	 */
	public Track(String name, byte[] data, AudioFormat format, boolean modified) {
		id = index++;																
		this.name = name;
		this.data = data;
		this.format = format;
		length = updateLength();
		this.modified = modified;
	}
	
	/**
	 * Constructor used by the Converter to store data in a Track object
	 * Modified is set to false, because the Track is not modified yet.
	 * @param name Name of the Track
	 * @param data Audio bytes
	 * @param format Audio format
	 */
	public Track(String name, byte[] data, AudioFormat format) {	//initConstructor
		this(name, data, format, false);							//Use this constructor only when creating an unmodified Track
	}
	
	/**
	 * Constructor using an already existing Track
	 * Modifier functions use this constructor.
	 * @param t Unmodified Track
	 * @param data modified audio bytes
	 */
	public Track(Track t, byte[] data) {				//modConstructor 
		this(t.getName(), data, t.getFormat(), true);	//Use this constructor after modification
	}
	
	/**
	 * @return Index
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Audio bytes array
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return Audio format
	 */
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * @return Track length in seconds
	 */
	public double getLength() {
		return updateLength();
	}
	
	/**
	 * Calculates the length of the Track in seconds using information from the data array and the Audio format.
	 * (Array length, sample rate, sample size, channels)
	 * @return Length of this Track in seconds
	 */
	public double updateLength() {	//Calculate length in seconds
		int sampleRate = (int)format.getSampleRate();
		int sampleSizeInBits = format.getSampleSizeInBits();
		int channels = format.getChannels();
		double bytesPerSecond = ((sampleRate * sampleSizeInBits * channels) / 8.0);
		return data.length/bytesPerSecond;
	}
	
	/**
	 * Custom toString function necessary for the user interface
	 */
	@Override
	public String toString() {
		if(modified) {
			return id + ": " + name + " |" + String.format("%.02f", length) + " s (modified)";
		}
		else {
			return id + ": " + name + " |" + String.format("%.02f", length) + " s";
		}
	}
}
