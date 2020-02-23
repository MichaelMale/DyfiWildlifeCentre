import React, { Fragment } from 'react';
import { Container } from 'semantic-ui-react';
import Menu from './components/Menu';
import GoogleMaps from "./components/GoogleMap";

const App = () => (
    <Fragment>
        <Menu />
        <Container fluid>
            <GoogleMaps/>
        </Container>
    </Fragment>
);

export default App;