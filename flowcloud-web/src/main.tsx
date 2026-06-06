import ReactDOM from 'react-dom/client';
import { Provider } from 'react-redux';
import './styles/semi.scss';
import { store } from './store';
import App from './App';
import './styles/global.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <Provider store={store}>
    <App />
  </Provider>
);
