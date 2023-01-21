package dev.vvasiliev.view.composable.modular.music.mapper

import kotlin.time.Duration.Companion.milliseconds

class CardElementsMapper constructor(
    private val progressString: String,
    private val timeString: String
) {

    fun mapTimeString(time: Long) =
        time.milliseconds.toComponents { minutes, seconds, _ ->
            String.format(
                timeString,
                minutes,
                if (seconds < 10) "0$seconds" else seconds
            )
        }


    fun mapTimeStringWithProgress(time: Long, progress: Float) =
        time.milliseconds.toComponents { minutes, seconds, _ ->
            val progressMs = (progress * time).toLong().milliseconds
            progressMs.toComponents { pminutes, pseconds, _ ->
                String.format(
                    progressString,
                    pminutes,
                    if (pseconds < 10) "0$pseconds" else pseconds,
                    minutes,
                    if (seconds < 10) "0$seconds" else seconds
                )
            }
        }

}