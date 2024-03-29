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
import br.com.embiess83.station.service.BannerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.absoluteValue
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val updaterHandler: Handler = Handler()

    private lateinit var imageView: ImageView

    private lateinit var button: Button

    private lateinit var editText: EditText

    private var list = mutableListOf<BannerModel>()

    /*
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("TAG", "" + keyCode)
        //I think you'll have to manually check for the digits and do what you want with them.
        //Perhaps store them in a String until an Enter event comes in (barcode scanners i've used can be configured to send an enter keystroke after the code)
        return true
    }*/

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val code = event.unicodeChar
        Log.d("event code:", "" + code)

        val intent: Intent = Intent(
            this,
            ProductActivity::class.java
        )
        intent. putExtra("code", "" + code)
        startActivity(intent)

        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent: Intent = Intent(
                this,
                ProductActivity::class.java
            )
            Log.i("value text", editText.text.toString())
            intent. putExtra("code", editText.text.toString())
            startActivity(intent)
        }


        val loadThread: Runnable = Runnable {
            try {
                println("Call retrofit ....")
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://station-api-dj49k.ondigitalocean.app")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service: BannerService = retrofit.create(BannerService::class.java)

                service.list().enqueue(object : Callback<List<BannerModel>> {
                    override fun onFailure(call: Call<List<BannerModel>>?, t: Throwable?) {
                        t?.printStackTrace()
                        Log.v("retrofit", "call failed")
                    }

                    override fun onResponse(call: Call<List<BannerModel>>?, response: Response<List<BannerModel>>?) {
                        Log.v("retrofit", "call ok")
                        response!!.body()!!.forEach {
                            list.add(it)
                            Log.v("retrofit", "call ok")
                        }
                    }

                })

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.i("---", "Exception in thread")
            }
        }

        val updateTimerThread: Runnable = object : Runnable {
            override fun run() {
                if(list !=null && list.isNotEmpty()) {
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

        updaterHandler.postDelayed(updateTimerThread, 5)


    }
}