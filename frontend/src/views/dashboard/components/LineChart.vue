<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
require("echarts/theme/macarons"); // echarts theme

export default {
  props: {
    className: {
      type: String,
      default: "chart",
    },
    width: {
      type: String,
      default: "100%",
    },
    height: {
      type: String,
      default: "350px",
    },
    autoResize: {
      type: Boolean,
      default: true,
    },
    chartData: {
      type: Array,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      chart: null,
      resizeTimeout: null,
    };
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.initChart();
      },
    },
    loading(val) {
      console.log("loading", val);
      if (!this.chart) {
        this.chart = echarts.init(this.$el, "macarons");
        return;
      }
      if (val) {
        this.chart.showLoading({
          text: "Loading...",
          color: "#5470C6",
          textColor: "#000",
          maskColor: "rgba(255, 255, 255, 0.8)",
          zlevel: 0,
        });
      } else {
        this.chart.hideLoading();
      }
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
    });
    window.addEventListener("resize", this.debounceResize);
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
    window.removeEventListener("resize", this.debounceResize);
  },
  methods: {
    handleResize() {
      if (this.chart) {
        this.initChart();
      }
    },
    debounceResize() {
      clearTimeout(this.resizeTimeout);
      this.resizeTimeout = setTimeout(() => {
        this.handleResize();
      }, 500);
    },
    initChart() {
      if (this.chart) {
        this.chart.dispose();
      }
      this.chart = echarts.init(this.$el, "macarons");
      if (this.loading) {
        this.chart.showLoading({
          text: "Loading...",
          color: "#5470C6",
          textColor: "#000",
          maskColor: "rgba(255, 255, 255, 0.8)",
          zlevel: 0,
        });
      }
      this.setOptions(this.chartData);
    },
    setOptions(data) {
      const timeData = data.map((item) => item.time);
      const questionCountData = data.map((item) => item.questionCount);
      const answerCountData = data.map((item) => item.answerCount);
      const commentCountData = data.map((item) => item.commentCount);

      this.chart.setOption({
        xAxis: {
          data: timeData, // 使用时间数据
          boundaryGap: false,
          axisTick: {
            show: false,
          },
        },
        grid: {
          left: 10,
          right: 20,
          bottom: 20,
          top: 30,
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
          },
          padding: [5, 10],
        },
        yAxis: {
          axisTick: {
            show: false,
          },
        },
        legend: {
          data: ["Question Count", "Answer Count", "Comment Count"],
        },
        series: [
          {
            name: "Question Count",
            itemStyle: { color: "#FF005A" },
            lineStyle: {
              color: "#FF005A",
              width: 2,
            },
            smooth: true,
            type: "line",
            data: questionCountData,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
          {
            name: "Answer Count",
            smooth: true,
            type: "line",
            itemStyle: { color: "#3888fa" },
            lineStyle: {
              color: "#3888fa",
              width: 2,
            },
            data: answerCountData,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
          {
            name: "Comment Count",
            smooth: true,
            type: "line",
            itemStyle: { color: "#FF9F00" },
            lineStyle: {
              color: "#FF9F00",
              width: 2,
            },
            data: commentCountData,
            animationDuration: 2800,
            animationEasing: "quadraticOut",
          },
        ],
      });
    },
  },
};
</script>
