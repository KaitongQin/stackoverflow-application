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
        Common Mistake
      </h1>
      <br>
      <p style="font-size: 22px;">
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mrow>
            <mo>
              Sort by
            </mo>
            <mi style="color: #f8ee37;">
              Number_of_questions
            </mi>
            <mo> and </mo>
            <mi style="color: #b275e3;">
              Average_views
            </mi>
            <mo> to find the top N most frequently mentioned exception or error.</mo>
          </mrow>
        </math>

        <br>
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>You can adjust the ratio and the number N to display below.</mi>
        </math>
        <br>
        <br>
        <v-img
          class="mb-4"
          height="160"
          src="@/assets/CommonMistake.png"
        />
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>Weighted</mi>
          <mi>Score</mi>
          <mo>=</mo>
          <mi>w</mi>
          <mo>&#x00D7;</mo>
          <mi style="color: #f8ee37;">
            Number_of_questions
          </mi>
          <mo>+</mo>
          <mi>(1 - w)</mi>
          <mo>&#x00D7;</mo>
          <mi style="color: #b275e3;">
            Average_views
          </mi>
        </math>
        <br>
      </p>
      <br>
    </div>


    <v-row>
      <v-col cols="12" sm="6">
        <v-slider
          v-model="w"
          label="w"
          min="0"
          max="1"
          step="0.01"
        ></v-slider>
      </v-col>
      <v-col cols="12" sm="6">
        <v-slider
          v-model="N"
          label="N"
          min="6"
          max="20"
          step="1"
        ></v-slider>
      </v-col>
    </v-row>

    <!-- 新增部分：选择框 -->
    <v-row justify="center">
      <v-col cols="4">
        <v-select
          v-model="filter"
          :items="['error', 'exception', null]"
          label="Choose Filter"
          outlined
          @change="fetchTopics"
          hide-details
        ></v-select>
      </v-col>
    </v-row>
    <br>


    <!-- Confirmation Button -->
    <div style="display: flex; justify-content: center;">
      <v-btn
        color="primary"
        @click="fetchTopics"
      >
        Explore Now
      </v-btn>
    </div>
    <br>

    <v-row>
      <!-- 第一行右侧 Card -->
      <v-col cols="12">
        <v-card style="background-color: #333">
          <v-card-title style="color: #fff"/>
          <v-card-text>
            <div id="pieChart" style="height: 400px;" />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 第二行 Card -->
    <v-row>
      <!-- 第二行左侧 Card (WeightedScore Histogram) -->
      <v-col cols="6">
        <v-card style="background-color: #333; color: #fff">
          <v-card-title> </v-card-title>
          <v-card-text>
            <div id="barChartFrequency" style="height: 380px;"></div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 第二行右侧 Card (Frequency Histogram) -->
      <v-col cols="6">
        <v-card style="background-color: #333; color: #fff">
          <v-card-title> </v-card-title>
          <v-card-text>
            <div id="barChartAvgView" style="height: 380px;"></div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import * as echarts from 'echarts';
import axios from 'axios';

let barChartFrequency;
let barChartAvgView;

export default {
  name: 'Dashboard',
  data() {
    return {
      w: 0.5,
      N: 13,
      filter: null,
      pieChart: null,
    };
  },

  methods: {

    async fetchTopics() {
      try {
        const response = await axios.get('/api/v1/error', {
          params: { weight: this.w, n: this.N, filter: this.filter },
        });
        const data = response.data;
        this.updateCharts(data);
      } catch (error) {
        console.error('Error fetching topics:', error);
      }
    },
    initCharts() {
      this.pieChart = echarts.init(document.getElementById('pieChart'));
      barChartFrequency = echarts.init(document.getElementById('barChartFrequency'));
      barChartAvgView = echarts.init(document.getElementById('barChartAvgView'));


      this.pieChart.setOption({
        title: {
          text: 'Mistake Distribution',
          left: 'center',
          textStyle: { color: '#fff' }
        },
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',   // This makes the legend vertical
          left: 0,         // Auto left alignment
          right: 'auto',             // Align to the right side
          top: 'center',        // Center vertically
          textStyle: { color: '#fff' },
        },
        series: [{
          name: 'Topics',
          type: 'pie',
          radius: '70%',
          center: ['72%', '55%'], // 将饼图中心水平向左移动，原始默认值为 ['50%', '50%']
          data: [],
          label: { color: '#fff' }
        }],
        backgroundColor: '#333',
      });

      barChartFrequency.setOption({
        title: {
          text: 'Number of Questions',
          left: 'center',
          textStyle: { color: '#fff' }
        },
        tooltip: {
          trigger: "axis",
          textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
          backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
        },
        grid: {
          top: '10%',      // 上边距，向上移动条形图
          bottom: '30%',   // 下边距
          left: '10%',     // 左边距
          right: '5%'     // 右边距
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: { rotate: 30, color: '#fff' },
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: [],
          itemStyle: { color: '#f8ee37' }
        }],
        backgroundColor: '#333',
      });


      barChartAvgView.setOption({
        title: {
          text: 'Average View Count',
          left: 'center',
          textStyle: { color: '#fff' }
        },
        tooltip: {
          trigger: "axis",
          textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
          backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
        },
        grid: {
          top: '10%',      // 上边距，向上移动条形图
          bottom: '30%',   // 下边距
          left: '10%',     // 左边距
          right: '5%'     // 右边距
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: { rotate: 30, color: '#fff' }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: [],
          itemStyle: { color: '#b275e3' }
        }],
        backgroundColor: '#333',
      });

    },
    updateCharts(topics) {
      const pieData = topics.map((topic) => ({ value: topic.normalizedWeightedScore, name: topic.errorName, errorType: topic.errorType }));
      const categories = topics.map((topic) => topic.errorName);
      const frequencies = topics.map((topic) => topic.baseFrequency);
      const avgViews = topics.map((topic) => topic.avgViewCount);
      const type = topics.map((topic) => topic.errorType);

      this.pieChart.setOption({
        series: [{ data: pieData }],
        tooltip: {
          trigger: "item",
          formatter: (params) => {
            // 提取参数中的信息
            const { name, value, data } = params;
            return `
          <strong>Error Name:</strong> ${name}<br/>
          <strong>Weighted Score:</strong> ${value}<br/>
          <strong>Error Type:</strong> ${data.errorType}
        `;
          },
        },
      });
      const updateBarChart = (chart, data, seriesData, errorTypeData) => {
        chart.setOption({
          xAxis: { data },
          series: [{ data: seriesData }],
          tooltip: {
            formatter: (params) => {
              const index = params[0].dataIndex;
              return `
                Error Name: ${data[index]}<br/>
                Value: ${seriesData[index]}<br/>
                Error Type: ${errorTypeData[index]}
              `;
            },
          },
        });
      };

      updateBarChart(barChartFrequency, categories, frequencies, type);
      updateBarChart(barChartAvgView, categories, avgViews, type);
    },
  },
  mounted() {
    this.initCharts();
    this.fetchTopics();
  },
};
</script>

<style scoped>
/* Custom styles */
</style>
