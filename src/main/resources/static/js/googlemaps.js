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

    return response.json();
}

/**
 * Performs initialisation of the Google Map, using the Google Maps for JavaScript API. The function also generates
 * markers from an array of them.
 *
 * @returns {Promise<void>} Promise for the initialisation of the Google Map.
 */
async function initMap() {
    const allPointsOfInterest = await getPointsOfInterest('/poi');
    const dyfiWildlifeCentre = {lat: 52.568774, lng: -3.918031}; // Co-ordinates for the Cors Dyfi Nature Reserve,
    // which should be located at the centre of the map.
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 16,
        center: dyfiWildlifeCentre,
        mapTypeId: 'hybrid',
        disableDefaultUI: true,
        clickableIcons: false
    });
    /* Creates a marker clusterer. Initial markers are null as they are
     added iteratively
     */
    markerCluster = new MarkerClusterer(map,null,{imagePath: 'images/clusters/m'});
    allPointsOfInterest.forEach(
        poi => {
            const marker = new google.maps.Marker({
                position: {lat: poi.latitude, lng: poi.longitude},
                map: map,
                title: poi.name,
                description: poi.description,
                distanceFromCentre: poi.distanceFromCentre,
                category: poi.category
            });
            allMarkers.push(marker);
            markerCluster.addMarker(marker); // Iterative addition of a
            // marker to the cluster, that performs clustering automatically
            marker.addListener('click', function () {
                const element = document.getElementById('poiCard');
                element.querySelector('#poi_title').innerHTML = marker.title;
                element.querySelector('#poi_description').innerHTML = marker.description;
                element.querySelector('#poi_distance').innerHTML = marker.distanceFromCentre;
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
 * @param category  String containing the marker's category.
 */
function filterMarkers(category) {

    markerCluster.clearMarkers();

    // Iterates through all points of interest. If a point of interest
    // matches the category, or has no category, then the marker is set
    // to visible. Otherwise, the marker is set to invisible.
    for (let i = 0; i < allMarkers.length; i ++) {
        let marker = allMarkers[i];


        if (category == null || marker.category === category) {
            marker.setVisible(true);
            markerCluster.addMarker(marker);
        } else {
            marker.setVisible(false);

        }
    }
}

/**
 * A 'panic button' feature, that resets the map to its starting position.
 */
function panicButton() {
    const latlng = new google.maps.LatLng(52.568774, -3.918031);
    map.panTo(latlng);
    map.setZoom(16);
}
