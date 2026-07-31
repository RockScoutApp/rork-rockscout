import SwiftUI

/// A bold, shiny gold "PRO" badge that stands out from everything else.
/// Gold gradient capsule with a shimmering sweep animation.
struct ProBadgeView: View {
    @State private var shimmerOffset: CGFloat = -1

    var body: some View {
        HStack(spacing: 3) {
            Image(systemName: "star.fill")
                .font(.system(size: 9, weight: .black))
            Text("PRO")
                .font(.system(size: 11, weight: .black))
                .tracking(1)
        }
        .foregroundStyle(.ink)
        .padding(.horizontal, 8)
        .padding(.vertical, 3)
        .background(
            LinearGradient(
                colors: [
                    Color(red: 0.72, green: 0.52, blue: 0.04),
                    Color(red: 1.0, green: 0.75, blue: 0.03),
                    Color(red: 1.0, green: 0.84, blue: 0.0),
                    Color(red: 1.0, green: 0.75, blue: 0.03),
                    Color(red: 0.72, green: 0.52, blue: 0.04),
                ],
                startPoint: .leading,
                endPoint: .trailing
            )
        )
        .clipShape(.capsule)
        .overlay(
            // Shimmer sweep
            GeometryReader { geo in
                LinearGradient(
                    colors: [
                        .clear,
                        Color(white: 1, opacity: 0.45),
                        .clear,
                    ],
                    startPoint: .leading,
                    endPoint: .trailing
                )
                .frame(width: geo.size.width * 0.6)
                .offset(x: shimmerOffset * geo.size.width)
                .onAppear {
                    withAnimation(.linear(duration: 2.2).repeatForever(autoreverses: false)) {
                        shimmerOffset = 2
                    }
                }
            }
            .clipShape(.capsule)
            .allowsHitTesting(false)
        )
        .shadow(color: Color(red: 1.0, green: 0.84, blue: 0.0, opacity: 0.4), radius: 4)
    }
}
