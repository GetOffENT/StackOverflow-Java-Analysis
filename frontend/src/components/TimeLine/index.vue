<template>
  <div class="time-slider-container" :style="{ width }">
    <!-- 滑块区域 -->
    <vue-slider
      ref="slider"
      v-model="currentRange"
      :min="adjustedStartTimestamp"
      :max="endTimestamp"
      :drag-on-click="true"
      :enable-cross="false"
      :interval="step"
      :tooltip="'always'"
      :lazy="true"
      :tooltip-formatter="formatTime2"
      @change="handleRangeChange"
      class="time-slider"
    ></vue-slider>

    <!-- 时间范围显示 -->
    <div class="time-range-display">
      <span>{{ formatTime2(start) }}</span>
      <span>{{ formatTime2(end) }}</span>
    </div>
  </div>
</template>

<script>
import VueSlider from "vue-slider-component";
import dayjs from "dayjs";
import { mapGetters } from "vuex";
import "vue-slider-component/theme/default.css";

export default {
  name: "TimeRangeSlider",
  components: { VueSlider },
  props: {
    width: {
      type: String,
      default: "100%",
    },
    step: {
      type: Number,
      default: 3600 * 24 * 1000, // 步长（默认为1小时）
    },
  },
  data() {
    return {
      currentRange: ["2022-01-01 00:00", "2022-01-02 00:00"],
    };
  },
  computed: {
    ...mapGetters(["start", "end"]),
    startTimestamp() {
      return new Date(this.start).getTime();
    },
    endTimestamp() {
      return new Date(this.end).getTime();
    },
    adjustedStartTimestamp() {
      // 计算调整后(this.endTimestamp - this.startTimestamp)可以被step整除的startTimestamp
      return (
        this.startTimestamp -
        (this.step - ((this.endTimestamp - this.startTimestamp) % this.step))
      );
    },
    timeMarks() {
      // 自动生成时间制度标记
      const marks = [];
      for (
        let t = this.adjustedStartTimestamp;
        t <= this.endTimestamp;
        t += this.step * 4
      ) {
        marks.push(this.formatTime(t));
      }
      return marks;
    },
  },
  watch: {
    start: "initializeRange",
    end: "initializeRange",
  },
  created() {
    this.initializeRange();
  },
  methods: {
    initializeRange() {
      this.currentRange = [
        this.adjustedStartTimestamp,
        this.endTimestamp, // 设置默认值
      ];
    },
    formatTime2(timestamp) {
      const date = new Date(timestamp);
      return dayjs(date).format("YYYY-MM-DD");
    },
    formatTime(timestamp) {
      const date = new Date(timestamp);
      return dayjs(date).format("YYYY-MM-DD HH:mm");
    },
    handleRangeChange(value) {
      this.$emit("update:range", {
        start: this.formatTime(value[0]),
        end: this.formatTime(value[1]),
      });
    },
  },
};
</script>

<style scoped>
.time-slider-container {
  margin: 20px auto;
  text-align: center;
  position: relative;
}

.time-scale {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #888;
}

.time-mark {
  flex: 1;
  text-align: center;
}

.time-slider {
  margin: 0 20px;
}

.time-range-display {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-weight: bold;
}
</style>
