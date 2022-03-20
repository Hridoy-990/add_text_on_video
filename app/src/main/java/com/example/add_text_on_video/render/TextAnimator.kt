package com.example.add_text_on_video.render

import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import kotlin.math.tan

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 16,March,2022
 */

/**
 * This class is responsible for moving text around a bit. Currently it only moves the
 * text slowly from top to bottom in a kind of "closing credits" fashion shown at the
 * end of movies. You can apply any kind of other transformations here.
 */
class TextAnimator {
    private val time = Time()

    private var speed = 120f
    private var textTarget = Vec3(0f, 0f, 0f)
    private var textTarget2 = Vec3(0f, 0f, 0f)

    private var textPosition = Vec3(0f, 0f, 0f)
    private var textPosition2 = Vec3(0f, 0f, 0f)
    private var textPosition3 = Vec3(0f, 0f, 0f)
    // Text moves towards textTarget until it reached the target within
    // this distance. After that I reset positions and repeat the cycle
    // once again.
    private var reachDistance = 2f

    // Scale for the text
    private var scale = 2f

    // Camera properties
    private var camDistance = 20f
    private var fovY = 60f

    // Boundaries of the visible area
    private var frustumWidth = -1f
    private var frustumHeight = -1f

    // Used to calculate final MVP matrix
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    fun update() {
        updateText()
        time.update()
    }

    fun update2() {
        updateText2()
        time.update()
    }

    fun getMVP(): FloatArray {
        return getMVP(textPosition.x, textPosition.y, textPosition.z)
    }

    fun getMVP2(): FloatArray {
        return getMVP2(textPosition2.x, textPosition2.y, textPosition2.z)
    }

    fun getMVP3(): FloatArray {
        return getMVP3(textPosition3.x, textPosition3.y, textPosition3.z)
    }

    private fun updateText() {
        // Text reached destination?

        if (distance(textTarget2, textPosition3) > reachDistance) textPosition3 += normalize(textTarget2 - textPosition3) * time.deltaTimeSec * speed
        else initPositions()
    }

    private fun updateText2() {
        // Text reached destination?
        if (distance(textTarget, textPosition2) > reachDistance) textPosition2 += normalize(textTarget - textPosition2) * time.deltaTimeSec * speed
        else initPositions()
    }

    fun setCamera(width: Int, height: Int) {
        // Set perspective projection
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, fovY, ratio, 1f, 100f)

        // Position camera
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -camDistance, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate visible area boundaries within frustum
        calculateBoundaries(ratio)

        initPositions()
      //  initPositions2()
    }

    private fun calculateBoundaries(ratio: Float) {
        frustumHeight = 2.0f * camDistance * tan(Math.toRadians(fovY * 0.5)).toFloat()
        frustumWidth = frustumHeight * ratio
    }

    private fun initPositions() {
        textPosition = Vec3(0f,  scale, 0f)
        textPosition2 = Vec3(0f,  scale, 0f)
        textPosition3 = Vec3(scale , 0f , 0f)

        textTarget = Vec3(0f, -frustumHeight/2 - scale, 0f)
        textTarget2 = Vec3(-frustumWidth/2 - scale, 0f, 0f)
    }



    private fun getMVP(x: Float, y: Float, z: Float): FloatArray {

        Log.e(TAG, "getMVP: calling" )
        // Move model to given position
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)

        // Scale to some reasonable size, flip
        Matrix.scaleM(modelMatrix, 0, -scale, -scale, scale)

        // Calculate final MVP matrix
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)

        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        val scratch = FloatArray(16)

        val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()
        val rotateMatrix  = FloatArray(16)
        Matrix.setRotateM(rotateMatrix, 0, angle, 0f, 0f, -1f)
       Matrix.multiplyMM(scratch, 0 , mvpMatrix , 0 , rotateMatrix , 0)

        return scratch
    }

    private fun getMVP2(x: Float, y: Float, z: Float): FloatArray {

        Log.e(TAG, "getMVP: calling" )
        // Move model to given position
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)

        // Scale to some reasonable size, flip
        Matrix.scaleM(modelMatrix, 0, -scale, -scale, scale)

        // Calculate final MVP matrix
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)

        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        return mvpMatrix
    }

    private fun getMVP3(x: Float, y: Float, z: Float): FloatArray {

        Log.e(TAG, "getMVP: calling" )
        // Move model to given position
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)

        // Scale to some reasonable size, flip
        Matrix.scaleM(modelMatrix, 0, -scale, -scale, scale)

        // Calculate final MVP matrix
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)

        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        return mvpMatrix
    }

    companion object {
        private const val TAG = "TextAnimator"
    }
}