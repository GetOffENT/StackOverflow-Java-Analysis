import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

/* Layout */
import Layout from "@/layout";

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: "/login",
    component: () => import("@/views/login/index"),
    hidden: true,
  },

  {
    path: "/404",
    component: () => import("@/views/404"),
    hidden: true,
  },

  {
    path: "/",
    component: Layout,
    redirect: "/overview",
    children: [
      {
        path: "overview",
        name: "Dashboard",
        component: () => import("@/views/dashboard/index"),
        meta: { title: "Overview", icon: "dashboard", affix: true },
      },
    ],
  },

  {
    path: "/analysis",
    component: Layout,
    redirect: "/analysis/index",
    meta: { title: "Data Analysis", icon: "el-icon-s-data" },
    children: [
      {
        path: "topic",
        name: "Topic",
        component: () => import("@/views/analysis/Topic/index.vue"),
        meta: { title: "Java Topics" },
      },
      {
        path: "engagement",
        name: "Engagement",
        component: () => import("@/views/analysis/Engagement/index.vue"),
        meta: { title: "User Engagement" },
      },
      {
        path: "mistake",
        name: "Mistake",
        component: () => import("@/views/analysis/Mistake/index.vue"),
        meta: { title: "Common Mistakes" },
      },
      {
        path: "answer-quality",
        name: "AnswerQuality",
        component: () => import("@/views/analysis/AnswerQuality/index"),
        meta: { title: "Answer Quality" },
        children: [
          {
            path: "elapsed-time",
            name: "ElapsedTime",
            component: () =>
              import("@/views/analysis/AnswerQuality/ElapsedTime/index"),
            meta: { title: "Elapsed Time" },
          },
          {
            path: "user-reputation",
            name: "UserReputation",
            component: () =>
              import("@/views/analysis/AnswerQuality/UserReputation/index"),
            meta: { title: "User Reputation" },
          },
          {
            path: "answer-length",
            name: "AnswerLength",
            component: () =>
              import("@/views/analysis/AnswerQuality/AnswerLength/index"),
            meta: { title: "Answer Length" },
          },
        ],
      },
    ],
  },

  {
    path: "/restful",
    component: Layout,
    redirect: "/restful/index",
    meta: { title: "RESTful API", icon: "el-icon-s-operation" },
    children: [
      {
        path: 'external-link',
        children: [
          {
            path: `${process.env.VUE_APP_BASE_API}/doc.html`,
            meta: { title: 'Documentation', icon: 'documentation' }
          }
        ]
      },
      {
        path: "toptopicfrequency", // 子路由路径
        name: "TopTopicFrequency",
        component: () => import("@/views/restful/TopTopicFrequencyPage"), // 新页面组件
        meta: { title: "Top Topic Frequency" },
      },
      {
        path: "topbugfrequency", // 子路由路径
        name: "TopBugFrequency",
        component: () => import("@/views/restful/TopBugFrequencyPage"), // 新页面组件
        meta: { title: "Top Bug Frequency" },
      },
      {
        path: "specifictopicfrequency", // 子路由路径
        name: "SpecificTopicFrequency",
        component: () => import("@/views/restful/SpecificTopicFrequency"), // 新页面组件
        meta: { title: "Specific Topic Frequency" },
      },
      {
        path: "specificbugfrequency", // 子路由路径
        name: "SpecificBugFrequency",
        component: () => import("@/views/restful/SpecificBugFrequency"), // 新页面组件
        meta: { title: "Specific Bug Frequency" },
      },
    ],
  },
  {
    path: "external-link",
    component: Layout,
    children: [
      {
        path: "https://github.com/GetOffENT/StackOverflow-Java-Analysis",
        meta: { title: "External Link", icon: "link" },
      },
    ],
  },

  // 404 page must be placed at the end !!!
  { path: "*", redirect: "/404", hidden: true },
];

const createRouter = () =>
  new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  });

const router = createRouter();

export function resetRouter() {
  const newRouter = createRouter();
  router.matcher = newRouter.matcher; // reset router
}

export default router;
