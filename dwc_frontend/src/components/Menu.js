import React from 'react';
import { Container, Menu } from 'semantic-ui-react';

export default () => (
    <Menu>
        <Container>
            <Menu.Item as="a" header>
                Dyfi Wildlife Centre
            </Menu.Item>

            <Menu.Menu position="right">
                <Menu.Item as="a" name="login">
                    Admin Panel
                </Menu.Item>
            </Menu.Menu>
        </Container>
    </Menu>
);