package com.livejournal.karino2.textbluetoothsharemash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import java.io.File
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.content.Context
import android.support.v4.content.FileProvider




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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private val editText: EditText by lazy {
            findViewById(R.id.editText) as EditText
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_share -> {
                val documentPath = filesDir
                val target = File(documentPath, FILE_NAME)
                // if(target.exists()) { target.delete() }

                val et = editText
                val msg = et.text.toString()
                target.writeText(msg)

                getPreferences(Context.MODE_PRIVATE).edit()
                        .putString("LAST", msg)
                        .apply()

                et.text.clear()

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val fileUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        target)
                intent.putExtra(Intent.EXTRA_STREAM, fileUri)
                startActivity(intent)
                return true
            }
            R.id.action_last -> {
                editText.setText(
                        getPreferences(Context.MODE_PRIVATE).getString("LAST", "")
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }


    val FILE_NAME = "text_bluetooth_share_mash.txt"
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
