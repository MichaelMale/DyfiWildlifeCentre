import React from 'react';
import { shallow } from 'enzyme';
import App from '../App';
import GoogleMaps from "../components/GoogleMap";
import BottomButtons from "../components/BottomButtons";

/**
 * Unit tests for the App component
 */
describe('App unit tests', () => {
    let wrapper;

    beforeEach(() => wrapper = shallow(<App />));

   it('renders one <Fragment /> component', () => {
       expect(wrapper.find('Fragment').length).toEqual(1);
   });

   it('renders one <GoogleMaps /> component', () => {
      expect(wrapper.find('GoogleMap').length).toEqual(1);
   });

   it('should render the GoogleMaps component', () => {
       expect(wrapper.containsMatchingElement(<GoogleMaps/>)).toEqual(true);
   });

   it('should render the BottomButton component', () => {
       expect(wrapper.containsMatchingElement(<BottomButtons/>)).toEqual(true);
    });

});