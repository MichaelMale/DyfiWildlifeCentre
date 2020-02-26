import React from 'react';
import {
    Button,
    Form,
    Grid,
    Header,
    Message,
    Segment,
} from 'semantic-ui-react';
export default () => (
    <Grid centered columns={2}>
        <Grid.Column>
            <Header as="h2" textAlign="center">
                Login
            </Header>
            <Segment>
                <Form size="large">
                    <Form.Input
                        fluid
                        icon="user"
                        iconPosition="left"
                        placeholder="Email address"
                    />
                    <Form.Input
                        fluid
                        icon="lock"
                        iconPosition="left"
                        placeholder="Password"
                        type="password"
                    />
                    <Button color="blue" fluid size="large">
                        Login
                    </Button>
                </Form>
            </Segment>
            <Message>
                Authorised users only. Please e-mail <a href="mailto:info@montwt.co.uk">
                info@montwt.co.uk</a> if you require an account.
            </Message>
        </Grid.Column>
    </Grid>
);