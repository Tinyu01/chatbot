import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Home from './components/Home';
import Chat from './components/Chat';
import NotFound from './components/NotFound';

const App: React.FC = () => {
    return (
        <Router>
            <div className="App">
                <Switch>
                    <Route path="/" exact component={Home} />
                    <Route path="/chat" component={Chat} />
                    <Route component={NotFound} />
                </Switch>
            </div>
        </Router>
    );
};

export default App;