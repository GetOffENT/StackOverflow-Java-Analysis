<template>
  <div>
    <h1 style="justify-self: center; margin-bottom: 30px">
      Answer Quality and Its Relation to the elapsed time between question and
      answer creation
    </h1>
    <TimeLine
      :width="'70vw'"
      @update:range="handleTimeRangeUpdate"
      :disabled="disabled"
    />
    <div class="pie-chart-container">
      <div class="pie-chart-left-contaniner">
        <h3 style="justify-self: center; color: #606266">
          Accepted Answer with First Ratio
        </h3>
        <div
          ref="pieChart1"
          class="pie-chart"
          :style="{ height: height }"
        ></div>
      </div>
      <div class="pie-chart-right-contaniner">
        <h3 style="justify-self: center; color: #606266">
          First Answer with Accepted Ratio
        </h3>
        <div
          ref="pieChart2"
          class="pie-chart"
          :style="{ height: height }"
        ></div>
      </div>
    </div>
    <div class="bottom-chart-contaniner">
      <h3 style="justify-self: center; color: #606266">
        Scatter Plot of Answer Data: Upvotes vs Duration
      </h3>
      <div ref="scatterChart" class="scatter-chart" style="height: 800px"></div>
    </div>
  </div>
</template>

<script>
import {
  getFirstAnswersWithCreateDate,
  getAcceptedAnswersWithCreateDate,
  getAnswersWithCreateDate,
} from "@/api/analysis";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";
import * as echarts from "echarts";
require("echarts/theme/macarons");

export default {
  name: "ElapsedTime",
  data() {
    return {
      startDate: null,
      endDate: null,
      start: null,
      end: null,
      acceptedAnswerData: [],
      displayedAcceptedAnswerData: [],
      pieAcceptedAnswerData: [],
      firstAnswerData: [],
      displayedFirstAnswerData: [],
      pieFirstAnswerData: [],
      allAnswerData: [],
      displayedAllAnswerData: [],
      height: "300px",
      pieChart1: null,
      pieChart2: null,
      scatterChart: null,
      resizeTimeout: null,
      loadingAcceptedAnswer: false,
      loadingFirstAnswer: false,
      loadingAllAnswer: false,
    };
  },
  watch: {
    startDate(val) {
      this.start = val ? dayjs(val).format("YYYY-MM-DD HH:mm") : null;
      this.fetchData();
    },
    endDate() {
      this.end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;
      this.fetchData();
    },
    displayedAcceptedAnswerData: {
      handler(val) {
        let temp = val.reduce(
          (acc, item) => {
            if (item.first) {
              acc.first += 1;
            } else {
              acc.notFirst += 1;
            }
            return acc;
          },
          { first: 0, notFirst: 0 }
        );
        this.pieAcceptedAnswerData = [
          { name: "First Answer", value: temp.first },
          { name: "Not First", value: temp.notFirst },
        ];
        this.initPieChart1();
      },
      deep: true,
    },
    displayedFirstAnswerData: {
      handler(val) {
        let temp = val.reduce(
          (acc, item) => {
            if (item.isAccepted) {
              acc.accepted += 1;
            } else {
              acc.notAccepted += 1;
            }
            return acc;
          },
          { accepted: 0, notAccepted: 0 }
        );
        this.pieFirstAnswerData = [
          { name: "Accepted Answer", value: temp.accepted },
          { name: "Not Accepted", value: temp.notAccepted },
        ];
        this.initPieChart2();
      },
      deep: true,
    },
    displayedAllAnswerData: {
      handler() {
        this.initScatterChart();
      },
      deep: true,
    },
  },
  components: {
    TimeLine,
  },
  computed: {
    disabled() {
      return (
        this.loadingAcceptedAnswer ||
        this.loadingFirstAnswer ||
        this.loadingAllAnswer
      );
    },
  },
  mounted() {
    this.fetchData();
    window.addEventListener("resize", this.debounceResize);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.debounceResize);
  },
  methods: {
    handleResize() {
      this.initPieChart1();
      this.initPieChart2();
      this.initScatterChart();
    },
    debounceResize() {
      clearTimeout(this.resizeTimeout);
      this.resizeTimeout = setTimeout(() => {
        this.handleResize();
      }, 500);
    },
    handleTimeRangeUpdate(dateRange) {
      this.startDate = dateRange["start"];
      this.endDate = dateRange["end"];
    },
    initPieChart1() {
      if (this.pieChart1) {
        this.pieChart1.dispose();
      }
      this.pieChart1 = echarts.init(this.$refs.pieChart1, "macarons");

      this.pieChart1.setOption({
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)",
        },
        legend: {
          left: "center",
          bottom: "10",
          data: this.pieAcceptedAnswerData.map((item) => item.name),
        },
        series: [
          {
            name: `Accepted Answer with First Ratio`,
            type: "pie",
            roseType: "radius",
            radius: [15, 95],
            center: ["50%", "38%"],
            data: [
              ...this.pieAcceptedAnswerData.map((item) => ({
                value: item.value,
                name: item.name,
              })),
            ],
            animationEasing: "cubicInOut",
            animationDuration: 2600,
          },
        ],
      });
    },
    initPieChart2() {
      if (this.pieChart2) {
        this.pieChart2.dispose();
      }
      this.pieChart2 = echarts.init(this.$refs.pieChart2, "macarons");

      this.pieChart2.setOption({
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)",
        },
        legend: {
          left: "center",
          bottom: "10",
          data: this.pieFirstAnswerData.map((item) => item.name),
        },
        series: [
          {
            name: `First Answer with Accepted Ratio`,
            type: "pie",
            roseType: "radius",
            radius: [15, 95],
            center: ["50%", "38%"],
            data: [
              ...this.pieFirstAnswerData.map((item) => ({
                value: item.value,
                name: item.name,
              })),
            ],
            animationEasing: "cubicInOut",
            animationDuration: 2600,
          },
        ],
      });
    },
    initScatterChart() {
      if (this.scatterChart) {
        this.scatterChart.dispose();
      }
      this.scatterChart = echarts.init(this.$refs.scatterChart, "macarons");

      const toDays = (ms) => Math.ceil(ms / (1000 * 60 * 60 * 24));

      this.scatterChart.setOption({
        tooltip: {
          trigger: "item",
          formatter: function (params) {
            const data = params.data;
            return `
              Duration: ${toDays(data.duration)} day(s) <br/>
              Upvotes: ${data.upVoteCount} <br/>
              Downvotes: ${data.downVoteCount} <br/>
              Accepted: ${data.isAccepted} <br/>
              First Answer: ${data.first}
            `;
          },
        },
        legend: {
          data: [
            "Accepted & Not First",
            "First & Not Accepted",
            "Accepted & First",
            "Not Accepted & Not First",
          ],
          top: "top",
          left: "center",
        },
        xAxis: {
          type: "log", // 对数坐标轴
          name: "Duration (days)",
          scale: true,
          min: 1,
        },
        yAxis: {
          type: "log",
          name: "Upvote Count",
          min: 1,
        },
        dataZoom: [
          {
            type: "inside",
            xAxisIndex: [0],
            yAxisIndex: [0],
            start: 0,
            end: 100,
          },
        ],
        series: [
          {
            name: "Accepted & First",
            type: "scatter",
            data: this.displayedAllAnswerData
              .filter((item) => item.isAccepted && item.first)
              .map((item) => ({
                value: [toDays(item.duration), item.upVoteCount],
                itemStyle: { color: "#2ec7c9" },
                symbolSize: 7,
                ...item,
              })),
          },
          {
            name: "Accepted & Not First",
            type: "scatter",
            data: this.displayedAllAnswerData
              .filter((item) => item.isAccepted && !item.first)
              .map((item) => ({
                value: [toDays(item.duration), item.upVoteCount],
                itemStyle: { color: "#b6a2de" },
                symbolSize: 7,
                ...item,
              })),
          },
          {
            name: "First & Not Accepted",
            type: "scatter",
            data: this.displayedAllAnswerData
              .filter((item) => !item.isAccepted && item.first)
              .map((item) => ({
                value: [toDays(item.duration), item.upVoteCount],
                itemStyle: { color: "#5ab1ef" },
                symbolSize: 7,
                ...item,
              })),
          },
          {
            name: "Not Accepted & Not First",
            type: "scatter",
            data: this.displayedAllAnswerData
              .filter((item) => !item.isAccepted && !item.first)
              .map((item) => ({
                value: [toDays(item.duration), item.upVoteCount],
                itemStyle: { color: "#ffb980" },
                symbolSize: 7,
                ...item,
              })),
          },
        ],
      });
    },
    showLoading(chart) {
      chart.showLoading({
        text: "Loading...",
        color: "#5470C6",
        textColor: "#000",
        maskColor: "rgba(255, 255, 255, 0.8)",
        zlevel: 0,
      });
    },
    fetchData() {
      this.fetchAcceptedAnswers();
      this.fetchFirstAnswers();
      this.fetchAllAnswers();
    },
    async fetchAcceptedAnswers() {
      if (!this.pieChart1) {
        this.pieChart1 = echarts.init(this.$refs.pieChart1, "macarons");
      }
      this.showLoading(this.pieChart1);
      this.loadingAcceptedAnswer = true;

      if (!this.acceptedAnswerData.length) {
        const params = {
          start: this.start,
          end: this.end,
        };

        const res = await getAcceptedAnswersWithCreateDate(params);
        this.acceptedAnswerData = res.data;
      }
      if (this.start && this.end) {
        this.displayedAcceptedAnswerData = this.acceptedAnswerData.filter(
          (item) =>
            dayjs(item.answerCreateDate).isAfter(this.start) &&
            dayjs(item.answerCreateDate).isBefore(this.end)
        );
      } else {
        this.displayedAcceptedAnswerData = JSON.parse(
          JSON.stringify(this.acceptedAnswerData)
        );
      }

      this.loadingAcceptedAnswer = false;
      this.pieChart1.hideLoading();
    },
    async fetchFirstAnswers() {
      if (!this.pieChart2) {
        this.pieChart2 = echarts.init(this.$refs.pieChart2, "macarons");
      }
      this.showLoading(this.pieChart2);
      this.loadingFirstAnswer = true;

      if (!this.firstAnswerData.length) {
        const params = {
          start: this.start,
          end: this.end,
        };

        const res = await getFirstAnswersWithCreateDate(params);
        this.firstAnswerData = res.data;
      }
      if (this.start && this.end) {
        this.displayedFirstAnswerData = this.firstAnswerData.filter(
          (item) =>
            dayjs(item.answerCreateDate).isAfter(this.start) &&
            dayjs(item.answerCreateDate).isBefore(this.end)
        );
      } else {
        this.displayedFirstAnswerData = JSON.parse(
          JSON.stringify(this.firstAnswerData)
        );
      }

      this.loadingFirstAnswer = false;
      this.pieChart2.hideLoading();
    },
    async fetchAllAnswers() {
      if (!this.scatterChart) {
        this.scatterChart = echarts.init(this.$refs.scatterChart, "macarons");
      }
      this.showLoading(this.scatterChart);
      this.loadingAllAnswer = true;

      if (!this.allAnswerData.length) {
        const params = {
          start: this.start,
          end: this.end,
        };

        const res = await getAnswersWithCreateDate(params);
        this.allAnswerData = res.data;
      }
      if (this.start && this.end) {
        this.displayedAllAnswerData = this.allAnswerData.filter(
          (item) =>
            dayjs(item.answerCreateDate).isAfter(this.start) &&
            dayjs(item.answerCreateDate).isBefore(this.end)
        );
      } else {
        this.displayedAllAnswerData = JSON.parse(
          JSON.stringify(this.allAnswerData)
        );
      }

      this.loadingAllAnswer = false;
      this.scatterChart.hideLoading();
    },
  },
};
</script>

<style scoped>
.pie-chart-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.pie-chart-left-contaniner,
.pie-chart-right-contaniner {
  flex: 1;
  margin: 0 10px;
}
.pie-chart {
  width: 100%;
}
</style>
