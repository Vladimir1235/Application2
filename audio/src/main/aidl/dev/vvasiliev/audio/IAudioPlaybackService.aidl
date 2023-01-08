// IAudioPlaybackService.aidl
package dev.vvasiliev.audio;

import dev.vvasiliev.audio.AudioServiceState;
import dev.vvasiliev.audio.AudioEventListener;

// Declare any non-default types here with import statements
interface IAudioPlaybackService {
    void play(in Uri uri, in long id, AudioEventListener listener, long startPosition);
    AudioServiceState getState();
    void seekTo(long position);
    void stopCurrent();
    long getCurrentSongId();
}