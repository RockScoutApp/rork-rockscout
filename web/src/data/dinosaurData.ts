/** Dinosaur dictionary data — 200+ entries from the Android app. */
export type DinoEra = "TRIASSIC" | "JURASSIC" | "CRETACEOUS" | "PALEOGENE" | "NEOGENE" | "QUATERNARY" | "OTHER";
export type DinoDiet = "CARNIVORE" | "HERBIVORE" | "OMNIVORE" | "PISCIVORE" | "FILTER_FEEDER" | "INSECTIVORE" | "SCAVENGER";

export interface DinoEntry {
  id: string;
  name: string;
  era: DinoEra;
  period: string;
  age: string;
  diet: DinoDiet;
  length: string;
  weight: string;
  habitat: string;
  description: string;
  funFacts: string[];
  foundIn: string[];
  color: string;
}

export const ERA_LABELS: Record<DinoEra, { label: string; subtitle: string }> = {
  TRIASSIC: { label: "Triassic", subtitle: "~252-201 mya" },
  JURASSIC: { label: "Jurassic", subtitle: "~201-145 mya" },
  CRETACEOUS: { label: "Cretaceous", subtitle: "~145-66 mya" },
  PALEOGENE: { label: "Paleogene", subtitle: "~66-23 mya" },
  NEOGENE: { label: "Neogene", subtitle: "~23-2.6 mya" },
  QUATERNARY: { label: "Quaternary", subtitle: "~2.6 mya-Present" },
  OTHER: { label: "Other Eras", subtitle: "Before & after dinosaurs" },
};

export const DIET_LABELS: Record<DinoDiet, string> = {
  CARNIVORE: "Carnivore",
  HERBIVORE: "Herbivore",
  OMNIVORE: "Omnivore",
  PISCIVORE: "Piscivore (fish-eater)",
  FILTER_FEEDER: "Filter Feeder",
  INSECTIVORE: "Insectivore",
  SCAVENGER: "Scavenger",
};

export const DINO_ENTRIES: DinoEntry[] = [
  {
    "id": "herrerasaurus",
    "name": "Herrerasaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~231 mya",
    "diet": "CARNIVORE",
    "length": "10–20 ft",
    "weight": "300–770 lb",
    "habitat": "Seasonal floodplains of South America",
    "description": "One of the earliest known dinosaurs, Herrerasaurus was a bipedal carnivore that prowled the Triassic landscapes of what is now Argentina. Its unique sliding jaw joint allowed it to grip struggling prey.",
    "funFacts": [
      "Named after Victorino Herrera who found the first fossils",
      "Unique sliding jaw joint for gripping prey",
      "Among the very first true dinosaurs",
      "Walked on two legs with sharp serrated teeth"
    ],
    "foundIn": [
      "Ischigualasto Provincial Park, Argentina"
    ],
    "color": "#FF8B5A3C"
  },
  {
    "id": "eoraptor",
    "name": "Eoraptor",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~231 mya",
    "diet": "OMNIVORE",
    "length": "3.3 ft",
    "weight": "22 lb",
    "habitat": "Volcanic floodplains of South America",
    "description": "One of the earliest dinosaurs, Eoraptor was a small, fast omnivore with both sharp and leaf-shaped teeth. It represents the dawn of dinosaur evolution before the major groups split apart.",
    "funFacts": [
      "Discovered 1991 by Ricardo Mart\\u00ednez",
      "Name means 'dawn thief'",
      "Had five fingers — a primitive trait",
      "One of the earliest known true dinosaurs"
    ],
    "foundIn": [
      "Ischigualasto Provincial Park, Argentina"
    ],
    "color": "#FFA0704C"
  },
  {
    "id": "staurikosaurus",
    "name": "Staurikosaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~233 mya",
    "diet": "CARNIVORE",
    "length": "6.5 ft",
    "weight": "66 lb",
    "habitat": "Dry forests of South America",
    "description": "A small early dinosaur from Brazil, Staurikosaurus was a slender predator that hunted small reptiles and proto-mammals in the Triassic. It is one of the oldest dinosaurs known.",
    "funFacts": [
      "Discovered 1936",
      "Name means 'Southern Cross lizard'",
      "Very lightly built and fast",
      "Known from incomplete remains"
    ],
    "foundIn": [
      "Santa Maria Formation, Brazil"
    ],
    "color": "#FF9B6840"
  },
  {
    "id": "plateosaurus",
    "name": "Plateosaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~214–204 mya",
    "diet": "HERBIVORE",
    "length": "16–33 ft",
    "weight": "1,300–8,800 lb",
    "habitat": "Floodplains of Europe",
    "description": "An early sauropodomorph that could walk on two or four legs. Hundreds of Plateosaurus skeletons have been found in Germany, making it one of the best-known Triassic dinosaurs.",
    "funFacts": [
      "Discovered 1834 by Johann Friedrich Engelhart",
      "Could walk on two legs or all fours",
      "Hundreds of skeletons found at Trossingen",
      "One of the first dinosaurs to grow larger than modern elephants"
    ],
    "foundIn": [
      "Trossingen, Germany",
      "France",
      "Switzerland"
    ],
    "color": "#FF7A9A5C"
  },
  {
    "id": "coelophysis",
    "name": "Coelophysis",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~215–200 mya",
    "diet": "CARNIVORE",
    "length": "10 ft",
    "weight": "33–55 lb",
    "habitat": "Dry upland forests of North America",
    "description": "A slender, fast-running early dinosaur. Thousands of Coelophysis skeletons found at Ghost Ranch, New Mexico, make it one of the most abundant dinosaur fossils ever found.",
    "funFacts": [
      "Discovered 1881 by Edward Drinker Cope",
      "Thousands found at Ghost Ranch bonebed",
      "Name means 'hollow form' — hollow bones",
      "New Mexico's state fossil"
    ],
    "foundIn": [
      "Ghost Ranch, New Mexico, USA",
      "Arizona, USA"
    ],
    "color": "#FF6A7050"
  },
  {
    "id": "liliensternus",
    "name": "Liliensternus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~210 mya",
    "diet": "CARNIVORE",
    "length": "17 ft",
    "weight": "280 lb",
    "habitat": "Floodplains of Europe",
    "description": "One of the largest Triassic theropods, Liliensternus was an active predator that hunted prosauropods and other dinosaurs. It was a close relative of Coelophysis.",
    "funFacts": [
      "Discovered 1934 by Hugo R\\u00fchle von Lilienstern",
      "One of the largest Triassic carnivores",
      "Hunted Plateosaurus",
      "Name honors its discoverer"
    ],
    "foundIn": [
      "Trossingen, Germany"
    ],
    "color": "#FF7A6A50"
  },
  {
    "id": "gojirasaurus",
    "name": "Gojirasaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~210 mya",
    "diet": "CARNIVORE",
    "length": "18 ft",
    "weight": "330 lb",
    "habitat": "Floodplains of North America",
    "description": "A large early theropod named after Godzilla (Gojira in Japanese). It was one of the biggest predators of the Late Triassic in North America.",
    "funFacts": [
      "Discovered 1997 in New Mexico",
      "Named after Godzilla — Gojira is the Japanese name",
      "One of the largest Triassic carnivores in North America",
      "Relatively rare in the fossil record"
    ],
    "foundIn": [
      "Cooper Canyon Formation, New Mexico, USA"
    ],
    "color": "#FF6B5A40"
  },
  {
    "id": "postosuchus",
    "name": "Postosuchus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~221–203 mya",
    "diet": "CARNIVORE",
    "length": "13–20 ft",
    "weight": "600–1,000 lb",
    "habitat": "Floodplains of North America",
    "description": "A massive land crocodile (rauisuchian) that was the apex predator of the Late Triassic, hunting early dinosaurs. It walked on four legs but could rear up on two.",
    "funFacts": [
      "Discovered 1980 at Post Quarry, Texas",
      "Apex predator before dinosaurs took over",
      "Related to crocodiles, not dinosaurs",
      "Bite force strong enough to crush bone"
    ],
    "foundIn": [
      "Texas, USA",
      "North Carolina, USA"
    ],
    "color": "#FF5A4A3A"
  },
  {
    "id": "desmatosuchus",
    "name": "Desmatosuchus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~230 mya",
    "diet": "HERBIVORE",
    "length": "16 ft",
    "weight": "1,000 lb",
    "habitat": "Semi-arid plains of North America",
    "description": "A heavily armored aetosaur with large shoulder spikes and a pig-like snout. Despite looking like a dinosaur, it was a crocodile relative that lived before dinosaurs dominated.",
    "funFacts": [
      "Discovered 1945",
      "Had shoulder spikes up to 16 inches long",
      "A plant-eating crocodile relative",
      "Used its snout to dig for roots"
    ],
    "foundIn": [
      "Texas, USA",
      "Arizona, USA"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "rutiodon",
    "name": "Rutiodon",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~228 mya",
    "diet": "PISCIVORE",
    "length": "10 ft",
    "weight": "200 lb",
    "habitat": "Rivers and lakes of North America",
    "description": "A long-snouted phytosaur that looked and lived like a modern gharial, ambushing fish in Triassic rivers. Phytosaurs were crocodile relatives that filled the same niche before true crocodiles evolved.",
    "funFacts": [
      "Discovered 1858",
      "Looked identical to modern crocodiles but was a separate group",
      "Hunted fish in Triassic rivers",
      "Had nostrils near its eyes, unlike true crocodiles"
    ],
    "foundIn": [
      "North Carolina, USA",
      "New Jersey, USA"
    ],
    "color": "#FF5A7A6A"
  },
  {
    "id": "mussaurus",
    "name": "Mussaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~215 mya",
    "diet": "HERBIVORE",
    "length": "10 ft",
    "weight": "220 lb",
    "habitat": "Floodplains of South America",
    "description": "Known as the 'mouse lizard' because the first fossils found were tiny hatchlings only 8 inches long. Adults grew to 10 feet — a dramatic size difference that confused early paleontologists.",
    "funFacts": [
      "Discovered 1979",
      "Hatchlings were only 8 inches — 'mouse lizard'",
      "Adults grew to 10 feet — huge size difference",
      "One of the earliest sauropodomorphs"
    ],
    "foundIn": [
      "Santa Maria Formation, Brazil"
    ],
    "color": "#FF7A8A5A"
  },
  {
    "id": "dilophosaurus",
    "name": "Dilophosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~193 mya",
    "diet": "CARNIVORE",
    "length": "23 ft",
    "weight": "880 lb",
    "habitat": "Riverside forests of North America",
    "description": "A distinctive predator with two thin bony crests on its skull. Despite its movie fame, there is no evidence it spat venom or had a frill. It was a powerful hunter with strong jaws.",
    "funFacts": [
      "Discovered 1944 by Sam Welles",
      "Did NOT spit venom — that's movie fiction",
      "Had two thin bony crests on its head",
      "One of the largest Early Jurassic predators"
    ],
    "foundIn": [
      "Arizona, USA",
      "China"
    ],
    "color": "#FF8B5A5A"
  },
  {
    "id": "scelidosaurus",
    "name": "Scelidosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~190 mya",
    "diet": "HERBIVORE",
    "length": "13 ft",
    "weight": "550 lb",
    "habitat": "Coastal regions of England",
    "description": "One of the earliest known armored dinosaurs, covered in bony scutes from snout to tail. A nearly complete specimen was found in England, making it one of the best-preserved early dinosaurs.",
    "funFacts": [
      "Discovered 1858 by James Harrison",
      "One of the first armored dinosaurs",
      "Nearly complete skeleton found",
      "Covered in rows of bony scutes"
    ],
    "foundIn": [
      "Dorset, England",
      "Arizona, USA"
    ],
    "color": "#FF6A7A50"
  },
  {
    "id": "cryolophosaurus",
    "name": "Cryolophosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~186 mya",
    "diet": "CARNIVORE",
    "length": "21 ft",
    "weight": "1,000 lb",
    "habitat": "Antarctic forests",
    "description": "A large early theropod with a distinctive crescent-shaped crest on its head, nicknamed 'Elvisaurus.' It hunted in what was then a temperate Antarctic forest — proving dinosaurs thrived in polar regions.",
    "funFacts": [
      "Discovered 1991 by William Hammer",
      "Nicknamed 'Elvisaurus' for its pompadour-like crest",
      "Lived in Antarctica when it was forested",
      "One of the largest Early Jurassic theropods"
    ],
    "foundIn": [
      "Mount Kirkpatrick, Antarctica"
    ],
    "color": "#FF4A8B9A"
  },
  {
    "id": "massospondylus",
    "name": "Massospondylus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~200–183 mya",
    "diet": "HERBIVORE",
    "length": "13–20 ft",
    "weight": "1,000–3,000 lb",
    "habitat": "Floodplains of South Africa",
    "description": "One of the best-known early sauropodomorphs. Massospondylus eggs with embryos have been found — the oldest dinosaur embryos ever discovered. The babies had no teeth and likely needed parental care.",
    "funFacts": [
      "Discovered 1854 by Richard Owen",
      "Oldest dinosaur embryos ever found",
      "Hatchlings had no teeth — needed parental care",
      "One of the best-studied early dinosaurs"
    ],
    "foundIn": [
      "South Africa",
      "Zimbabwe",
      "Argentina"
    ],
    "color": "#FF6A8A4A"
  },
  {
    "id": "lesothosaurus",
    "name": "Lesothosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~199–189 mya",
    "diet": "HERBIVORE",
    "length": "6.5 ft",
    "weight": "22 lb",
    "habitat": "Dry uplands of South Africa",
    "description": "A small, fast-running primitive ornithischian. Lesothosaurus had a simple, unspecialized body plan that represents the ancestral form from which all armored and horned dinosaurs would later evolve.",
    "funFacts": [
      "Discovered 1978",
      "Ancestral form of all ornithischian dinosaurs",
      "Fast runner with simple body plan",
      "Named after Lesotho, southern Africa"
    ],
    "foundIn": [
      "Lesotho",
      "South Africa"
    ],
    "color": "#FF7A9A5A"
  },
  {
    "id": "heterodontosaurus",
    "name": "Heterodontosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~200–190 mya",
    "diet": "HERBIVORE",
    "length": "3.9 ft",
    "weight": "5.5 lb",
    "habitat": "Dry uplands of South Africa",
    "description": "A small dinosaur with three different types of teeth — including tusks and grinding teeth. Heterodontosaurus shows that early dinosaurs had varied diets. Its tusks may have been for display or defense.",
    "funFacts": [
      "Discovered 1962",
      "Had three types of teeth including tusks",
      "Tusks may have been for fighting or display",
      "Name means 'different-toothed lizard'"
    ],
    "foundIn": [
      "South Africa"
    ],
    "color": "#FF8B7A5A"
  },
  {
    "id": "scutellosaurus",
    "name": "Scutellosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~196 mya",
    "diet": "HERBIVORE",
    "length": "3.9 ft",
    "weight": "22 lb",
    "habitat": "Dry forests of North America",
    "description": "One of the earliest armored dinosaurs. Scutellosaurus was covered in hundreds of small bony scutes but could still run on two legs. It represents the beginning of the armored dinosaur lineage.",
    "funFacts": [
      "Discovered 1981",
      "Covered in hundreds of small bony scutes",
      "Could still run on two legs",
      "Ancestor of all armored dinosaurs"
    ],
    "foundIn": [
      "Arizona, USA"
    ],
    "color": "#FF6A7A4A"
  },
  {
    "id": "camarasaurus",
    "name": "Camarasaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~155–140 mya",
    "diet": "HERBIVORE",
    "length": "49–75 ft",
    "weight": "20–51 tons",
    "habitat": "Floodplains of North America",
    "description": "The most common sauropod of the Morrison Formation. Camarasaurus had a short, boxy skull and large nostrils. Its hollow vertebrae gave it a lighter body than other sauropods.",
    "funFacts": [
      "Discovered 1877 by Oramel Lucas",
      "Most common sauropod in the Morrison Formation",
      "Name means 'chambered lizard' — hollow vertebrae",
      "Had spoon-shaped teeth for stripping tough plants"
    ],
    "foundIn": [
      "Colorado, USA",
      "Wyoming, USA",
      "Utah, USA"
    ],
    "color": "#FF7A9A60"
  },
  {
    "id": "allosaurus",
    "name": "Allosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~155–145 mya",
    "diet": "CARNIVORE",
    "length": "28–39 ft",
    "weight": "2–5 tons",
    "habitat": "Floodplains and forests of North America",
    "description": "The apex predator of the Late Jurassic, Allosaurus used its skull like a hatchet, slashing its upper jaw downward into prey. Over 46 individuals found at one Utah quarry.",
    "funFacts": [
      "Discovered 1877 by Othniel Charles Marsh",
      "Used its skull like a hatchet to slash prey",
      "Over 46 found at Cleveland-Lloyd Quarry",
      "Utah's state fossil"
    ],
    "foundIn": [
      "Cleveland-Lloyd Quarry, Utah, USA",
      "Colorado, USA",
      "Wyoming, USA",
      "Portugal"
    ],
    "color": "#FF7A9A60"
  },
  {
    "id": "brachiosaurus",
    "name": "Brachiosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~154–150 mya",
    "diet": "HERBIVORE",
    "length": "85 ft",
    "weight": "30–80 tons",
    "habitat": "Riverside forests of North America & Africa",
    "description": "A giraffe-like sauropod with front legs longer than hind legs, allowing it to browse treetops 50 feet high. Its heart likely weighed 400+ pounds to pump blood up its long neck.",
    "funFacts": [
      "Discovered 1903 by Elmer Riggs",
      "Could browse leaves 50 ft high — a four-story building",
      "Heart likely weighed 400+ lbs",
      "Front legs longer than hind legs — unique among sauropods"
    ],
    "foundIn": [
      "Colorado, USA",
      "Tanzania"
    ],
    "color": "#FF7AA860"
  },
  {
    "id": "diplodocus",
    "name": "Diplodocus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~154–150 mya",
    "diet": "HERBIVORE",
    "length": "90 ft",
    "weight": "12–16 tons",
    "habitat": "Floodplains of North America",
    "description": "One of the longest dinosaurs, Diplodocus had a whip-like tail that could crack like a bullwhip. Carnegie-funded casts of 'Dippy' have been displayed in museums worldwide since 1905.",
    "funFacts": [
      "Discovered 1878 by S.W. Williston",
      "'Dippy' casts displayed in museums worldwide",
      "Tail could crack like a bullwhip",
      "Peg-like teeth only at the front of its mouth"
    ],
    "foundIn": [
      "Wyoming, USA",
      "Colorado, USA",
      "Utah, USA"
    ],
    "color": "#FF7AA870"
  },
  {
    "id": "apatosaurus",
    "name": "Apatosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~156–146 mya",
    "diet": "HERBIVORE",
    "length": "75–90 ft",
    "weight": "22–40 tons",
    "habitat": "Floodplains of North America",
    "description": "Formerly known as Brontosaurus, Apatosaurus was a massive sauropod with a long whip tail. The Brontosaurus/Apatosaurus naming controversy lasted over a century before being partially resolved in 2015.",
    "funFacts": [
      "Discovered 1877 by Othniel Charles Marsh",
      "Formerly called 'Brontosaurus' — the name was reinstated in 2015",
      "Tail could break the sound barrier — possibly",
      "Weighed as much as 4 elephants"
    ],
    "foundIn": [
      "Colorado, USA",
      "Wyoming, USA",
      "Utah, USA"
    ],
    "color": "#FF6A9860"
  },
  {
    "id": "ceratosaurus",
    "name": "Ceratosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~153–148 mya",
    "diet": "CARNIVORE",
    "length": "20–23 ft",
    "weight": "1,800–2,100 lb",
    "habitat": "Floodplains and forests of North America & Africa",
    "description": "A distinctive predator with a sharp horn on its snout and blade-like teeth. Ceratosaurus was a contemporary of Allosaurus but may have specialized in different prey, reducing competition.",
    "funFacts": [
      "Discovered 1884 by Othniel Charles Marsh",
      "Had a horn on its snout — unique among large theropods",
      "Deeper, more flexible tail than other theropods",
      "May have been a semi-aquatic hunter"
    ],
    "foundIn": [
      "Colorado, USA",
      "Utah, USA",
      "Portugal",
      "Tanzania"
    ],
    "color": "#FF6A5A4A"
  },
  {
    "id": "compsognathus",
    "name": "Compsognathus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~150 mya",
    "diet": "CARNIVORE",
    "length": "3.3 ft",
    "weight": "6.6 lb",
    "habitat": "Lagoon islands of Europe",
    "description": "One of the smallest known dinosaurs, about the size of a chicken. A fossil lizard was found in the stomach of one specimen, confirming it preyed on small vertebrates.",
    "funFacts": [
      "Discovered 1859 by Johann Andreas Wagner",
      "About the size of a chicken",
      "A lizard was found in one specimen's stomach",
      "Once considered the smallest dinosaur"
    ],
    "foundIn": [
      "Solnhofen Limestone, Bavaria, Germany",
      "France"
    ],
    "color": "#FF5A8040"
  },
  {
    "id": "archaeopteryx",
    "name": "Archaeopteryx",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~150 mya",
    "diet": "CARNIVORE",
    "length": "1.6 ft",
    "weight": "1.8–2.2 lb",
    "habitat": "Lagoon islands of Europe",
    "description": "The iconic 'missing link' between dinosaurs and birds. Archaeopteryx had feathers, wings, and a wishbone, but also teeth, a long bony tail, and clawed fingers. It could fly or glide.",
    "funFacts": [
      "Discovered 1861",
      "The famous 'missing link' between dinosaurs and birds",
      "Had feathers AND teeth AND a long bony tail",
      "Could likely fly but not as well as modern birds"
    ],
    "foundIn": [
      "Solnhofen Limestone, Bavaria, Germany"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "kentrosaurus",
    "name": "Kentrosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~155 mya",
    "diet": "HERBIVORE",
    "length": "13–17 ft",
    "weight": "1–2 tons",
    "habitat": "Floodplains of Africa",
    "description": "A smaller relative of Stegosaurus with more extreme armor — plates on its back transitioning to sharp spikes along its back and tail. Found in the famous Tendaguru deposits of Tanzania.",
    "funFacts": [
      "Discovered 1909 by Edwin Hennig",
      "Had plates that transitioned to spikes along its back",
      "Spikes on its shoulders and tail",
      "African cousin of Stegosaurus"
    ],
    "foundIn": [
      "Tendaguru Formation, Tanzania"
    ],
    "color": "#FF7A6A4A"
  },
  {
    "id": "torvosaurus",
    "name": "Torvosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~153–148 mya",
    "diet": "CARNIVORE",
    "length": "33–40 ft",
    "weight": "2–3.5 tons",
    "habitat": "Floodplains of North America & Europe",
    "description": "A massive megalosaurid theropod that rivaled Allosaurus in size. Torvosaurus had enormous blade-like teeth and powerful arms with large claws. It may have been the top predator of its ecosystem.",
    "funFacts": [
      "Discovered 1972 by James Jensen",
      "Rivaled Allosaurus in size",
      "Had the largest teeth of any Jurassic theropod",
      "Portuguese specimens suggest it reached 40 ft"
    ],
    "foundIn": [
      "Colorado, USA",
      "Portugal"
    ],
    "color": "#FF6A4A3A"
  },
  {
    "id": "saurophaganax",
    "name": "Saurophaganax",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~151 mya",
    "diet": "CARNIVORE",
    "length": "31–43 ft",
    "weight": "2.5–3.8 tons",
    "habitat": "Floodplains of North America",
    "description": "An enormous allosauroid that may have been even larger than Allosaurus. Some paleontologists consider it a species of Allosaurus, while others maintain it is a distinct genus. It was a top predator of the Morrison Formation.",
    "funFacts": [
      "Discovered 1931 by John Willis Stovall",
      "Possibly larger than Allosaurus",
      "Oklahoma's state fossil",
      "Name means 'lizard-eating master'"
    ],
    "foundIn": [
      "Oklahoma, USA"
    ],
    "color": "#FF8B4A3A"
  },
  {
    "id": "tyrannosaurus",
    "name": "Tyrannosaurus rex",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~68–66 mya",
    "diet": "CARNIVORE",
    "length": "40 ft",
    "weight": "8.4–14 tons",
    "habitat": "Floodplains and forests of North America",
    "description": "The most famous dinosaur, T. rex had the strongest bite of any land animal ever — 12,800 psi. It could crush bone, and despite its tiny arms, each could lift 400 lbs. It could run up to 25 mph.",
    "funFacts": [
      "Discovered 1905 by Barnum Brown",
      "Strongest bite of any land animal — 12,800 psi",
      "Tiny arms could each lift 400 lbs",
      "Lived ~28 years based on bone growth rings",
      "Had banana-sized serrated teeth"
    ],
    "foundIn": [
      "Montana, USA",
      "South Dakota, USA",
      "Wyoming, USA",
      "Alberta, Canada"
    ],
    "color": "#FF8BBF6A"
  },
  {
    "id": "triceratops",
    "name": "Triceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~68–66 mya",
    "diet": "HERBIVORE",
    "length": "30 ft",
    "weight": "6–12 tons",
    "habitat": "Floodplains of North America",
    "description": "The iconic three-horned dinosaur with a 6-foot skull and bony frill. Its brow horns exceeded 3 feet in adults. Hundreds of teeth in continuously replaced 'dental batteries' sliced through tough vegetation.",
    "funFacts": [
      "Discovered 1887 by Othniel Charles Marsh",
      "Had a 6-foot skull — one of the largest of any land animal",
      "Brow horns exceeded 3 ft in adults",
      "Wyoming's state dinosaur",
      "Hundreds of teeth in dental batteries"
    ],
    "foundIn": [
      "Montana, USA",
      "Wyoming, USA",
      "South Dakota, USA",
      "Alberta, Canada"
    ],
    "color": "#FF9BBF7A"
  },
  {
    "id": "spinosaurus",
    "name": "Spinosaurus",
    "era": "CRETACEOUS",
    "period": "Mid Cretaceous",
    "age": "~99–93 mya",
    "diet": "PISCIVORE",
    "length": "46–59 ft",
    "weight": "7–20 tons",
    "habitat": "River systems of North Africa",
    "description": "The largest carnivorous dinosaur, longer than T. rex. Spinosaurus had a massive sail, crocodile-like skull, and dense bones adapted for swimming. It hunted giant fish in Cretaceous rivers — a semiaquatic predator.",
    "funFacts": [
      "Discovered 1912 by Ernst Stromer",
      "Largest carnivorous dinosaur — longer than T. rex",
      "Semiaquatic — hunted fish in rivers",
      "Had a paddle-like tail for swimming",
      "Original fossils destroyed in WWII Munich bombing"
    ],
    "foundIn": [
      "Bahariya Formation, Egypt",
      "Kem Kem Beds, Morocco"
    ],
    "color": "#FF609050"
  },
  {
    "id": "velociraptor",
    "name": "Velociraptor",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75–71 mya",
    "diet": "CARNIVORE",
    "length": "6.5 ft",
    "weight": "33 lb",
    "habitat": "Desert dunes of Mongolia",
    "description": "A turkey-sized feathered predator with a 2.5-inch retractable sickle claw on each foot. Quill knobs on arm bones prove it had feathers. The famous 'fighting dinosaurs' fossil shows one locked in combat with Protoceratops.",
    "funFacts": [
      "Discovered 1923 by Peter Kaisen",
      "Turkey-sized — much smaller than movie version",
      "Fully feathered — quill knobs prove it",
      "Sickle claw stabbed vital areas of prey",
      "'Fighting dinosaurs' fossil shows it vs Protoceratops"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF6A9050"
  },
  {
    "id": "ankylosaurus",
    "name": "Ankylosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~68–66 mya",
    "diet": "HERBIVORE",
    "length": "20–26 ft",
    "weight": "4.8–8 tons",
    "habitat": "Floodplains of North America",
    "description": "The most heavily armored dinosaur, covered in bony osteoderm plates. Its 100-lb tail club could shatter T. rex leg bones. Even its eyelids were armored. A living tank from the end of the dinosaur era.",
    "funFacts": [
      "Discovered 1908 by Barnum Brown",
      "Tail club could shatter a T. rex's leg bones",
      "Even its eyelids were armored",
      "Armor made of osteoderms — bone plates in skin"
    ],
    "foundIn": [
      "Montana, USA",
      "Wyoming, USA",
      "Alberta, Canada"
    ],
    "color": "#FF7A8A60"
  },
  {
    "id": "giganotosaurus",
    "name": "Giganotosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~99–97 mya",
    "diet": "CARNIVORE",
    "length": "39–43 ft",
    "weight": "6.5–8.9 tons",
    "habitat": "Plains of South America",
    "description": "Slightly longer than T. rex, Giganotosaurus had blade-like teeth for slicing flesh rather than crushing bone. It likely hunted the giant sauropod Argentinosaurus, possibly in coordinated packs.",
    "funFacts": [
      "Discovered 1993 by Rub\\u00e9n Carolini",
      "Possibly larger than T. rex",
      "Hunted Argentinosaurus — the largest land animal ever",
      "Blade-like teeth for slicing, not crushing"
    ],
    "foundIn": [
      "Candeleros Formation, Neuqu\\u00e9n, Argentina"
    ],
    "color": "#FF6A7A40"
  },
  {
    "id": "carcharodontosaurus",
    "name": "Carcharodontosaurus",
    "era": "CRETACEOUS",
    "period": "Mid Cretaceous",
    "age": "~99–94 mya",
    "diet": "CARNIVORE",
    "length": "39–46 ft",
    "weight": "6–8 tons",
    "habitat": "River deltas of North Africa",
    "description": "A massive predator with shark-like serrated teeth up to 8 inches long. Carcharodontosaurus was one of the largest carnivorous dinosaurs, competing with Spinosaurus for prey in Cretaceous North Africa.",
    "funFacts": [
      "Discovered 1924",
      "Had shark-like teeth up to 8 inches long",
      "Name means 'shark-toothed lizard'",
      "One of the largest carnivorous dinosaurs"
    ],
    "foundIn": [
      "Kem Kem Beds, Morocco",
      "Egypt",
      "Niger"
    ],
    "color": "#FF5A6A30"
  },
  {
    "id": "argentinosaurus",
    "name": "Argentinosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~90 mya",
    "diet": "HERBIVORE",
    "length": "98–115 ft",
    "weight": "65–100 tons",
    "habitat": "Plains of South America",
    "description": "Possibly the largest animal to ever walk the Earth. Argentinosaurus may have reached 100 tons — as heavy as 15 elephants. Only a partial skeleton is known, but its vertebrae were each the size of boulders.",
    "funFacts": [
      "Discovered 1987 by Guillermo Heredia",
      "Possibly the largest land animal ever — up to 100 tons",
      "Known from only a few massive bones",
      "Weighed as much as 15 elephants"
    ],
    "foundIn": [
      "R\\u00edo Limay Formation, Neuqu\\u00e9n, Argentina"
    ],
    "color": "#FF5A8A50"
  },
  {
    "id": "parasaurolophus",
    "name": "Parasaurolophus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76–73 mya",
    "diet": "HERBIVORE",
    "length": "31 ft",
    "weight": "2.5–3 tons",
    "habitat": "Forests and swamps of North America",
    "description": "A crested hadrosaur with a long curved hollow crest extending backward from its skull. The crest may have been used to produce low-frequency calls that could be heard for miles — a natural trumpet.",
    "funFacts": [
      "Discovered 1922 by William Parks",
      "Hollow crest may have produced sounds heard for miles",
      "Crest could have been a natural trumpet",
      "Communicated with deep resonant calls"
    ],
    "foundIn": [
      "Alberta, Canada",
      "New Mexico, USA",
      "Utah, USA"
    ],
    "color": "#FF7A9A8A"
  },
  {
    "id": "iguanodon",
    "name": "Iguanodon",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~126–122 mya",
    "diet": "HERBIVORE",
    "length": "33 ft",
    "weight": "3–4 tons",
    "habitat": "Forests of Europe",
    "description": "One of the first dinosaurs ever described (1825). Iguanodon had a distinctive thumb spike that may have been used for defense or stripping vegetation. Hundreds of skeletons found in a Belgian coal mine.",
    "funFacts": [
      "Discovered 1825 by Gideon Mantell",
      "One of the first dinosaurs ever named",
      "Had a thumb spike — possibly for defense",
      "38 skeletons found in a Belgian coal mine"
    ],
    "foundIn": [
      "Bernissart, Belgium",
      "England",
      "Spain"
    ],
    "color": "#FF7A8A5A"
  },
  {
    "id": "deinonychus",
    "name": "Deinonychus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~115–108 mya",
    "diet": "CARNIVORE",
    "length": "11 ft",
    "weight": "160–220 lb",
    "habitat": "Forests of North America",
    "description": "The dinosaur that sparked the 'Dinosaur Renaissance' in the 1960s. John Ostrom's discovery proved dinosaurs were active, warm-blooded animals. Its sickle claw inspired the raptors in Jurassic Park.",
    "funFacts": [
      "Discovered 1964 by John Ostrom",
      "Literally sparked the 'Dinosaur Renaissance'",
      "'Terrible claw' — held off ground to stay razor sharp",
      "Evidence of pack hunting with Tenontosaurus"
    ],
    "foundIn": [
      "Montana, USA",
      "Wyoming, USA",
      "Oklahoma, USA"
    ],
    "color": "#FF6A8A50"
  },
  {
    "id": "baryonyx",
    "name": "Baryonyx",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~130–125 mya",
    "diet": "PISCIVORE",
    "length": "25–33 ft",
    "weight": "1.2–2 tons",
    "habitat": "River deltas of Europe",
    "description": "A fish-eating theropod with a crocodile-like snout and enormous hooked claws on its thumbs. Fish scales were found in the stomach region of the first specimen, confirming its aquatic diet.",
    "funFacts": [
      "Discovered 1983 by William Walker",
      "Fish scales found in its stomach",
      "Had a 12-inch thumb claw",
      "Crocodile-like snout for catching fish"
    ],
    "foundIn": [
      "Surrey, England",
      "Spain",
      "Niger"
    ],
    "color": "#FF5A7A6A"
  },
  {
    "id": "utahraptor",
    "name": "Utahraptor",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~126 mya",
    "diet": "CARNIVORE",
    "length": "18–23 ft",
    "weight": "1,000–3,300 lb",
    "habitat": "Forests of North America",
    "description": "The largest known dromaeosaur — a real-life version of the movie raptors. Utahraptor had 9-inch killing claws on each foot and was a powerful pack hunter. It was the apex predator of Early Cretaceous Utah.",
    "funFacts": [
      "Discovered 1993 by James Kirkland",
      "The largest raptor — movie-sized but real",
      "9-inch killing claws on each foot",
      "Apex predator of Early Cretaceous Utah"
    ],
    "foundIn": [
      "Utah, USA"
    ],
    "color": "#FF8B5A40"
  },
  {
    "id": "microraptor",
    "name": "Microraptor",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~120 mya",
    "diet": "CARNIVORE",
    "length": "2.5 ft",
    "weight": "2.2 lb",
    "habitat": "Forests of China",
    "description": "A four-winged dinosaur with flight feathers on both its arms and legs. Microraptor could glide between trees, making it one of the most aerial non-avian dinosaurs. It hunted small birds and lizards.",
    "funFacts": [
      "Discovered 2000 in Liaoning, China",
      "Had FOUR wings — feathers on arms AND legs",
      "Could glide between trees",
      "Hunted early birds — one had a bird in its stomach"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF4A6A8A"
  },
  {
    "id": "protoceratops",
    "name": "Protoceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75–71 mya",
    "diet": "HERBIVORE",
    "length": "6 ft",
    "weight": "400 lb",
    "habitat": "Desert dunes of Mongolia",
    "description": "A small early ceratopsian without horns but with a large frill. Protoceratops is one of the most abundant dinosaurs of the Gobi Desert. The famous 'fighting dinosaurs' fossil shows one locked in combat with a Velociraptor.",
    "funFacts": [
      "Discovered 1923 by Walter Granger",
      "Famous 'fighting dinosaurs' fossil — vs Velociraptor",
      "May have inspired the griffin myth",
      "One of the most common Gobi Desert dinosaurs"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF9B8A6A"
  },
  {
    "id": "oviraptor",
    "name": "Oviraptor",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75 mya",
    "diet": "OMNIVORE",
    "length": "5 ft",
    "weight": "70 lb",
    "habitat": "Desert dunes of Mongolia",
    "description": "Wrongly named 'egg thief' — the first specimen was found on a nest of eggs that turned out to be its own. Oviraptor was actually brooding its eggs like a modern bird, proving parental care in dinosaurs.",
    "funFacts": [
      "Discovered 1923 by Roy Chapman Andrews",
      "Wrongly accused — was protecting its OWN eggs",
      "Died brooding its nest like a modern bird",
      "Had a toothless beak and a bony crest"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF5A7050"
  },
  {
    "id": "therizinosaurus",
    "name": "Therizinosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "HERBIVORE",
    "length": "33 ft",
    "weight": "3–5 tons",
    "habitat": "Forests of Mongolia",
    "description": "One of the most bizarre dinosaurs — a giant theropod that evolved herbivory. It had a pot belly, long neck, beaked mouth, and enormous 3-foot claws. Its claws were originally thought to belong to a giant turtle.",
    "funFacts": [
      "Discovered 1948",
      "Claws up to 3 feet long — longest of any animal",
      "A theropod that evolved to eat plants",
      "Claws likely used to pull down branches"
    ],
    "foundIn": [
      "Nemegt Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF6A7040"
  },
  {
    "id": "carnotaurus",
    "name": "Carnotaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~71–69 mya",
    "diet": "CARNIVORE",
    "length": "24–30 ft",
    "weight": "1.3–2.1 tons",
    "habitat": "Plains of South America",
    "description": "The 'meat-eating bull' with two bull-like horns above its eyes and even tinier arms than T. rex. Skin impressions show bumpy, non-feathered skin. It was a fast runner from Cretaceous Argentina.",
    "funFacts": [
      "Discovered 1984 by Jos\\u00e9 Bonaparte",
      "Had two bull-like horns above its eyes",
      "Arms even tinier than T. rex — truly vestigial",
      "Skin impressions show bumpy, non-feathered skin"
    ],
    "foundIn": [
      "La Colonia Formation, Chubut, Argentina"
    ],
    "color": "#FF7A6050"
  },
  {
    "id": "maiasaura",
    "name": "Maiasaura",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76.7 mya",
    "diet": "HERBIVORE",
    "length": "30 ft",
    "weight": "3 tons",
    "habitat": "Nesting grounds of North America",
    "description": "The 'good mother lizard' — the first dinosaur found with evidence of parental care. Entire nesting colonies with eggs, hatchlings, and juveniles show that Maiasaura cared for its young after hatching.",
    "funFacts": [
      "Discovered 1978 by Jack Horner",
      "First dinosaur proven to care for its young",
      "Nesting colonies with eggs and babies",
      "Montana's state fossil"
    ],
    "foundIn": [
      "Two Medicine Formation, Montana, USA"
    ],
    "color": "#FF7A9A6A"
  },
  {
    "id": "pachycephalosaurus",
    "name": "Pachycephalosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~68–66 mya",
    "diet": "HERBIVORE",
    "length": "15 ft",
    "weight": "990 lb",
    "habitat": "Forests of North America",
    "description": "The dome-headed dinosaur with a skull roof up to 10 inches thick. Pachycephalosaurus may have rammed heads like modern bighorn sheep, though some scientists argue the domes were for species recognition or display.",
    "funFacts": [
      "Discovered 1943 by Charles Gilmore",
      "Skull dome up to 10 inches thick of solid bone",
      "May have head-butted like bighorn sheep",
      "Name means 'thick-headed lizard'"
    ],
    "foundIn": [
      "Montana, USA",
      "South Dakota, USA",
      "Wyoming, USA"
    ],
    "color": "#FF8B5A5A"
  },
  {
    "id": "edmontosaurus",
    "name": "Edmontosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~73–66 mya",
    "diet": "HERBIVORE",
    "length": "39–43 ft",
    "weight": "4–5 tons",
    "habitat": "Floodplains of North America",
    "description": "One of the largest hadrosaurs, Edmontosaurus had a flattened duckbill and hundreds of teeth. A 'mummified' specimen with preserved skin shows it had a fleshy comb on its head, like a rooster's.",
    "funFacts": [
      "Discovered 1917 by Lawrence Lambe",
      "'Mummified' specimens with preserved skin",
      "Had a fleshy comb on its head like a rooster",
      "One of the last non-avian dinosaurs"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA",
      "South Dakota, USA"
    ],
    "color": "#FF6A8A6A"
  },
  {
    "id": "lambeosaurus",
    "name": "Lambeosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76–75 mya",
    "diet": "HERBIVORE",
    "length": "31 ft",
    "weight": "5 tons",
    "habitat": "Swamps of North America",
    "description": "A crested hadrosaur with a distinctive hatchet-shaped crest. The crest contained hollow nasal passages that may have amplified calls. Lambeosaurus was one of the most ornate duck-billed dinosaurs.",
    "funFacts": [
      "Discovered 1923",
      "Hatchet-shaped skull crest",
      "Crest contained hollow nasal passages",
      "One of the most ornate hadrosaurs"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA"
    ],
    "color": "#FF5A8A8A"
  },
  {
    "id": "styracosaurus",
    "name": "Styracosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75.5–74.5 mya",
    "diet": "HERBIVORE",
    "length": "18 ft",
    "weight": "3 tons",
    "habitat": "Forests of North America",
    "description": "A spectacular ceratopsian with a frill edged with 4–6 long spikes and a single nose horn. Styracosaurus looked like a prehistoric rhinoceros on steroids. Its impressive frill may have been for display or defense.",
    "funFacts": [
      "Discovered 1913 by Charles Sternberg",
      "Frill edged with 4–6 long spikes",
      "A single 2-foot nose horn",
      "Name means 'spiked lizard'"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA"
    ],
    "color": "#FF8B6A6A"
  },
  {
    "id": "centrosaurus",
    "name": "Centrosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76.5 mya",
    "diet": "HERBIVORE",
    "length": "18 ft",
    "weight": "3 tons",
    "habitat": "Floodplains of North America",
    "description": "A horned dinosaur with a large nose horn and a frill adorned with small hooks. Thousands of Centrosaurus skeletons found in a single bonebed suggest they moved in vast herds and may have died in mass drownings.",
    "funFacts": [
      "Discovered 1904 by Lawrence Lambe",
      "Thousands found in one bonebed — mass herd death",
      "Had a large forward-curving nose horn",
      "Moved in vast herds across ancient Canada"
    ],
    "foundIn": [
      "Alberta, Canada"
    ],
    "color": "#FF7A5A6A"
  },
  {
    "id": "euoplocephalus",
    "name": "Euoplocephalus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76–70 mya",
    "diet": "HERBIVORE",
    "length": "20 ft",
    "weight": "2–2.8 tons",
    "habitat": "Forests of North America",
    "description": "An armored ankylosaur covered in bony plates with a tail club and even armored eyelids. Euoplocephalus was better protected than a tank, with a secondary bony palate that may have helped it breathe while chewing tough plants.",
    "funFacts": [
      "Discovered 1897 by Lawrence Lambe",
      "Armored eyelids and a bony palate",
      "Tail club could deter predators",
      "One of the best-known ankylosaurs"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA"
    ],
    "color": "#FF6A7A50"
  },
  {
    "id": "nodosaurus",
    "name": "Nodosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~100–97 mya",
    "diet": "HERBIVORE",
    "length": "13–20 ft",
    "weight": "3 tons",
    "habitat": "Floodplains of North America",
    "description": "An armored dinosaur without a tail club but covered in bony plates and spikes. A spectacular 'mummified' nodosaur found in Canada preserves skin, armor, and even gut contents — one of the best-preserved dinosaur fossils ever found.",
    "funFacts": [
      "Discovered 1869",
      "Mummified specimen preserves skin and gut contents",
      "One of the best-preserved dinosaur fossils ever",
      "No tail club — relied on armor plates alone"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Kansas, USA"
    ],
    "color": "#FF7A7A4A"
  },
  {
    "id": "troodon",
    "name": "Troodon",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~78–66 mya",
    "diet": "OMNIVORE",
    "length": "8 ft",
    "weight": "110 lb",
    "habitat": "Forests of North America",
    "description": "The dinosaur with the largest brain relative to body size — potentially as smart as a modern opossum. Troodon had excellent binocular vision and may have been nocturnal. It had serrated teeth and a large sickle claw.",
    "funFacts": [
      "Discovered 1856 by Joseph Leidy",
      "Largest brain-to-body ratio of any dinosaur",
      "May have been nocturnal",
      "Exceptional binocular vision"
    ],
    "foundIn": [
      "Montana, USA",
      "Alberta, Canada",
      "Alaska, USA"
    ],
    "color": "#FF5A6A8A"
  },
  {
    "id": "ornithomimus",
    "name": "Ornithomimus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75–66 mya",
    "diet": "OMNIVORE",
    "length": "12 ft",
    "weight": "330 lb",
    "habitat": "Plains of North America",
    "description": "An ostrich-like dinosaur that could run up to 43 mph — among the fastest dinosaurs. It had a toothless beak, long legs, and a long tail. Some specimens show evidence of feathers and feather-like structures.",
    "funFacts": [
      "Discovered 1890 by Othniel Charles Marsh",
      "Could run up to 43 mph — one of the fastest dinosaurs",
      "Looked and ran like an ostrich",
      "Toothless beak for omnivorous diet"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Colorado, USA",
      "Montana, USA"
    ],
    "color": "#FF7A8A6A"
  },
  {
    "id": "gallimimus",
    "name": "Gallimimus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "OMNIVORE",
    "length": "20 ft",
    "weight": "1 ton",
    "habitat": "Plains of Mongolia",
    "description": "The largest ornithomimid, reaching 20 feet. Gallimimus had a long toothless beak and could run at high speeds. Fossils from Mongolia show it lived in open plains, similar to modern ostriches.",
    "funFacts": [
      "Discovered 1964 by Zofia Kielan-Jaworowska",
      "Largest ostrich-mimic dinosaur",
      "Could reach speeds of 35+ mph",
      "Name means 'chicken mimic'"
    ],
    "foundIn": [
      "Nemegt Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF8B8A5A"
  },
  {
    "id": "deinocheirus",
    "name": "Deinocheirus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "OMNIVORE",
    "length": "36 ft",
    "weight": "6.4 tons",
    "habitat": "River deltas of Mongolia",
    "description": "A bizarre giant ornithomimosaur with 8-foot arms bearing huge claws, a hump on its back, and a duck-like bill. For decades only its arms were known — the rest of the skeleton revealed one of the strangest dinosaurs ever.",
    "funFacts": [
      "Discovered 1965",
      "Had 8-foot arms with massive claws",
      "Duck-like bill on a giant body",
      "Hump on its back — like a camel",
      "For decades only its arms were known"
    ],
    "foundIn": [
      "Nemegt Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF6A7A5A"
  },
  {
    "id": "tarbosaurus",
    "name": "Tarbosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "CARNIVORE",
    "length": "33–40 ft",
    "weight": "4–5 tons",
    "habitat": "Floodplains of Mongolia",
    "description": "The Asian cousin of T. rex, nearly as large and equally fearsome. Tarbosaurus had a slightly narrower skull and more rigid bite. It was the apex predator of Late Cretaceous Mongolia, hunting hadrosaurs and sauropods.",
    "funFacts": [
      "Discovered 1955 by Evgeny Maleev",
      "Asian cousin of T. rex",
      "Apex predator of Cretaceous Mongolia",
      "Slightly narrower skull than T. rex"
    ],
    "foundIn": [
      "Nemegt Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF5A7A4A"
  },
  {
    "id": "albertosaurus",
    "name": "Albertosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~71–68 mya",
    "diet": "CARNIVORE",
    "length": "30 ft",
    "weight": "2.5–3 tons",
    "habitat": "Forests of North America",
    "description": "A smaller, earlier relative of T. rex. Albertosaurus was the apex predator of Late Cretaceous Canada. A bonebed with 26 individuals of different ages suggests it may have lived and hunted in family groups.",
    "funFacts": [
      "Discovered 1884 by Joseph Burr Tyrrell",
      "Bonebed of 26 individuals — possible pack behavior",
      "Apex predator before T. rex evolved",
      "Named after Alberta, Canada"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA"
    ],
    "color": "#FF6A8A4A"
  },
  {
    "id": "daspletosaurus",
    "name": "Daspletosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~77–74 mya",
    "diet": "CARNIVORE",
    "length": "26–33 ft",
    "weight": "2–3 tons",
    "habitat": "Forests of North America",
    "description": "A close relative of T. rex that lived a few million years earlier. Daspletosaurus had tiny arms, powerful jaws, and forward-facing eyes giving it excellent depth perception. It was a transitional tyrannosaurid.",
    "funFacts": [
      "Discovered 1921 by Charles Mortram Sternberg",
      "Direct ancestor or close relative of T. rex",
      "Forward-facing eyes for depth perception",
      "Transitional tyrannosaurid"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA"
    ],
    "color": "#FF5A6A4A"
  },
  {
    "id": "suchomimus",
    "name": "Suchomimus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~121–112 mya",
    "diet": "PISCIVORE",
    "length": "31–36 ft",
    "weight": "3–5 tons",
    "habitat": "River systems of North Africa",
    "description": "A fish-eating spinosaurid with a crocodile-like snout and a sail on its back. Suchomimus had over 100 sharp teeth for gripping slippery fish. It was a smaller relative of Spinosaurus.",
    "funFacts": [
      "Discovered 1997 by Paul Sereno",
      "Crocodile-like snout with 100+ teeth",
      "Large thumb claws for hooking fish",
      "Sail on its back for display or thermoregulation"
    ],
    "foundIn": [
      "Gadoufaoua, Niger"
    ],
    "color": "#FF5A7A50"
  },
  {
    "id": "concavenator",
    "name": "Concavenator",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~130 mya",
    "diet": "CARNIVORE",
    "length": "20 ft",
    "weight": "1 ton",
    "habitat": "Floodplains of Spain",
    "description": "A bizarre carcharodontosaur with two tall sails on its back — one over the hips and one on the neck. Even more surprisingly, it had quill knobs on its arms, suggesting it had proto-feathers despite being a large carnivore.",
    "funFacts": [
      "Discovered 2003",
      "Had TWO sails — one on hips, one on neck",
      "Quill knobs suggest it had proto-feathers",
      "One of the most bizarre large carnivores"
    ],
    "foundIn": [
      "Las Hoyas, Cuenca, Spain"
    ],
    "color": "#FF8B5A3A"
  },
  {
    "id": "sinoceratops",
    "name": "Sinoceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~72 mya",
    "diet": "HERBIVORE",
    "length": "18 ft",
    "weight": "2 tons",
    "habitat": "Floodplains of China",
    "description": "The first ceratopsian discovered in China. Sinoceratops had a large frill with forward-curving horns. It shows that horned dinosaurs had a wider geographic range than previously thought.",
    "funFacts": [
      "Discovered 2008",
      "First ceratopsian found in China",
      "Large frill with horn-like knobs",
      "Shows horned dinosaurs lived in Asia too"
    ],
    "foundIn": [
      "Shandong, China"
    ],
    "color": "#FF8B7A5A"
  },
  {
    "id": "kosmoceratops",
    "name": "Kosmoceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76–75.9 mya",
    "diet": "HERBIVORE",
    "length": "15 ft",
    "weight": "2.5 tons",
    "habitat": "Island continent of North America",
    "description": "The most ornate ceratopsian ever discovered, with 15 horns and horn-like structures on its skull — including 10 frill horns that curled forward like a bouffant hairstyle. It lived on the island continent of Laramidia.",
    "funFacts": [
      "Discovered 2010",
      "15 horns on its skull — most of any ceratopsian",
      "Frill horns curled forward like a hairstyle",
      "Name means 'ornate horned face'"
    ],
    "foundIn": [
      "Grand Staircase-Escalante, Utah, USA"
    ],
    "color": "#FF9B7A6A"
  },
  {
    "id": "pentaceratops",
    "name": "Pentaceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~76–73 mya",
    "diet": "HERBIVORE",
    "length": "20–26 ft",
    "weight": "4–5 tons",
    "habitat": "Floodplains of North America",
    "description": "A large ceratopsian with a massive frill — the largest skull of any land animal at up to 10 feet long. Despite its name meaning 'five-horned face,' it had only three true horns plus two epoccipital bumps.",
    "funFacts": [
      "Discovered 1921 by Henry Fairfield Osborn",
      "Largest skull of any land animal — up to 10 ft",
      "Massive frill with large windows",
      "Name means 'five-horned face'"
    ],
    "foundIn": [
      "New Mexico, USA",
      "Colorado, USA"
    ],
    "color": "#FF8B6A5A"
  },
  {
    "id": "achelousaurus",
    "name": "Achelousaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~83–70 mya",
    "diet": "HERBIVORE",
    "length": "20 ft",
    "weight": "6 tons",
    "habitat": "Floodplains of North America",
    "description": "A ceratopsian with bumps instead of horns — it may be a transitional form between horned and hornless ceratopsians. Named after the Greek god Achelous, whose horns were broken off in a myth.",
    "funFacts": [
      "Discovered 1995 by Scott Sampson",
      "Had bumps instead of horns",
      "Named after a Greek god whose horns were broken",
      "Transitional between horned and hornless ceratopsians"
    ],
    "foundIn": [
      "Montana, USA"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "einiosaurus",
    "name": "Einiosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~74.5 mya",
    "diet": "HERBIVORE",
    "length": "15 ft",
    "weight": "3 tons",
    "habitat": "Floodplains of North America",
    "description": "A ceratopsian with a forward-curving 'bottle opener' nose horn. Einiosaurus is known from bonebeds containing hundreds of individuals, suggesting it lived in large herds.",
    "funFacts": [
      "Discovered 1985",
      "Forward-curving 'bottle opener' nose horn",
      "Hundreds found in bonebeds — herd animals",
      "Name means 'buffalo lizard'"
    ],
    "foundIn": [
      "Montana, USA"
    ],
    "color": "#FF7A7A5A"
  },
  {
    "id": "achillobator",
    "name": "Achillobator",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~90 mya",
    "diet": "CARNIVORE",
    "length": "16–20 ft",
    "weight": "660 lb",
    "habitat": "Deserts of Mongolia",
    "description": "A large dromaeosaurid, second only to Utahraptor in size. Achillobator had powerful legs and a large sickle claw. It was a formidable predator that hunted hadrosaurs in Cretaceous Mongolia.",
    "funFacts": [
      "Discovered 1989",
      "Second-largest known raptor after Utahraptor",
      "Large sickle claw on each foot",
      "Named after Achilles — for its deadly heel claw"
    ],
    "foundIn": [
      "Bayan Mandahu, Inner Mongolia, China"
    ],
    "color": "#FF6A5A3A"
  },
  {
    "id": "citipati",
    "name": "Citipati",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75 mya",
    "diet": "OMNIVORE",
    "length": "10 ft",
    "weight": "165 lb",
    "habitat": "Deserts of Mongolia",
    "description": "An oviraptorid known from spectacular fossils — including a specimen brooding its nest, preserved in a posture identical to modern birds. Citipati had a tall crest and feathered arms.",
    "funFacts": [
      "Discovered 1996",
      "Famous brooding fossil — sitting on its nest",
      "Identical posture to modern nesting birds",
      "Had a tall crest on its head"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "pinacosaurus",
    "name": "Pinacosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~80–75 mya",
    "diet": "HERBIVORE",
    "length": "16 ft",
    "weight": "1 ton",
    "habitat": "Deserts of Mongolia",
    "description": "An ankylosaur found in large numbers in Mongolia. Juvenile Pinacosaurus skeletons have been found in groups, suggesting young ankylosaurs stayed together for protection before joining adult herds.",
    "funFacts": [
      "Discovered 1933",
      "Juveniles found in groups — young stayed together",
      "More lightly armored than other ankylosaurs",
      "Common in Gobi Desert deposits"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF7A6A4A"
  },
  {
    "id": "saichania",
    "name": "Saichania",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75 mya",
    "diet": "HERBIVORE",
    "length": "21 ft",
    "weight": "3 tons",
    "habitat": "Deserts of Mongolia",
    "description": "A heavily armored ankylosaur with a complex airway system — bony nasal passages that may have helped it survive in dry, dusty Cretaceous deserts. Saichania means 'beautiful one' in Mongolian.",
    "funFacts": [
      "Discovered 1971",
      "Complex nasal passages for dusty desert air",
      "Heavily armored with a tail club",
      "Name means 'beautiful one'"
    ],
    "foundIn": [
      "Gobi Desert, Mongolia"
    ],
    "color": "#FF6A7A4A"
  },
  {
    "id": "tarchia",
    "name": "Tarchia",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75 mya",
    "diet": "HERBIVORE",
    "length": "16–26 ft",
    "weight": "2–4 tons",
    "habitat": "Deserts of Mongolia",
    "description": "One of the largest Asian ankylosaurs. Tarchia had a wide, heavily armored skull and a powerful tail club. Its name means 'brainy one' — ironically, it had one of the smallest brains relative to body size.",
    "funFacts": [
      "Discovered 1970",
      "Name means 'brainy one' — but it had a tiny brain",
      "One of the largest Asian ankylosaurs",
      "Powerful tail club for defense"
    ],
    "foundIn": [
      "Barun Goyot Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF5A6A4A"
  },
  {
    "id": "shantungosaurus",
    "name": "Shantungosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~78 mya",
    "diet": "HERBIVORE",
    "length": "48–54 ft",
    "weight": "16 tons",
    "habitat": "Floodplains of China",
    "description": "The largest ornithischian dinosaur ever — a 54-foot duck-bill. Shantungosaurus was as long as many sauropods and weighed up to 16 tons. It was an enormous plant-eater that likely had few predators to fear.",
    "funFacts": [
      "Discovered 1973",
      "Largest ornithischian dinosaur — 54 ft long",
      "Weighed up to 16 tons — as heavy as some sauropods",
      "Few predators could threaten it"
    ],
    "foundIn": [
      "Shandong, China"
    ],
    "color": "#FF6A7A5A"
  },
  {
    "id": "sinornithosaurus",
    "name": "Sinornithosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~124–120 mya",
    "diet": "CARNIVORE",
    "length": "3.3 ft",
    "weight": "2.2 lb",
    "habitat": "Forests of China",
    "description": "A feathered dromaeosaurid with detailed feather impressions. Sinornithosaurus had different types of feathers — filaments, down, and branched feathers — showing the evolution of feathers toward modern bird feathers.",
    "funFacts": [
      "Discovered 1999",
      "Detailed feather impressions preserved",
      "Had filaments, down, and branched feathers",
      "Shows how feathers evolved toward modern bird feathers"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF6A7A8A"
  },
  {
    "id": "yutyrannus",
    "name": "Yutyrannus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~125 mya",
    "diet": "CARNIVORE",
    "length": "30 ft",
    "weight": "1.4 tons",
    "habitat": "Forests of China",
    "description": "The largest feathered dinosaur known. Yutyrannus was a tyrannosauroid the size of a bus, covered in shaggy filamentous feathers. It proves that even large carnivorous dinosaurs could be feathered.",
    "funFacts": [
      "Discovered 2012",
      "Largest feathered dinosaur ever found",
      "Covered in shaggy proto-feathers",
      "A feathered ancestor of T. rex"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF8B5A3A"
  },
  {
    "id": "beipiaosaurus",
    "name": "Beipiaosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~125 mya",
    "diet": "HERBIVORE",
    "length": "7 ft",
    "weight": "190 lb",
    "habitat": "Forests of China",
    "description": "A small early therizinosaur with long claws and a beaked jaw. Beipiaosaurus was covered in feathers, including unusual elongated feathers on its arms. It was a plant-eating theropod — a dinosaur paradox.",
    "funFacts": [
      "Discovered 1999",
      "Covered in feathers including elongated arm feathers",
      "A plant-eating theropod",
      "Small and lightly built for a therizinosaur"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF7A6A3A"
  },
  {
    "id": "incisivosaurus",
    "name": "Incisivosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~125 mya",
    "diet": "HERBIVORE",
    "length": "3.3 ft",
    "weight": "22 lb",
    "habitat": "Forests of China",
    "description": "A bizarre early oviraptorosaur with rodent-like front teeth. Incisivosaurus shows that oviraptorosaurs evolved from toothed ancestors before developing beaks. It was one of the most unusual-looking dinosaurs.",
    "funFacts": [
      "Discovered 2002",
      "Had rodent-like front teeth",
      "Shows beaked oviraptorosaurs evolved from toothed ancestors",
      "One of the most unusual-looking dinosaurs"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF8B6A4A"
  },
  {
    "id": "psittacosaurus",
    "name": "Psittacosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~133–120 mya",
    "diet": "HERBIVORE",
    "length": "6.5 ft",
    "weight": "44 lb",
    "habitat": "Forests of Asia",
    "description": "The 'parrot lizard' — an early ceratopsian with a parrot-like beak. Psittacosaurus is one of the most completely known dinosaurs, with specimens showing skin, quills on its tail, and even preserved color patterns.",
    "funFacts": [
      "Discovered 1923",
      "One of the most completely known dinosaurs",
      "Had quills on its tail — like a porcupine",
      "Preserved color patterns show countershading"
    ],
    "foundIn": [
      "Liaoning, China",
      "Mongolia",
      "Thailand",
      "Russia"
    ],
    "color": "#FF6A8A4A"
  },
  {
    "id": "pelecanimimus",
    "name": "Pelecanimimus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~125 mya",
    "diet": "OMNIVORE",
    "length": "8 ft",
    "weight": "110 lb",
    "habitat": "Lakes of Spain",
    "description": "An ornithomimosaur with over 200 tiny teeth — the most teeth of any theropod. Pelecanimimus had a pelican-like throat pouch and soft tissue impressions show it had a crest on its head.",
    "funFacts": [
      "Discovered 1994",
      "Over 200 teeth — most of any theropod",
      "Had a pelican-like throat pouch",
      "Soft tissue preserved showing a head crest"
    ],
    "foundIn": [
      "Las Hoyas, Cuenca, Spain"
    ],
    "color": "#FF7A7A5A"
  },
  {
    "id": "polacanthus",
    "name": "Polacanthus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~130–125 mya",
    "diet": "HERBIVORE",
    "length": "16 ft",
    "weight": "2 tons",
    "habitat": "Forests of Europe",
    "description": "A nodosaur with large spikes along its sides and a shield of bony plates over its hips. Polacanthus was well-protected from predators like Baryonyx and Neovenator that shared its habitat.",
    "funFacts": [
      "Discovered 1865",
      "Large spikes along its sides",
      "Hip shield of fused bony plates",
      "English dinosaur from the Wealden"
    ],
    "foundIn": [
      "Isle of Wight, England",
      "Spain"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "hypsilophodon",
    "name": "Hypsilophodon",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~130–125 mya",
    "diet": "HERBIVORE",
    "length": "7.5 ft",
    "weight": "110 lb",
    "habitat": "Forests of Europe",
    "description": "A small, fast-running herbivore. Early paleontologists thought Hypsilophodon could climb trees like a monkey — it couldn't, but it was an agile runner. It had sharp claws and excellent vision.",
    "funFacts": [
      "Discovered 1849",
      "Once thought to climb trees like a monkey — it couldn't",
      "Fast runner with excellent vision",
      "About the size of a large dog"
    ],
    "foundIn": [
      "Isle of Wight, England",
      "Spain"
    ],
    "color": "#FF5A8A6A"
  },
  {
    "id": "ouranosaurus",
    "name": "Ouranosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~112 mya",
    "diet": "HERBIVORE",
    "length": "23–27 ft",
    "weight": "2–4 tons",
    "habitat": "River deltas of North Africa",
    "description": "A distinctive hadrosaur relative with a large sail or hump on its back. Unlike Spinosaurus, its sail may have been a fatty hump like a camel's. Ouranosaurus lived alongside Spinosaurus and Sarcosuchus in Cretaceous Africa.",
    "funFacts": [
      "Discovered 1965 by Philippe Taquet",
      "Had a large sail or hump on its back",
      "May have stored fat like a camel",
      "Lived alongside Spinosaurus"
    ],
    "foundIn": [
      "Gadoufaoua, Niger"
    ],
    "color": "#FF8B7A4A"
  },
  {
    "id": "rebbachisaurus",
    "name": "Rebbachisaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~99–97 mya",
    "diet": "HERBIVORE",
    "length": "46–66 ft",
    "weight": "10 tons",
    "habitat": "Plains of North Africa",
    "description": "A sauropod with an unusually high dorsal fin of spines along its back. Rebbachisaurus was a diplodocoid — a group that thrived in Gondwana after their northern relatives went extinct.",
    "funFacts": [
      "Discovered 1954",
      "High fin of spines along its back",
      "Last surviving diplodocoid sauropods",
      "Thrived in Cretaceous Africa"
    ],
    "foundIn": [
      "Kem Kem Beds, Morocco",
      "Argentina"
    ],
    "color": "#FF5A8A4A"
  },
  {
    "id": "nigersaurus",
    "name": "Nigersaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~115–105 mya",
    "diet": "HERBIVORE",
    "length": "30 ft",
    "weight": "4 tons",
    "habitat": "River deltas of North Africa",
    "description": "A bizarre sauropod with a vacuum-cleaner-like mouth containing 500+ teeth arranged in a wide rectangular muzzle. Nigersaurus grazed low plants like a living lawnmower — its skull was lighter than a modern horse's.",
    "funFacts": [
      "Discovered 1976 by Philippe Taquet",
      "500+ teeth in a vacuum-cleaner mouth",
      "Skull lighter than a horse's skull",
      "Grazed like a living lawnmower"
    ],
    "foundIn": [
      "Gadoufaoua, Niger"
    ],
    "color": "#FF7A9A6A"
  },
  {
    "id": "dreadnoughtus",
    "name": "Dreadnoughtus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~77 mya",
    "diet": "HERBIVORE",
    "length": "85 ft",
    "weight": "65 tons",
    "habitat": "Plains of South America",
    "description": "One of the most complete giant titanosaurs. Dreadnoughtus weighed 65 tons — as much as 12 elephants. Its name means 'fears nothing,' reflecting its enormous size. It was still growing when it died.",
    "funFacts": [
      "Discovered 2005 by Kenneth Lacovara",
      "Weighed 65 tons — as much as 12 elephants",
      "Name means 'fears nothing'",
      "Still growing when it died",
      "One of the most complete giant titanosaurs"
    ],
    "foundIn": [
      "Patagonia, Argentina"
    ],
    "color": "#FF5A7A6A"
  },
  {
    "id": "patagotitan",
    "name": "Patagotitan",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~101 mya",
    "diet": "HERBIVORE",
    "length": "102 ft",
    "weight": "69 tons",
    "habitat": "Plains of South America",
    "description": "One of the largest animals ever to live. Patagotitan may have been the heaviest land animal of all time at 69 tons. It was so massive that a full-size cast barely fits in most museum halls.",
    "funFacts": [
      "Discovered 2008",
      "Possibly the heaviest land animal ever — 69 tons",
      "102 ft long — longer than a basketball court",
      "So big its cast barely fits in museums"
    ],
    "foundIn": [
      "Chubut, Argentina"
    ],
    "color": "#FF6A8A5A"
  },
  {
    "id": "puertasaurus",
    "name": "Puertasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "HERBIVORE",
    "length": "98 ft",
    "weight": "50–90 tons",
    "habitat": "Plains of South America",
    "description": "Known from only four massive vertebrae, Puertasaurus may have been among the largest dinosaurs ever. Its single dorsal vertebra is the widest of any known sauropod, suggesting an extraordinarily broad body.",
    "funFacts": [
      "Discovered 2001",
      "One vertebra is the widest of any sauropod",
      "Known from only 4 bones — but they're enormous",
      "May have been 90 tons or more"
    ],
    "foundIn": [
      "Santa Cruz, Argentina"
    ],
    "color": "#FF7A8A6A"
  },
  {
    "id": "saltasaurus",
    "name": "Saltasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "HERBIVORE",
    "length": "40 ft",
    "weight": "7 tons",
    "habitat": "Plains of South America",
    "description": "A small titanosaur that was covered in bony armor plates — the first proof that some sauropods had armor. Saltasaurus was relatively small for a sauropod but still enormous compared to a human.",
    "funFacts": [
      "Discovered 1980",
      "Armored sauropod — bony plates in its skin",
      "First proof that some long-necks had armor",
      "Relatively small for a sauropod at 40 ft"
    ],
    "foundIn": [
      "Salta, Argentina",
      "Uruguay"
    ],
    "color": "#FF8B7A6A"
  },
  {
    "id": "amargasaurus",
    "name": "Amargasaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~129–122 mya",
    "diet": "HERBIVORE",
    "length": "33 ft",
    "weight": "2.8 tons",
    "habitat": "Plains of South America",
    "description": "A sauropod with an extraordinary double row of tall spines along its neck and back — possibly supporting a sail or paired spines. Amargasaurus was one of the most visually striking dinosaurs ever.",
    "funFacts": [
      "Discovered 1984",
      "Double row of tall spines on its neck",
      "May have had paired sails or spines",
      "One of the most visually striking dinosaurs"
    ],
    "foundIn": [
      "La Amarga Formation, Neuqu\\u00e9n, Argentina"
    ],
    "color": "#FF6A7A8A"
  },
  {
    "id": "dakotaraptor",
    "name": "Dakotaraptor",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~66 mya",
    "diet": "CARNIVORE",
    "length": "18 ft",
    "weight": "700 lb",
    "habitat": "Forests of North America",
    "description": "A large dromaeosaurid that lived alongside T. rex. Dakotaraptor was 18 feet long with 10-inch killing claws. It was one of the last non-avian dinosaurs, going extinct in the end-Cretaceous event.",
    "funFacts": [
      "Discovered 2005",
      "Lived alongside T. rex — one of the last raptors",
      "10-inch killing claws on each foot",
      "One of the last non-avian dinosaurs"
    ],
    "foundIn": [
      "Hell Creek Formation, South Dakota, USA"
    ],
    "color": "#FF8B5A3A"
  },
  {
    "id": "majungasaurus",
    "name": "Majungasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70–66 mya",
    "diet": "CARNIVORE",
    "length": "20–26 ft",
    "weight": "1.5 tons",
    "habitat": "Plains of Madagascar",
    "description": "An abelisaurid with a short, deep skull and a single horn-like bump on its head. Majungasaurus is the only dinosaur with direct evidence of cannibalism — tooth marks on its own kind's bones.",
    "funFacts": [
      "Discovered 1896",
      "Only dinosaur with proven cannibalism",
      "Short deep skull with a horn bump",
      "Apex predator of Cretaceous Madagascar"
    ],
    "foundIn": [
      "Maevarano Formation, Madagascar"
    ],
    "color": "#FF6A5A3A"
  },
  {
    "id": "abelisaurus",
    "name": "Abelisaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~83–71 mya",
    "diet": "CARNIVORE",
    "length": "25–33 ft",
    "weight": "3 tons",
    "habitat": "Plains of South America",
    "description": "A large predatory dinosaur with a short, deep skull. Abelisaurus gave its name to the Abelisauridae — a family of Southern Hemisphere carnivores with tiny arms even shorter than T. rex's.",
    "funFacts": [
      "Discovered 1985",
      "Gave its name to an entire dinosaur family",
      "Arms even shorter than T. rex",
      "Short, deep skull suited for powerful bites"
    ],
    "foundIn": [
      "R\\u00edo Negro, Argentina"
    ],
    "color": "#FF5A6A3A"
  },
  {
    "id": "rajasaurus",
    "name": "Rajasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70–66 mya",
    "diet": "CARNIVORE",
    "length": "23–30 ft",
    "weight": "3–4 tons",
    "habitat": "Forests of India",
    "description": "An Indian abelisaurid with a single horn on its skull. Rajasaurus shows that abelisaurs spread across the Southern Hemisphere, including the island continent of India before it collided with Asia.",
    "funFacts": [
      "Discovered 2003",
      "Had a single horn on its skull",
      "Lived on the island continent of India",
      "Name means 'king lizard'"
    ],
    "foundIn": [
      "Gujarat, India"
    ],
    "color": "#FF7A5A5A"
  },
  {
    "id": "muttaburrasaurus",
    "name": "Muttaburrasaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~112–103 mya",
    "diet": "HERBIVORE",
    "length": "26 ft",
    "weight": "3 tons",
    "habitat": "Forests of Australia",
    "description": "An Australian dinosaur with a bulbous nasal cavity that may have produced loud trumpeting calls. Muttaburrasaurus could walk on two or four legs and may have included meat in its diet — unusual for an ornithopod.",
    "funFacts": [
      "Discovered 1963 by Doug Langdon",
      "Australian dinosaur with a bulbous nose",
      "May have been partially carnivorous",
      "Could trumpet like an elephant"
    ],
    "foundIn": [
      "Muttaburra, Queensland, Australia"
    ],
    "color": "#FF8B8A5A"
  },
  {
    "id": "minmi",
    "name": "Minmi",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~119–113 mya",
    "diet": "HERBIVORE",
    "length": "10 ft",
    "weight": "600 lb",
    "habitat": "Forests of Australia",
    "description": "A small Australian ankylosaur — the most complete dinosaur found in Australia. Minmi had bony armor and possibly horizontal bony plates along its sides. It is named after the Minmi Crossing where it was found.",
    "funFacts": [
      "Discovered 1964",
      "Most complete dinosaur found in Australia",
      "Had bony armor including side plates",
      "Shortest dinosaur genus name"
    ],
    "foundIn": [
      "Queensland, Australia"
    ],
    "color": "#FF7A7A4A"
  },
  {
    "id": "leaellynasaura",
    "name": "Leaellynasaura",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~118–110 mya",
    "diet": "HERBIVORE",
    "length": "8 ft",
    "weight": "55 lb",
    "habitat": "Polar forests of Australia",
    "description": "A small dinosaur that lived in polar Australia where winter brought months of darkness. Leaellynasaura had exceptionally large eyes — an adaptation for seeing in dark polar conditions.",
    "funFacts": [
      "Discovered 1989",
      "Lived in polar darkness — months without sun",
      "Exceptionally large eyes for night vision",
      "Named after Leaellyn Rich, the discoverer's daughter"
    ],
    "foundIn": [
      "Victoria, Australia"
    ],
    "color": "#FF6A9A8A"
  },
  {
    "id": "zuniceratops",
    "name": "Zuniceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~91 mya",
    "diet": "HERBIVORE",
    "length": "10 ft",
    "weight": "500 lb",
    "habitat": "Forests of North America",
    "description": "The oldest known ceratopsian with brow horns. Zuniceratops represents a critical transitional form — showing how horned dinosaurs evolved from hornless ancestors. It had a small frill and two brow horns.",
    "funFacts": [
      "Discovered 1996",
      "Oldest ceratopsian with true brow horns",
      "Transitional form in ceratopsian evolution",
      "Predates Triceratops by 25 million years"
    ],
    "foundIn": [
      "Arizona, USA"
    ],
    "color": "#FF8B7A7A"
  },
  {
    "id": "struthiomimus",
    "name": "Struthiomimus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~77–66 mya",
    "diet": "OMNIVORE",
    "length": "14 ft",
    "weight": "330 lb",
    "habitat": "Plains of North America",
    "description": "An ostrich-mimic dinosaur with long legs, a long neck, and a toothless beak. Struthiomimus could run at high speed to escape predators. It had some of the proportionally longest legs of any dinosaur.",
    "funFacts": [
      "Discovered 1914",
      "Ostrich-mimic — could run at high speed",
      "Some of the proportionally longest legs of any dinosaur",
      "Toothless beak for omnivorous foraging"
    ],
    "foundIn": [
      "Alberta, Canada",
      "New Jersey, USA"
    ],
    "color": "#FF8B8A6A"
  },
  {
    "id": "tsintaosaurus",
    "name": "Tsintaosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~84–70 mya",
    "diet": "HERBIVORE",
    "length": "27 ft",
    "weight": "3 tons",
    "habitat": "Floodplains of China",
    "description": "A hadrosaur with what was originally reconstructed as a unicorn-like spike crest on its head. Later studies suggest the spike was part of a more complex S-shaped crest. Either way, it was one of the most distinctive duck-bills.",
    "funFacts": [
      "Discovered 1950",
      "Originally thought to have a unicorn horn crest",
      "May have had an S-shaped crest instead",
      "One of the most distinctive hadrosaurs"
    ],
    "foundIn": [
      "Shandong, China"
    ],
    "color": "#FF7A8A5A"
  },
  {
    "id": "orodromeus",
    "name": "Orodromeus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75 mya",
    "diet": "HERBIVORE",
    "length": "6.5 ft",
    "weight": "50 lb",
    "habitat": "Floodplains of North America",
    "description": "A small bipedal herbivore. Orodromeus eggs and hatchlings show that some dinosaurs cared for their young. Its name means 'mountain runner' — it was fast and agile.",
    "funFacts": [
      "Discovered 1981 by Jack Horner",
      "Eggs and hatchlings found — evidence of nesting",
      "Name means 'mountain runner'",
      "Fast and agile — outran most predators"
    ],
    "foundIn": [
      "Montana, USA"
    ],
    "color": "#FF5A8A50"
  },
  {
    "id": "mei",
    "name": "Mei long",
    "era": "JURASSIC",
    "period": "Early Cretaceous",
    "age": "~130 mya",
    "diet": "CARNIVORE",
    "length": "2 ft",
    "weight": "1 lb",
    "habitat": "Forests of China",
    "description": "A tiny troodontid preserved in a sleeping posture — curled up with its head tucked under its arm. Mei long is one of the most perfectly preserved dinosaur fossils ever found, and its name means 'sleeping dragon.'",
    "funFacts": [
      "Discovered 2004",
      "Found sleeping with head tucked under its arm",
      "Name means 'sleeping dragon'",
      "One of the smallest dinosaurs ever",
      "Preserved in a bird-like sleeping posture"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF6A7A6A"
  },
  {
    "id": "sinosauropteryx",
    "name": "Sinosauropteryx",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~124 mya",
    "diet": "CARNIVORE",
    "length": "3.3 ft",
    "weight": "5.5 lb",
    "habitat": "Lake margins of China",
    "description": "The first dinosaur found with evidence of feathers that wasn't a bird. Its simple hair-like feathers were preserved in exquisite detail, proving non-avian dinosaurs had feathers.",
    "funFacts": [
      "Discovered 1996 in Liaoning, China",
      "First non-avian dinosaur found with feathers",
      "Had a banded tail — dark and light stripes",
      "About the size of a large rooster"
    ],
    "foundIn": [
      "Liaoning Province, China"
    ],
    "color": "#FF8B7A6A"
  },
  {
    "id": "Epidexipteryx",
    "name": "Epidexipteryx",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~152–168 mya",
    "diet": "INSECTIVORE",
    "length": "1.6 ft",
    "weight": "3.5 oz",
    "habitat": "Forests of China",
    "description": "A tiny dinosaur with four long ribbon-like tail feathers used for display. Epidexipteryx had short arms with elongated fingers and curved claws for climbing trees. It could not fly but was an agile climber.",
    "funFacts": [
      "Discovered 2008",
      "Had four long ribbon-like tail feathers for display",
      "Could not fly — was a tree climber",
      "Ate insects with its short snout and teeth"
    ],
    "foundIn": [
      "Inner Mongolia, China"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "tenontosaurus",
    "name": "Tenontosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~115–108 mya",
    "diet": "HERBIVORE",
    "length": "23–26 ft",
    "weight": "1–2 tons",
    "habitat": "Forests of North America",
    "description": "A large ornithopod with an extraordinarily long tail — over half its body length. Tenontosaurus is famous for being found alongside Deinonychus, providing evidence that raptors hunted in packs to bring down larger prey.",
    "funFacts": [
      "Discovered 1970 by John Ostrom",
      "Extraordinarily long tail — over half its body length",
      "Found alongside Deinonychus — evidence of pack hunting",
      "May have been the primary prey of raptors"
    ],
    "foundIn": [
      "Montana, USA",
      "Wyoming, USA",
      "Oklahoma, USA"
    ],
    "color": "#FF7A8A5A"
  },
  {
    "id": "monoclonius",
    "name": "Monoclonius",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~80–75 mya",
    "diet": "HERBIVORE",
    "length": "16 ft",
    "weight": "2 tons",
    "habitat": "Floodplains of North America",
    "description": "An early-named ceratopsian with a single large nose horn and a moderately sized frill. Monoclonius was once thought to be distinct from Centrosaurus but the two are now debated — some consider them the same genus.",
    "funFacts": [
      "Discovered 1876 by Edward Drinker Cope",
      "Had a single large nose horn",
      "May be the same as Centrosaurus",
      "Name means 'single sprout'"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Montana, USA"
    ],
    "color": "#FF6A5A6A"
  },
  {
    "id": "leptoceratops",
    "name": "Leptoceratops",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~67–66 mya",
    "diet": "HERBIVORE",
    "length": "6 ft",
    "weight": "400 lb",
    "habitat": "Forests of North America",
    "description": "A small primitive ceratopsian that lived alongside Triceratops at the very end of the dinosaur era. Leptoceratops had no horns and a small frill — it may have used burrowing as a defense strategy.",
    "funFacts": [
      "Discovered 1913",
      "Lived alongside Triceratops at the end",
      "May have burrowed for protection",
      "Small and hornless — unlike Triceratops"
    ],
    "foundIn": [
      "Alberta, Canada",
      "Wyoming, USA"
    ],
    "color": "#FF8B7A5A"
  },
  {
    "id": "austroraptor",
    "name": "Austroraptor",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "CARNIVORE",
    "length": "20 ft",
    "weight": "660 lb",
    "habitat": "Plains of South America",
    "description": "A large unenlagine dromaeosaurid from Argentina with a long, slender snout and relatively short arms. Austroraptor was one of the largest raptors of the Southern Hemisphere and hunted in Cretaceous Patagonia.",
    "funFacts": [
      "Discovered 2008",
      "One of the largest raptors of the Southern Hemisphere",
      "Had a long slender snout unlike northern raptors",
      "Hunted in Cretaceous Patagonia"
    ],
    "foundIn": [
      "Río Negro, Argentina"
    ],
    "color": "#FF7A5A40"
  },
  {
    "id": "megaraptor",
    "name": "Megaraptor",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~90 mya",
    "diet": "CARNIVORE",
    "length": "26–30 ft",
    "weight": "2 tons",
    "habitat": "Plains of South America",
    "description": "A large theropod with enormous 14-inch claws on its hands. Megaraptor was once thought to be a giant raptor, but its claws are on its hands, not its feet. Its exact classification remains debated — it may be a basal tyrannosauroid or a neovenatorid.",
    "funFacts": [
      "Discovered 1996",
      "Had 14-inch claws on its HANDS, not feet",
      "Classification is still debated",
      "Name means 'giant thief'"
    ],
    "foundIn": [
      "Neuquén, Argentina"
    ],
    "color": "#FF6A5A3A"
  },
  {
    "id": "rapetosaurus",
    "name": "Rapetosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70–66 mya",
    "diet": "HERBIVORE",
    "length": "49 ft",
    "weight": "16 tons",
    "habitat": "Plains of Madagascar",
    "description": "The most complete titanosaur from Madagascar. Juvenile Rapetosaurus bones show that baby titanosaurs were precocial — able to walk and forage on their own shortly after hatching, without parental care.",
    "funFacts": [
      "Discovered 1996",
      "Most complete titanosaur from Madagascar",
      "Babies were independent right after hatching",
      "Juvenile bones show rapid growth"
    ],
    "foundIn": [
      "Maevarano Formation, Madagascar"
    ],
    "color": "#FF7A8A5A"
  },
  {
    "id": "noasaurus",
    "name": "Noasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "CARNIVORE",
    "length": "8 ft",
    "weight": "33 lb",
    "habitat": "Plains of South America",
    "description": "A small predatory dinosaur from Madagascar-like Patagonia. Noasaurus gave its name to the Noasauridae — a family of small Southern Hemisphere carnivores related to abelisaurids but much smaller.",
    "funFacts": [
      "Discovered 1980",
      "Gave its name to an entire dinosaur family",
      "Small Southern Hemisphere predator",
      "Related to the larger abelisaurids"
    ],
    "foundIn": [
      "Salta, Argentina"
    ],
    "color": "#FF6A5A4A"
  },
  {
    "id": "masiakasaurus",
    "name": "Masiakasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70–66 mya",
    "diet": "CARNIVORE",
    "length": "6.5 ft",
    "weight": "35 lb",
    "habitat": "Plains of Madagascar",
    "description": "A bizarre noasaurid with forward-pointing, procumbent teeth — unlike any other theropod. Masiakasaurus likely hunted fish and small prey with its strange jaws. Its name means 'vicious lizard.'",
    "funFacts": [
      "Discovered 2001",
      "Had bizarre forward-pointing teeth",
      "Likely hunted fish and small prey",
      "Name means 'vicious lizard'"
    ],
    "foundIn": [
      "Maevarano Formation, Madagascar"
    ],
    "color": "#FF7A5A3A"
  },
  {
    "id": "zanabazar",
    "name": "Zanabazar",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~72 mya",
    "diet": "CARNIVORE",
    "length": "10 ft",
    "weight": "110 lb",
    "habitat": "Deserts of Mongolia",
    "description": "A large troodontid from the Gobi Desert with excellent binocular vision and a large brain. Zanabazar was a fast, intelligent predator that hunted lizards and small mammals in Cretaceous Mongolia.",
    "funFacts": [
      "Discovered 1985",
      "One of the largest troodontids",
      "Excellent binocular vision and large brain",
      "Named after the first Jebtsundamba Khutughtu"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF5A6A7A"
  },
  {
    "id": "byronosaurus",
    "name": "Byronosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~75 mya",
    "diet": "CARNIVORE",
    "length": "5 ft",
    "weight": "11 lb",
    "habitat": "Deserts of Mongolia",
    "description": "A small troodontid with needle-like teeth instead of the serrated teeth typical of theropods. Byronosaurus may have used its teeth to probe for small prey in crevices or for eating eggs.",
    "funFacts": [
      "Discovered 1993",
      "Had needle-like teeth — unusual for a theropod",
      "May have probed crevices for small prey",
      "Named after the Byron Reed Foundation"
    ],
    "foundIn": [
      "Djadochta Formation, Gobi Desert, Mongolia"
    ],
    "color": "#FF6A7A8A"
  },
  {
    "id": "pteranodon",
    "name": "Pteranodon",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~86–84 mya",
    "diet": "PISCIVORE",
    "length": "23 ft wingspan",
    "weight": "44 lb",
    "habitat": "Seas of North America",
    "description": "The largest toothless pterosaur, with a 23-foot wingspan. Over 1,200 Pteranodon specimens have been found. Males had large backward-pointing crests; females had small ones. It soared over the Western Interior Seaway scooping fish.",
    "funFacts": [
      "Discovered 1876 by Othniel Charles Marsh",
      "Over 1,200 specimens found",
      "Males had large crests; females had small ones",
      "NOT a dinosaur — a flying reptile",
      "Could fly across the inland sea in a day"
    ],
    "foundIn": [
      "Niobrara Chalk, Kansas, USA"
    ],
    "color": "#FF6FBF8A"
  },
  {
    "id": "quetzalcoatlus",
    "name": "Quetzalcoatlus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~68–66 mya",
    "diet": "CARNIVORE",
    "length": "36 ft wingspan",
    "weight": "440–550 lb",
    "habitat": "Plains of North America",
    "description": "The largest flying animal ever. Quetzalcoatlus stood as tall as a giraffe on the ground and had a 36-foot wingspan. Named after the Aztec feathered serpent god, it may have hunted small dinosaurs on foot like a giant stork.",
    "funFacts": [
      "Discovered 1975",
      "Largest flying animal ever — 36-ft wingspan",
      "Stood as tall as a giraffe on the ground",
      "Named after the Aztec god Quetzalcoatl",
      "May have hunted on the ground like a stork"
    ],
    "foundIn": [
      "Javelina Formation, Big Bend, Texas, USA"
    ],
    "color": "#FF5FA070"
  },
  {
    "id": "dimorphodon",
    "name": "Dimorphodon",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~195–190 mya",
    "diet": "INSECTIVORE",
    "length": "4.5 ft wingspan",
    "weight": "3.3 lb",
    "habitat": "Coastal forests of England",
    "description": "An early pterosaur with a large puffin-like head and two types of teeth. Discovered by Mary Anning on the Jurassic Coast. Dimorphodon had a short wingspan and may have been a clumsy flyer that climbed trees.",
    "funFacts": [
      "Discovered 1828 by Mary Anning",
      "Discovered by pioneering fossil hunter Mary Anning",
      "Had a puffin-like head with two tooth types",
      "May have been a tree-climbing pterosaur"
    ],
    "foundIn": [
      "Lyme Regis, Dorset, England"
    ],
    "color": "#FF5F9060"
  },
  {
    "id": "rhamphorhynchus",
    "name": "Rhamphorhynchus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~150 mya",
    "diet": "PISCIVORE",
    "length": "5.9 ft wingspan",
    "weight": "2.2 lb",
    "habitat": "Lagoon islands of Europe",
    "description": "A long-tailed pterosaur with needle-like teeth for catching fish. Rhamphorhynchus specimens from Solnhofen preserve wing membranes, showing it had a stiff tail with a diamond-shaped vane at the tip.",
    "funFacts": [
      "Discovered 1846",
      "Wing membranes preserved in exquisite detail",
      "Long stiff tail with a diamond vane",
      "Caught fish in Jurassic lagoons"
    ],
    "foundIn": [
      "Solnhofen Limestone, Bavaria, Germany"
    ],
    "color": "#FF6A9070"
  },
  {
    "id": "tapejara",
    "name": "Tapejara",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~112 mya",
    "diet": "OMNIVORE",
    "length": "16 ft wingspan",
    "weight": "50 lb",
    "habitat": "Coastal regions of Brazil",
    "description": "A colorful pterosaur with an enormous sail-like crest on its head. Tapejara may have been brightly colored, and its crest may have been used for display. Some specimens show crests as large as the head itself.",
    "funFacts": [
      "Discovered 1989",
      "Enormous sail-like crest on its head",
      "May have been brightly colored",
      "Crest may have been for species recognition"
    ],
    "foundIn": [
      "Araripe Basin, Brazil"
    ],
    "color": "#FF8A5A7A"
  },
  {
    "id": "hatzegopteryx",
    "name": "Hatzegopteryx",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70–66 mya",
    "diet": "CARNIVORE",
    "length": "33–36 ft wingspan",
    "weight": "550 lb",
    "habitat": "Islands of Europe",
    "description": "A giant pterosaur as large as Quetzalcoatlus but with a shorter, more robust neck. Hatzegopteryx lived on the island of Hateg in Europe, where it may have been the apex predator — hunting dwarf dinosaurs on foot.",
    "funFacts": [
      "Discovered 2002",
      "As large as Quetzalcoatlus but more robust",
      "Apex predator of a European island — hunted dwarf dinosaurs",
      "May have stalked prey on the ground"
    ],
    "foundIn": [
      "Ha\\u021beg Basin, Romania"
    ],
    "color": "#FF6A8A7A"
  },
  {
    "id": "elasmosaurus",
    "name": "Elasmosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~80 mya",
    "diet": "PISCIVORE",
    "length": "45 ft",
    "weight": "4 tons",
    "habitat": "Inland seas of North America",
    "description": "A plesiosaur with an astonishing 72 neck vertebrae — more than any other animal. Half its length was neck. When first described, Edward Cope put its head on the wrong end, starting a famous paleontological rivalry.",
    "funFacts": [
      "Discovered 1868 by Edward Drinker Cope",
      "72 neck vertebrae — more than any animal",
      "First reconstruction put the head on the tail — a famous mistake",
      "Half its length was just neck"
    ],
    "foundIn": [
      "Pierre Shale, Kansas, USA"
    ],
    "color": "#FF4078A0"
  },
  {
    "id": "plesiosaurus",
    "name": "Plesiosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~200–190 mya",
    "diet": "PISCIVORE",
    "length": "15 ft",
    "weight": "1,100 lb",
    "habitat": "Seas of Europe",
    "description": "The first complete plesiosaur, discovered by Mary Anning in 1823. Plesiosaurus had a small head on a long neck, four paddle-like flippers, and 'flew' through the water. It inspired legends of the Loch Ness Monster.",
    "funFacts": [
      "Discovered 1823 by Mary Anning",
      "Inspired the Loch Ness Monster legend",
      "Swam using all four flippers in a 'flying' motion",
      "Mary Anning discovered the first complete specimen"
    ],
    "foundIn": [
      "Lyme Regis, Dorset, England"
    ],
    "color": "#FF4080A0"
  },
  {
    "id": "ichthyosaurus",
    "name": "Ichthyosaurus",
    "era": "JURASSIC",
    "period": "Early Jurassic",
    "age": "~200–190 mya",
    "diet": "PISCIVORE",
    "length": "11 ft",
    "weight": "2,200 lb",
    "habitat": "Seas of Europe",
    "description": "A dolphin-shaped marine reptile with enormous eyes for deep-sea hunting. Ichthyosaurus gave birth to live young — fossilized mothers with embryos have been found. Its eyes were the size of dinner plates.",
    "funFacts": [
      "Discovered 1821 by Mary Anning",
      "Gave birth to live young — fossil embryos found",
      "Eyes the size of dinner plates",
      "Mary Anning discovered the first complete skeleton"
    ],
    "foundIn": [
      "Lyme Regis, England",
      "Holzmaden, Germany"
    ],
    "color": "#FF5090B0"
  },
  {
    "id": "liopleurodon",
    "name": "Liopleurodon",
    "era": "JURASSIC",
    "period": "Middle Jurassic",
    "age": "~166–160 mya",
    "diet": "CARNIVORE",
    "length": "20–33 ft",
    "weight": "3–8 tons",
    "habitat": "Seas of Europe",
    "description": "A short-necked pliosaur with massive jaws and 4-inch teeth. Liopleurodon was an apex predator of Jurassic seas, hunting fish, squid, and other marine reptiles. Its size was exaggerated in 'Walking with Dinosaurs.'",
    "funFacts": [
      "Discovered 1873",
      "Apex predator of the Jurassic seas",
      "Teeth up to 4 inches long",
      "Short neck, massive jaws — a pliosaur"
    ],
    "foundIn": [
      "Oxford Clay, England",
      "France"
    ],
    "color": "#FF306090"
  },
  {
    "id": "mosasaurus",
    "name": "Mosasaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~82–66 mya",
    "diet": "CARNIVORE",
    "length": "56 ft",
    "weight": "15 tons",
    "habitat": "Seas worldwide",
    "description": "A giant marine lizard — not a dinosaur but a close relative of monitor lizards and snakes. Mosasaurus was the apex predator of the Late Cretaceous seas, hunting everything from fish to other mosasaurs. It went extinct with the dinosaurs.",
    "funFacts": [
      "Discovered 1764",
      "Apex predator of the Cretaceous seas",
      "Related to monitor lizards and snakes",
      "Could unhinge its jaw to swallow large prey"
    ],
    "foundIn": [
      "Maastricht, Netherlands",
      "Kansas, USA",
      "Morocco"
    ],
    "color": "#FF205080"
  },
  {
    "id": "tylosaurus",
    "name": "Tylosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~85–80 mya",
    "diet": "CARNIVORE",
    "length": "33–50 ft",
    "weight": "10 tons",
    "habitat": "Inland seas of North America",
    "description": "A large mosasaur that dominated the Western Interior Seaway of North America. Tylosaurus had a long, cone-shaped snout used for ramming prey. Stomach contents show it ate fish, sharks, birds, and other mosasaurs.",
    "funFacts": [
      "Discovered 1869 by Othniel Charles Marsh",
      "Dominated the inland sea of North America",
      "Used its snout to ram prey",
      "Ate sharks and even other mosasaurs"
    ],
    "foundIn": [
      "Kansas, USA",
      "Alabama, USA"
    ],
    "color": "#FF306880"
  },
  {
    "id": "plotosaurus",
    "name": "Plotosaurus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~70 mya",
    "diet": "PISCIVORE",
    "length": "30 ft",
    "weight": "3 tons",
    "habitat": "Seas of North America",
    "description": "A highly derived mosasaur with a streamlined, fish-like body and a tail fluke like a shark. Plotosaurus was one of the fastest and most specialized mosasaurs — the most evolutionarily advanced of its kind.",
    "funFacts": [
      "Discovered 1942 by Charles Lewis Camp",
      "Most specialized mosasaur — shark-like tail fluke",
      "Highly streamlined for speed",
      "Most evolutionarily advanced mosasaur"
    ],
    "foundIn": [
      "California, USA"
    ],
    "color": "#FF408090"
  },
  {
    "id": "shonisaurus",
    "name": "Shonisaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~237–227 mya",
    "diet": "PISCIVORE",
    "length": "69 ft",
    "weight": "40 tons",
    "habitat": "Seas of North America",
    "description": "The largest ichthyosaur known — reaching 69 feet, as long as a sperm whale. Shonisaurus had a massive body and was a deep-sea predator. 37 individuals found together at Berlin-Ichthyosaur State Park in Nevada may represent a mass stranding.",
    "funFacts": [
      "Discovered 1928",
      "Largest ichthyosaur — 69 ft, as long as a sperm whale",
      "37 found together in Nevada — possible mass stranding",
      "Nevada's state fossil"
    ],
    "foundIn": [
      "Berlin-Ichthyosaur State Park, Nevada, USA"
    ],
    "color": "#FF5070A0"
  },
  {
    "id": "shastasaurus",
    "name": "Shastasaurus",
    "era": "TRIASSIC",
    "period": "Late Triassic",
    "age": "~235 mya",
    "diet": "PISCIVORE",
    "length": "69 ft",
    "weight": "40 tons",
    "habitat": "Seas of North America & Asia",
    "description": "The most specialized ichthyosaur — Shastasaurus had a short, toothless snout and may have been a suction feeder like modern beaked whales. It was among the largest marine reptiles ever.",
    "funFacts": [
      "Discovered 1895",
      "Short, toothless snout — suction feeder",
      "Among the largest marine reptiles ever",
      "Fed like a modern beaked whale"
    ],
    "foundIn": [
      "California, USA",
      "China"
    ],
    "color": "#FF4060A0"
  },
  {
    "id": "ophthalmosaurus",
    "name": "Ophthalmosaurus",
    "era": "JURASSIC",
    "period": "Late Jurassic",
    "age": "~165–160 mya",
    "diet": "PISCIVORE",
    "length": "20 ft",
    "weight": "2 tons",
    "habitat": "Seas worldwide",
    "description": "An ichthyosaur with the largest eyes of any vertebrate — up to 9 inches across. Ophthalmosaurus could see in the deepest, darkest ocean waters. It was a streamlined deep-diver that gave birth to live young.",
    "funFacts": [
      "Discovered 1874",
      "Largest eyes of any vertebrate — 9 inches across",
      "Deep-diver of the Jurassic seas",
      "Gave birth to live young in the water"
    ],
    "foundIn": [
      "England",
      "Argentina",
      "USA"
    ],
    "color": "#FF5080B0"
  },
  {
    "id": "kronosaurus",
    "name": "Kronosaurus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~112 mya",
    "diet": "CARNIVORE",
    "length": "30–33 ft",
    "weight": "10–12 tons",
    "habitat": "Seas of Australia",
    "description": "A massive pliosaur — a short-necked plesiosaur with a skull up to 9 feet long. Kronosaurus had large conical teeth for seizing large prey including other plesiosaurs. It was the apex predator of Australian Cretaceous seas.",
    "funFacts": [
      "Discovered 1899",
      "Skull up to 9 feet long",
      "Apex predator of Australian seas",
      "Hunted other marine reptiles"
    ],
    "foundIn": [
      "Queensland, Australia",
      "Colombia"
    ],
    "color": "#FF406890"
  },
  {
    "id": "sarcosuchus",
    "name": "Sarcosuchus",
    "era": "CRETACEOUS",
    "period": "Early Cretaceous",
    "age": "~112 mya",
    "diet": "CARNIVORE",
    "length": "31–40 ft",
    "weight": "3–8 tons",
    "habitat": "Rivers of North Africa",
    "description": "A giant crocodile that rivaled dinosaurs in size. Sarcosuchus, also called 'SuperCroc,' had a 6-foot skull and may have preyed on dinosaurs that came to drink. It lived alongside Spinosaurus and Suchomimus.",
    "funFacts": [
      "Discovered 1964",
      "'SuperCroc' — 40 ft long giant crocodile",
      "May have preyed on dinosaurs at water's edge",
      "6-foot skull with conical teeth"
    ],
    "foundIn": [
      "Gadoufaoua, Niger",
      "Brazil"
    ],
    "color": "#FF5A7A4A"
  },
  {
    "id": "deinosuchus",
    "name": "Deinosuchus",
    "era": "CRETACEOUS",
    "period": "Late Cretaceous",
    "age": "~82–73 mya",
    "diet": "CARNIVORE",
    "length": "33–40 ft",
    "weight": "5–8 tons",
    "habitat": "Rivers of North America",
    "description": "A giant alligator relative that was even larger than Sarcosuchus. Deinosuchus preyed on dinosaurs — bite marks on tyrannosaur bones show it even attacked young T. rex. It was the terror of Cretaceous rivers.",
    "funFacts": [
      "Discovered 1858",
      "Giant crocodile that hunted dinosaurs",
      "Bite marks found on tyrannosaur bones",
      "Even young T. rex feared it"
    ],
    "foundIn": [
      "Texas, USA",
      "Montana, USA",
      "North Carolina, USA"
    ],
    "color": "#FF4A6A3A"
  },
  {
    "id": "megalodon",
    "name": "Megalodon",
    "era": "NEOGENE",
    "period": "Miocene–Pliocene",
    "age": "~23–3.6 mya",
    "diet": "CARNIVORE",
    "length": "50–60 ft",
    "weight": "50–70 tons",
    "habitat": "Seas worldwide",
    "description": "The largest shark and largest predatory fish ever. Megalodon had 7-inch teeth and preyed on whales for 20 million years. Its bite force was the strongest of any animal — stronger than T. rex. It went extinct when oceans cooled.",
    "funFacts": [
      "Discovered 1843",
      "Largest shark ever — 60 ft, 3x a great white",
      "Teeth up to 7 inches — the size of a human hand",
      "Bite force stronger than T. rex",
      "Hunted whales worldwide"
    ],
    "foundIn": [
      "North Carolina, USA",
      "Peru",
      "Japan",
      "Florida, USA"
    ],
    "color": "#FF6090B0"
  },
  {
    "id": "leedsichthys",
    "name": "Leedsichthys",
    "era": "JURASSIC",
    "period": "Middle Jurassic",
    "age": "~165 mya",
    "diet": "FILTER_FEEDER",
    "length": "50 ft",
    "weight": "50 tons",
    "habitat": "Seas of Europe & South America",
    "description": "The largest bony fish ever — a 50-foot filter feeder like a giant Jurassic whale shark. Leedsichthys swam through the same seas as Liopleurodon, filtering plankton with enormous gill rakers. Its skeleton was so delicate that it is difficult to reconstruct.",
    "funFacts": [
      "Discovered 1886 by Alfred Nicholson Leeds",
      "Largest bony fish ever — 50 ft filter feeder",
      "Swam the same Jurassic seas as Liopleurodon",
      "Filtered plankton like a whale shark"
    ],
    "foundIn": [
      "Oxford Clay, Peterborough, England",
      "France",
      "Chile"
    ],
    "color": "#FF5090A0"
  },
  {
    "id": "basilosaurus",
    "name": "Basilosaurus",
    "era": "PALEOGENE",
    "period": "Late Eocene",
    "age": "~41–34 mya",
    "diet": "CARNIVORE",
    "length": "60 ft",
    "weight": "10 tons",
    "habitat": "Seas of North Africa & North America",
    "description": "An early whale with a serpentine body and tiny hind limbs — vestigial legs that link it to land ancestors. When first discovered it was mistaken for a marine reptile, hence the name 'king lizard.'",
    "funFacts": [
      "Discovered 1834",
      "Had tiny functional hind limbs — evidence of whale evolution from land",
      "Mistaken for a marine reptile — name means 'king lizard'",
      "Alabama's state fossil",
      "Wadi Al-Hitan in Egypt is filled with Basilosaurus fossils"
    ],
    "foundIn": [
      "Alabama, USA",
      "Wadi Al-Hitan, Egypt"
    ],
    "color": "#FF6090A0"
  },
  {
    "id": "paraceratherium",
    "name": "Paraceratherium",
    "era": "PALEOGENE",
    "period": "Oligocene",
    "age": "~34–23 mya",
    "diet": "HERBIVORE",
    "length": "26 ft",
    "weight": "15–20 tons",
    "habitat": "Plains of Asia",
    "description": "The largest land mammal ever — a hornless rhinoceros relative that stood 18 feet at the shoulder and weighed 20 tons. Paraceratherium browsed treetops like a mammalian sauropod across Oligocene Asia.",
    "funFacts": [
      "Discovered 1911 by Clive Forster Cooper",
      "Largest land mammal ever — 18 ft at the shoulder",
      "Weighed 20 tons — larger than any elephant",
      "Had no horn — a giant hornless rhino",
      "Browsed treetops like a mammalian sauropod"
    ],
    "foundIn": [
      "Mongolia",
      "Kazakhstan",
      "Pakistan",
      "China"
    ],
    "color": "#FF908070"
  },
  {
    "id": "andrewsarchus",
    "name": "Andrewsarchus",
    "era": "PALEOGENE",
    "period": "Eocene",
    "age": "~45 mya",
    "diet": "OMNIVORE",
    "length": "10 ft",
    "weight": "1,000 lb",
    "habitat": "Plains of Mongolia",
    "description": "Known only from a single 33-inch skull — the largest carnivorous mammal skull ever found. Andrewsarchus may have been the largest carnivorous land mammal, but without a skeleton, its true size and lifestyle remain a mystery.",
    "funFacts": [
      "Discovered 1923 by Roy Chapman Andrews",
      "Known only from one enormous skull",
      "May have been the largest carnivorous land mammal",
      "Its lifestyle is still a mystery",
      "Named after explorer Roy Chapman Andrews"
    ],
    "foundIn": [
      "Gobi Desert, Mongolia"
    ],
    "color": "#FF5A4A3A"
  },
  {
    "id": "entelodont",
    "name": "Entelodont",
    "era": "PALEOGENE",
    "period": "Oligocene",
    "age": "~33–23 mya",
    "diet": "OMNIVORE",
    "length": "6.5 ft",
    "weight": "1,000 lb",
    "habitat": "Plains of North America & Asia",
    "description": "The 'hell pig' or 'terminator pig' — a pig-like omnivore the size of a bison with bone-crushing jaws and bony knobs on its skull. Despite the nickname, entelodonts were more closely related to hippos and whales than to pigs.",
    "funFacts": [
      "Discovered 1873",
      "'Terminator pig' — size of a bison",
      "More closely related to whales and hippos than pigs",
      "Jaws could crush bone and turtle shells"
    ],
    "foundIn": [
      "South Dakota, USA",
      "Nebraska, USA",
      "Mongolia"
    ],
    "color": "#FF706050"
  },
  {
    "id": "arsinoitherium",
    "name": "Arsinoitherium",
    "era": "PALEOGENE",
    "period": "Oligocene",
    "age": "~30 mya",
    "diet": "HERBIVORE",
    "length": "11.5 ft",
    "weight": "3 tons",
    "habitat": "Forests of North Africa",
    "description": "A large herbivore with two enormous side-by-side horns on its snout and smaller horns behind. Despite its rhinoceros-like appearance, Arsinoitherium was related to elephants and hyraxes. It lived in lush Oligocene forests of Egypt.",
    "funFacts": [
      "Discovered 1902",
      "Two massive side-by-side horns on its snout",
      "Related to elephants, not rhinos",
      "Lived in ancient Egyptian forests",
      "Named after Queen Arsinoe of Egypt"
    ],
    "foundIn": [
      "Fayum, Egypt"
    ],
    "color": "#FF8A7A6A"
  },
  {
    "id": "titanoboa",
    "name": "Titanoboa",
    "era": "PALEOGENE",
    "period": "Paleocene",
    "age": "~60–58 mya",
    "diet": "CARNIVORE",
    "length": "42–50 ft",
    "weight": "2,500 lb",
    "habitat": "Tropical rivers of South America",
    "description": "The largest snake ever — longer than a school bus. Titanoboa lived just after the dinosaur extinction when tropical temperatures were higher, allowing cold-blooded animals to grow enormous. It hunted crocodiles and large fish.",
    "funFacts": [
      "Discovered 2009",
      "Largest snake ever — 42 to 50 ft long",
      "Hunted crocodiles and large fish",
      "Lived just after the dinosaur extinction",
      "Warmer climate allowed its enormous size"
    ],
    "foundIn": [
      "Cerrej\\u00f3n, Colombia"
    ],
    "color": "#FF4A6A4A"
  },
  {
    "id": "phorusrhacos",
    "name": "Phorusrhacos",
    "era": "NEOGENE",
    "period": "Miocene",
    "age": "~20–13 mya",
    "diet": "CARNIVORE",
    "length": "8 ft",
    "weight": "300 lb",
    "habitat": "Plains of South America",
    "description": "The 'terror bird' — a flightless apex predator with a massive hooked beak that could crush bone. Phorusrhacos dominated South American grasslands for millions of years after the dinosaurs went extinct. It could run down prey at high speed.",
    "funFacts": [
      "Discovered 1887 by Florentino Ameghino",
      "8-ft flightless apex predator with a bone-crushing beak",
      "Dominated South America after the dinosaurs",
      "Could run down prey at 30+ mph",
      "Related to modern seriemas"
    ],
    "foundIn": [
      "Santa Cruz Formation, Patagonia, Argentina"
    ],
    "color": "#FFB06050"
  },
  {
    "id": "kelenken",
    "name": "Kelenken",
    "era": "NEOGENE",
    "period": "Miocene",
    "age": "~15 mya",
    "diet": "CARNIVORE",
    "length": "10 ft",
    "weight": "350 lb",
    "habitat": "Plains of South America",
    "description": "The largest terror bird — with a skull 28 inches long, the largest bird skull known. Kelenken was a towering flightless predator that dominated Miocene Patagonia. Its enormous beak could deliver devastating pecks.",
    "funFacts": [
      "Discovered 2006",
      "Largest terror bird — 28-inch skull",
      "Largest bird skull ever found",
      "Towering 10-ft flightless predator",
      "Name comes from a Tehuelche mythological beast"
    ],
    "foundIn": [
      "Comallo, R\\u00edo Negro, Argentina"
    ],
    "color": "#FFA05040"
  },
  {
    "id": "argentavis",
    "name": "Argentavis",
    "era": "NEOGENE",
    "period": "Late Miocene",
    "age": "~6 mya",
    "diet": "SCAVENGER",
    "length": "24 ft wingspan",
    "weight": "160 lb",
    "habitat": "Skies of South America",
    "description": "The largest flying bird ever — with a 24-foot wingspan comparable to a small plane. Argentavis soared on thermal updrafts over Miocene Argentina, gliding for hundreds of miles without flapping. It may have been a scavenger like a condor.",
    "funFacts": [
      "Discovered 1980",
      "Largest flying bird ever — 24-ft wingspan",
      "Soared on thermals — rarely needed to flap",
      "Weighed about 160 lbs",
      "Comparable to a Cessna airplane"
    ],
    "foundIn": [
      "Argentina"
    ],
    "color": "#FFA06050"
  },
  {
    "id": "deinotherium",
    "name": "Deinotherium",
    "era": "NEOGENE",
    "period": "Miocene–Pleistocene",
    "age": "~20–1 mya",
    "diet": "HERBIVORE",
    "length": "13–16 ft",
    "weight": "8–13 tons",
    "habitat": "Savannas of Africa, Europe & Asia",
    "description": "A massive prehistoric elephant with downward-curving tusks on its lower jaw — unlike any modern elephant. Deinotherium used its strange tusks to dig up roots and strip bark. It survived until about 1 million years ago.",
    "funFacts": [
      "Discovered 1829",
      "Tusks curved DOWN from the lower jaw — unique among elephants",
      "Used tusks to dig roots and strip bark",
      "Survived until ~1 million years ago",
      "One of the largest proboscideans ever"
    ],
    "foundIn": [
      "Kenya",
      "Romania",
      "Germany",
      "India"
    ],
    "color": "#FF6B5040"
  },
  {
    "id": "chalicotherium",
    "name": "Chalicotherium",
    "era": "NEOGENE",
    "period": "Miocene",
    "age": "~20–13 mya",
    "diet": "HERBIVORE",
    "length": "8.5 ft",
    "weight": "1,300 lb",
    "habitat": "Forests of Europe & Asia",
    "description": "A bizarre mammal with the body of a horse, the head of a tapir, and the claws of a sloth. Chalicotherium had large hooked claws instead of hooves and walked on its knuckles like a gorilla. It used its claws to pull down branches.",
    "funFacts": [
      "Discovered 1893",
      "Horse body, tapir head, sloth claws — a chimera",
      "Walked on its knuckles like a gorilla",
      "Used claws to pull down branches",
      "Claws instead of hooves — unique among perissodactyls"
    ],
    "foundIn": [
      "Germany",
      "France",
      "China"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "megatherium",
    "name": "Megatherium",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~5 million–10,000 years",
    "diet": "HERBIVORE",
    "length": "20 ft",
    "weight": "4 tons",
    "habitat": "Forests of South America",
    "description": "The giant ground sloth — one of the largest land mammals ever. Megatherium stood 20 feet tall on its hind legs and could pull down entire trees. It had enormous claws and may have been partly bipedal. It survived until humans arrived in the Americas.",
    "funFacts": [
      "Discovered 1789",
      "Stood 20 ft tall on hind legs — larger than an elephant",
      "Could pull down entire trees with its claws",
      "Survived until humans arrived in the Americas",
      "One of the largest land mammals ever"
    ],
    "foundIn": [
      "Argentina",
      "Brazil",
      "Uruguay",
      "Peru"
    ],
    "color": "#FF8B7A5A"
  },
  {
    "id": "megalania",
    "name": "Megalania",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2.5 million–50,000 years",
    "diet": "CARNIVORE",
    "length": "20 ft",
    "weight": "1,000–2,000 lb",
    "habitat": "Plains of Australia",
    "description": "The largest terrestrial lizard ever — a giant monitor lizard related to the Komodo dragon. Megalania was the apex predator of Pleistocene Australia, hunting giant kangaroos and diprotodons. It likely had a venomous bite like modern monitors.",
    "funFacts": [
      "Discovered 1859",
      "Largest land lizard ever — 20 ft long",
      "Apex predator of Pleistocene Australia",
      "Likely venomous like its Komodo dragon relatives",
      "Coexisted with early Aboriginal Australians"
    ],
    "foundIn": [
      "Queensland, Australia",
      "New South Wales, Australia"
    ],
    "color": "#FF5A4030"
  },
  {
    "id": "pelagornis",
    "name": "Pelagornis",
    "era": "NEOGENE",
    "period": "Miocene–Pliocene",
    "age": "~25–2.5 mya",
    "diet": "PISCIVORE",
    "length": "24 ft wingspan",
    "weight": "80 lb",
    "habitat": "Seas worldwide",
    "description": "A giant seabird with bony 'teeth' — one of the largest flying birds of all time. Pelagornis had a 24-foot wingspan and soared over Miocene oceans for millions of years, hunting fish and squid. It went extinct during the Pliocene.",
    "funFacts": [
      "Discovered 1857",
      "One of the largest flying birds ever",
      "Had bony 'teeth' — not true teeth",
      "24-foot wingspan — soared over ancient oceans",
      "Hunted fish and squid across the globe"
    ],
    "foundIn": [
      "South Carolina, USA",
      "England",
      "Australia",
      "Morocco"
    ],
    "color": "#FF8A8A7A"
  },
  {
    "id": "terror-bird-titanis",
    "name": "Titanis",
    "era": "QUATERNARY",
    "period": "Pliocene–Pleistocene",
    "age": "~5 million–11,000 years",
    "diet": "CARNIVORE",
    "length": "8 ft",
    "weight": "330 lb",
    "habitat": "Plains of North America",
    "description": "A North American terror bird — one of the few top predators from South America that migrated north after the Panama land bridge formed. Titanis had a massive hooked beak and could run at high speed. It went extinct about 11,000 years ago.",
    "funFacts": [
      "Discovered 1963",
      "Only terror bird known from North America",
      "Migrated north after Panama land bridge formed",
      "Massive hooked beak could crush bone",
      "Went extinct ~11,000 years ago"
    ],
    "foundIn": [
      "Florida, USA",
      "Texas, USA"
    ],
    "color": "#FFA06050"
  },
  {
    "id": "uintatherium",
    "name": "Uintatherium",
    "era": "PALEOGENE",
    "period": "Eocene",
    "age": "~45–37 mya",
    "diet": "HERBIVORE",
    "length": "13 ft",
    "weight": "2 tons",
    "habitat": "Forests of North America",
    "description": "A bizarre early mammal with six bony knobs on its skull and large saber-like upper canines. Uintatherium was one of the first large mammals after the dinosaur extinction. It had a small brain despite its massive body.",
    "funFacts": [
      "Discovered 1872",
      "Six bony knobs on its skull and saber-like canines",
      "One of the first large mammals after dinosaurs",
      "Had a remarkably small brain",
      "Named after the Uinta Mountains of Utah"
    ],
    "foundIn": [
      "Wyoming, USA",
      "Utah, USA",
      "China"
    ],
    "color": "#FF7A7A6A"
  },
  {
    "id": "amphicyon",
    "name": "Amphicyon",
    "era": "NEOGENE",
    "period": "Miocene",
    "age": "~15–9 mya",
    "diet": "CARNIVORE",
    "length": "8 ft",
    "weight": "600 lb",
    "habitat": "Forests of Europe, Asia & North America",
    "description": "The 'bear dog' — neither a bear nor a dog, but a member of an extinct family that combined traits of both. Amphicyon was a powerful predator that hunted like a bear, using strength rather than speed. It was an apex predator of the Miocene.",
    "funFacts": [
      "Discovered 1836",
      "'Bear dog' — neither a bear nor a dog",
      "Powerful predator that hunted like a bear",
      "Apex predator of the Miocene",
      "Went extinct when true carnivores diversified"
    ],
    "foundIn": [
      "France",
      "Germany",
      "Nebraska, USA"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "merychippus",
    "name": "Merychippus",
    "era": "NEOGENE",
    "period": "Miocene",
    "age": "~17–11 mya",
    "diet": "HERBIVORE",
    "length": "5 ft",
    "weight": "220 lb",
    "habitat": "Plains of North America",
    "description": "A critical horse ancestor — the first horse to have a single hoof per foot while still retaining side toes. Merychippus had high-crowned teeth for grazing grass, which was spreading across North America during the Miocene.",
    "funFacts": [
      "Discovered 1857",
      "First horse with a single hoof per foot",
      "High-crowned teeth for grazing grass",
      "Had side toes that no longer touched the ground",
      "Critical link in horse evolution"
    ],
    "foundIn": [
      "Nebraska, USA",
      "Florida, USA",
      "Texas, USA"
    ],
    "color": "#FF9A8A6A"
  },
  {
    "id": "synthetoceras",
    "name": "Synthetoceras",
    "era": "NEOGENE",
    "period": "Miocene",
    "age": "~10–5 mya",
    "diet": "HERBIVORE",
    "length": "8 ft",
    "weight": "600 lb",
    "habitat": "Plains of North America",
    "description": "A bizarre protoceratid with a Y-shaped horn on its snout and a pair of horns behind its eyes. Synthetoceras was a distant relative of camels and deer, but with a truly strange head. Its forked nasal horn may have been used for display or fighting.",
    "funFacts": [
      "Discovered 1935",
      "Y-shaped horn on its snout — truly bizarre",
      "Distant relative of camels and deer",
      "Forked nasal horn for display or fighting",
      "No living relatives — a uniquely American lineage"
    ],
    "foundIn": [
      "Texas, USA",
      "Nebraska, USA",
      "Florida, USA"
    ],
    "color": "#FFA0805A"
  },
  {
    "id": "woolly-mammoth",
    "name": "Woolly Mammoth",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~400,000–4,000 years",
    "diet": "HERBIVORE",
    "length": "13 ft",
    "weight": "6 tons",
    "habitat": "Tundra of the Northern Hemisphere",
    "description": "The iconic Ice Age giant — covered in thick fur with a fat hump for energy storage and 15-foot curved tusks. Frozen carcasses in Siberian permafrost preserve hair, skin, and even stomach contents. The last mammoths survived on Wrangel Island until just 4,000 years ago.",
    "funFacts": [
      "Discovered 1799",
      "Frozen carcasses with hair, skin, and stomach contents preserved",
      "Last mammoths lived on Wrangel Island just 4,000 years ago",
      "Tusks up to 15 feet long",
      "Mammoth ivory is still found in Siberia today"
    ],
    "foundIn": [
      "Siberia, Russia",
      "Alaska, USA",
      "La Brea Tar Pits, California, USA"
    ],
    "color": "#FFC9A87C"
  },
  {
    "id": "columbian-mammoth",
    "name": "Columbian Mammoth",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~1 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "13–14 ft",
    "weight": "10 tons",
    "habitat": "Plains of North America",
    "description": "Larger and less hairy than the woolly mammoth, the Columbian mammoth roamed the warmer regions of North America. It stood 14 feet tall and had tusks up to 16 feet long. It coexisted with early humans who hunted it.",
    "funFacts": [
      "Discovered 1857",
      "Larger than the woolly mammoth — 14 ft tall",
      "Tusks up to 16 feet long",
      "Roamed warmer North American plains",
      "Hunted by early humans"
    ],
    "foundIn": [
      "Rancho La Brea, California, USA",
      "Texas, USA",
      "Mexico"
    ],
    "color": "#FFB8906C"
  },
  {
    "id": "pygmy-mammoth",
    "name": "Pygmy Mammoth",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~100,000–11,000 years",
    "diet": "HERBIVORE",
    "length": "5.5–7 ft",
    "weight": "1,600–2,000 lb",
    "habitat": "Channel Islands, California",
    "description": "A dwarf mammoth that evolved on the Channel Islands of California through insular dwarfism. The pygmy mammoth was less than half the height of its mainland ancestor — a dramatic example of island evolution shrinking large animals.",
    "funFacts": [
      "Discovered 1856",
      "Dwarf mammoth — only 5.5 ft tall at the shoulder",
      "Evolved through insular dwarfism on islands",
      "Less than half the size of its ancestor",
      "Found on California's Channel Islands"
    ],
    "foundIn": [
      "Santa Rosa Island, California, USA"
    ],
    "color": "#FFB8A06C"
  },
  {
    "id": "smilodon",
    "name": "Smilodon",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2.5 million–10,000 years",
    "diet": "CARNIVORE",
    "length": "6.5 ft",
    "weight": "600–900 lb",
    "habitat": "Plains and forests of the Americas",
    "description": "The iconic saber-toothed cat with 7-inch canine teeth used for a precise killing bite to the throat. Thousands of Smilodon skeletons from La Brea Tar Pits make it one of the best-known Ice Age predators. It was NOT closely related to modern tigers.",
    "funFacts": [
      "Discovered 1842",
      "7-inch saber teeth for a precise throat bite",
      "Over 2,000 skeletons from La Brea Tar Pits",
      "California's state fossil",
      "NOT closely related to modern tigers"
    ],
    "foundIn": [
      "La Brea Tar Pits, Los Angeles, USA",
      "Florida, USA",
      "South America"
    ],
    "color": "#FFC97050"
  },
  {
    "id": "smilodon-populator",
    "name": "Smilodon populator",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~1 million–10,000 years",
    "diet": "CARNIVORE",
    "length": "8.2 ft",
    "weight": "700–1,000 lb",
    "habitat": "Plains of South America",
    "description": "The largest saber-toothed cat — larger and more powerfully built than the North American Smilodon fatalis. Smilodon populator was the largest cat ever and the apex predator of Ice Age South America, hunting giant ground sloths and toxodonts.",
    "funFacts": [
      "Discovered 1842",
      "Largest saber-toothed cat — up to 1,000 lbs",
      "Largest cat to ever exist",
      "Apex predator of Ice Age South America",
      "Hunted giant ground sloths"
    ],
    "foundIn": [
      "Argentina",
      "Brazil",
      "Uruguay"
    ],
    "color": "#FFC96040"
  },
  {
    "id": "dire-wolf",
    "name": "Dire Wolf",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~250,000–9,500 years",
    "diet": "CARNIVORE",
    "length": "5 ft",
    "weight": "130–150 lb",
    "habitat": "Plains of North America",
    "description": "Larger and more heavily built than a modern gray wolf, the dire wolf had the strongest bite of any canid. Over 4,000 dire wolf skulls have been recovered from La Brea Tar Pits. It went extinct about 9,500 years ago, outlasted by the smaller gray wolf.",
    "funFacts": [
      "Discovered 1858",
      "Larger than a modern gray wolf with a stronger bite",
      "Over 4,000 skulls from La Brea Tar Pits",
      "Hunted in packs like modern wolves",
      "Went extinct ~9,500 years ago"
    ],
    "foundIn": [
      "La Brea Tar Pits, California, USA",
      "Florida, USA",
      "Indiana, USA"
    ],
    "color": "#FF8B7A60"
  },
  {
    "id": "short-faced-bear",
    "name": "Giant Short-Faced Bear",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~800,000–11,000 years",
    "diet": "CARNIVORE",
    "length": "12 ft standing",
    "weight": "1,800–2,000 lb",
    "habitat": "Plains of North America",
    "description": "The largest bear ever — standing 12 feet tall on its hind legs. The giant short-faced bear was a fast runner that could reach 40 mph, making it the most terrifying predator of Ice Age North America. It may have stolen kills from other predators including dire wolves and Smilodon.",
    "funFacts": [
      "Discovered 1858",
      "Largest bear ever — 12 ft standing on hind legs",
      "Could run 40 mph — faster than a grizzly",
      "Stole kills from other Ice Age predators",
      "Most terrifying predator of Ice Age North America"
    ],
    "foundIn": [
      "California, USA",
      "Indiana, USA",
      "Alaska, USA"
    ],
    "color": "#FF7A6A4A"
  },
  {
    "id": "woolly-rhino",
    "name": "Woolly Rhinoceros",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~350,000–10,000 years",
    "diet": "HERBIVORE",
    "length": "11 ft",
    "weight": "3,000–5,000 lb",
    "habitat": "Tundra of Eurasia",
    "description": "A heavily-built Ice Age rhinoceros with thick fur, a fatty hump, and a massive flattened front horn used to sweep away snow while grazing. Cave paintings by Ice Age humans show it with a dark band around its midsection.",
    "funFacts": [
      "Discovered 1769",
      "Massive front horn used to sweep away snow",
      "Cave paintings show a dark band around its body",
      "Thick fur and a fatty hump for cold",
      "Coexisted with early humans who hunted it"
    ],
    "foundIn": [
      "Siberia, Russia",
      "Ukraine",
      "England",
      "France"
    ],
    "color": "#FFA08060"
  },
  {
    "id": "elasmotherium",
    "name": "Elasmotherium",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2.5 million–29,000 years",
    "diet": "HERBIVORE",
    "length": "20 ft",
    "weight": "4.5 tons",
    "habitat": "Steppes of Eurasia",
    "description": "The 'Siberian unicorn' — a giant rhinoceros with a single 6-foot horn on its forehead. Elasmotherium was the largest rhinoceros ever, with legs like a horse for running across steppes. It may have inspired the unicorn legend and survived until at least 29,000 years ago.",
    "funFacts": [
      "Discovered 1808",
      "'Siberian unicorn' — 6-foot horn on its forehead",
      "Largest rhinoceros ever — 20 ft long",
      "May have inspired the unicorn legend",
      "Survived until ~29,000 years ago"
    ],
    "foundIn": [
      "Russia",
      "Kazakhstan",
      "China"
    ],
    "color": "#FF9A7A60"
  },
  {
    "id": "cave-lion",
    "name": "Cave Lion",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~370,000–14,000 years",
    "diet": "CARNIVORE",
    "length": "11.5 ft",
    "weight": "880–1,100 lb",
    "habitat": "Tundra of Eurasia",
    "description": "Larger than a modern African lion, the cave lion was one of the largest cats ever. Cave paintings show it had no mane. Frozen cave lion cubs found in Siberian permafrost are so perfectly preserved that their fur, whiskers, and even paw pads are visible.",
    "funFacts": [
      "Discovered 1810",
      "Larger than a modern African lion",
      "Frozen cubs preserve fur, whiskers, and paw pads",
      "Cave paintings show it had no mane",
      "Hunted mammoths, bison, and cave bears"
    ],
    "foundIn": [
      "Siberia, Russia",
      "France",
      "Germany"
    ],
    "color": "#FFB8905A"
  },
  {
    "id": "cave-bear",
    "name": "Cave Bear",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~575,000–27,500 years",
    "diet": "HERBIVORE",
    "length": "11.5 ft standing",
    "weight": "1,000–2,000 lb",
    "habitat": "Caves of Europe",
    "description": "Larger than the largest modern brown bear, the cave bear spent winters hibernating in European caves. Tens of thousands of cave bear skeletons have been found in caves. Early humans competed with cave bears for shelter and may have contributed to their extinction.",
    "funFacts": [
      "Discovered 1774",
      "Larger than the biggest modern brown bear",
      "Tens of thousands of skeletons found in caves",
      "Competed with early humans for cave shelter",
      "Hibernated through Ice Age winters"
    ],
    "foundIn": [
      "Austria",
      "Germany",
      "Romania",
      "Russia"
    ],
    "color": "#FF7A6A5A"
  },
  {
    "id": "american-lion",
    "name": "American Lion",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~340,000–11,000 years",
    "diet": "CARNIVORE",
    "length": "8 ft",
    "weight": "900 lb",
    "habitat": "Plains of North America",
    "description": "One of the largest cats ever — 25% larger than a modern African lion. The American lion ranged from Alaska to Peru. La Brea Tar Pits preserve many specimens. It may have been the largest cat to ever exist, though some give that title to Smilodon.",
    "funFacts": [
      "Discovered 1853",
      "25% larger than a modern African lion",
      "Ranged from Alaska to Peru",
      "One of the largest cats ever",
      "Hunted bison, horses, and possibly young mammoths"
    ],
    "foundIn": [
      "La Brea Tar Pits, California, USA",
      "Alaska, USA",
      "Peru"
    ],
    "color": "#FFC9A05A"
  },
  {
    "id": "american-cheetah",
    "name": "American Cheetah",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2.6 million–11,000 years",
    "diet": "CARNIVORE",
    "length": "5 ft",
    "weight": "150 lb",
    "habitat": "Plains of North America",
    "description": "A long-legged cat that evolved cheetah-like body proportions independently. The American cheetah may explain why pronghorns can run 60 mph — they evolved to outrun this extinct predator. It went extinct at the end of the Ice Age.",
    "funFacts": [
      "Discovered 1894",
      "Evolved cheetah-like speed independently",
      "Pronghorns run 60 mph to escape this extinct predator",
      "Convergent evolution with modern cheetahs",
      "Hunted pronghorns on American plains"
    ],
    "foundIn": [
      "Wyoming, USA",
      "Nevada, USA",
      "Texas, USA"
    ],
    "color": "#FFB8A05A"
  },
  {
    "id": "homotherium",
    "name": "Homotherium (Scimitar Cat)",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~3 million–10,000 years",
    "diet": "CARNIVORE",
    "length": "6.5 ft",
    "weight": "400–500 lb",
    "habitat": "Plains of North America & Eurasia",
    "description": "A saber-toothed cat with shorter, serrated scimitar-like canine teeth. Homotherium was a fast runner that hunted in packs, unlike the ambush-hunting Smilodon. It had longer legs than modern cats and may have chased down young mammoths.",
    "funFacts": [
      "Discovered 1894",
      "Scimitar-shaped serrated canine teeth",
      "Fast runner that hunted in packs",
      "Longer legs than modern cats",
      "May have hunted young mammoths"
    ],
    "foundIn": [
      "Texas, USA",
      "Alaska, USA",
      "England",
      "Netherlands"
    ],
    "color": "#FF9A7A5A"
  },
  {
    "id": "giant-beaver",
    "name": "Giant Beaver",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~1.4 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "8 ft",
    "weight": "450–575 lb",
    "habitat": "Wetlands of North America",
    "description": "The largest beaver ever — the size of a black bear, with 6-inch incisors. Giant beaver built enormous lodges and dams. Unlike modern beavers, it may not have felled trees — its teeth were better suited for aquatic plants.",
    "funFacts": [
      "Discovered 1846",
      "Largest beaver ever — size of a black bear",
      "6-inch incisor teeth",
      "Built enormous lodges and dams",
      "May have eaten aquatic plants rather than wood"
    ],
    "foundIn": [
      "Florida, USA",
      "New York, USA",
      "Minnesota, USA"
    ],
    "color": "#FF8B7A4A"
  },
  {
    "id": "glyptodont",
    "name": "Glyptodon",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2.5 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "11 ft",
    "weight": "2 tons",
    "habitat": "Plains of South America",
    "description": "A giant armadillo the size of a Volkswagen Beetle, covered in a domed shell of fused bony plates. Glyptodon had a spiked tail club like an ankylosaur. It could not roll into a ball like modern armadillos — its armor was too rigid.",
    "funFacts": [
      "Discovered 1839",
      "Giant armadillo the size of a VW Beetle",
      "Shell of fused bony plates — like a turtle",
      "Spiked tail club like an ankylosaur",
      "Hunted by early humans"
    ],
    "foundIn": [
      "Argentina",
      "Brazil",
      "Uruguay"
    ],
    "color": "#FF8B8A5A"
  },
  {
    "id": "doedicurus",
    "name": "Doedicurus",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "13 ft",
    "weight": "2 tons",
    "habitat": "Plains of South America",
    "description": "A glyptodont with a massive spiked tail club that could shatter bone. Doedicurus had the most extreme tail weapon of any mammal — a spiked club on a flexible tail that it could swing like a mace against predators or rivals.",
    "funFacts": [
      "Discovered 1867",
      "Massive spiked tail club — could shatter bone",
      "Most extreme tail weapon of any mammal",
      "Shell up to 2 inches thick",
      "Swung its tail like a mace"
    ],
    "foundIn": [
      "Argentina",
      "Uruguay",
      "Brazil"
    ],
    "color": "#FF7A7A4A"
  },
  {
    "id": "diprotodon",
    "name": "Diprotodon",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~1.6 million–46,000 years",
    "diet": "HERBIVORE",
    "length": "10 ft",
    "weight": "2.5–3 tons",
    "habitat": "Forests and plains of Australia",
    "description": "The largest marsupial ever — a wombat relative the size of a rhinoceros. Diprotodon weighed up to 3 tons and was the largest mammal in Ice Age Australia. Aboriginal Australians coexisted with diprotodons and may have hunted them.",
    "funFacts": [
      "Discovered 1863",
      "Largest marsupial ever — size of a rhinoceros",
      "Weighed up to 3 tons",
      "Coexisted with Aboriginal Australians",
      "A giant wombat relative"
    ],
    "foundIn": [
      "Queensland, Australia",
      "New South Wales, Australia",
      "South Australia"
    ],
    "color": "#FF9A8A6A"
  },
  {
    "id": "thylacoleo",
    "name": "Thylacoleo (Marsupial Lion)",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2 million–46,000 years",
    "diet": "CARNIVORE",
    "length": "5 ft",
    "weight": "220–280 lb",
    "habitat": "Forests of Australia",
    "description": "The 'marsupial lion' — the most specialized mammalian carnivore ever, with the strongest bite for its size of any mammal. Thylacoleo had blade-like premolars and a thumb claw for disemboweling prey. Despite its name, it was not closely related to true lions.",
    "funFacts": [
      "Discovered 1859",
      "Strongest bite for its size of any mammal",
      "Blade-like teeth and a disemboweling thumb claw",
      "Not related to true lions — a marsupial",
      "Most specialized mammalian carnivore ever"
    ],
    "foundIn": [
      "Queensland, Australia",
      "Victoria, Australia"
    ],
    "color": "#FF8A6A5A"
  },
  {
    "id": "megaloceros",
    "name": "Megaloceros (Irish Elk)",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~400,000–8,000 years",
    "diet": "HERBIVORE",
    "length": "7 ft",
    "weight": "1,200–1,500 lb",
    "habitat": "Forests and plains of Eurasia",
    "description": "The largest deer ever, with antlers spanning up to 12 feet from tip to tip and weighing up to 90 pounds. Despite the name 'Irish elk,' it was a giant fallow deer that ranged across all of Eurasia. Bogs in Ireland preserve spectacular complete skeletons.",
    "funFacts": [
      "Discovered 1847",
      "Largest deer ever — 12-ft antler span",
      "Antlers weighed up to 90 lbs",
      "Despite the name, it was a fallow deer",
      "Shed and regrew its enormous antlers annually"
    ],
    "foundIn": [
      "Bogs of Ireland",
      "Germany",
      "Russia",
      "China"
    ],
    "color": "#FFB8906A"
  },
  {
    "id": "mastodon",
    "name": "Mastodon",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~5 million–10,000 years",
    "diet": "HERBIVORE",
    "length": "9–10 ft",
    "weight": "8–10 tons",
    "habitat": "Forests of North America & Central America",
    "description": "Distantly related to mammoths, mastodons had different teeth — cone-shaped for crushing twigs, not grazing grass. They browsed in forests rather than open tundra. Mastodons were hunted by early humans and went extinct about 10,000 years ago.",
    "funFacts": [
      "Discovered 1739",
      "Different teeth from mammoths — for crushing twigs",
      "Browsed in forests, not open tundra",
      "Hunted by early humans",
      "Went extinct ~10,000 years ago"
    ],
    "foundIn": [
      "New York, USA",
      "Michigan, USA",
      "Florida, USA",
      "Mexico"
    ],
    "color": "#FFA8805A"
  },
  {
    "id": "steppe-bison",
    "name": "Steppe Bison",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~575,000–8,000 years",
    "diet": "HERBIVORE",
    "length": "8 ft",
    "weight": "2,000 lb",
    "habitat": "Tundra of Eurasia & North America",
    "description": "The giant bison of the Ice Age, with horns spanning 6 feet from tip to tip. Steppe bison are preserved in cave paintings at Lascaux and other sites. A nearly complete mummy named 'Blue Babe' was found frozen in Alaska with intact fur and skin.",
    "funFacts": [
      "Discovered 1827",
      "Horns spanning 6 feet tip to tip",
      "'Blue Babe' — frozen mummy with intact fur and skin",
      "Depicted in Lascaux cave paintings",
      "Larger than modern bison"
    ],
    "foundIn": [
      "Alaska, USA",
      "France",
      "Russia",
      "Canada"
    ],
    "color": "#FF9A7A4A"
  },
  {
    "id": "bison-antiquus",
    "name": "Bison antiquus",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~250,000–10,000 years",
    "diet": "HERBIVORE",
    "length": "7.5 ft",
    "weight": "3,300 lb",
    "habitat": "Plains of North America",
    "description": "A large extinct bison — the direct ancestor of the modern American bison. Bison antiquus was about 15-20% larger than modern bison with longer horns. It was hunted by early Native Americans and is depicted in cave paintings.",
    "funFacts": [
      "Discovered 1852",
      "Direct ancestor of the modern American bison",
      "15-20% larger than modern bison",
      "Hunted by early Native Americans",
      "Depicted in cave paintings"
    ],
    "foundIn": [
      "California, USA",
      "Texas, USA",
      "Alaska, USA"
    ],
    "color": "#FF8A7A4A"
  },
  {
    "id": "camelops",
    "name": "Camelops",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~3.5 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "7 ft",
    "weight": "1,800 lb",
    "habitat": "Plains of North America",
    "description": "The 'yesterday's camel' — a large North American camel that went extinct about 11,000 years ago. Camelops was larger than a modern dromedary and roamed from Alaska to Mexico. All camels evolved in North America before migrating to Asia and South America.",
    "funFacts": [
      "Discovered 1854",
      "'Yesterday's camel' — large Ice Age North American camel",
      "All camels evolved in North America",
      "Larger than a modern dromedary",
      "Went extinct ~11,000 years ago"
    ],
    "foundIn": [
      "Texas, USA",
      "California, USA",
      "Nebraska, USA"
    ],
    "color": "#FFA8906A"
  },
  {
    "id": "hagerman-horse",
    "name": "Hagerman Horse",
    "era": "NEOGENE",
    "period": "Pliocene",
    "age": "~3.5 million years",
    "diet": "HERBIVORE",
    "length": "5 ft",
    "weight": "1,000 lb",
    "habitat": "Plains of North America",
    "description": "An early horse with a single functional toe — the first truly modern-looking horse. The Hagerman horse is Idaho's state fossil. Hundreds of complete skeletons have been found at Hagerman Fossil Beds, representing an entire herd.",
    "funFacts": [
      "Discovered 1928",
      "First truly modern-looking horse",
      "Idaho's state fossil",
      "Hundreds of skeletons found together — a whole herd",
      "Single-toed like modern horses"
    ],
    "foundIn": [
      "Hagerman Fossil Beds, Idaho, USA"
    ],
    "color": "#FF9A8A6A"
  },
  {
    "id": "giant-horse",
    "name": "Equus giganteus",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~2.5 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "6.5 ft",
    "weight": "1,500 lb",
    "habitat": "Plains of North America",
    "description": "One of the largest horses ever — larger than a modern draft horse. Equus giganteus roamed Ice Age North America alongside mammoths and bison. All horses in the Americas went extinct about 11,000 years ago, only returning with European explorers.",
    "funFacts": [
      "Discovered 1858",
      "One of the largest horses ever",
      "All American horses went extinct ~11,000 years ago",
      "Larger than a modern draft horse",
      "Horses only returned to the Americas with Europeans"
    ],
    "foundIn": [
      "Texas, USA",
      "Nevada, USA",
      "Idaho, USA"
    ],
    "color": "#FFB8906A"
  },
  {
    "id": "toxodon",
    "name": "Toxodon",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~11 million–11,000 years",
    "diet": "HERBIVORE",
    "length": "9 ft",
    "weight": "3 tons",
    "habitat": "Plains of South America",
    "description": "A large hippo-like mammal with a bizarre skull — its nostrils faced upward, suggesting it may have been semi-aquatic. Toxodon was one of the most common large mammals of Ice Age South America. Darwin himself collected its fossils.",
    "funFacts": [
      "Discovered 1837 by Charles Darwin",
      "Charles Darwin collected its fossils",
      "Hippo-like with upward-facing nostrils",
      "One of the most common South American Ice Age mammals",
      "Classified by Richard Owen from Darwin's specimens"
    ],
    "foundIn": [
      "Argentina",
      "Brazil",
      "Uruguay",
      "Bolivia"
    ],
    "color": "#FF8A7A5A"
  },
  {
    "id": "macrauchenia",
    "name": "Macrauchenia",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~7 million–10,000 years",
    "diet": "HERBIVORE",
    "length": "10 ft",
    "weight": "2,000 lb",
    "habitat": "Plains of South America",
    "description": "A strange South American mammal with a camel-like body, elephant-like legs, and a trunk like a tapir. Macrauchenia was a litoptern — a group of unique South American hoofed mammals with no living relatives. It went extinct about 10,000 years ago.",
    "funFacts": [
      "Discovered 1838 by Charles Darwin",
      "Camel body, elephant legs, and a tapir-like trunk",
      "No living relatives — a uniquely South American lineage",
      "Charles Darwin collected its fossils",
      "Could change direction quickly while running"
    ],
    "foundIn": [
      "Argentina",
      "Bolivia",
      "Peru"
    ],
    "color": "#FFA8986A"
  },
  {
    "id": "eremotherium",
    "name": "Eremotherium",
    "era": "QUATERNARY",
    "period": "Pleistocene",
    "age": "~5 million–10,000 years",
    "diet": "HERBIVORE",
    "length": "20 ft",
    "weight": "5 tons",
    "habitat": "Forests of North & South America",
    "description": "A giant ground sloth that ranged from the southern US to Brazil. Eremotherium was nearly as large as Megatherium and could rear up on its hind legs to browse 20 feet high. It went extinct about 10,000 years ago, likely due to human hunting.",
    "funFacts": [
      "Discovered 1850",
      "Giant sloth ranging from the US to Brazil",
      "Could rear up to browse 20 ft high",
      "Nearly as large as Megatherium",
      "Went extinct ~10,000 years ago"
    ],
    "foundIn": [
      "Florida, USA",
      "Brazil",
      "Mexico"
    ],
    "color": "#FF8B8A6A"
  },
  {
    "id": "dodo",
    "name": "Dodo",
    "era": "QUATERNARY",
    "period": "Holocene",
    "age": "~1681 AD (extinct)",
    "diet": "HERBIVORE",
    "length": "3.3 ft",
    "weight": "23–39 lb",
    "habitat": "Mauritius",
    "description": "The flightless pigeon of Mauritius — the icon of human-caused extinction. The dodo went extinct just 83 years after its discovery by Europeans. Despite its fame, almost no complete skeletons existed until the 19th century. It was not clumsy — it was well-adapted before humans arrived.",
    "funFacts": [
      "Discovered 1598",
      "Extinct by 1681 — just 83 years after discovery",
      "Actually a giant flightless pigeon — not clumsy",
      "Almost no complete skeletons survive",
      "Icon of human-caused extinction"
    ],
    "foundIn": [
      "Mare aux Songes, Mauritius"
    ],
    "color": "#FFA08060"
  },
  {
    "id": "great-auk",
    "name": "Great Auk",
    "era": "QUATERNARY",
    "period": "Holocene",
    "age": "~1844 AD (extinct)",
    "diet": "PISCIVORE",
    "length": "33 in",
    "weight": "11 lb",
    "habitat": "North Atlantic coasts",
    "description": "The 'original penguin' — a large flightless seabird of the North Atlantic. The great auk was hunted to extinction for its feathers, oil, and eggs. The last confirmed pair was killed on Eldey Island in 1844, and their single egg was crushed underfoot.",
    "funFacts": [
      "Discovered 1813",
      "The 'original penguin' of the Northern Hemisphere",
      "Hunted to extinction by 1844",
      "Last pair killed and their egg crushed",
      "Gave the name 'penguin' to southern birds"
    ],
    "foundIn": [
      "Iceland",
      "Newfoundland, Canada",
      "Scotland"
    ],
    "color": "#FF7A7A8A"
  },
  {
    "id": "moa",
    "name": "Giant Moa",
    "era": "QUATERNARY",
    "period": "Holocene",
    "age": "~1445 AD (extinct)",
    "diet": "HERBIVORE",
    "length": "12 ft",
    "weight": "500 lb",
    "habitat": "New Zealand",
    "description": "The tallest bird ever, reaching 12 feet — with no wings at all, not even vestigial ones. Native to New Zealand, the giant moa was hunted to extinction by the M\\u0101ori by about 1445 AD. Complete skeletons with soft tissue have been found in caves.",
    "funFacts": [
      "Discovered 1839",
      "Tallest bird ever — 12 ft with no wings at all",
      "Hunted to extinction by M\\u0101ori by ~1445 AD",
      "Complete skeletons with soft tissue found in caves",
      "No wing bones whatsoever"
    ],
    "foundIn": [
      "South Island, New Zealand",
      "North Island, New Zealand"
    ],
    "color": "#FF8A7060"
  },
  {
    "id": "stellers-sea-cow",
    "name": "Steller's Sea Cow",
    "era": "QUATERNARY",
    "period": "Holocene",
    "age": "~1768 AD (extinct)",
    "diet": "HERBIVORE",
    "length": "30 ft",
    "weight": "10 tons",
    "habitat": "Commander Islands, Bering Sea",
    "description": "A giant sirenian related to dugongs that was discovered alive in 1741 and hunted to extinction by 1768 — just 27 years later. Steller's sea cow could not submerge and was easy prey for sailors. It is one of the most recently extinct large animals.",
    "funFacts": [
      "Discovered 1741",
      "Discovered alive in 1741 — extinct by 1768",
      "Could not submerge — floated at the surface",
      "Hunted to extinction in just 27 years",
      "One of the most recently extinct large animals"
    ],
    "foundIn": [
      "Commander Islands, Bering Sea"
    ],
    "color": "#FF8B8A7A"
  },
  {
    "id": "dimetrodon",
    "name": "Dimetrodon",
    "era": "OTHER",
    "period": "Early Permian",
    "age": "~295–272 mya",
    "diet": "CARNIVORE",
    "length": "15 ft",
    "weight": "550 lb",
    "habitat": "Swamps of North America & Europe",
    "description": "NOT a dinosaur — a synapsid (mammal ancestor) that lived 40 million years before the first dinosaur. Dimetrodon had a massive sail on its back and was the apex predator of the Permian. It is more closely related to you than to any dinosaur.",
    "funFacts": [
      "Discovered 1878",
      "NOT a dinosaur — a mammal ancestor",
      "More closely related to YOU than to any dinosaur",
      "Had a sail on its back for display or temperature regulation",
      "Apex predator 40 million years before dinosaurs",
      "Often mislabeled as a dinosaur in toy sets"
    ],
    "foundIn": [
      "Texas, USA",
      "Oklahoma, USA",
      "Germany"
    ],
    "color": "#FF5A7A6A"
  },
  {
    "id": "edaphosaurus",
    "name": "Edaphosaurus",
    "era": "OTHER",
    "period": "Late Carboniferous–Early Permian",
    "age": "~300–280 mya",
    "diet": "HERBIVORE",
    "length": "11 ft",
    "weight": "600 lb",
    "habitat": "Swamps of North America & Europe",
    "description": "A sail-backed synapsid like Dimetrodon, but a plant-eater. Edaphosaurus had a sail with cross-bars — different from Dimetrodon's smooth-spined sail. It had peg-like teeth for grinding plants and may have been the first large herbivore.",
    "funFacts": [
      "Discovered 1878",
      "A sail-backed plant-eater — first large herbivore",
      "Sail had cross-bars unlike Dimetrodon's smooth sail",
      "Peg-like teeth for grinding plants",
      "Mammal ancestor, not a dinosaur"
    ],
    "foundIn": [
      "Texas, USA",
      "New Mexico, USA",
      "Czech Republic"
    ],
    "color": "#FF6A8A6A"
  },
  {
    "id": "diplocaulus",
    "name": "Diplocaulus",
    "era": "OTHER",
    "period": "Early Permian",
    "age": "~295–270 mya",
    "diet": "CARNIVORE",
    "length": "3 ft",
    "weight": "20 lb",
    "habitat": "Swamps of North America",
    "description": "An amphibian with one of the strangest skulls in paleontology — a wide boomerang shape with two long lateral horns. The bizarre skull may have acted as a hydrofoil for swimming, or made it hard for predators to swallow.",
    "funFacts": [
      "Discovered 1878",
      "Boomerang-shaped skull — one of the strangest ever",
      "Skull may have acted as a hydrofoil for swimming",
      "Horns may have prevented predators from swallowing it",
      "A popular fossil for collectors"
    ],
    "foundIn": [
      "Texas Red Beds, USA",
      "Oklahoma, USA"
    ],
    "color": "#FF80A060"
  },
  {
    "id": "eryops",
    "name": "Eryops",
    "era": "OTHER",
    "period": "Early Permian",
    "age": "~295 mya",
    "diet": "CARNIVORE",
    "length": "6 ft",
    "weight": "200 lb",
    "habitat": "Swamps of North America",
    "description": "A crocodile-sized amphibian with a massive broad skull and sharp teeth. Eryops was one of the largest land animals of the early Permian. It looked like a cross between a crocodile and a salamander, hunting fish and small tetrapods in swamps.",
    "funFacts": [
      "Discovered 1884",
      "Crocodile-sized amphibian — 6 ft long",
      "One of the largest land animals of the early Permian",
      "Massive broad skull with sharp teeth",
      "Hunted in Permian swamps"
    ],
    "foundIn": [
      "Texas Red Beds, USA",
      "New Mexico, USA"
    ],
    "color": "#FF90B070"
  },
  {
    "id": "anomalocaris",
    "name": "Anomalocaris",
    "era": "OTHER",
    "period": "Middle Cambrian",
    "age": "~508 mya",
    "diet": "CARNIVORE",
    "length": "3.3 ft",
    "weight": "10 lb",
    "habitat": "Seas of North America & Asia",
    "description": "The world's first super-predator. Anomalocaris was a 3-foot swimming arthropod with large compound eyes, grasping frontal appendages, and a circular mouth of serrated plates. It ruled the Cambrian seas during the explosion of complex life.",
    "funFacts": [
      "Discovered 1892",
      "World's first super-predator",
      "Compound eyes with 16,000 lenses",
      "Mouthparts mistaken for a jellyfish for decades",
      "Apex predator of the Cambrian explosion"
    ],
    "foundIn": [
      "Burgess Shale, British Columbia, Canada",
      "Chengjiang, China"
    ],
    "color": "#FF8B7355"
  },
  {
    "id": "dunkleosteus",
    "name": "Dunkleosteus",
    "era": "OTHER",
    "period": "Late Devonian",
    "age": "~382–358 mya",
    "diet": "CARNIVORE",
    "length": "20–33 ft",
    "weight": "3–4 tons",
    "habitat": "Seas of North America & Europe",
    "description": "A massive armored fish with self-sharpening bony plates instead of teeth. Dunkleosteus was the apex predator of the Devonian 'Age of Fishes.' Its bite created suction that could suck prey in. It could open its mouth in 1/60 of a second.",
    "funFacts": [
      "Discovered 1873",
      "Armored fish with self-sharpening bony plates instead of teeth",
      "Could open its mouth in 1/60 of a second",
      "Apex predator of the Devonian Age of Fishes",
      "Bite created suction to suck in prey"
    ],
    "foundIn": [
      "Ohio, USA",
      "Pennsylvania, USA",
      "Belgium",
      "Morocco"
    ],
    "color": "#FF5A6A8A"
  },
  {
    "id": "tiktaalik",
    "name": "Tiktaalik",
    "era": "OTHER",
    "period": "Late Devonian",
    "age": "~375 mya",
    "diet": "CARNIVORE",
    "length": "9 ft",
    "weight": "200 lb",
    "habitat": "Rivers of North America",
    "description": "The iconic 'fish with wrists' — a transitional fossil between fish and land vertebrates. Tiktaalik had fish-like fins but with wrist bones that could support its body, allowing it to push up out of shallow water. It could breathe air.",
    "funFacts": [
      "Discovered 2004 by Neil Shubin",
      "The 'fish with wrists' — a critical transitional fossil",
      "Had wrist bones in its fins — the first step toward walking",
      "Could breathe air and push up out of water",
      "Targeted search in Arctic Canada found it"
    ],
    "foundIn": [
      "Ellesmere Island, Nunavut, Canada"
    ],
    "color": "#FF5A8A7A"
  },
  {
    "id": "acanthostega",
    "name": "Acanthostega",
    "era": "OTHER",
    "period": "Late Devonian",
    "age": "~365 mya",
    "diet": "CARNIVORE",
    "length": "2 ft",
    "weight": "8 lb",
    "habitat": "Swamps of Greenland",
    "description": "One of the first vertebrates with limbs and digits — but with 8 fingers on each hand. Acanthostega was still primarily aquatic and could not walk on land effectively. Its limbs were for navigating swampy vegetation, not walking.",
    "funFacts": [
      "Discovered 1952",
      "One of the first vertebrates with limbs and digits",
      "Had 8 fingers on each hand — more than any modern animal",
      "Still primarily aquatic — limbs for swamps",
      "Could not walk effectively on land"
    ],
    "foundIn": [
      "Greenland"
    ],
    "color": "#FF6A9A7A"
  },
  {
    "id": "eurypterid",
    "name": "Sea Scorpion (Eurypterid)",
    "era": "OTHER",
    "period": "Silurian",
    "age": "~443–419 mya",
    "diet": "CARNIVORE",
    "length": "8.5 ft",
    "weight": "200 lb",
    "habitat": "Seas of North America & Europe",
    "description": "Sea scorpions were the largest arthropods ever — Jaekelopterus reached 8.5 feet. These ocean predators had pincers and a spiked tail. They ruled the seas before fish evolved jaws. New York's state fossil is a eurypterid.",
    "funFacts": [
      "Discovered 1818",
      "Largest arthropods ever — up to 8.5 ft",
      "Ruled the seas before fish evolved jaws",
      "New York's state fossil",
      "Had pincers and a spiked tail"
    ],
    "foundIn": [
      "New York, USA",
      "Ontario, Canada",
      "England"
    ],
    "color": "#FF5A7A8A"
  },
  {
    "id": "meganeura",
    "name": "Meganeura",
    "era": "OTHER",
    "period": "Carboniferous",
    "age": "~305 mya",
    "diet": "CARNIVORE",
    "length": "28 in wingspan",
    "weight": "1 lb",
    "habitat": "Swamp forests of Europe",
    "description": "A giant griffinfly — ancestor of dragonflies — with a 28-inch wingspan. Meganeura flew through Carboniferous forests when oxygen levels were 35% (vs 21% today), allowing insects to grow to enormous sizes. The largest flying insect ever.",
    "funFacts": [
      "Discovered 1880",
      "Largest flying insect ever — 28-inch wingspan",
      "Giant size enabled by 35% oxygen levels",
      "Ancestor of modern dragonflies",
      "Wingspan like a modern crow"
    ],
    "foundIn": [
      "Commentry, France",
      "England"
    ],
    "color": "#FF70A0C9"
  },
  {
    "id": "arthropleura",
    "name": "Arthropleura",
    "era": "OTHER",
    "period": "Carboniferous",
    "age": "~315–299 mya",
    "diet": "HERBIVORE",
    "length": "8.5 ft",
    "weight": "200 lb",
    "habitat": "Swamp forests of North America & Europe",
    "description": "The largest land arthropod ever — an 8.5-foot millipede that crawled through Carboniferous coal swamps. Arthropleura trackways have been found in Scotland. It was likely herbivorous, eating decaying vegetation.",
    "funFacts": [
      "Discovered 1853",
      "Largest land arthropod ever — 8.5 ft long",
      "Actually a millipede — and likely herbivorous",
      "Giant size enabled by high Carboniferous oxygen",
      "Trackways show it moved quickly"
    ],
    "foundIn": [
      "Scotland, UK",
      "Eastern Canada",
      "Pennsylvania, USA"
    ],
    "color": "#FF6080A0"
  },
  {
    "id": "pulmonoscorpius",
    "name": "Pulmonoscorpius",
    "era": "OTHER",
    "period": "Carboniferous",
    "age": "~335 mya",
    "diet": "CARNIVORE",
    "length": "28 in",
    "weight": "11 lb",
    "habitat": "Swamp forests of Scotland",
    "description": "A giant scorpion reaching 28 inches — larger than a house cat. Pulmonoscorpius had book lungs allowing it to breathe air, an advantage in the high-oxygen Carboniferous. It hunted in the coal swamps of ancient Scotland.",
    "funFacts": [
      "Discovered 1984",
      "Giant scorpion larger than a house cat",
      "Had book lungs — could breathe air",
      "Hunted in Carboniferous coal swamps",
      "One of the largest scorpions ever"
    ],
    "foundIn": [
      "East Kirkton, West Lothian, Scotland"
    ],
    "color": "#FF507090"
  }
];
