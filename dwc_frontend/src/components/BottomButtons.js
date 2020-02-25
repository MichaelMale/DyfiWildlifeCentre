import React from 'react';
import { Button, Container } from "semantic-ui-react";

class BottomButtons extends React.Component {

    constructor(props) {
        super(props);
        this.resetClicked = this.resetClicked.bind(this);
    }

    resetClicked() {
        console.log("Reset button clicked");
    }

    filtersClicked() {
        console.log("Filter button clicked");
    }

    render() {
        return (
          <Container textAlign="center">
              <Button.Group>
                  <Button color="facebook" onClick={this.filtersClicked}>Filters</Button>
                  <Button color="red" onClick={this.resetClicked}>Reset</Button>
              </Button.Group>
          </Container>
        );
    }
}

export default BottomButtons;