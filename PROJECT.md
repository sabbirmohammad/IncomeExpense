# Project: হিসাব কিতাব (Expense Tracker) — v7.0 "Refined Ledger"

## 1. Project Overview & Vision
**হিসাব কিতাব (Expense Tracker)** is a privacy-first, 100% offline-first personal financial management and household economy mobile application crafted specifically for Bangladesh and the broader South Asian socioeconomic context.

- **Current Active Branch**: `redesign/v7`
- **Current Milestone**: Version 7.0 ("Refined Ledger" / Living Ledger 2.0 UI/UX Modernization)
- **Primary Source of Truth**: `docs/redesign/` (`1_REDESIGN_BRIEF.md`, `2_DESIGN_SYSTEM_V7.md`, `3_UX_FLOWS.md`, `4_COMPONENT_SPEC.md`, `5_ANTIGRAVITY_PROMPTS.md`, `6_SCREENSHOT_FINDINGS_AND_PATCH.md`, `APP_AUDIT.md`)
- **App Governance**: `AGENTS.md` (and `GEMINI.md`)

---

## 2. Architecture & Design System

### Layer 1: Foundations, Tokens & Theming (`theme/`)
- **4 Crafted Themes**: System Dark (`#0E1116`), AMOLED True Black (`#000000`), Crisp Light (`#F9F8F5`), and Nature Soft Green.
- **Semantic Tokens via `LocalAppColors`**:
  - `surfaceRaised`: Elevated card surfaces (`#1B212B` Dark, `#FFFFFF` Light, `#0F131B` AMOLED).
  - `accentContainer`: Positive/income chip backgrounds (`#12302A` Dark, `#E3F4EC` Light).
  - `expenseContainer`: Expense chip and warning backgrounds (`#3A1820` Dark, `#FCE8EC` Light).
  - `warningContainer`: Due-soon and 75–99% budget warning backgrounds (`#3A2D12` Dark, `#FBF0DC` Light).
  - `infoContainer`: Transfers and info highlight backgrounds (`#1F2147` Dark, `#E9E9FB` Light).
  - `scrim`, `onAccent`, `focusRing`: Dedicated interactive and contrast tokens.
- **Harmonized Geometry**: `RadiusL` (24dp), `RadiusM` (16dp), `RadiusS` (10dp), `RadiusFull` (999dp).
- **Surface Hierarchy**: Max 2 nesting levels; tonal surface separation in Dark/AMOLED (0 borders), crisp 0.5dp/1dp hairlines in Light mode.
- **Universal Tabular Figures (`tnum`)**: Monospace digit widths globally enforced on all amounts, balances, totals, and timestamps via `fontFeatureSettings = "tnum"`.
- **Universal Bengali Typography**: Minimum $\ge 12\text{sp}$ font size and $\ge 16\text{sp}$ line height with zero text clipping across English and Bengali strings.

### Layer 2: Core Navigation & App Shell (`ui/navigation/`, `ui/components/`)
- **AppScaffold (`AppScaffold.kt`)**: Pinned root container with uniform `contentPadding.bottom` (80–100dp) ensuring bottom items are never occluded by navigation docks.
- **Modern 5-Tab Bottom Dock (`ModernBottomNavBar.kt`)**: Clean 64dp opaque bottom bar with:
  1. **Home** — Daily financial overview and action hub.
  2. **Ledger (খাতা)** — Full transaction ledger, filters, search, and integrated reports.
  3. **+ (Add)** — Fluid 1-tap addition via context-aware extended FAB.
  4. **Bazaar (বাজার)** — Dedicated market checklist, shop HUD, and price intelligence.
  5. **Plan (পরিকল্পনা)** — Unified money hub: Budgets, Goals, Bills, and Debts.
- **AppAddFab (`AppAddFab.kt`)**: Elevated 56dp primary emerald pill FAB opening the transaction sheet in 1 tap without nested menus or long-press gestures.
- **Removed Legacy 3D Drawer**: Completely eliminated 3D perspective rotation scaffold and right-edge side drawer in favor of direct 1-tap navigation.

### Layer 3: Feature Modules & Decomposed Components
- **Home / Dashboard (`ui/dashboard/`)**:
  - Compact Hero Card ($\le 200\text{dp}$): Left-aligned Net Balance with secondary spending wallet balance switch.
  - Safe to Spend Row: Daily discretionary spending allowance calculated from monthly budget commitments.
  - 4 Quick Action Buttons: Accounts, Bills, Due/Lend, and Budgets with uniform `accentContainer` tinting and $\ge 48\text{dp}$ touch targets.
  - Above-The-Fold Transactions: At least 3 recent transactions visible without scrolling on 6" screens.
  - Collapsible Below-The-Fold Accordion Widgets: Active Market Lists, Debt Ledger, Savings Goals, and 7-day spending trends.
  - New User Onboarding Checklist: Progressive onboarding checklist for first-time users.
- **Transactions & Ledger (`ui/transactions/`, `ui/navigation/LedgerScreen.kt`)**:
  - Unified 3-Segment Hub: Transactions · Insights (Analytics) · Reports via top segmented control.
  - Grouped Daily Surface: Clean daily headers (`Today / Yesterday / Mon, 9 Nov` + day net total) with dividers.
  - 72dp Touch-Optimized Rows: 2-line title wrapping, category icons, payment method badge, and right-aligned amounts in `tnum`.
  - Filter Bar with Active Count: One filter button with active-count badge + removable filter chips.
  - Rich Transaction Detail Sheet: Full receipt breakdown with undoable deletion snackbar.
- **Add / Edit Transaction Sheet (`ui/transactions/AddEditTransactionSheet.kt`)**:
  - Full-Bleed Modal Sheet: Compact amount box ($\le 140\text{dp}$), custom `AmountKeypad` with inline math solver (`=`), category smart prediction, horizontal account chips, 48dp receipt attachment row, and sticky bottom-pinned CTA.
- **Bazaar & Market Hub (`ui/market/`)**:
  - 3-Tab Segmented Control: **Lists (ফর্দ)** · **Prices (দরদাম)** · **New (নতুন)**.
  - In-Store Shop HUD (`MarketShopModeView.kt`): Sticky running-total dock, live progress counter, and single-tap checkout.
  - Normalized Commodity Prices: NFC-normalized Bengali text deduplication, price variance badges (`▼ ৳5 vs last`), and historical sparklines.
  - WhatsApp / Bulk List Importer: Interactive tag chips (`MarketParsedTagChip.kt`) with 1-tap staple recommendations.
- **Money & Plan Hub (`ui/money/`, `ui/navigation/PlanScreen.kt`)**:
  - **Accounts & Wallets**: Net worth hero card, Cash/Bank/MFS breakdowns, balance reconciliation dialog, and transfer sheet.
  - **Budgets & Savings**: Monthly budget status bar, category warning cards (75–99% warning, 100%+ overrun), savings goals with circular progress rings and starter templates.
  - **Recurring Bills**: Monthly commitments hero, overdue/upcoming badges, and 1-tap "Pay Now" flow.
  - **Money Owed (Debts)**: Net position hero, dual tabs for Receivables & Payables, installment tracking, and bill splitter.
- **Insights & Reports (`ui/analytics/`, `ui/reports/`)**:
  - Interactive 160dp donut charts with tap segment highlight and center balance updates.
  - Dual income/expense spending bar charts with today indicator dot.
  - Monotone cubic Bézier sparklines for 7-day and monthly trends.
  - Live statement preview card with dual $\ge 48\text{dp}$ export buttons (**Export PDF** & **Export CSV**).
- **Settings & Google Drive Backup (`ui/settings/`)**:
  - 7 structured sections: Profile, Appearance, Language & Numerals, Categories, Backup & Sync, Data & Storage, About.
  - Persistent `SyncStatusCard` with live Google Drive sync badges, `tnum` timestamps, and expandable diagnostics.
  - Zero Toast Policy: Replaced ephemeral toasts with actionable `AppSnackbarHost` snackbars.
- **Onboarding (`ui/onboarding/`)**:
  - 3-screen skippable onboarding walkthrough explaining Track, Plan, and Bazaar workflows.

### Layer 4: Data Layer, Database & Persistence (`data/`)
- **SQLite Database Version 15**: WAL mode and foreign keys enabled.
- **Modular Table Delegates**: `AccountTableHelper`, `CategoryTableHelper`, `ExpenseDatabaseHelper`, `MarketTableHelper`, `MasterProductTableHelper`, `PayableTableHelper`, `ProductPriceTableHelper`, `ReceivableTableHelper`, `RecurringBillTableHelper`, `DatabaseMigrations`, `DatabaseRestoreHelper`, `DatabaseSeedData`.
- **Modular Repository Delegates**: `ExpenseRepository` orchestrator with `ExpenseRepositoryAccountsAndBudgets`, `ExpenseRepositoryBackup`, `ExpenseRepositoryMarketCompletion`.
- **Asynchronous Architecture**: Kotlin Coroutines, `StateFlow`, lifecycle-aware state collection (`collectAsStateWithLifecycle`).
- **Cloud Backup**: Direct Google Drive `AppDataFolder` backup with `GoogleAuthHelper` and `GoogleDriveSyncManager`. Scheduled background sync via `DailySyncWorker`.

---

## 3. Redesign v7 Execution & Phase Status

| Phase | Scope | Status | Verification |
|:---:|---|:---:|---|
| **Phase 1** | Baseline & Design Tokens (`theme/`, base components) | **DONE** | Tokens, `AppNumericKeypad`, `StatTile`, `AppTopBar`, `StarterEmptyState`, `SegmentedControl` |
| **Phase 2** | Navigation Overhaul (5 Tabs, `AppAddFab`, `AppScaffold`, `LedgerScreen`, `PlanScreen`) | **DONE** | Removed 3D drawer; 5 top-level tabs; zero occluded content |
| **Phase 3** | Add/Edit Transaction & `AmountKeypad` Flow | **DONE** | Inline math solver, category prediction, account chips, single-tap CTA |
| **Phase 4** | Home Dashboard & Market Entry Completion | **DONE** | Compact hero ($\le 200\text{dp}$), safe-to-spend, 4 quick actions, recent transactions above fold |
| **Phase 5** | Transactions Ledger & Market Hub Segmented View | **DONE** | Grouped day surfaces, 72dp rows, active filter count, Lists/Prices/New tabs |
| **Phase 6** | Money Hub Consolidation (Accounts, Budgets, Bills, Debts) | **DONE** | 4-segment plan hub, net worth hero, warning cards, installment tracking |
| **Phase 7** | Analytics & Reports Refinement | **DONE** | 160dp donut charts, spending bars, statement preview card, PDF/CSV export |
| **Phase 8** | Settings, Cloud Sync & Onboarding Overhaul | **DONE** | 7 sections, persistent `SyncStatusCard`, zero toast policy, 3-screen onboarding |
| **Phase 9** | Modular Decomposition & Regression Hardening | **DONE** | Monoliths decomposed into subcomponents; 562 tests passing (100% green) |

---

## 4. Key Invariants & Quality Rules

1. **Test Verification**: Full test suite must always remain 100% green:
   ```bash
   ./gradlew testDebugUnitTest
   ```
   Current suite: **562 unit & integration tests** passing (0 failures, 0 errors).
2. **Accessibility (WCAG 2.2 AA)**:
   - Zero touch targets $< 48\times 48\text{dp}$ across all interactive components.
   - Descriptive `contentDescription` on all actionable icons.
3. **Bilingual & Tabular Typography**:
   - `LocalAppStrings` for all user-facing copy in English and Bengali.
   - Minimum font size $\ge 12\text{sp}$ and line height $\ge 16\text{sp}$ for Bengali text. Zero clipping of key labels.
   - Tabular figures (`fontFeatureSettings = "tnum"`) on all amounts, balances, and timestamps.
4. **Color & Styling Compliance**:
   - Always access colors via `LocalAppColors.current` (never hardcode raw colors).
   - Never rely on color alone to convey meaning (pair with icons/signs/labels).
   - Flat surfaces with max 2 levels of nesting.
5. **Branching & Git Rules**:
   - Work strictly on branch `redesign/v7`. Never push to `main` during redesign.
   - Automatic push to `main` paused until owner explicitly approves merge.
   - 3-point version synchronization policy (`app/build.gradle.kts`, `AppConstants.kt`, git tags) via `./scripts/bump_version.sh`.
