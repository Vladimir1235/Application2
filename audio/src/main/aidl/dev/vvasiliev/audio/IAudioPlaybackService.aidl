// IAudioPlaybackService.aidl
package dev.vvasiliev.audio;

import dev.vvasiliev.audio.AudioServiceState;
import dev.vvasiliev.audio.AudioEventListener;
import dev.vvasiliev.audio.SongMetadata;
import dev.vvasiliev.audio.AudioServiceStateListener;

// Declare any non-default types here with import statements
interface IAudioPlaybackService {
    void play(in SongMetadata metadata, long startPosition);
    AudioServiceState getState();
    void seekTo(long position);
    void stopCurrent();
    long getCurrentSongId();

    /**
    * register audio event listener
    **/
    void registerAudioEventListener(AudioEventListener listener);
    void unregisterAudioEventListener(AudioEventListener listener);
    /**
    * registers new listener to observe global service state
    **/
    void registerStateListener(in AudioServiceStateListener listener);
    void unregisterStateListener();
}