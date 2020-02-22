package de.zortax.opus;

import java.nio.ByteBuffer;

public class OpusHandler {

  static {
    System.loadLibrary("opus_jni");
    System.load("/usr/lib/libopus.so");
  }

  public native long createEncoder(OpusOptions opts);

  public native long createDecoder(OpusOptions opts);

  public native void destroyEncoder(long encoder);

  public native void destroyDecoder(long decoder);

  public native byte[] encodeFrame(long encoder, byte[] in);

  public native byte[] decodeFrame(long decoder, byte[] out);

}
