package com.rork.rockscout.data

import android.util.Log

/**
 * Individual AI-generated specimen photographs hosted on R2 CDN.
 * 315+ specimens have their own dedicated image; ~100 location/shop entries use best-match fallbacks.
 * Generated 2026-07-01.
 */
object SpecimenImages {

    private const val Q = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets"

    private const val IMG_ACE_OF_DIAMONDS = "$Q/1a1f46b8-61b0-44cf-9c07-5ef0fc68dd00.png"
    private const val IMG_ADAMITE = "$Q/1d944251-1610-4bfb-b25d-f85c0e693cf3.png"
    private const val IMG_AGATE_BLUE = "$Q/be6771d8-7082-4cd6-ba1d-e5962a459e1c.png"
    private const val IMG_AGATE_GRAY = "$Q/76145b61-3285-47ab-a6cd-26d3921f6137.png"
    private const val IMG_AGATE_BROWN = "$Q/671d2687-56d7-4604-b3ef-915df47591e2.png"
    private const val IMG_AGATE_MOSS = "$Q/eba7615e-887d-4e30-824b-761034d36453.png"
    private const val IMG_AGATE = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_ALASKA_JADE = "$Q/23b01be9-e383-41ea-93f9-44f4998735e2.png"
    private const val IMG_ALEXANDRITE = "$Q/71cc8e58-c6f2-4283-9c3b-c74f7b6f1801.png"
    private const val IMG_AMAZONITE = "$Q/d5abe2fa-aa36-4527-8eff-5bebd1bea61e.png"
    private const val IMG_AMBER = "$Q/c1e92740-29aa-4c90-aeeb-390b2a645422.png"
    private const val IMG_AMETHYST = "$Q/83b2bd7a-36bd-4a87-9872-e784d4a3872a.png"
    private const val IMG_AMETRINE = "$Q/299f5914-b267-4877-b889-ed56a72ad90c.png"
    private const val IMG_AMMONITE = "$Q/43091c8c-862a-4f1d-b342-84ee86e12783.png"
    private const val IMG_AMPHIBOLITE = "$Q/738db003-c095-4d57-b93c-400911227343.png"
    private const val IMG_ANDALUSITE = "$Q/64e31bf3-85af-43dd-bac5-ba76be8415e2.png"
    private const val IMG_ANDERSON_MINE = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_ANGLESITE = "$Q/cdcb4702-1fac-4c35-95e1-94a8782294cb.png"
    private const val IMG_ANHYDRITE = "$Q/4c78394a-a995-4486-bc85-34be63a2b0b3.png"
    private const val IMG_ANNABERGITE = "$Q/2ed8d3b2-2f58-4ee7-8b70-75e1dba22373.png"
    private const val IMG_ANORTHOSITE = "$Q/e89ecb7e-2388-4042-a01e-6ec3b4532445.png"
    private const val IMG_ANTHRACITE = "$Q/a5b9a9af-6dc8-4919-9bf3-00f19cd29039.png"
    private const val IMG_APATITE = "$Q/bc721a54-1fad-441a-9687-7a469b6e798d.png"
    private const val IMG_APOPHYLLITE = "$Q/6fe5ba06-510f-42cc-b2c8-c4812cb33eef.png"
    private const val IMG_AQUAMARINE = "$Q/94d8fbc7-7909-494a-bd8c-4ecc639de8fc.png"
    private const val IMG_ARAGONITE = "$Q/dc4db129-50ec-4d28-9d36-4c5f5298b986.png"
    private const val IMG_ARAGONITE_FLOWERS = "$Q/a302c237-8b11-4ba0-a15d-5d3a013a72c8.png"
    private const val IMG_ARCHIMEDES = "$Q/7d89a6d8-e905-4aa9-9dbd-86fc855e9f5b.png"
    private const val IMG_ARKANSAS_QUARTZ_GALLERY = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_ARKOSE = "$Q/1761d90a-e03a-43df-bc5b-643bc83d2d4c.png"
    private const val IMG_ARSENOPYRITE = "$Q/ef14eabb-4ca4-4bd3-8480-9721c6ea95ad.png"
    private const val IMG_AUGITE = "$Q/53724707-5561-41e5-9298-2aec3f37bd8b.png"
    private const val IMG_PYROXENE_GROUP_ROUGH = "$Q/a69edaed-0390-4cfd-8e4f-334d1755d8d3.png"
    private const val IMG_AUTUNITE = "$Q/fd5967bd-b6b5-46df-99a5-ce9c5e39eef2.png"
    private const val IMG_AVENTURINE = "$Q/db0e24b6-df5a-4716-aba2-7b684ac18d1c.png"
    private const val IMG_AXINITE = "$Q/9eda2254-fc94-4211-a731-76402098ebe6.png"
    private const val IMG_AZURITE = "$Q/08135330-34fa-49d2-9350-c57b42c7faa2.png"
    private const val IMG_BACULITES = "$Q/91d3dd96-1ef8-41a9-9025-9eb17b831ef8.png"
    private const val IMG_BAKER_RANCH = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_BARITE = "$Q/8bb98c41-e700-41ed-8b36-1c2c73ec3673.png"
    private const val IMG_BASALT = "$Q/dcc7a373-69cf-4b81-8f77-95510c077ad8.png"
    private const val IMG_BELEMNITE = "$Q/ac51d207-45fe-4a25-a676-4512227c0517.png"
    private const val IMG_BENITOITE = "$Q/b08a2c40-d1d5-40a5-8858-34dae9214b77.png"
    private const val IMG_BERYL_AQUA = "$Q/c9df77dc-33dd-4ab4-9b69-e52fbde89a7d.png"
    private const val IMG_BERYL_MORGANITE = "$Q/db363b38-3d97-4bda-8dc2-d04c8d60e447.png"
    private const val IMG_BERYL_HELIODOR = "$Q/db4f4b53-478c-4b5e-a821-fe3057f62e1d.png"
    private const val IMG_BERYL_GOSHENITE = "$Q/d372a6d3-98e3-4b2a-ae89-c9919bd73881.png"
    private const val IMG_BERYL_HELIODOR_WILD = "$Q/3d8590ac-2bb8-46e8-bb24-39631149cb20.png"
    private const val IMG_BERYL_HELIODOR_MUSEUM = "$Q/f665052d-5ef4-47a7-8e22-f88c16a93941.png"
    private const val IMG_BERYL_MORGANITE_WILD = "$Q/52b94fa9-e14d-48f5-a2c3-f90e2fb9c3c9.png"
    private const val IMG_BERYL_MORGANITE_MUSEUM = "$Q/30ee6a70-6102-45d4-8448-1fdc5a52d5b9.png"
    private const val IMG_BERYL = "$Q/85ebf0e0-32d1-4916-9422-08d064c008c7.png"
    private const val IMG_BISMUTH_CRYSTAL = "$Q/f876baa4-cb5e-4734-9a7e-ffdc9c356aa9.png"
    private const val IMG_BIVALVE_FOSSIL = "$Q/b6b9b5ff-7271-452e-a48b-4936627e5345.png"
    private const val IMG_BLACK_HILLS_INSTITUTE = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_BLASTOID = "$Q/b916a77f-5499-4347-8d76-a1d944d08a8d.png"
    private const val IMG_BLOODSTONE = "$Q/c3d0ec3b-d91e-47c9-88c0-a599221220b6.png"
    private const val IMG_BLUE_ARAGONITE = "$Q/4b5061bf-7d9b-4dda-9b60-5d8887880bde.png"
    private const val IMG_BLUE_LACE_AGATE = "$Q/dcab33ba-ae88-4cd1-9566-ff582d5d47b3.png"
    private const val IMG_BORNITE = "$Q/da0c5d8a-b6ae-41e4-9e9b-c2e167dcf21e.png"
    private const val IMG_BOSTON_MINERAL_CLUB = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_BRACHIOPOD = "$Q/3b5c85f6-9b96-42c3-bb84-1f88774a6d26.png"
    private const val IMG_BRECCIA = "$Q/1f436f98-c7f3-44ef-b1fb-2d4657a1bc19.png"
    private const val IMG_BRIMLEY_YOOPER = "$Q/499875b4-0004-4548-970e-c6291491a942.png"
    private const val IMG_BROCHANTITE = "$Q/9af81aac-e854-4731-85b1-c8f58e656d0d.png"
    private const val IMG_BRONZITE = "$Q/672ba710-d2ca-4976-9751-42b58a26a151.png"
    private const val IMG_BURRO_CREEK = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_CAHABA_RIVER = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_CALAMITES = "$Q/3e6da3af-6f7a-45da-8dc5-56b93535c942.png"
    private const val IMG_CALCITE_AMBER = "$Q/242d26fc-58d2-4534-b667-3858c5a01f2e.png"
    private const val IMG_CALCITE_BLUE = "$Q/827ba0eb-2192-4bee-a7b5-2c66137840b7.png"
    private const val IMG_CALCITE_GREEN = "$Q/c7cf5180-d205-41a9-94e3-f09268b7de74.png"
    private const val IMG_CALCITE_WHITE = "$Q/2f923a04-6948-4840-97cd-8cddbc1c1088.png"
    private const val IMG_CALCITE = "$Q/968e8fb3-595c-4eb9-a25a-7d0d2d1e045a.png"
    private const val IMG_CALCITE_CARIBBEAN = "$Q/c63d2f66-d421-4fb1-809b-09cc2ae24c39.png"
    private const val IMG_HEXAGONAL_CALCITE = "$Q/7c860b67-3b5b-496c-870a-1b7220ba4e56.png"
    private const val IMG_HEXAGONAL_CALCITE_ROUGH = "$Q/438db595-79d2-4849-884c-39bf7abe9dae.png"
    private const val IMG_HEXAGONAL_CALCITE_WILD = "$Q/1d521a03-34fc-4372-a9be-98bf22df81b4.png"
    private const val IMG_HEXAGONAL_CALCITE_MUSEUM = "$Q/8ef38bd3-714b-4bd4-bc18-3e2bc9c2caf2.png"
    private const val IMG_HEXAGONAL_CALCITE_CABOCHON = "$Q/359e91bd-a225-4a16-8873-e39906cff4d7.png"
    private const val IMG_CALIFORNIA_ROCK_SHOP = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_CARNELIAN = "$Q/5d3bd82d-7c98-465f-9a1a-a45353b97ac9.png"
    private const val IMG_CARNOTITE = "$Q/f042f436-c367-4ea7-843a-bb25d2d4a09d.png"
    private const val IMG_CASSITERITE = "$Q/fd006839-65f1-4a1e-a26a-db55b14f4f3f.png"
    private const val IMG_CASSITERITE_BOLIVIA = "$Q/33e6c6a1-1ada-4787-b23d-a77b600c852c.png"
    private const val IMG_CASSITERITE_CORNWALL = "$Q/5ba3a9e3-132a-49b9-8fb4-adc52767a22a.png"
    private const val IMG_CELESTINE_COLORLESS = "$Q/74a3c8a8-cd19-46ae-a37b-b156cbf05cd0.png"
    private const val IMG_CELESTINE_WHITE = "$Q/3f239200-2069-42b7-b48e-8be32c473d86.png"
    private const val IMG_CELESTINE_PINK = "$Q/ee9141a0-4281-4159-aa89-e62089c810d0.png"
    private const val IMG_CELESTINE = "$Q/cb3f7c6b-fa2f-4493-b74c-d6b95faf1198.png"
    private const val IMG_CERUSSITE = "$Q/fdf65b1d-ca19-42af-bc74-1d173ea0b165.png"
    private const val IMG_CHALCOPYRITE = "$Q/028edd02-ea52-4952-a3ca-4128468abce9.png"
    private const val IMG_CHALK_ROCK = "$Q/7bc6fd52-e6b0-4e28-b86c-3e630adedc61.png"
    private const val IMG_CHAMBERSITE = "$Q/b3a53afe-ff6d-4654-bbd0-323c5365eeef.png"
    private const val IMG_CHAROITE = "$Q/43a6d051-05a0-4d93-b727-a550dff53369.png"
    private const val IMG_CHEROKEE_RUBY = "$Q/3acdaba5-968e-4e75-a3d1-2eb5ad61cf5b.png"
    private const val IMG_CHERT = "$Q/fd7d332f-c096-44f1-ac26-951a75a3b359.png"
    private const val IMG_CHLORASTROLITE = "$Q/499875b4-0004-4548-970e-c6291491a942.png"
    private const val IMG_CHROMITE = "$Q/30e47e9e-fa6f-4ced-9839-b7ef06de0ed8.png"
    private const val IMG_CHRYSOBERYL = "$Q/3dee1f13-d0ba-4105-be25-0e3cdb455970.png"
    private const val IMG_CHRYSOCOLLA = "$Q/a4b7d5a1-4b13-4f7b-a3c9-ab7f4f0c5832.png"
    private const val IMG_CHRYSOPRASE = "$Q/315dc844-0efc-465b-bff8-5318960fc7ba.png"
    private const val IMG_CINNABAR = "$Q/b9aef18c-fb66-483a-bb74-547ceca0a37e.png"
    private const val IMG_CITRINE = "$Q/3fcf5457-26b9-44ee-870b-ed47e10ed672.png"
    private const val IMG_COALINGA_JADE = "$Q/23b01be9-e383-41ea-93f9-44f4998735e2.png"
    private const val IMG_COBALTITE = "$Q/f3eebb33-8bc3-4412-a6d0-8486feec3222.png"
    private const val IMG_COELACANTH_FOSSIL = "$Q/b7ebf563-c531-4859-8b2d-c650c950eb85.png"
    private const val IMG_COLEMANITE = "$Q/908c8f78-dc88-41bd-a098-0ad7f4efcc91.png"
    private const val IMG_CONGLOMERATE = "$Q/4c238431-c61f-483c-959e-2faaeb8ea442.png"
    private const val IMG_CONICHALCITE = "$Q/648cdbca-662b-4f0d-9acf-3c9f2c03c36c.png"
    private const val IMG_CONULARIA = "$Q/9033157c-c323-420d-b967-04dc7e145497.png"
    private const val IMG_COPPER_HARBOR_YOOPER = "$Q/40fb406a-401c-43c6-9899-6d507589deed.png"
    private const val IMG_COPROLITE = "$Q/4003b2bd-aa8f-4209-bf03-253d33181581.png"
    private const val IMG_COQUINA = "$Q/6d936b5c-bc85-408d-9bfa-1f497e586d78.png"
    private const val IMG_CORUNDUM_BLUE = "$Q/49ed75a2-db2b-44d7-996d-9db5e639bb6d.png"
    private const val IMG_CORUNDUM_YELLOW = "$Q/b976c67b-2fd0-4037-8015-dafba5a9e85f.png"
    private const val IMG_CORUNDUM_GREEN = "$Q/53a31e43-e069-4665-afd3-ad203d77e16e.png"
    private const val IMG_CORUNDUM_PINK = "$Q/0bf20448-0e75-4917-af2c-d65a6719cb10.png"
    private const val IMG_CORUNDUM = "$Q/ac129330-3d4c-45fb-a24f-fe0fd40e6190.png"
    private const val IMG_CRABTREE_EMERALD = "$Q/33eacef5-cb8a-4793-9cd6-9252c42e7403.png"
    private const val IMG_CRATER_OF_DIAMONDS = "$Q/d3a5cdaf-cd76-4dfe-ab86-7e106532b9da.png"
    private const val IMG_CRINOID = "$Q/c8970948-e3eb-4119-8b49-a2a12780dbc0.png"
    private const val IMG_CROCOITE = "$Q/1855cb33-bc0d-4454-ad31-337f46e39cdc.png"
    private const val IMG_CRYSTAL_GROVE = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_CRYSTAL_MOUNTAIN = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_CRYSTAL_PARK = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_CRYSTAL_WORKS = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_CUPRITE = "$Q/5473555f-aa84-4348-9b59-799315b16c9b.png"
    private const val IMG_DANBURITE = "$Q/e37c77a7-8411-4df2-a23c-6976993b667d.png"
    private const val IMG_DENDRITE_AGATE = "$Q/d53d0506-4175-4d6b-9443-1dda1b4a9d15.png"
    private const val IMG_DENIO_THUNDEREGGS = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_DENVER_GEM_SHOW = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_DESCLOIZITE = "$Q/d6483ebc-5f0b-41b7-ba50-b210f9126756.png"
    private const val IMG_DESERT_ROSE = "$Q/84d12592-9de8-460f-9aa8-a1bd17c50548.png"
    private const val IMG_DIAMOND = "$Q/d3a5cdaf-cd76-4dfe-ab86-7e106532b9da.png"
    private const val IMG_DIAMOND_YELLOW = "$Q/849f0dea-e051-49a2-8b66-9732832b8402.png"
    private const val IMG_DIAMOND_PINK = "$Q/b796696e-8a4d-4ef9-9121-2166cf8b94d7.png"
    private const val IMG_DIAMOND_BLUE = "$Q/77b95353-dd4b-4fae-88f5-f468b353dd02.png"
    private const val IMG_DIAMOND_HILL = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_DIAMOND_PEAK = "$Q/d3a5cdaf-cd76-4dfe-ab86-7e106532b9da.png"
    private const val IMG_DIATOMITE = "$Q/e09b269c-b085-40eb-ba7a-7083c1f389ef.png"
    private const val IMG_DINOSAUR_BONE = "$Q/6bc8e37c-11c8-42ed-9250-dff8fbe2bfec.png"
    private const val IMG_DINOSAUR_EGGSHELL = "$Q/6e37e132-1935-4334-adf9-bdc613e144f4.png"
    private const val IMG_DINOSAUR_TRACK = "$Q/061c9256-1883-4c72-906b-f8caa015d444.png"
    private const val IMG_DIOPSIDE = "$Q/33652601-0c20-461f-ac52-b2e084e86c39.png"
    private const val IMG_DIOPTASE = "$Q/e365d410-13de-4872-bc2b-1e8cd85988a1.png"
    private const val IMG_DIORITE = "$Q/444eab0c-1550-45ba-aa6a-8769ef3543d5.png"
    private const val IMG_DIRE_WOLF_TOOTH = "$Q/d006cb33-88f8-49a1-a21f-55a012930505.png"
    private const val IMG_DIXIE_CRYSTAL_CO = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_DOLOMITE = "$Q/1af220d2-979c-4f3e-81be-251fe2e0c490.png"
    private const val IMG_DUGWAY_GEODES = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_DUMORTIERITE = "$Q/3bee65a7-fb24-4419-9d4e-57ca6241784d.png"
    private const val IMG_DUMORTIERITE_QUARTZ = "$Q/5c902613-10d7-482d-8963-3611df356089.png"
    private const val IMG_ECLOGITE = "$Q/8f9dff9a-8da3-4d08-a500-10168b7c38b4.png"
    private const val IMG_ECPHORA = "$Q/c0ca9cf4-abad-43cf-92bd-9e11ac38afae.png"
    private const val IMG_ELLENVILLE = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_EMERALD = "$Q/33eacef5-cb8a-4793-9cd6-9252c42e7403.png"
    private const val IMG_EMERALD_HOLLOW = "$Q/33eacef5-cb8a-4793-9cd6-9252c42e7403.png"
    private const val IMG_ENCHANTMENT_AGATES = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_ENCHODUS = "$Q/0f0e2a7f-3f64-43b1-af7a-c576f667c1fe.png"
    private const val IMG_ENDOCERAS = "$Q/6ae3f3db-d24e-41c3-9b68-75e78e8cae0a.png"
    private const val IMG_ENSTATITE = "$Q/bb8864b6-750b-4efc-b405-a7ef166ba983.png"
    private const val IMG_EPIDOTE = "$Q/1ec967b7-7fe5-4184-a008-e4be92de4d8e.png"
    private const val IMG_ERYTHRITE = "$Q/61b0e9cf-d711-406e-b5ae-9b39f0baed4f.png"
    private const val IMG_EUCLASE = "$Q/3f2c1fb2-23e6-4456-81a7-fa578cd71f52.png"
    private const val IMG_EURYPTERID = "$Q/2a9c19cd-8a48-415e-b8d7-a11cd4648f6e.png"
    private const val IMG_EXOGYRA = "$Q/e171c8f6-f9a1-4bb7-87d8-9b3d3ce2aa79.png"
    private const val IMG_FAVOSITES = "$Q/332f8a9c-067f-42d9-902e-eba65d77531a.png"
    private const val IMG_FENESTELLA = "$Q/0495228a-b7e6-4fc4-b206-18f07deed7c7.png"
    private const val IMG_FERBERITE = "$Q/85adedf0-da33-47b1-8cd8-ab64e3611fe4.png"
    private const val IMG_FIRE_AGATE = "$Q/dd8c1cc6-92c1-4f5f-a20a-872b2b4ac0f0.png"
    private const val IMG_FIRE_OPAL = "$Q/25501457-114a-4026-9158-0d49b505f672.png"
    private const val IMG_FLINT = "$Q/26b27c8a-7fcf-4393-a3b2-e510e6d3deeb.png"
    private const val IMG_FLORIDA_ROCK_SHACK = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_FLUORITE = "$Q/95a9e35b-30d9-4522-82e2-1415c612dbc7.png"
    private const val IMG_FLUORITE_GREEN = "$Q/75343202-8a5e-4e21-8497-2b45bc42fedc.png"
    private const val IMG_FLUORITE_BLUE = "$Q/c58815d6-a6e2-4748-911c-bf6ef0ab400f.png"
    private const val IMG_FLUORITE_YELLOW = "$Q/aab94207-30f7-4cf9-9e1e-edefc05f4640.png"
    private const val IMG_FLUORITE_RAINBOW = "$Q/2f715c18-26c4-401f-833a-6459b0f3331d.png"
    private const val IMG_FLUORITE_DISTRICT = "$Q/95a9e35b-30d9-4522-82e2-1415c612dbc7.png"
    private const val IMG_FOSSIL_CORAL = "$Q/fcf5f8b0-f3d0-4094-a28c-abe40ce33cdb.png"
    private const val IMG_FOSSIL_FERN = "$Q/35cd5715-e26e-4f89-9300-9dc8aa20424b.png"
    private const val IMG_FOSSIL_FISH = "$Q/e66e6d4d-356f-40fd-8630-a1e0e5fcdec1.png"
    private const val IMG_FRANKLIN_GEM = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_FRANKLIN_MINERAL = "$Q/95a9e35b-30d9-4522-82e2-1415c612dbc7.png"
    private const val IMG_FUSULINID = "$Q/c01f0043-dd65-4113-9182-6b94c64408b5.png"
    private const val IMG_GABBRO = "$Q/8388f81e-036a-4463-be88-ea75d8662756.png"
    private const val IMG_GALENA = "$Q/add3e57a-300d-49b4-8cb5-9188140a8c7b.png"
    private const val IMG_GARNET_ORANGE = "$Q/b4e927af-4f52-4ff1-a493-26cdc4509b2c.png"
    private const val IMG_GARNET_GREEN = "$Q/333f44eb-e761-43e6-9398-89121d6ccd73.png"
    private const val IMG_GARNET_PURPLE = "$Q/f9c58154-cecb-4775-b094-8d448da734c8.png"
    private const val IMG_GARNET = "$Q/271f5e11-141c-4bdf-ba3e-c52db9deb301.png"
    private const val IMG_GARNET_ALMANDINE_SCHIST = "$Q/9e2775e9-f7d8-456b-86a8-7a6af6104441.png"
    private const val IMG_GARNET_ALMANDINE_ROUGH = "$Q/e0667074-27d7-45ac-8393-8cce5ef72de4.png"
    private const val IMG_GARNET_ALMANDINE_MUSEUM = "$Q/b598b1de-6dc3-4b6f-a7de-6507f049837a.png"
    private const val IMG_GARNET_HILL = "$Q/271f5e11-141c-4bdf-ba3e-c52db9deb301.png"
    private const val IMG_GASPEITE = "$Q/d0bf7bbb-bda5-45e1-a37a-f5217836d0d0.png"
    private const val IMG_GASTROPOD_FOSSIL = "$Q/8088b0b1-6e8a-4bff-89ca-ac945cace4c6.png"
    private const val IMG_GEM_MINERAL_HALL = "$Q/d3a5cdaf-cd76-4dfe-ab86-7e106532b9da.png"
    private const val IMG_GEM_MOUNTAIN = "$Q/271f5e11-141c-4bdf-ba3e-c52db9deb301.png"
    private const val IMG_GIANT_SLOTH_CLAW = "$Q/78f360d3-8c70-4e47-8a44-ee705f614f06.png"
    private const val IMG_GILSUM_MINE = "$Q/95a9e35b-30d9-4522-82e2-1415c612dbc7.png"
    private const val IMG_GINKGO_FOSSIL = "$Q/bc435f0f-7749-4a1e-ad8d-f6866e9b713f.png"
    private const val IMG_GLASS_BUTTE = "$Q/a2b1f9e9-180d-42eb-b34c-a40a2a0df26c.png"
    private const val IMG_GLAUCOPHANE = "$Q/a05bebed-b162-4823-898f-65da1d5e4bdf.png"
    private const val IMG_GLOSSOPTERIS = "$Q/116877c8-ebb9-465e-8cac-a1c754c496ab.png"
    private const val IMG_GNEISS = "$Q/cdc89459-542b-42c9-a9a6-76b2c9c9a4de.png"
    private const val IMG_GOETHITE = "$Q/17a030a0-4d65-47a3-b60c-8a7a3241d415.png"
    private const val IMG_GONIATITE = "$Q/c8fa7b84-db0f-432a-b1d7-993e549e1d5a.png"
    private const val IMG_GOSHENITE = "$Q/603f9945-ce0b-4047-8c57-c37904fa1519.png"
    private const val IMG_GRAND_MARAIS_YOOPER = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_GRANDIDIERITE = "$Q/8a510d07-f77a-4da1-827f-d1aca208d5b8.png"
    private const val IMG_GRANITE_GRAY = "$Q/92aadce6-472c-4ab1-8cdb-e8d191062601.png"
    private const val IMG_GRANITE_WHITE = "$Q/f6978e12-d67a-43b7-9b6a-eb39d75d57b9.png"
    private const val IMG_GRANITE_BLACK_SPECKLED = "$Q/11fdc6de-c884-4bc3-91c9-089dbb2f0e81.png"
    private const val IMG_GRANITE = "$Q/8bff65d0-0992-413a-adda-8580519d929b.png"
    private const val IMG_GRAPHITE = "$Q/8e77d51b-e034-40f3-a882-42175da02e8d.png"
    private const val IMG_GRAPTOLITE = "$Q/6fb4c5ae-7ada-4ad6-ac7f-3e77fc00a8ff.png"
    private const val IMG_GRAVES_MOUNTAIN = "$Q/c62af512-3362-4f5f-913a-4e0b14226083.png"
    private const val IMG_GREAT_WHITE_TOOTH = "$Q/a10dbbcc-3955-494e-b8a6-feeef2a08d0e.png"
    private const val IMG_GREENSCHIST = "$Q/2f95793f-8fdd-4d0b-9d33-2707eac7c5f5.png"
    private const val IMG_GROSSULAR = "$Q/0bfbd593-de05-4d31-980a-716f093322f6.png"
    private const val IMG_GRYPHAEA = "$Q/5843d922-c1bb-4659-a329-f3b0ef0143c1.png"
    private const val IMG_GYPSUM = "$Q/55d597b2-0ca1-4f2a-8e70-d060602f29d8.png"
    private const val IMG_GYPSUM_MINERAL_ROUGH = "$Q/60c0c945-b692-48f4-8e4d-9b1d33a276b5.png"
    private const val IMG_GYPSUM_MINERAL_WILD = "$Q/7d8f28bf-a579-430c-8bd7-89f6f402bb69.png"
    private const val IMG_GYROLITE = "$Q/b89a5a03-5eda-4efe-8301-5976eb7311f7.png"
    private const val IMG_HALLELUJAH_JCT = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_HALYSITES = "$Q/82fe4bcf-e7f6-4798-a24f-d9332daec945.png"
    private const val IMG_HAMBERGITE = "$Q/45768fed-0eb3-4b6d-b296-b341b1e469a9.png"
    private const val IMG_HELENA_SAPPHIRE = "$Q/0968cca6-0b47-4cb3-9158-6ca11ee8811b.png"
    private const val IMG_HELICOPRION = "$Q/2acfe6e0-77fc-4c75-93a8-4337d68e86d8.png"
    private const val IMG_HELIODOR = "$Q/7fa59ee9-43fc-4298-871a-69a43ac4cdb9.png"
    private const val IMG_HELENITE = "$Q/bbf89ee1-793a-4038-bebe-dd21eade33ad.png"
    private const val IMG_HEMATITE = "$Q/bd023a00-299a-44c7-941c-de6a5a593616.png"
    private const val IMG_HERKIMER = "$Q/ffe8b73c-b664-4533-945e-5fea690c0ecc.png"
    private const val IMG_HEULANDITE = "$Q/2f7ecaf1-71ba-4381-923d-d58f33f3629f.png"
    private const val IMG_HIBONITE = "$Q/f503d323-91e6-43cf-9ad9-56823b99add4.png"
    private const val IMG_HIDDENITE = "$Q/247d9797-8cd4-4400-92e3-beaf906fb2fd.png"
    private const val IMG_HIMALAYA_MINE = "$Q/b8a1fdf6-758b-4739-8983-0c91f93eb352.png"
    private const val IMG_HOG_CREEK = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_HOGG_MINE = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_HORNFELS = "$Q/3cc3f121-cd3d-4a28-ae33-13f0f219043f.png"
    private const val IMG_HORSE_CANYON = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_HORSESHOE_CRAB = "$Q/013c1087-f5a9-4dda-b3c8-76f61a53aea8.png"
    private const val IMG_HUEBNERITE = "$Q/54ccc01f-d92e-4fb7-8d4d-4bf408e4344d.png"
    private const val IMG_HYBODUS = "$Q/efabddda-884e-4961-824a-4bd8852e18a9.png"
    private const val IMG_HYPERSTHENE = "$Q/0af23d51-8f41-491f-82ba-228ae841a97d.png"
    private const val IMG_ICHTHYOSAUR_VERTEBRA = "$Q/6c0232ec-fd0e-40c1-85ba-7a112e26b877.png"
    private const val IMG_INDICOLITE = "$Q/115e2f4b-083b-4eae-a426-6e9ac2c289a9.png"
    private const val IMG_INOCERAMUS = "$Q/4c46d44d-489f-42f5-bc13-7f6091ba43d7.png"
    private const val IMG_IOLITE = "$Q/81492d6b-ddda-4a2a-9df8-bc768b66f3cc.png"
    private const val IMG_IROQUOIS_COPPERMINE = "$Q/40fb406a-401c-43c6-9899-6d507589deed.png"
    private const val IMG_ISABELLA_MINE = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_JACKSON_CROSSROADS = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_JADE_WHITE = "$Q/4c0b09f6-cae7-4280-9a5d-b38a60529789.png"
    private const val IMG_JADE_LAVENDER = "$Q/e882fd8c-13e7-4d99-b167-eefe8b724ee1.png"
    private const val IMG_JADE_BLACK = "$Q/e617514c-4010-446d-980a-b6ded3f402cc.png"
    private const val IMG_JADE = "$Q/87c847ff-74ee-4145-9f7a-42c83c76d809.png"
    private const val IMG_JADE_COVE = "$Q/23b01be9-e383-41ea-93f9-44f4998735e2.png"
    private const val IMG_JASPER_YELLOW = "$Q/3c82cecd-d44f-48b5-ac2c-35b11a418686.png"
    private const val IMG_JASPER_OLIVE = "$Q/9eb31d3c-20e3-4e96-82c9-48638b71377d.png"
    private const val IMG_JASPER_OCHRE = "$Q/9b1f90dd-73d7-47a2-9ac5-58762a38d8a3.png"
    private const val IMG_JASPER_DALMATIAN = "$Q/bc24debf-fff3-4985-90f0-c733087fae9d.png"
    private const val IMG_JASPER = "$Q/328b7dd3-f045-49c5-a2da-824400d25194.png"
    private const val IMG_JEREMEJEVITE = "$Q/da0b4054-4390-43b5-be3c-c3ddc4ff2af6.png"
    private const val IMG_JOAQUINITE = "$Q/29a629c7-91d2-4dfc-9b45-bd990172359d.png"
    private const val IMG_KEENAN_QUARRY = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_KEOKUK_GEODES = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_KEYSTONE_GALLERY = "$Q/218853fe-e40a-45c3-a372-74ae1cda3aab.png"
    private const val IMG_KOMATIITE = "$Q/c02a88ba-86aa-4617-87fc-8e425dd8f8ff.png"
    private const val IMG_KUNZITE = "$Q/0502dda7-9a9a-4459-9789-3c57c5608964.png"
    private const val IMG_KYANITE = "$Q/a282f223-bc63-4b52-b20c-a421cffb350e.png"
    private const val IMG_LA_BREA_TAR_PITS_SHOP = "$Q/97dcce64-3670-4514-bb20-fb45d10ef4ed.png"
    private const val IMG_LABRADORITE = "$Q/a417f5fb-1e7a-46b0-bbf5-6d72df5ccfba.png"
    private const val IMG_LABRADORITE_PEACOCK = "$Q/3d98771f-3719-4006-9b16-0a9c5904d285.png"
    private const val IMG_LABRADORITE_GOLDEN = "$Q/35ed68f4-deff-4357-8f08-e45f81f1984a.png"
    private const val IMG_LAKE_GEORGE = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_LAKE_SUPERIOR_AGATE = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_LAPIS_LAZULI = "$Q/ce15c050-ec63-4ef0-bfea-d66c63efd132.png"
    private const val IMG_LARIMAR = "$Q/068761f2-3d0b-4dea-a4cb-7cfa1b531fff.png"
    private const val IMG_LAZULITE = "$Q/8bb4b12d-05a6-44d3-a771-030a3556acf7.png"
    private const val IMG_LEPIDOCROCITE = "$Q/618b7931-f17c-40d9-979f-4826676583b2.png"
    private const val IMG_LEPIDODENDRON = "$Q/5895047e-bffb-4502-ab2a-f5749d13c997.png"
    private const val IMG_LEPIDOLITE = "$Q/5ba3955e-1842-4355-be8d-f63f3ed799d0.png"
    private const val IMG_LEPIDOLITE_BOTRYOIDAL = "$Q/b0c6c2e0-17d3-402b-a05b-545f89331544.png"
    private const val IMG_LIMESTONE = "$Q/ef99523e-6d0b-470d-8dd6-e61c725e43f7.png"
    private const val IMG_LINARITE = "$Q/e7ab6c22-822d-44a2-86ca-f6eda2ba08af.png"
    private const val IMG_LINGULA = "$Q/8cff8fb5-dccc-471e-8dbc-5db33c7379a1.png"
    private const val IMG_LLANO_UPLIFT = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_MAGNESITE = "$Q/e3d25043-926c-4cb4-9c2c-b6d12c5369c3.png"
    private const val IMG_MAGNETITE = "$Q/56918ade-d762-4bbe-b28d-4628236046c7.png"
    private const val IMG_MAIN_STREET_ROCKS = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_MAIN_STREET_ROCKS_CLAWSON = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_MALACHITE = "$Q/25b82e18-646a-4751-95d7-cf4f37805733.png"
    private const val IMG_MALACHOLLA = "$Q/7d6661e9-c2f0-40a5-b7e5-6575c2c55c29.png"
    private const val IMG_MAMMOTH_TOOTH = "$Q/9f69bc77-75f6-4c8c-b076-b5884d986ebf.png"
    private const val IMG_MARBLE = "$Q/8085f892-cc06-48c3-8466-a9981d6535d5.png"
    private const val IMG_MARBLE_PINK = "$Q/1794c5f0-ed27-4492-afbd-7432223b7d32.png"
    private const val IMG_MARBLE_GREEN = "$Q/a5f1711d-fcf1-46ea-997f-bcf736497d50.png"
    private const val IMG_MARQUETTE_YOOPER = "$Q/499875b4-0004-4548-970e-c6291491a942.png"
    private const val IMG_MCLAIN_YOOPER = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_MEGALODON_TOOTH = "$Q/25b546bb-ad47-424b-8d5d-85f5d75204f7.png"
    private const val IMG_MESOLITE = "$Q/2d3cea09-1a34-4676-8a04-b66217feb232.png"
    private const val IMG_MIGMATITE = "$Q/1e4d8d22-bdfb-4db1-9b20-bc1c9751a818.png"
    private const val IMG_MIMETITE = "$Q/ad6313c6-5995-4e9e-9e11-87033663b3ee.png"
    private const val IMG_MOLYBDENITE = "$Q/db138736-a20c-4bae-8ca3-b98e73dd201c.png"
    private const val IMG_MOHAWKITE = "$Q/4a2ef6f8-f4ac-4f9f-ae9a-353753032ad6.png"
    private const val IMG_MOHAWKITE_WILD = "$Q/c6a29e61-b7b3-4654-8983-9fca7051c6e6.png"
    private const val IMG_MOHAWKITE_MUSEUM = "$Q/74adc77a-7ab1-4195-b12e-f19df95af704.png"
    private const val IMG_MOONSTONE = "$Q/4d013cde-377e-45a2-adf0-2e83079635d5.png"
    private const val IMG_MOREFIELD_MINE = "$Q/d5abe2fa-aa36-4527-8eff-5bebd1bea61e.png"
    private const val IMG_MORGANITE = "$Q/270152be-a6a7-4858-8215-789033d53d8d.png"
    private const val IMG_MOSASAUR_TOOTH = "$Q/990835dc-42c6-4862-9597-110614bef050.png"
    private const val IMG_MOSS_AGATE = "$Q/9b38f6a5-4d99-4700-82d3-6f2456b1c914.png"
    private const val IMG_MOTTRAMITE = "$Q/f8218a8d-839d-42d3-b262-a56210fd59ff.png"
    private const val IMG_MOUNT_APATITE = "$Q/bc721a54-1fad-441a-9687-7a469b6e798d.png"
    private const val IMG_MUNISING_YOOPER = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_MUSCOVITE = "$Q/c72c3fd3-9287-4381-957f-f57d0f6f61a4.png"
    private const val IMG_MUSGRAVITE = "$Q/978586ac-f9d7-42ac-b111-31a45cffd894.png"
    private const val IMG_MUSKALLONGE_YOOPER = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_MYLONITE = "$Q/cac3d0fe-0114-469c-af9d-379826b58b97.png"
    private const val IMG_NATIVE_COPPER = "$Q/40fb406a-401c-43c6-9899-6d507589deed.png"
    private const val IMG_NATIVE_GOLD = "$Q/bbb51387-2359-4f34-b89c-310b39ddfba0.png"
    private const val IMG_NATIVE_SILVER = "$Q/6de6b21e-9052-4c98-b0c2-e4c545f4ab57.png"
    private const val IMG_NATIVE_SULFUR = "$Q/36c15d91-53d2-4a78-87b0-321981d8e908.png"
    private const val IMG_NATROLITE = "$Q/db6bd39d-b8d2-4142-addc-d8bae9a5126c.png"
    private const val IMG_NAUTILOID = "$Q/ab973752-fef9-47ee-91eb-a7aa95b4d4ca.png"
    private const val IMG_NEPTUNITE = "$Q/222060d3-0c11-46bc-9141-4f5064636e9f.png"
    private const val IMG_NETHERS_FARM = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_NEW_MEXICO_ROCK_SHOP = "$Q/1e8be48f-0171-4f2f-8a9d-d71be01049a9.png"
    private const val IMG_NORITE = "$Q/a3a0ce3b-1c9e-4c1b-aa27-c7ab5819fc71.png"
    private const val IMG_NOVACULITE = "$Q/d21cd251-40a1-45cb-8e96-0a46cc1ac1b9.png"
    private const val IMG_NUMMULITES = "$Q/3609a8a4-2464-4cba-a395-63d6e355bab4.png"
    private const val IMG_OBSIDIAN = "$Q/a2b1f9e9-180d-42eb-b34c-a40a2a0df26c.png"
    // Real Wikimedia Commons photos for the merged Obsidian/Apache Tears card.
    private const val IMG_COMMONS_APACHE_TEARS = "https://upload.wikimedia.org/wikipedia/commons/f/fb/Obsydian_(%C5%81zy_Apacza)_w_perlicie_-_Arizna._USA..jpg"
    private const val IMG_COMMONS_APACHE_TEARS_PERLITE = "https://upload.wikimedia.org/wikipedia/commons/b/bf/Obsidian_in_perlite_(Arnett_Rhyolite,_Miocene,_~15-18.6_Ma;_near_Superior,_Arizona,_USA)_7.jpg"
    private const val IMG_OBSIDIAN_MAHOGANY = "$Q/4dcc8766-b9db-4670-87c2-bee093186d48.png"
    private const val IMG_OBSIDIAN_RAINBOW = "$Q/2a06ea43-a06f-4f92-8e5e-578eeddb12f1.png"
    private const val IMG_OCEANVIEW_MINE = "$Q/b8a1fdf6-758b-4739-8983-0c91f93eb352.png"
    private const val IMG_OKENITE = "$Q/038b9a47-c5ca-4ba4-b7ef-b047ff060c6a.png"
    private const val IMG_OLIVENITE = "$Q/56cd324e-e72d-403d-baf9-48bf4d5cd21b.png"
    private const val IMG_ONYX = "$Q/c7db20ce-bbe6-43ac-a051-10451df5f09e.png"
    private const val IMG_OPAL = "$Q/9afc10aa-00e3-4b76-9962-2a044af38854.png"
    private const val IMG_OPAL_BLACK = "$Q/f0a087c6-b37c-48f8-b03b-fdd2b34ba0b5.png"
    private const val IMG_OPAL_FIRE = "$Q/e63db84b-5472-47c8-8168-cb48e2b45bcb.png"
    private const val IMG_OREGON_PETRIFIED = "$Q/8e16ab74-6bc0-44a2-b6d9-f47e4279e4ac.png"
    private const val IMG_OREGON_ROCK_N_GEM = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_ORTHOCERAS = "$Q/4806c94c-a31d-42a9-b406-ca8c98faa14a.png"
    private const val IMG_ORTHOCLASE = "$Q/cbb8926b-5985-48ee-972b-993d68dfeff7.png"
    private const val IMG_PAINITE = "$Q/5415e1fa-4567-496a-908e-de1f93aa322a.png"
    private const val IMG_PARGASITE = "$Q/0c32d14f-550e-47f8-b8c2-0d46430835a3.png"
    private const val IMG_PEGMATITE = "$Q/2d6a928e-58e3-45b3-9c1c-5b4cd27bf3c3.png"
    private const val IMG_PELECYPOD = "$Q/2384d72d-6d55-419e-9fef-6022739ff3f5.png"
    private const val IMG_PERIDOT = "$Q/5555806c-d983-48d3-a8fd-2296c4805592.png"
    private const val IMG_PERIDOT_BEACH = "$Q/5555806c-d983-48d3-a8fd-2296c4805592.png"
    private const val IMG_PETOSKEY_HUNTING = "$Q/332f8a9c-067f-42d9-902e-eba65d77531a.png"
    private const val IMG_PETRIFIED_WOOD = "$Q/8e16ab74-6bc0-44a2-b6d9-f47e4279e4ac.png"
    private const val IMG_PEZZOTTAITE = "$Q/55ac632e-57d5-420b-b7cd-3c21cee8cd32.png"
    private const val IMG_PHENAKITE = "$Q/2a6f37c8-d50e-4212-a71d-eaa7bf643512.png"
    private const val IMG_PHLOGOPITE = "$Q/ebf2c845-ce66-4c76-b889-e4aa6bfc9d92.png"
    private const val IMG_PHOSPHOSIDERITE = "$Q/6eeb6aee-9286-4fff-b0e7-dc7c3b71e6e7.png"
    private const val IMG_PIETERSITE = "$Q/e2040e0e-7868-403f-b241-33e77b710e13.png"
    private const val IMG_PINK_HALITE = "$Q/24183443-245e-4ad3-9213-a71eb365b507.png"
    private const val IMG_PLESIOSAUR_TOOTH = "$Q/df25c623-b328-48b1-b6c4-3d91da80c996.png"
    private const val IMG_PLIOSAUR_TOOTH = "$Q/18c111f3-0525-4bd4-87ba-8dfcf83560ce.png"
    private const val IMG_PLUME_AGATE = "$Q/6165e046-1a3a-48d1-bcd9-531cd5717845.png"
    private const val IMG_POUDRETTEITE = "$Q/0944fbcb-98bb-4b7e-9822-fe3ea797a075.png"
    private const val IMG_PRASIOLITE = "$Q/7d736052-349e-4ec1-beee-c64d46283225.png"
    private const val IMG_PREHNITE = "$Q/25d859c6-839f-482f-b5c6-4132e56c0766.png"
    private const val IMG_PRODUCTUS = "$Q/e872bc73-71dc-496f-a2b5-f5aa995248c5.png"
    private const val IMG_PSILOMELANE = "$Q/cf22bc09-bf80-4381-8da5-6d4446884acb.png"
    private const val IMG_PTYCHODUS = "$Q/b17cb480-0f13-4aba-9a03-60974bd051b7.png"
    private const val IMG_PUMICE = "$Q/0a48a3a4-7e4e-4cc2-a3ea-57bb2baeb79f.png"
    private const val IMG_PUMPELLYITE = "$Q/c7727a67-5e2a-4e76-b4d2-9d6a4b8657a7.png"
    private const val IMG_PURPURITE = "$Q/1dbf6613-bb05-4db7-aa0e-5747c7e4f128.png"
    private const val IMG_PURPLE_ARAGONITE = "$Q/c2ab6f4b-0d7c-43c7-afca-61651444a7f1.png"
    private const val IMG_PYRITE = "$Q/ce54d4e8-b66c-4431-a33a-9bcae71ff5a5.png"
    private const val IMG_PYROLUSITE = "$Q/066fd757-6fba-4e9e-a69e-cb9940ce5af1.png"
    private const val IMG_PYROMORPHITE = "$Q/c71c41da-f606-4c62-acc0-ea8e5a4b71f2.png"
    private const val IMG_QUARTZ = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_QUARTZITE = "$Q/69708fc1-4a45-4565-8fb0-482d72eced1e.png"
    private const val IMG_RAINBOW_LATTICE = "$Q/d02a9792-da2f-4edb-b912-89655aab478f.png"
    private const val IMG_RAINBOW_RIDGE = "$Q/9afc10aa-00e3-4b76-9962-2a044af38854.png"
    private const val IMG_REALGAR = "$Q/da10718d-1248-4cf3-8b9f-875a998ea69e.png"
    private const val IMG_RHODOCHROSITE = "$Q/a7c07e6d-af77-42f1-8456-280602e20019.png"
    private const val IMG_RHODOCHROSITE_TAILINGS = "$Q/5bfb181d-cbeb-47dc-835e-f29791000ee0.png"
    private const val IMG_RHODONITE = "$Q/8b8f9185-d0b4-4efb-b9ac-99523aba121d.png"
    private const val IMG_RHYOLITE = "$Q/2bf7a5c3-b2c4-4c29-b1c1-66c09f54a56d.png"
    private const val IMG_RIEBECKITE = "$Q/00f5817c-c82a-443b-9b2d-c1cae12085fd.png"
    private const val IMG_ROCK_ELK = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_ROCK_SHOP_MAGGIE = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_ROCKHOUND_PARK = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_RONTONDA = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_ROSE_QUARTZ = "$Q/81179175-b664-4280-b096-58ade243bfa3.png"
    private const val IMG_ROYAL_PEACOCK = "$Q/e998f079-8703-4f63-aa95-014760ba0bbe.png"
    private const val IMG_RUBELLITE = "$Q/d157eafb-1799-448a-8dce-df84720656de.png"
    private const val IMG_RUGOSE_CORAL = "$Q/0c82d77c-7c97-485d-836f-bdfdb05ea6bb.png"
    private const val IMG_RUTILATED_QUARTZ = "$Q/93c9c161-ae2b-41ae-93c3-ccb20e66d8ec.png"
    private const val IMG_RUTILE = "$Q/c62af512-3362-4f5f-913a-4e0b14226083.png"
    private const val IMG_RUTILE_HARRISON = "$Q/c62af512-3362-4f5f-913a-4e0b14226083.png"
    private const val IMG_SAN_CARLOS = "$Q/5555806c-d983-48d3-a8fd-2296c4805592.png"
    private const val IMG_SANDSTONE = "$Q/d39d5d6c-1808-4cac-bc3f-a2a9f856ec4f.png"
    private const val IMG_SARDONYX = "$Q/45f44705-4de4-487c-bb3c-2a419abe07cf.png"
    private const val IMG_SCAPHITES = "$Q/ad9ead04-0389-4041-b58e-6f6bcc74d6f8.png"
    private const val IMG_SCHEELITE = "$Q/10837d6b-644e-46ba-a2f2-a030c37ed5ed.png"
    private const val IMG_SCHIST = "$Q/0052ed16-d488-4755-8ab1-236eeb6ea8ac.png"
    private const val IMG_SCORIA = "$Q/e4c0b9f1-d2d7-407f-bf78-8e1334404cc5.png"
    private const val IMG_SEA_URCHIN_FOSSIL = "$Q/c4c6aa7c-22a0-4043-9277-cd9564a3bb9f.png"
    private const val IMG_SEPIOLITE = "$Q/f6223518-40ae-4351-b7f1-55b0bb413bd4.png"
    private const val IMG_SERPENTINE = "$Q/a6766f56-1280-4ba8-813d-2b3cb820529d.png"
    private const val IMG_SHALE = "$Q/9bfb5ef0-a732-4291-bc15-a7802c31e43f.png"
    private const val IMG_SHATTUCKITE = "$Q/a789ec2a-fca1-4f1d-9f56-dd38a273acb1.png"
    private const val IMG_SHEEP_CREEK = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_SHORTITE = "$Q/540ad293-4608-4834-96c8-e698cb78898e.png"
    private const val IMG_SIDERITE = "$Q/3a8c8f5b-c6c5-4de6-bcd3-71fd4d12ce4c.png"
    private const val IMG_SILLIMANITE = "$Q/1e1b3ee6-f313-42ed-85c9-13d205cea67f.png"
    private const val IMG_SILTSTONE = "$Q/a5d920ff-e28d-4cf6-ab45-3b6d51a54275.png"
    private const val IMG_SKARN = "$Q/5937afaf-c3fa-4112-bd53-ae753b3dead7.png"
    private const val IMG_SLATE = "$Q/4ea2850c-d2d6-4011-9178-a426d9dd9c76.png"
    private const val IMG_SLATE_GREEN = "$Q/62ad1c9a-aab0-41b1-bc52-c41fcd6408b2.png"
    private const val IMG_SLATE_PURPLE = "$Q/ff650cd9-128f-402b-a4ee-c8d6b6417d1d.png"
    private const val IMG_SMILODON_TOOTH = "$Q/ae3dcd81-a03f-4868-981c-4946a2815271.png"
    private const val IMG_SMITHSONITE = "$Q/64fa5f29-6816-44a4-aa8a-981a02983052.png"
    private const val IMG_SMOKY_QUARTZ = "$Q/b906aa39-80c8-4da9-8f36-cf9ab5ad5cd2.png"
    private const val IMG_SOAPSTONE = "$Q/da2a933f-9f9b-4612-8c08-c3e5a92454e6.png"
    private const val IMG_SODALITE = "$Q/256de881-97b4-4984-ae17-e942d82cc9f6.png"
    private const val IMG_SPECTROLITE = "$Q/a753ae0d-1c1a-4625-973f-8b282403deb8.png"
    private const val IMG_SPENCER_OPAL = "$Q/9afc10aa-00e3-4b76-9962-2a044af38854.png"
    private const val IMG_SPESSARTINE = "$Q/683e1492-f79e-490e-8315-eede852a5ffa.png"
    private const val IMG_SPHALERITE_BROWN = "$Q/87a58b96-8317-4035-83a2-978419cc10fa.png"
    private const val IMG_SPHALERITE_HONEY = "$Q/6f67f807-a30b-4b4c-b8ba-1a49a3499638.png"
    private const val IMG_SPHALERITE_RUBY = "$Q/be7040b0-a473-4ecd-9f6e-b98ba4762b30.png"
    private const val IMG_SPHALERITE_GREEN = "$Q/13fa1ef1-55b0-4868-9c20-38b74a7c5828.png"
    private const val IMG_SPHALERITE = "$Q/c70379bb-8439-432f-a910-45cdfb6e7034.png"
    private const val IMG_SPHENE = "$Q/a9177418-56dc-4ea2-9748-14f4f98f7610.png"
    private const val IMG_SPINEL = "$Q/c3e8853e-16f7-4df2-b1e1-607da74a1027.png"
    private const val IMG_SPIRIFER = "$Q/f089d195-1355-4796-a32e-6617be94e34b.png"
    private const val IMG_STARFISH_FOSSIL = "$Q/00474a1e-f5ce-41a5-bd5d-c388cbdc0f1c.png"
    private const val IMG_STAUROLITE = "$Q/29081792-e0e1-43ae-a960-a2d6ab4279f4.png"
    private const val IMG_STERLING_HILL = "$Q/95a9e35b-30d9-4522-82e2-1415c612dbc7.png"
    private const val IMG_STEWART_MINE = "$Q/b8a1fdf6-758b-4739-8983-0c91f93eb352.png"
    private const val IMG_STIBNITE = "$Q/38c145b2-f373-4e61-a426-971f44e04e92.png"
    private const val IMG_STILBITE = "$Q/247321a4-e2a1-4130-83a6-1e14b3dcfbdc.png"
    private const val IMG_STINGRAY_BARB = "$Q/4d0831ee-5759-45a5-8f26-7833c972fd98.png"
    private const val IMG_STROMATOLITE = "$Q/99af6f03-0f2a-4808-a916-437e0af21acc.png"
    private const val IMG_STROMATOPOROID = "$Q/bf3be01c-e531-47f5-96b0-5c073e4d411c.png"
    private const val IMG_STRONTIANITE = "$Q/fe08114a-ad42-4911-8c8b-e84fcb2d8e63.png"
    private const val IMG_SUGILITE = "$Q/f9fb3608-43ec-4af6-b4d7-07a496dbf35b.png"
    private const val IMG_SUNSTONE = "$Q/1c698dac-df2d-48d2-8eaf-c3260119d3bf.png"
    private const val IMG_SUNSTONE_PLUSH = "$Q/1c698dac-df2d-48d2-8eaf-c3260119d3bf.png"
    private const val IMG_SWEET_HOME = "$Q/5bfb181d-cbeb-47dc-835e-f29791000ee0.png"
    private const val IMG_SYLVITE = "$Q/2e7192b6-77cc-4d11-ad18-9be23e279d43.png"
    private const val IMG_TAAFFEITE = "$Q/6936b717-dad0-4093-af2f-4a29b75e31b6.png"
    private const val IMG_TALC = "$Q/f7891ce8-a582-426f-bb78-1ecfaa988d58.png"
    private const val IMG_TANZANITE = "$Q/9de4590f-507a-42bd-965f-64ebe4c8c283.png"
    private const val IMG_TERLINGUA = "$Q/b9aef18c-fb66-483a-bb74-547ceca0a37e.png"
    private const val IMG_THOMSONITE = "$Q/d3e484dd-52ed-411c-b8b8-710ec02c7026.png"
    private const val IMG_TOPAZ_BLUE = "$Q/5dff7cc5-b15f-4843-96ea-b3aa228573f5.png"
    private const val IMG_TOPAZ_PINK = "$Q/2560ac47-049c-41cf-a2f3-779083bee5ab.png"
    private const val IMG_TOPAZ_COLORLESS = "$Q/eba05b49-f90a-49aa-a2a5-b4529ee3da8e.png"
    private const val IMG_TOPAZ_IMPERIAL = "$Q/20b8ebbc-6c41-469d-8759-522d803c5fbd.png"
    private const val IMG_TOPAZ = "$Q/6fd95ed3-adf1-45d4-a815-adcb3dda754b.png"
    private const val IMG_TOPAZ_MOUNTAIN = "$Q/6fd95ed3-adf1-45d4-a815-adcb3dda754b.png"
    private const val IMG_TORBERNITE = "$Q/10626e4e-2909-49b2-b33a-048c858c564b.png"
    private const val IMG_TOURMALINATED_QUARTZ = "$Q/942276ab-94ee-4719-aaa8-bce0803a92c9.png"
    private const val IMG_TOURMALINE_GREEN = "$Q/e9b46845-4667-4f79-af66-9c9870a87d4d.png"
    private const val IMG_TOURMALINE_BLUE = "$Q/5d178512-a6f6-49e2-96ac-44a3f158ad75.png"
    private const val IMG_TOURMALINE_BLACK = "$Q/f1366f15-1efe-4a6b-80a4-5263c93fdfad.png"
    private const val IMG_TOURMALINE_WATERMELON = "$Q/9d3c4095-1bb4-4e64-9196-f1af85d04e7b.png"
    private const val IMG_TOURMALINE = "$Q/b8a1fdf6-758b-4739-8983-0c91f93eb352.png"
    private const val IMG_TOURMALINE_QUEEN = "$Q/b8a1fdf6-758b-4739-8983-0c91f93eb352.png"
    private const val IMG_TRAVERTINE = "$Q/fde63316-61dd-4c91-901e-efd94cbbd1a6.png"
    private const val IMG_TREMOLITE = "$Q/ea9681c3-91c3-4a97-ab8a-cd76a2b31b64.png"
    private const val IMG_TRILOBITE = "$Q/ae6e4244-7c56-401f-be8d-6a5ff643ce4a.png"
    private const val IMG_TRINITY_AGATES = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_TUCSON_MINERAL_DEALERS = "$Q/08135330-34fa-49d2-9350-c57b42c7faa2.png"
    private const val IMG_TUFF = "$Q/41fb2961-26e1-48fe-899e-0784e8461545.png"
    private const val IMG_TURQUOISE = "$Q/000cf579-8174-4073-b4b8-fbe78fd7aa1d.png"
    private const val IMG_TURQUOISE_NEVADA_ROUGH = "$Q/198bcb8e-6503-4ce1-b050-48af53d3fc2b.png"
    private const val IMG_TURQUOISE_NEVADA_WILD = "$Q/84f914d2-e930-420a-be3f-1cb90b3f928c.png"
    private const val IMG_TURQUOISE_GREEN_BLUE = "$Q/445d2fe7-f064-4b7a-911e-977aad8a15df.png"
    private const val IMG_TURQUOISE_APPLE_GREEN = "$Q/b0735c7a-269c-401e-8d52-02c5c4d0d4cb.png"
    private const val IMG_TURRITELLA = "$Q/caba45f8-57d6-4d88-8b0c-12c2c8e3c84e.png"
    private const val IMG_ULEXITE = "$Q/cc1da64e-f3c4-42ba-b7ed-019945f428a1.png"
    private const val IMG_UVAROVITE = "$Q/52af9f95-b12c-4a63-8127-73477674b72d.png"
    private const val IMG_V_ROCK_SHOP = "$Q/35d2da4f-6205-4eb2-a4ad-0217c15fc3c6.png"
    private const val IMG_VANADINITE_NEW = "$Q/8eaac436-7be6-4fce-bbc0-08a379d31a89.png"
    private const val IMG_VARISCITE = "$Q/14709319-dbc8-4f78-b942-465dc8af6842.png"
    private const val IMG_VERDELITE = "$Q/eff7bd14-2a95-4afa-a251-9b041642bc3c.png"
    private const val IMG_VESUVIANITE = "$Q/a668cc94-30d0-4b6a-a4fb-29050082ec28.png"
    private const val IMG_VIVIANITE = "$Q/f62652e4-594c-467a-be94-06e936b0975a.png"
    private const val IMG_WACKE = "$Q/af4916ae-42c6-4f0b-a5a0-4cc11c0e33b4.png"
    private const val IMG_WAVELLITE = "https://r2-pub.rork.com/web-fetch-images/c3ae5de17f1376315cb34212c836634af3b244e4c3e8ee29cb90f6c9760b399c.jpeg"
    private const val IMG_WEGNER_QUARTZ = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_WHITEFISH_YOOPER = "$Q/aeee0cb3-a8fd-438a-8266-a0fd28833b06.png"
    private const val IMG_WILLEMITE = "$Q/4418575a-e42d-47a4-94a7-4fca662fb4e3.png"
    private const val IMG_WILLIAMS_QUARRY = "$Q/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png"
    private const val IMG_WITHERITE = "$Q/eb297c50-4ec4-45fe-8f62-419b23222b4b.png"
    private const val IMG_WOLFRAMITE = "$Q/c3bd75db-17d5-469a-8698-6a56154f97c9.png"
    private const val IMG_WULFENITE_NEW = "$Q/1c43f7ad-e2ef-4478-ad0a-460dd6203d4b.png"
    private const val IMG_XIPHACTINUS = "$Q/19aa79d8-6321-420a-8923-1ec442f15066.png"
    private const val IMG_ZIRCON = "$Q/8e655226-84ef-489e-961d-ede766c9228e.png"
    private const val IMG_ZOISITE = "$Q/ee8b16cf-72b9-4539-b508-0c03409c6fed.png"
    private const val IMG_ZULTANITE = "$Q/50e10a4d-5002-4444-8fcc-47d1c5c67474.png"
    private const val IMG_ZUNYITE = "$Q/f3898f0a-fbe8-4f90-84c3-482ffc64d3e9.png"
    // ── NEW: Opal, Agate & Obsidian varieties ──
    private const val IMG_BLACK_OPAL = "$Q/8d23cfb0-e35f-43c7-9336-7b5fd3e25f9d.png"
    private const val IMG_BOULDER_OPAL = "$Q/16b3434c-aa85-4c4d-8121-ef369494b35c.png"
    private const val IMG_MATRIX_OPAL = "$Q/0f849dfd-e348-4914-b9aa-5aa507eafa53.png"
    private const val IMG_BLUE_OPAL = "$Q/0031cbf8-cc85-43f0-a57a-600dd687beff.png"
    private const val IMG_WHITE_OPAL = "$Q/fac03024-177a-434d-8f88-6e89a9ead5bc.png"
    private const val IMG_CRYSTAL_OPAL = "$Q/3782dfc1-6423-4bba-8f38-9b919fdc87da.png"
    private const val IMG_ETHIOPIAN_OPAL = "$Q/55af257e-212c-49a4-8c4b-4266905d8b08.png"
    private const val IMG_BOTSWANA_AGATE = "$Q/14e56f3b-29a6-4b4d-b6bf-8034b6e9442b.png"
    private const val IMG_LAGUNA_AGATE = "$Q/f99bf492-9dfe-475d-8e98-b90cde5ac847.png"
    private const val IMG_CRAZY_LACE_AGATE = "$Q/f7c67027-529f-4675-b0d0-2d7aa74f8b7b.png"
    private const val IMG_CONDOR_AGATE = "$Q/dc864324-d16c-49df-83fd-895f4f6423da.png"
    private const val IMG_FORTIFICATION_AGATE = "$Q/70d120f9-68e8-4b98-8b4d-c1a4774e4abc.png"
    private const val IMG_SNOWFLAKE_OBSIDIAN = "$Q/7704bd5c-a6c0-45ba-a7cf-1fd7eb22cea6.png"
    private const val IMG_ELECTRIC_BLUE_OBSIDIAN = "$Q/55fa038c-cbc0-4305-bd45-40e49278598f.png"
    private const val IMG_SILVER_SHEEN_OBSIDIAN = "$Q/bd422f4b-4f09-4b05-a116-f50fcd12b721.png"
    private const val IMG_GOLDEN_SHEEN_OBSIDIAN = "$Q/16f4c33c-f318-4408-bf0e-393b83465c27.png"
    private const val IMG_MIDNIGHT_LACE_OBSIDIAN = "$Q/15d573d5-dcf5-42de-961a-e7b0a87b83b3.png"

    // ── Alabaster (gypsum variety) — Wikipedia/mindat reference, AI-generated ──
    private const val IMG_ALABASTER_ROUGH = "$Q/82c87b34-b2f3-48ab-9e69-3e48d8685631.png"
    private const val IMG_ALABASTER_WILD = "$Q/bb5c27ed-e2b9-4560-9daa-5bc726d972e5.png"
    private const val IMG_ALABASTER_MUSEUM = "$Q/7567b66d-56a0-4807-ad82-b190c984efb4.png"

    // ── NEW: Fossil soup, pearls, grape agate, fordite ──
    private const val IMG_FOSSIL_SOUP = "$Q/d4127405-a3c5-46c2-a5e2-bbb9cba02aba.png"
    private const val IMG_NATURAL_PEARLS = "$Q/28d0926e-1b79-469d-b5d6-56c55758cd45.png"
    private const val IMG_GRAPE_AGATE = "$Q/eda52832-f064-444b-82ba-f747f1358ea5.png"
    private const val IMG_FORDITE = "$Q/d74897a4-2d27-46af-93aa-a60287903ad2.png"

    // ── Petrified wood varieties ──
    private const val IMG_PETRIFIED_BLUE_FOREST = "$Q/babc5350-aca5-4a1d-892e-e8359d5ff16d.png"
    private const val IMG_PETRIFIED_RAINBOW = "$Q/abebfc9b-21b4-454c-b83e-f98ce333bf84.png"
    private const val IMG_PETRIFIED_ARIZONA = "$Q/d4286bc7-9205-4d73-836d-477f1d267fb5.png"
    private const val IMG_PETRIFIED_OREGON = "$Q/b41916c1-bf2c-49f3-9032-3d28515a6bf5.png"
    private const val IMG_PETRIFIED_OPALIZED = "$Q/ee20a7c5-91dd-4784-8789-35bc4a2cd617.png"

    // ── NEW mineral assemblage photos ──
    private const val IMG_AMAZONITE_SMOKY_QUARTZ = "$Q/6249a68c-94f4-4b90-82db-0434741d21af.png"
    private const val IMG_AMAZONITE_SMOKY_QUARTZ_ROUGH = "$Q/43822197-bd69-47eb-a36e-3c57d8782c1b.png"
    private const val IMG_AMAZONITE_SMOKY_QUARTZ_MUSEUM = "$Q/08d7c1d2-d18b-4773-ac19-4deb60a4beac.png"
    private const val IMG_AMAZONITE_SMOKY_QUARTZ_CAB = "$Q/79ac579f-57f8-42a6-b8f3-5504ea479e6d.png"
    private const val IMG_FLUORITE_PYRITE_GALENA = "$Q/6c65143c-7642-4e20-8d21-3f57d35a164e.png"
    private const val IMG_QUARTZ_CHALCOPYRITE = "$Q/ec12a0fe-b373-431e-afab-bd44d52efa42.png"
    private const val IMG_AMETHYST_CALCITE = "$Q/631a9fd5-70fa-4aac-a329-a464a5436117.png"
    private const val IMG_GOLD_QUARTZ = "$Q/2a92b451-8b7f-417f-91c6-ff49106a0b3c.png"
    private const val IMG_NATIVE_SILVER_SPEC = "$Q/becb1c4a-eb15-42c8-b5b9-39d925e07362.png"
    private const val IMG_BASALT_COPPER_CALCITE = "$Q/700070d9-50bb-4a37-a1d9-350b91d4af5a.png"
    private const val IMG_CHLORASTROLITE_BASALT = "$Q/710d2f0e-4fd7-4153-a525-4ced0b1ff768.png"

    // ── Alive organism reconstructions ──
    private const val IMG_ALIVE_TREX = "$Q/e6bf052f-52e0-46f0-bf17-e0d9cc85842a.png"
    private const val IMG_ALIVE_TRICERATOPS = "$Q/128685a4-59c0-4cdb-950b-48894b64a4f7.png"
    private const val IMG_ALIVE_BRACHIOSAURUS = "$Q/8ee0094a-f302-4d71-bcbb-f1292c785e8f.png"
    private const val IMG_ALIVE_VELOCIRAPTOR = "$Q/ece983e7-76d3-4c9b-b903-5121a7eaff77.png"
    private const val IMG_ALIVE_SPINOSAURUS = "$Q/3cb5547a-53d2-4da7-832b-4f3a45df2046.png"
    private const val IMG_ALIVE_ANKYLOSAURUS = "$Q/a8938a8c-e343-45f9-8cce-e5c3ec462274.png"
    private const val IMG_ALIVE_ALLOSAURUS = "$Q/e466d1b0-9a82-4d2b-8e94-b5de4efa770f.png"
    private const val IMG_ALIVE_DIPLODOCUS = "$Q/5be0ab62-8dda-42c4-8278-d19e0fd8110c.png"
    private const val IMG_ALIVE_PTERANODON = "$Q/0fd7b618-e3dd-48e4-a4f9-13168a1e81f2.png"
    private const val IMG_ALIVE_QUETZALCOATLUS = "$Q/272ea438-f584-4ad1-ba49-b99b12fc49dd.png"
    private const val IMG_ALIVE_COMPSOGNATHUS = "$Q/c5292091-919f-440e-bbb8-101d9d896f00.png"
    private const val IMG_ALIVE_DEINONYCHUS = "$Q/cb0caac8-95b6-4b0e-9fcb-b39cb6b31c9f.png"
    private const val IMG_ALIVE_CARNOTAURUS = "$Q/b0733e6a-c878-4324-9b3a-96dc10b00afb.png"
    private const val IMG_ALIVE_THERIZINOSAURUS = "$Q/f5048a46-3bf1-490c-8ba0-7b66ab787f8a.png"
    private const val IMG_ALIVE_GIGANOTOSAURUS = "$Q/c9666c4b-92c8-4b8e-a8b1-c8597d7e2fc9.png"
    private const val IMG_ALIVE_COELOPHYSIS = "$Q/0c0cd437-b2e6-4f02-9df5-373a61c0c787.png"
    private const val IMG_ALIVE_DIMORPHODON = "$Q/2f91ffb0-68fa-4380-9df7-caacd67d0379.png"
    private const val IMG_ALIVE_ICHTHYOSAURUS = "$Q/5fcdff76-4a25-486e-a97f-cc10023e6df5.png"
    private const val IMG_ALIVE_PLESIOSAURUS = "$Q/87379a88-2400-4d56-9d98-d246ae2c355c.png"
    private const val IMG_ALIVE_ELASMOSAURUS = "$Q/3bc075c1-8db6-4739-a6f7-f08ecfe2f3b9.png"
    private const val IMG_ALIVE_LIOPLEURODON = "$Q/f2938a97-a095-466e-862f-2f73c91c79e8.png"
    private const val IMG_ALIVE_WOOLLY_MAMMOTH = "$Q/729ae503-2abe-4720-86b0-3dcf96b924d5.png"
    private const val IMG_ALIVE_SMILODON = "$Q/d692831f-6e06-44a3-85d5-6c8e02556a30.png"
    private const val IMG_ALIVE_WOOLLY_RHINO = "$Q/48a003b0-12df-4d02-9eba-11370865e14c.png"
    private const val IMG_ALIVE_MEGALOCEROS = "$Q/c5375c9a-b3ce-43fa-9bdf-81821d73b749.png"
    private const val IMG_ALIVE_PARACERATHERIUM = "$Q/915f76eb-f3ae-4bce-9c8a-17c551612f31.png"
    private const val IMG_ALIVE_BASILOSAURUS = "$Q/15d99fa2-4f9e-4101-adb0-9ec152c6c50a.png"
    private const val IMG_ALIVE_ENTELODONT = "$Q/d4688c23-8fe3-45f2-b26f-c39b6bebb685.png"
    private const val IMG_ALIVE_THYLACINE = "$Q/38f02904-6b57-440b-baa2-0fd21baa83ea.png"
    private const val IMG_ALIVE_MEGALANIA = "$Q/f3240a8b-492e-44fa-8be9-289e5d61b163.png"
    private const val IMG_ALIVE_GASTORNIS = "$Q/745baebe-6d19-421a-af50-60294b6d15e5.png"
    private const val IMG_ALIVE_PHORUSRHACOS = "$Q/86343a62-33df-4e5b-aeb6-556dac1efe3d.png"
    private const val IMG_ALIVE_ARGENTAVIS = "$Q/a6177c8e-de30-47dd-ae9d-0094100c7af1.png"
    private const val IMG_ALIVE_DODO = "$Q/77822072-ecd9-445d-ad9a-0f3c49002bdf.png"
    private const val IMG_ALIVE_MOA = "$Q/26ef55e0-66c5-4f7a-a1b2-2ceb9e931e28.png"
    private const val IMG_ALIVE_MEGANEURA = "$Q/715af849-4535-431f-abf6-ee33b1b1496f.png"
    private const val IMG_ALIVE_ARTHROPLEURA = "$Q/0112c467-10df-456e-a964-95e03c5c6d18.png"
    private const val IMG_ALIVE_PULMONOSCORPIUS = "$Q/0bfa5b08-f6a7-411e-b00f-29b0896c696c.png"
    private const val IMG_ALIVE_MEGALODON = "$Q/a7209213-5043-4a5f-85b6-cdd5f5ab2fe0.png"
    private const val IMG_ALIVE_HELICOPRION = "$Q/f67f3005-a05b-4779-b99d-301469788c5c.png"
    private const val IMG_ALIVE_LEEDSICHTHYS = "$Q/cd245eae-0f44-4a9c-b52c-457977376580.png"
    private const val IMG_ALIVE_ERYOPS = "$Q/0ff03818-f65c-4bdd-a021-6b271ca7d40f.png"
    private const val IMG_ALIVE_DIPLOCAULUS = "$Q/958e2ddf-f972-4da9-b838-6eec7d05625f.png"
    private const val IMG_ALIVE_LEPIDODENDRON = "$Q/7d339bb3-b349-45c3-aa10-8172e678b03d.png"
    private const val IMG_ALIVE_SIGILLARIA = "$Q/ab5a3a11-4937-4a96-a8fc-a89527a80d9b.png"
    private const val IMG_ALIVE_CORDAITES = "$Q/5c7ef9d0-261f-44e2-be79-1cdead364db0.png"
    private const val IMG_ALIVE_GLOSSOPTERIS = "$Q/ec14704a-5e3b-4a50-a124-7876fd82a282.png"
    private const val IMG_ALIVE_GINKGO = "$Q/15077240-28c9-44e2-aec9-24470b528499.png"
    private const val IMG_ALIVE_FORAMINIFERA = "$Q/36bb2ee1-4acc-48c9-8fd2-99600d325974.png"
    private const val IMG_ALIVE_RADIOLARIA = "$Q/a2d030aa-c048-4a0e-9b30-138021610aba.png"
    private const val IMG_ALIVE_STROMATOLITE = "$Q/03ccaf85-fe04-4ada-b9f9-e5f74c89fe3a.png"
    private const val IMG_ALIVE_COCCOLITHOPHORE = "$Q/838a3864-76ec-41e1-80be-70a109f78541.png"

    // ── 6 pending fossil photos ──
    private const val IMG_DEINOTHERIUM = "$Q/aea2a45b-bb5c-43b8-acdc-0f6527297d8b.png"
    private const val IMG_ANDREWSARCHUS = "$Q/973ec744-3dc6-415f-8001-a7e3f9a6af81.png"
    private const val IMG_GASTORNIS = "$Q/f6786b74-6d6f-4d2e-99a4-dc91c40df1f2.png"
    private const val IMG_PLATYBELODON = "$Q/de326384-ccfc-4912-b61a-001663d55d1d.png"
    private const val IMG_MAMMOTH_TUSK = "$Q/42766de4-18e6-4b76-9327-9c9d45d233b3.png"
    private const val IMG_MEGALOCEROS = "$Q/0469b86f-8f29-49e2-94cc-b75563fa236b.png"

    // ── Yooperlite photos ──
    private const val IMG_YOOPERLITE_NORMAL = "$Q/65488726-6ef5-48a2-ad9b-a68be4257f9b.png"
    private const val IMG_YOOPERLITE_NORMAL_2 = "$Q/3e1b258f-867d-4998-9161-a31bf211d7d7.png"
    private const val IMG_YOOPERLITE_UV = "$Q/396b2ffc-cecc-47dc-b869-61099cfa0597.png"
    private const val IMG_YOOPERLITE_GRAND_MARAIS = "$Q/fd29cc56-eb2e-4c62-a3b5-e5607e49a6cd.png"
    private const val IMG_YOOPERLITE_MCLAIN = "$Q/07f8c902-807a-4dcf-82c9-ac7c63145782.png"
    private const val IMG_YOOPERLITE_MUNISING = "$Q/22b927bb-a89f-4aae-bf5d-66ffbe796712.png"
    private const val IMG_YOOPERLITE_MUSKALLONGE = "$Q/f0a08c88-b77e-4e8b-8d88-8c4c81786988.png"
    private const val IMG_YOOPERLITE_WHITEFISH = "$Q/31766adb-7672-4d0d-8645-f99f09aefcad.png"

    // ── Meteorite photos ──
    private const val IMG_IRON_METEORITE_FIELD = "$Q/3dad8e52-c966-496c-abe9-ade8c6a3f59f.png"
    private const val IMG_IRON_METEORITE_CROSS = "$Q/3f5c5031-bea6-48b3-8b02-4179b201edde.png"
    private const val IMG_HED_ACHONDRITE_ROUGH = "$Q/e5dc9a0f-888f-48ee-ae52-340feaa56d57.png"
    private const val IMG_WILLAMETTE_METEORITE = "$Q/3dad8e52-c966-496c-abe9-ade8c6a3f59f.png"
    private const val IMG_CHONDRITE_FIELD = "$Q/62c91829-1ca3-4961-88b2-f747c72608b6.png"
    private const val IMG_CHONDRITE_CROSS = "$Q/b2f95efd-739a-4f4d-911d-72007c29b08c.png"
    private const val IMG_PALLASITE_FIELD = "$Q/75d7cdc0-1877-464d-bcbf-1ea636c237e5.png"
    private const val IMG_PALLASITE_CROSS = "$Q/22a99a9e-3cd2-423e-8eeb-f1519095c891.png"
    private const val IMG_ACHONDRITE = "$Q/2d35cc52-6fbd-45b2-a0e1-7d41d552571c.png"
    private const val IMG_CARBONACEOUS_CROSS = "$Q/be190401-82e5-40f8-8787-e9157e736462.png"
    private const val IMG_MESOSIDERITE = "$Q/dd4f726b-e035-48f9-962e-8bf10fd6b08d.png"
    private const val IMG_TEKTITE = "$Q/c0d41f4d-77a0-4863-8905-f3c61d25fd3c.png"

    // ── Mineral assemblage photos ──
    private const val IMG_AZURITE_MALACHITE = "$Q/8f814acf-1dc0-4525-8de9-4e9ec1292828.png"
    private const val IMG_AMYGDALOIDAL_BASALT = "$Q/c9aff469-dc9b-411d-a6c8-7b95a17bcc6d.png"
    private const val IMG_CHRYSOCOLLA_AZURITE_MALACHITE = "$Q/1de80aa7-d2c1-428a-9e48-777d53941585.png"
    private const val IMG_RUBY_ZOISITE = "$Q/417e2bf7-4729-4213-ba43-683bb01ee3a4.png"
    private const val IMG_TOURMALINE_QUARTZ_FELDSPAR = "$Q/01830082-41a9-4f52-bb3f-51e0618b4db6.png"
    private const val IMG_GALENA_SPHALERITE_PYRITE = "$Q/ca86edda-156a-4f0e-bb52-2487f638600d.png"
    private const val IMG_BANDED_IRON_FORMATION_NEW = "$Q/8f5976f8-26a8-44be-bb1c-6e156d154556.png"

    // ── In-the-wild photos ──
    private const val IMG_QUARTZ_WILD = "$Q/e9b59a4e-b5ac-4ca8-956a-9790ca2c556f.png"
    private const val IMG_GEODE_WILD = "$Q/6549b03b-f04f-4f66-a92c-1771c1cc3de3.png"
    private const val IMG_GALENA_WILD = "$Q/92fbbea1-beab-4495-a6d2-4aa1a84c0713.png"
    private const val IMG_FLUORITE_WILD = "$Q/8da89daa-5b79-4b48-b41f-213102d769aa.png"
    private const val IMG_GARNET_WILD = "$Q/431920fa-021d-4d5d-b04f-22d7101f5ee2.png"
    private const val IMG_PETOSKEY_WILD = "$Q/befac1cf-0283-4703-8a36-2785ad9b2deb.png"
    private const val IMG_PETOSKEY_BEACH = "$Q/b4868921-bfdb-4ccc-aab6-3f25df931f00.png"

    // ── NEW: Massive expansion batch (~144 minerals, rocks, fossils) ──
    private const val IMG_BIOTITE = "$Q/53eb17f9-0103-4b31-b7f8-46494bde35fd.png"
    private const val IMG_HORNBLENDE = "$Q/a5873e1c-5ffb-421c-abe9-fcf47e5dd7b8.png"
    private const val IMG_OLIVINE = "$Q/5bd21be8-f3c4-42f6-8286-e3e33b5a82c1.png"
    private const val IMG_CHLORITE = "$Q/7e638ce9-b6cc-4462-95ab-ef19ed4fc003.png"
    private const val IMG_ACTINOLITE = "$Q/44636288-975e-47bb-9400-b65723023623.png"
    private const val IMG_WOLLASTONITE = "$Q/7d12451b-4309-446e-89e1-010a49815a93.png"
    private const val IMG_KAOLINITE = "$Q/181349b5-5742-4c63-8dd8-6702ac205ae5.png"
    private const val IMG_ALBITE = "$Q/fde67aef-0308-4eb4-b9ae-54dfd06e17ff.png"
    private const val IMG_NEPHELINE = "$Q/e21d8569-3276-4500-bf86-8c535222be79.png"
    private const val IMG_LEUCITE = "$Q/5af2befa-5c52-411a-b8aa-b801e94195d1.png"
    private const val IMG_AEGIRINE = "$Q/8a839f3c-6182-40bd-8b9c-be95f61f8a51.png"
    private const val IMG_CANCRINITE = "$Q/1efc533d-d7c9-4c90-aaa3-46d26c3b68c0.png"
    private const val IMG_GLAUCONITE = "$Q/95494a5a-0d07-4e8b-9e1d-fea3da6308d4.png"
    private const val IMG_MONTMORILLONITE = "$Q/32d79f1d-3e49-4b09-b275-7fc05edcd230.png"
    private const val IMG_FORSTERITE = "$Q/fc6677b5-85fd-4bd0-9fb1-e063a6969aea.png"
    private const val IMG_FAYALITE = "$Q/0e0c7029-9a5c-4965-8cd9-b118c85eee40.png"
    private const val IMG_HEMIMORPHITE = "$Q/00f0ed98-3f80-4bd6-8914-9b623ad11421.png"
    private const val IMG_FRANKLINITE = "$Q/e28ad02c-395f-49f2-80f1-66cf3abae8ba.png"
    private const val IMG_ILMENITE = "$Q/71eaedb8-d7d1-4257-b676-fcc25e6cdec3.png"
    private const val IMG_MARCASITE = "$Q/ac9df454-d3bc-4e39-8511-05a23e413093.png"
    private const val IMG_MILLERITE = "$Q/72217383-db73-47f2-b09f-71f4472fd52e.png"
    private const val IMG_COVELLITE = "$Q/e8ecebfb-1ce0-4da0-b37c-81adad5a3eea.png"
    private const val IMG_CHALCOCITE = "$Q/dfcc827c-3da8-4151-b076-1a6ff34453a4.png"
    private const val IMG_HALITE = "$Q/55f36dbc-7d44-4296-be82-b1cfc1e03e99.png"
    private const val IMG_HOWLITE = "$Q/f2467106-46cf-44cb-a155-6ba3df397b11.png"
    private const val IMG_BORAX = "$Q/86fbfbf5-87c2-496b-891a-e3b456332066.png"
    private const val IMG_KERNITE = "$Q/b4e4e30a-bfe7-4287-866f-f717e9cc37b3.png"
    private const val IMG_CARNALLITE = "$Q/1ea2df9e-f4e1-4ce4-9ffc-c28c7fb10cef.png"
    private const val IMG_LIMONITE = "$Q/85c64e39-398c-467b-8b74-c103090612bd.png"
    private const val IMG_URANINITE = "$Q/0680df36-3265-4ddd-ab39-baa79a4280a6.png"
    private const val IMG_MONAZITE = "$Q/637ec657-7a99-455d-b5ea-f4ef8e4fcdcf.png"
    private const val IMG_BASTNASITE = "$Q/92be161a-9e02-4f85-8b40-4e08402a5e92.png"
    private const val IMG_EUDIALYTE = "$Q/d8d51998-f8b5-4d8f-bcd9-82f4f101fb07.png"
    private const val IMG_ALLANITE = "$Q/8f364cb0-5e72-43ff-a7e2-55426a491d1e.png"
    private const val IMG_ILVAITE = "$Q/c4477dc8-fc6a-4df0-8b35-567fc4e1e4d1.png"
    private const val IMG_AURICHALCITE = "$Q/8d598525-8669-4195-9c85-fb0cdc2885d3.png"
    private const val IMG_BOURNONITE = "$Q/2cc656c0-2c87-4448-848f-5d1671c90e99.png"
    private const val IMG_CHALCOPHYLLITE = "$Q/eff03805-9878-4899-8ab5-f4eee767884c.png"
    private const val IMG_CUBANITE = "$Q/50b981bd-99b8-4121-9124-9963a5779bba.png"
    private const val IMG_ENARGITE = "$Q/67448d99-446c-4d71-847d-f99d54c5a42f.png"
    private const val IMG_EUXENITE = "$Q/94aea7ef-c921-4ff3-a10a-d823bccc776c.png"
    private const val IMG_GREENOCKITE = "$Q/a1525898-53a3-414f-9815-3f0524e64678.png"
    private const val IMG_HANKSITE = "$Q/f3369e3e-c1f2-48b8-bf0e-a6cad7dc4da8.png"
    private const val IMG_JAMESONITE = "$Q/ddaefe81-effc-4d90-890c-66e732d775d3.png"
    private const val IMG_JAROSITE = "$Q/5a45b6fb-01a9-4f93-8c31-450a0851fb24.png"
    private const val IMG_LANGBEINITE = "$Q/458e135c-479a-410a-9367-7d0cd32f5c47.png"
    private const val IMG_LAUMONTITE = "$Q/ad2e0eb0-b9ea-49a4-880d-d92a3ce73f1b.png"
    private const val IMG_LAWSONITE = "$Q/73bc12fc-ba59-479b-bbb0-69c9ab49abb2.png"
    private const val IMG_PYROPHYLLITE = "$Q/1b138467-563c-432b-a24d-0de36b32b2dd.png"
    private const val IMG_SERANDITE = "$Q/06f04e87-03f1-4417-8aa5-0185797638fc.png"
    private const val IMG_ROSASITE = "$Q/f7b94caf-f5b8-49a7-8810-52f884ccaf84.png"
    private const val IMG_LEGRANDITE = "$Q/be22d7bc-0b66-4312-9455-924c75422a82.png"
    private const val IMG_LUDLAMITE = "$Q/c813647e-53ea-42ed-9ae5-a248c8087ae8.png"
    private const val IMG_MELLITE = "$Q/6cb881e1-fccb-44a0-ae47-9cecd62bfb54.png"
    private const val IMG_BAUXITE = "$Q/35c57909-fdf1-4a20-829e-9b96b20ca7b9.png"
    private const val IMG_NATIVE_ARSENIC = "$Q/35b0ba5f-d711-41e6-9462-4dff676a8a58.png"
    private const val IMG_BISMUTHINITE = "$Q/bde4d882-efc5-4986-92e5-638bd40fca59.png"
    private const val IMG_CALAVERITE = "$Q/715d7aaa-0445-4da6-9a0e-c193f2baebae.png"
    private const val IMG_COLUMBITE = "$Q/9d5b7a7d-d7ad-4159-8b6b-33625a0762f3.png"
    private const val IMG_CRYOLITE = "$Q/fc13d809-54b6-410f-b10c-0ab1a29926ab.png"
    private const val IMG_PERIDOTITE = "$Q/c950218e-71d4-4c14-a618-8529a203c604.png"
    private const val IMG_DUNITE = "$Q/c8cb2a5d-12a6-4162-9609-4f0e1cec0988.png"
    private const val IMG_SYENITE = "$Q/55a37f5f-8a0b-4d7d-b3b5-53b96c2ec662.png"
    private const val IMG_MONZONITE = "$Q/e3679443-4efb-46d0-a6b1-d16f188eea52.png"
    private const val IMG_ANDESITE = "$Q/b0d5b28b-5306-4ef9-ad15-407a6ea0967d.png"
    private const val IMG_DACITE = "$Q/04533132-ba49-488c-8c62-42f852127044.png"
    private const val IMG_TRACHYTE = "$Q/f834424b-7b5e-4e1a-bd29-f50c39ee4eb9.png"
    private const val IMG_PHONOLITE = "$Q/c93d35b2-f175-492a-8c35-1b63ac9c5495.png"
    private const val IMG_KIMBERLITE = "$Q/12b70bbd-0841-438a-ad29-7e15393ae2b5.png"
    private const val IMG_CARBONATITE = "$Q/eef353ad-e63d-4976-8077-fcdcdd92d5b1.png"
    private const val IMG_BLUESCHIST = "$Q/987fa6fd-b06c-4a23-a350-66c934f1bbc3.png"
    private const val IMG_PHYLLITE = "$Q/22aa3f1e-6cd3-45ca-b326-f6979c122e2c.png"
    private const val IMG_GRANULITE = "$Q/af6394ef-1306-4f78-854d-3e443467b973.png"
    private const val IMG_LIGNITE = "$Q/fb77ef8e-d5d2-4e7a-a1f3-e2de5c324e33.png"
    private const val IMG_PHOSPHORITE = "$Q/8eda00d0-ed34-48f3-b5da-d27a74f08132.png"
    private const val IMG_BANDED_IRON_FORMATION = "$Q/c163d4fd-c9a6-424c-854c-d936a7a66c19.png"
    private const val IMG_LATERITE = "$Q/2a14eec7-4a29-44de-aff8-919ee6612a29.png"
    private const val IMG_ARGILLITE = "$Q/0d3de5ff-0f84-451f-844e-30df243fc431.png"
    private const val IMG_TROCTOLITE = "$Q/01b71f0e-a7d2-4976-96fc-769cbe4cb8c8.png"
    private const val IMG_PYROXENITE = "$Q/08866839-fa44-4753-8598-c269114daafc.png"
    private const val IMG_LAMPROITE = "$Q/e9c157bc-aae6-499f-97cb-b58a2400925e.png"
    private const val IMG_CHARNOCKITE = "$Q/93fa0eda-d05a-48a6-ae03-a31b022d000f.png"
    private const val IMG_BITUMINOUS_COAL = "$Q/19e4c6f9-ff41-4802-bd3e-f757d284060a.png"
    private const val IMG_OIL_SHALE = "$Q/44c433f9-acb8-4984-9a90-7a455bc8802d.png"
    private const val IMG_DIAMICTITE = "$Q/29d2aebe-1ff6-474d-b88f-187beb4ad7d1.png"
    private const val IMG_ITACOLUMITE = "$Q/caaefa09-1169-45e4-894f-6eee8994b19e.png"
    private const val IMG_SCOLECITE = "$Q/56f3fc9f-ba72-42e0-aa07-7c5912f0842e.png"
    private const val IMG_CHABAZITE = "$Q/1ae007e9-6edc-46c5-968c-c12685ae3008.png"
    private const val IMG_ANALCIME = "$Q/f921118b-aee7-4eb5-b224-492603e730af.png"
    private const val IMG_MORDENITE = "$Q/abd56ca5-85d3-4e84-8956-518eb67c6db1.png"
    private const val IMG_CLINOPTILOLITE = "$Q/c248ed8a-d965-4b67-9f1a-c282e513089e.png"
    private const val IMG_RED_BERYL = "$Q/cda9e0f7-2817-4bcb-9c08-a0d260cc70b2.png"
    private const val IMG_SERENDIBITE = "$Q/513bc9fc-3c06-4b64-ba8e-63e972546885.png"
    private const val IMG_SINHALITE = "$Q/5e8f3bf0-e217-4a9e-9607-a29d6e91b240.png"
    private const val IMG_DIASPORE = "$Q/c958911e-5f0f-4454-ba65-76f4b3cdfef6.png"
    private const val IMG_KORNERUPINE = "$Q/467f6743-923a-40b5-ac3e-500f0d003eec.png"
    private const val IMG_BROOKITE = "$Q/660a909f-90d3-4ee8-98dc-2351ffda358d.png"
    private const val IMG_ANATASE = "$Q/1b6b072e-d460-4c68-8f4c-d12cece35b46.png"
    private const val IMG_CHALCANTHITE = "$Q/3e7417fb-11c1-4ff2-bb1d-9086a944b3c5.png"
    private const val IMG_HAUSMANNITE = "$Q/8fefe8cd-a07b-478e-aec5-dbb5a0c6ea12.png"
    private const val IMG_LEADHILLITE = "$Q/e8b07a5e-ac31-4ad4-af47-0330adb5c793.png"
    private const val IMG_MANGANITE = "$Q/76f6e31b-4758-43f5-b3b4-93296e9d9158.png"
    private const val IMG_XENOTIME = "$Q/c140d22f-ad12-4b89-b946-307402d95143.png"
    private const val IMG_PARISITE = "$Q/dd2567ee-6c51-40a2-b5de-496731041689.png"
    private const val IMG_ASTROPHYLLITE = "$Q/d006d021-0b35-4127-aa31-b81e58b7d690.png"
    private const val IMG_DELHAYELITE_ROUGH = "$Q/c8836a5f-2de7-4370-9369-f30b7c14ff6c.png"
    private const val IMG_DELHAYELITE_WILD = "$Q/6f5d0c3f-538a-4fd2-9fe8-7c328483439b.png"
    private const val IMG_TAGAMITE_ROUGH = "$Q/8568faa4-b672-4f28-9dd6-5e371ef7e801.png"
    private const val IMG_EUCRYPTITE = "$Q/057c79f7-6ce3-4d6d-8feb-3e4f1e18eb3f.png"
    private const val IMG_POLLUCITE = "$Q/3e5130b5-5ed5-4d7c-baa8-095d1b7802cf.png"
    private const val IMG_POLYHALITE = "$Q/c28e80fc-9c87-4ca9-a02d-ec5eb39b4237.png"
    private const val IMG_KAINITE = "$Q/0e4ae4ef-c6e7-4ab1-a207-faaa7a0349a1.png"
    private const val IMG_THENARDITE = "$Q/e346c6ec-1b12-41ca-8180-d6ced0dacbc8.png"
    private const val IMG_MIRABILITE = "$Q/0a2462d5-c63b-46ec-9706-87ebf2745f43.png"
    private const val IMG_MELANTERITE = "$Q/8acd097d-e4c2-43cb-a436-98c133a134f3.png"
    private const val IMG_FERGUSONITE = "$Q/d4a163f8-62a0-4447-928c-d7122decc539.png"
    private const val IMG_GADOLINITE = "$Q/eda0ea27-e86e-4b67-b0c7-be91a93b367c.png"
    private const val IMG_SAMARSKITE = "$Q/92219e83-233d-4688-bf2e-675ee523db1f.png"
    private const val IMG_BRAZILIANITE = "$Q/eb7c3ee3-7de0-49ac-be5a-2c55c1b4e6cd.png"
    private const val IMG_CHILDRENITE = "$Q/472f887f-a907-4065-86a4-bd6f6009fa49.png"
    private const val IMG_HERDERITE = "$Q/822592b5-2318-4312-8ce8-65580b764b18.png"
    private const val IMG_BERYLLONITE = "$Q/6bcf7ea4-9cde-4530-a34d-39b8f1234714.png"
    private const val IMG_AMBLYGONITE = "$Q/7aa852ad-a334-4fdd-8409-c8cb499f1b86.png"
    private const val IMG_TRIPHYLITE = "$Q/4f298051-0e17-4c8e-94e5-fb1cc470b20d.png"
    private const val IMG_DUNKLEOSTEUS = "$Q/effd3f49-4568-45a8-a5d4-51701cee061f.png"
    private const val IMG_ANOMALOCARIS = "$Q/cf7c1b1d-6953-404f-9954-d045047af03a.png"
    private const val IMG_TIKTAALIK = "$Q/fb0ca94a-819b-42ff-b5ae-1894088c2254.png"
    private const val IMG_ICHTHYOSTEGA = "$Q/80cd8cec-2abd-4f92-82e7-6091487654ff.png"
    private const val IMG_DIMETRODON = "$Q/dc570a9f-4308-461b-9b66-473ad6dd4716.png"
    private const val IMG_COELOPHYSIS = "$Q/c255ffa3-5043-4304-9f42-0cd50514ff78.png"
    private const val IMG_ALLOSAURUS = "$Q/ebb3e596-bd77-40b8-891f-2ff51f43ff61.png"
    private const val IMG_STEGOSAURUS = "$Q/6581b4d7-9523-41bf-920b-034bde57fd50.png"
    private const val IMG_DIPLODOCUS = "$Q/2491e38d-1058-442c-a3b6-ff0eb644a69e.png"
    private const val IMG_VELOCIRAPTOR = "$Q/10cfcb69-0e4d-4bb2-9c35-48387ae2015b.png"
    private const val IMG_TRICERATOPS = "$Q/df3fa250-1998-42bb-889d-41a1ff748225.png"
    private const val IMG_TREX = "$Q/ccad4481-0d21-4fa8-9a72-a7ba2654437f.png"
    private const val IMG_BRACHIOSAURUS = "$Q/a85b67a4-74eb-46d0-92f2-2104f11fbc18.png"
    private const val IMG_COMPSOGNATHUS = "$Q/4a3cb557-027a-4400-b6ea-f167370f0de9.png"
    private const val IMG_DEINONYCHUS = "$Q/fb1e7772-f92b-478c-9d82-d75c04b08efe.png"
    private const val IMG_PARASAUROLOPHUS = "$Q/0a121b2f-d9e1-41fa-bac6-f0e7cb85a917.png"
    private const val IMG_ANKYLOSAURUS = "$Q/de0d0a95-0dd6-4275-991e-fa6e07981932.png"
    private const val IMG_PTERANODON = "$Q/e69ef4ac-370e-4570-811e-111a9abc9dd2.png"
    private const val IMG_ELASMOSAURUS = "$Q/c7cce62f-8ded-4a36-bfa5-9a59d13e1197.png"
    private const val IMG_MOSASAURUS = "$Q/4e565efb-1dde-4561-93e5-42a2346da1ab.png"
    private const val IMG_ARCHELON = "$Q/32243a0f-db8d-4a0a-a0bc-b5e505eb2190.png"
    private const val IMG_BASILOSAURUS = "$Q/9d4cf347-5098-472b-93f2-935599cc5a81.png"
    private const val IMG_BRONTOTHERIUM = "$Q/7a577e32-bcaa-4ac3-8c52-259ffaa29776.png"
    private const val IMG_GLYPTODON = "$Q/78497b48-62bb-4a82-b771-f09da955e784.png"
    private const val IMG_MEGATHERIUM = "$Q/010fac8a-cfe3-4d0c-aa0d-81a1684560a3.png"
    private const val IMG_ARCHAEOPTERYX = "$Q/097412d8-82f8-43b8-aa2a-c4b65c15e736.png"
    private const val IMG_SMILODON_SKULL = "$Q/4de08915-e457-4614-bb0b-133ea5751a39.png"
    private const val IMG_UINTATHERIUM = "$Q/4bc00395-3f10-4116-a5bf-56065aa86e2f.png"

    // ── Mohs hardness scale reference minerals (AI-generated museum specimens) ──
    const val IMG_MOHS_1_TALC = "$Q/a757451c-9d9f-45eb-836d-593b522fcb1e.png"
    const val IMG_MOHS_2_GYPSUM = "$Q/cbe0eb37-fab5-405e-a512-31d838615d5d.png"
    const val IMG_MOHS_3_CALCITE = "$Q/a00f3bad-abb4-44b7-a82c-43848d18910c.png"
    const val IMG_MOHS_4_FLUORITE = "$Q/da1c926b-6155-4e5e-b2f0-bcaf54114bc0.png"
    const val IMG_MOHS_5_APATITE = "$Q/a72fa8ad-c1b6-4bc0-bf54-16688e0f9bfd.png"
    const val IMG_MOHS_6_ORTHOCLASE = "$Q/f2e271b2-0001-4ce0-8f38-bc02e62c1a27.png"
    const val IMG_MOHS_7_QUARTZ = "$Q/3946d5c5-a966-44da-b0af-d4db4a8eff56.png"
    const val IMG_MOHS_8_TOPAZ = "$Q/c38dd7fe-1368-49d1-8a9d-749d92a6c4b4.png"
    const val IMG_MOHS_9_CORUNDUM = "$Q/b0e3ad5f-51f1-42a2-a0b9-4b06311df895.png"
    const val IMG_MOHS_10_DIAMOND = "$Q/54d112c6-f8ae-4485-9710-84edad4ad56f.png"

    // ── NEW: Banded chert ──
    private const val IMG_BANDED_CHERT = "$Q/0ff70c27-2cee-4305-b49e-d91f3da722f4.png"

    // ── NEW: Jasper varieties (19 new types) ──
    private const val IMG_JASPER_PICTURE = "$Q/f1f060aa-6eef-4c7d-abe9-824d753cf924.png"
    private const val IMG_JASPER_BRECCIATED = "$Q/4af73089-a166-4dd8-a444-de09b3e28b7f.png"
    private const val IMG_JASPER_MOOKAITE = "$Q/49ad6eb0-fbc4-4387-961c-6a3b14fe2b99.png"
    private const val IMG_JASPER_OCEAN = "$Q/9c37a9ac-ad1c-425e-8d21-001e12d97871.png"
    private const val IMG_JASPER_MORRISONITE = "$Q/2edc94b8-5bea-4cdb-81d6-1d8eaceac6dd.png"
    private const val IMG_JASPER_LEOPARD_SKIN = "$Q/c83a81c9-17c9-48d8-b3bd-c32107c77d67.png"
    private const val IMG_JASPER_WILLOW_CREEK = "$Q/45107d34-eee1-41f3-b9a9-8203a5eab708.png"
    private const val IMG_JASPER_BRUNEAU = "$Q/e146c688-5a3f-4476-9ea8-1c91d03ff6ec.png"
    private const val IMG_JASPER_BIGGS = "$Q/ed44f0a0-ab6b-499e-a8d0-d4efb1c81c89.png"
    private const val IMG_JASPER_IMPERIAL = "$Q/27bebe00-340f-4cc8-9a61-a761a3572930.png"
    private const val IMG_JASPER_KAMBABA = "$Q/24269fd4-6e94-42d7-86a1-f02e6d79823d.png"
    private const val IMG_JASPER_POLYCHROME = "$Q/5ccd6109-796a-4171-92c1-e5c3d8574fe8.png"
    private const val IMG_JASPER_RED_CREEK = "$Q/5fdb1d23-1cad-4001-8616-4670f6f95def.png"
    private const val IMG_JASPER_NOREENA = "$Q/fc2fa822-ad31-4836-9daf-204d4371dce8.png"
    private const val IMG_JASPER_ORBICULAR = "$Q/285cbd49-0d2a-4ae3-9578-a4cd8c11503a.png"
    private const val IMG_JASPER_PORCELAIN = "$Q/70280b30-e3f0-45b1-a8b9-2caf30cc8763.png"
    private const val IMG_JASPER_POPPY = "$Q/0582cbbf-726b-4e1d-863a-3f86f3d9e4de.png"
    private const val IMG_JASPER_STONE_CANYON = "$Q/b2af4ce3-f6b8-476f-896e-bcdd559a9683.png"
    private const val IMG_JASPER_BLUE_MOUNTAIN = "$Q/24d770a1-8b0b-4622-aee5-e8abda38aaab.png"

    // ── NEW: Expanded chalcedony colors ──
    private const val IMG_CHALCEDONY_BLUE = "$Q/9b9f08bf-2687-475d-b109-7cb6c527c4ab.png"
    private const val IMG_CHALCEDONY_PINK = "$Q/32963b61-bbee-45ab-b598-47f15faead01.png"
    private const val IMG_CHALCEDONY_YELLOW = "$Q/ae9cac7a-8c71-4fbf-8aa0-a3c2b03d0fea.png"
    private const val IMG_CHALCEDONY_PURPLE = "$Q/1c9d3e42-d301-4661-86b0-d632784e40c9.png"

    // ── NEW: Expanded flint colors & formations ──
    private const val IMG_FLINT_BROWN = "$Q/6df94ade-4c6f-4593-a096-32a5b56722cf.png"
    private const val IMG_FLINT_BANDED = "$Q/55195c32-44b1-413b-a548-4a0d6af2c272.png"
    private const val IMG_FLINT_GRAY = "$Q/52ccd7b1-dd59-4c20-9cb3-19bf44b5024d.png"
    private const val IMG_FLINT_RED = "$Q/025a56ca-ca3c-4834-adba-a881cf870f73.png"

    // ── NEW: Expanded granite colors ──
    private const val IMG_GRANITE_PINK = "$Q/6a8bd314-c0f7-4771-8577-e3aa6da7ca6a.png"
    private const val IMG_GRANITE_RED = "$Q/dd681aad-032d-4321-ac31-47020df49273.png"

    // ── NEW: Ammolite and additional agate varieties ──
    private const val IMG_AMMOLITE = "$Q/4e0f46b5-18d1-456e-a98c-d486572516bc.png"
    private const val IMG_LAKE_SUPERIOR_AGATE_SPEC = "$Q/b08f394c-9930-479b-ace5-b8ee23c2cfdf.png"
    private const val IMG_FAIRBURN_AGATE = "$Q/1a8e2cb1-df6c-4208-ac0a-a2315a3979fd.png"
    private const val IMG_COYAMITO_AGATE = "$Q/a55893c5-6cf9-4ec3-bd7f-fc82246168d3.png"
    private const val IMG_POLKA_DOT_AGATE = "$Q/e2f98f29-5dcb-4317-8110-bdad283039b5.png"
    private const val IMG_IRIS_AGATE = "$Q/3ba2a7dd-4de6-4758-9b2f-55028395a076.png"
    private const val IMG_SAGENITIC_AGATE = "$Q/ba6c8ecc-4c92-4ef9-8871-640c9ef5764b.png"
    private const val IMG_MOLDAVITE = "$Q/201e0151-a6db-45c1-a73f-46da802cf9e9.png"
    private const val IMG_LIBYAN_DESERT_GLASS = "$Q/79af2894-c89b-44e6-94f1-e85e138ec761.png"
    private const val IMG_COLDWATER_AGATE = "$Q/2ed74743-9ed9-4654-afe3-a59c4625e1a5.png"

    // ── Expanded agate varieties (8 new types) ──
    private const val IMG_AGATE_TURRITELLA = "$Q/c739daae-c298-4fa8-8179-f0aea4599b35.png"
    private const val IMG_AGATE_BRAZILIAN = "$Q/2f961780-fdf5-4f40-8e5e-a72107d49738.png"
    private const val IMG_AGATE_THUNDER_EGG = "$Q/067a6962-f9b5-4ec3-b49e-6eed0ebb0072.png"
    private const val IMG_AGATE_SNAKE_SKIN = "$Q/ebcec98f-343c-4862-9390-43aa3cb74dce.png"
    private const val IMG_AGATE_TUBE = "$Q/ec68195b-c9c6-4cc3-992c-53a73bd8aa16.png"
    private const val IMG_AGATE_EYE = "$Q/5a10587f-041e-4010-867c-81b04b6a428d.png"
    private const val IMG_AGATE_ENHYDRO = "$Q/4adbe500-974a-4369-b24a-77f2fd867f23.png"
    private const val IMG_AGATE_DRYHEAD = "$Q/d4018eae-cbed-4bf7-8b28-e91cbf87894f.png"

    // ── Expanded jasper varieties (8 new types) ──
    private const val IMG_JASPER_DESCHUTES = "$Q/3a3d5588-6110-4b1a-bf1b-42d8e68d8dc9.png"
    private const val IMG_JASPER_WILD_HORSE = "$Q/7e2ea277-dc5f-40cb-b7f3-b9574513898a.png"
    private const val IMG_JASPER_OWYHEE = "$Q/92293089-80c7-4d11-9767-b377abba468e.png"
    private const val IMG_JASPER_ZEBRA = "$Q/779d8d9b-07d9-474e-9e3a-bb7cd851c385.png"
    private const val IMG_JASPER_SPIDERWEB = "$Q/45fe31ac-013e-482c-9108-3d32c25238f7.png"
    private const val IMG_JASPER_AUTUMN = "$Q/5acce929-2a3b-4d48-b4de-3de9e670aba4.png"
    private const val IMG_JASPER_RAINFOREST = "$Q/3c592837-e25b-4433-a0f9-2afc3a7cd526.png"
    private const val IMG_JASPER_SUNSET = "$Q/6f33e7b1-1f30-4bb3-824a-b35001910dbf.png"

    // ── Expanded opal varieties (6 new types) ──
    private const val IMG_OPAL_YOWAH = "$Q/67e5cc92-d668-4ced-8af7-f9c12f9cd22a.png"
    private const val IMG_OPAL_KOROIT = "$Q/4c5ae674-47bf-4a8e-bb7f-b8e768e60e6f.png"
    private const val IMG_OPAL_HONDURAN = "$Q/18301ec4-9162-4357-9eb5-7eb70c98fc56.png"
    private const val IMG_OPAL_PERUVIAN = "$Q/af6cd947-7a52-43f3-befb-08d3f3cc22d5.png"
    private const val IMG_OPAL_BRAZILIAN = "$Q/1d110112-225c-4048-b911-861502f1e799.png"
    private const val IMG_OPAL_MEXICAN = "$Q/bca24328-db03-4599-96c2-e5ee3e945752.png"

    // ── Expanded granite varieties (5 new types) ──
    private const val IMG_GRANITE_ORBICULAR = "$Q/b9ff0a5d-73c9-433b-b0c4-5b2a6616fd03.png"
    private const val IMG_GRANITE_PORPHYRITIC = "$Q/2bb9c72f-8553-4ce9-89d0-fcbf69e51264.png"
    private const val IMG_GRANITE_GRAPHIC = "$Q/796fcf0d-f934-4cb2-9393-da3dce8d7ca9.png"
    private const val IMG_GRANITE_RAPAKIVI = "$Q/7104e1cc-0599-4a64-9479-f36389617f23.png"
    private const val IMG_GRANITE_UNAKITE = "$Q/4aae1f42-fc05-4fa3-a19d-c569c9ebdf92.png"

    // ── Expanded petrified wood varieties (4 new types) ──
    private const val IMG_PETRIFIED_ARIZONA_RAINBOW = "$Q/c4fee8ad-65d1-4ad7-b583-d1354cef8e6d.png"
    private const val IMG_PETRIFIED_WASHINGTON = "$Q/0b8f131e-b32b-4349-bc0d-6a84f35d7365.png"
    private const val IMG_PETRIFIED_INDONESIAN = "$Q/ab104840-43c4-415b-9d9c-1a55c1b04158.png"
    private const val IMG_PETRIFIED_ARGENTINE = "$Q/68a96a29-4c39-4877-ae8d-0ac44465e719.png"

    // ── Expanded beryl varieties (5 new types) ──
    private const val IMG_BERYL_RED = "$Q/ec5ecdad-ebf8-4e73-a91e-4e7491241061.png"
    private const val IMG_BERYL_GOLDEN = "$Q/0450e938-eefb-4ee6-b4e1-a695bdbe0bd7.png"
    private const val IMG_BERYL_MAXIXE = "$Q/b3470e52-e3dd-421d-9272-6eea573fd54e.png"
    private const val IMG_BERYL_CATS_EYE = "$Q/a72b8594-b0ce-4fa7-b803-eab3f57e81d6.png"
    private const val IMG_BERYL_YELLOW = "$Q/1f5e27f2-579a-43c1-be85-79c12a6d51c7.png"

    // ── NEW: Rocks Are Amazing — 33 geological wonders ──
    private const val IMG_ENHYDRO_AGATE = "$Q/0cc4817a-1959-40a2-a550-6dae17514dc1.png"
    private const val IMG_ENHYDRO_MULTI = "$Q/9d1d5dcd-a846-4eb8-ae32-d08ac14123fd.png"
    private const val IMG_ENHYDRO_QUARTZ = "$Q/73f6fbc6-cd74-4964-836c-e22d29851f90.png"
    private const val IMG_PSEUDO_QZ_FLUORITE = "$Q/98c6438b-1919-4bfa-8eb3-65166000cf7f.png"
    private const val IMG_PSEUDO_GOETHITE_PY = "$Q/8ece5b93-631a-4871-a00c-e60f0c3238a3.png"
    private const val IMG_PSEUDO_MALACHITE_AZ = "$Q/48740f9d-82ab-40da-895d-40193f7cb060.png"
    private const val IMG_PSEUDO_LIMONITE_PY = "$Q/d3d6e753-d319-4a71-8fa7-8f5d020af118.png"
    private const val IMG_PSEUDO_COPPER_ARAG = "$Q/a0064518-9e7a-4866-abae-7c8ac51aea0c.png"
    private const val IMG_PSEUDO_OPAL_WOOD = "$Q/aab6c31e-cb2e-4fd7-991a-37a2aea31df6.png"
    private const val IMG_PSEUDO_SERP_OLIVINE = "$Q/b7937cc9-2ba0-4692-8af4-c9abecbd587d.png"
    private const val IMG_PETROLEUM_QUARTZ = "$Q/b9f732b1-df17-4b44-8765-687bb4efbeb2.png"
    private const val IMG_PETROLEUM_FLUORITE = "$Q/df8b9534-409c-4030-9a49-6ffed79cb485.png"
    private const val IMG_BITUMEN_CALCITE = "$Q/4ec5e314-98ae-4a75-9a74-fa571f4437cf.png"
    private const val IMG_HYDROCARBON_HALITE = "$Q/aadd17d3-162d-48f5-84ba-a3f4cd360f56.png"
    private const val IMG_CHLORITE_QUARTZ = "$Q/a40a72ce-94fa-4594-94b6-f62d365026d1.png"
    private const val IMG_FIRE_QUARTZ = "$Q/feb8c31d-c3bc-4102-b794-51a9a7b73acf.png"
    private const val IMG_THETIS_HAIR = "$Q/d896c007-d506-4e99-9bf0-e687e8415d4b.png"
    private const val IMG_DUMORTIERITE_QZ_AMAZE = "$Q/dbc39ecd-dcec-46c2-95c0-8cf3ce48c044.png"
    private const val IMG_FLUOR_WILLEMITE = "$Q/0513ae54-986d-4a9f-b6a3-c0334c47cc38.png"
    private const val IMG_FLUOR_FLUORITE_AMAZE = "$Q/729b080a-09f2-4de0-aa1b-cf7defabc560.png"
    private const val IMG_FLUOR_AUTUNITE = "$Q/c99b9578-4888-4e15-9042-dfdf7cb5d206.png"
    private const val IMG_PHOSPHOR_CALCITE = "$Q/48626a67-d6cc-4c9f-8c15-e96c24977511.png"
    private const val IMG_CATSEYE_CHRYSO = "$Q/5eacd1b3-4cca-4fa9-917a-bcf392325157.png"
    private const val IMG_STAR_SAPPHIRE = "$Q/dc7fdadb-e8ce-451f-8941-556a0c69dd4e.png"
    private const val IMG_IRID_AMMOLITE = "$Q/cf7093ab-4a65-443a-9908-015e1758e0c5.png"
    private const val IMG_LABRADORITE_AMAZE = "$Q/a818927e-8242-4799-9f6b-63e7e0927b23.png"
    private const val IMG_FULGURITE_AMAZE = "$Q/78397578-b36f-4da5-a77f-5866537e45af.png"
    private const val IMG_VIVIANITE_AMAZE = "$Q/0b2f491d-f071-46e0-a7d2-9e11582436b7.png"
    private const val IMG_HACKMANITE = "$Q/5d4643f8-e856-4e8f-8cb5-d4a7b141d39b.png"
    private const val IMG_PYRITE_SUN = "$Q/3adca516-772e-4e90-8086-74f9be1837a8.png"
    private const val IMG_DESERT_ROSE_AMAZE = "$Q/5fa1670b-602d-40c2-9d9b-679c771a5707.png"
    private const val IMG_THUNDEREGG_AMAZE = "$Q/a9182d78-42b6-43e1-b87b-ea445f2fede5.png"
    private const val IMG_TRAPICHE_EMERALD = "$Q/b37b042c-5854-4bf8-ba34-3fcf33c498d8.png"
    private const val IMG_NATROLITE_AMAZE = "$Q/380a7ac7-0771-47ed-89d6-29152bd60981.png"
    private const val IMG_CAVE_PEARL = "$Q/f9a996d7-d7c4-40a1-8591-84f2bdf29eba.png"

    // ── NEW: Industrial slag & slag glass — Rocks Are Amazing ──
    private const val IMG_LELAND_BLUE = "$Q/6d48dc3b-710c-4c74-ada4-f8d29d85b815.png"
    private const val IMG_SLAG_BLUE = "$Q/f5609c5b-44ab-4e56-962c-9f86b3fa65f7.png"
    private const val IMG_SLAG_GREEN = "$Q/766c807d-db01-4d73-8b3b-14ff6a919605.png"
    private const val IMG_SLAG_PURPLE = "$Q/b6b86d75-f31f-4efa-a76f-6918cb359537.png"
    private const val IMG_SLAG_AMBER = "$Q/cb3de22e-191b-484c-885e-9dac55a5ff02.png"
    private const val IMG_IRON_FURNACE_SLAG = "$Q/b154cbce-e924-415d-b2a1-592aff049e40.png"
    private const val IMG_COPPER_SMELTING_SLAG = "$Q/0b2e25c4-160c-4e54-98e7-7261b132c633.png"
    private const val IMG_SLAG_MANGANESE = "$Q/687bcd82-d765-491c-a0ed-a45d048ed183.png"
    private const val IMG_SLAG_STEEL_FURNACE = "$Q/19b3a067-1645-4dd4-8b61-437ad54c334e.png"

    // ── Fluorescent UV images: natural, longwave, midwave, shortwave ──
    // Willemite & Calcite (Franklin NJ)
    private const val IMG_FLUOR_WILLEMITE_NAT = "$Q/84411ed3-7f1e-40d0-b619-17902f8fe15c.png"
    private const val IMG_FLUOR_WILLEMITE_LW = "$Q/0e0058e7-6e34-4403-8718-619096e98a5a.png"
    private const val IMG_FLUOR_WILLEMITE_MW = "$Q/2974b147-38d5-4f6e-9038-d1687ce25882.png"
    private const val IMG_FLUOR_WILLEMITE_SW = "$Q/e2a3886d-6f1f-45dd-9824-7e391fbc8d27.png"
    // Fluorite
    private const val IMG_FLUOR_FLUORITE_NAT = "$Q/ebdc38f6-3799-4a49-bf1d-b42553319bd3.png"
    private const val IMG_FLUOR_FLUORITE_LW = "$Q/d28875c4-12ea-4ee1-8af2-fc1682be76d3.png"
    private const val IMG_FLUOR_FLUORITE_MW = "$Q/baa8a9ec-230e-499d-a6ae-6d31f76adf57.png"
    private const val IMG_FLUOR_FLUORITE_SW = "$Q/5fa582da-20df-4002-8909-a6df138a017f.png"
    // Autunite
    private const val IMG_FLUOR_AUTUNITE_NAT = "$Q/d452d0f5-eebd-4c61-88ef-f839a0d57a2c.png"
    private const val IMG_FLUOR_AUTUNITE_LW = "$Q/bc6a80e8-df60-4017-b0a8-41caf2a4df00.png"
    private const val IMG_FLUOR_AUTUNITE_MW = "$Q/d447e0e6-8048-4e5e-a66d-a4f7f0bdb006.png"
    private const val IMG_FLUOR_AUTUNITE_SW = "$Q/99a774d3-913a-43cb-a97a-d7f1db8e2265.png"
    // Phosphorescent Calcite
    private const val IMG_PHOSPHOR_CALCITE_NAT = "$Q/39c23e86-476b-4a9d-9733-079366382cb8.png"
    private const val IMG_PHOSPHOR_CALCITE_LW = "$Q/f91e38ba-86c6-4f33-97f9-27f4d56472fd.png"
    private const val IMG_PHOSPHOR_CALCITE_MW = "$Q/394191eb-bd6e-46a9-be5b-b472b17b628c.png"
    private const val IMG_PHOSPHOR_CALCITE_SW = "$Q/7e9b6457-b392-4a80-973b-ee88bf65ee8c.png"
    // Scheelite
    private const val IMG_FLUOR_SCHEELITE_NAT = "$Q/af3cea4a-8eaf-4812-a845-ef664a1a21ec.png"
    private const val IMG_FLUOR_SCHEELITE_LW = "$Q/9ef2cafb-0507-42d8-b909-296839f3b87b.png"
    private const val IMG_FLUOR_SCHEELITE_MW = "$Q/03f558c5-4088-4288-bf38-8101886e5088.png"
    private const val IMG_FLUOR_SCHEELITE_SW = "$Q/1d466930-caab-4933-b257-ebd038da42e9.png"
    // Sphalerite
    private const val IMG_FLUOR_SPHALERITE_NAT = "$Q/e7447289-36f4-40ac-b2e6-88478dc9b61b.png"
    private const val IMG_FLUOR_SPHALERITE_LW = "$Q/d41731e0-997b-44d4-95f3-45b659eb51bc.png"
    private const val IMG_FLUOR_SPHALERITE_MW = "$Q/84732316-da92-4883-bdd7-4582db05dcab.png"
    private const val IMG_FLUOR_SPHALERITE_SW = "$Q/d9bd7e6f-e383-49a3-9af2-2ac5c16c122d.png"
    // Scapolite (Wernerite)
    private const val IMG_FLUOR_SCAPOLITE_NAT = "$Q/d228e630-4f5e-4afb-8397-4610ecc1debe.png"
    private const val IMG_FLUOR_SCAPOLITE_LW = "$Q/2e3cbe7f-ab33-410b-8bdb-8bbaee991ced.png"
    private const val IMG_FLUOR_SCAPOLITE_MW = "$Q/1f7f62f2-565d-4127-949d-9dceeb3e100b.png"
    private const val IMG_FLUOR_SCAPOLITE_SW = "$Q/074ceddd-9c52-452e-85e5-ce8d80033f48.png"
    // Hackmanite
    private const val IMG_FLUOR_HACKMANITE_NAT = "$Q/ee18394d-e711-49e0-b528-7d87050e30cd.png"
    private const val IMG_FLUOR_HACKMANITE_LW = "$Q/56368b65-2a15-4df8-b630-636643698404.png"
    private const val IMG_FLUOR_HACKMANITE_AFTER_UV = "$Q/95c5cbd3-a216-4419-9ec4-2a31ca43958a.png"
    private const val IMG_FLUOR_HACKMANITE_SW = "$Q/a44140f8-4d81-4b9e-aa53-f24313f468e8.png"
    private const val IMG_FLUOR_HACKMANITE_CAB_BEFORE_SUN = "$Q/6e90ea28-6051-4050-85d8-1ea5b33660e3.png"
    private const val IMG_FLUOR_HACKMANITE_CAB_AFTER_SUN = "$Q/4a3e84b6-d4d5-4800-86b1-d2cb17fd8346.png"
    // Adamite
    private const val IMG_FLUOR_ADAMITE_NAT = "$Q/6f4cfc55-55e4-4d3d-8da2-c11c685d5712.png"
    private const val IMG_FLUOR_ADAMITE_LW = "$Q/0968a291-d8b5-49fd-9040-4314a91be954.png"
    private const val IMG_FLUOR_ADAMITE_MW = "$Q/c98fc5bf-e86c-4695-8b07-ff2718e13423.png"
    private const val IMG_FLUOR_ADAMITE_SW = "$Q/e80d8008-a1b3-4f81-abbd-7175a7fe2975.png"
    // Yooperlite
    private const val IMG_FLUOR_YOOPERLITE_NAT = "$Q/65488726-6ef5-48a2-ad9b-a68be4257f9b.png"
    private const val IMG_FLUOR_YOOPERLITE_LW = "$Q/37e52af1-309c-44ba-8613-169f13329b49.png"
    private const val IMG_FLUOR_YOOPERLITE_MW = "$Q/61a811c2-6df7-49c2-a64b-209f8c1e5671.png"
    private const val IMG_FLUOR_YOOPERLITE_SW = "$Q/1f234acb-751f-4bd5-8df0-9d6f813f234a.png"
    // Fluorescent Syenite
    private const val IMG_FLUOR_SYENITE_NAT = "$Q/b14bd0c0-fbe4-473b-bb90-4f4fa02ceb25.png"
    private const val IMG_FLUOR_SYENITE_LW = "$Q/4c9386bd-844c-400e-815a-a3dd13acea01.png"
    private const val IMG_FLUOR_SYENITE_MW = "$Q/a05d952a-4192-47b6-846f-56b1a193c278.png"
    private const val IMG_FLUOR_SYENITE_SW = "$Q/9b12e6cc-3423-43ac-b9ba-17945c0b6665.png"
    // Squid Game Calcite
    private const val IMG_SQUID_GAME_CALCITE_NAT = "$Q/6d9bfb75-e921-4afd-9c07-2d7fe1fb6e42.png"
    private const val IMG_SQUID_GAME_CALCITE_LW = "$Q/14b745b2-cdcb-4fd2-8350-2a5271d6715e.png"
    private const val IMG_SQUID_GAME_CALCITE_ROUGH = "$Q/412723d2-715c-4fc8-b337-a0e5d099b5df.png"
    private const val IMG_SQUID_GAME_CALCITE_SW = "$Q/79be4a77-b1ac-4492-876e-6b04b7c3dfca.png"

    // ── Coprolite images: rough specimen + source animal ──
    private const val IMG_COPROLITE_TREX_ROUGH = "$Q/3d048767-7c49-4330-8327-5bfd72161e09.png"
    private const val IMG_COPROLITE_TREX_ANIMAL = "$Q/5e60efb4-6f57-4aa2-ba91-3ea97162a2eb.png"
    private const val IMG_COPROLITE_FISH_ROUGH = "$Q/fa6415f8-9a2b-452e-a2da-7eee578df033.png"
    private const val IMG_COPROLITE_FISH_ANIMAL = "$Q/3f924886-3611-4d8b-8cba-4f41aeae1448.png"
    private const val IMG_COPROLITE_CROC_ROUGH = "$Q/76c815dc-cb51-4332-bd3b-a6648257c27e.png"
    private const val IMG_COPROLITE_CROC_ANIMAL = "$Q/21ede11a-93dd-46ba-abb3-bc731e15413c.png"
    private const val IMG_COPROLITE_SHARK_ROUGH = "$Q/5e728bb7-52aa-4174-871d-6196dc229a4f.png"
    private const val IMG_COPROLITE_SHARK_ANIMAL = "$Q/57950926-12db-4637-a6e5-69a7cecd0e80.png"
    private const val IMG_COPROLITE_HERB_ROUGH = "$Q/0fd15357-9ece-4161-822b-076e005a1599.png"
    private const val IMG_COPROLITE_HERB_ANIMAL = "$Q/014069b9-ef20-4bf7-b76c-89c7ce665604.png"
    private const val IMG_COPROLITE_JURASSIC_ROUGH = "$Q/85421cf8-25ca-403c-8bb2-96ea3a4095db.png"
    private const val IMG_COPROLITE_JURASSIC_ANIMAL = "$Q/5d806084-06f0-48ad-b5d7-5fe7b8c20d8a.png"

    // ── Variety images for single-image specimens (Batch 41) ──
    private const val IMG_ACANTHITE_MUSEUM = "$Q/dd4d2f88-bdd2-4aa8-a4c9-4e722d4ce277.png"
    private const val IMG_AEGIRINE_MUSEUM = "$Q/3ccf81f9-de95-4e79-a575-271319db9fa8.png"
    private const val IMG_AGATE_BRAZILIAN_VAR = "$Q/bbe04342-50b4-4741-806a-27e0cab2bcd0.png"
    private const val IMG_AGATE_DRYHEAD_VAR = "$Q/a1c66686-4062-40c3-b133-caca57d0ab43.png"
    private const val IMG_AGATE_EYE_VAR = "$Q/11f1d967-77a8-43df-9fc4-b8d24d581ffb.png"
    private const val IMG_AGATE_SNAKE_SKIN_VAR = "$Q/c35122be-54e4-4f30-b4ef-5c8e45ae73dd.png"
    private const val IMG_AGATE_TUBE_VAR = "$Q/93aba94e-c828-4d89-aff6-51c233a8ae30.png"
    private const val IMG_AGATE_TURRITELLA_VAR = "$Q/bb6d422f-3f6a-4f58-a6df-ab9000e4a0ac.png"
    private const val IMG_ALBITE_MUSEUM = "$Q/d1bf46fe-e60c-4f25-90bd-ad6b4d0eda5d.png"
    private const val IMG_ACHONDRITE_MUSEUM = "$Q/2cc9033b-0640-4a18-a01c-a713066690ef.png"
    private const val IMG_ENHYDRO_AGATE_VAR = "$Q/3e1ff784-8837-4390-80ec-b9bc67683e12.png"
    private const val IMG_THUNDEREGG_AMAZE_VAR = "$Q/e8e96fcb-b2be-4aff-8af9-7348f976b8b1.png"
    private const val IMG_IRID_AMMOLITE_VAR = "$Q/e120917d-0782-4a20-a861-393269510c48.png"
    private const val IMG_CATSEYE_CHRYSO_CAB = "$Q/a87d5103-3328-4500-a541-a0bb1e4308e3.png"
    private const val IMG_STAR_SAPPHIRE_CAB = "$Q/659468ca-ae36-4744-aca7-f6491420937c.png"
    private const val IMG_LABRADORITE_AMAZE_VAR = "$Q/fb7fdfc3-4389-4d32-b080-b9e87cf1d15b.png"
    private const val IMG_FULGURITE_AMAZE_VAR = "$Q/62ff5115-bd89-44bd-8a39-7f6c6e4a3155.png"
    private const val IMG_VIVIANITE_AMAZE_VAR = "$Q/e808435b-023a-46a1-ba93-0e11377ca3c9.png"
    private const val IMG_DESERT_ROSE_AMAZE_VAR = "$Q/7be9a385-3a21-4c25-900a-34c625c13b35.png"
    private const val IMG_PYRITE_SUN_VAR = "$Q/7fa4efb1-5c94-42f8-a961-444f0998705b.png"
    private const val IMG_TRAPICHE_EMERALD_VAR = "$Q/bd8dd54d-83b0-4bdc-9d2b-0450d8e65839.png"
    private const val IMG_NATROLITE_AMAZE_VAR = "$Q/8e7d7515-5033-42a4-b249-8beb438f2c12.png"
    private const val IMG_CAVE_PEARL_VAR = "$Q/878bd033-85e8-47e2-9519-06d8e0a39eda.png"
    private const val IMG_PETROLEUM_QUARTZ_VAR = "$Q/0eda24dd-738f-419b-99b0-fb433afc1a1d.png"
    // Pseudomorph & inclusion variety images (Batch 43)
    private const val IMG_PSEUDO_QZ_FLUORITE_VAR = "$Q/e30ddf9b-35e8-4602-b682-5c7dbfb8a78c.png"
    private const val IMG_PSEUDO_GOETHITE_PY_VAR = "$Q/9124fec0-1817-4270-bd71-27aad62ecc8a.png"
    private const val IMG_PSEUDO_MALACHITE_AZ_VAR = "$Q/5055ad8d-200a-4956-b7c1-10b3895dfe85.png"
    private const val IMG_PSEUDO_LIMONITE_PY_VAR = "$Q/c691d9f4-fe16-49eb-82ac-2d5ae33269a9.png"
    private const val IMG_PSEUDO_COPPER_ARAG_VAR = "$Q/7ac3124d-f587-4591-8099-cd149527e12f.png"
    private const val IMG_PSEUDO_OPAL_WOOD_VAR = "$Q/25d18d39-7b2f-45cd-b3e8-904dd3a758ae.png"
    private const val IMG_PSEUDO_SERP_OLIVINE_VAR = "$Q/340bb8e4-f1ed-4967-9025-71f5c09646d3.png"
    private const val IMG_PETROLEUM_FLUORITE_VAR = "$Q/c4d7ea4e-4833-4c08-97d4-c2b0a8430f4b.png"
    // Inclusion & enhydro variety images (Batch 44)
    private const val IMG_BITUMEN_CALCITE_VAR = "$Q/4ca8f5d9-1e47-4af8-b344-e2e0259d7ace.png"
    private const val IMG_HYDROCARBON_HALITE_VAR = "$Q/f53a8c49-b22b-48ef-96c6-15ab103eb20f.png"
    private const val IMG_CHLORITE_QUARTZ_VAR = "$Q/5e7c24d6-9da3-4891-a69f-0fa6fb008910.png"
    private const val IMG_FIRE_QUARTZ_VAR = "$Q/5ecfb78e-9ff5-4626-a571-b23a9dcd05e2.png"
    private const val IMG_THETIS_HAIR_VAR = "$Q/0278d31d-3417-44f2-b474-81f94538ef3f.png"
    private const val IMG_DUMORTIERITE_QZ_AMAZE_VAR = "$Q/8a1e715e-6bb3-463f-83ba-1bc96a6eaa73.png"
    private const val IMG_ENHYDRO_MULTI_VAR = "$Q/621f8c3a-a815-496d-ad0b-c004449ac343.png"
    private const val IMG_ENHYDRO_QUARTZ_VAR = "$Q/8f8385b1-3c08-4b69-a768-27b2cc7b96b2.png"
    // Slag cabochon & wild images (Batch 45)
    private const val IMG_HACKMANITE_TENEB_VAR = "$Q/8d7e4608-ef21-4d72-86de-a148c9c7b1bd.png"
    private const val IMG_LELAND_BLUE_CAB = "$Q/98663437-47eb-473d-a3a2-9468f64266af.png"
    private const val IMG_LELAND_BLUE_WILD = "$Q/93f8f111-9be3-4c69-bf69-a6adfae323ca.png"
    private const val IMG_LELAND_BLUE_BEACH = "$Q/d3f1f76e-1984-4fa2-9d17-0019e299ff0f.png"
    private const val IMG_SLAG_BLUE_CAB = "$Q/eced779e-3950-49e8-83e4-da3c398c82ff.png"
    private const val IMG_SLAG_GREEN_CAB = "$Q/dc44e925-3358-4392-a5f4-b0813deb75f3.png"
    private const val IMG_SLAG_PURPLE_CAB = "$Q/925175bc-e20b-4874-b34f-d333ae01802e.png"
    private const val IMG_SLAG_AMBER_CAB = "$Q/73dffec3-a174-415c-afe3-eb40e8974f96.png"
    // Slag rough/wild & coprolite variety images (Batch 46)
    private const val IMG_THUNDEREGG_AMAZE_WILD = "$Q/09b6f92e-08e6-4f08-a9ce-b0f986dd09c9.png"
    private const val IMG_COPPER_SMELTING_SLAG_WILD = "$Q/2d5c4b41-37ca-4dac-887d-511286e733fa.png"
    private const val IMG_IRON_FURNACE_SLAG_WILD = "$Q/bb9ba2ec-d37c-4432-b161-19f4cff7220d.png"
    private const val IMG_SLAG_MANGANESE_WILD = "$Q/e8c6f884-cf51-4817-8f26-b6904c0ce7a8.png"
    private const val IMG_SLAG_STEEL_FURNACE_WILD = "$Q/a75b3d80-ead2-4d23-83e2-e9fab983805e.png"
    private const val IMG_COPROLITE_TREX_VAR = "$Q/c1b69170-aa85-433a-b19c-14d3392097ec.png"
    private const val IMG_COPROLITE_FISH_VAR = "$Q/d46907db-b818-418e-9851-59e8993a8837.png"
    private const val IMG_COPROLITE_SHARK_VAR = "$Q/52238a01-3193-4930-ada5-3f1d8c524bf1.png"
    // Opal & calcite variety images (Batch 47)
    private const val IMG_BLACK_OPAL_VAR = "$Q/19f1facc-bcc0-4c66-906f-9f9f7f187ac3.png"
    private const val IMG_BLUE_OPAL_VAR = "$Q/855b5131-23ce-430d-b537-087bc40beb3e.png"
    private const val IMG_BOULDER_OPAL_VAR = "$Q/5b8278db-060b-49be-95b6-1587ab30f7bc.png"
    private const val IMG_BOTSWANA_AGATE_VAR = "$Q/26b134d0-8c7e-4c39-a212-f52d5fd13bb3.png"
    private const val IMG_CALCITE_COBALT_VAR = "$Q/71cb2dc6-479e-435f-8f56-5135c9cbe965.png"
    private const val IMG_CALCITE_DOGTOOTH_VAR = "$Q/1ac07b30-f64f-4525-babd-9174234dd877.png"
    private const val IMG_CALCITE_ICELAND_VAR = "$Q/c07f0c0b-5250-4e7d-990b-59710eaa813c.png"
    private const val IMG_CALCITE_MANGANO_VAR = "$Q/e433f8f6-5e0e-4e70-b349-4da2de5f7270.png"
    // Beryl, opal & mineral varieties (Batch 48)
    private const val IMG_BERYL_GOLDEN_VAR = "$Q/413a2bde-cf5f-4fd8-a06f-c1de16fe90ed.png"
    private const val IMG_BERYL_RED_VAR = "$Q/f11f445e-e48c-4804-a4b1-4d20e2465858.png"
    private const val IMG_BERYL_YELLOW_VAR = "$Q/52abadee-89cf-48b7-899a-138a6d8393b5.png"
    private const val IMG_BERYL_CATS_EYE_VAR = "$Q/c84421ca-cd98-49e3-84bf-2e882e0ef097.png"
    private const val IMG_BERYL_MAXIXE_VAR = "$Q/5ddcfb04-aa6b-4c3f-b7c3-d2190710c543.png"
    private const val IMG_AMMOLITE_VAR = "$Q/0f856366-128d-4520-a268-5dd4b34ce8a6.png"
    private const val IMG_AZURITE_MALACHITE_VAR = "$Q/b4517698-0ed9-4ced-9033-318beafd04a7.png"
    private const val IMG_CANCRINITE_VAR = "$Q/74341c5e-f3ce-4a95-8f91-0a1092941846.png"
    // Batch 49: borax, calcite-nailhead, banded-chert, amygdaloidal-basalt, agate-enhydro, agate-thunderegg, archaeopteryx, anomalocaris
    private const val IMG_BORAX_VAR = "$Q/6d5afab5-f622-4204-9857-af5e013127bb.png"
    private const val IMG_CALCITE_NAILHEAD_VAR = "$Q/033a9d9a-96ea-4749-9e30-203f4a5a19b6.png"
    private const val IMG_BANDED_CHERT_VAR = "$Q/2c29664c-1c28-41b9-b1a6-29794969acab.png"
    private const val IMG_AMYGDALOIDAL_BASALT_VAR = "$Q/019fd42e-9336-4e86-a4c0-3ea49c5f2d1a.png"
    private const val IMG_AGATE_ENHYDRO_VAR = "$Q/4b2e2a44-b751-4979-857a-8c806345f1fb.png"
    private const val IMG_AGATE_THUNDER_EGG_VAR = "$Q/11abc57c-8982-429a-b867-368d131e347f.png"
    // Real Wikimedia Commons photos for the merged Agate-Thunderegg card.
    private const val IMG_COMMONS_THUNDER_EGG_PRIDAY = "https://upload.wikimedia.org/wikipedia/commons/e/e9/Thunder_Egg_Agate_(Priday_Blue_Bed,_John_Day_Formation,_Miocene;_near_Madras,_Oregon,_USA)_2_(34416129410).jpg"
    private const val IMG_COMMONS_THUNDER_EGG_QUARTZ = "https://upload.wikimedia.org/wikipedia/commons/8/8d/Quartz-agate_nodule_(Killer_Green_Claim,_Ochoco_Mountains,_Prineville,_Oregon,_USA)_(33993292453).jpg"
    private const val IMG_COMMONS_THUNDER_EGG_FRIEND = "https://upload.wikimedia.org/wikipedia/commons/e/e2/Friend_ranch-thunderegg.JPG"
    private const val IMG_ARCHAEOPTERYX_VAR = "$Q/d9fe7464-0c62-4377-ac7b-ac773b99e302.png"
    private const val IMG_ANOMALOCARIS_VAR = "$Q/c3d7bee1-9bb1-4e62-a0b9-5623a3bd0ccd.png"
    // Batch 50: andrewsarchus, banded-iron, amethyst-calcite, basalt-copper-calcite, calcite-cobalt-cab, calcite-nailhead-polish, banded-chert-slab, borax-cluster
    private const val IMG_ANDREWSARCHUS_VAR = "$Q/b834d4d1-9bbf-4c72-9cc6-ff0cb9ffb6d9.png"
    private const val IMG_BANDED_IRON_ASSEMBLAGE_VAR = "$Q/9d480f6a-cbf3-4520-bc04-4bf3439c076a.png"
    private const val IMG_AMETHYST_CALCITE_VAR = "$Q/63522172-f4fb-48e6-9b1a-8318afbe26a6.png"
    private const val IMG_BASALT_COPPER_CALCITE_VAR = "$Q/9ce5e50b-bcc4-4173-8c77-95c2bac893bb.png"
    private const val IMG_CALCITE_COBALT_CAB = "$Q/88a5be81-e12b-4b27-8c53-4efbc1f651e7.png"
    private const val IMG_CALCITE_NAILHEAD_POLISH = "$Q/ea150455-8210-4199-9ad9-eaf0ff99c856.png"
    private const val IMG_BANDED_CHERT_SLAB = "$Q/fbc3ce67-3ff2-4baa-9982-178b9342c55d.png"
    private const val IMG_BORAX_CLUSTER = "$Q/d99ea530-86ec-4571-abd7-836128429bb2.png"
    private const val IMG_CHERT_MOZARKITE = "$Q/d0f92092-01f1-4d03-932c-e14d67d33b9a.png"
    private const val IMG_CHERT_PORCELLANITE = "$Q/6e2513ca-b25f-49f4-aee4-66ffa5d707e9.png"
    private const val IMG_CHERT_TRIPOLITIC = "$Q/2805eb6e-90f8-4717-8725-856746134a95.png"
    // Batch 51: opal variety images
    private const val IMG_FIRE_OPAL_CAB_VAR = "$Q/dd40603f-4ceb-4467-a11b-4b78e803c5bf.png"
    private const val IMG_CRYSTAL_OPAL_VAR = "$Q/6d635b74-3e13-4749-955c-feea4c2a411f.png"
    private const val IMG_WHITE_OPAL_VAR = "$Q/10486f39-8de6-4930-8261-02742cfdf3f0.png"
    private const val IMG_PETRIFIED_OPALIZED_VAR = "$Q/a818297c-0508-4776-888a-552fd7b263ca.png"
    private const val IMG_MATRIX_OPAL_VAR = "$Q/108cb8ca-afa5-497e-9a35-9245b3d58afc.png"
    private const val IMG_OPAL_PERUVIAN_VAR = "$Q/cdca819d-dd40-4ef5-a665-134914730d6f.png"
    private const val IMG_OPAL_YOWAH_VAR = "$Q/ee5b048d-2380-4ef6-81f8-69cd2d867e2a.png"
    private const val IMG_OPAL_MEXICAN_VAR = "$Q/79a7511d-3d90-4960-8278-70404c246c42.png"
    // Batch 52: coprolite variety + improved source animal images
    private const val IMG_COPROLITE_CROC_VAR = "$Q/f33e46fa-77f2-4536-970b-0f6e32a901a3.png"
    private const val IMG_COPROLITE_HERB_VAR = "$Q/4f748d81-9db1-467c-9870-918421a6bf3d.png"
    private const val IMG_COPROLITE_JURASSIC_VAR = "$Q/47847f7c-266d-4a0a-a764-6af60637e8f8.png"
    // Copper-inclusion agates — Keweenaw Peninsula, Michigan
    private const val IMG_COPPER_BANDED_AGATE_CUT = "$Q/9f4b58d1-6e54-40c4-9c78-fb85f1f48c86.png"
    private const val IMG_COPPER_BANDED_AGATE_ROUGH = "$Q/f681c284-f7b1-450b-86a2-95beef4ee596.png"
    private const val IMG_COPPER_REPLACEMENT_AGATE_CUT = "$Q/981b7e84-491d-4f5d-ae60-5da7a4767651.png"
    private const val IMG_COPPER_REPLACEMENT_AGATE_WHOLE = "$Q/93979f9a-8c80-4fe7-9ac1-0866a54d2656.png"
    private const val IMG_COPPER_INFUSED_AGATE_CUT = "$Q/e3fde77a-9164-4ff5-9c2c-fa6680f891b9.png"
    private const val IMG_COPPER_INFUSED_AGATE_ROUGH = "$Q/1d32872e-f8b0-4bb5-bc49-741cf72d8a87.png"
    private const val IMG_SILVER_COPPER_AGATE_CUT = "$Q/a55019cc-2bfd-4df8-afb7-eca8651d6f31.png"
    private const val IMG_SILVER_COPPER_AGATE_ROUGH = "$Q/2b286945-2c72-4cd3-a3f2-2510c81f2bc7.png"
    private const val IMG_COPROLITE_TREX_SKULL = "$Q/5cf19633-977e-45da-8ad7-7bcecec22f33.png"
    private const val IMG_COPROLITE_STEGO = "$Q/2c730218-4941-4143-9098-fe55309ada46.png"
    private const val IMG_COPROLITE_FISH_ANIMAL_VAR = "$Q/afda36b4-8778-4f14-af02-7f9ac4af2bc1.png"
    private const val IMG_COPROLITE_CROC_ANIMAL_VAR = "$Q/fdd48d22-d205-4987-8760-24815d42df0f.png"
    private const val IMG_COPROLITE_TURTLE = "$Q/1344014f-82c8-4923-82b9-63a9d3c857c9.png"
    // Batch 53: agate & sapphire variety images
    private const val IMG_CONDOR_AGATE_VAR = "$Q/7f7858c7-b466-4871-820b-639d6ec3f4c3.png"
    private const val IMG_FAIRBURN_AGATE_VAR = "$Q/7a418701-436e-4e62-a0d4-1f350ec05a58.png"
    private const val IMG_CRAZY_LACE_AGATE_VAR = "$Q/67b44ec5-9fe5-45f4-a515-0454f51ea380.png"
    private const val IMG_COYAMITO_AGATE_VAR = "$Q/ba38d869-19d2-4da7-8def-5d9c83fc0ffd.png"
    private const val IMG_COLDWATER_AGATE_VAR = "$Q/d64c2612-8220-477a-a802-736cc7410941.png"
    private const val IMG_CORUNDUM_GREEN_SAPPHIRE_VAR = "$Q/b40630af-8061-46d2-900c-f93c2bc9eb82.png"
    private const val IMG_CORUNDUM_PINK_SAPPHIRE_VAR = "$Q/d637582f-fe5c-463f-9d4e-1502e0b6ef41.png"
    private const val IMG_CORUNDUM_YELLOW_SAPPHIRE_VAR = "$Q/a29f7d3d-c7ca-4f57-b0fa-bc5de0acfddc.png"
    // Batch 54: corundum, feldspar, copper, jade varieties
    private const val IMG_CORUNDUM_PURPLE_SAPPHIRE_VAR = "$Q/5c9cd7ad-4102-4779-a19c-388a4ae6a47c.png"
    private const val IMG_CORUNDUM_TEAL_SAPPHIRE_VAR = "$Q/eacd6a1e-15e9-4afa-8fc5-17cc50b11a43.png"
    private const val IMG_CORUNDUM_RUBY_BURMA_VAR = "$Q/f170640f-ebba-4af5-81e0-da92f72205c6.png"
    private const val IMG_CORUNDUM_RUBY_MOZAMBIQUE_VAR = "$Q/fe04c1f5-6886-4abf-8e9c-31ca493bb551.png"
    private const val IMG_FELDSPAR_ANDESINE_VAR = "$Q/f0c993c3-a29f-43ad-bf04-03ead1c73aab.png"
    private const val IMG_FELDSPAR_ANORTHITE_VAR = "$Q/b9c7091a-f442-4349-85d8-c98594331526.png"
    private const val IMG_COPPER_ORE_ASSEMBLAGE_VAR = "$Q/4cd02e00-db35-4d8a-a510-694ee5d90dd9.png"
    private const val IMG_COALINGA_JADE_VAR = "$Q/395e79b5-9f88-4e7c-89de-5e62ef340119.png"
    // Batch 55: obsidian, fluorite, fordite, agate, fossil varieties
    private const val IMG_ELECTRIC_BLUE_OBSIDIAN_VAR = "$Q/a373f6b5-bc9b-4859-bc25-32bbf1373bb1.png"
    private const val IMG_FLUORITE_CUBIC_GREEN_VAR = "$Q/3621a0fb-d875-4bea-b7c6-de89f69c7ea9.png"
    private const val IMG_FLUORITE_PINK_VAR = "$Q/86b8aa81-c369-404e-9b91-ddba55fc4a10.png"
    private const val IMG_FLUORITE_PYRITE_GALENA_VAR = "$Q/8062f6c4-10f2-4124-8daf-ce8310b12139.png"
    private const val IMG_FORDITE_VAR = "$Q/a4e1c35e-caf8-431e-82ec-9368a446b16a.png"
    private const val IMG_FORTIFICATION_AGATE_VAR = "$Q/e399509c-0503-491b-aeab-a4881b15286b.png"
    private const val IMG_DIMETRODON_VAR = "$Q/e764e9ce-1cc5-4537-8447-dea0f07f9700.png"
    private const val IMG_DUNKLEOSTEUS_VAR = "$Q/acc96fb1-0907-4b41-aeac-5b369e671630.png"
    // Batch 56: franklinite, gadolinite, golden-sheen-obsidian, glauconite, gold-quartz, galena-sphalerite-pyrite, forsterite, glass-butte
    private const val IMG_FRANKLINITE_VAR = "$Q/beb1decc-17b1-4b6d-9a74-4a71f12ffd7a.png"
    private const val IMG_GADOLINITE_VAR = "$Q/17c303d9-2381-49ec-9786-5da23f927d2b.png"
    private const val IMG_GOLDEN_SHEEN_OBSIDIAN_VAR = "$Q/06480c23-f9d8-4a64-be9b-3529c91bb90e.png"
    private const val IMG_GLAUCONITE_VAR = "$Q/7fb9f4c3-a9e8-4133-96ef-b8db54373085.png"
    private const val IMG_GOLD_QUARTZ_VAR = "$Q/32fc6627-2838-4f10-8b8c-c15127f47e61.png"
    private const val IMG_GALENA_SPHALERITE_PYRITE_VAR = "$Q/9de2497e-c639-4ba9-bcd7-d20fc7fcc679.png"
    private const val IMG_FORSTERITE_VAR = "$Q/573bc93f-89f5-4fdb-ac92-7205465b2ac0.png"
    private const val IMG_GLASS_BUTTE_VAR = "$Q/1441ecc9-3b37-48e3-8c9c-c18356943f04.png"
    // Batch 57: fossil & mineral variety images
    private const val IMG_FOSSIL_BLASTOID_VAR = "$Q/9fb0ca7a-f140-4ab9-ab47-63583c857649.png"
    private const val IMG_FOSSIL_HORN_CORAL_VAR = "$Q/db250e76-9c3a-437c-8a04-45d5ad3f9096.png"
    private const val IMG_FOSSIL_SHARK_TOOTH_VAR = "$Q/3afcb256-1e69-4257-a6cf-2a52c1d399c8.png"
    private const val IMG_FOSSIL_TRILOBITE_ENROLLED_VAR = "$Q/81b88609-7b5d-415e-b89e-7de16725be5a.png"
    private const val IMG_FOSSIL_NAUTILOID_VAR = "$Q/76717739-8edd-48e3-b2ed-70d875a9512a.png"
    private const val IMG_FOSSIL_FERN_PECOPTERIS_VAR = "$Q/c5d3d363-c404-48c6-b807-e6cd1208c48a.png"
    private const val IMG_FAYALITE_VAR = "$Q/01c915eb-0c39-46d7-9990-7c4fcffefafe.png"
    private const val IMG_ETHIOPIAN_OPAL_VAR = "$Q/ad50d75c-af0f-4cd1-b691-a6f30047cebc.png"
    // Batch 58: feldspar & fossil varieties
    private const val IMG_FELDSPAR_BYTOWNITE_VAR = "$Q/a8e10e55-0007-463d-9cb6-42a78b471b87.png"
    private const val IMG_FELDSPAR_LARVIKITE_VAR = "$Q/7af4b37a-651b-4510-aa6c-14671e721488.png"
    private const val IMG_FELDSPAR_OLIGOCLASE_VAR = "$Q/07c93d09-45b7-4655-a583-86a64421cd9c.png"
    private const val IMG_FELDSPAR_PERISTERITE_VAR = "$Q/43d57d6c-d944-4594-ad87-580e526beb9c.png"
    private const val IMG_FLUORITE_ILLINOIS_VAR = "$Q/d6ed5280-5933-4345-88b0-fc9a458139b9.png"
    private const val IMG_FOSSIL_MOSASAUR_JAW_VAR = "$Q/cad063bd-4203-4498-8a6d-f94cbf494b0d.png"
    private const val IMG_FOSSIL_PTEROSAUR_BONE_VAR = "$Q/2759f696-ce9b-4dde-8ac1-c1226bdc1542.png"
    private const val IMG_FOSSIL_WHALE_VERTEBRA_VAR = "$Q/8d2dbec4-4154-440b-9a2e-f262eac9b320.png"
    // Batch 59: hemimorphite, jasper & igneous varieties
    private const val IMG_HEMIMORPHITE_VAR = "$Q/c6751d5e-382a-4d04-9223-f776b4b325c3.png"
    private const val IMG_JASPER_AUTUMN_VAR = "$Q/ed7cb4b4-fd3a-415a-89ff-1f51e3ca211d.png"
    private const val IMG_JASPER_BIGGS_VAR = "$Q/5b4cd560-1ab2-4ef9-a271-799a9c180290.png"
    private const val IMG_JASPER_BLUE_MOUNTAIN_VAR = "$Q/7f54816b-68e9-4473-b125-d3ea2919501d.png"
    private const val IMG_IRIS_AGATE_VAR = "$Q/370c777e-1614-4d14-8e46-5f53cb983646.png"
    private const val IMG_ILMENITE_VAR = "$Q/7345ce98-386b-4aa2-a8d0-5e8556f1f3d3.png"
    private const val IMG_IGNEOUS_PORPHYRY_VAR = "$Q/4a852072-8fd9-46ae-8b1b-c4ef7d5701fc.png"
    private const val IMG_IGNEOUS_SCORIA_VAR = "$Q/1f096739-b0ba-44fa-90eb-218426e7abdd.png"
    // Batch 60: jade, igneous & industrial varieties
    private const val IMG_JADE_OMPHACITE_VAR = "$Q/eac3f643-0dde-4aa3-9c76-912dff1ba332.png"
    private const val IMG_IGNEOUS_TUFF_VAR = "$Q/8f6dcab0-76ef-46fa-82fd-c2dfb90154dc.png"
    private const val IMG_IGNEOUS_KOMATIITE_VAR = "$Q/0657cf65-d405-49d2-823c-09d8fbadadd3.png"
    private const val IMG_IGNEOUS_PEGMATITE_VAR = "$Q/d9efe32c-7491-4fe5-acb2-7f3af68df2b0.png"
    private const val IMG_IGNEOUS_TRACHYTE_VAR = "$Q/e564d5f9-f056-4b1a-a18e-99d769c39a58.png"
    private const val IMG_INDUSTRIAL_BORAX_VAR = "$Q/afae35a5-77e3-471e-95a0-01f8b2796c68.png"
    private const val IMG_IGNEOUS_PERIDOTITE_XENOLITH_VAR = "$Q/eed50717-ab60-44ac-af37-2adefb950f57.png"
    private const val IMG_IGNEOUS_CARBONATITE_VAR = "$Q/80c83b14-4c1d-4acd-99c0-95bb0f9175c0.png"
    // Batch 61: igneous rock varieties
    private const val IMG_IGNEOUS_GRANODIORITE_VAR = "$Q/fc2db5a4-2b5f-4e6b-965b-75a8b1da9d5d.png"
    private const val IMG_IGNEOUS_NORITE_VAR = "$Q/61a9cd98-d92f-4204-a5e6-d12b9cb23abc.png"
    private const val IMG_IGNEOUS_MONZONITE_VAR = "$Q/a4570727-470d-4411-93c2-53d19f7235a8.png"
    private const val IMG_IGNEOUS_ANORTHOSITE_VAR = "$Q/5650b008-ebdc-4868-91e0-f812df116df7.png"
    private const val IMG_IGNEOUS_DIABASE_VAR = "$Q/2d2bfea1-d3f0-402c-abe9-56fde98f07b6.png"
    private const val IMG_IGNEOUS_OBSIDIAN_MIDNIGHT_LACE_VAR = "$Q/4f188ea6-c38b-43b5-b96c-c70d70ad4d01.png"
    private const val IMG_IGNEOUS_APLITE_VAR = "$Q/ff60f402-6340-4923-8c23-55e4638da395.png"
    private const val IMG_IGNEOUS_BASANITE_VAR = "$Q/691f5be5-482a-4d67-b5aa-f0a11c6aed79.png"
    // Batch 62: jasper, agate, mineral & fossil varieties
    private const val IMG_JASPER_WILLOW_CREEK_VAR = "$Q/ecdbf389-c882-4489-beb9-6bb47c4f7273.png"
    private const val IMG_JASPER_ZEBRA_VAR = "$Q/7bbb4f49-48ed-4d15-bb58-93222573aa2b.png"
    private const val IMG_LAGUNA_AGATE_VAR = "$Q/b2dfa4a3-2126-46fa-90f6-a05b19f42ef2.png"
    private const val IMG_LAKE_SUPERIOR_AGATE_SPEC_VAR = "$Q/47fddbbf-6229-4465-9566-e70414cd6204.png"
    private const val IMG_KEOKUK_GEODES_VAR = "$Q/bcf0dc2c-a411-408b-9216-9b944bb19d97.png"
    private const val IMG_MARCASITE_VAR = "$Q/523216bf-88c0-426c-880d-073627d6e95e.png"
    private const val IMG_LIBYAN_DESERT_GLASS_VAR = "$Q/f359923c-ce57-4026-ac2e-f90355156df5.png"
    private const val IMG_LEUCITE_VAR = "$Q/71ba82d1-3b9c-4c59-91e6-cc697628f164.png"
    // Batch 63: metamorphic & meteorite varieties
    private const val IMG_METAMORPHIC_BLUESCHIST_VAR = "$Q/32434f2a-d91e-4911-9836-9b5644a625c2.png"
    private const val IMG_METAMORPHIC_ECLOGITE_VAR = "$Q/07a9cad0-5960-4b47-a9b0-495dfb4fb89f.png"
    private const val IMG_METAMORPHIC_GREENSCHIST_VAR = "$Q/da167b8f-8cef-4837-82cd-24d3332ab683.png"
    private const val IMG_METAMORPHIC_HORNFELS_VAR = "$Q/4376b8ab-b2a3-4ed9-a969-322c61c9e1d0.png"
    private const val IMG_METAMORPHIC_PHYLLITE_VAR = "$Q/fba5f599-508e-449a-b644-2c9d0c2dc8ba.png"
    private const val IMG_METAMORPHIC_QUARTZITE_VAR = "$Q/4a6305d2-5e62-4145-9577-f7e93af18aaa.png"
    private const val IMG_METAMORPHIC_SOAPSTONE_VAR = "$Q/cf55afa9-c8e1-4739-8d93-b73a30e66676.png"
    private const val IMG_METEORITE_LUNAR_VAR = "$Q/676e0152-70bf-4ffc-ae86-b8e508d746e0.png"
    // Batch 64: millerite, mesosiderite, nepheline, midnight-lace, oxide varieties
    private const val IMG_MILLERITE_VAR = "$Q/32ef6f65-0f20-4beb-ac7c-f45ad5707181.png"
    private const val IMG_MESOSIDERITE_VAR = "$Q/bfde8182-b8e8-45cb-9ec2-aa1a451bd831.png"
    private const val IMG_NEPHELINE_VAR = "$Q/ed0e952b-b75f-41ba-9ce2-8f199fe73a53.png"
    private const val IMG_MIDNIGHT_LACE_OBSIDIAN_VAR = "$Q/d3fdfb2a-5640-478f-b0c8-2347459f146a.png"
    private const val IMG_OXIDE_BROOKITE_VAR = "$Q/3e94b18e-d58e-439f-b2b7-a52e53fafd4d.png"
    private const val IMG_OXIDE_GOETHITE_VAR = "$Q/8de8846e-577a-4c12-9e6d-e6e67c28afa1.png"
    private const val IMG_OXIDE_PYROLUSITE_VAR = "$Q/8dac50e5-9906-4ba1-97ed-b73e5c30b39d.png"
    private const val IMG_OXIDE_URANINITE_VAR = "$Q/f3678604-8912-4f65-aa47-622df6d84957.png"
    // Batch 65: petrified wood, opal & halite varieties
    private const val IMG_PETRIFIED_ARIZONA_RAINBOW_VAR = "$Q/7afdb035-e2b8-4986-afa1-3116acb566a5.png"
    private const val IMG_PETRIFIED_BLUE_FOREST_VAR = "$Q/b7f1facb-9e76-4ef7-a2e8-68f1ff28a988.png"
    private const val IMG_PETRIFIED_INDONESIAN_VAR = "$Q/de071e34-f4f6-42b9-9ecc-4028647e9ef7.png"
    private const val IMG_OPAL_KOROIT_VAR = "$Q/4baae01c-bd4e-4cde-bd6f-8ad381ace124.png"
    private const val IMG_OPAL_HONDURAN_VAR = "$Q/d4fd170d-2b94-4e6d-9810-8a907b4342e3.png"
    private const val IMG_OPAL_BRAZILIAN_VAR = "$Q/64465c7e-0ed8-4e37-80cd-59d2be588774.png"
    private const val IMG_PINK_HALITE_VAR = "$Q/883c09d7-135a-4856-9eb0-3dc58609e9cb.png"
    // Batch 66: corundum, mineral & fossil varieties
    private const val IMG_CORUNDUM_PADPARADSCHA_VAR = "$Q/8d8c02c3-5866-4051-8629-f80e9c19d2fc.png"
    private const val IMG_CHABAZITE_VAR = "$Q/b3d13e28-a20f-4792-8b88-2269a4111066.png"
    private const val IMG_CHALCANTHITE_VAR = "$Q/7d2aaf74-6167-454c-ab5f-2cc513f87acd.png"
    private const val IMG_CHALCOCITE_VAR = "$Q/8eedb24e-8038-48ea-8243-86e09bd34800.png"
    private const val IMG_CARBONATE_ANKERITE_VAR = "$Q/55436585-fa8f-43c0-902f-38d601ac5910.png"
    private const val IMG_CHALK_ROCK_VAR = "$Q/afd5dfa4-d0b4-469a-ac13-71c2b4e70edf.png"
    private const val IMG_DEINOTHERIUM_VAR = "$Q/aa913736-4633-459a-9a39-593528a479d6.png"
    private const val IMG_ECPHORA_VAR = "$Q/dceb5486-afaf-4303-a3dc-6753364ad55b.png"
    // Batch 67: polka-dot-agate, rainbow-lattice, wavellite, polyhalite, pentlandite, pyrrhotite, nummulites, platybelodon
    private const val IMG_POLKA_DOT_AGATE_VAR = "$Q/944e9698-7eda-4acb-8d48-b3ac9a45435b.png"
    private const val IMG_RAINBOW_LATTICE_VAR = "$Q/bdbbc433-8e8b-4ff4-9aea-036c02b4b0b5.png"
    private const val IMG_PHOSPHATE_WAVELLITE_VAR = "$Q/afb583f6-9ebe-43d3-b856-543604d720aa.png"
    private const val IMG_POLYHALITE_VAR = "$Q/6e1ad33e-8d63-4816-ae7d-2a7398bff79e.png"
    private const val IMG_PENTLANDITE_VAR = "$Q/fdab891a-38ee-4a01-8948-1a6333023cfb.png"
    private const val IMG_PYRRHOTITE_VAR = "$Q/d884e69b-4f76-4179-a1a9-32ce1c92757a.png"
    private const val IMG_NUMMULITES_VAR = "$Q/e37968ba-9a05-48e5-bd7d-b1f691f77e9b.png"
    private const val IMG_PLATYBELODON_VAR = "$Q/5d6cb3a8-0c5a-4512-91dd-c41ddc672d74.png"
    // Batch 68: fossil variety images
    private const val IMG_DIRE_WOLF_TOOTH_VAR = "$Q/fb1155ad-9356-4b8b-9ce4-fb3e9ec548c4.png"
    private const val IMG_FOSSIL_CEPHALOPOD_VAR = "$Q/79379d3d-f281-4d1c-82be-a66f9eaac6d2.png"
    private const val IMG_FOSSIL_PETRIFIED_WOOD_WHOLE_VAR = "$Q/742cc209-7ac9-4618-b4fe-f5bc07b006ae.png"
    private const val IMG_PELECYPOD_VAR = "$Q/0d9e8b62-288c-40b9-90f5-8e8976a0f0ec.png"
    private const val IMG_PLESIOSAUR_TOOTH_VAR = "$Q/f97ec1d5-1f45-477d-bdc6-844728bf18d8.png"
    private const val IMG_PLIOSAUR_TOOTH_VAR = "$Q/57858696-cd34-43dc-82fe-f1b9bece35e9.png"
    private const val IMG_PTYCHODUS_VAR = "$Q/149f8de9-0547-46e4-a7db-73718727a0d9.png"
    private const val IMG_PRODUCTUS_VAR = "$Q/d36ae83d-a6e6-4088-ad29-324f46eb5903.png"
    // Batch 69: fossil & gem varieties
    private const val IMG_FOSSIL_SABER_TOOTH_VAR = "$Q/21b1b9c9-ca17-466f-9349-39024fb92ae7.png"
    private const val IMG_FOSSIL_SOUP_VAR = "$Q/1e41d9fe-be1b-4140-b238-88cf9d3671f1.png"
    private const val IMG_GEM_PHENAKITE_VAR = "$Q/137e6d49-ef53-4bbf-b9c1-6a96b9f2f300.png"
    private const val IMG_GEM_SAPPHIRE_STAR_VAR = "$Q/48028c57-487f-4dc7-88b8-d620cab32b0b.png"
    private const val IMG_GEM_TAAFFEITE_VAR = "$Q/4c0904c9-99c1-4b3f-8456-85bc0a3e53c6.png"
    private const val IMG_CHLORASTROLITE_BASALT_ASSEMBLAGE_VAR = "$Q/e26b89e7-3006-4245-8435-8f9c66b860b6.png"
    private const val IMG_GIANT_SLOTH_CLAW_VAR = "$Q/d9b2a49b-3728-42d1-9104-041e9f36a996.png"
    private const val IMG_HELICOPRION_VAR = "$Q/080d9d4c-ef46-483a-be6d-906c857ddea1.png"
    // Batch 70: fossil & mineral varieties
    private const val IMG_INOCERAMUS_VAR = "$Q/d3b3460f-ba3b-4de6-8283-ac854c9a364b.png"
    private const val IMG_LINGULA_VAR = "$Q/7bb43ece-a0aa-4c3e-a018-d05298b4747c.png"
    private const val IMG_ICHTHYOSAUR_VERTEBRA_VAR = "$Q/7265a7ed-1fce-41cc-a740-2088466b64dd.png"
    private const val IMG_ICHTHYOSTEGA_VAR = "$Q/131b34ab-0761-4658-bb07-315b4bab129d.png"
    private const val IMG_HORSESHOE_CRAB_VAR = "$Q/97802f7c-d47f-4bef-a3bd-fec1b7a338cf.png"
    private const val IMG_HYBODUS_VAR = "$Q/3ca32224-df70-4dc9-8729-b7778d2af4dd.png"
    private const val IMG_KAOLINITE_VAR = "$Q/32963e0f-bdec-4aa6-bba4-7920228b65e3.png"
    private const val IMG_KERNITE_VAR = "$Q/dbdb17e8-1549-4771-b0ce-63590c2e3f05.png"

    // Batch 85: jaspillite (jasper-rich banded iron formation)
    // Regenerated from 3 reference photos of Michigan jaspillite showing red jasper bands and dark iron layers.
    private const val IMG_JASPILLITE = "$Q/cb9506e2-6663-4da9-abcb-a4f888dc9378.png"

    // ── Mohs hardness scale infographic ──
    const val IMG_MOHS_SCALE_INFOGRAPHIC = "$Q/032a05b9-b2df-49d4-88ba-b285e425b842.png"

    // ── Additional Mohs hardness scale educational infographics ──
    const val IMG_MOHS_SCRATCH_TEST_CHART = "$Q/9c2c9cd9-619e-479c-9740-b3ae7d4a0f28.png"
    const val IMG_MOHS_ABSOLUTE_HARDNESS = "$Q/c1861127-c22a-4015-bd31-2ba78e2b59f8.png"
    const val IMG_MOHS_FIELD_TEST_KIT = "$Q/dd8a4610-460f-445c-bab1-18cb6c7cb852.png"
    const val IMG_MOHS_SCRATCH_TEST_STEPS = "$Q/0b8316bb-3955-42c5-8326-d0db85c8ceb6.png"
    const val IMG_MOHS_COMMON_ITEMS = "$Q/01937ba3-d43f-4360-b2ca-64685ca3a862.png"
    const val IMG_MOHS_ALL_MINERALS_GRID = "$Q/87a1602c-c820-468e-8ec2-0249f819bb28.png"

    // ── 4-image upgrade: museum, wild, cabochon photos for key specimens ──
    private const val IMG_AMETHYST_MUSEUM = "$Q/86a27f10-5b3d-495f-afd0-bb3e164a532a.png"
    private const val IMG_AMETHYST_CABOCHON = "$Q/52550d08-f822-4d2e-9c47-c524ef2343ef.png"
    private const val IMG_AMETHYST_WILD = "$Q/4fb0d8cc-1070-4e43-8a1e-69775fc6b306.png"
    // ── NEW: Chevron Amethyst photos (cabochon is the former misidentified amethyst wild image) ──
    private const val IMG_CHEVRON_AMETHYST = "$Q/b72e96f0-0a8a-4b76-b9e1-d0c6f3d99862.png"
    private const val IMG_CHEVRON_AMETHYST_WILD = "$Q/118f5903-298f-459f-99a7-f28c2572bdb4.png"
    private const val IMG_CHEVRON_AMETHYST_MUSEUM = "$Q/5327d511-c691-4c48-8189-8a35a9c00eea.png"
    private const val IMG_CHEVRON_AMETHYST_CABOCHON = "$Q/34f740c5-6da6-44e4-ba60-776996bf93ed.png"
    private const val IMG_QUARTZ_WILD_2 = "$Q/1a851fa4-191b-4c6f-b843-b0c2c5f7ce0a.png"
    private const val IMG_QUARTZ_MUSEUM = "$Q/6fe3c5d9-3cf1-41e0-b6e2-1f3e859e587e.png"
    private const val IMG_QUARTZ_CABOCHON = "$Q/8f82bd10-5b5a-402e-ac06-56b0ec0fb62c.png"
    private const val IMG_MALACHITE_WILD = "$Q/ee666d30-41e9-42d2-a95d-4dc73ab407ea.png"
    private const val IMG_MALACHITE_MUSEUM = "$Q/8094e28f-1ef0-4ddf-8997-a8a8c0781e64.png"
    private const val IMG_MALACHITE_CABOCHON = "$Q/b9c71449-64c6-454b-88e6-10ff23e954f8.png"
    private const val IMG_TURQUOISE_WILD = "$Q/a0c46ec6-fcd8-446e-84a0-f21f3f391bae.png"
    private const val IMG_TURQUOISE_MUSEUM = "$Q/3c5fa26d-4001-49b6-aedb-d5c3f67850c0.png"
    private const val IMG_TURQUOISE_CABOCHON = "$Q/785f5d2a-35eb-45e7-a0bb-6ae6d14bfe9e.png"
    private const val IMG_PYRITE_WILD = "$Q/3e261041-bb48-4cb1-8e9d-2673aa349752.png"
    private const val IMG_PYRITE_MUSEUM = "$Q/206fa4cd-f730-47e4-a6e0-cca8524ed47b.png"
    private const val IMG_PYRITE_CABOCHON = "$Q/39b1a900-9ed7-44f9-b201-d3de889c5531.png"
    private const val IMG_AGATE_WILD = "$Q/f559fd90-43f6-4404-907c-a5a96cd7b200.png"
    private const val IMG_AGATE_MUSEUM = "$Q/b2f3a061-c6a9-4859-919d-1cfeb6844732.png"
    private const val IMG_RHODOCHROSITE_WILD = "$Q/cf152232-ca5f-406e-8731-592fdf5689b9.png"
    private const val IMG_RHODOCHROSITE_MUSEUM = "$Q/2bc3e70c-3634-4208-9d30-6b7ae3207a47.png"
    private const val IMG_RHODOCHROSITE_CABOCHON = "$Q/7ed46370-6f65-4140-92fa-3f61588b3edf.png"
    private const val IMG_FLUORITE_WILD_2 = "$Q/82e9e762-e46c-41e2-acf9-52ffa6069ae1.png"
    private const val IMG_FLUORITE_MUSEUM = "$Q/ffb1df5e-ab15-46a4-b866-0ff27447bcf1.png"
    private const val IMG_FLUORITE_CABOCHON = "$Q/c5dd1ac9-be9a-4c9a-b010-a1d58d5821d8.png"
    private const val IMG_GARNET_WILD_2 = "$Q/71f1f25c-f6e8-410a-ac8f-8fa6ce8752b9.png"
    private const val IMG_GARNET_MUSEUM = "$Q/c21c4cf8-c147-41a0-bf2c-0da05b60fd9f.png"
    private const val IMG_GARNET_CABOCHON = "$Q/d00075f1-5968-4673-9925-ba814737e860.png"
    private const val IMG_EMERALD_WILD = "$Q/79d8db0f-ba37-4bb8-9a6e-5b6140c9f3dc.png"
    private const val IMG_EMERALD_MUSEUM = "$Q/ab43ce6d-09b9-487c-b898-f7722cd8aea9.png"
    private const val IMG_EMERALD_CABOCHON = "$Q/8898d6ec-18c2-4327-9b1d-25975f4f541c.png"

    // ── 4-image upgrade batch 2: rough, wild, museum, cabochon for 20 more specimens ──
    private const val IMG_AZURITE_WILD = "$Q/d9ce31da-0acb-4118-8542-65ea1231d4ab.png"
    private const val IMG_AZURITE_MUSEUM = "$Q/7ce9b3f9-91b7-41ca-b0b9-485b3e99eef1.png"
    private const val IMG_AZURITE_CABOCHON = "$Q/556b2c14-a0e5-465b-8f7a-98ffe46b40d1.png"
    private const val IMG_AZURITE_ROUGH = "$Q/ed988125-737e-433a-a221-f4b58d4ca323.png"
    private const val IMG_BENITOITE_WILD = "$Q/0600e897-fdf6-414c-9314-89ef4a543626.png"
    private const val IMG_BENITOITE_MUSEUM = "$Q/b3ed0002-8a7a-48ae-b316-89e80e52aeda.png"
    private const val IMG_BENITOITE_CABOCHON = "$Q/0b8e6600-e07c-4407-a81f-ce486bf69f2e.png"
    private const val IMG_BENITOITE_ROUGH = "$Q/6509668e-bc91-4d94-9c59-c954519727ea.png"
    private const val IMG_ALEXANDRITE_WILD = "$Q/81aff6f8-2a9f-4cba-abcb-915dbb268ee6.png"
    private const val IMG_ALEXANDRITE_MUSEUM = "$Q/677abfeb-241c-4211-a376-d53e880643df.png"
    private const val IMG_ALEXANDRITE_CABOCHON = "$Q/02c01161-0e62-4f27-9618-9728403d2659.png"
    private const val IMG_ALEXANDRITE_ROUGH = "$Q/471b5015-d316-4f31-9afd-1352ce28abb7.png"
    private const val IMG_AMBER_WILD = "$Q/056284fc-758c-4cf8-ae96-cc74d2af23d5.png"
    private const val IMG_AMBER_MUSEUM = "$Q/78cf4a9d-1ce7-4630-b11a-a605e0e45d31.png"
    private const val IMG_AMBER_CABOCHON = "$Q/3de2437d-0a98-4904-87b8-0ab8ff160804.png"
    private const val IMG_AMBER_ROUGH = "$Q/be138016-4dce-4f45-8a68-50cf14d54db8.png"
    private const val IMG_AQUAMARINE_WILD = "$Q/17659834-e984-4862-b840-cdebb88cd144.png"
    private const val IMG_AQUAMARINE_MUSEUM = "$Q/9b07595e-89e3-47d3-9eed-2a45704091e4.png"
    private const val IMG_AQUAMARINE_CABOCHON = "$Q/8f2b29a8-c1b1-48dd-b475-0153047d734b.png"
    private const val IMG_AQUAMARINE_ROUGH = "$Q/a59a068b-6150-4efd-bdf7-42dfe47eb2d8.png"
    private const val IMG_APATITE_WILD = "$Q/568c4bbf-5354-4400-b480-02b352f2243f.png"
    private const val IMG_APATITE_MUSEUM = "$Q/31a9d6ec-0a98-4573-8e16-b0363db5b5af.png"
    private const val IMG_APATITE_CABOCHON = "$Q/c8f7c313-8a0d-4aa5-a350-9346c5dfd3fb.png"
    private const val IMG_APATITE_ROUGH = "$Q/6142a918-630a-4f59-923e-8c08531ee002.png"
    private const val IMG_BARITE_WILD = "$Q/cefa8530-63cf-4848-9fb0-974a32ab1012.png"
    private const val IMG_BARITE_MUSEUM = "$Q/048556c1-f794-4549-956e-53f525aad506.png"
    private const val IMG_BARITE_CABOCHON = "$Q/f056b356-a8f6-44d1-8415-212d8ba1975f.png"
    private const val IMG_BORNITE_WILD = "$Q/d29d9731-72fc-4cf8-bcfb-a19d8d8365e5.png"
    private const val IMG_BORNITE_MUSEUM = "$Q/106f5699-08fb-4938-ab8e-3b1ea398cbbb.png"
    private const val IMG_BLOODSTONE_WILD = "$Q/67bec27e-bff2-4dd9-a528-8fb376a15457.png"
    private const val IMG_BLOODSTONE_CABOCHON = "$Q/dd2ef240-90fb-473b-8ea3-641fde1c3292.png"
    private const val IMG_CARNELIAN_WILD = "$Q/be9e5a7d-885c-48b7-83b4-e7c34275bc45.png"
    private const val IMG_CARNELIAN_MUSEUM = "$Q/85939169-da1a-4b97-b0c9-6b2e37318710.png"
    private const val IMG_CARNELIAN_CABOCHON = "$Q/f415345c-629b-434f-906d-40eb2281cc9e.png"
    private const val IMG_CHAROITE_WILD = "$Q/610edf43-ef06-4179-ad22-82aa05151fff.png"
    private const val IMG_CHAROITE_MUSEUM = "$Q/11fe3e55-89c1-4e4d-adb2-01a06ae474a4.png"
    private const val IMG_CHAROITE_CABOCHON = "$Q/c1e612df-84f6-46dd-9767-3eb4b481c3df.png"
    private const val IMG_CHALCOPYRITE_WILD = "$Q/af3ff9ac-68ae-4965-8cf7-b6517c135efd.png"
    private const val IMG_CHALCOPYRITE_MUSEUM = "$Q/6db3bf7a-31b0-4188-979e-e8fbcbb1e68a.png"
    private const val IMG_CROCOITE_WILD = "$Q/6a4d3725-60d9-4160-8cb1-11062f097a25.png"
    private const val IMG_CROCOITE_MUSEUM = "$Q/b0be6150-9ff1-4b1b-96e1-893a55951eb2.png"
    private const val IMG_DIOPTASE_WILD = "$Q/9b673741-a31c-4299-aaeb-4416416c5086.png"
    private const val IMG_DIOPTASE_MUSEUM = "$Q/45730b2a-29d7-4af2-9147-aefc6eb9f253.png"
    private const val IMG_DIOPTASE_CABOCHON = "$Q/fc5065e8-b9b9-4fa2-b80a-0687484844c2.png"
    private const val IMG_HEMATITE_WILD = "$Q/943def19-6631-4ff5-b44a-5261dd8b91b0.png"
    private const val IMG_HEMATITE_MUSEUM = "$Q/13ab54e1-e834-4ee6-a7a8-99b5a583797a.png"
    private const val IMG_HEMATITE_CABOCHON = "$Q/06452a97-8d43-4260-a912-f061c51d6691.png"
    private const val IMG_LABRADORITE_WILD_NEW = "$Q/0c2a1e41-7594-4c42-b5bd-e1a559626e5f.png"
    private const val IMG_LABRADORITE_MUSEUM = "$Q/f608c35b-af49-4888-b281-b1359e2b97f2.png"
    private const val IMG_LABRADORITE_CABOCHON = "$Q/4984334e-fd43-4402-907f-10d9b056b6d7.png"
    private const val IMG_CASSITERITE_WILD = "$Q/7b9e50a1-7a06-44ba-b5fa-6ba821d84d1a.png"
    private const val IMG_CASSITERITE_MUSEUM = "$Q/0743fdff-362e-4814-8635-37b55ca0952d.png"
    private const val IMG_SPHALERITE_WILD = "$Q/acf65e6f-f48f-4906-ac03-ba07a426e718.png"
    private const val IMG_SPHALERITE_MUSEUM = "$Q/b6990b72-cce4-458c-9806-b14049a34ac2.png"
    private const val IMG_SPHALERITE_CABOCHON = "$Q/3a1c092c-476f-4a96-8cbc-c2e0347c4868.png"
    private const val IMG_AMAZONITE_WILD = "$Q/f858a91e-5503-41b7-b74d-065ea8e91b7b.png"
    private const val IMG_AMAZONITE_MUSEUM = "$Q/60707e1c-85f7-4ed8-a955-1308f7997a4d.png"
    private const val IMG_AMAZONITE_CABOCHON = "$Q/5c3de826-e5ba-4f5b-b2d1-18b8d019353e.png"
    private const val IMG_OPAL_WILD = "$Q/c98429d1-7dff-4bb5-8b7c-2cf4e396e75f.png"
    private const val IMG_OPAL_CABOCHON_NEW = "$Q/f61392e3-9637-42bb-b3f0-61e728399fb0.png"
    private const val IMG_OPALITE = "$Q/c92fe2cb-a851-4c39-a538-aecdc2a9bdf0.png"

    // ── 4-image upgrade batch 3: wild, museum, cabochon for 25 more specimens ──
    private const val IMG_CITRINE_WILD = "$Q/3f71b4e2-555e-47fc-8b69-3c365c78692d.png"
    private const val IMG_CITRINE_MUSEUM = "$Q/e39d5e7d-4379-42b1-be4e-e5ac851c0c3d.png"
    private const val IMG_CITRINE_CABOCHON = "$Q/918c1155-377f-41eb-9da7-5cf80ab9f8d5.png"
    private const val IMG_AVENTURINE_WILD = "$Q/f8359ed2-44bd-47ab-a8aa-38028f13abbc.png"
    private const val IMG_AVENTURINE_MUSEUM = "$Q/64604e25-e997-4ed5-9405-0bbeb2ea4a11.png"
    private const val IMG_AVENTURINE_CABOCHON = "$Q/8208bd61-5573-4ccf-946d-8f3f07e3b5ef.png"
    private const val IMG_CHRYSOCOLLA_WILD = "$Q/4f0f504c-1895-4b22-a620-367e612e2412.png"
    private const val IMG_CHRYSOCOLLA_MUSEUM = "$Q/18f8b86b-966b-4cea-b625-f23b21db0af8.png"
    private const val IMG_CHRYSOCOLLA_CABOCHON = "$Q/bf18cbea-3327-44e2-a130-445fc26e89d4.png"
    private const val IMG_CHRYSOPRASE_WILD = "$Q/2b07e573-eaf7-4906-9fe4-713e0ff01744.png"
    private const val IMG_CHRYSOPRASE_MUSEUM = "$Q/6fa44f4f-5ef1-4249-8240-a5e3314b4513.png"
    private const val IMG_CHRYSOPRASE_CABOCHON = "$Q/14ea58a6-e1fe-4cd4-9faa-8ee0696d1dcd.png"
    private const val IMG_ARAGONITE_WILD = "$Q/aecf0392-ba06-47d7-8de4-7f4802a97911.png"
    private const val IMG_ARAGONITE_MUSEUM = "$Q/930eb87d-5bc9-43d2-a009-5bb6ca3c0970.png"
    private const val IMG_ARAGONITE_CABOCHON = "$Q/9d959033-ab70-4750-8da3-002e13fc6239.png"
    private const val IMG_BISMUTH_WILD = "$Q/2e4a2af4-5837-410b-9743-10d5dc4db5b8.png"
    private const val IMG_BISMUTH_MUSEUM = "$Q/1bf0eb95-74f6-4421-a6de-9fb624092afc.png"
    private const val IMG_BISMUTH_CABOCHON = "$Q/aff8d03d-5936-4eb0-858b-967e8aedc467.png"
    private const val IMG_APOPHYLLITE_WILD = "$Q/36e1c013-6f15-477c-b80e-0dfed533b617.png"
    private const val IMG_APOPHYLLITE_MUSEUM = "$Q/8ce80c32-ac95-4c78-9345-e101122d5859.png"
    private const val IMG_APOPHYLLITE_CABOCHON = "$Q/36cea43a-5024-4509-889c-79561701f6e3.png"
    private const val IMG_AUTUNITE_WILD = "$Q/a1608f55-c4cb-4fcc-a926-df4d7736353a.png"
    private const val IMG_AUTUNITE_MUSEUM = "$Q/af0298dd-acad-4e4a-a791-9584709cae8c.png"
    private const val IMG_AUTUNITE_CABOCHON = "$Q/0947e55e-3346-4fb0-86ff-a7402c06a500.png"
    private const val IMG_ADAMITE_WILD = "$Q/64d87aec-a81a-4d3d-b467-354a553a798f.png"
    private const val IMG_ADAMITE_MUSEUM = "$Q/8b33a839-3506-45fd-ac9d-fc07f6193947.png"
    private const val IMG_ADAMITE_CABOCHON = "$Q/77d3e892-10f2-41ac-94bd-073cf3505958.png"
    private const val IMG_CHRYSOBERYL_WILD = "$Q/df811c50-63b3-4955-b2b9-a072f00757cd.png"
    private const val IMG_CHRYSOBERYL_MUSEUM = "$Q/b1e222a7-6fb9-43eb-9f55-a857dea6f7d0.png"
    private const val IMG_CHRYSOBERYL_CABOCHON = "$Q/53d246e9-e9c4-4cb8-be81-7e05b9898b6b.png"
    private const val IMG_ANDALUSITE_WILD = "$Q/c67322b3-68b1-4f78-a23a-21def3023f2e.png"
    private const val IMG_ANDALUSITE_MUSEUM = "$Q/8091e400-a0a8-474e-9e37-c9740ef5385c.png"
    private const val IMG_ANDALUSITE_CABOCHON = "$Q/1c92af4f-0f98-41e5-9119-970d27100202.png"
    private const val IMG_AXINITE_WILD = "$Q/630f993f-994b-4acc-b3db-4516c06fec8d.png"
    private const val IMG_AXINITE_MUSEUM = "$Q/fb21b7f1-15b3-4b97-916f-b5861d72f48e.png"
    private const val IMG_AXINITE_CABOCHON = "$Q/0523f428-21ec-49c3-b855-f22e8fbfc83e.png"
    private const val IMG_CORUNDUM_WILD = "$Q/9f381e94-8655-4529-8be4-d333f993b557.png"
    private const val IMG_CORUNDUM_MUSEUM = "$Q/d57e6af1-2b5d-4900-9e80-e7fcb1ab5bdd.png"
    private const val IMG_CORUNDUM_CABOCHON = "$Q/78171047-c8be-4443-9f02-b65794c6dc7a.png"
    private const val IMG_DIAMOND_WILD = "$Q/2e186820-459b-41c7-ba48-18e946674b96.png"
    private const val IMG_DIAMOND_MUSEUM = "$Q/33a4c7d6-9156-4672-973e-7f5005ce7240.png"
    private const val IMG_DIAMOND_CABOCHON = "$Q/a97c987e-8bd9-4afe-954e-78edd446f431.png"
    private const val IMG_TOPAZ_WILD = "$Q/d687677a-970b-4fd1-9268-1575436db25f.png"
    private const val IMG_TOPAZ_MUSEUM = "$Q/6e44ca17-3bf6-412c-809e-39a2740f4e9e.png"
    private const val IMG_TOPAZ_CABOCHON = "$Q/5f022fce-e55b-419b-b655-807936e74015.png"
    private const val IMG_TOURMALINE_WILD = "$Q/5d40ff2a-84b7-43de-8074-95374db722ee.png"
    private const val IMG_TOURMALINE_MUSEUM = "$Q/571a3e5b-0b79-4f89-bae6-864d99198815.png"
    private const val IMG_TOURMALINE_CABOCHON = "$Q/152208f0-db0e-4fff-a13e-d9e0021aaeda.png"
    private const val IMG_JADE_WILD = "$Q/cbfb1e2c-99e7-4d41-a203-120aff5cd602.png"
    private const val IMG_JADE_MUSEUM = "$Q/38669d56-5a9e-4a06-b867-6f289b96173d.png"
    private const val IMG_JADE_CABOCHON = "$Q/9fad2050-460d-4534-8ad5-f73ea1e97d13.png"
    private const val IMG_CALCITE_WILD = "$Q/db540e16-88b0-4059-b8ab-929f4478c780.png"
    private const val IMG_CALCITE_MUSEUM = "$Q/5b96ce4f-ede3-4aaf-a9be-6fc9526e604f.png"
    private const val IMG_CALCITE_CABOCHON = "$Q/e8028405-0ee3-44a4-a929-588ed7f97e40.png"
    private const val IMG_CELESTINE_WILD = "$Q/9e2e13da-f4f5-43d0-84ab-6f93a4d802a4.png"
    private const val IMG_CELESTINE_MUSEUM = "$Q/433adae3-3cb4-4aa9-8017-5f5279be071e.png"
    private const val IMG_CELESTINE_CABOCHON = "$Q/094bd0bc-253e-4649-8fd5-4bae144ef45b.png"
    private const val IMG_CELESTINE_BLUE_ROUGH = "$Q/b0b090ac-7c50-4f04-9ff2-c047b5dd00e0.png"
    private const val IMG_CELESTINE_BLUE_WILD = "$Q/eaa6b458-d06f-4871-83af-4d7b0a1898a9.png"
    private const val IMG_CELESTINE_BLUE_MUSEUM = "$Q/8d292bd2-3a8c-4cb1-83b1-367fad578419.png"
    private const val IMG_CINNABAR_WILD = "$Q/9f335638-a154-4d4c-83ab-e258c53e5952.png"
    private const val IMG_CINNABAR_MUSEUM = "$Q/3109d07e-4ef9-4d36-9a6c-c7cc5addfe81.png"
    private const val IMG_CINNABAR_CABOCHON = "$Q/1e4612d7-0511-425c-af8c-7e53773693dc.png"
    private const val IMG_CERUSSITE_WILD = "$Q/f1c3a3dc-620e-4455-a0ee-fdd880e9e6a2.png"
    private const val IMG_CERUSSITE_MUSEUM = "$Q/4537bfd2-3317-4b9e-b523-b689b5e495b6.png"
    private const val IMG_CERUSSITE_CABOCHON = "$Q/50821335-50ad-45e1-ba2a-49977e46e12a.png"
    private const val IMG_COBALTITE_WILD = "$Q/e96223c0-2f68-46a4-ad88-5b18e6a533ea.png"
    private const val IMG_COBALTITE_MUSEUM = "$Q/f283ca0f-e65b-4a73-9713-2cb8ea42e108.png"
    private const val IMG_COBALTITE_CABOCHON = "$Q/30e86f11-6b29-4406-a3b1-e564dee89151.png"
    private const val IMG_BRONZITE_WILD = "$Q/0c9abae6-75d9-4b33-a714-fd33640c7295.png"
    private const val IMG_BRONZITE_MUSEUM = "$Q/4a8026f2-6039-41e5-b867-3538fcfea536.png"
    private const val IMG_BRONZITE_CABOCHON = "$Q/05dc3e4e-cc35-40c2-870f-4ff57ab57e27.png"
    private const val IMG_BROCHANTITE_WILD = "$Q/c8805b46-74c5-44e0-a03d-2f5694c1c2d7.png"
    private const val IMG_BROCHANTITE_MUSEUM = "$Q/1a6af88f-2675-4da5-b879-8db8d61d691a.png"
    private const val IMG_BROCHANTITE_CABOCHON = "$Q/6b21a5d9-2b83-4873-ad5d-1f5a20b989f3.png"
    private const val IMG_CHROMITE_WILD = "$Q/6390bbbf-dbf6-4ef4-bdbf-cb1b321cb231.png"
    private const val IMG_CHROMITE_MUSEUM = "$Q/63d1ebca-cc5f-4be6-b705-f258b7fb3784.png"
    private const val IMG_CHROMITE_CABOCHON = "$Q/f8583e3f-0691-46df-9353-ddad29cb7973.png"

    // ── 4-image upgrade batch 3: wild, museum, cabochon for 24 more specimens ──
    private const val IMG_AMETRINE_WILD = "$Q/e08a9904-84fd-425c-b000-d40d7d3744cb.png"
    private const val IMG_AMETRINE_MUSEUM = "$Q/e7e45e9f-3bb8-4492-8a6a-0bc28ee44e84.png"
    private const val IMG_AMETRINE_CABOCHON = "$Q/5efc28b3-82b8-4b49-9d64-a774b8515d32.png"
    private const val IMG_CUPRITE_WILD = "$Q/8cf489b2-5537-4f5d-924e-bec414d07a9b.png"
    private const val IMG_CUPRITE_MUSEUM = "$Q/a8b81ee7-4f4f-4594-a28d-9de0046eed81.png"
    private const val IMG_CUPRITE_CABOCHON = "$Q/ca9468e6-eb61-4dc2-b502-58794c2f305b.png"
    private const val IMG_DANBURITE_WILD = "$Q/d3937d88-5d33-49cf-ba49-c99d3ca2c8bb.png"
    private const val IMG_DANBURITE_MUSEUM = "$Q/224a2849-f80e-4693-985e-5d39b95afc95.png"
    private const val IMG_DANBURITE_CABOCHON = "$Q/ce9e0461-a0ca-40b7-8b56-f9742811bd41.png"
    private const val IMG_DESERT_ROSE_WILD = "$Q/28fac126-3f06-4108-93ee-ccceab09e200.png"
    private const val IMG_DESERT_ROSE_MUSEUM = "$Q/5e51ed26-0072-4ddd-9636-0705673d63a5.png"
    private const val IMG_DESERT_ROSE_CABOCHON = "$Q/7df8248a-ae2f-446b-81c6-4c8acf345190.png"
    private const val IMG_GALENA_WILD_2 = "$Q/32a60f1b-6a31-4346-968a-6ade2e2cc75f.png"
    private const val IMG_GALENA_MUSEUM = "$Q/78948763-801c-47ab-b1da-eb6d72536d27.png"
    private const val IMG_GALENA_CABOCHON = "$Q/ea3c8276-2125-425e-9b3f-40a891d22b0d.png"
    private const val IMG_GALENA_CABOCHON_NEW = "$Q/2621f101-2090-4994-9367-32d19f2f5a7a.png"
    private const val IMG_GOETHITE_WILD = "$Q/cf08ee28-86ec-466e-8636-3037288c2148.png"
    private const val IMG_GOETHITE_MUSEUM = "$Q/082a2e81-a86e-40e4-8838-bf8ad8b94b79.png"
    private const val IMG_GOETHITE_CABOCHON = "$Q/051078d7-e623-4878-9b78-215115b9a2f4.png"
    private const val IMG_KYANITE_WILD = "$Q/a5e2f8da-1ced-4566-99ff-0f829263dca7.png"
    private const val IMG_KYANITE_MUSEUM = "$Q/c66ca0de-2bb2-4deb-b6b5-22aefa61d243.png"
    private const val IMG_KYANITE_CABOCHON = "$Q/da43627d-99cb-40ce-847e-b5ccefa30d75.png"
    private const val IMG_LEPIDOLITE_WILD = "$Q/d37d569d-2de6-4eed-b121-7158f712cb08.png"
    private const val IMG_LEPIDOLITE_MUSEUM = "$Q/42e9c1c7-c584-4d03-8a0e-ab89ca56939b.png"
    private const val IMG_LEPIDOLITE_CABOCHON = "$Q/b58e79fd-612f-4870-95a2-59f46271bc7e.png"
    private const val IMG_MAGNETITE_WILD = "$Q/265c8915-1742-4346-a120-2b22abc5aeaf.png"
    private const val IMG_MAGNETITE_MUSEUM = "$Q/d38ce19a-cfc5-4bcd-aeba-f66f79577367.png"
    private const val IMG_MAGNETITE_CABOCHON = "$Q/dd620fe3-5de9-4898-bf29-abdffdad57c5.png"
    private const val IMG_MORGANITE_WILD = "$Q/3fa15d91-bafd-4d52-86bc-fead86c66e33.png"
    private const val IMG_MORGANITE_MUSEUM = "$Q/67d3b9dc-10c1-41d0-b310-29d5a91e5cdc.png"
    private const val IMG_MORGANITE_CABOCHON = "$Q/9b2a280b-ba4f-478e-a004-df9c0393ecc4.png"
    private const val IMG_OBSIDIAN_WILD = "$Q/bfd413da-38e1-41d2-a201-9480f3d38f7c.png"
    private const val IMG_OBSIDIAN_MUSEUM = "$Q/1c4d3309-81a2-4795-8b86-4b93938b473a.png"
    private const val IMG_OBSIDIAN_CABOCHON = "$Q/b830eefa-76d7-4ba7-abae-c8286abab20e.png"
    private const val IMG_PREHNITE_WILD = "$Q/5f711d4f-d5c4-4c88-9ea8-ec0a49408653.png"
    private const val IMG_PREHNITE_MUSEUM = "$Q/1cfdd559-a251-443c-ad95-11db48cd58f5.png"
    private const val IMG_PREHNITE_CABOCHON = "$Q/be2d1434-fde2-422f-b260-1db558bc2b1a.png"
    private const val IMG_RHODONITE_WILD = "$Q/cf97e421-6c7c-4ee7-be50-52f7acaa23a6.png"
    private const val IMG_RHODONITE_MUSEUM = "$Q/8cff1964-eb2e-4891-aa53-4d42c13b686c.png"
    private const val IMG_RHODONITE_CABOCHON = "$Q/13f1bb71-5497-4a9d-a7f4-5bcb14a97a3e.png"
    private const val IMG_RUTILE_WILD = "$Q/4c86742b-fdee-460d-8e9d-8a8002f8ec54.png"
    private const val IMG_RUTILE_MUSEUM = "$Q/2faac2df-9d80-49dc-bd77-08c7671d6b80.png"
    private const val IMG_RUTILE_CABOCHON = "$Q/e1c6ecec-3709-4051-8e2f-663bad36e46e.png"
    private const val IMG_SCHEELITE_WILD = "$Q/185816d4-9392-4644-81a8-57e764a35fc7.png"
    private const val IMG_SCHEELITE_MUSEUM = "$Q/35695417-ea99-4299-a0ba-55ae050a750b.png"
    private const val IMG_SCHEELITE_CABOCHON = "$Q/21a877ae-96c7-45e6-bc1a-73f602cfcf9d.png"
    private const val IMG_SERPENTINE_WILD = "$Q/d782c848-7e19-44ae-acca-02de4e093a4b.png"
    private const val IMG_SERPENTINE_MUSEUM = "$Q/23125b1d-ae17-4f84-b554-09cef126d1ff.png"
    private const val IMG_SERPENTINE_CABOCHON = "$Q/ec48b81c-b918-4aad-8136-0f2b158f7bbe.png"
    private const val IMG_SMITHSONITE_WILD = "$Q/1fc3caf7-fcca-4f86-ad2b-168b1a5bf710.png"
    private const val IMG_SMITHSONITE_MUSEUM = "$Q/e05ad053-7c95-48e8-95bc-b03fe2107e21.png"
    private const val IMG_SMITHSONITE_CABOCHON = "$Q/872a8ade-99f4-4584-ad1d-61461a3edafe.png"
    private const val IMG_SODALITE_WILD = "$Q/6d1cf9f6-02a3-4c0a-b71a-bf028b7ed526.png"
    private const val IMG_SODALITE_MUSEUM = "$Q/2ceb8f6f-0e89-4587-bc8a-88562c3c399d.png"
    private const val IMG_SODALITE_CABOCHON = "$Q/a1f80ad6-cffc-4230-9470-10efce4343f8.png"
    private const val IMG_STIBNITE_WILD = "$Q/fd14154b-11c7-49a1-9bc2-6d2bc24bcff5.png"
    private const val IMG_STIBNITE_MUSEUM = "$Q/87a606b0-800b-4129-9ef1-f152d2318557.png"
    private const val IMG_STIBNITE_CABOCHON = "$Q/6d160efa-25ea-4f36-9c54-8eb76e7b57cc.png"
    private const val IMG_STILBITE_WILD = "$Q/f61750c3-10b4-4214-9a6b-d22e92bd28e6.png"
    private const val IMG_STILBITE_MUSEUM = "$Q/1561ece0-3a39-47bf-bfb2-832512b4a21f.png"
    private const val IMG_STILBITE_CABOCHON = "$Q/616cf0f4-4689-455a-b8f7-235e72faa52a.png"
    private const val IMG_VARISCITE_WILD = "$Q/59acabe5-0134-4a4f-ba7a-f737c271dce1.png"
    private const val IMG_VARISCITE_MUSEUM = "$Q/1db9f5fe-4ccb-47c7-9df5-4a966d3e8fe3.png"
    private const val IMG_VARISCITE_CABOCHON = "$Q/4b268335-831f-440b-95b2-e7c72edc2894.png"
    private const val IMG_ZIRCON_WILD = "$Q/8244a24b-00b4-4706-a075-144eb82835b9.png"
    private const val IMG_ZIRCON_MUSEUM = "$Q/cd2206f3-b8d5-4f4c-bea4-7ce25f237506.png"
    private const val IMG_ZIRCON_CABOCHON = "$Q/3973dcb8-6495-466b-84b3-b2de09b7583a.png"
    private const val IMG_ZIRCON_BROWN_MAIN = "$Q/b527e923-d666-4244-8d49-c260546cdc91.png"
    private const val IMG_ZIRCON_BROWN_ROUGH = "$Q/3c9a88a2-8cc4-46b7-8915-ff535a50e206.png"
    private const val IMG_ZOISITE_WILD = "$Q/4a0ebdb9-7b81-49da-896b-1031a1b3c7b6.png"
    private const val IMG_ZOISITE_MUSEUM = "$Q/3197b675-e056-4ab7-bee6-d008a4cca87f.png"
    private const val IMG_ZOISITE_CABOCHON = "$Q/1b517b77-4e72-48fa-8d65-0456416f7831.png"

    // ── 4-image upgrade batch 4: wild, museum, cabochon for 9 more specimens ──
    private const val IMG_AMMONITE_WILD = "$Q/d44246c4-4b65-4dbb-9cd2-ed5c88bf105a.png"
    private const val IMG_AMMONITE_MUSEUM = "$Q/45b60d05-72fe-4b7e-ab26-f47db150c433.png"
    private const val IMG_AMMONITE_CABOCHON = "$Q/a90136a0-8bed-478b-acb4-a857cdfd5cc1.png"
    private const val IMG_BASALT_WILD = "$Q/55faa98d-b28d-446c-9309-7ccde78257ab.png"
    private const val IMG_BASALT_MUSEUM = "$Q/85a9ce1b-70df-4d85-949d-90bc9089b0da.png"
    private const val IMG_BASALT_CABOCHON = "$Q/5ed200ce-de08-4ce1-83be-f35a4cfa7841.png"
    private const val IMG_CRINOID_WILD = "$Q/c22aab5d-6d31-425d-bb08-f9d9942622bb.png"
    private const val IMG_CRINOID_MUSEUM = "$Q/b13c9057-89a6-48a0-8c9b-3c4318b877c8.png"
    private const val IMG_CRINOID_CABOCHON = "$Q/05a7185b-41fb-4a9f-9870-89c3f3e50571.png"
    private const val IMG_BRECCIA_WILD = "$Q/78487160-a460-4365-8fb0-67895a671a81.png"
    private const val IMG_BRECCIA_MUSEUM = "$Q/c92ad5fc-fe87-4c28-a146-0317f0703a43.png"
    private const val IMG_BRECCIA_CABOCHON = "$Q/4301452b-5127-4919-a9c9-55e638822bda.png"
    private const val IMG_COLEMANITE_WILD = "$Q/d35dcc07-19ef-4b1c-81fd-76dbde6c9de6.png"
    private const val IMG_COLEMANITE_MUSEUM = "$Q/8bc7d067-09d3-4f61-81a0-dd531bf40618.png"
    private const val IMG_COLEMANITE_CABOCHON = "$Q/52766dc1-9a96-47a0-9470-02695fd739b5.png"
    private const val IMG_COPROLITE_WILD = "$Q/44126f13-2f96-42a4-a38e-38659d6ffccc.png"
    private const val IMG_COPROLITE_MUSEUM = "$Q/85549a77-7be9-46de-b882-832b902c67c1.png"
    private const val IMG_COPROLITE_CABOCHON = "$Q/90a2e721-a713-4e68-8f2c-c348d0f27b95.png"
    private const val IMG_COQUINA_WILD = "$Q/c7739f0d-81c3-410b-9615-66451438d1f0.png"
    private const val IMG_COQUINA_MUSEUM = "$Q/9910a5da-6457-479c-b01f-8a4dcd0e814b.png"
    private const val IMG_COQUINA_CABOCHON = "$Q/8f32b790-42f9-4846-b5fc-fca8ca07d7e8.png"
    private const val IMG_CONGLOMERATE_WILD = "$Q/52b39793-b38b-44ad-a166-fd13357e8b31.png"
    private const val IMG_CONGLOMERATE_MUSEUM = "$Q/d95df40c-d5c3-42f9-8be3-cf9e64d6fa78.png"
    private const val IMG_CONGLOMERATE_CABOCHON = "$Q/04eee814-1302-4ecc-909a-62e90c1c6c3f.png"
    private const val IMG_CHERT_WILD = "$Q/86c5c4a7-4281-4223-bd88-2869a6b7fb8e.png"
    private const val IMG_CHERT_MUSEUM = "$Q/f5223aa1-2066-4703-af41-d2731f11348b.png"
    private const val IMG_CHERT_CABOCHON = "$Q/6c83039f-779a-4309-8361-70075a82f7c4.png"
    private const val IMG_CHERT_CABOCHON_NEW = "$Q/005777b6-6f45-441c-8c9e-2f6adeb13ba6.png"

    // ── 4-image upgrade batch 5: wild, museum, cabochon for 10 more specimens ──
    private const val IMG_BLOODSTONE_MUSEUM = "$Q/16be40b2-bfdf-4d5e-82a3-aa96876273e4.png"
    private const val IMG_CHLORASTROLITE_WILD = "$Q/1e3a6143-e576-4851-ae6d-97d9f7af3e81.png"
    private const val IMG_CHLORASTROLITE_MUSEUM = "$Q/956b34f1-f0d6-46b3-83d4-844a59498179.png"
    private const val IMG_CHLORASTROLITE_CABOCHON = "$Q/ad7765fe-5fb6-47a4-bdde-c508839da4e9.png"
    private const val IMG_DENDRITE_AGATE_WILD = "$Q/04c3855a-8773-495b-992c-1bf4a8142751.png"
    private const val IMG_DENDRITE_AGATE_MUSEUM = "$Q/0d4d7cb3-54bb-4f8e-a87d-9ec5449569bd.png"
    private const val IMG_DENDRITE_AGATE_CABOCHON = "$Q/3dac89b2-0098-41bb-bad2-80832fe25569.png"
    private const val IMG_DINOSAUR_BONE_WILD = "$Q/9a056110-0d25-443e-91e4-7763e4027757.png"
    private const val IMG_DINOSAUR_BONE_MUSEUM = "$Q/cbd4700f-60ed-41d7-aefc-779dc5a52217.png"
    private const val IMG_DINOSAUR_BONE_CABOCHON = "$Q/18fb21e3-e1c1-4162-90a4-6d1b7437b4df.png"
    private const val IMG_DOLOMITE_WILD = "$Q/b8a0de54-4bec-4e83-93dc-a116e22c18ea.png"
    private const val IMG_DOLOMITE_MUSEUM = "$Q/61f4e594-c968-48d3-ac46-c243859050d8.png"
    private const val IMG_DOLOMITE_CABOCHON = "$Q/8b582659-6cd3-4f05-88ed-3f143ae7339b.png"
    private const val IMG_DUMORTIERITE_WILD = "$Q/10e7fd08-9c8c-491b-9ca8-7589c76f164d.png"
    private const val IMG_DUMORTIERITE_MUSEUM = "$Q/622d8c01-9bfc-420e-acab-5a207a9f8f03.png"
    private const val IMG_DUMORTIERITE_CABOCHON = "$Q/44a0a3b8-125d-4ab3-bf61-d2482c0cceeb.png"
    private const val IMG_EPIDOTE_WILD = "$Q/46d5736f-660f-4184-81fe-ee49c11e1dd5.png"
    private const val IMG_EPIDOTE_MUSEUM = "$Q/8bf67141-d8d3-4aa5-a0e1-551bb1be15be.png"
    private const val IMG_EPIDOTE_CABOCHON = "$Q/45bccca4-dfe9-45b2-b530-d47667fc03f9.png"
    private const val IMG_FLINT_WILD = "$Q/21820db1-992e-4ce8-8969-5e36647a4613.png"
    private const val IMG_FLINT_MUSEUM = "$Q/594df29b-fa39-40aa-a5ca-5c6350ff6458.png"
    private const val IMG_FLINT_CABOCHON = "$Q/c3b69cc0-5144-493b-b37f-adb3b799062f.png"
    private const val IMG_GRANITE_WILD = "$Q/eac45c56-3611-4d72-b004-1d92f7388e62.png"
    private const val IMG_GRANITE_MUSEUM = "$Q/4fc3f29a-c58c-41b6-8ca1-056c5c4c6038.png"
    private const val IMG_GRANITE_CABOCHON = "$Q/438af506-d3f8-42e5-a9ee-50dc59fdefe1.png"
    private const val IMG_HALITE_WILD = "$Q/9387638d-5b4c-4e39-8116-fcf5e14ee64c.png"
    private const val IMG_HALITE_MUSEUM = "$Q/a391916e-effa-4bc9-a6e1-716708b978c7.png"
    private const val IMG_HALITE_CABOCHON = "$Q/c012351c-adf8-46f0-aaac-68c3bf493ddf.png"
    // Halite variety images (accuracy audit)
    private const val IMG_HALITE_BLUE = "$Q/960a3106-2a02-492d-a5a5-3dcfbaa2800e.png"
    private const val IMG_HALITE_BLUE_MUSEUM = "$Q/f7dc1c83-f2e5-44f1-9169-1ee8d8a07088.png"
    private const val IMG_HALITE_PINK_SPECIMEN = "$Q/09804fbc-fba2-4a32-9758-d678f164c1e4.png"
    private const val IMG_HALITE_PINK_MUSEUM = "$Q/06dbe1ed-e463-48b3-948a-174a60b12da3.png"
    private const val IMG_HALITE_HOPPER = "$Q/eb0c2f7d-0ab4-43f8-87c2-fcc8ef3cf0f6.png"
    private const val IMG_HALITE_HOPPER_MUSEUM = "$Q/439020af-51f4-43f1-938d-5f236b7e62b2.png"
    private const val IMG_HALITE_FLOWERS = "$Q/94f9b667-1b7b-4f61-bd9d-5edda4a96eb1.png"
    private const val IMG_HALITE_FLOWERS_MUSEUM = "$Q/bcaa2c33-319e-484f-9718-206f3f585d1c.png"
    private const val IMG_HALITE_GREEN_SPECIMEN = "$Q/d04786d8-9848-44be-83f7-afca54c75bc5.png"
    private const val IMG_HALITE_GREEN_MUSEUM = "$Q/0a372c70-7d57-4e0a-93d8-19babe118a2f.png"
    private const val IMG_EVAPORITE_HALITE_SPECIMEN = "$Q/22c35945-b5f3-471b-9a75-a13d6a2b0888.png"
    private const val IMG_EVAPORITE_HALITE_DEDICATED = "$Q/ed823d83-01bf-443a-bae8-16026ae3eee2.png"
    private const val IMG_EVAPORITE_HALITE_MUSEUM = "$Q/ee6fac37-c9af-4959-8853-261b1b6f523b.png"
    private const val IMG_SEDIMENTARY_EVAPORITE_SPECIMEN = "$Q/a96bb9ee-d203-4744-9a98-c61acb13be4a.png"
    private const val IMG_HOWLITE_WILD = "$Q/19acb212-c80d-434b-8f74-4002a2684e26.png"
    private const val IMG_HOWLITE_MUSEUM = "$Q/cb677cb0-07cf-40da-9914-4436e277e529.png"
    private const val IMG_HOWLITE_CABOCHON = "$Q/63a0d2c6-7f10-4eb1-94c7-e9c482afab49.png"
    private const val IMG_IOLITE_WILD = "$Q/c646dec8-708e-4779-a874-67d3153a9f68.png"
    private const val IMG_IOLITE_MUSEUM = "$Q/7f52973c-6234-4753-93ba-6af98478c816.png"
    private const val IMG_IOLITE_CABOCHON = "$Q/88a1cc9e-c0d5-4787-af67-a9ebb2a1f7cb.png"
    private const val IMG_KUNZITE_WILD = "$Q/822420bb-1e11-486a-b80e-69164b5b36cf.png"
    private const val IMG_KUNZITE_MUSEUM = "$Q/6ad1ef4c-450d-42b9-aff1-cfa6371c84b7.png"
    private const val IMG_KUNZITE_CABOCHON = "$Q/2c88e097-af4a-4228-b0ee-c4c8b50c3d62.png"
    private const val IMG_MARBLE_WILD = "$Q/b2ca75d9-79ff-4404-9941-ded2ac560a07.png"
    private const val IMG_MARBLE_MUSEUM = "$Q/8d390460-7a27-4f2e-b764-863028a7bf43.png"
    private const val IMG_MARBLE_CABOCHON = "$Q/d45d4a69-bdda-4c61-8d71-0b3112eee063.png"
    private const val IMG_PETRIFIED_WOOD_WILD = "$Q/9149a4e2-2840-4e34-8479-84eadc6ab06b.png"
    private const val IMG_PETRIFIED_WOOD_MUSEUM = "$Q/e6a7c762-c7cf-45a1-8321-8420f14f783a.png"
    private const val IMG_PETRIFIED_WOOD_CABOCHON = "$Q/7ca7751c-8c9f-4e38-99ed-328463eabb22.png"
    private const val IMG_RHYOLITE_WILD = "$Q/0af48e65-1744-45a6-8df4-8bee4b3448b6.png"
    private const val IMG_RHYOLITE_MUSEUM = "$Q/5620f34d-c9f3-416e-b085-e893e4b2db10.png"
    private const val IMG_RHYOLITE_CABOCHON = "$Q/4b996246-cbe1-4f5e-a365-1bdcdd256df3.png"
    private const val IMG_GYPSUM_WILD = "$Q/71555f60-2f90-4c50-9d7b-110f1d9712fe.png"
    private const val IMG_GYPSUM_MUSEUM = "$Q/c6a545c9-6956-48fe-972f-2a27b2031706.png"
    private const val IMG_GYPSUM_CABOCHON = "$Q/cab27499-edb7-4889-b64c-75d992eaca10.png"
    private const val IMG_SLATE_WILD = "$Q/756068dc-6890-4283-964f-6aafe3dc1abf.png"
    private const val IMG_SLATE_MUSEUM = "$Q/f282bd20-d1ee-4ae6-b479-6f081f95c4e7.png"
    private const val IMG_SLATE_CABOCHON = "$Q/89f31c77-356c-440d-ac93-7d9d01d00f30.png"
    private const val IMG_ULEXITE_WILD = "$Q/693d74e1-d85d-463c-bab2-d9836568fa8c.png"
    private const val IMG_ULEXITE_MUSEUM = "$Q/0f8776a7-ff74-4013-891d-393e0cccb097.png"
    private const val IMG_ULEXITE_CABOCHON = "$Q/7f3bacf4-d8fe-49a4-bb79-022a9908b95f.png"
    private const val IMG_BERYL_WILD = "$Q/3e3064ae-e0dc-4758-a607-d17fcf74660f.png"
    private const val IMG_BERYL_MUSEUM = "$Q/3984bafe-fbba-4144-ac54-0cc6166600fd.png"
    private const val IMG_BERYL_CABOCHON = "$Q/a198c36f-3ebe-40c2-a9fd-6f7ab64506c2.png"
    private const val IMG_BLUE_LACE_AGATE_WILD = "$Q/6949ee55-959f-450b-b302-057ccb5acd58.png"
    private const val IMG_BLUE_LACE_AGATE_MUSEUM = "$Q/149ef56e-df10-494b-af87-4ca706cd0cb5.png"
    private const val IMG_BLUE_LACE_AGATE_CABOCHON = "$Q/a52bf3de-7d36-4341-b11d-c90745c16c84.png"
    private const val IMG_BELEMNITE_WILD = "$Q/c4f2d0f0-0476-4073-bc83-44c1e9152e70.png"
    private const val IMG_BELEMNITE_MUSEUM = "$Q/a5b2317d-875f-4977-9b31-e7293591784e.png"
    private const val IMG_BELEMNITE_CABOCHON = "$Q/c55ab55c-6837-466e-b406-2b53fb2933ed.png"
    private const val IMG_BLASTOID_WILD = "$Q/9b8059e8-360e-49f8-8ffe-ad4cd7fb5877.png"
    private const val IMG_BLASTOID_MUSEUM = "$Q/b3a957a5-3a31-410e-8d56-85cdf4829152.png"
    private const val IMG_BLASTOID_CABOCHON = "$Q/71ca771a-2df6-4be9-b36c-ed3af13882df.png"
    private const val IMG_BRACHIOPOD_WILD = "$Q/fc15ff3f-2bc3-42db-a0fd-6269eeac3d90.png"
    private const val IMG_BRACHIOPOD_MUSEUM = "$Q/6a18a65e-cb1c-45ed-ad64-01821e39476e.png"
    private const val IMG_BRACHIOPOD_CABOCHON = "$Q/90994cab-2aee-4ca7-9636-d5a5477f0217.png"
    private const val IMG_BACULITES_WILD = "$Q/cfb6e9d3-d9cd-4898-9ff1-46cc25084dea.png"
    private const val IMG_BACULITES_MUSEUM = "$Q/87fdf147-d55d-47ef-85f5-16beceb898ff.png"
    private const val IMG_BACULITES_CABOCHON = "$Q/7d5ebfc5-2bf3-47be-974b-b49da89af431.png"
    private const val IMG_ARCHIMEDES_WILD = "$Q/7737f9c5-be73-4e8a-84e1-cbf88a7885e0.png"
    private const val IMG_ARCHIMEDES_MUSEUM = "$Q/d1fa590c-dd6f-418e-add7-78abe4877bce.png"
    private const val IMG_ARCHIMEDES_CABOCHON = "$Q/62d38684-3d71-4c17-9efd-89a2898f3140.png"
    private const val IMG_CALAMITES_WILD = "$Q/be14a967-ebd4-4f5b-887d-5afe30a9cd94.png"
    private const val IMG_CALAMITES_MUSEUM = "$Q/466c7ae8-7143-410c-ac27-1d9388130fe5.png"
    private const val IMG_CALAMITES_CABOCHON = "$Q/39cc2ed0-f728-4435-990e-6802c54be5be.png"
    private const val IMG_BIVALVE_WILD = "$Q/1086aa71-8f82-4517-a07e-da83584e6d60.png"
    private const val IMG_BIVALVE_MUSEUM = "$Q/b6ce849e-97b0-4506-93fd-435f6e8af202.png"
    private const val IMG_BIVALVE_CABOCHON = "$Q/2c777fda-6a6f-4070-9054-b75bf57ab175.png"
    private const val IMG_CONULARIA_WILD = "$Q/27ff689c-1617-49ec-b43b-181fb42c08f7.png"
    private const val IMG_CONULARIA_MUSEUM = "$Q/5d709ff9-3f22-4927-bdd8-28b28504a04c.png"
    private const val IMG_CONULARIA_CABOCHON = "$Q/97a51973-95c3-410e-87b3-e57c848e962d.png"
    private const val IMG_COELACANTH_WILD = "$Q/c422ea8e-9cd1-43c5-bd55-23ddf381ca16.png"
    private const val IMG_COELACANTH_MUSEUM = "$Q/179f670e-6cf0-4aaa-b6d5-45d969a0a379.png"
    private const val IMG_COELACANTH_CABOCHON = "$Q/0d2184b3-d94d-4630-a29f-bfe9ddb01361.png"
    private const val IMG_DINOSAUR_EGGSHELL_WILD = "$Q/6a61eab3-b26c-4a32-9d70-859ec6ae0100.png"
    private const val IMG_DINOSAUR_EGGSHELL_MUSEUM = "$Q/35f85213-3b85-487d-be50-fef10c5bd71d.png"
    private const val IMG_DINOSAUR_EGGSHELL_CABOCHON = "$Q/9738a2df-186b-4d78-8e7e-63a00489847b.png"
    private const val IMG_DINOSAUR_TRACK_WILD = "$Q/1d5593ac-3b99-4573-8b4a-e6ad04dc29ed.png"
    private const val IMG_DINOSAUR_TRACK_MUSEUM = "$Q/781d31db-96c7-4949-89fc-dbffa62c11cc.png"
    private const val IMG_DINOSAUR_TRACK_CABOCHON = "$Q/23bb84da-e176-421b-bab3-3d7d6458774c.png"
    private const val IMG_DIOPSIDE_WILD = "$Q/cb396c87-766c-4564-a3e9-4427d3274d4f.png"
    private const val IMG_DIOPSIDE_MUSEUM = "$Q/50c228d3-f2a5-4399-bf59-653788f2a25c.png"
    private const val IMG_DIOPSIDE_CABOCHON = "$Q/1c32c23d-b302-49c3-b3c1-342aca5c086d.png"
    private const val IMG_DIORITE_WILD = "$Q/f80f55b0-858a-4cca-bc0a-9f9624dce13a.png"
    private const val IMG_DIORITE_MUSEUM = "$Q/ccc263a7-8651-4ccf-bf93-5de01effcc99.png"
    private const val IMG_DIORITE_CABOCHON = "$Q/7f2ad001-4bea-434d-9c67-aba3d6d8ffb5.png"
    private const val IMG_ECLOGITE_WILD = "$Q/48f51ddc-526e-420a-a0f7-e8db5575ae45.png"
    private const val IMG_ECLOGITE_MUSEUM = "$Q/5c88bb80-3310-43ab-8fc0-40bed7906c6d.png"
    private const val IMG_ECLOGITE_CABOCHON = "$Q/efa5f142-41b0-4d68-97dc-d59542c41eb0.png"
    private const val IMG_ENSTATITE_WILD = "$Q/e2b681c8-367a-44fe-baf4-11819ba785bb.png"
    private const val IMG_ENSTATITE_MUSEUM = "$Q/d6347ceb-a12b-4220-9696-453e5320cb67.png"
    private const val IMG_ENSTATITE_CABOCHON = "$Q/a8df7a1f-8a6e-4677-bb29-d26ecaafdb08.png"
    private const val IMG_EUCLASE_WILD = "$Q/04d03571-7f66-4491-817e-f84bf3801812.png"
    private const val IMG_EUCLASE_MUSEUM = "$Q/c5383b50-e17b-497d-bf69-237456c27742.png"
    private const val IMG_EUCLASE_CABOCHON = "$Q/52a27a67-a6d3-47c5-9409-38d94fa5529b.png"
    private const val IMG_ERYTHRITE_WILD = "$Q/815dd257-28de-4dfa-91bb-92888e06f7ac.png"
    private const val IMG_ERYTHRITE_MUSEUM = "$Q/6fe8d33e-a25c-4dbd-beb2-b15b275bc456.png"
    private const val IMG_ERYTHRITE_CABOCHON = "$Q/47cd00a2-5959-4968-a570-2f5848241c20.png"
    private const val IMG_EURYPTERID_WILD = "$Q/8f68a207-8033-4de1-8690-ee7602264e05.png"
    private const val IMG_EURYPTERID_MUSEUM = "$Q/d71804ca-56e0-4401-acb4-f0cdedae2a86.png"
    private const val IMG_EURYPTERID_CABOCHON = "$Q/b6a242bf-a263-4db0-8ce7-2a48f2dbc091.png"
    private const val IMG_FAVOSITES_WILD = "$Q/72c29b26-69cb-4c8d-af1c-27266638d217.png"
    private const val IMG_FAVOSITES_MUSEUM = "$Q/19fecab9-d1d6-48f5-b972-d19afd7f7720.png"
    private const val IMG_FAVOSITES_CABOCHON = "$Q/25e85ead-b41b-4e35-9981-3b0b5995ba88.png"
    private const val IMG_FIRE_AGATE_WILD = "$Q/8e604ee8-010b-4589-a3cc-eef9edd00f60.png"
    private const val IMG_FIRE_AGATE_MUSEUM = "$Q/da1132f9-3d43-4052-b2ad-809b0beb76f9.png"
    private const val IMG_FIRE_AGATE_CABOCHON = "$Q/85118b2f-9797-4810-bd7e-572aa273563e.png"
    private const val IMG_ENDOCERAS_WILD = "$Q/6f0ade2a-028d-4c57-8300-3bf1d412e5d4.png"
    private const val IMG_ENDOCERAS_MUSEUM = "$Q/eaa77a02-0f8c-4512-9312-a5535b28dfad.png"
    private const val IMG_ENDOCERAS_CABOCHON = "$Q/fe3e5f87-9833-41b8-9ef6-2099a50952c9.png"
    private const val IMG_FENESTELLA_WILD = "$Q/95e1785e-1639-4ab8-949e-7ec54ad5dcec.png"
    private const val IMG_FENESTELLA_MUSEUM = "$Q/b1edb91e-bff5-4309-80be-189d3fa1cf6e.png"
    private const val IMG_FENESTELLA_CABOCHON = "$Q/c47a7314-3809-4095-a930-0b3d11b07a09.png"
    private const val IMG_FIRE_OPAL_WILD = "$Q/d1ef8f56-e5b2-4c6e-8304-e65fd4aa44fb.png"
    private const val IMG_FIRE_OPAL_MUSEUM = "$Q/7b016007-270b-4959-b921-d4db2f0670e5.png"
    private const val IMG_FIRE_OPAL_CABOCHON = "$Q/0455cc82-6603-45b0-baa6-39aa8a271ae1.png"
    private const val IMG_EXOGYRA_WILD = "$Q/1a3f9d16-23ca-4401-bdf3-002a6fdba117.png"
    private const val IMG_EXOGYRA_MUSEUM = "$Q/ea7f7a31-d1c9-4271-8a0e-51215a7da358.png"
    private const val IMG_EXOGYRA_CABOCHON = "$Q/3ec9abd8-7c60-4399-9177-dd1ac070de56.png"
    private const val IMG_ENCHODUS_WILD = "$Q/9d9dde36-546a-46f0-9d2c-295797f54631.png"
    private const val IMG_ENCHODUS_MUSEUM = "$Q/e479746b-14cb-4259-b4c4-4a8ab3669bff.png"
    private const val IMG_ENCHODUS_CABOCHON = "$Q/88987ab4-52f2-4cf0-a107-4673686dfad8.png"
    private const val IMG_FOSSIL_CORAL_WILD = "$Q/d49fdc53-a54b-4e43-a302-a644474b226e.png"
    private const val IMG_FOSSIL_CORAL_MUSEUM = "$Q/2901c2fa-53e8-4a53-8565-605142d2934f.png"
    private const val IMG_FOSSIL_CORAL_CABOCHON = "$Q/2e46a3db-9888-4ed5-a511-f351a59b8c00.png"
    private const val IMG_FOSSIL_FERN_WILD = "$Q/0670a9f2-ae96-4c96-85e7-0cd6765c6f62.png"
    private const val IMG_FOSSIL_FERN_MUSEUM = "$Q/3b59a9fd-ae7d-47b8-9929-2dbfd2a8cddf.png"
    private const val IMG_FOSSIL_FERN_CABOCHON = "$Q/e88c9b52-0378-4e01-9bae-a1bf36b91f18.png"
    private const val IMG_FOSSIL_FISH_WILD = "$Q/be2b3e2f-75bd-4863-a800-417d67133d5d.png"
    private const val IMG_FOSSIL_FISH_MUSEUM = "$Q/7f1eb4ae-c41b-4ebb-91e9-09f30e07587f.png"
    private const val IMG_FOSSIL_FISH_CABOCHON = "$Q/2329aaac-fd61-49fb-ae21-eb9e24303a7c.png"
    private const val IMG_GABBRO_WILD = "$Q/5cc414ae-1eda-46f7-b971-f4db85b7e154.png"
    private const val IMG_GABBRO_MUSEUM = "$Q/01a54bfd-4276-415b-bb0f-a093b0406dca.png"
    private const val IMG_GABBRO_CABOCHON = "$Q/4f32c98e-0b79-490b-ab7c-3ee3fa2f0c2e.png"
    private const val IMG_GASPEITE_WILD = "$Q/debb97d3-cb4b-44af-9460-03976d4e3ad4.png"
    private const val IMG_GASPEITE_MUSEUM = "$Q/3f281c74-09cd-454b-a027-220e742922f8.png"
    private const val IMG_GASPEITE_CABOCHON = "$Q/cb07db16-9bac-40a4-8ec1-950bc5184b80.png"
    private const val IMG_GASTROPOD_WILD = "$Q/09efa86f-1766-4c6c-bc3c-f4fd29d09a51.png"
    private const val IMG_GASTROPOD_MUSEUM = "$Q/4d3030a4-36ad-4c1a-a247-4ca6325d517f.png"
    private const val IMG_GASTROPOD_CABOCHON = "$Q/df67cf6d-60d8-47ec-8fde-fe817e90fffe.png"
    private const val IMG_GNEISS_WILD = "$Q/5d6444da-e522-4e8e-8df0-aa63b11d20af.png"
    private const val IMG_GNEISS_MUSEUM = "$Q/32793267-a058-4a35-8183-33f6f6af3792.png"
    private const val IMG_GNEISS_CABOCHON = "$Q/0cdd751a-84a5-4f36-a8ff-620375314b51.png"
    private const val IMG_GONIATITE_WILD = "$Q/cef57b6f-f5a7-4267-8f52-0d4978b5a0f0.png"
    private const val IMG_GONIATITE_MUSEUM = "$Q/7d843132-db8f-4a42-ba14-17d09de2c007.png"
    private const val IMG_GONIATITE_CABOCHON = "$Q/7755f2dd-b533-45a3-831e-8cfb4fdc7018.png"
    private const val IMG_FUSULINID_WILD = "$Q/53e9d2b0-cb5c-4c88-ab5b-cb1aa2101311.png"
    private const val IMG_FUSULINID_MUSEUM = "$Q/f84057c3-ae73-4a6b-ad2a-7b8b704bf2c9.png"
    private const val IMG_FUSULINID_CABOCHON = "$Q/8e200bb0-2479-47cb-9bcc-eac9d11f5b24.png"
    private const val IMG_GINKGO_WILD = "$Q/a5920126-9880-4707-ada5-5169dde5945a.png"
    private const val IMG_GINKGO_MUSEUM = "$Q/dd472fc3-cc7d-4ce4-958b-ff48f37986d3.png"
    private const val IMG_GINKGO_CABOCHON = "$Q/03721118-77d1-4a20-b172-09fc84ec31e9.png"
    private const val IMG_GLAUCOPHANE_WILD = "$Q/6691e4c5-d265-483b-9753-c88c211bcddc.png"
    private const val IMG_GLAUCOPHANE_MUSEUM = "$Q/89a6a3e7-db73-4425-acfc-e166ab332f83.png"
    private const val IMG_GLAUCOPHANE_CABOCHON = "$Q/9cd31ff3-90d0-45da-a99f-d523628399e8.png"
    private const val IMG_GLOSSOPTERIS_WILD = "$Q/0e44e9f0-302e-476f-96dd-b5606d5fd53f.png"
    private const val IMG_GLOSSOPTERIS_MUSEUM = "$Q/12d42dff-fbbe-4d61-acd8-7daf58f6066c.png"
    private const val IMG_GLOSSOPTERIS_CABOCHON = "$Q/4cb56d83-ee37-4f9d-aa82-b308e95650b5.png"

    // ── 4-image upgrade batch 6: lapis, larimar, peridot, sunstone, moonstone, sugilite, native copper/gold, pietersite, shattuckite, thomsonite, spectrolite, spinel, vanadinite, wulfenite, pyromorphite, mimetite, herkimer, rose/smoky quartz, moss agate, sardonyx, onyx ──
    private const val IMG_LAPIS_WILD = "$Q/66ba020b-bd04-403c-a741-8bf5d0c3c462.png"
    private const val IMG_LAPIS_MUSEUM = "$Q/ff78cab9-7a49-462e-bc13-3e864f4f4d59.png"
    private const val IMG_LAPIS_CABOCHON = "$Q/abbbc2e7-97ca-4eae-bf77-5345ce973fd0.png"
    private const val IMG_LAPIS_ROUGH = "$Q/3ab284c9-69b5-4ee9-86cd-543831cf8c3a.png"
    private const val IMG_LARIMAR_WILD = "$Q/d6e343e6-563e-44b7-8ba3-0dda6de43c75.png"
    private const val IMG_LARIMAR_MUSEUM = "$Q/903b0cd3-4dd6-4145-9325-502e21ffb846.png"
    private const val IMG_LARIMAR_CABOCHON = "$Q/67d6c6ce-711c-40c6-9326-18ff805d0489.png"
    private const val IMG_PECTOLITE = "$Q/a51757e8-93cc-47e1-b56c-77b4d3933c5f.png"
    private const val IMG_PECTOLITE_FIBROUGH = "$Q/6b13d780-49ef-4369-9148-540de140bcde.png"
    private const val IMG_PECTOLITE_WILD = "$Q/ff7098c8-179b-4de1-8761-6561969143b1.png"
    private const val IMG_PECTOLITE_MUSEUM = "$Q/feda1cc5-0d1c-46d9-8691-8bc03aeb38eb.png"
    private const val IMG_PECTOLITE_CABOCHON = "$Q/457ccb2d-9738-4937-b543-2420d0b265a7.png"
    private const val IMG_PECTOLITE_SCHIZOLITE = "$Q/28053c74-776c-4a11-b762-23336b05e6da.png"
    private const val IMG_PECTOLITE_MASSIVE = "$Q/e8cde9bd-f224-4d3e-9b54-0e96b7c3745a.png"
    private const val IMG_PECTOLITE_GREEN = "$Q/a974b39f-d382-4c3f-8333-fceb15e75346.png"
    private const val IMG_PERIDOT_WILD = "$Q/e3da2d08-4f7d-44d9-afd0-cc9bb2d17ed7.png"
    private const val IMG_PERIDOT_MUSEUM = "$Q/5de61fcf-c437-44c0-b5bd-a956e42aab48.png"
    private const val IMG_PERIDOT_CABOCHON = "$Q/68db3e16-4fbb-4932-97d0-c56e2823a7e0.png"
    private const val IMG_SUNSTONE_WILD = "$Q/a4ac952b-2337-4314-ab36-e10dca7ecb3c.png"
    private const val IMG_SUNSTONE_MUSEUM = "$Q/70b8f0c1-d56d-4ab9-b763-73a88bc43fba.png"
    private const val IMG_SUNSTONE_CABOCHON = "$Q/c2f9ec98-6a86-46ee-802e-aad560d31007.png"
    private const val IMG_MOONSTONE_WILD = "$Q/23c0e1e2-54b6-4266-86ab-88320fe06a36.png"
    private const val IMG_MOONSTONE_MUSEUM = "$Q/437b21c9-2a1e-40b7-a46e-4cbe2ef04e90.png"
    private const val IMG_MOONSTONE_CABOCHON = "$Q/63ac0762-269e-4a1b-b9a5-063a3317ded4.png"
    private const val IMG_SUGILITE_WILD = "$Q/4702918c-b757-490d-88c2-b8f7b49177c3.png"
    private const val IMG_SUGILITE_MUSEUM = "$Q/5b8370dc-31f0-4c62-a3dc-ca99403020fe.png"
    private const val IMG_SUGILITE_CABOCHON = "$Q/adc44244-0752-4a5c-9010-9915d14459f6.png"
    private const val IMG_NATIVE_COPPER_WILD = "$Q/810cdabe-fbbb-4de5-8e2b-9282915b0829.png"
    private const val IMG_NATIVE_COPPER_MUSEUM = "$Q/d5de53ae-f790-4a57-a896-9861ad107d8c.png"
    private const val IMG_NATIVE_GOLD_WILD = "$Q/ddb5d782-a3b8-4579-bb34-466e2993977e.png"
    private const val IMG_NATIVE_GOLD_MUSEUM = "$Q/7f466c44-798f-4ae4-8726-8f90922bc4cb.png"
    private const val IMG_PIETERSITE_WILD = "$Q/5ebb1064-363a-4890-bac4-dabee424034d.png"
    private const val IMG_PIETERSITE_CABOCHON = "$Q/31f80160-b938-4078-86af-190cb7b7537f.png"
    private const val IMG_SHATTUCKITE_WILD = "$Q/4859f7f1-ea96-42b9-bed3-ee2b30b3518a.png"
    private const val IMG_SHATTUCKITE_CABOCHON = "$Q/2f267907-f0ee-4a96-92f6-68791014b4b7.png"
    private const val IMG_THOMSONITE_WILD = "$Q/1d5f576e-666e-4a1b-b33d-896e534abe88.png"
    private const val IMG_THOMSONITE_CABOCHON = "$Q/ae5ad039-c03b-4b8b-a002-9d59303c6648.png"
    private const val IMG_SPECTROLITE_WILD = "$Q/52a648cf-ba06-45ff-b936-d5f0d6ec5ae1.png"
    private const val IMG_SPECTROLITE_CABOCHON = "$Q/a07190a1-afea-4198-ae8f-34c885c15c39.png"
    private const val IMG_SPINEL_MUSEUM = "$Q/d8d545ca-962b-4286-b7da-ebd32e7c3a69.png"
    private const val IMG_VANADINITE_MUSEUM = "$Q/59005b91-f680-49fe-895d-2b877de3c059.png"
    private const val IMG_VANADINITE_WILD = "$Q/9db9204f-cc0f-4b47-a300-356b88fffd1e.png"
    private const val IMG_WULFENITE_MUSEUM = "$Q/734ee9e8-3d18-430c-93a7-b48c20a55afa.png"
    private const val IMG_WULFENITE_WILD = "$Q/c6606d22-e0c8-450e-9e13-3828d36054b1.png"
    private const val IMG_PYROMORPHITE_MUSEUM = "$Q/db700e57-ec36-456e-83e1-c80b38f16280.png"
    private const val IMG_PYROMORPHITE_WILD = "$Q/a947b400-99f2-4af7-bc2a-06b2005ae7d4.png"
    private const val IMG_MIMETITE_MUSEUM = "$Q/cd13c84c-07b6-4cf1-a08d-25cb306c6776.png"
    private const val IMG_HERKIMER_WILD = "$Q/a9c3cb9b-eb2f-46a4-b5b2-474a8d68df1a.png"
    private const val IMG_HERKIMER_MUSEUM = "$Q/ad218e7f-3a4b-4b0d-9fa6-2815daefaf4f.png"
    private const val IMG_ROSE_QUARTZ_WILD = "$Q/61e834b1-b8b8-4214-8ad0-d7558663c104.png"
    private const val IMG_ROSE_QUARTZ_MUSEUM = "$Q/424e25e2-b90b-4876-9008-6958abc3bcee.png"
    private const val IMG_ROSE_QUARTZ_CABOCHON = "$Q/950ad09b-f3da-4a2b-8bd2-da6fd6427bda.png"
    private const val IMG_SMOKY_QUARTZ_WILD = "$Q/b4c18161-9209-4cdf-8378-ee245e97a5d6.png"
    private const val IMG_SMOKY_QUARTZ_MUSEUM = "$Q/b93c3011-6c89-4df0-9c8a-e41e14e38260.png"
    private const val IMG_SMOKY_QUARTZ_CABOCHON = "$Q/07c260f5-0a75-4393-ac51-3dd828b00cd0.png"
    private const val IMG_MOSS_AGATE_WILD = "$Q/6b7ec2dc-c5fb-43f6-a973-af5942210601.png"
    private const val IMG_MOSS_AGATE_MUSEUM = "$Q/312f85a7-05e7-488d-a9d5-9fb5919592d7.png"
    private const val IMG_MOSS_AGATE_CABOCHON = "$Q/e9be38e0-1ba8-4750-a718-54baf5f18e42.png"
    private const val IMG_SARDONYX_WILD = "$Q/9bea07c4-4650-4836-bca0-5553376372f6.png"
    private const val IMG_SARDONYX_MUSEUM = "$Q/5d55d075-ad7e-4be9-bff0-e9e7e9e9268b.png"
    private const val IMG_SARDONYX_CABOCHON = "$Q/036701a2-eabf-4cc6-a2de-a3221800aab5.png"
    private const val IMG_ONYX_WILD = "$Q/ebcdaaea-517a-48c4-a578-aceaa88a21ca.png"
    private const val IMG_ONYX_MUSEUM = "$Q/081540a2-843c-4d07-87a0-a3e529b9eced.png"
    private const val IMG_ONYX_CABOCHON = "$Q/e2549c73-cd5f-4642-b100-329c1ec6d9c9.png"
    // ── 4-image upgrade batch 7: tanzanite, spessartine, lazulite, hiddenite, phenakite, moldavite, tektite, native-silver, anglesite ──
    private const val IMG_TANZANITE_WILD = "$Q/21b08861-d7e9-4ee4-a8ad-85c092b6d005.png"
    private const val IMG_TANZANITE_MUSEUM = "$Q/3407e678-c00b-46eb-8a94-6219d85fc0af.png"
    private const val IMG_TANZANITE_CABOCHON = "$Q/1fee8813-74a6-4739-b3b2-4f356bb095bf.png"
    private const val IMG_SPESSARTINE_WILD = "$Q/7c3a6eeb-810e-4fba-9b15-4188655c5555.png"
    private const val IMG_SPESSARTINE_MUSEUM = "$Q/7c6e3b53-6526-4513-9563-5d2300fb0eb4.png"
    private const val IMG_LAZULITE_WILD = "$Q/8f277dd9-15a9-4783-8d21-a0dddd7a3b46.png"
    private const val IMG_LAZULITE_MUSEUM = "$Q/7af60613-250f-4cba-8f3c-7b0052cace71.png"
    private const val IMG_HIDDENITE_WILD = "$Q/8d52f167-b90e-43db-9109-9459cc3a1420.png"
    private const val IMG_HIDDENITE_MUSEUM = "$Q/6fe5d56c-4417-4107-b4de-e5f8ef881ae7.png"
    private const val IMG_PHENAKITE_WILD = "$Q/15dffc34-a3ee-4e60-8ce4-b9ea11478ee9.png"
    private const val IMG_PHENAKITE_MUSEUM = "$Q/1fab270a-2c4f-4367-8e79-0b279d3a9658.png"
    private const val IMG_MOLDAVITE_WILD = "$Q/b9c06488-c074-4f52-9b1d-75b55335a9fd.png"
    private const val IMG_MOLDAVITE_MUSEUM = "$Q/55f24018-ede1-4949-a4e0-e45159832c75.png"
    private const val IMG_MOLDAVITE_CABOCHON = "$Q/e22f37fe-693d-40f3-b0ce-cbeb8d658675.png"
    private const val IMG_TEKTITE_WILD = "$Q/86c42a51-a545-4640-b995-62e0da05cdd6.png"
    private const val IMG_TEKTITE_MUSEUM = "$Q/fee51a13-3b37-412f-8528-83004241cd15.png"
    private const val IMG_NATIVE_SILVER_WILD = "$Q/d0106f62-cc72-4207-9394-34644f530ad0.png"
    private const val IMG_NATIVE_SILVER_MUSEUM = "$Q/cbf2d9d3-6e55-4f5b-9f64-fe70d5c46c6c.png"
    private const val IMG_ANGLESITE_WILD = "$Q/ca9ca08e-cb79-4e2c-9644-ffdb4acf8eea.png"
    private const val IMG_ANGLESITE_MUSEUM = "$Q/eb6d0a8f-ce26-4a3b-af52-d5ea4cce0a82.png"
    // ── 4-image upgrade batch 8: grandidierite, goshenite, heliodor, zultanite, sinhalite, kornerupine, serendibite, taaffeite ──
    private const val IMG_GRANDIDIERITE_WILD = "$Q/7f7dc534-ed74-40a7-92dc-75cc5007ab6f.png"
    private const val IMG_GRANDIDIERITE_MUSEUM = "$Q/b33277ba-764f-4b19-9b4e-dcd1cac98b6a.png"
    private const val IMG_GOSHENITE_WILD = "$Q/e766e071-c183-4c60-bd6b-fd45317401c9.png"
    private const val IMG_GOSHENITE_MUSEUM = "$Q/6added14-515d-4e4b-8fe5-2d887c9ccd33.png"
    private const val IMG_HELIODOR_WILD = "$Q/27f5a378-2201-452a-ba05-b4afe01aadbb.png"
    private const val IMG_HELIODOR_MUSEUM = "$Q/2bdb5b19-31f4-45f4-b502-6aba1f2de123.png"
    private const val IMG_ZULTANITE_WILD = "$Q/b3cc13ed-6c3d-4bf9-896c-cc11ddb080ba.png"
    private const val IMG_ZULTANITE_MUSEUM = "$Q/5a2217b5-059c-4fdc-bb12-030bc5fd6c50.png"
    private const val IMG_SINHALITE_WILD = "$Q/a7e5fd49-49ca-49a9-9f72-6624f8bc3070.png"
    private const val IMG_SINHALITE_MUSEUM = "$Q/1e2bd7e4-67ba-40c8-aa21-2edae528711f.png"
    private const val IMG_KORNERUPINE_WILD = "$Q/b1e9ec1a-4a7a-448a-9d83-d1530f01f7fb.png"
    private const val IMG_KORNERUPINE_MUSEUM = "$Q/a3b6424f-d685-430d-9842-7fcfa21e5782.png"
    private const val IMG_SERENDIBITE_WILD = "$Q/1fcb70bb-5386-4f3d-8781-314679c7a761.png"
    private const val IMG_SERENDIBITE_MUSEUM = "$Q/e8d61e66-04c7-460b-aacb-335c75843cab.png"
    private const val IMG_TAAFFEITE_WILD = "$Q/f90d54e4-c2c9-4238-b53f-0ffa8838f384.png"
    // ── 4-image upgrade batch 9: alaska-jade, grossular, staurolite, indicolite, verdelite, rubellite, olivine, arsenopyrite ──
    private const val IMG_ALASKA_JADE_WILD = "$Q/9394a1a1-1ca0-497d-9929-82ebacf3ea55.png"
    private const val IMG_ALASKA_JADE_MUSEUM = "$Q/4c54da77-cef7-4f0a-9644-62d34b3b76ec.png"
    private const val IMG_GROSSULAR_WILD = "$Q/be7d9d4d-54fe-4f92-b4e3-2126d64a209d.png"
    private const val IMG_GROSSULAR_MUSEUM = "$Q/52d1a0a5-a20f-4d6a-a5aa-4f7b756d3469.png"
    private const val IMG_STAUROLITE_WILD = "$Q/60c246e2-8c9e-43d8-8356-8e9b57c3b600.png"
    private const val IMG_INDICOLITE_WILD = "$Q/60de0a6a-2d2f-4715-ab87-4f94c7dd55ea.png"
    private const val IMG_INDICOLITE_MUSEUM = "$Q/00023f97-eeec-43c7-94a5-1b7c9d37039f.png"
    private const val IMG_VERDELITE_WILD = "$Q/dc585380-a70b-4d0a-96e5-fae722faf539.png"
    private const val IMG_VERDELITE_MUSEUM = "$Q/0eebacb3-983c-4e82-a339-9abc13f355f5.png"
    private const val IMG_RUBELLITE_WILD = "$Q/9e0c8ed4-736f-4142-94f9-d6a1893da96b.png"
    private const val IMG_RUBELLITE_MUSEUM = "$Q/73290b03-b4ef-40cb-9efc-15a9f902132f.png"
    private const val IMG_OLIVINE_WILD = "$Q/2ef0bccd-377a-4f25-bbfd-a4d5da270cb5.png"
    private const val IMG_OLIVINE_MUSEUM = "$Q/6aa69e86-f32a-485f-869c-c3156b89dd3d.png"
    private const val IMG_ARSENOPYRITE_WILD = "$Q/68d27ec8-7bb5-4f50-a2b8-94d6f12f1181.png"
    // ── 4-image upgrade batch 10: carnotite, native-sulfur, realgar, orpiment + variety images for fluorite, hematite, rutile, barite ──
    private const val IMG_CARNOTITE_WILD = "$Q/fc6980e9-ca84-4f52-912f-db505130f137.png"
    private const val IMG_CARNOTITE_MUSEUM = "$Q/20a84f99-aeb2-47e7-ac69-f6b6f7f0da4d.png"
    private const val IMG_NATIVE_SULFUR_WILD = "$Q/6217c72c-163a-4e5d-b9a1-f6971ae81154.png"
    private const val IMG_NATIVE_SULFUR_MUSEUM = "$Q/b8311394-3a05-40fe-87d2-123dfc725b7a.png"
    private const val IMG_REALGAR_WILD = "$Q/16d54ee3-5387-4e36-963d-d92ec4b22af6.png"
    private const val IMG_REALGAR_MUSEUM = "$Q/64429a16-a4e1-4410-90a5-6e01c0845c29.png"
    private const val IMG_ORPIMENT_WILD = "$Q/3694b4d7-e93a-45a9-af5a-a94967617326.png"
    private const val IMG_ORPIMENT_MUSEUM = "$Q/4f242cb5-9bcb-4ddf-873a-cafb642ccecd.png"
    private const val IMG_FLUORITE_WILD_3 = "$Q/7c1ccfee-971e-463c-a1ca-7a4ba936c5f9.png"
    private const val IMG_FLUORITE_MUSEUM_2 = "$Q/8c552f86-bb7c-484a-9212-9d57430111a8.png"
    private const val IMG_RUTILE_WILD_2 = "$Q/c9987575-8838-4aa1-b8f1-3659d57dc90e.png"
    private const val IMG_RUTILE_MUSEUM_2 = "$Q/0b60652f-2b45-46b1-9f44-c0e3516bd6db.png"
    private const val IMG_HEMATITE_WILD_2 = "$Q/67941b7e-1d30-4e5e-9f40-b1f5d3523ecc.png"
    private const val IMG_HEMATITE_MUSEUM_2 = "$Q/7a5cb1ec-38ac-4156-852b-1b0499d9315f.png"
    private const val IMG_BARITE_WILD_2 = "$Q/0c53f9e4-d3ff-4ffc-975a-26d713b2e675.png"
    // ── 4-image upgrade batch 24: jasper, graphite, augite, hornblende, heulandite, muscovite, biotite ──
    private const val IMG_JASPER_WILD = "$Q/c83ec09a-a6b8-4433-a606-05dc70092904.png"
    private const val IMG_JASPER_MUSEUM = "$Q/fc668095-c7f1-46eb-b176-559c466297f6.png"
    private const val IMG_JASPER_CABOCHON = "$Q/c0542330-b2b8-4eb8-ac50-733f1018d67b.png"
    private const val IMG_GRAPHITE_WILD = "$Q/beb4ebdd-c79d-4dd2-b158-599e51ca9bd4.png"
    private const val IMG_GRAPHITE_MUSEUM = "$Q/2291a4f4-8d9c-4308-aa55-21e72a829d2f.png"
    private const val IMG_AUGITE_WILD = "$Q/5fd49d59-fb6e-4ff8-a0e7-cf4de6a6ad99.png"
    private const val IMG_AUGITE_MUSEUM = "$Q/054b691a-e7a7-4d8e-8825-430058fe1770.png"
    private const val IMG_HORNBLENDE_WILD = "$Q/dc0b86c0-e92b-4d2f-8701-304ef3c23cad.png"
    private const val IMG_HORNBLENDE_MUSEUM = "$Q/f26e1577-b74f-45b0-828d-ae21a91925bd.png"
    private const val IMG_HEULANDITE_WILD = "$Q/3a67146e-1eb1-4669-84d8-f372c052deee.png"
    private const val IMG_HEULANDITE_MUSEUM = "$Q/6643484a-8043-4648-987e-4b4a18388cea.png"
    private const val IMG_MUSCOVITE_WILD = "$Q/1331e476-b0fb-441e-a5dc-167d03a0dc49.png"
    private const val IMG_MUSCOVITE_MUSEUM = "$Q/61f1bf18-3e92-42da-b2ca-188409d084e0.png"
    private const val IMG_BIOTITE_WILD = "$Q/bf9744bf-c05b-464c-a59f-6a5a35066699.png"
    private const val IMG_BIOTITE_MUSEUM = "$Q/46414dc2-9c11-487f-8d69-cba6925b8d6c.png"
    // ── 4-image upgrade batch 25: orthoclase, natrolite + variety images for spinel, magnetite, galena, pyrite, chalcopyrite, sphalerite ──
    private const val IMG_ORTHOCLASE_WILD = "$Q/ec979e27-8af9-4857-8879-51e5e3cbe5b2.png"
    private const val IMG_ORTHOCLASE_MUSEUM = "$Q/76ed8d7a-8d14-46f7-9a9e-79b04b77526e.png"
    private const val IMG_NATROLITE_WILD = "$Q/9a237c82-5893-4366-ba4f-a35edd0f0eb5.png"
    private const val IMG_NATROLITE_MUSEUM = "$Q/f553a427-f2a6-4333-94fa-b52927db2b6f.png"
    private const val IMG_SPINEL_WILD = "$Q/ca71c348-b58f-4847-bbff-6ac35864c062.png"
    private const val IMG_MAGNETITE_WILD_2 = "$Q/ccaca34c-92a6-48dd-b0df-f77b453eb7f0.png"
    private const val IMG_MAGNETITE_MUSEUM_2 = "$Q/e680ba8e-6695-4bbb-a4a2-937dade852d0.png"
    private const val IMG_GALENA_WILD_3 = "$Q/2db52c1b-a000-47dc-9ee1-0f1f1802abc0.png"
    private const val IMG_GALENA_MUSEUM_2 = "$Q/a57520eb-3486-4267-b1db-b1ce8d0fbf4b.png"
    private const val IMG_PYRITE_WILD_2 = "$Q/cfc52435-6fab-49b2-9b98-2b92edc7c982.png"
    private const val IMG_PYRITE_MUSEUM_2 = "$Q/0faf3860-75b4-4cf2-8cfa-816a7600a429.png"
    private const val IMG_CHALCOPYRITE_WILD_2 = "$Q/841922f4-e0f8-4e5f-8910-3cbd53347af9.png"
    private const val IMG_CHALCOPYRITE_MUSEUM_2 = "$Q/44ec03fa-1a2f-41f5-a0f6-d55efed55f67.png"
    private const val IMG_SPHALERITE_WILD_2 = "$Q/d0ab40c2-7bda-4574-b33f-a435022502ae.png"
    private const val IMG_SPHALERITE_MUSEUM_2 = "$Q/aa24b012-4e88-4836-95bf-9e4798ef9c3f.png"
    // ── 4-image upgrade batch 26: quartzite, sandstone, shale, limestone, pumice, tuff, schist + basalt variety ──
    private const val IMG_QUARTZITE_WILD = "$Q/082e2ac7-1008-46bb-908f-b5139bfc07b2.png"
    private const val IMG_QUARTZITE_MUSEUM = "$Q/7d0645d4-d169-4dea-866e-632c75455482.png"
    private const val IMG_SANDSTONE_WILD = "$Q/48f4196f-405d-4958-a7ea-0f12bffa42e9.png"
    private const val IMG_SANDSTONE_MUSEUM = "$Q/25b7525c-0c69-4cbf-a7f6-ac9415ee53d3.png"
    private const val IMG_SHALE_WILD = "$Q/6cfaaf74-e13a-4a1f-ba9b-004f4b288580.png"
    private const val IMG_SHALE_MUSEUM = "$Q/885bde45-44ed-4afd-9005-1b89fcff3bda.png"
    private const val IMG_LIMESTONE_WILD = "$Q/a3ee962d-8013-4edf-a240-faa1477e3fb3.png"
    private const val IMG_LIMESTONE_MUSEUM = "$Q/a5da846b-ed03-4296-9120-f9f059a48a2e.png"
    private const val IMG_PUMICE_WILD = "$Q/9fde617d-f235-4c96-8404-31c389a717db.png"
    private const val IMG_PUMICE_MUSEUM = "$Q/a25b599b-a90a-44b8-bb73-72c1a9e3c143.png"
    private const val IMG_TUFF_WILD = "$Q/d72b093a-aed5-4ba8-9076-8c8f9d24310a.png"
    private const val IMG_TUFF_MUSEUM = "$Q/c40d7f3e-e0cc-42d3-9222-16607398c2fc.png"
    private const val IMG_SCHIST_WILD = "$Q/98bdc69f-fac5-4f16-9e93-9fd45277cd04.png"
    private const val IMG_BASALT_WILD_2 = "$Q/0e32f5b7-7ee7-41d8-b82c-ab1a2ff0189e.png"
    private const val IMG_BASALT_MUSEUM_2 = "$Q/d566e620-f9dd-40be-b1eb-b24c1069883f.png"
    // ── 4-image upgrade batch 27: travertine, novaculite, diatomite, amphibolite, anorthosite, anthracite, arkose, chlorite ──
    private const val IMG_TRAVERTINE_WILD = "$Q/faa9d825-ee3e-49c8-a1f7-3b5ec37cff26.png"
    private const val IMG_TRAVERTINE_MUSEUM = "$Q/6485f015-32d9-4cb2-84b0-f14116280a68.png"
    private const val IMG_NOVACULITE_WILD = "$Q/604c3351-4eeb-4e98-9ea9-3c7a639802fb.png"
    private const val IMG_NOVACULITE_MUSEUM = "$Q/436869a1-23db-4667-b242-86b69767f45f.png"
    private const val IMG_DIATOMITE_WILD = "$Q/4f32340b-31f0-4deb-acc4-9091689f12a1.png"
    private const val IMG_DIATOMITE_MUSEUM = "$Q/162c0769-7e22-45ff-a5f1-b2d31e58c684.png"
    private const val IMG_AMPHIBOLITE_WILD = "$Q/e0f77207-b47c-4dc0-b1ee-5a7f9e742b70.png"
    private const val IMG_AMPHIBOLITE_MUSEUM = "$Q/4bd4410d-a556-44fd-babe-ef6201c89ea6.png"
    private const val IMG_AMPHIBOLE_GROUP = "$Q/8b7fa6c9-b404-43c1-92a4-7cf2b16c11b9.png"
    private const val IMG_ANORTHOSITE_WILD = "$Q/9e755ab5-2fc0-4683-939c-591446d86829.png"
    private const val IMG_ANORTHOSITE_MUSEUM = "$Q/aa50ef2d-a7ff-4f7d-8573-b51591342705.png"
    private const val IMG_ANTHRACITE_WILD = "$Q/2279b1ac-e28f-4250-8940-d1bf588f807b.png"
    private const val IMG_ANTHRACITE_MUSEUM = "$Q/b7be5b03-7a76-4f6e-9865-6e67d69ec3a8.png"
    private const val IMG_ARKOSE_WILD = "$Q/cee8fc79-0de2-4791-a39d-a4634b29ff68.png"
    private const val IMG_ARKOSE_MUSEUM = "$Q/a9836ece-9409-4d23-ae7b-0ab56442c1f8.png"
    private const val IMG_CHLORITE_WILD = "$Q/1ea50f84-a880-4555-9b64-e5c6cfc0ac62.png"
    // ── 4-image upgrade batch 28: scoria, pegmatite, migmatite, mylonite, hornfels, skarn, soapstone, talc ──
    private const val IMG_SCORIA_WILD = "$Q/ad8c1ce6-45fa-45e6-a580-8710b582d13a.png"
    private const val IMG_SCORIA_MUSEUM = "$Q/4ae9c325-3e45-467b-ac9a-25e96cc0c84b.png"
    private const val IMG_PEGMATITE_WILD = "$Q/298b418c-3983-42eb-b214-67a725d692c1.png"
    private const val IMG_PEGMATITE_MUSEUM = "$Q/786683e4-0167-4128-af06-191286e34e43.png"
    private const val IMG_MIGMATITE_WILD = "$Q/16d78705-487a-4dbf-945f-b04d3c259aa5.png"
    private const val IMG_MIGMATITE_MUSEUM = "$Q/f6eb171a-bd05-4bd3-a20f-0996f01b0c1e.png"
    private const val IMG_MYLONITE_WILD = "$Q/38cd9ea5-be39-4c65-912a-e7e3b12b7454.png"
    private const val IMG_MYLONITE_MUSEUM = "$Q/2776ca12-c98d-42b4-a9d0-db409237a2cd.png"
    private const val IMG_HORNFELS_WILD = "$Q/c443478f-b679-495d-930e-90e6bd47f217.png"
    private const val IMG_HORNFELS_MUSEUM = "$Q/920e5a2e-31a3-47c7-a021-31eb2cf8ea5a.png"
    private const val IMG_SKARN_WILD = "$Q/0fa9134c-aa86-4fce-ab0b-e2492ec8af73.png"
    private const val IMG_SKARN_MUSEUM = "$Q/112c8329-e356-459b-baa4-203f2910d349.png"
    private const val IMG_SOAPSTONE_WILD = "$Q/6c19a3be-f6b7-4a70-ad42-ce2e1eed6d61.png"
    private const val IMG_SOAPSTONE_MUSEUM = "$Q/103f6f39-de3a-449e-a85b-3db626a4b484.png"
    private const val IMG_TALC_WILD = "$Q/743833d3-9128-4605-9956-44ed935a98a4.png"
    // ── 4-image upgrade batch 29: rutilated-quartz, tourmalinated-quartz, lake-superior-agate, plume-agate, prasiolite, dumortierite-quartz, norite ──
    private const val IMG_RUTILATED_QUARTZ_WILD = "$Q/f0f7725b-2110-4157-bc6a-e2c0c13ee432.png"
    private const val IMG_RUTILATED_QUARTZ_MUSEUM = "$Q/9b37056b-801b-4330-adec-3e5d14f03e6a.png"
    private const val IMG_TOURMALINATED_QUARTZ_WILD = "$Q/c0362f94-5b7c-4313-98f5-60531c88c111.png"
    private const val IMG_TOURMALINATED_QUARTZ_MUSEUM = "$Q/517ec761-e605-4bcc-800e-0a16f793a864.png"
    private const val IMG_LAKE_SUPERIOR_AGATE_WILD = "$Q/42801097-a59f-4ab0-af53-8fad7b7ff666.png"
    private const val IMG_LAKE_SUPERIOR_AGATE_MUSEUM = "$Q/eae2facc-7c25-4284-83ec-ac7a28f1b0b0.png"
    private const val IMG_LAKE_SUPERIOR_AGATE_CABOCHON = "$Q/0d9ede1c-8555-4dce-bc8b-be25f9b4f0fc.png"
    private const val IMG_PLUME_AGATE_WILD = "$Q/a266b31c-8839-4026-b070-9ddc3980a993.png"
    private const val IMG_PLUME_AGATE_MUSEUM = "$Q/e41e0c16-7314-4f15-a37d-75ce064350c3.png"
    private const val IMG_PLUME_AGATE_CABOCHON = "$Q/8460f078-375a-4ddb-84d1-8f866a83584e.png"
    private const val IMG_PRASIOLITE_WILD = "$Q/5461c8dc-6405-49f9-9d31-3a8c5958b791.png"
    private const val IMG_PRASIOLITE_MUSEUM = "$Q/fb3ce459-4f0f-4bcc-90c3-661ffd347166.png"
    private const val IMG_DUMORTIERITE_QUARTZ_WILD = "$Q/1053f884-c5f1-41fd-9916-8ee5b6a41f3e.png"
    private const val IMG_DUMORTIERITE_QUARTZ_MUSEUM = "$Q/84c59174-0896-46bd-91cf-e0f952fe85eb.png"
    private const val IMG_NORITE_WILD = "$Q/a7c90597-c141-4a4a-a457-12c9925f90e8.png"
    // ── Batch 30: additional variety/rough images for popular multi-color specimens ──
    private const val IMG_LAPIS_ROUGH_2 = "$Q/220f8363-0e49-453d-b205-158d56757a8f.png"
    private const val IMG_LAPIS_CABOCHON_2 = "$Q/d6ef8915-62e1-48e6-9a8e-3b23745b7805.png"
    private const val IMG_PETRIFIED_WOOD_CABOCHON_2 = "$Q/479d3791-402b-4f27-995b-22d88d41d1df.png"
    private const val IMG_LARIMAR_ROUGH_2 = "$Q/5dd99753-f1d1-4ce2-aa45-99a332f925b0.png"
    private const val IMG_PERIDOT_ROUGH_2 = "$Q/6ca24e68-a9fa-491a-a141-4fcc57abd315.png"
    private const val IMG_SUNSTONE_ROUGH_2 = "$Q/480f20c1-89a4-4373-a50d-d125613ae946.png"
    private const val IMG_SUGILITE_ROUGH_2 = "$Q/c60f33ca-27b6-4c6a-8754-fe87915bda2b.png"
    private const val IMG_MOONSTONE_ROUGH_2 = "$Q/3925f2ac-b597-47df-bc34-3c5a09b119ea.png"
    private const val IMG_MALACHITE_ROUGH_2 = "$Q/437f0e7e-7c51-45da-ba81-4befe1894a5f.png"
    private const val IMG_TURQUOISE_ROUGH_2 = "$Q/3c61756e-e3ee-4079-918e-98e5aba0aab5.png"
    private const val IMG_LABRADORITE_ROUGH_2 = "$Q/ffddb752-6e16-4994-ab9f-2f006b11b1df.png"
    private const val IMG_AMAZONITE_ROUGH_2 = "$Q/b6bead8c-0cd0-4a6b-a7c4-ead67954319c.png"
    private const val IMG_CHAROITE_ROUGH_2 = "$Q/96de9ce7-85f1-4145-901b-4409a450405f.png"
    private const val IMG_RHODOCHROSITE_ROUGH_2 = "$Q/abf85741-f3f7-40a3-a069-e7b9af73fe6e.png"
    private const val IMG_RHODONITE_ROUGH_2 = "$Q/e0ee1689-2085-463d-8a9f-7e5c89a9a755.png"
    // ── Batch 31: komatiite, carbonatite, peridotite, dunite, pyroxenite, granulite, phyllite + petrified-wood cabochon variety ──
    private const val IMG_KOMATIITE_WILD = "$Q/b56663f4-77ea-4edb-bc69-a1122c05645a.png"
    private const val IMG_KOMATIITE_MUSEUM = "$Q/d03d30f0-0132-4ed9-b270-2c20a6498186.png"
    private const val IMG_CARBONATITE_WILD = "$Q/45202a8b-ff46-4b4c-83d0-3c65a5783b4f.png"
    private const val IMG_CARBONATITE_MUSEUM = "$Q/6f531be6-256f-4d25-9bb4-71fa8be135f2.png"
    private const val IMG_PERIDOTITE_WILD = "$Q/e8418f65-cef2-447f-a150-badffa528514.png"
    private const val IMG_PERIDOTITE_MUSEUM = "$Q/1804f20c-d016-47db-8301-3d1af851575f.png"
    private const val IMG_DUNITE_WILD = "$Q/930f1aba-3eec-4255-8c32-70e151a0b320.png"
    private const val IMG_DUNITE_MUSEUM = "$Q/4bcf08eb-bf71-4f27-8c6c-8afcbfcde20c.png"
    private const val IMG_PYROXENITE_WILD = "$Q/a45858b5-c12a-4976-8172-f878903515f9.png"
    private const val IMG_PYROXENITE_MUSEUM = "$Q/9643e93b-1847-4ca1-b14e-d458062ace0b.png"
    private const val IMG_GRANULITE_WILD = "$Q/1f4cb0f4-a84b-48ff-8f22-4413362886b9.png"
    private const val IMG_GRANULITE_MUSEUM = "$Q/98b9a722-b96f-4081-a0aa-6cf2b5626c32.png"
    private const val IMG_PHYLLITE_WILD = "$Q/530cbae0-8d87-4c8f-8c22-02a8a141f307.png"
    private const val IMG_PHYLLITE_MUSEUM = "$Q/90fa3569-d176-482c-8e8f-f510b57cf307.png"
    private const val IMG_PETRIFIED_WOOD_CABOCHON_3 = "$Q/67cffb66-92bf-4f78-90b2-0800473e8f02.png"
    // ── Batch 32: syenite, andesite, dacite, trachyte, blueschist, greenschist, laterite, bauxite ──
    private const val IMG_SYENITE_WILD = "$Q/af04d9fe-5041-4a98-bf5a-46810dc0f52a.png"
    private const val IMG_SYENITE_MUSEUM = "$Q/1e70d61c-7725-464d-ae78-08b65fb1b376.png"
    private const val IMG_ANDESITE_WILD = "$Q/d51b763d-1a60-4fc7-8971-ace472e3667e.png"
    private const val IMG_ANDESITE_MUSEUM = "$Q/7d6b89fe-87cf-446f-b118-265e07a5e86a.png"
    private const val IMG_DACITE_WILD = "$Q/7a6c4933-c327-4fa2-b2dd-ce2062f07e8a.png"
    private const val IMG_DACITE_MUSEUM = "$Q/e79f1c91-3f22-4c3a-aa63-ed7536acf37a.png"
    private const val IMG_TRACHYTE_WILD = "$Q/2e11516a-57e2-4e57-87fe-b82af00e8245.png"
    private const val IMG_TRACHYTE_MUSEUM = "$Q/44f56e6c-3e46-4221-8c5a-a3d627084e2d.png"
    private const val IMG_BLUESCHIST_WILD = "$Q/14732c67-fe82-4190-8d20-9d39c6fe2294.png"
    private const val IMG_BLUESCHIST_MUSEUM = "$Q/929e1570-6b43-4409-a2af-b98db51a3c40.png"
    private const val IMG_GREENSCHIST_WILD = "$Q/0d51bddd-63de-421a-b5e1-577b90225d99.png"
    private const val IMG_GREENSCHIST_MUSEUM = "$Q/14971738-e56f-4ad9-8621-10093f3a411c.png"
    private const val IMG_LATERITE_WILD = "$Q/686b50b8-e4ed-4e8b-9424-189261824d6e.png"
    private const val IMG_LATERITE_MUSEUM = "$Q/1e649b5a-19d3-4582-bbcd-3c9cf2fd8c79.png"
    private const val IMG_BAUXITE_WILD = "$Q/0b77f76d-9be5-4011-b57f-2e9c6774a805.png"
    // ── Batch 33: oil-shale, lignite, bituminous-coal, kimberlite, lamproite, charnockite, banded-iron-formation ──
    private const val IMG_OIL_SHALE_WILD = "$Q/032feba4-309a-4dbb-b740-a2769e632f94.png"
    private const val IMG_OIL_SHALE_MUSEUM = "$Q/a53ad3ed-60ad-4d3a-b9a1-a455d76f1133.png"
    private const val IMG_LIGNITE_WILD = "$Q/adcf9c49-e1bb-4e33-a6ff-83a40987e920.png"
    private const val IMG_LIGNITE_MUSEUM = "$Q/1634dce9-931f-4fba-b846-aabd1b4e1657.png"
    private const val IMG_BITUMINOUS_COAL_WILD = "$Q/48fba8ba-70fe-470e-9be8-93c27a94a9f4.png"
    private const val IMG_BITUMINOUS_COAL_MUSEUM = "$Q/9f565026-2ca2-4228-8737-3bdb8b765c51.png"
    private const val IMG_KIMBERLITE_WILD = "$Q/90f50260-69db-470a-97bf-791984c020fa.png"
    private const val IMG_KIMBERLITE_MUSEUM = "$Q/79e02c2e-d475-483a-8d4d-aa997969da10.png"
    private const val IMG_LAMPROITE_WILD = "$Q/04f73b72-9709-4b17-8bfd-76b295538061.png"
    private const val IMG_LAMPROITE_MUSEUM = "$Q/7e0e7ea2-4d99-4aa1-839b-0b1dd4e7cbd0.png"
    private const val IMG_CHARNOCKITE_WILD = "$Q/cae547db-b640-419b-a361-86b8d8464162.png"
    private const val IMG_CHARNOCKITE_MUSEUM = "$Q/823ddcea-33c3-4e73-a6a7-d0137a6f8507.png"
    private const val IMG_BANDED_IRON_WILD = "$Q/9028de16-751b-4028-9a95-70a12a216ace.png"
    private const val IMG_BANDED_IRON_MUSEUM = "$Q/2be38ede-57c0-4984-bb1b-492fce566a52.png"
    // ── Batch 34: magnesite, molybdenite, megalodon-tooth, trilobite, nautiloid, orthoceras, mammoth-tooth, spirifer ──
    private const val IMG_MAGNESITE_WILD = "$Q/31747ff4-8a50-4b01-99f3-6e4b0eac0f04.png"
    private const val IMG_MAGNESITE_MUSEUM = "$Q/25a67b4b-2910-4f39-a8be-9c80b8df6adb.png"
    private const val IMG_MOLYBDENITE_WILD = "$Q/1bc02262-cdae-4ae7-8936-897a65188e0e.png"
    private const val IMG_MOLYBDENITE_MUSEUM = "$Q/96042734-ae7b-44ee-9d22-63c85c961481.png"
    private const val IMG_MEGALODON_TOOTH_WILD = "$Q/e34444cd-cf56-402c-9fa2-c00b35f15ab2.png"
    private const val IMG_MEGALODON_TOOTH_MUSEUM = "$Q/50725c1d-b8e7-40fc-89c6-f566c68da440.png"
    private const val IMG_TRILOBITE_WILD = "$Q/dd169096-a962-4a8d-b058-7795c6fd3ecd.png"
    private const val IMG_TRILOBITE_MUSEUM = "$Q/e7bdf3d9-73e3-40f4-9fdf-8be65fe432b8.png"
    private const val IMG_NAUTILOID_WILD = "$Q/d87f468d-ed3a-4a4f-b552-91fc8d1aab93.png"
    private const val IMG_NAUTILOID_MUSEUM = "$Q/95a921e8-a392-41ec-9796-c4e52c3d1760.png"
    private const val IMG_ORTHOCERAS_WILD = "$Q/67480dfa-e0f7-49be-9c1d-3998d7531fee.png"
    private const val IMG_ORTHOCERAS_MUSEUM = "$Q/f53b096e-e4f6-4a9e-b82d-81092e4dc83f.png"
    private const val IMG_MAMMOTH_TOOTH_WILD = "$Q/d51d91b4-2dc8-4710-9db2-4e2138c106a2.png"
    private const val IMG_MAMMOTH_TOOTH_MUSEUM = "$Q/ae27980b-afaa-4d8c-930d-4125b10e6d32.png"
    private const val IMG_SPIRIFER_WILD = "$Q/cf24aebe-95da-4ccd-82cf-4c569e2a2627.png"
    // ── Batch 35: mesolite, okenite, neptunite, joaquinite, painite, jeremejevite + benitoite/grandidierite variety ──
    private const val IMG_MESOLITE_WILD = "$Q/1b5fb4d4-57ea-41a7-a736-06db2b5ecb71.png"
    private const val IMG_MESOLITE_MUSEUM = "$Q/3fc36a5a-e1c8-4a89-bda6-209b64165eed.png"
    private const val IMG_OKENITE_WILD = "$Q/0a966fe8-a005-425a-a0f1-b83d47523df2.png"
    private const val IMG_OKENITE_MUSEUM = "$Q/362b3b3a-8b54-4993-980c-0fec70fd6442.png"
    private const val IMG_NEPTUNITE_WILD = "$Q/e323a275-42fb-425b-9730-641b4e0026b7.png"
    private const val IMG_NEPTUNITE_MUSEUM = "$Q/53ab399d-7eb1-4da5-914d-c183662aed43.png"
    private const val IMG_JOAQUINITE_WILD = "$Q/d1394bbb-8fe7-41b8-a200-a0e951f06c15.png"
    private const val IMG_JOAQUINITE_MUSEUM = "$Q/5e2337b6-9ea5-4460-b3c6-633cd9e472db.png"
    private const val IMG_BENITOITE_WILD_2 = "$Q/2aed2e5c-79b4-49b6-bebe-e839c0b2bcf5.png"
    private const val IMG_BENITOITE_MUSEUM_2 = "$Q/ae81237e-f3f7-490f-bdc2-bdbc89e2cd7f.png"
    private const val IMG_PAINITE_WILD = "$Q/2923681b-75c9-4710-a387-7eb056a4dbe8.png"
    private const val IMG_PAINITE_MUSEUM = "$Q/30a7e8ba-cf69-4b8c-be9a-e814b4cf58ed.png"
    private const val IMG_GRANDIDIERITE_WILD_2 = "$Q/cbbf9fb3-a2ab-41af-844d-8ca22f0171cd.png"
    private const val IMG_JEREMEJEVITE_WILD = "$Q/56c35110-de12-4377-86ec-47b454da210b.png"
    private const val IMG_JEREMEJEVITE_MUSEUM = "$Q/89229d5d-d15c-41e0-bd21-3e65cf5cd3af.png"
    // ── Batch 36: phlogopite, hypersthene, pargasite, pumpellyite, sillimanite, tremolite, actinolite, riebeckite ──
    private const val IMG_PHLOGOPITE_WILD = "$Q/cd68b5bd-a2fd-40d0-8209-b45f05fb2190.png"
    private const val IMG_PHLOGOPITE_MUSEUM = "$Q/716deaa9-534f-4792-a55b-3f866cbde58d.png"
    private const val IMG_HYPERSTHENE_WILD = "$Q/324db9ca-9cc3-466e-8042-18e040d4e046.png"
    private const val IMG_HYPERSTHENE_MUSEUM = "$Q/aa3a18b2-769a-4772-a865-991118e3005c.png"
    private const val IMG_PARGASITE_WILD = "$Q/8bbd70b5-8a07-4bd7-ae0e-f3b9dc1ec8f4.png"
    private const val IMG_PARGASITE_MUSEUM = "$Q/eeca522f-5efe-47f8-b8bb-e53e9315135e.png"
    private const val IMG_PUMPELLYITE_WILD = "$Q/f78cd84b-8cac-415b-a839-00b3a881bc48.png"
    private const val IMG_PUMPELLYITE_MUSEUM = "$Q/5fcf9d56-7e13-4add-9cfc-99d0de8cb5b5.png"
    private const val IMG_SILLIMANITE_WILD = "$Q/6008dff6-4cff-4600-a4d5-5cb1bf4d1f78.png"
    private const val IMG_SILLIMANITE_MUSEUM = "$Q/b069e4b7-5e38-4455-8728-edc560c75ba3.png"
    private const val IMG_TREMOLITE_WILD = "$Q/fc71cd5b-0f37-4b4d-93ef-6d64febe4638.png"
    private const val IMG_TREMOLITE_MUSEUM = "$Q/4d99b7f9-5a22-4552-8529-c5507c34d83e.png"
    private const val IMG_ACTINOLITE_WILD = "$Q/bbd78bc0-a8b9-47b5-a5ef-fa1013c1da2e.png"
    private const val IMG_ACTINOLITE_MUSEUM = "$Q/c5e2626b-34df-46ac-bec1-5532f3a02215.png"
    private const val IMG_RIEBECKITE_WILD = "$Q/7ad180e2-d673-4503-8b2c-6d5473c591d2.png"
    // ── Batch 37: lepidocrocite, linarite, malacholla, musgravite, pezzottaite, phosphosiderite, poudretteite, psilomelane ──
    private const val IMG_LEPIDOCROCITE_WILD = "$Q/1f92f100-b51c-4951-b6eb-97c6b90bd3e1.png"
    private const val IMG_LEPIDOCROCITE_MUSEUM = "$Q/80e6fe1e-d832-4793-a103-80dc8b1ba45f.png"
    private const val IMG_LINARITE_WILD = "$Q/6d7ef618-7c07-4382-9768-9aa47b07129c.png"
    private const val IMG_LINARITE_MUSEUM = "$Q/6e7ea71d-eefe-4b56-9079-d96cf9410c8b.png"
    private const val IMG_MALACHOLLA_WILD = "$Q/c1da0117-148f-4c04-9368-9265fe9a5824.png"
    private const val IMG_MALACHOLLA_MUSEUM = "$Q/42cae139-c00f-4639-af86-f2c60b69a03b.png"
    private const val IMG_MUSGRAVITE_WILD = "$Q/40d4bf18-1e6a-4a96-8a8e-98fa0b724b2f.png"
    private const val IMG_MUSGRAVITE_MUSEUM = "$Q/a5ef0beb-4e54-413e-97aa-a0ea0bd60765.png"
    private const val IMG_PEZZOTTAITE_WILD = "$Q/699b6b8e-88b2-47b5-b15a-9e41daba4dde.png"
    private const val IMG_PEZZOTTAITE_MUSEUM = "$Q/7a5e33aa-2302-4692-bbf0-53afcbe71d41.png"
    private const val IMG_PHOSPHOSIDERITE_WILD = "$Q/708e8a8f-7d25-45f9-8a52-39eeb6c6baa4.png"
    private const val IMG_PHOSPHOSIDERITE_MUSEUM = "$Q/bce33d90-59a8-4452-9b27-6cc0dc0d54be.png"
    private const val IMG_POUDRETTEITE_WILD = "$Q/27980f82-3dc8-47ec-8952-bf86134f1bad.png"
    private const val IMG_POUDRETTEITE_MUSEUM = "$Q/e1e61ad2-188e-4d72-b85c-c2a962dea5d1.png"
    private const val IMG_PSILOMELANE_WILD = "$Q/017bd748-4bb8-4c98-a2b9-b104ec66074c.png"
    // ── Batch 38: olivenite, conichalcite, descloizite, mottramite, annabergite, chambersite, ferberite, huebnerite ──
    private const val IMG_OLIVENITE_WILD = "$Q/f174303d-7225-43f3-8fce-db2c18a044a0.png"
    private const val IMG_OLIVENITE_MUSEUM = "$Q/ae703fda-8a8a-4678-99c4-33610f5d090b.png"
    private const val IMG_CONICHALCITE_WILD = "$Q/4387bb04-e00f-440a-863f-380588796865.png"
    private const val IMG_CONICHALCITE_MUSEUM = "$Q/2b410b7f-c37c-41bb-9f9a-dfd8d9fa541c.png"
    private const val IMG_DESCLOIZITE_WILD = "$Q/5b0eb954-3272-4603-956b-3e1100c27782.png"
    private const val IMG_DESCLOIZITE_MUSEUM = "$Q/07a06489-d68e-4905-b825-6e087ad5943c.png"
    private const val IMG_MOTTRAMITE_WILD = "$Q/28e64dcd-0407-412b-ba36-4d2961d94f86.png"
    private const val IMG_MOTTRAMITE_MUSEUM = "$Q/07d5a3a4-7ae1-4387-91e9-96f88efaa989.png"
    private const val IMG_ANNABERGITE_WILD = "$Q/9cf8559a-10df-41b9-ad6a-0f24ffd94f34.png"
    private const val IMG_ANNABERGITE_MUSEUM = "$Q/28b993da-914b-4c44-807d-2e452b99c652.png"
    private const val IMG_CHAMBERSITE_WILD = "$Q/40eeb152-6710-4afb-8d36-40f4593cb683.png"
    private const val IMG_CHAMBERSITE_MUSEUM = "$Q/6b5aef58-1450-4311-890c-9f363a49a74e.png"
    private const val IMG_FERBERITE_WILD = "$Q/49df1b48-4457-4a31-a40e-c5b8eb20d31b.png"
    private const val IMG_FERBERITE_MUSEUM = "$Q/868b0b44-5d48-4cbd-bcd6-57822b61b8ff.png"
    private const val IMG_HUEBNERITE_WILD = "$Q/f1561113-6a97-4656-8f46-2c05086b1cb7.png"
    // ── Batch 39: purpurite, siderite, pyrolusite, anhydrite, gyrolite, hambergite, hibonite ──
    private const val IMG_PURPURITE_WILD = "$Q/14b1a894-245e-410b-a2d7-ad1ebd2e06af.png"
    private const val IMG_PURPURITE_MUSEUM = "$Q/091975ff-19cd-4fb2-a25b-31d47c82cab4.png"
    private const val IMG_PURPURITE_CABOCHON = "$Q/0e350823-a084-48c6-bd7e-183b95d8a892.png"
    private const val IMG_SIDERITE_WILD = "$Q/82e5c7fb-c226-4bac-8527-171f291b85a0.png"
    private const val IMG_SIDERITE_MUSEUM = "$Q/083f32d3-686b-438e-9fd4-2028f9556832.png"
    private const val IMG_PYROLUSITE_WILD = "$Q/627d8873-edc5-4e9d-898d-edf313f84aeb.png"
    private const val IMG_PYROLUSITE_MUSEUM = "$Q/cace0c61-55a5-46ab-9525-683c21a31e2e.png"
    private const val IMG_ANHYDRITE_WILD = "$Q/852839c8-6d01-4ead-bcd8-3247912fb010.png"
    private const val IMG_ANHYDRITE_MUSEUM = "$Q/2bec44bc-efa2-45b9-a6c8-e756ffdfda8a.png"
    private const val IMG_GYROLITE_WILD = "$Q/93198f01-afda-4c9e-a5c9-e368049d8b0e.png"
    private const val IMG_GYROLITE_MUSEUM = "$Q/dc13c9c0-c262-441b-a575-459929f8524a.png"
    private const val IMG_HAMBERGITE_WILD = "$Q/037cbab5-c8bb-42ac-b5d9-5a7a93ae7721.png"
    private const val IMG_HAMBERGITE_MUSEUM = "$Q/547285b0-3141-4c65-ae88-56c6c3aed3ab.png"
    private const val IMG_HIBONITE_WILD = "$Q/c3a38c59-adf4-4e18-b685-5a1426fbdf46.png"
    private const val IMG_HIBONITE_MUSEUM = "$Q/8bdf018c-cd00-4e9b-b066-4f262771cfd7.png"

    // ── Batch 70: Jasper 4-image upgrades (wild, museum, cabochon) ──
    private const val IMG_JASPER_PICTURE_WILD = "$Q/fa3400ca-6aaf-4ff8-98ce-483fdb900a7d.png"
    private const val IMG_JASPER_PICTURE_MUSEUM = "$Q/7014f55f-d8ac-40c6-b2fe-6791fd623ebc.png"
    private const val IMG_JASPER_PICTURE_CAB = "$Q/d67e8a42-af17-4976-a8ca-cac0b74fbea5.png"
    private const val IMG_JASPER_MOOKAITE_WILD = "$Q/c541cb15-551c-4c65-82cd-ebc4a04ed2bc.png"
    private const val IMG_JASPER_MOOKAITE_MUSEUM = "$Q/0d91fc17-fecd-4571-bfae-8d8c9e148781.png"
    private const val IMG_JASPER_MOOKAITE_CAB = "$Q/318a9d53-8870-41e9-814e-f44e3661a196.png"
    private const val IMG_JASPER_OCEAN_WILD = "$Q/7efd27ff-ba9f-4805-bc20-418387ab7f1f.png"
    private const val IMG_JASPER_OCEAN_MUSEUM = "$Q/cbd81713-d526-437b-bccc-fa73a3ce8f57.png"
    private const val IMG_JASPER_OCEAN_CAB = "$Q/5d2b53da-4786-4568-8ed1-4a8d7c4ebc55.png"
    private const val IMG_JASPER_MORRISONITE_WILD = "$Q/9176b672-da2a-4165-b2ec-bc77cd4bb493.png"
    private const val IMG_JASPER_MORRISONITE_MUSEUM = "$Q/0fb01145-e90b-4057-b93d-fc29995eaace.png"
    private const val IMG_JASPER_MORRISONITE_CAB = "$Q/39df222d-5865-4c9e-92f9-ae5bc51311ac.png"
    private const val IMG_JASPER_LEOPARD_WILD = "$Q/221048f0-c8a0-48a8-bad8-ae3c2f917425.png"
    private const val IMG_JASPER_LEOPARD_MUSEUM = "$Q/eaa9ac04-8a38-4611-adc2-3a091b84b158.png"
    private const val IMG_JASPER_LEOPARD_CAB = "$Q/6871457d-4103-4a48-8397-6599894176d0.png"
    private const val IMG_JASPER_BRUNEAU_WILD = "$Q/ade04cbc-33ba-4d32-bd3d-3124a6807ab2.png"
    private const val IMG_JASPER_BRUNEAU_MUSEUM = "$Q/b190471e-7840-409d-a207-3cdcf68ffb18.png"
    private const val IMG_JASPER_BRUNEAU_CAB = "$Q/2e9c2b11-91f0-4ace-a2a3-039f5f43a87a.png"
    private const val IMG_JASPER_IMPERIAL_WILD = "$Q/adf3807d-2281-4861-bf34-4d44e43b02a6.png"
    private const val IMG_JASPER_IMPERIAL_MUSEUM = "$Q/a01b56e2-c926-4e70-8200-e21660aec79a.png"
    private const val IMG_JASPER_IMPERIAL_CAB = "$Q/5ec8e8fc-6249-44e5-8a9f-e73ac207057f.png"
    private const val IMG_JASPER_KAMBABA_WILD = "$Q/7193d6d1-704a-4789-a7c6-139980f665a8.png"
    private const val IMG_JASPER_KAMBABA_MUSEUM = "$Q/882c7594-be66-43db-96f7-49c881236a69.png"
    private const val IMG_JASPER_KAMBABA_CAB = "$Q/36a8f6ef-63fe-41c7-b4b3-0149baceb32f.png"
    private const val IMG_JASPER_POLYCHROME_WILD = "$Q/0ad03f80-302b-449c-acc8-a2cf18974ec8.png"
    private const val IMG_JASPER_POLYCHROME_MUSEUM = "$Q/6304b71f-8106-4c30-8b26-7dcb3bdaf6be.png"
    private const val IMG_JASPER_POLYCHROME_CAB = "$Q/c8fa4fc2-2509-471a-ad56-8ea162f7dafd.png"
    private const val IMG_JASPER_NOREENA_WILD = "$Q/7cc423b1-ed1f-4624-8132-b99eb8fce47f.png"
    private const val IMG_JASPER_NOREENA_MUSEUM = "$Q/06b9a399-da6d-4613-b933-60c6c8d0284c.png"
    private const val IMG_JASPER_NOREENA_CAB = "$Q/1555e237-8297-41ed-bbef-86c9e9802c4f.png"
    private const val IMG_JASPER_ORBICULAR_WILD = "$Q/60d7a450-86c8-4237-be3d-9d0cb930663f.png"
    private const val IMG_JASPER_ORBICULAR_MUSEUM = "$Q/c44cc87c-2bba-4cd3-bdae-4fed8c654049.png"
    private const val IMG_JASPER_ORBICULAR_CAB = "$Q/eeebcd88-6295-46a0-bea8-7468e2748f9a.png"
    private const val IMG_JASPER_POPPY_WILD = "$Q/a69e9867-4288-4e0b-b18e-cd41a6628fe0.png"
    private const val IMG_JASPER_POPPY_MUSEUM = "$Q/499d1d25-5b3d-4c2b-ad79-877ac3c696d7.png"
    private const val IMG_JASPER_POPPY_CAB = "$Q/d9b07be2-d5a6-42e0-b9ad-ff2cc0755fe9.png"
    private const val IMG_JASPER_PORCELAIN_WILD = "$Q/000284a9-5209-4e42-84fb-c40a2dba7229.png"
    private const val IMG_JASPER_PORCELAIN_MUSEUM = "$Q/f2fd4e1e-3518-46a9-ac12-4689d8179877.png"
    private const val IMG_JASPER_PORCELAIN_CAB = "$Q/fac27a48-f594-40da-ad97-37cbff2d9371.png"
    private const val IMG_JASPER_BRECCIATED_WILD = "$Q/39e6d094-4af5-4bd4-b7d0-3fdaeef679dc.png"
    private const val IMG_JASPER_BRECCIATED_MUSEUM = "$Q/61c11074-bbb1-4864-a94f-fdd7583ab7e3.png"
    private const val IMG_JASPER_BRECCIATED_CAB = "$Q/e18fec26-97e7-41b8-be88-900e33fff24d.png"
    private const val IMG_JASPER_DESCHUTES_WILD = "$Q/ed8e65ce-1128-4b7c-bdb1-7d5accc5e5d8.png"
    private const val IMG_JASPER_DESCHUTES_MUSEUM = "$Q/e97a9b20-dee2-4cf7-9a30-ed55bcd86375.png"
    private const val IMG_JASPER_DESCHUTES_CAB = "$Q/128e99f0-0117-404d-bfe2-805083dd11db.png"
    private const val IMG_JASPER_WILD_HORSE_WILD = "$Q/d3d1cc74-709d-4293-babb-7bb080a83eb9.png"
    private const val IMG_JASPER_WILD_HORSE_MUSEUM = "$Q/36195542-69e7-45d7-8a02-6cb425a5b4c9.png"
    private const val IMG_JASPER_WILD_HORSE_CAB = "$Q/b345f1eb-1fe0-4607-b16b-1b982dab0f90.png"
    private const val IMG_JASPER_OWYHEE_WILD = "$Q/5cc9949b-63c4-48f6-9e9b-0ea44c8c21f3.png"
    private const val IMG_JASPER_OWYHEE_MUSEUM = "$Q/931a6c2b-bc29-4f11-8344-eaadcd9c4a63.png"
    private const val IMG_JASPER_OWYHEE_CAB = "$Q/fa19067b-a942-487f-a674-23af0c5bae9a.png"
    private const val IMG_JASPER_SPIDERWEB_WILD = "$Q/590ed0e4-786e-44f8-a65d-a5b04abceded.png"
    private const val IMG_JASPER_SPIDERWEB_MUSEUM = "$Q/253ad418-9709-48f9-bc6d-2ca204188cb1.png"
    private const val IMG_JASPER_SPIDERWEB_CAB = "$Q/2007fda6-0c4f-4086-bb8b-d7b70035299b.png"
    private const val IMG_JASPER_RAINFOREST_WILD = "$Q/3cdaccee-ec29-4fa8-aa2c-0ed7daafde13.png"
    private const val IMG_JASPER_RAINFOREST_MUSEUM = "$Q/9155dc88-5bb1-4b16-b15b-42856e084117.png"
    private const val IMG_JASPER_RAINFOREST_CAB = "$Q/729c1e56-a64b-4d60-8ed4-50276916fa5d.png"
    private const val IMG_JASPER_SUNSET_WILD = "$Q/0700d85f-9d69-4004-b776-684013e0751d.png"
    private const val IMG_JASPER_SUNSET_MUSEUM = "$Q/016ba263-fde7-40e6-9dfe-26d4cfb405c6.png"
    private const val IMG_JASPER_SUNSET_CAB = "$Q/da9a31b1-c5ce-4b41-9891-df73c821022a.png"
    // ── Granite 4-image upgrades (wild, museum — no cabochon for granites except unakite) ──
    private const val IMG_GRANITE_ORBICULAR_WILD = "$Q/37e74403-8927-4758-b9f5-670351e7d7ef.png"
    private const val IMG_GRANITE_ORBICULAR_MUSEUM = "$Q/1ef98f6d-58ba-442b-aa03-0192f3f67988.png"
    private const val IMG_GRANITE_PORPHYRITIC_WILD = "$Q/4562cc8c-7f52-4c71-8aff-175aebfd3a09.png"
    private const val IMG_GRANITE_PORPHYRITIC_MUSEUM = "$Q/fb94529d-16a6-41c2-a7ad-323778fed9ad.png"
    private const val IMG_GRANITE_GRAPHIC_WILD = "$Q/8d5f71f6-397c-4a01-937e-efd11d3295b0.png"
    private const val IMG_GRANITE_GRAPHIC_MUSEUM = "$Q/7731c6fe-44bf-4648-8297-7ac1dfc6a06a.png"
    private const val IMG_GRANITE_RAPAKIVI_WILD = "$Q/0aa4d830-9488-4450-a406-be81487e7af8.png"
    private const val IMG_GRANITE_RAPAKIVI_MUSEUM = "$Q/56b7b26d-4c5e-45bf-b9f2-cabc5c41b421.png"
    private const val IMG_GRANITE_UNAKITE_WILD = "$Q/c0e0e0ae-e251-4ca7-9120-6f463e450f52.png"
    private const val IMG_GRANITE_UNAKITE_MUSEUM = "$Q/e2a42f64-df73-40ba-bf6f-1819c012dc7d.png"
    private const val IMG_GRANITE_UNAKITE_CAB = "$Q/ae201ceb-936e-4e37-b6a9-cf75ad988575.png"
    // ── Grape agate museum quality ──
    private const val IMG_GRAPE_AGATE_MUSEUM = "$Q/ea839b30-84d9-4c26-96ea-ed9cc2ee43db.png"
    // ── Corundum 4-image upgrades (wild, museum, faceted) ──
    private const val IMG_RUBY_BURMA_WILD = "$Q/884c210f-58ed-4cac-81fc-77f055102685.png"
    private const val IMG_RUBY_BURMA_MUSEUM = "$Q/39358cbd-1dce-4030-851c-4676c2807d70.png"
    private const val IMG_RUBY_BURMA_FACETED = "$Q/4d25aeae-31d3-410b-968b-3e482a88c6e1.png"
    private const val IMG_RUBY_MOZAMBIQUE_WILD = "$Q/50624475-09da-4d9a-8d49-74e8f83d9e6c.png"
    private const val IMG_RUBY_MOZAMBIQUE_MUSEUM = "$Q/60772794-4f43-4761-b94b-2431cc8bdd76.png"
    private const val IMG_RUBY_MOZAMBIQUE_FACETED = "$Q/16fe294a-5347-4bc2-838b-8a9d6f37367a.png"
    private const val IMG_PADPARADSCHA_WILD = "$Q/442cffb7-229b-4407-a1ce-76e45c1fed68.png"
    private const val IMG_PADPARADSCHA_MUSEUM = "$Q/1342c08e-157b-4311-a16c-9cb170a61269.png"
    private const val IMG_PADPARADSCHA_FACETED = "$Q/79621cbb-1150-4983-9820-f72a7dc9c616.png"
    private const val IMG_PINK_SAPPHIRE_WILD = "$Q/0eca9968-f333-4e98-9c81-fe052fac0f5a.png"
    private const val IMG_PINK_SAPPHIRE_MUSEUM = "$Q/7b297bb9-69df-40b4-9db7-d9624eadcb96.png"
    private const val IMG_PINK_SAPPHIRE_FACETED = "$Q/83554489-4fc2-4591-b361-4795d5071a84.png"
    private const val IMG_YELLOW_SAPPHIRE_WILD = "$Q/cc456e67-16ab-40ff-baf7-4f8ca244a4d0.png"
    private const val IMG_YELLOW_SAPPHIRE_MUSEUM = "$Q/45667313-6ca0-4e3a-9288-70aa4438d44b.png"
    private const val IMG_YELLOW_SAPPHIRE_FACETED = "$Q/4288c5ad-c390-4f12-ada9-1f96106d2318.png"
    // ── Calcite 4-image upgrades ──
    private const val IMG_CALCITE_ICELAND_WILD = "$Q/14a1e513-2ce5-4d49-a5d3-4a5f61cda010.png"
    private const val IMG_CALCITE_ICELAND_MUSEUM = "$Q/ebd1aa5c-9e54-4a3e-8906-18c37ec2edf2.png"
    private const val IMG_CALCITE_DOGTOOTH_WILD = "$Q/07898cce-3737-47f9-bca2-9f6c2754f324.png"
    private const val IMG_CALCITE_DOGTOOTH_MUSEUM = "$Q/c66717ee-9a93-40d6-bf4d-d134f2c86094.png"
    private const val IMG_CALCITE_MANGANO_WILD = "$Q/fa4db915-4ce6-45a7-919b-3a2c95e4ba0a.png"
    private const val IMG_CALCITE_MANGANO_MUSEUM = "$Q/9cc2a63a-e50f-4c10-bdf5-b14dac395070.png"
    private const val IMG_CALCITE_MANGANO_CAB = "$Q/102f3d02-f733-4cbc-8b84-86264dbdda8d.png"
    // ── Fluorite 4-image upgrades ──
    private const val IMG_FLUORITE_ILLINOIS_WILD = "$Q/23bb7b1a-1f68-4232-ad55-a33298ff6f05.png"
    private const val IMG_FLUORITE_ILLINOIS_MUSEUM = "$Q/46ec841f-719a-4130-960e-d5e83ef8bc40.png"
    private const val IMG_FLUORITE_PINK_WILD = "$Q/4e8823d9-869c-468d-9c14-98a911cf8a7d.png"
    private const val IMG_FLUORITE_PINK_MUSEUM = "$Q/52f1fd90-743b-437f-81e1-532e026b04cb.png"
    private const val IMG_FLUORITE_GREEN_WILD = "$Q/db2b602d-3137-4921-b18a-37e094c9a5fa.png"
    private const val IMG_FLUORITE_GREEN_MUSEUM = "$Q/b50b71d2-c4a8-446e-acff-0a83bef5dcb4.png"
    // ── Feldspar 4-image upgrades ──
    private const val IMG_FELDSPAR_OLIGOCLASE_WILD = "$Q/0a025e2f-1f6a-46fa-bad8-43f7010551c7.png"
    private const val IMG_FELDSPAR_OLIGOCLASE_MUSEUM = "$Q/350e43f5-821b-41c5-a040-61a39067349a.png"
    // ── Batch 71: Sphene, Vesuvianite, Strontianite, Witherite, Wavellite, Vivianite, Stromatolite ──
    private const val IMG_SPHENE_WILD = "$Q/c3c72480-5af5-4815-a37c-d0a349b2cd6a.png"
    private const val IMG_SPHENE_MUSEUM = "$Q/1d428c58-7c2b-4b27-827e-a46e3d383fa5.png"
    private const val IMG_SPHENE_FACETED = "$Q/1cc27c48-0954-40da-ac15-149f96ddfd83.png"
    private const val IMG_VESUVIANITE_WILD = "$Q/b4364a01-32ec-4f09-9be7-8fea247950be.png"
    private const val IMG_VESUVIANITE_MUSEUM = "$Q/16d17f54-e5d6-446d-b324-50729d248b11.png"
    private const val IMG_STRONTIANITE_WILD = "$Q/99fcb49e-6f91-4865-a61f-5f74ee1f61e0.png"
    private const val IMG_STRONTIANITE_MUSEUM = "$Q/40afe7f5-7262-443a-b46e-a108fc642ad3.png"
    private const val IMG_WITHERITE_WILD = "$Q/7424dec3-61bf-4db2-bdcb-d622da464bc6.png"
    private const val IMG_WITHERITE_MUSEUM = "$Q/298c083f-3dcc-4bbd-93c9-67f9ea9fb7c5.png"
    private const val IMG_WAVELLITE_WILD = "https://r2-pub.rork.com/web-fetch-images/640328ede12ac9642f52dfb73954b87d7fad1d283a244f2fd9ad7ea0d583af56.jpeg"
    private const val IMG_WAVELLITE_MUSEUM = "https://r2-pub.rork.com/web-fetch-images/b75eeb76907215c9540f694af3308aaff1de7951c63f46526b3c2115951c5286.jpeg"
    private const val IMG_VIVIANITE_WILD = "$Q/dba5dad2-32e1-41c9-826b-454a9808226b.png"
    private const val IMG_VIVIANITE_MUSEUM = "$Q/a94d2bf2-5610-49d9-b063-bfa44a574342.png"
    private const val IMG_STROMATOLITE_WILD = "$Q/c6a4194d-6f27-4ff4-9952-b08ad5223378.png"
    private const val IMG_STROMATOLITE_MUSEUM = "$Q/948685c0-95ca-4b6f-ab3b-dd632e6e065e.png"
    // ── Batch 72: Fossil 4-image upgrades (wild, museum) ──
    private const val IMG_GRAPTOLITE_WILD = "$Q/cd9864a2-434b-4b7e-9926-b387132fca72.png"
    private const val IMG_GRAPTOLITE_MUSEUM = "$Q/c7be8120-5e74-4b2c-9a84-469a303e6388.png"
    private const val IMG_GRYPHAEA_WILD = "$Q/85dd8a2a-9755-4cf4-afb0-ff0c396bbe75.png"
    private const val IMG_GRYPHAEA_MUSEUM = "$Q/3625776e-a3ec-479b-9eeb-82aed3c37404.png"
    private const val IMG_HALYSITES_WILD = "$Q/3207379a-e295-4216-acd7-8127b052930b.png"
    private const val IMG_HALYSITES_MUSEUM = "$Q/6d2ae56b-cb1c-4647-8056-337c54eda4e1.png"
    private const val IMG_RUGOSE_CORAL_WILD = "$Q/10e90b61-7270-4c47-8356-95e3a1305f4e.png"
    private const val IMG_RUGOSE_CORAL_MUSEUM = "$Q/27b7867d-acb9-4af3-b6b7-827c4442188e.png"
    private const val IMG_SCAPHITES_WILD = "$Q/aef57394-1cb0-43ba-b3da-5be3614fd6b4.png"
    private const val IMG_SCAPHITES_MUSEUM = "$Q/3f198eed-23e8-4b82-b6b5-bc4799a0ae84.png"
    private const val IMG_TURRITELLA_WILD = "$Q/caf9eb1c-944b-4132-8e34-34d90b61bcea.png"
    private const val IMG_TURRITELLA_MUSEUM = "$Q/06c45ce3-674c-4d79-8eba-8e02bec38c61.png"
    private const val IMG_SEA_URCHIN_WILD = "$Q/579c57e4-2120-44bc-9697-3531eec3b432.png"
    private const val IMG_SEA_URCHIN_MUSEUM = "$Q/eb15d135-78a1-4e53-a1b5-e68aa1add1ed.png"
    private const val IMG_STARFISH_WILD = "$Q/b11453a2-cf6d-4dd9-83c5-f32356145fd0.png"
    // ── Batch 73: Astrophyllite, Brazilianite, Eudialyte, Torbernite, Willemite, Uvarovite, Sagenitic Agate ──
    private const val IMG_ASTROPHYLLITE_WILD = "$Q/a229a087-ed6e-4985-8205-db776395ce89.png"
    private const val IMG_ASTROPHYLLITE_MUSEUM = "$Q/0c4cd552-670d-4c8f-b19d-047889083d47.png"
    private const val IMG_BRAZILIANITE_WILD = "$Q/4fcdf2f7-49b1-446b-8913-56c28ad00a0c.png"
    private const val IMG_BRAZILIANITE_MUSEUM = "$Q/ffcc5d11-7c9c-4b1e-93ac-7676be26e8fb.png"
    private const val IMG_BRAZILIANITE_FACETED = "$Q/0150cee0-4aa3-481c-b630-89b0b11946d7.png"
    private const val IMG_EUDIALYTE_WILD = "$Q/af3e0e12-dae1-49fa-a7f9-2f073770cdf8.png"
    private const val IMG_EUDIALYTE_MUSEUM = "$Q/9b2c7edb-8a3a-4f9c-bce3-b9b9fb92b029.png"
    private const val IMG_EUDIALYTE_CAB = "$Q/e7f21c35-e940-4505-b8e3-dd6bffdb4774.png"
    private const val IMG_TORBERNITE_WILD = "$Q/0637909d-7b63-48a3-9a05-349cac885a2b.png"
    private const val IMG_TORBERNITE_MUSEUM = "$Q/abe3196c-ed5e-436f-bde2-8013b9981307.png"
    private const val IMG_WILLEMITE_WILD = "$Q/c97340d6-7170-47c8-9c97-748629d89689.png"
    private const val IMG_WILLEMITE_MUSEUM = "$Q/242ab93e-0a8b-4312-9336-0095459a9f68.png"
    private const val IMG_UVAROVITE_WILD = "$Q/a54f6bc4-30a9-4340-9efb-38c791688019.png"
    private const val IMG_UVAROVITE_MUSEUM = "$Q/f4c1a146-5a7c-42f4-9eb1-53a2a00429ff.png"
    private const val IMG_SAGENITIC_AGATE_WILD = "$Q/e29ff918-217d-42a3-b647-002b068d34ca.png"
    // ── Batch 74: Anatase, Brookite, Aurichalcite, Bismuthinite, Bournonite, Columbite, Cubanite, Diaspore ──
    private const val IMG_ANATASE_WILD = "$Q/b0d23e05-1e5f-4fa8-94b9-9c936c77b848.png"
    private const val IMG_ANATASE_MUSEUM = "$Q/1d90684e-ebbf-4bba-8454-38fa7641c6e0.png"
    private const val IMG_BROOKITE_WILD = "$Q/06094aa8-973a-487d-8622-2f61a931c349.png"
    private const val IMG_BROOKITE_MUSEUM = "$Q/31266981-79b3-41cb-bdee-0e3404515a3f.png"
    private const val IMG_AURICHALCITE_WILD = "$Q/bbe71837-1e4d-4d87-9e58-56c835af3d81.png"
    private const val IMG_AURICHALCITE_MUSEUM = "$Q/e22ae118-7665-42e5-969c-f3b06ea3a939.png"
    private const val IMG_BISMUTHINITE_WILD = "$Q/ace232ae-4ab8-41db-a187-bc6a6cf7fd14.png"
    private const val IMG_BISMUTHINITE_MUSEUM = "$Q/a795897c-0ae8-4401-ac2b-1eb37af53722.png"
    private const val IMG_BOURNONITE_WILD = "$Q/3aaf6ae1-cf95-4bed-b094-a8b60f2d26f6.png"
    private const val IMG_BOURNONITE_MUSEUM = "$Q/52b898f3-13e6-49cc-a8d7-9cb704e4677a.png"
    private const val IMG_COLUMBITE_WILD = "$Q/b4f20a9b-13ff-445b-9cde-ff1261f47599.png"
    private const val IMG_COLUMBITE_MUSEUM = "$Q/32449cb4-2c6a-4fa7-9597-47dc5b8ccda8.png"
    private const val IMG_CUBANITE_WILD = "$Q/ae8f649a-0603-4490-a084-2cf1a2abc0e3.png"
    private const val IMG_CUBANITE_MUSEUM = "$Q/12845195-645a-4871-b2c1-1d58534453fa.png"
    private const val IMG_DIASPORE_WILD = "$Q/e49bc047-3f52-4be5-bb00-8276da2abe64.png"
    // ── Batch 75: Fossil/prehistoric organism upgrades (wild, museum) ──
    private const val IMG_HELICOPRION_WILD = "$Q/457fadf6-08a3-4d3a-be86-414087bb2079.png"
    private const val IMG_HELICOPRION_MUSEUM = "$Q/b48f9f2e-6441-4782-aa1d-493d103bd983.png"
    private const val IMG_MOSASAUR_TOOTH_WILD = "$Q/be9c4d34-e33b-4584-8721-ded870a29430.png"
    private const val IMG_MOSASAUR_TOOTH_MUSEUM = "$Q/c0813035-216f-4364-bb4e-c5d60dd4fef4.png"
    private const val IMG_GREAT_WHITE_TOOTH_WILD = "$Q/d526e7f2-85a7-454f-a7d6-2c143820a0aa.png"
    private const val IMG_GREAT_WHITE_TOOTH_MUSEUM = "$Q/69dd0783-95bf-4b9a-a51c-bca6b23678a0.png"
    private const val IMG_SMILODON_TOOTH_WILD = "$Q/6c548003-b5be-4964-a086-4b483f8adc28.png"
    private const val IMG_SMILODON_TOOTH_MUSEUM = "$Q/4089793c-ae9b-4517-a45d-7ab1e8a0c353.png"
    private const val IMG_STINGRAY_BARB_WILD = "$Q/6ba9f1b7-6e0e-473d-bc1e-f3cfc3f36e4c.png"
    private const val IMG_STINGRAY_BARB_MUSEUM = "$Q/d3393ace-3789-4249-97e4-aeea2d1d0df3.png"
    private const val IMG_STROMATOPOROID_WILD = "$Q/38fd07b5-78d5-4195-9aa4-b5f73bbc239f.png"
    private const val IMG_STROMATOPOROID_MUSEUM = "$Q/56e7336a-2769-4c33-8dc1-9a7143d1a7dc.png"
    private const val IMG_XIPHACTINUS_WILD = "$Q/08929e0b-c7de-412f-a0c0-aa3a80f36084.png"
    private const val IMG_XIPHACTINUS_MUSEUM = "$Q/8431ec23-3c7e-4024-a99c-285c540f8009.png"
    private const val IMG_LEPIDODENDRON_WILD = "$Q/9e405242-9c7f-4d0e-a753-5fc73e224cc0.png"
    // ── Batch 76: Silver Sheen Obsidian, Quartz, Allanite, Amblygonite, Analcime, Enargite, Wolframite ──
    private const val IMG_SILVER_SHEEN_WILD = "$Q/fa05ab6d-e47e-452b-b87e-7b60520f4ce5.png"
    private const val IMG_SILVER_SHEEN_MUSEUM = "$Q/11d80e98-1444-492d-870e-b7c6877abcee.png"
    private const val IMG_SILVER_SHEEN_CAB = "$Q/07a0c470-e012-4b3a-b979-219f1658896b.png"
    private const val IMG_QUARTZ_WILD_3 = "$Q/f82b5be0-340f-4a97-8f50-53dd7bdd76d7.png"
    private const val IMG_QUARTZ_MUSEUM_2 = "$Q/89beb84d-ed89-4431-9629-0d4ecf1b7061.png"
    private const val IMG_ALLANITE_WILD = "$Q/7524928f-6774-4398-b164-301f12818c73.png"
    private const val IMG_ALLANITE_MUSEUM = "$Q/76107dd1-1dea-49e4-9670-43093bf2343f.png"
    private const val IMG_AMBLYGONITE_WILD = "$Q/63ca32ac-f950-4102-97e0-861da4a540f2.png"
    private const val IMG_AMBLYGONITE_MUSEUM = "$Q/f6f616b7-5ef1-48b1-bc75-ad2080368a64.png"
    private const val IMG_ANALCIME_WILD = "$Q/af09fa9f-a830-4ac0-bf04-04beec72a483.png"
    private const val IMG_ANALCIME_MUSEUM = "$Q/f65358ec-40c4-4aca-a5ce-c8251d685fbc.png"
    private const val IMG_ENARGITE_WILD = "$Q/e7784704-8e5e-4baa-8c6e-f8bf4668e45a.png"
    private const val IMG_ENARGITE_MUSEUM = "$Q/95f69f6e-8fdd-4c11-ba8a-05695193187e.png"
    private const val IMG_WOLFRAMITE_WILD = "$Q/25a875da-c56f-496d-96e2-19c317a18870.png"
    private const val IMG_WOLFRAMITE_MUSEUM = "$Q/7f51291a-e3bb-4d1c-82bc-093558f76ebe.png"
    // ── Batch 77: Herderite, Ilvaite, Jarosite, Legrandite, Limonite, Manganite, Hausmannite, Cryolite ──
    private const val IMG_HERDERITE_WILD = "$Q/ab2aa075-38d1-4012-be65-f0f428f847ae.png"
    private const val IMG_HERDERITE_MUSEUM = "$Q/370922ce-3531-42b9-9a6f-3bfc00f74fce.png"
    private const val IMG_ILVAITE_WILD = "$Q/791e1c91-13ce-4f10-9f0a-dbdc71cb2aea.png"
    private const val IMG_ILVAITE_MUSEUM = "$Q/92e04428-36be-45ec-a4ac-58224704f5df.png"
    private const val IMG_JAROSITE_WILD = "$Q/09ecf7bc-c5b7-4284-868d-b525f08082ca.png"
    private const val IMG_JAROSITE_MUSEUM = "$Q/e1bf9c05-b8b4-42d8-89d7-ece04f873ac9.png"
    private const val IMG_LEGRANDITE_WILD = "$Q/397edcc7-7ea6-48f1-aca5-3290bf408294.png"
    private const val IMG_LEGRANDITE_MUSEUM = "$Q/02d9146a-78a3-4fa4-87d8-4753b7e0b990.png"
    private const val IMG_LIMONITE_WILD = "$Q/0bf0b86c-e059-402d-b61c-af0007f32e6d.png"
    private const val IMG_LIMONITE_MUSEUM = "$Q/d3f5cb56-7dd3-42b1-b453-f24c1ba5e4cf.png"
    private const val IMG_MANGANITE_WILD = "$Q/bbe863b6-550b-483d-82b2-8e728fae8261.png"
    private const val IMG_MANGANITE_MUSEUM = "$Q/e409e4bc-537f-4d6e-bf80-9cbbad3df6fc.png"
    private const val IMG_HAUSMANNITE_WILD = "$Q/96be2fe7-8250-4e4f-ab7d-b05a3162be18.png"
    private const val IMG_HAUSMANNITE_MUSEUM = "$Q/ee1a5d80-ac92-45a5-8853-8fa42277d7a9.png"
    private const val IMG_CRYOLITE_WILD = "$Q/90bc2866-7879-48ff-8eb8-59690ec28053.png"
    // ── Batch 78: Beryllonite, Calaverite, Chalcophyllite, Childrenite, Clinoptilolite, Euxenite, Fergusonite, Greenockite ──
    private const val IMG_BERYLLONITE_WILD = "$Q/f9454999-02ad-42ec-8924-de5a8746a1d4.png"
    private const val IMG_BERYLLONITE_MUSEUM = "$Q/29053f89-1a15-43e3-9a56-1215c6230c23.png"
    private const val IMG_CALAVERITE_WILD = "$Q/f7cedf39-688f-4f9a-8234-c6a33fb36950.png"
    private const val IMG_CALAVERITE_MUSEUM = "$Q/abedac24-6525-4143-817f-d5f2ed690bb3.png"
    private const val IMG_CHALCOPHYLLITE_WILD = "$Q/fc0c605e-b6bd-4056-a1e2-ad616a681b94.png"
    private const val IMG_CHALCOPHYLLITE_MUSEUM = "$Q/09f2bd4b-d67e-4caa-b5f1-aeccc6de63ec.png"
    private const val IMG_CHILDRENITE_WILD = "$Q/c0050ada-c121-4241-8ae3-206ab4632d06.png"
    private const val IMG_CHILDRENITE_MUSEUM = "$Q/dae97f78-5be4-4715-b974-34774353cfbc.png"
    private const val IMG_CLINOPTILOLITE_WILD = "$Q/53eaa3f0-acde-4105-83d5-fd7048c9d24c.png"
    private const val IMG_CLINOPTILOLITE_MUSEUM = "$Q/4b414324-1dc2-4e66-9840-b89e39cf369b.png"
    private const val IMG_EUXENITE_WILD = "$Q/19e3061f-8df5-456a-bce4-5f1d3aa5b0c7.png"
    private const val IMG_EUXENITE_MUSEUM = "$Q/c94092c8-5daf-4942-a276-35fc622588c1.png"
    private const val IMG_FERGUSONITE_WILD = "$Q/e720574f-83a7-4d6d-aed4-490d6730fb9d.png"
    private const val IMG_FERGUSONITE_MUSEUM = "$Q/6e324463-2400-4c1d-8d51-930f82509ad9.png"
    private const val IMG_GREENOCKITE_WILD = "$Q/160eefeb-c140-43a5-872e-ef16d055d54a.png"
    // ── Batch 79: Hanksite, Lawsonite, Leadhillite, Laumontite, Ludlamite, Melanterite, Mellite, Monazite ──
    private const val IMG_HANKSITE_WILD = "$Q/e7e4bdb6-4c3b-445c-82d6-8547180ce4bb.png"
    private const val IMG_HANKSITE_MUSEUM = "$Q/7fa9a458-6e71-4ddd-9ef7-1692ec6d556e.png"
    private const val IMG_LAWSONITE_WILD = "$Q/cc37003f-41e7-464f-b971-45b2f8f1354d.png"
    private const val IMG_LAWSONITE_MUSEUM = "$Q/41c223a0-ac61-456d-bcbe-30523cd24fe0.png"
    private const val IMG_LEADHILLITE_WILD = "$Q/9063c396-df03-419f-96aa-e62dfc611bf1.png"
    private const val IMG_LEADHILLITE_MUSEUM = "$Q/db65d126-12d2-4e3a-a654-aaf65ce3c896.png"
    private const val IMG_LAUMONTITE_WILD = "$Q/06119b7c-9a07-4675-ab3a-65dfeb75d646.png"
    private const val IMG_LAUMONTITE_MUSEUM = "$Q/d97e1651-bc19-4aef-b94b-61746c3ba9cb.png"
    private const val IMG_LUDLAMITE_WILD = "$Q/f42c95c6-e0a4-4f1c-a634-e243f67feb95.png"
    private const val IMG_LUDLAMITE_MUSEUM = "$Q/6c10ea8f-f137-47d3-867e-322eabdbe902.png"
    private const val IMG_MELANTERITE_WILD = "$Q/1b1e89f3-7833-4526-819d-439bd9a5fec7.png"
    private const val IMG_MELANTERITE_MUSEUM = "$Q/90212eea-0974-4bd0-9911-d56f31caaa8b.png"
    private const val IMG_MELLITE_WILD = "$Q/4b3baf27-453e-42f3-aaaa-055b32aa445f.png"
    private const val IMG_MELLITE_MUSEUM = "$Q/950aa59a-fc28-42a8-a8cb-b034666ca4f0.png"
    private const val IMG_MONAZITE_WILD = "$Q/85ac040a-56e4-423c-a84f-b637476c58a2.png"
    // ── Batch 80: Montmorillonite, Mordenite, Mirabilite, Pollucite, Pyrophyllite, Rosasite, Samarskite, Scolecite ──
    private const val IMG_MONTMORILLONITE_WILD = "$Q/c8729822-e56d-45ea-a22f-a8fe563449a0.png"
    private const val IMG_MONTMORILLONITE_MUSEUM = "$Q/93f8bf3f-c205-4ee9-83c4-b8931232eb3c.png"
    private const val IMG_MORDENITE_WILD = "$Q/de6e5a21-47f1-41d9-95e4-723c586d8f47.png"
    private const val IMG_MORDENITE_MUSEUM = "$Q/26118b38-c302-41b6-b6f7-0882a811e5e6.png"
    private const val IMG_MIRABILITE_WILD = "$Q/39efde55-9afa-4284-80d9-a88337c7cca0.png"
    private const val IMG_MIRABILITE_MUSEUM = "$Q/d1921226-0ed7-40e4-8c50-26b3bf56814f.png"
    private const val IMG_POLLUCITE_WILD = "$Q/2c9f07df-20f7-4455-a729-bb60cf269ef1.png"
    private const val IMG_POLLUCITE_MUSEUM = "$Q/791b070e-fa63-45b4-9db5-1fa3a2ae67f2.png"
    private const val IMG_PYROPHYLLITE_WILD = "$Q/cac3cc02-8fe1-48c8-871c-bcd4de04e3df.png"
    private const val IMG_PYROPHYLLITE_MUSEUM = "$Q/f01b0aca-29b7-4102-a758-89d72657fb8d.png"
    private const val IMG_ROSASITE_WILD = "$Q/876d0c2a-49e3-4692-8f02-663224804634.png"
    private const val IMG_ROSASITE_MUSEUM = "$Q/0b92c3fb-e35b-4597-b5a8-a9de66927746.png"
    private const val IMG_SAMARSKITE_WILD = "$Q/139fa008-2cd7-47ef-b270-cc8601b2f08c.png"
    private const val IMG_SAMARSKITE_MUSEUM = "$Q/c4fcd2b5-7306-481f-a889-664261863727.png"
    private const val IMG_SCOLECITE_WILD = "$Q/fcc5db65-f177-4343-b770-8f7f05c2ea24.png"
    // ── Batch 81: Red Beryl, Uraninite, Wollastonite, Xenotime, Native Arsenic, Serandite, Bastnasite ──
    private const val IMG_RED_BERYL_WILD = "$Q/8eb3352e-028b-45ab-8acb-b3211935a907.png"
    private const val IMG_RED_BERYL_MUSEUM = "$Q/ae9d69d9-8c36-425d-a985-3aaabe9d215e.png"
    private const val IMG_RED_BERYL_FACETED = "$Q/8bd8da0c-01b1-47bd-8d9c-61b401f17e89.png"
    private const val IMG_URANINITE_WILD = "$Q/22aca935-4a41-4b22-9a07-a48b6f551d53.png"
    private const val IMG_URANINITE_MUSEUM = "$Q/1761b159-b006-40da-83af-8b5bc24ac3d5.png"
    private const val IMG_WOLLASTONITE_WILD = "$Q/90b99bd9-1ca6-4edd-a482-0c45d86f6eb6.png"
    private const val IMG_WOLLASTONITE_MUSEUM = "$Q/69c0ab71-2914-45e1-ab1f-e73b36c43649.png"
    private const val IMG_XENOTIME_WILD = "$Q/fa402d2c-0177-42c8-8520-4b609dedd3e6.png"
    private const val IMG_XENOTIME_MUSEUM = "$Q/54173f4e-91f0-45ad-9087-ccc9bb5e7e94.png"
    private const val IMG_NATIVE_ARSENIC_WILD = "$Q/02b1c76c-457a-4b6b-912f-fa77aa7ab228.png"
    private const val IMG_NATIVE_ARSENIC_MUSEUM = "$Q/7628388a-edfd-42fa-aaf1-cb4cda6fcadd.png"
    private const val IMG_SERANDITE_WILD = "$Q/6be88320-4cff-43b5-a041-953e6a25a9a1.png"
    private const val IMG_SERANDITE_MUSEUM = "$Q/88152b34-8154-40af-94a5-d20715d1b21a.png"
    private const val IMG_BASTNASITE_WILD = "$Q/1dfab234-e587-4a36-9ed7-d271d7fad713.png"
    private const val IMG_BASTNASITE_MUSEUM = "$Q/723492d3-ac14-4c9e-8f73-226579e7d9f8.png"
    // ── Batch 82: Prehistoric organism fossil upgrades (wild, museum) ──
    private const val IMG_BRONTOTHERIUM_WILD = "$Q/3c46695c-8820-4d1b-93cf-156c6d471a7c.png"
    private const val IMG_BRONTOTHERIUM_MUSEUM = "$Q/49c41eed-3c37-4204-a8c2-73a08c7c1ccc.png"
    private const val IMG_GLYPTODON_WILD = "$Q/e348d9e8-4004-447a-902c-c619d71314a5.png"
    private const val IMG_GLYPTODON_MUSEUM = "$Q/5a3fb1e4-1ce9-49c6-a972-132763438f23.png"
    private const val IMG_MEGATHERIUM_WILD = "$Q/9b759b65-3183-40af-9aeb-4fd16ce81196.png"
    private const val IMG_MEGATHERIUM_MUSEUM = "$Q/346bd7bc-4967-415b-b51f-9960822f47be.png"
    private const val IMG_PARASAUROLOPHUS_WILD = "$Q/ccb26f9b-c7ba-4983-a1d7-a228ce5db0bd.png"
    private const val IMG_PARASAUROLOPHUS_MUSEUM = "$Q/482bc20e-02b7-4a90-9871-cac6effc936e.png"
    private const val IMG_STEGOSAURUS_WILD = "$Q/6717c509-d5a3-49a9-8e81-9ed98747f008.png"
    private const val IMG_STEGOSAURUS_MUSEUM = "$Q/3eeebdbc-70ce-4811-bfad-47d15a1c5240.png"
    private const val IMG_TRICERATOPS_WILD = "$Q/e244d70a-a1cc-4f62-b5c0-2755aedaab47.png"
    private const val IMG_TRICERATOPS_MUSEUM = "$Q/b169d58e-7ee5-4183-83f2-b314f8a6eb38.png"
    private const val IMG_VELOCIRAPTOR_WILD = "$Q/c277aeb8-683c-46ed-b6d0-6ac8bc3e3e5c.png"
    private const val IMG_VELOCIRAPTOR_MUSEUM = "$Q/4230d133-3ad7-46c3-862c-20c57c831840.png"
    private const val IMG_PTERANODON_WILD = "$Q/72feb8b7-48f3-457e-9bd3-e8dfcfe6ee67.png"
    // ── Batch 83: Corundum varieties (purple, green, teal sapphire) + Feldspar larvikite + Ruby zoisite ──
    private const val IMG_PURPLE_SAPPHIRE_WILD = "$Q/33ac5ecc-6141-426e-abfc-428c0200ccf3.png"
    private const val IMG_PURPLE_SAPPHIRE_MUSEUM = "$Q/cb6a1d12-df31-4c46-a667-95a3ad09a394.png"
    private const val IMG_PURPLE_SAPPHIRE_FACETED = "$Q/46843b76-5a2e-4255-8b17-bb72b61d3c39.png"
    private const val IMG_GREEN_SAPPHIRE_WILD = "$Q/a0e42f61-a765-4ca6-af95-7d05d7b65fe0.png"
    private const val IMG_GREEN_SAPPHIRE_MUSEUM = "$Q/d9b3ef74-a916-4f00-b88b-89cfb8fb11cf.png"
    private const val IMG_GREEN_SAPPHIRE_FACETED = "$Q/6c55d585-6831-4f0d-96be-cf328f9ca786.png"
    private const val IMG_TEAL_SAPPHIRE_WILD = "$Q/fd4bbfa3-359e-410a-9b7a-b11a305270b6.png"
    private const val IMG_TEAL_SAPPHIRE_MUSEUM = "$Q/6a52808f-5973-4cac-88f9-6b3a5bb0115e.png"
    private const val IMG_TEAL_SAPPHIRE_FACETED = "$Q/661e6de2-cc5b-4665-a2c9-7bc4ed20629e.png"
    private const val IMG_LARVIKITE_WILD = "$Q/9c94e178-7581-4965-bcce-70b13d7ee6f1.png"
    private const val IMG_LARVIKITE_MUSEUM = "$Q/e923df92-fac3-433d-9f0b-ef219f5c5fc9.png"
    private const val IMG_LARVIKITE_CAB = "$Q/7d617f39-1af4-4e00-8c82-b5f25e8000d1.png"
    private const val IMG_RUBY_ZOISITE_WILD = "$Q/a94897c1-a98c-4084-a940-ce10cab82071.png"
    private const val IMG_RUBY_ZOISITE_MUSEUM = "$Q/e543f76e-8642-4964-b1fa-64fec0a5ad1c.png"
    private const val IMG_RUBY_ZOISITE_CAB = "$Q/188129b6-eb0a-4b04-82d2-22087dafddbf.png"
    // ── Batch 84: Prehistoric organism fossil upgrades (wild, museum) ──
    private const val IMG_SPINOSAURUS_WILD = "$Q/ef08c59e-07d6-4716-bac2-809c76d71e8f.png"
    private const val IMG_SPINOSAURUS_MUSEUM = "$Q/0a93a5f2-db1d-4aad-97ad-b45637a08767.png"
    private const val IMG_CARNOTAURUS_WILD = "$Q/905a7d56-d54f-413f-a2d4-6c7ad5b102d0.png"
    private const val IMG_CARNOTAURUS_MUSEUM = "$Q/dc9298fa-5389-464d-bcfc-db8ca0881d65.png"
    private const val IMG_THERIZINOSAURUS_WILD = "$Q/74ceb09b-e713-46a2-abcc-ed18bb9f7ce0.png"
    private const val IMG_THERIZINOSAURUS_MUSEUM = "$Q/934044a3-e68a-47e5-a2f1-d5a1e3e4d41a.png"
    private const val IMG_GIGANOTOSAURUS_WILD = "$Q/24b5de95-ad10-43b3-95ef-9e01a40f0002.png"
    private const val IMG_GIGANOTOSAURUS_MUSEUM = "$Q/8014080a-312e-4e59-8dda-1cba4c47670a.png"
    private const val IMG_QUETZALCOATLUS_WILD = "$Q/78596a00-904d-4f71-a218-e7fe2f2ecc2b.png"
    private const val IMG_QUETZALCOATLUS_MUSEUM = "$Q/4779138a-637e-47c5-9c90-4bfa1b673a13.png"
    private const val IMG_ICHTHYOSAURUS_WILD = "$Q/3ac6176d-6876-41f5-b33d-91e731767005.png"
    private const val IMG_ICHTHYOSAURUS_MUSEUM = "$Q/479c1281-c0b4-4b9a-b400-48422c9a4395.png"
    private const val IMG_PLESIOSAURUS_WILD = "$Q/e7f146fb-3f1d-471f-bafa-55060ac2c413.png"
    private const val IMG_PLESIOSAURUS_MUSEUM = "$Q/18d2de38-e236-4409-a497-67619701b9af.png"
    private const val IMG_LIOPLEURODON_WILD = "$Q/ee81dd7b-7f17-4d23-8a7b-ec31b81a376b.png"
    // ── Batch 85: More prehistoric organism fossil upgrades (wild, museum) ──
    private const val IMG_DIMORPHODON_WILD = "$Q/ab3edaaa-59fd-46d5-8393-bcf328f2f548.png"
    private const val IMG_DIMORPHODON_MUSEUM = "$Q/e34f1412-de9a-46fc-8888-ed424e021246.png"
    private const val IMG_MOSASAURUS_WILD = "$Q/c022ff44-7c2b-4275-9383-dd8bab601874.png"
    private const val IMG_MOSASAURUS_MUSEUM = "$Q/bd212f56-e560-4a3a-9691-d462f6917d18.png"
    private const val IMG_WOOLLY_RHINO_WILD = "$Q/9f52172d-2a7d-418e-9cd0-f90e90edd219.png"
    private const val IMG_WOOLLY_RHINO_MUSEUM = "$Q/f3fd1b9d-e0ac-4964-b6b0-22ad9dd5729a.png"
    private const val IMG_PARACERATHERIUM_WILD = "$Q/863b3c19-0a2b-4ceb-aadc-5ffd0815a125.png"
    private const val IMG_PARACERATHERIUM_MUSEUM = "$Q/30abe38d-a3aa-4b19-93f9-c48f6f6fb246.png"
    private const val IMG_ENTELODONT_WILD = "$Q/4a50ab29-c57f-45f7-ab8b-a9a50690404b.png"
    private const val IMG_ENTELODONT_MUSEUM = "$Q/48a017ef-c1b1-470c-a8ab-65ca8096c73a.png"
    private const val IMG_THYLACINE_WILD = "$Q/f70a5c86-780b-486e-9184-2909b825031d.png"
    private const val IMG_THYLACINE_MUSEUM = "$Q/c2677c2d-1c0c-42b7-9e66-b3894463300f.png"
    private const val IMG_MEGALANIA_WILD = "$Q/c9412bf1-ae81-4662-a64d-22add24787d9.png"
    private const val IMG_MEGALANIA_MUSEUM = "$Q/22636707-0198-476f-997e-41dd0b70595a.png"
    private const val IMG_PHORUSRHACOS_WILD = "$Q/aa219a81-39a5-43e7-909b-d926e59d7620.png"
    // ── Batch 86: More prehistoric organism fossil upgrades (wild, museum) ──
    private const val IMG_ARGENTAVIS_WILD = "$Q/295d2b66-8074-4672-a8ac-2266a91cf245.png"
    private const val IMG_ARGENTAVIS_MUSEUM = "$Q/bdea7641-f9fb-4828-87b4-28773aea0956.png"
    private const val IMG_DODO_WILD = "$Q/e2cdde9b-63b5-486b-be9e-f734e84135e3.png"
    private const val IMG_DODO_MUSEUM = "$Q/7510e1d9-0a7f-4433-81c2-7eb462e62bad.png"
    private const val IMG_MOA_WILD = "$Q/9f949e6b-dd83-4af9-a714-c2dc4afff32a.png"
    private const val IMG_MOA_MUSEUM = "$Q/e440b9b3-97a1-4156-b07a-6cd8db10c2b0.png"
    private const val IMG_MEGANEURA_WILD = "$Q/7d3b3816-f5ac-4dfb-b60b-86db22d9d1fd.png"
    private const val IMG_MEGANEURA_MUSEUM = "$Q/0aeff437-55aa-4cce-ac87-c719a8fde636.png"
    private const val IMG_ARTHROPLEURA_WILD = "$Q/82cfadcc-6445-4381-8bd9-6f9b6378f5d5.png"
    private const val IMG_ARTHROPLEURA_MUSEUM = "$Q/398acd9b-a8d5-4a6d-acd9-10c30f81d171.png"
    private const val IMG_PULMONOSCORPIUS_WILD = "$Q/82c7f077-19e0-49f4-b778-dd58ee7164cd.png"
    private const val IMG_PULMONOSCORPIUS_MUSEUM = "$Q/9c0952df-ad6c-44ba-bd56-4fd189b14477.png"
    private const val IMG_LEEDSICHTHYS_WILD = "$Q/2ae7fb8f-ed5e-47fe-a5da-18132693eddf.png"
    private const val IMG_LEEDSICHTHYS_MUSEUM = "$Q/4c5dd406-f1ee-40a8-90f0-48f4ba233070.png"
    private const val IMG_ERYOPS_WILD = "$Q/98bb6de6-0f99-4c00-8685-918d5c4da6e0.png"
    // ── Batch 87: Diplocaulus, Tiktaalik, Sigillaria, Cordaites, Ginkgo, Foraminifera, Radiolaria, Coccolithophore ──
    private const val IMG_DIPLOCAULUS_WILD = "$Q/016dbc3a-52f0-425b-9b3e-d5f4679a1564.png"
    private const val IMG_DIPLOCAULUS_MUSEUM = "$Q/08a4055a-e088-465f-a616-f5ecb168cc29.png"
    private const val IMG_TIKTAALIK_WILD = "$Q/3d397b1f-845e-44ce-99e6-43f49c863107.png"
    private const val IMG_TIKTAALIK_MUSEUM = "$Q/4aaefee2-364f-4f0b-a1cd-e79f1d8f13e9.png"
    private const val IMG_SIGILLARIA_WILD = "$Q/c1ec2faf-4503-41fc-ac54-4683196579cc.png"
    private const val IMG_SIGILLARIA_MUSEUM = "$Q/bc66643c-f3d9-434d-9579-109e7cb8a533.png"
    private const val IMG_CORDAITES_WILD = "$Q/b679cdd2-ea2d-4ec6-ae3d-3739aacb7a8c.png"
    private const val IMG_CORDAITES_MUSEUM = "$Q/e4d294fd-f6bf-458e-9fb9-952cea342d33.png"
    private const val IMG_FORAMINIFERA_WILD = "$Q/bfd21def-d92f-4c8a-9657-e4a172cf5e21.png"
    private const val IMG_FORAMINIFERA_MUSEUM = "$Q/21ed570d-9649-4a5b-9022-9b79b425371c.png"
    private const val IMG_RADIOLARIA_WILD = "$Q/7c375d8b-e1aa-46de-8125-d5960ef7628c.png"
    private const val IMG_RADIOLARIA_MUSEUM = "$Q/bcf3c165-fa63-47fa-b639-c0fcaf6564f4.png"
    private const val IMG_COCCOLITHOPHORE_WILD = "$Q/4330fca5-3ff1-4021-b8c7-2a94285d316c.png"

    // ── Batch 70: 4-image upgrades for remaining specimens ──
    private const val IMG_SNOWFLAKE_OBSIDIAN_WILD = "$Q/e8c8db5f-306a-4f6d-9a30-893ff21ee8b4.png"
    private const val IMG_SNOWFLAKE_OBSIDIAN_MUSEUM = "$Q/95bc85d1-bffd-4bd5-a2e9-948365d05d60.png"
    private const val IMG_SEPIOLITE_WILD = "$Q/3aa5c1d4-50a4-4129-8960-56dc2061d35d.png"
    private const val IMG_SEPIOLITE_MUSEUM = "$Q/c5130ae6-1cf4-4c55-9223-91555bfa21e6.png"
    private const val IMG_SHORTITE_WILD = "$Q/d7628da1-02a2-4ffe-9bf6-c6b2f7e46aeb.png"
    private const val IMG_SHORTITE_MUSEUM = "$Q/2612ff6f-369c-4c82-815c-93be0e8624de.png"
    private const val IMG_SILTSTONE_WILD = "$Q/a51ba96e-d719-4814-bab2-d2502041352a.png"
    private const val IMG_SILTSTONE_MUSEUM = "$Q/b8c6ebd0-f36a-4b72-81f3-ebb4f99e3f00.png"
    private const val IMG_SPENCER_OPAL_WILD = "$Q/4244e68b-6479-4089-a6b5-e80b5cdaa27b.png"
    private const val IMG_SPENCER_OPAL_MUSEUM = "$Q/4c4963c8-19b8-4a42-9cb3-a869af1a8615.png"
    private const val IMG_SPENCER_OPAL_CAB = "$Q/032d614e-529a-472d-b05b-901e13719f21.png"
    private const val IMG_SUNSTONE_PLUSH_WILD = "$Q/37984885-2030-4f89-8c8c-c2abe5e545c8.png"
    private const val IMG_SUNSTONE_PLUSH_MUSEUM = "$Q/a49cab4f-71db-4d2b-aa1f-f8c385b9fc21.png"
    private const val IMG_SUNSTONE_PLUSH_CAB = "$Q/7d6a074c-2a36-4dbf-9472-5c226cbff367.png"
    private const val IMG_SWEET_HOME_WILD = "$Q/332437c3-c2cb-4b6a-a901-34157abe5b8e.png"
    private const val IMG_SWEET_HOME_MUSEUM = "$Q/3fa3b800-784e-4273-b9a3-93b0863ad9e5.png"
    private const val IMG_SWEET_HOME_CAB = "$Q/a94e61cd-5d82-4250-be59-917ed17231c7.png"
    private const val IMG_TERLINGUA_WILD = "$Q/d0989e97-bf66-4271-8b44-66639065ba8a.png"
    private const val IMG_TERLINGUA_MUSEUM = "$Q/2953fbe4-93e6-4b80-8b7e-b36e489c6fc3.png"
    private const val IMG_TOURMALINE_QUEEN_WILD = "$Q/048a6d43-f3b6-423b-beff-ab1a45f39763.png"
    private const val IMG_TOURMALINE_QUEEN_MUSEUM = "$Q/b81d4568-450c-4e4e-8d74-543db16bb77f.png"
    private const val IMG_TOURMALINE_QUEEN_CAB = "$Q/ddc61134-be8e-4fc2-ba69-38070542908a.png"
    private const val IMG_TRINITY_AGATE_WILD = "$Q/c7698408-f0c8-4267-9183-cffdf1f34efd.png"
    private const val IMG_TRINITY_AGATE_MUSEUM = "$Q/928d4239-5e15-45c7-9aa7-04314ad90156.png"
    private const val IMG_TRINITY_AGATE_CAB = "$Q/ef7d0604-0175-4039-929e-01e6c2f290a8.png"
    private const val IMG_WACKE_WILD = "$Q/bd8898f5-b607-4648-ad38-22cdc27cb9f7.png"
    private const val IMG_WACKE_MUSEUM = "$Q/0c171453-31eb-4147-afb8-f3a3397e2178.png"
    private const val IMG_WEGNER_QUARTZ_WILD = "$Q/059fd3b2-2e71-4d65-8ae2-eb618ab61b5a.png"
    private const val IMG_WEGNER_QUARTZ_MUSEUM = "$Q/8c3b3846-0c61-484a-a71f-ec5aaca4d570.png"
    private const val IMG_ZUNYITE_WILD = "$Q/e279a7a5-5c40-4f67-878b-9511d889074e.png"
    private const val IMG_ZUNYITE_MUSEUM = "$Q/8e2d8fd1-10d3-4f93-976e-6bf450ca6373.png"
    private const val IMG_SYLVITE_WILD = "$Q/c25d5e42-ca73-49e9-94f5-b5967c941f7d.png"
    private const val IMG_SYLVITE_MUSEUM = "$Q/3df60e42-c962-4f1f-b4c3-c6dacfecc4b3.png"
    private const val IMG_ARGILLITE_WILD = "$Q/8b4725cc-22b8-4127-ad18-a40adabc309d.png"
    private const val IMG_ARGILLITE_MUSEUM = "$Q/3ebcfe97-86ec-4a59-92b1-2f51f9263702.png"
    private const val IMG_CARNALLITE_WILD = "$Q/3ace7a04-ca60-4360-93dd-efbc1117e83e.png"
    private const val IMG_CARNALLITE_MUSEUM = "$Q/b1f86432-8438-4ae1-bb76-0c0951c27dc5.png"
    private const val IMG_DIAMICTITE_WILD = "$Q/b96f02a7-1cea-45b1-aa8e-5a95edfbd30b.png"
    private const val IMG_DIAMICTITE_MUSEUM = "$Q/d68fe013-c706-4232-8e49-4113f86441c7.png"
    private const val IMG_EUCRYPTITE_WILD = "$Q/554cf6f1-50bb-433c-b8b1-9ac442636273.png"
    private const val IMG_EUCRYPTITE_MUSEUM = "$Q/5fa5337d-6f35-4515-8def-756411c00083.png"
    private const val IMG_ITACOLUMITE_WILD = "$Q/9e041335-7bcb-4632-8aca-b64efc5e9262.png"
    private const val IMG_ITACOLUMITE_MUSEUM = "$Q/04642a2c-8de4-49dc-bc12-4acc92135c96.png"
    private const val IMG_JAMESONITE_WILD = "$Q/55711029-407a-4c7b-afe6-23893acdbe45.png"
    private const val IMG_JAMESONITE_MUSEUM = "$Q/dc10d56c-eaf3-499a-8e62-fb74bd9a37e6.png"
    private const val IMG_KAINITE_WILD = "$Q/864e0893-6ba1-4240-9121-a058f3fb9222.png"
    private const val IMG_KAINITE_MUSEUM = "$Q/d64eb649-8fd2-414f-ac83-70418aa95d71.png"
    private const val IMG_LANGBEINITE_WILD = "$Q/96338039-0b2f-4915-ad7f-18fe62af8d46.png"
    private const val IMG_LANGBEINITE_MUSEUM = "$Q/af210a76-04f7-4021-bd12-31ebcfcc4ada.png"
    private const val IMG_PARISITE_WILD = "$Q/517b4382-8b33-4bbf-9dbf-9e87bca8654f.png"
    private const val IMG_PARISITE_MUSEUM = "$Q/d294e38c-3283-4e26-bf44-0fb4d60fdab5.png"
    private const val IMG_PHONOLITE_WILD = "$Q/567d424b-fc76-4a66-b6d1-bb3a1addd89e.png"
    private const val IMG_PHONOLITE_MUSEUM = "$Q/4b9c7d9b-6ac1-470c-bd5f-6c6b57fb6012.png"
    private const val IMG_PHOSPHORITE_WILD = "$Q/fd0134b7-3282-46cb-beaf-04279be86ee7.png"
    private const val IMG_PHOSPHORITE_MUSEUM = "$Q/be5d84ee-60d9-4123-b603-8c9a2b6a949a.png"
    private const val IMG_THENARDITE_WILD = "$Q/85b48de0-7610-485b-b4bf-7df9c79fe0f6.png"
    private const val IMG_THENARDITE_MUSEUM = "$Q/a8fd4eea-d41e-4217-b967-84ae85cd5706.png"
    private const val IMG_TRIPHYLITE_WILD = "$Q/b5441b3b-b4f3-47d5-bb42-8edee951107a.png"
    private const val IMG_TRIPHYLITE_MUSEUM = "$Q/763a4f70-d38e-4905-9118-f6e6f3759e92.png"
    private const val IMG_TROCTOLITE_WILD = "$Q/b0e287bc-0906-4588-b2de-d8885544118d.png"
    private const val IMG_TROCTOLITE_MUSEUM = "$Q/da786c58-c04b-4d86-a3c7-27069c7672d5.png"
    // Batch 71: cherokee-ruby, crabtree-emerald, dugway-geodes (quartz already had wild/museum)
    private const val IMG_CHEROKEE_RUBY_WILD = "$Q/5cefbc57-c755-4370-bb8f-cd28401155a6.png"
    private const val IMG_CHEROKEE_RUBY_MUSEUM = "$Q/bdfef4cb-3aca-47ba-a149-6e3b65fe4c6d.png"
    private const val IMG_CRABTREE_EMERALD_WILD = "$Q/cfa47279-ab6c-4103-85c4-1d3d07006af9.png"
    private const val IMG_CRABTREE_EMERALD_MUSEUM = "$Q/92d3fdf7-a0f6-4bcc-8069-9ca3700eca71.png"
    private const val IMG_CRABTREE_EMERALD_CAB = "$Q/5b3471bb-74da-49df-8a27-9078f509eb9a.png"
    private const val IMG_DUGWAY_GEODES_WILD = "$Q/2b53e824-4ecc-43f5-9ed6-19abe9995720.png"
    private const val IMG_DUGWAY_GEODES_MUSEUM = "$Q/e9acc377-0324-4e0e-9898-c09a1f115c61.png"
    private const val IMG_DUGWAY_GEODES_CAB = "$Q/c5afc498-8acd-410f-ae01-9467c890a31b.png"
    // Batch 72: ellenville, emerald-hollow, enchantment-agates, helena-sapphire
    private const val IMG_ELLENVILLE_WILD = "$Q/36ac0a1a-9e99-4443-a6df-40aaa576de27.png"
    private const val IMG_ELLENVILLE_MUSEUM = "$Q/2f0037ca-a451-4da4-a9ac-b671b63c4722.png"
    private const val IMG_EMERALD_HOLLOW_WILD = "$Q/5cecc374-8e46-4211-a989-1b73aab5ee1e.png"
    private const val IMG_EMERALD_HOLLOW_MUSEUM = "$Q/e5e63420-a9f2-4b19-894a-e20c9c6e9e2f.png"
    private const val IMG_EMERALD_HOLLOW_CAB = "$Q/a0796569-cff1-45c4-8f76-a8c96efb1572.png"
    private const val IMG_ENCHANTMENT_AGATE_WILD = "$Q/4e497046-c23b-4965-bf55-87855324136c.png"
    private const val IMG_ENCHANTMENT_AGATE_MUSEUM = "$Q/6aa588bc-459f-4746-aced-ebc07b41abe7.png"
    private const val IMG_ENCHANTMENT_AGATE_CAB = "$Q/562d50cd-1206-4631-9b75-55a7ee9edef7.png"
    private const val IMG_HELENA_SAPPHIRE_WILD = "$Q/28ad6f79-7a65-4dee-b86f-594172241493.png"
    private const val IMG_HELENA_SAPPHIRE_MUSEUM = "$Q/2308a36e-7f1c-435b-a62f-2a9607c2d963.png"
    // Batch 73: jackson-crossroads, lake-george, mount-apatite, rutile-harrison
    private const val IMG_JACKSON_CROSSROADS_WILD = "$Q/533151d6-16f7-47d3-aee2-2eb6d6603935.png"
    private const val IMG_JACKSON_CROSSROADS_MUSEUM = "$Q/aadd404a-2603-49a0-b748-67853555443b.png"
    private const val IMG_LAKE_GEORGE_WILD = "$Q/c9095c67-e0c5-4db0-92f4-6312cddc9437.png"
    private const val IMG_LAKE_GEORGE_MUSEUM = "$Q/9db07b8a-58e3-4e21-9805-a61751e30050.png"
    private const val IMG_LAKE_GEORGE_CAB = "$Q/1bfa3b54-8454-4a9a-91f2-7daa5e5812f4.png"
    private const val IMG_MOUNT_APATITE_WILD = "$Q/e408a0ee-9399-4a30-8a50-c2c6a688821a.png"
    private const val IMG_MOUNT_APATITE_MUSEUM = "$Q/714f55af-147f-486f-ac42-b34ac93d9ffa.png"
    private const val IMG_MOUNT_APATITE_CAB = "$Q/17004caf-1529-4011-9153-5e9add83145c.png"
    private const val IMG_RUTILE_HARRISON_WILD = "$Q/cd578592-572e-4ad3-bc4d-9f3035e0bb66.png"
    private const val IMG_RUTILE_HARRISON_MUSEUM = "$Q/e12f059b-6a24-4401-807e-55170e548fcd.png"
    // Batch 74: san-carlos, denio-thundereggs, royal-peacock, petrified-wood-rainbow
    private const val IMG_SAN_CARLOS_WILD = "$Q/ca30a4ac-91e3-404e-bfd9-d0f690ba81dc.png"
    private const val IMG_SAN_CARLOS_MUSEUM = "$Q/407917f3-480b-48c8-8a59-b0d68ce42c5d.png"
    private const val IMG_SAN_CARLOS_CAB = "$Q/19b498b8-17da-4fd3-b7b9-11f769d80c3a.png"
    private const val IMG_DENIO_THUNDEREGG_WILD = "$Q/b92c52d1-7bf9-4e41-919c-4b4e2678a1c8.png"
    private const val IMG_DENIO_THUNDEREGG_MUSEUM = "$Q/77c3751f-43d8-49e9-baff-205359339e72.png"
    private const val IMG_DENIO_THUNDEREGG_CAB = "$Q/ce1441e9-3a42-4bbb-a97a-212562b70eae.png"
    private const val IMG_ROYAL_PEACOCK_WILD = "$Q/1f064772-80b3-4166-98a6-a7133a59c768.png"
    private const val IMG_ROYAL_PEACOCK_MUSEUM = "$Q/cf55890d-c62d-472f-954b-7e31bf446e52.png"
    private const val IMG_ROYAL_PEACOCK_CAB = "$Q/84a33fd7-c780-4224-a51d-01401f6eea1f.png"
    private const val IMG_PETRIFIED_RAINBOW_WILD = "$Q/11f20c01-76e3-4fc3-8934-5881eeffe017.png"
    // Batch 75: petrified-wood-arizona, oregon-green, washington, argentine, archelon
    private const val IMG_PETRIFIED_ARIZONA_WILD = "$Q/cfbeaa32-7d1b-41e2-b74c-845881ddc6e8.png"
    private const val IMG_PETRIFIED_ARIZONA_MUSEUM = "$Q/a5e33096-2ddd-4dfd-8f6c-4801c49db004.png"
    private const val IMG_PETRIFIED_ARIZONA_CAB = "$Q/c4bd924b-a091-464a-9c44-5e13c97b80e6.png"
    private const val IMG_PETRIFIED_OREGON_WILD = "$Q/2c1a2543-8c26-4b09-9062-03847bb750cd.png"
    private const val IMG_PETRIFIED_OREGON_MUSEUM = "$Q/e34b81b0-2d00-4a29-9108-4f40fcf467e2.png"
    private const val IMG_PETRIFIED_WASHINGTON_WILD = "$Q/50c6b1da-e7b4-4092-ad2d-46cb0402d4ef.png"
    private const val IMG_PETRIFIED_WASHINGTON_MUSEUM = "$Q/3615c1af-03d6-4615-9b11-1a3f4ed54ba6.png"
    private const val IMG_PETRIFIED_ARGENTINE_WILD = "$Q/99056f4a-4353-47b7-9284-c854764d9811.png"
    private const val IMG_PETRIFIED_ARGENTINE_MUSEUM = "$Q/1b41b0f4-bc98-4598-ab6d-e0a550a58271.png"
    private const val IMG_ARCHELON_WILD = "$Q/aa387990-2bac-4f0a-9f74-d426dded5465.png"
    // Batch 76: smilodon-skull, uintatherium, mammoth-tusk, tourmaline-pegmatite
    private const val IMG_SMILODON_SKULL_WILD = "$Q/496401a7-944a-49a7-81cc-0ce65e834fd0.png"
    private const val IMG_SMILODON_SKULL_MUSEUM = "$Q/e3647519-8e8a-43c5-9161-2d1ad6ae14c4.png"
    private const val IMG_UINTATHERIUM_WILD = "$Q/b023bf41-90d1-4aab-8fba-f2c8feae19fe.png"
    private const val IMG_UINTATHERIUM_MUSEUM = "$Q/000df048-1dff-485a-bdb0-592c0b20f2c3.png"
    private const val IMG_MAMMOTH_TUSK_WILD = "$Q/a6ec23b8-510e-4ebf-80fd-4090afe1aa81.png"
    private const val IMG_MAMMOTH_TUSK_MUSEUM = "$Q/fcf83338-6289-4cd3-a2f2-7543aca60ffb.png"
    private const val IMG_MAMMOTH_TUSK_CROSS = "$Q/309c26ff-f187-4467-a9d9-8cf22f2f5139.png"
    private const val IMG_MAMMOTH_TUSK_CABOCHON = "$Q/8dd8e4c8-18ae-4093-ac4d-73f534f21485.png"
    private const val IMG_TOURMALINE_PEGMATITE_WILD = "$Q/67fb79c0-3159-4d3e-b593-af5aad4a2fe9.png"
    private const val IMG_TOURMALINE_PEGMATITE_MUSEUM = "$Q/15ebd34a-25eb-4d8b-b267-927fbb25e176.png"
    private const val IMG_TOURMALINE_PEGMATITE_CAB = "$Q/2b8800c9-0f30-4934-8e7f-d00f13de2f74.png"
    // Batch 77: petoskey-stone, natural-pearls, geode, fossil-soup-assemblage
    private const val IMG_PETOSKEY_WILD_2 = "$Q/79c4e2ce-828e-4196-9aba-f6149fc7cf55.png"
    private const val IMG_PETOSKEY_MUSEUM = "$Q/0a6a7fe0-778b-4c16-934e-3d367c73544c.png"
    private const val IMG_PETOSKEY_CAB = "$Q/865d67f8-f5b5-4ca9-9a80-0e4c52f1032e.png"
    private const val IMG_PETOSKEY = "$Q/7e337778-c293-4e20-a831-17e4cdb3eb94.png"
    private const val IMG_NATURAL_PEARLS_WILD = "$Q/567b61e2-9290-4afa-9ebb-6caf38c29db8.png"
    private const val IMG_NATURAL_PEARLS_MUSEUM = "$Q/6e41e562-af1a-4e4b-9660-67c3eb08c989.png"
    private const val IMG_GEODE_WILD_2 = "$Q/3142290d-8d81-4fd0-b829-32643180ae64.png"
    private const val IMG_GEODE_MUSEUM = "$Q/0f2b7bd0-001f-45af-8678-def09cf2ffeb.png"
    private const val IMG_GEODE_CAB = "$Q/ec641a6a-1c9e-4e69-94e9-9abf225cc542.png"
    private const val IMG_GEODE = "$Q/fba90315-edf1-44b4-97cd-ab4a25f53e2b.png"
    private const val IMG_FOSSIL_SOUP_WILD = "$Q/586ba54d-e81f-4c09-bf85-b8a4d82d8320.png"
    private const val IMG_FOSSIL_SOUP_MUSEUM = "$Q/761a8f3c-1d0f-4b84-97eb-bf2051d86796.png"
    // Batch 78: quartz-chalcopyrite, native-silver, pyrrhotite, pentlandite, acanthite
    private const val IMG_QUARTZ_CHALCOPYRITE_WILD = "$Q/d3894249-0ff5-42b9-9336-9b5f35a8da14.png"
    private const val IMG_QUARTZ_CHALCOPYRITE_MUSEUM = "$Q/af428930-e9d6-407b-9482-e833bc45fe78.png"
    private const val IMG_NATIVE_SILVER_ASSEMBLAGE_WILD = "$Q/f38cd7aa-484e-4d6f-9818-f458a9c6973c.png"
    private const val IMG_NATIVE_SILVER_ASSEMBLAGE_MUSEUM = "$Q/949223f3-dcb5-4def-81cd-e2047cfd7ec8.png"
    private const val IMG_PYRRHOTITE_WILD = "$Q/4e5c877b-5c68-44bb-8821-496bc63d7c3b.png"
    private const val IMG_PYRRHOTITE_MUSEUM = "$Q/8836f368-f864-4ece-8837-27059a040e1d.png"
    private const val IMG_PENTLANDITE_WILD = "$Q/fc714f07-c6c8-47bd-a33f-0f5d92d9554b.png"
    private const val IMG_PENTLANDITE_MUSEUM = "$Q/239bfe54-692b-45e9-afe1-f2dd2064e309.png"
    private const val IMG_ACANTHITE_WILD = "$Q/5c703327-f3fc-4327-8fad-17f8fda6fd1f.png"
    private const val IMG_ACANTHITE_MUSEUM_2 = "$Q/3f9b547e-01d4-48a8-88d5-20d69387bde8.png"
    // Batch 79: feldspar-peristerite, andesine, bytownite, anorthite, jade-omphacite
    private const val IMG_FELDSPAR_PERISTERITE_WILD = "$Q/7e0c1b3d-f3d2-436e-9db7-940068b6b596.png"
    private const val IMG_FELDSPAR_PERISTERITE_MUSEUM = "$Q/f1e199d7-0f08-46ba-b7fd-2be88c2ae1a0.png"
    private const val IMG_FELDSPAR_ANDESINE_WILD = "$Q/acd96989-40fc-418e-9a52-2a15e5e42547.png"
    private const val IMG_FELDSPAR_ANDESINE_MUSEUM = "$Q/c4ce593c-f37e-4ff9-b213-967cb2bb590b.png"
    private const val IMG_FELDSPAR_ANDESINE_CAB = "$Q/fd741d46-0700-4c63-a4bc-8e7751a4769b.png"
    private const val IMG_FELDSPAR_BYTOWNITE_WILD = "$Q/ea8b58c6-455e-41cc-b0e8-3011401fc02e.png"
    private const val IMG_FELDSPAR_BYTOWNITE_MUSEUM = "$Q/c57b290e-bc90-421c-a8b0-24d3727e489a.png"
    private const val IMG_FELDSPAR_ANORTHITE_WILD = "$Q/28321403-2162-4dbf-b0cb-7487acceced4.png"
    private const val IMG_FELDSPAR_ANORTHITE_MUSEUM = "$Q/e1af2ed8-a5bc-4abb-90ad-207477db61f1.png"
    private const val IMG_JADE_OMPHACITE_WILD = "$Q/99277f68-9e83-474a-a728-fd590a6a72e9.png"
    // Batch 80: igneous-monzonite, sedimentary-dolostone, chalk, oolitic-limestone, coquina
    private const val IMG_IGNEOUS_MONZONITE_WILD = "$Q/e137b2f3-e66a-4cad-a19a-bf6246ecff6a.png"
    private const val IMG_IGNEOUS_MONZONITE_MUSEUM = "$Q/777ec38e-bb65-4f6f-88b1-2058837da4a7.png"
    private const val IMG_DOLOSTONE_WILD = "$Q/bed1d2ec-110e-4237-bada-953b873753bc.png"
    private const val IMG_DOLOSTONE_MUSEUM = "$Q/ebfd3069-fcc5-4e6a-930e-17388a4bbde9.png"
    private const val IMG_CHALK_WILD = "$Q/fa50319c-92ee-4d70-bb32-cd0c0d14c165.png"
    private const val IMG_CHALK_MUSEUM = "$Q/01d5ab54-1de2-46d8-833f-3dbfd8c18e34.png"
    private const val IMG_OOLITIC_LIMESTONE_WILD = "$Q/c3cf33de-c22c-49da-bc53-e510aa5024bc.png"
    private const val IMG_OOLITIC_LIMESTONE_MUSEUM = "$Q/7f94640a-4dc2-44f4-930e-44ee89e2f4b8.png"
    private const val IMG_COQUINA_SED_WILD = "$Q/9c9dc5be-fbfc-4589-a527-7a3bd0c8d99d.png"
    private const val IMG_COQUINA_SED_MUSEUM = "$Q/53b1c2e0-900f-40da-943e-67ef66ede398.png"
    // Batch 81: sedimentary-arkose, chert-nodule, sulfide-enargite, silicate-zircon, igneous-felsite
    private const val IMG_ARKOSE_SED_WILD = "$Q/bd89dba8-da88-407b-8591-bcc20a5f9238.png"
    private const val IMG_ARKOSE_SED_MUSEUM = "$Q/8908a0b3-4e1d-45f4-8348-b8450cfe8d24.png"
    private const val IMG_CHERT_NODULE_WILD = "$Q/abd8c13c-abee-4e78-b91f-7714dacb76da.png"
    private const val IMG_ENARGITE_SULF_WILD = "$Q/85e10004-4023-4965-a8e0-715a9475f050.png"
    private const val IMG_ENARGITE_SULF_MUSEUM = "$Q/5fbb2c04-244c-4d36-bdb8-7bfe70781d2e.png"
    private const val IMG_ZIRCON_SIL_WILD = "$Q/3b5484a0-e5e4-4aec-9bf9-96cdd8ed427d.png"
    private const val IMG_ZIRCON_SIL_MUSEUM = "$Q/11c441bc-8f24-4702-b1b7-229571b0bb3c.png"
    private const val IMG_FELSITE_WILD = "$Q/4845e73e-f5cb-41bc-b4d8-7511cc1ac57d.png"
    private const val IMG_FELSITE_MUSEUM = "$Q/57bff8df-c7b0-49f9-99bb-9056e0126734.png"
    // Batch 82: igneous-pitchstone, sedimentary-marl, oil-shale, lignite, banded-iron
    private const val IMG_PITCHSTONE_WILD = "$Q/b42dd025-186b-4ffa-a308-c8af8b72c4a0.png"
    private const val IMG_PITCHSTONE_MUSEUM = "$Q/966a89de-ad49-46e8-8583-184418131f97.png"
    private const val IMG_MARL_WILD = "$Q/aa0bb3e2-30e1-42b3-ab61-d6cedb3eb9c3.png"
    private const val IMG_MARL_MUSEUM = "$Q/eda7ecc1-bc7b-422f-b782-675d4851061d.png"
    private const val IMG_OIL_SHALE_SED_WILD = "$Q/61ae5387-1695-431b-8094-8021182cbccb.png"
    private const val IMG_OIL_SHALE_SED_MUSEUM = "$Q/8c071c9a-2a23-4374-98f8-21be7d5218d9.png"
    private const val IMG_LIGNITE_SED_WILD = "$Q/ebb5ea13-fb9d-4bdc-9e8d-27b7a7d57a2c.png"
    private const val IMG_LIGNITE_SED_MUSEUM = "$Q/989a33ad-3972-43bd-a94e-f6639ff0033a.png"
    private const val IMG_BANDED_IRON_SED_WILD = "$Q/c5d95070-e622-41ea-9bcf-d47c1b1c378f.png"
    private const val IMG_BANDED_IRON_SED_MUSEUM = "$Q/51904b38-785f-4875-b89b-542d82f49927.png"
    // Batch 83: silicate-nepheline, sedimentary-evaporite, silicate-lepidolite, sulfide-cinnabar, sedimentary-travertine
    private const val IMG_NEPHELINE_SIL_WILD = "$Q/3e4f53c6-3126-4afa-bfd9-a4744b9e10a3.png"
    private const val IMG_NEPHELINE_SIL_MUSEUM = "$Q/905e2512-9ba9-4f1f-a882-d03d1e90ea6b.png"
    private const val IMG_EVAPORITE_WILD = "$Q/19ff0365-13d5-43bb-8867-1469238ec998.png"
    private const val IMG_EVAPORITE_MUSEUM = "$Q/01643e8e-d6b8-4aa3-9b50-da506c0fe3a5.png"
    private const val IMG_LEPIDOLITE_SIL_WILD = "$Q/98339095-858d-4320-b0a7-e7efe7ca4f1b.png"
    private const val IMG_LEPIDOLITE_SIL_MUSEUM = "$Q/35774050-74fe-4c3f-9811-8bea3c4ead9f.png"
    private const val IMG_CINNABAR_SULF_WILD = "$Q/2ec45fe3-0a75-4c6a-8713-d41a0b06ba57.png"
    private const val IMG_CINNABAR_SULF_MUSEUM = "$Q/81cdec66-39f0-4de4-bf75-9b22239aa991.png"
    private const val IMG_TRAVERTINE_SED_WILD = "$Q/4b9a51c6-5bbd-4cfb-92c3-e4c5a6ca0b13.png"
    private const val IMG_TRAVERTINE_SED_MUSEUM = "$Q/80d1e5dd-c795-4fe5-aedb-a2477f676bff.png"
    // Batch 83b: silicate-tremolite, sedimentary-siltstone
    private const val IMG_TREMOLITE_SIL_WILD = "$Q/b4e269eb-8d44-4cbb-8ad0-aed9e6cd4e14.png"
    private const val IMG_TREMOLITE_SIL_MUSEUM = "$Q/644ac98b-172a-45d2-ad2f-a59d05af9093.png"
    private const val IMG_SILTSTONE_SED_WILD = "$Q/63484bdd-62ca-4c16-837f-9a037ab731cb.png"
    private const val IMG_SILTSTONE_SED_MUSEUM = "$Q/0471fed2-92ae-4c30-b0d9-7975a6f2f0eb.png"
    // ── Missing specimen images — generated 2026-07-03 ──
    // Chiastolite AI-generated replicas (cross-stone variety)
    private const val IMG_CHIASTOLITE = "$Q/b30a9cca-98f7-418d-a550-a7aef377896c.png"
    private const val IMG_CHIASTOLITE_WILD = "$Q/daf0e907-b377-4dc3-876a-6ca811a10286.png"
    private const val IMG_CHIASTOLITE_MUSEUM = "$Q/350dd46d-0402-42fb-9534-87d6076d433f.png"
    // Andalusite AI-generated replicas (gem-quality pleochroic crystal)
    private const val IMG_ANDALUSITE_GEM_REPLICA = "$Q/65f07502-7861-4131-9177-d854511cc269.png"
    // Chiastolite AI-generated replica (cross-stone polished slice)
    private const val IMG_CHIASTOLITE_CROSS_REPLICA = "$Q/9cb43531-7192-4893-9ed2-4d55e9dabe02.png"
    // Real Wikimedia Commons photos — andalusite (gem-quality pleochroic crystals)
    private const val IMG_COMMONS_ANDALUSITE_MRZ335A = "https://upload.wikimedia.org/wikipedia/commons/9/9e/Andalusite-mrz335a.jpg"
    private const val IMG_COMMONS_ANDALUSITE_RH1_24B = "https://upload.wikimedia.org/wikipedia/commons/1/15/Andalusite-rh1-24b.jpg"
    private const val IMG_COMMONS_ANDALUSITE_LUCOMAGNO = "https://upload.wikimedia.org/wikipedia/commons/6/62/Andalusite_-_Lucomagno%2C_Svizzera_01.jpg"
    // Real Wikimedia Commons photos — chiastolite (cross-stone variety)
    private const val IMG_COMMONS_CHIASTOLITE = "https://upload.wikimedia.org/wikipedia/commons/f/fa/Chiastolite.JPG"
    private const val IMG_COMMONS_CHIASTOLITE_2581 = "https://upload.wikimedia.org/wikipedia/commons/1/18/Chiastolite_2581.jpg"
    private const val IMG_COMMONS_CHIASTOLITE_CHINE = "https://upload.wikimedia.org/wikipedia/commons/e/ed/Chiastolite_%28Chine%29.jpg"
    private const val IMG_COMMONS_CHIASTOLITE_GEODIL = "https://upload.wikimedia.org/wikipedia/commons/f/f0/Andalusite_(chiastolite)_(GeoDIL_number_-_388).jpg"
    private const val IMG_CLINOHUMITE = "$Q/d546e9e8-2f89-4984-b977-ff5c5db51e05.png"
    private const val IMG_EOSPHORITE = "$Q/7776b9f0-e049-4f18-bcb3-9aa59fc44efb.png"
    private const val IMG_VERMICULITE = "$Q/42f8a1bc-368a-4cdf-8107-3843eb5db565.png"
    private const val IMG_FUCHSITE = "$Q/0f9e7872-cc5f-452d-8768-9f68a21d05dc.png"
    private const val IMG_NATIVE_PLATINUM = "$Q/96b39a6f-b076-484d-a420-ec3ed7311590.png"
    private const val IMG_PHARMACOSIDERITE = "$Q/589139c3-b157-41e4-aaa7-994a0bfe13d7.png"
    private const val IMG_PHOSPHOPHYLLITE = "$Q/f9e4955b-1b1c-44c8-bcfa-cafcd21bfc33.png"
    private const val IMG_HAWK_EYE = "$Q/2b19e150-d740-4675-b250-2a0a2698a0e6.png"
    private const val IMG_MILKY_QUARTZ = "$Q/74ce8a02-42a5-4cff-98c0-76bfd226d2b9.png"
    private const val IMG_ROCK_CRYSTAL = "$Q/95afb1d3-2a42-49ff-9600-8aaccbc887d4.png"
    private const val IMG_TIGER_EYE = "$Q/93f84d55-fac7-4df7-bf21-e638f6142c32.png"
    private const val IMG_SCORODITE = "$Q/26319be7-0415-49ff-b6fc-ef6146676db8.png"
    private const val IMG_COBRA_JASPER = "$Q/b890f444-7043-46b4-9169-b61d791812ab.png"
    private const val IMG_TRIPHANE = "$Q/2795633b-cf5f-44c7-ac41-e4ce2949acdc.png"
    private const val IMG_THORITE = "$Q/445229c4-3ba7-4ade-9173-2071a63954cc.png"
    private const val IMG_BREWSTERITE = "$Q/aef95e7c-0387-46f1-a308-7fb3683d10c8.png"
    private const val IMG_THULITE = "$Q/55114dcf-61f9-4e97-ac45-a2d4fcad9519.png"

    // Batch 84: museum images for remaining single-image and no-image specimens
    private const val IMG_HERRERASAURUS_MUSEUM = "$Q/4b1a7d71-072d-4e81-95f7-c271be5e3451.png"
    private const val IMG_METEORITE_HUNTING = "$Q/06428f02-9e5f-4953-9d47-45fa156b24cd.png"
    private const val IMG_OVIRAPTOR_MUSEUM = "$Q/33a4f65b-a0f0-4df2-9841-650b584b09d2.png"
    private const val IMG_CLINOHUMITE_MUSEUM = "$Q/9247cea4-41c6-4f98-83ed-cc93db23f184.png"
    private const val IMG_COVELLITE_MUSEUM = "$Q/1ce9f65e-bb27-49a8-aa5a-1c4a9af989ec.png"
    private const val IMG_EOSPHORITE_MUSEUM = "$Q/67466174-9ee4-4f99-a3b8-82fc80ac238d.png"
    private const val IMG_VERMICULITE_MUSEUM = "$Q/2314b8a0-3ac8-435b-a49e-85ff5592bedd.png"
    private const val IMG_JASPER_RED_CREEK_MUSEUM = "$Q/340926b5-fb49-44cb-b9fc-9e99da6e3ec5.png"
    private const val IMG_JASPER_STONE_CANYON_MUSEUM = "$Q/4ecdca79-22fd-4306-9856-d14d09732127.png"
    private const val IMG_FUCHSITE_MUSEUM = "$Q/eac78486-b1f4-4ab3-bf33-e0dccfb3b835.png"
    private const val IMG_NATIVE_PLATINUM_MUSEUM = "$Q/15857036-c5f2-4fd3-bcac-65bd59118bcb.png"
    private const val IMG_PECTOLITE_GREEN_MUSEUM = "$Q/204c7f1e-8fdc-4d8b-ba7e-fae616e70494.png"
    private const val IMG_PECTOLITE_MASSIVE_MUSEUM = "$Q/866febfe-6c74-4369-ac58-7344b1b9b27f.png"
    private const val IMG_PECTOLITE_SCHIZOLITE_MUSEUM = "$Q/376b9681-9fd1-4634-aa5e-2623c3a3f9f6.png"
    private const val IMG_PHARMACOSIDERITE_MUSEUM = "$Q/1d549833-ee49-4136-bc7b-2a3a6195cb80.png"
    private const val IMG_PHOSPHOPHYLLITE_MUSEUM = "$Q/f68501b6-1204-4435-b5d7-1b7d759d3f70.png"
    private const val IMG_HAWK_EYE_MUSEUM = "$Q/58b748f5-1a61-4d95-a74a-b94b7aff25da.png"
    private const val IMG_MILKY_QUARTZ_MUSEUM = "$Q/ad52b711-ed8c-4ffc-912f-d7afff0ac590.png"
    private const val IMG_ROCK_CRYSTAL_MUSEUM = "$Q/c545c71d-2549-4b08-938e-5e57b948e406.png"
    private const val IMG_TIGER_EYE_MUSEUM = "$Q/d2556017-b932-44e2-97b3-1b779852db7a.png"
    // Batch 85: green halite, tube agate polished, fossil soup beach
    private const val IMG_GREEN_HALITE = "$Q/c04a0e13-cf29-4649-bc6d-184b8e378bd2.png"
    private const val IMG_AGATE_TUBE_POLISHED = "$Q/655acf95-2584-449f-ad27-78a700c62d7f.png"
    private const val IMG_FOSSIL_SOUP_BEACH = "$Q/65387d7d-6c32-44ea-ba21-ea55bebad520.png"
    private const val IMG_SCORODITE_MUSEUM = "$Q/90dcb0f4-82d3-45a2-9dfe-18ea600895b1.png"
    private const val IMG_COBRA_JASPER_MUSEUM = "$Q/0dcb48b5-03e4-45d4-b2f8-20fd4a650c0c.png"
    private const val IMG_TRIPHANE_MUSEUM = "$Q/b2b4f163-c1e6-484b-885d-532f452388f0.png"
    private const val IMG_THORITE_MUSEUM = "$Q/4eef1ffd-f58c-44fe-8b0c-54eb6c4805c1.png"
    private const val IMG_BREWSTERITE_MUSEUM = "$Q/28e27b17-0a75-478b-b3df-02d16d756d4a.png"
    // Batch 86: accurate sardonyx, botswana, dead sea, ankara, amethyst-calcite
    private const val IMG_SARDONYX_SPECIMEN_NEW = "$Q/d2465f83-e155-4dfa-b068-663137ba0bf3.png"
    private const val IMG_SARDONYX_CABOCHON_NEW = "$Q/4eef8e46-b437-4c2e-8973-99848da1e41f.png"
    private const val IMG_BOTSWANA_AGATE_SPECIMEN = "$Q/a4dda749-c2d0-4272-bf92-0eb32d2801d5.png"
    private const val IMG_BOTSWANA_AGATE_WILD_2 = "$Q/339b6676-817b-4cda-bc9b-da9e836565de.png"
    private const val IMG_DEAD_SEA_AGATE = "$Q/863f3d5d-37c6-4287-9a67-a4f68ebcc7d8.png"
    private const val IMG_DEAD_SEA_AGATE_WILD = "$Q/0ced9022-6bf5-4ec2-8804-baa7235328c9.png"
    private const val IMG_AGATE_ANKARA = "$Q/e6014a37-eed6-4dd6-a54c-b701a1288450.png"
    private const val IMG_AGATE_ANKARA_WILD = "$Q/c8e4b80b-88bf-4bb2-840e-cbe0408cd7fb.png"
    private const val IMG_AMETHYST_CALCITE_GEODE = "$Q/2653a54a-5aab-41d2-92b2-8c544f0abf88.png"
    private const val IMG_AMETHYST_CALCITE_CLOSEUP = "$Q/bf1341b4-d9df-4c91-b9a6-18e4c374d510.png"

    // ── Tourmaline expansion (8 varieties, 3 images each) — 2026-07-04 ──
    private const val IMG_TOURMALINE_PARAIBA = "$Q/c8e27c35-1cde-4d99-98bd-9810aba2a961.png"
    private const val IMG_TOURMALINE_PARAIBA_WILD = "$Q/3f793b48-7577-40f6-b270-ab31134a6137.png"
    private const val IMG_TOURMALINE_PARAIBA_MUSEUM = "$Q/f293632d-4de9-45ec-a48b-dfae70f97523.png"
    private const val IMG_TOURMALINE_DRAVITE = "$Q/db6917d5-0ef7-4834-b018-02ac79a19648.png"
    private const val IMG_TOURMALINE_DRAVITE_WILD = "$Q/5859a44b-dd24-4934-a3ea-d6b112e30d48.png"
    private const val IMG_TOURMALINE_DRAVITE_MUSEUM = "$Q/10f8c95b-301b-4c66-9e7b-c3a8eb2c50d6.png"
    private const val IMG_TOURMALINE_LIDDICOATITE = "$Q/afbbbeeb-ffe9-4170-9a1a-76bb7a1d07a1.png"
    private const val IMG_TOURMALINE_LIDDICOATITE_WILD = "$Q/3f292f65-274d-4f99-bc72-da525834cef9.png"
    private const val IMG_TOURMALINE_LIDDICOATITE_MUSEUM = "$Q/f372a470-d627-4a44-95b9-15429523ec17.png"
    private const val IMG_TOURMALINE_CANARY = "$Q/35caae40-3caa-4e10-b7cc-c77204c0ec73.png"
    private const val IMG_TOURMALINE_CANARY_WILD = "$Q/8f64fb7f-da20-40fc-9296-3e7a85c35959.png"
    private const val IMG_TOURMALINE_CANARY_MUSEUM = "$Q/3a41d5fa-5113-442d-bc4c-74d85cacbec8.png"
    private const val IMG_TOURMALINE_CATS_EYE = "$Q/e4baf615-5c77-494d-a2d5-153979be2a91.png"
    private const val IMG_TOURMALINE_CATS_EYE_WILD = "$Q/78b92524-b68c-4f67-b34e-88d951945128.png"
    private const val IMG_TOURMALINE_CATS_EYE_MUSEUM = "$Q/0a5e6b24-e4e7-47ec-880c-22cda3c0e2c3.png"
    private const val IMG_TOURMALINE_BICOLOR = "$Q/724f8bb1-3679-415e-92ee-1be2de9a28bf.png"
    private const val IMG_TOURMALINE_BICOLOR_WILD = "$Q/0071a6cd-911d-4fff-9688-e7d92196a2a8.png"
    private const val IMG_TOURMALINE_BICOLOR_MUSEUM = "$Q/cae8cb79-76bc-4470-bb01-6ab060593b76.png"
    private const val IMG_TOURMALINE_CHROME = "$Q/04ff46e5-7140-44fa-ad85-20e5c1f2bb18.png"
    private const val IMG_TOURMALINE_CHROME_WILD = "$Q/bdd06fea-cf5a-49c0-b0a0-e6e841863dc0.png"
    private const val IMG_TOURMALINE_CHROME_MUSEUM = "$Q/ab21a04d-5ca5-427f-80ba-f4bf8e175756.png"
    private const val IMG_TOURMALINE_ACHROITE = "$Q/41dd8305-64bb-4008-91a9-18f0c8a499c5.png"

    // ── Granite expansion (11 colors/formations, 3 images each) — 2026-07-04 ──
    private const val IMG_GRANITE_BLACK_GALAXY = "$Q/2c2fd945-34f1-4a64-8984-5cf7bf07e3bf.png"
    private const val IMG_GRANITE_BLACK_GALAXY_WILD = "$Q/28f45c71-835e-46e1-8b0f-5283ae4b8d3b.png"
    private const val IMG_GRANITE_BLACK_GALAXY_MUSEUM = "$Q/2fe27761-e58a-4216-898c-7c1f16bb1566.png"
    private const val IMG_GRANITE_BLUE_PEARL = "$Q/9fd5f4eb-c8f5-4cd0-b907-6df136d96c4f.png"
    private const val IMG_GRANITE_BLUE_PEARL_WILD = "$Q/c11b06e1-a3f7-4277-8727-3b9231083650.png"
    private const val IMG_GRANITE_BLUE_PEARL_MUSEUM = "$Q/3998d714-6e0e-4136-a840-f8c3e4ccd3ad.png"
    private const val IMG_GRANITE_BALTIC_BROWN = "$Q/e74c5a4f-754c-48c2-b333-ce21b86a4a07.png"
    private const val IMG_GRANITE_BALTIC_BROWN_WILD = "$Q/ed391334-16e8-4d19-b0e8-a7db7414d16f.png"
    private const val IMG_GRANITE_BALTIC_BROWN_MUSEUM = "$Q/e9f6312b-56a1-460c-92f7-d61874b4f4af.png"
    private const val IMG_GRANITE_KASHMIR_WHITE = "$Q/9728934e-83df-4bc9-aed4-367e86604724.png"
    private const val IMG_GRANITE_KASHMIR_WHITE_WILD = "$Q/6c2f0c0e-ce87-4b54-a41b-f2c7a1fafbff.png"
    private const val IMG_GRANITE_KASHMIR_WHITE_MUSEUM = "$Q/786466e9-53c2-4e9e-8deb-6e023407a2e4.png"
    private const val IMG_GRANITE_TAN_BROWN = "$Q/4bbd9deb-39f6-4628-beda-8cc085ebcbd5.png"
    private const val IMG_GRANITE_TAN_BROWN_WILD = "$Q/7b78efd4-a794-494e-a56e-da6993872299.png"
    private const val IMG_GRANITE_TAN_BROWN_MUSEUM = "$Q/4ba08cee-cb24-487a-a99f-7d858c588daf.png"
    private const val IMG_GRANITE_VERDE_UBATUBA = "$Q/422c5615-eef0-4de3-acdf-2930c516e13a.png"
    private const val IMG_GRANITE_VERDE_UBATUBA_WILD = "$Q/34de2827-3897-4835-b3a6-6f36edb16b67.png"
    private const val IMG_GRANITE_VERDE_UBATUBA_MUSEUM = "$Q/ade3d377-26ac-442e-a70b-f256e0db5c7b.png"
    private const val IMG_GRANITE_WHITE_MOUNT_AIRY = "$Q/3108ffa1-0b0d-4a36-bc95-6d00ab611a69.png"
    private const val IMG_GRANITE_WHITE_MOUNT_AIRY_WILD = "$Q/0fdea59a-16af-4754-826a-04801f7ab226.png"
    private const val IMG_GRANITE_WHITE_MOUNT_AIRY_MUSEUM = "$Q/65f82249-b7b7-4993-a869-3a3418fe9bf5.png"
    private const val IMG_GRANITE_A_TYPE = "$Q/dcb0db6e-323a-4681-b5ff-f3f78b612b63.png"
    private const val IMG_GRANITE_A_TYPE_WILD = "$Q/f2ae84ad-46eb-436d-af79-91901801c975.png"
    private const val IMG_GRANITE_A_TYPE_MUSEUM = "$Q/0c460e6d-061c-4d5e-96ae-fd8bc8988ef2.png"
    private const val IMG_GRANITE_S_TYPE = "$Q/3013dbb1-1b14-4bce-bc1a-840198f39ce8.png"
    private const val IMG_GRANITE_S_TYPE_WILD = "$Q/0525dafb-e209-47f3-bb26-28960afb9536.png"
    private const val IMG_GRANITE_S_TYPE_MUSEUM = "$Q/cf2ef1b7-c54e-4a0c-8e3d-c934ab168866.png"
    private const val IMG_GRANITE_I_TYPE = "$Q/90e71a39-0ccc-40e7-bdad-fa1d99f38f9d.png"
    private const val IMG_GRANITE_I_TYPE_WILD = "$Q/f3604b65-cc95-4d27-bf96-1bc6b219aeb1.png"
    private const val IMG_GRANITE_I_TYPE_MUSEUM = "$Q/32ae8d1e-66f1-4e52-acc0-4ac5a30c14c5.png"
    private const val IMG_GRANITE_M_TYPE = "$Q/d99165e8-1f26-431f-b54c-24a6df17ed25.png"
    private const val IMG_GRANITE_M_TYPE_WILD = "$Q/2a61d105-e70f-4c87-bdf7-5b2a9d8f4780.png"
    private const val IMG_GRANITE_M_TYPE_MUSEUM = "$Q/fbbc2a86-c26e-479a-83a8-fd3cb02cb1b2.png"

    // ── Trinitite (atomic blast glass) — 2026-07-04 ──
    private const val IMG_TRINITITE = "$Q/b805c171-4d53-4ba5-a7ae-b5a48da153cb.png"
    private const val IMG_TRINITITE_WILD = "$Q/97b7a3a6-bad9-4480-b77c-963e5459a385.png"
    private const val IMG_TRINITITE_MUSEUM = "$Q/5e77eb03-3836-48bb-a97c-3fba4c5ae23e.png"

    // ── Opal expansion (hydrophane, chocolate, pink, common) — 2026-07-04 ──
    private const val IMG_OPAL_HYDROPHANE = "$Q/4814b918-d111-409e-99f9-fea4bbb9c814.png"
    private const val IMG_OPAL_HYDROPHANE_WILD = "$Q/f6e50e23-16b2-4b09-acbe-a2b6bd95246d.png"
    private const val IMG_OPAL_HYDROPHANE_MUSEUM = "$Q/49232bb6-e928-4066-b4e3-be35f4efdd36.png"
    private const val IMG_OPAL_CHOCOLATE = "$Q/91e0e63a-e9d3-47f6-994d-e5c0361da10e.png"
    private const val IMG_OPAL_CHOCOLATE_WILD = "$Q/2078a819-1c33-49d5-9889-6b9081bc144b.png"
    private const val IMG_OPAL_CHOCOLATE_MUSEUM = "$Q/c25b0fd8-f5a9-4dc5-8264-b230637da1b5.png"
    private const val IMG_OPAL_PINK = "$Q/aba6c503-0824-48e8-a333-c8882535ce3e.png"
    private const val IMG_OPAL_PINK_WILD = "$Q/26249359-f4db-4bfb-b11b-adbc87f1b523.png"
    private const val IMG_OPAL_PINK_MUSEUM = "$Q/117131f9-95ad-431f-a9c9-2965349b5901.png"
    private const val IMG_OPAL_COMMON = "$Q/c60609fb-c1dc-4c33-ada9-b73901df4e87.png"
    private const val IMG_OPAL_COMMON_WILD = "$Q/f2893509-cd63-43a2-b550-7a1756b654bd.png"
    private const val IMG_OPAL_COMMON_MUSEUM = "$Q/1b12fd8c-c269-40b4-8d00-d321e1847f35.png"

    // ── Accuracy corrections: specimens that were sharing incorrect images with unrelated entries ──
    private const val IMG_CONGLOMERATE_SANDSTONE = "$Q/a9646be7-fba8-4676-80f5-fdc1c5449b09.png"
    private const val IMG_TREX_TOOTH = "$Q/5492f6ba-9a66-4d77-bb10-66aeeac3e322.png"
    private const val IMG_ZEOLITE = "$Q/76982233-d62a-44a8-a335-a9466539daad.png"

    // ── NEW: Zeolite variety photo set (12 reference-based replicas) ──
    private const val IMG_ZEOLITE_CLEAR_GLASSY_01 = "$Q/9df0da43-d717-4286-a09a-02763b0bc010.png"
    private const val IMG_ZEOLITE_WHITE_NEEDLES_02 = "$Q/62be456c-39f0-4fa6-8058-f6e582a857a6.png"
    private const val IMG_ZEOLITE_GREEN_APOPHYLLITE_03 = "$Q/f0d243a0-b48f-48f6-aef6-275b691ebc11.png"
    private const val IMG_ZEOLITE_YELLOW_BOTRYOIDAL_04 = "$Q/5e61e5e2-9753-43bf-b254-3b07b65b64b4.png"
    private const val IMG_ZEOLITE_WHITE_FLAT_05 = "$Q/9f5c3c9b-5973-48c8-b44a-e5cd3da980e0.png"
    private const val IMG_ZEOLITE_BROWN_MATRIX_06 = "$Q/7faf1686-5a51-421e-a86f-80b749d37526.png"
    private const val IMG_ZEOLITE_PEACH_STILBITE_07 = "$Q/bff7b13d-369c-43b5-bcc9-0307b6d54f90.png"
    private const val IMG_ZEOLITE_GREEN_FAN_08 = "$Q/d82e41fe-ec7f-4ab3-bb79-4863add16b26.png"
    private const val IMG_ZEOLITE_BEIGE_DRUSY_09 = "$Q/3dafb07e-70d5-405d-a1c5-4eebb34212b2.png"
    private const val IMG_ZEOLITE_PURPLE_LAVENDER_10 = "$Q/991e9d11-9fbd-4fe0-8f1c-0d9042ee2f0f.png"
    private const val IMG_ZEOLITE_WHITE_FROSTY_11 = "$Q/dd324dfa-b321-4534-9ef0-a107b25e4eb3.png"
    private const val IMG_ZEOLITE_PALE_BLUE_GREEN_12 = "$Q/17ec7c2f-95a1-4795-9e68-d8cd66a74a2f.png"

    private const val IMG_WAVELLITE_HALF_SPHERE = "https://r2-pub.rork.com/web-fetch-images/85d5abe2c78dac8088ec70469336701da305560afe06aa90e50e62142b41e181.jpeg"
    private const val IMG_WAVELLITE_COLORS = "https://r2-pub.rork.com/web-fetch-images/bd7f4b121cf1cfa1a4aeb068bf4b8bd6d47f1373d45362572f8120e85578cac0.jpeg"
    private const val IMG_FOSSIL_WHALE_VERTEBRA = "$Q/98e0d6ec-4bcb-4465-a6a9-24f81146ba2a.png"
    private const val IMG_TURGITE_IRIDESCENT = "$Q/e8751e83-d0ee-4f3f-915e-ece0d105a285.png"
    private const val IMG_PITCHSTONE_NEW = "$Q/45ad9336-8adc-4657-a9f6-f9aec316bfff.png"
    private const val IMG_URBANITE_NEW = "$Q/c590fa17-90a8-4469-929a-301af30138e5.png"
    private const val IMG_URBANITE_WILD_DEDICATED = "$Q/8a7b6db5-9227-4f0e-b0bd-269f33f12dfc.png"
    private const val IMG_DIABASE_NEW = "$Q/ea44469a-51df-44e3-9d9f-1d5c1788ab53.png"
    private const val IMG_GRANODIORITE_NEW = "$Q/2e6ff4e9-bc7d-4997-8dd7-24540e5c031c.png"
    private const val IMG_PORCELLANITE_NEW = "$Q/5e4dc21e-e578-446f-97ec-ebb55206d7a8.png"
    private const val IMG_IRONSTONE_NEW = "$Q/62e5e3e5-c97d-4b9d-a517-b390f7d87d01.png"
    private const val IMG_BARITE_DESERT_ROSE_NEW = "$Q/4d67c67e-6472-497f-9d8b-e117092710ba.png"
    private const val IMG_GYPSUM_DESERT_ROSE_NEW = "$Q/d7f3bcbe-f273-4e12-b326-68ebddeeaae5.png"
    private const val IMG_APLITE = "$Q/efcebb4d-14fb-49b1-be6a-6675595b648e.png"
    private const val IMG_PORPHYRY = "$Q/4fcc08d5-2180-405f-9f31-1917aa6a191a.png"
    private const val IMG_MARL = "$Q/de2b0ca5-5d08-4992-8c77-96fbac76e402.png"

    // ── Database overhaul: new specimen images ──
    private const val IMG_HYALITE_OPAL_NAT = "$Q/58ea8aa6-929c-4223-8ea9-b91b060158e4.png"
    private const val IMG_HYALITE_OPAL_WILD = "$Q/82f821dd-df76-4a32-a17f-2fa65ce64ae6.png"
    private const val IMG_HYALITE_OPAL_LW = "$Q/b9a89d5f-8420-4797-85d9-5b975dfa2756.png"
    private const val IMG_BASALT_MORB_WILD = "$Q/a1a8ef23-031d-4444-8c7e-552ae669a3a1.png"
    private const val IMG_BASALT_MORB_MUSEUM = "$Q/5e91326c-9407-4989-9806-620bfe423d57.png"
    private const val IMG_BLUE_AVENTURINE = "$Q/09099ecc-b02a-491e-a8c7-80ec02562f6e.png"
    private const val IMG_BLUE_AVENTURINE_WILD = "$Q/d31f6d6a-7bc4-4b17-8fd3-b5caa78f109a.png"
    private const val IMG_BLUE_AVENTURINE_MUSEUM = "$Q/eb3cdb35-d6a0-46d0-836f-edffce405b7f.png"
    private const val IMG_BLUE_AVENTURINE_CABOCHON = "$Q/9e9d58e9-b5f8-4e90-806b-9cce18db6942.png"
    private const val IMG_BIF_OUTCROP = "$Q/4671b355-ad09-4f73-bb45-7a92d78598eb.png"
    private const val IMG_BIF_HAND_SAMPLE = "$Q/de0de5bb-e0b0-47b4-a73b-b4dea31459d7.png"
    private const val IMG_BIF_MUSEUM_SLAB = "$Q/ee677dfa-bcc4-4039-952f-d9afce1f00fb.png"
    private const val IMG_TREX_TOOTH_FOSSIL_NEW = "$Q/de0d7701-15ef-44a0-a946-ac4224b1dcbe.png"
    private const val IMG_PETRIFIED_PALM_WILD = "$Q/48be2851-82a8-4a9d-bc2d-a96679cddeaa.png"
    private const val IMG_PETRIFIED_PALM_MUSEUM = "$Q/3e7da9b0-5a0a-4f9b-931b-575d528674e9.png"
    private const val IMG_PETRIFIED_BADLANDS_WILD = "$Q/69794090-9c75-4eec-ab95-1251ed222036.png"
    private const val IMG_PETRIFIED_BADLANDS_MUSEUM = "$Q/b1c6673b-2b87-4be7-84d1-d61ef70b7e10.png"
    private const val IMG_PETRIFIED_WOOD_GENERIC_DESERT = "$Q/150eb776-5855-4315-bb84-397d779f7b7f.png"
    private const val IMG_SALTWATER_PEARL = "$Q/5a568372-8357-4ba1-86cd-88dd448bdd20.png"
    private const val IMG_LIMONITE_STANDALONE = "$Q/d1edd9b5-0c6c-478f-8a9b-f5b9ce4be893.png"
    private const val IMG_AMMONITE_IRIDESCENT_NEW = "$Q/34adab23-e535-4b9e-a6ab-4ca70ff6361a.png"
    private const val IMG_OPAL_LIGHTNING_RIDGE = "$Q/fae1b94f-534a-48b0-8788-8fdc06746521.png"
    private const val IMG_TRAPICHE_AMETHYST = "$Q/a2f34207-6b4d-4443-86cc-f31d861e7be6.png"

    // ── Specimen corrections: new dedicated images for duplicated/confused entries ──
    private const val IMG_SERPENTINE_LIZARDITE = "$Q/dc076cb5-d5f7-4a5b-ac45-1803f85c3068.png"
    private const val IMG_LIMESTONE_MAIN_NEW = "$Q/5fec1d3b-b4bb-4115-b55b-3f4dfd4eb307.png"
    private const val IMG_MOZARKITE_ROUGH = "$Q/f5b4b847-779b-439e-94cf-029a32f3d842.png"
    private const val IMG_MOZARKITE_WILD = "$Q/9a7eaad6-e0b1-4551-853e-cc79667072cd.png"
    private const val IMG_MOZARKITE_MUSEUM = "$Q/f60f1e09-baaf-4a1b-ae50-10b1c88a4040.png"
    private const val IMG_MOZARKITE_CABOCHON = "$Q/ff0f8254-4e79-4b6c-9f70-a5c34a8397e6.png"
    private const val IMG_HEMATITE_ROUGH = "$Q/d8819704-2199-4d4e-9dc6-9001f35cbccc.png"
    private const val IMG_HEMATITE_WILD_NEW = "$Q/f7e587da-21ff-49a2-8347-c75544c024f6.png"
    private const val IMG_HEMATITE_MUSEUM_NEW = "$Q/fe21b659-61f4-4c55-92f9-39e20e9d63e2.png"
    private const val IMG_HEMATITE_BOTRYOIDAL_ROUGH = "$Q/46e37d11-a9dc-4fc1-badf-b678d3bd5329.png"
    private const val IMG_HEMATITE_SPECULAR_ROUGH = "$Q/1366a028-5c09-40f4-a538-28c61c67eb94.png"
    private const val IMG_HEMATITE_SPECULAR_WILD = "$Q/c5ab391b-ee44-42e8-9fad-981934e66fd6.png"
    private const val IMG_HEMATITE_SPECULAR_MUSEUM = "$Q/c0142bf6-9103-4894-8907-7a54f5e8e49a.png"
    private const val IMG_IRONSTONE_ROUGH = "$Q/5e0cf667-939f-4070-9b56-625a007dc629.png"
    private const val IMG_IRONSTONE_WILD = "$Q/68d7089e-3e15-45e2-8b53-674a9c52c48c.png"
    private const val IMG_IRONSTONE_MUSEUM = "$Q/8ffec561-a06e-4d33-89af-f4abf03d013e.png"
    private const val IMG_HAWK_EYE_ROUGH = "$Q/338840e4-3032-41d3-a288-10896d30c238.png"
    private const val IMG_HAWK_EYE_WILD = "$Q/7d1c35e7-25ee-4506-a347-060a1c438136.png"
    private const val IMG_HAWK_EYE_MUSEUM_NEW = "$Q/906a36bc-228a-4e25-97db-c95ec60a902d.png"
    private const val IMG_CHERT_NODULE = "$Q/86a46e6b-1ea1-4988-b7ab-fc1f10e6571e.png"
    private const val IMG_CHERT_NODULE_WILD_NEW = "$Q/b55628f5-1c58-4176-87b0-0b8d576c9901.png"
    private const val IMG_CHERT_NODULE_MUSEUM_NEW = "$Q/a16e36f0-6acb-468a-95eb-61e5b98769cf.png"
    private const val IMG_TEKTITE_ROUGH = "$Q/69ec2136-2ddb-42c7-b740-5a90b9cd7a51.png"
    private const val IMG_TEKTITE_WILD_NEW = "$Q/6877f1d1-5aee-4257-a373-53f9531f1033.png"
    private const val IMG_TEKTITE_MUSEUM_NEW = "$Q/b86d6d0e-0894-4bc6-ae45-9643800774e5.png"
    private const val IMG_TEKTITE_TAGAMITE_ROUGH = "$Q/106eece3-30a1-4476-86fb-4c5776dc3f1f.png"
    private const val IMG_TEKTITE_TAGAMITE_WILD = "$Q/86c42a51-a545-4640-b995-62e0da05cdd6.png"
    private const val IMG_TEKTITE_TAGAMITE_MUSEUM = "$Q/fee51a13-3b37-412f-8528-83004241cd15.png"

    // ── Database overhaul batch 2: garnet, goethite, datolite, peristerite, dolomite, dolostone ──
    private const val IMG_ANDRADITE_NEW = "$Q/21e4d87e-2b99-4fd6-97c5-20bc3bf28a27.png"
    private const val IMG_ANDRADITE_ROUGH = "$Q/bdbf4a29-72ff-4c4d-81e0-1622b1bfe65d.png"
    private const val IMG_ANDRADITE_WILD = "$Q/ff86efc6-7c5a-4aea-b583-eaffaf7f9174.png"
    private const val IMG_ANDRADITE_MUSEUM = "$Q/cb476450-c746-4bcd-ae89-68c11ac8305c.png"
    private const val IMG_PYROPE_ROUGH = "$Q/fd3b6022-5da8-4dfa-843a-7f0524728e34.png"
    private const val IMG_PYROPE_WILD = "$Q/4b79c055-5586-4551-b449-ae0e6f017484.png"
    private const val IMG_PYROPE_MUSEUM = "$Q/b8629ed7-5844-4142-9ddf-77a4ab611a5b.png"
    private const val IMG_TSAVORITE_NEW = "$Q/4d3ff41b-db56-481e-bf03-51f77ee3d66c.png"
    private const val IMG_TSAVORITE_WILD = "$Q/b203d1bb-e316-4381-97d8-a9bdbde791d7.png"
    private const val IMG_TSAVORITE_MUSEUM = "$Q/d83b5152-dcc3-44a2-952b-ce671a7b35af.png"
    private const val IMG_IRIDESCENT_GOETHITE_NEW = "$Q/06d48f1c-71d4-4ed3-bace-479206496d20.png"
    private const val IMG_DATOLITE_KEWEENAW = "$Q/e483ce5b-266c-46ac-8ee3-cf133e6d3d91.png"
    private const val IMG_DATOLITE_WILD_NEW = "$Q/72fe6871-e93c-44e9-9ee3-5212d8bddac1.png"
    private const val IMG_DATOLITE_MUSEUM_NEW = "$Q/4256ee9b-f69e-4808-afde-d0219481b481.png"
    private const val IMG_STAR_SAPPHIRE_BLUE = "$Q/4f555b3a-e040-46b4-8de2-63c2c5276998.png"
    private const val IMG_STAR_SAPPHIRE_RED = "$Q/afcf1d09-50a7-4355-9925-575d3068bb66.png"
    private const val IMG_CHARLEVOIX_SPECIMEN = "$Q/1f52f47c-7c5d-4736-9c40-054b20b27468.png"
    private const val IMG_CHARLEVOIX_WILD = "$Q/a948f6df-407e-4502-be75-65e0a4fb90d5.png"
    private const val IMG_PERISTERITE_NEW = "$Q/2a35f707-f5a6-47f6-a38c-c7e781773f5f.png"
    private const val IMG_DOLOMITE_CRYSTALS_NEW = "$Q/6bcbba32-8957-4d6d-bae2-b0ddf331595b.png"
    private const val IMG_DOLOSTONE_NEW = "$Q/ec5e9ce9-3e9a-44ec-ac1f-3b76bb899531.png"

    // ── Section 12-14: New specimen images ──
    private const val IMG_PUDDINGSTONE_SPECIMEN = "$Q/05364310-4dd5-4ebd-a184-9c3bc017b0a2.png"
    private const val IMG_PUDDINGSTONE_WILD = "$Q/55f75111-4d51-4b26-be63-3952dd67abb2.png"
    private const val IMG_PUDDINGSTONE_MUSEUM = "$Q/9c184e63-5e58-478e-81cd-50c6c984e476.png"
    private const val IMG_MARTITE_SPECIMEN = "$Q/a0002485-c449-4899-8167-45a86ec8a892.png"
    private const val IMG_MARTITE_WILD = "$Q/6c8cf42e-35b3-4810-92ac-c0f085ad5fe5.png"
    private const val IMG_MARTITE_MUSEUM = "$Q/132dd5c7-0d43-4d82-9e81-ad4be1a98ed2.png"
    private const val IMG_DRUZY_QUARTZ_JASPER = "$Q/55e07f74-b268-4fd8-9f72-a3f443db2b29.png"
    private const val IMG_DRUZY_QUARTZ_CORAL = "$Q/c117c445-3a95-4320-9700-9e36c1e09ba7.png"
    private const val IMG_DRUZY_QUARTZ_MALACHITE = "$Q/b24da5a5-cb41-4e57-b435-0e80a6385ed5.png"
    private const val IMG_DRUZY_QUARTZ_CHALCOPYRITE = "$Q/4d8777c5-306e-420b-90ed-af5debd43b1c.png"
    private const val IMG_THUNDER_BAY_AMETHYST_NAT = "$Q/1ad6aa4f-9b8f-41cc-8aef-0673f89d9523.png"
    private const val IMG_THUNDER_BAY_AMETHYST_WILD = "$Q/4e101eb5-61a7-4b04-b73d-b43e48e2f5f6.png"
    private const val IMG_THUNDER_BAY_AMETHYST_MUSEUM = "$Q/b147fbf2-0555-4ce4-8f8d-b4dc885c412b.png"
    // ── NEW: Vera Cruz Amethyst photos ──
    private const val IMG_VERA_CRUZ_AMETHYST = "$Q/8daed364-e97d-430a-9fa7-146370b56f8f.png"
    private const val IMG_VERA_CRUZ_AMETHYST_WILD = "$Q/8c252421-b024-402a-86e5-0471b8a1ceff.png"
    private const val IMG_VERA_CRUZ_AMETHYST_MUSEUM = "$Q/4a2c6eeb-b847-4cd5-98e8-4f36de849bc0.png"
    // ── NEW: Mexican Amethyst Geode photos ──
    private const val IMG_MEXICAN_AMETHYST_GEODE = "$Q/b9f160cc-9d43-426e-a386-80e05693b51a.png"
    private const val IMG_MEXICAN_AMETHYST_GEODE_WILD = "$Q/a7dff036-f164-456f-b5c0-45b46a5b57db.png"
    private const val IMG_MEXICAN_AMETHYST_GEODE_MUSEUM = "$Q/48742f04-7f27-4a8b-8a6f-26aca5f8a4c4.png"
    private const val IMG_MEXICAN_AMETHYST_GEODE_CABOCHON = "$Q/2e870945-da52-4e4b-8d38-1dbef906b38d.png"
    private const val IMG_FLUOR_BENITOITE_NAT = "$Q/ccc21ca7-7d45-480e-9b14-d5aae88061b7.png"
    private const val IMG_FLUOR_BENITOITE_SW = "$Q/999e0a61-3770-467d-830f-a887bb7c0295.png"
    private const val IMG_FLUOR_TUGTUPITE_NAT = "$Q/077169a7-43d3-46af-b552-ace000c68084.png"
    private const val IMG_FLUOR_TUGTUPITE_SW = "$Q/b132a8ed-5452-4d94-86b7-f6f362ac3172.png"
    private const val IMG_RAINBOW_LATTICE_ROUGH = "$Q/06b746ad-abde-46f5-9d08-b79648c911d2.png"
    private const val IMG_RAINBOW_LATTICE_CABOCHON = "$Q/339edc2a-da7d-420d-8c60-e511547f6a56.png"
    private const val IMG_RAINBOW_LATTICE_CLOSEUP = "$Q/b59966fc-9845-4dc2-9f7c-090a97557dec.png"
    private const val IMG_RAINBOW_LATTICE_WILD = "$Q/965e96bf-b6e2-4bf7-9deb-8802076799dc.png"
    private const val IMG_PSEUDO_CALCITE_ARAGONITE = "$Q/074b4877-8497-49f4-9198-ac9af28f1939.png"
    private const val IMG_PSEUDO_CHALCEDONY_CORAL = "$Q/f96d2cbf-b3a9-4364-a141-94238dab6662.png"
    private const val IMG_PYRITE_QUARTZ_INCLUSION = "$Q/6e54ccf4-291f-497d-bc40-0fab58abbcaf.png"
    private const val IMG_TOURMALINE_QUARTZ_INCLUSION = "$Q/1d30f919-9600-485e-96f7-204786b395ad.png"
    private const val IMG_MOONSTONE_ADULESCENCE = "$Q/9b75ab72-cac9-4d66-924b-7efe28edc6c1.png"
    private const val IMG_OPAL_PLAY_OF_COLOR = "$Q/69f1168d-88bc-4bf2-a491-2ca2e3f5e68a.png"
    private const val IMG_PINK_AMETHYST = "$Q/bab556c2-6d84-42c5-a030-11a0f0ab6152.png"
    private const val IMG_FLUORITE_RAINBOW_NEW = "$Q/9c704758-8981-475b-8695-e9bcddcbf148.png"
    private const val IMG_SELENITE_COLUMN = "$Q/7f3913ff-2bd8-480e-8005-e4dbaea95ee8.png"
    private const val IMG_SELENITE_HOURGLASS = "$Q/679f19b4-ccf5-45c8-9728-673c0b17a39d.png"
    private const val IMG_SELENITE_HOURGLASS_WILD = "$Q/9c510aeb-c032-4a0b-9756-aaf9cf5fec91.png"
    private const val IMG_SELENITE_HOURGLASS_MUSEUM = "$Q/771c6145-32f7-431f-97a0-d261d1b732c5.png"
    private const val IMG_SELENITE_SATIN_SPAR = "$Q/559b7b5c-2d97-44c6-b517-ddba0e299ec9.png"
    private const val IMG_SELENITE_FISHTAIL = "$Q/c398deda-2479-4d77-b50b-d8c1a898809c.png"
    private const val IMG_CHONDRODITE = "$Q/5f3f5380-6e06-4719-9d43-d6d81a67199b.png"
    private const val IMG_EUDIALYTE_NEW = "$Q/d7b65c32-42b2-4e3e-9c86-f099ad2b6b13.png"
    private const val IMG_ASTROPHYLLITE_NEW = "$Q/3557824b-5d4b-48ef-8309-39fadd0da2c1.png"
    private const val IMG_CLINOCHLORE = "$Q/a0ce1d57-5f17-4c9c-8f68-4e383e14e3be.png"
    private const val IMG_GARNIERITE = "$Q/fc9c49e0-1997-49e8-bd18-effa637a78ea.png"
    private const val IMG_EDENITE = "$Q/c3297193-ba02-4e37-8b5a-5b989b550a12.png"
    private const val IMG_LUDWIGITE = "$Q/e8d3e563-ec7b-4dfb-a22c-88ec7e0392c8.png"
    private const val IMG_TILLITE_NEW = "$Q/4d92807d-c6f2-433e-a5bc-b54677a585c5.png"
    private const val IMG_GOWANDA_TILLITE_NEW = "$Q/f6279776-c231-4211-bba4-b2005242d3c5.png"
    private const val IMG_COOPER_PEDY_OPAL_NEW = "$Q/565df26a-c118-4370-80fe-7f101a28ee9a.png"
    private const val IMG_ONYX_NICOTINO_NEW = "$Q/f3d391c3-d48c-4999-8bcf-6e4d10b0938d.png"
    private const val IMG_ONYX_SARD_NEW = "$Q/13fbed17-5248-4b2e-ba3d-320404aed3c2.png"
    private const val IMG_SELENITE_GREAT_SALT_PLAINS = "$Q/43526be5-5c1e-4a87-ba3a-37f9af6b216a.png"

    // ── Accuracy audit corrections: dedicated images for specimens that were sharing wrong images ──
    private const val IMG_ANKERITE_MUSEUM = "$Q/44af095c-5ae3-4ef5-ad78-8a9996396d3f.png"
    private const val IMG_ANKERITE_WILD = "$Q/e8dfc554-74b4-4906-bf7a-385760d38657.png"
    private const val IMG_BASANITE_MUSEUM = "$Q/48c4a9d9-95dd-4584-9291-0427ef9a4f13.png"
    private const val IMG_BASANITE_WILD = "$Q/4e1743ad-5ca9-47c0-808f-e6c600338bf1.png"
    private const val IMG_LAMPROPHYRE_MUSEUM = "$Q/723f5358-efc2-453b-86e6-5a77511d4d97.png"
    private const val IMG_LAMPROPHYRE_WILD = "$Q/3474cb4a-564e-4563-b276-4d4dcae6ccd9.png"
    private const val IMG_MUDSTONE_MUSEUM = "$Q/82eccca1-b468-4ff5-8c52-50b3cae7b887.png"
    private const val IMG_MUDSTONE_WILD = "$Q/6c80c42d-7d6b-40cb-bc49-497119997c23.png"
    private const val IMG_PTEROSAUR_BONE_MUSEUM = "$Q/0652138f-0290-444b-a924-490286fce776.png"
    private const val IMG_PTEROSAUR_BONE_WILD = "$Q/5a1a8a91-b1d0-4471-a1d7-b885b86e1295.png"
    private const val IMG_LUNAR_METEORITE_MUSEUM = "$Q/43f539fe-2c58-4486-8504-8316e4dd00d2.png"
    private const val IMG_LUNAR_METEORITE_WILD = "$Q/30190b37-e4b8-4376-bc46-cb1fe6873bff.png"
    private const val IMG_SARD_MUSEUM = "$Q/f602506c-ff92-4489-9381-fd0a932fc875.png"
    private const val IMG_SARD_CABOCHON = "$Q/dabd5f2a-b254-49eb-bc05-f12c2f513d7d.png"

    // ── Batch 15: concretions, septarian, Michigan lightning stone, heart-shaped rock ──
    private const val IMG_SEPTARIAN_NODULE = "$Q/1e48b80c-5810-4720-ae16-d80ad655dd1b.png"
    private const val IMG_SEPTARIAN_NODULE_CUT_1 = "$Q/d4ca3e17-fc4a-4316-a466-1036ce569b4c.png"
    private const val IMG_SEPTARIAN_NODULE_CUT_2 = "$Q/667b2ec6-af15-426f-9df3-ea644b50269e.png"
    private const val IMG_SEPTARIAN_NODULE_CUT_3 = "$Q/8fd4a039-b726-48f4-b2cb-91fcb9c019a1.png"
    private const val IMG_SEPTARIAN_NODULE_CUT_4 = "$Q/def9154b-d7e8-4df5-aa4a-3744e61c7ac5.png"
    private const val IMG_MICHIGAN_LIGHTNING_STONE = "$Q/5a6cfb62-3e37-4b32-aece-4fb013b97d49.png"
    private const val IMG_MICHIGAN_LIGHTNING_STONE_CUT_1 = "$Q/440b63f6-cc6f-4c2c-ae52-166135a9ef4a.png"
    private const val IMG_MICHIGAN_LIGHTNING_STONE_CUT_2 = "$Q/3655ba3a-4bb6-4796-852c-e37a53bd8039.png"
    private const val IMG_MICHIGAN_LIGHTNING_STONE_CUT_3 = "$Q/29f36ea7-40c7-417d-bdff-36a04dc7b12b.png"
    private const val IMG_MICHIGAN_LIGHTNING_STONE_CUT_4 = "$Q/bc21e11b-f8c4-4da9-94d2-421883cd2625.png"
    private const val IMG_HAGSTONE_PALE_GRAY = "$Q/bac78054-ea7d-4c47-99cf-46fb1bf66f32.png"
    private const val IMG_HAGSTONE_BEIGE_CREAM = "$Q/919ae9eb-6037-486d-bdda-e2259a7afdf3.png"
    private const val IMG_HAGSTONE_PINK_PITTED = "$Q/01b6d7b3-e94b-493a-9406-c9ceca5c4cbf.png"
    private const val IMG_HAGSTONE_DARK_GRAY = "$Q/d16dc9f3-73d2-44ea-b97c-6fd19fbc4a81.png"
    private const val IMG_HAGSTONE_TAN_ROUGH = "$Q/7238981a-614e-434a-8b86-ae77689576b6.png"
    private const val IMG_IRON_CONCRETION = "$Q/4e494b79-27cd-4b17-bc70-7bab63795b88.png"
    private const val IMG_IRON_CONCRETION_CRACKED_NODULE = "$Q/e58a91e8-984c-453c-b118-989bc73e7563.png"
    private const val IMG_IRON_CONCRETION_SPLIT_RINGS = "$Q/622b20c3-d164-44db-b900-d64078cc1ceb.png"
    private const val IMG_IRON_CONCRETION_DARK_BLOCKY = "$Q/6f8d844f-e1c5-445e-8571-b58314f19a9f.png"
    private const val IMG_IRON_CONCRETION_BROWN_BOTRYOIDAL = "$Q/0f1df69c-d9f5-4734-81a0-44fbc044dbaa.png"
    private const val IMG_IRON_CONCRETION_REDDISH_LAYERED = "$Q/4dbef38d-b472-47ca-9161-cd399bbc5b91.png"
    private const val IMG_HEART_SHAPED_ROCK = "$Q/723ce0d3-ed22-4f1f-aad8-8a36a9fd6296.png"
    private const val IMG_FOSSILIZED_MUD_CONCRETION_SPLIT_V2 = "$Q/975911ce-2e2c-4a30-bbda-f2a02387c3ca.png"
    private const val IMG_FOSSILIZED_MUD_CONCRETION_PYRAMID_V2 = "$Q/3d2cf6e0-4c78-4438-a6c2-fbb710577304.png"
    private const val IMG_CONCRETIONS_RAA_SEPTARIAN = "$Q/1442fc02-48e4-4035-b039-292bf3ac5e8e.png"
    private const val IMG_CONCRETIONS_RAA_FAIRY_STONE = "$Q/3e580864-78e1-4ec6-99fd-5de4a6cbd387.png"
    private const val IMG_CONCRETIONS_RAA_IRON = "$Q/1fde096b-9325-4d61-a9e3-3ed14afdf2dd.png"
    private const val IMG_CONCRETIONS_RAA_CANNONBALL = "$Q/66edd338-a638-4bef-a336-2e1355d4ca41.png"
    private const val IMG_CONCRETIONS_RAA_MOQUI = "$Q/857463e5-a2a2-4043-a39f-267b0618c5a6.png"
    private const val IMG_CONCRETIONS_RAA_MUD = "$Q/7cc8847e-9646-4069-b46c-e976f51bd3c6.png"
    private const val IMG_CONCRETIONS_RAA_CLAY_IRONSTONE = "$Q/faf27a71-9a1e-420b-a744-29ca6bfc8b0f.png"
    private const val IMG_CONCRETIONS_RAA_GLACIAL = "$Q/9561f71b-a729-47ec-ab1f-09b015007c09.png"
    private const val IMG_CONCRETIONS_RAA_SANDSTONE = "$Q/8604a5b9-314c-42ba-972a-d15872e123c5.png"
    private const val IMG_PYRITE_CONCRETION_BAND_SPHERE = "$Q/23ff3340-ae72-4a59-a91d-656230c27425.png"
    private const val IMG_PYRITE_CONCRETION_CRYSTAL_SPHERE = "$Q/a0b02d23-f9dd-4e27-8e88-3fec2542e31d.png"
    private const val IMG_PYRITE_CONCRETION_OVAL_HAND = "$Q/21c9b548-37da-4ea6-bfd6-328d09e12ab0.png"
    private const val IMG_PYRITE_CONCRETION_BANDED_STAND = "$Q/c34ab45a-735e-47eb-9144-78b6f582dfbc.png"
    private const val IMG_PYRITE_CONCRETION_RINGED_SPHERE = "$Q/ef713b8c-a3cb-4093-851f-f6f212eb8bd2.png"
    private const val IMG_PYRITE_CONCRETION_LARGE_DEPRESSION = "$Q/4203ddc5-bebc-4f22-932b-3365053038bf.png"

    // ── Master build batch: regenerated + new specimen photos (2026-07-19) ──
    private const val IMG_TREX_TOOTH_NEW = "$Q/2034a4eb-3291-4364-a49e-f06d1575d072.png"
    private const val IMG_SPINOSAURUS_TOOTH_NEW = "$Q/a42bf894-d828-41a9-b33e-8ef0d6d3e2a9.png"
    private const val IMG_TRICERATOPS_TOOTH_NEW = "$Q/d47c3157-e2bf-416b-bcbb-576aab76708e.png"
    private const val IMG_MAHOGANY_OBSIDIAN_NEW = "$Q/4a90a52d-d59d-484d-a8cc-de9fa3fa6b49.png"
    private const val IMG_GABBRO_INDIGO_NEW = "$Q/4909ffc9-0516-4b49-9c9a-8ce11cbbc116.png"
    private const val IMG_FUCHSITE_RUBY_NEW = "$Q/0ba897be-b87b-4a76-9d6e-613203595fb5.png"
    private const val IMG_ORPIMENT_REALGAR_NEW = "$Q/f9017d40-fd78-415c-b04c-ed042edfc6d4.png"
    private const val IMG_VISHNEVITE_NEW = "$Q/cf5fa0e3-760d-4af2-be28-4b9959fd18d0.png"
    private const val IMG_WAVELLITE_NEW = "$Q/a19ab3ff-3506-400c-855b-7c87088f561c.png"
    private const val IMG_TURGITE_IRIDESCENT_NEW = "$Q/0250aa70-59ac-450c-9bf9-3740374ab9e9.png"
    private const val IMG_TIGER_EYE_NEW = "$Q/a34fee49-0505-4e34-9ac6-887c514dc478.png"
    private const val IMG_SHATTUCKITE_NEW = "$Q/63938a1a-a6f1-42f6-9b17-ec790ea3c865.png"
    private const val IMG_ONYX_NEW = "$Q/f9418f11-fade-478f-9f42-c7cce5c5c4eb.png"
    private const val IMG_FUCHSITE_NEW = "$Q/1894fadc-da6f-496c-8f2d-c1275457e932.png"
    private const val IMG_GOETHITE_IRIDESCENT_NEW = "$Q/158200d1-38b8-479a-a733-9b50aba9aafe.png"
    private const val IMG_ORPIMENT_NEW = "$Q/b8c76d17-d649-4a09-b6df-8f260efa811c.png"
    private const val IMG_TRAPICHE_COMPANIONS_NEW = "$Q/c5d7a6db-4ff2-482a-a0d8-1fbdd3414521.png"
    private const val IMG_FAIRY_STONE_CONCRETION_NEW = "$Q/1ac1db48-1491-4489-a6f9-95d04f8625f4.png"
    private const val IMG_GOWANDA_TILLITE_NEW2 = "$Q/aa72e936-8af5-433b-98f0-ab357fe0b949.png"
    private const val IMG_SEPTARIAN_ROUGH_NEW = "$Q/6336edab-cd7c-404b-babd-8c9c23700a19.png"

    // ── Single-specimen image sets for stacked-card replacements (2026-07-20) ──
    private const val IMG_MAHOGANY_OBSIDIAN_ROUGH = "$Q/396e204c-0683-4691-956e-c3b6e5975dea.png"
    private const val IMG_MAHOGANY_OBSIDIAN_WILD = "$Q/ff904141-5b1a-4774-b805-75ad9775865c.png"
    private const val IMG_MAHOGANY_OBSIDIAN_MUSEUM = "$Q/2a6fc4e7-0e2e-4014-a7cc-accab2ccbc45.png"
    private const val IMG_MAHOGANY_OBSIDIAN_CABOCHON = "$Q/9f1ff21e-7fb5-4dec-9bfd-26a0ea1a3b0a.png"

    private const val IMG_FUCHSITE_MICA_ROUGH = "$Q/a5336ba8-54e8-4022-9770-01d80c2c97a1.png"
    private const val IMG_FUCHSITE_MICA_WILD = "$Q/8ade2dbc-f4df-4eed-93a3-1e2ffdb37905.png"
    private const val IMG_FUCHSITE_MICA_MUSEUM = "$Q/fb16b6ec-db11-4b5b-820d-8354ece52a73.png"
    private const val IMG_FUCHSITE_MICA_CABOCHON = "$Q/fbe57e89-e5e9-4f29-9780-15ae1fdedec5.png"

    private const val IMG_INDIGO_GABBRO_ROUGH = "$Q/1c5df762-bd42-4d20-bf8c-d5f45bc494bf.png"
    private const val IMG_INDIGO_GABBRO_WILD = "$Q/867d561a-d161-4da2-98c9-a18b375943de.png"
    private const val IMG_INDIGO_GABBRO_MUSEUM = "$Q/0b837635-4934-42ae-b6cb-626e25312468.png"
    private const val IMG_INDIGO_GABBRO_CABOCHON = "$Q/de3e024f-76f2-4a8f-9fa3-404e53037d95.png"

    private const val IMG_RUBY_FUCHSITE_ROUGH = "$Q/94312740-11c6-4f60-a020-2ee3d4e244d3.png"
    private const val IMG_RUBY_FUCHSITE_WILD = "$Q/78e70de2-b766-4b1b-9a89-a82a9f7d50a8.png"
    private const val IMG_RUBY_FUCHSITE_MUSEUM = "$Q/cd038e18-6faf-4475-88e9-0b49d2ec5bea.png"
    private const val IMG_RUBY_FUCHSITE_CABOCHON = "$Q/748c5019-a2b6-4609-a68d-87648b551441.png"

    // ── Follow-up single-specimen image replacements (2026-07-19) ──
    private const val IMG_BLACK_ONYX_SOLID_ROUGH = "$Q/1f3d89ae-c8f7-4da3-a04a-3f5b68eea586.png"
    private const val IMG_BLACK_ONYX_CABOCHON = "$Q/ddb0e666-ba74-4c1e-aab9-d509355d1608.png"
    private const val IMG_BLACK_ONYX_IN_HAND = "$Q/0610bf17-8278-4f1a-b4e6-82d6766ceb17.png"
    private const val IMG_BLACK_ONYX_CAMEO = "$Q/160e66de-3ef3-4685-b02d-5faa0ad3743e.png"
    private const val IMG_ORPIMENT_ROUGH_CLUSTER = "$Q/9e7d3e95-fc87-4ceb-bfb6-1cba3d033ac5.png"
    private const val IMG_ORPIMENT_ROUGH_MASS = "$Q/5dce4759-ac43-4a45-ae46-5a91c3817721.png"
    private const val IMG_ORPIMENT_ROUGH_IN_HAND = "$Q/e63955a9-1d5f-4511-b1a6-ac4543a5adc4.png"
    private const val IMG_ORPIMENT_ROUGH_CLOSEUP = "$Q/2778521c-b0b3-4ce1-b758-4aec3a815e33.png"
    private const val IMG_ORPIMENT_REALGAR_ROUGH_ASSEMBLAGE = "$Q/044b1505-1183-4ed0-8760-1d2f039d7b44.png"
    private const val IMG_ORPIMENT_REALGAR_ROUGH_CLUSTER = "$Q/47155639-120b-405c-abea-e9bab8605f46.png"
    private const val IMG_ORPIMENT_REALGAR_ROUGH_IN_HAND = "$Q/41f0302f-f95a-48c6-b552-cf114004d1bb.png"
    private const val IMG_ORPIMENT_REALGAR_ROUGH_CLOSEUP = "$Q/5c03c6e8-c5d1-466b-8baa-053404d2f7bb.png"

    // ── Reference-based replica batch (2026-07-19) ──
    private const val IMG_MICHIGAN_LIGHTNING_ROUGH_PILE = "$Q/86a7eb10-1c6e-4614-8d0c-4afc00fec6c1.png"
    private const val IMG_MICHIGAN_LIGHTNING_HAND_BEACH = "$Q/b4636e34-4481-4ca1-8779-819809cbd487.png"
    private const val IMG_MICHIGAN_LIGHTNING_CUT_PAIR = "$Q/3d957b1b-6a3f-432d-94f4-28cd8ec0c5d4.png"
    private const val IMG_MICHIGAN_LIGHTNING_HEART_CUT = "$Q/0817226c-df45-4bf3-b6b6-522e50031916.png"
    private const val IMG_FAIRY_STONE_DISC = "$Q/84b033bd-9e0c-43ad-b0ba-c9f60a89218a.png"
    private const val IMG_FAIRY_STONE_HAND = "$Q/6ddae467-be52-49a0-ab98-812c6201e8b8.png"
    private const val IMG_IRIDESCENT_TURGITE_GOETHITE_REPLICA = "$Q/7f8e81af-107c-4bed-8474-4a0a846a66eb.png"
    private const val IMG_WAVELLITE_GREEN_SPHERE_REPLICA = "$Q/d034ff48-c682-4c9e-b412-85c9a00426e8.png"
    private const val IMG_WAVELLITE_BLUE_GREEN_CLUSTER_REPLICA = "$Q/62c21229-f236-4d6f-80a5-45ca579b2d04.png"
    private const val IMG_WAVELLITE_BOTRYOIDAL_REPLICA = "$Q/6493d4c2-5c5c-47a4-8a2d-a8df6e61bc03.png"
    private const val IMG_TRICERATOPS_TOOTH_REPLICA = "$Q/52a62936-9858-4a4a-af7b-5018c0de219b.png"
    private const val IMG_TRICERATOPS_TOOTH_V2 = "$Q/3d169f42-69f8-4f37-9afd-741fee82376d.png"
    private const val IMG_VELOCIRAPTOR_TEETH_GROUP = "$Q/129f6508-be83-4d53-8af2-f784fd49a031.png"
    private const val IMG_VELOCIRAPTOR_TOOTH_SIDE = "$Q/445a9324-7bf5-420e-b859-f273616c4271.png"
    private const val IMG_VELOCIRAPTOR_TOOTH_DARK = "$Q/ee949e51-7a12-4c7a-9a6a-126dedbaf517.png"
    private const val IMG_ZEOLITE_NEEDLE_CLUSTER_REPLICA = "$Q/c29c31e7-ec75-4cd2-8ffa-9edfa572758b.png"
    private const val IMG_LEPIDOLITE_ROUGH_CHUNK = "$Q/b60f56ac-28a1-4869-b398-98a5e1e744f5.png"
    private const val IMG_LEPIDOLITE_ROUGH_HAND = "$Q/0ea0fe83-de29-4dca-b1a2-6c4c7155a186.png"

    // ── Follow-up reference-based replica batch (2026-07-19) ──
    private const val IMG_DATOLITE_ROUGH_NODULE_REPLICA = "$Q/7ae53a32-403e-44f8-bdeb-bc9047f9fd20.png"
    private const val IMG_DATOLITE_POLISHED_HALF_PINK_REPLICA = "$Q/bac48380-d69a-48b8-a3db-10c152805cfe.png"
    private const val IMG_DATOLITE_POLISHED_HALF_CREAM_REPLICA = "$Q/70cfb252-adbd-42b8-ba64-9ae85294c564.png"
    private const val IMG_FOSSIL_CEPHALOPOD_SHELL_REPLICA = "$Q/0a7d33e3-cbd3-4b13-af1a-f42300be2abe.png"
    private const val IMG_FOSSIL_CRINOID_STEM_REPLICA = "$Q/7da22fe2-1838-447c-b2ab-f19de895c65c.png"
    private const val IMG_K2_AZURITE_POLISHED_REPLICA = "$Q/4d656e9c-3321-4845-b093-a8e75017570d.png"
    private const val IMG_K2_AZURITE_ROUGH_REPLICA = "$Q/6bcd4a80-0b07-429e-a4ab-263dcc153e0a.png"

    // ── Individual Datolite replicas from reference attachment (2026-07-19) ──
    private const val IMG_DATOLITE_ROUGH_NODULE_V3 = "$Q/ec58fa96-8aa8-4d01-82ea-951d21d1805d.png"
    private const val IMG_DATOLITE_POLISHED_HALF_PINK_GREEN_V2 = "$Q/5b042391-47f5-4609-8ee0-311cd39a21c4.png"
    private const val IMG_DATOLITE_POLISHED_HALF_CREAM_GREEN_V2 = "$Q/0779b401-a767-491b-bc3e-4c810f33e731.png"
    private const val IMG_DATOLITE_SMALL_ROUGH_NODULE_V2 = "$Q/afe38e48-6986-4d6f-9558-0070d7848dcb.png"
    private const val IMG_DATOLITE_POLISHED_HALF_WHITE_V2 = "$Q/ba2244e6-bce8-4938-8eb8-3c432f7af5e4.png"
    private const val IMG_DATOLITE_ROUGH_WITH_MATRIX_V2 = "$Q/27881f41-28e6-40d9-b097-e708e8c1824f.png"

    // ── Corrected Datolite split-half color palette (2026-07-19) ──
    private const val IMG_DATOLITE_DARK_PINK_HALF = "$Q/1657b42d-959d-4896-829d-c9b1853028fa.png"
    private const val IMG_DATOLITE_LIGHT_GREEN_HALF = "$Q/e083ac1d-f360-4712-b125-a309ac39a910.png"
    private const val IMG_DATOLITE_CREAM_WHITE_HALF = "$Q/04e9c5a8-ca62-4f21-8aac-7449ea724ea7.png"
    private const val IMG_DATOLITE_PINK_WHITE_MARBLED_HALF = "$Q/eca048ce-c4cc-4da9-bc8d-8a93255b3080.png"

    // ── 1:1 mapping: every specimen id → one image URL ──
    private val urlChunk1: Map<String, List<String>> by lazy { mapOf(
        "ace-of-diamonds" to listOf(IMG_ACE_OF_DIAMONDS),
        "adamite" to listOf(IMG_ADAMITE, IMG_ADAMITE_WILD, IMG_ADAMITE_MUSEUM),
        "alaska-jade" to listOf(IMG_ALASKA_JADE, IMG_ALASKA_JADE_WILD, IMG_ALASKA_JADE_MUSEUM),
        "alexandrite" to listOf(IMG_ALEXANDRITE_ROUGH, IMG_ALEXANDRITE_WILD, IMG_ALEXANDRITE_MUSEUM),
        "amazonite" to listOf(IMG_AMAZONITE, IMG_AMAZONITE_ROUGH_2, IMG_AMAZONITE_WILD, IMG_AMAZONITE_MUSEUM, IMG_AMAZONITE_CABOCHON),
        "amber" to listOf(IMG_AMBER_ROUGH, IMG_AMBER_WILD, IMG_AMBER_MUSEUM, IMG_AMBER_CABOCHON),
        "amethyst" to listOf(IMG_AMETHYST, IMG_AMETHYST_WILD, IMG_AMETHYST_MUSEUM, IMG_AMETHYST_CABOCHON),
        "ametrine" to listOf(IMG_AMETRINE, IMG_AMETRINE_WILD, IMG_AMETRINE_MUSEUM, IMG_AMETRINE_CABOCHON),
        "ammonite" to listOf(IMG_AMMONITE, IMG_AMMONITE_WILD, IMG_AMMONITE_MUSEUM, IMG_AMMONITE_CABOCHON),
        "amphibolite" to listOf(IMG_AMPHIBOLITE, IMG_AMPHIBOLITE_WILD, IMG_AMPHIBOLITE_MUSEUM),
        "andalusite" to listOf(IMG_COMMONS_ANDALUSITE_MRZ335A, IMG_COMMONS_ANDALUSITE_RH1_24B, IMG_COMMONS_ANDALUSITE_LUCOMAGNO, IMG_ANDALUSITE, IMG_ANDALUSITE_WILD, IMG_ANDALUSITE_MUSEUM, IMG_ANDALUSITE_CABOCHON, IMG_ANDALUSITE_GEM_REPLICA),
        "chiastolite" to listOf(IMG_COMMONS_CHIASTOLITE, IMG_COMMONS_CHIASTOLITE_2581, IMG_COMMONS_CHIASTOLITE_CHINE, IMG_COMMONS_CHIASTOLITE_GEODIL, IMG_CHIASTOLITE, IMG_CHIASTOLITE_WILD, IMG_CHIASTOLITE_MUSEUM, IMG_CHIASTOLITE_CROSS_REPLICA),
        "anderson-mine" to listOf(IMG_ANDERSON_MINE),
        "anglesite" to listOf(IMG_ANGLESITE, IMG_ANGLESITE_WILD, IMG_ANGLESITE_MUSEUM),
        "anhydrite" to listOf(IMG_ANHYDRITE, IMG_ANHYDRITE_WILD, IMG_ANHYDRITE_MUSEUM),
        "annabergite" to listOf(IMG_ANNABERGITE, IMG_ANNABERGITE_WILD, IMG_ANNABERGITE_MUSEUM),
        "anorthosite" to listOf(IMG_ANORTHOSITE, IMG_ANORTHOSITE_WILD, IMG_ANORTHOSITE_MUSEUM),
        "anthracite" to listOf(IMG_ANTHRACITE, IMG_ANTHRACITE_WILD, IMG_ANTHRACITE_MUSEUM),
        "apatite" to listOf(IMG_APATITE_ROUGH, IMG_APATITE_WILD, IMG_APATITE_MUSEUM),
        "apophyllite" to listOf(IMG_APOPHYLLITE, IMG_APOPHYLLITE_WILD, IMG_APOPHYLLITE_MUSEUM),
        "aquamarine" to listOf(IMG_AQUAMARINE_ROUGH, IMG_AQUAMARINE_WILD, IMG_AQUAMARINE_MUSEUM),
        "aragonite" to listOf(IMG_ARAGONITE, IMG_ARAGONITE_WILD, IMG_ARAGONITE_MUSEUM),
        "aragonite-flowers" to listOf(IMG_ARAGONITE_FLOWERS),
        "archimedes" to listOf(IMG_ARCHIMEDES, IMG_ARCHIMEDES_WILD, IMG_ARCHIMEDES_MUSEUM),
        "arkansas-quartz-gallery" to listOf(IMG_ARKANSAS_QUARTZ_GALLERY),
        "arkose" to listOf(IMG_ARKOSE, IMG_ARKOSE_WILD, IMG_ARKOSE_MUSEUM),
        "arsenopyrite" to listOf(IMG_ARSENOPYRITE, IMG_ARSENOPYRITE_WILD),
        "augite" to listOf(IMG_AUGITE, IMG_AUGITE_WILD, IMG_AUGITE_MUSEUM),
        "autunite" to listOf(IMG_AUTUNITE, IMG_AUTUNITE_WILD, IMG_AUTUNITE_MUSEUM),
        "axinite" to listOf(IMG_AXINITE, IMG_AXINITE_WILD, IMG_AXINITE_MUSEUM),
        "azurite" to listOf(IMG_AZURITE_ROUGH, IMG_AZURITE_WILD, IMG_AZURITE_MUSEUM, IMG_AZURITE_CABOCHON),
        "baculites" to listOf(IMG_BACULITES, IMG_BACULITES_WILD, IMG_BACULITES_MUSEUM),
        "baker-ranch" to listOf(IMG_BAKER_RANCH),
        "barite" to listOf(IMG_BARITE, IMG_BARITE_WILD, IMG_BARITE_MUSEUM),
        "basalt" to listOf(IMG_BASALT, IMG_BASALT_WILD, IMG_BASALT_MUSEUM),
        "belemnite" to listOf(IMG_BELEMNITE, IMG_BELEMNITE_WILD, IMG_BELEMNITE_MUSEUM),
        "benitoite" to listOf(IMG_BENITOITE_ROUGH, IMG_BENITOITE_WILD, IMG_BENITOITE_MUSEUM),
        "beryl" to listOf(IMG_BERYL, IMG_BERYL_AQUA, IMG_BERYL_MORGANITE, IMG_BERYL_HELIODOR, IMG_BERYL_GOSHENITE, IMG_BERYL_WILD, IMG_BERYL_MUSEUM),
        "bismuth-crystal" to listOf(IMG_BISMUTH_CRYSTAL, IMG_BISMUTH_WILD, IMG_BISMUTH_MUSEUM),
        "bivalve-fossil" to listOf(IMG_BIVALVE_FOSSIL, IMG_BIVALVE_WILD, IMG_BIVALVE_MUSEUM),
        "black-hills-institute" to listOf(IMG_BLACK_HILLS_INSTITUTE),
        "blastoid" to listOf(IMG_BLASTOID, IMG_BLASTOID_WILD, IMG_BLASTOID_MUSEUM),
        "bloodstone" to listOf(IMG_BLOODSTONE, IMG_BLOODSTONE_WILD, IMG_BLOODSTONE_MUSEUM, IMG_BLOODSTONE_CABOCHON),
        "blue-aragonite" to listOf(IMG_BLUE_ARAGONITE),
        "blue-lace-agate" to listOf(IMG_BLUE_LACE_AGATE, IMG_BLUE_LACE_AGATE_WILD, IMG_BLUE_LACE_AGATE_MUSEUM, IMG_BLUE_LACE_AGATE_CABOCHON),
        "bornite" to listOf(IMG_BORNITE, IMG_BORNITE_WILD, IMG_BORNITE_MUSEUM),
        "boston-mineral-club" to listOf(IMG_BOSTON_MINERAL_CLUB),
        "brachiopod" to listOf(IMG_BRACHIOPOD, IMG_BRACHIOPOD_WILD, IMG_BRACHIOPOD_MUSEUM),
        "breccia" to listOf(IMG_BRECCIA, IMG_BRECCIA_WILD, IMG_BRECCIA_MUSEUM),
        "brimley-yooper" to listOf(IMG_BRIMLEY_YOOPER),
        "brochantite" to listOf(IMG_BROCHANTITE, IMG_BROCHANTITE_WILD, IMG_BROCHANTITE_MUSEUM),
        "bronzite" to listOf(IMG_BRONZITE, IMG_BRONZITE_WILD, IMG_BRONZITE_MUSEUM),
        "burro-creek" to listOf(IMG_BURRO_CREEK),
        "cahaba-river" to listOf(IMG_CAHABA_RIVER),
        "calamites" to listOf(IMG_CALAMITES, IMG_CALAMITES_WILD, IMG_CALAMITES_MUSEUM),
        "calcite" to listOf(IMG_CALCITE, IMG_CALCITE_AMBER, IMG_CALCITE_BLUE, IMG_CALCITE_GREEN, IMG_CALCITE_WHITE, IMG_CALCITE_WILD, IMG_CALCITE_MUSEUM),
        "california-rock-shop" to listOf(IMG_CALIFORNIA_ROCK_SHOP),
        "caribbean-calcite" to listOf(IMG_CALCITE_CARIBBEAN),
        "carnelian" to listOf(IMG_CARNELIAN, IMG_CARNELIAN_WILD, IMG_CARNELIAN_MUSEUM, IMG_CARNELIAN_CABOCHON),
        "carnotite" to listOf(IMG_CARNOTITE, IMG_CARNOTITE_WILD, IMG_CARNOTITE_MUSEUM),
        "cassiterite" to listOf(IMG_CASSITERITE_BOLIVIA, IMG_CASSITERITE, IMG_CASSITERITE_MUSEUM),
        "cerussite" to listOf(IMG_CERUSSITE, IMG_CERUSSITE_WILD, IMG_CERUSSITE_MUSEUM),
        "chalcopyrite" to listOf(IMG_CHALCOPYRITE, IMG_CHALCOPYRITE_WILD, IMG_CHALCOPYRITE_MUSEUM),
        "chalk-rock" to listOf(IMG_CHALK_ROCK, IMG_CHALK_ROCK_VAR),
        "chambersite" to listOf(IMG_CHAMBERSITE, IMG_CHAMBERSITE_WILD, IMG_CHAMBERSITE_MUSEUM),
        "charoite" to listOf(IMG_CHAROITE, IMG_CHAROITE_ROUGH_2, IMG_CHAROITE_WILD, IMG_CHAROITE_MUSEUM, IMG_CHAROITE_CABOCHON),
        "cherokee-ruby" to listOf(IMG_CHEROKEE_RUBY, IMG_CHEROKEE_RUBY_WILD, IMG_CHEROKEE_RUBY_MUSEUM),
        "charlevoix-stone" to listOf(IMG_CHARLEVOIX_SPECIMEN, IMG_CHARLEVOIX_WILD),
        "chert" to listOf(IMG_CHERT, IMG_CHERT_WILD, IMG_CHERT_MUSEUM, IMG_CHERT_CABOCHON_NEW),
        "chlorastrolite" to listOf(IMG_CHLORASTROLITE, IMG_CHLORASTROLITE_WILD, IMG_CHLORASTROLITE_MUSEUM, IMG_CHLORASTROLITE_CABOCHON),
        "chromite" to listOf(IMG_CHROMITE, IMG_CHROMITE_WILD, IMG_CHROMITE_MUSEUM),
        "chrysoberyl" to listOf(IMG_CHRYSOBERYL, IMG_CHRYSOBERYL_WILD, IMG_CHRYSOBERYL_MUSEUM, IMG_CHRYSOBERYL_CABOCHON),
        "chrysocolla" to listOf(IMG_CHRYSOCOLLA, IMG_CHRYSOCOLLA_WILD, IMG_CHRYSOCOLLA_MUSEUM, IMG_CHRYSOCOLLA_CABOCHON),
        "cinnabar" to listOf(IMG_CINNABAR, IMG_CINNABAR_WILD, IMG_CINNABAR_MUSEUM),
        "citrine" to listOf(IMG_CITRINE, IMG_CITRINE_WILD, IMG_CITRINE_MUSEUM, IMG_CITRINE_CABOCHON),
        "coalinga-jade" to listOf(IMG_COALINGA_JADE, IMG_COALINGA_JADE_VAR),
        "cobaltite" to listOf(IMG_COBALTITE, IMG_COBALTITE_WILD, IMG_COBALTITE_MUSEUM),
        "coelacanth-fossil" to listOf(IMG_COELACANTH_FOSSIL, IMG_COELACANTH_WILD, IMG_COELACANTH_MUSEUM),
        "colemanite" to listOf(IMG_COLEMANITE, IMG_COLEMANITE_WILD, IMG_COLEMANITE_MUSEUM),
        "brewsterite" to listOf(IMG_BREWSTERITE, IMG_BREWSTERITE_MUSEUM),
        "conglomerate" to listOf(IMG_CONGLOMERATE_SANDSTONE, IMG_CONGLOMERATE_WILD, IMG_CONGLOMERATE_MUSEUM),
        "concretion-pyrite" to listOf(IMG_PYRITE_CONCRETION_BAND_SPHERE, IMG_PYRITE_CONCRETION_CRYSTAL_SPHERE, IMG_PYRITE_CONCRETION_OVAL_HAND, IMG_PYRITE_CONCRETION_BANDED_STAND, IMG_PYRITE_CONCRETION_RINGED_SPHERE, IMG_PYRITE_CONCRETION_LARGE_DEPRESSION),
        "conichalcite" to listOf(IMG_CONICHALCITE, IMG_CONICHALCITE_WILD, IMG_CONICHALCITE_MUSEUM),
        "conularia" to listOf(IMG_CONULARIA, IMG_CONULARIA_WILD, IMG_CONULARIA_MUSEUM),
        "copper-harbor-yooper" to listOf(IMG_COPPER_HARBOR_YOOPER),
        "coprolite" to listOf(IMG_COPROLITE, IMG_COPROLITE_WILD, IMG_COPROLITE_MUSEUM),
        "coquina" to listOf(IMG_COQUINA, IMG_COQUINA_WILD, IMG_COQUINA_MUSEUM),
        "corundum" to listOf(IMG_CORUNDUM, IMG_CORUNDUM_BLUE, IMG_CORUNDUM_YELLOW, IMG_CORUNDUM_GREEN, IMG_CORUNDUM_PINK, IMG_CORUNDUM_WILD, IMG_CORUNDUM_MUSEUM, IMG_CORUNDUM_CABOCHON),
        "crabtree-emerald" to listOf(IMG_CRABTREE_EMERALD, IMG_CRABTREE_EMERALD_WILD, IMG_CRABTREE_EMERALD_MUSEUM, IMG_CRABTREE_EMERALD_CAB),
        "crater-of-diamonds" to listOf(IMG_CRATER_OF_DIAMONDS),
        "crinoid" to listOf(IMG_CRINOID, IMG_CRINOID_WILD, IMG_CRINOID_MUSEUM),
        "crocoite" to listOf(IMG_CROCOITE, IMG_CROCOITE_WILD, IMG_CROCOITE_MUSEUM),
        "crystal-grove" to listOf(IMG_CRYSTAL_GROVE),
        "crystal-mountain" to listOf(IMG_CRYSTAL_MOUNTAIN),
        "crystal-park" to listOf(IMG_CRYSTAL_PARK),
        "crystal-works" to listOf(IMG_CRYSTAL_WORKS),
        "cuprite" to listOf(IMG_CUPRITE, IMG_CUPRITE_WILD, IMG_CUPRITE_MUSEUM),
        "danburite" to listOf(IMG_DANBURITE, IMG_DANBURITE_WILD, IMG_DANBURITE_MUSEUM),
        "datolite" to listOf(
            IMG_DATOLITE_ROUGH_NODULE_V3,
            IMG_DATOLITE_DARK_PINK_HALF,
            IMG_DATOLITE_LIGHT_GREEN_HALF,
            IMG_DATOLITE_CREAM_WHITE_HALF,
            IMG_DATOLITE_PINK_WHITE_MARBLED_HALF
        ),
        "dendrite-agate" to listOf(IMG_DENDRITE_AGATE, IMG_DENDRITE_AGATE_WILD, IMG_DENDRITE_AGATE_MUSEUM, IMG_DENDRITE_AGATE_CABOCHON),
        "denio-thundereggs" to listOf(IMG_DENIO_THUNDEREGGS, IMG_DENIO_THUNDEREGG_WILD, IMG_DENIO_THUNDEREGG_MUSEUM, IMG_DENIO_THUNDEREGG_CAB),
        "denver-gem-show" to listOf(IMG_DENVER_GEM_SHOW),
        "descloizite" to listOf(IMG_DESCLOIZITE, IMG_DESCLOIZITE_WILD, IMG_DESCLOIZITE_MUSEUM),
        "diamond" to listOf(IMG_DIAMOND, IMG_DIAMOND_YELLOW, IMG_DIAMOND_PINK, IMG_DIAMOND_BLUE, IMG_DIAMOND_WILD, IMG_DIAMOND_MUSEUM),
        "diamond-hill" to listOf(IMG_DIAMOND_HILL),
        "diamond-peak" to listOf(IMG_DIAMOND_PEAK),
        "diatomite" to listOf(IMG_DIATOMITE, IMG_DIATOMITE_WILD, IMG_DIATOMITE_MUSEUM),
        "dinosaur-bone" to listOf(IMG_DINOSAUR_BONE, IMG_DINOSAUR_BONE_WILD, IMG_DINOSAUR_BONE_MUSEUM, IMG_DINOSAUR_BONE_CABOCHON),
        "dinosaur-eggshell" to listOf(IMG_DINOSAUR_EGGSHELL, IMG_DINOSAUR_EGGSHELL_WILD, IMG_DINOSAUR_EGGSHELL_MUSEUM),
        "dinosaur-track" to listOf(IMG_DINOSAUR_TRACK, IMG_DINOSAUR_TRACK_WILD, IMG_DINOSAUR_TRACK_MUSEUM),
        "diopside" to listOf(IMG_DIOPSIDE, IMG_DIOPSIDE_WILD, IMG_DIOPSIDE_MUSEUM),
        "dioptase" to listOf(IMG_DIOPTASE, IMG_DIOPTASE_WILD, IMG_DIOPTASE_MUSEUM),
        "diorite" to listOf(IMG_DIORITE, IMG_DIORITE_WILD, IMG_DIORITE_MUSEUM),
        "dire-wolf-tooth" to listOf(IMG_DIRE_WOLF_TOOTH, IMG_DIRE_WOLF_TOOTH_VAR),
        "dixie-crystal-co" to listOf(IMG_DIXIE_CRYSTAL_CO),
        "dolomite" to listOf(IMG_DOLOMITE_CRYSTALS_NEW, IMG_DOLOMITE_WILD, IMG_DOLOMITE_MUSEUM),
        "dugway-geodes" to listOf(IMG_DUGWAY_GEODES, IMG_DUGWAY_GEODES_WILD, IMG_DUGWAY_GEODES_MUSEUM, IMG_DUGWAY_GEODES_CAB),
        "dumortierite" to listOf(IMG_DUMORTIERITE, IMG_DUMORTIERITE_WILD, IMG_DUMORTIERITE_MUSEUM),
        "dumortierite-quartz" to listOf(IMG_DUMORTIERITE_QUARTZ, IMG_DUMORTIERITE_QUARTZ_WILD, IMG_DUMORTIERITE_QUARTZ_MUSEUM),
        "eclogite" to listOf(IMG_ECLOGITE, IMG_ECLOGITE_WILD, IMG_ECLOGITE_MUSEUM),
        "ecphora" to listOf(IMG_ECPHORA, IMG_ECPHORA_VAR),
        "ellenville" to listOf(IMG_ELLENVILLE, IMG_ELLENVILLE_WILD, IMG_ELLENVILLE_MUSEUM),
        "emerald" to listOf(IMG_EMERALD, IMG_EMERALD_WILD, IMG_EMERALD_MUSEUM),
        "emerald-hollow" to listOf(IMG_EMERALD_HOLLOW, IMG_EMERALD_HOLLOW_WILD, IMG_EMERALD_HOLLOW_MUSEUM, IMG_EMERALD_HOLLOW_CAB),
        "enchantment-agates" to listOf(IMG_ENCHANTMENT_AGATES, IMG_ENCHANTMENT_AGATE_WILD, IMG_ENCHANTMENT_AGATE_MUSEUM, IMG_ENCHANTMENT_AGATE_CAB),
        "enchodus" to listOf(IMG_ENCHODUS, IMG_ENCHODUS_WILD, IMG_ENCHODUS_MUSEUM),
        "endoceras" to listOf(IMG_ENDOCERAS, IMG_ENDOCERAS_WILD, IMG_ENDOCERAS_MUSEUM),
        "enstatite" to listOf(IMG_ENSTATITE, IMG_ENSTATITE_WILD, IMG_ENSTATITE_MUSEUM),
        "epidote" to listOf(IMG_EPIDOTE, IMG_EPIDOTE_WILD, IMG_EPIDOTE_MUSEUM),
        "erythrite" to listOf(IMG_ERYTHRITE, IMG_ERYTHRITE_WILD, IMG_ERYTHRITE_MUSEUM),
        "euclase" to listOf(IMG_EUCLASE, IMG_EUCLASE_WILD, IMG_EUCLASE_MUSEUM),
        "eurypterid" to listOf(IMG_EURYPTERID, IMG_EURYPTERID_WILD, IMG_EURYPTERID_MUSEUM),
        "exogyra" to listOf(IMG_EXOGYRA, IMG_EXOGYRA_WILD, IMG_EXOGYRA_MUSEUM),
        "favosites" to listOf("$Q/443a83d2-405c-4310-9eb3-6829b2e877e7.png", "$Q/b7cb11d4-04fd-4167-ac5d-ae994852c0d7.png", IMG_FAVOSITES_MUSEUM),
        "fenestella" to listOf(IMG_FENESTELLA, IMG_FENESTELLA_WILD, IMG_FENESTELLA_MUSEUM),
        "ferberite" to listOf(IMG_FERBERITE, IMG_FERBERITE_WILD, IMG_FERBERITE_MUSEUM),
        "fire-agate" to listOf(IMG_FIRE_AGATE, IMG_FIRE_AGATE_WILD, IMG_FIRE_AGATE_MUSEUM, IMG_FIRE_AGATE_CABOCHON),
        "fire-opal" to listOf(IMG_FIRE_OPAL, IMG_FIRE_OPAL_WILD, IMG_FIRE_OPAL_MUSEUM, IMG_FIRE_OPAL_CABOCHON),
        "flint" to listOf(IMG_FLINT, IMG_FLINT_BROWN, IMG_FLINT_BANDED, IMG_FLINT_GRAY, IMG_FLINT_RED, IMG_FLINT_WILD, IMG_FLINT_MUSEUM),
        "florida-rock-shack" to listOf(IMG_FLORIDA_ROCK_SHACK),
        "fluorite" to listOf(IMG_FLUORITE, IMG_FLUORITE_GREEN, IMG_FLUORITE_BLUE, IMG_FLUORITE_YELLOW, IMG_FLUORITE_RAINBOW, IMG_FLUORITE_WILD, IMG_FLUORITE_WILD_2, IMG_FLUORITE_MUSEUM),
        "fluorite-district" to listOf(IMG_FLUORITE_DISTRICT),
        "fossil-coral" to listOf(IMG_FOSSIL_CORAL, IMG_FOSSIL_CORAL_WILD, IMG_FOSSIL_CORAL_MUSEUM, IMG_FOSSIL_CORAL_CABOCHON),
        "fossil-fern" to listOf(IMG_FOSSIL_FERN, IMG_FOSSIL_FERN_WILD, IMG_FOSSIL_FERN_MUSEUM),
        "fossil-fish" to listOf(IMG_FOSSIL_FISH, IMG_FOSSIL_FISH_WILD, IMG_FOSSIL_FISH_MUSEUM),
        "franklin-gem" to listOf(IMG_FRANKLIN_GEM),
        "franklin-mineral" to listOf(IMG_FRANKLIN_MINERAL),
        "fusulinid" to listOf(IMG_FUSULINID, IMG_FUSULINID_WILD, IMG_FUSULINID_MUSEUM),
        "gabbro" to listOf(IMG_GABBRO, IMG_GABBRO_WILD, IMG_GABBRO_MUSEUM),
        "galena" to listOf(IMG_GALENA, IMG_GALENA_WILD, IMG_GALENA_WILD_2, IMG_GALENA_MUSEUM, IMG_GALENA_CABOCHON_NEW),
        "garnet" to listOf(IMG_GARNET, IMG_GARNET_ORANGE, IMG_GARNET_GREEN, IMG_GARNET_PURPLE, IMG_GARNET_WILD, IMG_GARNET_WILD_2, IMG_GARNET_MUSEUM),
        "garnet-hill" to listOf(IMG_GARNET_HILL),
        "gaspeite" to listOf(IMG_GASPEITE, IMG_GASPEITE_WILD, IMG_GASPEITE_MUSEUM, IMG_GASPEITE_CABOCHON),
        "gastropod-fossil" to listOf(IMG_GASTROPOD_FOSSIL, IMG_GASTROPOD_WILD, IMG_GASTROPOD_MUSEUM),
        "gem-mineral-hall" to listOf(IMG_GEM_MINERAL_HALL),
        "gem-mountain" to listOf(IMG_GEM_MOUNTAIN),
        "giant-sloth-claw" to listOf(IMG_GIANT_SLOTH_CLAW, IMG_GIANT_SLOTH_CLAW_VAR),
        "gilsum-mine" to listOf(IMG_GILSUM_MINE),
        "ginkgo-fossil" to listOf(IMG_GINKGO_FOSSIL, IMG_GINKGO_WILD, IMG_GINKGO_MUSEUM),
        "glass-butte" to listOf(IMG_GLASS_BUTTE, IMG_GLASS_BUTTE_VAR),
        "glaucophane" to listOf(IMG_GLAUCOPHANE, IMG_GLAUCOPHANE_WILD, IMG_GLAUCOPHANE_MUSEUM),
        "glossopteris" to listOf(IMG_GLOSSOPTERIS, IMG_GLOSSOPTERIS_WILD, IMG_GLOSSOPTERIS_MUSEUM, IMG_ALIVE_GLOSSOPTERIS),
        "gneiss" to listOf(IMG_GNEISS, IMG_GNEISS_WILD, IMG_GNEISS_MUSEUM),
        "goethite" to listOf(IMG_GOETHITE, IMG_GOETHITE_WILD, IMG_GOETHITE_MUSEUM),
        "goniatite" to listOf(IMG_GONIATITE, IMG_GONIATITE_WILD, IMG_GONIATITE_MUSEUM),
        "goshenite" to listOf(IMG_GOSHENITE, IMG_GOSHENITE_WILD, IMG_GOSHENITE_MUSEUM),
        "grand-marais-yooper" to listOf(IMG_YOOPERLITE_GRAND_MARAIS),
        "grandidierite" to listOf(IMG_GRANDIDIERITE, IMG_GRANDIDIERITE_WILD, IMG_GRANDIDIERITE_MUSEUM),
        "granite" to listOf(IMG_GRANITE, IMG_GRANITE_GRAY, IMG_GRANITE_WHITE, IMG_GRANITE_BLACK_SPECKLED, IMG_GRANITE_PINK, IMG_GRANITE_RED, IMG_GRANITE_WILD, IMG_GRANITE_MUSEUM),
        "graphite" to listOf(IMG_GRAPHITE, IMG_GRAPHITE_WILD, IMG_GRAPHITE_MUSEUM),
        "graptolite" to listOf(IMG_GRAPTOLITE, IMG_GRAPTOLITE_WILD, IMG_GRAPTOLITE_MUSEUM),
        "graves-mountain" to listOf(IMG_GRAVES_MOUNTAIN),
        "great-white-tooth" to listOf(IMG_GREAT_WHITE_TOOTH, IMG_GREAT_WHITE_TOOTH_WILD, IMG_GREAT_WHITE_TOOTH_MUSEUM),
        "greenschist" to listOf(IMG_GREENSCHIST, IMG_GREENSCHIST_WILD, IMG_GREENSCHIST_MUSEUM),
        "grossular" to listOf(IMG_GROSSULAR, IMG_GROSSULAR_WILD, IMG_GROSSULAR_MUSEUM),
        "gryphaea" to listOf(IMG_GRYPHAEA, IMG_GRYPHAEA_WILD, IMG_GRYPHAEA_MUSEUM),
        "gypsum" to listOf(IMG_GYPSUM, IMG_GYPSUM_WILD, IMG_GYPSUM_MUSEUM),
        "gypsum-mineral" to listOf(IMG_GYPSUM_MINERAL_ROUGH, IMG_GYPSUM_MINERAL_WILD),
        "gyrolite" to listOf(IMG_GYROLITE, IMG_GYROLITE_WILD, IMG_GYROLITE_MUSEUM)
    ) }

    private val urlChunk2: Map<String, List<String>> by lazy { mapOf(
        "hallelujah-jct" to listOf(IMG_HALLELUJAH_JCT),
        "halysites" to listOf(IMG_HALYSITES, IMG_HALYSITES_WILD, IMG_HALYSITES_MUSEUM),
        "hambergite" to listOf(IMG_HAMBERGITE, IMG_HAMBERGITE_WILD, IMG_HAMBERGITE_MUSEUM),
        "helena-sapphire" to listOf(IMG_HELENA_SAPPHIRE, IMG_HELENA_SAPPHIRE_WILD, IMG_HELENA_SAPPHIRE_MUSEUM),
        "helenite" to listOf(IMG_HELENITE),
        "helicoprion" to listOf(IMG_HELICOPRION, IMG_HELICOPRION_WILD, IMG_HELICOPRION_MUSEUM, IMG_ALIVE_HELICOPRION),
        "heliodor" to listOf(IMG_HELIODOR, IMG_HELIODOR_WILD, IMG_HELIODOR_MUSEUM),
        "hematite" to listOf(IMG_HEMATITE_ROUGH, IMG_HEMATITE_WILD_NEW, IMG_HEMATITE_MUSEUM_NEW),
        "hematite-botryoidal" to listOf(IMG_HEMATITE, IMG_HEMATITE_WILD, IMG_HEMATITE_MUSEUM),
        "hematite-specular" to listOf(IMG_HEMATITE_SPECULAR_ROUGH, IMG_HEMATITE_SPECULAR_WILD, IMG_HEMATITE_SPECULAR_MUSEUM),
        "herrerasaurus" to listOf(IMG_HERRERASAURUS_MUSEUM),
        "herkimer" to listOf(IMG_HERKIMER, IMG_HERKIMER_WILD, IMG_HERKIMER_MUSEUM),
        "heulandite" to listOf(IMG_HEULANDITE, IMG_HEULANDITE_WILD, IMG_HEULANDITE_MUSEUM),
        "hibonite" to listOf(IMG_HIBONITE, IMG_HIBONITE_WILD, IMG_HIBONITE_MUSEUM),
        "hiddenite" to listOf(IMG_HIDDENITE, IMG_HIDDENITE_WILD, IMG_HIDDENITE_MUSEUM),
        "himalaya-mine" to listOf(IMG_HIMALAYA_MINE),
        "hog-creek" to listOf(IMG_HOG_CREEK),
        "hogg-mine" to listOf(IMG_HOGG_MINE),
        "hornfels" to listOf(IMG_HORNFELS, IMG_HORNFELS_WILD, IMG_HORNFELS_MUSEUM),
        "horse-canyon" to listOf(IMG_HORSE_CANYON),
        "horseshoe-crab" to listOf(IMG_HORSESHOE_CRAB_VAR),
        "huebnerite" to listOf(IMG_HUEBNERITE, IMG_HUEBNERITE_WILD),
        "hybodus" to listOf(IMG_HYBODUS, IMG_HYBODUS_VAR),
        "hypersthene" to listOf(IMG_HYPERSTHENE, IMG_HYPERSTHENE_WILD, IMG_HYPERSTHENE_MUSEUM),
        "ichthyosaur-vertebra" to listOf(IMG_ICHTHYOSAUR_VERTEBRA, IMG_ICHTHYOSAUR_VERTEBRA_VAR),
        "inoceramus" to listOf(IMG_INOCERAMUS, IMG_INOCERAMUS_VAR),
        "iolite" to listOf(IMG_IOLITE, IMG_IOLITE_WILD, IMG_IOLITE_MUSEUM),
        "iroquois-coppermine" to listOf(IMG_IROQUOIS_COPPERMINE),
        "isabella-mine" to listOf(IMG_ISABELLA_MINE),
        "jackson-crossroads" to listOf(IMG_JACKSON_CROSSROADS, IMG_JACKSON_CROSSROADS_WILD, IMG_JACKSON_CROSSROADS_MUSEUM),
        "jade" to listOf(IMG_JADE, IMG_JADE_WHITE, IMG_JADE_LAVENDER, IMG_JADE_BLACK, IMG_JADE_WILD, IMG_JADE_MUSEUM, IMG_JADE_CABOCHON),
        "jade-cove" to listOf(IMG_JADE_COVE),
        "banded-chert" to listOf(IMG_BANDED_CHERT, IMG_BANDED_CHERT_VAR, IMG_BANDED_CHERT_SLAB),
        "jasper-picture" to listOf(IMG_JASPER_PICTURE, IMG_JASPER_PICTURE_WILD, IMG_JASPER_PICTURE_MUSEUM, IMG_JASPER_PICTURE_CAB),
        "jasper-brecciated" to listOf(IMG_JASPER_BRECCIATED, IMG_JASPER_BRECCIATED_WILD, IMG_JASPER_BRECCIATED_MUSEUM, IMG_JASPER_BRECCIATED_CAB),
        "jasper-mookaite" to listOf(IMG_JASPER_MOOKAITE, IMG_JASPER_MOOKAITE_WILD, IMG_JASPER_MOOKAITE_MUSEUM, IMG_JASPER_MOOKAITE_CAB),
        "jasper-ocean" to listOf(IMG_JASPER_OCEAN, IMG_JASPER_OCEAN_WILD, IMG_JASPER_OCEAN_MUSEUM, IMG_JASPER_OCEAN_CAB),
        "jasper-morrisonite" to listOf(IMG_JASPER_MORRISONITE, IMG_JASPER_MORRISONITE_WILD, IMG_JASPER_MORRISONITE_MUSEUM, IMG_JASPER_MORRISONITE_CAB),
        "jasper-leopard-skin" to listOf(IMG_JASPER_LEOPARD_SKIN, IMG_JASPER_LEOPARD_WILD, IMG_JASPER_LEOPARD_MUSEUM, IMG_JASPER_LEOPARD_CAB),
        "jasper-willow-creek" to listOf(IMG_JASPER_WILLOW_CREEK, IMG_JASPER_WILLOW_CREEK_VAR),
        "jasper-bruneau" to listOf(IMG_JASPER_BRUNEAU, IMG_JASPER_BRUNEAU_WILD, IMG_JASPER_BRUNEAU_MUSEUM, IMG_JASPER_BRUNEAU_CAB),
        "jasper-biggs" to listOf(IMG_JASPER_BIGGS, IMG_JASPER_BIGGS_VAR),
        "jasper-imperial" to listOf(IMG_JASPER_IMPERIAL, IMG_JASPER_IMPERIAL_WILD, IMG_JASPER_IMPERIAL_MUSEUM, IMG_JASPER_IMPERIAL_CAB),
        "jasper-kambaba" to listOf(IMG_JASPER_KAMBABA, IMG_JASPER_KAMBABA_WILD, IMG_JASPER_KAMBABA_MUSEUM, IMG_JASPER_KAMBABA_CAB),
        "jasper-polychrome" to listOf(IMG_JASPER_POLYCHROME, IMG_JASPER_POLYCHROME_WILD, IMG_JASPER_POLYCHROME_MUSEUM, IMG_JASPER_POLYCHROME_CAB),
        "jasper-red-creek" to listOf(IMG_JASPER_RED_CREEK_MUSEUM, IMG_JASPER_RED_CREEK),
        "jasper-noreena" to listOf(IMG_JASPER_NOREENA, IMG_JASPER_NOREENA_WILD, IMG_JASPER_NOREENA_MUSEUM, IMG_JASPER_NOREENA_CAB),
        "jasper-orbicular" to listOf(IMG_JASPER_ORBICULAR, IMG_JASPER_ORBICULAR_WILD, IMG_JASPER_ORBICULAR_MUSEUM, IMG_JASPER_ORBICULAR_CAB),
        "jasper-porcelain" to listOf(IMG_JASPER_PORCELAIN, IMG_JASPER_PORCELAIN_WILD, IMG_JASPER_PORCELAIN_MUSEUM, IMG_JASPER_PORCELAIN_CAB),
        "jasper-poppy" to listOf(IMG_JASPER_POPPY, IMG_JASPER_POPPY_WILD, IMG_JASPER_POPPY_MUSEUM, IMG_JASPER_POPPY_CAB),
        "jasper-stone-canyon" to listOf(IMG_JASPER_STONE_CANYON_MUSEUM, IMG_JASPER_STONE_CANYON),
        "jasper-blue-mountain" to listOf(IMG_JASPER_BLUE_MOUNTAIN, IMG_JASPER_BLUE_MOUNTAIN_VAR),
        "k2-jasper" to listOf(IMG_K2_AZURITE_POLISHED_REPLICA, IMG_K2_AZURITE_ROUGH_REPLICA),
        "jaspillite" to listOf(IMG_JASPILLITE),
        "jeremejevite" to listOf(IMG_JEREMEJEVITE, IMG_JEREMEJEVITE_WILD, IMG_JEREMEJEVITE_MUSEUM),
        "joaquinite" to listOf(IMG_JOAQUINITE, IMG_JOAQUINITE_WILD, IMG_JOAQUINITE_MUSEUM),
        "keenan-quarry" to listOf(IMG_KEENAN_QUARRY),
        "keokuk-geodes" to listOf(IMG_KEOKUK_GEODES, IMG_KEOKUK_GEODES_VAR),
        "keystone-gallery" to listOf(IMG_KEYSTONE_GALLERY),
        "komatiite" to listOf(IMG_KOMATIITE, IMG_KOMATIITE_WILD, IMG_KOMATIITE_MUSEUM),
        "kunzite" to listOf(IMG_KUNZITE, IMG_KUNZITE_WILD, IMG_KUNZITE_MUSEUM),
        "kyanite" to listOf(IMG_KYANITE, IMG_KYANITE_WILD, IMG_KYANITE_MUSEUM),
        "la-brea-tar-pits-shop" to listOf(IMG_LA_BREA_TAR_PITS_SHOP),
        "labradorite" to listOf(IMG_LABRADORITE, IMG_LABRADORITE_ROUGH_2, IMG_LABRADORITE_WILD_NEW, IMG_LABRADORITE_MUSEUM, IMG_LABRADORITE_CABOCHON, IMG_LABRADORITE_PEACOCK, IMG_LABRADORITE_GOLDEN),
        "lake-george" to listOf(IMG_LAKE_GEORGE, IMG_LAKE_GEORGE_WILD, IMG_LAKE_GEORGE_MUSEUM, IMG_LAKE_GEORGE_CAB),
        "lake-superior-agate" to listOf(IMG_LAKE_SUPERIOR_AGATE, IMG_LAKE_SUPERIOR_AGATE_WILD, IMG_LAKE_SUPERIOR_AGATE_MUSEUM, IMG_LAKE_SUPERIOR_AGATE_CABOCHON),
        "lapis-lazuli" to listOf(IMG_LAPIS_LAZULI, IMG_LAPIS_ROUGH, IMG_LAPIS_ROUGH_2, IMG_LAPIS_WILD, IMG_LAPIS_MUSEUM, IMG_LAPIS_CABOCHON, IMG_LAPIS_CABOCHON_2),
        "lazulite" to listOf(IMG_LAZULITE, IMG_LAZULITE_WILD, IMG_LAZULITE_MUSEUM),
        "lepidocrocite" to listOf(IMG_LEPIDOCROCITE, IMG_LEPIDOCROCITE_WILD, IMG_LEPIDOCROCITE_MUSEUM),
        "lepidodendron" to listOf(IMG_LEPIDODENDRON, IMG_LEPIDODENDRON_WILD, IMG_ALIVE_LEPIDODENDRON),
        "lepidolite" to listOf(IMG_LEPIDOLITE_ROUGH_CHUNK, IMG_LEPIDOLITE_ROUGH_HAND, IMG_LEPIDOLITE, IMG_LEPIDOLITE_WILD, IMG_LEPIDOLITE_MUSEUM),
        "lepidolite-botryoidal" to listOf(IMG_LEPIDOLITE_BOTRYOIDAL),
        "limestone" to listOf(IMG_LIMESTONE_MAIN_NEW, IMG_LIMESTONE_WILD, IMG_LIMESTONE_MUSEUM),
        "linarite" to listOf(IMG_LINARITE, IMG_LINARITE_WILD, IMG_LINARITE_MUSEUM),
        "lingula" to listOf(IMG_LINGULA, IMG_LINGULA_VAR),
        "llano-uplift" to listOf(IMG_LLANO_UPLIFT),
        "magnesite" to listOf(IMG_MAGNESITE, IMG_MAGNESITE_WILD, IMG_MAGNESITE_MUSEUM),
        "magnetite" to listOf(IMG_MAGNETITE, IMG_MAGNETITE_WILD, IMG_MAGNETITE_MUSEUM),
        "main-street-rocks" to listOf(IMG_MAIN_STREET_ROCKS),
        "main-street-rocks-clawson" to listOf(IMG_MAIN_STREET_ROCKS_CLAWSON),
        "malachite" to listOf(IMG_MALACHITE, IMG_MALACHITE_ROUGH_2, IMG_MALACHITE_WILD, IMG_MALACHITE_MUSEUM, IMG_MALACHITE_CABOCHON),
        "malacholla" to listOf(IMG_MALACHOLLA, IMG_MALACHOLLA_WILD, IMG_MALACHOLLA_MUSEUM),
        "mammoth-tooth" to listOf(IMG_MAMMOTH_TOOTH, IMG_MAMMOTH_TOOTH_WILD, IMG_MAMMOTH_TOOTH_MUSEUM),
        "marble" to listOf(IMG_MARBLE, IMG_MARBLE_PINK, IMG_MARBLE_GREEN, IMG_MARBLE_WILD, IMG_MARBLE_MUSEUM),
        "marquette-yooper" to listOf(IMG_MARQUETTE_YOOPER),
        "mclain-yooper" to listOf(IMG_YOOPERLITE_MCLAIN),
        "megalodon-tooth" to listOf(IMG_MEGALODON_TOOTH, IMG_MEGALODON_TOOTH_WILD, IMG_MEGALODON_TOOTH_MUSEUM),
        "mesolite" to listOf(IMG_MESOLITE, IMG_MESOLITE_WILD, IMG_MESOLITE_MUSEUM),
        "migmatite" to listOf(IMG_MIGMATITE, IMG_MIGMATITE_WILD, IMG_MIGMATITE_MUSEUM),
        "mimetite" to listOf(IMG_MIMETITE, IMG_MIMETITE_MUSEUM),
        "molybdenite" to listOf(IMG_MOLYBDENITE, IMG_MOLYBDENITE_WILD, IMG_MOLYBDENITE_MUSEUM),
        "moonstone" to listOf(IMG_MOONSTONE, IMG_MOONSTONE_ROUGH_2, IMG_MOONSTONE_WILD, IMG_MOONSTONE_MUSEUM, IMG_MOONSTONE_CABOCHON),
        "morefield-mine" to listOf(IMG_MOREFIELD_MINE),
        "morganite" to listOf(IMG_MORGANITE, IMG_MORGANITE_WILD, IMG_MORGANITE_MUSEUM),
        "mosasaur-tooth" to listOf(IMG_MOSASAUR_TOOTH, IMG_MOSASAUR_TOOTH_WILD, IMG_MOSASAUR_TOOTH_MUSEUM),
        "moss-agate" to listOf(IMG_MOSS_AGATE, IMG_MOSS_AGATE_WILD, IMG_MOSS_AGATE_MUSEUM, IMG_MOSS_AGATE_CABOCHON),
        "mottramite" to listOf(IMG_MOTTRAMITE, IMG_MOTTRAMITE_WILD, IMG_MOTTRAMITE_MUSEUM),
        "mount-apatite" to listOf(IMG_MOUNT_APATITE, IMG_MOUNT_APATITE_WILD, IMG_MOUNT_APATITE_MUSEUM, IMG_MOUNT_APATITE_CAB),
        "munising-yooper" to listOf(IMG_YOOPERLITE_MUNISING),
        "muscovite" to listOf(IMG_MUSCOVITE, IMG_MUSCOVITE_WILD, IMG_MUSCOVITE_MUSEUM),
        "musgravite" to listOf(IMG_MUSGRAVITE, IMG_MUSGRAVITE_WILD, IMG_MUSGRAVITE_MUSEUM),
        "muskallonge-yooper" to listOf(IMG_YOOPERLITE_MUSKALLONGE),
        "mylonite" to listOf(IMG_MYLONITE, IMG_MYLONITE_WILD, IMG_MYLONITE_MUSEUM),
        "mohawkite" to listOf(IMG_MOHAWKITE, IMG_MOHAWKITE_WILD, IMG_MOHAWKITE_MUSEUM),
        "native-copper" to listOf(IMG_NATIVE_COPPER, IMG_NATIVE_COPPER_WILD, IMG_NATIVE_COPPER_MUSEUM),
        "native-gold" to listOf(IMG_NATIVE_GOLD, IMG_NATIVE_GOLD_WILD, IMG_NATIVE_GOLD_MUSEUM),
        "native-silver" to listOf(IMG_NATIVE_SILVER, IMG_NATIVE_SILVER_WILD, IMG_NATIVE_SILVER_MUSEUM),
        "native-sulfur" to listOf(IMG_NATIVE_SULFUR, IMG_NATIVE_SULFUR_WILD, IMG_NATIVE_SULFUR_MUSEUM),
        "natrolite" to listOf(IMG_NATROLITE, IMG_NATROLITE_WILD, IMG_NATROLITE_MUSEUM),
        "nautiloid" to listOf(IMG_NAUTILOID, IMG_NAUTILOID_WILD, IMG_NAUTILOID_MUSEUM),
        "neptunite" to listOf(IMG_NEPTUNITE, IMG_NEPTUNITE_WILD, IMG_NEPTUNITE_MUSEUM),
        "nethers-farm" to listOf(IMG_NETHERS_FARM),
        "new-mexico-rock-shop" to listOf(IMG_NEW_MEXICO_ROCK_SHOP),
        "norite" to listOf(IMG_NORITE, IMG_NORITE_WILD),
        "novaculite" to listOf(IMG_NOVACULITE, IMG_NOVACULITE_WILD, IMG_NOVACULITE_MUSEUM),
        "nummulites" to listOf(IMG_NUMMULITES, IMG_NUMMULITES_VAR),
        "oceanview-mine" to listOf(IMG_OCEANVIEW_MINE),
        "okenite" to listOf(IMG_OKENITE, IMG_OKENITE_WILD, IMG_OKENITE_MUSEUM),
        "olivenite" to listOf(IMG_OLIVENITE, IMG_OLIVENITE_WILD, IMG_OLIVENITE_MUSEUM),
        "onyx" to listOf(IMG_ONYX, IMG_ONYX_WILD, IMG_ONYX_MUSEUM, IMG_ONYX_CABOCHON),
        "oregon-petrified" to listOf(IMG_OREGON_PETRIFIED),
        "oregon-rock-n-gem" to listOf(IMG_OREGON_ROCK_N_GEM),
        "orthoceras" to listOf(IMG_ORTHOCERAS, IMG_ORTHOCERAS_WILD, IMG_ORTHOCERAS_MUSEUM),
        "orthoclase" to listOf(IMG_ORTHOCLASE, IMG_ORTHOCLASE_WILD, IMG_ORTHOCLASE_MUSEUM),
        "painite" to listOf(IMG_PAINITE, IMG_PAINITE_WILD, IMG_PAINITE_MUSEUM),
        "pargasite" to listOf(IMG_PARGASITE, IMG_PARGASITE_WILD, IMG_PARGASITE_MUSEUM),
        "pegmatite" to listOf(IMG_PEGMATITE, IMG_PEGMATITE_WILD, IMG_PEGMATITE_MUSEUM),
        "peridot" to listOf(IMG_PERIDOT, IMG_PERIDOT_ROUGH_2, IMG_PERIDOT_WILD, IMG_PERIDOT_MUSEUM, IMG_PERIDOT_CABOCHON),
        "peridot-beach" to listOf(IMG_PERIDOT_BEACH),
        "pectolite" to listOf(IMG_PECTOLITE, IMG_PECTOLITE_WILD, IMG_PECTOLITE_MUSEUM, IMG_PECTOLITE_CABOCHON),
        "pectolite-fibrous" to listOf(IMG_PECTOLITE_FIBROUGH),
        "pectolite-schizolite" to listOf(IMG_PECTOLITE_SCHIZOLITE_MUSEUM, IMG_PECTOLITE_SCHIZOLITE),
        "pectolite-massive" to listOf(IMG_PECTOLITE_MASSIVE_MUSEUM, IMG_PECTOLITE_MASSIVE),
        "pectolite-green" to listOf(IMG_PECTOLITE_GREEN_MUSEUM, IMG_PECTOLITE_GREEN),
        "petoskey-hunting" to listOf(IMG_PETOSKEY_BEACH),
        "petrified-wood" to listOf(IMG_PETRIFIED_WOOD_GENERIC_DESERT, IMG_PETRIFIED_WOOD_WILD, IMG_PETRIFIED_WOOD_MUSEUM, IMG_PETRIFIED_WOOD_CABOCHON, IMG_PETRIFIED_BLUE_FOREST, IMG_PETRIFIED_RAINBOW, IMG_PETRIFIED_ARIZONA, IMG_PETRIFIED_OREGON, IMG_PETRIFIED_OPALIZED),
        "pezzottaite" to listOf(IMG_PEZZOTTAITE, IMG_PEZZOTTAITE_WILD, IMG_PEZZOTTAITE_MUSEUM),
        "phenakite" to listOf(IMG_PHENAKITE, IMG_PHENAKITE_WILD, IMG_PHENAKITE_MUSEUM),
        "phlogopite" to listOf(IMG_PHLOGOPITE, IMG_PHLOGOPITE_WILD, IMG_PHLOGOPITE_MUSEUM),
        "phosphosiderite" to listOf(IMG_PHOSPHOSIDERITE, IMG_PHOSPHOSIDERITE_WILD, IMG_PHOSPHOSIDERITE_MUSEUM),
        "pietersite" to listOf(IMG_PIETERSITE, IMG_PIETERSITE_WILD, IMG_PIETERSITE_CABOCHON),
        "plesiosaur-tooth" to listOf(IMG_PLESIOSAUR_TOOTH, IMG_PLESIOSAUR_TOOTH_VAR),
        "pliosaur-tooth" to listOf(IMG_PLIOSAUR_TOOTH, IMG_PLIOSAUR_TOOTH_VAR),
        "plume-agate" to listOf(IMG_PLUME_AGATE, IMG_PLUME_AGATE_WILD, IMG_PLUME_AGATE_MUSEUM, IMG_PLUME_AGATE_CABOCHON),
        "poudretteite" to listOf(IMG_POUDRETTEITE, IMG_POUDRETTEITE_WILD, IMG_POUDRETTEITE_MUSEUM),
        "prasiolite" to listOf(IMG_PRASIOLITE, IMG_PRASIOLITE_WILD, IMG_PRASIOLITE_MUSEUM),
        "prehnite" to listOf(IMG_PREHNITE, IMG_PREHNITE_WILD, IMG_PREHNITE_MUSEUM, IMG_PREHNITE_CABOCHON),
        "productus" to listOf(IMG_PRODUCTUS, IMG_PRODUCTUS_VAR),
        "psilomelane" to listOf(IMG_PSILOMELANE, IMG_PSILOMELANE_WILD),
        "ptychodus" to listOf(IMG_PTYCHODUS, IMG_PTYCHODUS_VAR),
        "pumice" to listOf(IMG_PUMICE, IMG_PUMICE_WILD, IMG_PUMICE_MUSEUM),
        "pumpellyite" to listOf(IMG_PUMPELLYITE, IMG_PUMPELLYITE_WILD, IMG_PUMPELLYITE_MUSEUM),
        "purpurite" to listOf(IMG_PURPURITE, IMG_PURPURITE_WILD, IMG_PURPURITE_MUSEUM, IMG_PURPURITE_CABOCHON),
        "purple-aragonite" to listOf(IMG_PURPLE_ARAGONITE),
        "pyrite" to listOf(IMG_PYRITE, IMG_PYRITE_WILD, IMG_PYRITE_MUSEUM),
        "pyrolusite" to listOf(IMG_PYROLUSITE, IMG_PYROLUSITE_WILD, IMG_PYROLUSITE_MUSEUM),
        "pyromorphite" to listOf(IMG_PYROMORPHITE, IMG_PYROMORPHITE_WILD, IMG_PYROMORPHITE_MUSEUM),
        "quartzite" to listOf(IMG_QUARTZITE, IMG_QUARTZITE_WILD, IMG_QUARTZITE_MUSEUM),
        "rainbow-ridge" to listOf(IMG_RAINBOW_RIDGE),
        "realgar" to listOf(IMG_REALGAR, IMG_REALGAR_WILD, IMG_REALGAR_MUSEUM),
        "rhodochrosite" to listOf(IMG_RHODOCHROSITE, IMG_RHODOCHROSITE_ROUGH_2, IMG_RHODOCHROSITE_WILD, IMG_RHODOCHROSITE_MUSEUM, IMG_RHODOCHROSITE_CABOCHON),
        "rhodochrosite-tailings" to listOf(IMG_RHODOCHROSITE_TAILINGS),
        "rhodonite" to listOf(IMG_RHODONITE, IMG_RHODONITE_ROUGH_2, IMG_RHODONITE_WILD, IMG_RHODONITE_MUSEUM, IMG_RHODONITE_CABOCHON),
        "rhyolite" to listOf(IMG_RHYOLITE, IMG_RHYOLITE_WILD, IMG_RHYOLITE_MUSEUM),
        "riebeckite" to listOf(IMG_RIEBECKITE, IMG_RIEBECKITE_WILD),
        "rock-elk" to listOf(IMG_ROCK_ELK),
        "rock-shop-maggie" to listOf(IMG_ROCK_SHOP_MAGGIE),
        "rockhound-park" to listOf(IMG_ROCKHOUND_PARK),
        "rontonda" to listOf(IMG_RONTONDA),
        "rose-quartz" to listOf(IMG_ROSE_QUARTZ, IMG_ROSE_QUARTZ_WILD, IMG_ROSE_QUARTZ_MUSEUM, IMG_ROSE_QUARTZ_CABOCHON),
        "royal-peacock" to listOf(IMG_ROYAL_PEACOCK, IMG_ROYAL_PEACOCK_WILD, IMG_ROYAL_PEACOCK_MUSEUM, IMG_ROYAL_PEACOCK_CAB),
        "rugose-coral" to listOf(IMG_RUGOSE_CORAL, IMG_RUGOSE_CORAL_WILD, IMG_RUGOSE_CORAL_MUSEUM),
        "rutilated-quartz" to listOf(IMG_RUTILATED_QUARTZ, IMG_RUTILATED_QUARTZ_WILD, IMG_RUTILATED_QUARTZ_MUSEUM),
        "rutile" to listOf(IMG_RUTILE, IMG_RUTILE_WILD, IMG_RUTILE_MUSEUM),
        "rutile-harrison" to listOf(IMG_RUTILE_HARRISON, IMG_RUTILE_HARRISON_WILD, IMG_RUTILE_HARRISON_MUSEUM),
        "san-carlos" to listOf(IMG_SAN_CARLOS, IMG_SAN_CARLOS_WILD, IMG_SAN_CARLOS_MUSEUM, IMG_SAN_CARLOS_CAB),
        "sandstone" to listOf(IMG_SANDSTONE, IMG_SANDSTONE_WILD, IMG_SANDSTONE_MUSEUM),
        "sardonyx" to listOf(IMG_SARDONYX_SPECIMEN_NEW, IMG_SARDONYX_CABOCHON_NEW)
    ) }

    private val urlChunk3: Map<String, List<String>> by lazy { mapOf(
        "scaphites" to listOf(IMG_SCAPHITES, IMG_SCAPHITES_WILD, IMG_SCAPHITES_MUSEUM),
        "scheelite" to listOf(IMG_SCHEELITE, IMG_SCHEELITE_WILD, IMG_SCHEELITE_MUSEUM),
        "schist" to listOf(IMG_SCHIST, IMG_SCHIST_WILD),
        "scoria" to listOf(IMG_SCORIA, IMG_SCORIA_WILD, IMG_SCORIA_MUSEUM),
        "sea-urchin-fossil" to listOf(IMG_SEA_URCHIN_FOSSIL, IMG_SEA_URCHIN_WILD, IMG_SEA_URCHIN_MUSEUM),
        "sepiolite" to listOf(IMG_SEPIOLITE, IMG_SEPIOLITE_WILD, IMG_SEPIOLITE_MUSEUM),
        "serpentine" to listOf(IMG_SERPENTINE, IMG_SERPENTINE_WILD, IMG_SERPENTINE_MUSEUM, IMG_SERPENTINE_CABOCHON),
        "shale" to listOf(IMG_SHALE, IMG_SHALE_WILD, IMG_SHALE_MUSEUM),
        "shattuckite" to listOf(IMG_SHATTUCKITE_NEW, IMG_SHATTUCKITE, IMG_SHATTUCKITE_WILD, IMG_SHATTUCKITE_CABOCHON),
        "sheep-creek" to listOf(IMG_SHEEP_CREEK),
        "shortite" to listOf(IMG_SHORTITE, IMG_SHORTITE_WILD, IMG_SHORTITE_MUSEUM),
        "siderite" to listOf(IMG_SIDERITE, IMG_SIDERITE_WILD, IMG_SIDERITE_MUSEUM),
        "sillimanite" to listOf(IMG_SILLIMANITE, IMG_SILLIMANITE_WILD, IMG_SILLIMANITE_MUSEUM),
        "siltstone" to listOf(IMG_SILTSTONE, IMG_SILTSTONE_WILD, IMG_SILTSTONE_MUSEUM),
        "skarn" to listOf(IMG_SKARN, IMG_SKARN_WILD, IMG_SKARN_MUSEUM),
        "slate" to listOf(IMG_SLATE, IMG_SLATE_GREEN, IMG_SLATE_PURPLE, IMG_SLATE_WILD, IMG_SLATE_MUSEUM),
        "smilodon-tooth" to listOf(IMG_SMILODON_TOOTH, IMG_SMILODON_TOOTH_WILD, IMG_SMILODON_TOOTH_MUSEUM),
        "smithsonite" to listOf(IMG_SMITHSONITE, IMG_SMITHSONITE_WILD, IMG_SMITHSONITE_MUSEUM, IMG_SMITHSONITE_CABOCHON),
        "smoky-quartz" to listOf(IMG_SMOKY_QUARTZ, IMG_SMOKY_QUARTZ_WILD, IMG_SMOKY_QUARTZ_MUSEUM, IMG_SMOKY_QUARTZ_CABOCHON),
        "soapstone" to listOf(IMG_SOAPSTONE, IMG_SOAPSTONE_WILD, IMG_SOAPSTONE_MUSEUM),
        "sodalite" to listOf(IMG_SODALITE, IMG_SODALITE_WILD, IMG_SODALITE_MUSEUM, IMG_SODALITE_CABOCHON),
        "spectrolite" to listOf(IMG_SPECTROLITE, IMG_SPECTROLITE_WILD, IMG_SPECTROLITE_CABOCHON),
        "spencer-opal" to listOf(IMG_SPENCER_OPAL, IMG_SPENCER_OPAL_WILD, IMG_SPENCER_OPAL_MUSEUM, IMG_SPENCER_OPAL_CAB),
        "sphalerite" to listOf(IMG_SPHALERITE, IMG_SPHALERITE_BROWN, IMG_SPHALERITE_HONEY, IMG_SPHALERITE_RUBY, IMG_SPHALERITE_GREEN, IMG_SPHALERITE_WILD, IMG_SPHALERITE_MUSEUM),
        "spinel" to listOf(IMG_SPINEL, IMG_SPINEL_WILD, IMG_SPINEL_MUSEUM),
        "spirifer" to listOf(IMG_SPIRIFER, IMG_SPIRIFER_WILD),
        "starfish-fossil" to listOf(IMG_STARFISH_FOSSIL, IMG_STARFISH_WILD),
        "staurolite" to listOf(IMG_STAUROLITE, IMG_STAUROLITE_WILD),
        "sterling-hill" to listOf(IMG_STERLING_HILL),
        "stewart-mine" to listOf(IMG_STEWART_MINE),
        "stibnite" to listOf(IMG_STIBNITE, IMG_STIBNITE_WILD, IMG_STIBNITE_MUSEUM),
        "stilbite" to listOf(IMG_STILBITE, IMG_STILBITE_WILD, IMG_STILBITE_MUSEUM),
        "stingray-barb" to listOf(IMG_STINGRAY_BARB, IMG_STINGRAY_BARB_WILD, IMG_STINGRAY_BARB_MUSEUM),
        "stromatolite" to listOf(IMG_STROMATOLITE, IMG_STROMATOLITE_WILD, IMG_STROMATOLITE_MUSEUM, IMG_ALIVE_STROMATOLITE),
        "stromatoporoid" to listOf(IMG_STROMATOPOROID, IMG_STROMATOPOROID_WILD, IMG_STROMATOPOROID_MUSEUM),
        "strontianite" to listOf(IMG_STRONTIANITE, IMG_STRONTIANITE_WILD, IMG_STRONTIANITE_MUSEUM),
        "sugilite" to listOf(IMG_SUGILITE, IMG_SUGILITE_ROUGH_2, IMG_SUGILITE_WILD, IMG_SUGILITE_MUSEUM, IMG_SUGILITE_CABOCHON),
        "sunstone" to listOf(IMG_SUNSTONE, IMG_SUNSTONE_ROUGH_2, IMG_SUNSTONE_WILD, IMG_SUNSTONE_MUSEUM, IMG_SUNSTONE_CABOCHON),
        "sunstone-plush" to listOf(IMG_SUNSTONE_PLUSH, IMG_SUNSTONE_PLUSH_WILD, IMG_SUNSTONE_PLUSH_MUSEUM, IMG_SUNSTONE_PLUSH_CAB),
        "sweet-home" to listOf(IMG_SWEET_HOME, IMG_SWEET_HOME_WILD, IMG_SWEET_HOME_MUSEUM, IMG_SWEET_HOME_CAB),
        "sylvite" to listOf(IMG_SYLVITE, IMG_SYLVITE_WILD, IMG_SYLVITE_MUSEUM),
        "taaffeite" to listOf(IMG_TAAFFEITE, IMG_TAAFFEITE_WILD),
        "talc" to listOf(IMG_TALC, IMG_TALC_WILD),
        "tanzanite" to listOf(IMG_TANZANITE, IMG_TANZANITE_WILD, IMG_TANZANITE_MUSEUM, IMG_TANZANITE_CABOCHON),
        "terlingua" to listOf(IMG_TERLINGUA, IMG_TERLINGUA_WILD, IMG_TERLINGUA_MUSEUM),
        "thomsonite" to listOf(IMG_THOMSONITE, IMG_THOMSONITE_WILD, IMG_THOMSONITE_CABOCHON),
        "topaz" to listOf(IMG_TOPAZ, IMG_TOPAZ_BLUE, IMG_TOPAZ_PINK, IMG_TOPAZ_COLORLESS, IMG_TOPAZ_IMPERIAL, IMG_TOPAZ_WILD, IMG_TOPAZ_MUSEUM),
        "topaz-mountain" to listOf(IMG_TOPAZ_MOUNTAIN),
        "torbernite" to listOf(IMG_TORBERNITE, IMG_TORBERNITE_WILD, IMG_TORBERNITE_MUSEUM),
        "tourmalinated-quartz" to listOf(IMG_TOURMALINATED_QUARTZ, IMG_TOURMALINATED_QUARTZ_WILD, IMG_TOURMALINATED_QUARTZ_MUSEUM),
        "tourmaline" to listOf(IMG_TOURMALINE, IMG_TOURMALINE_GREEN, IMG_TOURMALINE_BLUE, IMG_TOURMALINE_BLACK, IMG_TOURMALINE_WATERMELON, IMG_TOURMALINE_WILD, IMG_TOURMALINE_MUSEUM, IMG_TOURMALINE_CABOCHON),
        "tourmaline-queen" to listOf(IMG_TOURMALINE_QUEEN, IMG_TOURMALINE_QUEEN_WILD, IMG_TOURMALINE_QUEEN_MUSEUM, IMG_TOURMALINE_QUEEN_CAB),
        "travertine" to listOf(IMG_TRAVERTINE, IMG_TRAVERTINE_WILD, IMG_TRAVERTINE_MUSEUM),
        "tremolite" to listOf(IMG_TREMOLITE, IMG_TREMOLITE_WILD, IMG_TREMOLITE_MUSEUM),
        "trilobite" to listOf(IMG_TRILOBITE, IMG_TRILOBITE_WILD, IMG_TRILOBITE_MUSEUM),
        "trinity-agates" to listOf(IMG_TRINITY_AGATES, IMG_TRINITY_AGATE_WILD, IMG_TRINITY_AGATE_MUSEUM, IMG_TRINITY_AGATE_CAB),
        "tucson-mineral-dealers" to listOf(IMG_TUCSON_MINERAL_DEALERS),
        "tuff" to listOf(IMG_TUFF, IMG_TUFF_WILD, IMG_TUFF_MUSEUM),
        "turquoise" to listOf(IMG_TURQUOISE, IMG_TURQUOISE_GREEN_BLUE, IMG_TURQUOISE_APPLE_GREEN, IMG_TURQUOISE_ROUGH_2, IMG_TURQUOISE_WILD, IMG_TURQUOISE_MUSEUM, IMG_TURQUOISE_CABOCHON),
        "ulexite" to listOf(IMG_ULEXITE, IMG_ULEXITE_WILD, IMG_ULEXITE_MUSEUM),
        "v-rock-shop" to listOf(IMG_V_ROCK_SHOP),
        "vanadinite-new" to listOf(IMG_VANADINITE_NEW, IMG_VANADINITE_WILD, IMG_VANADINITE_MUSEUM),
        "variscite" to listOf(IMG_VARISCITE, IMG_VARISCITE_WILD, IMG_VARISCITE_MUSEUM, IMG_VARISCITE_CABOCHON),
        "vesuvianite" to listOf(IMG_VESUVIANITE, IMG_VESUVIANITE_WILD, IMG_VESUVIANITE_MUSEUM),
        "vivianite" to listOf(IMG_VIVIANITE, IMG_VIVIANITE_WILD, IMG_VIVIANITE_MUSEUM),
        "wacke" to listOf(IMG_WACKE, IMG_WACKE_WILD, IMG_WACKE_MUSEUM),
        "wavellite" to listOf(IMG_WAVELLITE_GREEN_SPHERE_REPLICA, IMG_WAVELLITE_BLUE_GREEN_CLUSTER_REPLICA, IMG_WAVELLITE_BOTRYOIDAL_REPLICA, IMG_WAVELLITE_NEW, IMG_WAVELLITE, IMG_WAVELLITE_HALF_SPHERE, IMG_WAVELLITE_COLORS, IMG_WAVELLITE_WILD, IMG_WAVELLITE_MUSEUM),
        "wegner-quartz" to listOf(IMG_WEGNER_QUARTZ, IMG_WEGNER_QUARTZ_WILD, IMG_WEGNER_QUARTZ_MUSEUM),
        "whitefish-yooper" to listOf(IMG_YOOPERLITE_WHITEFISH),
        "willemite" to listOf(IMG_FLUOR_WILLEMITE_NAT, IMG_WILLEMITE_WILD, IMG_WILLEMITE_MUSEUM),
        "williams-quarry" to listOf(IMG_WILLIAMS_QUARRY),
        "witherite" to listOf(IMG_WITHERITE, IMG_WITHERITE_WILD, IMG_WITHERITE_MUSEUM),
        "wolframite" to listOf(IMG_WOLFRAMITE, IMG_WOLFRAMITE_WILD, IMG_WOLFRAMITE_MUSEUM),
        "wulfenite-new" to listOf(IMG_WULFENITE_NEW, IMG_WULFENITE_WILD, IMG_WULFENITE_MUSEUM),
        "xiphactinus" to listOf(IMG_XIPHACTINUS, IMG_XIPHACTINUS_WILD, IMG_XIPHACTINUS_MUSEUM),
        "zircon" to listOf(IMG_ZIRCON, IMG_ZIRCON_WILD, IMG_ZIRCON_MUSEUM),
        "zultanite" to listOf(IMG_ZULTANITE, IMG_ZULTANITE_WILD, IMG_ZULTANITE_MUSEUM),
        "zunyite" to listOf(IMG_ZUNYITE, IMG_ZUNYITE_WILD, IMG_ZUNYITE_MUSEUM),
        "black-opal" to listOf(IMG_BLACK_OPAL, IMG_BLACK_OPAL_VAR),
        "boulder-opal" to listOf(IMG_BOULDER_OPAL, IMG_BOULDER_OPAL_VAR),
        "matrix-opal" to listOf(IMG_MATRIX_OPAL, IMG_MATRIX_OPAL_VAR),
        "blue-opal" to listOf(IMG_BLUE_OPAL, IMG_BLUE_OPAL_VAR),
        "white-opal" to listOf(IMG_COOPER_PEDY_OPAL_NEW, IMG_WHITE_OPAL, IMG_WHITE_OPAL_VAR),
        "crystal-opal" to listOf(IMG_CRYSTAL_OPAL, IMG_CRYSTAL_OPAL_VAR),
        "ethiopian-opal" to listOf(IMG_ETHIOPIAN_OPAL, IMG_ETHIOPIAN_OPAL_VAR),
        "botswana-agate" to listOf(IMG_BOTSWANA_AGATE_SPECIMEN, IMG_BOTSWANA_AGATE_WILD_2),
        "laguna-agate" to listOf(IMG_LAGUNA_AGATE, IMG_LAGUNA_AGATE_VAR),
        "crazy-lace-agate" to listOf(IMG_CRAZY_LACE_AGATE, IMG_CRAZY_LACE_AGATE_VAR),
        "condor-agate" to listOf(IMG_CONDOR_AGATE, IMG_CONDOR_AGATE_VAR),
        "fortification-agate" to listOf(IMG_FORTIFICATION_AGATE, IMG_FORTIFICATION_AGATE_VAR),
        "snowflake-obsidian" to listOf(IMG_SNOWFLAKE_OBSIDIAN, IMG_SNOWFLAKE_OBSIDIAN_WILD, IMG_SNOWFLAKE_OBSIDIAN_MUSEUM),
        "electric-blue-obsidian" to listOf(IMG_ELECTRIC_BLUE_OBSIDIAN, IMG_ELECTRIC_BLUE_OBSIDIAN_VAR),
        "silver-sheen-obsidian" to listOf(IMG_SILVER_SHEEN_OBSIDIAN, IMG_SILVER_SHEEN_WILD, IMG_SILVER_SHEEN_MUSEUM, IMG_SILVER_SHEEN_CAB),
        "golden-sheen-obsidian" to listOf(IMG_GOLDEN_SHEEN_OBSIDIAN, IMG_GOLDEN_SHEEN_OBSIDIAN_VAR),
        "opal" to listOf(IMG_OPAL, IMG_OPAL_BLACK, IMG_OPAL_FIRE, IMG_BLACK_OPAL, IMG_BOULDER_OPAL, IMG_MATRIX_OPAL, IMG_BLUE_OPAL, IMG_WHITE_OPAL, IMG_CRYSTAL_OPAL, IMG_ETHIOPIAN_OPAL, IMG_OPAL_WILD, IMG_OPAL_CABOCHON_NEW),
        "opalite" to listOf(IMG_OPALITE),
        "chalcedony" to listOf(IMG_AGATE, IMG_AGATE_BLUE, IMG_AGATE_GRAY, IMG_AGATE_BROWN, IMG_AGATE_MOSS, IMG_BOTSWANA_AGATE, IMG_LAGUNA_AGATE, IMG_CRAZY_LACE_AGATE, IMG_CONDOR_AGATE, IMG_FORTIFICATION_AGATE, IMG_AGATE_TURRITELLA, IMG_AGATE_BRAZILIAN, IMG_AGATE_THUNDER_EGG, IMG_AGATE_SNAKE_SKIN, IMG_AGATE_TUBE, IMG_AGATE_EYE, IMG_AGATE_ENHYDRO, IMG_AGATE_DRYHEAD, IMG_AGATE_WILD, IMG_AGATE_MUSEUM),
        "ammolite" to listOf(IMG_AMMOLITE, IMG_AMMOLITE_VAR),
        "lake-superior-agate-spec" to listOf(IMG_LAKE_SUPERIOR_AGATE_SPEC, IMG_LAKE_SUPERIOR_AGATE_SPEC_VAR),
        "fairburn-agate" to listOf(IMG_FAIRBURN_AGATE, IMG_FAIRBURN_AGATE_VAR),
        "coyamito-agate" to listOf(IMG_COYAMITO_AGATE, IMG_COYAMITO_AGATE_VAR),
        "polka-dot-agate" to listOf(IMG_POLKA_DOT_AGATE, IMG_POLKA_DOT_AGATE_VAR),
        "iris-agate" to listOf(IMG_IRIS_AGATE, IMG_IRIS_AGATE_VAR),
        "sagenitic-agate" to listOf(IMG_SAGENITIC_AGATE, IMG_SAGENITIC_AGATE_WILD),
        "coldwater-agate" to listOf(IMG_COLDWATER_AGATE, IMG_COLDWATER_AGATE_VAR),
        "obsidian" to listOf(IMG_COMMONS_APACHE_TEARS, IMG_COMMONS_APACHE_TEARS_PERLITE, IMG_OBSIDIAN, IMG_OBSIDIAN_MAHOGANY, IMG_OBSIDIAN_RAINBOW, IMG_SNOWFLAKE_OBSIDIAN, IMG_ELECTRIC_BLUE_OBSIDIAN, IMG_SILVER_SHEEN_OBSIDIAN, IMG_GOLDEN_SHEEN_OBSIDIAN, IMG_MIDNIGHT_LACE_OBSIDIAN, IMG_OBSIDIAN_WILD, IMG_OBSIDIAN_MUSEUM, IMG_OBSIDIAN_CABOCHON),
        "actinolite" to listOf(IMG_ACTINOLITE, IMG_ACTINOLITE_WILD, IMG_ACTINOLITE_MUSEUM),
        "aegirine" to listOf(IMG_AEGIRINE, IMG_AEGIRINE_MUSEUM),
        "albite" to listOf(IMG_ALBITE, IMG_ALBITE_MUSEUM),
        "allanite" to listOf(IMG_ALLANITE, IMG_ALLANITE_WILD, IMG_ALLANITE_MUSEUM),
        "anomalocaris" to listOf(IMG_ANOMALOCARIS, IMG_ANOMALOCARIS_VAR),
        "archaeopteryx" to listOf(IMG_ARCHAEOPTERYX, IMG_ARCHAEOPTERYX_VAR),
        "astrophyllite" to listOf(IMG_ASTROPHYLLITE, IMG_ASTROPHYLLITE_WILD, IMG_ASTROPHYLLITE_MUSEUM),
        "bastnasite" to listOf(IMG_BASTNASITE, IMG_BASTNASITE_WILD, IMG_BASTNASITE_MUSEUM),
        "biotite" to listOf(IMG_BIOTITE, IMG_BIOTITE_WILD, IMG_BIOTITE_MUSEUM),
        "bournonite" to listOf(IMG_BOURNONITE, IMG_BOURNONITE_WILD, IMG_BOURNONITE_MUSEUM),
        "cancrinite" to listOf(IMG_CANCRINITE, IMG_CANCRINITE_VAR),
        "carnallite" to listOf(IMG_CARNALLITE, IMG_CARNALLITE_WILD, IMG_CARNALLITE_MUSEUM),
        "chabazite" to listOf(IMG_CHABAZITE, IMG_CHABAZITE_VAR),
        "chalcocite" to listOf(IMG_CHALCOCITE, IMG_CHALCOCITE_VAR),
        "chalcanthite" to listOf(IMG_CHALCANTHITE, IMG_CHALCANTHITE_VAR),
        "chlorite" to listOf(IMG_CHLORITE, IMG_CHLORITE_WILD),
        "covellite" to listOf(IMG_COVELLITE_MUSEUM, IMG_COVELLITE),
        "cubanite" to listOf(IMG_CUBANITE, IMG_CUBANITE_WILD, IMG_CUBANITE_MUSEUM),
        "dimetrodon" to listOf(IMG_DIMETRODON, IMG_DIMETRODON_VAR),
        "dunkleosteus" to listOf(IMG_DUNKLEOSTEUS, IMG_DUNKLEOSTEUS_VAR),
        "dunite" to listOf(IMG_DUNITE, IMG_DUNITE_WILD, IMG_DUNITE_MUSEUM),
        "eudialyte" to listOf(IMG_EUDIALYTE, IMG_EUDIALYTE_WILD, IMG_EUDIALYTE_MUSEUM, IMG_EUDIALYTE_CAB),
        "fayalite" to listOf(IMG_FAYALITE, IMG_FAYALITE_VAR),
        "fergusonite" to listOf(IMG_FERGUSONITE, IMG_FERGUSONITE_WILD, IMG_FERGUSONITE_MUSEUM),
        "forsterite" to listOf(IMG_FORSTERITE, IMG_FORSTERITE_VAR),
        "franklinite" to listOf(IMG_FRANKLINITE, IMG_FRANKLINITE_VAR),
        "gadolinite" to listOf(IMG_GADOLINITE, IMG_GADOLINITE_VAR),
        "glauconite" to listOf(IMG_GLAUCONITE, IMG_GLAUCONITE_VAR),
        "greenockite" to listOf(IMG_GREENOCKITE, IMG_GREENOCKITE_WILD),
        "halite" to listOf(IMG_HALITE, IMG_HALITE_WILD, IMG_HALITE_MUSEUM),
        "hanksite" to listOf(IMG_HANKSITE, IMG_HANKSITE_WILD, IMG_HANKSITE_MUSEUM),
        "hemimorphite" to listOf(IMG_HEMIMORPHITE, IMG_HEMIMORPHITE_VAR),
        "herderite" to listOf(IMG_HERDERITE, IMG_HERDERITE_WILD, IMG_HERDERITE_MUSEUM),
        "hornblende" to listOf(IMG_HORNBLENDE, IMG_HORNBLENDE_WILD, IMG_HORNBLENDE_MUSEUM),
        "howlite" to listOf(IMG_HOWLITE, IMG_HOWLITE_WILD, IMG_HOWLITE_MUSEUM)
    ) }

    private val urlChunk4: Map<String, List<String>> by lazy { mapOf(
        "ichthyostega" to listOf(IMG_ICHTHYOSTEGA, IMG_ICHTHYOSTEGA_VAR),
        "ilmenite" to listOf(IMG_ILMENITE, IMG_ILMENITE_VAR),
        "kainite" to listOf(IMG_KAINITE, IMG_KAINITE_WILD, IMG_KAINITE_MUSEUM),
        "kaolinite" to listOf(IMG_KAOLINITE, IMG_KAOLINITE_VAR),
        "kernite" to listOf(IMG_KERNITE, IMG_KERNITE_VAR),
        "kimberlite" to listOf(IMG_KIMBERLITE, IMG_KIMBERLITE_WILD, IMG_KIMBERLITE_MUSEUM),
        "leadhillite" to listOf(IMG_LEADHILLITE, IMG_LEADHILLITE_WILD, IMG_LEADHILLITE_MUSEUM),
        "legrandite" to listOf(IMG_LEGRANDITE, IMG_LEGRANDITE_WILD, IMG_LEGRANDITE_MUSEUM),
        "leucite" to listOf(IMG_LEUCITE, IMG_LEUCITE_VAR),
        "limonite" to listOf(IMG_LIMONITE, IMG_LIMONITE_WILD, IMG_LIMONITE_MUSEUM),
        "ludlamite" to listOf(IMG_LUDLAMITE, IMG_LUDLAMITE_WILD, IMG_LUDLAMITE_MUSEUM),
        "manganite" to listOf(IMG_MANGANITE, IMG_MANGANITE_WILD, IMG_MANGANITE_MUSEUM),
        "marcasite" to listOf(IMG_MARCASITE, IMG_MARCASITE_VAR),
        "millerite" to listOf(IMG_MILLERITE, IMG_MILLERITE_VAR),
        "monazite" to listOf(IMG_MONAZITE, IMG_MONAZITE_WILD),
        "nepheline" to listOf(IMG_NEPHELINE, IMG_NEPHELINE_VAR),
        "olivine" to listOf(IMG_OLIVINE, IMG_OLIVINE_WILD, IMG_OLIVINE_MUSEUM),
        "peridotite" to listOf(IMG_PERIDOTITE, IMG_PERIDOTITE_WILD, IMG_PERIDOTITE_MUSEUM),
        "polyhalite" to listOf(IMG_POLYHALITE, IMG_POLYHALITE_VAR),
        "pyroxenite" to listOf(IMG_PYROXENITE, IMG_PYROXENITE_WILD, IMG_PYROXENITE_MUSEUM),
        "scolecite" to listOf(IMG_SCOLECITE, IMG_SCOLECITE_WILD),
        "syenite-fluorescent" to listOf(IMG_FLUOR_SYENITE_NAT, IMG_FLUOR_SYENITE_LW, IMG_FLUOR_SYENITE_MW, IMG_FLUOR_SYENITE_SW),
        "tiktaalik" to listOf(IMG_TIKTAALIK, IMG_TIKTAALIK_WILD, IMG_TIKTAALIK_MUSEUM),
        "troctolite" to listOf(IMG_TROCTOLITE, IMG_TROCTOLITE_WILD, IMG_TROCTOLITE_MUSEUM),
        "wollastonite" to listOf(IMG_WOLLASTONITE, IMG_WOLLASTONITE_WILD, IMG_WOLLASTONITE_MUSEUM),
        "deinotherium" to listOf(IMG_DEINOTHERIUM, IMG_DEINOTHERIUM_VAR),
        "andrewsarchus" to listOf(IMG_ANDREWSARCHUS, IMG_ANDREWSARCHUS_VAR),
        "platybelodon" to listOf(IMG_PLATYBELODON, IMG_PLATYBELODON_VAR),
        "mammoth-tusk" to listOf(IMG_MAMMOTH_TUSK, IMG_MAMMOTH_TUSK_WILD, IMG_MAMMOTH_TUSK_MUSEUM, IMG_MAMMOTH_TUSK_CABOCHON),
        "yooperlite" to listOf(IMG_YOOPERLITE_NORMAL, IMG_YOOPERLITE_NORMAL_2, IMG_YOOPERLITE_UV),
        "iron-meteorite" to listOf(IMG_IRON_METEORITE_FIELD, IMG_IRON_METEORITE_CROSS),
        "chondrite" to listOf(IMG_CHONDRITE_FIELD, IMG_CHONDRITE_CROSS),
        "pallasite" to listOf(IMG_PALLASITE_FIELD, IMG_PALLASITE_CROSS),
        "achondrite" to listOf(IMG_ACHONDRITE, IMG_ACHONDRITE_MUSEUM),
        "carbonaceous-chondrite" to listOf(IMG_CHONDRITE_FIELD, IMG_CARBONACEOUS_CROSS),
        "mesosiderite" to listOf(IMG_MESOSIDERITE, IMG_MESOSIDERITE_VAR),
        "tektite" to listOf(IMG_TEKTITE_ROUGH, IMG_TEKTITE_WILD_NEW, IMG_TEKTITE_MUSEUM_NEW),
        "tektite-tagamite" to listOf(IMG_TEKTITE_TAGAMITE_ROUGH, IMG_TEKTITE_TAGAMITE_WILD, IMG_TEKTITE_TAGAMITE_MUSEUM),
        "azurite-malachite" to listOf(IMG_AZURITE_MALACHITE, IMG_AZURITE_MALACHITE_VAR),
        "amygdaloidal-basalt" to listOf("$Q/6a43aaef-2289-4e6e-823d-502206774e4e.png", "$Q/3697278d-c379-4786-891b-6a2dd5ca8714.png"),
        "copper-ore-assemblage" to listOf(IMG_CHRYSOCOLLA_AZURITE_MALACHITE, IMG_COPPER_ORE_ASSEMBLAGE_VAR),
        "ruby-zoisite" to listOf(IMG_RUBY_ZOISITE, IMG_RUBY_ZOISITE_WILD, IMG_RUBY_ZOISITE_MUSEUM, IMG_RUBY_ZOISITE_CAB),
        "tourmaline-pegmatite" to listOf(IMG_TOURMALINE_QUARTZ_FELDSPAR, IMG_TOURMALINE_PEGMATITE_WILD, IMG_TOURMALINE_PEGMATITE_MUSEUM, IMG_TOURMALINE_PEGMATITE_CAB),
        "galena-sphalerite-pyrite" to listOf(IMG_GALENA_SPHALERITE_PYRITE, IMG_GALENA_SPHALERITE_PYRITE_VAR),
        "quartz" to listOf(IMG_QUARTZ, IMG_QUARTZ_WILD, IMG_QUARTZ_WILD_2, IMG_QUARTZ_MUSEUM, IMG_QUARTZ_CABOCHON, "$Q/9a6317dd-e330-4f91-9e51-0a8443ca95eb.png", "$Q/6beafea5-90b5-4dd8-832e-e5b9e14ff923.png"),
        "fossil-soup" to listOf(IMG_FOSSIL_SOUP, IMG_FOSSIL_SOUP_VAR, IMG_FOSSIL_SOUP_BEACH),
        "natural-pearls" to listOf(IMG_NATURAL_PEARLS, IMG_NATURAL_PEARLS_WILD, IMG_NATURAL_PEARLS_MUSEUM),
        "grape-agate" to listOf(IMG_GRAPE_AGATE, IMG_GRAPE_AGATE_MUSEUM),
        "fordite" to listOf(IMG_FORDITE, IMG_FORDITE_VAR),
        "petrified-wood-blue-forest" to listOf(IMG_PETRIFIED_BLUE_FOREST, IMG_PETRIFIED_BLUE_FOREST_VAR),
        "petrified-wood-rainbow" to listOf(IMG_PETRIFIED_RAINBOW, IMG_PETRIFIED_RAINBOW_WILD),
        "petrified-wood-oregon-green" to listOf(IMG_PETRIFIED_OREGON, IMG_PETRIFIED_OREGON_WILD, IMG_PETRIFIED_OREGON_MUSEUM),
        "petrified-wood-opalized" to listOf(IMG_PETRIFIED_OPALIZED, IMG_PETRIFIED_OPALIZED_VAR),
        "amazonite-smoky-quartz-assemblage" to listOf(IMG_AMAZONITE_SMOKY_QUARTZ, IMG_AMAZONITE_SMOKY_QUARTZ_ROUGH, IMG_AMAZONITE_SMOKY_QUARTZ_MUSEUM, IMG_AMAZONITE_SMOKY_QUARTZ_CAB),
        "fluorite-pyrite-galena-assemblage" to listOf(IMG_FLUORITE_PYRITE_GALENA, IMG_FLUORITE_PYRITE_GALENA_VAR),
        "quartz-chalcopyrite-assemblage" to listOf(IMG_QUARTZ_CHALCOPYRITE, IMG_QUARTZ_CHALCOPYRITE_WILD, IMG_QUARTZ_CHALCOPYRITE_MUSEUM),
        "amethyst-calcite-assemblage" to listOf(IMG_AMETHYST_CALCITE_GEODE, IMG_AMETHYST_CALCITE_CLOSEUP),
        "gold-quartz-assemblage" to listOf(IMG_GOLD_QUARTZ, IMG_GOLD_QUARTZ_VAR),
        "basalt-copper-calcite-assemblage" to listOf(IMG_BASALT_COPPER_CALCITE, IMG_BASALT_COPPER_CALCITE_VAR),
        "chlorastrolite-basalt-assemblage" to listOf(IMG_CHLORASTROLITE_BASALT, IMG_CHLORASTROLITE_BASALT_ASSEMBLAGE_VAR),
        "tyrannosaurus-rex" to listOf(IMG_TREX, IMG_ALIVE_TREX),
        "triceratops-horridus" to listOf(IMG_TRICERATOPS, IMG_ALIVE_TRICERATOPS),
        "brachiosaurus" to listOf(IMG_BRACHIOSAURUS, IMG_ALIVE_BRACHIOSAURUS),
        "velociraptor-mongoliensis" to listOf(IMG_VELOCIRAPTOR, IMG_ALIVE_VELOCIRAPTOR),
        "spinosaurus" to listOf(IMG_SPINOSAURUS_WILD, IMG_SPINOSAURUS_MUSEUM, IMG_ALIVE_SPINOSAURUS),
        "ankylosaurus" to listOf(IMG_ANKYLOSAURUS, IMG_ALIVE_ANKYLOSAURUS),
        "allosaurus" to listOf(IMG_ALLOSAURUS, IMG_ALIVE_ALLOSAURUS),
        "diplodocus" to listOf(IMG_DIPLODOCUS, IMG_ALIVE_DIPLODOCUS),
        "compsognathus" to listOf(IMG_COMPSOGNATHUS, IMG_ALIVE_COMPSOGNATHUS),
        "deinonychus" to listOf(IMG_DEINONYCHUS, IMG_ALIVE_DEINONYCHUS),
        "carnotaurus" to listOf(IMG_CARNOTAURUS_WILD, IMG_CARNOTAURUS_MUSEUM, IMG_ALIVE_CARNOTAURUS),
        "therizinosaurus" to listOf(IMG_THERIZINOSAURUS_WILD, IMG_THERIZINOSAURUS_MUSEUM, IMG_ALIVE_THERIZINOSAURUS),
        "giganotosaurus" to listOf(IMG_GIGANOTOSAURUS_WILD, IMG_GIGANOTOSAURUS_MUSEUM, IMG_ALIVE_GIGANOTOSAURUS),
        "coelophysis" to listOf(IMG_COELOPHYSIS, IMG_ALIVE_COELOPHYSIS),
        "pteranodon-longiceps" to listOf(IMG_PTERANODON, IMG_ALIVE_PTERANODON),
        "quetzalcoatlus" to listOf(IMG_QUETZALCOATLUS_WILD, IMG_QUETZALCOATLUS_MUSEUM, IMG_ALIVE_QUETZALCOATLUS),
        "dimorphodon" to listOf(IMG_DIMORPHODON_WILD, IMG_DIMORPHODON_MUSEUM, IMG_ALIVE_DIMORPHODON),
        "ichthyosaurus" to listOf(IMG_ICHTHYOSAURUS_WILD, IMG_ICHTHYOSAURUS_MUSEUM, IMG_ALIVE_ICHTHYOSAURUS),
        "plesiosaurus" to listOf(IMG_PLESIOSAURUS_WILD, IMG_PLESIOSAURUS_MUSEUM, IMG_ALIVE_PLESIOSAURUS),
        "elasmosaurus" to listOf(IMG_ELASMOSAURUS, IMG_ALIVE_ELASMOSAURUS),
        "liopleurodon" to listOf(IMG_LIOPLEURODON_WILD, IMG_ALIVE_LIOPLEURODON),
        "woolly-mammoth" to listOf(IMG_MAMMOTH_TOOTH, IMG_ALIVE_WOOLLY_MAMMOTH),
        "smilodon" to listOf(IMG_SMILODON_TOOTH, IMG_ALIVE_SMILODON),
        "woolly-rhino" to listOf(IMG_WOOLLY_RHINO_WILD, IMG_WOOLLY_RHINO_MUSEUM, IMG_ALIVE_WOOLLY_RHINO),
        "megaloceros" to listOf(IMG_MEGALOCEROS, IMG_ALIVE_MEGALOCEROS),
        "paraceratherium" to listOf(IMG_PARACERATHERIUM_WILD, IMG_PARACERATHERIUM_MUSEUM, IMG_ALIVE_PARACERATHERIUM),
        "basilosaurus" to listOf(IMG_BASILOSAURUS, IMG_ALIVE_BASILOSAURUS),
        "entelodont" to listOf(IMG_ENTELODONT_WILD, IMG_ENTELODONT_MUSEUM, IMG_ALIVE_ENTELODONT),
        "thylacine" to listOf(IMG_THYLACINE_WILD, IMG_THYLACINE_MUSEUM, IMG_ALIVE_THYLACINE),
        "megalania" to listOf(IMG_MEGALANIA_WILD, IMG_MEGALANIA_MUSEUM, IMG_ALIVE_MEGALANIA),
        "gastornis" to listOf(IMG_GASTORNIS, IMG_ALIVE_GASTORNIS),
        "phorusrhacos" to listOf(IMG_PHORUSRHACOS_WILD, IMG_ALIVE_PHORUSRHACOS),
        "argentavis" to listOf(IMG_ARGENTAVIS_WILD, IMG_ARGENTAVIS_MUSEUM, IMG_ALIVE_ARGENTAVIS),
        "dodo" to listOf(IMG_DODO_WILD, IMG_DODO_MUSEUM, IMG_ALIVE_DODO),
        "moa" to listOf(IMG_MOA_WILD, IMG_MOA_MUSEUM, IMG_ALIVE_MOA),
        "meganeura" to listOf(IMG_MEGANEURA_WILD, IMG_MEGANEURA_MUSEUM, IMG_ALIVE_MEGANEURA),
        "oviraptor" to listOf(IMG_OVIRAPTOR_MUSEUM),
        "arthropleura" to listOf(IMG_ARTHROPLEURA_WILD, IMG_ARTHROPLEURA_MUSEUM, IMG_ALIVE_ARTHROPLEURA),
        "pulmonoscorpius" to listOf(IMG_PULMONOSCORPIUS_WILD, IMG_PULMONOSCORPIUS_MUSEUM, IMG_ALIVE_PULMONOSCORPIUS),
        "megalodon" to listOf(IMG_MEGALODON_TOOTH, IMG_ALIVE_MEGALODON),
        "leedsichthys" to listOf(IMG_LEEDSICHTHYS_WILD, IMG_LEEDSICHTHYS_MUSEUM, IMG_ALIVE_LEEDSICHTHYS),
        "eryops" to listOf(IMG_ERYOPS_WILD, IMG_ALIVE_ERYOPS),
        "diplocaulus" to listOf(IMG_DIPLOCAULUS_WILD, IMG_DIPLOCAULUS_MUSEUM, IMG_ALIVE_DIPLOCAULUS),
        "sigillaria" to listOf(IMG_SIGILLARIA_WILD, IMG_SIGILLARIA_MUSEUM, IMG_ALIVE_SIGILLARIA),
        "cordaites" to listOf(IMG_CORDAITES_WILD, IMG_CORDAITES_MUSEUM, IMG_ALIVE_CORDAITES),
        "ginkgo" to listOf(IMG_ALIVE_GINKGO),
        "foraminifera" to listOf(IMG_FORAMINIFERA_WILD, IMG_FORAMINIFERA_MUSEUM, IMG_ALIVE_FORAMINIFERA),
        "radiolaria" to listOf(IMG_RADIOLARIA_WILD, IMG_RADIOLARIA_MUSEUM, IMG_ALIVE_RADIOLARIA),
        "coccolithophore" to listOf(IMG_COCCOLITHOPHORE_WILD, IMG_ALIVE_COCCOLITHOPHORE),
        "agate-turritella" to listOf(IMG_AGATE_TURRITELLA, IMG_AGATE_TURRITELLA_VAR),
        "agate-brazilian" to listOf(IMG_AGATE_BRAZILIAN, IMG_AGATE_BRAZILIAN_VAR),
        "agate-thunderegg" to listOf(IMG_COMMONS_THUNDER_EGG_PRIDAY, IMG_COMMONS_THUNDER_EGG_QUARTZ, IMG_COMMONS_THUNDER_EGG_FRIEND, IMG_AGATE_THUNDER_EGG, IMG_AGATE_THUNDER_EGG_VAR),
        "agate-snake-skin" to listOf(IMG_AGATE_SNAKE_SKIN, IMG_AGATE_SNAKE_SKIN_VAR),
        "agate-tube" to listOf(IMG_AGATE_TUBE, IMG_AGATE_TUBE_VAR, IMG_AGATE_TUBE_POLISHED),
        "agate-eye" to listOf(IMG_AGATE_EYE, IMG_AGATE_EYE_VAR),
        "agate-enhydro" to listOf(IMG_AGATE_ENHYDRO, IMG_AGATE_ENHYDRO_VAR),
        "agate-dryhead" to listOf(IMG_AGATE_DRYHEAD, IMG_AGATE_DRYHEAD_VAR),
        "jasper-deschutes" to listOf(IMG_JASPER_DESCHUTES, IMG_JASPER_DESCHUTES_WILD, IMG_JASPER_DESCHUTES_MUSEUM, IMG_JASPER_DESCHUTES_CAB),
        "jasper-wild-horse" to listOf(IMG_JASPER_WILD_HORSE, IMG_JASPER_WILD_HORSE_WILD, IMG_JASPER_WILD_HORSE_MUSEUM, IMG_JASPER_WILD_HORSE_CAB),
        "jasper-owyhee" to listOf(IMG_JASPER_OWYHEE, IMG_JASPER_OWYHEE_WILD, IMG_JASPER_OWYHEE_MUSEUM, IMG_JASPER_OWYHEE_CAB),
        "jasper-zebra" to listOf(IMG_JASPER_ZEBRA, IMG_JASPER_ZEBRA_VAR),
        "jasper-spiderweb" to listOf(IMG_JASPER_SPIDERWEB, IMG_JASPER_SPIDERWEB_WILD, IMG_JASPER_SPIDERWEB_MUSEUM, IMG_JASPER_SPIDERWEB_CAB),
        "jasper-autumn" to listOf(IMG_JASPER_AUTUMN, IMG_JASPER_AUTUMN_VAR),
        "jasper-rainforest" to listOf(IMG_JASPER_RAINFOREST, IMG_JASPER_RAINFOREST_WILD, IMG_JASPER_RAINFOREST_MUSEUM, IMG_JASPER_RAINFOREST_CAB),
        "jasper-sunset" to listOf(IMG_JASPER_SUNSET, IMG_JASPER_SUNSET_WILD, IMG_JASPER_SUNSET_MUSEUM, IMG_JASPER_SUNSET_CAB),
        "opal-yowah" to listOf(IMG_OPAL_YOWAH, IMG_OPAL_YOWAH_VAR),
        "opal-koroit" to listOf(IMG_OPAL_KOROIT, IMG_OPAL_KOROIT_VAR),
        "opal-honduran" to listOf(IMG_OPAL_HONDURAN, IMG_OPAL_HONDURAN_VAR),
        "opal-peruvian" to listOf(IMG_OPAL_PERUVIAN, IMG_OPAL_PERUVIAN_VAR),
        "opal-brazilian" to listOf(IMG_OPAL_BRAZILIAN, IMG_OPAL_BRAZILIAN_VAR),
        "opal-mexican-fire" to listOf(IMG_OPAL_MEXICAN, IMG_OPAL_MEXICAN_VAR, IMG_FIRE_OPAL_CAB_VAR)
    ) }

    private val urlChunk5: Map<String, List<String>> by lazy { mapOf(
        "opal-hydrophane" to listOf(IMG_OPAL_HYDROPHANE, IMG_OPAL_HYDROPHANE_WILD, IMG_OPAL_HYDROPHANE_MUSEUM),
        "opal-chocolate" to listOf(IMG_OPAL_CHOCOLATE, IMG_OPAL_CHOCOLATE_WILD, IMG_OPAL_CHOCOLATE_MUSEUM),
        "opal-pink" to listOf(IMG_OPAL_PINK, IMG_OPAL_PINK_WILD, IMG_OPAL_PINK_MUSEUM),
        "opal-common" to listOf(IMG_OPAL_COMMON, IMG_OPAL_COMMON_WILD, IMG_OPAL_COMMON_MUSEUM),
        "granite-orbicular" to listOf(IMG_GRANITE_ORBICULAR, IMG_GRANITE_ORBICULAR_WILD, IMG_GRANITE_ORBICULAR_MUSEUM),
        "granite-porphyritic" to listOf(IMG_GRANITE_PORPHYRITIC, IMG_GRANITE_PORPHYRITIC_WILD, IMG_GRANITE_PORPHYRITIC_MUSEUM),
        "granite-graphic" to listOf(IMG_GRANITE_GRAPHIC, IMG_GRANITE_GRAPHIC_WILD, IMG_GRANITE_GRAPHIC_MUSEUM),
        "granite-rapakivi" to listOf(IMG_GRANITE_RAPAKIVI, IMG_GRANITE_RAPAKIVI_WILD, IMG_GRANITE_RAPAKIVI_MUSEUM),
        "granite-unakite" to listOf(IMG_GRANITE_UNAKITE, IMG_GRANITE_UNAKITE_WILD, IMG_GRANITE_UNAKITE_MUSEUM, IMG_GRANITE_UNAKITE_CAB),
        "petrified-wood-arizona-rainbow" to listOf(IMG_PETRIFIED_ARIZONA_RAINBOW, IMG_PETRIFIED_ARIZONA_RAINBOW_VAR),
        "petrified-wood-washington" to listOf(IMG_PETRIFIED_WASHINGTON, IMG_PETRIFIED_WASHINGTON_WILD, IMG_PETRIFIED_WASHINGTON_MUSEUM),
        "petrified-wood-indonesian" to listOf(IMG_PETRIFIED_INDONESIAN, IMG_PETRIFIED_INDONESIAN_VAR),
        "petrified-wood-argentine" to listOf(IMG_PETRIFIED_ARGENTINE, IMG_PETRIFIED_ARGENTINE_WILD, IMG_PETRIFIED_ARGENTINE_MUSEUM),
        "beryl-red" to listOf(IMG_BERYL_RED, IMG_BERYL_RED_VAR),
        "beryl-golden" to listOf(IMG_BERYL_GOLDEN, IMG_BERYL_GOLDEN_VAR),
        "beryl-maxixe" to listOf(IMG_BERYL_MAXIXE, IMG_BERYL_MAXIXE_VAR),
        "beryl-yellow" to listOf(IMG_BERYL_YELLOW, IMG_BERYL_YELLOW_VAR),
        "feldspar-oligoclase" to listOf("$Q/5fd141cb-107f-4112-b7fc-a2a87553005a.png", IMG_FELDSPAR_OLIGOCLASE_WILD, IMG_FELDSPAR_OLIGOCLASE_MUSEUM, IMG_FELDSPAR_OLIGOCLASE_VAR),
        "feldspar-peristerite" to listOf(IMG_PERISTERITE_NEW, IMG_FELDSPAR_PERISTERITE_VAR, IMG_FELDSPAR_PERISTERITE_WILD, IMG_FELDSPAR_PERISTERITE_MUSEUM),
        "feldspar-larvikite" to listOf("$Q/c29f8268-07b2-47cb-874e-b4550007fd33.png", IMG_LARVIKITE_WILD, IMG_LARVIKITE_MUSEUM, IMG_LARVIKITE_CAB, IMG_FELDSPAR_LARVIKITE_VAR),
        "feldspar-andesine" to listOf("$Q/301a5072-fe18-447a-bb6a-bdf04b3b197a.png", IMG_FELDSPAR_ANDESINE_VAR, IMG_FELDSPAR_ANDESINE_WILD, IMG_FELDSPAR_ANDESINE_MUSEUM, IMG_FELDSPAR_ANDESINE_CAB),
        "feldspar-bytownite" to listOf("$Q/d3341687-47a9-47ce-92fa-ce2f1cdf07cf.png", IMG_FELDSPAR_BYTOWNITE_VAR, IMG_FELDSPAR_BYTOWNITE_WILD, IMG_FELDSPAR_BYTOWNITE_MUSEUM),
        "feldspar-anorthite" to listOf("$Q/dd45894d-c57a-4f66-a365-8e685a923d31.png", IMG_FELDSPAR_ANORTHITE_VAR, IMG_FELDSPAR_ANORTHITE_WILD, IMG_FELDSPAR_ANORTHITE_MUSEUM),
        "corundum-ruby-burma" to listOf("$Q/a2f9b034-69b3-40c7-bd21-3483232b03ff.png", IMG_RUBY_BURMA_WILD, IMG_RUBY_BURMA_MUSEUM, IMG_RUBY_BURMA_FACETED, IMG_CORUNDUM_RUBY_BURMA_VAR),
        "corundum-ruby-mozambique" to listOf("$Q/4ac903ab-3fab-4ef1-858b-30f781c56352.png", IMG_RUBY_MOZAMBIQUE_WILD, IMG_RUBY_MOZAMBIQUE_MUSEUM, IMG_RUBY_MOZAMBIQUE_FACETED, IMG_CORUNDUM_RUBY_MOZAMBIQUE_VAR),
        "corundum-padparadscha" to listOf("$Q/b1d4abec-6100-4ff5-9279-fd9b5c1a0e7b.png", IMG_PADPARADSCHA_WILD, IMG_PADPARADSCHA_MUSEUM, IMG_PADPARADSCHA_FACETED, IMG_CORUNDUM_PADPARADSCHA_VAR),
        "corundum-pink-sapphire" to listOf("$Q/df4eb7ec-bdfa-465d-975b-d12b297eb519.png", IMG_PINK_SAPPHIRE_WILD, IMG_PINK_SAPPHIRE_MUSEUM, IMG_PINK_SAPPHIRE_FACETED, IMG_CORUNDUM_PINK_SAPPHIRE_VAR),
        "corundum-yellow-sapphire" to listOf("$Q/bbf5290b-8a50-4705-9d74-a330074ed775.png", IMG_YELLOW_SAPPHIRE_WILD, IMG_YELLOW_SAPPHIRE_MUSEUM, IMG_YELLOW_SAPPHIRE_FACETED, IMG_CORUNDUM_YELLOW_SAPPHIRE_VAR),
        "corundum-purple-sapphire" to listOf("$Q/a71b0cd3-e83e-486e-9aad-feb0c052f020.png", IMG_PURPLE_SAPPHIRE_WILD, IMG_PURPLE_SAPPHIRE_MUSEUM, IMG_PURPLE_SAPPHIRE_FACETED, IMG_CORUNDUM_PURPLE_SAPPHIRE_VAR),
        "corundum-green-sapphire" to listOf("$Q/b3aec9f7-8ab2-40f5-b6b8-8a7d13026647.png", IMG_GREEN_SAPPHIRE_WILD, IMG_GREEN_SAPPHIRE_MUSEUM, IMG_GREEN_SAPPHIRE_FACETED, IMG_CORUNDUM_GREEN_SAPPHIRE_VAR),
        "corundum-teal-sapphire" to listOf("$Q/ac5dda9a-7712-4bdf-bf95-8417d76a0da7.png", IMG_TEAL_SAPPHIRE_WILD, IMG_TEAL_SAPPHIRE_MUSEUM, IMG_TEAL_SAPPHIRE_FACETED, IMG_CORUNDUM_TEAL_SAPPHIRE_VAR),
        "calcite-iceland-spar" to listOf("$Q/a1ee7d98-4344-4dd1-bcb8-de233a6db66a.png", IMG_CALCITE_ICELAND_WILD, IMG_CALCITE_ICELAND_MUSEUM, IMG_CALCITE_ICELAND_VAR),
        "calcite-dogtooth" to listOf("$Q/4622f1a7-7cf6-4ebc-bd43-80353da73ad6.png", IMG_CALCITE_DOGTOOTH_WILD, IMG_CALCITE_DOGTOOTH_MUSEUM, IMG_CALCITE_DOGTOOTH_VAR),
        "calcite-nailhead" to listOf("$Q/daa508da-0631-4e89-a609-cbf29e3af5bd.png", IMG_CALCITE_NAILHEAD_VAR, IMG_CALCITE_NAILHEAD_POLISH),
        "calcite-cobalt" to listOf("$Q/b28be3b1-01bb-4448-b0fc-644a24336b36.png", IMG_CALCITE_COBALT_VAR, IMG_CALCITE_COBALT_CAB),
        "calcite-mangano" to listOf("$Q/1e7240f0-e3cc-4563-b61e-927537d8c3d9.png", IMG_CALCITE_MANGANO_WILD, IMG_CALCITE_MANGANO_MUSEUM, IMG_CALCITE_MANGANO_CAB, IMG_CALCITE_MANGANO_VAR),
        "fluorite-illinois" to listOf("$Q/32d4969c-b176-4c7f-aac1-bbc466c24494.png", IMG_FLUORITE_ILLINOIS_WILD, IMG_FLUORITE_ILLINOIS_MUSEUM, IMG_FLUORITE_ILLINOIS_VAR),
        "fluorite-pink" to listOf("$Q/f96b5b1c-305a-42db-a2f4-f3860dea8199.png", IMG_FLUORITE_PINK_WILD, IMG_FLUORITE_PINK_MUSEUM, IMG_FLUORITE_PINK_VAR),
        "fluorite-cubic-green" to listOf("$Q/6f2a60de-05a4-45c4-8750-c8733182c2cb.png", IMG_FLUORITE_GREEN_WILD, IMG_FLUORITE_GREEN_MUSEUM, IMG_FLUORITE_CUBIC_GREEN_VAR),
        "spinel-red" to listOf("$Q/8ed67858-6cc2-41ad-b631-114f04f7d065.png"),
        "spinel-blue" to listOf("$Q/0ff9e020-444a-45b8-be82-65e5b4ffb8db.png"),
        "spinel-pink" to listOf("$Q/e2bf0dfd-3e3f-47b6-9f84-9597875b860c.png"),
        "spinel-black" to listOf("$Q/7a6d4fcf-d733-4cf5-9aaf-24f41172822b.png"),
        "pyrrhotite" to listOf("$Q/5b081411-fcb3-493d-96a8-e9c5e4321c9d.png", IMG_PYRRHOTITE_VAR, IMG_PYRRHOTITE_WILD, IMG_PYRRHOTITE_MUSEUM),
        "pentlandite" to listOf("$Q/5044af99-7899-49e5-ab20-03377ccec5cb.png", IMG_PENTLANDITE_VAR, IMG_PENTLANDITE_WILD, IMG_PENTLANDITE_MUSEUM),
        "tetrahedrite" to listOf("$Q/e3a84b39-a4e9-4d26-803c-a7e49dca4324.png"),
        "acanthite" to listOf("$Q/a26790d6-eca9-4630-b165-6bff9ff25a68.png", IMG_ACANTHITE_MUSEUM, IMG_ACANTHITE_WILD, IMG_ACANTHITE_MUSEUM_2),
        "orpiment" to listOf(IMG_ORPIMENT_ROUGH_CLUSTER, IMG_ORPIMENT_ROUGH_MASS, IMG_ORPIMENT_ROUGH_IN_HAND, IMG_ORPIMENT_ROUGH_CLOSEUP),
        "serpentine-antigorite" to listOf("$Q/aae176b8-77cb-4012-84ea-8635ac3b444e.png"),
        "serpentine-chrysotile" to listOf("$Q/6dddc116-887f-4449-8568-9a89d83fadef.png"),
        "jade-omphacite" to listOf("$Q/cd0b6016-0779-4355-94b7-328f888e53b9.png", IMG_JADE_OMPHACITE_VAR, IMG_JADE_OMPHACITE_WILD),
        "turquoise-kingman" to listOf("$Q/068c21a3-d097-4569-abbc-d66016c7f77c.png"),
        "turquoise-persian" to listOf("$Q/e36eb00b-e6c4-4166-9ea0-179b560262ad.png"),
        "igneous-granodiorite" to listOf(IMG_GRANODIORITE_NEW, IMG_IGNEOUS_GRANODIORITE_VAR),
        "igneous-monzonite" to listOf(IMG_MONZONITE, IMG_IGNEOUS_MONZONITE_VAR, IMG_IGNEOUS_MONZONITE_WILD, IMG_IGNEOUS_MONZONITE_MUSEUM),
        "igneous-trachyte" to listOf(IMG_TRACHYTE, IMG_IGNEOUS_TRACHYTE_VAR),
        "sedimentary-dolostone" to listOf(IMG_DOLOSTONE_NEW, IMG_DOLOSTONE_WILD, IMG_DOLOSTONE_MUSEUM),
        "sedimentary-radiolarite" to listOf("$Q/768156a9-7a52-4b1b-a83d-388e22006d7a.png"),
        "oxide-uraninite" to listOf(IMG_URANINITE, IMG_OXIDE_URANINITE_VAR),
        "sulfate-kieserite" to listOf("$Q/4614892e-293a-46ed-9579-3f079ab54876.png"),
        "fossil-pterosaur-bone" to listOf(IMG_PTEROSAUR_BONE_MUSEUM, IMG_PTEROSAUR_BONE_WILD),
        "igneous-aplite" to listOf(IMG_APLITE, IMG_IGNEOUS_APLITE_VAR),
        "igneous-diabase" to listOf(IMG_DIABASE_NEW, IMG_IGNEOUS_DIABASE_VAR),
        "igneous-obsidian-midnight-lace" to listOf(IMG_IGNEOUS_OBSIDIAN_MIDNIGHT_LACE_VAR),
        "sedimentary-oolitic-limestone" to listOf(IMG_LIMESTONE, IMG_OOLITIC_LIMESTONE_WILD, IMG_OOLITIC_LIMESTONE_MUSEUM),
        "oxide-brookite" to listOf(IMG_BROOKITE, IMG_OXIDE_BROOKITE_VAR),
        "carbonate-ankerite" to listOf(IMG_ANKERITE_MUSEUM, IMG_ANKERITE_WILD),
        "fossil-mosasaur-jaw" to listOf(IMG_MOSASAUR_TOOTH, IMG_FOSSIL_MOSASAUR_JAW_VAR),
        "igneous-porphyry" to listOf(IMG_PORPHYRY, IMG_IGNEOUS_PORPHYRY_VAR),
        "sedimentary-chert-nodule" to listOf(IMG_CHERT_NODULE, IMG_CHERT_NODULE_WILD_NEW, IMG_CHERT_NODULE_MUSEUM_NEW),
        "sulfide-enargite" to listOf(IMG_ENARGITE, IMG_ENARGITE_SULF_WILD, IMG_ENARGITE_SULF_MUSEUM),
        "fossil-cephalopod" to listOf(IMG_FOSSIL_CEPHALOPOD_SHELL_REPLICA, IMG_FOSSIL_CEPHALOPOD_VAR),
        "gem-sapphire-star" to listOf(IMG_STAR_SAPPHIRE_BLUE, IMG_STAR_SAPPHIRE_RED, IMG_GEM_SAPPHIRE_STAR_VAR, IMG_STAR_SAPPHIRE_CAB),
        // Felsite — dedicated primary image (was borrowing IMG_RHYOLITE)
        "igneous-felsite" to listOf("$Q/c05730b5-a86d-45a6-92a7-d803d0360422.png", IMG_FELSITE_WILD, IMG_FELSITE_MUSEUM),
        "igneous-pitchstone" to listOf(IMG_PITCHSTONE_NEW, IMG_PITCHSTONE_WILD, IMG_PITCHSTONE_MUSEUM),
        "sedimentary-marl" to listOf(IMG_MARL, IMG_MARL_WILD, IMG_MARL_MUSEUM),
        "sedimentary-oil-shale" to listOf(IMG_OIL_SHALE, IMG_OIL_SHALE_SED_WILD, IMG_OIL_SHALE_SED_MUSEUM),
        "sulfate-thenardite" to listOf(IMG_THENARDITE, IMG_THENARDITE_WILD, IMG_THENARDITE_MUSEUM),
        "fossil-whale-vertebra" to listOf(IMG_FOSSIL_WHALE_VERTEBRA, IMG_FOSSIL_WHALE_VERTEBRA_VAR),
        "meteorite-lunar" to listOf(IMG_LUNAR_METEORITE_MUSEUM, IMG_LUNAR_METEORITE_WILD),
        "sedimentary-lignite" to listOf(IMG_LIGNITE, IMG_LIGNITE_SED_WILD, IMG_LIGNITE_SED_MUSEUM),
        "fossil-horn-coral" to listOf(IMG_RUGOSE_CORAL, IMG_FOSSIL_HORN_CORAL_VAR),
        "fossil-trilobite-enrolled" to listOf(IMG_FOSSIL_TRILOBITE_ENROLLED_VAR),
        "fossil-nautiloid" to listOf(IMG_NAUTILOID, IMG_FOSSIL_NAUTILOID_VAR),
        "igneous-komatiite" to listOf(IMG_KOMATIITE, IMG_IGNEOUS_KOMATIITE_VAR),
        "igneous-pegmatite" to listOf(IMG_PEGMATITE, IMG_IGNEOUS_PEGMATITE_VAR),
        "sedimentary-evaporite" to listOf(IMG_SEDIMENTARY_EVAPORITE_SPECIMEN, IMG_EVAPORITE_HALITE_SPECIMEN, IMG_EVAPORITE_HALITE_MUSEUM),
        "industrial-borax" to listOf(IMG_BORAX, IMG_BORAX_VAR, IMG_BORAX_CLUSTER, IMG_INDUSTRIAL_BORAX_VAR),
        "igneous-carbonatite" to listOf(IMG_CARBONATITE, IMG_IGNEOUS_CARBONATITE_VAR),
        "igneous-peridotite-xenolith" to listOf(IMG_IGNEOUS_PERIDOTITE_XENOLITH_VAR),
        "metamorphic-phyllite" to listOf(IMG_PHYLLITE, IMG_METAMORPHIC_PHYLLITE_VAR),
        "fossil-blastoid" to listOf(IMG_BLASTOID, IMG_FOSSIL_BLASTOID_VAR),
        "chert-mozarkite" to listOf(IMG_MOZARKITE_ROUGH, IMG_MOZARKITE_WILD, IMG_MOZARKITE_MUSEUM, IMG_MOZARKITE_CABOCHON),
        "chert-porcellanite" to listOf(IMG_PORCELLANITE_NEW, IMG_CHERT_PORCELLANITE),
        "chert-tripolitic" to listOf(IMG_CHERT_TRIPOLITIC),
        "igneous-basanite" to listOf(IMG_BASANITE_MUSEUM, IMG_BASANITE_WILD),
        "sedimentary-geode" to listOf(IMG_GEODE_WILD, IMG_GEODE_MUSEUM),
        "metamorphic-blueschist" to listOf(IMG_BLUESCHIST, IMG_METAMORPHIC_BLUESCHIST_VAR),
        "fossil-shark-tooth" to listOf(IMG_GREAT_WHITE_TOOTH, IMG_FOSSIL_SHARK_TOOTH_VAR),
        "fossil-petrified-wood-whole" to listOf(IMG_PETRIFIED_WOOD, IMG_FOSSIL_PETRIFIED_WOOD_WHOLE_VAR),
        "moldavite" to listOf(IMG_MOLDAVITE, IMG_MOLDAVITE_WILD, IMG_MOLDAVITE_MUSEUM, IMG_MOLDAVITE_CABOCHON),
        "libyan-desert-glass" to listOf(IMG_LIBYAN_DESERT_GLASS, IMG_LIBYAN_DESERT_GLASS_VAR),
        "amazing-enhydro-agate" to listOf(IMG_ENHYDRO_AGATE, IMG_ENHYDRO_AGATE_VAR),
        "amazing-enhydro-multi-chamber" to listOf(IMG_ENHYDRO_MULTI, IMG_ENHYDRO_MULTI_VAR),
        "amazing-enhydro-quartz" to listOf(IMG_ENHYDRO_QUARTZ, IMG_ENHYDRO_QUARTZ_VAR),
        "amazing-pseudo-quartz-fluorite" to listOf(IMG_PSEUDO_QZ_FLUORITE, IMG_PSEUDO_QZ_FLUORITE_VAR),
        "amazing-pseudo-goethite-pyrite" to listOf(IMG_PSEUDO_GOETHITE_PY, IMG_PSEUDO_GOETHITE_PY_VAR),
        "amazing-pseudo-malachite-azurite" to listOf(IMG_PSEUDO_MALACHITE_AZ, IMG_PSEUDO_MALACHITE_AZ_VAR),
        "amazing-pseudo-limonite-pyrite" to listOf(IMG_PSEUDO_LIMONITE_PY, IMG_PSEUDO_LIMONITE_PY_VAR),
        "amazing-pseudo-copper-aragonite" to listOf(IMG_PSEUDO_COPPER_ARAG, IMG_PSEUDO_COPPER_ARAG_VAR),
        "amazing-pseudo-opal-wood" to listOf(IMG_PSEUDO_OPAL_WOOD, IMG_PSEUDO_OPAL_WOOD_VAR),
        "amazing-pseudo-serpentine-olivine" to listOf(IMG_PSEUDO_SERP_OLIVINE, IMG_PSEUDO_SERP_OLIVINE_VAR),
        "amazing-petroleum-quartz" to listOf(IMG_PETROLEUM_QUARTZ, IMG_PETROLEUM_QUARTZ_VAR),
        "amazing-petroleum-fluorite" to listOf(IMG_PETROLEUM_FLUORITE, IMG_PETROLEUM_FLUORITE_VAR),
        "amazing-bitumen-calcite" to listOf(IMG_BITUMEN_CALCITE, IMG_BITUMEN_CALCITE_VAR),
        "amazing-hydrocarbon-halite" to listOf(IMG_HYDROCARBON_HALITE, IMG_HYDROCARBON_HALITE_VAR),
        "amazing-chlorite-quartz" to listOf(IMG_CHLORITE_QUARTZ, IMG_CHLORITE_QUARTZ_VAR),
        "amazing-hematite-quartz" to listOf(IMG_FIRE_QUARTZ, IMG_FIRE_QUARTZ_VAR),
        "amazing-actinolite-quartz" to listOf(IMG_THETIS_HAIR, IMG_THETIS_HAIR_VAR),
        "amazing-dumortierite-quartz-new" to listOf(IMG_DUMORTIERITE_QZ_AMAZE, IMG_DUMORTIERITE_QZ_AMAZE_VAR),
        "amazing-fluorescent-willemite" to listOf(IMG_FLUOR_WILLEMITE_SW, IMG_FLUOR_WILLEMITE_LW, IMG_FLUOR_WILLEMITE_MW, IMG_FLUOR_WILLEMITE_NAT),
        "amazing-fluorescent-fluorite" to listOf(IMG_FLUOR_FLUORITE_SW, IMG_FLUOR_FLUORITE_LW, IMG_FLUOR_FLUORITE_MW, IMG_FLUOR_FLUORITE_NAT),
        "amazing-fluorescent-autunite" to listOf(IMG_FLUOR_AUTUNITE_SW, IMG_FLUOR_AUTUNITE_LW, IMG_FLUOR_AUTUNITE_MW, IMG_FLUOR_AUTUNITE_NAT),
        "amazing-phosphorescent-calcite" to listOf(IMG_PHOSPHOR_CALCITE_SW, IMG_PHOSPHOR_CALCITE_LW, IMG_PHOSPHOR_CALCITE_MW, IMG_PHOSPHOR_CALCITE_NAT),
        "amazing-fluorescent-scheelite" to listOf(IMG_FLUOR_SCHEELITE_SW, IMG_FLUOR_SCHEELITE_LW, IMG_FLUOR_SCHEELITE_MW, IMG_FLUOR_SCHEELITE_NAT),
        "amazing-fluorescent-sphalerite" to listOf(IMG_FLUOR_SPHALERITE_LW, IMG_FLUOR_SPHALERITE_SW, IMG_FLUOR_SPHALERITE_MW, IMG_FLUOR_SPHALERITE_NAT),
        "amazing-fluorescent-scapolite" to listOf(IMG_FLUOR_SCAPOLITE_LW, IMG_FLUOR_SCAPOLITE_SW, IMG_FLUOR_SCAPOLITE_MW, IMG_FLUOR_SCAPOLITE_NAT),
        "amazing-fluorescent-hackmanite" to listOf(IMG_FLUOR_HACKMANITE_SW, IMG_FLUOR_HACKMANITE_LW, IMG_FLUOR_HACKMANITE_AFTER_UV, IMG_FLUOR_HACKMANITE_NAT, IMG_FLUOR_HACKMANITE_CAB_BEFORE_SUN, IMG_FLUOR_HACKMANITE_CAB_AFTER_SUN),
        "amazing-fluorescent-adamite" to listOf(IMG_FLUOR_ADAMITE_LW, IMG_FLUOR_ADAMITE_SW, IMG_FLUOR_ADAMITE_MW, IMG_FLUOR_ADAMITE_NAT),
        "amazing-fluorescent-syenite-yooperlite" to listOf(IMG_FLUOR_YOOPERLITE_LW, IMG_FLUOR_YOOPERLITE_SW, IMG_FLUOR_YOOPERLITE_MW, IMG_FLUOR_SYENITE_LW, IMG_FLUOR_SYENITE_SW, IMG_FLUOR_SYENITE_MW, IMG_FLUOR_YOOPERLITE_NAT, IMG_FLUOR_SYENITE_NAT),
        "amazing-chatoyant-chrysoberyl" to listOf(IMG_CATSEYE_CHRYSO, IMG_CATSEYE_CHRYSO_CAB),
        // amazing-asteriated-sapphire removed; Star Optics now uses gem-sapphire-star mapping above
        "amazing-iridescent-ammolite" to listOf(IMG_AMMOLITE, IMG_AMMOLITE_VAR, IMG_IRID_AMMOLITE, IMG_IRID_AMMOLITE_VAR),
        "amazing-labradorescence" to listOf(IMG_LABRADORITE_AMAZE, IMG_LABRADORITE_AMAZE_VAR),
        "amazing-fulgurite" to listOf(IMG_FULGURITE_AMAZE, IMG_FULGURITE_AMAZE_VAR),
        "amazing-vivianite-crystals" to listOf(IMG_VIVIANITE_AMAZE, IMG_VIVIANITE_AMAZE_VAR, IMG_VIVIANITE_MUSEUM),
        "amazing-pyrite-sun" to listOf(IMG_PYRITE_SUN, IMG_PYRITE_SUN_VAR),
        "amazing-desert-rose-new" to listOf(IMG_DESERT_ROSE_AMAZE, IMG_DESERT_ROSE_AMAZE_VAR),

        "amazing-zeolite-natrolite" to listOf(IMG_NATROLITE_AMAZE, IMG_NATROLITE_AMAZE_VAR),
        "amazing-cave-pearl" to listOf(IMG_CAVE_PEARL, IMG_CAVE_PEARL_VAR),
        "amazing-leland-blue" to listOf(IMG_LELAND_BLUE, IMG_LELAND_BLUE_WILD, IMG_LELAND_BLUE_CAB, IMG_LELAND_BLUE_BEACH),
        "amazing-slag-blue" to listOf(IMG_SLAG_BLUE, IMG_SLAG_BLUE_CAB),
        "amazing-slag-green" to listOf(IMG_SLAG_GREEN, IMG_SLAG_GREEN_CAB),
        "amazing-slag-purple" to listOf(IMG_SLAG_PURPLE, IMG_SLAG_PURPLE_CAB),
        "amazing-slag-amber" to listOf(IMG_SLAG_AMBER, IMG_SLAG_AMBER_CAB),
        "amazing-iron-furnace-slag" to listOf(IMG_IRON_FURNACE_SLAG, IMG_IRON_FURNACE_SLAG_WILD),
        "amazing-copper-smelting-slag" to listOf(IMG_COPPER_SMELTING_SLAG, IMG_COPPER_SMELTING_SLAG_WILD),
        "amazing-slag-manganese" to listOf(IMG_SLAG_MANGANESE, IMG_SLAG_MANGANESE_WILD),
        "amazing-slag-steel-furnace" to listOf(IMG_SLAG_STEEL_FURNACE, IMG_SLAG_STEEL_FURNACE_WILD),
        // Type-variety copies for the main Specimen Database
        "amazing-actinolite-quartz-tv" to listOf(IMG_THETIS_HAIR, IMG_THETIS_HAIR_VAR),
        "amazing-chlorite-quartz-tv" to listOf(IMG_CHLORITE_QUARTZ, IMG_CHLORITE_QUARTZ_VAR),
        "amazing-dumortierite-quartz-new-tv" to listOf(IMG_DUMORTIERITE_QZ_AMAZE, IMG_DUMORTIERITE_QZ_AMAZE_VAR),
        "amazing-hematite-quartz-tv" to listOf(IMG_FIRE_QUARTZ, IMG_FIRE_QUARTZ_VAR),
        "amazing-bitumen-calcite-tv" to listOf(IMG_BITUMEN_CALCITE, IMG_BITUMEN_CALCITE_VAR),
        "amazing-slag-amber-tv" to listOf(IMG_SLAG_AMBER, IMG_SLAG_AMBER_CAB),
        "amazing-slag-blue-tv" to listOf(IMG_SLAG_BLUE, IMG_SLAG_BLUE_CAB),
        "amazing-copper-smelting-slag-tv" to listOf(IMG_COPPER_SMELTING_SLAG, IMG_COPPER_SMELTING_SLAG_WILD),
        "amazing-slag-green-tv" to listOf(IMG_SLAG_GREEN, IMG_SLAG_GREEN_CAB),
        "amazing-iron-furnace-slag-tv" to listOf(IMG_IRON_FURNACE_SLAG, IMG_IRON_FURNACE_SLAG_WILD),
        "amazing-leland-blue-tv" to listOf(IMG_LELAND_BLUE, IMG_LELAND_BLUE_WILD, IMG_LELAND_BLUE_CAB, IMG_LELAND_BLUE_BEACH),
        "amazing-slag-manganese-tv" to listOf(IMG_SLAG_MANGANESE, IMG_SLAG_MANGANESE_WILD),
        "amazing-slag-purple-tv" to listOf(IMG_SLAG_PURPLE, IMG_SLAG_PURPLE_CAB),
        "amazing-slag-steel-furnace-tv" to listOf(IMG_SLAG_STEEL_FURNACE, IMG_SLAG_STEEL_FURNACE_WILD),
        "amazing-coprolite-trex" to listOf(IMG_COPROLITE_TREX_ROUGH, IMG_COPROLITE_TREX_ANIMAL, IMG_COPROLITE_TREX_VAR),
        "amazing-coprolite-fish" to listOf(IMG_COPROLITE_FISH_ROUGH, IMG_COPROLITE_FISH_ANIMAL, IMG_COPROLITE_FISH_VAR),
        "amazing-coprolite-crocodilian" to listOf(IMG_COPROLITE_CROC_ROUGH, IMG_COPROLITE_CROC_ANIMAL, IMG_COPROLITE_CROC_VAR),
        "amazing-coprolite-shark" to listOf(IMG_COPROLITE_SHARK_ROUGH, IMG_COPROLITE_SHARK_ANIMAL, IMG_COPROLITE_SHARK_VAR),
        "amazing-coprolite-herbivore" to listOf(IMG_COPROLITE_HERB_ROUGH, IMG_COPROLITE_HERB_ANIMAL, IMG_COPROLITE_HERB_VAR),
        "amazing-coprolite-jurassic" to listOf(IMG_COPROLITE_JURASSIC_ROUGH, IMG_COPROLITE_JURASSIC_ANIMAL, IMG_COPROLITE_JURASSIC_VAR),
        "amazing-copper-banded-agate" to listOf(IMG_COPPER_BANDED_AGATE_CUT, IMG_COPPER_BANDED_AGATE_ROUGH),
        "amazing-copper-replacement-agate" to listOf(IMG_COPPER_REPLACEMENT_AGATE_CUT, IMG_COPPER_REPLACEMENT_AGATE_WHOLE),
        "amazing-copper-infused-agate" to listOf(IMG_COPPER_INFUSED_AGATE_CUT, IMG_COPPER_INFUSED_AGATE_ROUGH),
        "amazing-silver-copper-agate" to listOf(IMG_SILVER_COPPER_AGATE_CUT, IMG_SILVER_COPPER_AGATE_ROUGH),
    ) }

    private val urlChunk6: Map<String, List<String>> by lazy { mapOf(
        "barite-rose" to listOf(IMG_BARITE_DESERT_ROSE_NEW),
        "bismuth-native" to listOf("$Q/beb4bf8f-32db-4248-ace2-c226c2f8f998.png", "$Q/1d50bd32-0dbf-48e2-a50c-dc20121efb5a.png", "$Q/b16c5435-7c99-4e91-bf18-a09bc291be93.png"),
        "celestine-blue" to listOf(IMG_CELESTINE_BLUE_ROUGH, IMG_CELESTINE_BLUE_WILD, IMG_CELESTINE_BLUE_MUSEUM),

        "clinohumite" to listOf(IMG_CLINOHUMITE_MUSEUM, IMG_CLINOHUMITE),
        "eosphorite" to listOf(IMG_EOSPHORITE_MUSEUM, IMG_EOSPHORITE),
        "evaporite-halite" to listOf(IMG_EVAPORITE_HALITE_DEDICATED),
        "fossil-crinoid-stem" to listOf(IMG_FOSSIL_CRINOID_STEM_REPLICA),
        "fossil-ichthyosaur-bone" to listOf(IMG_ICHTHYOSAUR_VERTEBRA),
        "garnet-almandine" to listOf(IMG_GARNET_ALMANDINE_SCHIST, IMG_GARNET_ALMANDINE_ROUGH, IMG_GARNET_ALMANDINE_MUSEUM),
        "garnet-andradite" to listOf(IMG_ANDRADITE_NEW, IMG_ANDRADITE_ROUGH, IMG_ANDRADITE_WILD, IMG_ANDRADITE_MUSEUM),
        "garnet-pyrope" to listOf(IMG_PYROPE_ROUGH, IMG_PYROPE_WILD, IMG_PYROPE_MUSEUM),
        "garnet-spessartine" to listOf(IMG_SPESSARTINE, IMG_SPESSARTINE_WILD, IMG_SPESSARTINE_MUSEUM),
        "garnet-tsavorite" to listOf(IMG_TSAVORITE_NEW, IMG_TSAVORITE_WILD, IMG_TSAVORITE_MUSEUM),
        "garnet-uvarovite" to listOf(IMG_UVAROVITE, IMG_UVAROVITE_WILD, IMG_UVAROVITE_MUSEUM),
        "gem-amblygonite" to listOf(IMG_AMBLYGONITE, IMG_AMBLYGONITE_WILD, IMG_AMBLYGONITE_MUSEUM),
        "gem-brazilianite" to listOf(IMG_BRAZILIANITE, IMG_BRAZILIANITE_FACETED, IMG_BRAZILIANITE_MUSEUM),
        // Greenstone-basalt — dedicated 3rd image (was borrowing IMG_BASALT_MUSEUM)
        "greenstone-basalt" to listOf("$Q/4948755b-6b2d-4abd-bc2a-9d5cf257c271.png", "$Q/5e5ceb23-be6b-40f8-9764-168a4ba59b2b.png", "$Q/db969595-857c-47d4-b29f-c7a46b0ba07e.png"),
        "gypsum-desert-rose" to listOf(IMG_GYPSUM_DESERT_ROSE_NEW),
        "halide-cryolite" to listOf(IMG_CRYOLITE, IMG_CRYOLITE_WILD),
        "igneous-dacite" to listOf(IMG_DACITE, IMG_DACITE_WILD, IMG_DACITE_MUSEUM),
        "igneous-lamprophyre" to listOf(IMG_LAMPROPHYRE_MUSEUM, IMG_LAMPROPHYRE_WILD),
        "industrial-vermiculite" to listOf(IMG_VERMICULITE_MUSEUM, IMG_VERMICULITE),
        "meteorite-hexlandrite" to listOf(IMG_HED_ACHONDRITE_ROUGH),
        "mica-fuchsite" to listOf(IMG_FUCHSITE_MICA_ROUGH, IMG_FUCHSITE_MICA_WILD, IMG_FUCHSITE_MICA_MUSEUM, IMG_FUCHSITE_MICA_CABOCHON),
        "native-platinum" to listOf(IMG_NATIVE_PLATINUM_MUSEUM, IMG_NATIVE_PLATINUM),
        "oxide-cassiterite" to listOf(IMG_CASSITERITE_CORNWALL, IMG_CASSITERITE_WILD, IMG_CASSITERITE_MUSEUM),
        "oxide-columbite" to listOf(IMG_COLUMBITE, IMG_COLUMBITE_WILD, IMG_COLUMBITE_MUSEUM),
        "pectolite-larimar" to listOf(IMG_LARIMAR, IMG_LARIMAR_ROUGH_2, IMG_LARIMAR_WILD, IMG_LARIMAR_MUSEUM, IMG_LARIMAR_CABOCHON),
        "pharmacosiderite" to listOf(IMG_PHARMACOSIDERITE_MUSEUM, IMG_PHARMACOSIDERITE),
        "phosphophyllite" to listOf(IMG_PHOSPHOPHYLLITE_MUSEUM, IMG_PHOSPHOPHYLLITE),
        "quartz-aventurine" to listOf(IMG_AVENTURINE, IMG_AVENTURINE_WILD, IMG_AVENTURINE_MUSEUM, IMG_AVENTURINE_CABOCHON),
        "quartz-chalcedony-blue" to listOf(IMG_CHALCEDONY_BLUE, IMG_CHALCEDONY_PINK, IMG_CHALCEDONY_PURPLE, IMG_CHALCEDONY_YELLOW),
        "quartz-chrysoprase" to listOf(IMG_CHRYSOPRASE, IMG_CHRYSOPRASE_WILD, IMG_CHRYSOPRASE_MUSEUM, IMG_CHRYSOPRASE_CABOCHON),
        "quartz-hawk-eye" to listOf(IMG_HAWK_EYE_ROUGH, IMG_HAWK_EYE_WILD, IMG_HAWK_EYE_MUSEUM_NEW),
        "quartz-herkimer" to listOf(IMG_HERKIMER, IMG_HERKIMER_WILD, IMG_HERKIMER_MUSEUM),
        "quartz-milky" to listOf(IMG_MILKY_QUARTZ_MUSEUM, IMG_MILKY_QUARTZ),
        "quartz-rock-crystal" to listOf(IMG_ROCK_CRYSTAL_MUSEUM, IMG_ROCK_CRYSTAL),
        "quartz-sard" to listOf(IMG_SARD_MUSEUM, IMG_SARD_CABOCHON),
        "quartz-tiger-eye" to listOf(IMG_TIGER_EYE_NEW, IMG_TIGER_EYE, IMG_TIGER_EYE_MUSEUM),
        "rainbow-obsidian" to listOf(IMG_OBSIDIAN_RAINBOW, IMG_OBSIDIAN_WILD, IMG_OBSIDIAN_MUSEUM),
        "scorodite" to listOf(IMG_SCORODITE_MUSEUM, IMG_SCORODITE),
        "sedimentary-ironstone" to listOf(IMG_IRONSTONE_ROUGH, IMG_IRONSTONE_WILD, IMG_IRONSTONE_MUSEUM),
        "sedimentary-mudstone" to listOf(IMG_MUDSTONE_MUSEUM, IMG_MUDSTONE_WILD),
        "sedimentary-tillite" to listOf(IMG_TILLITE_NEW, IMG_DIAMICTITE_WILD, IMG_DIAMICTITE_MUSEUM),
        "serpentine-cobra-jasper" to listOf(IMG_COBRA_JASPER_MUSEUM, IMG_COBRA_JASPER),
        "serpentine-lizardite" to listOf(IMG_SERPENTINE_LIZARDITE),
        "spodumene-triphane" to listOf(IMG_TRIPHANE_MUSEUM, IMG_TRIPHANE),
        "thorite" to listOf(IMG_THORITE_MUSEUM, IMG_THORITE),
        "titanite-sphene" to listOf(IMG_SPHENE, IMG_SPHENE_WILD, IMG_SPHENE_MUSEUM, IMG_SPHENE_FACETED),
        "tourmaline-indicolite" to listOf(IMG_INDICOLITE, IMG_INDICOLITE_WILD, IMG_INDICOLITE_MUSEUM),
        "tourmaline-rubellite" to listOf(IMG_RUBELLITE, IMG_RUBELLITE_WILD, IMG_RUBELLITE_MUSEUM),
        "tourmaline-schorl" to listOf(IMG_TOURMALINE_BLACK),
        "tourmaline-verdelite" to listOf(IMG_VERDELITE, IMG_VERDELITE_WILD, IMG_VERDELITE_MUSEUM),
        "tourmaline-watermelon" to listOf(IMG_TOURMALINE_WATERMELON),
        "turquoise-nevada" to listOf(IMG_TURQUOISE_NEVADA_ROUGH, IMG_TURQUOISE_NEVADA_WILD),
        "xenotime-y" to listOf(IMG_XENOTIME, IMG_XENOTIME_WILD, IMG_XENOTIME_MUSEUM),
        "zeolite" to listOf(
            IMG_ZEOLITE_CLEAR_GLASSY_01,
            IMG_ZEOLITE_WHITE_NEEDLES_02,
            IMG_ZEOLITE_GREEN_APOPHYLLITE_03,
            IMG_ZEOLITE_YELLOW_BOTRYOIDAL_04,
            IMG_ZEOLITE_WHITE_FLAT_05,
            IMG_ZEOLITE_BROWN_MATRIX_06,
            IMG_ZEOLITE_PEACH_STILBITE_07,
            IMG_ZEOLITE_GREEN_FAN_08,
            IMG_ZEOLITE_BEIGE_DRUSY_09,
            IMG_ZEOLITE_PURPLE_LAVENDER_10,
            IMG_ZEOLITE_WHITE_FROSTY_11,
            IMG_ZEOLITE_PALE_BLUE_GREEN_12,
            IMG_ZEOLITE_NEEDLE_CLUSTER_REPLICA,
            IMG_ZEOLITE
        ),
        "zircon-brown" to listOf(IMG_ZIRCON_BROWN_MAIN, IMG_ZIRCON_BROWN_ROUGH),
        "onyx-black" to listOf(IMG_BLACK_ONYX_SOLID_ROUGH, IMG_BLACK_ONYX_CABOCHON, IMG_BLACK_ONYX_IN_HAND, IMG_BLACK_ONYX_CAMEO),
        "onyx-nicotino" to listOf(IMG_ONYX_NICOTINO_NEW),
        "onyx-sard" to listOf(IMG_ONYX_SARD_NEW),
        // Gowanda tillite — dedicated 3rd/4th images (were borrowing IMG_WACKE_WILD/IMG_WACKE_MUSEUM)
        "gowanda-tillite" to listOf(IMG_GOWANDA_TILLITE_NEW2, IMG_GOWANDA_TILLITE_NEW, "$Q/6ccd1987-25f0-45e1-8cea-b3c9001f74ef.png", "$Q/fae0f186-ad91-4941-9963-8be2f3ced06e.png"),
        "zoisite-thulite" to listOf(IMG_THULITE, IMG_ZOISITE, IMG_ZOISITE_MUSEUM),
        "agate-ankara" to listOf(IMG_AGATE_ANKARA, IMG_AGATE_ANKARA_WILD),
        "agate-dead-sea" to listOf(IMG_DEAD_SEA_AGATE, IMG_DEAD_SEA_AGATE_WILD),
        "calcite-hexagonal" to listOf(IMG_HEXAGONAL_CALCITE, IMG_HEXAGONAL_CALCITE_WILD, IMG_HEXAGONAL_CALCITE_MUSEUM, IMG_HEXAGONAL_CALCITE_CABOCHON),
        "calcite-squid-game" to listOf(IMG_SQUID_GAME_CALCITE_NAT, IMG_SQUID_GAME_CALCITE_ROUGH, IMG_SQUID_GAME_CALCITE_LW, IMG_SQUID_GAME_CALCITE_SW),
        "urbanite" to listOf(IMG_URBANITE_NEW, IMG_URBANITE_WILD_DEDICATED),
        "goethite-iridescent" to listOf(IMG_IRIDESCENT_TURGITE_GOETHITE_REPLICA, IMG_GOETHITE_IRIDESCENT_NEW, IMG_IRIDESCENT_GOETHITE_NEW),
        "turgite-iridescent" to listOf(IMG_IRIDESCENT_TURGITE_GOETHITE_REPLICA, IMG_TURGITE_IRIDESCENT_NEW, IMG_TURGITE_IRIDESCENT),
        "halite-blue" to listOf(IMG_HALITE_BLUE, IMG_HALITE_BLUE_MUSEUM),
        "halite-pink" to listOf(IMG_HALITE_PINK_SPECIMEN, IMG_HALITE_PINK_MUSEUM),
        "halite-hopper" to listOf(IMG_HALITE_HOPPER, IMG_HALITE_HOPPER_MUSEUM),
        "halite-flowers" to listOf(IMG_HALITE_FLOWERS, IMG_HALITE_FLOWERS_MUSEUM),
        "halite-green" to listOf(IMG_HALITE_GREEN_SPECIMEN, IMG_HALITE_GREEN_MUSEUM),
        "amazing-fossil-soup" to listOf(IMG_FOSSIL_SOUP, IMG_FOSSIL_SOUP_WILD, IMG_FOSSIL_SOUP_MUSEUM),
        "amazing-meteorite-hunting" to listOf(IMG_METEORITE_HUNTING),
        "amazing-squid-game-calcite" to listOf(IMG_SQUID_GAME_CALCITE_SW, IMG_SQUID_GAME_CALCITE_LW, IMG_SQUID_GAME_CALCITE_ROUGH, IMG_SQUID_GAME_CALCITE_NAT),
        "tourmaline-paraiba" to listOf(IMG_TOURMALINE_PARAIBA, IMG_TOURMALINE_PARAIBA_WILD, IMG_TOURMALINE_PARAIBA_MUSEUM),
        "tourmaline-dravite" to listOf(IMG_TOURMALINE_DRAVITE, IMG_TOURMALINE_DRAVITE_WILD, IMG_TOURMALINE_DRAVITE_MUSEUM),
        "tourmaline-liddicoatite" to listOf(IMG_TOURMALINE_LIDDICOATITE_WILD, IMG_TOURMALINE_LIDDICOATITE, IMG_TOURMALINE_LIDDICOATITE_MUSEUM),
        "tourmaline-canary" to listOf(IMG_TOURMALINE_CANARY, IMG_TOURMALINE_CANARY_WILD, IMG_TOURMALINE_CANARY_MUSEUM),
        "tourmaline-cats-eye" to listOf(IMG_TOURMALINE_CATS_EYE, IMG_TOURMALINE_CATS_EYE_WILD, IMG_TOURMALINE_CATS_EYE_MUSEUM),
        "tourmaline-bicolor" to listOf(IMG_TOURMALINE_BICOLOR, IMG_TOURMALINE_BICOLOR_WILD, IMG_TOURMALINE_BICOLOR_MUSEUM),
        "tourmaline-chrome" to listOf(IMG_TOURMALINE_CHROME, IMG_TOURMALINE_CHROME_WILD, IMG_TOURMALINE_CHROME_MUSEUM),
        "tourmaline-achroite" to listOf(IMG_TOURMALINE_ACHROITE),
        "granite-black-galaxy" to listOf(IMG_GRANITE_BLACK_GALAXY, IMG_GRANITE_BLACK_GALAXY_WILD, IMG_GRANITE_BLACK_GALAXY_MUSEUM),
        "granite-blue-pearl" to listOf(IMG_GRANITE_BLUE_PEARL, IMG_GRANITE_BLUE_PEARL_WILD, IMG_GRANITE_BLUE_PEARL_MUSEUM),
        "granite-baltic-brown" to listOf(IMG_GRANITE_BALTIC_BROWN, IMG_GRANITE_BALTIC_BROWN_WILD, IMG_GRANITE_BALTIC_BROWN_MUSEUM),
        "granite-kashmir-white" to listOf(IMG_GRANITE_KASHMIR_WHITE, IMG_GRANITE_KASHMIR_WHITE_WILD, IMG_GRANITE_KASHMIR_WHITE_MUSEUM),
        "granite-tan-brown" to listOf(IMG_GRANITE_TAN_BROWN, IMG_GRANITE_TAN_BROWN_WILD, IMG_GRANITE_TAN_BROWN_MUSEUM),
        "granite-verde-ubatuba" to listOf(IMG_GRANITE_VERDE_UBATUBA, IMG_GRANITE_VERDE_UBATUBA_WILD, IMG_GRANITE_VERDE_UBATUBA_MUSEUM),
        "granite-white-mount-airy" to listOf(IMG_GRANITE_WHITE_MOUNT_AIRY, IMG_GRANITE_WHITE_MOUNT_AIRY_WILD, IMG_GRANITE_WHITE_MOUNT_AIRY_MUSEUM),
        "granite-a-type" to listOf(IMG_GRANITE_A_TYPE, IMG_GRANITE_A_TYPE_WILD, IMG_GRANITE_A_TYPE_MUSEUM),
        "granite-s-type" to listOf(IMG_GRANITE_S_TYPE, IMG_GRANITE_S_TYPE_WILD, IMG_GRANITE_S_TYPE_MUSEUM),
        "granite-i-type" to listOf(IMG_GRANITE_I_TYPE, IMG_GRANITE_I_TYPE_WILD, IMG_GRANITE_I_TYPE_MUSEUM),
        "granite-m-type" to listOf(IMG_GRANITE_M_TYPE, IMG_GRANITE_M_TYPE_WILD, IMG_GRANITE_M_TYPE_MUSEUM),
        "trinitite" to listOf(IMG_TRINITITE, IMG_TRINITITE_WILD, IMG_TRINITITE_MUSEUM),
        "amazing-copper-replacement-agate-tv" to listOf(IMG_COPPER_REPLACEMENT_AGATE_CUT, IMG_COPPER_REPLACEMENT_AGATE_WHOLE),
        "amazing-copper-banded-agate-tv" to listOf(IMG_COPPER_BANDED_AGATE_CUT, IMG_COPPER_BANDED_AGATE_ROUGH),
        "amazing-copper-infused-agate-tv" to listOf(IMG_COPPER_INFUSED_AGATE_CUT, IMG_COPPER_INFUSED_AGATE_ROUGH),
        "amazing-silver-copper-agate-tv" to listOf(IMG_SILVER_COPPER_AGATE_CUT, IMG_SILVER_COPPER_AGATE_ROUGH),
        "amazing-cave-pearl-tv" to listOf(IMG_CAVE_PEARL, IMG_CAVE_PEARL_VAR),
        "amazing-desert-rose-new-tv" to listOf(IMG_DESERT_ROSE_AMAZE, IMG_DESERT_ROSE_AMAZE_VAR),
        "amazing-fulgurite-tv" to listOf(IMG_FULGURITE_AMAZE, IMG_FULGURITE_AMAZE_VAR),
        "amazing-zeolite-natrolite-tv" to listOf(IMG_NATROLITE_AMAZE, IMG_NATROLITE_AMAZE_VAR),
        "amazing-pyrite-sun-tv" to listOf(IMG_PYRITE_SUN, IMG_PYRITE_SUN_VAR),
        "amazing-tenebrescent-sodalite-tv" to listOf(IMG_HACKMANITE, IMG_HACKMANITE_TENEB_VAR),
        "amazing-thunderegg-tv" to listOf(IMG_THUNDEREGG_AMAZE, IMG_THUNDEREGG_AMAZE_VAR),
        "amazing-vivianite-crystals-tv" to listOf(IMG_VIVIANITE_AMAZE, IMG_VIVIANITE_AMAZE_VAR, IMG_VIVIANITE_MUSEUM),
        "amazing-coprolite-crocodilian-tv" to listOf(IMG_COPROLITE_CROC_ROUGH, IMG_COPROLITE_CROC_ANIMAL, IMG_COPROLITE_CROC_VAR),
        "amazing-coprolite-fish-tv" to listOf(IMG_COPROLITE_FISH_ROUGH, IMG_COPROLITE_FISH_ANIMAL, IMG_COPROLITE_FISH_VAR),
        "amazing-coprolite-herbivore-tv" to listOf(IMG_COPROLITE_HERB_ROUGH, IMG_COPROLITE_HERB_ANIMAL, IMG_COPROLITE_HERB_VAR),
        "amazing-coprolite-jurassic-tv" to listOf(IMG_COPROLITE_JURASSIC_ROUGH, IMG_COPROLITE_JURASSIC_ANIMAL, IMG_COPROLITE_JURASSIC_VAR),
        "amazing-coprolite-shark-tv" to listOf(IMG_COPROLITE_SHARK_ROUGH, IMG_COPROLITE_SHARK_ANIMAL, IMG_COPROLITE_SHARK_VAR),
        "amazing-coprolite-trex-tv" to listOf(IMG_COPROLITE_TREX_ROUGH, IMG_COPROLITE_TREX_ANIMAL, IMG_COPROLITE_TREX_VAR),
        "basalt-morb" to listOf(IMG_BASALT_MORB_WILD, IMG_BASALT_MORB_MUSEUM),
        "blue-aventurine" to listOf(IMG_BLUE_AVENTURINE, IMG_BLUE_AVENTURINE_WILD, IMG_BLUE_AVENTURINE_MUSEUM, IMG_BLUE_AVENTURINE_CABOCHON),
        "pearl-saltwater" to listOf(IMG_SALTWATER_PEARL),
        "petrified-wood-palm" to listOf(IMG_PETRIFIED_PALM_WILD, IMG_PETRIFIED_PALM_MUSEUM),
        "petrified-wood-badlands" to listOf(IMG_PETRIFIED_BADLANDS_WILD, IMG_PETRIFIED_BADLANDS_MUSEUM),
        "opal-lightning-ridge" to listOf(IMG_OPAL_LIGHTNING_RIDGE),
        "ammonite-iridescent" to listOf(IMG_AMMONITE_IRIDESCENT_NEW),
        "opal-hyalite" to listOf(IMG_HYALITE_OPAL_NAT, IMG_HYALITE_OPAL_WILD, IMG_HYALITE_OPAL_LW),
        "amazing-fluorescent-hyalite" to listOf(IMG_HYALITE_OPAL_LW, IMG_HYALITE_OPAL_NAT, IMG_HYALITE_OPAL_WILD),
        "banded-iron-assemblage" to listOf(IMG_BIF_OUTCROP, IMG_BIF_HAND_SAMPLE, IMG_BIF_MUSEUM_SLAB),
        "amazing-trapiche-emerald" to listOf(IMG_TRAPICHE_EMERALD, IMG_TRAPICHE_EMERALD_VAR, IMG_TRAPICHE_AMETHYST, IMG_TRAPICHE_COMPANIONS_NEW),
        "amazing-trapiche-emerald-tv" to listOf(IMG_TRAPICHE_EMERALD, IMG_TRAPICHE_EMERALD_VAR, IMG_TRAPICHE_AMETHYST, IMG_TRAPICHE_COMPANIONS_NEW),
        "conglomerate-puddingstone" to listOf(IMG_PUDDINGSTONE_SPECIMEN, IMG_PUDDINGSTONE_WILD, IMG_PUDDINGSTONE_MUSEUM),
        "hematite-martite" to listOf(IMG_MARTITE_MUSEUM, IMG_MARTITE_SPECIMEN, IMG_MARTITE_WILD),
        "amazing-pseudo-hematite-magnetite" to listOf(IMG_MARTITE_SPECIMEN, IMG_MARTITE_WILD, IMG_MARTITE_MUSEUM),
        "quartz-druse" to listOf(IMG_DRUZY_QUARTZ_CORAL, IMG_DRUZY_QUARTZ_JASPER, IMG_DRUZY_QUARTZ_MALACHITE, IMG_DRUZY_QUARTZ_CHALCOPYRITE),
        "amazing-pseudo-druse-quartz" to listOf(IMG_DRUZY_QUARTZ_JASPER, IMG_DRUZY_QUARTZ_CORAL, IMG_DRUZY_QUARTZ_MALACHITE, IMG_DRUZY_QUARTZ_CHALCOPYRITE),
        "amethyst-thunder-bay" to listOf(IMG_THUNDER_BAY_AMETHYST_NAT, IMG_THUNDER_BAY_AMETHYST_WILD, IMG_THUNDER_BAY_AMETHYST_MUSEUM),
        "amazing-thunder-bay-amethyst" to listOf(IMG_THUNDER_BAY_AMETHYST_NAT, IMG_THUNDER_BAY_AMETHYST_WILD, IMG_THUNDER_BAY_AMETHYST_MUSEUM),
        "amethyst-chevron" to listOf(IMG_CHEVRON_AMETHYST, IMG_CHEVRON_AMETHYST_WILD, IMG_CHEVRON_AMETHYST_MUSEUM, IMG_CHEVRON_AMETHYST_CABOCHON),
        "amethyst-vera-cruz" to listOf(IMG_VERA_CRUZ_AMETHYST, IMG_VERA_CRUZ_AMETHYST_WILD, IMG_VERA_CRUZ_AMETHYST_MUSEUM),
        "amethyst-mexican-geode" to listOf(IMG_MEXICAN_AMETHYST_GEODE, IMG_MEXICAN_AMETHYST_GEODE_WILD, IMG_MEXICAN_AMETHYST_GEODE_MUSEUM, IMG_MEXICAN_AMETHYST_GEODE_CABOCHON),
        "fluorite-petroleum" to listOf(IMG_PETROLEUM_FLUORITE, IMG_PETROLEUM_FLUORITE_VAR),
        "quartz-petroleum" to listOf(IMG_PETROLEUM_QUARTZ, IMG_PETROLEUM_QUARTZ_VAR),
        "amethyst-pink" to listOf(IMG_PINK_AMETHYST),
        "fluorite-rainbow" to listOf(IMG_FLUORITE_RAINBOW_NEW),
        "selenite-column" to listOf(IMG_SELENITE_COLUMN),
        "selenite-hourglass" to listOf(IMG_SELENITE_HOURGLASS, IMG_SELENITE_HOURGLASS_WILD, IMG_SELENITE_HOURGLASS_MUSEUM),
        "selenite-satin-spar" to listOf(IMG_SELENITE_SATIN_SPAR),
        "selenite-fishtail" to listOf(IMG_SELENITE_FISHTAIL),
        "chondrodite" to listOf(IMG_CHONDRODITE),
        "edenite" to listOf(IMG_EDENITE),
        "garnierite" to listOf(IMG_GARNIERITE),
        "ludwigite" to listOf(IMG_LUDWIGITE),
        "clinochlore" to listOf(IMG_CLINOCHLORE),
        "delhayelite" to listOf(IMG_DELHAYELITE_ROUGH, IMG_DELHAYELITE_WILD),
        // Lujaurite — dedicated image (was borrowing IMG_GABBRO)
        "lujaurite" to listOf("$Q/dbf3b98c-3a3e-4d1c-a33f-bda3dfcab0ca.png"),
        // Miserite — dedicated image (was borrowing IMG_CHONDRODITE)
        "miserite" to listOf("$Q/aa0d2171-687c-4746-85c8-833d326e3ffa.png"),
        // Pintadoite — dedicated image (was borrowing IMG_CLINOCHLORE)
        "pintadoite" to listOf("$Q/a699e0ca-343f-4265-bd06-bf385563fac1.png"),
        "tagamite" to listOf(IMG_TAGAMITE_ROUGH),
        "vishnevite" to listOf(IMG_VISHNEVITE_NEW),
        "pyroxene-group" to listOf(IMG_PYROXENE_GROUP_ROUGH),
        "amphibole-group" to listOf(IMG_AMPHIBOLE_GROUP),
        "amazing-pseudo-calcite-aragonite" to listOf(IMG_PSEUDO_CALCITE_ARAGONITE),
        "amazing-pseudo-chalcedony-coral" to listOf(IMG_PSEUDO_CHALCEDONY_CORAL),
        "amazing-pyrite-quartz" to listOf(IMG_PYRITE_QUARTZ_INCLUSION),
        "septarian-nodule" to listOf(IMG_SEPTARIAN_NODULE_CUT_1, IMG_SEPTARIAN_ROUGH_NEW, IMG_SEPTARIAN_NODULE, IMG_SEPTARIAN_NODULE_CUT_2, IMG_SEPTARIAN_NODULE_CUT_3, IMG_SEPTARIAN_NODULE_CUT_4),
        "michigan-lightning-stone" to listOf(IMG_MICHIGAN_LIGHTNING_ROUGH_PILE, IMG_MICHIGAN_LIGHTNING_HAND_BEACH, IMG_MICHIGAN_LIGHTNING_CUT_PAIR, IMG_MICHIGAN_LIGHTNING_HEART_CUT, IMG_MICHIGAN_LIGHTNING_STONE, IMG_MICHIGAN_LIGHTNING_STONE_CUT_1, IMG_MICHIGAN_LIGHTNING_STONE_CUT_2, IMG_MICHIGAN_LIGHTNING_STONE_CUT_3, IMG_MICHIGAN_LIGHTNING_STONE_CUT_4),
        "hagstone" to listOf(IMG_HAGSTONE_PALE_GRAY, IMG_HAGSTONE_BEIGE_CREAM, IMG_HAGSTONE_PINK_PITTED, IMG_HAGSTONE_DARK_GRAY, IMG_HAGSTONE_TAN_ROUGH),
        "iron-concretions" to listOf(IMG_IRON_CONCRETION_CRACKED_NODULE, IMG_IRON_CONCRETION_SPLIT_RINGS, IMG_IRON_CONCRETION_DARK_BLOCKY, IMG_IRON_CONCRETION_BROWN_BOTRYOIDAL, IMG_IRON_CONCRETION_REDDISH_LAYERED),
        "fossilized-mud-concretions" to listOf(IMG_FOSSILIZED_MUD_CONCRETION_SPLIT_V2, IMG_FOSSILIZED_MUD_CONCRETION_PYRAMID_V2),
        "amazing-heart-shaped-rock" to listOf(IMG_HEART_SHAPED_ROCK),
        "amazing-concretions" to listOf(IMG_CONCRETIONS_RAA_SEPTARIAN, IMG_CONCRETIONS_RAA_FAIRY_STONE, IMG_CONCRETIONS_RAA_IRON, IMG_CONCRETIONS_RAA_CANNONBALL, IMG_CONCRETIONS_RAA_MOQUI, IMG_CONCRETIONS_RAA_MUD, IMG_CONCRETIONS_RAA_CLAY_IRONSTONE, IMG_CONCRETIONS_RAA_GLACIAL, IMG_CONCRETIONS_RAA_SANDSTONE)
    ) }

    private val urlChunk7: Map<String, List<String>> by lazy { mapOf(
        "amazing-tourmaline-quartz" to listOf(IMG_TOURMALINE_QUARTZ_INCLUSION),
        "amazing-adularescent-moonstone" to listOf(IMG_MOONSTONE_ADULESCENCE),
        "amazing-schiller-lattice-sunstone" to listOf(IMG_RAINBOW_LATTICE_CLOSEUP, IMG_RAINBOW_LATTICE_ROUGH, IMG_RAINBOW_LATTICE_CABOCHON, IMG_RAINBOW_LATTICE_WILD),
        "amazing-play-of-color-opal" to listOf(IMG_OPAL_PLAY_OF_COLOR),
        "amazing-fluorescent-benitoite" to listOf(IMG_FLUOR_BENITOITE_SW, IMG_FLUOR_BENITOITE_NAT, IMG_BENITOITE),
        "amazing-fluorescent-tugtupite" to listOf(IMG_FLUOR_TUGTUPITE_SW, IMG_FLUOR_TUGTUPITE_NAT),
        "rainbow-lattice" to listOf(IMG_RAINBOW_LATTICE_CABOCHON, IMG_RAINBOW_LATTICE_ROUGH, IMG_RAINBOW_LATTICE_CLOSEUP, IMG_RAINBOW_LATTICE_WILD),
        "enhydro-agate-multi-chamber" to listOf(IMG_ENHYDRO_MULTI, IMG_ENHYDRO_MULTI_VAR),
        "halite-hydrocarbon" to listOf(IMG_HYDROCARBON_HALITE, IMG_HYDROCARBON_HALITE_VAR),
        "selenite-great-salt-plains" to listOf(IMG_SELENITE_GREAT_SALT_PLAINS),
        // ── Master build batch: new specimens + regenerated images (2026-07-19) ──
        "tyrannosaurus-rex-tooth" to listOf(IMG_TREX_TOOTH_NEW),
        "spinosaurus-tooth" to listOf(IMG_SPINOSAURUS_TOOTH_NEW),
        "triceratops-tooth" to listOf(IMG_TRICERATOPS_TOOTH_V2),
        "obsidian-mahogany" to listOf(IMG_MAHOGANY_OBSIDIAN_ROUGH, IMG_MAHOGANY_OBSIDIAN_WILD, IMG_MAHOGANY_OBSIDIAN_MUSEUM, IMG_MAHOGANY_OBSIDIAN_CABOCHON),
        "gabbro-indigo" to listOf(IMG_INDIGO_GABBRO_ROUGH, IMG_INDIGO_GABBRO_WILD, IMG_INDIGO_GABBRO_MUSEUM, IMG_INDIGO_GABBRO_CABOCHON),
        "fuchsite-ruby-assemblage" to listOf(IMG_RUBY_FUCHSITE_ROUGH, IMG_RUBY_FUCHSITE_WILD, IMG_RUBY_FUCHSITE_MUSEUM, IMG_RUBY_FUCHSITE_CABOCHON),
        "orpiment-realgar-assemblage" to listOf(IMG_ORPIMENT_REALGAR_ROUGH_ASSEMBLAGE, IMG_ORPIMENT_REALGAR_ROUGH_CLUSTER, IMG_ORPIMENT_REALGAR_ROUGH_IN_HAND, IMG_ORPIMENT_REALGAR_ROUGH_CLOSEUP),
        "fairy-stone-concretions" to listOf(IMG_FAIRY_STONE_DISC, IMG_FAIRY_STONE_HAND, IMG_FAIRY_STONE_CONCRETION_NEW, IMG_CONCRETIONS_RAA_FAIRY_STONE)
    ) }

    // ── Expansion Batch 16-20: Phase 1-5 specimen images (2026-07-22) ──
    private const val IMG_SERAPHINITE_ROUGH = "$Q/55402ac4-8efb-4048-9d2b-7540678c32d7.png"
    private const val IMG_SERAPHINITE_WILD = "$Q/99797cb9-68a3-4df8-8276-f9cdaebd8ffd.png"
    private const val IMG_SERAPHINITE_MUSEUM = "$Q/743da30d-dc97-441a-b825-5ae3113e892f.png"
    private const val IMG_AJOITE_ROUGH = "$Q/c617617d-b46b-404b-a3d8-2beff95c8899.png"
    private const val IMG_AJOITE_WILD = "$Q/4c6766d3-47d3-43f0-abbc-70dee0789c93.png"
    private const val IMG_AJOITE_MUSEUM = "$Q/646dd84b-5a1d-420a-897c-f7915ee8daf6.png"
    private const val IMG_PROUSTITE_ROUGH = "$Q/5372d25b-954f-48a2-be73-c867058dd0b8.png"
    private const val IMG_PROUSTITE_WILD = "$Q/181a6acb-7b3d-4353-b9cf-bc6057d4694b.png"
    private const val IMG_PROUSTITE_MUSEUM = "$Q/fb47bed1-ebc6-4b5d-9161-089a73030f23.png"
    private const val IMG_POLYBASITE_ROUGH = "$Q/2722c2a5-eec6-423b-a887-2415f9e4ddc9.png"
    private const val IMG_POLYBASITE_WILD = "$Q/69c1c6b0-0169-4c01-a1e5-6eaa7a43d9a1.png"
    private const val IMG_POLYBASITE_MUSEUM = "$Q/967f2791-5954-4278-a57a-724b72564cc2.png"
    private const val IMG_STEPHANITE_ROUGH = "$Q/8523ecf6-792f-4657-96b1-5a6beaf82c5a.png"
    private const val IMG_STEPHANITE_WILD = "$Q/17d4772a-f4e0-4509-bf14-38ac9f255bee.png"
    private const val IMG_STEPHANITE_MUSEUM = "$Q/f1c80d62-4055-4b1f-917c-ebfc042cf50e.png"
    private const val IMG_DYSCRASITE_ROUGH = "$Q/ed431f8e-c7eb-4521-96ee-e8a7aceafc57.png"
    private const val IMG_DYSCRASITE_WILD = "$Q/2a9a5b99-c765-4bbd-a1f5-ef7b87b59694.png"
    private const val IMG_DYSCRASITE_MUSEUM = "$Q/bcf26066-2401-4e19-96b7-8e6a133e9c9b.png"
    private const val IMG_NICKELINE_ROUGH = "$Q/950d30ae-548b-42d4-8b0e-6898687550b6.png"
    private const val IMG_NICKELINE_WILD = "$Q/43a68c64-4148-4e49-aca1-d74abaf21657.png"
    private const val IMG_NICKELINE_MUSEUM = "$Q/f65440ef-932d-46ea-b0d6-1600d720cbaa.png"
    private const val IMG_TENNANTITE_ROUGH = "$Q/f51b596a-8e10-4be3-b961-9d9fcfc06b9d.png"
    private const val IMG_TENNANTITE_WILD = "$Q/5359cf43-e5d3-4ec5-ad77-d85606bd627f.png"
    private const val IMG_TENNANTITE_MUSEUM = "$Q/8e41f556-0c51-44d3-98d1-a20fe857b149.png"
    private const val IMG_ALABANDITE_ROUGH = "$Q/01eb7981-4710-4241-a6b2-03d1319f8710.png"
    private const val IMG_ALABANDITE_WILD = "$Q/c4436607-4c08-4d28-b960-07d1855d2f0d.png"
    private const val IMG_ALABANDITE_MUSEUM = "$Q/92f44e99-49ca-41ca-b5ce-d1cd17861145.png"
    private const val IMG_EB16_CUBANITE_ROUGH = "$Q/6cae3aab-d052-4d93-95b3-6cc2b68973d8.png"
    private const val IMG_EB16_BOURNONITE_ROUGH = "$Q/f40c0dee-c2ae-4ab7-8b93-126a54123b2d.png"
    private const val IMG_EB16_BOULANGERITE_ROUGH = "$Q/446f353b-07d7-4527-a72b-b5ff2bf2949c.png"
    private const val IMG_EB16_BOULANGERITE_WILD = "$Q/9c07ec92-78cd-4ed3-a31c-75a30e70b48e.png"
    private const val IMG_EB16_BOULANGERITE_MUSEUM = "$Q/ed876370-a2a9-483d-a48a-7b6f0d3b0bed.png"
    private const val IMG_EB16_MIARGYRITE_ROUGH = "$Q/411107bb-916b-41ee-89c0-df18a15983b7.png"
    private const val IMG_EB16_MIARGYRITE_WILD = "$Q/df08f146-582c-4bf4-a506-94d7e59a85e8.png"
    private const val IMG_EB16_MIARGYRITE_MUSEUM = "$Q/22f83fb4-3fb2-431a-b826-a4a3c7ad3217.png"
    private const val IMG_EB16_GREENOCKITE_ROUGH = "$Q/480cc10a-1711-4b97-9929-6e2e67451892.png"
    private const val IMG_EB16_GREENOCKITE_MUSEUM = "$Q/8516a752-3f60-45b3-a86b-fc48c856c72a.png"
    private const val IMG_EB16_PEROVSKITE_ROUGH = "$Q/3c1de9b2-5479-43ca-a4d5-276c7bbb495f.png"
    private const val IMG_EB16_PEROVSKITE_WILD = "$Q/5a11026e-973e-41f2-aafe-3565e1da843f.png"
    private const val IMG_EB16_PEROVSKITE_MUSEUM = "$Q/dd837e52-d9f4-4987-8d0f-2bbfc363e798.png"
    private const val IMG_EB16_ZINCITE_ROUGH = "$Q/4feca8d8-35a7-430a-9999-72e6d30e751b.png"
    private const val IMG_EB16_ZINCITE_WILD = "$Q/97e20f07-7227-4a59-9ced-d857216b063c.png"
    private const val IMG_EB16_ZINCITE_MUSEUM = "$Q/2342bc62-271d-4bb5-a34c-44d0a8964815.png"
    private const val IMG_EB16_GAHNITE_ROUGH = "$Q/f896f3e0-cc3c-4c0c-b586-46299fa0c377.png"
    private const val IMG_HERCYNITE_ROUGH = "$Q/5241fed2-331a-419b-bde4-b8d4e486003c.png"
    private const val IMG_EB16_TANTALITE_ROUGH = "$Q/13475430-8773-4896-8fd0-2b6e95dd78cc.png"
    private const val IMG_COLTAN_ROUGH = "$Q/7631ec16-ee4a-4b09-8772-b6e1330b411b.png"
    private const val IMG_EB16_TANTALITE_WILD = "$Q/81d6fcf3-3003-47bb-9473-af280eced37a.png"
    private const val IMG_EB16_TANTALITE_MUSEUM = "$Q/f351ed73-5a7f-4b02-b775-f8bb389b00d7.png"
    private const val IMG_EB16_BIXBYITE_ROUGH = "$Q/d09c18dc-0f79-4ef3-af21-421a0ccf2820.png"
    private const val IMG_EB16_BIXBYITE_WILD = "$Q/75744919-8f78-4adc-bcb3-0cc1f8116fa0.png"
    private const val IMG_EB16_BIXBYITE_MUSEUM = "$Q/5da75b21-9980-4167-83d6-c8a6f0e3ee7c.png"
    private const val IMG_EB16_BADDELEYITE_ROUGH = "$Q/510a25f9-58d1-40f3-b766-29ab032d823d.png"
    private const val IMG_EB16_BADDELEYITE_WILD = "$Q/93af153a-e22e-4971-bc55-86b704ba4738.png"
    private const val IMG_EB16_BADDELEYITE_MUSEUM = "$Q/1acfe48b-8383-4506-84dd-d76d11d90c53.png"
    private const val IMG_EB16_MINIUM_ROUGH = "$Q/dc7b7e20-69a9-44ee-9b97-ef9b59068d88.png"
    private const val IMG_STIBICONITE_ROUGH = "$Q/f4a0053c-d9f4-4d32-86e5-cb2f319d688c.png"
    private const val IMG_EB16_ATACAMITE_ROUGH = "$Q/a5430865-133d-489e-8d22-9314de2a183f.png"
    private const val IMG_EB16_ATACAMITE_WILD = "$Q/86bd26c6-8750-45bf-9f38-ad58fb02e87f.png"
    private const val IMG_EB16_ATACAMITE_MUSEUM = "$Q/fdfb6b19-e40e-4087-bdd8-46a32ebeb87b.png"
    private const val IMG_EB16_VILLIAUMITE_ROUGH = "$Q/f0ce8b9f-74c0-4ebd-9716-8e020b3b5c0d.png"
    private const val IMG_EB16_VILLIAUMITE_WILD = "$Q/a36a32e3-9d52-46c4-b0e4-4ba2dca4ef21.png"
    private const val IMG_EB16_VILLIAUMITE_MUSEUM = "$Q/8f8ce9d9-83a4-46a6-b4ac-4f226a39e7b6.png"
    private const val IMG_EB16_PACHNOLITE_ROUGH = "$Q/0abb8845-01b7-4a2c-9e68-50d296f9c671.png"
    private const val IMG_EB16_PACHNOLITE_WILD = "$Q/2e55233a-b842-4d69-a282-34319870bfb6.png"
    private const val IMG_EB16_PACHNOLITE_MUSEUM = "$Q/340f1c6d-4415-440d-a10d-01c9b0f352c0.png"
    private const val IMG_EB16_MENDIPITE_ROUGH = "$Q/ba398422-5530-46de-8076-2e2927233a25.png"
    private const val IMG_EB16_PHOSGENITE_ROUGH = "$Q/4425df03-80d1-4fa7-819a-4e64b6eebd91.png"
    private const val IMG_EB16_PHOSGENITE_WILD = "$Q/1913605f-c26c-4a8a-acb9-5ed2bd63a7e4.png"
    private const val IMG_EB16_PHOSGENITE_MUSEUM = "$Q/b227b723-cec4-4ad3-83a4-215b78df0739.png"
    private const val IMG_EB16_KUTNOHORITE_ROUGH = "$Q/ef3018e7-4d75-4c93-9556-b35c6de51497.png"
    private const val IMG_EB16_KUTNOHORITE_WILD = "$Q/fcbc7c85-0607-4e62-a1b1-e14810323308.png"
    private const val IMG_EB16_KUTNOHORITE_MUSEUM = "$Q/4b8e8f62-bb39-4efb-aab0-dc35d570f37f.png"
    private const val IMG_EB16_BENSTONITE_ROUGH = "$Q/bd312e90-7b38-4f33-8340-24b1e28b81b9.png"
    private const val IMG_EB16_CAVANSITE_ROUGH = "$Q/b1ce1a18-8e5e-4474-9fa2-b45ecfa2bff9.png"
    private const val IMG_EB16_CAVANSITE_WILD = "$Q/f9bf9814-e557-4380-8176-6fc700c3dd9d.png"
    private const val IMG_EB16_CAVANSITE_MUSEUM = "$Q/44a53e9a-3276-42fd-9496-4ceefe968185.png"
    private const val IMG_EB16_LIBETHENITE_ROUGH = "$Q/9dc907fb-dee7-440c-9794-dada3fac1783.png"
    private const val IMG_EB16_LIBETHENITE_WILD = "$Q/35c04488-2c4f-48f8-ac4c-c892f768bdb8.png"
    private const val IMG_EB16_LIBETHENITE_MUSEUM = "$Q/e977996e-159a-4915-983d-bde4498afcac.png"
    private const val IMG_EB16_CLINOZOISITE_ROUGH = "$Q/75fbe9b4-81e0-4c4f-b725-edffc2071593.png"
    private const val IMG_EB16_CLINOZOISITE_WILD = "$Q/fe7f002f-5843-4a3e-8404-0e39ea832ee6.png"
    private const val IMG_EB16_CLINOZOISITE_MUSEUM = "$Q/25eb0ac5-0040-4b5d-bcd4-3fe33e1faef2.png"
    private const val IMG_EB16_BRUCITE_ROUGH = "$Q/4b059626-08ee-48d2-8350-56d5e98920a9.png"
    private const val IMG_EB16_BRUCITE_WILD = "$Q/8c96fffd-01e7-4a32-98d1-bdbd58ceef45.png"
    private const val IMG_EB16_BRUCITE_MUSEUM = "$Q/3bb58fde-7a3a-4a26-b2f6-c56a6a6dbd0f.png"
    private const val IMG_EB16_KINOITE_ROUGH = "$Q/af2d7fd2-723c-438e-9381-5ae0e26ba07b.png"
    private const val IMG_EB16_HEDENBERGITE_ROUGH = "$Q/fdf646bc-baed-4e2c-9227-0df5a520a4a7.png"
    private const val IMG_EB16_HEDENBERGITE_WILD = "$Q/6ccd7b3c-6644-4b5f-a218-d0864983b766.png"
    private const val IMG_EB16_HEDENBERGITE_MUSEUM = "$Q/da4b087b-c8ed-46ca-8ffb-30f37c07f45b.png"
    private const val IMG_EB16_LAZURITE_ROUGH = "$Q/26770a39-e9ba-4f19-bcb2-a18d46039206.png"
    private const val IMG_EB16_LAZURITE_WILD = "$Q/c4371797-f7ab-42de-bf88-8cba93f77076.png"
    private const val IMG_EB16_LAZURITE_MUSEUM = "$Q/c8c1c83b-86f4-449d-acbf-09d08c3c2d89.png"
    private const val IMG_EB16_HESSONITE_ROUGH = "$Q/d0077b90-30e2-4484-8e3e-16c9e6a8eab5.png"
    private const val IMG_EB16_HESSONITE_CAB = "$Q/beab5207-fb38-4b7e-b334-ceb82be3a98e.png"
    private const val IMG_EB16_IMPERIAL_TOPAZ_ROUGH = "$Q/db3d8af4-dccb-4be6-8822-1fe13b6a0469.png"
    private const val IMG_EB16_IMPERIAL_TOPAZ_CAB = "$Q/d1c4628a-19c1-4273-b078-b181c73bd219.png"
    private const val IMG_EB16_MELANITE_ROUGH = "$Q/d4524ee6-c230-493e-bee5-bb3458f2a71d.png"
    private const val IMG_EB16_MELANITE_WILD = "$Q/fdf18f34-0f48-413b-892c-cc6e2a217de0.png"
    private const val IMG_EB16_MELANITE_MUSEUM = "$Q/92141ad0-f4b4-44ac-9411-0a4c3b263144.png"
    private const val IMG_EB16_BLUE_TOPAZ_ROUGH = "$Q/006a97cd-21d8-47b7-9025-3618930bbf3a.png"
    private const val IMG_EB16_BLUE_TOPAZ_CAB = "$Q/d25745d6-efda-46ea-a203-4d27d7b75170.png"
    private const val IMG_EB16_APACHE_TEARS_ROUGH = "$Q/89962392-99c6-462b-85bd-166bde34a5d8.png"
    private const val IMG_EB16_APACHE_TEARS_WILD = "$Q/70027d91-7418-4d0e-a64b-4ba3e17687d7.png"
    private const val IMG_EB16_APACHE_TEARS_MUSEUM = "$Q/b7c4e865-6e1d-49a8-a398-94d38a430f99.png"
    private const val IMG_EB16_ANGELITE_ROUGH = "$Q/cf9638eb-5caf-4e33-b5d2-a643e7cdf62b.png"
    private const val IMG_EB16_ANGELITE_WILD = "$Q/ee9023ec-1aab-4007-ab26-ef4133ce6752.png"
    private const val IMG_EB16_SHUNGITE_ROUGH = "$Q/af7b5b74-e2d1-4ae9-9459-df0aa0b5fc6d.png"
    private const val IMG_EB16_SHUNGITE_CAB = "$Q/9cab6cef-e033-4cf3-94e4-1af01885ae09.png"
    private const val IMG_EB16_BUMBLEBEE_ROUGH = "$Q/c827251e-07e3-447b-8198-8e9448974d12.png"
    private const val IMG_EB16_BUMBLEBEE_CAB = "$Q/92398669-d693-440c-b6c0-f23c1addd413.png"
    private const val IMG_EB16_TIGER_IRON_ROUGH = "$Q/e7fb7513-8c27-4da0-8c6e-390c34081f60.png"
    private const val IMG_EB16_TIGER_IRON_CAB = "$Q/c3031dfb-2792-423b-aa90-771321dba65a.png"
    private const val IMG_EB16_WILD_HORSE_ROUGH = "$Q/b3a965c9-24d6-41d2-b410-39ca36e61b83.png"
    private const val IMG_EB16_WILD_HORSE_CAB = "$Q/438e25b4-8926-4a42-a3cd-0a2986ab402d.png"
    private const val IMG_EB16_CHROME_DIOPSIDE_ROUGH = "$Q/2d35296a-6876-4d9c-a929-0ae222592904.png"
    private const val IMG_EB16_CHROME_DIOPSIDE_CAB = "$Q/233a38cb-714f-47b1-8110-efae0294eeaa.png"

    // ── Expansion Batch 17: Phase 2 specimen images ──
    private const val IMG_EB17_QUARTZ_BRANDBERG_ROUGH = "$Q/69bf3be1-f815-4abc-b372-7277e9d24126.png"
    private const val IMG_EB17_QUARTZ_BRANDBERG_WILD = "$Q/f8a56bf9-6cb6-4fd2-a402-26bc9fb88168.png"
    private const val IMG_EB17_QUARTZ_BRANDBERG_MUSEUM = "$Q/9cafe5a0-023b-45f9-bb76-fa68197944c3.png"
    private const val IMG_EB17_QUARTZ_ELESTIAL_ROUGH = "$Q/a01eaed2-d4ad-4e7f-9772-e5895a3082dd.png"
    private const val IMG_EB17_QUARTZ_ELESTIAL_WILD = "$Q/4b35815b-222a-415b-90f1-86f89cb572de.png"
    private const val IMG_EB17_QUARTZ_ELESTIAL_MUSEUM = "$Q/c5e17a53-03d2-409b-b95d-48414943d72b.png"
    private const val IMG_EB17_QUARTZ_FADEN_ROUGH = "$Q/bc4403fc-a573-439a-8b1e-8ef829805651.png"
    private const val IMG_EB17_QUARTZ_FADEN_WILD = "$Q/ee1ef65c-2406-42d4-bfc3-4605b2cd6a2f.png"
    private const val IMG_EB17_QUARTZ_FADEN_MUSEUM = "$Q/fe84b240-ac98-463d-aaf3-ee9bbc87384a.png"
    private const val IMG_EB17_QUARTZ_PHANTOM_ROUGH = "$Q/48d28875-4873-44b3-a40b-843d8d555960.png"
    private const val IMG_EB17_QUARTZ_PHANTOM_WILD = "$Q/9f94330c-929b-4635-9c72-5999e22bb601.png"
    private const val IMG_EB17_QUARTZ_PHANTOM_MUSEUM = "$Q/f811d023-62ee-4c20-b502-65fd59adcb19.png"
    private const val IMG_EB17_QUARTZ_TIBETAN_ROUGH = "$Q/a27315da-a1a5-41bc-8506-b9dfc5730f0c.png"
    private const val IMG_EB17_QUARTZ_TIBETAN_WILD = "$Q/26e47fce-b2e4-44c1-9d6d-af0c5a428dbd.png"
    private const val IMG_EB17_QUARTZ_TIBETAN_MUSEUM = "$Q/c72aeeb7-a18c-4ff9-bc1e-da339040289f.png"
    private const val IMG_EB17_QUARTZ_BLUE_ROUGH = "$Q/1d6e7799-370a-4865-b7c4-779485c7e54b.png"
    private const val IMG_EB17_QUARTZ_BLUE_WILD = "$Q/4e9f29b4-a33f-4917-9274-6a52a00c79f2.png"
    private const val IMG_EB17_QUARTZ_BLUE_MUSEUM = "$Q/6ed88d28-0297-4b6b-9415-c83065fcd1f4.png"
    private const val IMG_EB17_QUARTZ_AURA_ROUGH = "$Q/f0bd7715-93f2-41b3-8dfc-c631177afbc8.png"
    private const val IMG_EB17_QUARTZ_AURA_WILD = "$Q/fd32197a-bf94-4d91-8a31-ae7a66073714.png"
    private const val IMG_EB17_QUARTZ_AURA_MUSEUM = "$Q/4f77110f-7c68-44fe-879a-b0fa1140732b.png"
    private const val IMG_EB17_GARNET_STAR_ROUGH = "$Q/17dc3de9-55f3-418f-b90e-3c586a9749e6.png"
    private const val IMG_EB17_GARNET_STAR_WILD = "$Q/80d970f6-da42-4a81-9706-3aae7b52e729.png"
    private const val IMG_EB17_GARNET_STAR_MUSEUM = "$Q/c967775f-8aa2-4641-b5aa-23f67f753c89.png"
    private const val IMG_EB17_GARNET_MALAYA_ROUGH = "$Q/395eec2a-a51e-4f82-a675-a5f46aaa544f.png"
    private const val IMG_EB17_GARNET_MALAYA_WILD = "$Q/03134183-6580-4998-b00f-6051960a84b9.png"
    private const val IMG_EB17_GARNET_MALAYA_MUSEUM = "$Q/2d4808e9-d4d6-4944-a40a-00e622c5540a.png"
    private const val IMG_EB17_GARNET_RASPBERRY_ROUGH = "$Q/14a6ff46-12e2-4220-a491-2eb5c14caac1.png"
    private const val IMG_EB17_GARNET_RASPBERRY_WILD = "$Q/29d66ba6-d537-4eac-8af7-ce88f7df3e07.png"
    private const val IMG_EB17_GARNET_RASPBERRY_MUSEUM = "$Q/977e700a-746a-4da9-80d0-99d7f0b54023.png"
    private const val IMG_EB17_GARNET_HYDROGROSSULAR_ROUGH = "$Q/f45a5e46-1776-45ad-9b43-bd8a9f83bbb7.png"
    private const val IMG_EB17_GARNET_HYDROGROSSULAR_CAB = "$Q/e6733457-1313-4b31-a30d-c3365ac1c541.png"
    private const val IMG_EB17_GARNET_RHODOLITE_ROUGH = "$Q/063ec337-5748-463a-b613-85c52eeab077.png"
    private const val IMG_EB17_GARNET_RHODOLITE_CAB = "$Q/cf64ca4e-72fb-45d8-a4b8-6c5611157d62.png"
    private const val IMG_EB17_TOPAZ_CLEAR_ROUGH = "$Q/da43f61c-9e23-4334-b7bb-50d487e28ab7.png"
    private const val IMG_EB17_TOPAZ_CLEAR_WILD = "$Q/304d89f9-4153-4377-8056-c213ce4ed451.png"
    private const val IMG_EB17_TOPAZ_CLEAR_MUSEUM = "$Q/023fc7d0-ee5e-43a1-9fdc-057a12b40f9e.png"
    private const val IMG_EB17_OPAL_VIOLET_FLAME_ROUGH = "$Q/090570a2-f55c-4b3d-ad80-a46fe2a7a26f.png"
    private const val IMG_EB17_OPAL_VIOLET_FLAME_WILD = "$Q/57ec4f7c-085c-4d4b-aa06-997e7ba8dc34.png"
    private const val IMG_EB17_OPAL_VIOLET_FLAME_MUSEUM = "$Q/3f6e4da9-f27a-4b61-b589-23651d5815f9.png"
    private const val IMG_EB17_SPINEL_LAVENDER_ROUGH = "$Q/13fc6617-9b13-4d7b-a457-50e8d4e92eb9.png"
    private const val IMG_EB17_SPINEL_LAVENDER_WILD = "$Q/0dba2d3c-e52a-45ec-a698-141e6ca47333.png"
    private const val IMG_EB17_SPINEL_LAVENDER_MUSEUM = "$Q/3c90db35-b15c-4541-ac2f-080beb9806a9.png"
    private const val IMG_EB17_ZIRCON_RED_ROUGH = "$Q/f1c81319-9918-4813-b513-f46093d9cfc0.png"
    private const val IMG_EB17_ZIRCON_RED_WILD = "$Q/e3e3b934-63e0-4f92-81f7-ac9394b9cb99.png"
    private const val IMG_EB17_ZIRCON_RED_MUSEUM = "$Q/a79178fc-d4ac-4085-81ab-4c34b7636dfb.png"
    private const val IMG_EB17_OPAL_MOSS_ROUGH = "$Q/f05154d9-a326-4640-98db-ac62c9fed3cd.png"
    private const val IMG_EB17_OPAL_MOSS_CAB = "$Q/5ec223e4-1803-47d0-b746-67042e965bbd.png"
    private const val IMG_EB17_GOLDSTONE_GREEN_ROUGH = "$Q/c0797407-d950-442b-81e8-856c1d9368f2.png"
    private const val IMG_EB17_GOLDSTONE_GREEN_CAB = "$Q/2535d4c1-d3c9-4f55-8b62-1214d8759777.png"
    private const val IMG_EB17_GOLDSTONE_BLUE_ROUGH = "$Q/5d3a8ed4-d122-48a2-bbfc-31113ac8d852.png"
    private const val IMG_EB17_GOLDSTONE_BLUE_CAB = "$Q/1923c7cc-91c4-4f45-b911-05be242b281a.png"
    private const val IMG_EB17_GOLDSTONE_BROWN_ROUGH = "$Q/49b32d0b-c519-47d5-9102-307b9f6e2544.png"
    private const val IMG_EB17_GOLDSTONE_BROWN_CAB = "$Q/6454e551-fe1d-416c-b375-ff5ef8ca0a61.png"
    private const val IMG_EB17_ENSTATITE_GOLDEN_ROUGH = "$Q/c3b8d6d2-2525-458b-ad61-75bbd8608492.png"
    private const val IMG_EB17_ENSTATITE_GOLDEN_WILD = "$Q/1ea52ce8-b8f3-4d1d-882b-fd7761bcc7c1.png"
    private const val IMG_EB17_ENSTATITE_GOLDEN_MUSEUM = "$Q/3bc48102-1470-4be6-b58e-8a7474510ef2.png"
    private const val IMG_EB17_ATLANTISITE_ROUGH = "$Q/59ad35e4-0330-44aa-b074-fcfe2a0ea13b.png"
    private const val IMG_EB17_ATLANTISITE_CAB = "$Q/1354cbb6-4131-46b8-8b86-b88c7f7c4076.png"
    private const val IMG_EB17_GIRASOL_ROUGH = "$Q/99fd5e77-01db-4c28-bb34-50aa6a0e4f7e.png"
    private const val IMG_EB17_GIRASOL_CAB = "$Q/a70fb39f-54b0-495e-9140-8c0e6d5025f7.png"
    private const val IMG_EB17_DARWIN_GLASS_ROUGH = "$Q/7ec4e660-fa67-4c8b-9d2f-22eb7e921100.png"
    private const val IMG_EB17_DARWIN_GLASS_WILD = "$Q/c6085eba-3e10-4d20-bf00-9471311a6098.png"
    private const val IMG_EB17_DARWIN_GLASS_MUSEUM = "$Q/adf53c2f-0ff9-4a08-827e-56b047025de0.png"
    private const val IMG_EB17_K2_STONE_ROUGH = "$Q/418d5417-c42e-47be-b130-5cb8241a40c9.png"
    private const val IMG_EB17_K2_STONE_WILD = "$Q/989a3345-d182-44e6-aead-0caad1986f7a.png"
    private const val IMG_EB17_K2_STONE_MUSEUM = "$Q/6ba831d0-e388-4256-b214-99b00bf1bcba.png"
    private const val IMG_EB17_LLANITE_ROUGH = "$Q/13c9a8ec-34c2-42a0-ba68-f0ffee4c3781.png"
    private const val IMG_EB17_LLANITE_WILD = "$Q/42365e85-1f78-40ae-b5f0-2d142008e6ba.png"
    private const val IMG_EB17_LLANITE_MUSEUM = "$Q/abe18ed4-4b8d-4e9a-8f11-e501814b3ff5.png"
    private const val IMG_EB17_CELESTOBARITE_ROUGH = "$Q/409b1419-71f7-46ca-bead-2b890eabee5b.png"
    private const val IMG_EB17_CELESTOBARITE_CAB = "$Q/626c8e39-f8f1-44a8-bde6-f2024e873a85.png"
    private const val IMG_EB17_FLOWER_STONE_ROUGH = "$Q/45ea0f63-5b30-4740-b2ee-2381eede082b.png"
    private const val IMG_EB17_FLOWER_STONE_CAB = "$Q/c6773ed4-2b87-4cc4-aa83-0b25276ad278.png"
    private const val IMG_EB17_DRAGON_SCALE_ROUGH = "$Q/4e3a4ad1-609e-4dac-8e0c-b269ac60ddec.png"
    private const val IMG_EB17_DRAGON_SCALE_CAB = "$Q/adc6165f-03fc-49ec-bff6-14db15e8ed3c.png"
    private const val IMG_EB17_PICTURE_SANDSTONE_ROUGH = "$Q/140485f1-2d08-4b0e-b20b-f78da1cb8626.png"
    private const val IMG_EB17_PICTURE_SANDSTONE_WILD = "$Q/dcd950c8-949b-42a7-b4de-b4f96b2c276a.png"
    private const val IMG_EB17_PICTURE_SANDSTONE_MUSEUM = "$Q/c0240e16-8d9e-40b9-ace8-c0e516aff2de.png"
    private const val IMG_EB17_INDERITE_ROUGH = "$Q/f2ceb1e5-df16-41af-9e6e-dd53678bc021.png"
    private const val IMG_EB17_INDERITE_WILD = "$Q/ed1a9b53-d553-4154-a93e-49f653ad65c2.png"
    private const val IMG_EB17_INDERITE_MUSEUM = "$Q/147c40b1-d7a2-402e-88db-6b67a1346043.png"
    private const val IMG_EB17_INDOCHINITE_ROUGH = "$Q/1d93c280-586a-484b-ab5b-22076d519ab7.png"
    private const val IMG_EB17_INDOCHINITE_WILD = "$Q/461946d6-af95-4a44-bbf7-5459adafb971.png"
    private const val IMG_EB17_INDOCHINITE_MUSEUM = "$Q/1cefb9aa-b8de-4208-82b1-70dc1cec76e3.png"
    private const val IMG_EB17_PELES_HAIR_ROUGH = "$Q/f325e7ca-8726-4588-86eb-6d57b0eeaac5.png"
    private const val IMG_EB17_PELES_HAIR_WILD = "$Q/fde8f722-cd55-469e-9ceb-1bbc88261bb7.png"
    private const val IMG_EB17_PELES_HAIR_MUSEUM = "$Q/b4cfc73e-b7f6-4225-ac2c-b5543285b9c7.png"
    private const val IMG_EB17_PEARLITE_ROUGH = "$Q/beacd8d8-dec6-4b5b-bad2-233095d4bf93.png"
    private const val IMG_EB17_PEARLITE_WILD = "$Q/a1eb1cec-ffcf-44dd-8ba4-3f30545d6142.png"
    private const val IMG_EB17_PEARLITE_MUSEUM = "$Q/c47f5be4-7baf-407a-ba87-5f1a641b29fc.png"
    private const val IMG_EB17_IRGHIZITE_ROUGH = "$Q/d89fa2b9-612d-4d00-8360-4fbc978d3765.png"
    private const val IMG_EB17_IRGHIZITE_WILD = "$Q/be911ba6-3ec9-47c4-ac5e-97221935b9f9.png"
    private const val IMG_EB17_IRGHIZITE_MUSEUM = "$Q/c5d08a7c-3ff9-4102-80e7-b6e7dc0df21d.png"
    private const val IMG_EB17_YTTROFLUORITE_ROUGH = "$Q/c5cd166f-2c84-48e4-b66e-c4ab13e29bb3.png"
    private const val IMG_EB17_YTTROFLUORITE_WILD = "$Q/f3a8ffc4-3562-46b0-924b-541d81386219.png"
    private const val IMG_EB17_YTTROFLUORITE_MUSEUM = "$Q/eca200ce-8e0c-4539-a98e-c636e2dbaa65.png"
    private const val IMG_EB17_KAMMERERITE_ROUGH = "$Q/20156e79-d180-4637-974a-3332a0317379.png"
    private const val IMG_EB17_KAMMERERITE_CAB = "$Q/f2454804-798a-4e53-867f-fe458af708e1.png"
    private const val IMG_EB17_VESZELYITE_ROUGH = "$Q/c834cbb7-3cc8-4222-8a15-185795313be6.png"
    private const val IMG_EB17_VESZELYITE_WILD = "$Q/746d2077-47f8-41cb-8f01-7ea4c4807c69.png"
    private const val IMG_EB17_VESZELYITE_MUSEUM = "$Q/53514611-9dc5-4b9a-80ce-69b5c41e0aff.png"
    private const val IMG_EB17_STRIPED_FLINT_ROUGH = "$Q/760fc578-0f9a-4540-abc9-8e3e131be259.png"
    private const val IMG_EB17_STRIPED_FLINT_WILD = "$Q/cbbb9c59-e6e1-4180-a9ef-f969cca279c4.png"
    private const val IMG_EB17_STRIPED_FLINT_MUSEUM = "$Q/0ff4ce04-6725-47c5-8c9a-3154cd400b57.png"
    private const val IMG_EB17_CHRYSANTHEMUM_STONE_ROUGH = "$Q/ce7a4872-7d21-470c-9261-e19ee210ff3b.png"
    private const val IMG_EB17_CHRYSANTHEMUM_STONE_WILD = "$Q/36ec1b72-5601-48c9-886c-fbfd0800a095.png"
    private const val IMG_EB17_CHRYSANTHEMUM_STONE_MUSEUM = "$Q/01f7c129-56d6-429c-9bf1-54acebbc9a6c.png"

    // ── Expansion Batch 16 remaining: crandallite, wardite, augelite, alunite ──
    private const val IMG_EB16_CRANDALLITE_ROUGH = "$Q/0f437a18-3aed-4682-99aa-2abe6d5c30e7.png"
    private const val IMG_EB16_CRANDALLITE_WILD = "$Q/d3d75f11-aed1-4c72-8549-ff0057f987d0.png"
    private const val IMG_EB16_CRANDALLITE_MUSEUM = "$Q/8381c074-d2d3-446d-9422-c4df90e17253.png"
    private const val IMG_EB16_WARDITE_ROUGH = "$Q/2d23df82-08da-4c90-9e58-9469dd80c807.png"
    private const val IMG_EB16_WARDITE_WILD = "$Q/285b76cd-fc4d-48fa-b473-63a324067f8a.png"
    private const val IMG_EB16_WARDITE_MUSEUM = "$Q/78c0d3df-f2e3-41ef-885b-2ccf14bebac9.png"
    private const val IMG_EB16_AUGELITE_ROUGH = "$Q/b73367e2-5429-4089-84b0-dc990610ae1c.png"
    private const val IMG_EB16_AUGELITE_WILD = "$Q/e67eaa1e-6c0d-4a6b-a390-31519be6a204.png"
    private const val IMG_EB16_AUGELITE_MUSEUM = "$Q/cd689005-8d01-47e2-aea8-62b08fb79f01.png"
    private const val IMG_EB16_ALUNITE_ROUGH = "$Q/6839b5ed-b2b6-4e43-9e37-c41b39d28bbf.png"
    private const val IMG_EB16_ALUNITE_WILD = "$Q/c4ab866d-23ab-43f6-9c6f-c94eda4d71f6.png"
    private const val IMG_EB16_ALUNITE_MUSEUM = "$Q/d5556301-2205-46f4-8f94-3a66b2af12d9.png"

    // ── Expansion Batch 18: Phase 3 rock type images ──
    private const val IMG_EB18_DOLERITE_ROUGH = "$Q/f3d0817f-b5a8-40aa-8552-63b82cce81bf.png"
    private const val IMG_EB18_DOLERITE_WILD = "$Q/2d4ae446-cc1e-4808-8e93-55bb03bd74f4.png"
    private const val IMG_EB18_DOLERITE_MUSEUM = "$Q/1e225293-8818-4990-ad23-0e7ec57f0a10.png"
    private const val IMG_EB18_IGNIMBRITE_ROUGH = "$Q/020c7df1-3ef1-47c4-b3c0-dc92d94d9a07.png"
    private const val IMG_EB18_IGNIMBRITE_WILD = "$Q/27c16f51-b068-4a24-8e30-2140dfe1960e.png"
    private const val IMG_EB18_IGNIMBRITE_MUSEUM = "$Q/9d1962fd-8c1e-44cf-9e09-2e51b3eabfc9.png"
    private const val IMG_EB18_TONALITE_ROUGH = "$Q/b440169a-c81c-46b8-946d-98edf6f41b60.png"
    private const val IMG_EB18_TONALITE_WILD = "$Q/c36b76d8-8b5d-4450-8677-258bd5391817.png"
    private const val IMG_EB18_TONALITE_MUSEUM = "$Q/ef346343-b483-4390-848c-27b390bdd5c7.png"
    private const val IMG_EB18_ADARITE_ROUGH = "$Q/1a8ea182-b7c5-449f-9922-675a2dd02684.png"
    private const val IMG_EB18_ADARITE_WILD = "$Q/232559ff-f7b7-48e4-96c1-f141f3d0d7a1.png"
    private const val IMG_EB18_ADARITE_MUSEUM = "$Q/9e7d0c06-c065-407f-bc16-ebaba959e1da.png"
    private const val IMG_EB18_ESSEXITE_ROUGH = "$Q/9d8af034-d7a3-4559-bda5-44f18f5d2f0a.png"
    private const val IMG_EB18_ESSEXITE_WILD = "$Q/e9ab565d-3f37-49ac-890a-4b5f3b0ad7a5.png"
    private const val IMG_EB18_ESSEXITE_MUSEUM = "$Q/17ac4298-92db-4d86-ad62-f65e7e762f45.png"
    private const val IMG_EB18_ICELANDITE_ROUGH = "$Q/ba06145c-fd03-4ec4-a9c3-401533a26512.png"
    private const val IMG_EB18_ICELANDITE_WILD = "$Q/b225ead9-202b-413f-80a9-ede78ba159f1.png"
    private const val IMG_EB18_ICELANDITE_MUSEUM = "$Q/70fd0667-21f6-48ac-831d-1c83b24a71eb.png"
    private const val IMG_EB18_TRACHYANDESITE_ROUGH = "$Q/c3641d01-e7ca-4876-944f-15a2314ef216.png"
    private const val IMG_EB18_TRACHYANDESITE_WILD = "$Q/b2db22ed-be9b-4906-852a-e9949ec31735.png"
    private const val IMG_EB18_TRACHYANDESITE_MUSEUM = "$Q/2e4deed7-7d1f-4ce6-93cf-b08c61e1bcfc.png"
    private const val IMG_EB18_TRACHYBASALT_ROUGH = "$Q/f097467f-450b-4152-b0d1-3e825398d864.png"
    private const val IMG_EB18_TRACHYBASALT_WILD = "$Q/35f324c5-1a9e-48f2-ace7-18726e25f5a0.png"
    private const val IMG_EB18_TRACHYBASALT_MUSEUM = "$Q/1654e6e8-ea4e-4655-9662-ebd4f1a065bb.png"
    private const val IMG_EB18_TEPHRITE_ROUGH = "$Q/e6ede195-b68c-49b5-8671-b738e2bcfe3a.png"
    private const val IMG_EB18_TEPHRITE_WILD = "$Q/59572de3-4c53-46e6-8a45-f078f416b8be.png"
    private const val IMG_EB18_TEPHRITE_MUSEUM = "$Q/38741fc8-e077-4463-bc50-48630ab9e2ef.png"
    private const val IMG_EB18_QUARTZ_MONZONITE_ROUGH = "$Q/62ac5853-8a7b-442b-960b-df275deb11b9.png"
    private const val IMG_EB18_QUARTZ_MONZONITE_WILD = "$Q/7d69f68a-125e-46ad-92d1-848b6e74e8c1.png"
    private const val IMG_EB18_QUARTZ_MONZONITE_MUSEUM = "$Q/3f5ab27b-50d2-44e6-b7aa-2ffd3ba9e3f3.png"
    private const val IMG_EB18_VOLCANIC_BOMB_ROUGH = "$Q/164d55ab-2faa-445d-ac92-baaffbb34d7c.png"
    private const val IMG_EB18_VOLCANIC_BOMB_WILD = "$Q/e82fe8d4-2978-45f7-93f2-6b171b576105.png"
    private const val IMG_EB18_VOLCANIC_BOMB_MUSEUM = "$Q/a31e7e7a-592a-4db2-805c-cecf4f2a4f3d.png"
    private const val IMG_EB18_GRANOPHYRE_ROUGH = "$Q/d14a262a-a0cf-44f8-95cf-c4cad1d4e777.png"
    private const val IMG_EB18_GRANOPHYRE_WILD = "$Q/8be76ffd-b538-4742-8edb-b1bebca31169.png"
    private const val IMG_EB18_GRANOPHYRE_MUSEUM = "$Q/2dcef484-d09b-4b5d-a380-6664daaaae25.png"
    private const val IMG_EB18_MONZODIORITE_ROUGH = "$Q/52112cf0-c762-4b8c-b3c1-18025f71702d.png"
    private const val IMG_EB18_MONZODIORITE_WILD = "$Q/c075c2ff-8366-44be-af7a-8aa72061ae6b.png"
    private const val IMG_EB18_MONZODIORITE_MUSEUM = "$Q/3e646b92-8309-46d2-9341-fe80c37f146f.png"
    private const val IMG_EB18_RHYODACITE_ROUGH = "$Q/6c1837bb-2af6-4cf2-8a88-7481893dd60d.png"
    private const val IMG_EB18_RHYODACITE_WILD = "$Q/4cef45e8-61b8-450c-abf7-741fbea8612d.png"
    private const val IMG_EB18_RHYODACITE_MUSEUM = "$Q/211fa4a6-124e-4483-9ff2-1e172616599a.png"
    private const val IMG_EB18_TRONDHJEMITE_ROUGH = "$Q/a0d3d7be-d999-4103-beb2-d8f40a950678.png"
    private const val IMG_EB18_TRONDHJEMITE_WILD = "$Q/7753e070-7965-4d69-96f9-e9d95c98e59e.png"
    private const val IMG_EB18_TRONDHJEMITE_MUSEUM = "$Q/f8508308-4cf2-483a-9926-718ca9a945d0.png"
    private const val IMG_EB18_TEPHRIPHONOLITE_ROUGH = "$Q/dac59778-03cb-409a-8317-23082d263604.png"
    private const val IMG_EB18_TEPHRIPHONOLITE_WILD = "$Q/94c54c26-46d1-42e3-9773-60ba68e871fe.png"
    private const val IMG_EB18_TEPHRIPHONOLITE_MUSEUM = "$Q/e5c5f099-b0a3-43e1-b633-08b4d71ae58e.png"
    private const val IMG_EB18_CLAYSTONE_ROUGH = "$Q/5905a701-8804-46c0-b635-0032e7e74cda.png"
    private const val IMG_EB18_CLAYSTONE_WILD = "$Q/4ea89438-0f5f-4da9-8285-ff58f0fc36d6.png"
    private const val IMG_EB18_CLAYSTONE_MUSEUM = "$Q/5cbc9e1a-b273-40ba-9238-5698147d027a.png"
    private const val IMG_EB18_CALCARENITE_ROUGH = "$Q/2cec03d3-68b7-4aae-9661-f912ac8b2be4.png"
    private const val IMG_EB18_CALCARENITE_WILD = "$Q/17b3ee96-18f4-468c-adf5-a41ba4fba0b1.png"
    private const val IMG_EB18_CALCARENITE_MUSEUM = "$Q/b360f224-643d-498c-b900-b866687987b6.png"
    private const val IMG_EB18_GEYSERITE_ROUGH = "$Q/053eb870-97d6-411a-a70d-1882c7bd9c47.png"
    private const val IMG_EB18_GEYSERITE_WILD = "$Q/0d5d34da-aa8a-473a-9ec0-51b9f3962c36.png"
    private const val IMG_EB18_GEYSERITE_MUSEUM = "$Q/63bc2f78-701a-4991-98ed-cbd1b842ccc1.png"
    private const val IMG_EB18_GRITSTONE_ROUGH = "$Q/fc38554a-1011-4a67-b4fd-39716e84a8ce.png"
    private const val IMG_EB18_GRITSTONE_WILD = "$Q/25cbf847-a34e-4741-a87f-fa7b84f464e5.png"
    private const val IMG_EB18_GRITSTONE_MUSEUM = "$Q/66467a35-3a9d-4168-99af-508178aad4b0.png"
    private const val IMG_EB18_OOLITE_ROUGH = "$Q/2ead01ed-9886-4faf-ab6a-4d1a8aa52145.png"
    private const val IMG_EB18_OOLITE_WILD = "$Q/7c77d458-eabe-4fed-87dc-1516e1251653.png"
    private const val IMG_EB18_OOLITE_MUSEUM = "$Q/9678932b-eeae-4521-90da-bb2770a30c1e.png"
    private const val IMG_EB18_SYLVINITE_ROUGH = "$Q/55d9fe00-1875-43e4-b1e5-8015c34f470a.png"
    private const val IMG_EB18_SYLVINITE_WILD = "$Q/72c22ff5-6f1d-4637-bddf-1a1b6c286a84.png"
    private const val IMG_EB18_SYLVINITE_MUSEUM = "$Q/cd6c11d7-43bd-450e-aac0-8a885142ab41.png"
    private const val IMG_EB18_TURBIDITE_ROUGH = "$Q/a3eea653-912d-4be3-a6f2-446bc0e5e229.png"
    private const val IMG_EB18_TURBIDITE_WILD = "$Q/59820a49-d5a8-4f76-a993-38aaa0ba178c.png"
    private const val IMG_EB18_TURBIDITE_MUSEUM = "$Q/1a835fd7-1c78-400e-a600-809b25f05981.png"
    private const val IMG_EB18_WACKESTONE_ROUGH = "$Q/4ecafa2c-d9e4-4257-9b4c-236fc4b34e7a.png"
    private const val IMG_EB18_WACKESTONE_WILD = "$Q/73388b37-2d36-45cf-9242-c3e4fceac765.png"
    private const val IMG_EB18_WACKESTONE_MUSEUM = "$Q/19d9714e-87e2-4b98-9e2f-1c83c5c51724.png"
    private const val IMG_EB18_CATACLASITE_ROUGH = "$Q/c45b2b43-a4d0-4f8a-a56e-ef6757a3a4fe.png"
    private const val IMG_EB18_CATACLASITE_WILD = "$Q/83140485-465f-400c-bf30-c1a46dc9204f.png"
    private const val IMG_EB18_CATACLASITE_MUSEUM = "$Q/85d521f8-1b12-485c-8784-76c1b66a379f.png"
    private const val IMG_EB18_JADEITITE_ROUGH = "$Q/4647ecfd-4ecb-477e-ac70-bf719a046f38.png"
    private const val IMG_EB18_JADEITITE_WILD = "$Q/18151086-fe92-40f4-b009-c23a2ca66983.png"
    private const val IMG_EB18_JADEITITE_MUSEUM = "$Q/d2d9a5cf-f223-4d63-a0c4-dbe308c2e7f5.png"
    private const val IMG_EB18_LITCHFIELDITE_ROUGH = "$Q/4326df97-8793-47b3-b154-185323d60c72.png"
    private const val IMG_EB18_LITCHFIELDITE_WILD = "$Q/1fddfc7e-5c42-45a2-988e-207f2fe06c5a.png"
    private const val IMG_EB18_LITCHFIELDITE_MUSEUM = "$Q/64b5cb42-b065-41c0-9850-7f161490c44b.png"
    private const val IMG_EB18_METAPELITE_ROUGH = "$Q/b79051b9-5537-40cf-9d02-edb03a254e48.png"
    private const val IMG_EB18_METAPELITE_WILD = "$Q/87896fb9-7f1b-4a15-8bee-b95e597bdd4c.png"
    private const val IMG_EB18_METAPELITE_MUSEUM = "$Q/959c1e9f-da9a-46af-a793-07d98aeaaec5.png"
    private const val IMG_EB18_SERPENTINITE_ROUGH = "$Q/1eff0976-208f-4e1b-9401-0043655fb690.png"
    private const val IMG_EB18_SERPENTINITE_WILD = "$Q/6010df3f-dd9e-47fc-915f-168f0c9800e9.png"
    private const val IMG_EB18_SERPENTINITE_MUSEUM = "$Q/910f5ed0-d60b-485e-b95c-8c119804bf9b.png"
    private const val IMG_EB18_SUEVITE_ROUGH = "$Q/5cc6a00c-38a4-4c6c-955b-d7f42cf6bd17.png"
    private const val IMG_EB18_SUEVITE_WILD = "$Q/88c2bb65-5eba-4f53-8834-7a7ebfd6de0f.png"
    private const val IMG_EB18_SUEVITE_MUSEUM = "$Q/6412386d-4562-4219-9b01-b8cfe07dad65.png"
    private const val IMG_EB18_PSEUDOTACHYLITE_ROUGH = "$Q/89bede18-db73-40fe-8e99-7de16e26f255.png"
    private const val IMG_EB18_PSEUDOTACHYLITE_WILD = "$Q/c7b187c3-e0f3-4615-96bf-440ba688e49f.png"
    private const val IMG_EB18_PSEUDOTACHYLITE_MUSEUM = "$Q/fa26d029-e4d5-432d-a9c1-3d94f2bfee71.png"
    private const val IMG_EB18_CALCFLINTA_ROUGH = "$Q/0f437a18-3aed-4682-99aa-2abe6d5c30e7.png"
    private const val IMG_EB18_CALCFLINTA_WILD = "$Q/6ecce36d-3faf-4ae0-a7c0-7dfc79f93726.png"
    private const val IMG_EB18_CALCFLINTA_MUSEUM = "$Q/f2f8b31d-0112-4f47-851e-4a0a45da40b1.png"
    private const val IMG_EB18_METACONGLOMERATE_ROUGH = "$Q/2d23df82-08da-4c90-9e58-9469dd80c807.png"
    private const val IMG_EB18_METACONGLOMERATE_WILD = "$Q/81019c36-0218-4d76-8843-39655f44a6d4.png"
    private const val IMG_EB18_METACONGLOMERATE_MUSEUM = "$Q/b73367e2-5429-4089-84b0-dc990610ae1c.png"
    private const val IMG_EB18_AMPHIBOLITE_GOLDEN_ROUGH = "$Q/6839b5ed-b2b6-4e43-9e37-c41b39d28bbf.png"
    private const val IMG_EB18_AMPHIBOLITE_GOLDEN_WILD = "$Q/a5e319e1-26fd-4168-89ed-f7f14e6981d1.png"
    private const val IMG_EB18_AMPHIBOLITE_GOLDEN_MUSEUM = "$Q/50096e8d-4a91-4261-b99c-cf00d21f9f04.png"

    // ── Expansion Batch 19: Phase 4 rare mineral images ──
    private const val IMG_EB19_ABELSONITE_ROUGH = "$Q/4ecbcc57-8b3a-473b-b346-2b09ab89bd1f.png"
    private const val IMG_EB19_ABELSONITE_WILD = "$Q/0a7ca348-2823-4a00-821e-0121776112b6.png"
    private const val IMG_EB19_ABELSONITE_MUSEUM = "$Q/46cc1f11-6e40-485e-b08b-99a68a9f814a.png"
    private const val IMG_EB19_KASOLITE_ROUGH = "$Q/4506b946-0346-4f93-aaa3-1243edddc055.png"
    private const val IMG_EB19_KASOLITE_WILD = "$Q/25210e49-5294-47e6-b127-b8e75c51b6d3.png"
    private const val IMG_EB19_KASOLITE_MUSEUM = "$Q/35aa80ec-6375-4f1e-bb5f-614f1e64ec5e.png"
    private const val IMG_EB19_KOLBECKITE_ROUGH = "$Q/f142da3b-b08b-4324-9452-18b759fca8ac.png"
    private const val IMG_EB19_KOLBECKITE_WILD = "$Q/4adbb03f-9d56-45f4-b008-b4d821f28c60.png"
    private const val IMG_EB19_KOLBECKITE_MUSEUM = "$Q/bb84b01a-d42c-4b17-82c4-86bd9a3b09bb.png"
    private const val IMG_EB19_LAMMERITE_ROUGH = "$Q/579c15e1-9f87-45a9-bf36-c9bd8c10b995.png"
    private const val IMG_EB19_LAMMERITE_WILD = "$Q/1abecff9-ecbc-4c72-b22b-d491cbc8d6c9.png"
    private const val IMG_EB19_LAMMERITE_MUSEUM = "$Q/a8a2c382-850a-44bf-a451-d53815a3e895.png"
    private const val IMG_EB19_LAVENDULAN_ROUGH = "$Q/07243d85-7857-49c6-99e6-720c5920def0.png"
    private const val IMG_EB19_LAVENDULAN_WILD = "$Q/cf30f89b-a37c-4772-b041-663f4ec48601.png"
    private const val IMG_EB19_LAVENDULAN_MUSEUM = "$Q/001a4159-629b-44f1-be75-dbf14ff56843.png"
    private const val IMG_EB19_LEUCOPHANITE_ROUGH = "$Q/a190fd98-f90d-4fb0-b346-27fbc9fc5be9.png"
    private const val IMG_EB19_LEUCOPHANITE_WILD = "$Q/eb289502-6700-485d-9c6d-e09db65b2c46.png"
    private const val IMG_EB19_LEUCOPHANITE_MUSEUM = "$Q/95e8bfb0-d97f-4f9d-81eb-f7369f9a40a1.png"
    private const val IMG_EB19_BRITHOLITE_ROUGH = "$Q/3d41cf96-53e9-4cb2-be79-3621a8978a52.png"
    private const val IMG_EB19_BRITHOLITE_WILD = "$Q/d3532500-3b0d-4fb3-b02c-93daf621680b.png"
    private const val IMG_EB19_BRITHOLITE_MUSEUM = "$Q/b8af9b4f-49c2-4242-8e25-84e8ceaf2423.png"
    private const val IMG_EB19_META_AUTUNITE_ROUGH = "$Q/e73d1528-d695-4844-b01a-380559482d42.png"
    private const val IMG_EB19_META_AUTUNITE_WILD = "$Q/4e395e4b-482d-4993-9185-13458abc7eee.png"
    private const val IMG_EB19_META_AUTUNITE_MUSEUM = "$Q/7e0160ef-f4d8-40f7-9364-ed8c4fc2553f.png"
    private const val IMG_EB19_METATORBERNITE_ROUGH = "$Q/67c3e463-60df-4302-a573-828005423b49.png"
    private const val IMG_EB19_METATORBERNITE_WILD = "$Q/a3f32890-b136-40ad-82f5-2df63fcf1ff0.png"
    private const val IMG_EB19_METATORBERNITE_MUSEUM = "$Q/4b44e96a-301c-46fa-a820-ab14d520276e.png"
    private const val IMG_EB19_METAVARISCITE_ROUGH = "$Q/3981706f-4ebc-49ec-8d65-8f4555752838.png"
    private const val IMG_EB19_METAVARISCITE_WILD = "$Q/b7fca7a8-a72d-4064-88c4-9ee50f77fe68.png"
    private const val IMG_EB19_METAVARISCITE_MUSEUM = "$Q/fc052c80-a8ad-47b0-a9b3-49e359e16404.png"
    private const val IMG_EB19_DYPINGITE_ROUGH = "$Q/7bfe7d76-71e3-437e-b53f-ebfaba23065f.png"
    private const val IMG_EB19_DYPINGITE_WILD = "$Q/a147d221-c4bb-4431-87f9-1af5bf785b23.png"
    private const val IMG_EB19_DYPINGITE_MUSEUM = "$Q/09ddde1d-1d50-490e-acc8-4d6915e07d63.png"
    private const val IMG_EB19_FRANCOLITE_ROUGH = "$Q/27e9f944-efea-4b26-9404-de8e2c029c4e.png"
    private const val IMG_EB19_FRANCOLITE_WILD = "$Q/63dc0b67-fa4d-4447-aa14-040ac9b4893e.png"
    private const val IMG_EB19_FRANCOLITE_MUSEUM = "$Q/04d56e85-8bb8-44ee-97ad-bdc6e67c1de8.png"
    private const val IMG_EB19_MICROCLINE_ROUGH = "$Q/4f518f9a-acd7-45cc-8ba3-4a398b14df25.png"
    private const val IMG_EB19_MICROCLINE_WILD = "$Q/02d71de9-0725-4a7a-86e5-c3e78b28479c.png"
    private const val IMG_EB19_MICROCLINE_MUSEUM = "$Q/40aea007-19a7-48c6-a7de-725177ffabb1.png"
    private const val IMG_EB19_ALLOPHANE_ROUGH = "$Q/16c1448d-0336-4b8a-a753-b0905311762d.png"
    private const val IMG_EB19_ALLOPHANE_WILD = "$Q/6f8737fd-6288-4e4c-b22d-0915be133a31.png"
    private const val IMG_EB19_ALLOPHANE_MUSEUM = "$Q/14a74a15-dc48-48bd-9332-99ab06eeb5bb.png"
    private const val IMG_EB19_ERIONITE_ROUGH = "$Q/18064017-db19-4e61-b1d4-cbb9f912a165.png"
    private const val IMG_EB19_ERIONITE_WILD = "$Q/6e42ba4d-8170-4c69-acd2-8e97d28dd0c6.png"
    private const val IMG_EB19_ERIONITE_MUSEUM = "$Q/ea13a108-2896-49b6-99e4-79feab82e4ab.png"
    private const val IMG_EB19_FERRIERITE_ROUGH = "$Q/bc24e382-4d73-4913-aee7-c9291b582b31.png"
    private const val IMG_EB19_FERRIERITE_WILD = "$Q/8d41d5ba-86af-4782-9663-43277f99778c.png"
    private const val IMG_EB19_FERRIERITE_MUSEUM = "$Q/d0270538-e409-4dfa-be2e-d98f38649c43.png"
    private const val IMG_EB19_TAPIOLITE_ROUGH = "$Q/db95de98-b2d7-4375-8167-aacd73688583.png"
    private const val IMG_EB19_TAPIOLITE_WILD = "$Q/1bb386cc-94a0-4da6-8314-23e444a73a55.png"
    private const val IMG_EB19_TAPIOLITE_MUSEUM = "$Q/29b61bf3-354f-4bfb-b464-d23cff6c62d9.png"
    private const val IMG_EB19_TELLURIUM_ROUGH = "$Q/6a0cc1c8-0c58-4ea3-81b5-c4d278a7752e.png"
    private const val IMG_EB19_TELLURIUM_WILD = "$Q/ac450371-aea8-4cdf-b45f-c45c73b2cea0.png"
    private const val IMG_EB19_TELLURIUM_MUSEUM = "$Q/8741c117-6b1d-4b35-bf83-a10143cc3b50.png"
    private const val IMG_EB19_ARSENIC_ROUGH = "$Q/84b6ed65-ceb6-4f0e-8603-d8c7163a15bd.png"
    private const val IMG_EB19_ARSENIC_WILD = "$Q/e8722097-686f-4416-8457-f573389d0a91.png"
    private const val IMG_EB19_ARSENIC_MUSEUM = "$Q/a5598e43-9a3d-494d-8284-b55f058f59cd.png"
    private const val IMG_EB19_NITRATINE_ROUGH = "$Q/f8f3108f-b819-48b9-ad27-aa27088d8597.png"
    private const val IMG_EB19_NITRATINE_WILD = "$Q/1f017d7a-48d6-4636-9a35-eac752c2c6d1.png"
    private const val IMG_EB19_NITRATINE_MUSEUM = "$Q/442e011e-d5d5-4494-80a3-5e3c99be2cd2.png"
    private const val IMG_EB19_GERHARDTITE_ROUGH = "$Q/60b463d5-c3fd-48ca-b0d3-897c9e76f52d.png"
    private const val IMG_EB19_GERHARDTITE_WILD = "$Q/91b27308-18f9-44a5-9f3e-a6b23ccc1703.png"
    private const val IMG_EB19_GERHARDTITE_MUSEUM = "$Q/edf16f07-f097-4c57-8cac-2e383f939c7f.png"
    private const val IMG_EB19_REMONDITE_ROUGH = "$Q/89e4f01e-37cf-4a57-aade-7dd90ed65182.png"
    private const val IMG_EB19_REMONDITE_WILD = "$Q/e0650363-98b4-4899-b46b-e8a19383e3e6.png"
    private const val IMG_EB19_REMONDITE_MUSEUM = "$Q/c0443771-7f44-49bc-846a-ba7e3c71e433.png"
    private const val IMG_EB19_GORMANITE_ROUGH = "$Q/ea543897-b98d-4dbb-9fc9-f0e9d093af3e.png"
    private const val IMG_EB19_GORMANITE_WILD = "$Q/b3459a23-84f6-45b6-a36f-e41694b2e2ee.png"
    private const val IMG_EB19_GORMANITE_MUSEUM = "$Q/f36e7fe3-b3eb-4e53-9ce6-158be1377c94.png"
    private const val IMG_EB19_KULANITE_ROUGH = "$Q/9b8abd48-f652-40ae-85e7-4ce216684404.png"
    private const val IMG_EB19_KULANITE_WILD = "$Q/55ff2680-8b05-4401-9378-5fcd3cd35d39.png"
    private const val IMG_EB19_KULANITE_MUSEUM = "$Q/6a4ef93f-62bc-4ad5-98bc-700ec7aaa213.png"
    private const val IMG_EB19_CORNETITE_ROUGH = "$Q/8c107f23-fec7-4208-b45b-ce937ebdd6c3.png"
    private const val IMG_EB19_CORNETITE_WILD = "$Q/4520f446-9331-4dc1-bc69-ef4b5eeae6d4.png"
    private const val IMG_EB19_CORNETITE_MUSEUM = "$Q/1da2101d-10fd-4e99-abda-fd64d5d90e2b.png"
    private const val IMG_EB19_CLINOCLASE_ROUGH = "$Q/bf7d541d-6299-4306-8475-c46b132eac9c.png"
    private const val IMG_EB19_CLINOCLASE_WILD = "$Q/9ce729ca-35cc-48bc-823e-0efe1a8dfcef.png"
    private const val IMG_EB19_CLINOCLASE_MUSEUM = "$Q/c4df5e16-3979-4dbf-8404-6ba692923e70.png"
    private const val IMG_EB19_PLUMBOGUMMITE_ROUGH = "$Q/6f238f8c-05ac-4f4a-bcc8-c787265a9b92.png"
    private const val IMG_EB19_PLUMBOGUMMITE_WILD = "$Q/01655102-adb0-4c81-b7f6-36347e04e0da.png"
    private const val IMG_EB19_PLUMBOGUMMITE_MUSEUM = "$Q/d70831bf-e5e9-4e66-879c-f464d381a688.png"

    // ── Expansion Batch 20: Phase 5 gemstone images ──
    private const val IMG_EB20_GARNET_MALI_ROUGH = "$Q/b3479691-5557-4e79-b823-feb1855d75dd.png"
    private const val IMG_EB20_GARNET_MALI_CAB = "$Q/ae944fa7-60a1-4df4-adab-2a64ba62171b.png"
    private const val IMG_EB20_GARNET_UMBALITE_ROUGH = "$Q/692bb2a2-5ae9-40d3-879b-89052f8c2991.png"
    private const val IMG_EB20_GARNET_UMBALITE_CAB = "$Q/93cbb542-e487-404a-b599-7c3ad6d0e829.png"
    private const val IMG_EB20_TOURMALINE_FL_ROUGH = "$Q/2fa646c8-2d8d-429b-9b46-23b727cb761c.png"
    private const val IMG_EB20_TOURMALINE_FL_WILD = "$Q/2d6272a6-1a67-4aba-b904-539d6b53170e.png"
    private const val IMG_EB20_TOURMALINE_FL_MUSEUM = "$Q/5cfe4e5f-626b-4cd3-af49-e3624e0fd694.png"
    private const val IMG_EB20_TOURMALINE_OLENITE_ROUGH = "$Q/a452e92d-7241-4f1d-bfc6-c4a13f2ad775.png"
    private const val IMG_EB20_TOURMALINE_OLENITE_WILD = "$Q/9915af95-05ac-4730-8655-836a1ebc35a1.png"
    private const val IMG_EB20_TOURMALINE_OLENITE_MUSEUM = "$Q/fa90e5b3-6913-4c99-bbd8-e1fc42a7efd8.png"
    private const val IMG_EB20_TOURMALINE_ROSSMANITE_ROUGH = "$Q/cd67d012-3a1d-4556-a374-f16835932ac7.png"
    private const val IMG_EB20_TOURMALINE_ROSSMANITE_WILD = "$Q/c962ccf5-37f7-45e6-bad5-72c9c396f43f.png"
    private const val IMG_EB20_TOURMALINE_ROSSMANITE_MUSEUM = "$Q/b139a809-4d56-40fc-ac9e-ae1c3d9a2496.png"
    private const val IMG_EB20_MEIONITE_ROUGH = "$Q/b7e11191-30c7-448b-8bc8-e156bc5d0203.png"
    private const val IMG_EB20_MEIONITE_WILD = "$Q/98442b00-739d-4141-bb28-6da121ce91fe.png"
    private const val IMG_EB20_MEIONITE_MUSEUM = "$Q/12bcbf80-77f7-4acd-8d94-dc128e551bfd.png"
    private const val IMG_EB20_ADULARIA_ROUGH = "$Q/4748653d-aeb0-4e95-b6f5-7cae8c89d3ab.png"
    private const val IMG_EB20_ADULARIA_WILD = "$Q/ded52e51-b07e-4c6c-8420-d04235da04fd.png"
    private const val IMG_EB20_ADULARIA_MUSEUM = "$Q/26f1cec8-655b-4d96-beb8-495af481f1d8.png"
    private const val IMG_EB20_GEUDA_ROUGH = "$Q/5a6e2484-2489-4ff6-9627-c2fa90a5f8cc.png"
    private const val IMG_EB20_GEUDA_CAB = "$Q/f058b8a0-6896-40b3-abb0-862869478b1f.png"
    private const val IMG_EB20_PIEMONTITE_ROUGH = "$Q/0903bab3-fabe-4b20-92c7-efe86fd29f20.png"
    private const val IMG_EB20_PIEMONTITE_CAB = "$Q/2458beaa-9c3c-4a97-b1a5-94018c9429a5.png"
    private const val IMG_EB20_STICHTITE_ROUGH = "$Q/1cb70248-93e2-4a21-9fa8-d9aa5f17f4da.png"
    private const val IMG_EB20_STICHTITE_CAB = "$Q/1c5da141-d5f2-44af-adf8-ef28927590e7.png"
    private const val IMG_EB20_CALIFORNITE_ROUGH = "$Q/d2641ad0-45fc-45c6-b6b6-233add1e8530.png"
    private const val IMG_EB20_CALIFORNITE_CAB = "$Q/383c5d16-dab9-4674-8fac-04fc615cd165.png"
    private const val IMG_EB20_BUSTAMITE_ROUGH = "$Q/e3517a2e-00fe-42bd-a2b7-b5d2007a481b.png"
    private const val IMG_EB20_BUSTAMITE_WILD = "$Q/8b584e19-4a1f-47a5-87ab-148e79b51961.png"
    private const val IMG_EB20_BUSTAMITE_MUSEUM = "$Q/5fc258e9-f461-4c10-857b-6227f304b5ce.png"
    private const val IMG_EB20_GOOSECREEKITE_ROUGH = "$Q/81538e3c-c5ab-4a75-b888-3f441dc93670.png"
    private const val IMG_EB20_GOOSECREEKITE_WILD = "$Q/0d492c6b-bb38-4c3f-a177-b5d7c9b0ceb0.png"
    private const val IMG_EB20_GOOSECREEKITE_MUSEUM = "$Q/af3d4d91-f471-4040-9e73-7b0cf273f04b.png"
    private const val IMG_EB20_STELLERITE_ROUGH = "$Q/e8da7230-e2b5-46e0-b217-0080a4600be1.png"
    private const val IMG_EB20_STELLERITE_WILD = "$Q/5e73fdb1-c55e-46d2-b90f-f835bc742531.png"
    private const val IMG_EB20_STELLERITE_MUSEUM = "$Q/9cd1d4b7-e907-4fe3-ad29-4da3b52c22f7.png"
    private const val IMG_EB20_RICHTERITE_ROUGH = "$Q/a26edee6-177a-4444-aa3f-1afedf71bd16.png"
    private const val IMG_EB20_RICHTERITE_WILD = "$Q/bfbb472a-49ec-435a-b49a-ff2a018ddc3e.png"
    private const val IMG_EB20_RICHTERITE_MUSEUM = "$Q/76e40a8d-a157-4e70-96ef-b497c1c6858a.png"
    private const val IMG_EB20_CHLOROMELANITE_ROUGH = "$Q/2a8c2034-b754-45ff-8431-5f28af322778.png"
    private const val IMG_EB20_CHLOROMELANITE_CAB = "$Q/dc935767-d392-439e-b2b9-814a4749b6e2.png"

    private val urlChunk8: Map<String, List<String>> by lazy { mapOf(
        "seraphinite" to listOf(IMG_SERAPHINITE_ROUGH, IMG_SERAPHINITE_WILD, IMG_SERAPHINITE_MUSEUM),
        "ajoite" to listOf(IMG_AJOITE_ROUGH, IMG_AJOITE_WILD, IMG_AJOITE_MUSEUM),
        "proustite" to listOf(IMG_PROUSTITE_ROUGH, IMG_PROUSTITE_WILD, IMG_PROUSTITE_MUSEUM),
        "polybasite" to listOf(IMG_POLYBASITE_ROUGH, IMG_POLYBASITE_WILD, IMG_POLYBASITE_MUSEUM),
        "stephanite" to listOf(IMG_STEPHANITE_ROUGH, IMG_STEPHANITE_WILD, IMG_STEPHANITE_MUSEUM),
        "dyscrasite" to listOf(IMG_DYSCRASITE_ROUGH, IMG_DYSCRASITE_WILD, IMG_DYSCRASITE_MUSEUM),
        "nickeline" to listOf(IMG_NICKELINE_ROUGH, IMG_NICKELINE_WILD, IMG_NICKELINE_MUSEUM),
        "tennantite" to listOf(IMG_TENNANTITE_ROUGH, IMG_TENNANTITE_WILD, IMG_TENNANTITE_MUSEUM),
        "alabandite" to listOf(IMG_ALABANDITE_ROUGH, IMG_ALABANDITE_WILD, IMG_ALABANDITE_MUSEUM),
        "boulangerite" to listOf(IMG_EB16_BOULANGERITE_ROUGH, IMG_EB16_BOULANGERITE_WILD, IMG_EB16_BOULANGERITE_MUSEUM),
        "miargyrite" to listOf(IMG_EB16_MIARGYRITE_ROUGH, IMG_EB16_MIARGYRITE_WILD, IMG_EB16_MIARGYRITE_MUSEUM),
        "perovskite" to listOf(IMG_EB16_PEROVSKITE_ROUGH, IMG_EB16_PEROVSKITE_WILD, IMG_EB16_PEROVSKITE_MUSEUM),
        "zincite" to listOf(IMG_EB16_ZINCITE_ROUGH, IMG_EB16_ZINCITE_WILD, IMG_EB16_ZINCITE_MUSEUM),
        "gahnite" to listOf(IMG_EB16_GAHNITE_ROUGH),
        "tantalite" to listOf(IMG_EB16_TANTALITE_ROUGH, IMG_EB16_TANTALITE_WILD, IMG_EB16_TANTALITE_MUSEUM),
        "bixbyite" to listOf(IMG_EB16_BIXBYITE_ROUGH, IMG_EB16_BIXBYITE_WILD, IMG_EB16_BIXBYITE_MUSEUM),
        "baddeleyite" to listOf(IMG_EB16_BADDELEYITE_ROUGH, IMG_EB16_BADDELEYITE_WILD, IMG_EB16_BADDELEYITE_MUSEUM),
        "minium" to listOf(IMG_EB16_MINIUM_ROUGH),
        "atacamite" to listOf(IMG_EB16_ATACAMITE_ROUGH, IMG_EB16_ATACAMITE_WILD, IMG_EB16_ATACAMITE_MUSEUM),
        "villiaumite" to listOf(IMG_EB16_VILLIAUMITE_ROUGH, IMG_EB16_VILLIAUMITE_WILD, IMG_EB16_VILLIAUMITE_MUSEUM),
        "pachnolite" to listOf(IMG_EB16_PACHNOLITE_ROUGH, IMG_EB16_PACHNOLITE_WILD, IMG_EB16_PACHNOLITE_MUSEUM),
        "mendipite" to listOf(IMG_EB16_MENDIPITE_ROUGH),
        "phosgenite" to listOf(IMG_EB16_PHOSGENITE_ROUGH, IMG_EB16_PHOSGENITE_WILD, IMG_EB16_PHOSGENITE_MUSEUM),
        "kutnohorite" to listOf(IMG_EB16_KUTNOHORITE_ROUGH, IMG_EB16_KUTNOHORITE_WILD, IMG_EB16_KUTNOHORITE_MUSEUM),
        "benstonite" to listOf(IMG_EB16_BENSTONITE_ROUGH),
        "cavansite" to listOf(IMG_EB16_CAVANSITE_ROUGH, IMG_EB16_CAVANSITE_WILD, IMG_EB16_CAVANSITE_MUSEUM),
        "libethenite" to listOf(IMG_EB16_LIBETHENITE_ROUGH, IMG_EB16_LIBETHENITE_WILD, IMG_EB16_LIBETHENITE_MUSEUM),
        "clinozoisite" to listOf(IMG_EB16_CLINOZOISITE_ROUGH, IMG_EB16_CLINOZOISITE_WILD, IMG_EB16_CLINOZOISITE_MUSEUM),
        "brucite" to listOf(IMG_EB16_BRUCITE_ROUGH, IMG_EB16_BRUCITE_WILD, IMG_EB16_BRUCITE_MUSEUM),
        "kinoite" to listOf(IMG_EB16_KINOITE_ROUGH),
        "hedenbergite" to listOf(IMG_EB16_HEDENBERGITE_ROUGH, IMG_EB16_HEDENBERGITE_WILD, IMG_EB16_HEDENBERGITE_MUSEUM),
        "lazurite" to listOf(IMG_EB16_LAZURITE_ROUGH, IMG_EB16_LAZURITE_WILD, IMG_EB16_LAZURITE_MUSEUM),
        "hessonite" to listOf(IMG_EB16_HESSONITE_ROUGH, IMG_EB16_HESSONITE_CAB),
        "imperial-topaz" to listOf(IMG_EB16_IMPERIAL_TOPAZ_ROUGH, IMG_EB16_IMPERIAL_TOPAZ_CAB),
        "melanite" to listOf(IMG_EB16_MELANITE_ROUGH, IMG_EB16_MELANITE_WILD, IMG_EB16_MELANITE_MUSEUM),
        "blue-topaz" to listOf(IMG_EB16_BLUE_TOPAZ_ROUGH, IMG_EB16_BLUE_TOPAZ_CAB),

        "angelite" to listOf(IMG_EB16_ANGELITE_ROUGH, IMG_EB16_ANGELITE_WILD),
        "shungite" to listOf(IMG_EB16_SHUNGITE_ROUGH, IMG_EB16_SHUNGITE_CAB),
        "bumblebee-jasper" to listOf(IMG_EB16_BUMBLEBEE_ROUGH, IMG_EB16_BUMBLEBEE_CAB),
        "tiger-iron" to listOf(IMG_EB16_TIGER_IRON_ROUGH, IMG_EB16_TIGER_IRON_CAB),
        "wild-horse-magnesite" to listOf(IMG_EB16_WILD_HORSE_ROUGH, IMG_EB16_WILD_HORSE_CAB),
        "chrome-diopside" to listOf(IMG_EB16_CHROME_DIOPSIDE_ROUGH, IMG_EB16_CHROME_DIOPSIDE_CAB),
        "hercynite" to listOf(IMG_HERCYNITE_ROUGH),
        "stibiconite" to listOf(IMG_STIBICONITE_ROUGH),
        "coltan" to listOf(IMG_COLTAN_ROUGH),
        // Phase 2 — Expansion Batch 17
        "quartz-brandberg" to listOf(IMG_EB17_QUARTZ_BRANDBERG_ROUGH, IMG_EB17_QUARTZ_BRANDBERG_WILD, IMG_EB17_QUARTZ_BRANDBERG_MUSEUM),
        "quartz-elestial" to listOf(IMG_EB17_QUARTZ_ELESTIAL_ROUGH, IMG_EB17_QUARTZ_ELESTIAL_WILD, IMG_EB17_QUARTZ_ELESTIAL_MUSEUM),
        "quartz-faden" to listOf(IMG_EB17_QUARTZ_FADEN_ROUGH, IMG_EB17_QUARTZ_FADEN_WILD, IMG_EB17_QUARTZ_FADEN_MUSEUM),
        "quartz-phantom" to listOf(IMG_EB17_QUARTZ_PHANTOM_ROUGH, IMG_EB17_QUARTZ_PHANTOM_WILD, IMG_EB17_QUARTZ_PHANTOM_MUSEUM),
        "quartz-tibetan" to listOf(IMG_EB17_QUARTZ_TIBETAN_ROUGH, IMG_EB17_QUARTZ_TIBETAN_WILD, IMG_EB17_QUARTZ_TIBETAN_MUSEUM),
        "quartz-blue" to listOf(IMG_EB17_QUARTZ_BLUE_ROUGH, IMG_EB17_QUARTZ_BLUE_WILD, IMG_EB17_QUARTZ_BLUE_MUSEUM),
        "quartz-aura" to listOf(IMG_EB17_QUARTZ_AURA_ROUGH, IMG_EB17_QUARTZ_AURA_WILD, IMG_EB17_QUARTZ_AURA_MUSEUM),
        "garnet-star" to listOf(IMG_EB17_GARNET_STAR_ROUGH, IMG_EB17_GARNET_STAR_WILD, IMG_EB17_GARNET_STAR_MUSEUM),
        "garnet-malaya" to listOf(IMG_EB17_GARNET_MALAYA_ROUGH, IMG_EB17_GARNET_MALAYA_WILD, IMG_EB17_GARNET_MALAYA_MUSEUM),
        "garnet-raspberry" to listOf(IMG_EB17_GARNET_RASPBERRY_ROUGH, IMG_EB17_GARNET_RASPBERRY_WILD, IMG_EB17_GARNET_RASPBERRY_MUSEUM),
        "garnet-hydrogrossular" to listOf(IMG_EB17_GARNET_HYDROGROSSULAR_ROUGH, IMG_EB17_GARNET_HYDROGROSSULAR_CAB),
        "garnet-rhodolite" to listOf(IMG_EB17_GARNET_RHODOLITE_ROUGH, IMG_EB17_GARNET_RHODOLITE_CAB),
        "topaz-clear" to listOf(IMG_EB17_TOPAZ_CLEAR_ROUGH, IMG_EB17_TOPAZ_CLEAR_WILD, IMG_EB17_TOPAZ_CLEAR_MUSEUM),
        "opal-violet-flame" to listOf(IMG_EB17_OPAL_VIOLET_FLAME_ROUGH, IMG_EB17_OPAL_VIOLET_FLAME_WILD, IMG_EB17_OPAL_VIOLET_FLAME_MUSEUM),
        "spinel-lavender" to listOf(IMG_EB17_SPINEL_LAVENDER_ROUGH, IMG_EB17_SPINEL_LAVENDER_WILD, IMG_EB17_SPINEL_LAVENDER_MUSEUM),
        "zircon-red" to listOf(IMG_EB17_ZIRCON_RED_ROUGH, IMG_EB17_ZIRCON_RED_WILD, IMG_EB17_ZIRCON_RED_MUSEUM),
        "opal-moss" to listOf(IMG_EB17_OPAL_MOSS_ROUGH, IMG_EB17_OPAL_MOSS_CAB),
        "goldstone-green" to listOf(IMG_EB17_GOLDSTONE_GREEN_ROUGH, IMG_EB17_GOLDSTONE_GREEN_CAB),
        "goldstone-blue" to listOf(IMG_EB17_GOLDSTONE_BLUE_ROUGH, IMG_EB17_GOLDSTONE_BLUE_CAB),
        "goldstone-brown" to listOf(IMG_EB17_GOLDSTONE_BROWN_ROUGH, IMG_EB17_GOLDSTONE_BROWN_CAB),
        "enstatite-golden" to listOf(IMG_EB17_ENSTATITE_GOLDEN_ROUGH, IMG_EB17_ENSTATITE_GOLDEN_WILD, IMG_EB17_ENSTATITE_GOLDEN_MUSEUM),
        "atlantisite" to listOf(IMG_EB17_ATLANTISITE_ROUGH, IMG_EB17_ATLANTISITE_CAB),
        "girasol" to listOf(IMG_EB17_GIRASOL_ROUGH, IMG_EB17_GIRASOL_CAB),
        "darwin-glass" to listOf(IMG_EB17_DARWIN_GLASS_ROUGH, IMG_EB17_DARWIN_GLASS_WILD, IMG_EB17_DARWIN_GLASS_MUSEUM),
        "k2-stone" to listOf(IMG_EB17_K2_STONE_ROUGH, IMG_EB17_K2_STONE_WILD, IMG_EB17_K2_STONE_MUSEUM),
        "llanite" to listOf(IMG_EB17_LLANITE_ROUGH, IMG_EB17_LLANITE_WILD, IMG_EB17_LLANITE_MUSEUM),
        "celestobarite" to listOf(IMG_EB17_CELESTOBARITE_ROUGH, IMG_EB17_CELESTOBARITE_CAB),
        "flower-stone" to listOf(IMG_EB17_FLOWER_STONE_ROUGH, IMG_EB17_FLOWER_STONE_CAB),
        "dragon-scale-stone" to listOf(IMG_EB17_DRAGON_SCALE_ROUGH, IMG_EB17_DRAGON_SCALE_CAB),
        "picture-sandstone" to listOf(IMG_EB17_PICTURE_SANDSTONE_ROUGH, IMG_EB17_PICTURE_SANDSTONE_WILD, IMG_EB17_PICTURE_SANDSTONE_MUSEUM),
        "inderite" to listOf(IMG_EB17_INDERITE_ROUGH, IMG_EB17_INDERITE_WILD, IMG_EB17_INDERITE_MUSEUM),
        "indochinite" to listOf(IMG_EB17_INDOCHINITE_ROUGH, IMG_EB17_INDOCHINITE_WILD, IMG_EB17_INDOCHINITE_MUSEUM),
        "peles-hair" to listOf(IMG_EB17_PELES_HAIR_ROUGH, IMG_EB17_PELES_HAIR_WILD, IMG_EB17_PELES_HAIR_MUSEUM),
        "pearlite" to listOf(IMG_EB17_PEARLITE_ROUGH, IMG_EB17_PEARLITE_WILD, IMG_EB17_PEARLITE_MUSEUM),
        "irghizite" to listOf(IMG_EB17_IRGHIZITE_ROUGH, IMG_EB17_IRGHIZITE_WILD, IMG_EB17_IRGHIZITE_MUSEUM),
        "yttrofluorite" to listOf(IMG_EB17_YTTROFLUORITE_ROUGH, IMG_EB17_YTTROFLUORITE_WILD, IMG_EB17_YTTROFLUORITE_MUSEUM),
        "kammererite" to listOf(IMG_EB17_KAMMERERITE_ROUGH, IMG_EB17_KAMMERERITE_CAB),
        "veszelyite" to listOf(IMG_EB17_VESZELYITE_ROUGH, IMG_EB17_VESZELYITE_WILD, IMG_EB17_VESZELYITE_MUSEUM),
        "striped-flint" to listOf(IMG_EB17_STRIPED_FLINT_ROUGH, IMG_EB17_STRIPED_FLINT_WILD, IMG_EB17_STRIPED_FLINT_MUSEUM),
        "chrysanthemum-stone" to listOf(IMG_EB17_CHRYSANTHEMUM_STONE_ROUGH, IMG_EB17_CHRYSANTHEMUM_STONE_WILD, IMG_EB17_CHRYSANTHEMUM_STONE_MUSEUM),
        // Batch 16 remaining
        "crandallite" to listOf(IMG_EB16_CRANDALLITE_ROUGH, IMG_EB16_CRANDALLITE_WILD, IMG_EB16_CRANDALLITE_MUSEUM),
        "wardite" to listOf(IMG_EB16_WARDITE_ROUGH, IMG_EB16_WARDITE_WILD, IMG_EB16_WARDITE_MUSEUM),
        "augelite" to listOf(IMG_EB16_AUGELITE_ROUGH, IMG_EB16_AUGELITE_WILD, IMG_EB16_AUGELITE_MUSEUM),
        "alunite" to listOf(IMG_EB16_ALUNITE_ROUGH, IMG_EB16_ALUNITE_WILD, IMG_EB16_ALUNITE_MUSEUM),
        // Phase 3 — Expansion Batch 18
        "dolerite" to listOf(IMG_EB18_DOLERITE_ROUGH, IMG_EB18_DOLERITE_WILD, IMG_EB18_DOLERITE_MUSEUM),
        "ignimbrite" to listOf(IMG_EB18_IGNIMBRITE_ROUGH, IMG_EB18_IGNIMBRITE_WILD, IMG_EB18_IGNIMBRITE_MUSEUM),
        "tonalite" to listOf(IMG_EB18_TONALITE_ROUGH, IMG_EB18_TONALITE_WILD, IMG_EB18_TONALITE_MUSEUM),
        "adakite" to listOf(IMG_EB18_ADARITE_ROUGH, IMG_EB18_ADARITE_WILD, IMG_EB18_ADARITE_MUSEUM),
        "essexite" to listOf(IMG_EB18_ESSEXITE_ROUGH, IMG_EB18_ESSEXITE_WILD, IMG_EB18_ESSEXITE_MUSEUM),
        "icelandite" to listOf(IMG_EB18_ICELANDITE_ROUGH, IMG_EB18_ICELANDITE_WILD, IMG_EB18_ICELANDITE_MUSEUM),
        "trachyandesite" to listOf(IMG_EB18_TRACHYANDESITE_ROUGH, IMG_EB18_TRACHYANDESITE_WILD, IMG_EB18_TRACHYANDESITE_MUSEUM),
        "trachybasalt" to listOf(IMG_EB18_TRACHYBASALT_ROUGH, IMG_EB18_TRACHYBASALT_WILD, IMG_EB18_TRACHYBASALT_MUSEUM),
        "tephrite" to listOf(IMG_EB18_TEPHRITE_ROUGH, IMG_EB18_TEPHRITE_WILD, IMG_EB18_TEPHRITE_MUSEUM),
        "quartz-monzonite" to listOf(IMG_EB18_QUARTZ_MONZONITE_ROUGH, IMG_EB18_QUARTZ_MONZONITE_WILD, IMG_EB18_QUARTZ_MONZONITE_MUSEUM),
        "volcanic-bomb" to listOf(IMG_EB18_VOLCANIC_BOMB_ROUGH, IMG_EB18_VOLCANIC_BOMB_WILD, IMG_EB18_VOLCANIC_BOMB_MUSEUM),
        "granophyre" to listOf(IMG_EB18_GRANOPHYRE_ROUGH, IMG_EB18_GRANOPHYRE_WILD, IMG_EB18_GRANOPHYRE_MUSEUM),
        "monzodiorite" to listOf(IMG_EB18_MONZODIORITE_ROUGH, IMG_EB18_MONZODIORITE_WILD, IMG_EB18_MONZODIORITE_MUSEUM),
        "rhyodacite" to listOf(IMG_EB18_RHYODACITE_ROUGH, IMG_EB18_RHYODACITE_WILD, IMG_EB18_RHYODACITE_MUSEUM),
        "trondhjemite" to listOf(IMG_EB18_TRONDHJEMITE_ROUGH, IMG_EB18_TRONDHJEMITE_WILD, IMG_EB18_TRONDHJEMITE_MUSEUM),
        "tephriphonolite" to listOf(IMG_EB18_TEPHRIPHONOLITE_ROUGH, IMG_EB18_TEPHRIPHONOLITE_WILD, IMG_EB18_TEPHRIPHONOLITE_MUSEUM),
        "claystone" to listOf(IMG_EB18_CLAYSTONE_ROUGH, IMG_EB18_CLAYSTONE_WILD, IMG_EB18_CLAYSTONE_MUSEUM),
        "calcarenite" to listOf(IMG_EB18_CALCARENITE_ROUGH, IMG_EB18_CALCARENITE_WILD, IMG_EB18_CALCARENITE_MUSEUM),
        "geyserite" to listOf(IMG_EB18_GEYSERITE_ROUGH, IMG_EB18_GEYSERITE_WILD, IMG_EB18_GEYSERITE_MUSEUM),
        "gritstone" to listOf(IMG_EB18_GRITSTONE_ROUGH, IMG_EB18_GRITSTONE_WILD, IMG_EB18_GRITSTONE_MUSEUM),
        "oolite" to listOf(IMG_EB18_OOLITE_ROUGH, IMG_EB18_OOLITE_WILD, IMG_EB18_OOLITE_MUSEUM),
        "sylvinite" to listOf(IMG_EB18_SYLVINITE_ROUGH, IMG_EB18_SYLVINITE_WILD, IMG_EB18_SYLVINITE_MUSEUM),
        "turbidite" to listOf(IMG_EB18_TURBIDITE_ROUGH, IMG_EB18_TURBIDITE_WILD, IMG_EB18_TURBIDITE_MUSEUM),
        "wackestone" to listOf(IMG_EB18_WACKESTONE_ROUGH, IMG_EB18_WACKESTONE_WILD, IMG_EB18_WACKESTONE_MUSEUM),
        "cataclasite" to listOf(IMG_EB18_CATACLASITE_ROUGH, IMG_EB18_CATACLASITE_WILD, IMG_EB18_CATACLASITE_MUSEUM),
        "jadeitite" to listOf(IMG_EB18_JADEITITE_ROUGH, IMG_EB18_JADEITITE_WILD, IMG_EB18_JADEITITE_MUSEUM),
        "litchfieldite" to listOf(IMG_EB18_LITCHFIELDITE_ROUGH, IMG_EB18_LITCHFIELDITE_WILD, IMG_EB18_LITCHFIELDITE_MUSEUM),
        "metapelite" to listOf(IMG_EB18_METAPELITE_ROUGH, IMG_EB18_METAPELITE_WILD, IMG_EB18_METAPELITE_MUSEUM),
        "serpentinite" to listOf(IMG_EB18_SERPENTINITE_ROUGH, IMG_EB18_SERPENTINITE_WILD, IMG_EB18_SERPENTINITE_MUSEUM),
        "suevite" to listOf(IMG_EB18_SUEVITE_ROUGH, IMG_EB18_SUEVITE_WILD, IMG_EB18_SUEVITE_MUSEUM),
        "pseudotachylite" to listOf(IMG_EB18_PSEUDOTACHYLITE_ROUGH, IMG_EB18_PSEUDOTACHYLITE_WILD, IMG_EB18_PSEUDOTACHYLITE_MUSEUM),
        "calcflinta" to listOf(IMG_EB18_CALCFLINTA_ROUGH, IMG_EB18_CALCFLINTA_WILD, IMG_EB18_CALCFLINTA_MUSEUM),
        "metaconglomerate" to listOf(IMG_EB18_METACONGLOMERATE_ROUGH, IMG_EB18_METACONGLOMERATE_WILD, IMG_EB18_METACONGLOMERATE_MUSEUM),
        "amphibolite-golden" to listOf(IMG_EB18_AMPHIBOLITE_GOLDEN_ROUGH, IMG_EB18_AMPHIBOLITE_GOLDEN_WILD, IMG_EB18_AMPHIBOLITE_GOLDEN_MUSEUM),
        // Phase 4 — Expansion Batch 19
        "abelsonite" to listOf(IMG_EB19_ABELSONITE_ROUGH, IMG_EB19_ABELSONITE_WILD, IMG_EB19_ABELSONITE_MUSEUM),
        "kasolite" to listOf(IMG_EB19_KASOLITE_ROUGH, IMG_EB19_KASOLITE_WILD, IMG_EB19_KASOLITE_MUSEUM),
        "kolbeckite" to listOf(IMG_EB19_KOLBECKITE_ROUGH, IMG_EB19_KOLBECKITE_WILD, IMG_EB19_KOLBECKITE_MUSEUM),
        "lammerite" to listOf(IMG_EB19_LAMMERITE_ROUGH, IMG_EB19_LAMMERITE_WILD, IMG_EB19_LAMMERITE_MUSEUM),
        "lavendulan" to listOf(IMG_EB19_LAVENDULAN_ROUGH, IMG_EB19_LAVENDULAN_WILD, IMG_EB19_LAVENDULAN_MUSEUM),
        "leucophanite" to listOf(IMG_EB19_LEUCOPHANITE_ROUGH, IMG_EB19_LEUCOPHANITE_WILD, IMG_EB19_LEUCOPHANITE_MUSEUM),
        "britholite" to listOf(IMG_EB19_BRITHOLITE_ROUGH, IMG_EB19_BRITHOLITE_WILD, IMG_EB19_BRITHOLITE_MUSEUM),
        "meta-autunite" to listOf(IMG_EB19_META_AUTUNITE_ROUGH, IMG_EB19_META_AUTUNITE_WILD, IMG_EB19_META_AUTUNITE_MUSEUM),
        "metatorbernite" to listOf(IMG_EB19_METATORBERNITE_ROUGH, IMG_EB19_METATORBERNITE_WILD, IMG_EB19_METATORBERNITE_MUSEUM),
        "metavariscite" to listOf(IMG_EB19_METAVARISCITE_ROUGH, IMG_EB19_METAVARISCITE_WILD, IMG_EB19_METAVARISCITE_MUSEUM),
        "dypingite" to listOf(IMG_EB19_DYPINGITE_ROUGH, IMG_EB19_DYPINGITE_WILD, IMG_EB19_DYPINGITE_MUSEUM),
        "francolite" to listOf(IMG_EB19_FRANCOLITE_ROUGH, IMG_EB19_FRANCOLITE_WILD, IMG_EB19_FRANCOLITE_MUSEUM),
        "microcline" to listOf(IMG_EB19_MICROCLINE_ROUGH, IMG_EB19_MICROCLINE_WILD, IMG_EB19_MICROCLINE_MUSEUM),
        "allophane" to listOf(IMG_EB19_ALLOPHANE_ROUGH, IMG_EB19_ALLOPHANE_WILD, IMG_EB19_ALLOPHANE_MUSEUM),
        "erionite" to listOf(IMG_EB19_ERIONITE_ROUGH, IMG_EB19_ERIONITE_WILD, IMG_EB19_ERIONITE_MUSEUM),
        "ferrierite" to listOf(IMG_EB19_FERRIERITE_ROUGH, IMG_EB19_FERRIERITE_WILD, IMG_EB19_FERRIERITE_MUSEUM),
        "tapiolite" to listOf(IMG_EB19_TAPIOLITE_ROUGH, IMG_EB19_TAPIOLITE_WILD, IMG_EB19_TAPIOLITE_MUSEUM),
        "tellurium-native" to listOf(IMG_EB19_TELLURIUM_ROUGH, IMG_EB19_TELLURIUM_WILD, IMG_EB19_TELLURIUM_MUSEUM),
        "arsenic-native" to listOf(IMG_EB19_ARSENIC_ROUGH, IMG_EB19_ARSENIC_WILD, IMG_EB19_ARSENIC_MUSEUM),
        "nitratine" to listOf(IMG_EB19_NITRATINE_ROUGH, IMG_EB19_NITRATINE_WILD, IMG_EB19_NITRATINE_MUSEUM),
        "gerhardtite" to listOf(IMG_EB19_GERHARDTITE_ROUGH, IMG_EB19_GERHARDTITE_WILD, IMG_EB19_GERHARDTITE_MUSEUM),
        "remondite-ce" to listOf(IMG_EB19_REMONDITE_ROUGH, IMG_EB19_REMONDITE_WILD, IMG_EB19_REMONDITE_MUSEUM),
        "gormanite" to listOf(IMG_EB19_GORMANITE_ROUGH, IMG_EB19_GORMANITE_WILD, IMG_EB19_GORMANITE_MUSEUM),
        "kulanite" to listOf(IMG_EB19_KULANITE_ROUGH, IMG_EB19_KULANITE_WILD, IMG_EB19_KULANITE_MUSEUM),
        "cornetite" to listOf(IMG_EB19_CORNETITE_ROUGH, IMG_EB19_CORNETITE_WILD, IMG_EB19_CORNETITE_MUSEUM),
        "clinoclase" to listOf(IMG_EB19_CLINOCLASE_ROUGH, IMG_EB19_CLINOCLASE_WILD, IMG_EB19_CLINOCLASE_MUSEUM),
        "plumbogummite" to listOf(IMG_EB19_PLUMBOGUMMITE_ROUGH, IMG_EB19_PLUMBOGUMMITE_WILD, IMG_EB19_PLUMBOGUMMITE_MUSEUM),
        // Phase 5 — Expansion Batch 20
        "garnet-mali" to listOf(IMG_EB20_GARNET_MALI_ROUGH, IMG_EB20_GARNET_MALI_CAB),
        "garnet-umbalite" to listOf(IMG_EB20_GARNET_UMBALITE_ROUGH, IMG_EB20_GARNET_UMBALITE_CAB),
        "tourmaline-fluor-liddicoatite" to listOf(IMG_EB20_TOURMALINE_FL_ROUGH, IMG_EB20_TOURMALINE_FL_WILD, IMG_EB20_TOURMALINE_FL_MUSEUM),
        "tourmaline-olenite" to listOf(IMG_EB20_TOURMALINE_OLENITE_ROUGH, IMG_EB20_TOURMALINE_OLENITE_WILD, IMG_EB20_TOURMALINE_OLENITE_MUSEUM),
        "tourmaline-rossmanite" to listOf(IMG_EB20_TOURMALINE_ROSSMANITE_ROUGH, IMG_EB20_TOURMALINE_ROSSMANITE_WILD, IMG_EB20_TOURMALINE_ROSSMANITE_MUSEUM),
        "meionite" to listOf(IMG_EB20_MEIONITE_ROUGH, IMG_EB20_MEIONITE_WILD, IMG_EB20_MEIONITE_MUSEUM),
        "adularia" to listOf(IMG_EB20_ADULARIA_ROUGH, IMG_EB20_ADULARIA_WILD, IMG_EB20_ADULARIA_MUSEUM),
        "geuda" to listOf(IMG_EB20_GEUDA_ROUGH, IMG_EB20_GEUDA_CAB),
        "piemontite" to listOf(IMG_EB20_PIEMONTITE_ROUGH, IMG_EB20_PIEMONTITE_CAB),
        "stichtite" to listOf(IMG_EB20_STICHTITE_ROUGH, IMG_EB20_STICHTITE_CAB),
        "californite" to listOf(IMG_EB20_CALIFORNITE_ROUGH, IMG_EB20_CALIFORNITE_CAB),
        "bustamite" to listOf(IMG_EB20_BUSTAMITE_ROUGH, IMG_EB20_BUSTAMITE_WILD, IMG_EB20_BUSTAMITE_MUSEUM),
        "goosecreekite" to listOf(IMG_EB20_GOOSECREEKITE_ROUGH, IMG_EB20_GOOSECREEKITE_WILD, IMG_EB20_GOOSECREEKITE_MUSEUM),
        "stellerite" to listOf(IMG_EB20_STELLERITE_ROUGH, IMG_EB20_STELLERITE_WILD, IMG_EB20_STELLERITE_MUSEUM),
        "richterite" to listOf(IMG_EB20_RICHTERITE_ROUGH, IMG_EB20_RICHTERITE_WILD, IMG_EB20_RICHTERITE_MUSEUM),
        "chloromelanite" to listOf(IMG_EB20_CHLOROMELANITE_ROUGH, IMG_EB20_CHLOROMELANITE_CAB)
    ) }

    // ── Expansion Batch 21: Texas Cretaceous Fossils (2026-07-23) ──
    private val urlChunk9: Map<String, List<String>> by lazy { mapOf(
        "foraminifera-orbitolina" to listOf(
            "$Q/a98359f7-8b6c-4820-8d29-904553f3b232.png",
            "$Q/25c03b5c-2473-43fc-b6b7-b61206586c75.png",
            "$Q/570fa182-f50a-452f-b708-0208f4f88ace.png"
        ),
        "brachiopod-kingena" to listOf(
            "$Q/038541a9-619d-412e-afbd-8b4ca35104db.png",
            "$Q/6e8fa474-37da-45b4-8ccc-f8f2fdb632a3.png",
            "$Q/9858a5ed-7b74-4050-b3b2-d22b62cf41e3.png"
        ),
        "scallop-neithia" to listOf(
            "$Q/b029b7e3-7b8c-424d-acd7-0a8634897b78.png",
            "$Q/a60e9deb-61e8-4cb6-b864-97e3f25ca4f4.png",
            "$Q/8680ceda-10ef-46ff-9f2a-a512f6fe8862.png"
        ),
        "algae-porocystis" to listOf(
            "$Q/50326079-e623-4bea-af76-ca22695e91cb.png",
            "$Q/ac33374a-3edf-4a5d-82a9-5bef43f11e8b.png",
            "$Q/feb4f15a-7ea2-4014-8442-30611a169996.png"
        ),
        "crustacean-pagurus" to listOf(
            "$Q/5e9cdca1-1f5e-4aa0-9db7-7085f8c89b6c.png",
            "$Q/99899e52-c592-4ed8-8914-74c28da4b76e.png",
            "$Q/bfa8a280-16ec-4c0e-83f1-013927b9c650.png"
        ),
        "gastropod-gyrodes" to listOf(
            "$Q/dd88f9b5-3eaf-4e78-899d-e987acc01a54.png",
            "$Q/f65fda64-fb63-47b0-8943-f04259913ebf.png",
            "$Q/1f7c396e-a77c-4d2b-b296-80c48df3ed92.png"
        ),
        "echinoid-loriolia" to listOf(
            "$Q/9137285a-9e77-406e-963d-a939784f5ec5.png",
            "$Q/db7d6542-9511-4336-a7df-cb2875388daf.png",
            "$Q/535cab95-4ddc-4480-a55a-590a383c684a.png"
        ),
        "mussel-lima" to listOf(
            "$Q/5eccdad6-ffa4-47b6-a4bb-21b4f36a74ac.png",
            "$Q/544a40d1-474f-4615-bfc1-cbce60e190fb.png",
            "$Q/d67a5658-2396-44d4-b746-7a02429cb2d8.png"
        ),
        "echinoid-salenia" to listOf(
            "$Q/ae4e9b4d-041a-4a5f-9c00-2ed6b2328e82.png",
            "$Q/76b7d0a5-5f92-4485-a685-2788740b7737.png",
            "$Q/80829d1f-1a41-455c-ada1-e8edd4752edf.png"
        ),
        "coral-unidentified" to listOf(
            "$Q/9a68b12f-aaa7-40d1-a40b-9639a2ca61f8.png",
            "$Q/6c296a99-ddee-45e9-a245-04d176339b1d.png",
            "$Q/b1fa40c5-a9cd-45a7-add8-d92ceaf4e4c9.png"
        ),
        "gastropod-anchura" to listOf(
            "$Q/13beefb0-6e23-4d27-9cdf-4a6602bccbc3.png",
            "$Q/c7176d13-0812-4d47-9c04-3b6bfe5a1ffb.png",
            "$Q/9c2a1365-1c35-488b-95dc-66c45bd14789.png"
        ),
        "oyster-ilymatogyra" to listOf(
            "$Q/a9a03446-5b71-4cf1-aa61-93fb2486f916.png",
            "$Q/ff6f67b2-4687-463f-aeb8-44c3cddd5f89.png",
            "$Q/0f74abcb-0fc9-4e09-8058-094c350a1369.png"
        ),
        "gastropod-turritella" to listOf(
            "$Q/de797ca7-6474-4eea-80ac-d24e1646da04.png",
            "$Q/7bd91c09-6fba-49fc-863e-fe2c78e0074d.png",
            "$Q/71b700ba-2fd5-49d8-90ff-65df887d825b.png"
        ),
        "coral-parasmilia" to listOf(
            "$Q/9e6ff991-d4b8-437d-8b87-07906217571f.png",
            "$Q/949654af-3b5e-48c9-ba8b-c7336c6eeed2.png",
            "$Q/9063ad23-17fd-4781-8c37-99961cc5cf4a.png"
        ),
        "gastropod-nerinia" to listOf(
            "$Q/f229638f-9021-4961-bfd5-13f4088d0a85.png",
            "$Q/86854357-1e82-4427-acd2-e8693d72c36f.png",
            "$Q/594dc8b6-0a6a-4179-883a-48148c48f746.png"
        ),
        "bivalve-pterotrigonia" to listOf(
            "$Q/93098f5c-a8c6-4946-9194-e3a6fcd8a413.png",
            "$Q/fd34de8f-7853-4383-8c51-300b9ad0125d.png",
            "$Q/b4613402-c7ab-45c5-b90c-d2f608a6d28f.png"
        ),
        "ammonite-baculites" to listOf(
            "$Q/7242fb0c-ccfc-4447-8b67-6acf2f3c89f1.png",
            "$Q/551dc89d-7cb9-4c72-93de-bdf156cbae48.png",
            "$Q/5599a27a-97c6-44fe-b7e7-cb26a3d5a9be.png"
        ),
        "echinoid-coenholectypus" to listOf(
            "$Q/a8c52740-6a41-408d-9eb6-605415566096.png",
            "$Q/6ca9bc83-81f2-402e-82b4-7bd2a9330675.png",
            "$Q/e2f7a9ad-d312-48a4-8732-0f7a03452306.png"
        ),
        "bivalve-protocardia" to listOf(
            "$Q/a7022395-011c-4928-93ed-5ec19a251624.png",
            "$Q/0d729e12-ce6e-459e-95be-3b1717eada21.png",
            "$Q/f0f1517b-f0ab-4a59-8401-3504b5362c13.png"
        ),
        "echinoid-pilotoxaster" to listOf(
            "$Q/341d97dd-d872-46e1-ba66-236b9aa0d8cc.png",
            "$Q/0181c45d-9a0e-43bc-b48f-73b37c6ef10d.png",
            "$Q/ed6b1359-1c23-4d8b-8834-60d816d0ab63.png"
        ),
        "echinoid-phymosoma" to listOf(
            "$Q/897bc405-6f6f-4aa2-b388-188b0e5f506f.png",
            "$Q/ac27aad8-de06-446c-9622-defe81f19513.png",
            "$Q/fbc476fd-d36f-423a-8e20-28598b0d0b06.png"
        ),
        "bivalve-pectin" to listOf(
            "$Q/756a46ea-c4f2-4722-b024-bd37f69a9566.png",
            "$Q/6518bbf0-6773-4d76-b7ab-4af5c2a7a0f9.png",
            "$Q/0d15af06-d69e-4963-a6f0-80ec8b9a9ab3.png"
        ),
        "bivalve-rudist" to listOf(
            "$Q/1d32a684-29ac-448a-add3-9d535658669e.png",
            "$Q/021dc509-ccbc-41ee-b6ab-13772a962393.png",
            "$Q/bf148440-b716-4c33-874e-18ebcb6287ec.png"
        ),
        "echinoid-heteraster" to listOf(
            "$Q/3b2ad85c-4444-4a4f-86fe-9ae78a2623f5.png",
            "$Q/e8475608-a95d-43e5-ae03-d61aad9a61ec.png",
            "$Q/1cc5e4d3-ce3a-4ade-9e68-6ddf400c79eb.png"
        ),
        "oyster-pycnodonte" to listOf(
            "$Q/70b86adb-e18a-4837-a818-c936d54ff741.png",
            "$Q/e9b1cab9-ffcf-4b58-97f5-d368b2027a5c.png",
            "$Q/7219cb66-5433-4dc5-a4ca-ab1c63f0a3c4.png"
        ),
        "ammonite-engonoceras" to listOf(
            "$Q/ab9d9d94-7e75-4f29-aa49-3ea121ddfa2b.png",
            "$Q/21e462f8-a958-4958-bd67-664b35fa06a6.png",
            "$Q/d538a165-1cc4-4a27-b120-a1f3c02e9f6b.png"
        ),
        "oyster-ostrea" to listOf(
            "$Q/5ad389ad-567f-4f2a-991a-21b22da985e3.png",
            "$Q/cef63370-8981-4a31-90c5-30f91259e505.png",
            "$Q/5476d7c2-f50e-40b1-bdc6-6ea8b7f59f5e.png"
        ),
        "ammonite-budaiceras" to listOf(
            "$Q/dd8fd508-0202-42c8-86fd-4b5d70ff8050.png",
            "$Q/8fb01634-e6f7-4674-9662-3dc992d68f4d.png",
            "$Q/d1c12a92-9d79-4265-8ac2-8ebf8f4fc4a3.png"
        ),
        "oyster-texigryphaea" to listOf(
            "$Q/99a60522-97d3-4da8-83b9-53d67f146606.png",
            "$Q/fbc94a83-4884-4ca8-a8bf-b7c11b4171e1.png",
            "$Q/8d9c15a0-3e0f-4475-b9dc-11bda0e2d59c.png"
        ),
        "gastropod-leptomaria" to listOf(
            "$Q/697d8366-b716-4773-a8ef-f50f88626ca2.png",
            "$Q/7ffb17cc-ccbe-402f-985e-d7e5ded03d28.png",
            "$Q/e32fd2c0-334b-4388-88f8-226b73cd4770.png"
        ),
        "gastropod-tylostoma" to listOf(
            "$Q/9dad5fa5-512b-4458-adb2-8b63e5fc94a7.png",
            "$Q/e16b07ab-2ebd-4b49-b6e5-8bd13617ac13.png",
            "$Q/067fe26e-e3e3-40e9-9a1e-d8ba789d1008.png"
        ),
        "clam-cyprimeria" to listOf(
            "$Q/8ed3dc5c-e51e-47df-9258-ae80f379c94a.png",
            "$Q/e11f004b-7902-455e-b75a-fa1a3f4d421f.png",
            "$Q/d7da7282-ba08-46ee-beed-096a583d4932.png"
        ),
        "nautiloid-cymatoceras" to listOf(
            "$Q/6ed4eb88-b1ce-40e6-a7e6-557ba2815bee.png",
            "$Q/986dd01e-667b-43b9-b4c6-ed8cd2b243f3.png",
            "$Q/f5db69bd-32df-44f1-bd2d-6c8fd7d52ba2.png"
        ),
        "oyster-ceratostreon" to listOf(
            "$Q/dfa56684-2280-4e14-8301-61ab113feef6.png",
            "$Q/1de5338f-5ef5-4c0c-b972-307f80bf1cbd.png",
            "$Q/9848e774-b0f1-4764-b9fe-5a494f745d96.png"
        ),
        "clam-inoceramus" to listOf(
            "$Q/186e940b-1b40-44c1-8d13-870a53aeff78.png",
            "$Q/dc614748-4fee-481d-a503-c97c08f3aca1.png",
            "$Q/baa4da40-bdf8-43f8-b74d-c262e3df49ec.png"
        ),
        "ammonite-turrilites" to listOf(
            "$Q/d568e051-2ec9-4785-81ce-65dfb5db711a.png",
            "$Q/2bb55095-9e07-42ff-9c78-2e4a7008d38a.png",
            "$Q/f72ac4d7-fe99-4f33-8f84-c37680a10608.png"
        ),
        "gastropod-lunatia" to listOf(
            "$Q/c8dc8188-24e1-43d3-96a9-9468d2b6c89c.png",
            "$Q/963f8ce0-5168-4dd1-afdd-b6ab6621b871.png",
            "$Q/134eb7ea-d760-449f-9dad-64a442067269.png"
        ),
        "clam-artica" to listOf(
            "$Q/a663efde-68c7-46cf-a9fe-6205d2965a6f.png",
            "$Q/607fecc9-9062-49dd-b22b-1b75567ea9ec.png",
            "$Q/021cc2dc-805d-452b-ac64-44780615c1c4.png"
        ),
        "oyster-exogyra" to listOf(
            "$Q/e3fd6f8b-324f-4a94-80cd-e471439118cd.png",
            "$Q/5e28185c-8dd1-45dc-a977-72e9f083f355.png",
            "$Q/a11216bf-f580-4cdc-aa60-45a0d57f8551.png"
        ),
        "alabaster" to listOf(IMG_ALABASTER_ROUGH, IMG_ALABASTER_WILD, IMG_ALABASTER_MUSEUM),
        // Tile hero images used on the Home screen Explore & Learn row.
        "uv-tile" to listOf(IMG_FLUOR_FLUORITE_LW),
        "lapidary-tile" to listOf(IMG_LABRADORITE_CABOCHON)
    ) }

    val urls: Map<String, List<String>> by lazy {
        try {
            urlChunk1 + urlChunk2 + urlChunk3 + urlChunk4 + urlChunk5 + urlChunk6 + urlChunk7 + urlChunk8 + urlChunk9
        } catch (e: Throwable) {
            Log.e("SpecimenImages", "Failed to build urls map", e)
            emptyMap()
        }
    }
}
