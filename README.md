# opus-jni

This is a very simple and comfortable to use JNI wrapper for the Opus codec,
created by [Jan Brachthäuser](https://github.com/jan-br) and [Leonard Seibold](https://github.com/zortax) for [LabyMod](https://github.com/LabyMod)
It might lack a few functions of the original Opus codec specifications but should be perfectly fine for most usecases.

See [here](./src/examples/java/net/labymod/opus/MinimalEchoExample.java) for a very simple and complete echo example.
## How to use:

### Important to know:
- Every audio stream requires its own codec instance, because Opus is not stateless.
- Only the following sample rates are supported: 8khz, 12khz, 16khz, 24khz, 48khz
- Recommended bitrates for your purpose found [here](https://wiki.xiph.org/index.php?title=Opus_Recommended_Settings&mobileaction=toggle_view_desktop)
- The default codec settings should be fine for every simple VoIP communication

#### Native libraries:
The native libraries _**CAN**_ be loaded with :
```java
OpusCodec.loadNativesFromJar()
```

However this is not recommended, because it uses a hack to modify `java.library.path`, so it might break in some JVMs.
So if you want to use this little comfortable native library initialization method, be sure to know which JVM you are using.

If you want to get sure, place the [native libraries](./src/main/resources/native) in the path before launching the JVM. (Download link following)

#### Create a codec instance:
###### Default settings
```java
OpusCodec codec = OpusCodec.createDefault();
```

###### Custom settings
```java
OpusCodec codec = OpusCodec.newBuilder()
  .withSampleRate(24000)
  .withChannels(2)
  .withBitrate(128000)
  ...
  .build();
```

###### encode/decode
Encoding/Decoding an audio chunk is very straight forward.
```java
byte[] data = new byte[codec.getFrameSize() * codec.getChannels() * 2];
... //Fill data
byte[] encoded = codec.encodeFrame(data);
byte[] decode = codec.decodeFrame(encoded);
```
