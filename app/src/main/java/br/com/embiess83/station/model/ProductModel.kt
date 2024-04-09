package br.com.embiess83.station.model

import java.math.BigDecimal

class ProductModel(val id: String?, val code: String?, val barCode: String, val name: String, val description: String?, val price: BigDecimal, val base64: String?) {
}