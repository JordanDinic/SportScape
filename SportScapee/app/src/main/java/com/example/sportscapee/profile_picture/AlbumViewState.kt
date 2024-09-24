package com.example.sportscapee.profile_picture

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class AlbumViewState(
    /**
     * holds the URL of the temporary file which stores the image taken by the camera.
     */
    val tempFileUrl: Uri? = null,

    /**
     * holds the list of images taken by camera or selected pictures from the gallery.
     */
    val selectedPictures: List<ImageBitmap> = emptyList(),
)