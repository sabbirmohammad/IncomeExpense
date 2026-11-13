# Changelog

All notable changes to **হিসাব কিতাব (Expense Tracker)** are documented in this file.

---

## [v7.0] - 2026-11-13 — "Refined Ledger" UI/UX Overhaul

### Overview
Version 7.0 ("Refined Ledger") is a comprehensive, ground-up redesign of **হিসাব কিতাব (Expense Tracker)**. It introduces a modern, tactile, and highly legible financial ledger experience crafted specifically for daily personal finance and bazaar management in Bangladesh.

---

### Highlights & Major Changes

#### 1. Foundations & Design System ("Refined Ledger")
- **4 Crafted Themes**: System Dark, Crisp Light, AMOLED True Black, and Nature Soft Green.
- **C1 Flat Surfaces**: Cards and list items use elevated surfaces (`surfaceRaised`) with 0 borders in Dark/AMOLED and crisp 0.5dp/1dp hairlines in Light mode; max 2 nesting levels throughout.
- **Harmonized Geometry**: Clean radii hierarchy with `RadiusL` (24dp), `RadiusM` (16dp), `RadiusS` (10dp), and `RadiusFull` (999dp).
- **Tabular Figures (`tnum`)**: All amounts, totals, balances, and timestamps enforce monospace digit widths (`fontFeatureSettings = "tnum"`) to eliminate digit jitter.
- **Universal Bengali Typography**: Universal $\ge 12\text{sp}$ font size and $\ge 16\text{sp}$ line height with zero clipping across Bengali strings.
- **Accessible Motion**: Spring and ease curves respecting system `reduce-motion` preferences.

#### 2. Navigation Overhaul
- **Modern 4-Tab Bottom Dock**: Clean 64dp opaque bottom bar with **Home**, **Transactions**, **Market**, and **Money Hub**.
- **Prominent 56dp FAB**: Center action button with fluid haptic action sheet for adding expenses, income, transfers, and market lists.
- **Removed Legacy 3D Drawer**: Removed 3D perspective rotation scaffold and right-edge side menu in favor of the dedicated **Money Hub**.
- **No Occluded Content**: Uniform 80–100dp bottom padding across all scrolling surfaces ensures bottom items are always reachable.

#### 3. Streamlined Transaction Entry
- **Full-Bleed Modal Sheet**: Redesigned bottom sheet with large amount display and quick-action bar.
- **Custom Tactile AmountKeypad**: Built-in 4x4 keypad with inline math expression solver (`=`), backspace, and instant visual feedback.
- **Sticky Single-Tap CTA**: Bottom-pinned primary button with high touch ergonomics.
- **Smart Category & Account Pickers**: Horizontal recent-category chips and account balance badges.

#### 4. Home (Dashboard) Experience
- **Compact Hero Card ($\le 200\text{dp}$)**: Left-aligned net balance with secondary spending wallet balance and 1-tap toggle.
- **Safe to Spend Row**: Daily discretionary spending limit calculated from monthly budget commitments.
- **4 Quick Action Buttons**: Accounts, Bills, Due/Lend, and Budgets with uniform `accentContainer` tinting and $\ge 48\text{dp}$ touch targets.
- **Above-The-Fold Transactions**: At least 3 recent transactions visible immediately without scrolling on 6" screens.
- **Collapsible Below-The-Fold Widgets**: Accordion widgets for active market lists, debt ledger, savings goals, and 7-day spending trends.
- **New User Onboarding Checklist**: Progressive checklist displayed for new users until first transactions and budgets are logged.

#### 5. Transactions & Market Hub
- **Sticky Date Groups**: Clean transaction ledger with daily income, expense, and net summary headers.
- **72dp Touch-Optimized Rows**: 2-line title wrapping (no truncated merchant/expense names), category icons, payment method badge, and right-aligned amount in `tnum`.
- **Rich Transaction Detail Sheet**: Complete receipt breakdown with undoable deletion snackbar.
- **Market Hub Segmented Control**: 3 tabs — **Lists (ফর্দ)**, **Prices (দরদাম)**, and **New (নতুন)**.
- **Smart Multi-List Support**: Shopping HUD with sticky running-total dock and 1-tap checkout.
- **Normalized Commodity Prices**: NFC-normalized Bengali text deduplication for smart commodity price intelligence.

#### 6. Money Hub (Accounts, Budgets, Bills, Debts)
- **Accounts**: Total net worth hero card, bank/MFS/cash breakdowns, account reconciliation modal, and transfer shortcuts.
- **Budgets & Savings**: Monthly budget status bar, category budget warning cards, and savings goals with progress rings and starter templates.
- **Recurring Bills**: Monthly commitments hero, overdue/upcoming badges, and 1-tap "Pay Now" recording flow.
- **Money Owed (Debts)**: Net position hero, dual tabs for Receivables and Payables, partial payment tracking, and SMS sharing.

#### 7. Insights & Reports
- **Interactive 160dp Donut Charts**: Tap-to-highlight segments with dynamic center balance update and comprehensive legends.
- **Payment Method Breakdown**: Visual spend distribution across bKash, Nagad, Bank, Cash, and Cards.
- **Spending Bar Chart**: Dual income/expense bars with interactive tap tooltip callout and today indicator dot.
- **Smooth Bézier Sparkline**: Monotone cubic trend visualization with gradient fill.
- **Reports & Statement Preview**: Live statement preview card with dual $\ge 48\text{dp}$ export buttons (**Export PDF** & **Export CSV**).

#### 8. Settings, Cloud Sync & First-Launch Experience
- **Organized Settings Structure**: 7 logical sections (Profile, Appearance, Language & Numerals, Categories, Backup & Sync, Data & Storage, About).
- **Persistent SyncStatusCard**: Real-time Google Drive sync card with live status badges, last sync timestamp in `tnum`, and expandable error diagnostics.
- **Zero Toast in Settings**: Replaced ephemeral toast messages with actionable `AppSnackbarHost` snackbars (with retry and error details).
- **3-Screen Skippable Onboarding**: Interactive first-launch walkthrough explaining Track, Plan, and Bazaar workflows.

#### 9. Accessibility & Quality Assurance
- **100% WCAG 2.2 Touch Targets**: Zero touch targets below $48\times 48\text{dp}$ across all interactive elements (`MarketSegmentedPills`, `AccountCardMenu`, `FilterChips`, `SettingsProfileCard`).
- **TalkBack Semantics**: Descriptive content descriptions and accessibility summaries for all charts, empty states, and switches.
- **Added `android.permission.VIBRATE`**: System vibrator permission declared for haptic feedback.
- **Automated Test Coverage**: 540 unit tests passing with zero failures.
