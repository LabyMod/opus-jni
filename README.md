# opus-jni

opus-jni is a very simple and comfortable to use JNI wrapper for the 
[Opus codec](https://opus-codec.org/), created for [LabyMod](https://github.com/LabyMod).
It might lack a few functions of the original Opus codec specifications but should be perfectly
fine for most usecases.

[![CI](https://github.com/LabyMod/opus-jni/workflows/CI/badge.svg)](https://github.com/LabyMod/opus-jni/actions?query=workflow%3ACI)

See [here](./opus-jni-java/src/test/java/net/labymod/opus/MinimalEchoExample.java) for a very
simple and complete echo example.
## How to use:
### Cloning
This repository uses submodules, so you need to clone with `--recurse-submodules` or after
cloning run `git submodule update --init`

### Building
Building is slightly more complex than a normal Java project and only required if you want
to change the way the library works.

#### Building for your platform (development)
Install [CMake](https://cmake.org/download/) and make sure it is on the path. You will also need
a compiler (not a Java compiler, but a C one). For windows you should install Visual Studio. 
For Linux and OSX choose your favorite method of installing the compiler (gcc and clang should 
both work).

After this is done, the project might be used like every other gradle project. Note that the
resulting jar will be in `opus-jni-java/build/libs` and **will just work on the platform it
was built on!**. For instructions on how to build for all target platforms, see below.

#### Building for all platforms (production)
You will need a machine for every platform you want to build on. This means, if you want to
produce a jar which works on Linux, Windows and OSX, you will need an installation of each of
the operating systems (it might be possible to just use Linux and cross compile, but that is
not officially supported by us).

All 3 machines will need CMake and a compiler installed (refer to
[Building for your platform](#building-for-your-platform-development)). Then you need to run
the gradle task `opus-jni-native:build` on each machine, which will result in the following
binaries in `build/nativeBinaries`:
- `libopus-jni-native.dylib` (OSX)
- `libopus-jni-native.so` (Linux)
- `opus-jni-native.dll` (Windows)

Copy all of those binaries to one machine to one directory. On that machine invoke
`gradlew -PnativeBinaryExternalDir=/path/to/your/directory opus-jni-java:build`, where
`/path/to/your/directory` is the directory containing all 3 of the binaries above.
The final jar in `opus-jni-java/build/libs` will support all platforms you have built the 
binaries for. You may leave the ones out you don't need to support.

For further details, refer to the [Github workflow](./.github/workflows/ci.yml).

### Important to know:
- Every audio stream requires its own codec instance, because Opus is not stateless.
- Only the following sample rates are supported: 8khz, 12khz, 16khz, 24khz, 48khz
- Recommended bitrates for your purpose found 
  [here](https://wiki.xiph.org/index.php?title=Opus_Recommended_Settings&mobileaction=toggle_view_desktop)
- The default codec settings should be fine for every simple VoIP communication
- Unused instances of OpusCodec must be removed to prevent memory leaks with ```OpusCodec#destroy```. This might not be critical when using only a fixed amount of instances, but can get dangerous when those instances have a life cycle and might be recreated once in a while.

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
