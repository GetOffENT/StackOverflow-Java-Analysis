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
        {{ this.chartTitle }}
      </h3>
      <div style="justify-self: right; margin-right: 150px">
        <span class="define-span" @click="dialogVisible = true"
          >Define "High-quality"</span
        >
        <el-button @click="switchChart">{{ this.buttonText }}</el-button>
      </div>
      <div ref="bottomChart" class="scatter-chart" style="height: 800px"></div>
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
import { highQualityFilter } from "../utils";
import {
  getFirstAnswersWithCreateDate,
  getAcceptedAnswersWithCreateDate,
  getAnswersWithCreateDate,
} from "@/api/analysis";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";
import * as echarts from "echarts";
import * as ecStat from "echarts-stat";
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
      displayedLineData: [],
      height: "300px",
      pieChart1: null,
      pieChart2: null,
      bottomChart: null,
      resizeTimeout: null,
      loadingAcceptedAnswer: false,
      loadingFirstAnswer: false,
      loadingAllAnswer: false,

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
        this.fetchAcceptedAnswers();
        this.fetchFirstAnswers();
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
  
  computed: {
    disabled() {
      return (
        this.loadingAcceptedAnswer ||
        this.loadingFirstAnswer ||
        this.loadingAllAnswer
      );
    },
    chartTitle() {
      return this.buttonText === "details"
        ? "Scatter Plot with Regression Line: High-quality Rate vs Elapsed Time"
        : "Scatter Plot of Answer Data: Upvotes vs Elapsed Time";
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
      const MIN_GROUP_SIZE = 100; // 每组最小数量
      const MIN_GROUP_COUNT = 5; // 最小组数
      const N = 100; // 初始目标组数

      let groupSize, groupCount;
      // 把displayedAllAnswerData分为按照durantion从小到大分为groupCount组聚合，统计每组的高质量率
      if (this.displayedAllAnswerData.length / N >= MIN_GROUP_SIZE) {
        groupSize = Math.ceil(this.displayedAllAnswerData.length / N);
        groupCount = N;
      } else if (
        this.displayedAllAnswerData.length / MIN_GROUP_SIZE >=
        MIN_GROUP_COUNT
      ) {
        groupSize = MIN_GROUP_SIZE;
        groupCount = Math.ceil(this.displayedAllAnswerData.length / MIN_GROUP_SIZE);
      } else {
        groupCount = MIN_GROUP_COUNT;
        groupSize = Math.ceil(this.displayedAllAnswerData.length / groupCount);
      }
      
      const groupedResults = [];
      for (let i = 0; i < groupCount; i++) {
        const groupStart = i * groupSize;
        const groupEnd = Math.min(
          groupStart + groupSize,
          this.displayedAllAnswerData.length
        );
        const group = this.displayedAllAnswerData.slice(groupStart, groupEnd);

        // 统计高质量的比例
        const highQualityCount = group.filter((item) =>
          highQualityFilter(item, this.filter)
        ).length;
        const highQualityRate = group.length
          ? highQualityCount / group.length
          : 0;
        // 统计改组平均upvote和downvote
        const upVoteCount =
          group.reduce((acc, item) => acc + item.upVoteCount, 0) / group.length;
        const downVoteCount =
          group.reduce((acc, item) => acc + item.downVoteCount, 0) /
          group.length;

        // 统计Accepted和First的比例
        const acceptedRate = group.length
          ? group.filter((item) => item.isAccepted).length / group.length
          : 0;
        const firstRate = group.length
          ? group.filter((item) => item.first).length / group.length
          : 0;

        // 获取该组的最大 duration
        const xAxis = group[group.length - 1]?.duration || 0;

        groupedResults.push({
          highQualityRate,
          xAxis: xAxis / 1000,
          upVoteCount,
          downVoteCount,
          acceptedRate,
          firstRate,
        });
      }

      this.lineData = JSON.parse(JSON.stringify(groupedResults));
    },
    handleResize() {
      this.initPieChart1();
      this.initPieChart2();
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
    initLineChart() {
      if (this.bottomChart) {
        this.bottomChart.dispose();
      }
      this.bottomChart = echarts.init(this.$refs.bottomChart, "macarons");

      const regression = ecStat.regression(
        "logarithmic",
        this.lineData.map((item) => [
          parseFloat(item.xAxis),
          item.highQualityRate * 100,
        ])
      );

      const accRegression = ecStat.regression(
        "logarithmic",
        this.lineData.map((item) => [
          parseFloat(item.xAxis),
          item.acceptedRate * 100,
        ])
      );

      const firstRegression = ecStat.regression(
        "logarithmic",
        this.lineData.map((item) => [
          parseFloat(item.xAxis),
          item.firstRate * 100,
        ])
      );

      this.bottomChart.setOption({
        tooltip: {
          trigger: "item",
          axisPointer: {
            type: "cross",
          },
        },
        legend: {
          data: ["High-quality Rate", "Accepted Rate", "First Rate"],
        },
        xAxis: {
          type: "category", // 分类型 x 轴
          name: "Elapsed Time (s)",
          nameLocation: "middle",
          nameGap: 30,
          data: this.lineData.map((item) => item.xAxis), // 设置分类
        },
        yAxis: {
          type: "value",
          name: "Rate (%)",
          nameLocation: "middle",
          nameGap: 50,
        },
        series: [
          {
            name: "Scatter",
            type: "scatter",
            data: (() => {
              return this.lineData.map((item) => ({
                value: [item.xAxis.toString(), item.highQualityRate * 100],
                itemStyle: { color: "#2ec7c9" },
                symbolSize: 7,
                ...item,
              }));
            })(),
            tooltip: {
              formatter: (params) => {
                const data = params.data;
                return `
                Elapsed Time: ${data.value[0]} s <br/>
                High Quality Rate: ${data.value[1].toFixed(2)}% <br/>
                Accepted Rate: ${data.acceptedRate.toFixed(2)} <br/>
                First Rate: ${data.firstRate.toFixed(2)} <br/>
                Average Upvote: ${data.upVoteCount.toFixed(2)} <br/>
                Average Downvote: ${data.downVoteCount.toFixed(2)} <br/>
                `;
              },
            },
          },
          {
            name: "High-quality Rate",
            type: "line",
            showSymbol: false,
            data: regression.points.map((item) => [
              item[0].toString(),
              item[1],
            ]),
            lineStyle: {
              color: "#b6a2de",
              width: 3,
            },
            smooth: true,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
          {
            name: "Accepted Rate",
            type: "line",
            showSymbol: false,
            data: accRegression.points.map((item) => [
              item[0].toString(),
              item[1],
            ]),
            lineStyle: {
              color: "#5ab1ef",
              width: 3,
            },
            smooth: true,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
          {
            name: "First Rate",
            type: "line",
            showSymbol: false,
            data: firstRegression.points.map((item) => [
              item[0].toString(),
              item[1],
            ]),
            lineStyle: {
              color: "#ffb980",
              width: 3,
            },
            smooth: true,
            animationDuration: 2800,
            animationEasing: "cubicInOut",
          },
        ],
      });
    },
    initScatterChart() {
      if (this.bottomChart) {
        this.bottomChart.dispose();
      }
      this.bottomChart = echarts.init(this.$refs.bottomChart, "macarons");

      const toDays = (ms) => Math.ceil(ms / (1000 * 60 * 60 * 24));

      this.bottomChart.setOption({
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
            "High-quality",
            "Non-high-quality",
          ],
          top: "top",
          left: "center",
        },
        xAxis: {
          type: "log", // 对数坐标轴
          name: "Elapsed Time (days)",
          nameGap: 30,
          nameLocation: "middle",
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
            data: (() => {
              // 过滤出 isAccepted 且 first 的数据
              const filteredData = this.displayedAllAnswerData.filter(
                (item) => item.isAccepted && item.first
              );

              // 计算过滤后数据的总和和平均值
              const total = filteredData.reduce(
                (acc, item) => {
                  acc.duration += item.duration / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { duration: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                duration: (total.duration / filteredData.length) * 10000,
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                // 映射过滤后的数据
                ...filteredData.map((item) => ({
                  value: [toDays(item.duration), item.upVoteCount],
                  itemStyle: { color: "#2ec7c9" },
                  symbolSize: 7,
                  ...item,
                })),
                // 添加平均值数据点
                {
                  value: [toDays(average.duration), average.upVoteCount],
                  itemStyle: { color: "#2ec7c9" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                  first: null,
                },
              ];
            })(),
          },
          {
            name: "Accepted & Not First",
            type: "scatter",
            data: (() => {
              // 过滤出 isAccepted 且非 first 的数据
              const filteredData = this.displayedAllAnswerData.filter(
                (item) => item.isAccepted && !item.first
              );

              // 计算过滤后数据的总和和平均值
              const total = filteredData.reduce(
                (acc, item) => {
                  acc.duration += item.duration / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { duration: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                duration: (total.duration / filteredData.length) * 10000,
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                // 映射过滤后的数据
                ...filteredData.map((item) => ({
                  value: [toDays(item.duration), item.upVoteCount],
                  itemStyle: { color: "#b6a2de" },
                  symbolSize: 7,
                  ...item,
                })),
                // 添加平均值数据点
                {
                  value: [toDays(average.duration), average.upVoteCount],
                  itemStyle: { color: "#b6a2de" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                  first: null,
                },
              ];
            })(),
          },
          {
            name: "First & Not Accepted",
            type: "scatter",
            data: (() => {
              // 过滤出 first 且非 isAccepted 的数据
              const filteredData = this.displayedAllAnswerData.filter(
                (item) => item.first && !item.isAccepted
              );

              // 计算过滤后数据的总和和平均值
              const total = filteredData.reduce(
                (acc, item) => {
                  acc.duration += item.duration / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { duration: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                duration: (total.duration / filteredData.length) * 10000,
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                // 映射过滤后的数据
                ...filteredData.map((item) => ({
                  value: [toDays(item.duration), item.upVoteCount],
                  itemStyle: { color: "#5ab1ef" },
                  symbolSize: 7,
                  ...item,
                })),
                // 添加平均值数据点
                {
                  value: [toDays(average.duration), average.upVoteCount],
                  itemStyle: { color: "#5ab1ef" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                  first: null,
                },
              ];
            })(),
          },
          {
            name: "Not Accepted & Not First",
            type: "scatter",
            data: (() => {
              // 过滤出非 isAccepted 且非 first 的数据
              const filteredData = this.displayedAllAnswerData.filter(
                (item) => !item.isAccepted && !item.first
              );

              // 计算过滤后数据的总和和平均值
              const total = filteredData.reduce(
                (acc, item) => {
                  acc.duration += item.duration / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { duration: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                duration: (total.duration / filteredData.length) * 10000,
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                // 映射过滤后的数据
                ...filteredData.map((item) => ({
                  value: [toDays(item.duration), item.upVoteCount],
                  itemStyle: { color: "#ffb980" },
                  symbolSize: 7,
                  ...item,
                })),
                // 添加平均值数据点
                {
                  value: [toDays(average.duration), average.upVoteCount],
                  itemStyle: { color: "#ffb980" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                  first: null,
                },
              ];
            })(),
          },
          {
            name: "High-quality",
            type: "scatter",
            data: (() => {
              const filteredData = this.displayedAllAnswerData.filter(
                (item) => {
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
                }
              );

              const total = filteredData.reduce(
                (acc, item) => {
                  acc.duration += item.duration / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { duration: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                duration: (total.duration / filteredData.length) * 10000,
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                {
                  value: [toDays(average.duration), average.upVoteCount],
                  itemStyle: { color: "#d87a80" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                  first: null,
                },
              ];
            })(),
          },
          {
            name: "Non-high-quality",
            type: "scatter",
            data: (() => {
              const filteredData = this.displayedAllAnswerData.filter(
                (item) => {
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
                }
              );

              const total = filteredData.reduce(
                (acc, item) => {
                  acc.duration += item.duration / 10000;
                  acc.upVoteCount += item.upVoteCount;
                  acc.downVoteCount += item.downVoteCount;
                  return acc;
                },
                { duration: 0, upVoteCount: 0, downVoteCount: 0 }
              );

              const average = {
                duration: (total.duration / filteredData.length) * 10000,
                upVoteCount: (total.upVoteCount / filteredData.length).toFixed(
                  2
                ),
                downVoteCount: (
                  total.downVoteCount / filteredData.length
                ).toFixed(2),
              };

              return [
                {
                  value: [
                    toDays(average.duration),
                    average.upVoteCount < 1 ? 1 : average.upVoteCount,
                  ],
                  itemStyle: { color: "#8d98b3" },
                  symbolSize: 50,
                  name: "Average",
                  ...average,
                  isAccepted: null,
                  first: null,
                },
              ];
            })(),
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
    async fetchData() {
      if (!this.pieChart1) {
        this.pieChart1 = echarts.init(this.$refs.pieChart1, "macarons");
      }
      this.showLoading(this.pieChart1);

      
      if (!this.pieChart2) {
        this.pieChart2 = echarts.init(this.$refs.pieChart2, "macarons");
      }
      this.showLoading(this.pieChart2);

      
      if (!this.bottomChart) {
        this.bottomChart = echarts.init(this.$refs.bottomChart, "macarons");
      }
      this.showLoading(this.bottomChart);

      await this.fetchAllAnswers();

      this.bottomChart.hideLoading();
    },
    fetchAcceptedAnswers() {
      this.loadingAcceptedAnswer = true;

      this.displayedAcceptedAnswerData = this.displayedAllAnswerData.filter(
        (item) => item.isAccepted
      );

      this.loadingAcceptedAnswer = false;
      this.pieChart1.hideLoading();
    },
    fetchFirstAnswers() {
      this.loadingFirstAnswer = true;

      this.displayedFirstAnswerData = this.displayedAllAnswerData.filter(
        (item) => item.first
      );

      this.loadingFirstAnswer = false;
      this.pieChart2.hideLoading();
    },
    async fetchAllAnswers() {
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
