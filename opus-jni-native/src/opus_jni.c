/*
opus-jni, a simple Opus JNI Wrapper.
Copyright (C) 2020 LabyMedia GmbH This program is free software:
you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed
in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy
of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

#include <jni.h>
#include <opus.h>
#include <opus_types.h>
#include <stdio.h>
#include <stdlib.h>

#include "net_labymod_opus_OpusCodec.h"

#define APPLICATION OPUS_APPLICATION_AUDIO

typedef struct _OpusCodecOptions {
    int frameSize;
    int sampleRate;
    int channels;
    int bitrate;
    int maxFrameSize;
    int maxPacketSize;
} OpusCodecOptions;

typedef struct _OpusEncodeInfo {
    OpusEncoder *encoder;
    OpusCodecOptions opts;
} OpusEncodeInfo;

typedef struct _OpusDecodeInfo {
    OpusDecoder *decoder;
    OpusCodecOptions opts;
} OpusDecodeInfo;

jbyteArray as_byte_array(JNIEnv *env, unsigned char *buf, int len) {
    jbyteArray array = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, array, 0, len, (jbyte *) (buf));
    return array;
}

unsigned char *as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = (*env)->GetArrayLength(env, array);
    unsigned char *buf = (unsigned char *) malloc(len * sizeof(unsigned char));
    (*env)->GetByteArrayRegion(env, array, 0, len, (jbyte *) (buf));
    return buf;
}

OpusCodecOptions readOpusCodecOptions(JNIEnv *env, jobject obj) {
    OpusCodecOptions opts;

    jclass clsOpusCodecOptions;

    jfieldID fFrameSize;
    jfieldID fSampleRate;
    jfieldID fChannels;
    jfieldID fBitrate;
    jfieldID fMaxFrameSize;
    jfieldID fMaxPacketSize;

    clsOpusCodecOptions = (*env)->GetObjectClass(env, obj);

    fFrameSize = (*env)->GetFieldID(env, clsOpusCodecOptions, "frameSize", "I");
    fSampleRate = (*env)->GetFieldID(env, clsOpusCodecOptions, "sampleRate", "I");
    fChannels = (*env)->GetFieldID(env, clsOpusCodecOptions, "channels", "I");
    fBitrate = (*env)->GetFieldID(env, clsOpusCodecOptions, "bitrate", "I");
    fMaxFrameSize = (*env)->GetFieldID(env, clsOpusCodecOptions, "maxFrameSize", "I");
    fMaxPacketSize = (*env)->GetFieldID(env, clsOpusCodecOptions, "maxPacketSize", "I");

    opts.frameSize = (*env)->GetIntField(env, obj, fFrameSize);
    opts.sampleRate = (*env)->GetIntField(env, obj, fSampleRate);
    opts.channels = (*env)->GetIntField(env, obj, fChannels);
    opts.bitrate = (*env)->GetIntField(env, obj, fBitrate);
    opts.maxFrameSize = (*env)->GetIntField(env, obj, fMaxFrameSize);
    opts.maxPacketSize = (*env)->GetIntField(env, obj, fMaxPacketSize);

    return opts;
}

JNIEXPORT jlong JNICALL Java_net_labymod_opus_OpusCodec_createEncoder(JNIEnv *env, jobject inst, jobject obj) {
    OpusEncoder *encoder;
    OpusCodecOptions opts;
    OpusEncodeInfo *info = (OpusEncodeInfo *) malloc(sizeof(OpusEncodeInfo));
    int err;

    opts = readOpusCodecOptions(env, obj);
    encoder = opus_encoder_create((opus_int32) opts.sampleRate, opts.channels, APPLICATION, &err);
    if(err < 0) {
        fprintf(stderr, "failed to create encoder: %s\n", opus_strerror(err));
        free(info);
        return -1;
    }

    err = opus_encoder_ctl(encoder, OPUS_SET_BITRATE(opts.bitrate));
    if(err < 0) {
        fprintf(stderr, "failed to set bitrate: %s\n", opus_strerror(err));
        opus_encoder_destroy(encoder);
        free(info);
        return -1;
    }

    info->encoder = encoder;
    info->opts = opts;

    return (jlong) info;
}

JNIEXPORT jlong JNICALL Java_net_labymod_opus_OpusCodec_createDecoder(JNIEnv *env, jobject inst, jobject obj) {
    OpusDecoder *decoder;
    OpusCodecOptions opts;
    OpusDecodeInfo *info = (OpusDecodeInfo *) malloc(sizeof(OpusDecodeInfo));
    int err;

    opts = readOpusCodecOptions(env, obj);
    decoder = opus_decoder_create(opts.sampleRate, opts.channels, &err);
    if(err < 0) {
        fprintf(stderr, "failed to create decoder: %s\n", opus_strerror(err));
        free(info);
        return -1;
    }

    info->decoder = decoder;
    info->opts = opts;

    return (jlong) info;
}

JNIEXPORT jbyteArray JNICALL Java_net_labymod_opus_OpusCodec_encodeFrame(
    JNIEnv *env, jobject inst, jlong pointer, jbyteArray in_buff, jint in_buff_offset, jint in_buff_length) {
    OpusEncodeInfo *info = (OpusEncodeInfo *) pointer;
    int i, nbBytes;
    jbyteArray out;
    unsigned char *pcm_bytes = as_unsigned_char_array(env, in_buff);
    unsigned char *cbits = (unsigned char *) malloc(info->opts.maxPacketSize);
    opus_int16 *in = malloc(sizeof(opus_int16) * info->opts.frameSize * info->opts.channels * 2);

    for(i = 0; i < in_buff_length / 2; i++) {
        in[i] = pcm_bytes[in_buff_offset + 2 * i + 1] << 8 | pcm_bytes[in_buff_offset + 2 * i];
    }
    nbBytes = opus_encode(info->encoder, in, info->opts.frameSize, cbits, info->opts.maxPacketSize);
    if(nbBytes < 0) {
        free(in);
        free(cbits);
        free(pcm_bytes);
        return (*env)->NewByteArray(env, 0);
    }
    out = as_byte_array(env, cbits, nbBytes);

    free(in);
    free(cbits);
    free(pcm_bytes);

    return out;
}

JNIEXPORT jbyteArray JNICALL Java_net_labymod_opus_OpusCodec_decodeFrame(JNIEnv *env,
                                                                         jobject inst,
                                                                         jlong pointer,
                                                                         jbyteArray in_buff) {
    OpusDecodeInfo *info = (OpusDecodeInfo *) pointer;
    int i, frame_size, len, expected_samples, out_len;
    jbyteArray out_buff;
    unsigned char *pcm_bytes;
    unsigned char *cbits = as_unsigned_char_array(env, in_buff);
    opus_int16 *out = malloc(sizeof(opus_int16) * info->opts.maxFrameSize * info->opts.channels);

    /* Expected output size is always channels * frameSize * 2 bytes */
    expected_samples = info->opts.channels * info->opts.frameSize;
    out_len = expected_samples * 2;

    len = (*env)->GetArrayLength(env, in_buff);
    frame_size = opus_decode(info->decoder, cbits, len, out, info->opts.maxFrameSize, 0);
    if(frame_size < 0) {
        /* Return a silent frame of the expected size instead of an empty array
         * to prevent downstream crashes from undersized buffers */
        free(out);
        free(cbits);
        pcm_bytes = (unsigned char *) calloc(out_len, sizeof(unsigned char));
        out_buff = as_byte_array(env, pcm_bytes, out_len);
        free(pcm_bytes);
        return out_buff;
    }

    pcm_bytes = (unsigned char *) calloc(out_len, sizeof(unsigned char));

    /* Only copy as many samples as were actually decoded, up to the expected amount.
     * If frame_size < frameSize, the remaining bytes stay zero (silence).
     * If frame_size > frameSize, we clamp to avoid buffer overflow. */
    int samples_to_copy = info->opts.channels * frame_size;
    if(samples_to_copy > expected_samples) {
        samples_to_copy = expected_samples;
    }

    for(i = 0; i < samples_to_copy; i++) {
        pcm_bytes[2 * i] = out[i] & 0xFF;
        pcm_bytes[2 * i + 1] = (out[i] >> 8) & 0xFF;
    }

    out_buff = as_byte_array(env, pcm_bytes, out_len);
    free(out);
    free(pcm_bytes);
    free(cbits);
    return out_buff;
}

JNIEXPORT void JNICALL Java_net_labymod_opus_OpusCodec_destroyEncoder(JNIEnv *env, jobject inst, jlong pointer) {
    OpusEncodeInfo *info = (OpusEncodeInfo *) pointer;
    opus_encoder_destroy(info->encoder);
    free(info);
}

JNIEXPORT void JNICALL Java_net_labymod_opus_OpusCodec_destroyDecoder(JNIEnv *env, jobject inst, jlong pointer) {
    OpusDecodeInfo *info = (OpusDecodeInfo *) pointer;
    opus_decoder_destroy(info->decoder);
    free(info);
}
