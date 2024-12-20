<template>
  <div>
    <h1 style="justify-self: center; margin-bottom: 30px">
      Answer Quality and Its Relation to Answer Length
    </h1>
    <TimeLine
      :width="'70vw'"
      @update:range="handleTimeRangeUpdate"
      :disabled="disabled"
    />
    <div class="scatter-chart">
      <h3 style="justify-self: center; color: #606266">
        Scatter Plot of Answer Data: Upvotes vs Answer Length
      </h3>
      <div ref="scatterChart" class="scatter-chart" style="height: 80vh"></div>
    </div>
  </div>
</template>

<script>
import { getAnswersWithLength } from "@/api/analysis";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";
import * as echarts from "echarts";
require("echarts/theme/macarons");

export default {
  name: "AnswerLength",
  data() {
    return {
      startDate: null,
      endDate: null,
      start: null,
      end: null,
      answersWithLength: [],
      displayedData: [],
      scatterChart: null,
      resizeTimeout: null,
      disabled: false,
    };
  },
  components: {
    TimeLine,
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
    displayedData: {
      handler() {
        this.initScatterChart();
      },
      deep: true,
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
    async fetchData() {
      if (!this.scatterChart) {
        this.scatterChart = echarts.init(this.$refs.scatterChart, "macarons");
      }
      this.scatterChart.showLoading({
        text: "Loading...",
        color: "#5470C6",
        textColor: "#000",
        maskColor: "rgba(255, 255, 255, 0.8)",
        zlevel: 0,
      });
      this.disabled = true;

      if (!this.answersWithLength.length) {
        const params = {
          start: this.start,
          end: this.end,
        };

        const res = await getAnswersWithLength(params);
        this.answersWithLength = res.data;
      }
      if (this.start && this.end) {
        this.displayedData = this.answersWithLength.filter(
          (item) =>
            dayjs(item.answerCreateDate).isAfter(this.start) &&
            dayjs(item.answerCreateDate).isBefore(this.end)
        );
      } else {
        this.displayedData = JSON.parse(
          JSON.stringify(this.answersWithLength)
        );
      }

      this.disabled = false;
      this.scatterChart.hideLoading();
    },
    initScatterChart() {
      if (this.scatterChart) {
        this.scatterChart.dispose();
      }
      this.scatterChart = echarts.init(this.$refs.scatterChart, "macarons");

      this.scatterChart.setOption({
        tooltip: {
          trigger: "item",
          formatter: function (params) {
            const data = params.data;
            return `
              Answer Length: ${data.length} (characters) <br/>
              Upvotes: ${data.upVoteCount} <br/>
              Downvotes: ${data.downVoteCount} <br/>
              Accepted: ${data.isAccepted}
            `;
          },
        },
        legend: {
          data: ["Accepted", "Not Accepted"],
          top: "top",
          left: "center",
        },
        xAxis: {
          type: "log",
          name: "Answer Length(characters)",
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
        grid: {
          right: "12%",
        },
        series: [
          {
            name: "Accepted",
            type: "scatter",
            data: this.displayedData
              .filter((item) => item.isAccepted)
              .map((item) => ({
                value: [item.length, item.upVoteCount],
                itemStyle: { color: "#2ec7c9" },
                symbolSize: 7,
                ...item,
              })),
          },
          {
            name: "Not Accepted",
            type: "scatter",
            data: this.displayedData
              .filter((item) => !item.isAccepted)
              .map((item) => ({
                value: [item.length, item.upVoteCount],
                itemStyle: { color: "#b6a2de" },
                symbolSize: 7,
                ...item,
              })),
          },
        ],
      });
    },
  },
};
</script>

<style></style>
