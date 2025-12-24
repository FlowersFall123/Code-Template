<script setup>
import { computed, reactive, ref } from 'vue'

import { message } from 'ant-design-vue';

import router from "@/router/index.js";
import { post } from "@/net/index.js";
import { userUserStore } from "@/stores/userStore.js";
const [messageApi, contextHolder] = message.useMessage();

// 添加表单模式状态
const isLoginMode = ref(true)

// 注册步骤状态
const registerStep = ref(1) // 1: 邮箱验证码, 2: 密码昵称

// 登录表单
const loginForm = reactive({
  email: '',
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

const entered = ref(false)
setTimeout(() => {
  entered.value = true
}, 30)

const shakeAnim = ref(false)
const shake = () => {
  shakeAnim.value = false
  requestAnimationFrame(() => {
    shakeAnim.value = true
  })
}

const canSendCode = computed(() => {
  return !codeLoading.value && countdown.value <= 0
})

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
    shake()
    return
  }
  codeLoading.value = true
  post(`api/auth/sendCode/${registerForm.email}`, {}, (message) => {
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
  }, (message) => {
    messageApi.warning(message)
    codeLoading.value = false
  })
}

// 验证验证码并进入下一步
const verifyCodeAndNext = async () => {
  if (!registerForm.verificationCode) {
    messageApi.error('请输入验证码')
    shake()
    return
  }
  const verifyData = {
    email: registerForm.email,
    code: registerForm.verificationCode
  }

  loading.value = true
  post("api/auth/verifyCode", verifyData, (message) => {
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
    shake()
    return
  }

  // 验证密码长度
  if (registerForm.password.length < 6) {
    messageApi.error('密码长度至少6位')
    shake()
    return
  }

  // 验证昵称
  if (!registerForm.nickname.trim()) {
    messageApi.error('请输入昵称')
    shake()
    return
  }

  loading.value = true
  post("api/auth/register",{
    email: registerForm.email,
    password: registerForm.password,
    username: registerForm.nickname,
  },async (message) => {
    messageApi.success(message)
    // 注册接口不返回 token，这里注册成功后自动登录
    await new Promise((resolve) => {
      post("api/auth/login", {
        email: registerForm.email,
        password: registerForm.password,
      }, async (msg, data) => {
        messageApi.success('注册成功！')
        await localStorage.setItem('authToken', data)
        await userStore.login(data)
        await router.push("/")
        resolve()
      }, () => {
        resolve()
      })
    })
    loading.value = false
  }, () => {
    loading.value = false
  })
}

// 处理登录提交
const handleLoginSubmit = async () => {
  if (!loginForm.email || !loginForm.password) {
    messageApi.error('请输入邮箱和密码')
    shake()
    return
  }
  loading.value = true
  post("api/auth/login",{
    email: loginForm.email,
    password: loginForm.password,
  },async (message, data) => {
    messageApi.success(message)
    await localStorage.setItem('authToken', data)
    await userStore.login(data)
    await router.push("/");
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const userStore = userUserStore()
// const GetUserInfo = () => {
//   return new Promise((resolve) => {
//     get('api/user/getUserById', {}, (message, data) => {
//       userStore.login(data)
//       resolve()
//     }, () => {
//       resolve()
//     })
//   })
// }
</script>

<template>
  <contextHolder/>
  <!-- 透明背景容器 -->
  <div class="min-h-screen relative bg-white overflow-hidden">
    <div class="login-bg" aria-hidden="true">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
      <div class="grid"></div>
    </div>
    <!-- 主登录容器 -->
    <div class="relative flex items-center justify-center min-h-screen py-8 px-4 sm:px-6 lg:px-8">
      <!-- 左右分栏布局 -->
      <div class="w-full max-w-3xl mx-auto">
        <!-- 整体边框容器 -->
        <div
          class="auth-card p-5 sm:p-6"
          :class="[entered ? 'enter' : 'pre-enter', shakeAnim ? 'shake' : '']"
        >
          <div class="flex flex-col lg:flex-row gap-5 sm:gap-6 items-center">

            <!-- 左侧介绍区域 -->
            <div class="hidden lg:block space-y-6 flex-1">
              <!-- 主标题 -->
              <div class="space-y-4">
                <h1 class="text-3xl font-bold text-slate-900 login-title">
                  欢迎来到
                  <br>
                  <span class="text-4xl">ProPath</span>
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
            <div class="hidden lg:block w-px h-80 subtle-divider"></div>

            <!-- 右侧表单区域 -->
            <div class="w-full lg:flex-1">
              <!-- 登录表单 -->
              <Transition name="fade-slide" mode="out-in">
              <div v-if="isLoginMode" class="space-y-6" key="login">
                <!-- 标题区域 -->
                <div class="text-center space-y-3">
                  <div class="relative">
                    <h2 class="text-3xl font-bold text-slate-900 login-title">
                      欢迎登录
                    </h2>
                    <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-12 h-0.5 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-full"></div>
                  </div>
                  <p class="text-sm text-slate-700">
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
                      <label for="login-email" class="block text-sm font-medium text-slate-700 mb-1 transition-colors duration-200 group-focus-within:text-indigo-600">
                        邮箱
                      </label>
                      <div class="relative">
                        <input
                          id="login-email"
                          v-model="loginForm.email"
                          name="login-email"
                          type="email"
                          autocomplete="email"
                          required
                          class="neo-input w-full px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入邮箱地址"
                        />
                        <div class="input-glow"></div>
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
                          class="neo-input w-full px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入密码"
                        />
                        <div class="input-glow"></div>
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
                      class="neo-btn neo-btn-primary w-full py-2.5 px-4 font-semibold rounded-lg transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-indigo-500/40 focus:ring-offset-2 focus:ring-offset-white disabled:opacity-60 disabled:cursor-not-allowed text-base shadow-xl"
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
              <div v-else class="space-y-6" key="register">
                <!-- 标题区域 -->
                <div class="text-center space-y-3">
                  <div class="relative">
                    <h2 class="text-3xl font-bold text-slate-900 login-title">
                      {{ registerStep === 1 ? '验证邮箱' : '完善信息' }}
                    </h2>
                    <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-12 h-0.5 bg-gradient-to-r from-purple-500 to-blue-500 rounded-full"></div>
                  </div>



                  <p class="text-sm text-slate-700">
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
                          class="neo-input w-full px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入邮箱地址"
                        />
                        <div class="input-glow"></div>
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
                          class="neo-input flex-1 px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入验证码"
                        />
                        <button
                          @click="sendVerificationCode"
                          :disabled="!canSendCode"
                          class="neo-btn neo-btn-code px-4 py-2.5 text-sm font-semibold rounded-lg transition-all duration-300 disabled:opacity-60 disabled:cursor-not-allowed shadow-sm"
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
                      class="neo-btn neo-btn-primary w-full py-2.5 px-4 font-semibold rounded-lg transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-indigo-500/40 focus:ring-offset-2 focus:ring-offset-white disabled:opacity-60 disabled:cursor-not-allowed text-base shadow-sm"
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
                          class="neo-input w-full px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入昵称"
                        />
                        <div class="input-glow"></div>
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
                          class="neo-input w-full px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请输入密码（至少6位）"
                        />
                        <div class="input-glow"></div>
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
                          class="neo-input w-full px-3 py-2.5 bg-white/80 border border-white/40 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-indigo-500/35 focus:border-indigo-500/55 transition-all duration-300 shadow-sm hover:shadow-md text-sm"
                          placeholder="请再次输入密码"
                        />
                        <div class="input-glow"></div>
                      </div>
                    </div>
                  </div>

                  <!-- 注册按钮 -->
                  <div class="pt-1">
                    <button
                      @click="handleRegisterSubmit"
                      :disabled="loading"
                      class="neo-btn neo-btn-primary w-full py-2.5 px-4 font-semibold rounded-lg transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-indigo-500/40 focus:ring-offset-2 focus:ring-offset-white disabled:opacity-60 disabled:cursor-not-allowed text-base shadow-sm"
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
              </Transition>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<!-- 添加自定义样式 -->
<style scoped>
.login-title {
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.6), 0 8px 28px rgba(15, 23, 42, 0.25);
}

.login-bg {
  position: absolute;
  inset: -20vh -20vw;
  pointer-events: none;
  background: #fff;
  filter: saturate(1);
}

.login-bg::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(99, 102, 241, 0.08), rgba(168, 85, 247, 0.06), rgba(59, 130, 246, 0.08));
  opacity: 0.8;
  filter: blur(30px);
  transform: translate3d(0, 0, 0);
  animation: bg-pan 16s ease-in-out infinite;
}

.auth-card {
  position: relative;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.74), rgba(255, 255, 255, 0.62));
  border: 1px solid rgba(148, 163, 184, 0.14);
  box-shadow:
    0 8px 22px rgba(15, 23, 42, 0.07),
    0 1px 0 rgba(255, 255, 255, 0.62) inset;
  backdrop-filter: blur(22px) saturate(1.15);
  transition: transform 240ms cubic-bezier(0.16, 1, 0.3, 1), box-shadow 240ms cubic-bezier(0.16, 1, 0.3, 1);
}

.auth-card::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(
    135deg,
    rgba(99, 102, 241, 0.22),
    rgba(168, 85, 247, 0.18),
    rgba(59, 130, 246, 0.18)
  );

  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
  opacity: 0.30;
}

.auth-card::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    radial-gradient(820px 280px at 18% 0%, rgba(99, 102, 241, 0.14), transparent 56%),
    radial-gradient(700px 260px at 82% 18%, rgba(168, 85, 247, 0.11), transparent 54%);
  pointer-events: none;
  opacity: 0.35;
  mix-blend-mode: normal;
}

.auth-card:hover {
  box-shadow:
    0 10px 26px rgba(15, 23, 42, 0.08),
    0 1px 0 rgba(255, 255, 255, 0.65) inset;
}

.subtle-divider {
  background: linear-gradient(
    to bottom,
    rgba(15, 23, 42, 0),
    rgba(148, 163, 184, 0.55),
    rgba(15, 23, 42, 0)
  );
  opacity: 0.7;
  filter: blur(0.2px);
}

.grid {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 1px 1px, rgba(15, 23, 42, 0.06) 1px, transparent 0);
  background-size: 26px 26px;
  background-position: 0 0;
  mask-image: radial-gradient(circle at 50% 40%, rgba(0, 0, 0, 0.7), transparent 68%);
  opacity: 0.7;
  animation: grid-drift 28s linear infinite;
  will-change: background-position;
}

.orb {
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 9999px;
  filter: blur(50px);
  opacity: 0.22;
  mix-blend-mode: normal;
  animation: floaty 10s ease-in-out infinite;
}

.orb-1 {
  top: 12vh;
  left: 10vw;
  background: radial-gradient(circle at 30% 30%, rgba(99, 102, 241, 0.42), rgba(168, 85, 247, 0.0) 60%);
}

.orb-2 {
  top: 45vh;
  right: 8vw;
  width: 620px;
  height: 620px;
  background: radial-gradient(circle at 45% 55%, rgba(59, 130, 246, 0.38), rgba(236, 72, 153, 0.0) 62%);
  animation-duration: 13s;
  animation-direction: reverse;
}

.orb-3 {
  bottom: 8vh;
  left: 35vw;
  width: 480px;
  height: 480px;
  background: radial-gradient(circle at 50% 50%, rgba(168, 85, 247, 0.32), rgba(14, 165, 233, 0.0) 60%);
  animation-duration: 16s;
}

.pre-enter {
  transform: translateY(10px) scale(0.98);
  opacity: 0;
}

.enter {
  transform: translateY(0) scale(1);
  opacity: 1;
  transition: transform 700ms cubic-bezier(0.16, 1, 0.3, 1), opacity 700ms cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 260ms ease, transform 320ms cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-enter-to,
.fade-slide-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.neo-input {
  background-color: rgba(255, 255, 255, 0.58) !important;
  border-color: rgba(148, 163, 184, 0.26) !important;
  backdrop-filter: blur(12px) saturate(1.15);
}

.neo-input:focus {
  outline: none;
  border-color: rgba(99, 102, 241, 0.34) !important;
  --tw-ring-offset-shadow: 0 0 #0000 !important;
  --tw-ring-shadow: 0 0 #0000 !important;
  box-shadow: 0 0 0 1px rgba(99, 102, 241, 0.22);
}

.neo-input:not(:placeholder-shown) {
  background-color: rgba(255, 255, 255, 0.54) !important;
}

.neo-input:-webkit-autofill,
.neo-input:-webkit-autofill:hover,
.neo-input:-webkit-autofill:focus {
  -webkit-text-fill-color: rgba(15, 23, 42, 0.92);
  transition: background-color 9999s ease-out 0s;
  box-shadow:
    0 0 0 1000px rgba(255, 255, 255, 0.54) inset,
    0 0 0 1px rgba(99, 102, 241, 0.18);
}

.input-glow {
  position: absolute;
  inset: 0;
  border-radius: 10px;
  background: linear-gradient(90deg, rgba(99, 102, 241, 0.0), rgba(168, 85, 247, 0.22), rgba(59, 130, 246, 0.0));
  opacity: 0;
  transform: scale(0.98);
  transition: opacity 260ms ease, transform 260ms ease;
  pointer-events: none;
  z-index: -1;
}

.group:focus-within .input-glow {
  opacity: 0.75;
  transform: scale(1);
}

.neo-btn {
  position: relative;
  overflow: hidden;
  transform: translateZ(0);
}

.neo-btn-primary {
  color: #ffffff;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.95), rgba(168, 85, 247, 0.92));
  border: 1px solid rgba(255, 255, 255, 0.16);
  box-shadow:
    0 14px 30px rgba(15, 23, 42, 0.12),
    0 1px 0 rgba(255, 255, 255, 0.16) inset;
}

.neo-btn-primary:hover {
  filter: brightness(1.04) saturate(1.03);
  box-shadow:
    0 18px 38px rgba(15, 23, 42, 0.14),
    0 1px 0 rgba(255, 255, 255, 0.18) inset;
}

.neo-btn-primary:active {
  filter: brightness(0.98) saturate(1.01);
  box-shadow:
    0 10px 22px rgba(15, 23, 42, 0.12),
    0 1px 0 rgba(255, 255, 255, 0.12) inset;
}

.neo-btn-secondary {
  color: rgba(15, 23, 42, 0.92);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.45);
}

.neo-btn-secondary:hover {
  background: rgba(255, 255, 255, 1);
}

.neo-btn-code {
  color: rgba(15, 23, 42, 0.88);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.74), rgba(248, 250, 252, 0.54));
  border: 1px solid rgba(99, 102, 241, 0.24);
  box-shadow:
    0 10px 24px rgba(15, 23, 42, 0.09),
    0 1px 0 rgba(255, 255, 255, 0.55) inset;
  backdrop-filter: blur(10px);
}

.neo-btn-code::after {
  content: "";
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: linear-gradient(90deg, rgba(99, 102, 241, 0.22), rgba(168, 85, 247, 0.18), rgba(59, 130, 246, 0.20));
  opacity: 0;
  filter: blur(10px);
  transition: opacity 220ms ease;
  z-index: -1;
}

.neo-btn-code:hover {
  transform: translate3d(0, -1px, 0);
  color: rgba(15, 23, 42, 0.92);
  border-color: rgba(99, 102, 241, 0.40);
}

.neo-btn-code:hover::after {
  opacity: 1;
}

.neo-btn-code:disabled {
  transform: none;
}

.neo-btn::before {
  content: "";
  position: absolute;
  inset: -2px;
  background: linear-gradient(90deg, rgba(99, 102, 241, 0.22), rgba(168, 85, 247, 0.20), rgba(59, 130, 246, 0.22));
  opacity: 0;
  filter: blur(8px);
  transition: opacity 260ms ease;
}

.neo-btn:hover::before {
  opacity: 1;
}

.neo-btn:active {
  transform: translateY(1px);
}

.shake {
  animation: shake 420ms ease;
}

@keyframes floaty {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -18px, 0) scale(1.03);
  }
}

@keyframes bg-pan {
  0%,
  100% {
    transform: translate3d(-1.5%, -1%, 0) scale(1.02);
    opacity: 0.75;
  }
  50% {
    transform: translate3d(1.5%, 1%, 0) scale(1.05);
    opacity: 0.9;
  }
}

@keyframes grid-drift {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 180px 120px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .orb {
    animation: none;
  }

  .login-bg::before {
    animation: none;
  }

  .grid {
    animation: none;
  }
}

@keyframes shake {
  0% {
    transform: translate3d(0, 0, 0);
  }
  20% {
    transform: translate3d(-6px, 0, 0);
  }
  40% {
    transform: translate3d(6px, 0, 0);
  }
  60% {
    transform: translate3d(-4px, 0, 0);
  }
  80% {
    transform: translate3d(4px, 0, 0);
  }
  100% {
    transform: translate3d(0, 0, 0);
  }
}

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

