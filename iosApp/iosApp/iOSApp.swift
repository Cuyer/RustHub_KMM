import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinInitializerKt.doInitKoin { _ in }
    }
    private var appModule: any AppModule = AppModuleImpl()

	var body: some Scene {
		WindowGroup {
            ContentView(appModule: appModule)
		}
	}
}