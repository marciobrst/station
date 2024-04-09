package br.com.embiess83.station.service

import br.com.embiess83.station.model.BannerModel
import br.com.embiess83.station.model.ProductModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestService {

    @GET("/api/v1/banners")
    fun list(): Call<List<BannerModel>>

    @GET("/api/v1/products")
    fun get(@Query("code") code: String): Call<ProductModel>
}
