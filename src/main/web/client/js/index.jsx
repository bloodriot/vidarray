import React from 'react';
import ReactDOM from 'react-dom';
import {VidArray} from './VidArray.jsx';

class Index extends React.Component {
    constructor() {
        super();
        this.state = {
            message: "test state message"
        };
    }

    updateMessage() {
        this.setState({
            message: "changed message"
        });
    }
    
    render() {
        return (
            <div>
            <h1>Hello World! - {this.state.message}</h1>
            <button onClick={this.updateMessage.bind(this)}>Update State</button>
            </div>
        );
    }
}

var vidArray = new VidArray();
var details = vidArray.getFileDetails(1).then(function(details) {
    alert(details);
});

alert(vidArray.getFileDetails(1));

ReactDOM.render(
    <Index/>,
    document.getElementById("root")
);
module.hot.accept();