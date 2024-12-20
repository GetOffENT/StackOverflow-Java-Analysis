<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import echarts from "echarts";
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
        this.setOptions(val);
      },
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
        this.chart.dispose();
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
      this.chart = echarts.init(this.$el, "macarons");
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
            itemStyle: {
              normal: {
                color: "#FF005A",
                lineStyle: {
                  color: "#FF005A",
                  width: 2,
                },
              },
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
            itemStyle: {
              normal: {
                color: "#3888fa",
                lineStyle: {
                  color: "#3888fa",
                  width: 2,
                },
              },
            },
            data: answerCountData,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
          {
            name: "Comment Count",
            smooth: true,
            type: "line",
            itemStyle: {
              normal: {
                color: "#FF9F00",
                lineStyle: {
                  color: "#FF9F00",
                  width: 2,
                },
              },
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
