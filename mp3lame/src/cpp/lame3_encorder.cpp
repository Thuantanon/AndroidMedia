//
// Created by 蔡锡华 on 2020-12-12.
//


#include "lame3_encorder.h"
#include "stdio.h"


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

    // start
    int read = 0;
    int write = 0;
    int total = 0;
    const int buffer_size = 8 * 1024;
    short wavBuffer[buffer_size];
    unsigned char mp3Buffer[buffer_size];

    do
    {
        read = static_cast<int>(fread(wavBuffer, sizeof(short int), buffer_size, pcmFile));
        total += read * sizeof(short int);
        if (read != 0)
        {
            write = lame_encode_buffer(lame_instance, wavBuffer, nullptr, read, mp3Buffer, buffer_size);
        } else
        {
            write = lame_encode_flush(lame_instance, mp3Buffer, buffer_size);
        }

        fwrite(mp3Buffer, 1, static_cast<size_t>(write), mp3OutputFile);

    } while (read != 0);

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