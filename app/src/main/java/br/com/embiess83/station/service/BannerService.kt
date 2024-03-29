package br.com.embiess83.station.service

import br.com.embiess83.station.model.BannerModel
import retrofit2.Call
import retrofit2.http.GET

interface BannerService {

    @GET("/api/v1/banners")
    fun list(): Call<List<BannerModel>>
}
