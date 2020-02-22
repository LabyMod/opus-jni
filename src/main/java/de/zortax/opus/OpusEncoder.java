package de.zortax.opus;

public class OpusEncoder {

    private final OpusOptions options;
    private final long handle;
    private OpusHandler handler;
    private boolean destroyed;

    private OpusEncoder(OpusOptions options) {
        this.options = options;
        this.handler = new OpusHandler();
        this.handle = handler.createEncoder(options);
        this.destroyed = false;
    }

    public static OpusEncoder create(OpusOptions options) {
        return new OpusEncoder(options);
    }

    public byte[] encodeFrame(byte[] frame) {
        if (destroyed)
            throw new IllegalStateException("Encoder already destroyed!");
        return handler.encodeFrame(handle, frame);
    }

    public void destroy() {
        if (destroyed)
            throw new IllegalStateException("Encoder already destroyed!");
        handler.destroyEncoder(handle);
    }


}
