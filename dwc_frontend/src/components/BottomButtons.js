import React from 'react';
import { Button, Container } from "semantic-ui-react";

export default () => (
    <Container fluid>
        <Button floated="left">Filters</Button>
        <Button floated="right">Reset</Button>
    </Container>
);