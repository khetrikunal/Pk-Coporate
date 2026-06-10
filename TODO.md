# TODO - Product Deployment & Admin T‑Shirt Catalog

## Backend (Spring Boot)
- [ ] Update `TShirtProduct` entity: add `discountPrice`, `stockQuantity`, `brand`; ensure field names/types align with required API.
- [ ] Add/adjust DTOs for:
  - [ ] Create product request (validation)
  - [ ] Update product request (validation)
  - [ ] Product response for agent/admin including images + effectivePrice
- [ ] Implement admin secured CRUD APIs (admin-only):
  - [ ] POST create (multipart multi-image upload)
  - [ ] PUT update
  - [ ] DELETE delete
  - [ ] PATCH enable/disable
  - [ ] inventory update (if separate endpoint)
- [ ] Implement/adjust service + repository methods for CRUD + stock/discount/brand.
- [ ] Implement multi-image upload logic (Cloudinary) and persist `ProductImage` rows.
- [ ] Ensure agent endpoint returns only active products with correct DTO contract.
- [ ] Keep backward compatibility for existing `GET /v1/products` (agent/catalog).

## Frontend (React)
- [ ] Remove dummy/static `PRODUCTS` data from `frontend/src/pages/agent/catalog/CatalogPage.tsx`.
- [ ] Update `productService.ts` DTOs + ensure `getProducts()` maps backend response correctly.
- [ ] Update agent catalog UI to render backend data (images/colors/sizes/price/discount/effectivePrice) with loading/error states.
- [ ] Build/upgrade `frontend/src/pages/admin/products/ProductsPage.tsx`:
  - [ ] Product list table
  - [ ] Create/Edit form with multi-image upload
  - [ ] Enable/Disable toggle
  - [ ] Delete flow
  - [ ] Update success/error toasts + loading states
- [ ] Add admin API calls in `productService.ts`.

## Production readiness & cleanup
- [ ] Remove old deployment config references (e.g., `render.yaml` if obsolete) per new hosting.
- [ ] Fix any API mismatches/warnings found during implementation.
- [ ] Build & run end-to-end with `docker-compose up --build`.
- [ ] Validate:
  - [ ] Admin create/update/delete instantly reflected in Agent catalog after refresh.
  - [ ] No dummy products remain.

