package net.labymod.opus;

import net.labymod.opus.OpusCodec;

import javax.sound.sampled.*;

public class MinimalEchoExample {
  private static AudioFormat format =
          new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 1, 2, 48000, false);

  public static void main(String[] args) throws LineUnavailableException {
    OpusCodec.loadNativesFromJar();

    OpusCodec codec = OpusCodec.createDefault();
/*
    Could also use the builder to customize the codec.
    OpusCodec.newBuilder()
            .withSampleRate(24000)
            ...
            .build();
*/


//        get default microphone and open it at the selected format
    TargetDataLine microphone = AudioSystem.getTargetDataLine(format);
    microphone.open(microphone.getFormat());
    microphone.start();

//        get default speaker and open it at the selected format
    SourceDataLine speaker = AudioSystem.getSourceDataLine(format);
    speaker.open(microphone.getFormat());
    speaker.start();


    while (true) {
      //Reading microphone data
      byte[] data = new byte[codec.getChannels() * codec.getFrameSize() * 2];
      microphone.read(data, 0, data.length);

      //Encoding PCM data chunk
      byte[] encode = codec.encodeFrame(data);

      //Decoding PCM data chunk
      byte[] decoded = codec.decodeFrame(encode);
      speaker.write(decoded, 0, decoded.length);
    }
  }

}
