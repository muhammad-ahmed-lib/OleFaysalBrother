package ae.oleapp.employee.data.model.response

import ae.oleapp.models.Country
import ae.oleapp.models.Day
import ae.oleapp.models.OleClubFacility
import ae.oleapp.models.OwnerClub
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class GetClub(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("owner_phone")
    val ownerPhone: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("name_ar")
    val nameAr: String? = null,

    @SerializedName("country")
    val oleCountry: Country? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("latitude")
    val latitude: String? = null,

    @SerializedName("longitude")
    val longitude: String? = null,

    @SerializedName("slots_60")
    val slots60: String? = null,

    @SerializedName("slots_90")
    val slots90: String? = null,

    @SerializedName("slots_120")
    val slots120: String? = null,

    @SerializedName("contact")
    val contact: String? = null,

    @SerializedName("club_logo")
    val clubLogo: String? = null,

    @SerializedName("logo_path")
    val logoPath: String? = null,

    @SerializedName("club_cover")
    val clubCover: String? = null,

    @SerializedName("cover_path")
    val coverPath: String? = null,

    @SerializedName("is_featured")
    val isFeatured: String? = null,

    @SerializedName("price_per_day")
    val pricePerDay: String? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("is_offer")
    val isOffer: String? = null,

    @SerializedName("rating")
    val rating: String? = null,

    @SerializedName("start_price")
    val startPrice: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("booking_count")
    val bookingCount: Int? = null,

    @SerializedName("facilities")
    val facilities: List<OleClubFacility>? = null,

    @SerializedName("timings")
    val timings: List<Day>? = null,

    @SerializedName("distance")
    val distance: String? = null,

    @SerializedName("favorite")
    val favorite: String? = null,

    @SerializedName("match_allowed")
    val matchAllowed: String? = null,

    @SerializedName("favorite_count")
    val favoriteCount: String? = null,

    @SerializedName("fields_count")
    val fieldsCount: Int? = null,

    @SerializedName("today_earning")
    val todayEarning: String? = null,

    @SerializedName("total_confirmed")
    val totalConfirmed: String? = null,

    @SerializedName("total_completed")
    val totalCompleted: String? = null,

    @SerializedName("waiting_user_count")
    val waitingUserCount: String? = null,

    @SerializedName("total_hours")
    val totalHours: String? = null,

    @SerializedName("gap_allowed")
    val gapAllowed: String? = null,

    @SerializedName("games_allowed")
    val gamesAllowed: String? = null,

    @SerializedName("has_padel_fields")
    val hasPadelFields: String? = null,

    @SerializedName("club_payment_method")
    val clubPaymentMethod: String? = null,

    @SerializedName("available_fields")
    val availableFields: String? = null,

    @SerializedName("club_expiry_date")
    val clubExpiryDate: String? = null,

    @SerializedName("is_expired")
    val isExpired: String? = null,

    @SerializedName("offer")
    val offer: String? = null,

    @SerializedName("new_players_count")
    val newPlayersCount: String? = null,

    @SerializedName("club_type")
    val clubType: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("logo")
    val logo: String? = null,

    @SerializedName("banner")
    val banner: String? = null,

    @SerializedName("slot_pattern")
    val slotPattern: List<String>? = null,

    val ownerClub: OwnerClub? = null
){
    companion object {
        private var clubList: List<GetClub> = emptyList()

        fun storeList(list: List<GetClub>) {
            clubList = list
        }

        fun getList(): List<GetClub> {
            return clubList
        }
    }
}