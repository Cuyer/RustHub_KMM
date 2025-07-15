package pl.cuyer.rusthub.util

import platform.StoreKit.SKStoreReviewController

actual class ReviewRequester {
    actual fun requestReview() {
        SKStoreReviewController.requestReview()
    }
}
