package br.com.embiess83.station

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import br.com.embiess83.station.model.ProductModel
import br.com.embiess83.station.service.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class ProductActivity : ComponentActivity() {

    private val updaterHandler: Handler = Handler()

    private lateinit var productName: TextView

    private lateinit var productPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent.extras!!.getString("code")

        setContentView(R.layout.activity_product)
        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)

        val loadThread: Runnable = Runnable {
            try {
                println("Call retrofit ....")
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://station-api-dj49k.ondigitalocean.app")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service: ProductService = retrofit.create(ProductService::class.java)
                val decFormat = DecimalFormat("'R$ ' 0.00")

                service.get(code!!).enqueue(object : Callback<ProductModel> {
                    override fun onFailure(call: Call<ProductModel>?, t: Throwable?) {
                        t?.printStackTrace()
                        Log.v("retrofit", "call code failed")
                    }

                    override fun onResponse(call: Call<ProductModel>?, response: Response<ProductModel>?) {
                        Log.v("retrofit", "call code ok")
                        response?.body()?.run {
                            productName.setText(this?.name)
                            productName.invalidate()
                            productPrice.setText(decFormat.format(this?.price))
                            productPrice.invalidate()
                        }
                    }

                })

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.i("---", "Exception in thread")
            }
        }

        val backThread: Runnable = Runnable {
            try {
                finish()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.i("---", "Exception in thread")
            }
        }

        updaterHandler.postDelayed(loadThread, 0)

        updaterHandler.postDelayed(backThread, 5000)

    }
}
