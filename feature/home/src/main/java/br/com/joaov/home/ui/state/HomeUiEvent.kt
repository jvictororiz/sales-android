package br.com.joaov.home.ui.state

sealed class HomeUiEvent {
    data object Idle: HomeUiEvent()
    data class ShowMessageSnackBar(val message: String) : HomeUiEvent()
    data object ScrollEndClients : HomeUiEvent()
}