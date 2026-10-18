package com.example.data.repository

import com.example.data.model.CatalogItem
import com.example.data.model.ItemCategory
import com.example.data.model.StyleVibe
import com.example.data.model.UserGender

object CatalogRepository {

    val items: List<CatalogItem> = listOf(
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
