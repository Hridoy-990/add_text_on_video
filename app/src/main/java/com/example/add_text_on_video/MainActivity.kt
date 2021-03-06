package com.example.add_text_on_video

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.add_text_on_video.databinding.ActivityMainBinding
import com.example.add_text_on_video.service.VideoProcessingService
import java.io.File

class MainActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityMainBinding

    private var inputFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState != null) inputFile = savedInstanceState.getParcelable("inputFile")

        initView()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("inputFile", inputFile)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            Toast.makeText(this, getString(R.string.warn_no_storage_permission), Toast.LENGTH_LONG)
                .show()
        } else {
            if (requestCode == CODE_SELECT_VID) {
                performVideoSearch(
                    this@MainActivity, CODE_SELECT_VID)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_SELECT_VID && resultCode == Activity.RESULT_OK) {
            inputFile = data!!.data!!
        } else if (requestCode == CODE_PROCESSING_FINISHED) {
           binding.progressEncoding.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        configureUi()
    }

    private fun initView() {
       binding.butSelectVid.setOnClickListener {
            if (needsStoragePermission(this@MainActivity)) {
                requestStoragePermission(this@MainActivity, CODE_SELECT_VID)
            }
            else {
                performVideoSearch(this@MainActivity, CODE_SELECT_VID)
            }
        }

        binding.ivPreview.setOnClickListener {
            playPreview()
        }

        binding.butProcessVideo.setOnClickListener {
            processVideo()
        }
    }

    private fun playPreview() {
        val outFile = File(getOutputPath())
        if (outFile.exists()) {
            val uri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    FileProvider.getUriForFile(this, "$packageName.provider", outFile)
                else
                    Uri.parse(outFile.absolutePath)

            val intent = Intent(Intent.ACTION_VIEW, uri)
                .setDataAndType(uri,"video/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(uri, "video/mp4")

            startActivityForResult(intent, CODE_THUMB)

        } else {
            Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_LONG).show()
        }
    }

    private fun processVideo() {
        if (inputFile != null) {
            val intent = Intent(this, VideoProcessingService::class.java).apply {

                action = VideoProcessingService.ACTION_ENCODE_VIDEOS
                putExtra(VideoProcessingService.KEY_OUT_PATH, getOutputPath())
                putExtra(VideoProcessingService.KEY_INPUT_VID_URI, inputFile)
                putExtra(VideoProcessingService.KEY_TEXT, binding.etText.text?.toString())

                // We want this Activity to get notified once the encoding has finished
                val pi = createPendingResult(CODE_PROCESSING_FINISHED, intent, 0)
                putExtra(VideoProcessingService.KEY_RESULT_INTENT, pi)
            }

            startService(intent)

            binding.progressEncoding.visibility = View.VISIBLE
        } else {
            Toast.makeText(this@MainActivity, getString(R.string.err_no_input_file),
                Toast.LENGTH_LONG).show()
        }
    }

    private fun getOutputPath(): String {
        return cacheDir.absolutePath + "/" + OUT_FILE_NAME
    }

    private fun configureUi() {
        if (isServiceRunning(this, VideoProcessingService::class.java))
            binding.progressEncoding.visibility = View.VISIBLE
        else
            binding.progressEncoding.visibility = View.INVISIBLE

        binding.tvSelectedVideo.text = ""
        if (inputFile!= null)
            binding.tvSelectedVideo.text = getName(this, inputFile!!) ?: ""

        val outFile = File(getOutputPath())
        if (outFile.exists()) {
            val thumb = ThumbnailUtils.createVideoThumbnail(outFile.absolutePath,
                MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)
            binding.ivPreview.setImageBitmap(thumb)
        }
    }

    companion object {
        const val TAG = "MainActivity"

        const val CODE_SELECT_VID = 6660
        const val CODE_THUMB = 6661
        const val CODE_PROCESSING_FINISHED = 6662

        const val OUT_FILE_NAME = "out.mp4"
    }

}