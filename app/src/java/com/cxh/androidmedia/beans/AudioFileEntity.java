package com.cxh.androidmedia.beans;

/**
 * Created by Cxh
 * Time : 2018-09-26  10:26
 * Desc :
 */
public class AudioFileEntity {

    public static final int AUDIO_TYPE_PCM = 0;
    public static final int AUDIO_TYPE_WAV = 1;

    private String audioAbsolutePath;
    private String audioFileName;
    private int audioType;

    public AudioFileEntity(String audioAbsolutePath, String audioFileName) {
        this(audioAbsolutePath, audioFileName, AUDIO_TYPE_PCM);
    }

    public AudioFileEntity(String audioAbsolutePath, String audioFileName, int type) {
        this.audioAbsolutePath = audioAbsolutePath;
        this.audioFileName = audioFileName;
        this.audioType = type;
    }

    public int getAudioType() {
        return audioType;
    }

    public String getAudioAbsolutePath() {
        return audioAbsolutePath;
    }

    public void setAudioAbsolutePath(String audioAbsolutePath) {
        this.audioAbsolutePath = audioAbsolutePath;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }
}
