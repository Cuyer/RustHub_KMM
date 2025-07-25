import SwiftUI
import shared
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        KoinInitializerKt.doInitKoin { _ in }
    }
    private var appModule: any AppModule = AppModuleImpl()

	var body: some Scene {
		WindowGroup {
            ContentView(appModule: appModule)
		}
	}
}