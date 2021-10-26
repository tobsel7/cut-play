package wave;

/**
 * The WaveEffect class implements some elements of the wave class.
 * @author Daniel Binder
 */
public class WaveEffect {

    /**
     * This method is a wrapper for the cut method inside the wave object
     * @param wave Wave to be cut
     * @param startTime time to start cutting in seconds
     * @param duration duration of cut
     * @return a cut Wave object
     */
    public static Wave cutWave(Wave wave, double startTime, double duration) {
        return wave.cut(startTime, duration);
    }

    /**
     * This method cuts all parts below a threshold that are longer than minDuration
     * @param wave input wave
     * @param minDuration minimal duration that should be checked
     * @param percentage value below which should be cut
     * @return cut wave
     */
    public static Wave autoCut(Wave wave, double minDuration, int percentage) {
        double threshold = 65536 * ((0.0 + percentage) / 100);
        minDuration = wave.lengthFromSeconds(minDuration);
        double totalTime = 0;
        double cutTimeSave = 0;
        double cutTime = 0;
        double totalTimeSave = 0;
        boolean found = false;      //exit condition

        Wave result = wave.copy();

        for(int i : wave) {
            if((i < 0 ? (i * (-1)) : i) < threshold) {
                cutTime++;
            } else {
                cutTimeSave = cutTime;
                cutTime = 0;
            }

            totalTime++;

            if(cutTimeSave > minDuration) {
                cutTimeSave = wave.secondsFromLength((int) cutTimeSave);
                totalTimeSave = wave.secondsFromLength((int) totalTime);
                result = result.cut(2 * (totalTimeSave - cutTimeSave), 2 * cutTimeSave);

                found = true;

                break;
            }
        }

        return found ? autoCut(result, minDuration, percentage) : result;    //searches for next instance
    }

    /**
     * This method adds an Offset to every sample value
     * @param wave Object to use
     * @param offset Offset to add
     * @return Wave Object with added Offset
     */
    public static Wave addOffset(Wave wave, int offset) {
        return wave.modify(a -> a + offset);
    }

    /**
     * This method amplifies the entire wave by a percentage
     * @param wave Object to use
     * @param percentage < 100 = quieter
     *                   > 100 = louder
     * @return amplified Object
     */
    public static Wave amplify(Wave wave, int percentage) {
        return wave.modify(a -> (int) (a * ((1.0 * percentage) / 100)));
    }

    /**
     * This method amplifies a part of the wave (from start to end)
     * @param wave Object to use
     * @param percentage < 100 = quieter
     *                   > 100 = louder
     * @param startTime start time
     * @param duration end time
     * @return partly amplified Object
     */
    public static Wave amplify(Wave wave, int percentage, double startTime, double duration) {
        return wave.modify(startTime, duration, a -> (int) (a * ((1.0 * percentage) / 100)));
    }

    /**
     * This method adds a fade in to a wave
     * @param wave Object to use
     * @param toTime time to fade in to (in seconds)
     * @return Wave Object with fade in
     */
    public static Wave fadeIn(Wave wave, double toTime) {
        double duration = toTime / 100;
        double startTime = 0;

        Wave result = wave.modify(startTime, duration, a -> 0);
        startTime += duration;

        for(int i = 1; i < 100; i++) {
            result = amplify(result, i, startTime, duration);

            startTime += duration;
        }

        return result;
    }

    /**
     * This method add a fade out to a wave
     * @param wave Object to use
     * @param fromTime time to fade out from (in seconds)
     * @return Wave Object with fade out
     */
    public static Wave fadeOut(Wave wave, double fromTime) {
        double duration = (wave.getLengthInSec() - fromTime) / 100;
        double startTime = fromTime;

        Wave result = wave.copy();
        startTime += duration;

        for(int i = 99; i >= 0; i--) {
            result = amplify(result, i, startTime, duration);

            startTime += duration;
        }

        return result;
    }

    /**
     * Adds values of shorter wave to longer wave
     * @param wave1 Wave object
     * @param wave2 Wave object
     * @return sum Wave object
     */
    public static Wave add(Wave wave1, Wave wave2) {
        if(wave1.getLength() > wave2.getLength()) {
            return wave1.add(wave2);
        } else {
            return wave2.add(wave1);
        }
    }

    /**
     * Subtracts values of shorter wave from longer wave
     * @param wave1 Wave object
     * @param wave2 Wave object
     * @return sum Wave object
     */
    public static Wave subtract(Wave wave1, Wave wave2) {
        if(wave1.getLength() > wave2.getLength()) {
            return wave1.subtract(wave2);
        } else {
            return wave2.subtract(wave1);
        }
    }
}
