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
        }
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}