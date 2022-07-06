package elfak.mosis.dsoftapartments.data

import android.graphics.Bitmap

data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var description: String = "",
    var imageBitmap: Bitmap? = null
) {
    override fun toString(): String {
        return "$uid, $email"
    }
}