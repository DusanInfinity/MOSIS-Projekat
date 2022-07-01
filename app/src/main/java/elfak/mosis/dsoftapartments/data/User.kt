package elfak.mosis.dsoftapartments.data

data class User(var uid : String? = null, var name : String? = null, var email : String? = null, var phoneNumber: String? = null, var description: String? = null) {
    override fun toString(): String {
        return "$uid, $email"
    }
}