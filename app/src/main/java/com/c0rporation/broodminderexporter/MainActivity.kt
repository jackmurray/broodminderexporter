package com.c0rporation.broodminderexporter

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.WorkerThread
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.c0rporation.broodminderexporter.ui.theme.BroodMinderExporterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import okio.Okio

import okio.BufferedSink

import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.Response
import java.io.*


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
        }.setType("*/*")

        val openFileResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                val value = it.data?.also {
                    intent -> val uri = intent.data
                    if (uri != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            handleDatabaseFile(uri)
                        }
                    }
                }
            }
        }

        setContent {
            BroodMinderExporterTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    val context = LocalContext.current
                    Button(
                        // below line is use to add onclick
                        // parameter for our button onclick
                        onClick = {
                            Toast.makeText(context, "Select file to upload", Toast.LENGTH_LONG).show()
                            openFileResult.launch(intent)
                        },
                        // in below line we are using modifier
                        // which is use to add padding to our button
                        modifier = Modifier.padding(all = Dp(10F)),

                        // below line is use to set or
                        // button as enable or disable.
                        enabled = true,

                        // below line is use to
                        // add border to our button.
                        border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Blue)),

                        // below line is use to add shape for our button.
                        shape = MaterialTheme.shapes.medium,

                        content = {
                            Text(text = "Select DB3 File")
                        }
                    )
                }
            }
        }
    }

    private suspend fun handleDatabaseFile(uri: Uri) {
        val fileStream = contentResolver.openInputStream(uri)
        if (fileStream != null) {
            uploadDatabaseFile(fileStream)
        }
    }

    @WorkerThread
    private suspend fun uploadDatabaseFile(dbFile: InputStream) {
        withContext(Dispatchers.IO) {
            val url = "http://10.0.0.53:5000/upload"

            val dbFileRequestBody: RequestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return MediaType.parse("application/octet-stream")
                }

                override fun writeTo(sink: BufferedSink) {
                    sink.writeAll(Okio.buffer(Okio.source(dbFile)))
                }
            }

            var client = OkHttpClient()
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "file", dbFileRequestBody)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException) {
                    try {
                        dbFile.close()
                    } catch (ex: IOException) {
                        e.addSuppressed(ex)
                    }
                    Log.e("dbFileUpload", "failed", e)
                }

                override fun onResponse(call: Call?, response: Response?) {
                    dbFile.close()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@MainActivity,
                            "Upload successful!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }
    }
}