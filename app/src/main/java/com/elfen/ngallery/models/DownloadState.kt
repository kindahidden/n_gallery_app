package com.elfen.ngallery.models

sealed class DownloadState {
    data object Unsaved: DownloadState()
    data object Done:DownloadState()
    data object Pending:DownloadState()
    data object Failure: DownloadState()
    data class Downloading(val progress: Int, val total: Int): DownloadState()
}