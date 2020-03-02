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

function initMap() {
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
            outDuration: 500});

        instance.open();
    });

    // const springGeneratedMarker = new google.maps.Marker({
    //     position: {lat: [[${marker_test_lat}]], lng: [[${marker_test_long}]]},
    //     map: map,
    //     title: '[[${marker_test_name}]]'
    // });


}