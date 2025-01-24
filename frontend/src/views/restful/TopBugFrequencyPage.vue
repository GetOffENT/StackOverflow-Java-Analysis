<template>
  <div class="flex-container">
    <h1 class="page-title">Top Bug Frequency</h1>

    <!-- 输入框用于输入 top N 值 -->
    <div class="input-group">
      <label for="topN">Top N:</label>
      <input
        v-model.number="topN"
        type="number"
        placeholder="Enter top N"
        class="input-field"
      />
    </div>

    <!-- 输入框用于输入起始和结束时间 -->
    <div class="input-group">
      <label for="startDate">Start Date and Time:</label>
      <input v-model="startDate" type="datetime-local" class="input-field" />
      <label for="endDate">End Date and Time:</label>
      <input v-model="endDate" type="datetime-local" class="input-field" />
    </div>

    <!-- 输入框用于输入 mixed 参数 -->
    <div class="input-group">
      <label for="mixed">Mixed:</label>
      <input v-model="mixed" type="checkbox" class="input-field" />
    </div>

    <!-- 按钮 -->
    <div class="button-group">
      <button @click="getTopBugs" class="btn btn-primary">Run</button>
    </div>

    <!-- 显示实时更改的 URL -->
    <div class="url-display">
      <a :href="topBugsUrl" target="_blank" class="url-link">{{
        topBugsUrl
      }}</a>
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
      topN: 10,
      startDate: "",
      endDate: "",
      mixed: false,
      BASE_URL: "",
    };
  },
  computed: {
    // 计算属性，动态生成 URL
    topBugsUrl() {
      const params = [];
      const startTime = this.startDate
        ? `start=${this.formatDateTime(this.startDate)}`
        : "";
      const endTime = this.endDate
        ? `end=${this.formatDateTime(this.endDate)}`
        : "";
      const mixedParam = this.mixed ? "mixed=true" : "";
      if (this.topN) params.push(`n=${this.topN}`);
      if (startTime) params.push(startTime);
      if (endTime) params.push(endTime);
      if (mixedParam) params.push(mixedParam);
      return `${this.BASE_URL}/analysis/error-and-exception/top${
        params.length ? `?${params.join("&")}` : ""
      }`;
    },
  },
  created() {
    this.BASE_URL = process.env.VUE_APP_BASE_API;
  },
  methods: {
    async getTopBugs() {
      try {
        // 使用用户输入的 topN 值、日期时间范围和 mixed 参数
        const response = await request.get(this.topBugsUrl);
        this.topicResult = response.data;
      } catch (error) {
        console.error("Error fetching top bugs:", error);
      }
    },
    // 将日期时间字符串格式化为后端期望的格式
    formatDateTime(dateTime) {
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = ("0" + (date.getMonth() + 1)).slice(-2);
      const day = ("0" + date.getDate()).slice(-2);
      const hours = ("0" + date.getHours()).slice(-2);
      const minutes = ("0" + date.getMinutes()).slice(-2);
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
  },
};
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
