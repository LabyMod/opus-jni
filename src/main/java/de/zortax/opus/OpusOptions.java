package de.zortax.opus;

public class OpusOptions {

  private int frameSize;
  private int sampleRate;
  private int channels;
  private int bitrate;
  private int maxFrameSize;
  private int maxPacketSize;

  private OpusOptions(
      int frameSize,
      int sampleRate,
      int channels,
      int bitrate,
      int maxFrameSize,
      int maxPacketSize) {
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

  public static OpusOptionsBuilder builder() {
    return new OpusOptionsBuilder();
  }

  public static final class OpusOptionsBuilder {
    private int frameSize = 960;
    private int sampleRate = 48000;
    private int channels = 1;
    private int bitrate = 64000;
    private int maxFrameSize = 6*960;
    private int maxPacketSize = 3*1276;

    private OpusOptionsBuilder() {

    }

    public static OpusOptionsBuilder anOpusOptions() {
      return new OpusOptionsBuilder();
    }

    public OpusOptionsBuilder setFrameSize(int frameSize) {
      this.frameSize = frameSize;
      return this;
    }

    public OpusOptionsBuilder setSampleRate(int sampleRate) {
      this.sampleRate = sampleRate;
      return this;
    }

    public OpusOptionsBuilder setChannels(int channels) {
      this.channels = channels;
      return this;
    }

    public OpusOptionsBuilder setBitrate(int bitrate) {
      this.bitrate = bitrate;
      return this;
    }

    public OpusOptionsBuilder setMaxFrameSize(int maxFrameSize) {
      this.maxFrameSize = maxFrameSize;
      return this;
    }

    public OpusOptionsBuilder setMaxPacketSize(int maxPacketSize) {
      this.maxPacketSize = maxPacketSize;
      return this;
    }

    public OpusOptions build() {
      return new OpusOptions(frameSize, sampleRate, channels, bitrate, maxFrameSize, maxPacketSize);
    }
  }
}
