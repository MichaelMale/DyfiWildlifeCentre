import React, { Fragment } from 'react';
import { Container } from 'semantic-ui-react';

import Menu from './components/Menu';
// eslint-disable-next-line no-unused-vars
import AboutCard from './components/AboutCard';
import GoogleMaps from "./components/GoogleMap";

const App = () => (
    <Fragment>
        <Menu />
        <Container fluid id="cont">
         <GoogleMaps id="map_canvas"/>
        </Container>
    </Fragment>
);

export default App;