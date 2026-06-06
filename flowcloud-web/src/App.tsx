import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from '@douyinfe/semi-ui';
import zh_CN from '@douyinfe/semi-ui/lib/es/locale/source/zh_CN';
import AppRouter from './router';

export default function App() {
  return (
    <ConfigProvider locale={zh_CN}>
      <BrowserRouter>
        <AppRouter />
      </BrowserRouter>
    </ConfigProvider>
  );
}
