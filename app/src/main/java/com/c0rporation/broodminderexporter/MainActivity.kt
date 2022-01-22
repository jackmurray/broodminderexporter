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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

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
                        handleDatabaseFile(uri)
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
                            // when user is clicking the button
                            // we are displaying a toast message.
                            Toast.makeText(context, "Welcome to Geeks for Geeks", Toast.LENGTH_LONG).show()
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

    private fun handleDatabaseFile(uri: Uri) {
        // annoyingly, there doesn't seem to be a way to read the db directly.
        // this is because the android sqlite database class expects a File or
        // a String path to the db file to open, but with the new storage access
        // framework you can't get one. you CAN get a stream that will give you
        // the file contents though, so we copy the file to a location on disk
        // that we _do_ have the path for, and then open that. thanks google
        // you bunch of retards.
        val dbPath = getDatabasePath("tempdb")
        val dbWriter = FileOutputStream(dbPath)
        contentResolver.openInputStream(uri)?.copyTo(dbWriter)

        val db = DatabaseParser(this, dbPath)
        Log.d("DbRecord", db.getBroodMinderData().toString())
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    BroodMinderExporterTheme {
//        Greeting("Android")
//    }
//}