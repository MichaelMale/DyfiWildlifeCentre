import React from "react";
import Menu from './components/Menu';
import GoogleMaps from "./components/GoogleMap";
import BottomButtons from "./components/BottomButtons";

export default class App extends React.Component {
    render() {
        return (
            <React.Fragment>
                <Menu/>
                <GoogleMaps/>
                <BottomButtons/>
            </React.Fragment>
        )

    }
}