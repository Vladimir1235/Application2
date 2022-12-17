package dev.vvasiliev.audio.service.util

object AudioUtils {
    private const val ACCURACY_FAULT: Long = 150;

    fun Long.isEnd(duration: Long) =
        this >= duration - ACCURACY_FAULT

}