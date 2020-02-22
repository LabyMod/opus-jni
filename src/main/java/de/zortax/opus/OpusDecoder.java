package de.zortax.opus;

public class OpusDecoder {

  private final OpusOptions options;
  private final long handle;
  private OpusHandler handler;
  private boolean destroyed;

  private OpusDecoder(OpusOptions options) {
    this.options = options;
    this.handler = new OpusHandler();
    this.handle = handler.createDecoder(options);
    this.destroyed = false;
  }

  public static OpusDecoder create(OpusOptions options) {
    return new OpusDecoder(options);
  }

  public byte[] decodeFrame(byte[] frame) {
    if (destroyed) throw new IllegalStateException("Decoder already destroyed!");
    return handler.decodeFrame(handle, frame);
  }

  public OpusOptions getOptions() {
    return options;
  }

  public void destroy() {
    if (destroyed) throw new IllegalStateException("Decoder already destroyed!");
    destroyed = true;
    handler.destroyDecoder(handle);
  }
}
