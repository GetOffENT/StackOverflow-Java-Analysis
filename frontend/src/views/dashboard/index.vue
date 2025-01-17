<template>
  <div class="dashboard-editor-container">
    <github-corner class="github-corner" />

    <panel-group />

    <el-row style="background: #fff; padding: 16px 16px 0; margin-bottom: 32px">
      <line-chart :chart-data="lineChartData" :loading="loading"/>
      <div class="chart-controls">
        <label>
          <input type="radio" value="monthly" v-model="timeScale" />
          Monthly
        </label>
        <label>
          <input type="radio" value="yearly" v-model="timeScale" />
          Yearly
        </label>
      </div>
      <TimeLine :width="'70vw'" @update:range="handleTimeRangeUpdate" />
    </el-row>
  </div>
</template>

<script>
import GithubCorner from "@/components/GithubCorner";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";
import PanelGroup from "./components/PanelGroup";
import LineChart from "./components/LineChart";
import { getCountInSingleMonth } from "@/api/analysis";

export default {
  name: "DashboardAdmin",
  components: {
    GithubCorner,
    TimeLine,
    PanelGroup,
    LineChart,
  },
  data() {
    return {
      startDate: null,
      endDate: null,
      start: null,
      end: null,
      chartData: [],
      lineChartData: [],
      timeScale: "monthly",
      loading: true,
    };
  },
  watch: {
    startDate(val) {
      this.start = val ? dayjs(val).format("YYYY-MM-DD HH:mm") : null;

      this.processChartData();
    },
    endDate() {
      this.end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;

      this.processChartData();
    },
    chartData: {
      deep: true,
      handler(val) {
        this.processChartData();
      },
    },
    timeScale() {
      this.processChartData();
    },
  },
  methods: {
    handleTimeRangeUpdate(dateRange) {
      this.startDate = dateRange["start"];
      this.endDate = dateRange["end"];
    },
    async fetchData() {
      const params = {
        start: this.start,
        end: this.end,
      };

      this.loading = true;

      const { data } = await getCountInSingleMonth(params);
      this.chartData = data;

      this.loading = false;
    },
    // 处理图表数据：根据时间刻度（月或年）来聚合数据
    processChartData() {
      let data = JSON.parse(JSON.stringify(this.chartData));
      if (this.start && this.end) {
        data = data.filter((item) => {
          return item.time >= this.start && item.time <= this.end;
        });
      }

      if (this.timeScale === "monthly") {
        this.lineChartData = data.map((item) => ({
          time: item.time,
          questionCount: item.questionCount,
          answerCount: item.answerCount,
          commentCount: item.commentCount,
        }));
      } else {
        // 按年显示，将每一年的数据合并
        const aggregatedData = this.aggregateDataByYear(data);
        this.lineChartData = aggregatedData;
      }
    },

    // 按年份聚合数据
    aggregateDataByYear(data) {
      const aggregatedData = [];
      const groupedByYear = {};

      data.forEach((item) => {
        const year = item.time.split("-")[0];
        if (!groupedByYear[year]) {
          groupedByYear[year] = {
            time: year,
            questionCount: 0,
            answerCount: 0,
            commentCount: 0,
          };
        }

        groupedByYear[year].questionCount += item.questionCount;
        groupedByYear[year].answerCount += item.answerCount;
        groupedByYear[year].commentCount += item.commentCount;
      });
      for (const year in groupedByYear) {
        aggregatedData.push(groupedByYear[year]);
      }

      return aggregatedData;
    },
  },
  created() {
    this.fetchData();
    // console.log(this.lineChartData);
  },
};
</script>

<style lang="scss" scoped>
.chart-controls {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.chart-controls label {
  margin-left: 10px;
  font-size: 14px;
  cursor: pointer;
}

.chart-controls input[type="radio"] {
  margin-right: 5px;
  cursor: pointer;
}

.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
