

<script setup>
import {reactive, ref} from 'vue'

import { message,  } from 'ant-design-vue';

import router from "@/router/index.js";
import {get, post} from "@/net/index.js";
import {userUserStore} from "@/stores/userStore.js";
const [messageApi, contextHolder] = message.useMessage();

// 添加表单模式状态
const isLoginMode = ref(true)

// 注册步骤状态
const registerStep = ref(1) // 1: 邮箱验证码, 2: 密码昵称

// 登录表单
const loginForm = reactive({
  account: '',
  password: '',
  remember: false
})

// 注册表单
const registerForm = reactive({
  email: '',
  verificationCode: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const loading = ref(false)
const codeLoading = ref(false)
const countdown = ref(0)

// 步骤条配置
const stepItems = [
  {
    title: '验证邮箱',
    // description: '输入邮箱获取验证码',
  },
  {
    title: '完善信息',
    // description: '设置密码和昵称',
  },
]

// 切换到注册模式
const switchToRegister = () => {
  isLoginMode.value = false
  registerStep.value = 1
  // 重置注册表单
  Object.assign(registerForm, {
    email: '',
    verificationCode: '',
    password: '',
    confirmPassword: '',
    nickname: ''
  })
}

// 切换到登录模式
const switchToLogin = () => {
  isLoginMode.value = true
  registerStep.value = 1
}

// 发送验证码
const sendVerificationCode = async () => {
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.email)) {
    messageApi.error('请输入有效的邮箱地址')
    return
  }

  codeLoading.value = true
  post("api/auth/sendVerificationCode", {
    email: registerForm.email
  }, (message, data) => {
    messageApi.success(message)
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
    codeLoading.value = false
  }, () => {
    codeLoading.value = false
  })
}

// 验证验证码并进入下一步
const verifyCodeAndNext = async () => {
  if (!registerForm.verificationCode) {
    messageApi.error('请输入验证码')
    return
  }

  loading.value = true
  post("api/auth/verifyCode", {
    email: registerForm.email,
    code: registerForm.verificationCode
  }, (message, data) => {
    messageApi.success(message)
    registerStep.value = 2
    loading.value = false
  }, () => {
    loading.value = false
  })
}

// 处理注册提交
const handleRegisterSubmit = async () => {
  // 验证密码是否一致
  if (registerForm.password !== registerForm.confirmPassword) {
    messageApi.error('两次输入的密码不一致')
    return
  }

  // 验证密码长度
  if (registerForm.password.length < 6) {
    messageApi.error('密码长度至少6位')
    return
  }

  // 验证昵称
  if (!registerForm.nickname.trim()) {
    messageApi.error('请输入昵称')
    return
  }

  loading.value = true
  post("api/auth/register",{
    email: registerForm.email,
    verificationCode: registerForm.verificationCode,
    password: registerForm.password,
    nickname: registerForm.nickname,
  },async (message, data) => {
    messageApi.success(message)
    // 注册成功后自动登录
    await localStorage.setItem('authToken', data)
    await GetUserInfo();
    await router.push("/");
    loading.value = false
  }, () => {
    loading.value = false
  })
}

// 处理登录提交
const handleLoginSubmit = async () => {
  loading.value = true
  post("api/auth/login",{
    account: loginForm.account,
    password: loginForm.password,
  },async (message, data) => {
    messageApi.success(message)
    await localStorage.setItem('authToken', data)
    await GetUserInfo();
    await router.push("/");
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const userStore = userUserStore()
const GetUserInfo = ()=> {
  new Promise((resolve) => {
    get('api/user/getUserById', {},(message,data)=>{
      userStore.login(data)
    })
    resolve();
  })
}
</script>

<template>
  <contextHolder/>
  <!-- 透明背景容器 -->
  <div class="min-h-screen relative bg-transparent">
    <!-- 主登录容器 -->
    <div class="relative flex items-center justify-center min-h-screen py-8 px-4 sm:px-6 lg:px-8">
      <!-- 左右分栏布局 -->
      <div class="w-full max-w-4xl mx-auto">
        <!-- 整体边框容器 -->
        <div class="border border-white rounded-2xl backdrop-blur-sm bg-white/5 p-6 shadow-2xl">
          <div class="flex flex-col lg:flex-row gap-6 items-center">

            <!-- 左侧介绍区域 -->
            <div class="hidden lg:block space-y-6 flex-1">
              <!-- 主标题 -->
              <div class="space-y-4">
                <h1 class="text-3xl font-bold bg-gradient-to-r from-purple-600 via-blue-600 to-purple-600 bg-clip-text text-transparent">
                  欢迎来到
                  <br>
                  <span class="text-4xl">PrepGuide</span>
                </h1>
                <p class="text-lg text-gray-600 leading-relaxed">
                  您的智能学习伙伴，助您高效备考
                </p>
              </div>

              <!-- 特性介绍 -->
              <div class="space-y-4">
                <div class="flex items-center space-x-3">
                  <div class="flex-shrink-0 w-10 h-10 bg-gradient-to-r from-purple-500 to-blue-500 rounded-lg flex items-center justify-center">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                  </div>
                  <div >
                    <h3 class="text-base font-semibold text-gray-500 leading-tight">智能题库</h3>
                    <p class="text-sm text-gray-600 leading-tight">海量真题，精准练习</p>
                  </div>
                </div>

                <div class="flex items-center space-x-3">
                  <div class="flex-shrink-0 w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-500 rounded-lg flex items-center justify-center">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                    </svg>
                  </div>
                  <div >
                    <h3 class="text-base font-semibold text-gray-500 leading-tight">高效学习</h3>
                    <p class="text-sm text-gray-600 leading-tight">个性化学习计划</p>
                  </div>
                </div>

                <div class="flex items-center space-x-3">
                  <div class="flex-shrink-0 w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                    </svg>
                  </div>
                  <div >
                    <h3 class="text-base font-semibold text-gray-500 leading-tight">数据分析</h3>
                    <p class="text-sm text-gray-600 leading-tight">详细学习报告</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分割线 -->
            <div class="hidden lg:block w-px h-80 bg-gradient-to-b from-transparent via-white/30 to-transparent"></div>

            <!-- 右侧表单区域 -->
            <div class="w-full lg:flex-1">
              <!-- 登录表单 -->
              <div v-if="isLoginMode" class="space-y-6">
                <!-- 标题区域 -->
                <div class="text-center space-y-3">
                  <div class="relative">
                    <h2 class="text-3xl font-bold bg-gradient-to-r text-blue-500 from-slate-800 via-slate-700 to-slate-800 bg-clip-text text-transparent drop-shadow-lg">
                      欢迎登录
                    </h2>
                    <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-12 h-0.5 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-full"></div>
                  </div>
                  <p class="text-sm text-slate-600">
                    或
                    <a @click="switchToRegister" class="font-medium text-indigo-600 hover:text-indigo-500 cursor-pointer transition-colors duration-200">
                      注册新账户
                    </a>
                  </p>
                </div>

                <!-- 表单区域 -->
                <div class="space-y-5">
                  <!-- 输入框组 -->
                  <div class="space-y-4">
                    <div class="group">
                      <label for="email" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        账号
                      </label>
                      <div class="relative">
                        <input
                          id="email"
                          v-model="loginForm.account"
                          name="email"
                          type="text"
                          autocomplete="email"
                          required
                          class="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入邮箱地址"
                        />
                        <div class="absolute inset-0 rounded-lg bg-gradient-to-r from-indigo-500/5 to-purple-500/5 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 -z-10"></div>
                      </div>
                    </div>

                    <div class="group">
                      <label for="password" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        密码
                      </label>
                      <div class="relative">
                        <input
                          id="password"
                          v-model="loginForm.password"
                          name="password"
                          type="password"
                          autocomplete="current-password"
                          required
                          class="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入密码"
                        />
                        <div class="absolute inset-0 rounded-lg bg-gradient-to-r from-indigo-500/5 to-purple-500/5 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 -z-10"></div>
                      </div>
                    </div>
                  </div>

                  <!-- 选项区域 -->
                  <div class="flex items-center justify-between">
                    <div class="flex items-center group">
                      <div class="relative">
                        <input
                          id="remember-me"
                          v-model="loginForm.remember"
                          name="remember-me"
                          type="checkbox"
                          class="sr-only"
                        />
                        <label for="remember-me" class="flex items-center cursor-pointer">
                          <div class="w-4 h-4 border-2 border-slate-300 rounded transition-all duration-200 group-hover:border-indigo-400 relative">
                            <div v-if="loginForm.remember" class="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded flex items-center justify-center">
                              <svg class="w-2.5 h-2.5 text-white" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                              </svg>
                            </div>
                          </div>
                          <span class="ml-2 text-xs text-slate-600 group-hover:text-slate-800 transition-colors duration-200">
                            记住我
                          </span>
                        </label>
                      </div>
                    </div>

                    <a href="#" class="text-xs font-medium text-indigo-600 hover:text-indigo-500 transition-colors duration-200">
                      忘记密码?
                    </a>
                  </div>

                  <!-- 登录按钮 -->
                  <div class="pt-1">
                    <button
                      @click="handleLoginSubmit"
                      :disabled="loading"
                      class="w-full py-2.5 px-4 bg-transparent hover:border-white text-white font-medium rounded-lg transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-white/50 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed text-sm border border-white/60 shadow-xl"
                    >
                      <span class="flex items-center justify-center">
                        <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        {{ loading ? '登录中...' : '登录' }}
                      </span>
                    </button>
                  </div>
                </div>
              </div>

              <!-- 注册表单 -->
              <div v-else class="space-y-6">
                <!-- 标题区域 -->
                <div class="text-center space-y-3">
                  <div class="relative">
                    <h2 class="text-3xl font-bold bg-gradient-to-r from-purple-600 via-blue-600 to-purple-600 bg-clip-text text-transparent drop-shadow-lg">
                      {{ registerStep === 1 ? '验证邮箱' : '完善信息' }}
                    </h2>
                    <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-12 h-0.5 bg-gradient-to-r from-purple-500 to-blue-500 rounded-full"></div>
                  </div>



                  <p class="text-sm text-slate-600">
                    {{ registerStep === 1 ? '已有账户？' : '' }}
                    <a @click="switchToLogin" class="font-medium text-purple-600 hover:text-purple-500 cursor-pointer transition-colors duration-200">
                      {{ registerStep === 1 ? '立即登录' : '返回登录' }}
                    </a>
                  </p>

                  <!-- 步骤条 -->
                  <div class="flex justify-center">
                    <a-steps
                      progress-dot
                      :current="registerStep - 1"
                      :items="stepItems"
                      class="custom-steps"
                    ></a-steps>
                  </div>
                </div>

                <!-- 第一步：邮箱验证码 -->
                <div v-if="registerStep === 1" class="space-y-5">
                  <div class="space-y-4">
                    <div class="group">
                      <label for="register-email" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        邮箱地址
                      </label>
                      <div class="relative">
                        <input
                          id="register-email"
                          v-model="registerForm.email"
                          name="register-email"
                          type="email"
                          autocomplete="email"
                          required
                          class="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入邮箱地址"
                        />
                        <div class="absolute inset-0 rounded-lg bg-gradient-to-r from-indigo-500/5 to-purple-500/5 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 -z-10"></div>
                      </div>
                    </div>

                    <div class="group">
                      <label for="verification-code" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        验证码
                      </label>
                      <div class="relative flex space-x-2">
                        <input
                          id="verification-code"
                          v-model="registerForm.verificationCode"
                          name="verification-code"
                          type="text"
                          required
                          class="flex-1 px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入验证码"
                        />
                        <button
                          @click="sendVerificationCode"
                          :disabled="codeLoading || countdown > 0"
                          class="px-4 py-2.5 bg-transparent hover:border-white text-white text-sm font-medium rounded-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed border border-white/60 shadow-sm"
                        >
                          {{ codeLoading ? '发送中...' : countdown > 0 ? `${countdown}s` : '获取验证码' }}
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- 下一步按钮 -->
                  <div class="pt-1">
                    <button
                      @click="verifyCodeAndNext"
                      :disabled="loading"
                      class="w-full py-2.5 px-4 bg-transparent hover:border-white text-white font-medium rounded-lg transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-white/50 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed text-sm border border-white/60 shadow-sm"
                    >
                      <span class="flex items-center justify-center">
                        <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        {{ loading ? '验证中...' : '下一步' }}
                      </span>
                    </button>
                  </div>
                </div>

                <!-- 第二步：密码和昵称 -->
                <div v-else class="space-y-5">
                  <div class="space-y-4">
                    <div class="group">
                      <label for="register-nickname" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        昵称
                      </label>
                      <div class="relative">
                        <input
                          id="register-nickname"
                          v-model="registerForm.nickname"
                          name="register-nickname"
                          type="text"
                          autocomplete="nickname"
                          required
                          class="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入昵称"
                        />
                        <div class="absolute inset-0 rounded-lg bg-gradient-to-r from-indigo-500/5 to-purple-500/5 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 -z-10"></div>
                      </div>
                    </div>

                    <div class="group">
                      <label for="register-password" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        密码
                      </label>
                      <div class="relative">
                        <input
                          id="register-password"
                          v-model="registerForm.password"
                          name="register-password"
                          type="password"
                          autocomplete="new-password"
                          required
                          class="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入密码（至少6位）"
                        />
                        <div class="absolute inset-0 rounded-lg bg-gradient-to-r from-indigo-500/5 to-purple-500/5 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 -z-10"></div>
                      </div>
                    </div>

                    <div class="group">
                      <label for="register-confirm-password" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        确认密码
                      </label>
                      <div class="relative">
                        <input
                          id="register-confirm-password"
                          v-model="registerForm.confirmPassword"
                          name="register-confirm-password"
                          type="password"
                          autocomplete="new-password"
                          required
                          class="w-full px-3 py-2.5 bg-white border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请再次输入密码"
                        />
                        <div class="absolute inset-0 rounded-lg bg-gradient-to-r from-indigo-500/5 to-purple-500/5 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 -z-10"></div>
                      </div>
                    </div>
                  </div>

                  <!-- 注册按钮 -->
                  <div class="pt-1">
                    <button
                      @click="handleRegisterSubmit"
                      :disabled="loading"
                      class="w-full py-2.5 px-4 bg-transparent hover:border-white text-white font-medium rounded-lg transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-white/50 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed text-sm border border-white/60 shadow-sm"
                    >
                      <span class="flex items-center justify-center">
                        <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        {{ loading ? '注册中...' : '完成注册' }}
                      </span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<!-- 添加自定义样式 -->
<style scoped>
:deep(.custom-steps .ant-steps-item-title) {
  color: #6b7280 !important;
  font-size: 11px !important;
  margin-top: 4px !important;
}

:deep(.custom-steps .ant-steps-item-process .ant-steps-item-title) {
  color: #8b5cf6 !important;
  font-weight: 600 !important;
}

:deep(.custom-steps .ant-steps-item-finish .ant-steps-item-title) {
  color: #8b5cf6 !important;
}

:deep(.custom-steps .ant-steps-item-description) {
  color: #9ca3af !important;
  font-size: 10px !important;
  margin-top: 2px !important;
}

:deep(.custom-steps .ant-steps-item-process .ant-steps-item-description) {
  color: #8b5cf6 !important;
}

:deep(.custom-steps .ant-steps-item-finish .ant-steps-item-description) {
  color: #8b5cf6 !important;
}

:deep(.custom-steps .ant-steps-item-icon) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  width: 8px !important;
  height: 8px !important;
}

:deep(.custom-steps .ant-steps-item-process .ant-steps-item-icon) {
  background-color: #8b5cf6 !important;
  border-color: #8b5cf6 !important;
}

:deep(.custom-steps .ant-steps-item-finish .ant-steps-item-icon) {
  background-color: #8b5cf6 !important;
  border-color: #8b5cf6 !important;
}

:deep(.custom-steps .ant-steps-item-tail) {
  background-color: rgba(255, 255, 255, 0.2) !important;
  top: 4px !important;
  height: 1px !important;
}

:deep(.custom-steps .ant-steps-item-finish .ant-steps-item-tail) {
  background-color: #8b5cf6 !important;
}

:deep(.custom-steps .ant-steps-item-content) {
  margin-top: 8px !important;
}
</style>

