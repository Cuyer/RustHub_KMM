import SwiftUI
import shared

struct ContentView: View {

    let appModule: AppModule

    var body: some View {
        ZStack {
            Color.colorBackground
                .ignoresSafeArea()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}