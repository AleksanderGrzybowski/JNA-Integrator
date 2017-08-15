import React, { Component } from 'react';
import 'bootstrap/dist/css/bootstrap.css'
import axios from 'axios';

class App extends Component {

  constructor(props) {
    super(props);

    this.state = {
      func: 'sin(x)',
      left: '',
      right: '',
      points: 10000,
      result: ''
    }
  }

  handleFuncChanged = e => this.setState({func: e.target.value});
  handleLeftChanged = e => this.setState({left: e.target.value});
  handleRightChanged = e => this.setState({right: e.target.value});
  handlePointsChanged = e => this.setState({points: e.target.value});

  calculate = () => {
    axios.get('http://localhost:4567/calculate', {
      params: {
        left: this.state.left,
        right: this.state.right,
        numberOfPoints: this.state.points,
        func: this.state.func
      }
    })
      .then(response => this.setState({result: response.data.result}))
      .catch(err => console.error(err));
  };

  render() {
    return (
      <div className="container-fluid">
        <div className="row">
          <div className="col-md-3">
            <h1 className="text-center">Fill in data</h1>

            <div>
              <div className="form-group">
                <label>Function, for example sin(x^2)</label>
                <input type="text" className="form-control" value={this.state.func} onChange={this.handleFuncChanged}/>
              </div>
              <div className="form-inline form-group">
                <label>Range of integration</label><br/>
                <input type="text" size="2" className="form-control" value={this.state.left} onChange={this.handleLeftChanged}/>
                -
                <input type="text" size="2" className="form-control" value={this.state.right} onChange={this.handleRightChanged}/>
              </div>
              <div className="form-group">
                <label>Number of points</label>
                <input className="form-control" value={this.state.points} onChange={this.handlePointsChanged}/>
              </div>

              <button className="btn btn-default btn-block" onClick={this.calculate}>Calculate!</button>
            </div>
          </div>
        </div>

        <div className="row">
          <div className="col-md-12">
            <h1 id="result">{this.state.result}</h1>
          </div>
        </div>
      </div>
    );
  }
}

export default App;
