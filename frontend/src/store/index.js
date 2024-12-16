import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import tagsView from './modules/tagsView'
import user from './modules/user'
import dateRange from './modules/dateRange'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

const vuexLocalStorage = createPersistedState({
  key: "vuexLocalStorage",
  storage: window.localStorage,
  paths: ["dateRange"],
});

const store = new Vuex.Store({
  modules: {
    app,
    settings,
    tagsView,
    user,
    dateRange
  },
  getters,
  plugins: [vuexLocalStorage]
})

export default store
