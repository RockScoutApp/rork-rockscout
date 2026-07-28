import AVFoundation
import UIKit
import Observation

/// Camera manager using AVFoundation — captures photos for the Identify screen.
/// Includes `.external` device type so the cloud simulator's injected camera works.
@Observable
@MainActor
final class CameraManager: NSObject {
    private(set) var isSessionRunning: Bool = false
    private(set) var isFlashOn: Bool = false
    private(set) var capturedImage: UIImage?

    private let session = AVCaptureSession()
    private let output = AVCapturePhotoOutput()
    private var input: AVCaptureDeviceInput?
    private let sessionQueue = DispatchQueue(label: "camera.session")

    var session: AVCaptureSession { session }

    // MARK: - Session

    func startSession() async {
        guard !isSessionRunning else { return }

        await withCheckedContinuation { (continuation: CheckedContinuation<Void, Never>) in
            sessionQueue.async {
                self.configureSession()
                self.session.startRunning()

                Task { @MainActor in
                    self.isSessionRunning = self.session.isRunning
                    continuation.resume()
                }
            }
        }
    }

    func stopSession() {
        sessionQueue.async {
            self.session.stopRunning()
        }
        isSessionRunning = false
    }

    // MARK: - Configure

    private func configureSession() {
        session.beginConfiguration()
        session.sessionPreset = .photo

        // Discover camera — include .external for cloud simulator
        let discoverySession = AVCaptureDevice.DiscoverySession(
            deviceTypes: [.builtInWideAngleCamera, .external],
            mediaType: .video,
            position: .back
        )

        guard let camera = discoverySession.devices.first else {
            session.commitConfiguration()
            return
        }

        do {
            let newInput = try AVCaptureDeviceInput(device: camera)
            if let existing = input {
                session.removeInput(existing)
            }
            if session.canAddInput(newInput) {
                session.addInput(newInput)
                input = newInput
            }
        } catch {
            session.commitConfiguration()
            return
        }

        if session.canAddOutput(output) {
            session.addOutput(output)
        }

        session.commitConfiguration()
    }

    // MARK: - Capture

    func capturePhoto() {
        let settings = AVCapturePhotoSettings()

        if isFlashOn {
            settings.flashMode = .on
        } else {
            settings.flashMode = .off
        }

        output.capturePhoto(with: settings, delegate: self)
    }

    // MARK: - Flash

    func toggleFlash() {
        isFlashOn.toggle()
    }
}

// MARK: - Photo Capture Delegate

extension CameraManager: @preconcurrency AVCapturePhotoCaptureDelegate {
    nonisolated func photoOutput(
        _ output: AVCapturePhotoOutput,
        didFinishProcessingPhoto photo: AVCapturePhoto,
        error: Error?
    ) {
        guard error == nil,
              let data = photo.fileDataRepresentation(),
              let image = UIImage(data: data) else { return }

        Task { @MainActor in
            self.capturedImage = image
        }
    }
}
