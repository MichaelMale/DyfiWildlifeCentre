function initMap() {
    const dyfiWildlifeCentre = {lat: 52.568774, lng: -3.918031};
    const map = new google.maps.Map(document.getElementById('map'), {
        zoom: 16,
        center: dyfiWildlifeCentre,
        mapTypeId: 'hybrid',
        disableDefaultUI: true
    });

    const marker = new google.maps.Marker({
        position: dyfiWildlifeCentre,
        map: map,
        title: 'Dyfi Wildlife Centre'
    });
    marker.addListener('click', function () {
        const element = document.getElementById('poiCard');
        element.querySelector('#poi_title').innerHTML = "Title";
        element.querySelector('#poi_description').innerHTML = "Description";
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