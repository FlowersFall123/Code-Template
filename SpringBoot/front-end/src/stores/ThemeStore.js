import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    isDark: false,
    isSystem: true  // 新增系统偏好跟踪
  }),

  actions: {
    // 初始化主题（兼容本地存储和系统偏好）
    initTheme() {
      const savedTheme = localStorage.getItem('theme')

      if (savedTheme) {
        this.isDark = savedTheme === 'dark'
        this.isSystem = false
      } else {
        this.followSystem()
      }

      this.applyTheme()
    },

    // 跟随系统偏好
    followSystem() {
      this.isDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      this.isSystem = true
      localStorage.removeItem('theme')  // 清除本地存储
    },

    // 切换主题
    toggleTheme() {
      this.isSystem = false
      this.isDark = !this.isDark
      this.applyTheme()
    },

    // 应用主题到DOM
    applyTheme() {
      const root = document.documentElement
      root.classList.toggle('dark', this.isDark)

      // 平滑过渡处理
      root.classList.add('theme-transition')
      setTimeout(() => {
        root.classList.remove('theme-transition')
      }, 300)

      // 持久化存储（仅当非系统模式时）
      if (!this.isSystem) {
        localStorage.setItem('theme', this.isDark ? 'dark' : 'light')
      }
    }
  },

  getters: {
    currentTheme: (state) =>
      state.isSystem ? 'system' : state.isDark ? 'dark' : 'light'
  }
})
