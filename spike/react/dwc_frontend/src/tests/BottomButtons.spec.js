import React from 'react';
import {shallow} from 'enzyme';
import BottomButtons from "../components/BottomButtons";
import {Button} from "semantic-ui-react";

describe('Bottom buttons unit tests', () => {
    let wrapper;
    beforeEach(() => wrapper = shallow(<BottomButtons/>));

    it('should render two Button components', () => {
        expect(wrapper.find('Button').length).toEqual(2);
    });

    it('should render the BottomButton component', () => {
        expect(wrapper.containsMatchingElement(Button)).toEqual(true);
    });
});