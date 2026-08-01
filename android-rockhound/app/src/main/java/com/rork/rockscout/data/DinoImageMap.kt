package com.rork.rockscout.data

import com.rork.rockscout.ui.components.DinoBodyPlan

/**
 * Maps each [DinoBodyPlan] to a generated paleoart image URL hosted on R2.
 * These images are scientifically accurate museum-quality illustrations
 * representing the typical body plan of each category.
 *
 * One representative image per body plan — all 200+ entries share the image
 * of their respective body plan category.
 */
object DinoImageMap {

    private const val BASE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets"

    /** Body plan → paleoart image URL */
    val images: Map<DinoBodyPlan, String> = mapOf(
        DinoBodyPlan.THEROPOD_LARGE to "$BASE/46268019-90eb-4755-ac1e-b208aec87634.png",
        DinoBodyPlan.THEROPOD_SMALL to "$BASE/57579615-2acf-46dc-9eeb-599536da1e58.png",
        DinoBodyPlan.SAUROPOD to "$BASE/bc03e04a-c839-4d8d-9a71-116fff112a64.png",
        DinoBodyPlan.CERATOPSIAN to "$BASE/6fcc57a2-5f3d-4bd6-be38-46f5c6cf555c.png",
        DinoBodyPlan.STEGOSAUR to "$BASE/2896197e-41ed-4bea-881f-b59f5a6f3955.png",
        DinoBodyPlan.ANKYLOSAUR to "$BASE/f44f8d6d-d99e-43bf-9fcb-de1ef5d23fd8.png",
        DinoBodyPlan.ORNITHOPOD to "$BASE/d82700a7-f5e5-43d5-a8ca-836ae31aebdb.png",
        DinoBodyPlan.PTEROSAUR to "$BASE/d6e97103-49b9-4467-a8d7-fd96c1969ae5.png",
        DinoBodyPlan.PLESIOSAUR to "$BASE/26920226-cf15-47eb-82a7-47925d712178.png",
        DinoBodyPlan.ICHTHYOSAUR to "$BASE/1c33272b-aa06-4b73-a963-06a3df1ade99.png",
        DinoBodyPlan.MOSASAUR to "$BASE/161bda97-7713-4824-a696-69a4ecf38a6f.png",
        DinoBodyPlan.THERIZINOSAUR to "$BASE/d6b7203e-5769-409a-bf61-16706064007e.png",
        DinoBodyPlan.SYNAPSID to "$BASE/4b4e8651-5192-40d2-b201-269074aac813.png",
        DinoBodyPlan.MAMMOTH to "$BASE/a8e27526-753a-43cd-977d-3723bb37bfbf.png",
        DinoBodyPlan.SABERTOOTH to "$BASE/810a958c-decc-49fb-8108-2210b15adb5f.png",
        DinoBodyPlan.RHINO_GIANT to "$BASE/0b335739-5026-4a72-b119-90c6fba868c3.png",
        DinoBodyPlan.BIRD_PREHISTORIC to "$BASE/89783331-02fd-4a1c-957e-97b6c029d207.png",
        DinoBodyPlan.CROCODILIAN to "$BASE/40ab6e51-c252-4cc9-a279-d5c8e44dfeca.png",
        DinoBodyPlan.SHARK_GIANT to "$BASE/29b57914-e908-47e4-839f-96db81fab9d5.png",
        DinoBodyPlan.SLOTH_GIANT to "$BASE/8fa90aa0-addb-4b70-a5ab-20baaaa5b374.png",
        DinoBodyPlan.BEAR_GIANT to "$BASE/58234a53-4c6a-44f7-9bac-b53c6335daa4.png",
        DinoBodyPlan.WOLF_PREHISTORIC to "$BASE/3d3dcfe5-cf60-406d-81cb-a5ed10c9dbba.png",
        DinoBodyPlan.ELASMOSAUR to "$BASE/2e2790b3-b3ef-46e3-bf1f-06ba9586d6f1.png",
    )

    /** Get the paleoart image URL for a body plan, or null if not found. */
    fun imageUrl(bodyPlan: DinoBodyPlan): String? = images[bodyPlan]

    /** Get the paleoart image URL for a [DinoEntry]. */
    fun imageUrl(entry: DinoEntry): String? = images[entry.bodyPlan]
}
