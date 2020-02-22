package de.zortax.opus;

import java.io.*;

public class Demo {

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Syntax: <input.pcm> <output.pcm>");
    }

    OpusOptions opts = OpusOptions.builder().build();
    OpusEncoder encoder = OpusEncoder.create(opts);
    OpusDecoder decoder = OpusDecoder.create(opts);


    try {
      InputStream inStream = new FileInputStream(args[0]);
      OutputStream outputStream = new FileOutputStream(args[1]);

      byte[] buffer = new byte[opts.getFrameSize() * opts.getChannels() * 2 ];

      while (inStream.read(buffer) != -1) {
        System.out.println("Encoding frame...");
        byte[] encoded = encoder.encodeFrame(buffer);
        System.out.println("Decoding frame...");
        byte[] decoded = decoder.decodeFrame(encoded);
        outputStream.write(decoded);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


    encoder.destroy();
    decoder.destroy();
  }
}
