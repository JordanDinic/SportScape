package com.example.sportscapee.album

import android.graphics.ImageDecoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

class ProfilePictureViewModel(private val coroutineContext: CoroutineContext): ViewModel() {

    private val _profileViewState: MutableStateFlow<ProfilePictureViewState> = MutableStateFlow(
        ProfilePictureViewState()
    )
    val viewStateFlow: StateFlow<ProfilePictureViewState>
        get() = _profileViewState

    @RequiresApi(Build.VERSION_CODES.P)
    fun onReceive(intent: ProfileIntent) = viewModelScope.launch(coroutineContext) {
        when (intent) {
            is ProfileIntent.OnPermissionGrantedWith -> {
                val tempFile = File.createTempFile(
                    "temp_profile_picture_", ".jpg", intent.compositionContext.cacheDir
                )

                val uri = FileProvider.getUriForFile(
                    intent.compositionContext,
                    "com.example.sportscapee.provider",
                    tempFile
                )
                _profileViewState.value = _profileViewState.value.copy(tempFileUrl = uri)
            }

            is ProfileIntent.OnPermissionDenied -> {
                println("Camera permission was denied")
            }

            is ProfileIntent.OnImageSavedWith -> {
                val tempImageUrl = _profileViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val source = ImageDecoder.createSource(intent.compositionContext.contentResolver, tempImageUrl)
                    val bitmap = ImageDecoder.decodeBitmap(source).asImageBitmap()

                    _profileViewState.value = _profileViewState.value.copy(
                        profilePicture = bitmap,
                        tempFileUrl = null
                    )
                }
            }

            is ProfileIntent.OnImageSavingCanceled -> {
                _profileViewState.value = _profileViewState.value.copy(tempFileUrl = null)
            }
        }
    }
}
