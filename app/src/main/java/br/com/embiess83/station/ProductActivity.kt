package br.com.embiess83.station

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import br.com.embiess83.station.model.ProductModel
import br.com.embiess83.station.service.RestService
import br.com.embiess83.station.service.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class ProductActivity : ComponentActivity() {

    private val updaterHandler: Handler = Handler()

    private lateinit var productName: TextView

    private lateinit var productPrice: TextView

    private lateinit var productCode: TextView

    private lateinit var productError: TextView

    private val decFormat = DecimalFormat("'R$' 0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)
        productCode = findViewById(R.id.productCode)
        productError = findViewById(R.id.productError)

        val code = intent.extras!!.getString("code")
        println("Product barcode $code")
        productCode.text = code
        productCode.invalidate()

        val backThread: Runnable = Runnable {
            try {
                finish()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.i("---", "Exception in thread")
            }
        }

        val loadThread: Runnable = Runnable {
            try {
                val service: RestService = RestClient.getClient().create(RestService::class.java)
                println("Call product ....")
                println("Product barcode2 $code")
                val call: Call<ProductModel> = service.get(code!!)
                println("Enqueue call product ....")

                call.enqueue(object : Callback<ProductModel> {
                    override fun onFailure(call: Call<ProductModel>?, t: Throwable?) {
                        Log.v("fail retrofit", "call code failed")
                        println("Falha na consulta de produto")
                        productError.text = t?.message
                        productError.invalidate()
                        call?.cancel()
                        updaterHandler.postDelayed(backThread, 5000)
                    }

                    override fun onResponse(call: Call<ProductModel>?, response: Response<ProductModel>?) {
                        Log.v("success retrofit", "call code ok")
                        println("Sucesso na consulta de produto")
                        println(response?.code())
                        println(response?.body())
                        response?.body()?.run {
                            Log.v("response body", this.toString())
                            Log.v("response body", this.name)
                            Log.v("response body", this?.price.toString())
                            productName.text = this?.name
                            productName.invalidate()
                            productPrice.text = decFormat.format(this?.price)
                            productPrice.invalidate()

                            productError.text = "-"
                            productError.invalidate()
                        }
                        updaterHandler.postDelayed(backThread, 5000)
                    }

                })
                println("End Call product ....")

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.i("---", "Exception in thread")
                updaterHandler.postDelayed(backThread, 5000)
            }
        }

        updaterHandler.postDelayed(loadThread, 0)

    }
}
