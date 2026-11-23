
package com.example.malofind.navigation
import com.example.malofind.screens.test.FirestoreTestScreen
object Routes {

    // Authentication
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ROLE_SELECTION = "role_selection"
    const val FORGOT_PASSWORD = "forgot_password"

    // Temporary Firestore test
    const val FIRESTORE_TEST = "firestore_test"

    // Seeker
    const val SEEKER_DASHBOARD = "seeker_dashboard"
    const val SEARCH = "search"
    const val PROPERTY_DETAILS = "property_details"
    const val PROPERTY_MAP = "property_map"
    const val FAVOURITES = "favourites"
    const val SUBMIT_REQUEST = "submit_request"
    const val MY_REQUESTS = "my_requests"
    const val SEEKER_PROFILE = "seeker_profile"

    // Common
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"

    // Owner
    const val OWNER_DASHBOARD = "owner_dashboard"
    const val ADD_PROPERTY = "add_property"
    const val EDIT_PROPERTY = "edit_property"
    const val MY_PROPERTIES = "my_properties"
    const val APPROVAL_STATUS = "approval_status"
    const val OWNER_REQUESTS = "owner_requests"

    // Administrator
    const val ADMIN_LOGIN = "admin_login"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val MANAGE_USERS = "manage_users"
    const val MANAGE_OWNERS = "manage_owners"
    const val MANAGE_PROPERTIES = "manage_properties"
    const val EMPTY_STATE = "empty_state"
    const val PROPERTY_APPROVAL = "property_approval"
    const val REPORTS = "reports"
}