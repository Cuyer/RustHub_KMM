package pl.cuyer.rusthub.util

expect class InAppUpdateManager() {
    fun check(activity: Any)
    fun onResume(activity: Any)
}
