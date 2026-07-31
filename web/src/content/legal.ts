// Shared legal content for RockScout — single source of truth for website pages.
// The in-app DisclaimerScreen mirrors these clauses so web + app stay in sync.

export const SITE = {
  name: "RockScout",
  domain: "rockscout.net",
  url: "https://rockscout.net",
  tagline: "Identify. Collect. Explore.",
  description:
    "RockScout is the field companion for rockhounds — AI rock & mineral identification powered by three models, a 900+ specimen database, dig-site maps, a collection tracker, a trip planner, an aurora forecaster, a Share-a-Spot deep-link tool, and a moderated community of hunters and traders.",
  supportEmail: "support@rockscout.net",
  pressEmail: "press@rockscout.net",
  playStoreUrl: "https://play.google.com/store/apps/details?id=com.rork.rockscout",
  appStoreNote: "iOS coming soon",
  foundedYear: 2025,
  jurisdiction: "United States",
  effectiveDate: "July 20, 2026",
};

export type LegalSection = {
  heading: string;
  body: string[]; // paragraphs
};

export const PRIVACY_SECTIONS: LegalSection[] = [
  {
    heading: "Overview",
    body: [
      "RockScout (\"we\", \"us\", \"our\") operates the RockScout Android application and the RockScout.net website. This Privacy Policy explains what information we collect, why we collect it, and the choices you have.",
      "We are committed to collecting only the data needed to run the app's features and to keeping it minimal. We do not sell your personal information.",
    ],
  },
  {
    heading: "Account information",
    body: [
      "When you sign up, we collect your email address and a display name you choose. Authentication is handled by Supabase Auth, which stores your hashed credentials. Your email is used for account recovery, important service notices, and support replies.",
    ],
  },
  {
    heading: "Camera and photos",
    body: [
      "RockScout uses your device camera to capture images for AI rock and mineral identification, for saving captures to your collection, and for posting to community features. Images submitted for identification are sent to our backend identification service for analysis. You can also choose images from your photo library.",
      "Identification images may be retained to improve model quality unless you request deletion. Captures you save to your collection are stored in your account until you delete them.",
    ],
  },
  {
    heading: "Location",
    body: [
      "With your permission, RockScout uses your device's fine location to show nearby dig sites, gem shows, and other rockhounds on the map, to drop pins on your trip planner, and to surface relevant field guides.",
      "Optional background location and nearby-place alerts allow the app to notify you of interesting locations while you are out in the field. Background location is off by default and only enabled when you explicitly turn it on in settings.",
      "Your coarse location may be shown on the RockScouts proximity map if you opt in. You can switch this off at any time from Social Settings.",
    ],
  },
  {
    heading: "Push notifications",
    body: [
      "With your permission, we send push notifications for friend requests, messages, trade interest, marked-traded updates, location-approval notices, and optional engagement summaries. You can fine-tune which notifications you receive in Settings, or disable all notifications from your device system settings.",
    ],
  },
  {
    heading: "Subscriptions and billing",
    body: [
      "RockScout offers subscriptions and in-app purchases managed by RevenueCat. Payment is processed by Google Play Billing. We receive anonymous purchase state and entitlement information from RevenueCat so we can grant or revoke access. Your full payment card details never reach RockScout — they stay with Google Play.",
    ],
  },
  {
    heading: "Advertising",
    body: [
      "The free tier of RockScout may be supported by advertising served by Google AdMob. AdMob may collect a device advertising identifier and coarse diagnostic data to serve and measure ads. You can reset the advertising identifier in your device settings at any time.",
    ],
  },
  {
    heading: "User-generated content",
    body: [
      "Posts, comments, direct messages, trade listings, profile details, and images you upload are stored in our Supabase database and storage. You control this content and can delete it from the app. We may retain limited copies for safety and abuse investigations after deletion, as described below.",
      "All images uploaded to community features pass through automated moderation via our backend proxy to detect prohibited content (explicit, violent, or illegal imagery) before they are visible to others.",
    ],
  },
  {
    heading: "Crash and diagnostic logs",
    body: [
      "If the app crashes, a crash log may be written to your device's internal storage and, with your opt-in, uploaded to our diagnostic pipeline. These logs may include device model, OS version, app version, and a stack trace — never your specimen photos, location coordinates at rest, or message contents.",
    ],
  },
  {
    heading: "Children",
    body: [
      "RockScout is not intended for children under 13. We do not knowingly collect personal information from children under 13. If you believe a child has provided us personal information, contact support@rockscout.net and we will delete it.",
    ],
  },
  {
    heading: "Your choices and rights",
    body: [
      "You can review or delete your account, collection, captures, posts, and messages from within the app. You can revoke camera, location, and notification permissions from your device settings at any time.",
      "Depending on where you live, you may have rights to access, correct, export, or delete your personal data. To exercise any of these rights, email support@rockscout.net.",
    ],
  },
  {
    heading: "Data retention",
    body: [
      "We keep your account data for as long as your account is active. We delete or anonymize your data within 30 days of an account deletion request, except for limited records we must retain for fraud, abuse, or legal compliance investigations.",
    ],
  },
  {
    heading: "Security",
    body: [
      "We use TLS for data in transit and at rest where supported by our providers. No method of transmission or storage is fully secure, but we work to protect your data with industry-standard practices.",
    ],
  },
  {
    heading: "Changes to this policy",
    body: [
      "If we materially change this policy, we will notify you in the app and update the effective date above. Continued use after the effective date means you accept the updated policy.",
    ],
  },
  {
    heading: "Contact",
    body: [
      "Questions about this policy? Email support@rockscout.net. We aim to respond within 36 hours.",
    ],
  },
];

export const TERMS_SECTIONS: LegalSection[] = [
  {
    heading: "Agreement to terms",
    body: [
      "By creating a RockScout account or using any RockScout feature, you agree to these Terms of Service and the RockScout Privacy Policy. If you do not agree, do not create an account or use the app.",
    ],
  },
  {
    heading: "Eligibility",
    body: [
      "You must be at least 13 years old to use RockScout. Users between 13 and 18 must have a parent or guardian's permission. RockScout does not knowingly allow users under 13.",
    ],
  },
  {
    heading: "Your account",
    body: [
      "You are responsible for keeping your account credentials secure and for all activity under your account. Provide accurate information at sign-up and keep it current. You may delete your account at any time from the app.",
    ],
  },
  {
    heading: "Acceptable use",
    body: [
      "You agree not to: (a) post content that is illegal, hateful, threatening, harassing, sexually explicit, or that promotes violence; (b) impersonate another person or entity; (c) spam, scam, or mislead other users; (d) attempt to access another user's account or data; (e) reverse-engineer, scrape, or overload the service; (f) use RockScout for any commercial purpose without our written permission; or (g) violate any applicable law.",
      "RockScout uses automated profanity and image moderation, and we may remove content or restrict accounts that violate these rules.",
    ],
  },
  {
    heading: "User-generated content",
    body: [
      "You retain ownership of content you create in RockScout (posts, comments, images, trade listings). By posting, you grant RockScout a worldwide, non-exclusive, royalty-free license to host, display, and process that content within the app for the purpose of operating the service.",
      "You are solely responsible for your content and for ensuring you have the rights to post it. We may remove any content that violates these Terms or that we determine is harmful to the community.",
    ],
  },
  {
    heading: "Subscriptions, renewals, and cancellations",
    body: [
      "RockScout offers auto-renewing subscriptions and one-time in-app purchases. Subscriptions renew automatically through Google Play Billing until you cancel. You can cancel anytime from your Google Play subscriptions settings. Cancellation stops renewal at the end of the current billing period; you keep access until then.",
      "Refunds are governed by Google Play's refund policy. RockScout does not issue refunds directly. Pricing and plan details are shown in the app before any purchase.",
    ],
  },
  {
    heading: "Trade board and peer-to-peer exchanges",
    body: [
      "RockScout offers a trade board where users can list specimens they would like to swap and message each other to arrange exchanges. RockScout is not a party to any trade, does not facilitate payment, does not hold specimens in escrow, and does not guarantee the authenticity, value, or condition of any item.",
      "All trades are strictly between users and at your own risk. See our Trade & Swap Disclaimer and Safety & Meetup Notice before arranging any in-person exchange.",
    ],
  },
  {
    heading: "AI identification",
    body: [
      "RockScout's AI identification feature provides a best-effort suggestion based on the image you submit. Results are not a guarantee of identity, value, or safety. Always confirm identifications with a qualified expert before handling, cutting, or selling any specimen, and never handle specimens you suspect may be toxic, radioactive, or otherwise hazardous based solely on an AI result.",
    ],
  },
  {
    heading: "Disclaimers",
    body: [
      "RockScout is provided \"as is\" and \"as available\" without warranties of any kind, express or implied. We do not warrant that the app will be uninterrupted, error-free, secure, or that identification results are accurate.",
      "Use RockScout at your own risk. You are responsible for your own safety, compliance with local collecting laws, and the condition and authenticity of any specimen you acquire, trade, or handle.",
    ],
  },
  {
    heading: "Limitation of liability",
    body: [
      "To the maximum extent permitted by law, RockScout and its operators are not liable for any indirect, incidental, special, consequential, or punitive damages, or any loss of data, specimens, money, or goodwill, arising out of or related to your use of the app — including any trade, meetup, identification, or collection decision you make based on the app.",
      "Our total liability for any claim arising out of these Terms is limited to the amount you paid us in the 12 months preceding the claim, or $50, whichever is greater.",
    ],
  },
  {
    heading: "Indemnity",
    body: [
      "You agree to indemnify and hold RockScout harmless from any claim, loss, or damage arising out of your content, your conduct, your trades, or your violation of these Terms.",
    ],
  },
  {
    heading: "Suspension and termination",
    body: [
      "We may suspend or terminate your access to RockScout at any time for violation of these Terms or for conduct we determine is harmful to the community. You can stop using RockScout and delete your account at any time.",
    ],
  },
  {
    heading: "Governing law",
    body: [
      "These Terms are governed by the laws of the United States, without regard to conflict-of-law principles. Disputes will be resolved in the courts located in the United States.",
    ],
  },
  {
    heading: "Changes to these terms",
    body: [
      "We may update these Terms from time to time. We will notify you in the app of material changes and update the effective date. Continued use after the effective date means you accept the updated Terms.",
    ],
  },
  {
    heading: "Contact",
    body: ["Questions about these Terms? Email support@rockscout.net."],
  },
];

export const COMMUNITY_SECTIONS: LegalSection[] = [
  {
    heading: "Our community standard",
    body: [
      "RockScout is a community of rockhounds, collectors, and learners. These guidelines apply to all user-generated content: posts, comments, direct messages, trade listings, profile details, and images. They work alongside our Terms of Service.",
    ],
  },
  {
    heading: "Be respectful and helpful",
    body: [
      "Treat other users with respect. Disagreements about an identification or a trade are fine; personal attacks, slurs, bullying, and harassment are not.",
      "Help newcomers. Share what you know. Credit other collectors and sources for photos and information that are not yours.",
    ],
  },
  {
    heading: "Keep it family-friendly",
    body: [
      "RockScout is open to users 13 and up and is used by collectors of all ages. No nudity, sexual content, graphic violence, or illegal activity. RockScout uses automated image moderation on every upload and a profanity filter on text content.",
      "Content that slips past automation and is reported by users is reviewed and removed if it violates these guidelines.",
    ],
  },
  {
    heading: "No spam, scams, or misleading listings",
    body: [
      "Do not post repetitive, promotional, or off-topic content. Do not misrepresent a specimen's identity, origin, condition, or value in a trade listing or post. Do not use RockScout to coordinate off-platform scams.",
    ],
  },
  {
    heading: "Direct messages",
    body: [
      "Direct messages follow the same rules as public posts. Unsolicited harassment, sales pressure, or abusive messages are not allowed and are reportable. You can block any user from their profile or from a message thread.",
    ],
  },
  {
    heading: "Reporting and enforcement",
    body: [
      "Every post, comment, message, trade listing, and profile has a report option. Reports go to our moderation team and may result in content removal, warnings, temporary suspensions, or permanent bans.",
      "When you accumulate active reports, you will see an in-app warning with the count and a path to appeal. Repeated or serious violations result in escalating suspensions.",
    ],
  },
  {
    heading: "Appeals",
    body: [
      "If you believe a moderation action against you was wrong, use the Contact Us screen in the app (or email support@rockscout.net) to appeal. Include what happened and why you think the action was incorrect. We review appeals and respond within 36 hours.",
    ],
  },
  {
    heading: "Safety first",
    body: [
      "Community features can lead to in-person meetups for trades. Read our Safety & Meetup Notice before arranging to meet anyone. Do not share your home address or other identifying information in posts or messages.",
    ],
  },
];

export const TRADE_SECTIONS: LegalSection[] = [
  {
    heading: "Trades are between you and the other user",
    body: [
      "The RockScout trade board is a place to list specimens you would like to swap and to find other collectors interested in trading. Every trade that results is a private arrangement between two users.",
      "RockScout is not a party to any trade. RockScout is not a marketplace, not an auction site, not an escrow service, and not a payment processor.",
    ],
  },
  {
    heading: "No escrow, no guarantee",
    body: [
      "RockScout does not hold specimens, does not verify authenticity or condition, does not mediate disputes, and does not guarantee that either party will follow through on a trade. Any trade you arrange is at your own risk.",
      "If a trade goes wrong, you may report the other user via the app and we may take moderation action under our Community Guidelines, but RockScout cannot recover specimens, money, or time on your behalf.",
    ],
  },
  {
    heading: "No fees from RockScout",
    body: [
      "RockScout does not charge a fee for listing or completing a trade through the trade board. If anyone asks you to pay RockScout to facilitate a trade, that is a scam — report it.",
    ],
  },
  {
    heading: "Verify before you trade",
    body: [
      "Ask for additional photos, ask about provenance, ask about condition, and ask questions before agreeing to a trade. Use the app's AI identification and the specimen database as a starting point, not as proof of identity or value. For high-value specimens, seek a third-party appraisal.",
    ],
  },
  {
    heading: "Shipping versus in-person",
    body: [
      "Some trades are arranged to ship; others are arranged to meet in person. Either way, follow our Safety & Meetup Notice. For shipped trades, agree on shipping method, insurance, and timing in writing inside the app before you send anything.",
    ],
  },
  {
    heading: "Prohibited items",
    body: [
      "Do not list items that are illegal to own, sell, or ship in your jurisdiction — including but not limited to protected fossils, artifacts taken from public or tribal land, conflict minerals, and items that require permits you do not have. You are responsible for knowing and following the law where you collect, own, and trade.",
    ],
  },
];

export const SAFETY_SECTIONS: LegalSection[] = [
  {
    heading: "In-person meetups carry risk",
    body: [
      "RockScout's trade board, trip planner, and community features can lead to in-person meetings with people you do not know. Meeting a stranger to exchange specimens carries real risk. This notice is here so you can make safe choices.",
    ],
  },
  {
    heading: "Meet in public, well-lit places",
    body: [
      "Always arrange to meet in a busy, public, well-lit location — a coffee shop, a public library, a police station lobby, or a public park during daylight. Many police stations have a designated \"safe exchange zone\". If the other person refuses to meet in public, that is a red flag — do not proceed.",
    ],
  },
  {
    heading: "Bring a friend and tell someone",
    body: [
      "Bring a friend or family member to the meeting if you can. If you cannot, tell someone you trust where you are going, who you are meeting, and when you expect to be back. Share your live location with that person from your phone.",
    ],
  },
  {
    heading: "Do not share your home address",
    body: [
      "Never share your home address, your workplace, or other identifying location details in posts, comments, trade listings, or direct messages. Do not invite strangers to your home and do not agree to meet at a private residence for a first exchange.",
    ],
  },
  {
    heading: "Inspect before you trade",
    body: [
      "Inspect the specimen in person before you hand anything over. Check for damage, repairs, or misrepresentation. If something does not match what was described in the listing, you can walk away — you are not obligated to complete the trade.",
    ],
  },
  {
    heading: "Trust your instincts",
    body: [
      "If a situation feels off — pressure tactics, last-minute location changes, requests for cash beyond the agreed trade, or anything that makes you uncomfortable — leave. You can report the user in the app afterwards.",
    ],
  },
  {
    heading: "Age recommendation and minors",
    body: [
      "The free tier is recommended for everyone — kids, adults, beginners, and experts alike. The Premium tier is recommended for users 18 and older because it unlocks the social layer (friends, messaging, trade, and community). Safety is the first, second, and third rule at RockScout.",
      "RockScout is for users 13 and older, but minors should not arrange in-person meetups without a parent or guardian's permission and presence. If you are a minor, do not agree to meet a stranger from RockScout without your parent or guardian involved.",
      "Adults: never arrange to meet a minor one-on-one. If you discover the other user is a minor, end the conversation and do not proceed with a meetup.",
    ],
  },
  {
    heading: "If something goes wrong",
    body: [
      "If you are the victim of theft, fraud, assault, or any crime during a meetup, contact local emergency services first, then report the user in the app and email support@rockscout.net. We will cooperate with law enforcement as required.",
    ],
  },
];

export const COOKIES_SECTIONS: LegalSection[] = [
  {
    heading: "What this covers",
    body: [
      "RockScout.net and the RockScout app use a small number of cookies and SDK identifiers. This page lists each one and why it is there. We do not use tracking cookies for ad targeting on the website itself.",
    ],
  },
  {
    heading: "Google AdMob (in-app, free tier)",
    body: [
      "On the free tier of the RockScout app, Google AdMob may set an advertising identifier on your device to serve and measure ads. You can reset or clear the advertising identifier from your device's privacy settings.",
    ],
  },
  {
    heading: "RevenueCat (subscriptions)",
    body: [
      "RevenueCat uses a device identifier to track your subscription entitlement across reinstalls and devices. No advertising profile is built from this.",
    ],
  },
  {
    heading: "Supabase (authentication and storage)",
    body: [
      "Supabase Auth uses session tokens to keep you signed in. These are first-party, strictly necessary, and are cleared when you sign out.",
    ],
  },
  {
    heading: "Analytics",
    body: [
      "We may collect anonymous, aggregated usage analytics to understand which features are used and to fix bugs. These do not include your name, email, or specimen photos.",
    ],
  },
  {
    heading: "Your choices",
    body: [
      "On the website, most cookies are strictly necessary and cannot be disabled without breaking the site. In the app, you can reset the advertising identifier from device settings and control notifications and location permissions from device settings.",
    ],
  },
];
