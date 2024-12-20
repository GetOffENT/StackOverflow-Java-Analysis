<template>
  <div>
    <h1 style="justify-self: center; margin-bottom: 30px">
      Top N errors frequently discussed errors and exceptions
    </h1>
    <el-form :inline="true" class="form" style="justify-self: center">
      <el-form-item label="top N" style="margin-right: 30px">
        <el-input-number
          v-model="topN"
          :min="2"
          :max="100"
          @change="debounceCountChange"
        />
      </el-form-item>
    </el-form>
    <TimeLine :width="'70vw'" @update:range="handleTimeRangeUpdate" />
    <div class="chart-container">
      <div class="top-container">
        <div class="top-left-contaniner">
          <MistakeOne
            :chartData="exceptionData"
            :type="'exceptions'"
            :title="'Exceptions'"
            :loading="loading"
          />
        </div>
        <div class="top-right-contaniner">
          <MistakeOne
            :chartData="errorData"
            :type="'errors'"
            :title="'Errors'"
            :loading="loading"
          />
        </div>
      </div>
      <div class="bottom-container">
        <MistakeOne
          :chartData="mixedData"
          :type="'errors and exceptions'"
          :title="'Exceptions and Errors together'"
          :loading="loading"
        />
      </div>
    </div>
  </div>
</template>

<script>
import MistakeOne from "./components/MistakeOne.vue";
import { getTopNErrorsAndExceptions } from "@/api/analysis";
import TimeLine from "@/components/TimeLine";
import dayjs from "dayjs";

export default {
  name: "Mistake",
  components: {
    MistakeOne,
    TimeLine,
  },
  data() {
    return {
      chartData: null,
      errorData: [],
      exceptionData: [],
      mixedData: [],
      topN: 10,
      startDate: null,
      endDate: null,
      loading: false,
    };
  },
  methods: {
    debounceCountChange() {
      clearTimeout(this.countTimeout);
      this.countTimeout = setTimeout(() => {
        this.initData();
        this.initMixedData();
      }, 200);
    },
    handleTimeRangeUpdate(dateRange) {
      this.startDate = dateRange["start"];
      this.endDate = dateRange["end"];

      this.initData();
      this.initMixedData();
    },
    async initData() {
      const start = this.startDate
        ? dayjs(this.startDate).format("YYYY-MM-DD HH:mm")
        : null;
      const end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;
      let params = {
        n: this.topN,
        start,
        end,
      };
      const res = await getTopNErrorsAndExceptions(params);
      this.chartData = res.data;
      this.errorData = this.chartData.errors;
      this.exceptionData = this.chartData.exceptions;
      // console.log(this.chartData, this.errorData, this.exceptionData);
    },
    async initMixedData() {
      const start = this.startDate
        ? dayjs(this.startDate).format("YYYY-MM-DD HH:mm")
        : null;
      const end = this.endDate
        ? dayjs(this.endDate).format("YYYY-MM-DD HH:mm")
        : null;
      let params = {
        n: this.topN,
        start,
        end,
        mixed: true,
      };
      const res = await getTopNErrorsAndExceptions(params);
      this.mixedData = res.data;
      // console.log(this.mixedData);
    },
  },
  created() {
    this.initData();
    this.initMixedData();
  },
};
</script>

<style scoped></style>
