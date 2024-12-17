<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { getDateRange } from './api/analysis';
export default {
  name: 'App',
  async created() {
    const res = await getDateRange();
    this.$store.dispatch('dateRange/setDateRange', res.data);
  },
}
</script>
