// IAudioPlaybackService.aidl
package dev.vvasiliev.audio;

import dev.vvasiliev.audio.AudioServiceState;

// Declare any non-default types here with import statements
interface IAudioPlaybackService {
    void play(in Uri uri);
    AudioServiceState getState();
    void stopCurrent();
    void resumeCurrent();
}