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

/* Co-ordinates for the Cors Dyfi Nature Reserve, which should be located at
 the centre of the map. */
const DYFI_WILDLIFE_CENTRE = [52.568774, -3.918031];
const INITIAL_ZOOM = 16;

let allMarkers = [];
let markerCluster;
let map;

/**
 * An asynchronous function, that uses the JavaScript Fetch API to perform an HTTP GET Request to an API containing
 * all Points Of Interest. The current utilisation of this is as a helper function to support initMap() in creating
 * markers.
 * @param url   The URL of a RESTful API that the request is to be made to.
 * @returns {Promise<Array>} A promise that should contain an array containing the Points of Interest.
 */
async function getPointsOfInterest(url) {
    const response = await fetch(url, {
        method: 'GET',
        credentials: 'include'
    });

    if (!response.ok) {
        throw new Error('Failed to fetch points of interest: HTTP ' + response.status);
    }

    return response.json();
}

/**
 * Performs initialisation of the map, using the open-source Leaflet library
 * with OpenStreetMap tiles. The function also generates markers for every
 * Point of Interest, grouped into clusters with Leaflet.markercluster.
 *
 * @returns {Promise<void>} Promise for the initialisation of the map.
 */
async function initMap() {
    map = L.map('map', {
        center: DYFI_WILDLIFE_CENTRE,
        zoom: INITIAL_ZOOM,
        zoomControl: false
    });

    /* OpenStreetMap's standard tile layer. Attribution is required by the
     tile usage policy: https://operations.osmfoundation.org/policies/tiles/
     */
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    markerCluster = L.markerClusterGroup();
    map.addLayer(markerCluster);

    const allPointsOfInterest = await getPointsOfInterest('/poi');
    allPointsOfInterest.forEach(
        poi => {
            const marker = L.marker([poi.latitude, poi.longitude], {
                title: poi.name
            });
            // Custom properties used by the POI card and the filters
            marker.name = poi.name;
            marker.description = poi.description;
            marker.distanceFromCentre = poi.distanceFromCentre;
            marker.category = poi.category;

            allMarkers.push(marker);
            markerCluster.addLayer(marker); // Iterative addition of a
            // marker to the cluster, that performs clustering automatically
            marker.on('click', function () {
                const element = document.getElementById('poiCard');
                /* textContent is used rather than innerHTML so that
                 user-supplied text cannot inject markup (XSS) */
                element.querySelector('#poi_title').textContent = marker.name;
                element.querySelector('#poi_description').textContent =
                    marker.description == null ? '' : marker.description;
                element.querySelector('#poi_distance').textContent =
                    marker.distanceFromCentre;
                const instance = M.Modal.init(element, {
                    dismissible: true,
                    inDuration: 500,
                    outDuration: 500
                });

                instance.open();
            });
        }
    );
}

/**
 * Filters markers based upon a category.
 * @param category  String containing the marker's category. If this is
 *                  omitted, every marker is shown.
 */
function filterMarkers(category) {
    markerCluster.clearLayers();

    // Iterates through all points of interest. If a point of interest
    // matches the category, or no category was given, then the marker is
    // added back to the cluster. Otherwise, it is left out.
    for (let i = 0; i < allMarkers.length; i++) {
        const marker = allMarkers[i];

        if (category == null || marker.category === category) {
            markerCluster.addLayer(marker);
        }
    }
}

/**
 * A 'panic button' feature, that resets the map to its starting position.
 */
function panicButton() {
    map.flyTo(DYFI_WILDLIFE_CENTRE, INITIAL_ZOOM);
}
