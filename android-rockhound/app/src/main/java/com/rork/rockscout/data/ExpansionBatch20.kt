package com.rork.rockscout.data

/**
 * Database expansion wave 20 — Phase 5: Gemstones by Species from Wikipedia.
 * Garnet varieties, tourmaline species, scapolite, rare mineral species,
 * zeolites, amphiboles, and jadeite variety from the List of Gemstones by Species.
 * Generated 2026-07-22.
 */
@Suppress("unused")
object ExpansionBatch20 {
    val specimens: List<Specimen> = listOf(

        // ════════════════════════════════════════════
        // GARNET VARIETIES (2)
        // ════════════════════════════════════════════

        Specimen(
            id = "garnet-mali",
            name = "Garnet, Mali",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Grandite (Andradite-Grossular Hybrid)",
            tagline = "Warm yellow-green garnet hybrid from Mali — a recent discovery.",
            emoji = "🟡",
            colorHex = 0xFFD4A017,
            description = "Mali Garnet is a natural hybrid between grossular and andradite garnets, found in Mali, West Africa. It displays a range of warm colors including yellow, green, gold, and brown. Mali garnet is notable for its high dispersion (fire) — even higher than diamond — which gives cut stones exceptional brilliance. It was only discovered in the 1990s.",
            formation = "Found in contact metamorphic skarn deposits. The grossular-andradite hybrid composition reflects the specific chemistry of the metamorphic environment.",
            hardness = "7–7.5",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "Ca₃(Al,Fe)₂(SiO₄)₃ (grossular-andradite solid solution)",
            commonColors = listOf("Yellow", "Green", "Gold", "Brownish-green", "Honey"),
            whereFound = listOf("Mali, West Africa (primary source)", "Namibia", "Tanzania"),
            funFacts = listOf(
                "Mali garnet was only discovered in the 1990s — it's one of the newest garnet varieties.",
                "It has exceptionally high dispersion (fire), even higher than diamond.",
                "The grossular-andradite hybrid composition gives it unique optical properties.",
                "Mali garnet can show a color-shift effect, appearing different in daylight vs. incandescent light."
            ),
            uses = "Gemstone (faceted and cabochon), jewelry, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "garnet-umbalite",
            name = "Garnet, Umbalite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Pyrope-Spessartine Garnet (Pink)",
            tagline = "Delicate pink garnet from the Umba Valley — one of the rarest garnet colors.",
            emoji = "🌸",
            colorHex = 0xFFE8A0C0,
            description = "Umbalite is a rare pink to pinkish-red garnet variety that is a hybrid between pyrope and spessartine. It is found primarily in the Umba Valley of Tanzania. The delicate pink color is quite rare among garnets, which are more commonly deep red or orange. Umbalite is highly valued by collectors for its unique color.",
            formation = "Found in alluvial deposits in the Umba Valley of Tanzania, derived from metamorphic source rocks. The pyrope-spessartine composition gives it the distinctive pink color.",
            hardness = "7–7.5",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "(Mg,Mn)₃Al₂(SiO₄)₃",
            commonColors = listOf("Pink", "Pinkish-red", "Rose-pink", "Peachy-pink"),
            whereFound = listOf("Umba Valley, Tanzania (primary source)", "Madagascar (rare)"),
            funFacts = listOf(
                "Umbalite is named after the Umba Valley in Tanzania where it was discovered.",
                "The delicate pink color is one of the rarest among garnets.",
                "It is a pyrope-spessartine hybrid — the same series as malaya garnet but with different proportions.",
                "Fine umbalite specimens can command high prices due to their rarity and unique color."
            ),
            uses = "Gemstone (faceted and cabochon), fine jewelry, collector specimens.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // TOURMALINE SPECIES (3)
        // ════════════════════════════════════════════

        Specimen(
            id = "tourmaline-fluor-liddicoatite",
            name = "Tourmaline, Fluor-liddicoatite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Calcium-Rich Tourmaline Species",
            tagline = "Calcium-rich tourmaline with spectacular multicolored zoned crystals.",
            emoji = "🌈",
            colorHex = 0xFF6B8E6B,
            description = "Fluor-liddicoatite is a calcium-rich tourmaline species named for its fluorine content and after American gemologist Richard Liddicoat. It is famous for spectacular multicolored crystals with concentric color zoning — a single crystal can show pink, green, brown, and white zones when cut crosswise. The best specimens come from Madagascar.",
            formation = "Found in granitic pegmatites, particularly in lithium-rich environments. The color zoning reflects changes in the fluid chemistry during crystal growth.",
            hardness = "7–7.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "Ca(Li₂Al)Al₆(Si₆O₁₈)(BO₃)₃(OH)₃F",
            commonColors = listOf("Multicolored", "Pink", "Green", "Brown", "White zones"),
            whereFound = listOf("Madagascar (primary source)", "Afghanistan", "Brazil", "Mozambique", "USA (California)"),
            funFacts = listOf(
                "Fluor-liddicoatite is named after Richard Liddicoat, the 'father of modern gemology'.",
                "Cross-sections of crystals show spectacular concentric color zoning — like tree rings.",
                "A single crystal can display pink, green, brown, and white color zones simultaneously.",
                "It was only recognized as a separate tourmaline species in 1998."
            ),
            uses = "Gemstone (faceted and sliced), collector specimens, jewelry.",
            rarity = "Rare"
        ),

        Specimen(
            id = "tourmaline-olenite",
            name = "Tourmaline, Olenite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Aluminum-Rich Tourmaline Species",
            tagline = "Aluminum-rich tourmaline — one of the rarest tourmaline species.",
            emoji = "⚪",
            colorHex = 0xFFD0D0E0,
            description = "Olenite is a rare aluminum-rich tourmaline species. It is one of the less common tourmaline species and is typically found in lithium-poor, aluminum-rich pegmatite environments. Olenite is usually pale-colored — white, pink, or light green. It was named after the Greek 'olene' meaning 'elbow', referring to the shape of early specimens.",
            formation = "Found in granitic pegmatites in aluminum-rich, lithium-poor environments. Associated with other tourmaline species and pegmatite minerals.",
            hardness = "7–7.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "NaAl₃Al₆(Si₆O₁₈)(BO₃)₃(OH)₃(OH)",
            commonColors = listOf("White", "Pale pink", "Light green", "Colorless"),
            whereFound = listOf("Kola Peninsula, Russia (type locality)", "Austria", "Mozambique", "Madagascar"),
            funFacts = listOf(
                "Olenite is named after the Greek 'olene' meaning 'elbow'.",
                "It is one of the rarest and least-known tourmaline species.",
                "It is the aluminum-richest member of the tourmaline group.",
                "Olenite was only recognized as a distinct species in 1994."
            ),
            uses = "Collector specimens, mineralogical research, rare gemstone.",
            rarity = "Very Rare"
        ),

        Specimen(
            id = "tourmaline-rossmanite",
            name = "Tourmaline, Rossmanite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Lithium Tourmaline Species",
            tagline = "Lithium-bearing tourmaline — the newest recognized tourmaline species.",
            emoji = "🌸",
            colorHex = 0xFFE8B0C0,
            description = "Rossmanite is a lithium-bearing tourmaline species and one of the newest tourmaline species to be recognized. It is typically pale pink to colorless and is the lithium analog of elbaite. Rossmanite is very rare and is found in only a few localities worldwide. Named after California Institute of Technology mineralogist George Rossman.",
            formation = "Found in lithium-rich granitic pegmatites. Associated with other lithium tourmaline species like elbaite.",
            hardness = "7–7.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "(LiAl₂)Al₆(Si₆O₁₈)(BO₃)₃(OH)₃(OH)",
            commonColors = listOf("Pale pink", "Colorless", "Light pink"),
            whereFound = listOf("Rosicky, Czech Republic (type locality)", "Madagascar", "Brazil", "Mozambique"),
            funFacts = listOf(
                "Rossmanite is named after George Rossman, a renowned mineralogist at Caltech.",
                "It is one of the newest tourmaline species, only recognized in 1998.",
                "It is the lithium analog of elbaite — the most common gem tourmaline.",
                "It is so rare that only a handful of localities produce it."
            ),
            uses = "Collector specimens, mineralogical research, extremely rare gemstone.",
            rarity = "Very Rare"
        ),

        // ════════════════════════════════════════════
        // SCAPOLITE & OTHER (2)
        // ════════════════════════════════════════════

        Specimen(
            id = "meionite",
            name = "Meionite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Calcium-Rich Scapolite Endmember",
            tagline = "Calcium-rich endmember of the scapolite group — sometimes cut as a gem.",
            emoji = "⚪",
            colorHex = 0xFFE8E0D0,
            description = "Meionite is the calcium-rich endmember of the scapolite mineral group. It forms tetragonal crystals that are typically white, grayish, or colorless. Meionite is found in metamorphic rocks, particularly in skarns and contact metamorphic zones. Some transparent specimens are cut as gemstones, though it is not commonly seen in jewelry.",
            formation = "Found in contact metamorphic rocks, particularly skarns and impure marbles. Associated with other calc-silicate minerals like diopside and wollastonite.",
            hardness = "5–6",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Tetragonal",
            chemicalFormula = "Ca₄Al₆Si₆O₂₄CO₃",
            commonColors = listOf("White", "Colorless", "Grayish-white", "Pale yellow"),
            whereFound = listOf("Vesuvius, Italy (type locality)", "New York, USA", "Ontario, Canada", "Madagascar", "Tanzania"),
            funFacts = listOf(
                "Meionite is the calcium endmember of the scapolite group.",
                "The name comes from Greek 'meion' meaning 'less', referring to its pyramidal crystal shape.",
                "Some meionite specimens are fluorescent under UV light.",
                "Transparent meionite is occasionally cut as a collector gemstone."
            ),
            uses = "Collector specimens, rare gemstone, mineralogical research.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "adularia",
            name = "Adularia",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Orthoclase Variety (Moonstone Base)",
            tagline = "Transparent to translucent orthoclase — the mineral behind moonstone's glow.",
            emoji = "🌙",
            colorHex = 0xFFE8E0E8,
            description = "Adularia is a low-temperature variety of orthoclase feldspar that forms in Alpine fissures. It is the primary mineral that produces moonstone — when adularia forms with the right internal structure, it displays a billowy white or blue sheen called adularescence. Adularia was named after the Adula Mountains in Switzerland.",
            formation = "Found in Alpine-type fissure veins in metamorphic rocks. Forms at low temperatures from hydrothermal fluids in fractures.",
            hardness = "6–6.5",
            luster = "Vitreous to pearly",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "KAlSi₃O₈",
            commonColors = listOf("Colorless", "White", "Pale blue sheen", "Milky white"),
            whereFound = listOf("Adula Mountains, Switzerland (type locality)", "St. Gotthard, Switzerland", "Austria", "Norway", "Madagascar"),
            funFacts = listOf(
                "Adularia is the mineral that gives moonstone its famous glow — called 'adularescence'.",
                "Named after the Adula Mountains in Switzerland.",
                "The billowy blue-white sheen is caused by light scattering from alternating layers of different feldspar compositions.",
                "Not all adularia shows adularescence — only specimens with the right exsolution structure."
            ),
            uses = "Gemstone (as moonstone), collector specimens, mineralogical research.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // DISTINCT MINERAL SPECIES (5)
        // ════════════════════════════════════════════

        Specimen(
            id = "geuda",
            name = "Geuda",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Milky Corundum (Sapphire Precursor)",
            tagline = "Milky, semi-transparent corundum that becomes blue sapphire when heat-treated.",
            emoji = "⚪",
            colorHex = 0xFFD0D0E0,
            description = "Geuda is a milky, semi-transparent variety of corundum that is the primary precursor material for heat-treated blue sapphires. Before treatment, geuda appears cloudy and milky due to microscopic rutile inclusions. When heated to high temperatures, the rutile dissolves into the corundum, producing a clear blue sapphire. Most commercial blue sapphires start as geuda.",
            formation = "Found in alluvial deposits derived from metamorphic source rocks. The milky appearance is caused by exsolved rutile silk within the corundum.",
            hardness = "9",
            luster = "Vitreous to silky",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "Al₂O₃ (with rutile inclusions)",
            commonColors = listOf("Milky white", "Semi-transparent", "Grayish-white", "Bluish-white"),
            whereFound = listOf("Sri Lanka (primary source)", "Madagascar", "Tanzania", "Burma (Myanmar)", "Thailand"),
            funFacts = listOf(
                "Most commercial blue sapphires are made from geuda that has been heat-treated.",
                "Before heating, geuda looks milky and cloudy — not very attractive.",
                "The rutile 'silk' that makes it milky dissolves during heating, turning it blue.",
                "Sri Lanka is the world's primary source of gem-quality geuda."
            ),
            uses = "Heat-treated to create blue sapphire, collector specimens (raw form), gemstone industry.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "piemontite",
            name = "Piemontite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Manganese Epidote (Purple-Red)",
            tagline = "Manganese-rich epidote with distinctive purple-red color and striated crystals.",
            emoji = "🟣",
            colorHex = 0xFF8B4D6B,
            description = "Piemontite is a manganese-rich variety of the epidote mineral group. It has a distinctive dark red to purple-red color caused by manganese content. Piemontite forms prismatic, striated crystals in metamorphic rocks. It is sometimes cut as a cabochon gemstone, though it is quite rare in gem quality.",
            formation = "Found in low-grade metamorphic rocks, particularly manganese-rich schists and in some hydrothermal veins. Associated with quartz, epidote, and other metamorphic minerals.",
            hardness = "6",
            luster = "Vitreous",
            streak = "Reddish-brown to gray",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Ca₂(Al,Mn,Fe)₃(SiO₄)(Si₂O₇)O(OH)",
            commonColors = listOf("Dark red", "Purple-red", "Reddish-brown", "Blackish-red"),
            whereFound = listOf("Saint Marcel, Piedmont, Italy (type locality)", "Japan", "Sweden", "Norway", "USA (Alaska)"),
            funFacts = listOf(
                "Named after the Piedmont region of Italy where it was first found.",
                "The purple-red color comes from manganese substituting for aluminum in the crystal structure.",
                "It is the manganese-rich analog of epidote.",
                "Some piemontite specimens show a striking pleochroism (different colors from different angles)."
            ),
            uses = "Collector specimens, cabochon gemstone (rare), mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "stichtite",
            name = "Stichtite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Magnesium Chromium Carbonate Hydroxide (Serpentine Subgroup)",
            tagline = "Pink-purple serpentine-group mineral — often found intergrown with green serpentine.",
            emoji = "🟣",
            colorHex = 0xFFB8738C,
            description = "Stichtite is a magnesium chromium carbonate hydroxide mineral in the serpentine subgroup. It has a distinctive pink to purple color caused by chromium. Stichtite is often found intergrown with green serpentine, creating striking pink and green composite specimens. It is popular for cabochons and carvings, and is the main component of atlantisite.",
            formation = "Found in serpentine deposits where chromium-rich fluids have altered the serpentine. Common in ophiolite complexes and serpentinized ultramafic rocks.",
            hardness = "1.5–2",
            luster = "Waxy to pearly",
            streak = "Pink to white",
            crystalSystem = "Trigonal",
            chemicalFormula = "Mg₆Cr₂CO₃(OH)₁₆·4H₂O",
            commonColors = listOf("Pink", "Purple", "Rose-pink", "Lilac"),
            whereFound = listOf("Dundas, Tasmania, Australia (type locality)", "South Africa", "Canada (Quebec)", "Russia", "Morocco"),
            funFacts = listOf(
                "Stichtite is named after Robert Sticht, a Tasmanian mining manager.",
                "The pink-purple color is caused by chromium.",
                "When intergrown with green serpentine, it creates 'atlantisite' — a popular decorative stone.",
                "Tasmania is the world's primary source of stichtite.",
                "It is soft enough to be carved and is popular for cabochons and ornamental objects."
            ),
            uses = "Cabochons, carvings, decorative objects, collector specimens, component of atlantisite.",
            rarity = "Rare"
        ),

        Specimen(
            id = "californite",
            name = "Californite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Vesuvianite/Idocrase Variety (Green)",
            tagline = "Massive green vesuvianite from California — sometimes called 'California jade'.",
            emoji = "🟢",
            colorHex = 0xFF4A8B5C,
            description = "Californite is a massive, compact variety of vesuvianite (idocrase) that resembles jade. It is typically green to yellowish-green and is found in contact metamorphic zones in California. Because of its jade-like appearance, it is sometimes called 'California jade' or 'American jade'. Californite takes a good polish and is used for cabochons and carvings.",
            formation = "Found in contact metamorphic zones where limestone has been intruded by igneous rock. Associated with other calc-silicate minerals.",
            hardness = "6–6.5",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Tetragonal (massive)",
            chemicalFormula = "Ca₁₀(Mg,Fe)₂Al₄(SiO₄)₅(Si₂O₇)₂(OH)₄",
            commonColors = listOf("Green", "Yellowish-green", "Dark green", "Olive green"),
            whereFound = listOf("California, USA (type locality)", "Pakistan", "Italy", "Norway", "Canada"),
            funFacts = listOf(
                "Californite is sometimes called 'California jade' or 'American jade'.",
                "It is actually vesuvianite (idocrase), not jade — but it looks similar.",
                "It takes an excellent polish, making it popular for cabochons and carvings.",
                "Named after California where it was first identified."
            ),
            uses = "Cabochons, carvings, beads, decorative objects, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "bustamite",
            name = "Bustamite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Manganese Calcium Silicate (Pyroxenoid)",
            tagline = "Pink to brown manganese calcium silicate — the manganese analog of wollastonite.",
            emoji = "🌸",
            colorHex = 0xFFC8788C,
            description = "Bustamite is a manganese calcium silicate mineral in the pyroxenoid group. It has a distinctive pink to brownish-pink color and forms massive aggregates or bladed crystals. Bustamite is the manganese analog of wollastonite and is found in manganese-rich metamorphic deposits. It is sometimes cut as a cabochon for its attractive pink color.",
            formation = "Found in manganese-rich metamorphic deposits, particularly in skarns and contact metamorphic zones. Associated with rhodonite, rhodochrosite, and other manganese minerals.",
            hardness = "5.5–6.5",
            luster = "Vitreous",
            streak = "White to pinkish",
            crystalSystem = "Triclinic",
            chemicalFormula = "CaMnSi₂O₆ (Mn,Ca)SiO₃",
            commonColors = listOf("Pink", "Brownish-pink", "Salmon-pink", "Pale pink"),
            whereFound = listOf("Tetela de Ocampo, Mexico (type locality)", "Broken Hill, Australia", "Franklin, New Jersey, USA", "Sweden", "Japan"),
            funFacts = listOf(
                "Named after Mexican general and mining engineer Miguel Bustamante.",
                "It is the manganese analog of wollastonite.",
                "Bustamite can be confused with rhodonite — both are pink manganese silicates.",
                "Franklin, New Jersey produced some of the finest bustamite specimens."
            ),
            uses = "Collector specimens, cabochon gemstone (rare), manganese ore (minor).",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // ZEOLITE GROUP (2)
        // ════════════════════════════════════════════

        Specimen(
            id = "goosecreekite",
            name = "Goosecreekite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Zeolite Group",
            tagline = "Rare white zeolite forming distinctive radiating crystal sprays.",
            emoji = "⚪",
            colorHex = 0xFFE8E8E0,
            description = "Goosecreekite is a rare zeolite mineral that forms white to colorless radiating sprays of acicular (needle-like) crystals. It is found in cavities in basaltic volcanic rocks and is one of the rarer zeolite species. Goosecreekite was named after its type locality at Goose Creek, Virginia.",
            formation = "Found in vesicles and cavities in basaltic volcanic rocks. Forms by alteration of volcanic glass in the presence of saline waters.",
            hardness = "4–4.5",
            luster = "Vitreous to silky",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "CaAl₂Si₆O₁₆·5H₂O",
            commonColors = listOf("White", "Colorless", "Cream"),
            whereFound = listOf("Goose Creek, Virginia, USA (type locality)", "Bombay, India", "Iceland", "Nova Scotia, Canada", "Oregon, USA"),
            funFacts = listOf(
                "Named after Goose Creek in Virginia, the type locality.",
                "It is one of the rarer zeolite species.",
                "The radiating sprays of needle-like crystals are distinctive.",
                "Goosecreekite is easily confused with other white zeolites like stilbite."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "stellerite",
            name = "Stellerite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Zeolite Group",
            tagline = "White to orange zeolite forming tabular crystal clusters.",
            emoji = "⚪",
            colorHex = 0xFFE8D8C0,
            description = "Stellerite is a zeolite mineral that forms white, cream, or pale orange tabular to bladed crystals in radiating or sheaf-like aggregates. It is closely related to stilbite and stellerite is often confused with it. Stellerite is found in volcanic cavities and is named after Georg Wilhelm Steller, the German naturalist.",
            formation = "Found in vesicles and cavities in volcanic rocks, particularly basalts and andesites. Forms by alteration of volcanic glass.",
            hardness = "3.5–4",
            luster = "Vitreous to pearly",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Ca₄(Si₂₈Al₈)O₇₂·28H₂O (CaAl₂Si₇O₁₈·7H₂O)",
            commonColors = listOf("White", "Cream", "Pale orange", "Yellowish-white"),
            whereFound = listOf("Commander Islands, Russia (type locality)", "Oregon, USA", "Nova Scotia, Canada", "Iceland", "India"),
            funFacts = listOf(
                "Named after Georg Wilhelm Steller, the German naturalist and explorer.",
                "Stellerite is closely related to stilbite — the two minerals look very similar.",
                "The type locality is the remote Commander Islands in the Bering Sea.",
                "It forms attractive 'sheaf-like' crystal aggregates popular with collectors."
            ),
            uses = "Collector specimens, molecular sieve applications, mineralogical research.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // AMPHIBOLE (1)
        // ════════════════════════════════════════════

        Specimen(
            id = "richterite",
            name = "Richterite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Sodium Calcium Amphibole",
            tagline = "Rare blue to brown amphibole mineral from metamorphic environments.",
            emoji = "🔵",
            colorHex = 0xFF4F6B8C,
            description = "Richterite is a sodium calcium magnesium iron amphibole mineral. It is typically brown, but a rare blue variety exists. The blue richterite from Afghanistan and Pakistan is sometimes cut as a gemstone. Richterite forms elongated, prismatic crystals in metamorphic rocks and is named after German mineralogist Theodor Richter.",
            formation = "Found in metamorphic rocks, particularly in contact metamorphic zones and in some skarns. The blue variety is associated with specific sodium-rich, high-temperature conditions.",
            hardness = "5–6",
            luster = "Vitreous to silky",
            streak = "White to grayish",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Na₂Ca(Mg,Fe)₅Si₈O₂₂(OH)₂",
            commonColors = listOf("Blue", "Brown", "Colorless", "Greenish-brown"),
            whereFound = listOf("Langban, Sweden (type locality)", "Afghanistan (blue variety)", "Pakistan", "Russia", "Canada"),
            funFacts = listOf(
                "Named after German mineralogist Theodor Richter.",
                "The blue variety from Afghanistan is sometimes cut as a rare gemstone.",
                "It is a member of the amphibole group, which includes hornblende and actinolite.",
                "Blue richterite can resemble blue tourmaline or tanzanite when cut."
            ),
            uses = "Collector specimens, rare gemstone (blue variety), mineralogical research.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // ADDITIONAL (1)
        // ════════════════════════════════════════════

        Specimen(
            id = "chloromelanite",
            name = "Chloromelanite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Dark Green Jadeite Variety (Iron-Rich)",
            tagline = "Dark green iron-rich jadeite — the deepest colored form of jadeite jade.",
            emoji = "🟢",
            colorHex = 0xFF1A4A2A,
            description = "Chloromelanite is a dark green to nearly black variety of jadeite, colored by high iron content. The name means 'green-black' in Greek. It is the darkest natural color of jadeite and was historically carved by Mesoamerican cultures. Chloromelanite is denser and darker than typical green jadeite due to its iron-rich composition.",
            formation = "Found in high-pressure metamorphic environments similar to jadeitite, but with higher iron content. The iron substitutes for aluminum in the jadeite crystal structure.",
            hardness = "6.5–7",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Na(Al,Fe)Si₂O₆",
            commonColors = listOf("Dark green", "Blackish-green", "Deep green", "Nearly black"),
            whereFound = listOf("Burma (Myanmar)", "Guatemala", "Mexico", "Japan", "California, USA"),
            funFacts = listOf(
                "The name 'chloromelanite' means 'green-black' in Greek.",
                "It is the darkest natural variety of jadeite jade.",
                "The dark color comes from high iron content substituting for aluminum.",
                "Mesoamerican cultures, particularly the Olmec and Maya, carved chloromelanite.",
                "It is sometimes called 'Olmec blue jade' in archaeological contexts."
            ),
            uses = "Jade carvings, cabochons, jewelry, cultural artifacts, collector specimens.",
            rarity = "Rare"
        )
    )
}
