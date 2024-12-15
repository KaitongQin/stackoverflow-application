<template>
  <v-container>
    <br>
    <v-img
      class="mb-4"
      height="150"
      src="@/assets/java.png"
    />
    <div class="text-center">
      <h1 class="text-h2 font-weight-bold">
        Answer Quality
      </h1>
    </div>
    <br>

    <!-- 输入框区域 -->
    <v-row>
      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.acceptedWeight"
          label="Accepted Weight"
          type="number"
          min="0"
          max="1"
          step="0.01"
          :rules="[v => v >= 0 && v <= 1 || 'Accepted weight must be between 0 and 1']"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.minGroupSize"
          label="Min Group Size"
          type="number"
          min="1"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.maxResults"
          label="Max Results"
          type="number"
          min="1"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.timeGapBuckets"
          label="Time Gap Buckets"
          type="number"
          min="1"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.reputationBuckets"
          label="Reputation Buckets"
          type="number"
          min="1"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.lengthBuckets"
          label="Length Buckets"
          type="number"
          min="1"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.minAnswerLength"
          label="Min Answer Length"
          type="number"
          min="0"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.maxAnswerLength"
          label="Max Answer Length"
          type="number"
          min="1"
        ></v-text-field>
      </v-col>

      <v-col cols="12" md="4">
        <v-text-field
          v-model.number="params.maxTimeGap"
          label="Max Time Gap"
          type="number"
          min="0"
        ></v-text-field>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" class="text-center">
        <!-- 更新按钮 -->
        <v-btn @click="updateData" color="primary">Update Data</v-btn>
      </v-col>
    </v-row>

    <br>

    <!-- 四个v-card-->
    <v-row>
      <!-- Basic Stats & Correlation -->
      <v-col cols="12" md="6">
        <v-card class="pa-4" outlined>
          <h2 class="text-h5 text-center">Basic Stats</h2>
          <v-data-table
            :headers="basicStatsHeaders"
            :items="Object.entries(basicStats)"
            item-value="value"
            item-text="key"
            dense
            hide-default-footer
          >
            <template v-slot:item="props">
              <tr :key="props.item[0]">
                <td>{{ props.item[0] }}</td>
                <td>{{ props.item[1] }}</td>
              </tr>
            </template>
          </v-data-table>

          <h2 class="text-h5 mt-4 text-center">Correlation</h2>
          <v-data-table
            :headers="correlationHeaders"
            :items="Object.entries(correlations)"
            item-value="value"
            item-text="key"
            dense
            hide-default-footer
          >
            <template v-slot:item="props">
              <tr :key="props.item[0]">
                <td>{{ props.item[0] }}</td>
                <td>{{ props.item[1].coefficient }}</td>
                <td>{{ props.item[1].pvalue }}</td>
              </tr>
            </template>
          </v-data-table>
        </v-card>
      </v-col>

      <!-- Time Gap Groups -->
      <v-col cols="12" md="6">
        <v-card class="pa-4" outlined>
          <h2 class="text-h5 text-center">Time Gap VS Quality Score</h2>
          <div ref="timeGapChart" style="width: 100%; height: 492px;"></div>
        </v-card>
      </v-col>

      <!-- Reputation Groups -->
      <v-col cols="12" md="6">
        <v-card class="pa-4" outlined>
          <h2 class="text-h5 text-center">Reputation VS Quality Score</h2>
          <div ref="reputationChart" style="width: 100%; height: 450px;"></div>
        </v-card>
      </v-col>

      <!-- Answer Length Groups -->
      <v-col cols="12" md="6">
        <v-card class="pa-4" outlined>
          <h2 class="text-h5 text-center">Answer Length VS Quality Score</h2>
          <div ref="lengthChart" style="width: 100%; height: 450px;"></div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import * as echarts from "echarts";
import axios from "axios";

// 引用图表容器
const timeGapChart = ref<HTMLElement | null>(null);
const reputationChart = ref<HTMLElement | null>(null);
const lengthChart = ref<HTMLElement | null>(null);

// 表格数据存储
const basicStats = ref<any>({});
const correlations = ref<any>({});

// 表格头部信息
const basicStatsHeaders = ref([
  { title: 'Metric', align: 'start', key: 'key' },
  { title: 'Value', align: 'start', key: 'value' },
]);

const correlationHeaders = ref([
  { title: 'Metric', align: 'start', key: 'key' },
  { title: 'Coefficient', align: 'start', key: 'coefficient' },
  { title: 'P-Value', align: 'start', key: 'pvalue' },
]);

// 数据请求参数
const params = ref({
  scoreWeight: 0.5,
  acceptedWeight: 0.5,
  minGroupSize: 10,
  maxResults: 1000,
  timeGapBuckets: 24,
  reputationBuckets: 10,
  lengthBuckets: 20,
  minAnswerLength: 100,
  maxAnswerLength: 5000,
  maxTimeGap: 86400,
});

const updateData = () => {
  // 确保 acceptedWeight 和 scoreWeight 之和为 1
  params.value.scoreWeight = 1 - params.value.acceptedWeight;
  fetchData();
};

// 获取后端数据
const fetchData = async () => {
  try {
    const response = await axios.post("/api/v1/answer-quality", { ...params.value }); // 解包 params
    const { groupedStats, correlations: corrData, basicStats: stats } = response.data;

    // 更新表格数据
    basicStats.value = stats;
    correlations.value = corrData;

    // 调用图表渲染方法
    renderTimeGapChart(groupedStats.timeGapGroups);
    renderReputationChart(groupedStats.reputationGroups);
    renderLengthChart(groupedStats.lengthGroups);
  } catch (error) {
    console.error("Error fetching data: ", error);
  }
};


// 渲染时间间隔分组图表
const renderTimeGapChart = (data: any[]) => {
  const chart = echarts.init(timeGapChart.value);
  const option = {
    grid: {
      top: '10%',      // 上边距，向上移动条形图
      bottom: '30%',   // 下边距
      left: '10%',     // 左边距
      right: '5%'     // 右边距
    },
    xAxis: {
      type: "category",
      data: data.map(d => `${d.rangeStart}-${d.rangeEnd}`),
      axisLine: { lineStyle: { color: "#FFFFFF" } }, // X轴线条颜色
      axisLabel: { color: "#FFFFFF" }, // X轴标签颜色
    },
    yAxis: [
      {
        type: "value",
        name: "Count",
        axisLine: { lineStyle: { color: "#ffffff" } }, // Y轴线条颜色
        axisLabel: { color: "#FFFFFF" }, // Y轴标签颜色
        splitLine: { show: false }, // 隐藏网格线
      },
      {
        type: "value",
        name: "Score/Rate",
        position: "right",
        axisLine: { lineStyle: { color: "#ffffff" } }, // Y轴右侧线条颜色
        axisLabel: { color: "#FFFFFF" }, // Y轴右侧标签颜色
        splitLine: { show: false }, // 隐藏网格线
      },
    ],
    series: [
      {
        name: "Count",
        type: "bar",
        data: data.map(d => d.count),
        itemStyle: { color: "#aff6aa" }, // Bright green for Count
      },
      {
        name: "Average Quality Score",
        type: "line",
        data: data.map(d => d.avgQualityScore),
        smooth: true,
        yAxisIndex: 1, // Use the second yAxis
        lineStyle: { color: "#22dd22", width: 2 }, // Red for clarity
        itemStyle: { color: "#22dd22" },
      },
      {
        name: "Accepted Rate",
        type: "line",
        data: data.map(d => d.acceptedRate),
        smooth: true,
        yAxisIndex: 1, // Use the second yAxis
        lineStyle: { color: "#0a7cff", width: 2 }, // Blue for clarity
        itemStyle: { color: "#0a7cff" },
      },
    ],
    tooltip: {
      trigger: "axis",
      textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
      backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
    },
    legend: {
      data: ["Count", "Average Quality Score", "Accepted Rate"],
      orient: "horizontal",
      bottom: 10, // Place legend at the bottom
      textStyle: {
        color: "#FFFFFF", // 图例文字颜色
        fontSize: 12,
      },
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '15%', // Adjust to fit legend at the bottom
      top: '15%',
    },
  };
  chart.setOption(option);
};

// 渲染声望分组图表
const renderReputationChart = (data: any[]) => {
  const chart = echarts.init(reputationChart.value);
  const option = {
    xAxis: {
      type: "category",
      data: data.map(d => `${d.rangeStart}-${d.rangeEnd}`),
      axisLine: { lineStyle: { color: "#FFFFFF" } }, // X轴线条颜色
      axisLabel: { color: "#FFFFFF" }, // X轴标签颜色
    },
    yAxis: [
      {
        type: "value",
        name: "Count",
        axisLine: { lineStyle: { color: "#ffffff" } }, // Y轴线条颜色
        axisLabel: { color: "#FFFFFF" }, // Y轴标签颜色
        splitLine: { show: false }, // 隐藏网格线
      },
      {
        type: "value",
        name: "Score/Rate",
        position: "right",
        axisLine: { lineStyle: { color: "#ffffff" } }, // Y轴右侧线条颜色
        axisLabel: { color: "#FFFFFF" }, // Y轴右侧标签颜色
        splitLine: { show: false }, // 隐藏网格线
      },
    ],
    series: [
      {
        name: "Count",
        type: "bar",
        data: data.map(d => d.count),
        itemStyle: { color: "#faf69a" }, // Bright green for Count
      },
      {
        name: "Average Quality Score",
        type: "line",
        data: data.map(d => d.avgQualityScore),
        smooth: true,
        yAxisIndex: 1, // Use the second yAxis
        lineStyle: { color: "#fa670c", width: 2 }, // Red for clarity
        itemStyle: { color: "#fa670c" },
      },
      {
        name: "Accepted Rate",
        type: "line",
        data: data.map(d => d.acceptedRate),
        smooth: true,
        yAxisIndex: 1, // Use the second yAxis
        lineStyle: { color: "#f83e3e", width: 2 }, // Blue for clarity
        itemStyle: { color: "#f83e3e" },
      },
    ],
    tooltip: {
      trigger: "axis",
      textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
      backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
    },
    legend: {
      data: ["Count", "Average Quality Score", "Accepted Rate"],
      orient: "horizontal",
      bottom: 10, // Place legend at the bottom
      textStyle: {
        color: "#FFFFFF", // 图例文字颜色
        fontSize: 12,
      },
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '15%', // Adjust to fit legend at the bottom
      top: '15%',
    },
  };
  chart.setOption(option);
};

// 渲染回答长度分组图表
const renderLengthChart = (data: any[]) => {
  const chart = echarts.init(lengthChart.value);
  const option = {
    xAxis: {
      type: "category",
      data: data.map(d => `${d.rangeStart}-${d.rangeEnd}`),
      axisLine: { lineStyle: { color: "#FFFFFF" } }, // X轴线条颜色
      axisLabel: { color: "#FFFFFF" }, // X轴标签颜色
    },
    yAxis: [
      {
        type: "value",
        name: "Count",
        axisLine: { lineStyle: { color: "#ffffff" } }, // Y轴线条颜色
        axisLabel: { color: "#FFFFFF" }, // Y轴标签颜色
        splitLine: { show: false }, // 隐藏网格线
      },
      {
        type: "value",
        name: "Score/Rate",
        position: "right",
        axisLine: { lineStyle: { color: "#ffffff" } }, // Y轴右侧线条颜色
        axisLabel: { color: "#FFFFFF" }, // Y轴右侧标签颜色
        splitLine: { show: false }, // 隐藏网格线
      },
    ],
    series: [
      {
        name: "Count",
        type: "bar",
        data: data.map(d => d.count),
        itemStyle: { color: "#80f4df" }, // Bright green for Count
      },
      {
        name: "Average Quality Score",
        type: "line",
        data: data.map(d => d.avgQualityScore),
        smooth: true,
        yAxisIndex: 1, // Use the second yAxis
        lineStyle: { color: "#bf70ff", width: 2 }, // Red for clarity
        itemStyle: { color: "#bf70ff" },
      },
      {
        name: "Accepted Rate",
        type: "line",
        data: data.map(d => d.acceptedRate),
        smooth: true,
        yAxisIndex: 1, // Use the second yAxis
        lineStyle: { color: "#547eff", width: 2 }, // Blue for clarity
        itemStyle: { color: "#547eff" },
      },
    ],
    tooltip: {
      trigger: "axis",
      textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
      backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
    },
    legend: {
      data: ["Count", "Average Quality Score", "Accepted Rate"],
      orient: "horizontal",
      bottom: 10, // Place legend at the bottom
      textStyle: {
        color: "#FFFFFF", // 图例文字颜色
        fontSize: 12,
      },
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '15%', // Adjust to fit legend at the bottom
      top: '15%',
    },
  };
  chart.setOption(option);
};

// 页面挂载时获取数据并初始化图表
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.v-img {
  margin: auto;
}

h2 {
  margin-bottom: 20px;
}
</style>
