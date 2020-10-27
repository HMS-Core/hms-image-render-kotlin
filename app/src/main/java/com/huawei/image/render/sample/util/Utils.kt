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
package com.huawei.image.render.sample.util

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Function Description
 *
 * @since 2020-04-28
 */
object Utils {
    /**
     * TAG
     */
    private const val TAG = "Utils"

    /**
     * create demo dir
     *
     * @param dirPath dir path
     * @return result
     */
    fun createResourceDirs(dirPath: String?): Boolean {
        val dir = File(dirPath)
        return if (!dir.exists()) {
            if (dir.parentFile.mkdir()) {
                dir.mkdir()
            } else {
                dir.mkdir()
            }
        } else false
    }

    /**
     * copy assets folders to sdCard
     * @param context context
     * @param foldersName folderName
     * @param path path
     * @return result
     */
    fun copyAssetsFilesToDirs(
        context: Context,
        foldersName: String,
        path: String
    ): Boolean {
        try {
            val files = context.assets.list(foldersName)
            for (file in files) {
                if (!copyAssetsFileToDirs(
                        context,
                        foldersName + File.separator + file,
                        path + File.separator + file
                    )
                ) {
                    Log.e(
                        TAG,
                        "Copy resource file fail, please check permission"
                    )
                    return false
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message)
            return false
        }
        return true
    }

    /**
     * copy resource file to sdCard
     *
     * @param context  context
     * @param fileName fileName
     * @param path     sdCard path
     * @return result
     */
    fun copyAssetsFileToDirs(
        context: Context,
        fileName: String?,
        path: String?
    ): Boolean {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = context.assets.open(fileName)
            val file = File(path)
            outputStream = FileOutputStream(file)
            val temp = ByteArray(4096)
            var n: Int
            while (-1 != inputStream.read(temp).also { n = it }) {
                outputStream.write(temp, 0, n)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message)
            return false
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, e.message)
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        Log.e(TAG, e.message)
                    }
                }
            }
        }
        return true
    }

    /**
     * Add authentication parameters.
     *
     * @return JsonObject of Authentication parameters.
     */
    val authJson: JSONObject
        get() {
            val authJson = JSONObject()
            try {
                authJson.put("projectId", "projectId-test")
                authJson.put("appId", "appId-test")
                authJson.put("authApiKey", "authApiKey-test")
                authJson.put("clientSecret", "clientSecret-test")
                authJson.put("clientId", "clientId-test")
                authJson.put("token", "token-test")
            } catch (e: JSONException) {
                Log.w(
                    TAG,
                    "Get authJson fail, please check auth info"
                )
            }
            return authJson
        }
}