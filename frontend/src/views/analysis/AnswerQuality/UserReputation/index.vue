<template>
  <div>
    <h1 style="justify-self: center; margin-bottom: 30px">
      Answer Quality and Its Relation to User Reputation
    </h1>
    <TimeLine :width="'70vw'" @update:range="handleTimeRangeUpdate" />
    <div class="scatter-chart">
      <h3 style="justify-self: center; color: #606266">
        Scatter Plot of Answer Data: Upvotes vs User Reputation
      </h3>
      <div ref="scatterChart" class="scatter-chart" style="height: 80vh"></div>
    </div>
  </div>
</template>

<script>
import { getAnswersWithUserReputation } from "@/api/analysis";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";
import * as echarts from "echarts";
require("echarts/theme/macarons");

export default {
  name: "UserReputation",
  data() {
    return {
      startDate: null,
      endDate: null,
      start: null,
      end: null,
      answerWithUserReputationData: [],
      scatterChart: null,
      resizeTimeout: null,
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
    answerWithUserReputationData: {
      handler() {
        this.initScatterChart();
      },
      deep: true,
    },
  },
  components: {
    TimeLine,
  },
  created() {
    this.fetchData();
  },
  mounted() {
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
      const params = {
        start: this.start,
        end: this.end,
      };

      const res = await getAnswersWithUserReputation(params);
      this.answerWithUserReputationData = res.data;
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
              Reputation: ${data.reputation} <br/>
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
          type: "log", // 对数坐标轴
          name: "Reputation",
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
            name: "Accepted",
            type: "scatter",
            data: this.answerWithUserReputationData
              .filter((item) => item.isAccepted)
              .map((item) => ({
                value: [item.reputation, item.upVoteCount],
                itemStyle: { color: "#2ec7c9" },
                symbolSize: 7,
                ...item,
              })),
          },
          {
            name: "Not Accepted",
            type: "scatter",
            data: this.answerWithUserReputationData
              .filter((item) => !item.isAccepted)
              .map((item) => ({
                value: [item.reputation, item.upVoteCount],
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
