package mod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.sound.sampled.*;

/**
 * @author Philipp Götzenberger
 * @author Tobias Haider
 * The converter class handles all input and output operations with audio files.
 * Files with the Format .mp3 and .wav can be loaded and saved into a Track object.
 * Track objects can be saved to a .wav file
 */
public class Converter {
	
	/**
	 * Gets data from a .mp3 file and saves it in a Track object
	 * @param f .mp3 File
	 * @return Track object with data from a .mp3 file
	 * @throws UnsupportedAudioFileException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static Track getTrackFromMP3 (File f)  throws UnsupportedAudioFileException, IllegalArgumentException, Exception  {
		byte[] bytes;
		
			try (final AudioInputStream in = AudioSystem.getAudioInputStream(f)){
				AudioFormat baseFormat = in.getFormat();
				AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
															baseFormat.getSampleRate(),
															16,
															baseFormat.getChannels(),
															baseFormat.getChannels() * 2,
															baseFormat.getSampleRate(),
															false);
			try (final ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				 final AudioInputStream decodedIn = AudioSystem.getAudioInputStream(decodedFormat, in)){
				  byte [] buffer = new byte[8192];
		            while(true){
		                int readCount = decodedIn.read(buffer, 0, buffer.length);
		                if(readCount == -1){
		                    break;
		                }
		                out.write(buffer, 0, readCount);
		            }
		            bytes = out.toByteArray();
		            return new Track(f.getName(), bytes, in.getFormat());
				}
			}
	}
	
	/**
	 * Gets data from a .wav file and saves it in a Track object
	 * @param f
	 * @return Track object with data from a .wav file
	 */
	public static Track getTrackFromWav(File f) {	
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AudioInputStream in;
		try {
			in = AudioSystem.getAudioInputStream(f);
			int read;
			byte[] buff = new byte[1024];
			while ((read = in.read(buff)) > 0) {
			    out.write(buff, 0, read);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		byte[] audioBytes = out.toByteArray();
		
		return new Track(f.getName(), audioBytes, in.getFormat());
	}
	
	/**
	 * Saves Track object in .wav File
	 * @param t Track object 
	 * @param name Name of the created file
	 * @param parentFolder Location where the file is saved
	 */
	public static void save(Track t, String name, File parentFolder) {
		byte[] bytes = t.getData();

		AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(bytes), t.getFormat(),
                bytes.length / t.getFormat().getFrameSize());

		try {
		    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(parentFolder + File.separator + name +".wav"));
        } catch(Exception e) {
		    e.printStackTrace();
        }
	}
}
