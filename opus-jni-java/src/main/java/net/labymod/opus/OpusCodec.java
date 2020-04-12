package net.labymod.opus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/*
opus-jni, a simple Opus JNI Wrapper.
Copyright (C) 2020 LabyMedia GmbH This program is free software:
you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation,
either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
public class OpusCodec {

  private final OpusCodecOptions opusOptions;
  private boolean encoderInitialized = false;
  private boolean decoderInitialized = false;
  private long encoderState;
  private long decoderState;

  private OpusCodec(OpusCodecOptions opusOptions) {
    this.opusOptions = opusOptions;
  }

  public int getFrameSize() {
    return this.opusOptions.getFrameSize();
  }

  public int getSampleRate() {
    return this.opusOptions.getSampleRate();
  }

  public int getChannels() {
    return this.opusOptions.getChannels();
  }

  public int getBitrate() {
    return this.opusOptions.getBitrate();
  }

  public int getMaxFrameSize() {
    return this.opusOptions.getMaxFrameSize();
  }

  public int getMaxPacketSize() {
    return this.opusOptions.getMaxPacketSize();
  }


  public static OpusCodec createDefault() {
    return newBuilder().build();
  }

  public static OpusCodec createByOptions(OpusCodecOptions opusCodecOptions) {
    return new OpusCodec(opusCodecOptions);
  }

  public static Builder newBuilder() {
    return new Builder();
  }


  /**
   * Encodes a chunk of raw PCM data.
   *
   * @param bytes data to encode. Must have a length of CHANNELS * FRAMESIZE * 2.
   * @return encoded data
   * <p>
   * throws {@link IllegalArgumentException} if bytes has an invalid length
   */
  public byte[] encodeFrame(byte[] bytes) {
    return this.encodeFrame(bytes, 0, bytes.length);
  }

  /**
   * Encodes a chunk of raw PCM data.
   *
   * @param bytes data to encode. Must have a length of CHANNELS * FRAMESIZE * 2.
   * @return encoded data
   * <p>
   * throws {@link IllegalArgumentException} if length is invalid
   */
  public byte[] encodeFrame(byte[] bytes, int offset, int length) {
    if (length != getChannels() * getFrameSize() * 2)
      throw new IllegalArgumentException(String.format("data length must be == CHANNELS * FRAMESIZE * 2 (%d bytes) but is %d bytes", getChannels() * getFrameSize() * 2, bytes.length));
    this.ensureEncoderExistence();
    return this.encodeFrame(this.encoderState, bytes, offset, length);
  }

  private native byte[] encodeFrame(long encoder, byte[] in, int offset, int length);

  /**
   * Decodes a chunk of opus encoded pcm data.
   *
   * @param bytes data to decode. Length may vary because the less complex the encoded pcm data is, the compressed data size is smaller.
   * @return encoded data.
   */
  public byte[] decodeFrame(byte[] bytes) {
    this.ensureDecoderExistence();
    return this.decodeFrame(this.decoderState, bytes);
  }

  private native byte[] decodeFrame(long decoder, byte[] out);


  private void ensureEncoderExistence() {
    if (this.encoderInitialized) return;
    this.encoderState = this.createEncoder(this.opusOptions);
    this.encoderInitialized = true;
  }

  private native long createEncoder(OpusCodecOptions opts);


  private void ensureDecoderExistence() {
    if (this.decoderInitialized) return;
    this.decoderState = this.createDecoder(this.opusOptions);
    this.decoderInitialized = true;
  }

  private native long createDecoder(OpusCodecOptions opts);

  /**
   * destroys Opus encoder and decoder
   */
  public void destroy() {
    if (this.encoderInitialized) this.destroyEncoder(this.encoderState);
    if (this.decoderInitialized) this.destroyDecoder(this.decoderState);
  }

  private native void destroyEncoder(long encoder);

  private native void destroyDecoder(long decoder);

  /**
   * Default settings should be good to use for most cases.
   */
  public static class Builder {

    private int frameSize = 960;
    private int sampleRate = 48000;
    private int channels = 1;
    private int bitrate = 64000;
    private int maxFrameSize = 6 * 960;
    private int maxPacketSize = 3 * 1276;

    private Builder() {
    }

    public int getFrameSize() {
      return this.frameSize;
    }

    public Builder withFrameSize(int frameSize) {
      this.frameSize = frameSize;
      return this;
    }

    public int getSampleRate() {
      return this.sampleRate;
    }

    /**
     * @param sampleRate The sample rate to use in the codec instance.
     *                   8, 12, 16, 24 and 48khz are supported.
     * @return this
     */
    public Builder withSampleRate(int sampleRate) {
      this.sampleRate = sampleRate;
      return this;
    }

    public int getChannels() {
      return this.channels;
    }

    public Builder withChannels(int channels) {
      this.channels = channels;
      return this;
    }

    public int getBitrate() {
      return this.bitrate;
    }

    public Builder withBitrate(int bitrate) {
      this.bitrate = bitrate;
      return this;
    }

    public int getMaxFrameSize() {
      return this.maxFrameSize;
    }

    public Builder withMaxFrameSize(int maxFrameSize) {
      this.maxFrameSize = maxFrameSize;
      return this;
    }

    public int getMaxPacketSize() {
      return this.maxPacketSize;
    }

    public Builder withMaxPacketSize(int maxPacketSize) {
      this.maxPacketSize = maxPacketSize;
      return this;
    }

    public OpusCodec build() {
      return new OpusCodec(OpusCodecOptions.of(frameSize, sampleRate, channels, bitrate, maxFrameSize, maxPacketSize));
    }
  }

  private static String getNativeLibraryName() {
    String bitnessArch = System.getProperty("os.arch").toLowerCase();
    String bitnessDataModel = System.getProperty("sun.arch.data.model", null);
    if(bitnessDataModel != null) {
      bitnessArch = bitnessDataModel.toLowerCase();
    }

    boolean is64bit = bitnessArch.contains("64");
    if(is64bit) {
      String library64 = processLibraryName("opus-jni-native-64");
      if(hasResource("/native-binaries/" + library64)) {
        return library64;
      }
    } else {
      String library32 = processLibraryName("opus-jni-native-32");
      if(hasResource("/native-binaries/" + library32)) {
        return library32;
      }
    }

    String library = processLibraryName("opus-jni-native");
    if(!hasResource("/native-binaries/" + library)) {
      throw new NoSuchElementException("No binary for the current system found, even after trying bit neutral names");
    } else {
      return library;
    }
  }

  private static String processLibraryName(String library) {
    String systemName = System.getProperty("os.name", "bare-metal?").toLowerCase();

    if (systemName.contains("nux") || systemName.contains("nix")) {
      return "lib" + library + ".so";
    } else if (systemName.contains("mac")) {
      return "lib" + library + ".dylib";
    } else if (systemName.contains("windows")) {
      return library + ".dll";
    } else {
      throw new NoSuchElementException("No native library for system " + systemName);
    }
  }

  private static boolean hasResource(String resource) {
    return OpusCodec.class.getResource(resource) != null;
  }

  public static void extractNatives(File directory) throws IOException {
    String nativeLibraryName = getNativeLibraryName();
    Files.copy(OpusCodec.class.getResourceAsStream("/native-binaries/" + nativeLibraryName),
            directory.toPath().resolve(nativeLibraryName), StandardCopyOption.REPLACE_EXISTING);
  }

  public static void loadNative(File directory) {
    System.load(new File(directory, getNativeLibraryName()).getAbsolutePath());
  }

  /**
   * Extract the native library and load it
   *
   * @throws IOException          In case an error occurs while extracting the native library
   * @throws UnsatisfiedLinkError In case the native libraries fail to load
   */
  public static void setupWithTemporaryFolder() throws IOException {
    File temporaryDir = Files.createTempDirectory("opus-jni").toFile();
    temporaryDir.deleteOnExit();
    extractNatives(temporaryDir);
    loadNative(temporaryDir);
  }
}
