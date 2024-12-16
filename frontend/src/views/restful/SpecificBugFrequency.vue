<template>
  <div class="flex-container">
    <h1>Specific Bug Frequency</h1>
    <div class="input-group">
      <label for="tagName">tagName</label>
      <input v-model="bugName" type="text" placeholder="Enter bug name">
    </div>
    <div class="button-group">
      <button @click="getSpecificBug">run</button>
    </div>
    <!-- 新增：显示实时更改的 URL 并且可以点击跳转 -->
    <div class="url-display">
      <a :href="bugUrl" target="_blank">{{ bugUrl }}</a>
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
      bugName: null
    }
  },
  computed: {
    // 计算属性，动态生成 URL
    bugUrl() {
      const params = [];
      if (this.bugName) params.push(`bugName=${this.bugName}`);
      return `http://localhost:8080/analysis/error-and-exception${params ? `?${params.join('&')}` : ''}`;
    }
  },
  methods: {
    async getSpecificBug() {
      try {
        // 使用用户输入的 topN 值和日期时间范围
        const response = await axios.get(`http://localhost:8080/analysis/error-and-exception`, {
          params: {
            bugName: this.bugName
          }
        })
        this.topicResult = response.data
      } catch (error) {
        console.error('Error fetching top topics:', error)
      }
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
