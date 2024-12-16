<template>
  <v-container>
    <v-btn color="primary" @click="goBack">Back to Index</v-btn>
    <br>
    <v-img
      class="mb-4"
      height="150"
      src="@/assets/java.png"
    />
    <div class="text-center">
      <h1 class="text-h2 font-weight-bold">
        Top Java Topics
      </h1>
      <br>
      <p style="font-size: 22px;">
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mrow>
            <mo>
              Sort by
            </mo>
            <mi style="color: #377af8;">
              Number_of_questions
            </mi>
            <mo> and </mo>
            <mi style="color: #7be375;">
              Average_views
            </mi>
            <mo> to find the top N most frequently mentioned Java topics.</mo>
          </mrow>
        </math>

        <br>
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>You can adjust the ratio and the number N to display below.</mi>
        </math>
        <v-img
          class="mb-4"
          height="220"
          src="@/assets/JavaTopics.png"
        />
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>Weighted</mi>
          <mi>Score</mi>
          <mo>=</mo>
          <mi>w</mi>
          <mo>&#x00D7;</mo>
          <mi>Number_of_questions</mi>
          <mo>+</mo>
          <mi>(1 - w)</mi>
          <mo>&#x00D7;</mo>
          <mi>Average_views</mi>
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
          append-icon="mdi-numeric"
          append-icon-cb="() => {}"
          thumb-label
        ></v-slider>
      </v-col>
      <v-col cols="12" sm="6">
        <v-slider
          v-model="N"
          label="N"
          min="6"
          max="20"
          step="1"
          append-icon="mdi-numeric"
          append-icon-cb="() => {}"
          thumb-label
        ></v-slider>
      </v-col>
    </v-row>

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
            <div id="barChartFrequency" style="height: 380px;" />
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 第二行右侧 Card (Frequency Histogram) -->
      <v-col cols="6">
        <v-card style="background-color: #333; color: #fff">
          <v-card-title> </v-card-title>
          <v-card-text>
            <div id="barChartAvgView" style="height: 380px;" />
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
        const response = await axios.get('/api/v1/topic', {
          params: { weight: this.w, n: this.N, filter: this.filter },
        });
        const data = response.data;
        console.log(data);
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
        title: { text: 'Topics Distribution', left: 'center', textStyle: { color: '#fff' } },
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',   // This makes the legend vertical
          left: 'auto',         // Auto left alignment
          right: 0,             // Align to the right side
          top: 'center',        // Center vertically
          textStyle: { color: '#fff' },
        },
        series: [{
          name: 'Topics',
          type: 'pie',
          radius: '70%',
          data: [],
          label: { color: '#fff' }
        }],
        backgroundColor: '#333',
      });
      barChartFrequency.setOption({
        title: { text: 'Number of Questions', left: 'center', textStyle: { color: '#fff' } },
        xAxis: { type: 'category', data: [], axisLabel: { rotate: 45, color: '#fff' } },
        yAxis: { type: 'value' },
        tooltip: {
          trigger: "axis",
          textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
          backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
        },
        series: [{ type: 'bar', data: [], itemStyle: { color: '#377af8' } }],
        backgroundColor: '#333',
      });

      barChartAvgView.setOption({
        title: { text: 'Average View Count', left: 'center', textStyle: { color: '#fff' } },
        xAxis: { type: 'category', data: [], axisLabel: { rotate: 45, color: '#fff' } },
        yAxis: { type: 'value' },
        tooltip: {
          trigger: "axis",
          textStyle: { color: "#FFFFFF" }, // Tooltip文字颜色
          backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
        },
        series: [{ type: 'bar', data: [], itemStyle: { color: '#7be375' } }],
        backgroundColor: '#333',
      });
    },
    updateCharts(topics) {
      const pieData = topics.map((topic) => ({ value: topic.weightedScore, name: topic.tagName }));
      const categories = topics.map((topic) => topic.tagName);
      const frequencies = topics.map((topic) => topic.baseFrequency);
      const avgViews = topics.map((topic) => topic.avgViewCount);

      this.pieChart.setOption({ series: [{ data: pieData }] });
      barChartFrequency.setOption({ xAxis: { data: categories }, series: [{ data: frequencies }] });
      barChartAvgView.setOption({ xAxis: { data: categories }, series: [{ data: avgViews }] });
    },
    goBack() {
      this.$router.push('/');
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
