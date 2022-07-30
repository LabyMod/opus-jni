package net.labymod.opus;

/*
opus-jni, a simple Opus JNI Wrapper.
Copyright (C) 2020 LabyMedia GmbH This program is free software:
you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation,
either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
public class OpusCodecOptions {

    private final int frameSize;
    private final int sampleRate;
    private final int channels;
    private final int bitrate;
    private final int maxFrameSize;
    private final int maxPacketSize;

    private OpusCodecOptions(int frameSize, int sampleRate, int channels, int bitrate, int maxFrameSize, int maxPacketSize) {
        this.frameSize = frameSize;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitrate = bitrate;
        this.maxFrameSize = maxFrameSize;
        this.maxPacketSize = maxPacketSize;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    protected static OpusCodecOptions of(int frameSize, int sampleRate, int channels, int bitrate, int maxFrameSize, int maxPacketSize) {
        return new OpusCodecOptions(frameSize, sampleRate, channels, bitrate, maxFrameSize, maxPacketSize);
    }
}
