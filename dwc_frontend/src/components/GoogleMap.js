/**
 * Google Maps component - This component utilises the Google Maps JavaScript API and
 * produces a map.
 *
 * Original code by Peter Kassenaar, available here: https://medium.com/@PeterKassenaar/icyi-i-rewrote-the-component-as-a-functional-component-using-react-hooks-6cf644a73bdd
 */
import React, {useEffect, useRef} from 'react';

// Variables
const GOOGLE_MAP_API_KEY = 'AIzaSyBORcn8smmSs49a79sVJoW5Zpk-p4HxfQg';
const myLocation = { // CN Tower Landmark
    lat: 43.642567,
    lng: -79.387054
};
// styles
const mapStyles = {
    width: '100%',
    height: '1000px',
};

function GoogleMaps(props) {
    // refs
    const googleMapRef = React.createRef();
    const googleMap = useRef(null);
    const marker = useRef(null);

    // helper functions
    const createGoogleMap = () =>
        new window.google.maps.Map(googleMapRef.current, {
            zoom: 14,
            center: {
                lat: myLocation.lat,
                lng: myLocation.lng
            }
        });

    const createMarker = () =>
        new window.google.maps.Marker({
            position: {lat: myLocation.lat, lng: myLocation.lng},
            map: googleMap.current
        });

    // useEffect Hook
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