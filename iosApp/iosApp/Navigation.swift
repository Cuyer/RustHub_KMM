import SwiftUI

struct OnboardingView: View {
    var body: some View {
        Text("Onboarding")
    }
}

struct CredentialsView: View {
    var body: some View {
        Text("Credentials")
    }
}

struct ServerListView: View {
    var body: some View {
        List {
            NavigationLink("Server Details", destination: ServerDetailsView())
        }
        .navigationTitle("Servers")
    }
}

struct ServerDetailsView: View {
    var body: some View {
        Text("Server Details")
            .navigationTitle("Server")
    }
}

struct SettingsView: View {
    var body: some View {
        List {
            NavigationLink("Change Password", destination: ChangePasswordView())
            NavigationLink("Delete Account", destination: DeleteAccountView())
            NavigationLink("Upgrade Account", destination: UpgradeAccountView())
            NavigationLink("Confirm Email", destination: ConfirmEmailView())
            NavigationLink("Privacy Policy", destination: PrivacyPolicyView())
            NavigationLink("Terms", destination: TermsView())
            NavigationLink("About", destination: AboutView())
            NavigationLink("Subscription", destination: SubscriptionView())
        }
        .navigationTitle("Settings")
    }
}

struct ChangePasswordView: View {
    var body: some View {
        Text("Change Password")
            .navigationTitle("Change Password")
    }
}

struct DeleteAccountView: View {
    var body: some View {
        Text("Delete Account")
            .navigationTitle("Delete Account")
    }
}

struct UpgradeAccountView: View {
    var body: some View {
        Text("Upgrade Account")
            .navigationTitle("Upgrade Account")
    }
}

struct ConfirmEmailView: View {
    var body: some View {
        Text("Confirm Email")
            .navigationTitle("Confirm Email")
    }
}

struct ItemListView: View {
    var body: some View {
        List {
            NavigationLink("Item Details", destination: ItemDetailsView())
        }
        .navigationTitle("Items")
    }
}

struct ItemDetailsView: View {
    var body: some View {
        Text("Item Details")
            .navigationTitle("Item")
    }
}

struct MonumentListView: View {
    var body: some View {
        List {
            NavigationLink("Monument Details", destination: MonumentDetailsView())
        }
        .navigationTitle("Monuments")
    }
}

struct MonumentDetailsView: View {
    var body: some View {
        Text("Monument Details")
            .navigationTitle("Monument")
    }
}

struct PrivacyPolicyView: View {
    var body: some View {
        Text("Privacy Policy")
            .navigationTitle("Privacy Policy")
    }
}

struct ResetPasswordView: View {
    var body: some View {
        Text("Reset Password")
            .navigationTitle("Reset Password")
    }
}

struct TermsView: View {
    var body: some View {
        Text("Terms")
            .navigationTitle("Terms")
    }
}

struct AboutView: View {
    var body: some View {
        Text("About")
            .navigationTitle("About")
    }
}

struct SubscriptionView: View {
    var body: some View {
        Text("Subscription")
            .navigationTitle("Subscription")
    }
}

