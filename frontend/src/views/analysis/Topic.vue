<template>
  <div style="margin-top: 20px">
    <el-form :inline="true" class="form" style="justify-self: center">
      <el-form-item label="from">
        <el-date-picker
          v-model="startDate"
          type="date"
          placeholder="start date"
        />
      </el-form-item>

      <!-- 结束日期选择器 -->
      <el-form-item label="to">
        <el-date-picker
          v-model="endDate"
          type="date"
          placeholder="end date"
        />
      </el-form-item>
      <!-- 显示数量输入框 -->
      <el-form-item label="显示数量">
        <el-input-number
          v-model="topN"
          :min="1"
          :max="100"
          @change="adjustChartHeight"
        />
      </el-form-item>

      <!-- 查询按钮 -->
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="startRaceChart">播放</el-button>
      </el-form-item>
    </el-form>

    <div class="chart-type-select">
      <el-tooltip content="词云" placement="top">
        <div class="ciyun" @click="cloudChart">
          <svg-icon icon-class="ciyun" />
        </div>
      </el-tooltip>
      <el-tooltip content="水平柱状图" placement="top">
        <div class="horizontal-bar-chart" @click="staticChart">
          <svg-icon icon-class="horizontal-bar-chart" />
        </div>
      </el-tooltip>
    </div>

    <!-- 图表区域 -->
    <div id="chart" style="justify-self: center"></div>
  </div>
</template>

<script>
import { getTopNTopics, getRaceChartData } from "@/api/tag";
import dayjs from "dayjs";
import * as d3 from "d3";
import cloud from "d3-cloud";

export default {
  name: "RaceChart",
  data() {
    return {
      currentClick: "cloud",
      startDate: null,
      endDate: null,
      topN: 10,
      staticData: [], // 静态柱状图数据
      raceData: [], // 动态竞赛图数据
      chartWidth: 1200,
      barHeight: 50,
      margin: { top: 50, right: 150, bottom: 50, left: 150 },
    };
  },
  async mounted() {
    this.cloudChart();
  },
  methods: {
    async search() {
      if (this.currentClick === "static") {
        this.staticChart();
      } else if (this.currentClick === "dynamic") {
        this.startRaceChart();
      } else {
        this.cloudChart();
      }
    },
    async cloudChart() {
      this.currentClick = "cloud";
      d3.select("#chart").selectAll("*").remove();

      await this.fetchStaticData();

      const chartWidth = this.chartWidth + this.margin.left + this.margin.right;
      const chartHeight =
        this.topN * this.barHeight + this.margin.top + this.margin.bottom;

      const svg = d3
        .select("#chart")
        .append("svg")
        .attr("width", chartWidth)
        .attr("height", chartHeight)
        .append("g")
        .attr("transform", `translate(${chartWidth / 2}, ${chartHeight / 2})`);

      const layout = cloud()
        .size([chartWidth, chartHeight])
        .words(
          this.staticData.map((d) => ({
            text: d.tagName,
            size: Math.sqrt(d.count) * 10,
            percentage: (d.percentage * 100).toFixed(2) + "%",
          }))
        )
        .padding(5) // 词语之间的间距
        .rotate((d) => {
          const rotationProbability = Math.max(0.2, 1 - d.size / 100);

          // 决定是否旋转
          if (Math.random() < rotationProbability) {
            return Math.random() * 180 - 90;
          } else {
            return 0;
          }
        }) // 随机旋转
        .font("Impact")
        .fontSize((d) => d.size)
        .on("end", draw); // 布局完成后调用绘制函数

      layout.start();

      // 绘制词云
      function draw(words) {
        svg
          .selectAll("text")
          .data(words)
          .enter()
          .append("text")
          .style("font-family", "Impact")
          .style(
            "fill",
            () => d3.schemeCategory10[Math.floor(Math.random() * 10)]
          ) // 随机颜色
          .style("font-size", (d) => `${d.size}px`)
          .attr("text-anchor", "middle")
          .attr(
            "transform",
            (d) => `translate(${d.x}, ${d.y}) rotate(${d.rotate})`
          )
          .text((d) => d.text)
          .append("title")
          .text(
            (d) =>
              `${d.text}\ncount: ${d.size}\npercentage: ${
                d.percentage
              }`
          );
      }
    },
    async fetchStaticData() {
      const start = this.startDate
        ? dayjs(this.startDate).format("YYYY-MM-DD HH:mm")
        : null;
      const end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;

      const res = await getTopNTopics({ start, end, n: this.topN });
      this.staticData = res.data;
    },
    async fetchRaceData() {
      const start = this.startDate
        ? dayjs(this.startDate).format("YYYY-MM-DD HH:mm")
        : null;
      const end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;

      const res = await getRaceChartData({ start, end, n: this.topN });
      this.raceData = res.data;
    },
    adjustChartHeight() {
      const chartHeight =
        this.topN * this.barHeight + this.margin.top + this.margin.bottom;
      d3.select("#chart").select("svg").attr("height", chartHeight);

      this.search();
    },
    async staticChart() {
      this.currentClick = "static";
      d3.select("#chart").selectAll("*").remove(); // 清空之前的图表
      await this.fetchStaticData();

      const chartHeight =
        this.topN * this.barHeight + this.margin.top + this.margin.bottom;
      const svg = d3
        .select("#chart")
        .append("svg")
        .attr("width", this.chartWidth + this.margin.left + this.margin.right)
        .attr("height", chartHeight)
        .append("g")
        .attr("transform", `translate(${this.margin.left},${this.margin.top})`);

      const x = d3
        .scaleLinear()
        .domain([0, d3.max(this.staticData, (d) => d.count)])
        .range([0, this.chartWidth]);

      const y = d3
        .scaleBand()
        .domain(this.staticData.map((d) => d.tagName))
        .range([0, this.topN * this.barHeight])
        .padding(0.1);

      svg
        .append("g")
        .attr("class", "x-axis")
        .attr("transform", `translate(0,${this.topN * this.barHeight})`)
        .call(d3.axisBottom(x).ticks(5));

      svg.append("g").attr("class", "y-axis").call(d3.axisLeft(y));

      svg
        .append("text")
        .attr("class", "x-axis-label")
        .attr("x", this.chartWidth / 2)
        .attr("y", this.topN * this.barHeight + 40) // 轴线下方的偏移量
        .attr("text-anchor", "middle")
        .style("font-size", "14px")
        .text("Number of questions");

      svg
        .append("text")
        .attr("class", "y-axis-label")
        .attr("x", 0) // 调整位置到纵轴左侧
        .attr("y", -(this.margin.top / 2)) // 轴线左侧的偏移量
        .attr("text-anchor", "middle")
        .style("font-size", "14px")
        .text("Topic");

      const color = d3.scaleOrdinal(d3.schemeCategory10);

      svg
        .selectAll(".bar")
        .data(this.staticData)
        .enter()
        .append("rect")
        .attr("class", "bar")
        .attr("x", 0)
        .attr("y", (d) => y(d.tagName))
        .attr("height", y.bandwidth())
        .attr("width", (d) => x(d.count))
        .style("fill", (d) => color(d.tagName));

      svg
        .selectAll(".label")
        .data(this.staticData)
        .enter()
        .append("text")
        .attr("class", "label")
        .attr("x", (d) => x(d.count) + 5)
        .attr("y", (d) => y(d.tagName) + y.bandwidth() / 2)
        .attr("dy", ".35em")
        .text((d) => d.count);
    },
    async startRaceChart() {
      this.currentClick = "dynamic";
      d3.select("#chart").selectAll("*").remove(); // 清空之前的图表
      await this.fetchRaceData();

      const chartHeight =
        this.topN * this.barHeight + this.margin.top + this.margin.bottom;
      const svg = d3
        .select("#chart")
        .append("svg")
        .attr("width", this.chartWidth + this.margin.left + this.margin.right)
        .attr("height", chartHeight)
        .append("g")
        .attr("transform", `translate(${this.margin.left},${this.margin.top})`);

      const x = d3.scaleLinear().range([0, this.chartWidth]);
      const y = d3
        .scaleBand()
        .range([0, this.topN * this.barHeight])
        .padding(0.1);

      const color = d3.scaleOrdinal(d3.schemeCategory10);

      // 添加年份标签
      const yearLabel = svg
        .append("text")
        .attr("class", "year-label")
        .attr("x", this.chartWidth)
        .attr("y", -10)
        .attr("text-anchor", "end")
        .style("font-size", "24px")
        .style("font-weight", "bold");

      const update = (data, time) => {
        const currentData = data
          .filter((d) => d.time === time)
          .sort((a, b) => b.count - a.count)
          .slice(0, this.topN);

        x.domain([0, d3.max(currentData, (d) => d.count)]);
        y.domain(currentData.map((d) => d.tagName));

        svg
          .selectAll(".x-axis")
          .data([null])
          .join("g")
          .attr("class", "x-axis")
          .attr("transform", `translate(0,${this.topN * this.barHeight})`)
          .call(d3.axisBottom(x).ticks(5));

        svg
          .selectAll(".y-axis")
          .data([null])
          .join("g")
          .attr("class", "y-axis")
          .call(d3.axisLeft(y));

        svg
          .append("text")
          .attr("class", "x-axis-label")
          .attr("x", this.chartWidth / 2)
          .attr("y", this.topN * this.barHeight + 40) // 轴线下方的偏移量
          .attr("text-anchor", "middle")
          .style("font-size", "14px")
          .text("Number of questions");

        svg
          .append("text")
          .attr("class", "y-axis-label")
          .attr("x", 0) // 调整位置到纵轴左侧
          .attr("y", -(this.margin.top / 2)) // 轴线左侧的偏移量
          .attr("text-anchor", "middle")
          .style("font-size", "14px")
          .text("Topic");

        const bars = svg.selectAll(".bar").data(currentData, (d) => d.tagName);

        bars
          .enter()
          .append("rect")
          .attr("class", "bar")
          .attr("x", 0)
          .attr("y", (d) => y(d.tagName))
          .attr("height", y.bandwidth())
          .attr("width", 0)
          .style("fill", (d) => color(d.tagName))
          .transition()
          .duration(1000)
          .attr("width", (d) => x(d.count));

        bars
          .transition()
          .duration(1000)
          .attr("width", (d) => x(d.count))
          .attr("y", (d) => y(d.tagName));

        bars.exit().transition().duration(1000).attr("width", 0).remove();

        const labels = svg
          .selectAll(".label")
          .data(currentData, (d) => d.tagName);

        labels
          .enter()
          .append("text")
          .attr("class", "label")
          .attr("x", (d) => x(d.count) + 5)
          .attr("y", (d) => y(d.tagName) + y.bandwidth() / 2)
          .attr("dy", ".35em")
          .text((d) => d.count)
          .style("opacity", 0)
          .transition()
          .duration(1000)
          .style("opacity", 1);

        labels
          .transition()
          .duration(1000)
          .attr("x", (d) => x(d.count) + 5)
          .attr("y", (d) => y(d.tagName) + y.bandwidth() / 2)
          .text((d) => d.count);

        labels.exit().transition().duration(1000).style("opacity", 0).remove();

        // 更新年份
        yearLabel.text(time);
      };

      const times = [...new Set(this.raceData.map((d) => d.time))];
      let frame = 0;

      const interval = setInterval(() => {
        if (frame >= times.length) {
          clearInterval(interval);
        } else {
          update(this.raceData, times[frame]);
          frame++;
        }
      }, 1500);
    },
  },
};
</script>

<style scoped>
.form {
  margin-bottom: 20px;
}
.ciyun {
  font-size: 35px;
}
.chart-type-select {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.ciyun:hover {
  cursor: pointer;
}
.horizontal-bar-chart {
  font-size: 30px;
  margin-left: 20px;
}
.horizontal-bar-chart:hover {
  cursor: pointer;
}
</style>
