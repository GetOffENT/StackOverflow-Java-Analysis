<template>
  <div class="flex-container">
    <h1 class="page-title">Specific Bug Frequency</h1>

    <!-- 输入框用于输入 bug 名称 -->
    <div class="input-group">
      <label for="tagName">Bug Name:</label>
      <input
        v-model="bugName"
        type="text"
        placeholder="Enter bug name"
        class="input-field"
      />
    </div>

    <!-- 按钮 -->
    <div class="button-group">
      <button @click="getSpecificBug" class="btn btn-primary">Run</button>
    </div>

    <!-- 显示实时更改的 URL -->
    <div class="url-display">
      <a :href="bugUrl" target="_blank" class="url-link">{{ bugUrl }}</a>
    </div>

    <!-- 显示结果 -->
    <div v-if="topicResult" class="result-display">
      <pre>{{ topicResult }}</pre>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  data() {
    return {
      topicQuery: "",
      topicResult: null,
      bugName: null,
      BASE_URL: "",
    };
  },
  computed: {
    // 计算属性，动态生成 URL
    bugUrl() {
      const params = [];
      if (this.bugName) params.push(`bugName=${this.bugName}`);
      return `${this.BASE_URL}/analysis/error-and-exception${
        params.length > 0 ? `?${params.join("&")}` : ""
      }`;
    },
  },
  created() {
    this.BASE_URL = process.env.VUE_APP_BASE_API;
  },
  methods: {
    async getSpecificBug() {
      if (!this.bugName) {
        this.$message.error("Please enter a bug name");
        return;
      }
      try {
        // 使用用户输入的 bugName
        const response = await request.get(this.bugUrl);
        this.topicResult = response.data;
      } catch (error) {
        console.error("Error fetching specific bug data:", error);
      }
    },
  },
};
</script>

<style>
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
