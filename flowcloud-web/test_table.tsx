import React from 'react';
import ReactDOMServer from 'react-dom/server';
import { Table } from '@douyinfe/semi-ui';

const columns = [{ title: 'Name', dataIndex: 'name' }];
const data = [{ key: '1', name: 'John Brown' }];
const pagination = { total: 100, pageSize: 10 };
const html = ReactDOMServer.renderToString(React.createElement(Table, { columns, dataSource: data, pagination }));
console.log(html);
