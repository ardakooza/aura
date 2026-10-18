# Aura Studio 4.0 - Drag & Drop Staging, Shimmer AI, Room Persistence & High-Fashion Suite

Major upgrade specification for Aura Studio implementing drag-and-drop interactions from the product shelf onto the portrait viewport, an animated linear shimmer loading effect over the portrait, persistent Room database storage for user onboarding preferences, a Vogue-inspired geometric fashion brand emblem, and signature reference widgets (floating bottom navigation dock and dot-matrix look calendar).

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions synthesize your answers from Phase 1:

- **Drag & Drop to Portrait**:
  - Users can press and drag items from the product shelf directly onto the portrait viewport with a visual drop-target indicator and haptic drop feedback.
  - Dropped items enter the **"Deneme Rafı" (Staging Shelf)**.
  - The Deneme Rafı remains hidden initially and smoothly slides/expands into view once the first item is added.
- **Vogue-Inspired High Fashion Logo**:
  - Replaces generic iconography with an editorial Vogue-style geometric typographic emblem (`AURA` haute couture monogram with sharp serifs and geometric framing).
- **Dot-Matrix Calendar & Bottom Floating Dock**:
  - **Bottom Floating Dock**: Floating pill navigation dock at the bottom of the screen with rounded icon tabs (Studio, Calendar/Matrix, Lookbook) directly matching `IMG_20261007_232842_233.jpg`.
  - **Dot-Matrix Style Calendar**: Visual matrix widget showing try-on history, daily style logs, and completion dots directly matching `IMG_20261007_232842_316.jpg`.
- **Shimmer Loading Animation**:
  - High-precision gradient sweep animation traveling diagonally across the portrait area while AI analyzes landmarks or renders the try-on composite.
- **Room Database Integration**:
  - Full local SQLite persistence via Room (`UserProfileEntity`, `UserProfileDao`, and `AuraDatabase`) to store gender, age, and preferred styles.

---

## 1. Overview & Core Concept

- **Atelier Experience**: Transforms the app into a tactile fashion studio where users physically drag curated 3D accessories onto their portrait, watch an editorial shimmer scan, and track their daily looks in a minimalist dot matrix.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Onboarding & Room Storage**:
   - On first launch, user selects Gender, Age, and Styles.
   - Saved automatically to the local Room database (`AuraDatabase`) and observed as a `StateFlow`.
   - Modifiable anytime via the profile/settings modal.

2. **Drag & Drop Workflow**:
   - User uploads selfie/portrait.
   - Shelf displays real 3D Eyewear and Hairstyles.
   - User drags an item card upward toward the portrait canvas.
   - Portrait canvas pulses with an illuminated drop border (`Drop Target Active`).
   - Releasing adds the piece to the Staging Shelf (`Deneme Rafı`), which animates into view directly beneath the canvas.

3. **Shimmer AI Synthesis**:
   - User taps "Üret ve Puanla".
   - Portrait viewport transitions into an editorial monochromatic shimmer wave with progress stages ("Yüz geometrisi taranıyor...", "3D ışıklandırma simüle ediliyor...").
   - Result renders with the Before/After split slider and top-right Aura Quality Badge.

4. **Dot-Matrix Style Tracker & Floating Dock**:
   - Bottom floating dock lets user switch between **Stüdyo**, **Stil Takvimi (Dot Matrix)**, and **Lookbook**.
   - Dot-Matrix view displays a monthly 7-day grid with luminous dots indicating saved looks and style activity.

---

## 3. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                 Aura Studio UI (Compose)                    │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │     Portrait Viewport + Shimmer Scan + Drop Target    │  │
│  └───────────────────────────▲───────────────────────────┘  │
│                              │ (Drag & Drop Gesture)        │
│  ┌───────────────────────────┴───────────────────────────┐  │
│  │   Deneme Rafı (Hidden -> Slides in on item drop)      │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │   Product Shelf (Draggable 3D Eyewear & Hairstyles)   │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │   Floating Bottom Dock (Studio / Calendar / Lookbook) │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   Room Database Layer                       │
│                                                             │
│  ┌───────────────────────┐    ┌──────────────────────────┐  │
│  │   UserProfileEntity   │    │      UserProfileDao      │  │
│  │  (gender, age, styles)│    │  (Flow<UserProfileEntity>│  │
│  └───────────┬───────────┘    └────────────┬─────────────┘  │
│              │                             │                │
│              ▼                             ▼                │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           AuraDatabase : RoomDatabase                 │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Component Details
1. **`AuraDatabase` & `UserProfileDao`**:
   - Manages user demographic and style state with reactive `Flow` updates.
2. **`ShimmerPortraitOverlay`**:
   - Linear brush gradient with an infinite transition offset sweeping across the canvas.
3. **`DragDropCanvas`**:
   - Detects drag gesture translations from shelf items and validates drop inside the portrait bounds.
4. **`DotMatrixCalendarWidget`**:
   - Renders month/day progress matrix with luminous dots inspired by the user's reference screenshots.
5. **`FloatingBottomDock`**:
   - Floating navigation pill anchored to bottom insets.
