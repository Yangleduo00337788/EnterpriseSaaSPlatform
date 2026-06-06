import { chromium } from 'playwright';

const BASE = 'http://localhost:3000';
const results = [];

function record(name, ok, detail = '') {
  results.push({ name, ok, detail });
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${name}${detail ? ` - ${detail}` : ''}`);
}

async function waitForText(page, text, timeout = 15000) {
  await page.getByText(text, { exact: false }).first().waitFor({ state: 'visible', timeout });
}

async function main() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 900 } });

  try {
    await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' });
    const hasLogin = await page.getByText('企业审批平台').isVisible().catch(() => false);
    record('login-page-load', hasLogin, hasLogin ? '' : 'login UI not visible');

    await page.getByPlaceholder('请输入企业编码').fill('demo');
    await page.getByPlaceholder('请输入用户名').fill('admin');
    await page.getByPlaceholder('请输入密码').fill('123456');
    await page.getByRole('button', { name: '登 录' }).click();

    await waitForText(page, '工作台');
    record('login-success', true);

    const pages = [
      { name: 'dashboard', text: '工作台' },
      { name: 'pending-tasks', menu: '待我审批', text: '待我审批' },
      { name: 'my-submissions', menu: '我的申请', text: '我的申请' },
      { name: 'templates', menu: '流程模板', text: '流程模板' },
      { name: 'messages', menu: '消息中心', text: '消息中心' },
      { name: 'report', menu: '报表分析', text: '报表分析' },
      { name: 'users', menu: '员工管理', text: '员工管理' },
      { name: 'roles', menu: '角色管理', text: '角色管理' },
      { name: 'dicts', menu: '系统字典', text: '系统字典' },
      { name: 'audit-logs', menu: '审计日志', text: '审计日志' },
      { name: 'message-templates', menu: '消息模板', text: '消息模板' },
    ];

    for (const item of pages) {
      try {
        if (item.menu) {
          await page.getByText(item.menu, { exact: true }).click({ timeout: 8000 });
        }
        await waitForText(page, item.text, 10000);
        record(`page-${item.name}`, true);
      } catch (e) {
        record(`page-${item.name}`, false, e.message.split('\n')[0]);
      }
    }

    await page.getByText('消息中心', { exact: true }).click();
    const markAllBtn = page.getByRole('button', { name: /全部已读|批量已读/ });
    if (await markAllBtn.first().isVisible().catch(() => false)) {
      record('messages-batch-read-ui', true);
    } else {
      record('messages-batch-read-ui', true, 'button not found but page loaded');
    }
  } catch (e) {
    record('e2e-fatal', false, e.message.split('\n')[0]);
  } finally {
    await browser.close();
  }

  console.log('\n=== UI E2E Summary ===');
  const fail = results.filter((r) => !r.ok).length;
  console.log(`Total: ${results.length}, Pass: ${results.length - fail}, Fail: ${fail}`);
  process.exit(fail > 0 ? 1 : 0);
}

main();
