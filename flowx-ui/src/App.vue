<template>
  <n-config-provider :locale="zhCN" :date-locale="dateZhCN" :theme-overrides="themeOverrides">
    <n-loading-bar-provider>
      <n-message-provider>
        <n-dialog-provider>
          <n-notification-provider>
            <router-view />
          </n-notification-provider>
        </n-dialog-provider>
      </n-message-provider>
    </n-loading-bar-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { zhCN, dateZhCN, type GlobalThemeOverrides } from 'naive-ui'

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#18a058',
    primaryColorHover: '#36ad6a',
    primaryColorPressed: '#0c7a43',
    primaryColorSuppl: '#36ad6a',
    borderRadius: '12px',
    borderRadiusSmall: '8px',
  },
  Card: {
    borderRadius: '16px',
  },
  Button: {
    borderRadius: '10px',
  },
  Input: {
    borderRadius: '10px',
  },
  Select: {
    borderRadius: '10px',
  },
  Tag: {
    borderRadius: '8px',
  },
  Modal: {
    borderRadius: '20px',
  },
  Dialog: {
    borderRadius: '20px',
  },
  Drawer: {
    borderRadius: '16px',
  },
  DataTable: {
    borderRadius: '12px',
  },
  Alert: {
    borderRadius: '12px',
  },
  Message: {
    borderRadius: '12px',
  },
  Notification: {
    borderRadius: '16px',
  },
  Tabs: {
    borderRadius: '10px',
  },
  Switch: {
    borderRadius: '12px',
  },
  Checkbox: {
    borderRadius: '6px',
  },
  Radio: {
    borderRadius: '10px',
  },
  Pagination: {
    borderRadius: '8px',
  },
  Breadcrumb: {
    borderRadius: '8px',
  },
  Dropdown: {
    borderRadius: '12px',
  },
  Popover: {
    borderRadius: '12px',
  },
  Tooltip: {
    borderRadius: '8px',
  },
  Upload: {
    borderRadius: '12px',
  },
  Progress: {
    borderRadius: '8px',
  },
  Avatar: {
    borderRadius: '50%',
  },
  Badge: {
    borderRadius: '8px',
  },
}
</script>

<style>
#app {
  height: 100%;
}
</style>
