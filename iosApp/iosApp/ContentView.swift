import SwiftUI
import shared

struct ContentView: View {

    let appModule: AppModule

    var body: some View {
        ZStack {
            LinearGradient(
                colors: [
                    Color.colorBackground,
                    Color.colorBackground.opacity(0.85)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            TabView {
                NavigationStack {
                    ServerListView()
                }
                .tabItem {
                    Label("Servers", systemImage: "list.bullet")
                }

                NavigationStack {
                    ItemListView()
                }
                .tabItem {
                    Label("Items", systemImage: "shippingbox")
                }

                NavigationStack {
                    MonumentListView()
                }
                .tabItem {
                    Label("Monuments", systemImage: "building.columns")
                }

                NavigationStack {
                    SettingsView()
                }
                .tabItem {
                    Label("Settings", systemImage: "gear")
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(appModule: AppModuleImpl())
    }
}