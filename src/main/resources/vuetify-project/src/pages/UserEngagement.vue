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
        User Engagement
      </h1>
      <br>
      <p style="font-size: 22px;">
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>We sort by TotalScore to find the top N most engaged Java topics.</mi>
        </math>
        <br>
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>You can adjust the N topics to show and the metric how to check high reputation.</mi>
        </math>
        <v-img
          class="mb-4"
          height="240"
          src="@/assets/UserEngagement.png"
        />

        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>score</mi>
          <mo>=</mo>
          <mi>AVG</mi>
          <mo>(</mo>
          <mn>5</mn>
          <mo>&#x00D7;</mo>
          <mi style="color: #377af8;">
            AnswerNum
          </mi>
          <mo>+</mo>
          <mn>2</mn>
          <mo>&#x00D7;</mo>
          <mi style="color: #7be375;">
            CommentNum
          </mi>
          <mo>+</mo>
          <mn>2</mn>
          <mo>&#x00D7;</mo>
          <mi style="color: #fa8b0c;">
            RevisionNum
          </mi>
          <mo>+</mo>
          <mn>1</mn>
          <mo>&#x00D7;</mo>
          <mi style="color: #b275e3;">
            VoteNum
          </mi>
          <mo>)</mo>
        </math>
        <br>

        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>engagementScore</mi>
          <mo>=</mo>
          <mrow>
            <munderover>
              <mo>∑</mo>
              <mrow>
                <mi>u</mi>
                <mo>∈</mo>
                <mi>All_users</mi>
              </mrow>
            </munderover>
            <mo>(</mo>
            <mi>score</mi>
            <mo>)</mo>
          </mrow>
        </math>
        <br>

        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mi>highReputationEngagementScore</mi>
          <mo>=</mo>
          <mrow>
            <munderover>
              <mo>∑</mo>
              <mrow>
                <mi>u</mi>
                <mo>∈</mo>
                <mi>high_reputation</mi>
              </mrow>
            </munderover>
            <mo>(</mo>
            <mi>score</mi>
            <mo>)</mo>
          </mrow>
        </math>
        <br>
      </p>
      <br>
    </div>

    <v-row>
      <v-col cols="12" sm="6">
        <v-slider
          v-model="n"
          label="n"
          min="4"
          max="10"
          step="1"
          append-icon="mdi-numeric"
          append-icon-cb="() => {}"
          thumb-label
        ></v-slider>
      </v-col>
      <v-col cols="12" sm="6">
        <v-slider
          v-model="R"
          label="R"
          min="500"
          max="100000"
          step="50"
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
        @click="fetchData"
      >
        Explore Now
      </v-btn>
    </div>
    <br>

    <v-row>
      <!-- 第一行右侧 Card -->
      <v-col cols="6">
        <v-card style="background-color: #333">
          <v-card-title style="color: #fff"/>
          <v-card-text>
            <div id="AllData" style="height: 500px;"/>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="6">
        <v-card style="background-color: #333">
          <v-card-title style="color: #fff"/>
          <v-card-text>
            <div id="HighReputation" style="height: 500px;"/>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="6">
        <v-card style="background-color: #333; color: #fff">
          <v-card-title/>
          <v-card-text>
            <div id="AllChart" style="height: 610px;"/>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="6">
        <v-card style="background-color: #333; color: #fff">
          <v-card-title></v-card-title>
          <v-card-text>
            <div id="HighReputationChart" style="height: 610px;"/>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";
import * as echarts from "echarts";

let AllChart;
let HighReputationChart;
export default {
  data() {
    return {
      n: 7,
      R: 1000,
      AllData: null,
      HighReputation: null,
    };
  },
  methods: {
    async fetchData() {
      try {
        const response = await axios.get("/api/v1/participation", {
          params: {n: this.n, R: this.R},
        });
        const data = response.data;
        console.log(data);
        this.updateCharts(data);
      } catch (error) {
        console.log(error);
      }
    },
    initCharts() {
      this.AllData = echarts.init(document.getElementById("AllData"));
      this.HighReputation = echarts.init(document.getElementById("HighReputation"));
      AllChart = echarts.init(document.getElementById("AllChart"));
      HighReputationChart = echarts.init(document.getElementById("HighReputationChart"));

      // Pie chart initialization
      this.AllData.setOption({
        title: {text: 'Top Total Engagement', left: 'left', textStyle: {color: '#fff'}},
        tooltip: {trigger: 'item'},
        legend: {
          orient: 'horizontal',
          left: 'auto',
          bottom: 0,
          textStyle: {color: '#fff'},
        },
        series: [
          {
            name: 'Topics',
            type: 'pie',
            radius: '50%',
            data: [],
            label: {color: '#fff'},
          },
        ],
        backgroundColor: '#333',
      });

      this.HighReputation.setOption({
        title: {text: 'Top HighReputation Engagement', left: 'left', textStyle: {color: '#fff'}},
        tooltip: {trigger: 'item'},
        legend: {
          orient: 'horizontal',
          left: 'auto',
          bottom: 0,
          textStyle: {color: '#fff'},
        },
        series: [
          {
            name: 'Topics',
            type: 'pie',
            radius: '50%',
            data: [],
            label: {color: '#fff'},
          },
        ],
        backgroundColor: '#333',
      });

      // Bar chart initialization
      AllChart.setOption({
        title: {
          text: 'Overall Engagement Metrics',
          textStyle: {color: '#fff'},
          left: 'center' // 标题居中
        },
        tooltip: {
          trigger: "axis",
          textStyle: {color: "#FFFFFF"}, // Tooltip文字颜色
          backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
        },
        grid: {
          top: '10%',      // 上边距，向上移动条形图
          bottom: '20%',   // 下边距
          left: '10%',     // 左边距
          right: '5%'     // 右边距
        },
        legend: {
          data: ['AnswerNum', 'CommentNum', 'RevisionNum', 'VoteNum'],
          textStyle: {color: '#fff'},
          bottom: -5,    // 图例放到下方
          left: 'center' // 图例居中
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {
            color: '#fff',
            rotate: 45  // 让标签倾斜45度
          },
        },
        yAxis: {type: 'value', axisLabel: {color: '#fff'}},
        series: [
          {name: 'AnswerNum', type: 'bar', data: [], itemStyle: {color: '#377af8'}},
          {name: 'CommentNum', type: 'bar', data: [], itemStyle: {color: '#7be375'}},
          {name: 'RevisionNum', type: 'bar', data: [], itemStyle: {color: '#fa8b0c'}},
          {name: 'VoteNum', type: 'bar', data: [], itemStyle: {color: '#b275e3'}},
        ],
        backgroundColor: '#333',
      });

      HighReputationChart.setOption({
        title: {
          text: 'High Reputation Engagement Metrics',
          textStyle: {color: '#fff'},
          left: 'center' // 标题居中
        },
        tooltip: {
          trigger: "axis",
          textStyle: {color: "#FFFFFF"}, // Tooltip文字颜色
          backgroundColor: "rgba(50, 50, 50, 0.9)", // Tooltip背景颜色
        },
        grid: {
          top: '10%',      // 上边距，向上移动条形图
          bottom: '20%',   // 下边距
          left: '10%',     // 左边距
          right: '5%'     // 右边距
        },
        legend: {
          data: ['AnswerNum', 'CommentNum', 'RevisionNum', 'VoteNum'],
          textStyle: {color: '#fff'},
          bottom: -5,    // 图例放到下方
          left: 'center' // 图例居中
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {rotate: 45, color: '#fff'},
        },
        yAxis: {type: 'value', axisLabel: {color: '#fff'}},
        series: [
          {name: 'AnswerNum', type: 'bar', data: [], itemStyle: {color: '#377af8'}},
          {name: 'CommentNum', type: 'bar', data: [], itemStyle: {color: '#7be375'}},
          {name: 'RevisionNum', type: 'bar', data: [], itemStyle: {color: '#fa8b0c'}},
          {name: 'VoteNum', type: 'bar', data: [], itemStyle: {color: '#b275e3'}},
        ],
        backgroundColor: '#333',
      });

    },
    updateCharts(data) {
      // Pie chart data
      const AllData_data = data.map((topic) => ({value: topic.engagementScore, name: topic.tagName}));
      const HighReputation_data = data.map((topic) => ({
        value: topic.highReputationEngagementScore,
        name: topic.highReputationTagName,
      }));

      this.AllData.setOption({series: [{data: AllData_data}]});
      this.HighReputation.setOption({series: [{data: HighReputation_data}]});

      // Bar chart data
      const tagNames = data.map((topic) => topic.tagName);
      const answerNums = data.map((topic) => topic.answerNum);
      const commentNums = data.map((topic) => topic.commentNum);
      const revisionNums = data.map((topic) => topic.revisionNum);
      const voteNums = data.map((topic) => topic.voteNum);

      AllChart.setOption({
        xAxis: {data: tagNames},
        series: [
          {name: 'AnswerNum', data: answerNums},
          {name: 'CommentNum', data: commentNums},
          {name: 'RevisionNum', data: revisionNums},
          {name: 'VoteNum', data: voteNums},
        ],
      });

      const highRepTagNames = data.map((topic) => topic.highReputationTagName);
      const highRepAnswerNums = data.map((topic) => topic.highReputationAnswerNum);
      const highRepCommentNums = data.map((topic) => topic.highReputationCommentNum);
      const highRepRevisionNums = data.map((topic) => topic.highReputationRevisionNum);
      const highRepVoteNums = data.map((topic) => topic.highReputationVoteNum);

      HighReputationChart.setOption({
        xAxis: {data: highRepTagNames},
        series: [
          {name: 'AnswerNum', data: highRepAnswerNums},
          {name: 'CommentNum', data: highRepCommentNums},
          {name: 'RevisionNum', data: highRepRevisionNums},
          {name: 'VoteNum', data: highRepVoteNums},
        ],
      });
    },
    goBack() {
      this.$router.push('/');
    },
  },
  mounted() {
    this.initCharts();
    this.fetchData();
  },
};

</script>


<style scoped>
/* Custom Styles */
</style>
