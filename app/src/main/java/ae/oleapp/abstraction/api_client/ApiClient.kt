package ae.oleapp.abstraction.api_client

import ae.oleapp.abstraction.interfaces.InventoryApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "https://finance.ole-app.ae/api/"
    private const val OWNER_BASE_URL = "https://owner.ole-app.ae/api/"

    private val financeApi: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build()
            chain.proceed(request)
        }
        .build()

    private val ownerApi: Retrofit = Retrofit.Builder()
        .baseUrl(OWNER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val inventoryService: InventoryApiService = financeApi.create(InventoryApiService::class.java)
    val ownerApiService: InventoryApiService = ownerApi.create(InventoryApiService::class.java)

}


//delivered remove

