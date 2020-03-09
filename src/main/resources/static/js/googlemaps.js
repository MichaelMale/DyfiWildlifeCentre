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

/**
 * An asynchronous function, that uses the JavaScript Fetch API to perform an HTTP GET Request to an API containing
 * all Points Of Interest. The current utilisation of this is as a helper function to support initMap() in creating
 * markers.
 * @param url   The URL of a RESTful API that the request is to be made to.
 * @returns {Promise<Array>} A promise that should contain an array containing the Points of Interest.
 */
async function getPointsOfInterest(url) {
    const response = await fetch(url);

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
    console.log(allPointsOfInterest); // Debug data to confirm correct POIs have been entered.
    const dyfiWildlifeCentre = {lat: 52.568774, lng: -3.918031};
    const map = new google.maps.Map(document.getElementById('map'), {
        zoom: 16,
        center: dyfiWildlifeCentre,
        mapTypeId: 'hybrid',
        disableDefaultUI: true,
        clickableIcons: false
    });

    const marker = new google.maps.Marker({
        position: dyfiWildlifeCentre,
        map: map,
        title: 'Dyfi Wildlife Centre',
        description: 'Description'
    });
    marker.addListener('click', function () {
        const element = document.getElementById('poiCard');
        element.querySelector('#poi_title').innerHTML = marker.title;
        element.querySelector('#poi_description').innerHTML = marker.description;
        const instance = M.Modal.init(element, {
            dismissible: true,
            inDuration: 500,
            outDuration: 500
        });

        instance.open();
    });

    // const springGeneratedMarker = new google.maps.Marker({
    //     position: {lat: [[${marker_test_lat}]], lng: [[${marker_test_long}]]},
    //     map: map,
    //     title: '[[${marker_test_name}]]'
    // });


}