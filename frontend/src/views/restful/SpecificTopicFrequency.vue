<template>
  <div class="flex-container">
    <h1 class="page-title">Specific Topic Frequency</h1>
    
    <!-- 新增：输入框用于输入 tagName -->
    <div class="input-group">
      <label for="tagName">Tag Name:</label>
      <input v-model="tagName" type="text" placeholder="Enter tag name" class="input-field">
    </div>

    <!-- 单独的按钮 -->
    <div class="button-group">
      <button @click="getSpecificTopic" class="btn btn-primary">Run</button>
    </div>

    <!-- 新增：显示实时更改的 URL 并且可以点击跳转 -->
    <div class="url-display">
      <a :href="topicUrl" target="_blank" class="url-link">{{ topicUrl }}</a>
    </div>

    <div v-if="topicResult" class="result-display">
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
      return `http://localhost:8080/analysis/topic${params.length ? `?${params.join('&')}` : ''}`;
    }
  },
  methods: {
    async getSpecificTopic() {
      if(!this.tagName) {
        this.$message.error('Please enter a tag name')
        return
      }
      try {
        // 使用用户输入的 tagName
        const response = await axios.get(`http://localhost:8080/analysis/topic`, {
          params: {
            tagName: this.tagName
          }
        })
        this.topicResult = response.data
      } catch (error) {
        console.error('Error fetching specific topic:', error)
      }
    }
  }
}
</script>

<style scoped>
/* 整体容器 */
.flex-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 页面标题 */
.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

/* 输入框组 */
.input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  width: 100%;
}

.input-group label {
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
}

.input-field {
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  outline: none;
  width: 100%;
  box-sizing: border-box;
}

.input-field:focus {
  border-color: #4caf50;
}

/* 按钮组 */
.button-group {
  margin-bottom: 30px;
}

.btn {
  padding: 12px 25px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary {
  background-color: #4caf50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

/* URL 显示 */
.url-display {
  margin-bottom: 20px;
}

.url-link {
  font-size: 14px;
  color: #1e88e5;
  text-decoration: none;
  word-wrap: break-word;
}

.url-link:hover {
  text-decoration: underline;
}

/* 结果显示 */
.result-display {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
}

.result-display pre {
  font-size: 14px;
  color: #333;
  overflow: auto;
}
</style>
