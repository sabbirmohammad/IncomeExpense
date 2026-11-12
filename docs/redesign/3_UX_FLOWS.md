# UX FLOWS & INFORMATION ARCHITECTURE — v7

## 1. New navigation (replaces 3D drawer)
Bottom bar, 5 slots, labels always visible (bn + en):

```
[ Home ]  [ Transactions ]  [  +  ]  [ Market ]  [ Money ]
```
- **Home** — daily overview.
- **Transactions** — full ledger, search, filters.
- **+** (center) — ONE tap opens Add sheet. No long-press dependency.
- **Market** — bazaar checklist (signature local feature, kept at top level).
- **Money** — hub: Accounts & Wallets · Budgets & Goals · Bills · Money Owed (ধার ও দেনা) · Insights (Analytics) · Reports.
- **Settings** — opens from the avatar in the Home header (and a gear in Money hub). Not a tab.

Rules:
- Bottom bar is visible ONLY on the 5 top-level screens. Drill-down screens
  (Accounts, Budgets, Bills, Money Owed, Insights, Reports, Settings) hide it and show a back arrow + large title.
- Back: drill-down -> its parent (Money hub, not Home). Top-level tab -> Home. Home -> exit.
- Predictive back animation supported.
- Delete `Navigation3DScaffold.kt` + `SideMenuDrawer.kt`; remove unused navigation3 dependency.

## 2. Home (DashboardScreen) — above the fold
```
[avatar] Good evening, <name>            [👁 privacy] [🔔]
┌─────────────────────────────────────────┐
│ Total balance                            │
│ ৳ 48,250.00         (displayLarge)       │
│ ▲ Income ৳60,000    ▼ Expense ৳11,750    │  <- month, chips
│ ───────────── budget 62% ──────────────  │
└─────────────────────────────────────────┘
 Safe to spend today: ৳ 820   (info row)
 [Add expense] [Add income] [Transfer] [Bazaar]   <- 4 quick actions, 48dp+ 
 Upcoming bill banner (only if due <= 4 days / overdue)
 Recent transactions (3)            See all >
 ─── Below the fold (each collapsible, remembers state) ───
 Active market list · Debts snapshot · Savings goals · 7-day spend
```
- Max 5 sections visible by default. Sections with no data are hidden (not empty cards) except one "Get started" checklist for new users.
- Pull-to-refresh not needed (local data).
- Privacy toggle in header masks all amounts instantly (••••).

## 3. Add / Edit Transaction (single sheet, no nested modals)
Full-height sheet (90%), drag handle, title changes by type.
```
[ Expense | Income | Transfer ]   <- 48dp segmented control, remembers last used
        ৳ 0                       <- big amount, tabular, live calculator expression line above
Category  (chips grid, 2 rows scroll horizontally, "More" expands inline grid)
Account   (horizontal chips with balance)  — Transfer shows From -> To
Date: Today · Yesterday · Pick   |  Note (single line, optional)  |  📎 Receipt
┌ AmountKeypad (1 2 3 / 4 5 6 / 7 8 9 / . 0 ⌫ ; operators + − × ÷ ; ✓ Save) ┐
```
- Custom `AmountKeypad` replaces system keyboard for amount (same component as Market price entry).
  Note field uses the system keyboard; when it's focused, keypad collapses and Save stays pinned above IME (`imePadding`).
- Save button pinned at bottom, always visible, shows amount ("Save ৳ 450").
- Insufficient balance = inline warning banner (not blocking).
- Market type is removed from this sheet; market expenses are created by Market checkout.
- Edit mode: same sheet prefilled, Delete in overflow menu with confirm + undo snackbar.
- After save: sheet closes, snackbar "Saved · Undo", subtle confetti only for first-ever transaction.

## 4. Transactions
- Large-title collapsing header + search icon expands to search field.
- Filters: one "Filters" button (badge with active count) opens a filter sheet (type, account, category, date range) — replaces the row of dropdowns. Active filters shown as removable chips under the header.
- Day-grouped sections with sticky day headers and day net total.
- `TransactionRow`: 40dp category circle · title + (category · account) · amount right-aligned colored with sign · time muted.
- Swipe left = delete (with undo), swipe right = edit; both ALSO available via tap -> detail sheet (no swipe-only).
- Export (PDF/CSV) moves into overflow menu -> goes to Reports.

## 5. Market (keep behavior, polish)
- Keep 3 tabs as segmented control: Checklist · Prices · New list.
- Visual: aisle sections as collapsible cards with progress ring; item rows 64dp; swipe-to-check kept (SwipeToDismissBox) + tap checkbox alternative.
- In-store dock: total, count, Checkout button; uses `AmountKeypad` for price entry.
- Segmented pills min height 48dp.

## 6. Money hub (new screen)
Grid/list of 6 entries with live summary numbers:
```
Accounts      ৳ 48,250   (3 wallets)
Budgets       62% used   · ৳820/day safe
Bills         2 due soon · ৳6,400/mo
Money Owed    Net +৳3,000
Insights      Top: Food 38%
Reports       Export PDF/CSV
```
Each opens a drill-down screen. Budgets & Savings Goals stay as two tabs inside one screen.

## 7. Drill-down screens
- **Accounts:** hero net-worth card, horizontally paged account cards (keep digital card look, refined), quick actions Transfer / Add / Reconcile as 48dp buttons, per-account recent activity.
- **Budgets:** hero = month ring (spent / limit) + daily safe spend; category budgets as list with thin progress bars (emerald <75, turmeric 75-99, terracotta >=100) + status text, not color alone.
- **Bills:** hero monthly commitment; segmented Upcoming · Paid · All; "Pay now" is the primary button on each due card.
- **Money Owed:** tabs Owed to me / I owe; net banner; person-grouped cards with progress; Split Bill as FAB-style extended button.
- **Insights:** period chips (Today/Week/Month/Year/All) + KPI strip + donut + 7/30-day bars + top categories. Share button in top bar.
- **Reports:** period picker, preview card of the statement, two big buttons Export PDF / Export CSV.
- **Settings:** grouped sections (Profile · Appearance · Language & numerals · Categories · Backup & Sync · About). Sync section shows a **status card**: last backup time, state (OK / failed + reason), Backup now / Restore buttons. Results via snackbar with action + persistent status, never Toast.

## 8. Empty states & onboarding
- First launch: 3-screen skim (Track · Plan · Bazaar) max, skippable, then Home with a "Get started" checklist (Add account, Add first expense, Set budget).
- Bills empty: template chips (Electricity, Gas, Wi-Fi, Rent, Mobile recharge, Subscription).
- Savings empty: templates (Emergency fund, Eid savings, Education, Travel).
- Budgets empty: "Suggest budget from last month" button when history exists.

## 9. Microcopy
- Short, active, bilingual. Buttons are verbs ("Save", "Pay now"). Errors say what happened + what to do.
- All strings via AppStrings in English AND Bengali. Review Bengali for natural phrasing, not literal translation.
