const React = require('react');
const ReactDOMServer = require('react-dom/server');
const { Nav } = require('@douyinfe/semi-ui/lib/cjs/navigation/index.js');
const html = ReactDOMServer.renderToString(React.createElement(Nav, {
  items: [{ itemKey: '1', text: 'Item 1' }],
  footer: { collapseButton: true }
}));
const matches = html.match(/class="([^"]*navigation[^"]*)"/g);
console.log(matches);
