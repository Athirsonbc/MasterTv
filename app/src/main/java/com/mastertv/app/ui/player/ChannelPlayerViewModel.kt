package com.mastertv.app.ui.player

import androidx.lifecycle.ViewModel
import com.mastertv.app.models.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChannelPlayerViewModel : ViewModel() {

    private val _current = MutableStateFlow<Channel?>(null)
    val current = _current.asStateFlow()

    private val _epg = MutableStateFlow<List<String>>(emptyList())
    val epg = _epg.asStateFlow()

    fun setChannel(channel: Channel) {
        _current.value = channel
        loadEPG(channel)
    }

    private fun loadEPG(channel: Channel) {
        // placeholder simples – pode ser integrado a um EPG real depois
        _epg.value = listOf(
            "Programa Atual - 14:00",
            "Próximo Programa - 15:00"
        )
    }
}
