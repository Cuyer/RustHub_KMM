import Foundation
import FirebaseCrashlytics

@objc public class CrashReporterBridge: NSObject {
    @objc(log:)
    public static func log(_ message: String) {
        Crashlytics.crashlytics().log(message)
    }

    @objc(recordError:)
    public static func record(error: NSError) {
        Crashlytics.crashlytics().record(error: error)
    }

    @objc(setUserId:)
    public static func setUserId(_ userId: String?) {
        Crashlytics.crashlytics().setUserID(userId ?? "")
    }
}
