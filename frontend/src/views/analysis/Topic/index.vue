<template>
  <div style="margin-top: 20px; overflow: auto">
    <h1 style="justify-self: center; margin-bottom: 30px">
      Top N most frequently asked topics
    </h1>
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
        <el-date-picker v-model="endDate" type="date" placeholder="end date" />
      </el-form-item>
      <!-- 显示数量输入框 -->
      <el-form-item label="top N">
        <el-input-number v-model="topN" :min="2" :max="2000" @change="search" />
      </el-form-item>

      <!-- 查询按钮 -->
      <el-form-item>
        <el-button type="primary" @click="search">query</el-button>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          @click="startRaceChart"
          v-if="currentClick === 'static' || currentClick === 'dynamic'"
          >play</el-button
        >
      </el-form-item>
    </el-form>

    <div class="chart-type-select">
      <el-tooltip content="word cloud" placement="top">
        <div class="ciyun" @click="cloudChart">
          <svg-icon icon-class="ciyun" />
        </div>
      </el-tooltip>
      <el-tooltip content="horizontal bar chart" placement="top">
        <div class="horizontal-bar-chart" @click="staticChart">
          <svg-icon icon-class="horizontal-bar-chart" />
        </div>
      </el-tooltip>
    </div>

    <!-- 图表区域 -->
    <div v-if="currentClick === 'cloud'">
      <div id="cloudChart"></div>
    </div>
    <div v-else>
      <div id="chart" style="justify-self: center"></div>
    </div>
  </div>
</template>

<script>
import { getTopNTopics, getRaceChartData } from "@/api/tag";
import dayjs from "dayjs";
import * as d3 from "d3";
import * as echarts from "echarts";
import "echarts-wordcloud";
require("echarts/theme/macarons");

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
      colors: [
        "#86D4FF",
        "#FF8F6C",
        "#2CF263",
        "#9FA8F7",
        "#1274FF",
        "#E6613D",
        "#FFC629",
        "#FFAB2E",
        "#F78289",
        "#FF6C96",
        "#45BFD4",
        "#4E31FF",
        "#31FBFB",
        "#86D4FF",
        "#BF8AFD",
        "#FFF500",
        "#DE58FF",
        "#72ED7C",
        "#0BEEB8",
        "#931CFF",
        "#3D25F2",
        "#F995C8",
        "#FBE9B4",
        "#FF4AB6",
      ],
    };
  },
  async mounted() {
    this.cloudChart();
  },
  watch: {
    currentClick(newVal, oldVal) {
      // 销毁旧图表实例
      if (oldVal === "cloud") {
        const cloudChart = echarts.getInstanceByDom(
          document.getElementById("cloudChart")
        );
        if (cloudChart) {
          cloudChart.dispose(); // 销毁旧实例
        }
      } else if (oldVal === "static" || oldVal === "dynamic") {
        d3.select("#chart").selectAll("*").remove(); // 清空 D3 图表
      }
    },
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

      // 等待元素加载完成
      while (!document.getElementById("cloudChart")) {
        await new Promise((resolve) => setTimeout(resolve, 100));
      }
      const cloudChartContainer = document.getElementById("cloudChart");
      const screenWidth = window.innerWidth;
      const screenHeight = window.innerHeight;

      const containerWidth = screenWidth;
      const containerHeight = screenHeight * 0.6;

      cloudChartContainer.style.width = `${containerWidth}px`;
      cloudChartContainer.style.height = `${containerHeight}px`;
      cloudChartContainer.style.margin = "20px auto";
      cloudChartContainer.style.maxWidth = "100%";

      // 获取静态数据
      await this.fetchStaticData();
      const wordCloudData = this.staticData.map((item) => ({
        name: item.tagName,
        value: item.count,
        percentage: (item.percentage * 100).toFixed(2),
      }));

      const chart = echarts.init(cloudChartContainer, "macarons");
      chart.setOption({
        tooltip: {
          show: true,
          trigger: "item",
          formatter: function (params) {
            const { name, value, data } = params;
            return `
          <div>
            <strong>${name}</strong><br/>
            Count: ${value}<br/>
            Percentage: ${data.percentage}%<br/>
          </div>
        `;
          },
        },
        series: [
          {
            type: "wordCloud",
            shape: "circle",
            sizeRange: [20, 100],
            rotationRange: [-45, 45],
            gridSize: 8,
            textStyle: {
              fontFamily: "sans-serif",
              fontWeight: "bold",
              normal: {
                color: () => {
                  return this.colors[
                    Math.floor(Math.random() * this.colors.length)
                  ];
                },
              },
            },
            emphasis: {
              focus: "self",
              textStyle: {
                textShadowBlur: 10,
                textShadowColor: "#333",
              },
            },
            data: wordCloudData,
          },
        ],
      });
      let timer = null;
      // 设备视口大小改变时，重置 echarts
      window.onresize = function () {
        // 简单的防抖动处理
        clearTimeout(timer);
        timer = setTimeout(() => {
          console.log(timer);
          chart.resize();
        }, 500);
      };
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
        .attr("y", this.topN * this.barHeight + 40)
        .attr("text-anchor", "middle")
        .style("font-size", "14px")
        .text("Number of questions");

      svg
        .append("text")
        .attr("class", "y-axis-label")
        .attr("x", 0)
        .attr("y", -(this.margin.top / 2))
        .attr("text-anchor", "middle")
        .style("font-size", "14px")
        .text("Topic");

      const color = d3.scaleOrdinal(d3.schemeCategory10);

      // 添加动画：让每个条形图的宽度从0到最终值
      const bars = svg
        .selectAll(".bar")
        .data(this.staticData)
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

      // 添加标签并且动画逐渐显示
      svg
        .selectAll(".label")
        .data(this.staticData)
        .enter()
        .append("text")
        .attr("class", "label")
        .attr("x", (d) => x(d.count) + 5)
        .attr("y", (d) => y(d.tagName) + y.bandwidth() / 2)
        .attr("dy", ".35em")
        .text((d) => d.count)
        .style("opacity", 0) // 初始透明度为0
        .transition() // 动画
        .duration(1000) // 动画时长
        .style("opacity", 1); // 动画结束时透明度为1
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

#chart {
  min-height: 300px;
  max-width: 100%;
}
</style>
