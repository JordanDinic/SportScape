package com.example.sportscapee.album

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class ProfilePictureViewState(
    /**
     * Holds the URL of the temporary file used to store the image taken by the camera.
     */
    val tempFileUrl: Uri? = null,

    /**
     * Holds the profile picture (image taken by camera).
     */
    val profilePicture: ImageBitmap? = null
)
