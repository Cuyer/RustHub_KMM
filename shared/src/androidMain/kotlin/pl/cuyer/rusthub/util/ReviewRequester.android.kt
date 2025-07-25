package pl.cuyer.rusthub.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory

actual class ReviewRequester(private val context: Context) {
    actual fun requestReview() {
        val activity = context as? Activity ?: return
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
