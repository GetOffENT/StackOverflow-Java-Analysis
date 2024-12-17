<template>
  <div class="chart-container">
    <h1 style="font-size: 26px;">Top N Topics with the Most User Engagement from High-Reputation Users</h1>
    <el-form :inline="true" class="form" style="justify-self: center">
      <el-form-item label="top N" style="margin-right: 30px">
        <el-input-number
          v-model="topN"
          :min="2"
          :max="100"
          @change="debounceCountChange"
        />
      </el-form-item>
      <el-form-item label="Top X% of reputation" class="my-el-input-number">
        <el-input-number
          v-model="percentage"
          :min="0"
          :max="100"
          @change="debouncePercentageChange"
        />
      </el-form-item>
      <el-form-item class="weight-form-item">
        <label class="weight-label">weight</label>
        <span class="weight-hint">Question Weight: {{ questionWeight }}%</span>
        <span class="weight-hint">Answer Weight: {{ answerWeight }}%</span>
        <span class="weight-hint">Comment Weight: {{ commentWeight }}%</span>
        <vue-slider
          v-model="weight"
          :process="process"
          :min-range="1"
          :width="'40vw'"
          :dot-options="dotOptions"
          :tooltip="'none'"
          :lazy="true"
          @change="handleWeightChange"
        />
      </el-form-item>
    </el-form>

    <div ref="chart" class="chart"></div>
    <TimeLine :width="'70vw'" @update:range="handleTimeRangeUpdate" />
  </div>
</template>

<script>
import VueSlider from "vue-slider-component";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";
import * as echarts from "echarts";
import { getTopNTopicsByEngagementOfUserWithHigherReputation } from "@/api/analysis";
require("echarts/theme/macarons");

export default {
  name: "Engagement",
  data() {
    return {
      chartData: [],
      startDate: null,
      endDate: null,
      barChart: null,
      weight: [0, 33, 66, 100],
      dotOptions: [
        { disabled: true },
        { disabled: false },
        { disabled: false },
        { disabled: true },
      ],
      questionWeight: 33,
      answerWeight: 33,
      commentWeight: 34,
      topN: 10,
      percentage: 30,
      resizeTimeout: null,
      countTimeout: null,
      percentageTimeout: null,
    };
  },
  components: {
    TimeLine,
    VueSlider,
  },
  methods: {
    debounceCountChange() {
      clearTimeout(this.countTimeout);
      this.countTimeout = setTimeout(() => {
        this.initChart(false);
      }, 200);
    },
    debouncePercentageChange() {
      clearTimeout(this.percentageTimeout);
      this.percentageTimeout = setTimeout(() => {
        this.initChart(false);
      }, 200);
    },
    handleWeightChange() {
      this.initChart(false);
    },
    process(dotsPos) {
      this.questionWeight = (dotsPos[1] - dotsPos[0]).toFixed(0);
      this.answerWeight = (dotsPos[2] - dotsPos[1]).toFixed(0);
      this.commentWeight = (dotsPos[3] - dotsPos[2]).toFixed(0);
      return [
        [dotsPos[0], dotsPos[1], { backgroundColor: "#2ec7c9" }],
        [dotsPos[1], dotsPos[2], { backgroundColor: "#b6a2de" }],
        [dotsPos[2], dotsPos[3], { backgroundColor: "#5ab1ef" }],
      ];
    },
    handleTimeRangeUpdate(dateRange) {
      this.startDate = dateRange["start"];
      this.endDate = dateRange["end"];
      this.initChart(false);
    },
    async fetchChartData() {
      const start = this.startDate
        ? dayjs(this.startDate).format("YYYY-MM-DD HH:mm")
        : null;
      const end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;

      const params = {
        start,
        end,
        percentage: this.percentage / 100,
        questionWeight: this.questionWeight,
        answerWeight: this.answerWeight,
        commentWeight: this.commentWeight,
        n: this.topN,
      };
      const res = await getTopNTopicsByEngagementOfUserWithHigherReputation(
        params
      );
      this.chartData = res.data;
    },
    async initChart(resize = false) {
      if (!resize) {
        await this.fetchChartData();
      }

      const chartWidth = this.chartData.length * 150;
      this.$refs.chart.style.width = `${chartWidth}px`;
      if (this.topN >= 5) {
        const chartHeight = chartWidth / 1.6;
        this.$refs.chart.style.height = `${chartHeight}px`;
      } else {
        this.$refs.chart.style.height = "300px";
      }

      if (this.barChart) {
        this.barChart.dispose();
      }

      this.barChart = echarts.init(this.$refs.chart, "macarons");
      const chartOption = {
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "shadow" },
        },
        legend: {
          top: '0',
          data: ["Question Score", "Answer Score", "Comment Score"],
          orient: 'horizontal',
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: this.chartData.map((item) => item.tagName),
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: "Question Score",
            type: "bar",
            stack: "Total Score",
            barWidth: "60%",
            data: this.chartData.map((item) => item.questionScore.toFixed(2)),
          },
          {
            name: "Answer Score",
            type: "bar",
            stack: "Total Score",
            barWidth: "60%",
            data: this.chartData.map((item) => item.answerScore.toFixed(2)),
          },
          {
            name: "Comment Score",
            type: "bar",
            stack: "Total Score",
            barWidth: "60%",
            data: this.chartData.map((item) => item.commentScore.toFixed(2)),
          },
        ],
      };

      this.barChart.setOption(chartOption);
    },
    handleResize() {
      this.initChart(true);
    },
    debounceResize() {
      clearTimeout(this.resizeTimeout);
      this.resizeTimeout = setTimeout(() => {
        this.handleResize();
      }, 500);
    },
  },
  mounted() {
    this.initChart(false);
    window.addEventListener("resize", this.debounceResize);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.debounceResize);
  },
};
</script>

<style scoped>
.chart-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  max-height: 90vh;
}

.my-el-input-number {
  position: relative;
}
.my-el-input-number::after {
  content: "%";
  display: inline-block;
  height: 20px;
  line-height: 20px;
  width: 20px;
  text-align: center;
  position: absolute;
  right: 62px;
  top: 50%;
  transform: translateY(-50%);
}

.weight-form-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.weight-label {
  white-space: nowrap;
  color: #606266;
  font-weight: 600;
}

.weight-hint {
  margin-left: 40px;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
}

.vue-slider {
  flex: 1;
}

.chart {
  width: 100%;
  height: 100%;
  max-width: 100%;
  max-height: 75vh;
}
</style>
