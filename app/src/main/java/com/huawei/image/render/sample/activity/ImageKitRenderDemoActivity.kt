/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package com.huawei.image.render.sample.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.huawei.hms.image.render.*
import com.huawei.hms.image.render.ImageRender.RenderCallBack
import com.huawei.image.render.sample.R
import com.huawei.image.render.sample.activity.ImageKitRenderDemoActivity
import com.huawei.image.render.sample.util.Utils
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * The ImageRenderSample code provides examples of initializing the service, obtaining views, playing animation, pausing animation, and destroying resources.
 *
 * @author huawei
 * @since 5.0.0
 */
class ImageKitRenderDemoActivity : Activity() {
    /**
     * Layout container
     */
    private var contentView: FrameLayout? = null

    // imageRender object
    private var imageRenderAPI: ImageRenderImpl? = null
    private var sourcePath: String? = null
    private var textProgress: TextView? = null
    private var mCurrentDemo: String? = null
    private var marqueeVis = 1
    private var hashCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_image_kit_demo)
        sourcePath =
            filesDir.path + File.separator + SOURCE_PATH
        initView()
        val permissionCheck = ContextCompat.checkSelfPermission(
            this@ImageKitRenderDemoActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            initData()
            initImageRender()
        } else {
            ActivityCompat.requestPermissions(
                this@ImageKitRenderDemoActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Initialize the view.
     */
    private fun initView() {
        contentView = findViewById(R.id.content)
        textProgress = findViewById(R.id.text_progress)
        val spinner =
            findViewById<Spinner>(R.id.spinner_animations)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                mCurrentDemo = spinner.adapter.getItem(position).toString()
                changeAnimation(mCurrentDemo!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * change the animation which is choose in spinner
     * @param animationName animationName
     */
    private fun changeAnimation(animationName: String) {
        if (!Utils.copyAssetsFilesToDirs(
                this,
                animationName,
                sourcePath.toString()
            )
        ) {
            Log.e(
                TAG,
                "copy files failure, please check permissions"
            )
            return
        }
        if (imageRenderAPI == null) {
            Log.e(
                TAG,
                "initRemote fail, please check kit version"
            )
            return
        }
        if (contentView!!.childCount > 0) {
            imageRenderAPI!!.removeRenderView()
            contentView!!.removeAllViews()
            addView()
        }
    }

    /**
     * Create default resources.
     * You can compile the manifest.xml file and image resource file. The code is for reference only.
     */
    private fun initData() {
        // Absolute path of the resource files.
        if (!Utils.createResourceDirs(sourcePath)) {
            Log.e(
                TAG,
                "Create dirs fail, please check permission"
            )
        }
        if (!Utils.copyAssetsFileToDirs(
                this,
                "AlphaAnimation" + File.separator + "aixin7.png",
                sourcePath + File.separator + "aixin7.png"
            )
        ) {
            Log.e(
                TAG,
                "Copy resource file fail, please check permission"
            )
        }
        if (!Utils.copyAssetsFileToDirs(
                this,
                "AlphaAnimation" + File.separator + "bj.jpg",
                sourcePath + File.separator + "bj.jpg"
            )
        ) {
            Log.e(
                TAG,
                "Copy resource file fail, please check permission"
            )
        }
        if (!Utils.copyAssetsFileToDirs(
                this,
                "AlphaAnimation" + File.separator + "manifest.xml",
                sourcePath + File.separator + "manifest.xml"
            )
        ) {
            Log.e(
                TAG,
                "Copy resource file fail, please check permission"
            )
        }
    }

    /**
     * Use the ImageRender API.
     */
    private fun initImageRender() {
        // Obtain an ImageRender object.
        ImageRender.getInstance(this, object : RenderCallBack {
            override fun onSuccess(imageRender: ImageRenderImpl) {
                Log.i(
                    TAG,
                    "getImageRenderAPI success"
                )
                imageRenderAPI = imageRender
                useImageRender()
            }

            override fun onFailure(i: Int) {
                Log.e(
                    TAG,
                    "getImageRenderAPI failure, errorCode = $i"
                )
            }
        })
    }

    /**
     * The Image Render service is required.
     */
    private fun useImageRender() {
        if (imageRenderAPI == null) {
            Log.e(
                TAG,
                "initRemote fail, please check kit version"
            )
            return
        }
        addView()
    }

    private fun addView() {
        // Initialize the ImageRender object.
        val initResult = imageRenderAPI!!.doInit(
            sourcePath,
            Utils.authJson
        )
        Log.i(
            TAG,
            "DoInit result == $initResult"
        )
        if (initResult == 0) {
            // Obtain the rendered view.
            val renderView = imageRenderAPI!!.renderView
            if (renderView.getResultCode() == ResultCode.SUCCEED) {
                val view = renderView.getView()
                if (null != view) {
                    // Add the rendered view to the layout.
                    contentView!!.addView(view)
                    hashCode = view.hashCode().toString()
                } else {
                    Log.w(
                        TAG,
                        "GetRenderView fail, view is null"
                    )
                }
            } else if (renderView.getResultCode() == ResultCode.ERROR_GET_RENDER_VIEW_FAILURE) {
                Log.w(TAG, "GetRenderView fail")
            } else if (renderView.getResultCode() == ResultCode.ERROR_XSD_CHECK_FAILURE) {
                Log.w(
                    TAG,
                    "GetRenderView fail, resource file parameter error, please check resource file."
                )
            } else if (renderView.getResultCode() == ResultCode.ERROR_VIEW_PARSE_FAILURE) {
                Log.w(
                    TAG,
                    "GetRenderView fail, resource file parsing failed, please check resource file."
                )
            } else if (renderView.getResultCode() == ResultCode.ERROR_REMOTE) {
                Log.w(
                    TAG,
                    "GetRenderView fail, remote call failed, please check HMS service"
                )
            } else if (renderView.getResultCode() == ResultCode.ERROR_DOINIT) {
                Log.w(
                    TAG,
                    "GetRenderView fail, init failed, please init again"
                )
            }
        } else {
            Log.w(
                TAG,
                "Do init fail, errorCode == $initResult"
            )
        }
    }

    override fun onDestroy() {
        // Destroy the view.
        if (null != imageRenderAPI) {
            imageRenderAPI!!.removeRenderView()
        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        if (null != imageRenderAPI) {
            imageRenderAPI!!.bindRenderView(
                sourcePath,
                Utils.authJson,
                object : IBindCallBack {
                    override fun onBind(renderView: RenderView, i: Int) {
                        if (renderView != null) {
                            if (renderView.getResultCode() == ResultCode.SUCCEED) {
                                val view = renderView.getView()
                                if (null != view) {
                                    contentView!!.addView(view)
                                    hashCode = view.hashCode().toString()
                                }
                            }
                        }
                    }

                    override fun onParseEnd() {}
                })
        }
    }

    /**
     * Play the animation.
     *
     * @param view button
     */
    fun startAnimation(view: View?) {
        // Play the rendered view.
        Log.i(TAG, "Start animation")
        if (null != imageRenderAPI) {
            val playResult = imageRenderAPI!!.playAnimation()
            if (playResult == ResultCode.SUCCEED) {
                Log.i(
                    TAG,
                    "Start animation success"
                )
            } else {
                Log.i(
                    TAG,
                    "Start animation failure"
                )
            }
        } else {
            Log.w(
                TAG,
                "Start animation fail, please init first."
            )
        }
    }

    /**
     * Stop the animation.
     *
     * @param view button
     */
    fun stopAnimation(view: View?) {
        // Stop the renderView animation.
        Log.i(TAG, "Stop animation")
        if (null != imageRenderAPI) {
            val playResult = imageRenderAPI!!.stopAnimation()
            if (playResult == ResultCode.SUCCEED) {
                Log.i(
                    TAG,
                    "Stop animation success"
                )
            } else {
                Log.i(
                    TAG,
                    "Stop animation failure"
                )
            }
        } else {
            Log.w(
                TAG,
                "Stop animation fail, please init first."
            )
        }
    }

    /**
     * Pause animation
     *
     * @param view button
     */
    fun pauseAnimation(view: View?) {
        if (imageRenderAPI != null) {
            val result = imageRenderAPI!!.pauseAnimation(true)
            Log.d(
                TAG,
                "pauseAnimation result == $result"
            )
        }
    }

    /**
     * Resume animation
     *
     * @param view button
     */
    fun resumeAnimation(view: View?) {
        if (imageRenderAPI != null) {
            val result = imageRenderAPI!!.resumeAnimation()
            Log.d(
                TAG,
                "resumeAnimation result == $result"
            )
        }
    }

    /**
     * Set variable value
     *
     * @param view button
     */
    fun setVariable(view: View?) {
        if ("Marquee" == mCurrentDemo) {
            marqueeVis = 1 - marqueeVis
            imageRenderAPI!!.setKeyValueInfo("SetVariable", "var", "" + marqueeVis)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        if (null != imageRenderAPI) {
            imageRenderAPI!!.unBindRenderView(hashCode)
        }
    }

    /**
     * Next page
     *
     * @param view button
     */
    fun nextPage(view: View?) {
        val intent =
            Intent(this@ImageKitRenderDemoActivity, ImageKitRenderDemoActivity1::class.java)
        startActivity(intent)
    }

    /**
     * Start record.
     *
     * @param view button
     */
    fun startRecord(view: View?) {
        Log.d(
            "#######start record:",
            System.currentTimeMillis().toString() + ""
        )
        Toast.makeText(this, "start record", Toast.LENGTH_SHORT).show()
        if (null != imageRenderAPI) {
            val result = recordInfo
            val start = imageRenderAPI!!.startRecord(result, object : IStreamCallBack {
                override fun onRecordSuccess(hashMap: HashMap<String, Any>) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ImageKitRenderDemoActivity,
                            "record success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    saveRecordResult(hashMap)
                }

                override fun onRecordFailure(hashMap: HashMap<String, Any>) {
                    val errorCode = hashMap["errorCode"] as Int
                    val errorMessage = hashMap["errorMessage"] as String?
                    Log.i(
                        TAG,
                        "back result$errorCode;back msg$errorMessage"
                    )
                }

                // progress:{1-100}
                override fun onProgress(progress: Int) {
                    showRecordProgress(progress)
                }
            })
            if (start == ResultCode.SUCCEED) {
                Log.i(TAG, "start record success")
            } else {
                Log.i(
                    TAG,
                    "start record failure:$start"
                )
            }
        } else {
            Log.w(
                TAG,
                "start record fail, please init first."
            )
        }
    }

    private val recordInfo: JSONObject
        private get() {
            val result = JSONObject()
            val recordType =
                (findViewById<View>(R.id.recordtype) as EditText).text.toString()
            val videoScale =
                (findViewById<View>(R.id.videoscale) as EditText).text.toString()
            val videoFps =
                (findViewById<View>(R.id.videofps) as EditText).text.toString()
            val gifScale =
                (findViewById<View>(R.id.gifscale) as EditText).text.toString()
            val gifFps =
                (findViewById<View>(R.id.giffps) as EditText).text.toString()
            try {
                val videoJson = JSONObject()
                val gifJson = JSONObject()
                result.put("recordType", recordType)
                videoJson.put("videoScale", videoScale)
                videoJson.put("videoFps", videoFps)
                gifJson.put("gifScale", gifScale)
                gifJson.put("gifFps", gifFps)
                result.put("video", videoJson)
                result.put("gif", gifJson)
            } catch (e: JSONException) {
                Log.w(TAG, e.message)
            }
            return result
        }

    private fun saveRecordResult(hashMap: HashMap<String, Any>) {
        val fileName = (Environment.getExternalStorageDirectory()
            .toString() + File.separator
                + "VideoAndPic")
        val fileDir = File(fileName)
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                return
            }
        }
        val mp4Path =
            fileName + File.separator + System.currentTimeMillis() + ".mp4"
        val gifPath =
            fileName + File.separator + System.currentTimeMillis() + ".gif"
        val recordType = hashMap["recordType"] as String?
        val videoBytes = hashMap["videoBytes"] as ByteArray?
        val gifBytes = hashMap["gifBytes"] as ByteArray?
        try {
            if (recordType == "1") {
                videoBytes?.let { saveFile(it, mp4Path) }
            } else if (recordType == "2") {
                gifBytes?.let { saveFile(it, gifPath) }
            } else if (recordType == "3") {
                videoBytes?.let { saveFile(it, mp4Path) }
                gifBytes?.let { saveFile(it, gifPath) }
            }
        } catch (e: IOException) {
            Log.w(TAG, e.message)
        }
    }

    @Throws(IOException::class)
    private fun saveFile(bytes: ByteArray, path: String) {
        val fos = FileOutputStream(File(path))
        try {
            fos.write(bytes, 0, bytes.size)
            fos.close()
        } catch (e: IOException) {
            Log.e(TAG, e.message)
        } finally {
            try {
                fos.close()
            } catch (e: IOException) {
                Log.e(TAG, e.message)
            }
        }
    }

    private fun showRecordProgress(progress: Int) {
        runOnUiThread { textProgress!!.text = "progress:$progress%" }
    }

    /**
     * Stop record.
     *
     * @param view button
     */
    fun stopRecord(view: View?) {
        Toast.makeText(this, "stop record", Toast.LENGTH_SHORT).show()
        if (null != imageRenderAPI) {
            val result = imageRenderAPI!!.stopRecord()
            if (result == ResultCode.SUCCEED) {
                Log.i(TAG, "stop record success")
            } else {
                Log.i(TAG, "stop record failure:$result")
            }
        } else {
            Log.w(
                TAG,
                "stop record fail, please init first."
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // The permission is granted.
                initData()
                initImageRender()
            } else {
                // The permission is rejected.
                Log.w(TAG, "permission denied")
                Toast.makeText(
                    this@ImageKitRenderDemoActivity,
                    "Please grant the app the permission to read the SD card",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        /**
         * TAG
         */
        const val TAG = "ImageKitRenderDemo"

        /**
         * Resource folder, which can be set as you want.
         */
        const val SOURCE_PATH = "sources"

        /**
         * requestCode for applying for permissions.
         */
        const val PERMISSION_REQUEST_CODE = 0x01
    }
}