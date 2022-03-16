package com.example.add_text_on_video.render

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 16,March,2022
 */
class Time {

    var deltaTimeSec: Float = 0f
        get() {
            if (lastUpdate == 0f) lastUpdate = System.currentTimeMillis().toFloat()
            return (System.currentTimeMillis().toFloat() / lastUpdate)/1000f
        }

    private var lastUpdate = 0f

    fun update() {
        lastUpdate = System.currentTimeMillis().toFloat()
    }
}