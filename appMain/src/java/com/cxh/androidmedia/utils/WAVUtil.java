package com.cxh.androidmedia.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Cxh
 * Time : 2018-10-09  16:24
 * Desc : PCM格式文件 和 WAV格式文件的转换
 */
public class WAVUtil {

    /**
     *
     * WAVE文件是以RIFF(Resource Interchange File Format, "资源交互文件格式")格式来组织内部结构的。
     *    RIFF文件结构可以看作是树状结构,其基本构成是称为"块"（Chunk）的单元.
     * WAVE文件是由若干个Chunk组成的。按照在文件中的出现位置包括：RIFF WAVE Chunk, Format Chunk, Fact Chunk(可选), Data Chunk。
     *
     *
     * WAV格式音频文件，在PCM文件的基础上，加了44个字节的文件头，详情见"wav_structure.png"
     * 1、第一部分RIFF：
     * ChunkID         存储了“RIFF”字段，表示这是一个“RIFF”格式的文件。
     * ChunkSize       下个地址开始到文件尾的总字节数(此Chunk的数据大小)
     * Format          存储了“WAVE”字段，表示这是一个wav文件。
     * <p>
     * 2、第二部分fmt：
     * 这部分的内容主要是记录一些关键参数，比如采样率，声道数，量化精度等等。
     * Subchunk1 ID    存储了“fmt ”字段, 最后一位空格。
     *  Subchunk1 Size  存储“fmt”字段的长度, 一般为16，表示fmt Chunk的数据块大小为16字节，即20-35
     *  AudioFormat     存储 量化精度, 1：表示是PCM 编码
     *  Num Channels    存储声道数, 声道数，单声道为1，双声道为2
     *  SampleRate      存储采样率
     *  ByteRate        存储比特率（码率）      SampleRate * NumChannels * BitsPerSample/8
     *  BlockAlign      每次采样的大小 == NumChannels * BitsPerSample/8
     *  BitsPerSample   8 bits = 8, 16 bits = 16, etc.
     * <p>
     * 3、第三部分data ：
     * 主要描述数据块
     * Subchunk2 ID    存储“data”字段
     * Subchunk2Size   记录存储的二进制原始音频数据的长度
     * data            存储二进制原始音频数据
     */
    public static byte[] getWavHeader(long totalDataLen, long longSampleRate, int channels, byte bitWidth) {
        byte[] header = new byte[44];
        long totalAudioLen = totalDataLen + 36;
        long byteRate = longSampleRate * channels * bitWidth / 8;

        // RIFF WAVE Chunk
        // RIFF标记占据四个字节
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // 数据大小表示，由于原始数据为long型，通过四次计算得到长度
        // 注意这里的数据长度是文件头36个字节 + 原始数据长度
        header[4] = (byte) (totalAudioLen & 0xff);
        header[5] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[6] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[7] = (byte) ((totalAudioLen >> 24) & 0xff);
        //WAVE标记占据四个字节
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // FMT Chunk
        // 'fmt '标记符占据四个字节
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小, 10H为PCM编码格式
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // 1表示pcm数据
        header[20] = 1; // format = 1
        header[21] = 0;
        // 通道数
        header[22] = (byte) channels;
        header[23] = 0;
        // 采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        // 音频数据传送速率（码率）, 采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数/8
        header[32] = (byte) (channels * bitWidth / 8);
        header[33] = 0;
        // 位宽
        header[34] = bitWidth;
        header[35] = 0;
        // Data chunk
        header[36] = 'd';//data标记符
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        // 数据长度
        header[40] = (byte) (totalDataLen & 0xff);
        header[41] = (byte) ((totalDataLen >> 8) & 0xff);
        header[42] = (byte) ((totalDataLen >> 16) & 0xff);
        header[43] = (byte) ((totalDataLen >> 24) & 0xff);

        return header;
    }


    public static boolean pcmToWav(File pcmFile, File wavFile, byte[] wavHeader) {
        if (null == pcmFile || null == wavFile || !pcmFile.exists() || !wavFile.getName().endsWith(".wav")) {
            return false;
        }

        boolean result = false;
        FileInputStream is = null;
        FileOutputStream os = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!wavFile.exists()) {
                FileUtil.makeFile(wavFile.getAbsolutePath());
            }
            is = new FileInputStream(pcmFile);
            os = new FileOutputStream(wavFile);
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(os);
            // 头部4个字节
            bos.write(wavHeader, 0, wavHeader.length);

            byte[] buffer = new byte[1024 * 2];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            result = true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            FileUtil.tryClose(bis);
            FileUtil.tryClose(is);
            FileUtil.tryClose(bos);
            FileUtil.tryClose(os);
        }
        return result;
    }

    public static void readWavFile(String filePath){

    }

}
