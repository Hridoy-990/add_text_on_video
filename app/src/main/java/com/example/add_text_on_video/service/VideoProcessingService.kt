package com.example.add_text_on_video.service

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import com.example.add_text_on_video.processing.AddTextToVideoProcessing

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 16,March,2022
 */
class VideoProcessingService : IntentService("VideoProcessingService") {

    override fun onHandleIntent(p0: Intent?) {
        when (p0?.action) {
            ACTION_ENCODE_VIDEOS -> encodeVideos(p0)
        }
    }

    private fun encodeVideos(intent: Intent) {
        val outPath = intent?.getStringExtra(KEY_OUT_PATH)
        val inputVidUri = intent?.getParcelableExtra<Uri>(KEY_INPUT_VID_URI)
        val text = intent?.getStringExtra(KEY_TEXT)
        AddTextToVideoProcessing().process(outPath!!,
            contentResolver.openFileDescriptor(inputVidUri!!, "r")!!.fileDescriptor, text ?: "Md Jahirul Islam Hridoy")
        val pi = intent?.getParcelableExtra<PendingIntent>(KEY_RESULT_INTENT)
        pi?.send()
    }

    companion object {
        const val KEY_OUT_PATH = "com.example.add_text_on_video.key.OUT_PATH"
        const val KEY_INPUT_VID_URI = "com.example.add_text_on_video.key.INPUT_VID_URI"
        const val KEY_TEXT = "com.example.add_text_on_video.key.TEXT"
        const val KEY_RESULT_INTENT = "com.example.add_text_on_video.key.RESULT_INTENT"
        const val ACTION_ENCODE_VIDEOS = "com.example.add_text_on_video.action.ENCODE_VIDEOS"
    }
}