package ae.oleapp.employee.data.di

import ae.oleapp.api.APIClient
import ae.oleapp.employee.data.api.ApiService
import org.koin.core.qualifier.named

import org.koin.dsl.module


val networkModule = module {
    single<ApiService>(named("partnerApi")) {
        APIClient.getPartnerClient().create(ApiService::class.java)
    }

    single<ApiService>(named("newApi")) {
        APIClient.getClientNew().create(ApiService::class.java)
    }
}

