#!/usr/bin/env python3
"""Rewrite all specimen taglines to be fun, punchy, and eye-catching."""

import re

# Map of specimen ID -> new fun tagline
NEW_TAGLINES = {
    # SeedData.kt specimens
    "apatite": "The mineral in your teeth and bones — literally!",
    "aragonite": "Calcite's rebellious twin that builds seashells.",
    "azurite": "Nature's blue paint — crushed for medieval masterpieces.",
    "barite": "Deceptively heavy — pick it up and your brain breaks.",
    "basalt": "The lava that paved the entire ocean floor.",
    "beryl": "One mineral family, a dozen world-famous gems.",
    "aquamarine": "Seawater frozen into a gem you can wear.",
    "emerald": "Cleopatra's obsession — green fire from the deep.",
    "bornite": "Peacock ore — nature's most glamorous oil slick.",
    "breccia": "Earthquake scars frozen in stone forever.",
    "calcite": "See double through nature's own magic crystal.",
    "chalcedony": "The parent of agates, jaspers, and onyx — a quartz superhero.",
    "chalcopyrite": "The brassy trickster that fooled a thousand prospectors.",
    "chert": "The stone that sparked human civilization — literally.",
    "chromite": "No chromite, no shiny chrome — no stainless steel either!",
    "chrysoberyl": "Home of alexandrite — the gem that changes color like magic.",
    "chrysocolla": "Electric blue-green copper that looks like a desert swimming pool.",
    "cinnabar": "Vermillion red so vivid it poisoned ancient painters.",
    "conglomerate": "Ancient riverbeds frozen mid-stream — pebbles and all.",
    "corundum": "Ruby and sapphire are the same mineral — mind blown!",
    "cuprite": "Redder than ruby, with more fire than diamond.",
    "diamond": "The undisputed heavyweight champion of hardness.",
    "dolomite": "The mountain-builder that only fizzes when crushed.",
    "epidote": "Pistachio-green blades forged by mountain-smashing heat.",
    "amazonite": "Turquoise feldspar named after a river it's never near.",
    "labradorite": "Hold the Northern Lights in the palm of your hand.",
    "moonstone": "Romans swore it was made of solidified moonlight.",
    "orthoclase": "The pink mineral that builds entire continents.",
    "fluorite": "Rainbow cubes that glow under UV — fluorescence is named after it!",
    "gabbro": "The dark engine of the deep ocean crust.",
    "galena": "Perfect metallic cubes — heavy enough to fool you into thinking it's lead.",
    "garnet": "Deep-red gems forged in the fires of mountain-building.",
    "gneiss": "Nature's layered art — bold stripes from intense pressure. (It's pronounced 'nice'!)",
    "granite": "The speckled stone that holds up whole mountains.",
    "graphite": "Pencil 'lead' that's pure carbon — cousin of diamond!",
    "gypsum": "Scratch it with your fingernail — crystals 39 feet long!",
    "hematite": "The mineral that paints Mars red and streaks blood.",
    "jade": "Tougher than steel, carved for 7,000 years, worth more than diamonds.",
    "kyanite": "One crystal, two hardnesses — nature's own paradox.",
    "limestone": "Built from billions of ancient seashells and coral.",
    "magnetite": "The original compass — lodestone that pointed the way home.",
    "malachite": "Swirling green bands — nature's most beautiful copper ore.",
    "marble": "Limestone that went to finishing school under heat and pressure.",
    "muscovite": "Peel it thinner than paper — medieval Russia's window glass!",
    "mohawkite": "Found at ONE mine in Michigan — nowhere else on Earth.",
    "molybdenite": "Silvery flakes that write on paper but aren't graphite.",
    "native-copper": "The first metal humans ever hammered into shape.",
    "native-gold": "The treasure that launched a thousand gold rushes.",
    "native-silver": "Wire-like silver curls — nature's most elegant metal art.",
    "native-sulfur": "Bright yellow crystals that reek of volcanoes — brimstone!",
    "obsidian": "Volcanic glass so sharp it beats surgical steel.",
    "peridot": "The only gemstone that also falls from space in meteorites.",
    "opal": "A rainbow trapped in stone — fire from microscopic spheres.",
    "pegmatite": "Crystals the size of phone poles — nature's jewelry box.",
    "pumice": "The only rock on Earth that floats on water!",
    "pyrite": "Fool's Gold — perfect metallic cubes that sparkle like treasure.",
    "quartz": "The most abundant mineral on Earth — and it vibrates in your watch!",
    "amethyst": "Ancient Greeks wore it to stay sober. (It didn't work.)",
    "citrine": "Liquid sunshine in a gem — the golden stone of abundance.",
    "rose-quartz": "A cloud of pink — the stone of love and ancient face masks.",
    "quartzite": "Sandstone that went supernova — it rings when you hit it!",
    "realgar": "Fiery red crystals that literally crumble in sunlight.",
    "rhodochrosite": "Rose-red bands so beautiful they made it Colorado's state mineral.",
    "rhodonite": "A pink garden in stone — rose petals with black manganese veins.",
    "lapis-lazuli": "Ground into ultramarine paint worth more than gold. King Tut wore it.",
    "rutile": "Golden needles of captured sunlight frozen inside quartz.",
    "sandstone": "Cemented sand that holds the story of ancient deserts and rivers.",
    "schist": "Glittering mica sheets that sparkle like a disco in sunlight.",
    "serpentine": "California's state rock — green, waxy, and snaky.",
    "shale": "The world's best fossil preserver — splits open like a book.",
    "slate": "Splits into perfect sheets — old chalkboards and century-old roofs.",
    "smithsonite": "Bubbly pastel mounds — named after the guy who founded the Smithsonian.",
    "soapstone": "So soft you can carve it with a butter knife. Seriously.",
    "sodalite": "Royal blue with white veins — lapis lazuli's budget-friendly cousin.",
    "sphalerite": "More fire than diamond — the zinc ore that sparkles like crazy.",
    "spinel": "The Crown Jewels' 'ruby' is actually this — fooled everyone for 500 years.",
    "staurolite": "Nature carves perfect crosses — 'fairy crosses' for good luck.",
    "stibnite": "Gleaming metallic swords up to 3 feet long — Cleopatra's eyeliner!",
    "talc": "The softest mineral on Earth — Mohs hardness 1, scratch it with your nail.",
    "topaz": "The fiery November gem — hardest silicate mineral on the planet.",
    "tourmaline": "Every color of the rainbow in one mineral — the ultimate gem chameleon.",
    "turquoise": "Sky-blue stone of the desert — prized for 5,000 years by every culture.",
    "zeolite": "Delicate crystal sprays in volcanic bubbles — nature's molecular sieve.",
    "zircon": "Earth's oldest mineral — 4.4 billion years old and still sparkling.",

    # JasperSpecimens.kt
    "banded-chert": "Nature's sedimentary metronome — each band is a thousand-year heartbeat.",
    "jasper-biggs": "Oregon's desert sunset frozen in stone — panoramic landscape art.",
    "jasper-blue-mountain": "Chinese ink painting in stone — dark dendrites on pale silk.",
    "jasper-brecciated": "Shattered by earthquakes, then healed with quartz veins. Nature's kintsugi!",
    "jasper-bruneau": "Idaho's canyon treasure — fortification bands as intricate as agate.",
    "jasper-imperial": "Mexico's pastel jewel — banding finer than a human hair.",
    "jasper-kambaba": "Hold a 2-billion-year-old fossil in your hand — crocodile jasper!",
    "jasper-leopard-skin": "Spotted like a big cat — the wildest jasper in the jungle.",
    "jasper-mookaite": "Australia's boldest stone — mustard, burgundy, and cream in wild swirls.",
    "jasper-morrisonite": "The most beautiful jasper on Earth — collectors fight over it.",
    "jasper-noreena": "Australia's shattered masterpiece — stained glass in stone.",
    "jasper-ocean": "Colorful orbs like bubbles frozen in green water — Madagascar's treasure.",
    "jasper-orbicular": "Perfectly round orbs packed like bubbles — nature's geometric art.",
    "jasper-picture": "Every slab is a different desert landscape — no two are alike.",
    "jasper-polychrome": "Madagascar's color explosion — teal, mustard, and burgundy ribbons.",
    "jasper-poppy": "California's floral stone — 'poppies' blooming in cream chalcedony.",
    "jasper-porcelain": "Feels like unglazed ceramic — the most tactilely pleasing stone ever.",
    "jasper-red-creek": "Deep red waves with white quartz — frozen rapids in stone.",
    "jasper-stone-canyon": "Bold geometric patterns — looks like a city skyline in rock.",
    "jasper-willow-creek": "Idaho's mossy swirls — a miniature abstract painting in every piece.",
    "k2-jasper": "Bright blue azurite splashed on white granite from the foot of K2!",
}

def rewrite_seeddata(content):
    """Rewrite taglines in SeedData.kt using regex to find specimen entries."""
    for spec_id, new_tagline in NEW_TAGLINES.items():
        # Pattern: id = "spec_id" ... tagline = "old_tagline"
        # We need to find the tagline that belongs to this specimen
        # Match: id = "spec_id" ... tagline = "..."  (non-greedy, within same Specimen call)
        pattern = r'(id\s*=\s*"' + re.escape(spec_id) + r'"[^)]*?tagline\s*=\s*)"[^"]*"'
        replacement = r'\1"' + new_tagline.replace('\\', '\\\\').replace('"', '\\"') + '"'
        new_content, count = re.subn(pattern, replacement, content, flags=re.DOTALL)
        if count > 0:
            content = new_content
    return content

def rewrite_jasper(content):
    """Rewrite taglines in JasperSpecimens.kt."""
    for spec_id, new_tagline in NEW_TAGLINES.items():
        pattern = r'(id\s*=\s*"' + re.escape(spec_id) + r'"[^)]*?tagline\s*=\s*)"[^"]*"'
        replacement = r'\1"' + new_tagline.replace('\\', '\\\\').replace('"', '\\"') + '"'
        new_content, count = re.subn(pattern, replacement, content, flags=re.DOTALL)
        if count > 0:
            content = new_content
    return content

# Process SeedData.kt
with open("android-rockhound/app/src/main/java/com/rork/rockscout/data/SeedData.kt", "r") as f:
    seeddata = f.read()
new_seeddata = rewrite_seeddata(seeddata)
with open("android-rockhound/app/src/main/java/com/rork/rockscout/data/SeedData.kt", "w") as f:
    f.write(new_seeddata)
print("SeedData.kt updated")

# Process JasperSpecimens.kt
with open("android-rockhound/app/src/main/java/com/rork/rockscout/data/JasperSpecimens.kt", "r") as f:
    jasper = f.read()
new_jasper = rewrite_jasper(jasper)
with open("android-rockhound/app/src/main/java/com/rork/rockscout/data/JasperSpecimens.kt", "w") as f:
    f.write(new_jasper)
print("JasperSpecimens.kt updated")

# Verify count
import subprocess
result = subprocess.run(["grep", "-c", "tagline", "android-rockhound/app/src/main/java/com/rork/rockscout/data/SeedData.kt"], capture_output=True, text=True)
print(f"SeedData tagline count: {result.stdout.strip()}")
result = subprocess.run(["grep", "-c", "tagline", "android-rockhound/app/src/main/java/com/rork/rockscout/data/JasperSpecimens.kt"], capture_output=True, text=True)
print(f"JasperSpecimens tagline count: {result.stdout.strip()}")
