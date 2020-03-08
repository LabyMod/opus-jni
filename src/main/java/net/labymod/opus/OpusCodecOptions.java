package net.labymod.opus;

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
