<template>
  <div>
    <h1>Bug Frequency</h1>
    <input v-model="bugQuery" placeholder="Enter error" />
    <button @click="getBugFrequency">Query Bug Frequency</button>
    <button @click="getTopBugs">Query Top Bugs</button>
    <div v-if="bugResult">
      <pre>{{ bugResult }}</pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      bugQuery: '',
      bugResult: null,
    };
  },
  methods: {
    async getBugFrequency() {
      try {
        const response = await axios.get('http://localhost:8080/api/bug-frequency', {
          params: { error: this.bugQuery },
        });
        this.bugResult = response.data;
      } catch (error) {
        console.error('Error fetching bug frequency:', error);
      }
    },
    async getTopBugs() {
      try {
        const response = await axios.get('http://localhost:8080/api/bug-frequency', {
          params: { top: 5 },
        });
        this.bugResult = response.data;
      } catch (error) {
        console.error('Error fetching top bugs:', error);
      }
    },
  },
};
</script>
