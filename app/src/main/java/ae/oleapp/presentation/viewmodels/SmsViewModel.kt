package ae.oleapp.presentation.viewmodels

import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.PlayersCountResponse
import ae.oleapp.abstraction.models.SaleReportData
import ae.oleapp.abstraction.models.SmsData
import ae.oleapp.abstraction.models.SmsHomeResponse
import ae.oleapp.abstraction.models.SmsPurchaseRequest
import ae.oleapp.abstraction.models.SmsPurchaseResponse
import ae.oleapp.abstraction.models.SmsRequest
import ae.oleapp.abstraction.models.SmsResponse
import ae.oleapp.abstraction.models.SmsUsageResponse
import ae.oleapp.abstraction.models.UpdateSmsRequest
import ae.oleapp.abstraction.repository.SmsRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SmsViewModel(private val repository: SmsRepository) : ViewModel() {
    private val _smsResponse = MutableLiveData<ApiResponse<SmsResponse>>()
    val smsResponse: LiveData<ApiResponse<SmsResponse>> = _smsResponse

    fun sendSmsRequest(request: SmsRequest) {
        viewModelScope.launch {
            _smsResponse.value = ApiResponse.Loading()
            _smsResponse.value = repository.sendSmsRequest(request)
        }
    }

    // New update method
    fun updateSmsRequest(accessToken: String, request: UpdateSmsRequest) {
        viewModelScope.launch {
            _smsResponse.value = ApiResponse.Loading()
            _smsResponse.value = repository.updateSmsRequest(accessToken, request)
        }
    }


    private val _purchaseResponse = MutableLiveData<ApiResponse<SmsPurchaseResponse>>()
    val purchaseResponse: LiveData<ApiResponse<SmsPurchaseResponse>> = _purchaseResponse

    fun purchaseSms(request: SmsPurchaseRequest) {
        viewModelScope.launch {
            _purchaseResponse.value = ApiResponse.Loading()
            _purchaseResponse.value = repository.purchaseSms(request)
        }
    }

    private val _smsHomeRes = MutableLiveData<ApiResponse<SmsHomeResponse>>()
    val smsHomeResponse: LiveData<ApiResponse<SmsHomeResponse>> = _smsHomeRes


    fun fetchHomeSms( clubId: Int = 1) {
        _smsHomeRes.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading inventory products...")

        viewModelScope.launch {
            repository.fetchHomeSms(clubId) { result ->
                result.onSuccess { productsList ->
                    _smsHomeRes.postValue(ApiResponse.Success(productsList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch products: ${error.message}", error)
                    _smsHomeRes.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }


    private val _playerCount = MutableLiveData<ApiResponse<PlayersCountResponse>>()
    val playerCount: LiveData<ApiResponse<PlayersCountResponse>> = _playerCount


    fun fetchPlayersCount( clubId: Int = 1) {
        _playerCount.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading inventory products...")

        viewModelScope.launch {
            repository.fetchPlayersCount(clubId) { result ->
                result.onSuccess { productsList ->
                    _playerCount.postValue(ApiResponse.Success(productsList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch products: ${error.message}", error)
                    _playerCount.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }


    private val _smsUsageResponse = MutableLiveData<ApiResponse<SmsUsageResponse>>()
    val smsUsageResponse: LiveData<ApiResponse<SmsUsageResponse>> = _smsUsageResponse


    fun fetchSmsHistory( clubId: Int = 1) {
        _playerCount.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading inventory products...")

        viewModelScope.launch {
            repository.fetchSmsHistory(clubId) { result ->
                result.onSuccess { productsList ->
                    _smsUsageResponse.postValue(ApiResponse.Success(productsList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch products: ${error.message}", error)
                    _smsUsageResponse.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }
}