package pl.cuyer.rusthub.util

import com.google.android.play.core.review.ReviewManagerFactory

actual class ReviewRequester(private val activityProvider: ActivityProvider) {
    actual fun requestReview() {
        val activity = activityProvider.currentActivity() ?: return
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val info = task.result
                manager.launchReviewFlow(activity, info)
            }
        }
    }
}
