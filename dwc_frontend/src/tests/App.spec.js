import React from 'react';
import { shallow } from 'enzyme';
import App from '../App';
import GoogleMaps from "../components/GoogleMap";
import {Menu} from "semantic-ui-react";

describe('App', () => {
    let wrapper;

    beforeEach(() => wrapper = shallow(<App />));

   it('renders one <Fragment /> component', () => {
       expect(wrapper.find('Fragment').length).toEqual(1);
   });

   it('renders one <GoogleMaps /> component', () => {
      expect(wrapper.find('GoogleMaps').length).toEqual(1);
   });

   it('should render the GoogleMaps component', () => {
       expect(wrapper.containsMatchingElement(<GoogleMaps/>)).toEqual(true);
   });

});