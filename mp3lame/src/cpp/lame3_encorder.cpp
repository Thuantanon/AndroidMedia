//
// Created by 蔡锡华 on 2020-12-12.
//


#include "lame3_encorder.h"
#include "android/log.h"

void init_encoder(int sampleRate, int channels, int brate, int quality)
{
    if (nullptr != lame_instance)
    {
        lame_close(lame_instance);
        lame_instance = nullptr;
    }
    // 初始化
    lame_instance = lame_init();
    // 设置采样率(输出默认)
    lame_set_in_samplerate(lame_instance, sampleRate);
    // 声道数
    lame_set_num_channels(lame_instance, channels);
    // 位宽（16）
    lame_set_brate(lame_instance, brate);
    // 声音质量
    lame_set_quality(lame_instance, quality);

    // VBR
    lame_set_VBR(lame_instance, vbr_default);

    // 初始化
    lame_init_params(lame_instance);
}

bool encode(const char *pcmFilePath, const char *mp3FilePath)
{
    if (nullptr == lame_instance)
    {
        return false;
    }

    FILE *pcmFile = fopen(pcmFilePath, "r");
    FILE *mp3OutputFile = fopen(mp3FilePath, "w");

    if (!pcmFile || !mp3OutputFile)
    {
        return false;
    }

    int channelCount = lame_get_num_channels(lame_instance);
    if (channelCount > 2)
    {
        fclose(pcmFile);
        fclose(mp3OutputFile);
        return false;
    }

    // start
    int readSize;
    int writeSize;
    int total = 0;
    const int buffer_size = 8 * 1024;
    const int pcmShortSize = buffer_size / 2;
    short pcmBuffer[pcmShortSize];
    short pcmBufferL[pcmShortSize / 2];
    short pcmBufferR[pcmShortSize / 2];
    unsigned char mp3Buffer[buffer_size];

    do
    {
        readSize = static_cast<int>(fread(pcmBuffer, sizeof(short int), buffer_size / 2, pcmFile));
        total += readSize * sizeof(short int);

        if (channelCount == 1)
        {
            // 单声道
            if (readSize > 0)
            {
                writeSize = lame_encode_buffer(lame_instance, pcmBuffer, nullptr, readSize, mp3Buffer, buffer_size);
            } else
            {
                writeSize = lame_encode_flush(lame_instance, mp3Buffer, buffer_size);
            }
        } else if (channelCount == 2)
        {
            // 交换左右声道
            for (int i = 0; i < readSize; i++)
            {
                if (i % 2 == 0)
                {
                    pcmBufferL[i / 2] = pcmBuffer[i];
                } else
                {
                    pcmBufferR[i / 2] = pcmBuffer[i];
                }
            }

            if (readSize > 0)
            {
                writeSize = lame_encode_buffer(lame_instance, pcmBufferL, pcmBufferR, readSize / 2, mp3Buffer, buffer_size);
            } else
            {
                writeSize = lame_encode_flush(lame_instance, mp3Buffer, buffer_size);
            }
        }

        fwrite(mp3Buffer, 1, static_cast<size_t>(writeSize), mp3OutputFile);

    } while (readSize != 0);

    // 写入Xing VBR/INFO标签
    lame_mp3_tags_fid(lame_instance, mp3OutputFile);

    fclose(pcmFile);
    fclose(mp3OutputFile);

    return true;
}

void release_encoder()
{
    if (nullptr != lame_instance)
    {
        lame_close(lame_instance);
    }
}