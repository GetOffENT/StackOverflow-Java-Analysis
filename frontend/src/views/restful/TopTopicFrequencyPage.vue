<template>
  <div class="flex-container">
    <h1>Top Topic Frequency</h1>
    <!-- 新增：输入框用于输入 top N 值 -->
    <div class="input-group">
      <label for="topN">top N</label>
      <input v-model.number="topN" type="number" placeholder="Enter top N">
    </div>
    <!-- 单独的按钮 -->
    <!-- 新增：输入框用于输入起始和结束时间 -->
    <div class="input-group">
      <label for="startDate">Start Date and Time:</label>
      <input v-model="startDate" type="datetime-local" placeholder="Start Date and Time">
      <label for="endDate">End Date and Time:</label>
      <input v-model="endDate" type="datetime-local" placeholder="End Date and Time">
    </div>
    <div class="button-group">
      <button @click="getTopTopics">run</button>
    </div>
    <!-- 新增：显示实时更改的 URL 并且可以点击跳转 -->
    <div class="url-display">
      <a :href="topTopicsUrl" target="_blank">{{ topTopicsUrl }}</a>
    </div>
    <div v-if="topicResult">
      <pre>{{ topicResult }}</pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      topicQuery: '',
      topicResult: null,
      topN: 10, // 用于存储用户输入的 top N 值，默认值为 10
      startDate: '', // 用于存储用户输入的起始时间和日期
      endDate: '' // 用于存储用户输入的结束时间和日期
    }
  },
  computed: {
    // 计算属性，动态生成 URL
    topTopicsUrl() {
      const params = [];
      const startTime = this.startDate ? `start=${this.formatDateTime(this.startDate)}` : '';
      const endTime = this.endDate ? `end=${this.formatDateTime(this.endDate)}` : '';
      if (this.topN) params.push(`n=${this.topN}`);
      if (startTime) params.push(startTime);
      if (endTime) params.push(endTime);
      return `http://localhost:8080/analysis/topic/top${params.length ? `?${params.join('&')}` : ''}`;
    }
  },
  methods: {
    async getTopTopics() {
      try {
        // 使用用户输入的 topN 值和日期时间范围
        const response = await axios.get(`http://localhost:8080/analysis/topic/top`, {
          params: {
            n: this.topN,
            start: this.startDate,
            end: this.endDate
          }
        })
        this.topicResult = response.data
      } catch (error) {
        console.error('Error fetching top topics:', error)
      }
    },
    // 新增：将日期时间字符串格式化为后端期望的格式
    formatDateTime(dateTime) {
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = ('0' + (date.getMonth() + 1)).slice(-2)
      const day = ('0' + date.getDate()).slice(-2)
      const hours = ('0' + date.getHours()).slice(-2)
      const minutes = ('0' + date.getMinutes()).slice(-2)
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style>
.flex-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.input-group {
  margin-bottom: 25px; /* 增加间隔 */
}

.button-group {
  margin-bottom: 25px; /* 增加间隔 */
}

.url-display {
  margin-bottom: 16px; /* 增加间隔 */
}
</style>
