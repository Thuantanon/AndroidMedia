//
// Created by 蔡锡华 on 2020-12-12.
//

#ifndef ANDROIDMEDIA_LAME3_ENCORDER_H
#define ANDROIDMEDIA_LAME3_ENCORDER_H

#include "lame/lame.h"

static lame_t lame_instance = nullptr;

void init_encoder(int sampleRate, int channels, int brate, int quality);

bool encode(const char *pcmFilePath, const char *mp3FilePath);

void release_encoder();


#endif //ANDROIDMEDIA_LAME3_ENCORDER_H
