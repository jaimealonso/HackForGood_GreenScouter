package com.hackforgood.myapplication;

/**
 * Created by jaime on 17/4/15.
 */
public class PolarMessageParser {

    private int lastHeartRate = 0;

    /**
     * Applies Polar packet validation rules to buffer.
     *   Polar packets are checked for following;
     *     offset 0 = header byte, 254 (0xFE).
     *     offset 1 = packet length byte, 8, 10, 12, 14.
     *     offset 2 = check byte, 255 - packet length.
     *     offset 3 = sequence byte, range from 0 to 15.
     *
     * @param buffer an array of bytes to parse
     * @param i buffer offset to beginning of packet.
     * @return whether buffer has a valid packet at offset i
     */
    private boolean packetValid (byte[] buffer, int i) {
        boolean headerValid = (buffer[i] & 0xFF) == 0xFE;
        boolean checkbyteValid = (buffer[i + 2] & 0xFF) == (0xFF - (buffer[i + 1] & 0xFF));
        boolean sequenceValid = (buffer[i + 3] & 0xFF) < 16;

        return headerValid && checkbyteValid && sequenceValid;
    }

    public int parseBuffer(byte[] buffer) {

        int heartRate = 0;
        boolean heartrateValid = false;

        // Minimum length Polar packets is 8, so stop search 8 bytes before buffer ends.
        for (int i = 0; i < buffer.length - 8; i++) {
            heartrateValid = packetValid(buffer,i);
            if (heartrateValid)  {
                heartRate = buffer[i + 5] & 0xFF;
                break;
            }
        }

        // If our buffer is corrupted, use decaying last good value.
        if(!heartrateValid) {
            heartRate = (int) (lastHeartRate * 0.8);
            if(heartRate < 50)
                heartRate = 0;
        }

        lastHeartRate = heartRate;                          // Remember good value for next time.

        return lastHeartRate;

//        // Heart Rate
//        Sensor.SensorData.Builder b = Sensor.SensorData.newBuilder()
//                .setValue(heartRate)
//                .setState(Sensor.SensorState.SENDING);
//
//        Sensor.SensorDataSet sds = Sensor.SensorDataSet.newBuilder()
//                .setCreationTime(System.currentTimeMillis())
//                .setHeartRate(b)
//                .build();
//
//        return sds;
    }

    /**
     * Applies packet validation rules to buffer
     *
     * @param buffer an array of bytes to parse
     * @return whether buffer has a valid packet starting at index zero
     */
    public boolean isValid(byte[] buffer) {
        return packetValid(buffer,0);
    }

    /**
     * Polar uses variable packet sizes; 8, 10, 12, 14 and rarely 16.
     * The most frequent are 8 and 10.
     * We will wait for 16 bytes.
     * This way, we are assured of getting one good one.
     *
     * @return the size of buffer needed to parse a good packet
     */
    public int getFrameSize() {
        return 16;
    }

    /**
     * Searches buffer for the beginning of a valid packet.
     *
     * @param buffer an array of bytes to parse
     * @return index to beginning of good packet, or -1 if none found.
     */
    public int findNextAlignment(byte[] buffer) {
        // Minimum length Polar packets is 8, so stop search 8 bytes before buffer ends.
        for (int i = 0; i < buffer.length - 8; i++) {
            if (packetValid(buffer,i)) {
                return i;
            }
        }
        return -1;
    }

}
