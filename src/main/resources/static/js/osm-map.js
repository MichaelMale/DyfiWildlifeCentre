/*
 * Copyright (C) 2020 Michael Male
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* GLOBAL VARIABLES */

const DYFI_CENTRE_COORDS = [52.568774, -3.918031];
const DEFAULT_ZOOM = 16;
const DEFAULT_POI_IMAGE = '/images/osprey.jpeg';
const DEFAULT_TRAIN_IMAGE = '/images/thomas-train.jpg';
const DEFAULT_BUS_IMAGE = '/images/magic-school-bus.jpg';

const CATEGORY_MARKER_CONFIG = {
    transport: { modifier: 'transport', icon: 'train' },
    wildlife: { modifier: 'wildlife', icon: 'emoji_nature' },
    business: { modifier: 'business', icon: 'business' }
};

let allMarkers = [];
let markerCluster;
let map;
let reserveTrailsLayer = null;
let activeRouteLayer = null;
let activeDestinationMarker = null;
let webcamFeeds = [];
let currentModalPoi = null;
let activeCategoryFilter = null;

/**
 * Performs an HTTP GET request and returns the parsed JSON response.
 *
 * @param {string} url The REST endpoint URL.
 * @returns {Promise<any>}
 */
async function fetchJson(url) {
    const response = await fetch(url, {
        method: 'GET',
        credentials: 'include'
    });
    if (!response.ok) {
        const errorBody = await response.json().catch(() => null);
        const message = errorBody && errorBody.error ? errorBody.error : `Request failed: ${response.status}`;
        throw new Error(message);
    }
    return response.json();
}

/**
 * Returns true if the active page locale is Welsh (cy).
 *
 * @returns {boolean}
 */
function isWelshLocale() {
    const lang = (document.documentElement.lang || 'en').toLowerCase();
    return lang === 'cy' || lang.startsWith('cy-');
}

/**
 * Picks the localised POI name, falling back to English when Welsh text is absent.
 *
 * @param {Object} poi Point of Interest payload from /poi.
 * @returns {string}
 */
function getLocalizedName(poi) {
    if (isWelshLocale() && poi.nameCy && poi.nameCy.trim().length > 0) {
        return poi.nameCy;
    }
    return poi.name ?? '';
}

/**
 * Picks the localised POI description, falling back to English when Welsh text is absent.
 *
 * @param {Object} poi Point of Interest payload from /poi.
 * @returns {string}
 */
function getLocalizedDescription(poi) {
    if (isWelshLocale() && poi.descriptionCy && poi.descriptionCy.trim().length > 0) {
        return poi.descriptionCy;
    }
    return poi.description ?? '';
}

function getDefaultImageForPoi(poi) {
    if (poi && poi.stationCode && String(poi.stationCode).trim().length > 0) {
        return DEFAULT_TRAIN_IMAGE;
    }
    if (poi && poi.busStopCode && String(poi.busStopCode).trim().length > 0) {
        return DEFAULT_BUS_IMAGE;
    }
    return DEFAULT_POI_IMAGE;
}

/**
 * Validates POI image URLs, defaulting safely to the POI-appropriate fallback image.
 *
 * @param {string|null|undefined} imageUrl Configured uploaded or hotlinked image URL.
 * @param {Object|null|undefined} poi Optional POI payload for transport fallback selection.
 * @returns {string}
 */
function getSafeImageUrl(imageUrl, poi) {
    const fallback = getDefaultImageForPoi(poi);
    if (!imageUrl || typeof imageUrl !== 'string') {
        return fallback;
    }
    const trimmed = imageUrl.trim();
    if (
        trimmed.startsWith('https://') ||
        trimmed.startsWith('http://') ||
        trimmed.startsWith('/uploads/') ||
        trimmed.startsWith('/images/') ||
        trimmed.startsWith('images/')
    ) {
        return trimmed;
    }
    return fallback;
}

/**
 * Creates a color-coded, icon-badged Leaflet marker icon for accessibility and clear
 * differentiation of public transport, wildlife, and local businesses.
 *
 * @param {string|null|undefined} category POI category.
 * @returns {L.DivIcon}
 */
function createCategoryMarkerIcon(category, poi) {
    const config = { ...(CATEGORY_MARKER_CONFIG[category] || { modifier: 'default', icon: 'place' }) };
    if (poi && poi.busStopCode) {
        config.icon = 'directions_bus';
    }
    return L.divIcon({
        className: 'dyfi-marker-wrapper',
        html: `<div class="dyfi-marker-pin dyfi-marker-pin--${config.modifier}"><i class="material-icons">${config.icon}</i></div>`,
        iconSize: [34, 34],
        iconAnchor: [17, 17]
    });
}

/**
 * Opens the Materialize POI modal populated with the selected POI's image and details.
 *
 * @param {Object} poi Point of Interest payload from /poi.
 */
function openPoiModal(poi) {
    currentModalPoi = poi;
    const element = document.getElementById('poiCard');
    const title = getLocalizedName(poi);
    const imageEl = element.querySelector('#poi_image');
    if (imageEl) {
        imageEl.src = getSafeImageUrl(poi.imageUrl, poi);
        imageEl.alt = title;
    }
    element.querySelector('#poi_title').textContent = title;
    element.querySelector('#poi_description').textContent = getLocalizedDescription(poi);
    element.querySelector('#poi_distance').textContent = poi.distanceFromCentre ?? '';

    const categoryBadge = element.querySelector('#poi_category_badge');
    if (categoryBadge) {
        if (poi.category && poi.category.trim().length > 0) {
            categoryBadge.textContent = poi.category;
            categoryBadge.style.display = 'inline-block';
        } else {
            categoryBadge.style.display = 'none';
        }
    }

    const codeBadge = element.querySelector('#poi_code_badge');
    if (codeBadge) {
        const code = poi.stationCode || poi.busStopCode;
        if (code) {
            codeBadge.textContent = code;
            codeBadge.style.display = 'inline-block';
        } else {
            codeBadge.style.display = 'none';
        }
    }

    const instance = M.Modal.getInstance(element) || M.Modal.init(element, {
        dismissible: true,
        inDuration: 250,
        outDuration: 200
    });
    instance.open();
}

function directionsToCurrentPoi() {
    if (!currentModalPoi) {
        return;
    }
    const modalEl = document.getElementById('poiCard');
    const modalInstance = modalEl ? M.Modal.getInstance(modalEl) : null;
    if (modalInstance) {
        modalInstance.close();
    }

    const destinationInput = document.getElementById('directionsDestination');
    const query = (currentModalPoi.postcode && currentModalPoi.postcode.trim().length > 0)
        ? currentModalPoi.postcode.trim()
        : `${Number(currentModalPoi.latitude).toFixed(6)}, ${Number(currentModalPoi.longitude).toFixed(6)}`;

    openDirectionsModal();
    const fromCentreRadio = document.querySelector('input[name="directionsDirection"][value="fromCentre"]');
    if (fromCentreRadio) {
        fromCentreRadio.checked = true;
    }
    if (destinationInput) {
        destinationInput.value = query;
        if (window.M && M.updateTextFields) {
            M.updateTextFields();
        }
        const form = document.getElementById('directionsForm');
        if (form) {
            form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
        }
    }
}

/**
 * Loads Cors Dyfi boardwalk trails and wildlife hides from static GeoJSON into a Leaflet overlay group.
 */
async function initReserveTrailsOverlay(layerControl) {
    reserveTrailsLayer = L.layerGroup();
    try {
        const geojson = await fetchJson('/data/cors-dyfi-trails.geojson');
        const geoLayer = L.geoJSON(geojson, {
            style: function (feature) {
                const props = (feature && feature.properties) || {};
                return {
                    color: props.color || '#00897b',
                    weight: props.kind === 'accessible_trail' ? 5 : 3.5,
                    opacity: 0.9,
                    dashArray: props.dash || null
                };
            },
            pointToLayer: function (feature, latlng) {
                const props = (feature && feature.properties) || {};
                const isNest = props.kind === 'nest';
                return L.circleMarker(latlng, {
                    radius: isNest ? 8 : 7,
                    color: isNest ? '#b71c1c' : '#004d40',
                    fillColor: isNest ? '#ffb300' : '#26a69a',
                    fillOpacity: 0.95,
                    weight: 2
                });
            },
            onEachFeature: function (feature, layer) {
                const props = (feature && feature.properties) || {};
                const title = isWelshLocale() && props.nameCy ? props.nameCy : (props.nameEn || 'Trail');
                const desc = isWelshLocale() && props.descriptionCy ? props.descriptionCy : (props.descriptionEn || '');
                const esc = str => String(str).replace(/[&<>"']/g, c => ({
                    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
                }[c]));
                layer.bindPopup(`<strong>${esc(title)}</strong><br><span>${esc(desc)}</span>`);
            }
        });
        geoLayer.addTo(reserveTrailsLayer);
        reserveTrailsLayer.addTo(map);
        const overlayLabel = isWelshLocale() ? 'Llwybrau a Chuddfannau Cors Dyfi' : 'Reserve Trails & Hides';
        if (layerControl) {
            layerControl.addOverlay(reserveTrailsLayer, overlayLabel);
        }
    } catch (err) {
        console.warn('Could not load reserve trails GeoJSON:', err);
    }
}

/**
 * Initialises the Leaflet / OpenStreetMap map, base layers, overlays, clustered POIs, and webcams.
 *
 * @returns {Promise<void>}
 */
async function initMap() {
    const streetLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    });
    const topoLayer = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
        maxZoom: 17,
        attribution: 'Map data: &copy; OpenStreetMap contributors, SRTM | Map style: &copy; OpenTopoMap (CC-BY-SA)'
    });
    const satelliteLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
        maxZoom: 19,
        attribution: 'Tiles &copy; Esri — Source: Esri, Maxar, Earthstar Geographics'
    });

    map = L.map('map', {
        center: DYFI_CENTRE_COORDS,
        zoom: DEFAULT_ZOOM,
        layers: [streetLayer]
    });

    const baseLayers = isWelshLocale()
        ? { 'Map Stryd (OSM)': streetLayer, 'Topograffig': topoLayer, 'Lloeren': satelliteLayer }
        : { 'Street Map': streetLayer, 'Topographic': topoLayer, 'Satellite': satelliteLayer };

    const layerControl = L.control.layers(baseLayers, {}, { position: 'topright' }).addTo(map);

    markerCluster = L.markerClusterGroup();
    map.addLayer(markerCluster);

    try {
        const allPointsOfInterest = await fetchJson('/poi');
        allPointsOfInterest.forEach(poi => {
            const localizedTitle = getLocalizedName(poi);
            const marker = L.marker([poi.latitude, poi.longitude], {
                title: localizedTitle,
                alt: localizedTitle,
                icon: createCategoryMarkerIcon(poi.category, poi)
            });
            marker.poiData = poi;
            marker.on('click', () => openPoiModal(poi));

            allMarkers.push(marker);
            markerCluster.addLayer(marker);
        });
    } catch (err) {
        console.error('Error loading Points of Interest:', err);
    }

    initReserveTrailsOverlay(layerControl);
    initMapSearch();
    initDirectionsControls();
    initWebcams();
    initKioskMode();
}

function initMapSearch() {
    const inputEl = document.getElementById('mapSearchInput');
    const clearEl = document.getElementById('mapSearchClear');
    const resultsEl = document.getElementById('mapSearchResults');
    if (!inputEl || !resultsEl) {
        return;
    }

    function hideResults() {
        resultsEl.style.display = 'none';
        resultsEl.innerHTML = '';
    }

    function updateSearch() {
        const raw = (inputEl.value || '').trim();
        if (clearEl) {
            clearEl.style.display = raw.length > 0 ? 'inline-flex' : 'none';
        }
        if (raw.length < 2) {
            hideResults();
            return;
        }
        const q = raw.toLowerCase();
        const matches = allMarkers
            .filter(m => {
                const p = m.poiData;
                const haystack = [
                    p.name,
                    p.nameCy,
                    p.stationCode,
                    p.busStopCode,
                    p.category
                ].filter(Boolean).join(' ').toLowerCase();
                return haystack.indexOf(q) !== -1;
            })
            .slice(0, 8);

        resultsEl.innerHTML = '';
        if (matches.length === 0) {
            const li = document.createElement('li');
            li.className = 'grey-text';
            li.style.padding = '10px 14px';
            li.textContent = resultsEl.dataset.emptyLabel || 'No matching places found';
            resultsEl.appendChild(li);
            resultsEl.style.display = 'block';
            return;
        }

        matches.forEach(marker => {
            const p = marker.poiData;
            const li = document.createElement('li');
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'dyfi-search-result-btn';

            const icon = document.createElement('i');
            icon.className = 'material-icons';
            icon.style.fontSize = '18px';
            icon.style.color = '#005b4f';
            icon.textContent = p.busStopCode
                ? 'directions_bus'
                : (p.stationCode ? 'train' : (p.category === 'wildlife' ? 'emoji_nature' : 'place'));

            const nameSpan = document.createElement('span');
            nameSpan.textContent = getLocalizedName(p);

            const metaSpan = document.createElement('span');
            metaSpan.className = 'dyfi-search-result-meta';
            metaSpan.textContent = p.stationCode || p.busStopCode || (p.distanceFromCentre ? `${p.distanceFromCentre} mi` : '');

            btn.appendChild(icon);
            btn.appendChild(nameSpan);
            btn.appendChild(metaSpan);
            btn.addEventListener('click', () => {
                hideResults();
                if (activeCategoryFilter && p.category !== activeCategoryFilter) {
                    filterMarkers();
                }
                if (map) {
                    map.setView([p.latitude, p.longitude], 16, { animate: true });
                }
                openPoiModal(p);
            });
            li.appendChild(btn);
            resultsEl.appendChild(li);
        });
        resultsEl.style.display = 'block';
    }

    inputEl.addEventListener('input', updateSearch);
    inputEl.addEventListener('focus', updateSearch);
    if (clearEl) {
        clearEl.addEventListener('click', () => {
            inputEl.value = '';
            clearEl.style.display = 'none';
            hideResults();
            inputEl.focus();
        });
    }
    document.addEventListener('click', event => {
        if (!event.target.closest('.dyfi-map-search-wrap')) {
            hideResults();
        }
    });
}

function updateFilterButtonsState(category) {
    const target = category || 'all';
    document.querySelectorAll('.dyfi-filter-pill[data-filter]').forEach(btn => {
        const isActive = btn.getAttribute('data-filter') === target;
        btn.classList.toggle('active', isActive);
        btn.setAttribute('aria-pressed', isActive ? 'true' : 'false');
    });
}

/**
 * Filters displayed markers based upon category (or shows all if omitted).
 *
 * @param {string|undefined} category Category name to filter by.
 */
function filterMarkers(category) {
    activeCategoryFilter = category || null;
    updateFilterButtonsState(activeCategoryFilter);

    if (!markerCluster) {
        return;
    }

    markerCluster.clearLayers();
    const matching = allMarkers.filter(
        marker => category == null || marker.poiData.category === category
    );
    markerCluster.addLayers(matching);
}

/**
 * Resets filters and map view back to Cors Dyfi Nature Reserve and clears any route overlay.
 */
function panicButton() {
    clearDirectionsRoute();
    filterMarkers();
    if (!map) {
        return;
    }
    map.setView(DYFI_CENTRE_COORDS, DEFAULT_ZOOM, { animate: true });
}

/* ==================== DIRECTIONS (TASK 9) ==================== */

function openDirectionsModal() {
    const element = document.getElementById('directionsModal');
    const instance = M.Modal.getInstance(element) || M.Modal.init(element, {
        dismissible: true
    });
    instance.open();
}

function clearDirectionsRoute() {
    if (activeRouteLayer && map) {
        map.removeLayer(activeRouteLayer);
        activeRouteLayer = null;
    }
    if (activeDestinationMarker && map) {
        map.removeLayer(activeDestinationMarker);
        activeDestinationMarker = null;
    }
    const resultsEl = document.getElementById('directionsResults');
    const errorEl = document.getElementById('directionsError');
    if (resultsEl) resultsEl.style.display = 'none';
    if (errorEl) {
        errorEl.style.display = 'none';
        errorEl.textContent = '';
    }
}

function initDirectionsControls() {
    const geoBtn = document.getElementById('directionsUseMyLocationBtn');
    const destinationInput = document.getElementById('directionsDestination');
    const errorEl = document.getElementById('directionsError');
    if (!geoBtn || !destinationInput) {
        return;
    }
    geoBtn.addEventListener('click', () => {
        if (!navigator.geolocation) {
            if (errorEl) {
                errorEl.textContent = geoBtn.dataset.errorGeo || 'Location access is unavailable in this browser.';
                errorEl.style.display = 'block';
            }
            return;
        }
        geoBtn.disabled = true;
        navigator.geolocation.getCurrentPosition(
            position => {
                geoBtn.disabled = false;
                const lat = position.coords.latitude.toFixed(6);
                const lng = position.coords.longitude.toFixed(6);
                destinationInput.value = `${lat}, ${lng}`;
                const toCentreRadio = document.querySelector('input[name="directionsDirection"][value="toCentre"]');
                if (toCentreRadio) {
                    toCentreRadio.checked = true;
                }
                if (window.M && M.updateTextFields) {
                    M.updateTextFields();
                }
                const form = document.getElementById('directionsForm');
                if (form) {
                    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
                }
            },
            () => {
                geoBtn.disabled = false;
                if (errorEl) {
                    errorEl.textContent = geoBtn.dataset.errorGeo || 'Could not retrieve your GPS position.';
                    errorEl.style.display = 'block';
                }
            },
            { enableHighAccuracy: true, timeout: 8000 }
        );
    });
}

async function submitDirectionsForm(event) {
    event.preventDefault();
    const destinationInput = document.getElementById('directionsDestination');
    const errorEl = document.getElementById('directionsError');
    const resultsEl = document.getElementById('directionsResults');
    const destination = (destinationInput.value || '').trim();

    if (!destination) {
        errorEl.textContent = resultsEl.dataset.errorEmpty || 'Please enter a destination.';
        errorEl.style.display = 'block';
        return false;
    }

    const modeRadio = document.querySelector('input[name="directionsMode"]:checked');
    const mode = modeRadio ? modeRadio.value : 'driving';
    const dirRadio = document.querySelector('input[name="directionsDirection"]:checked');
    const direction = dirRadio ? dirRadio.value : 'fromCentre';

    errorEl.style.display = 'none';
    const loadingEl = document.getElementById('directionsLoading');
    const submitBtn = document.getElementById('directionsSubmitBtn');
    if (loadingEl) loadingEl.style.display = 'inline-block';
    if (submitBtn) submitBtn.disabled = true;

    try {
        const url = `/directions?destination=${encodeURIComponent(destination)}&mode=${encodeURIComponent(mode)}&direction=${encodeURIComponent(direction)}`;
        const data = await fetchJson(url);
        renderDirectionsResult(data);
    } catch (err) {
        errorEl.textContent = err.message || resultsEl.dataset.errorGeneric || 'Could not calculate directions.';
        errorEl.style.display = 'block';
    } finally {
        if (loadingEl) loadingEl.style.display = 'none';
        if (submitBtn) submitBtn.disabled = false;
    }
    return false;
}

function renderDirectionsResult(data) {
    clearDirectionsRoute();

    const resultsEl = document.getElementById('directionsResults');
    const summaryEl = document.getElementById('directionsSummary');
    const stepsHeadingEl = document.getElementById('directionsStepsHeading');
    const stepsListEl = document.getElementById('directionsStepsList');
    const transitSectionEl = document.getElementById('directionsTransitSection');
    const transitHeadingEl = document.getElementById('directionsTransitHeading');
    const transitListEl = document.getElementById('directionsTransitList');
    const osmLinkEl = document.getElementById('directionsOsmLink');
    const travelineLinkEl = document.getElementById('directionsTravelineLink');

    const estLabel = data.estimated ? ` ${resultsEl.dataset.labelEstimated || ''}` : '';
    const routeHeadline = data.originLabel
        ? `${data.originLabel} \u2192 ${data.destinationLabel}`
        : data.destinationLabel;
    summaryEl.textContent =
        `${routeHeadline} — ${resultsEl.dataset.labelDistance}: ${data.distanceMiles} ${resultsEl.dataset.labelMiles} | ` +
        `${resultsEl.dataset.labelDuration}: ${data.durationMinutes} ${resultsEl.dataset.labelMinutes}${estLabel}`;

    if (osmLinkEl && data.externalDirectionsUrl) {
        osmLinkEl.href = data.externalDirectionsUrl;
    }
    if (travelineLinkEl && data.transitPlannerUrl) {
        travelineLinkEl.href = data.transitPlannerUrl;
    }

    stepsHeadingEl.textContent = resultsEl.dataset.labelSteps || 'Route guidance';
    stepsListEl.innerHTML = '';
    (data.steps || []).forEach(stepText => {
        const li = document.createElement('li');
        li.textContent = stepText;
        stepsListEl.appendChild(li);
    });

    transitListEl.innerHTML = '';
    if (data.nearestTransitHubs && data.nearestTransitHubs.length > 0) {
        transitHeadingEl.textContent = resultsEl.dataset.labelTransit || 'Nearest public transport stops';
        data.nearestTransitHubs.forEach(hub => {
            const li = document.createElement('li');
            li.className = 'collection-item';
            const hubName = isWelshLocale() && hub.nameCy ? hub.nameCy : hub.name;
            li.textContent = `${hubName} (${hub.distanceMiles} ${resultsEl.dataset.labelMiles})`;
            transitListEl.appendChild(li);
        });
        transitSectionEl.style.display = 'block';
    } else {
        transitSectionEl.style.display = 'none';
    }

    resultsEl.style.display = 'block';

    if (map && Array.isArray(data.coordinates) && data.coordinates.length > 0) {
        const routeColor = data.mode === 'transit' ? '#2e7d32' : '#0277bd';
        const dashArray = data.estimated ? '8, 8' : null;
        activeRouteLayer = L.polyline(data.coordinates, {
            color: routeColor,
            weight: 5,
            opacity: 0.85,
            dashArray: dashArray
        }).addTo(map);

        const pinLat = typeof data.remoteLat === 'number' && data.remoteLat !== 0 ? data.remoteLat : data.destinationLat;
        const pinLng = typeof data.remoteLng === 'number' && data.remoteLng !== 0 ? data.remoteLng : data.destinationLng;
        const pinLabel = data.remoteLabel || data.destinationLabel;
        activeDestinationMarker = L.marker([pinLat, pinLng], {
            title: pinLabel
        }).addTo(map);

        map.fitBounds(activeRouteLayer.getBounds(), { padding: [40, 40] });
    }
}

/* ==================== WEBCAMS (TASK 10) ==================== */

async function initWebcams() {
    try {
        webcamFeeds = await fetchJson('/webcams');
        renderWebcamButtons();
    } catch (err) {
        console.error('Error loading webcams:', err);
    }
}

function getWebcamTitle(feed) {
    return isWelshLocale() && feed.nameCy ? feed.nameCy : (feed.nameEn || feed.id);
}

function getWebcamDescription(feed) {
    return isWelshLocale() && feed.descriptionCy ? feed.descriptionCy : (feed.descriptionEn || '');
}

function renderWebcamButtons() {
    const modalButtonsEl = document.getElementById('webcamButtons');
    const quickButtonsEl = document.getElementById('quickWebcamButtons');

    if (modalButtonsEl) {
        modalButtonsEl.innerHTML = '';
        modalButtonsEl.style.display = webcamFeeds.length > 1 ? 'block' : 'none';
    }
    if (quickButtonsEl) quickButtonsEl.innerHTML = '';

    webcamFeeds.forEach((feed, index) => {
        const label = getWebcamTitle(feed);

        if (modalButtonsEl && webcamFeeds.length > 1) {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'btn-small waves-effect waves-light teal darken-1';
            btn.style.marginRight = '8px';
            btn.style.marginBottom = '6px';
            btn.textContent = label;
            btn.addEventListener('click', () => selectWebcamFeed(feed));
            modalButtonsEl.appendChild(btn);
        }

        if (quickButtonsEl && webcamFeeds.length > 1) {
            const quickBtn = document.createElement('button');
            quickBtn.type = 'button';
            quickBtn.className = 'btn waves-effect waves-light teal darken-1';
            quickBtn.textContent = label;
            quickBtn.addEventListener('click', () => openWebcamModal(index));
            quickButtonsEl.appendChild(quickBtn);
        }
    });
}

function extractYouTubeVideoId(url) {
    if (!url) return null;
    try {
        const parsed = new URL(url, window.location.origin);
        const host = parsed.hostname.replace(/^www\./, '');
        if (host === 'youtu.be') {
            const id = parsed.pathname.split('/').filter(Boolean)[0];
            return id || null;
        }
        if (host === 'youtube.com' || host === 'youtube-nocookie.com') {
            if (parsed.pathname === '/watch') {
                return parsed.searchParams.get('v');
            }
            const parts = parsed.pathname.split('/').filter(Boolean);
            if ((parts[0] === 'embed' || parts[0] === 'live') && parts[1]) {
                return parts[1];
            }
        }
    } catch (e) {
        return null;
    }
    return null;
}

function toYouTubeEmbedUrl(url) {
    const videoId = extractYouTubeVideoId(url);
    if (!videoId) return null;
    return `https://www.youtube.com/embed/${encodeURIComponent(videoId)}?rel=0`;
}

function openWebcamModal(feedIndex) {
    const modalEl = document.getElementById('webcamModal');
    const videoEl = document.getElementById('webcamVideoPlayer');
    const iframeEl = document.getElementById('webcamYoutubePlayer');

    const instance = M.Modal.getInstance(modalEl) || M.Modal.init(modalEl, {
        dismissible: true,
        onCloseEnd: () => {
            if (videoEl) {
                videoEl.pause();
            }
            if (iframeEl) {
                iframeEl.src = '';
            }
        }
    });

    if (webcamFeeds.length > 0) {
        const chosen = webcamFeeds[feedIndex || 0] || webcamFeeds[0];
        selectWebcamFeed(chosen);
    }

    instance.open();
}

function selectWebcamFeed(feed) {
    const modalEl = document.getElementById('webcamModal');
    const titleEl = document.getElementById('webcamFeedTitle');
    const descEl = document.getElementById('webcamFeedDescription');
    const videoEl = document.getElementById('webcamVideoPlayer');
    const youtubeContainerEl = document.getElementById('webcamYoutubeContainer');
    const iframeEl = document.getElementById('webcamYoutubePlayer');
    const externalLinkEl = document.getElementById('webcamExternalLink');
    const errorEl = document.getElementById('webcamStreamError');

    const title = getWebcamTitle(feed);
    if (titleEl) titleEl.textContent = title;
    if (descEl) descEl.textContent = getWebcamDescription(feed);
    if (errorEl) {
        errorEl.style.display = 'none';
        errorEl.textContent = '';
    }

    const youtubeId = extractYouTubeVideoId(feed.streamUrl);
    const youtubeEmbedUrl = toYouTubeEmbedUrl(feed.streamUrl);

    if (youtubeEmbedUrl && iframeEl && youtubeContainerEl) {
        if (videoEl) {
            videoEl.pause();
            videoEl.removeAttribute('src');
            videoEl.style.display = 'none';
        }
        youtubeContainerEl.style.display = 'block';
        iframeEl.title = title || 'Dyfi Osprey Webcam';
        iframeEl.src = youtubeEmbedUrl;
        if (externalLinkEl && youtubeId) {
            externalLinkEl.href = `https://www.youtube.com/watch?v=${encodeURIComponent(youtubeId)}`;
            externalLinkEl.style.display = 'inline-block';
        }
        return;
    }

    if (youtubeContainerEl) youtubeContainerEl.style.display = 'none';
    if (iframeEl) iframeEl.src = '';
    if (externalLinkEl) externalLinkEl.style.display = 'none';

    if (videoEl && feed.streamUrl) {
        videoEl.style.display = 'block';
        videoEl.poster = getSafeImageUrl(feed.posterUrl);
        videoEl.onerror = () => {
            if (errorEl) {
                errorEl.textContent = modalEl.dataset.errorStream || 'Unable to play video stream.';
                errorEl.style.display = 'block';
            }
        };
        videoEl.src = feed.streamUrl;
        videoEl.load();
        videoEl.play().catch(() => {
            // Autoplay may be blocked by browser policy until the visitor clicks play.
        });
    }
}

/* ==================== WILDLIFE SIGHTINGS & KIOSK MODE ==================== */

async function openSightingsModal() {
    const modalEl = document.getElementById('sightingsModal');
    const listEl = document.getElementById('sightingsFeedList');
    if (!modalEl || !listEl) return;

    const instance = M.Modal.getInstance(modalEl) || M.Modal.init(modalEl, { dismissible: true });
    listEl.innerHTML = '';

    try {
        const items = await fetchJson('/sightings');
        if (!Array.isArray(items) || items.length === 0) {
            const li = document.createElement('li');
            li.className = 'collection-item grey-text';
            li.textContent = modalEl.dataset.emptyLabel || 'No wildlife sightings recorded yet.';
            listEl.appendChild(li);
        } else {
            items.forEach(item => {
                const li = document.createElement('li');
                li.className = 'collection-item';
                const speciesName = isWelshLocale() && item.speciesCy ? item.speciesCy : item.species;
                const notesText = isWelshLocale() && item.notesCy ? item.notesCy : (item.notes || '');
                const when = item.sightedAt ? new Date(item.sightedAt).toLocaleString() : '';

                const header = document.createElement('div');
                header.style.display = 'flex';
                header.style.justifyContent = 'space-between';
                header.style.flexWrap = 'wrap';
                header.style.gap = '6px';

                const titleEl = document.createElement('strong');
                titleEl.textContent = speciesName;
                header.appendChild(titleEl);

                if (when) {
                    const timeEl = document.createElement('span');
                    timeEl.className = 'grey-text';
                    timeEl.style.fontSize = '0.84rem';
                    timeEl.textContent = when;
                    header.appendChild(timeEl);
                }
                li.appendChild(header);

                if (item.locationName) {
                    const locEl = document.createElement('div');
                    locEl.className = 'teal-text text-darken-2';
                    locEl.style.fontSize = '0.88rem';
                    locEl.style.fontWeight = '600';
                    locEl.textContent = item.locationName;
                    li.appendChild(locEl);
                }
                if (notesText) {
                    const noteEl = document.createElement('p');
                    noteEl.style.margin = '4px 0 0 0';
                    noteEl.textContent = notesText;
                    li.appendChild(noteEl);
                }
                listEl.appendChild(li);
            });
        }
    } catch (err) {
        const li = document.createElement('li');
        li.className = 'collection-item red-text';
        li.textContent = err.message || 'Unable to load sightings.';
        listEl.appendChild(li);
    }

    instance.open();
}

function resetKioskState() {
    ['poiCard', 'directionsModal', 'webcamModal', 'sightingsModal'].forEach(id => {
        const el = document.getElementById(id);
        if (el && window.M && M.Modal) {
            const inst = M.Modal.getInstance(el);
            if (inst && inst.isOpen) {
                inst.close();
            }
        }
    });
    const searchInput = document.getElementById('mapSearchInput');
    const searchClear = document.getElementById('mapSearchClear');
    const searchResults = document.getElementById('mapSearchResults');
    if (searchInput) searchInput.value = '';
    if (searchClear) searchClear.style.display = 'none';
    if (searchResults) {
        searchResults.style.display = 'none';
        searchResults.innerHTML = '';
    }
    panicButton();
}

function initKioskMode() {
    const params = new URLSearchParams(window.location.search);
    if (params.get('kiosk') !== 'true') {
        return;
    }
    document.body.classList.add('dyfi-kiosk-active');

    let idleTimer = null;
    const IDLE_RESET_MS = 60000;
    function scheduleReset() {
        if (idleTimer) {
            window.clearTimeout(idleTimer);
        }
        idleTimer = window.setTimeout(resetKioskState, IDLE_RESET_MS);
    }

    ['pointerdown', 'keydown', 'touchstart', 'wheel', 'click'].forEach(evt => {
        document.addEventListener(evt, scheduleReset, { passive: true });
    });
    scheduleReset();
}

document.addEventListener('DOMContentLoaded', initMap);
