const React = require('react');
const ReactDOMServer = require('react-dom/server');
const { Table } = require('@douyinfe/semi-ui/lib/cjs/table/index.js');
const columns = [{ title: 'Name', dataIndex: 'name' }];
const data = [{ key: '1', name: 'John Brown' }];
const pagination = { total: 100, pageSize: 10 };
const html = ReactDOMServer.renderToString(React.createElement(Table, { columns, dataSource: data, pagination }));
const matches = html.match(/class="([^"]*pagination[^"]*)"/g);
console.log(matches);
