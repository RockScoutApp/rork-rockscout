package com.rork.rockscout.data

/**
 * Curated artifact catalog — arrowheads, hand axes, flaked tools, effigies,
 * beads, shell tools, pipes, game discs, and pottery. STONE / SHELL / WOOD /
 * CERAMIC only (no apparel, baskets, or European trade goods).
 *
 * This is a SEPARATE list from [SeedData.allSpecimens]. Artifacts never enter
 * the main specimen database, search, or AI candidate set. The only connection
 * point is the ARTIFACTS chip in [CategoryFilterRow], which swaps the list
 * source at the screen level.
 */
object ArtifactSpecimens {

    private const val BASE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/"

    // ── Arrowheads (Lanceolate / Paleoindian) ───────────────────────────────
    val arrowheads: List<Artifact> = listOf(
        Artifact(
            id = "art-clovis-point",
            name = "Clovis Point",
            family = "Arrowheads",
            subFamily = "Lanceolate Point",
            tagline = "The spear tip that hunted mammoths at the end of the Ice Age",
            emoji = "🔺",
            accentHex = 0xFF8B6D4B,
            imageUrl = BASE + "ca44cafb-2e4f-4d3b-9334-174ceedf713b.png",
            whereFound = listOf("North America", "Central America"),
            description = "The Clovis point is the iconic fluted lanceolate spear point of the Paleoindian period. Large, finely flaked, and fluted on both faces, it was the weapon of choice for the first peoples of the Americas who hunted megafauna like mammoths and giant bison.",
            howMade = "Knapped from high-quality chert, obsidian, or agate using hard-hammer percussion followed by pressure flaking. The distinctive flutes were created by removing long flakes from the base — a technique so demanding that fewer than 1 in 100 modern knappers can reproduce it.",
            funFacts = listOf(
                "First identified near Clovis, New Mexico in 1929.",
                "Some Clovis points are over 8 inches long.",
                "The flute may have helped seat the point onto a wooden foreshaft.",
            ),
            tribe = "Paleoindian (Clovis culture)",
            timePeriod = "Paleoindian · 10,000–9,000 BCE",
        ),
        Artifact(
            id = "art-folsom-point",
            name = "Folsom Point",
            family = "Arrowheads",
            subFamily = "Lanceolate Point",
            tagline = "Smaller, finer, fluted — built for the bison runners",
            emoji = "🔺",
            accentHex = 0xFF9E8B6D,
            imageUrl = BASE + "89f466fe-00b0-4300-98bb-637799db6530.png",
            whereFound = listOf("Great Plains", "Southwest"),
            description = "The Folsom point is a smaller, more refined lanceolate point than Clovis, with deep parallel fluting running nearly the full length of the blade. It marks a shift to hunting modern bison after the megafauna extinction.",
            howMade = "Knapped from fine-grained chert or flint. The deep fluting was achieved by a specialized jig technique — the knapper would clamp the preform and drive a channel flake off each face with a billet.",
            funFacts = listOf(
                "Named for Folsom, New Mexico, where a point was found embedded in extinct bison bones in 1908.",
                "The fluting is so deep it nearly reaches the tip — a feat few modern knappers can replicate.",
                "Folsom hunters used atlatls to launch these darts at running bison.",
            ),
            tribe = "Paleoindian (Folsom culture)",
            timePeriod = "Paleoindian · 9,000–8,000 BCE",
        ),
        Artifact(
            id = "art-dalton-point",
            name = "Dalton Point",
            family = "Arrowheads",
            subFamily = "Lanceolate Point",
            tagline = "The transitional blade that bridged the Paleoindian and Archaic worlds",
            emoji = "🔺",
            accentHex = 0xFFB89A6A,
            imageUrl = BASE + "bb77784b-f258-4061-adf4-af09386e0415.png",
            whereFound = listOf("Southeast", "Mid-South"),
            description = "The Dalton point is a medium lanceolate point with a concave base and finely ground edges. It bridges the gap between the fluted Paleoindian points and the stemmed Archaic points, showing the transition as the climate warmed and megafauna disappeared.",
            howMade = "Knapped from tan to cream chert. The edges were ground smooth near the base for hafting, and the blade was resharpened repeatedly — many Daltons show beveling from years of reuse.",
            funFacts = listOf(
                "Dalton points were often resharpened so many times they became tiny 'thumbnail' scrapers.",
                "The concave base may have helped bind the point to the haft with pitch.",
                "Some Daltons were placed as grave goods — among the earliest burial offerings in North America.",
            ),
            tribe = "Transitional Paleoindian / Early Archaic (Dalton culture)",
            timePeriod = "Transitional · 8,500–7,900 BCE",
        ),
        Artifact(
            id = "art-scottsbluff-point",
            name = "Scottsbluff Point",
            family = "Arrowheads",
            subFamily = "Stemmed Point",
            tagline = "A stemmed Paleoindian spear point from the bison plains",
            emoji = "🔺",
            accentHex = 0xFFC9A87C,
            imageUrl = BASE + "d6d57b37-541e-40a3-a391-13bbe91261ef.png",
            whereFound = listOf("Great Plains", "Western"),
            description = "The Scottsbluff is a large, stemmed Paleoindian spear point with parallel sides and a shouldered stem. It belongs to the Cody complex — a Paleoindian tradition that specialized in bison hunting on the open plains.",
            howMade = "Knapped from banded agate or chert using collateral flaking — each flake scar runs straight across the blade in parallel rows. The stem was ground smooth for hafting.",
            funFacts = listOf(
                "Named after Scottsbluff, Nebraska.",
                "Some Scottsbluff points are made from Teepee Canyon agate — a stone so beautiful it was traded hundreds of miles.",
                "The Cody complex also produced the ' Eden' point, a cousin with even finer flaking.",
            ),
            tribe = "Paleoindian (Cody complex)",
            timePeriod = "Paleoindian · 8,500–8,000 BCE",
        ),
        Artifact(
            id = "art-agate-basin-point",
            name = "Agate Basin Point",
            family = "Arrowheads",
            subFamily = "Lanceolate Point",
            tagline = "A long, slender Paleoindian killer — no stem, no flute, just edge",
            emoji = "🔺",
            accentHex = 0xFFA87C5C,
            imageUrl = BASE + "ee3c96a2-79f4-42fb-9691-b4149ffa8591.png",
            whereFound = listOf("Great Plains", "Rocky Mountains"),
            description = "The Agate Basin point is a large, unstemmed lanceolate spear point with long parallel flaking scars. It has no flute and no stem — it was hafted directly into a split wooden shaft.",
            howMade = "Knapped from maroon or banded chert using long, sweeping percussion flakes. The basal edges were ground smooth so the point wouldn't cut through its haft binding.",
            funFacts = listOf(
                "Named after the Agate Basin site in eastern Wyoming.",
                "Found in association with bison kill sites — some still embedded in bone.",
                "The absence of fluting makes it easier to identify than Clovis or Folsom.",
            ),
            tribe = "Paleoindian (Agate Basin culture)",
            timePeriod = "Paleoindian · 8,500–7,500 BCE",
        ),
        Artifact(
            id = "art-plano-point",
            name = "Plano Point",
            family = "Arrowheads",
            subFamily = "Lanceolate Point",
            tagline = "The unstemmed workhorse of the Late Paleoindian plains",
            emoji = "🔺",
            accentHex = 0xFFB8A88A,
            imageUrl = BASE + "f1d8e5a7-900b-42cb-857e-d9987e558420.png",
            whereFound = listOf("Great Plains", "Canada"),
            description = "Plano points are a broad category of large, unstemmed lanceolate spear points from the Late Paleoindian period. They lack fluting and stems, relying on fine collateral flaking and ground bases for hafting.",
            howMade = "Knapped from gray-tan chert using parallel to collateral flaking. The base was ground or smoothed for a snug fit into a split shaft.",
            funFacts = listOf(
                "'Plano' means 'flat' — referring to the unworked, flat base.",
                "Plano hunters drove bison over cliff jumps called 'pishkun' — some kills took hundreds of animals.",
                "The tradition spans over 2,000 years across the plains.",
            ),
            tribe = "Late Paleoindian (Plano cultures)",
            timePeriod = "Late Paleoindian · 8,000–5,000 BCE",
        ),
        Artifact(
            id = "art-adena-point",
            name = "Adena Point",
            family = "Arrowheads",
            subFamily = "Stemmed Point",
            tagline = "The broad-bladed spear of the mound builders",
            emoji = "🔺",
            accentHex = 0xFF9E8B6D,
            imageUrl = BASE + "76f13766-609a-4873-95e9-245cc119a04c.png",
            whereFound = listOf("Ohio Valley", "Eastern Central", "Northeastern", "Gulf Coastal", "Northern Central"),
            description = "The Adena point is a medium-to-large broad-bladed Early Woodland spear point with a straight to slightly convex stem. It was used by the Adena culture — among the first peoples of the Eastern Woodlands to build burial mounds.",
            howMade = "Knapped from olive-gray chert with rust inclusions. The broad blade was finished with pressure flaking, and the stem was ground for hafting. Regional variants differ in stem shape.",
            funFacts = listOf(
                "Adena people built some of the earliest burial mounds in North America.",
                "Regional variants span from the Gulf Coast to the Northeast — all merged here as one card.",
                "The Adena 'pipe' — a carved stone effigy of a kneeling figure — is Ohio's state artifact.",
            ),
            tribe = "Early Woodland (Adena culture)",
            timePeriod = "Early Woodland · 1,000–200 BCE",
        ),
        Artifact(
            id = "art-corner-notched-point",
            name = "Corner-Notched Point",
            family = "Arrowheads",
            subFamily = "Corner-Notched",
            tagline = "Barbed shoulders that bit deep — the Archaic upgrade",
            emoji = "🔺",
            accentHex = 0xFFD4A56A,
            imageUrl = BASE + "8ba894ed-9e54-4551-ac63-50d4c02fc244.png",
            whereFound = listOf("Eastern", "Southeast", "Midwest"),
            description = "Corner-notched points are a broad Archaic and Woodland category defined by sharp notches cut into the corners of the blade, creating barbed shoulders and a stemmed base. The barbs helped the point stay in a wound.",
            howMade = "Knapped from translucent amber chert with cream banding. The notches were created by precise pressure flaking from both faces — a single misstep could snap the barb.",
            funFacts = listOf(
                "The barbed design made the point harder to remove from prey — an early mechanical advantage.",
                "Corner-notched styles span thousands of years and dozens of named types (Kirk, Palmer, Guilford).",
                "Some were used as knives as well as spear points.",
            ),
            tribe = "Archaic / Woodland (various)",
            timePeriod = "Archaic–Woodland · 6,000 BCE–1,000 CE",
        ),
    )

    // ── Hand Axes ──────────────────────────────────────────────────────────
    val handAxes: List<Artifact> = listOf(
        Artifact(
            id = "art-acheulean-hand-axe",
            name = "Acheulean Hand Axe",
            family = "Hand Axes",
            subFamily = "Bifacial Core Tool",
            tagline = "The stone tool that built humanity — used for a million years",
            emoji = "🪓",
            accentHex = 0xFF6B5B4A,
            imageUrl = BASE + "6ed6f0d9-455c-4ec6-9164-9dbff4c5f6af.png",
            whereFound = listOf("Africa", "Europe", "Asia"),
            description = "The Acheulean hand axe is the longest-used tool in human history — a teardrop-shaped bifacial stone tool made by Homo erectus. It served as a multi-purpose tool: chopping, cutting, scraping, digging, and butchering.",
            howMade = "Knapped from flint or basalt using hard-hammer percussion. The maker removed flakes from both faces to create a symmetrical cutting edge around the entire perimeter, leaving a rounded butt for the hand.",
            funFacts = listOf(
                "Used continuously for over 1.5 million years — longer than any other tool.",
                "Some hand axes show no wear — they may have been status objects or courtship gifts.",
                "The symmetry is so consistent that archaeologists call it 'the Acheulean ideal'.",
            ),
            tribe = "Homo erectus / Lower Paleolithic",
            timePeriod = "Lower Paleolithic · 1.76 million–130,000 years ago",
        ),
        Artifact(
            id = "art-cordiform-hand-axe",
            name = "Cordiform Hand Axe",
            family = "Hand Axes",
            subFamily = "Heart-Shaped Bifacial",
            tagline = "The heart-shaped axe of the Neanderthals",
            emoji = "🪓",
            accentHex = 0xFFD49A6E,
            imageUrl = BASE + "24afd3bd-533d-442e-8776-75359fd418fe.png",
            whereFound = listOf("Europe", "Southwest Asia"),
            description = "The cordiform (heart-shaped) hand axe is a refined Middle Paleolithic bifacial tool associated with Neanderthals and early modern humans. Its carefully shaped point and rounded base make it one of the most aesthetically refined stone tools of the era.",
            howMade = "Knapped from honey-amber flint using soft-hammer percussion (bone or antler billet). Fine retouch around the entire edge created a sharp, symmetrical cutting surface.",
            funFacts = listOf(
                "'Cordiform' means 'heart-shaped' — some are so symmetrical they look intentional.",
                "Neanderthals may have preferred certain colored flints — an early sign of aesthetics.",
                "These tools overlap with the Mousterian tool kit.",
            ),
            tribe = "Neanderthals / Middle Paleolithic",
            timePeriod = "Middle Paleolithic · 300,000–40,000 years ago",
        ),
    )

    // ── Flaked Stone Tools ─────────────────────────────────────────────────
    val flakedStoneTools: List<Artifact> = listOf(
        Artifact(
            id = "art-end-scraper",
            name = "End Scraper",
            family = "Flaked Stone Tools",
            subFamily = "Unifacial Scraper",
            tagline = "The hide-prep tool that kept people warm through the Ice Age",
            emoji = "🔪",
            accentHex = 0xFFB8A88A,
            imageUrl = BASE + "0793c6ae-5a4a-4fd0-86c1-b8a54e644219.png",
            whereFound = listOf("North America", "Europe"),
            description = "The end scraper is a unifacial flake tool with steep retouch on one end, creating a scraping edge. It was primarily used to scrape fat and tissue from animal hides during leather and clothing production.",
            howMade = "Made on a blade-like flake of chert. The working end was retouched at a steep angle (60–90°) to create a durable scraping edge that wouldn't dull quickly.",
            funFacts = listOf(
                "End scrapers are among the most common tools at Paleoindian campsites.",
                "Wear patterns show some were used to scrape hides, others to work wood or bone.",
                "Spent end scrapers were often re-sharpened until nothing was left.",
            ),
            tribe = "Paleoindian / Archaic (various)",
            timePeriod = "Paleoindian–Archaic · 10,000 BCE onward",
        ),
    )

    // ── Stone Effigies ─────────────────────────────────────────────────────
    val stoneEffigies: List<Artifact> = listOf(
        Artifact(
            id = "art-stone-effigy",
            name = "Stone Effigy",
            family = "Stone Effigies",
            subFamily = "Ceremonial Carving",
            tagline = "Carved stone spirits — the physical form of a prayer",
            emoji = "🗿",
            accentHex = 0xFF5B8B6D,
            imageUrl = BASE + "c6431984-837e-4d4c-bca3-3f69f6afc3b2.png",
            whereFound = listOf("Southeast", "Mississippian"),
            description = "Stone effigies are free-standing carved stone figures representing animals, birds, serpents, or spiritual beings. They were ceremonial objects — used in rituals, placed in mounds, or carried as personal power objects.",
            howMade = "Carved from polished greenstone or steatite using stone chisels and abrasives. The surface was then polished with sand and water until smooth.",
            funFacts = listOf(
                "Bird effigies are associated with the Southeastern Ceremonial Complex.",
                "Some effigies were buried with their owners — personal guardians for the afterlife.",
                "The greenstone used often came from quarries hundreds of miles away.",
            ),
            tribe = "Mississippian / Southeastern Ceremonial Complex",
            timePeriod = "Mississippian · 1,000–1,500 CE",
        ),
    )

    // ── Native Beads (shell) ───────────────────────────────────────────────
    val nativeBeads: List<Artifact> = listOf(
        Artifact(
            id = "art-shell-beads",
            name = "Shell Beads",
            family = "Native Beads",
            subFamily = "Shell Disc & Tubular",
            tagline = "The first currency of the continent — wampum before wampum",
            emoji = "📿",
            accentHex = 0xFFE8DCC8,
            imageUrl = BASE + "87b24473-32ce-4354-a598-0f9ad054c3f3.png",
            whereFound = listOf("Eastern", "Gulf Coastal", "Northeastern"),
            description = "Shell beads are among the oldest ornaments in North America. Drilled disc beads and tubular beads were made from whelk and conch shells, strung into necklaces, belts, and wampum — serving as decoration, ceremony, and even currency.",
            howMade = "Shells were broken into blanks, ground round on a stone, and drilled with a stone bit or reed-and-sand drill. The beads were then polished smooth and strung on sinew or plant fiber.",
            funFacts = listOf(
                "Shell beads over 5,000 years old have been found in Archaic shell rings.",
                "Wampum belts recorded treaties and history — each bead was a 'word'.",
                "Purple wampum came from the inside of the quahog shell — rarer and more valuable.",
            ),
            tribe = "Archaic / Woodland / Northeastern (various)",
            timePeriod = "Archaic onward · 3,000 BCE–contact",
        ),
    )

    // ── Shell Tools ────────────────────────────────────────────────────────
    val shellTools: List<Artifact> = listOf(
        Artifact(
            id = "art-shell-gouge",
            name = "Shell Gouge",
            family = "Shell Tools",
            subFamily = "Shell Cutting Tool",
            tagline = "When stone was scarce, the shell became the blade",
            emoji = "🐚",
            accentHex = 0xFFE8DCC8,
            imageUrl = BASE + "c4270618-bb2a-4707-be40-d90c26042fdd.png",
            whereFound = listOf("Gulf Coastal", "Southeast"),
            description = "Shell gouges are large marine shells modified into cutting and scraping tools. The natural edge of the shell was sharpened, and a beveled working end was ground for precision work on wood, hide, or food.",
            howMade = "A large whelk shell was broken to expose the working edge, then ground on an abrasive stone to create a beveled gouge. The natural curvature of the shell provided the handle.",
            funFacts = listOf(
                "Shell tools were common in coastal areas where good knapping stone was scarce.",
                "Some shell gouges show wear from woodworking — they carved canoe paddles.",
                "The whelk shell's natural spiral makes an excellent grip.",
            ),
            tribe = "Archaic / Coastal (various)",
            timePeriod = "Archaic onward · 3,000 BCE–contact",
        ),
    )

    // ── Shell Effigies ─────────────────────────────────────────────────────
    val shellEffigies: List<Artifact> = listOf(
        Artifact(
            id = "art-shell-effigy",
            name = "Shell Effigy Pendant",
            family = "Shell Effigies",
            subFamily = "Engraved Pendant",
            tagline = "A whispered prayer carved on a shell",
            emoji = "🐚",
            accentHex = 0xFF6D8B7B,
            imageUrl = BASE + "7f21ba08-649f-4de9-ac9c-b46bfe3163e3.png",
            whereFound = listOf("Southeast", "Mississippian"),
            description = "Shell effigy pendants are engraved marine shells depicting serpents, birds, or spiritual figures. These were ceremonial objects — worn as pendants, placed in graves, or traded as sacred objects across the Mississippian world.",
            howMade = "A polished whelk shell was engraved using a flint graver. The design was cut into the shell's surface, sometimes filled with pigment to make the image stand out.",
            funFacts = listOf(
                "Some shell engravings depict the 'weeping eye' motif — a Mississippian symbol.",
                "Shells were traded from the Gulf Coast as far north as Ohio.",
                "Engraved shells are often found in elite graves — markers of high status.",
            ),
            tribe = "Mississippian / Southeastern Ceremonial Complex",
            timePeriod = "Mississippian · 1,000–1,500 CE",
        ),
    )

    // ── Pipes & Medicine Tubes ─────────────────────────────────────────────
    val pipesMedicineTubes: List<Artifact> = listOf(
        Artifact(
            id = "art-stone-pipe",
            name = "Stone Pipe",
            family = "Pipes & Medicine Tubes",
            subFamily = "Platform Pipe",
            tagline = "The ceremonial pipe — where smoke carried prayer",
            emoji = "🪨",
            accentHex = 0xFF8B4A3C,
            imageUrl = BASE + "66a123ba-d109-446d-b11a-e8ba913ef472.png",
            whereFound = listOf("Midwest", "Southeast"),
            description = "The platform pipe is a carved stone smoking pipe with a rectangular base and a bowl rising from the platform. It was used in ceremony and diplomacy — the smoke carried prayers to the spirits, and sharing a pipe sealed agreements.",
            howMade = "Carved from steatite (soapstone) or catlinite (pipestone) using stone tools. The bowl was drilled with a reed-and-sand bore, and the surface was polished. Catlinite turns red when fired.",
            funFacts = listOf(
                "Catlinite comes from the Pipestone Quarry in Minnesota — a site sacred to many tribes.",
                "Platform pipes were sometimes carved into effigy shapes — birds, animals, humans.",
                "The 'calumet' (peace pipe) became a symbol of diplomacy across the plains.",
            ),
            tribe = "Mississippian / Plains (various)",
            timePeriod = "Mississippian onward · 1,000 CE–contact",
        ),
        Artifact(
            id = "art-ceramic-pipe",
            name = "Ceramic Pipe",
            family = "Pipes & Medicine Tubes",
            subFamily = "Earthenware Pipe",
            tagline = "Fired clay and medicine smoke — the everyday ritual",
            emoji = "🧵",
            accentHex = 0xFFB86A4A,
            imageUrl = BASE + "c7dbd2a7-b921-47fc-b324-09e93392bec6.png",
            whereFound = listOf("Eastern", "Southeast"),
            description = "Ceramic pipes are molded clay smoking pipes with a rounded bowl and tapered stem. More common than stone pipes, they were used for everyday smoking of tobacco and herbal mixtures — both recreationally and medicinally.",
            howMade = "Hand-molded from local clay, then fired in an open pit. The warm orange-brown color comes from iron-rich clay fired in an oxidizing atmosphere.",
            funFacts = listOf(
                "Early European trade pipes were excluded — these are Native-made.",
                "Some ceramic pipes have decorated rims — stamped, incised, or punctated.",
                "The stem was often long enough to rest on the ground while seated.",
            ),
            tribe = "Woodland / Mississippian (various)",
            timePeriod = "Woodland onward · 500 BCE–contact",
        ),
    )

    // ── Game Discs ─────────────────────────────────────────────────────────
    val gameDiscs: List<Artifact> = listOf(
        Artifact(
            id = "art-game-disc",
            name = "Stone Game Disc",
            family = "Game Discs",
            subFamily = "Polished Disc",
            tagline = "The casino chip of the ancient world",
            emoji = "⚪",
            accentHex = 0xFF5A5A5A,
            imageUrl = BASE + "8055b243-c83a-48d1-a4e0-b596234af342.png",
            whereFound = listOf("Southeast", "Midwest"),
            description = "Stone game discs are flat, circular, polished stones used in Native games of chance and skill. They were thrown, spun, or rolled in betting games — and some may have been used in a version of 'chunkey', a Mississippian sport.",
            howMade = "Ground from dark schist or slate into a perfect disc, then polished with sand and water. Some have a central drilling — possibly for a spindle or string.",
            funFacts = listOf(
                "Chunkey was a Mississippian game where players threw spears at a rolling disc.",
                "Game discs were often buried with their owners — you took your luck to the afterlife.",
                "Some discs show wear from rolling — they were used for thousands of throws.",
            ),
            tribe = "Mississippian / Southeastern (various)",
            timePeriod = "Mississippian · 1,000–1,500 CE",
        ),
    )

    // ── Pottery ────────────────────────────────────────────────────────────
    val pottery: List<Artifact> = listOf(
        Artifact(
            id = "art-pottery-sherd",
            name = "Decorated Pottery Sherd",
            family = "Pottery",
            subFamily = "Incised & Cord-Marked",
            tagline = "A fragment of everyday life — fired and preserved for 1,000 years",
            emoji = "🏺",
            accentHex = 0xFFB8805A,
            imageUrl = BASE + "4ef0dcf4-1e57-4333-afe9-f5ecf055d4f5.png",
            whereFound = listOf("Southeast", "Midwest", "Northeastern"),
            description = "This decorated pottery sherd shows the geometric surface treatment typical of Woodland and Mississippian pottery — incised lines and cord-marked texture pressed into the wet clay before firing. Pottery revolutionized storage and cooking.",
            howMade = "Coil-built from local clay mixed with grit or shell temper. The surface was decorated by impressing cord-wrapped paddles or incising geometric patterns. Fired in an open pit to a warm tan color.",
            funFacts = listOf(
                "Pottery appeared in North America around 2,500 BCE — later than most of the world.",
                "Cord-marking wasn't just decoration — it helped the pot expand and contract during heating.",
                "A single pot can be identified to its tribe and time period from its surface treatment.",
            ),
            tribe = "Woodland / Mississippian (various)",
            timePeriod = "Woodland onward · 1,000 BCE–contact",
        ),
    )

    // ── Rare Wooden Artifacts ──────────────────────────────────────────────
    val woodenArtifacts: List<Artifact> = listOf(
        Artifact(
            id = "art-atlatl-handle",
            name = "Wooden Atlatl Hook",
            family = "Wooden Artifacts",
            subFamily = "Spear-Thrower",
            tagline = "The lever that made the spear fly twice as far",
            emoji = "🪵",
            accentHex = 0xFF7A5A3A,
            imageUrl = BASE + "a4685565-f6f1-4ff4-acd2-cec4de2f9a1b.png",
            whereFound = listOf("Great Basin", "Southwest"),
            description = "The atlatl is a wooden spear-thrower that acts as a lever, doubling the throwing range and force of a dart. Wooden atlatls are rare — they only survive in dry caves and arid environments.",
            howMade = "Carved from a single piece of hardwood with a finger grip and a hooked distal end. The hook engaged the nock of a dart, and the thrower's arm leveraged the flexible shaft for added power.",
            funFacts = listOf(
                "The atlatl predates the bow and arrow by thousands of years.",
                "Some atlatls had stone or shell weights attached — possibly for balance or ceremony.",
                "Atlatl darts could fly over 100 meters — further than most bows.",
            ),
            tribe = "Archaic / Great Basin (various)",
            timePeriod = "Paleoindian–Archaic · 10,000 BCE onward",
        ),
    )

    /** All artifacts, grouped by family in display order. */
    val allArtifacts: List<Artifact> = arrowheads +
        handAxes +
        flakedStoneTools +
        stoneEffigies +
        nativeBeads +
        shellTools +
        shellEffigies +
        pipesMedicineTubes +
        gameDiscs +
        pottery +
        woodenArtifacts

    /** Section list for the Artifacts screen (family title + its cards). */
    data class ArtifactSection(
        val title: String,
        val subtitle: String,
        val artifacts: List<Artifact>,
    )

    val sections: List<ArtifactSection> = listOf(
        ArtifactSection(
            title = "Arrowheads",
            subtitle = "Lanceolate, stemmed & notched points from the Paleoindian to Woodland periods",
            artifacts = arrowheads,
        ),
        ArtifactSection(
            title = "Hand Axes",
            subtitle = "Bifacial core tools from the Lower & Middle Paleolithic",
            artifacts = handAxes,
        ),
        ArtifactSection(
            title = "Flaked Stone Tools",
            subtitle = "Scrapers, burins, drills & unifacial tools",
            artifacts = flakedStoneTools,
        ),
        ArtifactSection(
            title = "Stone Effigies",
            subtitle = "Ceremonial carved stone figures",
            artifacts = stoneEffigies,
        ),
        ArtifactSection(
            title = "Native Beads",
            subtitle = "Shell, bone & stone beads (copper trade beads excluded)",
            artifacts = nativeBeads,
        ),
        ArtifactSection(
            title = "Shell Tools",
            subtitle = "Shell gouges, hoes & celts",
            artifacts = shellTools,
        ),
        ArtifactSection(
            title = "Shell Effigies",
            subtitle = "Engraved shell pendants & figures",
            artifacts = shellEffigies,
        ),
        ArtifactSection(
            title = "Pipes & Medicine Tubes",
            subtitle = "Stone & ceramic pipes (trade pipes excluded)",
            artifacts = pipesMedicineTubes,
        ),
        ArtifactSection(
            title = "Game Discs",
            subtitle = "Stone & ceramic discs for Native games",
            artifacts = gameDiscs,
        ),
        ArtifactSection(
            title = "Pottery",
            subtitle = "A representative cross-section of Native pottery types",
            artifacts = pottery,
        ),
        ArtifactSection(
            title = "Rare Wooden Artifacts",
            subtitle = "Preserved wooden tools — a rare finds sub-section",
            artifacts = woodenArtifacts,
        ),
    )

    /** Lookup an artifact by id (used by the detail screen). */
    fun byId(id: String): Artifact? = allArtifacts.firstOrNull { it.id == id }
}
