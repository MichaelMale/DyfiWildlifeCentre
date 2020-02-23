/**
 * Google Maps component - This component utilises the Google Maps JavaScript API and
 * produces a map.
 *
 * Original code by Peter Kassenaar, available here: https://medium.com/@PeterKassenaar/icyi-i-rewrote-the-component-as-a-functional-component-using-react-hooks-6cf644a73bdd
 */
import React, {useEffect, useRef} from 'react';

// Variables
const GOOGLE_MAP_API_KEY = 'AIzaSyBORcn8smmSs49a79sVJoW5Zpk-p4HxfQg';
const centreMarker = { // The centre marker is the Dyfi Wildlife Centre, where the map
    // focuses.
    lat: 52.568774,
    lng: -3.918031
};
// styles, height and width should be the same size as the container holding it
const mapStyles = {
    width: '100%',
    height: '100vh',
};

function GoogleMaps(props) {
    // refs
    const googleMapRef = React.createRef();
    const googleMap = useRef(null);
    const marker = useRef(null);

    /**
     * Creates an instance of a Google map
     * @returns {Map<any, any>} Said instance
     */
    const createGoogleMap = () =>
        new window.google.maps.Map(googleMapRef.current, {
            zoom: 17,
            center: {
                lat: centreMarker.lat,
                lng: centreMarker.lng
            },
            disableDefaultUI: true,
            mapTypeId: 'hybrid'
        });

    /**
     * Creates a marker
     * @returns {window.google.maps.Marker} marker to be passed to the map
     */
    const createMarker = () =>
        new window.google.maps.Marker({
            position: {lat: centreMarker.lat, lng: centreMarker.lng},
            map: googleMap.current
        });

    /**
     *
     */
    useEffect(() => {
        const googleMapScript = document.createElement('script');
        googleMapScript.src = `https://maps.googleapis.com/maps/api/js?key=${GOOGLE_MAP_API_KEY}&libraries=places`
        window.document.body.appendChild(googleMapScript);

        googleMapScript.addEventListener('load', () => {
            googleMap.current = createGoogleMap();
            marker.current = createMarker()
        })
    });

    return (
        <div
            id="google-map"
            ref={googleMapRef}
            style={mapStyles}
        />
    )

}

export default GoogleMaps