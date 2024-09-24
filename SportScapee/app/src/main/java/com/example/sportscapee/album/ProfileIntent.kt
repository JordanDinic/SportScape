package com.example.sportscapee.album

import android.content.Context

sealed class ProfileIntent {
    data class OnPermissionGrantedWith(val compositionContext: Context): ProfileIntent()
    data object OnPermissionDenied: ProfileIntent()
    data object OnImageSavingCanceled: ProfileIntent()
    data class OnImageSavedWith(val compositionContext: Context): ProfileIntent()
}
