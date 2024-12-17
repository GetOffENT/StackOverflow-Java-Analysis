const state = {
  start: "",
  end: "",
};

const mutations = {
  SET_DATE_RANGE: (state, { start, end }) => {
    state.start = start;
    state.end = end;
  },
};

const actions = {
  setDateRange({ commit }, data) {
    commit("SET_DATE_RANGE", data);
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
