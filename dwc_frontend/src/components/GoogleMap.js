import React from 'react';

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
    height: '90vh',
};

class GoogleMap extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
        this.googleMapRef = React.createRef();
    }

    componentDidMount() {
        const googleMapScript = document.createElement("script");

        googleMapScript.src =
            `https://maps.googleapis.com/maps/api/js?key=${GOOGLE_MAP_API_KEY}&libraries=places`

        window.document.body.appendChild(googleMapScript);

        googleMapScript.addEventListener("load", () => {
           this.googleMap = this.createGoogleMap();
           this.marker = this.createMarker();
        });
    }

    createGoogleMap = () =>
        new window.google.maps.Map(this.googleMapRef.current, {
            zoom: 17,
            center: {
                lat: centreMarker.lat,
                lng: centreMarker.lng
            },
            disableDefaultUI: true,
            mapTypeId: 'hybrid',
        });

    createMarker = () =>
        new window.google.maps.Marker({
            position: { lat: centreMarker.lat, lng: centreMarker.lng },
            map: this.googleMap
        });

    render() {
        return (
            <div
                id="google-map"
                ref={this.googleMapRef}
                style ={mapStyles}
                />
        )
    }
}

export default GoogleMap;