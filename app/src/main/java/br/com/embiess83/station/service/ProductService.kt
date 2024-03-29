package br.com.embiess83.station.service

import br.com.embiess83.station.model.ProductModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Optional

interface ProductService {

    @GET("/api/v1/products")
    fun get(@Query(value = "code") code: String): Call<ProductModel>
}
