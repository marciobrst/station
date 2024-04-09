package br.com.embiess83.station

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import br.com.embiess83.station.model.BannerModel
import br.com.embiess83.station.service.RestService
import br.com.embiess83.station.service.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.absoluteValue
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val updaterHandler: Handler = Handler()

    private lateinit var imageView: ImageView

    private lateinit var button: Button

    private lateinit var editText: EditText

    private var list = mutableListOf<BannerModel>()

    private var barcode = StringBuilder()

    /*
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("TAG", "" + keyCode)
        //I think you'll have to manually check for the digits and do what you want with them.
        //Perhaps store them in a String until an Enter event comes in (barcode scanners i've used can be configured to send an enter keystroke after the code)
        return true
    }*/

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_DOWN) {
            val pressedKey = event.unicodeChar.toChar()
            barcode.append(pressedKey)

        }
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d("barcode", "" + barcode)
            val intent: Intent = Intent(
                this,
                ProductActivity::class.java
            )
            intent. putExtra("code", barcode.toString().trim())
            startActivity(intent)
            barcode = StringBuilder()

        }

        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)

        val loadThread: Runnable = Runnable {
            try {
                val service: RestService = RestClient.getClient().create(RestService::class.java)
                println("Call banner list ....")
                val call = service.list()
                println("Enqueue call banner list ....")
                call.enqueue(object : Callback<List<BannerModel>> {
                    override fun onFailure(call: Call<List<BannerModel>>?, t: Throwable?) {
                        println("Fail call ....")
                        t?.printStackTrace()
                        Log.v("retrofit", "call failed")
                        call?.cancel()
                    }

                    override fun onResponse(call: Call<List<BannerModel>>?, response: Response<List<BannerModel>>?) {
                        println("Success call ....")
                        Log.v("retrofit", "call ok")
                        response!!.body()!!.forEach {
                            list.add(it)
                            Log.v("retrofit", "call ok")
                        }
                    }

                })

                println("End call banner list ....")

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.i("---", "Exception in thread")
            }
        }

        val updateTimerThread: Runnable = object : Runnable {
            override fun run() {
                if(list !=null && list.isNotEmpty()) {
                    println("Tamanho da consulta: ")
                    println(list.size)
                    var pos = Random.nextInt().absoluteValue % list.size
                    var pp: BannerModel = list[pos]
                    Log.i("---> POSITION 1", pos.toString())
                    var imageBytes = Base64.decode(pp.base64.replace("data:image/png;base64,", "").encodeToByteArray(), Base64.DEFAULT)
                    var decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageView.setImageBitmap(decodedImage)
                    imageView.invalidate()
                }
                updaterHandler.postDelayed(this, 5000)
            }
        }

        updaterHandler.postDelayed(loadThread, 0)

        updaterHandler.postDelayed(updateTimerThread, 5000)


    }
}