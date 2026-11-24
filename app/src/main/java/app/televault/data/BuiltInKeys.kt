package app.televault.data

/**
 * TDLib cannot start without an api_id/api_hash pair, so every Telegram client (TDrive included)
 * carries one inside the app. Users never see or type it. To use your own pair, change these two lines.
 * Currently the public pair from Telegram's open-source Android client.
 */
object BuiltInKeys {
    const val API_ID = 4
    const val API_HASH = "014b35b6184100b085b0d0572f9b5103"
}
