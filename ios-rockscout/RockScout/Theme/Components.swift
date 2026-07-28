import SwiftUI

// MARK: - Button Styles

struct RockScoutPrimaryButton: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(.ink)
            .font(.headline)
            .background {
                if configuration.isPressed {
                    RockScoutColors.citrineDeep
                } else {
                    LinearGradient(
                        colors: [RockScoutColors.citrine, RockScoutColors.citrineSoft],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                }
            }
            .clipShape(.rect(cornerRadius: 14))
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .animation(.snappy(duration: 0.15), value: configuration.isPressed)
    }
}

struct RockScoutSecondaryButton: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(.rsAccent)
            .font(.headline)
            .padding(.vertical, 14)
            .frame(maxWidth: .infinity)
            .background {
                if configuration.isPressed {
                    RockScoutColors.slate700
                } else {
                    RockScoutColors.slate800
                }
            }
            .clipShape(.rect(cornerRadius: 14))
            .overlay(
                .rect(cornerRadius: 14)
                    .stroke(RockScoutColors.citrine.opacity(0.4), lineWidth: 1)
            )
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .animation(.snappy(duration: 0.15), value: configuration.isPressed)
    }
}

extension ButtonStyle where Self == RockScoutPrimaryButton {
    static var rockScoutPrimary: RockScoutPrimaryButton { .init() }
}

extension ButtonStyle where Self == RockScoutSecondaryButton {
    static var rockScoutSecondary: RockScoutSecondaryButton { .init() }
}

// MARK: - TextField Style

struct RockScoutTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<_Label>) -> some View {
        configuration
            .padding(.vertical, 14)
            .padding(.horizontal, 16)
            .foregroundStyle(.rsText)
            .background(RockScoutColors.slate800, in: .rect(cornerRadius: 12))
            .overlay(
                .rect(cornerRadius: 12)
                    .stroke(RockScoutColors.stoneLine.opacity(0.4), lineWidth: 1)
            )
    }
}

extension TextFieldStyle where Self == RockScoutTextFieldStyle {
    static var rockScout: RockScoutTextFieldStyle { .init() }
}

// MARK: - View Extensions

extension View {
    /// Applies the dark card background with rounded corners and subtle border.
    func rsCard(cornerRadius: CGFloat = 14) -> some View {
        self
            .background(RockScoutColors.slate800, in: .rect(cornerRadius: cornerRadius))
            .overlay(
                .rect(cornerRadius: cornerRadius)
                    .stroke(RockScoutColors.stoneLine.opacity(0.3), lineWidth: 0.5)
            )
    }

    /// Shimmering loading placeholder.
    func shimmering() -> some View {
        self
            .redacted(reason: .placeholder)
            .overlay(
                LinearGradient(
                    colors: [
                        .clear,
                        Color.white.opacity(0.05),
                        .clear,
                    ],
                    startPoint: .leading,
                    endPoint: .trailing
                )
                .animation(
                    .linear(duration: 1.5).repeatForever(autoreverses: false),
                    value: true
                )
            )
    }
}

// MARK: - Components

/// A rarity badge — colored pill showing the rarity level.
struct RarityBadge: View {
    let rarity: String

    var body: some View {
        let color = rarityColor
        Text(rarity)
            .font(.caption2.weight(.semibold))
            .foregroundStyle(color)
            .padding(.horizontal, 8)
            .padding(.vertical, 3)
            .background(color.opacity(0.15), in: .capsule)
    }

    private var rarityColor: Color {
        let lowered = rarity.lowercased()
        if lowered.contains("rare") && !lowered.contains("uncommon") {
            return RockScoutColors.citrine
        }
        if lowered.contains("uncommon") {
            return RockScoutColors.copper
        }
        return RockScoutColors.textLow
    }
}

/// A NEW badge — gold pill shown on entries added within the last 7 days.
struct NewBadge: View {
    var body: some View {
        Text("NEW")
            .font(.caption2.weight(.bold))
            .foregroundStyle(.ink)
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(RockScoutColors.citrine, in: .capsule)
    }
}

/// A rock-class icon badge with the class color.
struct RockClassIcon: View {
    let rockClass: RockClass?
    let category: String?
    var size: CGFloat = 36

    var body: some View {
        let cls: RockClass = rockClass ?? .mineral
        Image(systemName: cls.icon)
            .font(.system(size: size * 0.45))
            .foregroundStyle(cls.color)
            .frame(width: size, height: size)
            .background(cls.color.opacity(0.15), in: .rect(cornerRadius: size * 0.25))
    }
}

/// A stat row for specimen properties (hardness, luster, etc.).
struct StatRow: View {
    let label: String
    let value: String
    var icon: String?

    var body: some View {
        HStack(spacing: 12) {
            if let icon {
                Image(systemName: icon)
                    .foregroundStyle(.rsAccent)
                    .frame(width: 20)
            }
            Text(label)
                .font(.subheadline)
                .foregroundStyle(.rsTextSecondary)
            Spacer()
            Text(value)
                .font(.subheadline.weight(.medium))
                .foregroundStyle(.rsText)
                .multilineTextAlignment(.trailing)
        }
        .padding(.vertical, 6)
    }
}

/// A tag chip for specimen properties (colors, locations, etc.).
struct TagChip: View {
    let text: String
    var color: Color = RockScoutColors.copper

    var body: some View {
        Text(text)
            .font(.caption)
            .foregroundStyle(color)
            .padding(.horizontal, 10)
            .padding(.vertical, 5)
            .background(color.opacity(0.12), in: .capsule)
    }
}

/// Section header with consistent styling.
struct SectionHeader: View {
    let title: String
    var subtitle: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.rsText)
            if let subtitle {
                Text(subtitle)
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

/// Empty state view with an icon and message.
struct EmptyStateView: View {
    let icon: String
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 44))
                .foregroundStyle(.rsTextMuted)
            Text(title)
                .font(.title3.weight(.semibold))
                .foregroundStyle(.rsText)
            Text(message)
                .font(.subheadline)
                .foregroundStyle(.rsTextSecondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .padding(40)
    }
}
