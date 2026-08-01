package com.rork.rockscout.data

/**
 * Extra kid-friendly facts for a dictionary entry: how to say the name, what
 * the name means, and how the animal got around.
 *
 * Sourced from the Natural History Museum (London) Dino Directory dataset.
 */
data class DinoFactExtras(
    /** Phonetic spelling, e.g. "BRAK-ee-oh-sore-us". */
    val pronunciation: String,
    /** Literal translation of the scientific name, e.g. "arm lizard". */
    val meaning: String,
    /** How the animal moved, e.g. "on 4 legs". */
    val locomotion: String,
)

object DinoExtraFacts {

    private val extras: Map<String, DinoFactExtras> = mapOf(
        "abelisaurus" to DinoFactExtras("ah-BEEL-ee-sore-us", "Abel's lizard", ""),
        "achelousaurus" to DinoFactExtras("ah-KEL-oo-SORE-us", "Achelous' lizard", ""),
        "achillobator" to DinoFactExtras("a-kil-oh-bah-tor", "Achilles hero", ""),
        "albertosaurus" to DinoFactExtras("al-BERT-oh-saw-russ", "Alberta lizard", "on 2 legs"),
        "allosaurus" to DinoFactExtras("AL-oh-saw-russ", "different lizard", "on 2 legs"),
        "amargasaurus" to DinoFactExtras("A-MARG-oh-sore-us", "Amarga lizard", ""),
        "ankylosaurus" to DinoFactExtras("An-KYE-low-sore-us", "fused lizard", "on 4 legs"),
        "apatosaurus" to DinoFactExtras("ah-PAT-oh-sore-us", "deceptive lizard", ""),
        "archaeopteryx" to DinoFactExtras("ark-ee-OPT-er-ix", "ancient wing", "on 2 legs and by flying"),
        "argentinosaurus" to DinoFactExtras("AR-gent-eeno-sore-us", "Argentina lizard", "on four legs"),
        "baryonyx" to DinoFactExtras("bah-ree-ON-icks", "heavy claw", "on 2 legs"),
        "beipiaosaurus" to DinoFactExtras("bay-pyow-sore-us", "Beipiao lizard", ""),
        "brachiosaurus" to DinoFactExtras("BRAK-ee-oh-sore-us", "arm lizard", "on 4 legs"),
        "camarasaurus" to DinoFactExtras("KAM-ar-a-sore-us", "chambered lizard", "on 4 legs"),
        "carcharodontosaurus" to DinoFactExtras("kar-KAR-o-don-toe-sore-us", "Carcharodon lizard", ""),
        "carnotaurus" to DinoFactExtras("Kar-noh-TORE-us", "carnivorous bull", ""),
        "centrosaurus" to DinoFactExtras("Cen-TROH-sore-us", "sharp pointed lizard", "on 4 legs"),
        "ceratosaurus" to DinoFactExtras("Ser-AT-oh-sore-us", "horned lizard", ""),
        "citipati" to DinoFactExtras("chit-i-puh-tih", "lord of the funeral pyre", ""),
        "coelophysis" to DinoFactExtras("seel-OH-fie-sis", "hollow form", "on 2 legs"),
        "compsognathus" to DinoFactExtras("Komp-sog-NATH-us", "pretty jaw", "on 2 legs"),
        "concavenator" to DinoFactExtras("kon-ka-VEN-at-or", "hunter from the Spanish province of Cuenca", ""),
        "cryolophosaurus" to DinoFactExtras("cry-o-loaf-oh-sore-us", "frozen crested lizard", "on 2 legs"),
        "daspletosaurus" to DinoFactExtras("dass-PLEE-toe-SORE-us", "frightful lizard", ""),
        "deinocheirus" to DinoFactExtras("DINE-oh-KIRE-us", "terrible hand", ""),
        "deinonychus" to DinoFactExtras("dye-NON-ick-us", "terrible claw", "on 2 legs"),
        "dilophosaurus" to DinoFactExtras("die-LOAF-oh-sore-us", "two-crested lizard", "on 2 legs"),
        "diplodocus" to DinoFactExtras("DIP-low-DOCK-us", "double beam", "on 4 legs"),
        "dreadnoughtus" to DinoFactExtras("dred-NAW-tus", "fears nothing", "on four legs"),
        "edmontosaurus" to DinoFactExtras("ed-MON-toe-sore-us", "Edmonton lizard", "on 2 or 4 legs"),
        "einiosaurus" to DinoFactExtras("ie-nee-oh-sore-us", "bison lizard", ""),
        "eoraptor" to DinoFactExtras("EE-oh-RAP-tor", "early plunderer", "on 2 legs"),
        "euoplocephalus" to DinoFactExtras("you-OH-plo-kef-ah-luss", "well-armoured head", "on 4 legs"),
        "gallimimus" to DinoFactExtras("galley-MIME-us", "chicken mimic", "on 2 legs"),
        "giganotosaurus" to DinoFactExtras("gig-an-OH-toe-SORE-us", "giant southern lizard", "on 2 legs"),
        "herrerasaurus" to DinoFactExtras("herr-ray-rah-SORE-us", "Herrera's lizard", ""),
        "heterodontosaurus" to DinoFactExtras("HET-er-oh-DON’T-oh-sore-us", "different-teeth lizard", ""),
        "hypsilophodon" to DinoFactExtras("hip-sih-LOH-foh-don", "high-crested tooth", "on 2 legs"),
        "iguanodon" to DinoFactExtras("ig-WHA-noh-don", "iguana tooth", "on 2 or 4 legs"),
        "kentrosaurus" to DinoFactExtras("ken-TROH-sore-us", "spiky lizard", ""),
        "lambeosaurus" to DinoFactExtras("lam-BEE-oh-SORE-us", "Lambe's lizard", ""),
        "leaellynasaura" to DinoFactExtras("LEE-ELL-IN-a-SORE-a", "Leaellyn's lizard", ""),
        "leptoceratops" to DinoFactExtras("lep-toh-ker-ah-tops", "slim horned face", ""),
        "lesothosaurus" to DinoFactExtras("Le-SO-toe-sore-us", "Lesotho lizard", "on 2 legs"),
        "liliensternus" to DinoFactExtras("lil-ee-en-shtern-us", "[for] Lilienstern", ""),
        "maiasaura" to DinoFactExtras("my-ah-SORE-ah", "good mother lizard", "on 2 or 4 legs"),
        "majungasaurus" to DinoFactExtras("mah-joon-gah-sore-us", "Majunga lizard", ""),
        "masiakasaurus" to DinoFactExtras("mah-shee-ah-kah-sore-us", "vicious lizard", ""),
        "massospondylus" to DinoFactExtras("mass-oh-SPON-di-luss", "massive vertebrae", "on 2 or 4 legs"),
        "microraptor" to DinoFactExtras("MIKE-roe-rap-tor", "tiny plunderer", "on 2 legs, gliding or flying"),
        "minmi" to DinoFactExtras("min-mie", "named after Minmi Crossing, Queensland", ""),
        "mussaurus" to DinoFactExtras("moos-SORE-us", "mouse lizard", ""),
        "muttaburrasaurus" to DinoFactExtras("MUT-a-BURR-a-SORE-us", "Muttaburra lizard", ""),
        "nigersaurus" to DinoFactExtras("nee-zhayr-sore-us", "Niger lizard", ""),
        "noasaurus" to DinoFactExtras("noh-ah-sore-us", "northwestern Argentina lizard", ""),
        "nodosaurus" to DinoFactExtras("no-doh-SORE-us", "node lizard", ""),
        "ornithomimus" to DinoFactExtras("orn-ITH-oh-MEE-mus", "bird mimic", ""),
        "orodromeus" to DinoFactExtras("or-oh-DROM-ee-us", "mountain runner", "on 2 legs"),
        "ouranosaurus" to DinoFactExtras("oo-RAH-noh-sore-us", "brave monitor lizard", ""),
        "oviraptor" to DinoFactExtras("OH-vee-RAP-tor", "egg thief", "on 2 legs"),
        "pachycephalosaurus" to DinoFactExtras("pack-ee-KEF-ah-lo-sore-us", "thick-headed lizard", "on 2 legs"),
        "parasaurolophus" to DinoFactExtras("pa-ra-saw-ROL-off-us", "like 'Saurolophus'", "on 2 or 4 legs"),
        "patagotitan" to DinoFactExtras("Pat-ah-go-tie-tan", "titan from Patagonia", "on four legs"),
        "pelecanimimus" to DinoFactExtras("pel-e-kan-i-mim-us", "pelican mimic", ""),
        "pentaceratops" to DinoFactExtras("pent-ah-ker-ah-tops", "five-horned face", ""),
        "pinacosaurus" to DinoFactExtras("pin-ak-oh-sore-us", "plank lizard", ""),
        "plateosaurus" to DinoFactExtras("plat-ee-oh-sore-us", "flat lizard", "on 2 or 4 legs"),
        "polacanthus" to DinoFactExtras("pol-a-KAN-thus", "many spines", ""),
        "protoceratops" to DinoFactExtras("pro-toe-ker-ah-tops", "first horned face", "on 4 legs"),
        "psittacosaurus" to DinoFactExtras("SIT-ak-oh-sore-us", "parrot lizard", "on 2 or 4 legs"),
        "puertasaurus" to DinoFactExtras("PWER-ta-sore-us", "Puerta and Reuil's lizard", "on four legs"),
        "rajasaurus" to DinoFactExtras("RAH-juh-sore-us", "Princely lizard", ""),
        "rebbachisaurus" to DinoFactExtras("re-bash-i-sore-us", "[Ait] Rebbach lizard", ""),
        "saichania" to DinoFactExtras("sigh-CHAN-ee-a", "beautiful", ""),
        "saltasaurus" to DinoFactExtras("salt-a-sore-us", "Salta lizard", ""),
        "saurophaganax" to DinoFactExtras("sore-oh-fag-ah-naks", "king of the lizard eaters", ""),
        "scelidosaurus" to DinoFactExtras("skel-EYE-doh-sore-us", "limb lizard", ""),
        "scutellosaurus" to DinoFactExtras("skoo-tell-oh-sore-us", "small shield lizard", ""),
        "shantungosaurus" to DinoFactExtras("shan-TUN-go-sore-us", "Shantung (=Shandong) lizard", ""),
        "sinornithosaurus" to DinoFactExtras("sine-or-nith-oh-sore-us", "Chinese bird lizard", ""),
        "sinosauropteryx" to DinoFactExtras("sine-oh-sore-op-ter-iks", "Chinese lizard wing", "on 2 legs"),
        "spinosaurus" to DinoFactExtras("SPINE-oh-SORE-us", "spine lizard", ""),
        "staurikosaurus" to DinoFactExtras("stor-ik-oh-sore-us", "Southern Cross lizard", ""),
        "stegosaurus" to DinoFactExtras("STEG-oh-SORE-us", "roof lizard", ""),
        "struthiomimus" to DinoFactExtras("STRUTH-ee-oh-MEEM-us", "ostrich mimic", "on 2 legs"),
        "styracosaurus" to DinoFactExtras("sty-RAK-oh-sore-us", "spiked lizard", "on 4 legs"),
        "suchomimus" to DinoFactExtras("sook-oh-mim-us", "crocodile mimic", ""),
        "tarbosaurus" to DinoFactExtras("TAR-bow-SORE-us", "alarming lizard", ""),
        "tarchia" to DinoFactExtras("TAR-kee-ah", "brainy one", ""),
        "tenontosaurus" to DinoFactExtras("ten-ON-toe-sore-us", "sinew lizard", "on 4 legs"),
        "therizinosaurus" to DinoFactExtras("theh-riz-in-oh-SORE-us", "scythe lizard", "on 2 legs"),
        "torvosaurus" to DinoFactExtras("TOR-voh-SORE-us", "savage lizard", ""),
        "triceratops" to DinoFactExtras("tri-SERRA-tops", "three-horned face", "on 4 legs"),
        "troodon" to DinoFactExtras("TROH-oh-don", "wounding tooth", "on 2 legs"),
        "tsintaosaurus" to DinoFactExtras("ching-dow-sore-us", "Qingdao lizard", ""),
        "tyrannosaurus" to DinoFactExtras("tie-RAN-oh-sore-us", "tyrant lizard", "on 2 legs"),
        "utahraptor" to DinoFactExtras("YOO-tah-RAP-tor", "Utah plunderer", "on 2 legs"),
        "velociraptor" to DinoFactExtras("vel-OSS-ee-rap-tor", "quick plunderer", "on 2 legs"),
        "yutyrannus" to DinoFactExtras("YOO-tie-ran-us", "feathered tyrant", ""),
        "zuniceratops" to DinoFactExtras("zoo-nee-serra-tops", "Zuni (the name of a tribe of Indigenous People in the USA) horned-face", ""),
    )

    /** Extra facts for this entry, or null when none are known. */
    fun forEntry(entry: DinoEntry): DinoFactExtras? = extras[entry.id]

    /**
     * A playful, kid-readable size comparison derived from the entry length,
     * e.g. "About as long as 2 school buses".
     */
    fun sizeComparison(lengthFeet: Float): String = when {
        lengthFeet <= 0f -> ""
        lengthFeet < 1.5f -> "Small enough to sit in your hands"
        lengthFeet < 4f -> "About the size of a house cat"
        lengthFeet < 7f -> "About as long as a bicycle"
        lengthFeet < 12f -> "About as long as a small car"
        lengthFeet < 20f -> "About as long as a pickup truck"
        lengthFeet < 30f -> "About as long as a school bus"
        lengthFeet < 45f -> "Longer than a school bus"
        lengthFeet < 70f -> "About as long as 2 school buses"
        lengthFeet < 100f -> "About as long as 3 school buses"
        else -> "Longer than a blue whale"
    }
}
