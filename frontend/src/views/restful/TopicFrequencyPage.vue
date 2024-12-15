<template>
  <div>
    <h1>Topic Frequency</h1>
    <input v-model="topicQuery" placeholder="Enter topic" />
    <button @click="getTopicFrequency">Query Topic Frequency</button>
    <button @click="getTopTopics">Query Top Topics</button>
    <div v-if="topicResult">
      <pre>{{ topicResult }}</pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      topicQuery: '',
      topicResult: null,
    };
  },
  methods: {
    async getTopicFrequency() {
      try {
        const response = await axios.get('http://localhost:8080/api/topic-frequency', {
          params: { topic: this.topicQuery },
        });
        this.topicResult = response.data;
      } catch (error) {
        console.error('Error fetching topic frequency:', error);
      }
    },
    async getTopTopics() {
      try {
        const response = await axios.get('http://localhost:8080/api/topic-frequency', {
          params: { top: 5 },
        });
        this.topicResult = response.data;
      } catch (error) {
        console.error('Error fetching top topics:', error);
      }
    },
  },
};
</script>
