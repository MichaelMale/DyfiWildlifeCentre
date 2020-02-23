import React, { Fragment } from 'react';
import { Container } from 'semantic-ui-react';
import Menu from './components/Menu';
import GoogleMaps from "./components/GoogleMap";

const App = () => (
    <Fragment>
        <Menu />
            <GoogleMaps/>
    </Fragment>
);

export default App;