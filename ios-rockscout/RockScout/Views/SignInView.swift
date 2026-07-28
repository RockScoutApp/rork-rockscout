import SwiftUI
import Observation

/// Sign-in / sign-up screen — uses the same Supabase auth as web and Android.
/// Accounts are shared across all platforms.
struct SignInView: View {
    @Environment(AuthManager.self) private var auth
    @State private var mode: AuthMode = .signIn
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var confirmPassword: String = ""
    @State private var showPasswordReset: Bool = false
    @State private var resetEmail: String = ""
    @State private var resetSent: Bool = false
    @State private var isSubmitting: Bool = false
    @State private var localError: String?
    @FocusState private var focusedField: Field?

    private enum AuthMode {
        case signIn
        case signUp
    }

    private enum Field {
        case email
        case password
        case confirmPassword
    }

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            ScrollView {
                VStack(spacing: 32) {
                    headerSection

                    authForm

                    if let error = localError ?? auth.error {
                        errorBanner(error)
                    }

                    submitButton

                    switchModeButton

                    resetPasswordButton
                }
                .padding(.horizontal, 24)
                .padding(.top, 40)
            }
            .scrollDismissesKeyboard(.interactively)
        }
        .sheet(isPresented: $showPasswordReset) {
            passwordResetSheet
        }
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(spacing: 12) {
            Image(systemName: "diamond.fill")
                .font(.system(size: 56))
                .foregroundStyle(.rsAccent)
                .symbolEffect(.pulse)

            Text("RockScout")
                .font(.system(.largeTitle, design: .rounded, weight: .bold))
                .foregroundStyle(.rsText)

            Text(mode == .signIn ? "Welcome back, rockhound" : "Start your rockhounding journey")
                .font(.subheadline)
                .foregroundStyle(.rsTextSecondary)
        }
        .padding(.bottom, 8)
    }

    // MARK: - Form

    private var authForm: some View {
        VStack(spacing: 16) {
            TextField("Email", text: $email)
                .textFieldStyle(.rockScout)
                .keyboardType(.emailAddress)
                .textContentType(.emailAddress)
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .focused($focusedField, equals: .email)
                .submitLabel(.next)
                .onSubmit { focusedField = .password }

            SecureField("Password", text: $password)
                .textFieldStyle(.rockScout)
                .textContentType(mode == .signIn ? .password : .newPassword)
                .focused($focusedField, equals: .password)
                .submitLabel(mode == .signIn ? .go : .next)
                .onSubmit {
                    if mode == .signUp {
                        focusedField = .confirmPassword
                    } else {
                        focusedField = nil
                        Task { await submit() }
                    }
                }

            if mode == .signUp {
                SecureField("Confirm Password", text: $confirmPassword)
                    .textFieldStyle(.rockScout)
                    .textContentType(.newPassword)
                    .focused($focusedField, equals: .confirmPassword)
                    .submitLabel(.go)
                    .onSubmit {
                        focusedField = nil
                        Task { await submit() }
                    }
            }
        }
    }

    // MARK: - Error Banner

    private func errorBanner(_ message: String) -> some View {
        HStack(spacing: 8) {
            Image(systemName: "exclamationmark.triangle.fill")
                .foregroundStyle(.rsDanger)
            Text(message)
                .font(.footnote)
                .foregroundStyle(.rsTextSecondary)
            Spacer()
        }
        .padding(12)
        .background(RockScoutColors.danger.opacity(0.12), in: .rect(cornerRadius: 10))
    }

    // MARK: - Submit

    private var submitButton: some View {
        Button {
            Task { await submit() }
        } label: {
            HStack {
                if isSubmitting {
                    ProgressView()
                        .tint(.ink)
                }
                Text(mode == .signIn ? "Sign In" : "Create Account")
                    .fontWeight(.semibold)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
        }
        .buttonStyle(.rockScoutPrimary)
        .disabled(isSubmitting || email.isEmpty || password.isEmpty)
    }

    private var switchModeButton: some View {
        Button {
            mode = mode == .signIn ? .signUp : .signIn
            localError = nil
            confirmPassword = ""
        } label: {
            Text(mode == .signIn
                ? "Don't have an account? Sign up"
                : "Already have an account? Sign in"
            )
            .font(.subheadline)
            .foregroundStyle(.rsAccent)
        }
    }

    private var resetPasswordButton: some View {
        Button("Forgot password?") {
            resetEmail = email
            showPasswordReset = true
        }
        .font(.footnote)
        .foregroundStyle(.rsTextMuted)
    }

    // MARK: - Password Reset Sheet

    private var passwordResetSheet: some View {
        NavigationStack {
            ZStack {
                Color.rsBackground.ignoresSafeArea()

                VStack(spacing: 24) {
                    Text("Reset Password")
                        .font(.title2.bold())
                        .foregroundStyle(.rsText)

                    Text("Enter your email and we'll send you a reset link.")
                        .font(.subheadline)
                        .foregroundStyle(.rsTextSecondary)
                        .multilineTextAlignment(.center)

                    TextField("Email", text: $resetEmail)
                        .textFieldStyle(.rockScout)
                        .keyboardType(.emailAddress)
                        .textContentType(.emailAddress)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)

                    if resetSent {
                        Label("Reset link sent! Check your email.", systemImage: "checkmark.circle.fill")
                            .foregroundStyle(.rsSuccess)
                            .font(.footnote)
                    }

                    Button {
                        Task { await sendReset() }
                    } label: {
                        Text("Send Reset Link")
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                    }
                    .buttonStyle(.rockScoutPrimary)
                    .disabled(resetEmail.isEmpty)

                    Spacer()
                }
                .padding(24)
                .navigationTitle("Reset Password")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("Done") { showPasswordReset = false }
                            .foregroundStyle(.rsAccent)
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }

    // MARK: - Actions

    private func submit() async {
        guard !email.isEmpty && !password.isEmpty else { return }

        if mode == .signUp {
            guard password == confirmPassword else {
                localError = "Passwords don't match."
                return
            }
            guard password.count >= 6 else {
                localError = "Password must be at least 6 characters."
                return
            }
        }

        isSubmitting = true
        localError = nil
        defer { isSubmitting = false }

        do {
            if mode == .signIn {
                try await auth.signIn(email: email, password: password)
                await EntitlementManager.shared.login(userId: auth.currentUserId?.uuidString ?? "")
            } else {
                try await auth.signUp(email: email, password: password)
                await EntitlementManager.shared.login(userId: auth.currentUserId?.uuidString ?? "")
            }
        } catch {
            // error is set on auth.error
        }
    }

    private func sendReset() async {
        do {
            try await auth.resetPassword(email: resetEmail)
            resetSent = true
        } catch {
            // error shown via auth.error
        }
    }
}
