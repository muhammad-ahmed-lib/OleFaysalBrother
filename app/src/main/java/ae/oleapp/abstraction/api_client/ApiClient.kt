package ae.oleapp.abstraction.api_client

import ae.oleapp.abstraction.interfaces.InventoryApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "https://finance.ole-app.ae/api/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val inventoryService: InventoryApiService = retrofit.create(InventoryApiService::class.java)
}

