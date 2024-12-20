<template>
  <div>
    <h3 style="justify-self: center; color: #606266">{{ this.title }}</h3>
    <div class="chart-container">
      <div class="chart-left-contaniner">
        <div ref="barChart" class="bar-chart" :style="{ height: height }"></div>
      </div>
      <div class="chart-right-contaniner">
        <div ref="pieChart" class="pie-chart" :style="{ height: height }"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
require("echarts/theme/macarons");

export default {
  name: "MistakeOne",
  props: {
    title: {
      type: String,
      required: true,
    },
    type: {
      type: String,
      required: true,
    },
    chartData: {
      type: Array,
      required: true,
    },
    height: {
      type: String,
      default: "500px",
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    chartData: {
      handler() {
        this.initChart();
      },
      deep: true,
    },
    loading: {
      handler(val) {
        if (val) {
          this.barChart.showLoading({
            text: "Loading...",
            color: "#5470C6",
            textColor: "#000",
            maskColor: "rgba(255, 255, 255, 0.8)",
            zlevel: 0,
          });
          this.pieChart.showLoading({
            text: "Loading...",
            color: "#5470C6",
            textColor: "#000",
            maskColor: "rgba(255, 255, 255, 0.8)",
            zlevel: 0,
          });
        } else {
          this.barChart.hideLoading();
          this.pieChart.hideLoading();
        }
      },
    },
  },
  data() {
    return {
      barChart: null,
      pieChart: null,
      resizeTimeout: null,
    };
  },
  mounted() {
    this.initChart();
    window.addEventListener("resize", this.debounceResize);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.debounceResize);
  },
  methods: {
    handleResize() {
      this.initChart();
    },
    debounceResize() {
      clearTimeout(this.resizeTimeout);
      this.resizeTimeout = setTimeout(() => {
        this.handleResize();
      }, 500);
    },
    async initChart() {
      // Bar Chart
      if (this.chartData.length === 0) {
        this.barChart = echarts.init(this.$refs.barChart, "macarons");
        this.barChart.showLoading({
          text: "Loading...",
          color: "#5470C6",
          textColor: "#000",
          maskColor: "rgba(255, 255, 255, 0.8)",
          zlevel: 0,
        });
        this.pieChart = echarts.init(this.$refs.pieChart, "macarons");
        this.pieChart.showLoading({
          text: "Loading...",
          color: "#5470C6",
          textColor: "#000",
          maskColor: "rgba(255, 255, 255, 0.8)",
          zlevel: 0,
        });
        return;
      }

      if (this.barChart) {
        this.barChart.dispose();
      }

      this.barChart = echarts.init(this.$refs.barChart, "macarons");
      const chartOption = {
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "shadow" },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: this.chartData.map((item) => item.name),
        },
        yAxis: {
          type: "value",
          data: this.chartData.map((item) => item.count),
        },
        series: [
          {
            name: "Count",
            type: "bar",
            stack: "Total Score",
            barWidth: "60%",
            data: this.chartData.map((item) => item.count),
          },
        ],
        animationDuration: 2600,
      };

      this.barChart.setOption(chartOption);

      // Pie Chart
      let pieData = JSON.parse(JSON.stringify(this.chartData));
      pieData.push({
        name: "Others",
        percentage: 100 - pieData.reduce((acc, cur) => acc + cur.percentage, 0),
      });

      if (this.pieChart) {
        this.pieChart.dispose();
      }
      this.pieChart = echarts.init(this.$refs.pieChart, "macarons");

      this.pieChart.setOption({
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {d}%",
        },
        legend: {
          left: "center",
          bottom: "10",
          data: pieData.map((item) => item.name),
        },
        series: [
          {
            name: `Top ${this.chartData.length} frequently discussed ${this.type}`,
            type: "pie",
            roseType: "radius",
            radius: [15, 95],
            center: ["50%", "38%"],
            data: [
              ...pieData.map((item) => ({
                value: item.percentage,
                name: item.name,
              })),
            ],
            animationEasing: "cubicInOut",
            animationDuration: 2600,
          },
        ],
      });
    },
  },
};
</script>

<style scoped>
.chart-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.chart-left-contaniner,
.chart-right-contaniner {
  flex: 1;
  margin: 0 10px;
}

.bar-chart,
.pie-chart {
  width: 100%;
  height: 100%;
}
</style>
