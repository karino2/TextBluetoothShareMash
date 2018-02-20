package com.livejournal.karino2.textbluetoothsharemash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import java.io.File
import android.content.Intent



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if(it.action == "com.adamrocker.android.simeji.ACTION_INTERCEPT") {

                insertLocalFileAndDelete()
                finish()
                return
            }
        }


        setContentView(R.layout.activity_main)

    }

    val FILE_NAME_PAT = Regex("""text_bluetooth_share_mash(-[0-9]+)?\.txt""")

    fun showMessage(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun insertLocalFileAndDelete() {
        val downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val files = downloadPath.list { dir, fname -> FILE_NAME_PAT.matchEntire(fname) != null  }
                .map{ File(downloadPath, it) }
                .sortedByDescending { it.lastModified() }

        if(files.size == 0) {
            showMessage("No file found. Please send file first.")
            return
        }

        // showMessage("Size is ${files.size}, name is ${files[0].absolutePath}")

        val data = Intent()
        data.putExtra("replace_key", files[0].readText())
        setResult(RESULT_OK, data)

        files.map { it.delete() }
    }
}
