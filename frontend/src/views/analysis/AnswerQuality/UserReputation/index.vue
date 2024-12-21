<template>
  <div>
    <h1 style="justify-self: center; margin-bottom: 30px">
      Answer Quality and Its Relation to User Reputation
    </h1>
    <TimeLine
      :width="'70vw'"
      @update:range="handleTimeRangeUpdate"
      :disabled="disabled"
    />
    <div class="chart-container">
      <h3 style="justify-self: center; color: #606266">
        Scatter Plot of Answer Data: Upvotes vs User Reputation
      </h3>
      <div style="justify-self: right; margin-right: 150px">
        <span class="define-span" @click="dialogVisible = true"
          >Define "High-quality"</span
        >
        <el-button @click="switchChart">{{ this.buttonText }}</el-button>
      </div>
      <div ref="chart" class="chart" style="height: 80vh"></div>
    </div>

    <el-dialog
      title="Define a high-quality answer"
      :visible.sync="dialogVisible"
      width="500px"
    >
      <el-form :model="filter" label-width="140px">
        <!-- upVoteCount -->
        <el-form-item label="UpVote Count ≥">
          <el-input-number
            v-model="filter.upVoteCount"
            :min="0"
            placeholder="Enter minimum count"
          />
        </el-form-item>

        <!-- downVoteCount -->
        <el-form-item label="DownVote Count ≤">
          <el-input-number
            v-model="filter.downVoteCount"
            :min="0"
            placeholder="Enter maximum count"
          />
        </el-form-item>

        <!-- Condition (or/and) -->
        <el-form-item>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-select
                v-model="filter.orAccepted"
                placeholder="Select Condition"
                :disabled="!filter.isAccepted"
              >
                <el-option :value="true" label="or"></el-option>
                <el-option :value="false" label="and"></el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              <el-checkbox v-model="filter.isAccepted">Accepted</el-checkbox>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <!-- Dialog Footer -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleDialogClose">Confirm</el-button>
      </div>
    </el-dialog>
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
      displayedData: [],
      displayedLineData: [],
      chart: null,
      resizeTimeout: null,
      disabled: false,

      filter: {
        upVoteCount: 5,
        downVoteCount: 100000,
        orAccepted: true,
        isAccepted: true,
      },
      buttonText: "details",
      dialogVisible: false,
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
    displayedData: {
      handler() {
        // this.initScatterChart();
        if (this.buttonText === "details") {
          this.filterLineData();
          this.initLineChart();
        } else {
          this.initScatterChart();
        }
      },
      deep: true,
    },
  },
  components: {
    TimeLine,
  },
  mounted() {
    this.fetchData();
    window.addEventListener("resize", this.debounceResize);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.debounceResize);
  },
  methods: {
    handleDialogClose() {
      this.dialogVisible = false;
      if (this.buttonText === "details") {
        this.filterLineData();
        this.initLineChart();
      } else {
        this.initScatterChart();
      }
    },
    switchChart() {
      if (this.buttonText === "details") {
        this.buttonText = "back";
        this.initScatterChart();
      } else {
        this.buttonText = "details";
        this.initLineChart();
      }
    },
    filterLineData() {
      // 把displayedData按照bins(reputation方向)分组, count计数, xAxis为从bins[1]开始到bins[length-1]
      const bins = [0, 10, 100, 1000, 10000, 100000, 1000000, 10000000];
      this.lineData = bins
        .map((bin, index) => {
          if (index !== 0) {
            let highQualityCount = 0,
              nonHighQualityCount = 0;
            for (let i = 0; i < this.displayedData.length; i++) {
              const item = this.displayedData[i];
              if (item.reputation > bins[index - 1] && item.reputation <= bin) {
                let highQualityFlag = false;
                if (this.filter.orAccepted && this.filter.isAccepted) {
                  if (item.isAccepted) {
                    highQualityFlag = true;
                  } else {
                    highQualityFlag =
                      this.filter.upVoteCount <= item.upVoteCount &&
                      item.downVoteCount <= this.filter.downVoteCount;
                  }
                } else {
                  highQualityFlag =
                    this.filter.upVoteCount <= item.upVoteCount &&
                    item.downVoteCount <= this.filter.downVoteCount &&
                    (this.filter.isAccepted ? item.isAccepted : true);
                }
                if (highQualityFlag) {
                  highQualityCount += 1;
                } else {
                  nonHighQualityCount += 1;
                }
              }
            }
            if (index === bins.length - 1) {
              return {
                xAxis: `${bins[index - 1]}+`,
                highQualityCount,
                nonHighQualityCount,
              };
            }
            return {
              xAxis: `${bins[index - 1]}-${bins[index]}`,
              highQualityCount,
              nonHighQualityCount,
            };
          }
        })
        .slice(1);
    },
    handleResize() {
      if (this.buttonText === "back") {
        this.initScatterChart();
      } else {
        this.initLineChart();
      }
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
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.chart, "macarons");
      }
      this.chart.showLoading({
        text: "Loading...",
        color: "#5470C6",
        textColor: "#000",
        maskColor: "rgba(255, 255, 255, 0.8)",
        zlevel: 0,
      });
      this.disabled = true;

      if (!this.answerWithUserReputationData.length) {
        const params = {
          start: this.start,
          end: this.end,
        };

        const res = await getAnswersWithUserReputation(params);
        this.answerWithUserReputationData = res.data;
      }
      if (this.start && this.end) {
        this.displayedData = this.answerWithUserReputationData.filter(
          (item) =>
            dayjs(item.answerCreateDate).isAfter(this.start) &&
            dayjs(item.answerCreateDate).isBefore(this.end)
        );
      } else {
        this.displayedData = JSON.parse(
          JSON.stringify(this.answerWithUserReputationData)
        );
      }

      this.disabled = false;
      this.chart.hideLoading();
    },
    initLineChart() {
      if (this.chart) {
        this.chart.dispose();
      }
      this.chart = echarts.init(this.$refs.chart, "macarons");

      this.chart.setOption({
        xAxis: {
          name: "Reputation",
          type: "category",
          boundaryGap: false,
          data: this.lineData.map((item) => item.xAxis),
          axisTick: {
            show: false,
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
          },
          padding: [5, 10],
        },
        yAxis: {
          name: "Answer Count",
          axisTick: {
            show: false,
          },
        },
        legend: {
          data: ["High-quality", "Non-high-quality"],
        },
        series: [
          {
            name: "High-quality",
            type: "line",
            data: this.lineData.map((item) => item.highQualityCount),
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
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
          {
            name: "Non-high-quality",
            type: "line",
            data: this.lineData.map((item) => item.nonHighQualityCount),
            itemStyle: {
              normal: {
                color: "#3888fa",
                lineStyle: {
                  color: "#3888fa",
                  width: 2,
                },
              },
            },
            smooth: true,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
        ],
      });
    },
    initScatterChart() {
      if (this.chart) {
        this.chart.dispose();
      }
      this.chart = echarts.init(this.$refs.chart, "macarons");

      this.chart.setOption({
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
          data: [
            "Accepted",
            "Not Accepted",
            "High-quality",
            "Non-high-quality",
          ],
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
            data: (() => {
              const filteredData = this.displayedData.filter(
                (item) => item.isAccepted
              );

              const total = filteredData.reduce(
                (acc, item) => {
                  acc.reputation = acc.reputation + item.reputation / 100;
                  acc.upVoteCount = acc.upVoteCount + item.upVoteCount;
                  acc.downVoteCount = acc.downVoteCount + item.downVoteCount;
                  return acc;
                },
                { reputation: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                reputation: (
                  (total.reputation / filteredData.length) *
                  100
                ).toFixed(2),
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                ...filteredData.map((item) => ({
                  value: [item.reputation, item.upVoteCount],
                  itemStyle: { color: "#2ec7c9" },
                  symbolSize: 7,
                  ...item,
                })),
                {
                  value: [average.reputation, average.upVoteCount],
                  itemStyle: { color: "#2ec7c9" },
                  symbolSize: 50,
                  ...average,
                  isAccepted: null,
                },
              ];
            })(),
          },
          {
            name: "Not Accepted",
            type: "scatter",
            data: (() => {
              const filteredData = this.displayedData.filter(
                (item) => !item.isAccepted
              );

              const total = filteredData.reduce(
                (acc, item) => {
                  acc.reputation = acc.reputation + item.reputation / 100;
                  acc.upVoteCount = acc.upVoteCount + item.upVoteCount;
                  acc.downVoteCount = acc.downVoteCount + item.downVoteCount;
                  return acc;
                },
                { reputation: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                reputation: (
                  (total.reputation / filteredData.length) *
                  100
                ).toFixed(2),
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                ...filteredData.map((item) => ({
                  value: [item.reputation, item.upVoteCount],
                  itemStyle: { color: "#b6a2de" },
                  symbolSize: 7,
                  ...item,
                })),
                {
                  value: [average.reputation, average.upVoteCount],
                  itemStyle: { color: "#b6a2de" },
                  symbolSize: 50,
                  ...average,
                  isAccepted: null,
                },
              ];
            })(),
          },
          {
            name: "High-quality",
            type: "scatter",
            data: (() => {
              const filteredData = this.displayedData.filter((item) => {
                let highQualityFlag = false;
                if (this.filter.orAccepted && this.filter.isAccepted) {
                  if (item.isAccepted) {
                    highQualityFlag = true;
                  } else {
                    highQualityFlag =
                      this.filter.upVoteCount <= item.upVoteCount &&
                      item.downVoteCount <= this.filter.downVoteCount;
                  }
                } else {
                  highQualityFlag =
                    this.filter.upVoteCount <= item.upVoteCount &&
                    item.downVoteCount <= this.filter.downVoteCount &&
                    (this.filter.isAccepted ? item.isAccepted : true);
                }
                return highQualityFlag;
              });

              const total = filteredData.reduce(
                (acc, item) => {
                  acc.reputation += item.reputation / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { reputation: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                reputation: (
                  (total.reputation / filteredData.length) *
                  10000
                ).toFixed(2),
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                {
                  value: [average.reputation, average.upVoteCount],
                  itemStyle: { color: "#5ab1ef" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                },
              ];
            })(),
          },
          {
            name: "Non-high-quality",
            type: "scatter",
            data: (() => {
              const filteredData = this.displayedData.filter((item) => {
                let highQualityFlag = false;
                if (this.filter.orAccepted && this.filter.isAccepted) {
                  if (item.isAccepted) {
                    highQualityFlag = true;
                  } else {
                    highQualityFlag =
                      this.filter.upVoteCount <= item.upVoteCount &&
                      item.downVoteCount <= this.filter.downVoteCount;
                  }
                } else {
                  highQualityFlag =
                    this.filter.upVoteCount <= item.upVoteCount &&
                    item.downVoteCount <= this.filter.downVoteCount &&
                    (this.filter.isAccepted ? item.isAccepted : true);
                }
                return !highQualityFlag;
              });

              const total = filteredData.reduce(
                (acc, item) => {
                  acc.reputation += item.reputation / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { reputation: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                reputation: (
                  (total.reputation / filteredData.length) *
                  10000
                ).toFixed(2),
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                {
                  value: [average.reputation, average.upVoteCount < 1 ? 1 : average.upVoteCount],
                  itemStyle: { color: "#ffb980" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                },
              ];
            })(),
          },
        ],
      });
    },
  },
};
</script>

<style scoped>
.define-span {
  margin-right: 30px;
  color: #3498db;
  cursor: pointer;
  transition: color 0.3s ease;
}

.define-span:hover {
  color: #1d4e89;
  text-decoration: underline;
}
</style>
