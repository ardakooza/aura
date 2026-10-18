package com.example.data.repository

import com.example.data.model.CatalogItem
import com.example.data.model.ItemCategory
import com.example.data.model.StyleVibe
import com.example.data.model.UserGender

object CatalogRepository {

    val items: List<CatalogItem> = listOf(
        // ==========================================
        // === ŞAPKA & BAŞLIK MODELLERİ (HEADWEAR & HATS)
        // ==========================================
        CatalogItem(
            id = "head_yankees_baseball",
            name = "New Era 9FORTY Spor Kasket",
            brandOrModelRef = "New Era 9FORTY Clean Cap",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Kavisli vizörlü, ayarlanabilir mat siyah pamuklu fitilli spor kasket.",
            styleTag = "Mat Siyah Pamuk",
            material = "Ağır Gramajlı Pamuk Fitil & Kavisli Siper",
            promptPromptModifier = "photorealistic classic curved-brim baseball cap in deep matte black, sitting snugly on the crown of the head just above the eyebrows with authentic fabric stitch texture and realistic rim shadow"
        ),
        CatalogItem(
            id = "head_borsalino_fedora",
            name = "Borsalino Klasik Fötr Şapka",
            brandOrModelRef = "Borsalino Alessandria",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "İtalyan el yapımı koyu tütün rengi saf yün keçe fötr şapka ve ipek grogren kurdele.",
            styleTag = "İtalyan Yün Keçe",
            material = "Doğal Yün Keçe & İpek Kurdele",
            promptPromptModifier = "luxurious Borsalino wide-brim fedora in deep tobacco brown felt with a grosgrain silk band, casting an editorial shadow over the upper forehead and resting with natural head tilt"
        ),
        CatalogItem(
            id = "head_fisherman_beanie",
            name = "Minimalist Balıkçı Bere",
            brandOrModelRef = "Nordic Fisherman Beanie",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.MINIMALIST,
            description = "Katlamalı kulak üstü formda antrasit nervürlü kuzu yünü bere.",
            styleTag = "Kuzu Yünü Ribana",
            material = "Merinos Kuzu Yünü",
            promptPromptModifier = "modern snug-fitting charcoal ribbed fisherman beanie sitting comfortably above the ears on the upper cranium, featuring detailed yarn weave and micro-fibers"
        ),
        CatalogItem(
            id = "head_prada_bucket",
            name = "Prada Re-Nylon Kova Şapka",
            brandOrModelRef = "Prada Bucket 2HC137",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Geometrik silüetli parlak siyah teknik naylon bucket şapka.",
            styleTag = "Teknik Siyah Naylon",
            material = "Geri Dönüştürülmüş Re-Nylon",
            promptPromptModifier = "high-fashion designer bucket hat in glossy black technical nylon, resting naturally above the brow line with clean geometric silhouette and subtle studio rim specular highlights"
        ),
        CatalogItem(
            id = "head_french_beret",
            name = "Parisian Klasik Fransız Bere",
            brandOrModelRef = "Laulhère Heritage Beret",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.VINTAGE,
            description = "Yana yatık kullanılan geleneksel bordo saf keçe Fransız ressam beresi.",
            styleTag = "Bordo Saf Keçe",
            material = "100% Fransız Keçe Yünü",
            promptPromptModifier = "elegant authentic French wool beret in rich deep burgundy tilted chicly to the side on the crown of the head, framing face shape with Parisian art-house aesthetic"
        ),
        CatalogItem(
            id = "head_jacquemus_straw",
            name = "Jacquemus Geniş Hasır Şapka",
            brandOrModelRef = "Jacquemus Le Chapeau Bomba",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "Akdeniz esintili geniş saçaklı doğal altın saman hasır şapka.",
            styleTag = "Geniş Kenar Hasır",
            material = "Doğal Dokuma Hasır Elyafı",
            promptPromptModifier = "dramatic wide-brimmed golden natural woven straw sun hat gently undulating around the head with soft dappled sunlight shadows across the face"
        ),
        CatalogItem(
            id = "head_techwear_visor",
            name = "Cyberpunk Fütüristik Kasket",
            brandOrModelRef = "Neo-Tokyo Cyber Visor",
            category = ItemCategory.HEADWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.CYBER,
            description = "Mat karbon fiber dokulu, ayarlanabilir neon cyan biyeli taktik siperlik.",
            styleTag = "Karbon Fiber & Neon",
            material = "Karbon Kompozit & Taktik Dokuma",
            promptPromptModifier = "futuristic cyberpunk tactical cap with matte carbon fiber weave, subtle neon edge trim, and angular visor silhouette resting snugly over the forehead"
        ),

        // ==========================================
        // === GÖZLÜK MODELLERİ (REAL-WORLD EYEWEAR)
        // ==========================================

        // --- Erkek & Unisex Modeller ---
        CatalogItem(
            id = "eye_tf_aviator",
            name = "Tom Ford Double Bridge",
            brandOrModelRef = "Tom Ford FT0826",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "İkonik çift köprülü fırçalanmış titanyum ve duman grisi polarize camlar.",
            styleTag = "Fırçalanmış Titanyum",
            material = "Japon Titanyumu & CR-39 Lens",
            promptPromptModifier = "photorealistic Tom Ford style double-bridge titanium aviator sunglasses with subtle blue anti-reflective coating, resting precisely on the nasal bone with raytraced specular highlights and natural ear stem fitting"
        ),
        CatalogItem(
            id = "eye_gm_bold",
            name = "Gentle Monster Bold Black",
            brandOrModelRef = "Gentle Monster Bold 01",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.CYBER,
            description = "Keskin hatlı kalın mat siyah asetat çerçeve ve zifiri siyah lensler.",
            styleTag = "Kalın Mat Asetat",
            material = "İtalyan Mazzucchelli Asetat",
            promptPromptModifier = "ultra-sleek Gentle Monster style rectangular thick black acetate sunglasses with pitch-black polarized lenses, sitting seamlessly over the eye sockets with realistic ambient occlusion"
        ),
        CatalogItem(
            id = "eye_rb_wayfarer",
            name = "Klasik Siyah Wayfarer",
            brandOrModelRef = "Ray-Ban RB2140 Original",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.MINIMALIST,
            description = "Zamansız parlak siyah çerçeve ve yeşil G-15 mineral camlar.",
            styleTag = "Zamansız Klasik",
            material = "Cilalı Asetat & Mineral Cam",
            promptPromptModifier = "authentic Ray-Ban Wayfarer style classic black sunglasses with dark crystal lenses, perfectly proportioned to the face with authentic bevelled edges"
        ),
        CatalogItem(
            id = "eye_cartier_rimless",
            name = "Cartier Première Rimless",
            brandOrModelRef = "Cartier CT0332O",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "Altın kaplama cıvata detaylı, çerçevesiz dikdörtgen optik gözlük.",
            styleTag = "Çerçevesiz 18K",
            material = "18K Altın Kaplama Titanyum",
            promptPromptModifier = "luxurious Cartier style rimless rectangular eyeglasses with delicate gold hardware, clear antireflective prescription lenses, and elegant horn temple arms"
        ),
        CatalogItem(
            id = "eye_matsuda_octa",
            name = "Matsuda Geometrik Tel",
            brandOrModelRef = "Matsuda M3085",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.MINIMALIST,
            description = "Sekizgen formlu el oyması gravürlü gümüş tel optik çerçeve.",
            styleTag = "Geometrik Gümüş",
            material = "El İşçiliği Gümüş",
            promptPromptModifier = "intricate Matsuda style octagonal handcrafted silver wireframe spectacles with crystal-clear antireflective lenses and subtle filigree nose bridge"
        ),
        CatalogItem(
            id = "eye_oakley_sports",
            name = "Oakley Radar EV Path Spor",
            brandOrModelRef = "Oakley Radar EV 0OO9208",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Hafif O-Matter aerodinamik çerçeve ve Prizm Road kırmızı ayna performans lensi.",
            styleTag = "Prizm Spor Kalkanı",
            material = "O-Matter Polimer & Plutonite Lens",
            promptPromptModifier = "aerodynamic athletic performance shield sunglasses with wrapped iridescent red-orange mirror lens and matte white frame, hugging facial contours with realistic sweat-resistant rubber ear socks"
        ),
        CatalogItem(
            id = "eye_clubmaster_gold",
            name = "Ray-Ban Vintage Clubmaster",
            brandOrModelRef = "Ray-Ban RB3016 Clubmaster",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.VINTAGE,
            description = "Üst asetat kaş çizgisi ve altın metal jantlı 1950'ler entelektüel retro tasarım.",
            styleTag = "Kaplumbağa & 24K",
            material = "Asetat Kaş Çizgisi & Altın Kaplama Metal",
            promptPromptModifier = "authentic vintage browline Clubmaster sunglasses with mock tortoiseshell upper frame and polished gold wire rims, resting elegantly across the nasal bone"
        ),
        CatalogItem(
            id = "eye_neo_slim_futuristic",
            name = "Balenciaga Matrix Slim Metal",
            brandOrModelRef = "Balenciaga Neo Slim BB0157S",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.CYBER,
            description = "Ultra ince oval mat çelik çerçeve ve koyu gri dar lensler.",
            styleTag = "Ultra İnce Çelik",
            material = "Fırçalanmış Paslanmaz Çelik",
            promptPromptModifier = "ultra-narrow micro-oval black metal sunglasses inspired by The Matrix, resting low on the bridge of the nose with dark gunmetal reflections and razor-thin temples"
        ),

        // --- Kadın & Unisex Modeller ---
        CatalogItem(
            id = "eye_prada_cateye",
            name = "Prada Symbole Cat-Eye",
            brandOrModelRef = "Prada PR 17WS",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "Geometrik kesimli kalın siyah asetat ve yukarı kıvrık cat-eye formu.",
            styleTag = "Geometrik Cat-Eye",
            material = "Yüksek Parlaklıkta Asetat",
            promptPromptModifier = "chic Prada style geometric angled cat-eye sunglasses in high-gloss black with dark smoke lenses, hugging cheekbones with sharp editorial reflections"
        ),
        CatalogItem(
            id = "eye_op_round",
            name = "Oliver Peoples Gregory Peck",
            brandOrModelRef = "Oliver Peoples OV5186",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.VINTAGE,
            description = "Koyu amber kaplumbağa deseniyle entelektüel yuvarlak retro çerçeve.",
            styleTag = "Amber Kaplumbağa",
            material = "Doğal Reçine Asetat",
            promptPromptModifier = "vintage Oliver Peoples style round keyhole bridge eyeglasses in warm tortoiseshell acetate with clear lenses, resting gently on the bridge of the nose"
        ),
        CatalogItem(
            id = "eye_balenciaga_shield",
            name = "Balenciaga Mono Cyber Shield",
            brandOrModelRef = "Balenciaga BB0003S",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.CYBER,
            description = "Yüzü saran kavisli tek parça fütüristik gümüş ayna vizör.",
            styleTag = "Gümüş Ayna Vizör",
            material = "Aynalı Polikarbonat",
            promptPromptModifier = "futuristic Balenciaga style wrap-around continuous single-piece mirrored silver visor sunglasses with architectural rimless curve"
        ),
        CatalogItem(
            id = "eye_gucci_oversized",
            name = "Gucci 70s Kare Oversize",
            brandOrModelRef = "Gucci GG0034S",
            category = ItemCategory.EYEWEAR,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Büyük boy kare formlu siyah asetat ve altın geçişli saplar.",
            styleTag = "Oversized Kare",
            material = "Parlak Siyah Asetat",
            promptPromptModifier = "glamorous oversized square sunglasses with subtle gradient tint lenses, perfectly balanced across the eyebrows and cheek line"
        ),

        // ==========================================
        // === SAÇ STİLLERİ (SALON HAIRSTYLES)
        // ==========================================

        // --- Erkek Saç Stilleri ---
        CatalogItem(
            id = "hair_french_crop",
            name = "Modern Textured French Crop",
            brandOrModelRef = "Parisian Studio Crop",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.MINIMALIST,
            description = "Yanları skin fade tıraşlı, üstleri mat kil dokulu öne taranmış modern kesim.",
            styleTag = "Mat Kil Dokusu",
            material = "Doğal Saç Kökü Geçişi",
            promptPromptModifier = "contemporary textured French crop haircut with a sharp skin fade on temples and forward clay-textured layered strands blending naturally with the forehead hairline"
        ),
        CatalogItem(
            id = "hair_low_taper",
            name = "Low Taper & Textured Quiff",
            brandOrModelRef = "London Barbershop Quiff",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "Doğal hacimli yukarı ve hafif yana taranmış lüks centilmen kesimi.",
            styleTag = "Doğal Hacim",
            material = "Hacimli Saç Katmanı",
            promptPromptModifier = "sophisticated textured quiff hairstyle with clean low taper on the neck and sides, natural hair volume and satin sheen with authentic individual hair follicles"
        ),
        CatalogItem(
            id = "hair_slicked_pompadour",
            name = "Slicked Back Klasik Pompadour",
            brandOrModelRef = "Milano Classic Slick",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.VINTAGE,
            description = "Hafif saten ışıltılı, geriye doğru kusursuz taranmış ayrık saç.",
            styleTag = "Saten Centilmen",
            material = "Pürüzsüz Saç Lifleri",
            promptPromptModifier = "impeccably groomed slicked back pompadour hairstyle with clean side parting, subtle pomade shine, and seamless integration with the user's natural hairline"
        ),
        CatalogItem(
            id = "hair_military_buzz",
            name = "Keskin Line-Up Buzz Cut",
            brandOrModelRef = "Minimalist Buzz 01",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Şakak çizgisi jilet gibi milimetrik düzeltilmiş askeri kısa tıraş.",
            styleTag = "Milimetrik Fade",
            material = "Ultra Kısa Saç Yoğunluğu",
            promptPromptModifier = "ultra-clean military grade buzz cut with razor-sharp temple line-up and smooth seamless skin fade on sides, hugging the skull curvature"
        ),
        CatalogItem(
            id = "hair_sculpted_afro",
            name = "Sculpted Afro Taper Fade",
            brandOrModelRef = "Studio Afro Fade",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Üst kısımları doğal spiral bukleli, yanları kusursuz alçak fade geçişli.",
            styleTag = "Doğal Bukleli Fade",
            material = "Doğal Yoğun Bukle Katmanı",
            promptPromptModifier = "impeccably shaped compact afro hairstyle with defined spiral coil texture on top and a smooth clean gradient taper fade on temples and neckline"
        ),
        CatalogItem(
            id = "hair_italian_wavy_flow",
            name = "İtalyan Dalgalı Katmanlı Flow",
            brandOrModelRef = "Milano Wavy Flow",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.MALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "Kulak üstüne dökülen doğal parlak dalgalı orta boy lüks İtalyan kesimi.",
            styleTag = "Orta Boy Dalgalı",
            material = "İpeksi Parlak Dalgalar",
            promptPromptModifier = "luxurious medium-length natural wavy flow hairstyle falling gracefully behind the ears and framing the forehead with natural soft sheen and movement"
        ),
        CatalogItem(
            id = "hair_curly_fringe",
            name = "Modern Textured Curly Fringe",
            brandOrModelRef = "London Textured Curls",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.UNISEX,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Alna doğru düşen hareketli belirgin bukleler ve kısa ense/yanlar.",
            styleTag = "Hacimli Bukle",
            material = "Nemli Dokulu Kıvırcık Saç",
            promptPromptModifier = "playful and dynamic textured curly fringe with soft defined ringlets falling lightly across the upper forehead, tapering cleanly into the sides"
        ),

        // --- Kadın Saç Stilleri ---
        CatalogItem(
            id = "hair_curtain_bangs",
            name = "Katmanlı Perde Kahkül (Curtain)",
            brandOrModelRef = "70s French Curtain Wave",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.MINIMALIST,
            description = "Ortadan ayrılan, elmacık kemiklerini çerçeveleyen ipeksi doğal dalgalar.",
            styleTag = "İpeksi Dalga",
            material = "Doğal Saç Hacmi",
            promptPromptModifier = "soft layered curtain bangs hairstyle parted in the center, framing the cheekbones with effortless natural flowing waves and realistic hair sheen"
        ),
        CatalogItem(
            id = "hair_blunt_bob",
            name = "Keskin Çene Boyu Blunt Bob",
            brandOrModelRef = "Paris Editorial Bob",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.OLD_MONEY,
            description = "Çene hizasında milimetrik düz kesilmiş parlak küt saç silüeti.",
            styleTag = "Kusursuz Düz",
            material = "Glossy Düz Saç",
            promptPromptModifier = "sleek jawline-length blunt bob haircut, pin-straight glossy dark hair framing the jaw with sharp precision edges and natural scalp root transition"
        ),
        CatalogItem(
            id = "hair_wolf_cut",
            name = "Modern Shaggy Wolf Cut",
            brandOrModelRef = "Tokyo Shag Layered",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.STREETWEAR,
            description = "Dağınık katmanlı, havalı ve enerjik modern shaggy kesim.",
            styleTag = "Dinamik Katman",
            material = "Doğal Hareketli Katman",
            promptPromptModifier = "trendy textured wolf cut with feathered shaggy layers, face-framing wisp bangs, and natural matte movement"
        ),
        CatalogItem(
            id = "hair_pixie_wave",
            name = "Zarif Dalgalı Pixie Kesim",
            brandOrModelRef = "Audrey Modern Pixie",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.VINTAGE,
            description = "Yüz hatlarını ve gözleri öne çıkaran kısa dalgalı sofistike pixie.",
            styleTag = "Kısa Dalgalı",
            material = "Hafif Doku",
            promptPromptModifier = "sophisticated textured pixie haircut with soft sweeping micro-waves, accentuating facial bone structure and eyes"
        ),
        CatalogItem(
            id = "hair_platinum_lob",
            name = "Platin Buzulu Düz Küt Lob",
            brandOrModelRef = "Nordic Platinum Lob",
            category = ItemCategory.HAIRSTYLES,
            targetGender = UserGender.FEMALE,
            styleVibe = StyleVibe.CYBER,
            description = "Omuz hizasında dökülen fütüristik platin sarısı cam parlaklığında düz saç.",
            styleTag = "Buzul Platin Lob",
            material = "Platin Düz İpek Lifleri",
            promptPromptModifier = "striking ice-platinum blonde long bob (lob) falling straight to collarbones with ultra-smooth glass-hair reflections and seamless root blending"
        )
    )

    fun getFilteredItems(category: ItemCategory, genderFilter: UserGender?, showAll: Boolean): List<CatalogItem> {
        val categoryItems = items.filter { it.category == category }
        if (showAll || genderFilter == null || genderFilter == UserGender.UNISEX) {
            return categoryItems
        }
        return categoryItems.filter { it.targetGender == genderFilter || it.targetGender == UserGender.UNISEX }
    }
}
