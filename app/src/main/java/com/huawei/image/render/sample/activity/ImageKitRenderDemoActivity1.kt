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

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.huawei.hms.image.render.*
import com.huawei.hms.image.render.ImageRender.RenderCallBack
import com.huawei.image.render.sample.R
import com.huawei.image.render.sample.util.Utils
import java.io.File

/**
 * 功能描述
 *
 * @author c00511068
 * @since 2020-09-07
 */
class ImageKitRenderDemoActivity1 : Activity() {
    /**
     * 布Layout container
     */
    private var contentView: FrameLayout? = null

    // imageRender object
    private var imageRenderAPI: ImageRenderImpl? = null

    // Resource folder, which can be set as you want.
    private var sourcePath: String? = null
    private var hashCode: String? = null
    private var view: View? = null

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_image_kit_demo_1)
        sourcePath =
            filesDir.path + File.separator + ImageKitRenderDemoActivity.SOURCE_PATH
        initView()
        initImageRender()
    }

    /**
     * Initialize the view.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        contentView = findViewById(R.id.content)
    }

    /**
     * Get ImageRenderAPI
     */
    private fun initImageRender() {
        Log.i(
            TAG,
            "timerecorde" + SystemClock.elapsedRealtime()
        )
        Log.d(
            TAG,
            "initImageRender time = " + SystemClock.uptimeMillis()
        )
        ImageRender.getInstance(this, object : RenderCallBack {
            override fun onSuccess(imageRender: ImageRenderImpl) {
                imageRenderAPI = imageRender
            }

            override fun onFailure(i: Int) {
                Log.e(
                    TAG,
                    "Get ImageRender instance failed, errorCode:$i"
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        if (null != imageRenderAPI) {
            imageRenderAPI!!.bindRenderView(
                sourcePath,
                Utils.authJson,
                object : IBindCallBack {
                    override fun onParseEnd() {}
                    override fun onBind(renderView: RenderView, i: Int) {
                        if (renderView != null) {
                            if (renderView.getResultCode() == ResultCode.SUCCEED) {
                                view = renderView.getView()
                                if (null != view) {
                                    hashCode = view.hashCode().toString()
                                    contentView!!.addView(view)
                                }
                            }
                        }
                    }
                })
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        if (null != imageRenderAPI) {
            imageRenderAPI!!.unBindRenderView(hashCode)
        }
    }

    /**
     * Start the animation
     *
     * @param view button
     */
    fun startAnimation(view: View?) {
        Log.i(TAG, "Start animation")
        if (null != imageRenderAPI) {
            val playResult = imageRenderAPI!!.playAnimation()
            Log.i(
                TAG,
                "Start animation:$playResult"
            )
        } else {
            Log.w(
                TAG,
                "Start animation fail, please init first."
            )
        }
    }

    /**
     * Pause the animation
     *
     * @param view button
     */
    fun pauseAnimation(view: View?) {
        // imageRender停止renderView动画
        Log.i(TAG, "Pause animation")
        if (null != imageRenderAPI) {
            val pauseResult = imageRenderAPI!!.pauseAnimation(true)
            if (pauseResult == ResultCode.SUCCEED) {
                Log.i(
                    TAG,
                    "Pause animation success"
                )
            } else {
                Log.i(
                    TAG,
                    "Pause animation failure"
                )
            }
        } else {
            Log.w(
                TAG,
                "Pause animation fail, please init first."
            )
        }
    }

    /**
     * Resume the animation
     *
     * @param view button
     */
    fun restartAnimation(view: View?) {
        Log.i(TAG, "restart animation")
        if (null != imageRenderAPI) {
            val playResult = imageRenderAPI!!.resumeAnimation()
            if (playResult == ResultCode.SUCCEED) {
                Log.i(
                    TAG,
                    "Restart animation success"
                )
            } else {
                Log.i(
                    TAG,
                    "Restart animation failure"
                )
            }
        } else {
            Log.w(
                TAG,
                "Restart animation fail, please init first."
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

    companion object {
        /**
         * TAG
         */
        const val TAG = "ImageKitRenderDemo1"

        /**
         * requestCode for applying for permissions.
         */
        const val PERMISSION_REQUEST_CODE = 0x01
    }
}