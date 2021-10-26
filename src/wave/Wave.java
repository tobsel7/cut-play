package wave;

import mod.Track;

import javax.sound.sampled.AudioFormat;
import java.util.Iterator;
import java.util.function.Function;

/**
 * The Wave class represents sample audio data saved in little endian format.
 * A wave object can only be created by using a factory method.
 * @author Daniel Binder
 */
public class Wave implements Iterable<Integer> {
    private double length;
    private int rate;           //in Hz e.g. 44100
    private int resolution;     //in byte e.g. 2
    private byte[] wave;

    /**
     * Constructor for Wave Object
     * @param rate rate of Wave
     * @param resolution resolution of Wave
     * @param data Wave data (needs to be little endian)
     */
    private Wave(int rate, int resolution, byte[] data) {
        this.rate = rate;
        this.resolution = resolution;
        this.wave = data;
        this.length = wave.length;
    }

    /**
     * Constructor for Wave Object
     * @param other Wave to use as reference for rate and resolution
     * @param data Data to insert instead of other.data (needs to be little endian)
     */
    private Wave(Wave other, byte[] data) {
        this.rate = other.rate;
        this.resolution = other.resolution;
        this.wave = data.clone();
        this.length = wave.length;
    }

    /**
     * This is a Factory method to create a Wave from a Track
     * @param t Track to create Wave from (needs to be little endian)
     * @return Wave Object
     */
    public static Wave createWave(Track t) {
        return new Wave((int) t.getFormat().getSampleRate(), 2, t.getData().clone());
    }

    /**
     * This method converts a Wave Object to a Track Object
     * @param name Name of the Track
     * @param format Audio format of the Track
     * @return Track Object
     */
    public Track toTrack(String name, AudioFormat format) {
        return new Track(name, wave, format, true);
    }

    /**
     * This method cuts out a duration starting from startTime
     * @param startTime time to start in seconds
     * @param duration time to cut out in seconds
     * @return cut Wave object
     */
    public Wave cut(double startTime, double duration) {
        startTime = lengthFromSeconds(startTime);
        duration = lengthFromSeconds(duration);

        byte[] data = new byte[wave.length - ((int) duration)];

        int j = 0;
        for(int i = 0; i < wave.length; i++) {
            if(startTime > 0) {
                data[i] = wave[i];

                startTime--;
                j++;
            } else {
                if(duration > 0) {
                    duration--;
                } else {
                    data[j] = wave[i];
                    j++;
                }
            }
        }

        return new Wave(this, data);
    }

    /**
     * Returns a clone of this
     * @return Wave clone
     */
    public Wave copy() {
        return new Wave(this, wave.clone());
    }

    /**
     * Modifies the whole wave
     * @param mapper Function<Integer, Integer> to modify each value with
     * @return modified Wave
     */
    public Wave modify(Function<Integer, Integer> mapper) {
        return modify(0, getLengthInSec(), mapper);
    }

    /**
     * Modifies wave from startTime to startTime + duration
     * @param startTime time to start in seconds
     * @param duration duration of modification
     * @param mapper Function<Integer, Integer> to modify values during duration
     * @return modified Wave
     */
    public Wave modify(double startTime, double duration, Function<Integer, Integer> mapper) {
        startTime = lengthFromSeconds(startTime);
        duration = lengthFromSeconds(duration);

        byte[] data = new byte[wave.length];

        int value, result;
        for(int i = 0; i < wave.length - 1; i += resolution) {

            value = (wave[i + 1] << 8) | (255 & wave[i]);
            if(startTime > 0) {
                data[i] = wave[i];
                data[i + 1] = wave[i + 1];

                startTime--;
            } else {
                if(duration > 0) {
                    result = mapper.apply(value);

                    if(result > 32767) {
                        data[i] = 127;
                        data[i + 1] = 127;
                    } else {
                        if(result < -32768) {
                            data[i] = -128;
                            data[i + 1] = -128;
                        } else {
                            data[i + 1] = (byte) (255 & (result >> 8));     //MSB
                            data[i] = (byte) (255 & result);    //LSB
                        }
                    }

                    duration--;
                } else {
                        data[i] = wave[i];
                        data[i + 1] = wave[i + 1];
                }
            }
        }

        return new Wave(this, data);
    }

    /**
     * Adds another Wave to this
     * @param other Wave to add to this (needs to be shorter)
     * @return added Wave
     */
    Wave add(Wave other) {
        if(other.length > length) {
            throw new IllegalArgumentException("Wave other needs to be shorter");
        }

        byte[] otherWave = other.getWave().clone();
        byte[] thisWave = wave.clone();

        for(int i = 0; i < otherWave.length; i++) {
            if(thisWave[i] + otherWave[i] > 127) {
                thisWave[i] = 127;
            } else {
                if(thisWave[i] + otherWave[i] < -128) {
                    thisWave[i] = - 128;
                } else {
                    thisWave[i] += otherWave[i];
                }
            }
        }

        return new Wave(this, thisWave);
    }

    /**
     * Subtracts a Wave from this
     * @param other Wave to subtract from this (needs to be shorter)
     * @return subtracted Wave
     */
    Wave subtract(Wave other) {
        if(other.length > length) {
            throw new IllegalArgumentException("Wave other needs to be shorter");
        }

        byte[] otherWave = other.getWave().clone();
        byte[] thisWave = wave.clone();

        for(int i = 0; i < otherWave.length; i++) {
            if(thisWave[i] - otherWave[i] > 127) {
                thisWave[i] = 127;
            } else {
                if(thisWave[i] - otherWave[i] < -128) {
                    thisWave[i] = - 128;
                } else {
                    thisWave[i] -= otherWave[i];
                }
            }
        }

        return new Wave(this, thisWave);
    }

    //getter
    public int getRate() {
        return rate;
    }

    public int getResolution() {
        return resolution;
    }

    double getLength() {
        return length;
    }

    public double getLengthInSec() {
        return (2 * length) / (rate * resolution);
    }

    public byte[] getWave() {
        return wave.clone();
    }

    /**
     * Calculates wave length from seconds
     * @param seconds seconds to calculate wave from
     * @return wave.length (array length of data with a given amount of seconds)
     */
    double lengthFromSeconds(double seconds) {
        return (seconds * rate * resolution) / 4;
    }

    /**
     * Calculates seconds from a wave length
     * Useful if one counted the amount of values and wants to use that amount in a method
     * @param length amount of values passed
     * @return time passed during that amount of values
     */
    double secondsFromLength(int length) {
        return (4.0 * length) / (rate * resolution);
    }

    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < wave.length - 3;
            }

            @Override
            public Integer next() {
                i += 2;
                return (wave[i + 1] << 8) | (255 & wave[i]);
            }
        };
    }
}
