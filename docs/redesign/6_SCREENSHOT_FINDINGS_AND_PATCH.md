# SCREENSHOT FINDINGS & SPEC PATCH (v7)

> Source: 9 real screenshots (Dark theme, Android, English UI, Bengali content).
> This file PATCHES files 1-5. Where it conflicts with them, THIS FILE WINS.

## A. What the screenshots show (verified problems)

### Global
1. **Bottom bar ghosting:** list content shows through the translucent bottom bar (e.g. "Saturday 7 November", "Categories", item text bleed behind the labels). Looks broken.
2. **Cards inside cards, borders everywhere:** every block is a heavily rounded (~28dp) outlined card, often with another bordered box inside. Result: noisy, low information density, little breathing hierarchy.
3. **Oversized elements:** Home hero card ~45% of the screen height; Market list items ~90dp tall each; Add Expense amount box ~1/3 of the sheet. Few useful things fit per screen.
4. **Too many accent colors:** quick-action icons use 5 unrelated hues (teal/orange/purple/pink/blue); center "+" uses a blue-to-teal gradient that is off-brand next to emerald.
5. **Truncation of important text:** "Price Tr…", "Avg…", "Market Refere…", "ধনিয়া পা…", "মাশের …", a filter chip cut off at the screen edge ("Me…", "All…").
6. **Header clipping:** on scroll, content is cut by a hard edge under the Home header (no fade/elevation).
7. **Dual hierarchy confirmed:** Settings shows back arrow AND bottom bar with "Menu" highlighted.
8. **Inconsistent balance:** drawer says Total Balance ৳33,015, Home hero says "Spending Wallet Balance ৳15". Users will not know which is "my money".

### Per screen
- **Home:** hero is huge and centered; Income/Expense are two large tinted boxes with "+" inside; quick actions in another bordered card; only ONE transaction row visible above the fold. Below: Market card shows 4 big item cards + "+2 more", Debts card cut off.
- **Add Expense:** amount box huge with low-contrast "0.00"; default category is "Agriculture" (alphabetical first, not smart); separate "Title" and "Notes" fields; Attach receipt and Date as two more big boxes; Save button is a muted dark red.
- **Transactions:** 3 header icons (download, share, profile) with unclear meaning; the profile icon doesn't belong here; filters in a horizontally scrolling row that is cut off; row subtitle repeats "Bazar • Bazar" (title = category); "Cash in Hand" pill repeated on every row though only one account is used; long full-date day headers.
- **Market > Price Trends:** tab labels truncated; "All (38) / Hiked (0) / Dropped (2)" as three big boxes; sort chips scroll off-screen; every row has a large dark-green "+" circle, a sparkline AND an arrow icon (redundant), "0%" badge on unchanged items; **wrong icons** (salt shaker for DAP fertilizer, khoil, mustard etc. = department icon, not item icon); **duplicate items** ("কাঁচা মরিচ" and "কাঁচামরিচ").
- **Market > New List:** "New List" is shown as a tab although it's an action; title "Create New List" + numbered circle "1" + "Step 1/2: Name" + progress bar = 3 indicators for one step; emoji style differs per chip; half the screen is empty.
- **Market > Lists:** each item is a large card; the category chip repeats the section name (e.g. "Vegetables" inside Vegetables); the Running Total dock is partly covered by the bottom bar so the checkout action is not visible.
- **Settings:** good structure (profile summary tiles, theme and language segmented controls) but the Bengali-numerals switch is low contrast; bottom bar still visible.

## B. Direction (one sentence)
Same dark emerald identity, but **flatter, denser, calmer**: fewer borders, smaller radii, one accent color, left-aligned big numbers, compact rows instead of cards, no truncation of key text, and nothing hidden behind the bottom bar.

## C. Spec patch (numbers are targets; tune in previews)

### C1. Surfaces & shape (overrides DESIGN_SYSTEM section 4)
- Radius: cards 16dp, chips/inputs 12dp, sheets 24dp top, pills/buttons full. Remove 28dp card corners.
- **No outline on cards in Dark/AMOLED.** Separate by tonal surface (`surface` on `background`) only. Hairline allowed ONLY for list dividers, inputs and empty-state dashed boxes.
- **Max nesting = 2.** Never put a bordered box inside a bordered card. Use dividers, spacing, or tint chips instead.
- Lists inside a card use rows (56-72dp) with hairline dividers, not one card per row.

### C2. Bottom bar (overrides COMPONENT_SPEC AppBottomBar)
- Opaque `surface` background + 0.5dp top hairline + no translucency/blur. Scrollable screens get `contentPadding.bottom = barHeight + navigationBarInsets + 16dp` so nothing is ever hidden.
- Center "+" = solid accent (emerald), 56dp, remove blue gradient. Selected tab = accentContainer pill; labels always visible.
- Tabs: Home · Transactions · + · Market · **Money** (replaces "Menu" drawer; see UX_FLOWS).
- Screens with a bottom action dock (Market running total, Add sheet) place the dock ABOVE the bar, never under it.

### C3. Home (patch to UX_FLOWS section 2)
```
Good evening, Sabbir                [👁] [avatar→Settings]
Total balance
৳ 33,015                         (displayLarge, left aligned)
▲ Income ৳8,000   ▼ Expense ৳8,077   <- inline metrics, one row, no boxes
Budget 89% ━━━━━━━━━━━━━━━━━━░░   ৳8,077 / ৳9,000   (6dp bar, warning color + text)
Safe to spend today ৳ ...
[Expense] [Income] [Transfer] [Bazaar]     <- single accent color, no container card
Recent transactions                      See all
 row · row · row                           <- at least 3 rows above the fold on a 6" phone
```
- Hero card target height <= 200dp (was ~45% of screen). One hero number only.
- **Balance rule (needs owner decision):** hero = "Total balance" (sum of all accounts). "Spending wallet" becomes a small secondary line/switch under the number. Until the owner decides, implement the total as default and keep the switch.
- Quick actions: all icons use accent tint on `accentContainer` (no rainbow). Debts/Budget/Bills are reached from Money tab, not Home quick actions.
- Market and Debts snapshot cards: collapsed summary row by default (title, progress, spent, "Open"); no embedded item cards.
- Fix header clipping: pinned header with `background` color and 0.5dp hairline appearing on scroll (no hard cut).

### C4. Add / Edit Transaction (patch to UX_FLOWS section 3)
- Amount area <= 140dp: type segmented control, big amount (left or centered), expression line. Placeholder "0" (show decimals only after "." is typed).
- **Smart default category:** last used category for this type + this time of day, else most frequent, else "Other". Never alphabetical first.
- Merge "Title" and "Notes": one "Title" field (autocomplete from previous titles) + a "+ Add note" text button that expands a second field.
- Receipt = small "📎 Attach receipt" row (48dp), not a large boxed card.
- Date/time = chips: [Today] [Yesterday] [Pick date] and a time chip; not a large box.
- Save button: full width, solid `accent` for income, solid `expense` token for expense, text >= 4.5:1 contrast, shows the amount.

### C5. Transactions (patch to UX_FLOWS section 4)
- Header: title + Search icon + overflow (Export PDF/CSV, Share). Remove the profile icon from this screen.
- Filters: one "Filters" button with active-count badge + removable chips under it. No horizontally scrolling dropdown row.
- Row: category icon · **title** · subtitle = `category` only if different from title, + time · amount right. Show the account pill ONLY when the user has more than one account in use or the account differs from the default.
- Day header: "Today / Yesterday / Mon, 9 Nov" (short) + day net total.
- Rows live in one grouped surface per day with dividers (as now) but without outline.

### C6. Market (patch to UX_FLOWS section 5)
- Tabs: segmented control with short labels **Lists · Prices · New** (no truncation, Bengali-safe), min 48dp. "New list" ALSO available as an extended button on the Lists tab. Don't style an action as a tab.
- **Lists tab:** item = 72dp row (not a card): 28dp checkbox · name (1-2 lines, never ellipsized) with qty chip inline · price right · tiny price-variance text under price (e.g. "▼ ৳5 vs last"). Remove the category chip inside a section with the same name. Section header = icon + name + "2/2" + chevron.
- **Running total dock:** sticky above the bottom bar: total, count, and the primary "Finish & save ৳650" button, always visible. At 100% the button becomes primary-filled.
- **Prices tab:** filter = one segmented control `All 38 · Hiked 0 · Dropped 2` (compact, no big red/green boxes); sort = a "Sort: Recent ▾" menu button (no off-screen chip row); search stays in the header.
- Price row (72dp): name (full, wraps to 2 lines) / sub "Avg ৳48 · 9 logged" / right: `৳50/কেজি` + delta badge **only if change != 0** / sparkline small (drop the extra arrow) / trailing 48dp outlined "+" icon button OR tap the row to log a price (pick one; prefer tap row + small "+" ghost).
- **Item icons:** use a neutral item avatar (first letter on a tinted circle) unless a specific item icon is known. Do NOT reuse the department icon for unrelated items (DAP, khoil, etc.).
- **Dedupe:** normalize commodity names (trim, collapse spaces, NFC-normalize Bengali, ignore ZWJ/ZWNJ) so "কাঁচা মরিচ" and "কাঁচামরিচ" merge; offer a one-time "Merge duplicates?" prompt. Do not auto-delete data without confirmation and keep price history.
- **New list:** one indicator only (progress dots OR "Step 1 of 2", not both), single title ("New list"), title field + suggestion chips with one consistent icon style (Material icons, not mixed emoji), sticky bottom button "Next: Add items". Fill the screen: add "Recent lists" or "Repeat last list" shortcuts below the chips.

### C7. Settings
- Keep the profile summary tiles and segmented controls (they work). Hide bottom bar; back = Money hub/Home.
- Switches: off-state must have >= 3:1 contrast against surface; add on/off text state description for TalkBack.
- Email: truncate in the middle is fine on the profile card; show full on tap.

### C8. Truncation rule (global)
Key labels (names, amounts, tab labels) must not ellipsize: allow 2 lines for names, auto-shrink amounts, shorten tab/chip labels. Ellipsis allowed only for notes, emails and secondary meta. Test every screen with Bengali strings and font scale 130%.

## D. Prompt amendments (apply to docs/redesign/5_ANTIGRAVITY_PROMPTS.md)
- Phase 0: add `6_SCREENSHOT_FINDINGS_AND_PATCH.md` to the files to read, and tell the agent "file 6 overrides files 1-5 on conflicts".
- Phase 1: also implement C1 (flat surfaces, new radii, no outlines) and C2 (opaque bottom bar + content bottom padding) in the base components.
- Phase 4: follow C3. Phase 3: follow C4. Phase 5: follow C5 and C6 (including dedupe + item avatars). Phase 8: follow C7.
- Phase 9: add a checklist "no content hidden behind bottom bar", "no ellipsized key labels", "max 2 nesting levels".

## E. Open decision for the owner
Which number should the Home hero show: **Total balance (all accounts)** or **Spending wallet balance**? Default in this patch = Total balance with the Spending wallet as a secondary line.
