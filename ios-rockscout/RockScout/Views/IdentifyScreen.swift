import SwiftUI
import AVFoundation

/// Identify screen — camera capture + AI identification via the Cloudflare backend.
/// Mirrors the Android IdentifyScreen.
struct IdentifyScreen: View {
    @Environment(EntitlementManager.self) private var entitlement
    @State private var cameraManager = CameraManager()
    @State private var showImagePicker: Bool = false
    @State private var capturedImage: UIImage?
    @State private var isAnalyzing: Bool = false
    @State private var matches: [IdentifyMatch] = []
    @State private var summary: String?
    @State private var errorMessage: String?
    @State private var scanState: ScanState = .camera

    private enum ScanState {
        case camera
        case analyzing
        case results
    }

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            switch scanState {
            case .camera:
                cameraView
            case .analyzing:
                analyzingView
            case .results:
                resultsView
            }
        }
        .navigationTitle("Identify")
        .navigationBarTitleDisplayMode(.large)
        .sheet(isPresented: $showImagePicker) {
            ImagePicker(image: $capturedImage, onImagePicked: { image in
                capturedImage = image
                Task { await identify(image: image) }
            })
        }
        .alert("Error", isPresented: .constant(errorMessage != nil)) {
            Button("OK") { errorMessage = nil }
        } message: {
            Text(errorMessage ?? "")
        }
    }

    // MARK: - Camera

    private var cameraView: some View {
        VStack {
            // Camera preview or placeholder
            ZStack {
                if cameraManager.isSessionRunning {
                    CameraPreviewView(session: cameraManager.session)
                        .ignoresSafeArea()
                } else {
                    VStack(spacing: 16) {
                        Image(systemName: "camera.viewfinder")
                            .font(.system(size: 48))
                            .foregroundStyle(.rsTextMuted)
                        Text("Camera Ready")
                            .font(.headline)
                            .foregroundStyle(.rsText)
                        Text("Tap the shutter to identify a rock, mineral, or fossil")
                            .font(.subheadline)
                            .foregroundStyle(.rsTextSecondary)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(RockScoutColors.slate900)
                }

                // Overlay frame
                RoundedRectangle(cornerRadius: 12)
                    .stroke(.white.opacity(0.3), lineWidth: 2)
                    .frame(width: 240, height: 240)
                    .allowsHitTesting(false)
            }

            // Controls
            HStack(spacing: 40) {
                Button {
                    showImagePicker = true
                } label: {
                    Image(systemName: "photo.on.ramdisk.fill")
                        .font(.title2)
                        .foregroundStyle(.rsText)
                        .frame(width: 56, height: 56)
                        .background(RockScoutColors.slate700, in: .circle)
                }

                Button {
                    cameraManager.capturePhoto()
                } label: {
                    Circle()
                        .fill(.white)
                        .frame(width: 72, height: 72)
                        .overlay(
                            Circle()
                                .stroke(.rsAccent, lineWidth: 4)
                                .frame(width: 80, height: 80)
                        )
                }
                .disabled(!cameraManager.isSessionRunning)

                Button {
                    cameraManager.toggleFlash()
                } label: {
                    Image(systemName: cameraManager.isFlashOn ? "bolt.fill" : "bolt.slash.fill")
                        .font(.title2)
                        .foregroundStyle(.rsText)
                        .frame(width: 56, height: 56)
                        .background(RockScoutColors.slate700, in: .circle)
                }
            }
            .padding(.bottom, 24)
        }
        .task {
            await cameraManager.startSession()
        }
        .onDisappear {
            cameraManager.stopSession()
        }
        .onChange(of: cameraManager.capturedImage) { _, newImage in
            if let image = newImage {
                Task { await identify(image: image) }
            }
        }
    }

    // MARK: - Analyzing

    private var analyzingView: some View {
        VStack(spacing: 24) {
            Spacer()

            if let image = capturedImage {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 200, height: 200)
                    .clipShape(.rect(cornerRadius: 16))
                    .overlay {
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(.rsAccent, lineWidth: 3)
                    }
                    .shadow(color: .rsAccent.opacity(0.3), radius: 12)
            }

            VStack(spacing: 8) {
                ProgressView()
                    .tint(.rsAccent)
                    .scaleEffect(1.5)

                Text("Identifying...")
                    .font(.headline)
                    .foregroundStyle(.rsText)

                Text(entitlement.isPremium ? "Using premium AI models" : "Analyzing your specimen")
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
            }

            Spacer()
        }
    }

    // MARK: - Results

    private var resultsView: some View {
        ScrollView {
            VStack(spacing: 20) {
                if let image = capturedImage {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFill()
                        .frame(maxWidth: .infinity)
                        .frame(height: 220)
                        .clipShape(.rect(cornerRadius: 16))
                        .padding(.horizontal, 16)
                }

                if let summary {
                    VStack(alignment: .leading, spacing: 8) {
                        SectionHeader(title: "AI Analysis")
                        Text(summary)
                            .font(.body)
                            .foregroundStyle(.rsTextSecondary)
                    }
                    .padding(16)
                    .rsCard()
                    .padding(.horizontal, 16)
                }

                VStack(alignment: .leading, spacing: 12) {
                    SectionHeader(title: "Top Matches", subtitle: "\(matches.count) potential matches")

                    ForEach(matches) { match in
                        MatchCard(match: match)
                    }
                }
                .padding(.horizontal, 16)

                Button {
                    scanState = .camera
                    capturedImage = nil
                    matches = []
                    summary = nil
                } label: {
                    Text("Scan Another")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                }
                .buttonStyle(.rockScoutPrimary)
                .padding(.horizontal, 16)

                Spacer(minLength: 24)
            }
            .padding(.top, 8)
        }
    }

    // MARK: - Identify

    private func identify(image: UIImage) async {
        scanState = .analyzing
        isAnalyzing = true
        errorMessage = nil

        guard let imageData = image.jpegData(compressionQuality: 0.85) else {
            errorMessage = "Could not process the image."
            scanState = .camera
            isAnalyzing = false
            return
        }

        // 5MB limit
        if imageData.count > 5_000_000 {
            errorMessage = "Image is too large. Please use a smaller photo."
            scanState = .camera
            isAnalyzing = false
            return
        }

        do {
            let response = try await IdentifyService.identify(
                imageData: imageData,
                isPremium: entitlement.isPremium
            )
            matches = response.matches
            summary = response.summary
            scanState = .results
        } catch let error as IdentifyError {
            errorMessage = error.localizedDescription
            scanState = .camera
        } catch {
            errorMessage = error.localizedDescription
            scanState = .camera
        }

        isAnalyzing = false
    }
}

// MARK: - Match Card

private struct MatchCard: View {
    let match: IdentifyMatch

    var body: some View {
        HStack(spacing: 12) {
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(match.name)
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.rsText)
                    Spacer()
                    confidenceBadge
                }
                if let reasoning = match.reasoning {
                    Text(reasoning)
                        .font(.caption)
                        .foregroundStyle(.rsTextSecondary)
                        .lineLimit(3)
                }
            }
        }
        .padding(14)
        .rsCard()
    }

    private var confidenceBadge: some View {
        let color: Color = match.confidence >= 85 ? .rsSuccess : (match.confidence >= 60 ? .rsAccent : RockScoutColors.textLow)
        return Text("\(match.confidence)%")
            .font(.caption.weight(.bold))
            .foregroundStyle(color)
            .padding(.horizontal, 8)
            .padding(.vertical, 3)
            .background(color.opacity(0.15), in: .capsule)
    }
}

// MARK: - Camera Preview

private struct CameraPreviewView: UIViewRepresentable {
    let session: AVCaptureSession

    func makeUIView(context: Context) -> PreviewView {
        let view = PreviewView()
        view.videoPreviewLayer.session = session
        view.videoPreviewLayer.videoGravity = .resizeAspectFill
        return view
    }

    func updateUIView(_ uiView: PreviewView, context: Context) {}

    final class PreviewView: UIView {
        override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }
        var videoPreviewLayer: AVCaptureVideoPreviewLayer {
            layer as! AVCaptureVideoPreviewLayer
        }
    }
}

// MARK: - Image Picker

private struct ImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    let onImagePicked: (UIImage) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .photoLibrary
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    final class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: ImagePicker

        init(_ parent: ImagePicker) {
            self.parent = parent
        }

        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]
        ) {
            if let uiImage = info[.originalImage] as? UIImage {
                parent.image = uiImage
                parent.onImagePicked(uiImage)
            }
            picker.dismiss(animated: true)
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }
}
