package ae.oleapp.employee.data.model.response

import com.google.gson.annotations.SerializedName

data class ReviewData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("rating")
    val rating: Int? = null,

    @SerializedName("comments")
    val comments: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("expression")
    val expression: String? = null,

    @SerializedName("tip_amount")
    val tipAmount: Int? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("user")
    val user: User? = null,

    @SerializedName("booking")
    val booking: Booking? = null
) {

    data class User(
        @SerializedName("id")
        val id: Int? = null,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("date_of_birth")
        val dateOfBirth: String? = null,

        @SerializedName("attachments")
        val attachments: List<Any?>? = null,

        @SerializedName("picture")
        val picture: String? = null,

        @SerializedName("age")
        val age: Int? = null
    )

    data class Booking(
        @SerializedName("id")
        val id: Int? = null,

        @SerializedName("date")
        val date: String? = null,

        @SerializedName("club")
        val club: Club? = null
    )

    data class Club(
        @SerializedName("id")
        val id: Int? = null,

        @SerializedName("name")
        val name: String? = null
    )
}