# Missing APIs for Basic Auction Site

## Currently Implemented
| Feature | Endpoint |
|---|---|
| Register / Login | POST /users/register, POST /users/login |
| Publish item | POST /items |
| Get single item | GET /items/{itemId} |
| Delete item | DELETE /items/{itemId} |
| Place a bid | POST /bids |
| Get auction status | GET /item/status/{itemId} |
| Dev-only list all | GET /items/all, GET /item/status/all |

## Missing APIs (prioritized)

### 1. Browse / Search Active Auctions *(Critical)*
- GET /items — list active auctions with pagination.
- Query params: search (title), status (active/ended), page, size.
- Right now buyers have no way to discover what is for sale without knowing the ID.

### 2. Auction Ending / Winner Resolution *(Critical)*
- endTime and itemStatus exist but nothing ever transitions an auction to "ended".
- You need either:
  - A scheduled job (e.g., @Scheduled or a background task) that checks endTime and closes expired auctions.
  - Or a POST /items/{itemId}/end (seller/admin triggered).
- On end: mark itemStatus as closed, lock in the highestBidUser as winner.


### 4. My Account / My Activity *(High)*
- GET /users/me — view own profile + balance.
- GET /users/me/items — items I am selling (with their status).
- GET /users/me/bids — auctions I have bid on.
- GET /users/me/winnings — items I have won.

### 5. Bid History *(Medium)*
- GET /items/{itemId}/bids — chronological list of all bids for transparency.

### 6. Item Description *(Medium)*
- Items currently only have a title. Even a basic auction needs a description field so sellers can describe what they are selling.

### 7. Edit Item Before Bidding *(Medium)*
- PUT /items/{itemId} — update title/description/prices before any bids are placed.

### 8. Balance Operations *(Low-Medium)*
- balance exists on User but is dead weight right now.
- At minimum expose it (see #4). Ideally add POST /users/me/deposit for a basic wallet top-up so you can simulate payment flow.

## One Existing Bug to Fix
- Bid.user is mapped as @OneToOne. This incorrectly enforces a globally unique bidder in the bids table — a user can only ever place one bid total. It should be @ManyToOne.

## Suggested next step
Minimum viable auction site:
- Start with #1 (browse active auctions), #2 (auction ending logic), and #3 (buy it now), plus fixing the @OneToOne bug — that would make the core loop actually work.
