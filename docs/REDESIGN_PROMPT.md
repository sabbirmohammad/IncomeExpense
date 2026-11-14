# ANTIGRAVITY MASTER PROMPT v2 — "PULSE" Design Language + Completely New Bazaar Experience
## Project: Expense Tracker (হিসাব কিতাব) — /Users/sabbir/Dev/ExpenseTracker
## Package: com.example.expensetracker | Current: v6.21 (versionCode 85) → Target: v7.0 (MAJOR)

> This prompt FULLY REPLACES any earlier redesign instructions. Ignore any previous "Living Ledger 2.0", 5-tab, parchment, FAB-based design. This is a clean-slate visual and UX direction.

---

## 0. ROLE & NON-NEGOTIABLE WORKING RULES

You are a senior Android engineer + product designer. The app is a working, 100% offline-first native Android app (Kotlin, Jetpack Compose, Material 3, SQLite schema v15). You will redesign the **entire UI/UX** and **completely rebuild the Market (Bazaar) experience**, while keeping all data, business logic and features intact. This is NOT a backend rewrite.

1. **Read before you write.** Read `AGENTS.md`/`GEMINI.md`, `AppNavigation.kt`, `NavigationScreenHost.kt`, `ModernBottomNavBar.kt`, `SideMenuDrawer.kt`, `Navigation3DScaffold.kt`, `DashboardScreen.kt`, `DashboardViewModel.kt`, everything in `ui/market/`, `MarketListParser.kt`, `UnitConverterEngine.kt`, `ExpenseRepositoryMarketCompletion.kt`, `MarketTableHelper.kt`, `AddEditTransactionSheet.kt`, `theme/*`, `AppStrings.kt`, `StringsCore.kt`, `StringsFeatures.kt` BEFORE changing anything.
2. **Baseline first:** run `./gradlew testDebugUnitTest` (471 tests must pass). Re-run after EVERY phase. Never continue on a red build.
3. **Phases (Section 11):** implement → `./gradlew assembleDebug` → `./gradlew testDebugUnitTest` → commit `v7.0-phaseN: <summary>`. No giant single change.
4. **No network dependencies, no analytics, no AI/OCR.** App stays fully offline (only existing Google Drive backup + GitHub update checker use network). The only allowed new assets are **bundled font files** (Section 3.3). No new Gradle libraries (responsiveness is done with our own `WindowClass` helper, Section 2).
5. **Do not change DB schema (stays v15)** and do not change `BackupPayload` JSON. v6.x backups must restore on v7.0. (Optional Phase 10 is the only exception.)
6. Keep existing ViewModel/Repository public contracts. If splitting `DashboardViewModel`, keep a delegating facade.
7. If an existing structural/invariant test fails because this redesign intentionally changes structure/colors, update that test to assert the NEW intent. Never delete a test just to pass. NEVER weaken: `OfflineReceiptAndAiPurgeVerificationTest` (zero AI deps), min-touch-target tests (`ChallengerPhase2Iteration2TouchTargetTest`), and "`MarketItemRow` uses `SwipeToDismissBox`".
8. When unsure: choose the simpler, faster, more one-handed, more accessible option, and record the decision in the commit message.

### Codebase constraints that still apply
- UI colors come from `LocalAppColors.current` (AppColors) — NOT raw `MaterialTheme.colorScheme`. Any standard M3 component you use must have colors explicitly mapped from `LocalAppColors`. (You MAY change the token VALUES and ADD tokens per Section 3.)
- All UI strings via `LocalAppStrings` (`AppStrings.kt`, `StringsCore.kt`, `StringsFeatures.kt`), English + Bengali. No hardcoded literals. Do NOT migrate to `strings.xml`.
- Bengali numerals toggle, Privacy Mode (`••••••`) and `tnum` tabular numbers must work in every new component. Bengali text ≥ 12sp, line-height ≥ 16sp, never clip কার/ফলা.
- 4 theme modes stay: Light, Dark, AMOLED (#000000), System.
- minSdk 24 / targetSdk 35 / compileSdk 36 / Kotlin 2.3.20 / Compose BOM 2026.03.01 — do not bump.
- StateFlow invariant (public immutable `StateFlow<T>` over private `MutableStateFlow<T>`), `collectAsStateWithLifecycle()`.
- Naming: `XxxScreen.kt`, `XxxViewModel.kt`, package `com.example.expensetracker.ui.<feature>`; shared widgets in `ui/components/`.
- Unused `androidx.navigation3.*` dependency: keep the current state-driven navigation. Remove the dependency only if a grep proves zero usage and build+tests stay green; otherwise leave and mention in the report.
- Performance: smooth on API 24 low-end phones. No whole-screen 3D transforms, no nested modal stacks, stable `key`s in lazy lists, `derivedStateOf`/`remember` where needed.

---

## 1. DESIGN CONCEPT — "PULSE"

**Pulse = modern, bold, color-blocked, big-number, thumb-first finance for Bangladeshi households.**
Think: confident large numbers, soft pill shapes, generous whitespace, each module has its own color identity, almost no hairline borders, no paper/parchment look. It must feel fresh, friendly and fast — not like a spreadsheet.

Principles:
1. **Answer first.** Every screen opens with the single most useful answer ("আজ কত খরচ করা যাবে?", "বাজারে এখন পর্যন্ত কত গেছে?").
2. **Thumb zone.** Primary actions live in the bottom 40% of the screen on phones.
3. **Color = module identity + meaning.** Each module has one hero color; income/expense colors stay semantic.
4. **Adaptive, not stretched.** Phone, large phone, foldable, tablet, landscape all get purpose-built layouts (Section 2).
5. **Bengali-first.** Layouts must be designed and tested in Bengali with Bengali numerals first, English second.
6. **Speed over decoration.** Max 2 taps for any frequent action; zero hidden gestures required.

---

## 2. RESPONSIVE & ADAPTIVE SYSTEM (CRITICAL — the previous design failed here)

### 2.1 `WindowClass` helper (no new dependency)
Create `ui/adaptive/WindowClass.kt`. Derive from the available width in dp (via `LocalConfiguration` / `BoxWithConstraints` at the root, provided through a `LocalWindowClass` CompositionLocal):
- **Compact:** width < 600dp (phones, portrait)
- **Medium:** 600dp ≤ width < 840dp (large foldable inner screen, small tablets, phones in landscape ≥600)
- **Expanded:** width ≥ 840dp (tablets, landscape tablets, desktop-mode)
Also expose `heightClass` (Compact <480dp = landscape phone, Medium 480–900, Expanded >900) and `isLandscape`.
Everything below reads `LocalWindowClass.current`. **Never hardcode widths in dp for layout**; use `fillMaxWidth`, `weight`, `widthIn(max=…)`, `LazyVerticalGrid(GridCells.Adaptive(minSize = …))`, `FlowRow`.

### 2.2 Adaptive navigation shell (`AdaptiveScaffold`)
| Class | Navigation | Content |
|---|---|---|
| Compact | Floating **bottom dock** (Section 4.1) | single column, screen padding 16dp |
| Medium | **Navigation Rail** on the left (80dp wide, icons+labels, Add button on top) | single column centered, `widthIn(max=720dp)`, or two-pane where noted; padding 24dp |
| Expanded | **Permanent side panel** on the left (280dp, icons + labels + profile at bottom, Add button as wide pill at top) | up to two panes (list + detail) inside a max content width of 1200dp, padding 32dp |
Landscape phones (heightClass Compact) use the Medium rail layout and hide the top hero collapse area to save vertical space.

### 2.3 Responsive rules every screen must follow
- Forms/sheets: Compact = bottom sheet (max height 92%); Medium/Expanded = **centered dialog**, width `min(560dp, 90%)`. Never a full-width sheet on tablets.
- Lists with cards (wallets, goals, bills, budget categories, Bazaar items catalog): use adaptive grids — Compact 1–2 columns, Medium 2–3, Expanded 3–4 (`minSize` 160–320dp as appropriate).
- Two-pane (list-detail) on Expanded and landscape Medium for: Money→Activity (list | transaction detail), Bazaar Shop Mode (items | cart/price book), Plan→Debts (people | ledger of that person), Wallets (accounts | selected account activity).
- Text must reflow, not truncate: no fixed heights on text containers; allow 2 lines for Bengali labels; `Modifier.heightIn(min=…)` instead of `height(…)`.
- Support font scale up to **200%** and display-size "Largest" without overlap/clipping.
- Numeric keypad key size = `clamp(56dp, availableWidth/4 - gaps, 72dp)`; on Expanded the keypad is capped at 360dp width and centered.
- Insets: edge-to-edge, handle status bar, navigation bar/gesture bar, display cutouts, IME (`imePadding`), foldable hinge safe area (just keep content inside `safeDrawing`).
- Must work and be verified at these sizes (add Compose `@Preview`s + a manual QA table): 320×568, 360×640, 393×852, 412×915, 600×960, 840×1280, 1280×800 (landscape tablet), 915×412 (landscape phone), plus font scale 1.0 / 1.3 / 2.0.
- Add a debug-only "responsive preview" test composable or `@Preview(device=…)` set for each main screen.

---

## 3. PULSE DESIGN TOKENS (replace Living Ledger values)

### 3.1 Color tokens (in `theme/Color.kt`, exposed via `LocalAppColors`)
Keep the token NAMES that exist (`background, surface, surfaceVariant, border, textPrimary, textSecondary, textMuted, accent, income, expense, warning, info`…) but give them the NEW values, and add the new ones.

| Token | Light | Dark | AMOLED | Use |
|---|---|---|---|---|
| background | #F4F6FB | #0B0F19 | #000000 | App canvas (cool, clean) |
| surface | #FFFFFF | #141A28 | #0A0D14 | Cards, sheets |
| surfaceVariant | #EAEEF7 | #1D2538 | #121722 | Chips, inputs, keypad keys |
| border (rarely used) | #DDE3F0 | #27304A | #1B2133 | Only for inputs/focus; NOT for cards |
| textPrimary | #0F1729 | #F2F5FF | #FFFFFF | |
| textSecondary | #4B5675 | #A6B0CC | #A6B0CC | AA on surface |
| textMuted | #6B7596 | #7F8AAA | #7F8AAA | |
| accent (brand "Mango") | #F57C00 | #FFA033 | #FFA033 | Add button, primary CTAs, active nav indicator |
| onAccent | #FFFFFF (check contrast; use #1A1200 if <4.5:1) | #1A1200 | #1A1200 | text on accent |
| income | #0E9F6E | #34D399 | #34D399 | Income, savings positive |
| expense | #E5484D | #FF7A7F | #FF7A7F | Expense, over-budget |
| transfer / info | #3B6CF6 | #7C9BFF | #7C9BFF | Transfers, analytics |
| warning | #D97706 | #FBBF24 | #FBBF24 | Due soon, 75–99% budgets |
| Module hero colors | Home #3B6CF6 · Money #7C4DFF · **Bazaar #0E9F6E (leaf green)** · Plan #F57C00 | brighter equivalents | same as dark | Hero card backgrounds / indicator tint |
| *Container tints | each semantic color at 12% over surface (light) / 18% (dark) | | | Chips, selected states, icon circles |
| scrim | #000000 @ 52% | | | |

Every text-on-color pair must meet WCAG AA (≥4.5:1 text, ≥3:1 icons/large). Fix any failing pair (adjust `onAccent`, etc.). Hero cards use a subtle 2-stop gradient of the module color (top-left → bottom-right, ±8% lightness) with white/near-white text verified for contrast.

### 3.2 Shape & elevation
- Radii: `RadiusCard = 24dp`, `RadiusSheet = 28dp`, `RadiusButton = 16dp`, `RadiusInput = 14dp`, `RadiusPill = 999dp`, `RadiusIconTile = 14dp`.
- Cards: **no hairline borders**; separation by surface contrast + tonal elevation. Soft shadow (y=2dp, blur 12dp, 6% black) only on floating elements (dock, sheets, FAB-like buttons). Dark/AMOLED: no shadows; use `surfaceVariant` contrast.

### 3.3 Typography
- Bundle **Hind Siliguri** (Regular 400, Medium 500, SemiBold 600, Bold 700; SIL OFL — include license file) in `res/font/` for excellent Bengali + Latin rendering. Fallback to `FontFamily.Default` if loading fails. Verify `tnum`; if Hind Siliguri lacks tabular figures for Latin digits, apply `tnum` only where supported and keep fixed-width alignment via `TextAlign.End` + `widthIn(min)` on amount columns.
- Scale (sp / line-height): `displayHero 44/52 Bold` (hero money number), `displayAmount 40/48 Bold` (composer), `headlineLarge 30/38 Bold`, `headlineMedium 24/32 SemiBold`, `titleLarge 20/28 SemiBold`, `titleMedium 16/24 SemiBold`, `titleSmall 14/20 SemiBold`, `bodyLarge 16/24`, `bodyMedium 14/20`, `bodySmall 12/18`, `labelLarge 14/20 SemiBold`, `labelMedium 12/16 SemiBold`, `numberKeypad 24/30 Medium`. Minimum 12sp ANYWHERE (Bengali line-height ≥ 16sp).
- Sentence case for English; no ALL-CAPS section labels.

### 3.4 Spacing & sizes
4dp grid. Screen padding: 16 (Compact) / 24 (Medium) / 32 (Expanded). Gap between cards 12 (Compact) / 16 (Medium+). Min tappable row height 56dp. **Every interactive target ≥ 48×48dp** (visual smaller is OK if target expanded with `minimumInteractiveComponentSize()`).

### 3.5 Motion
Springs (stiffness medium-low) for press/scale/expand; 200–300ms for transitions. Tab switch = 150ms crossfade; pushed screen = fade + 24dp slide; list item add/remove = `animateItem`. All animations must honor system animator scale = 0 (`LocalReduceMotion` → instant).

### 3.6 Iconography
Use `Icons.Rounded.*` (Material Icons Extended). Icons sit in 40–44dp rounded-square tinted tiles (`RadiusIconTile`) for categories, quick actions and list leading elements. Emoji stay for Bazaar stalls/products.

---

## 4. NAVIGATION & INFORMATION ARCHITECTURE

**Delete** `Navigation3DScaffold.kt`, `SideMenuDrawer.kt`, `GlobalActionChooserSheet.kt`, long-press add behavior, and the old `ModernBottomNavBar` design. 

### 4.1 Four destinations + a central Add action
| Tab | EN | BN | Contains |
|---|---|---|---|
| 1 | Home | হোম | "Today" dashboard |
| 2 | Money | টাকা | Segments: **Activity · Wallets · Insights** (Insights includes Reports/export) |
| 3 | Bazaar | বাজার | Completely new Bazaar experience (Section 6) |
| 4 | Plan | পরিকল্পনা | Segments: **Budgets · Bills · Goals · Debts (ধার ও দেনা)** |

- Settings: opened from the avatar in the Home header (pushed screen; on Expanded, the profile area at the bottom of the side panel).
- **Compact – floating dock:** a pill-shaped floating bar (height 68dp, horizontal margin 16dp, bottom margin 12dp + nav-bar inset, `RadiusPill`, `surface` with soft shadow). Layout: [Home][Money][ ＋ ][Bazaar][Plan]. The center **＋** is a 56dp circle in `accent` raised slightly (−8dp) inside the dock; single tap opens the Add Composer (Section 5.3). NO long-press requirement. On first run show a one-time coach-mark tooltip on ＋. Selected tab: icon in a pill indicator (`accent` @ 16%) + label in `accent`; unselected `textSecondary`. Labels always visible (12sp+).
- The dock hides (slide down) on pushed/immersive screens (Composer, Settings, Bazaar Shop Mode) and when the IME is open.
- Lists get bottom content padding = dock height + 24dp so nothing is hidden behind it.
- **Medium – rail / Expanded – side panel:** same 4 destinations + a prominent Add button at the top (icon-only in rail, "＋ Add / ＋ যোগ করুন" wide pill in side panel).
- Tapping the active tab scrolls to top and resets the inner segment. Plan tab shows a coral dot when a bill is overdue/debt due; Bazaar shows a green dot while a shopping trip is in progress.
- Back handling: pushed screen → pop; non-Home tab → Home; Home → exit. Top-level tabs have NO back arrow; pushed screens have ← (48dp) and no dock.
- Notification deep links (bill/budget reminders) map to Plan with the right segment. Keep old `NavigationItem` values as aliases.
- Segment controls (Money/Plan): shared `PulseSegmented` component — pill track `surfaceVariant`, selected pill `surface` with soft shadow and `textPrimary`, scrollable if >4 or overflow; each segment ≥48dp tall; remembered per tab with `rememberSaveable`.

---

## 5. SCREENS (non-Bazaar)

### 5.1 Home — "Today"
Single `LazyColumn` (on Expanded: two columns — left: hero + quick entry + timeline; right: wallets, attention, bazaar).
1. **Header:** avatar (48dp → Settings), greeting ("শুভ সকাল, Sabbir"), date, Privacy-eye toggle (48dp). Tapping a masked amount reveals it for 3 seconds.
2. **Hero card (module color gradient, RadiusCard 24):** big answer = **"Safe to spend today ৳X"** (`displayHero`), subline "of ৳Y left this month · Z days to go". Below: a segmented month bar (spent vs remaining; threshold colors Emerald <75%, Amber 75–99%, Red ≥100%) and two mini stats: Income / Spent this month. If no budget is set: show "Net balance" instead, with a "Set a monthly budget" chip. Month switcher chip (‹ Nov 2026 ›).
3. **Quick Entry bar (new feature):** a 56dp rounded input "Type: চা ৩০ / rickshaw 50 / salary 25000". On submit, `QuickEntryParser` (new, in `common/`, unit-tested incl. Bengali digits) extracts: amount (first number, Bengali/ASCII), type (income keywords: salary, বেতন, আয়, income, পেলাম; otherwise expense), account (match wallet/MFS names e.g. bkash, nagad), category guess (exact category name match → most frequent category for the same title token in history → "Uncategorized"). Show an inline confirmation row of editable chips [Category][Account][Today] + Save button; Enter/Save stores via the existing add-transaction repository API. "Full form" icon button opens the Composer prefilled.
4. **Wallet carousel:** horizontal snap pager of compact wallet cards (bank/MFS/cash gradients with brand tints; peek 24dp of next card); end card "＋ Add wallet". Tap → Money→Wallets.
5. **Attention strip:** horizontal chips (only if any): overdue/due-soon bills (≤4 days), debts due, budget categories ≥90%. Each chip tappable → right Plan segment.
6. **Today timeline:** today's transactions (max 6) as a vertical timeline with time on the left, tinted icon tile, title, category·account, amount right (tnum). Empty: "আজ এখনো কোনো খরচ নেই ✓". "See all" → Money→Activity.
7. **Bazaar mini card (only if a list/trip exists):** list name, progress, spent vs budget, button "Continue" → Bazaar.
8. **Plan glance (compact row):** Net owed ৳X (→ Debts) and top savings goal ring (→ Goals); show a single "Set up" chip when empty.

### 5.2 Money tab
Top: title + search icon + export icon; `PulseSegmented` [Activity | Wallets | Insights].
- **Activity:** summary header "In ৳X · Out ৳Y · Net ৳Z" for the active filter; ONE filter button opens a single filter sheet/dialog (period, type incl. Market trips, account, category) with an "applied filters" chip row below; day-grouped list with sticky date headers (day totals); row = icon tile, title, "category · account · time", amount (+/− sign and color); swipe-to-delete with Undo snackbar AND a visible ⋮ (48dp) menu (Edit / Duplicate / Delete / Share slip). Expanded: list-detail two-pane. Empty: starter suggestions.
- **Wallets:** hero = net worth (`displayHero`) with Bank/MFS/Cash chips; actions Transfer + Add wallet; adaptive grid of wallet cards (Compact 1 col; Medium 2; Expanded 3) with 48dp overflow menu; Account detail (balance, recent activity, Reconcile, Transfer). Keep all existing reconcile/transfer logic.
- **Insights:** period chips (Today/Week/Month/Year/All), KPI tiles (daily average, savings rate, biggest expense), category donut ⇄ payment-method donut toggle, ranking list (tap → filters Activity), 7-day bars; plus **Statements** section: period selector, "Export PDF", "Export CSV" buttons, itemized preview. Charts expose text summaries for TalkBack.

### 5.3 Add Composer (replaces `AddEditTransactionSheet`)
- Compact: near-full-height sheet (92%) – Medium/Expanded: centered dialog (560dp max). Opened by ＋ (always Expense by default, or the last-used type).
- **Header zone changes color with the type** (Expense coral / Income green / Transfer blue / Market leaf-green), containing: close ✕, 4-way full-width type switcher (খরচ · আয় · ট্রান্সফার · বাজার, each ≥48dp), and the big `displayAmount` with live expression line. Inline balance warning (not a dialog).
- **Middle zone:** (a) Category as a horizontally paged **2-row emoji/icon grid** (no nested sheet; recent/frequent first; last cell "More…" expands inline), Transfer = From→To wallet chips with swap button, Market = trip/list selector; (b) one-row chips: Wallet · Date (Today/Yesterday/−2d/−3d/Pick) · Time · Receipt (camera/gallery with thumbnail); (c) Note field (when focused, keypad hides, IME shows, Save stays visible via `imePadding`).
- **Bottom zone (thumb):** shared `PulseKeypad` (+ − × ÷, backspace, long-press clear) and a 56dp full-width **Save** button (color follows type).
- Everything fits without scrolling on 360×640 with keypad visible. System keyboard NEVER opens for the amount. Preserve: validation, edit mode prefill, balance warning logic, transfer paired transactions, market trip mode, receipt pipeline (`ReceiptImageHelper`, JPEG 85%, max 1280px), haptics, confirmation overlay, Undo snackbar.
- The same `PulseKeypad` is reused in: Bazaar Price Pad, payments, budgets, goals, transfers, reconcile.

### 5.4 Plan tab
`PulseSegmented` [Budgets | Bills | Goals | Debts]. Contextual primary button per segment in the header (and as Add button behavior in rail/panel): Budgets "＋ Budget", Bills "＋ Bill", Goals "＋ Goal", Debts "＋ Entry" + visible "Split bill" button.
- **Budgets:** hero "৳X/day safe" + monthly progress; category budgets as adaptive card grid with 8dp progress bars (threshold colors); month-start rollover prompt as a bottom sheet/dialog with 2 big buttons (Roll over / Adjust).
- **Bills:** hero (monthly commitment, active count, overdue/due soon), cards sorted by urgency; per-card primary "Pay now" (48dp), active toggle (48dp).
- **Goals:** adaptive grid of progress-ring cards; goal detail with contributions (keypad).
- **Debts:** Receivables | Payables toggle, net position banner, person cards with payment progress, record payment, WhatsApp/SMS reminder buttons (48dp), Split bill (logic unchanged). Expanded: people list | selected person's ledger.
- All empty states use `StarterEmptyState` with one-tap templates: Bills (বিদ্যুৎ, গ্যাস, ইন্টারনেট, বাসা ভাড়া, মোবাইল রিচার্জ), Goals (জরুরি তহবিল, ঈদ কেনাকাটা, হজ/ওমরাহ, ফোন/ল্যাপটপ, শিক্ষা), Budgets (বাজার, যাতায়াত, ইউটিলিটি, চিকিৎসা, শিক্ষা), Debts (ধার দিলাম, ধার নিলাম, বিল ভাগ).

### 5.5 Settings, sheets, feedback
- Settings = grouped list screen (Profile · Appearance [theme as 4 visual swatches, language, Bengali numerals, privacy] · Categories [pushed screen, not accordion] · Backup & Sync · About/Updates).
- **Replace every `Toast.makeText`** with `PulseSnackbarHost` (action: Undo/Retry/Details) or inline status. Backup & Sync uses a persistent `SyncStatusCard`: account email, last backup (relative + absolute), state chip (Synced/Pending/Failed), buttons Back up now / Restore (confirm sheet explaining the automatic safety snapshot) / Details (expands full error text). Progress is a linear indicator. Keep `DailySyncWorker` and safety snapshot logic unchanged.
- Every sheet: RadiusSheet 28dp, drag handle, 20dp padding, pinned 56dp primary button with `imePadding`, ZERO nested modals; becomes a centered dialog on Medium/Expanded.
- Destructive actions → Snackbar "Deleted · Undo" (5s).

### 5.6 Shared components to build (`ui/components/`, each with @Preview in 4 themes × EN/BN × Compact/Expanded)
`PulseKeypad`, `AmountDisplay`, `PulseSegmented`, `PulseChip` (40dp visual/48dp target), `IconTile`, `HeroCard` (gradient), `StatTile`, `SectionHeader`, `PulseTopBar`, `StarterEmptyState`, `PulseSnackbarHost`, `SyncStatusCard`, `AdaptiveScaffold`, `FloatingDock`, `AdaptiveSheet` (sheet↔dialog), `AdaptiveGrid`, `ProgressRing`, `ProgressBarThreshold`.

---

## 6. BAZAAR (MARKET) — COMPLETELY NEW DESIGN & UX

**Goal:** make the bazaar trip feel like a guided, one-handed experience: **Plan → Shop → Done**, instead of the old 3 pager tabs. Keep ALL existing capabilities (multiple named lists, WhatsApp/SMS paste parser, department stalls কাঁচাবাজার/মাছ-মাংস/দুধ-বেকারি/মুদি, swipe check-off + haptics (`SwipeToDismissBox`), price entry with historical variance, forgot-staples prompt, unit conversion, commodity price history/sparklines, atomic trip completion creating a unified expense + updating price history + master commodity records via `ExpenseRepositoryMarketCompletion`). Reuse the existing repository/DB APIs; **no schema changes**. Where the old UI used a feature, the new UI must expose it (see 6.9 mapping).

Real-world context to design for: lists often arrive as WhatsApp text from family; shopper is standing in a crowded market, holding bags, one hand free, sun glare, cash prices said verbally ("কেজি ৮০", "দুই কেজি ১৬০"), price haggling, multiple stalls in a fixed walking order, a mental budget, and things get forgotten or are unavailable.

### 6.1 Bazaar Home (the tab's landing screen — "Bazaar Hub")
Leaf-green `HeroCard` that adapts to state:
- **No active trip:** headline "আজ কি বাজার করবেন?" / "Going to the bazaar?", with 3 big action tiles (adaptive row/grid, each ≥96dp tall, icon tile + title + 1-line subtitle):
  1. **Paste list / লিস্ট পেস্ট করুন** (primary, most prominent — WhatsApp/SMS text)
  2. **Build a list / লিস্ট বানান** (opens Plan Mode with the Catalog)
  3. **Repeat a past trip / আগের বাজার আবার** (opens Past Trips → "Buy again")
- **Trip in progress:** hero shows trip name, a progress ring (done/total), **spent vs budget** bar, and a big **Resume shopping** button (→ Shop Mode).
Below the hero (all optional sections, hidden when empty):
- **My lists:** horizontal cards (name, items count, estimated total, last edited), each with ⋮ (rename, duplicate, delete, share as text). Tap → Plan Mode (or Resume if in progress). "＋ New list" card at the end.
- **Price Book search card (দামের খাতা):** a search field "Search item price (আলু, চাল…)" → opens Price Book (6.7).
- **Recent trips:** last 3 trips (date, total, items count, vs budget ±) with "Buy again" and "View".
- **Smart suggestions:** "You usually buy ডিম, পেঁয়াজ every Friday" style chips derived locally from history (frequency/day-of-week; no AI, simple counting) → tap adds to a new list. Hide if insufficient data.
Expanded layout: left column = hero + lists; right column = recent trips + price book + suggestions.

### 6.2 Step 1 — Plan Mode (List Builder)
Full-screen pushed screen (dock hidden). Top bar: ← , list name (editable inline), ⋮ (share, duplicate, delete), and a **Start shopping** button that becomes primary when ≥1 item exists.
Layout (Compact: single column with a sticky bottom bar; Medium/Expanded: two panes — left = my list, right = Catalog):
- **Smart Add bar (sticky near the thumb zone, above the bottom bar):** one text field "Add item… e.g. আলু ২ কেজি". Live parsing with the existing `MarketListParser` (name + quantity + unit). Autocomplete dropdown from product history/master commodities showing last price & usual unit; tapping a suggestion adds immediately. Press Enter to add; multiple adds in a row keep focus.
- **Catalog tab/pane:** adaptive grid of emoji tiles grouped by stall (🥬 কাঁচাবাজার, 🐟 মাছ ও মাংস, 🥛 দুধ ও বেকারি, 🧂 মুদি সদাই) using the app's existing commodity seeds; tap = add 1 default unit; tap again = increase; long-press NOT required. Items already on the list show a count badge. Search field at top filters tiles.
- **My list pane:** items grouped by stall with collapsible stall headers (stall emoji, name, count). Each row (min 64dp): name, quantity stepper (− qty +, 48dp targets; unit chip tappable → unit picker with kg/g/L/ml/pc/dozen/হালি via `UnitConverterEngine`), estimated price (from last price) right-aligned in muted text. Swipe left to remove (Undo snackbar). Auto-assign stall via the existing department mapping; tap stall chip on a row to change it.
- **Estimate & budget bar (sticky):** "Estimated ৳1,840" and optional **Budget cap** chip ("Set budget" → keypad). If estimate > budget, the bar turns amber with "৳160 over".
- **Paste / import** (also reachable from the hub): bottom sheet/dialog "Paste list". On open, if the clipboard contains text that parses into ≥2 items, show a prominent banner "We found a list in your clipboard — Import?" (tapping imports; clipboard is read only on user action on the sheet). Parsed result is shown as **editable preview rows** (name, qty, unit, stall chip); lines the parser couldn't understand are flagged in amber with inline fix field; "Create list (N items)" button. Never silently drop lines.
- **Share list:** "Share as text" produces a clean WhatsApp-friendly list (grouped by stall, with checkboxes □). 
- Auto-save continuously (no explicit Save).

### 6.3 Step 2 — Shop Mode (immersive "Shopping Mode")
Full-screen pushed screen; bottom dock hidden; **keep screen awake** (`FLAG_KEEP_SCREEN_ON` while visible, cleared on exit); high-contrast option follows theme; designed for one-handed use in glare. Layout:
1. **Top strip (compact, 56dp):** ← (exit confirm only if trip has data: "Pause trip — progress is saved"), list name, progress "5/12", and a **budget bar** (spent / cap, thresholds Emerald/Amber/Red). If no cap: show spent only.
2. **Stall route (sticky chips row):** horizontally scrollable chips for each stall with counts "🥬 3/6". Tap = scroll to that stall's section; the current visible stall is highlighted. Chips for fully-done stalls turn green with ✓. Users can long-press-free reorder? NO — keep fixed order; instead allow a "Walking order" setting later (not now).
3. **Items list (single column on Compact; two columns on Medium; on Expanded: items in left pane and the Cart/Price Book in a right pane):** sections per stall. Each item card (min **72dp**, `RadiusCard`): large check circle (visual 32dp, touch 56dp), name (titleMedium), qty+unit chip, and right side shows the bought price (once done) or the **last price hint** ("গতবার ৳80/কেজি") in muted text.
   - **Tap the card → Price Pad (6.4).**
   - **Swipe right (SwipeToDismissBox, emerald reveal + haptic) = "Done at last price"** one-gesture check-off (uses last known price × qty; if none exists, opens the Price Pad instead).
   - **Swipe left = "Not available / Skip"** (grey reveal): item moves to a collapsed **"Missed (N)"** section at the bottom of the list, shown dimmed. If the existing model has no skipped state, keep skip state in the session (`rememberSaveable`/ViewModel) and use it at checkout to offer carrying items over (6.5) — do NOT change the DB schema.
   - Done items collapse into a compact struck-through row at the end of their stall section (tap to edit price; swipe right again to undo).
4. **Forgot staples strip:** once ≥3 items are done, show a dismissible chip row above the list: "Forgot? লবণ · কাঁচা মরিচ · লেবু" (existing staples logic). Tap a chip adds it as an item in the correct stall and highlights it.
5. **Cart bar (bottom, persistent, 88dp, `surface`, soft shadow, `RadiusSheet` top corners):** left: "Spent ৳1,120" (`titleLarge`) + "Left ৳880" (if cap); center: done/total count; right: **Checkout** button (56dp, leaf-green) enabled when ≥1 item done. **Swipe up / tap the bar** expands a "Receipt" bottom panel (mini bill: each bought item, qty, price, line total, running total, editable). On Expanded this panel is the permanent right pane.
6. **Quick add item:** a small "＋ Add item" button at the end of each stall section (inline text field, no sheet) for things remembered mid-trip.

### 6.4 The Price Pad (new price entry — fast, thumb-zone, Bangladeshi pricing habits)
A compact bottom sheet (Compact) / popover dialog (Medium+), opened on item tap. No nested modals. Contents top→bottom:
- Item header: emoji, name, qty+unit (tap to change qty/unit inline using steppers).
- **Mode toggle (pill, 48dp):** **"Per unit / প্রতি কেজি"** | **"Total paid / মোট দাম"**. Default = the mode used last for that item. Typing 80 in per-unit mode with 2 kg shows live "= ৳160"; typing 160 in total mode shows "= ৳80/kg". Use `UnitConverterEngine` for unit math (e.g., 500g at ৳120/kg = ৳60).
- **Big amount display** (`displayAmount`) pre-filled with last price (selected so first key replaces it).
- **Smart info chips (single row, scrollable):** "গতবার ৳80", "গড় ৳78", "সর্বনিম্ন ৳70", and a **variance badge** that updates live: ▲ ৳10 (12%) more than last time (amber/red) or ▼ cheaper (green) — replaces the old variance dialog alert. A tiny sparkline (last 6 prices) sits next to the chips.
- **Quick price chips:** "same as last", "+5", "+10", "−5" (adjust amount in one tap).
- `PulseKeypad` (with +, −, ×, ÷ for things like "৩০+৪০") and full-width **Done ✓** button (56dp). Enter advances to the next unchecked item automatically (setting "Auto-advance", default ON) with a subtle scroll animation.
- Secondary actions row: "Not available" (skip), "Remove item".
- Haptic tick per key, success haptic on Done.

### 6.5 Step 3 — Checkout & Done
**Checkout sheet** (Compact: bottom sheet; Medium+: centered dialog), single level:
- Summary: items bought N, skipped M, **Total** (sum, editable via keypad — to handle rounding/bargaining), **Other costs** optional field (rickshaw/bag, added to total, default 0).
- **Pay from:** wallet selector as horizontal chips (default last-used wallet; shows balance; inline warning if balance < total).
- Date/time chip (default now), optional note.
- If M>0: toggle **"Carry over unbought items to a new list"** (ON by default; uses the existing create-list API, name "<list name> – বাকি").
- Primary button **"Complete trip · ৳1,920"** → calls the existing atomic completion (creates the unified expense, updates price history + master commodity records). Disable the button while processing; handle failure with a Retry snackbar (no data loss).
**Done screen (celebration, full-screen):** check animation + existing `ConfettiCelebration` (respect reduce-motion), big total, insights lines computed locally: "৳80 under budget", "3 items cheaper than usual", "2 items pricier (↑)". Buttons: **Share receipt** (reuse the slip/WhatsApp share), **View in Ledger**, **Back to Bazaar**. The trip appears in Recent trips.

### 6.6 Past Trips & "Buy again"
Screen reachable from the Hub ("See all"): list of completed trips (date, total, item count, vs budget), search by item name; trip detail = receipt-style view; **Buy again** clones the trip into a new list prefilled with items (quantities kept, estimated prices from latest history). Reuse existing market transactions/history data.

### 6.7 Price Book (দামের খাতা) — replaces the old "Commodity price intelligence" tab
- Search field + "Popular" chips; results = adaptive list/grid of commodity cards: name, latest price/unit, trend arrow & % vs 30-day average, mini sparkline, badge **"ভালো সময় / Good time to buy"** when current < average by ≥8%, **"দাম বেশি / Pricey"** when > average by ≥8%.
- Detail view (sheet/dialog or right pane on Expanded): line chart of price history (7/30/90 days toggle) using the existing custom Canvas chart approach (no new library), min/avg/max, last 5 entries (date, price, qty), and "Add to list" button.
- Uses existing price history tables; no schema change.

### 6.8 Empty, error and edge states
- Empty hub: friendly illustration-free layout with the 3 action tiles and a sample list suggestion ("Try: Weekly staples").
- No parser result: show the preview with all lines flagged + "Add manually".
- Interrupted trip (app killed): state persists; Hub shows "Resume shopping" with a sticky message "Trip saved automatically".
- Insufficient balance at checkout: inline warning; allow continue (existing behavior).
- Large lists (100+ items): lazy lists with stable keys; sticky headers; no jank.

### 6.9 Feature-parity mapping (must all remain reachable)
| Old feature | New location |
|---|---|
| Active checklist with stalls | Shop Mode |
| 1-swipe check-off + haptic (`SwipeToDismissBox`) | Shop Mode item cards (swipe right) |
| In-store dock (spent, completed, checkout) | Cart bar |
| Price entry dialog + keypad + operator bar + variance alert | Price Pad |
| Forgot staples prompt | Forgot strip in Shop Mode |
| Commodity price intelligence + sparklines | Price Book + Price Pad chips |
| Notepad list wizard + WhatsApp bulk paste | Plan Mode + Paste sheet |
| Multiple named lists | Hub "My lists" |
| Atomic trip completion → unified expense | Checkout sheet → existing completion API |
| Unit conversion | Price Pad + qty unit picker |
Update the structural tests that referenced old Market UI files; keep `MarketItemRow` using `SwipeToDismissBox` (it may be renamed/moved but the invariant test must still pass for the new row).

### 6.10 Bazaar responsiveness summary
Compact: Hub single column; Plan Mode single list with Catalog as a toggle (List | Catalog segmented); Shop Mode single column + cart bar. Medium: Plan Mode two-pane; Shop Mode items + collapsible receipt panel. Expanded: Hub two columns; Plan Mode (list | catalog); Shop Mode (items | receipt+price book). Landscape phone: Shop Mode uses the Medium layout with the stall chips as a vertical rail.

---

## 7. ACCESSIBILITY (must pass)
- Every interactive element ≥ 48×48dp (fix `MarketSegmentedPills`, `AccountCardMenu`, `FilterChips` equivalents).
- Meaningful localized `contentDescription` for every informational icon; decorative = null with merged parent semantics. Merge row semantics so TalkBack reads one sentence per row ("চা, খাবার, নগদ, ৩০ টাকা খরচ, বিকাল ৩:৪০").
- `selected`/`stateDescription` on tabs, chips, toggles; roles set (Tab, RadioButton, Switch, Button).
- Color never the only signal (use ▲▼, +/−, ✓ icons).
- Charts have text summaries. Keypad keys focusable via D-pad.
- Font scale 200% OK; reduce-motion respected; contrast AA in all 4 themes.

## 8. THINGS NOT TO DO
No Firebase/ads/analytics/AI/OCR; no network calls (other than existing Drive/GitHub); no schema or backup-format changes; no Toasts; no nested sheets/dialogs; no 3D screen transforms; no hardcoded strings/colors/widths in composables; don't remove any working feature (map every one per 6.9 and the audit's section 4.1); don't break notification deep links.

## 9. STRINGS
Add English + Bengali for every new label (tabs: Home/হোম, Money/টাকা, Bazaar/বাজার, Plan/পরিকল্পনা; Price Book/দামের খাতা; Shop Mode/বাজার মোড; Complete trip/বাজার শেষ করুন; etc.). Use natural Bengali, not literal machine translation. Respect the Bengali numerals toggle in every new text including keypad labels, chips, badges and charts.

## 10. TESTS TO ADD
- `QuickEntryParserTest` (Bengali/ASCII digits, income/expense detection, account/category guess, edge cases).
- `WindowClassTest` (breakpoints 599/600/839/840, landscape).
- Keypad expression/formatting tests; Price Pad math tests (per-unit ⇄ total, unit conversion, variance %, good-time-to-buy threshold).
- Shop Mode state tests (skip, undo, carry-over list creation, auto-advance, checkout total with other costs).
- Navigation tests (alias mapping for notification routes, back handling).
- Touch-target and contrast token tests (update existing, don't weaken).
- Backup/restore round-trip with a v6.21 payload.

---

## 11. EXECUTION PLAN (do in order; build + test + commit after each)

**Phase 1 — Foundation:** `WindowClass`/`LocalWindowClass`, new tokens (colors, shapes, spacing), bundled Hind Siliguri + typography, `LocalReduceMotion`, shared components (5.6) with previews across themes/sizes.
**Phase 2 — Navigation shell:** `AdaptiveScaffold` with floating dock / rail / side panel; 4 tabs; remove 3D drawer/chooser; back handling; deep-link aliases; segment control.
**Phase 3 — Bazaar (priority):** Hub → Plan Mode → Shop Mode + Price Pad → Checkout/Done → Past Trips → Price Book. Update/replace old market UI files and structural tests. Verify atomic completion unchanged.
**Phase 4 — Add Composer + Quick Entry** (5.3, 5.1-3).
**Phase 5 — Home** (5.1).
**Phase 6 — Money tab** (Activity, Wallets, Insights/Statements).
**Phase 7 — Plan tab** (Budgets, Bills, Goals, Debts) + starter templates.
**Phase 8 — Settings, sheets, Snackbar/SyncStatusCard**, remove all Toasts.
**Phase 9 — Responsive & accessibility hardening:** verify every screen at the 8 sizes + font scales (Section 2.3), fix overflow/clipping, contrast, touch targets, TalkBack semantics.
**Phase 10 (OPTIONAL, only if all green):** link transfer legs with nullable `transfer_pair_id` (schema v16, safe ALTER, migration + backup round-trip tests). If any doubt, skip and report.
**Phase 11 — Release:** manual QA checklist, full tests (0 failures), `./scripts/bump_version.sh major "PULSE redesign + new Bazaar experience"`, ensure 3-point sync (`build.gradle.kts` versionCode/versionName, `AppConstants.kt APP_VERSION`, annotated tag `v7.0`), commit and push to `origin main` per `AGENTS.md`.

---

## 12. ACCEPTANCE CHECKLIST (report PASS/FAIL for each with evidence)
Responsive: [ ] Compact/Medium/Expanded layouts implemented (dock / rail / side panel) [ ] No horizontal overflow or clipped text at all 8 test sizes [ ] 200% font scale OK [ ] Sheets become dialogs on Medium+ [ ] Two-pane layouts on Expanded where specified.
Navigation: [ ] 4 tabs + center Add; no 3D drawer, no long-press needed [ ] Notification deep links work [ ] Back behavior correct.
Bazaar: [ ] Hub, Plan Mode, Shop Mode, Price Pad, Checkout, Done, Past Trips, Price Book all implemented [ ] Paste import with editable preview [ ] Per-unit/total toggle + live math + variance badge [ ] Swipe-right done / swipe-left skip + carry-over [ ] Forgot staples [ ] Cart bar + receipt panel [ ] Screen stays awake in Shop Mode [ ] Atomic completion unchanged and verified [ ] Every row of the 6.9 table reachable.
General: [ ] Quick Entry works (Bengali digits) [ ] Composer fits 360×640 with keypad, no system keyboard for amount [ ] No `Toast.makeText` left (grep 0) [ ] All colors via `LocalAppColors`, AA contrast in 4 themes [ ] Privacy Mode + Bengali numerals everywhere [ ] ≥48dp targets, TalkBack semantics [ ] DB schema v15 and v6.21 backup restores [ ] 0 test failures, no new warnings [ ] Version synced in 3 places.

## 13. FINAL REPORT FORMAT
Output: (1) phases completed + commit hashes; (2) files added/deleted/renamed; (3) tests added/changed and why; (4) the checklist above with PASS/FAIL + evidence (screenshots or test names where possible); (5) deviations from this spec and why; (6) follow-ups. Never claim PASS for anything not verified by build, test, or inspection.

BEGIN with Phase 1. First, summarize your understanding of this plan in ≤12 lines and list any files you could not find, then start.
