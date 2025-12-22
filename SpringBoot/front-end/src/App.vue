<script setup>
import { RouterView } from 'vue-router'
import {onMounted} from "vue";
import {useThemeStore} from "@/stores/ThemeStore.js";
const themeStore = useThemeStore()

onMounted(() => {
  themeStore.initTheme()
  // 监听系统偏好变化
  window.matchMedia('(prefers-color-scheme: dark)')
    .addEventListener('change', e => {
      if (themeStore.isSystem) {
        themeStore.isDark = e.matches
        themeStore.applyTheme()
      }
    })
})
</script>

<template>
  <RouterView />
</template>
