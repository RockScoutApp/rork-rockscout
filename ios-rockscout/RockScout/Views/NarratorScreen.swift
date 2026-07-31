import SwiftUI
import AVFoundation

/// 16-chapter narrator player — mirrors the Android NarratorScreen and web Narrator.
/// Plays narration MP3s from the app bundle with auto-queue, seek, and resume.
struct NarratorScreen: View {
    @Environment(\.dismiss) private var dismiss

    @State private var currentIndex: Int = 0
    @State private var isPlaying: Bool = false
    @State private var currentTime: Double = 0
    @State private var duration: Double = 0
    @State private var isSeeking: Bool = false

    private let chapters = NarratorChapterData.chapters
    private let player = AVAudioPlayerWrapper()

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                HStack {
                    Button {
                        player.pause()
                        dismiss()
                    } label: {
                        Image(systemName: "chevron.left")
                            .font(.title3)
                            .foregroundStyle(.rsAccent)
                    }
                    Text("Narrator")
                        .font(.title2.bold())
                        .foregroundStyle(.rsText)
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)

                ScrollView {
                    VStack(spacing: 16) {
                        // Total running time banner
                        HStack {
                            Text("\(chapters.count) Chapters")
                                .font(.subheadline.bold())
                                .foregroundStyle(.rsAccent)
                            Spacer()
                            Text("Total: ~\(totalRunningTime)")
                                .font(.subheadline)
                                .foregroundStyle(.rsText)
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(.ultraThinMaterial, in: .rect(cornerRadius: 12))

                        // Player controls
                        playerCard

                        // Chapter list
                        VStack(spacing: 8) {
                            ForEach(chapters.indices, id: \.self) { idx in
                                let chapter = chapters[idx]
                                Button {
                                    loadChapter(idx)
                                } label: {
                                    ChapterRow(
                                        chapter: chapter,
                                        isCurrent: idx == currentIndex,
                                        isPlaying: isPlaying && idx == currentIndex
                                    )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                }
            }
        }
        .navigationBarBackButtonHidden()
        .onAppear {
            restoreState()
        }
        .onDisappear {
            saveState()
            player.pause()
        }
    }

    // MARK: - Player Card

    private var playerCard: some View {
        VStack(spacing: 12) {
            Text("\(chapters[currentIndex].index). \(chapters[currentIndex].title)")
                .font(.headline)
                .foregroundStyle(.rsText)
                .multilineTextAlignment(.center)

            // Seek slider
            Slider(
                value: Binding(
                    get: { currentTime },
                    set: { newValue in
                        isSeeking = true
                        currentTime = newValue
                    }
                ),
                in: 0...max(duration, 1),
                onEditingChanged: { editing in
                    if !editing {
                        player.seek(to: currentTime)
                        isSeeking = false
                    }
                }
            )
            .tint(.rsAccent)

            HStack {
                Text(formatTime(currentTime))
                Spacer()
                Text(formatTime(duration))
            }
            .font(.caption)
            .foregroundStyle(.rsTextMuted)

            // Playback controls
            HStack(spacing: 32) {
                Button {
                    if currentIndex > 0 { loadChapter(currentIndex - 1) }
                } label: {
                    Image(systemName: "backward.fill")
                        .font(.title2)
                        .foregroundStyle(currentIndex > 0 ? .rsAccent : .rsTextMuted)
                }
                .disabled(currentIndex == 0)

                Button {
                    togglePlay()
                } label: {
                    Image(systemName: isPlaying ? "pause.circle.fill" : "play.circle.fill")
                        .font(.system(size: 56))
                        .foregroundStyle(.rsAccent)
                }

                Button {
                    if currentIndex < chapters.count - 1 { loadChapter(currentIndex + 1) }
                } label: {
                    Image(systemName: "forward.fill")
                        .font(.title2)
                        .foregroundStyle(currentIndex < chapters.count - 1 ? .rsAccent : .rsTextMuted)
                }
                .disabled(currentIndex == chapters.count - 1)
            }
            .padding(.top, 4)
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Actions

    private func loadChapter(_ index: Int) {
        currentIndex = index
        currentTime = 0
        player.load(chapters[index]) { dur in
            duration = dur
        }
        player.play()
        isPlaying = true
    }

    private func togglePlay() {
        if isPlaying {
            player.pause()
            isPlaying = false
        } else {
            if currentTime >= duration && duration > 0 {
                loadChapter(currentIndex)
            } else {
                player.play()
                isPlaying = true
            }
        }
    }

    private func restoreState() {
        let savedChapter = UserDefaults.standard.integer(forKey: "narrator_chapter")
        let savedPos = UserDefaults.standard.double(forKey: "narrator_position")
        let idx = max(0, min(savedChapter, chapters.count - 1))
        currentIndex = idx
        player.load(chapters[idx]) { dur in
            duration = dur
            if savedPos > 0 && savedPos < dur {
                player.seek(to: savedPos)
                currentTime = savedPos
            }
        }
    }

    private func saveState() {
        UserDefaults.standard.set(currentIndex, forKey: "narrator_chapter")
        UserDefaults.standard.set(player.currentTime, forKey: "narrator_position")
    }

    private func formatTime(_ seconds: Double) -> String {
        let total = Int(seconds)
        return "\(total / 60):\(String(total % 60, radix: 10).leftPad(toLength: 2, withPad: "0"))"
    }

    private var totalRunningTime: String {
        formatTime(Double(chapters.count * 35))
    }
}

// MARK: - Chapter Row

private struct ChapterRow: View {
    let chapter: NarratorChapter
    let isCurrent: Bool
    let isPlaying: Bool

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(isCurrent ? Color.rsAccent.opacity(0.2) : Color.rsSurface)
                    .frame(width: 34, height: 34)
                if isPlaying {
                    Image(systemName: "play.fill")
                        .font(.caption)
                        .foregroundStyle(.rsAccent)
                } else {
                    Text("\(chapter.index)")
                        .font(.caption.bold())
                        .foregroundStyle(isCurrent ? .rsAccent : .rsTextMuted)
                }
            }
            VStack(alignment: .leading, spacing: 2) {
                Text(chapter.title)
                    .font(.subheadline.weight(isCurrent ? .bold : .medium))
                    .foregroundStyle(isCurrent ? .rsText : .rsTextSecondary)
                    .lineLimit(1)
                Text(chapter.preview)
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
                    .lineLimit(1)
            }
            Spacer()
        }
        .padding(12)
        .background(
            isCurrent ? Color.rsAccent.opacity(0.08) : Color.rsSurface.opacity(0.5),
            in: .rect(cornerRadius: 12)
        )
        .overlay(
            .rect(cornerRadius: 12)
                .stroke(isCurrent ? Color.rsAccent.opacity(0.3) : Color.clear, lineWidth: 1)
        )
    }
}

// MARK: - Chapter Data

struct NarratorChapter {
    let index: Int
    let title: String
    let filename: String
    let preview: String
}

enum NarratorChapterData {
    static let chapters: [NarratorChapter] = [
        NarratorChapter(index: 1, title: "Welcome", filename: "rockscout_welcome_intro", preview: "Hey there. Welcome to RockScout."),
        NarratorChapter(index: 2, title: "5-Source AI Rock ID", filename: "rock_identification_voice", preview: "This is the big one. Tap Identify a Rock."),
        NarratorChapter(index: 3, title: "Your Collection", filename: "rocks_collection_guide", preview: "This is My Rocks — your personal collection."),
        NarratorChapter(index: 4, title: "Field Tools", filename: "field_capture_voice", preview: "Field Captures is where you log photos."),
        NarratorChapter(index: 5, title: "Dig Sites & Gem Shows", filename: "treasure_map_voice", preview: "This is your treasure map."),
        NarratorChapter(index: 6, title: "Trip Planning", filename: "trip_planner_voice", preview: "The Trip Planner is where you build your route."),
        NarratorChapter(index: 7, title: "Trading & Community", filename: "trade_board_intro_voice", preview: "The Trade Board is where you post specimens."),
        NarratorChapter(index: 8, title: "Social", filename: "social_network_voice", preview: "RockScout's got a whole social network."),
        NarratorChapter(index: 9, title: "Aurora & Night Sky", filename: "aurora_forecaster_voice", preview: "This is your personal space weather station."),
        NarratorChapter(index: 10, title: "Your Profile", filename: "profile_level_up_voice", preview: "Tap your avatar to open your Profile."),
        NarratorChapter(index: 11, title: "Reference Library", filename: "periodic_table_voice_guide", preview: "The Periodic Table — all 118 elements."),
        NarratorChapter(index: 12, title: "Artifacts & Wonders", filename: "artifact_catalog_voice", preview: "The Artifacts tile takes you to prehistoric artifacts."),
        NarratorChapter(index: 13, title: "Field Kit", filename: "rockhounding_guide_voice", preview: "The BLM Public Lands Guide breaks down rules."),
        NarratorChapter(index: 14, title: "Learn & Explore", filename: "educational_guides_intro", preview: "The Educational Guides hub is where you learn."),
        NarratorChapter(index: 15, title: "Premium & Free Tier", filename: "pricing_explanation_voice", preview: "Let's talk about the money side."),
        NarratorChapter(index: 16, title: "Outro", filename: "rockscout_voice_intro", preview: "That's RockScout. I built it for rockhounders."),
    ]
}

// MARK: - AVAudioPlayer Wrapper

@MainActor
final class AVAudioPlayerWrapper {
    private var audioPlayer: AVAudioPlayer?

    var currentTime: Double {
        audioPlayer?.currentTime ?? 0
    }

    func load(_ chapter: NarratorChapter, onReady: @escaping (Double) -> Void) {
        guard let url = Bundle.main.url(forResource: chapter.filename, withExtension: "mp3") else {
            onReady(0)
            return
        }
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .spokenAudio)
            try AVAudioSession.sharedInstance().setActive(true)
            audioPlayer = try AVAudioPlayer(contentsOf: url)
            audioPlayer?.delegate = PlaybackDelegate.shared
            onReady(audioPlayer?.duration ?? 0)
        } catch {
            onReady(0)
        }
    }

    func play() {
        audioPlayer?.play()
    }

    func pause() {
        audioPlayer?.pause()
    }

    func seek(to time: Double) {
        audioPlayer?.currentTime = time
    }
}

private final class PlaybackDelegate: NSObject, AVAudioPlayerDelegate {
    static let shared = PlaybackDelegate()
    nonisolated func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        Task { @MainActor in
            NotificationCenter.default.post(name: .narratorChapterEnded, object: nil)
        }
    }
}

extension Notification.Name {
    static let narratorChapterEnded = Notification.Name("narratorChapterEnded")
}

// MARK: - String Padding Helper

private extension String {
    func leftPad(toLength length: Int, withPad character: Character) -> String {
        if count >= length { return self }
        return String(repeating: character, count: length - count) + self
    }
}
