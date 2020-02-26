import React from 'react';
import { Card } from 'semantic-ui-react';

const items = [
    {
        header: 'About',
        description:
            'This web application is designed to help staff and visitors at the Dyfi' +
            ' Wildlife Centre learn more about the environment that they are in, and' +
            ' the work that the centre are doing.' +
            '' +
            'The application was created by Michael Male, in cooperation with' +
            ' Aberystwyth University and the Montgomeryshire Wildlife Trust.'
    },
    {
        header: 'Disclaimer',
        description: 'Source code is licensed under the Apache License 2.0.' +
            '' +
            '"Maps JavaScript API" was created by Google LLC, with offices at Gordon' +
            ' House, Barrow Street, Dublin 4, Ireland. It is used under the Google' +
            ' Maps Platform License Agreement.'
    },
    {
        header: 'Technical stuff',
        description: 'The frontend of this website was created using React with the' +
            ' Semantic UI framework. This website linked to an API created in Java' +
            ' using Spring Boot, which gathers data from a PostgreSQL relational' +
            ' database.'
    }
]

const CardExampleGroupCentered = () => <Card.Group centered items={items} />;

export default CardExampleGroupCentered
