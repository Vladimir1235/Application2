// AudioEventListener.aidl
package dev.vvasiliev.audio;

// Declare any non-default types here with import statements

interface AudioEventListener {
    void onPositionChange(long position);
    void onPlaybackStopped();
}