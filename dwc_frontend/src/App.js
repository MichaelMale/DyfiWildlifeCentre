import React, { Fragment } from 'react';
import { Container } from 'semantic-ui-react';

import Menu from './components/Menu';
import Login from './components/Login';

const App = () => (
    <Fragment>
        <Menu />
        <Container>
            <Login />
        </Container>
    </Fragment>
);

export default App;