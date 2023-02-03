package dev.vvasiliev.application.screen.songs.usecase

import android.database.ContentObserver
import android.net.Uri
import dev.vvasiliev.application.core.di.main.ServiceAssistFactory
import dev.vvasiliev.application.screen.songs.usecase.service.Service
import dev.vvasiliev.application.screen.songs.usecase.storage.*
import dev.vvasiliev.audio.service.util.AudioServiceConnector
import dev.vvasiliev.structures.android.operation.ContentDeletionLauncher
import dev.vvasiliev.view.composable.modular.music.data.MusicCardData
import kotlinx.coroutines.*
import javax.inject.Inject

class Audio @Inject constructor(
    private val getAudio: GetAudio,
    private val deleteAudio: DeleteAudio,
    private val shareAudio: ShareAudio,
    private val registerObserver: RegisterObserver,
    private val updateSong: UpdateSong,
    private val serviceAssistFactory: ServiceAssistFactory,
    private val serviceConnector: AudioServiceConnector
) {

    /**
     * Here is dark magic trick about service
     *
     * The goal is init service val automatically, it's also can not be done outside suspended scope
     * That's why runBlocking used here, BUT it'll not block main thread as it will be 100% called in init block
     * and it'll be first initialization, it'll happen on [Dispatchers.IO] specified context
     */

    init {
        CoroutineScope(context = Dispatchers.IO).launch { service }
    }

    private val service: Service by lazy {
        runBlocking {
            serviceAssistFactory.createServiceUsecase(
                serviceConnector.getService()
            )
        }
    }


    fun play(musicCardData: MusicCardData) = service.play(musicCardData)
    fun stop() = service.stop()
    fun onChangePosition(musicCardData: MusicCardData, position: Float) =
        service.onChangePosition(musicCardData, position)

    fun registerAudioEventListener(musicCardData: MusicCardData) =
        service.registerAudioEventListener(musicCardData)


    fun getAll() = getAudio()

    fun remove(uri: Uri, coroutineScope: CoroutineScope) {
        deleteAudio.createDeletionRequest(uri)?.let { sender ->
            coroutineScope.launch { ContentDeletionLauncher.launch(sender) }
        }
    }

    fun share(uri: Uri, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            shareAudio(uri)
        }
    }

    fun update(cardData: MusicCardData, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            updateSong(cardData)
        }
    }

    fun registerContentObserver(contentObserver: ContentObserver) {
        registerObserver(contentObserver)
    }

}