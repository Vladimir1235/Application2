// AudioServiceStateListener.aidl
package dev.vvasiliev.audio;
import dev.vvasiliev.audio.AudioServiceState;
// Declare any non-default types here with import statements

interface AudioServiceStateListener {
    void onAudioServiceStateChanged(in AudioServiceState state);
    void onPlaybackStarted(in long songId);
    void onPlaybackStopped(in long songId);
}