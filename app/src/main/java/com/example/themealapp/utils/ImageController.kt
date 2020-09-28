package com.example.themealapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import java.io.File

object ImageController {

    const val IMAGE_PICK_CODE = 1000

    fun selectPhotoFromGallery(fragment: Fragment, code: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        fragment.startActivityForResult(intent, code)
    }

    fun saveImage(context: Context, id: String, uri: Uri) {
        val file = File(context.filesDir, id)
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()!!
        file.writeBytes(bytes)
    }

    fun getImageUri(context: Context, id: String): Uri {
        val file = File(context.filesDir, id)
        return if (file.exists()) Uri.fromFile(file)
        else Uri.parse("android.resource://com.example.themealapp/drawable/no_image")
    }

    fun deleteImage(context: Context, id: String) {
        val file = File(context.filesDir, id)
        file.delete()
    }
}