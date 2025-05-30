package ae.oleapp.presentation.viewmodels

import ae.oleapp.abstraction.repository.SmsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SmsViewModelFactory(private val repository: SmsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmsViewModel::class.java)) {
            return SmsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
