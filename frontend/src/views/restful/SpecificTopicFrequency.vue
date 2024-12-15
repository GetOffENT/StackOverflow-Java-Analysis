<template>
  <div class="flex-container">
    <h1>Specific Topic Frequency</h1>
    <!-- 新增：输入框用于输入 top N 值 -->
    <div class="input-group">
      <label for="tagName">tagName</label>
      <input v-model="tagName" type="text" placeholder="Enter tag name">
    </div>
    <!-- 单独的按钮 -->
    <div class="button-group">
      <button @click="getSpecificTopic">run</button>
    </div>
    <!-- 新增：显示实时更改的 URL 并且可以点击跳转 -->
    <div class="url-display">
      <a :href="topicUrl" target="_blank">{{ topicUrl }}</a>
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
      tagName: null
    }
  },
  computed: {
    // 计算属性，动态生成 URL
    topicUrl() {
      const params = [];
      if (this.tagName) params.push(`tagName=${this.tagName}`);
      return `http://localhost:8080/analysis/topic${params ? `?${params.join('&')}` : ''}`;
    }
  },
  methods: {
    async getSpecificTopic() {
      try {
        // 使用用户输入的 topN 值和日期时间范围
        const response = await axios.get(`http://localhost:8080/analysis/topic`, {
          params: {
            tagName: this.tagName
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
