package com.cxh.androidmedia.manager;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.cxh.androidmedia.utils.CCLog;
import com.cxh.androidmedia.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Cxh
 * Time : 2021/7/21  00:39
 * Desc :
 */
public class VideoParseManager {


    public static void parseAndMuxer(Context context, Uri videoUri, String rootPath) {
        CCLog.i("parseAndMuxer, videoUri: " + videoUri + " , rootPath: " + rootPath);

        MediaExtractor extractor = null;
        FileOutputStream outputStreamH264 = null;
        FileOutputStream outputStreamAAC = null;
        MediaMuxer mediaMuxer = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(context, videoUri, null);
            String fileName = FileUtil.getTimeFormat();

            // 视频混合
            String newVideoName = rootPath + File.separator + fileName + ".mp4";
            mediaMuxer = new MediaMuxer(newVideoName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;

            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

                ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
                extractor.selectTrack(i);
                if (MediaFormat.MIMETYPE_VIDEO_AVC.equals(mimeType)) {
                    String videoFileName = rootPath + File.separator + fileName + ".h264";
                    outputStreamH264 = new FileOutputStream(videoFileName);
                    videoTrackIndex = mediaMuxer.addTrack(mediaFormat);

                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        byte[] buffer = new byte[readByteCount];
                        byteBuffer.get(buffer);
                        outputStreamH264.write(buffer);
                        byteBuffer.clear();
                        extractor.advance();
                    }

                } else if (MediaFormat.MIMETYPE_AUDIO_AAC.equals(mimeType)) {
                    String videoFileName = rootPath + File.separator + fileName + ".aac";
                    outputStreamAAC = new FileOutputStream(videoFileName);
                    audioTrackIndex = mediaMuxer.addTrack(mediaFormat);

                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        byte[] buffer = new byte[readByteCount];
                        byteBuffer.get(buffer);
                        outputStreamAAC.write(buffer);
                        byteBuffer.clear();
                        extractor.advance();
                    }
                }

                extractor.unselectTrack(i);
            }

            CCLog.i("videoTrack: " + videoTrackIndex + " , audioTrack: " + audioTrackIndex);
            extractor.release();
            extractor = new MediaExtractor();
            extractor.setDataSource(context, videoUri, null);

            // 为了方便，直接在这里重新合成
            mediaMuxer.start();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
                extractor.selectTrack(i);

                CCLog.i("startMuxer, maxInputSize: " + maxInputSize);

                if (MediaFormat.MIMETYPE_VIDEO_AVC.equals(mimeType)) {
                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        bufferInfo.size = readByteCount;
                        bufferInfo.offset = 0;
                        bufferInfo.flags = extractor.getSampleFlags();
                        bufferInfo.presentationTimeUs = extractor.getSampleTime();
                        mediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, bufferInfo);

                        byteBuffer.clear();
                        extractor.advance();
                    }
                } else if (MediaFormat.MIMETYPE_AUDIO_AAC.equals(mimeType)) {
                    while (true) {
                        int readByteCount = extractor.readSampleData(byteBuffer, 0);
                        if (readByteCount <= 0) {
                            break;
                        }

                        bufferInfo.size = readByteCount;
                        bufferInfo.offset = 0;
                        bufferInfo.flags = extractor.getSampleFlags();
                        bufferInfo.presentationTimeUs = extractor.getSampleTime();
                        mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, bufferInfo);

                        byteBuffer.clear();
                        extractor.advance();
                    }
                }
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;
        } catch (IOException e) {
            e.printStackTrace();
            CCLog.i("parseVideoInfo, e:" + e.toString());
        } finally {
            FileUtil.tryClose(outputStreamH264);
            FileUtil.tryClose(outputStreamAAC);

            if (null != extractor) {
                extractor.release();
            }

            if (null != mediaMuxer) {
                mediaMuxer.stop();
                mediaMuxer.release();
                mediaMuxer = null;
            }
        }
    }

    public static int selectTrackFromMediaExtractor(MediaExtractor extractor, String type) {
        int track = -1;

        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (!TextUtils.isEmpty(mimeType) && mimeType.startsWith(type)) {
                track = i;
                break;
            }
        }
        return track;
    }

    public static MediaFormat parseAudioFromVideoUri(Context context, Uri videoUri, String pcmFile) {
        CCLog.i("getAudioFromVideoUri, videoUri: " + videoUri + " , pcmFile: " + pcmFile);

        if (null == videoUri || TextUtils.isEmpty(pcmFile)) {
            return null;
        }

        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(context, videoUri, null);
        } catch (IOException e) {
            e.printStackTrace();
            CCLog.e("getAudioFromVideoUri, e:" + e.toString());

            mediaExtractor.release();
            mediaExtractor = null;
        }

        if (null == mediaExtractor) {
            CCLog.e("getAudioFromVideoUri, mediaExtractor is null!");
            return null;
        }

        int audioTrack = selectTrackFromMediaExtractor(mediaExtractor, "audio/");
        MediaFormat mediaFormat = mediaExtractor.getTrackFormat(audioTrack);
        String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
        int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

        CCLog.i("parseAudioFromVideoUri, audioTrack: " + audioTrack + " , mimeType: " + mimeType);

        MediaCodec mediaCodec = null;
        FileChannel fileOutputStream = null;
        try {
            mediaCodec = MediaCodec.createDecoderByType(mimeType);
            mediaCodec.configure(mediaFormat, null, null, 0);
            mediaCodec.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaExtractor.selectTrack(audioTrack);
            fileOutputStream = new FileOutputStream(pcmFile).getChannel();

            boolean isDecodeFinish = false;
            while (!isDecodeFinish) {
                int inIndex = mediaCodec.dequeueInputBuffer(50 * 1000);
                if (inIndex >= 0) {
                    int sampleSize = mediaExtractor.readSampleData(byteBuffer, 0);

                    if (sampleSize <= 0) {
                        CCLog.i("getAudioFromVideoUri, dequeueInputBuffer EOF!");
                        mediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inIndex);
                        inputBuffer.put(byteBuffer);
                        mediaCodec.queueInputBuffer(inIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                        mediaExtractor.advance();
                    }
                    byteBuffer.clear();

                    boolean isOutputFinish = false;
                    while (!isOutputFinish) {
                        int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                        switch (outIndex) {
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                CCLog.i("getAudioFromVideoUri, dequeueOutputBuffer INFO_TRY_AGAIN_LATER!");
                                break;
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                CCLog.i("getAudioFromVideoUri, dequeueOutputBuffer INFO_OUTPUT_FORMAT_CHANGED!");
                                break;
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                CCLog.i("getAudioFromVideoUri, dequeueOutputBuffer INFO_OUTPUT_BUFFERS_CHANGED!");
                                break;
                            default:
                                break;
                        }

                        if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                            isOutputFinish = true;
                        } else if (outIndex >= 0) {
                            if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                                CCLog.i("getAudioFromVideoUri, dequeueOutputBuffer BUFFER_FLAG_END_OF_STREAM!");
                                isDecodeFinish = true;
                            } else {
                                ByteBuffer outBuffer = mediaCodec.getOutputBuffer(outIndex);
                                if (null != outBuffer) {
                                    fileOutputStream.write(outBuffer);
                                    outBuffer.clear();
                                }

                                mediaCodec.releaseOutputBuffer(outIndex, false);
                            }
                        }
                    }
                } else {
                    CCLog.i("getAudioFromVideoUri, invalid inIndex!");
                    isDecodeFinish = true;
                }
            }

            mediaExtractor.release();
            mediaExtractor = null;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != mediaCodec) {
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
            }

            FileUtil.tryClose(fileOutputStream);
        }

        return mediaFormat;
    }

    public static void playVideoFromFile(Context context, Uri videoUri,
                                         final Surface previewSurface,
                                         Handler decodeHandler,
                                         DecodingProvider provider) {
        CCLog.i("playVideoFromFile, videoUri: " + videoUri);
        if (null == videoUri || null == provider) {
            return;
        }

        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(context, videoUri, null);
        } catch (IOException e) {
            e.printStackTrace();
            CCLog.e("playVideoFromFile, e: " + e.toString());

            mediaExtractor.release();
        }

        int videoTrack = selectTrackFromMediaExtractor(mediaExtractor, "video/");
        MediaFormat mediaFormat = mediaExtractor.getTrackFormat(videoTrack);
        String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);

        try {
            mediaExtractor.selectTrack(videoTrack);
            final MediaCodec mediaCodec = MediaCodec.createDecoderByType(mimeType);
            mediaCodec.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    CCLog.i("playVideoFromFile, onInputBufferAvailable, index: " + index);

                    ByteBuffer byteBuffer = codec.getInputBuffer(index);
                    if (null == byteBuffer) {
                        return;
                    }

                    int size = mediaExtractor.readSampleData(byteBuffer, 0);
                    if (size <= 0 || provider.needPrevent()) {
                        codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        codec.queueInputBuffer(index, 0, size, mediaExtractor.getSampleTime(), mediaExtractor.getSampleFlags());
                        mediaExtractor.advance();
                    }

                    CCLog.i("playVideoFromFile, onInputBufferAvailable X, queue size: " + size);
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    ByteBuffer byteBuffer = codec.getOutputBuffer(index);
                    switch (info.flags) {
                        case MediaCodec.BUFFER_FLAG_KEY_FRAME:
                            CCLog.i("playVideoFromFile, onOutputBufferAvailable, BUFFER_FLAG_KEY_FRAME!");
                            break;
                        case MediaCodec.BUFFER_FLAG_PARTIAL_FRAME:
                            CCLog.i("playVideoFromFile, onOutputBufferAvailable, BUFFER_FLAG_PARTIAL_FRAME!");
                            break;
                        case MediaCodec.BUFFER_FLAG_CODEC_CONFIG:
                            CCLog.i("playVideoFromFile, onOutputBufferAvailable, BUFFER_FLAG_CODEC_CONFIG!");
                            break;
                        case MediaCodec.BUFFER_FLAG_END_OF_STREAM: {
                            CCLog.i("playVideoFromFile, onOutputBufferAvailable, BUFFER_FLAG_END_OF_STREAM!");
                            mediaCodec.stop();
                            mediaCodec.release();
                            mediaExtractor.release();
                        }
                        break;
                        default: {
                            CCLog.i("playVideoFromFile, onOutputBufferAvailable, flag: " + info.flags + " , size: " + info.size);
                            codec.releaseOutputBuffer(index, true);
                            byteBuffer.clear();
                        }
                        break;
                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    CCLog.i("playVideoFromFile, onError, e: " + e.toString());
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    CCLog.i("playVideoFromFile, onOutputFormatChanged, format: " + format);
                }
            }, decodeHandler);

            mediaCodec.configure(mediaFormat, previewSurface, null, 0);
            // 传null取到的byteBuffer就是原始yuv数据
            // mediaCodec.configure(mediaFormat, null, null, 0);
            mediaCodec.start();

        } catch (IOException e) {
            e.printStackTrace();
            CCLog.i("playVideoFromFile, e: " + e.toString());
        } finally {

        }
    }


    public interface DecodingProvider {
        boolean needPrevent();
    }
}
