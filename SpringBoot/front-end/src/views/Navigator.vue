<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {userUserStore} from "@/stores/userStore.js";

const router = useRouter()
const route = useRoute()
const userStore=userUserStore();

// 基础导航数据：支持二级（仅需设置 icon 名称）
const baseNav = [
  {
    label: '首页',
    icon: 'home',
    route: '/',
    show: true // 始终显示
  },{
    label: '任务',
    icon: 'task',
    show: true,
    children: [
      {label: '制作', route: '/task/make', icon: 'make', show: true},
      {label: '管理',route: '/task/manage',icon: 'manage',show: true},
    ]
  },
  {
    label: 'AI',
    icon: 'AI',
    show: true, // 始终显示
    children: [
      {label: 'AI对话', route: '/AI/chat', icon: 'chat', show: true},
      {label: 'AI助手', route: '/AI/assistant', icon: 'chart', show: false},
      {label: 'AI生成', route: '/AI/generate', icon: 'doc', show: false}, // 只有管理员可见
    ],
  },
  {
    label: '评估',
    icon: 'evaluate',
    show: true,
    children: [
      {label: '自测', route: '/evaluate/check', icon: 'check', show: true},
      {label: '自述', route: '/evaluate/Readme', icon: 'Readme', show: true},
    ],
  },
  {
    label: '协作',
    icon: 'collaboration',
    show: true,
    children: [
      {label: '管理协作任务', route: '/collaboration/ManageTask', icon: 'ManageCollaborationTask', show: true},
      {label: '在线交流', route: '/collaboration/communication', icon: 'communication', show: true},
      {label: '创建', route: '/collaboration/CreateTask', icon: 'createCollaborationTask', show: true},
      {label: "记录",route: "/collaboration/record",icon: 'record', show: true}
    ],
  },
  {
    label: '人员',
    icon: 'personnel',
    show: true,
    children: [
      {label: '个人空间', route: '/personnel/personal', icon: 'personal', show: true},
      {label: '实验室成员', route: '/personnel/member', icon: 'member', show: true}
    ],
  },
  {
    label: '消息中心',
    icon: 'messages',
    show: true,
    children: [
      {label: '收件箱', route: '/messages/inbox', icon: 'inbox', show: true},
      {label: '发邮件', route: '/messages/communication', icon: 'communication', show: true},
      // {label: '通知', route: '/messages/notification', icon: 'notification', show: true}
    ],
  },{
    label: '退出',
    icon: 'output',
    show: true,
  },
]

function goOut(){
  userStore.logout();
  router.push('/auth');
}

// 过滤显示的导航项 - 只显示 show 为 true 的项
const filteredRootNav = computed(() => {
  return baseNav.filter(item => {
    // 检查主菜单项是否显示
    if (item.show !== true) {
      return false
    }

    // 如果有子菜单，过滤子菜单
    if (item.children) {
      item.children = item.children.filter(child => child.show === true)
      // 如果子菜单全部被过滤掉，则不显示主菜单
      return item.children.length > 0
    }
    return true
  })
})

// 简单：名称 -> 内联 SVG 字符串（统一 24x24 视口，使用 currentColor）
const icons = {
  createCollaborationTask:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 9.4c0-2.24 0-3.36.436-4.216a4 4 0 0 1 1.748-1.748C6.04 3 7.16 3 9.4 3h5.2c2.24 0 3.36 0 4.216.436a4 4 0 0 1 1.748 1.748C21 6.04 21 7.16 21 9.4v5.2c0 2.24 0 3.36-.436 4.216a4 4 0 0 1-1.748 1.748C17.96 21 16.84 21 14.6 21H9.4c-2.24 0-3.36 0-4.216-.436a4 4 0 0 1-1.748-1.748C3 17.96 3 16.84 3 14.6zM15.5 12H12m0 0H8.5m3.5 0V8.5m0 3.5v3.5"/>
</svg>`,
  chat:`<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48">
<path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" d="M25.875 3.944L29.39 17.23a1.94 1.94 0 0 0 1.38 1.379l13.287 3.515c1.924.51 1.924 3.24 0 3.75L30.769 29.39a1.94 1.94 0 0 0-1.379 1.38l-3.515 13.287c-.51 1.924-3.24 1.924-3.75 0L18.61 30.769a1.94 1.94 0 0 0-1.38-1.379L3.944 25.875c-1.924-.51-1.924-3.24 0-3.75l13.288-3.515a1.94 1.94 0 0 0 1.379-1.38l3.515-13.287c.51-1.924 3.24-1.924 3.75 0"/>
</svg>`,
  make:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
<path fill="currentColor" d="M5.808 20q-.348 0-.578-.23T5 19.192v-1.02q0-.324.13-.629t.349-.522L18.214 4.292q.165-.165.348-.228T18.926 4q.189 0 .378.064q.19.063.33.228l1.073 1.074q.165.14.229.33q.063.189.063.377q0 .183-.063.366t-.23.348L7.98 19.52q-.218.218-.522.348T6.829 20zM6 19h1.073L17.629 8.45l-.527-.552l-.552-.527L6 17.927zM20 6.079L18.921 5zm-2.898 1.819l-.552-.527l1.079 1.079zM14.269 20q1.658 0 3.079-.79T18.769 17q0-.762-.477-1.388t-1.377-1.068q-.177-.096-.375-.038t-.3.223t-.044.372t.235.303q.638.313.988.721t.35.875q0 .844-1.037 1.422q-1.038.578-2.463.578q-.213 0-.357.143t-.143.357t.143.357t.357.143M10 6.5q0 .523-.553.945T7.262 8.592q-1.866.817-2.564 1.443T4 11.5q0 .592.339 1.025t.813.73q.171.122.36.075t.31-.218t.076-.37t-.215-.319q-.349-.221-.516-.452T5 11.5q0-.454.556-.889q.555-.434 2.102-1.098q1.969-.834 2.656-1.504Q11 7.341 11 6.5q0-1.086-.975-1.793T7.5 4q-.933 0-1.772.342q-.84.343-1.219.84q-.14.172-.119.37t.193.333q.17.12.36.1q.188-.021.328-.162q.408-.408.967-.615Q6.798 5 7.5 5q1.179 0 1.84.444Q10 5.89 10 6.5"/>
</svg>
  `,
  task:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48">
<g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="4"><rect width="40" height="30" x="4" y="10" stroke-linejoin="round" rx="2"/><path d="M14 6v8m11 9H14m20 8H14M34 6v8"/></g>
</svg>
  `,
  manage:`
 <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
 <path fill="currentColor" d="M3 17.962v-1h9.558v1zm0-4.789v-1h4.462v1zm0-4.788v-1h4.462v1zM20.292 18l-3.888-3.83q-.543.405-1.178.627q-.636.222-1.303.222q-1.684 0-2.87-1.164q-1.188-1.164-1.188-2.845t1.188-2.845Q12.242 7 13.928 7t2.869 1.165T17.981 11q0 .667-.222 1.303q-.223.636-.648 1.159L21 17.292zm-6.369-3.98q1.274 0 2.166-.876q.892-.875.892-2.135t-.892-2.134T13.923 8t-2.166.875t-.892 2.135t.892 2.134t2.166.875"/>
 </svg>
  `,
  moon:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill-opacity="0" d="M15.22 6.03l2.53-1.94L14.56 4L13.5 1l-1.06 3l-3.19.09l2.53 1.94l-.91 3.06l2.63-1.81l2.63 1.81z" fill="currentColor"><animate id="IconifyId1989193d75be32dea38" fill="freeze" attributeName="fill-opacity" begin="0.6s;IconifyId1989193d75be32dea38.begin+6s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+2.2s" dur="0.4s" values="1;0"/></path><path fill-opacity="0" d="M13.61 5.25L15.25 4l-2.06-.05L12.5 2l-.69 1.95L9.75 4l1.64 1.25l-.59 1.98l1.7-1.17l1.7 1.17z" fill="currentColor"><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+3s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+5.2s" dur="0.4s" values="1;0"/></path><path fill-opacity="0" d="M19.61 12.25L21.25 11l-2.06-.05L18.5 9l-.69 1.95l-2.06.05l1.64 1.25l-.59 1.98l1.7-1.17l1.7 1.17z" fill="currentColor"><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+0.4s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+2.8s" dur="0.4s" values="1;0"/></path><path fill-opacity="0" d="M20.828 9.731l1.876-1.439l-2.366-.067L19.552 6l-.786 2.225l-2.366.067l1.876 1.439L17.601 12l1.951-1.342L21.503 12z" fill="currentColor"><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+3.4s" dur="0.4s" values="0;1"/><animate fill="freeze" attributeName="fill-opacity" begin="IconifyId1989193d75be32dea38.begin+5.6s" dur="0.4s" values="1;0"/></path><g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><g><path stroke-dasharray="2" stroke-dashoffset="4" d="M12 21v1M21 12h1M12 3v-1M3 12h-1"><animate fill="freeze" attributeName="stroke-dashoffset" dur="0.2s" values="4;2"/></path><path stroke-dasharray="2" stroke-dashoffset="4" d="M18.5 18.5l0.5 0.5M18.5 5.5l0.5 -0.5M5.5 5.5l-0.5 -0.5M5.5 18.5l-0.5 0.5"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.2s" dur="0.2s" values="4;2"/></path><set fill="freeze" attributeName="opacity" begin="0.5s" to="0"/></g><path d="M7 6 C7 12.08 11.92 17 18 17 C18.53 17 19.05 16.96 19.56 16.89 C17.95 19.36 15.17 21 12 21 C7.03 21 3 16.97 3 12 C3 8.83 4.64 6.05 7.11 4.44 C7.04 4.95 7 5.47 7 6 Z" opacity="0"><set fill="freeze" attributeName="opacity" begin="0.5s" to="1"/></path></g><mask id="IconifyId1989193d75be32dea39"><circle cx="12" cy="12" r="12" fill="#fff"/><circle cx="12" cy="12" r="4"><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="4;8"/></circle><circle cx="22" cy="2" r="3" fill="#fff"><animate fill="freeze" attributeName="cx" begin="0.1s" dur="0.4s" values="22;18"/><animate fill="freeze" attributeName="cy" begin="0.1s" dur="0.4s" values="2;6"/><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="3;12"/></circle><circle cx="22" cy="2" r="1"><animate fill="freeze" attributeName="cx" begin="0.1s" dur="0.4s" values="22;18"/><animate fill="freeze" attributeName="cy" begin="0.1s" dur="0.4s" values="2;6"/><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="1;10"/></circle></mask><circle cx="12" cy="12" r="6" mask="url(#IconifyId1989193d75be32dea39)" fill="currentColor"><animate fill="freeze" attributeName="r" begin="0.1s" dur="0.4s" values="6;10"/><set fill="freeze" attributeName="opacity" begin="0.5s" to="0"/></circle>
  </svg>
  `,
  son:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path stroke-dasharray="2" stroke-dashoffset="2" d="M12 19v1M19 12h1M12 5v-1M5 12h-1"><animate fill="freeze" attributeName="d" begin="1.2s" dur="0.2s" values="M12 19v1M19 12h1M12 5v-1M5 12h-1;M12 21v1M21 12h1M12 3v-1M3 12h-1"/><animate fill="freeze" attributeName="stroke-dashoffset" begin="1.2s" dur="0.2s" values="2;0"/></path><path stroke-dasharray="2" stroke-dashoffset="2" d="M17 17l0.5 0.5M17 7l0.5 -0.5M7 7l-0.5 -0.5M7 17l-0.5 0.5"><animate fill="freeze" attributeName="d" begin="1.4s" dur="0.2s" values="M17 17l0.5 0.5M17 7l0.5 -0.5M7 7l-0.5 -0.5M7 17l-0.5 0.5;M18.5 18.5l0.5 0.5M18.5 5.5l0.5 -0.5M5.5 5.5l-0.5 -0.5M5.5 18.5l-0.5 0.5"/><animate fill="freeze" attributeName="stroke-dashoffset" begin="1.4s" dur="0.2s" values="2;0"/></path><animateTransform attributeName="transform" dur="30s" repeatCount="indefinite" type="rotate" values="0 12 12;360 12 12"/></g><g fill="currentColor"><path d="M15.22 6.03L17.75 4.09L14.56 4L13.5 1L12.44 4L9.25 4.09L11.78 6.03L10.87 9.09L13.5 7.28L16.13 9.09L15.22 6.03Z"><animate fill="freeze" attributeName="fill-opacity" dur="0.4s" values="1;0"/></path><path d="M19.61 12.25L21.25 11L19.19 10.95L18.5 9L17.81 10.95L15.75 11L17.39 12.25L16.8 14.23L18.5 13.06L20.2 14.23L19.61 12.25Z"><animate fill="freeze" attributeName="fill-opacity" begin="0.2s" dur="0.4s" values="1;0"/></path></g><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 6 C7 12.08 11.92 17 18 17 C18.53 17 19.05 16.96 19.56 16.89 C17.95 19.36 15.17 21 12 21 C7.03 21 3 16.97 3 12 C3 8.83 4.64 6.05 7.11 4.44 C7.04 4.95 7 5.47 7 6 Z"><set fill="freeze" attributeName="opacity" begin="0.6s" to="0"/></path><mask id="IconifyId1989193d75be32dea35"><circle cx="12" cy="12" r="12" fill="#fff"/><circle cx="12" cy="12" r="8"><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="8;4"/></circle><circle cx="18" cy="6" r="12" fill="#fff"><animate fill="freeze" attributeName="cx" begin="0.6s" dur="0.4s" values="18;22"/><animate fill="freeze" attributeName="cy" begin="0.6s" dur="0.4s" values="6;2"/><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="12;3"/></circle><circle cx="18" cy="6" r="10"><animate fill="freeze" attributeName="cx" begin="0.6s" dur="0.4s" values="18;22"/><animate fill="freeze" attributeName="cy" begin="0.6s" dur="0.4s" values="6;2"/><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="10;1"/></circle></mask><circle cx="12" cy="12" r="10" mask="url(#IconifyId1989193d75be32dea35)" opacity="0" fill="currentColor"><animate fill="freeze" attributeName="r" begin="0.6s" dur="0.4s" values="10;6"/><set fill="freeze" attributeName="opacity" begin="0.6s" to="1"/></circle>
  </svg>
  `,
  AI: `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
      <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5">
        <path d="M4 16.5a3 3 0 0 0 3 3a2.5 2.5 0 0 0 5 0a2.5 2.5 0 1 0 5 0a3 3 0 0 0 2.567-4.553a3.001 3.001 0 0 0 0-5.893A3 3 0 0 0 17 4.5a2.5 2.5 0 1 0-5 0a2.5 2.5 0 0 0-5 0a3 3 0 0 0-2.567 4.553a3.001 3.001 0 0 0 0 5.893A3 3 0 0 0 4 16.5"/>
        <path d="m7.5 14.5l1.842-5.526a.694.694 0 0 1 1.316 0L12.5 14.5m3-6v6m-7-2h3"/>
      </g>
    </svg>
  `,
  personnel:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 28 28">
  <path fill="currentColor" d="M6.75 3A3.75 3.75 0 0 0 3 6.75v11.5A3.75 3.75 0 0 0 6.75 22h11.5A3.75 3.75 0 0 0 22 18.25V6.75A3.75 3.75 0 0 0 18.25 3zM4.5 6.75A2.25 2.25 0 0 1 6.75 4.5h11.5a2.25 2.25 0 0 1 2.25 2.25v11.5a2.25 2.25 0 0 1-2.25 2.25H6.75a2.25 2.25 0 0 1-2.25-2.25zM9.25 24.5a3.75 3.75 0 0 1-3.021-1.528q.256.028.522.028H18.75A4.25 4.25 0 0 0 23 18.75v-12q0-.264-.028-.52A3.75 3.75 0 0 1 24.5 9.25v9.5a5.75 5.75 0 0 1-5.75 5.75zM7 14.875C7 13.839 7.84 13 8.875 13h7.25C17.16 13 18 13.84 18 14.875c0 1.397-.682 2.465-1.73 3.153c-1.019.669-2.364.972-3.77.972s-2.751-.303-3.77-.972C7.682 17.341 7 16.272 7 14.875m1.875-.375a.375.375 0 0 0-.375.375c0 .84.382 1.459 1.053 1.9c.7.459 1.73.725 2.947.725s2.247-.266 2.947-.726c.671-.44 1.053-1.06 1.053-1.899a.375.375 0 0 0-.375-.375zM11 9a1.5 1.5 0 1 1 3 0a1.5 1.5 0 0 1-3 0m1.5-3a3 3 0 1 0 0 6a3 3 0 0 0 0-6"/>
  </svg>
  `,
  member:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 26 26">
  <g fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"><path d="M6.36 12.977a5.5 5.5 0 0 0-.923 3.05V17a.5.5 0 1 1-1 0v-.972a6.5 6.5 0 0 1 1.092-3.606l.108-.162a.5.5 0 1 1 .832.555z"/><path d="M9.18 11.365c-1.09 0-2.107.544-2.711 1.45l-.832-.554a4.26 4.26 0 0 1 3.542-1.896h.22a.5.5 0 0 1 0 1zm3.078 1.6c.47.706.721 1.534.721 2.382h1a5.3 5.3 0 0 0-.889-2.936l-.1-.15a.5.5 0 1 0-.832.554z"/><path d="M9.448 11.365c1.089 0 2.106.544 2.71 1.45l.832-.554a4.26 4.26 0 0 0-3.542-1.896h-.22a.5.5 0 1 0 0 1z"/><path d="M9.25 10.25a2.25 2.25 0 1 0 0-4.5a2.25 2.25 0 0 0 0 4.5m0 1a3.25 3.25 0 1 0 0-6.5a3.25 3.25 0 0 0 0 6.5m4.259 4.936a5.5 5.5 0 0 0-.924 3.051v1.034a.5.5 0 1 1-1 0v-1.034a6.5 6.5 0 0 1 1.091-3.605l.133-.2a.5.5 0 0 1 .832.556z"/><path d="M16.42 14.5a3.34 3.34 0 0 0-2.78 1.488l-.831-.555A4.34 4.34 0 0 1 16.42 13.5h.224a.5.5 0 1 1 0 1zm3.187 1.686a5.5 5.5 0 0 1 .924 3.051v1.034a.5.5 0 1 0 1 0v-1.034a6.5 6.5 0 0 0-1.092-3.605l-.133-.2a.5.5 0 1 0-.832.556z"/><path d="M16.695 14.5a3.34 3.34 0 0 1 2.78 1.488l.832-.555a4.34 4.34 0 0 0-3.612-1.933h-.225a.5.5 0 1 0 0 1z"/><path d="M16.5 13.5a2.25 2.25 0 1 0 0-4.5a2.25 2.25 0 0 0 0 4.5m0 1a3.25 3.25 0 1 0 0-6.5a3.25 3.25 0 0 0 0 6.5"/><path d="M13 24.5c6.351 0 11.5-5.149 11.5-11.5S19.351 1.5 13 1.5S1.5 6.649 1.5 13S6.649 24.5 13 24.5m0 1c6.904 0 12.5-5.596 12.5-12.5S19.904.5 13 .5S.5 6.096.5 13S6.096 25.5 13 25.5"/></g>
  </svg>
  `,
  personal:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 48 48">
<g fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="4"><path stroke-linecap="round" d="M24 44c11.046 0 20-8.954 20-20S35.046 4 24 4S4 12.954 4 24s8.954 20 20 20" clip-rule="evenodd"/><path d="M24 23a5 5 0 1 0 0-10a5 5 0 0 0 0 10Z"/><path stroke-linecap="round" d="M10.022 38.332C10.366 33.121 14.702 29 20 29h8c5.291 0 9.623 4.11 9.977 9.311"/></g>
</svg>
  `,
  Readme:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
<path fill="currentColor" d="M9.197 10a.75.75 0 0 0 0 1.5h6.5a.75.75 0 0 0 0-1.5zm-2.382 4a.75.75 0 0 0 0 1.5h6.5a.75.75 0 0 0 0-1.5zm-1.581 4a.75.75 0 0 0 0 1.5h6.5a.75.75 0 0 0 0-1.5z"/><path fill="currentColor" d="M4.125 0h15.75a4.1 4.1 0 0 1 2.92 1.205A4.1 4.1 0 0 1 24 4.125c0 1.384-.476 2.794-1.128 4.16c-.652 1.365-1.515 2.757-2.352 4.104l-.008.013c-.849 1.368-1.669 2.691-2.28 3.97c-.614 1.283-.982 2.45-.982 3.503a2.625 2.625 0 1 0 4.083-2.183a.75.75 0 1 1 .834-1.247A4.126 4.126 0 0 1 19.875 24H4.5a4.125 4.125 0 0 1-4.125-4.125c0-2.234 1.258-4.656 2.59-6.902c.348-.586.702-1.162 1.05-1.728c.8-1.304 1.567-2.553 2.144-3.738H3.39c-.823 0-1.886-.193-2.567-1.035A3.65 3.65 0 0 1 0 4.125A4.125 4.125 0 0 1 4.125 0M15.75 19.875c0-1.38.476-2.786 1.128-4.15c.649-1.358 1.509-2.743 2.343-4.086l.017-.028c.849-1.367 1.669-2.692 2.28-3.972c.614-1.285.982-2.457.982-3.514A2.615 2.615 0 0 0 19.875 1.5a2.625 2.625 0 0 0-2.625 2.625c0 .865.421 1.509 1.167 2.009A.75.75 0 0 1 18 7.507H7.812c-.65 1.483-1.624 3.069-2.577 4.619c-.334.544-.666 1.083-.98 1.612c-1.355 2.287-2.38 4.371-2.38 6.137A2.625 2.625 0 0 0 4.5 22.5h12.193a4.1 4.1 0 0 1-.943-2.625M1.5 4.125c-.01.511.163 1.008.487 1.403c.254.313.74.479 1.402.479h12.86a3.65 3.65 0 0 1-.499-1.882a4.1 4.1 0 0 1 .943-2.625H4.125A2.625 2.625 0 0 0 1.5 4.125"/>
</svg>
`,
  evaluate:`
<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 32 32">
<path fill="currentColor" d="M4 28c-1.7 0-3-1.3-3-3s1.3-3 3-3s3 1.3 3 3s-1.3 3-3 3m0-4c-.6 0-1 .4-1 1s.4 1 1 1s1-.4 1-1s-.4-1-1-1m24 4c-1.7 0-3-1.3-3-3s1.3-3 3-3s3 1.3 3 3s-1.3 3-3 3m0-4c-.6 0-1 .4-1 1s.4 1 1 1s1-.4 1-1s-.4-1-1-1M22.4 4.4l-.9 1.8C26.1 8.4 29 13 29 18c0 .7-.1 1.4-.2 2.1l2 .3c.1-.8.2-1.6.2-2.5c0-5.7-3.4-11-8.6-13.5M16 7c-1.7 0-3-1.3-3-3s1.3-3 3-3s3 1.3 3 3s-1.3 3-3 3m0-4c-.6 0-1 .4-1 1s.4 1 1 1s1-.4 1-1s-.4-1-1-1"/><path fill="currentColor" d="m25.5 13.6l-1-1.7l-7.5 4.4V9h-2v7.2L6.2 11c-.5-.3-1.1-.1-1.4.3s-.1 1.1.3 1.4L14 18l-6.2 3.6l1 1.7l6.2-3.6V30c0 .6.4 1 1 1s1-.4 1-1V19.8l6.2 3.7l1-1.7L18 18z"/>
</svg>
  `,
  share:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M12 3a3 3 0 1 0 3 3m-9.5 9a3 3 0 1 1-3 3m16 3a3 3 0 1 1 3-3M20 13a7.98 7.98 0 0 0-2.708-6M4 13a7.98 7.98 0 0 1 2.708-6M10 20.748A8 8 0 0 0 12 21a8 8 0 0 0 2-.252"/>
  </svg>
  `,
  communication:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" fill-rule="evenodd" clip-rule="evenodd"><path fill="currentColor" d="M14.777 11.27a.31.31 0 0 0 .27-.35a2.3 2.3 0 0 1 .4-1.309q.375-.564.84-1.059q.673-.779 1.428-1.479q.46.102.93.12a.28.28 0 0 0 0-.55c-1.78-.18-2.559-1.338-2.459-2.487a2.46 2.46 0 0 1 2.658-2.149a2.2 2.2 0 0 1 2.239 2.099a2.28 2.28 0 0 1-1.8 2.678a.312.312 0 1 0 .08.62c.35-.032.689-.13 1-.29q.372.416.67.889q.18.27.33.54c.11.189.22.379.319.569q.364.709.61 1.469a.3.3 0 0 0 .36.24a.31.31 0 0 0 .249-.36a7.3 7.3 0 0 0-.31-1.729a3.7 3.7 0 0 0-.29-.73a4 4 0 0 0-.46-.639a4.4 4.4 0 0 0-.848-.67a3.39 3.39 0 0 0 .999-2.687A3.127 3.127 0 0 0 18.814.908a3.387 3.387 0 0 0-3.487 3.198a2.9 2.9 0 0 0 1.669 2.708a7 7 0 0 0-1.2.789a4.6 4.6 0 0 0-.909 1.05a3.06 3.06 0 0 0-.46 2.347a.3.3 0 0 0 .35.27m-6.355 8.533a4 4 0 0 0-.42-.63a4.2 4.2 0 0 0-.849-.699a3.38 3.38 0 0 0 1-2.668A3.117 3.117 0 0 0 4.934 12.7a3.37 3.37 0 0 0-3.487 3.187a2.9 2.9 0 0 0 1.658 2.708a7 7 0 0 0-1.199.79a4.6 4.6 0 0 0-.899 1.049a3.06 3.06 0 0 0-.46 2.358a.3.3 0 0 0 .35.26a.31.31 0 0 0 .27-.35a2.3 2.3 0 0 1 .4-1.309q.375-.565.839-1.06q.677-.775 1.429-1.478q.457.116.93.12a.28.28 0 0 0 0-.55c-1.76-.18-2.519-1.339-2.419-2.488a2.46 2.46 0 0 1 2.658-2.188a2.2 2.2 0 0 1 2.198 2.138a2.28 2.28 0 0 1-1.788 2.678a.31.31 0 0 0-.275.345a.31.31 0 0 0 .345.275c.348-.031.688-.13.999-.29q.371.416.67.89q.179.265.329.549c.11.18.22.37.32.56q.364.71.61 1.468a.31.31 0 0 0 .589-.12a7.3 7.3 0 0 0-.31-1.728a4 4 0 0 0-.27-.71M1.798 10.27a6.7 6.7 0 0 0 4.076 1.25A55 55 0 0 0 12 11a.27.27 0 0 0 .26-.3a.27.27 0 0 0-.29-.25q-2.988.335-5.996.35a5.86 5.86 0 0 1-3.517-1.199a4.73 4.73 0 0 1-1.449-3.597a5.36 5.36 0 0 1 1.22-3.667a3.48 3.48 0 0 1 2.208-1a5.15 5.15 0 0 1 2.568.36a3 3 0 0 1 1.708 1.84c.48 1.378.36 3.047.92 4.386c.23.544.599 1.02 1.069 1.379a10 10 0 0 0 2.118 1.229a.31.31 0 0 0 .42-.14a.33.33 0 0 0-.14-.39A9.8 9.8 0 0 1 11.1 8.802a2.86 2.86 0 0 1-.829-1.12c-.57-1.678-.27-3.676-1.06-5.185A3.84 3.84 0 0 0 7.364.818a6.2 6.2 0 0 0-2.997-.5a4.6 4.6 0 0 0-2.908 1.29a6.38 6.38 0 0 0-1.45 4.396a5.68 5.68 0 0 0 1.789 4.267m20.743 3.527a4.5 4.5 0 0 0-2.877-1.289a6.1 6.1 0 0 0-2.998.5a3.78 3.78 0 0 0-1.849 1.668c-.829 1.52-.49 3.548-1.099 5.166a2.84 2.84 0 0 1-.859 1.15a9.8 9.8 0 0 1-1.998 1.209a.32.32 0 0 0-.14.42a.33.33 0 0 0 .42.139a10.4 10.4 0 0 0 2.118-1.239c.467-.358.835-.83 1.069-1.369c.56-1.349.44-2.998.92-4.397a3 3 0 0 1 1.748-1.878a5.15 5.15 0 0 1 2.538-.35a3.48 3.48 0 0 1 2.208 1a5.37 5.37 0 0 1 1.249 3.647a4.76 4.76 0 0 1-1.339 3.567a5.85 5.85 0 0 1-3.517 1.189a52 52 0 0 1-5.995-.35a.28.28 0 1 0 0 .55a55 55 0 0 0 6.095.57a6.7 6.7 0 0 0 4.077-1.25a5.68 5.68 0 0 0 1.678-4.256a6.34 6.34 0 0 0-1.449-4.397"/><path fill="currentColor" d="M4.485 4.635h2.289a.319.319 0 0 0 .1-.63c-.42-.13-.83-.259-1.26-.349a3.4 3.4 0 0 0-.56-.07a3.6 3.6 0 0 0-.569 0q-.664.09-1.299.31a.27.27 0 0 0-.18.36a.28.28 0 0 0 .31.24c.37.01.76.1 1.17.14m12.74 12.21h2.288c.41 0 .799-.13 1.229-.15a.28.28 0 0 0 .351-.235a.28.28 0 0 0-.281-.315a6.6 6.6 0 0 0-1.3-.31a3.6 3.6 0 0 0-.569 0a5 5 0 0 0-.56.07q-.64.144-1.258.36a.32.32 0 0 0-.26.36a.31.31 0 0 0 .36.22M2.747 7.403a.27.27 0 0 0 .33.2h2.008l.6-.06l1.428-.18a.31.31 0 1 0 .02-.62q-.73-.133-1.469-.19a5 5 0 0 0-.639 0a3.3 3.3 0 0 0-.63.08a7.6 7.6 0 0 0-1.388.44a.28.28 0 0 0-.26.33m13.819 11.831a.31.31 0 0 0 .33.3l1.419.18l.6.06h2.008a.28.28 0 0 0 .299-.417a.27.27 0 0 0-.17-.123a7.6 7.6 0 0 0-1.438-.46c-.21 0-.42-.06-.64-.08s-.42 0-.63 0q-.739.055-1.468.19a.32.32 0 0 0-.31.35"/></g>
  </svg>
  `,
  record:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5"><path d="m11.777 10l4.83 1.294M11 12.898l2.898.776m6.414-1.027c-.605 2.255-.907 3.383-1.592 4.114a4 4 0 0 1-2.01 1.161q-.145.034-.295.052c-.915.113-2.032-.186-4.064-.73c-2.255-.605-3.383-.907-4.114-1.592a4 4 0 0 1-1.161-2.011c-.228-.976.074-2.103.679-4.358l.517-1.932l.244-.905c.455-1.666.761-2.583 1.348-3.21a4 4 0 0 1 2.01-1.16c.976-.228 2.104.074 4.36.679c2.254.604 3.382.906 4.113 1.59a4 4 0 0 1 1.161 2.012c.161.69.057 1.456-.231 2.643"/><path stroke-linejoin="round" d="M3.272 16.647c.604 2.255.907 3.383 1.592 4.114a4 4 0 0 0 2.01 1.161c.976.227 2.104-.075 4.36-.679c2.254-.604 3.382-.906 4.113-1.591a4 4 0 0 0 1.068-1.678M8.516 6.445q-.527.137-1.165.31c-2.255.604-3.383.906-4.114 1.59a4 4 0 0 0-1.161 2.012c-.161.69-.057 1.456.231 2.643"/></g>
  </svg>
  `,
  check:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor"><path stroke-width="1.5" d="M14.54 2v2.486c0 1.748 0 2.622.215 3.461c.213.84.633 1.61 1.472 3.15l1.136 2.088c2.138 3.926 3.207 5.89 2.33 7.345l-.014.022C18.79 22 16.526 22 12 22s-6.79 0-7.68-1.448l-.013-.022c-.877-1.455.192-3.419 2.33-7.345l1.136-2.088c.84-1.54 1.258-2.31 1.472-3.15s.214-1.713.214-3.46V2"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 16.002L9.009 16M15 18.002l.009-.002"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 2h8"/><path stroke-linecap="round" stroke-width="1.5" d="M7.5 11.556c1-1.153 2.6-.322 4.5.762c2.5 1.426 4 .332 4.5-.703"/></g>
  </svg>
  `,
  collaboration:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5">
  <path d="M1.874 17.625a2.625 2.625 0 1 0 5.25 0a2.625 2.625 0 0 0-5.25 0"/><path d="M8.249 23.25a4.25 4.25 0 0 0-7.5 0m16.125-5.625a2.625 2.625 0 1 0 5.25 0a2.625 2.625 0 0 0-5.25 0"/><path d="M23.249 23.25a4.25 4.25 0 0 0-7.5 0M9.374 3.375a2.625 2.625 0 1 0 5.25 0a2.625 2.625 0 0 0-5.25 0m5.874 4.875a4.27 4.27 0 0 0-6.5 0m.302 11.457a8.28 8.28 0 0 0 5.944-.018M6.348 6a8.22 8.22 0 0 0-2.6 6c0 .253.015.5.038.75m16.425 0c.022-.248.038-.5.038-.75a8.22 8.22 0 0 0-2.6-6"/></g></svg>
  `,
  ManageCollaborationTask:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="currentColor" fill-rule="evenodd" d="M9.068 10.263c.43.17.887.261 1.349.27q.58.037 1.16 0c.799-.06 1.388-.24 2.477-.29c.22 0 .86.07 1.27 0a1.36 1.36 0 0 0 .609-.2c.351-.26.608-.63.73-1.05c.18-.758.237-1.54.17-2.318c0-.57.079-1.638 0-2.508a3.3 3.3 0 0 0-.32-1.259a7.5 7.5 0 0 0-.89-1.219A17 17 0 0 0 14.104.16a.63.63 0 0 0-.44-.16c-.33 0-1.119.15-1.249.16L9.508.55a.39.39 0 0 0-.32.26a.35.35 0 0 0-.33-.23a.34.34 0 0 0-.33.35l-.1 5.935c0 .44-.14 1.17-.15 1.789c-.016.32.024.642.12.95a1.18 1.18 0 0 0 .67.659m5.756-7.894c.275.33.517.689.72 1.069c.074.181.117.374.129.57l-.06-.01a15 15 0 0 0-1.709-.23a1.8 1.8 0 0 1-.6-.2c0-.17.08-.44.08-.63v-.95l-.06-.859c.14 0 .26 0 .31-.1q.632.638 1.19 1.34m-5.226-1l2.867-.26l-.15.85c0 .08-.11.42-.18.81a7 7 0 0 0-.06.889a1.16 1.16 0 0 0 .38.939c.323.18.681.285 1.05.31c.66 0 1.349-.16 1.998-.12a.32.32 0 0 0 .19 0c0 .76-.11 1.559-.11 1.998q.057.705 0 1.41c-.2 1.568-.91.789-1.549.819c-1.109.07-1.709.25-2.528.33a8 8 0 0 1-1 0a4 4 0 0 1-.769-.06c-.09 0-.19-.14-.24-.09a2.4 2.4 0 0 1-.08-.74c0-.57.07-1.18.05-1.559l-.25-5.776a.38.38 0 0 0 .38.2zm12.21 17.767a1.8 1.8 0 0 0-.47-.36a3 3 0 0 0-.429-.14a2.47 2.47 0 0 0 .67-2.598a2.93 2.93 0 0 0-2.569-2.048a2.15 2.15 0 0 0-1.478.45a2.88 2.88 0 0 0-1 1.798a2.18 2.18 0 0 0 .74 2.178q-.39.093-.75.27a1.9 1.9 0 0 0-.44.35a2.5 2.5 0 0 0-.33.47a3.3 3.3 0 0 0-.369 1.239a3 3 0 0 0-.62-.58a5 5 0 0 0-.759-.38a2.78 2.78 0 0 0 .54-2.758a3.08 3.08 0 0 0-3.787-1.948a3 3 0 0 0-1.3 4.906c-.14.06-.29.11-.419.18q-.363.229-.67.53c0 .06-.08.13-.13.18a3.4 3.4 0 0 0-.4-1.27a2 2 0 0 0-.369-.47a2 2 0 0 0-.45-.349a4 4 0 0 0-.45-.14a2.47 2.47 0 0 0 .67-2.608a2.93 2.93 0 0 0-2.528-1.998a2.12 2.12 0 0 0-1.469.45a2.84 2.84 0 0 0-.999 1.798a2.16 2.16 0 0 0 .74 2.178a3.6 3.6 0 0 0-.76.28q-.243.152-.44.36a2.5 2.5 0 0 0-.33.47a3.4 3.4 0 0 0-.429 1.219a.38.38 0 0 0 .25.48a.39.39 0 0 0 .49-.24q.138-.259.33-.48q.188-.206.399-.39l.37-.39q.365-.474.8-.889a.3.3 0 0 0 .139-.16h.17a.35.35 0 0 0 .36-.33a.33.33 0 0 0-.31-.31c-.86-.11-1-.899-.81-1.598s.67-1.27 1.38-1.18a1.72 1.72 0 0 1 1.498 1.12a1.48 1.48 0 0 1-.46 1.809a1.06 1.06 0 0 1-.43.2q-.29.06-.589.08a.39.39 0 0 0-.336.546a.38.38 0 0 0 .366.223a3.6 3.6 0 0 0 .86-.06h.14c.26.23.47.48.689.7l.4.399q.22.183.42.39q.196.226.34.49a.37.37 0 0 0 .349.25a5.1 5.1 0 0 0-.62 1.788a.39.39 0 1 0 .74.22a5.5 5.5 0 0 1 1.319-1.779l.47-.44q.362-.331.769-.61a1 1 0 0 0 .1 0c.512.09 1.036.09 1.549 0q.044.015.09 0a.39.39 0 0 0 .46.14q.22-.101.419-.24q.445.336.85.72c.16.16.299.32.429.47a5.8 5.8 0 0 1 1.11 1.998a.39.39 0 0 0 .489.26a.4.4 0 0 0 .29-.46a7.2 7.2 0 0 0-.4-1.908a2.6 2.6 0 0 0-.21-.36a.38.38 0 0 0 .44-.24q.148-.255.34-.48c.11-.14.26-.239.39-.379c.129-.14.249-.24.369-.38q.365-.47.8-.879a.3.3 0 0 0 .15-.16h.169a.34.34 0 0 0 0-.68c-.85-.11-1-.899-.81-1.598s.68-1.27 1.39-1.18a1.71 1.71 0 0 1 1.488 1.12a1.48 1.48 0 0 1-.41 1.839c-.13.096-.28.164-.439.2q-.286.06-.58.08a.39.39 0 0 0-.37.399a.37.37 0 0 0 .39.37q.439.021.87-.06h.14c.26.23.47.47.69.69s.269.26.399.389c.13.13.29.24.42.39q.19.225.34.48a.38.38 0 0 0 .479.26a.39.39 0 0 0 .26-.48a3.3 3.3 0 0 0-.42-1.3a2 2 0 0 0-.38-.52m-11.58.34a1.9 1.9 0 0 1-.29-1.829a1.74 1.74 0 0 1 1.168-1.259a1.93 1.93 0 0 1 2.429 1.07c1.129 2.697-2.648 2.747-3.308 2.018M2.533 13.12c.15 0 .2.14.88-1.339c.09-.19.16-.4.23-.59c.459-1.198.609-1.258.379-1.488c-.45-.4-1.41.85-1.42.88a2.1 2.1 0 0 0-.419.959c-.06.3-.11 1.538.35 1.578m20.285-2.128a2 2 0 0 0-.26-.45s-1-1.279-1.42-.879c-.249.23-.109.31.35 1.489c.08.19.15.4.23.59c.69 1.478.73 1.348.89 1.339c.46 0 .4-1.28.36-1.58a2 2 0 0 0-.15-.509M6.02 8.714q.144-.066.27-.16a1.1 1.1 0 0 0 .24-.19c.76-.67.9-.72.79-1c0-.11-.16-.479-1.4-.05a1.6 1.6 0 0 0-.64.41c-.16.18-.749 1-.509 1.24c.09.08.15.25 1.25-.25m12.569-.35q.114.105.24.19q.133.094.28.16c1.11.5 1.17.33 1.25.24c.24-.26-.36-1.06-.51-1.24a1.8 1.8 0 0 0-.65-.41c-1.229-.429-1.359-.06-1.399.05c-.1.29.04.34.79 1.01" clip-rule="evenodd"/>
  </svg>`,
  home: `
    <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
        <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5"><path stroke-linejoin="round" d="M12 22.887c6.012 0 10.887-4.874 10.887-10.887S18.012 1.113 12 1.113S1.113 5.987 1.113 12S5.987 22.887 12 22.887"/><path stroke-linejoin="round" d="M6.43 15.815a1.686 1.686 0 1 0 0-3.373a1.686 1.686 0 0 0 0 3.373m11.358-7.758a3.035 3.035 0 0 1-5.821 1.206a2.18 2.18 0 1 1 .872-3.562a3.035 3.035 0 0 1 4.95 2.356"/><path d="M15.46 17.458c.273-2.51 3.08-1.508 2.96-4.127"/><path stroke-linejoin="round" d="M12 15.005a.296.296 0 1 1 0-.591m0 .591a.296.296 0 1 0 0-.591M5.778 7.373a.296.296 0 0 1 0-.591m0 .591a.296.296 0 0 0 0-.591m7.358 12.793a.296.296 0 0 1 0-.592m0 .592a.296.296 0 0 0 0-.592"/></g>
    </svg>`
  ,
  messages:`
  <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
  <path fill="currentColor" d="M23.963 9.61c-.09-2-1.61-4.821-4.191-6.882A12.25 12.25 0 0 0 12.16.008a12.36 12.36 0 0 0-8.122 2.83A11.33 11.33 0 0 0 .037 10.44c-.18 3.06.3 4.851 1.37 6.082s2.941 2 5.482 2.85c.09 0 .12.15.17.3c.114.51.114 1.04 0 1.55a4.44 4.44 0 0 1-.9 2.081a.43.43 0 0 0 .36.69q.441.01.88-.05a8.8 8.8 0 0 0 3.56-1.16a5.4 5.4 0 0 0 1.001-.95c.69-.84 1.28-1.9 1.62-2.34c.41 0 1.4 0 2.441-.13a13.5 13.5 0 0 0 1.76-.3a8.35 8.35 0 0 0 4.862-3.562a10 10 0 0 0 1.32-5.891m-2 5.481a7.54 7.54 0 0 1-4.352 3.321q-.819.214-1.66.31c-1.32.16-2.4-.07-2.73.17s-.921 1.33-1.561 2.16a5.3 5.3 0 0 1-1.09 1.121a7.8 7.8 0 0 1-3.251 1c.378-.729.597-1.53.64-2.35a3.6 3.6 0 0 0-.18-1.59a1.12 1.12 0 0 0-.64-.7c-2.33-.851-4.001-1.461-5.001-2.661s-1.25-2.681-1-5.362a10.2 10.2 0 0 1 3.7-6.792a11.35 11.35 0 0 1 7.402-2.56a11.2 11.2 0 0 1 6.942 2.4c2.35 1.82 3.81 4.301 3.93 6.102a9.16 9.16 0 0 1-1.21 5.431z"/><path fill="currentColor" d="M5.138 8.779a1.49 1.49 0 0 0 1.35-2.64c-1.43-.85-3.45 1.87-1.35 2.64m1.37 3.391c-1.43-.84-3.45 1.84-1.35 2.65a1.5 1.5 0 0 0 1.35-2.65M19.342 7.3c-.49-.06-1.87-.2-3.471-.29H12.9a40 40 0 0 0-3.462.32a.3.3 0 0 0 .03.6c.71 0 2 .12 3.441.18h2.94c1.591-.1 2.942-.1 3.432-.1a.34.34 0 0 0 .37-.31a.35.35 0 0 0-.31-.4m-.249 5.47H14.3c-.48 0-1 0-1.44.08c-1.631.13-3.002.3-3.322.31a.3.3 0 1 0 0 .601c.36 0 1.73.16 3.401.18h1.44c.44 0 1-.06 1.44-.1a78 78 0 0 0 3.352-.36a.34.34 0 1 0 0-.68z"/>
  </svg>
  `,
  notification: `
     <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24">
        <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M14.857 17.082a24 24 0 0 0 5.454-1.31A8.97 8.97 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.97 8.97 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.3 24.3 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0M3.124 7.5A8.97 8.97 0 0 1 5.292 3m13.416 0a8.97 8.97 0 0 1 2.168 4.5"/>
     </svg>
  `,

  inbox: `
    <svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><g fill="none">
    <path stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M10.5 22v-2m4 2v-2"/><path fill="currentColor" d="M11 20v.75h.75V20zm-9.75-8a.75.75 0 0 0 1.5 0zm1.5 4a.75.75 0 0 0-1.5 0zM14 19.25a.75.75 0 0 0 0 1.5zm7.25-8a.75.75 0 0 0 1.5 0zm-3.75-6a.75.75 0 0 0 0 1.5zM22.75 15a.75.75 0 0 0-1.5 0zM7 5.25a.75.75 0 0 0 0 1.5zm2 14a.75.75 0 0 0 0 1.5zm6 1.5a.75.75 0 0 0 0-1.5zm-4-1.5H4.233v1.5H11zm-6.767 0c-.715 0-1.483-.718-1.483-1.855h-1.5c0 1.74 1.231 3.355 2.983 3.355zM6.5 6.75c1.967 0 3.75 1.902 3.75 4.5h1.5c0-3.201-2.246-6-5.25-6zm0-1.5c-3.004 0-5.25 2.799-5.25 6h1.5c0-2.598 1.783-4.5 3.75-4.5zM10.25 17v3h1.5v-3zm0-5.75V17h1.5v-5.75zm-7.5.75v-.75h-1.5V12zm0 5.395V16h-1.5v1.395zm17.043 1.855H14v1.5h5.793zm1.457-1.825c0 1.12-.757 1.825-1.457 1.825v1.5c1.738 0 2.957-1.601 2.957-3.325zm1.5-6.175c0-3.201-2.246-6-5.25-6v1.5c1.967 0 3.75 1.902 3.75 4.5zM21.25 15v2.425h1.5V15zM7 6.75h11v-1.5H7zm2 14h6v-1.5H9z"/><path stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M5 16h3m8-6.116V5.411m0 0V2.635c0-.236.168-.439.4-.484l.486-.093a3.2 3.2 0 0 1 1.755.156l.08.03c.554.214 1.16.254 1.737.115a.44.44 0 0 1 .542.427v2.221a.51.51 0 0 1-.393.499l-.066.016a3.2 3.2 0 0 1-1.9-.125a3.2 3.2 0 0 0-1.755-.156z"/></g>
    </svg>
  `,

}

// 图标通过模板内联 SVG 渲染，无需组件或注册表
const currentParent = ref(null)
const activeList = computed(() => (currentParent.value?.children ?? filteredRootNav.value))

function isActive(item) {
  return item.route ? (route.path === item.route || route.path.startsWith(item.route + '/')) : false
}

function onItemClick(item) {
  if(item.icon==='output') goOut();
  else if (item.children && item.children.length) {
    currentParent.value = item
  } else if (item.route) {
    router.push(item.route)
  }
}

function goBack() {
  currentParent.value = null
}

// 根据当前路由自动展开对应的父级
watch(
  () => route.path,
  (path) => {
    // 精确匹配路由，避免路径前缀误匹配
    const parent = filteredRootNav.value.find((p) => {
      if (!p.children) return false
      return p.children.some((c) => {
        if (!c.route) return false
        // 精确匹配完整路径，或者匹配以 / 结尾的路径
        return path === c.route || (c.route.endsWith('/') && path.startsWith(c.route))
      })
    })
    currentParent.value = parent ?? null
  },
  {immediate: true}
)
</script>

<template>
  <div class="layout">
    <aside class="sidebar flex flex-col">
      <div v-if="currentParent" class="nav-header">
        <button class="back-btn" @click="goBack" aria-label="返回" title="返回">
          <svg class="back-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path d="M15 6l-6 6l6 6" fill="none" stroke="currentColor" stroke-linecap="round"
                  stroke-linejoin="round" stroke-width="1.7"/>
          </svg>
        </button>
        <span class="nav-header-title">{{ currentParent.label }}</span>
      </div>

      <div class="nav-list ">
        <div
          v-for="item in activeList"
          :key="item.label"
          class="nav-item gap-2"
          :class="{ 'is-active': isActive(item) }"
          :data-icon="item.icon"
          @click="onItemClick(item)"
        >
          <div class="flex items-center">
            <span class="nav-icon" v-html="icons[item.icon] || icons.dot"/>
            <span class="nav-text">{{ item.label }}</span>
          </div>
        </div>
      </div>

      <div class="nav-spacer"/>
    </aside>

    <main class="content">
      <router-view/>
    </main>
  </div>
</template>

<style scoped>

.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  /* theme tokens - 亮色模式默认值 */
  --accent: #7c3aed; /* violet-600 */
  --accent-2: #6366f1; /* indigo-500 */
  --ring: rgba(99, 102, 241, 0.35);
  --border: rgba(148, 163, 184, 0.25);
  --text: #334155; /* slate-700 */
  --text-dim: #1f61bd; /* slate-600 */

  width: 5rem;
  border-right: 1px solid var(--ring);
  border-radius: 0 0.75rem 0.75rem 0;
  transition: width 0.38s cubic-bezier(.2, .8, .2, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0.85rem 0.5rem 0.75rem;
  gap: 0.35rem;

  background: radial-gradient(120% 90% at 0% 0%, rgba(124, 58, 237, 0.10) 0%, rgba(124, 58, 237, 0.00) 50%),
  radial-gradient(120% 90% at 100% 0%, rgba(99, 102, 241, 0.12) 0%, rgba(99, 102, 241, 0.00) 45%),
  linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.60));
  backdrop-filter: saturate(140%) blur(12px);
  box-shadow: 0 10px 25px rgba(2, 6, 23, 0.08),
  inset 0 1px 0 rgba(255, 255, 255, 0.35),
  inset 0 -1px 0 rgba(2, 6, 23, 0.03);

  position: sticky;
  top: 0;
  height: 100vh;
}

.sidebar:hover {
  width: 12.5rem;
}

/* 顶部：返回与标题 */
.nav-header {
  display: flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0.2rem 0.35rem 0.45rem 0.35rem;
  margin: 0 0.25rem 0.4rem;
}

.back-btn {
  position: relative;
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  border-radius: 9999px;
  color: var(--text-dim);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.65), rgba(255, 255, 255, 0.35));
  border: 1px solid rgba(148, 163, 184, 0.30);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.75), 0 8px 18px rgba(2, 6, 23, 0.06);
  transition: transform 0.22s ease, box-shadow 0.22s ease, color 0.22s ease, background 0.22s ease, border-color 0.22s ease;
  cursor: pointer;
}

.back-btn::before {
  content: "";
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  background: radial-gradient(80% 80% at 30% 20%, rgba(124, 58, 237, 0.35), rgba(99, 102, 241, 0.22));
  filter: blur(8px);
  opacity: 0;
  transition: opacity 0.28s ease;
  z-index: -1;
}

.back-btn:hover {
  color: var(--text);
  transform: translateX(-1px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.85), 0 10px 22px rgba(2, 6, 23, 0.08);
  border-color: rgba(148, 163, 184, 0.35);
}

.back-btn:hover::before {
  opacity: 1;
}

.back-icon {
  width: 1rem;
  height: 1rem;
}

.nav-header-title {
  display: inline-block;
  max-width: 0;
  opacity: 0;
  transform: translateX(-4px);
  font-size: 0.96rem;
  font-weight: 600;
  color: var(--text);
  letter-spacing: 0.2px;
  white-space: nowrap;
  overflow: hidden;
  transition: max-width 0.38s cubic-bezier(.2, .8, .2, 1), opacity 0.28s ease, transform 0.28s ease;
}

/* 展开时平滑显现标题 */
.sidebar:hover .nav-header-title {
  max-width: 10rem;
  opacity: 1;
  transform: translateX(0);
}

.nav-list {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-top: 0.05rem;
}

.content {
  flex: 1;
  min-width: 0;
  transition: all 0.3s ease;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 0.6rem 0.7rem;
  margin: 0 0.3rem;
  border-radius: 0.75rem;
  cursor: pointer;
  transition: padding 0.25s ease,
  transform 0.22s ease,
  box-shadow 0.22s ease,
  background-color 0.22s ease,
  border-color 0.22s ease,
  backdrop-filter 0.22s ease;
  border: 1px solid transparent;
  background: transparent;
}

/* 左侧渐变高亮条（更纤细） */
.nav-item::before {
  content: "";
  position: absolute;
  left: 0.35rem;
  top: 22%;
  bottom: 22%;
  width: 3px;
  background: linear-gradient(180deg, var(--accent), var(--accent-2));
  border-radius: 6px;
  opacity: 0;
  transform: translateX(-6px) scaleY(0.6);
  transition: all 0.28s ease;
  filter: drop-shadow(0 1px 4px rgba(99, 102, 241, 0.28));
}

/* hover 背景轻薄填充 */
.nav-item::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.08), rgba(99, 102, 241, 0.04));
  opacity: 0;
  transform: scale(0.985);
  transition: all 0.22s ease;
  pointer-events: none;
}

.nav-item:hover::before {
  opacity: 1;
  transform: translateX(0) scaleY(1);
}

.nav-item:hover::after {
  opacity: 1;
  transform: scale(1);
}

.nav-item:hover {
  transform: translateX(1px);
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(148, 163, 184, 0.24);
  backdrop-filter: saturate(125%) blur(7px);
  box-shadow: 0 6px 14px rgba(2, 6, 23, 0.05);
  padding: 0.6rem 0.7rem 0.6rem 0.95rem; /* 增加左侧内边距，拉开竖线与圆框距离 */
}

/* 活跃态（可在路由联动时加 .is-active） */
.nav-item.is-active,
.nav-item.is-active:hover {
  background: linear-gradient(180deg, rgba(124, 58, 237, 0.08), rgba(99, 102, 241, 0.06)),
  rgba(255, 255, 255, 0.6);
  border-color: rgba(99, 102, 241, 0.35);
  box-shadow: 0 10px 22px rgba(79, 70, 229, 0.18);
}

.nav-item.is-active .nav-text {
  font-weight: 600;
}

/* 仅使用名称映射的图标，保持固定尺寸与对齐 */

/* 容器 = 固定尺寸；内部 svg 100% 填充，继承颜色 */
.nav-icon {
  width: 1.35rem;
  height: 1.35rem;
  color: var(--text-dim);
  transition: transform 0.22s ease, color 0.22s ease, filter 0.22s ease, box-shadow 0.22s ease;
  display: block;
  position: relative;
  border-radius: 9999px;
  isolation: isolate;
}

/* 玻璃质感圆底（悬停/激活时显现） */
.nav-icon::before {
  content: "";
  position: absolute;
  left: 50%;
  top: 50%;
  width: 1.9rem;
  height: 1.9rem;
  transform: translate(-50%, -50%) scale(0.86);
  border-radius: inherit;
  background: radial-gradient(120% 120% at 0% 0%, rgba(124, 58, 237, 0.10), rgba(99, 102, 241, 0.05)),
  linear-gradient(180deg, rgba(255, 255, 255, 0.65), rgba(248, 246, 246, 0.35));
  border: 1px solid rgba(148, 163, 184, 0.28);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.55), 0 6px 14px rgba(2, 6, 23, 0.06);
  opacity: 0;
  transition: opacity 0.22s ease, transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
  z-index: -1;
  pointer-events: none;
}

/* 渐变光晕（更柔和） */
.nav-icon::after {
  content: "";
  position: absolute;
  inset: -6px;
  border-radius: inherit;
  background: conic-gradient(from 220deg, rgba(124, 58, 237, 0.55), rgba(99, 102, 241, 0.25), transparent 240deg);
  filter: blur(12px);
  opacity: 0;
  transform: scale(0.92);
  transition: opacity 0.28s ease, transform 0.28s ease, filter 0.28s ease;
  z-index: -2;
  pointer-events: none;
}

.nav-icon :where(svg) {
  width: 100%;
  height: 100%;
  display: block;
}

/* 悬停用中性文本色，激活态主色（沿用原动效） */
.nav-item:hover .nav-icon {
  transform: translateY(-0.5px) scale(1.05);
  color: var(--text);
  filter: drop-shadow(0 2px 6px rgba(2, 6, 23, 0.12));
}

.nav-item:hover .nav-icon::before {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7), 0 8px 18px rgba(2, 6, 23, 0.08);
  border-color: rgba(148, 163, 184, 0.32);
}

.nav-item:hover .nav-icon::after {
  opacity: 0.75;
  transform: scale(1);
}

.nav-item.is-active .nav-icon {
  transform: translateY(-0.5px) scale(1.05);
  color: var(--accent);
  filter: drop-shadow(0 3px 8px rgba(79, 70, 229, 0.25));
}

.nav-item.is-active .nav-icon::before {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8), 0 10px 22px rgba(79, 70, 229, 0.20);
  border-color: rgba(99, 102, 241, 0.38);
}

.nav-item.is-active .nav-icon::after {
  opacity: 1;
  filter: blur(14px);
  transform: scale(1.02);
}

/* 悬停时不展示图标圆框与光晕（提升优先级覆盖下方规则） */
.nav-item:hover .nav-icon::before,
.nav-item:hover .nav-icon::after {
  opacity: 0 !important;
}

/* 点击反馈 */
.nav-item:active .nav-icon {
  transform: translateY(0) scale(0.98);
  filter: drop-shadow(0 1px 4px rgba(2, 6, 23, 0.10));
}

.nav-text {
  display: inline-block;
  max-width: 0;
  margin-left: 0;
  font-size: 0.94rem;
  color: var(--text-dim);
  white-space: nowrap;
  opacity: 0;
  overflow: hidden;
  font-weight: 500;
  letter-spacing: 0.1px;
  transform: translateX(-4px);
  will-change: max-width, opacity, transform;
  transition: max-width 0.38s cubic-bezier(.2, .8, .2, 1), opacity 0.28s ease, color 0.22s ease, transform 0.28s ease;
}

/* 展开时平滑显现文字 */
.sidebar:hover .nav-text {
  max-width: 12rem;
  margin-left: 0.7rem;
  opacity: 1;
  transform: translateX(0);
}

.nav-item:hover .nav-text,
.nav-item.is-active .nav-text {
  color: var(--text);
}

.nav-spacer {
  flex: 1;
}

/* 折叠态（非 hover）视觉优化：保留元素，靠动画过渡 */
.sidebar:not(:hover) .nav-list {
  gap: 0.28rem;
}

.sidebar:not(:hover) .nav-item {
  padding: 0.52rem 0.55rem;
  margin: 0 0.24rem;
  border-color: transparent;
  background: transparent;
  box-shadow: none;
}

/* 不再使用 display:none 和居中对齐，避免跳变 */
</style>
