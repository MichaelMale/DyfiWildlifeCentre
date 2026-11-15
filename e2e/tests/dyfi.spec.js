const { test, expect } = require('@playwright/test');

const ADMIN_USER = process.env.E2E_ADMIN_USERNAME || 'michael';
const ADMIN_PASS = process.env.E2E_ADMIN_PASSWORD || 'dyfi';

test.describe('Dyfi Wildlife Centre Visitor & Admin E2E', () => {
  test('visitor map loads Leaflet, layer switcher, trails, search, webcams, sightings, and directions', async ({ page }) => {
    await page.goto('/');

    await expect(page.locator('#map')).toBeVisible();
    await expect(page.locator('.leaflet-control-layers')).toBeVisible();

    // Open base layer control to verify Street, Topographic, Satellite, and Reserve Trails overlay
    await page.locator('.leaflet-control-layers').hover();
    await expect(page.locator('.leaflet-control-layers')).toContainText('Street Map');
    await expect(page.locator('.leaflet-control-layers')).toContainText('Topographic');
    await expect(page.locator('.leaflet-control-layers')).toContainText('Satellite');
    await expect(page.locator('.leaflet-control-layers')).toContainText('Reserve Trails & Hides');

    // Category filters update active pill state
    await page.locator('.dyfi-filter-pill[data-filter="transport"]').click();
    await expect(page.locator('.dyfi-filter-pill[data-filter="transport"]')).toHaveClass(/active/);
    await page.locator('.dyfi-filter-pill[data-filter="all"]').click();

    // Search for a railway station and open POI modal
    const searchInput = page.locator('#mapSearchInput');
    await searchInput.fill('Machynlleth');
    const firstResult = page.locator('#mapSearchResults button').first();
    await expect(firstResult).toBeVisible();
    await firstResult.click();
    await expect(page.locator('#poiCard')).toBeVisible();
    await expect(page.locator('#poi_title')).toContainText('Machynlleth');
    await page.locator('#poiCard .modal-close').click();
    await expect(page.locator('#poiCard')).toBeHidden();

    // Osprey Webcam modal embeds requested YouTube feed
    await page.getByRole('button', { name: /Osprey Webcam/i }).click();
    await expect(page.locator('#webcamModal')).toBeVisible();
    await expect(page.locator('#webcamYoutubePlayer')).toHaveAttribute('src', /youtube\.com\/embed\/YnKZJ3SF5WM/);
    await page.locator('#webcamModal .modal-close').click();
    await expect(page.locator('#webcamModal')).toBeHidden();

    // Latest Sightings modal loads sightings feed from PostgreSQL
    await page.locator('#openSightingsBtn').click();
    await expect(page.locator('#sightingsModal')).toBeVisible();
    await expect(page.locator('#sightingsFeedList .collection-item').first()).toBeVisible();
    await page.locator('#sightingsModal .modal-close').click();
    await expect(page.locator('#sightingsModal')).toBeHidden();

    // Two-way Directions modal + coordinate query
    await page.getByRole('button', { name: 'Directions', exact: true }).click();
    await expect(page.locator('#directionsModal')).toBeVisible();
    await expect(page.locator('#directionsUseMyLocationBtn')).toBeVisible();
    await expect(page.locator('input[name="directionsDirection"][value="toCentre"]')).toBeChecked();
    await page.locator('#directionsDestination').fill('52.5923, -3.8548');
    await page.locator('#directionsSubmitBtn').click();
    await expect(page.locator('#directionsResults')).toBeVisible({ timeout: 15000 });
    await expect(page.locator('#directionsSummary')).toContainText('Dyfi Wildlife Centre');
    await page.locator('#directionsModal .modal-close').click();
    await expect(page.locator('#directionsModal')).toBeHidden();
  });

  test('kiosk mode hides admin navigation links cleanly', async ({ page }) => {
    await page.goto('/?kiosk=true');
    await expect(page.locator('#kioskModeBadge')).toBeVisible();
    await expect(page.locator('#adminNavLink')).toHaveCount(0);
    await expect(page.locator('.lang-switch[data-lang="cy"]').first()).toHaveAttribute('href', /kiosk=true/);
  });

  test('admin authentication, interactive map picker on add & edit, and staff consoles', async ({ page }) => {
    await page.goto('/login');
    await page.locator('#username').fill(ADMIN_USER);
    await page.locator('#password').fill(ADMIN_PASS);
    await page.locator('input[type="submit"]').click();

    await expect(page).toHaveURL(/\/$/);

    // Interactive map picker on /admin/add populates coordinates on click
    await page.goto('/admin/add');
    const picker = page.locator('#addPoiPickerMap');
    await expect(picker).toBeVisible();
    await picker.scrollIntoViewIfNeeded();
    await picker.click({ position: { x: 220, y: 160 } });
    await expect(page.locator('#mapPickerStatus')).toHaveClass(/dyfi-picker-status--active/);
    const pickedLat = parseFloat(await page.locator('#latitude').inputValue());
    expect(Math.abs(pickedLat)).toBeGreaterThan(10);

    // /admin/edit_active includes interactive map picker (read-only/locked on station, unlocked on general POI)
    await page.goto('/admin/list');
    await expect(page.locator('#generalSearch')).toBeVisible();
    const firstGeneralEdit = page.locator('#generalPoisList a[href^="/admin/edit_active"]').first();
    await firstGeneralEdit.click();
    await expect(page.locator('#addPoiPickerMap')).toBeVisible();

    // Staff consoles render cleanly
    for (const path of ['/admin/webcams', '/admin/sightings', '/admin/import', '/admin/users']) {
      await page.goto(path);
      await expect(page.locator('main')).toBeVisible();
    }
  });
});
