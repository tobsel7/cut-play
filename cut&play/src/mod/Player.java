package mod;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * Media player class which can play audio files (represented by track objects)
 * The value track in this class represents the currently selected value.
 * @author Tobias Haider
 */
public class Player {
	private Track track;	//Currently selected Audio file
	private boolean playing;	//status
	private int position;		//Current position in byte[]
	private long playingTime;	//Current position in milliseconds
	
	/**
	 * Constructor initializing all variables with standard values.
	 */
	public Player() {
		track = null;
		playing = false;
		position = 0;
	}
	
	/**
	 * Stops the player and sets a new Track.
	 * @param track New Track
	 */
	public void setTrack(Track track) {
		stop();
		this.track = track;
	}
	
	/**
	 * @return Current Track
	 */
	public Track getTrack() {
		return track;
	}
	
	/**
	 * Sets the start position to a custom value.
	 * @param seconds Start position in seconds
	 */
	public void setPosition(float seconds) {
		position = Modifier.calcPosition(track, seconds);
	}
	
	/**
	 * @return Current absolute position
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * @return Current position in seconds
	 */
	public long getPlayingTime() {
		return playingTime;
	}
	
	/**
	 * @return Playing status
	 */
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * Starts a new Thread and plays the audio Data using the DataLine from the AudioSystem.
	 * Cuts byte[] if the start position is not 0.
	 */
	public void play() {	//Play function in new thread
		if(track == null) {	
			return;
		}
		new Thread() {
			public void run() {
				byte[] playBytes = cutPlayBytes(track.getData(), position);	//Cut away data if start is not 0
				ByteArrayInputStream stream = new ByteArrayInputStream(playBytes);
				byte[] buffer = new byte[1024];
				int size = 0;	//sourceLine, buffers for output
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, track.getFormat());
				SourceDataLine sourceLine;
				try {
					sourceLine = (SourceDataLine)AudioSystem.getLine(info);
					sourceLine.open(); 
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				sourceLine.start(); 
				playing = true;
				final long start = java.lang.System.currentTimeMillis();
				while(playing) {	//Reading and writing from/to buffer
					size = stream.read(buffer, 0, 1024);
					if(size == -1) {
						break;
					}
					else {
						sourceLine.write(buffer, 0, buffer.length);
						playingTime = -start + java.lang.System.currentTimeMillis();
					}
				}	//Closing procedure
				if(playing) {
					sourceLine.drain(); 
				}
				sourceLine.stop();  
				sourceLine.close();
				playing = false;
			}
		}.start();
	}
	
	/**
	 * Resets the playing time and stops the player.
	 */
	public void stop() {
		playingTime = 0;
		playing = false;
	}
	
	/**
	 * Cuts away all audio data before the selected position.
	 * @param data Audio bytes
	 * @param position Absolute array position
	 * @return Cut byte array
	 */
	private byte[] cutPlayBytes(byte[] data, long position) {	//Cut out bytes from start to position
		if(position > data.length) {
			return data;
		}
		else {
			setPosition(position);	//set position
			return Arrays.copyOfRange(data, (int)position, data.length);
		}
	}
}
