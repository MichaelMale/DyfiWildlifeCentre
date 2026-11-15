/*
 * Copyright (C) 2020-2026 Michael Male / Dyfi Wildlife Centre
 * Licensed under the Apache License, Version 2.0
 */

(function () {
    const DYFI_CENTRE = [52.568774, -3.918031];
    const EARTH_RADIUS_MILES = 3958.756;

    function toRad(deg) {
        return deg * (Math.PI / 180);
    }

    function calculateDistanceMiles(lat, lng) {
        const dLat = toRad(lat - DYFI_CENTRE[0]);
        const dLng = toRad(lng - DYFI_CENTRE[1]);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(toRad(DYFI_CENTRE[0])) * Math.cos(toRad(lat))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS_MILES * c * 100) / 100;
    }

    document.addEventListener('DOMContentLoaded', function () {
        const mapEl = document.getElementById('addPoiPickerMap');
        if (!mapEl || typeof L === 'undefined') {
            return;
        }

        const latInput = document.getElementById('latitude');
        const lngInput = document.getElementById('longitude');
        const postcodeInput = document.getElementById('postcode');
        const statusEl = document.getElementById('mapPickerStatus');
        const clearBtn = document.getElementById('clearMapPinBtn');
        const centreBtn = document.getElementById('centreMapReserveBtn');
        const lookupBtn = document.getElementById('lookupPostcodeBtn');
        const errorEl = document.getElementById('locationPickerError');
        const form = document.getElementById('poiSubmissionForm') || document.getElementById('poiUpdateForm');

        let isLocked = mapEl.dataset.locked === 'true';
        const nonePickedMsg = mapEl.dataset.nonePicked || 'No map point selected yet. Click on the map below to place a pin.';
        const pickedTemplate = mapEl.dataset.pickedTemplate || 'Selected point: {0}, {1} ({2} miles from Dyfi Wildlife Centre)';
        const lookupErrorMsg = mapEl.dataset.lookupError || 'Could not find that UK postcode.';
        const requiredMsg = mapEl.dataset.requiredMsg || 'Please pick a point on the map (or enter coordinates or a UK postcode).';

        const pickerMap = L.map(mapEl, {
            center: DYFI_CENTRE,
            zoom: 15
        });

        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(pickerMap);

        // Subtle marker showing Cors Dyfi / Wildlife Centre reference
        L.circleMarker(DYFI_CENTRE, {
            radius: 7,
            color: '#004239',
            fillColor: '#00796b',
            fillOpacity: 0.9,
            weight: 2
        }).bindTooltip('Dyfi Wildlife Centre', { direction: 'top' }).addTo(pickerMap);

        // Plot existing POIs lightly as spatial context so staff avoid duplicate pins
        fetch('/poi', { credentials: 'include' })
            .then(resp => resp.ok ? resp.json() : [])
            .then(pois => {
                if (!Array.isArray(pois)) return;
                pois.forEach(p => {
                    if (typeof p.latitude === 'number' && typeof p.longitude === 'number') {
                        L.circleMarker([p.latitude, p.longitude], {
                            radius: 5,
                            color: '#455a64',
                            fillColor: '#90a4ae',
                            fillOpacity: 0.55,
                            weight: 1
                        }).bindTooltip(p.name || 'POI', { direction: 'top' }).addTo(pickerMap);
                    }
                });
            })
            .catch(() => {});

        let pickedMarker = null;

        function hideError() {
            if (errorEl) {
                errorEl.style.display = 'none';
                errorEl.textContent = '';
            }
        }

        function showError(msg) {
            if (errorEl) {
                errorEl.textContent = msg;
                errorEl.style.display = 'flex';
            }
        }

        function updateStatus(lat, lng) {
            if (!statusEl) return;
            if (lat == null || lng == null || (lat === 0 && lng === 0)) {
                statusEl.textContent = nonePickedMsg;
                statusEl.className = 'dyfi-picker-status';
                if (clearBtn) clearBtn.disabled = true;
                return;
            }
            const dist = calculateDistanceMiles(lat, lng).toFixed(2);
            statusEl.textContent = pickedTemplate
                .replace('{0}', lat.toFixed(6))
                .replace('{1}', lng.toFixed(6))
                .replace('{2}', dist);
            statusEl.className = 'dyfi-picker-status dyfi-picker-status--active';
            if (clearBtn) clearBtn.disabled = isLocked;
        }

        function applyLockVisualState() {
            mapEl.classList.toggle('dyfi-picker-map--locked', isLocked);
            if (pickedMarker && pickedMarker.dragging) {
                if (isLocked) {
                    pickedMarker.dragging.disable();
                } else {
                    pickedMarker.dragging.enable();
                }
            }
            if (clearBtn) {
                clearBtn.disabled = isLocked || !pickedMarker;
            }
        }

        window.dyfiSetMapPickerLocked = function (locked) {
            isLocked = Boolean(locked);
            mapEl.dataset.locked = isLocked ? 'true' : 'false';
            applyLockVisualState();
        };

        function setPickedCoordinates(lat, lng, options) {
            const opts = options || {};
            const fixedLat = Number(lat.toFixed(6));
            const fixedLng = Number(lng.toFixed(6));

            if (latInput) latInput.value = fixedLat.toFixed(6);
            if (lngInput) lngInput.value = fixedLng.toFixed(6);
            if (postcodeInput && !opts.preservePostcode) {
                // Backend rejects simultaneous postcode + non-zero coordinates
                postcodeInput.value = '';
            }
            if (window.M && M.updateTextFields) {
                M.updateTextFields();
            }

            if (!pickedMarker) {
                const pinIcon = L.divIcon({
                    className: 'dyfi-picker-pin-wrapper',
                    html: '<div class="dyfi-picker-pin"><i class="material-icons">add_location_alt</i></div>',
                    iconSize: [38, 38],
                    iconAnchor: [19, 36]
                });
                pickedMarker = L.marker([fixedLat, fixedLng], {
                    draggable: !isLocked,
                    icon: pinIcon
                }).addTo(pickerMap);

                pickedMarker.on('dragend', function () {
                    const pos = pickedMarker.getLatLng();
                    setPickedCoordinates(pos.lat, pos.lng);
                });
            } else {
                pickedMarker.setLatLng([fixedLat, fixedLng]);
            }

            if (opts.pan) {
                pickerMap.setView([fixedLat, fixedLng], Math.max(pickerMap.getZoom(), 15), { animate: !opts.immediate });
            }

            hideError();
            updateStatus(fixedLat, fixedLng);
            applyLockVisualState();
        }

        function clearPickedPin() {
            if (isLocked) return;
            if (pickedMarker) {
                pickerMap.removeLayer(pickedMarker);
                pickedMarker = null;
            }
            if (latInput) latInput.value = '0';
            if (lngInput) lngInput.value = '0';
            if (window.M && M.updateTextFields) {
                M.updateTextFields();
            }
            updateStatus(0, 0);
        }

        pickerMap.on('click', function (e) {
            if (isLocked) return;
            setPickedCoordinates(e.latlng.lat, e.latlng.lng);
        });

        function syncFromManualInputs(immediate) {
            const lat = parseFloat(latInput ? latInput.value : '0');
            const lng = parseFloat(lngInput ? lngInput.value : '0');
            if (!Number.isNaN(lat) && !Number.isNaN(lng) && (lat !== 0 || lng !== 0)
                && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                setPickedCoordinates(lat, lng, { pan: true, immediate: Boolean(immediate) });
            } else if (lat === 0 && lng === 0) {
                clearPickedPin();
            }
        }

        if (latInput) latInput.addEventListener('change', () => syncFromManualInputs(false));
        if (lngInput) lngInput.addEventListener('change', () => syncFromManualInputs(false));

        if (postcodeInput) {
            postcodeInput.addEventListener('input', function () {
                if (!isLocked && (postcodeInput.value || '').trim().length > 0) {
                    clearPickedPin();
                    hideError();
                }
            });
        }

        if (clearBtn) {
            clearBtn.addEventListener('click', clearPickedPin);
        }

        if (centreBtn) {
            centreBtn.addEventListener('click', function () {
                pickerMap.setView(DYFI_CENTRE, 15, { animate: true });
            });
        }

        if (lookupBtn && postcodeInput) {
            lookupBtn.addEventListener('click', function () {
                if (isLocked) return;
                const raw = (postcodeInput.value || '').trim();
                if (!raw) {
                    showError(lookupErrorMsg);
                    return;
                }
                hideError();
                lookupBtn.disabled = true;
                fetch('https://api.postcodes.io/postcodes/' + encodeURIComponent(raw))
                    .then(r => r.json())
                    .then(payload => {
                        if (payload && payload.status === 200 && payload.result
                            && typeof payload.result.latitude === 'number'
                            && typeof payload.result.longitude === 'number') {
                            setPickedCoordinates(payload.result.latitude, payload.result.longitude, { pan: true });
                        } else {
                            showError(lookupErrorMsg);
                        }
                    })
                    .catch(() => showError(lookupErrorMsg))
                    .finally(() => {
                        lookupBtn.disabled = false;
                    });
            });
        }

        if (form) {
            form.addEventListener('submit', function (event) {
                const lat = parseFloat(latInput ? latInput.value : '0') || 0;
                const lng = parseFloat(lngInput ? lngInput.value : '0') || 0;
                const postcode = (postcodeInput ? postcodeInput.value : '').trim();
                if (lat === 0 && lng === 0 && postcode.length === 0) {
                    event.preventDefault();
                    showError(requiredMsg);
                    mapEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            });
        }

        // Sync initial value if page loaded with non-zero coordinates
        syncFromManualInputs(true);
        applyLockVisualState();
    });
})();
