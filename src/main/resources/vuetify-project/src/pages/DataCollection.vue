<template>
  <v-container>
    <!-- Page Title -->
    <br>
    <v-img
      class="mb-4"
      height="150"
      src="@/assets/java.png"
    />
    <div class="text-center">
      <h1 class="text-h2 font-weight-bold">
        Data Collection
      </h1>
      <br>
      <p style="font-size: 22px;">
        <math xmlns="http://www.w3.org/1998/Math/MathML">
          <mrow>
            <mo>This page provides an overview of the data we collected, including tables and their main fields.</mo>
          </mrow>
        </math>
      </p>
    </div>
    <br>

    <v-row>
      <v-col cols="12">
        <v-card outlined>
          <v-card-title class="text-h5">
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" sm="6">
                <v-card
                  :variant="variant"
                  class="mx-auto"
                  color="surface-variant"
                  max-width="500">
                  <v-card-title class="text-h6">Total Questions</v-card-title>
                  <v-card-text>
                    <h1 class="text-center">{{ questionNum }}</h1>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6">
                <v-card
                  :variant="variant"
                  class="mx-auto"
                  color="surface-variant"
                  max-width="500">
                  <v-card-title class="text-h6">Total Answers</v-card-title>
                  <v-card-text>
                    <h1 class="text-center">{{ answerNum }}</h1>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6">
                <v-card
                  :variant="variant"
                  class="mx-auto"
                  color="surface-variant"
                  max-width="500">
                  <v-card-title class="text-h6">Total Comments</v-card-title>
                  <v-card-text>
                    <h1 class="text-center">{{ commentNum }}</h1>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6">
                <v-card
                  :variant="variant"
                  class="mx-auto"
                  color="surface-variant"
                  max-width="500">
                  <v-card-title class="text-h6">Total Users</v-card-title>
                  <v-card-text>
                    <h1 class="text-center">{{ userNum }}</h1>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6">
                <v-card
                  :variant="variant"
                  class="mx-auto"
                  color="surface-variant"
                  max-width="500">
                  <v-card-title class="text-h6">Total Tags</v-card-title>
                  <v-card-text>
                    <h1 class="text-center">{{ tagNum }}</h1>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6">
                <v-card
                  :variant="variant"
                  class="mx-auto"
                  color="surface-variant"
                  max-width="500">
                  <v-card-title class="text-h6">Total Errors</v-card-title>
                  <v-card-text>
                    <h1 class="text-center">{{ errorNum }}</h1>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Database Tables Overview -->
    <v-row>
      <v-col cols="12">
        <v-card outlined>
          <v-card-title class="text-h5">
            Database Tables
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <v-list-item-title class="font-weight-bold">question</v-list-item-title>
                <v-list-item-subtitle>
                  Stores details of questions, including title, body, view count, and creation date.
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title class="font-weight-bold">answer</v-list-item-title>
                <v-list-item-subtitle>
                  Contains answers to questions with details such as score, acceptance status, and owner ID.
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title class="font-weight-bold">comment</v-list-item-title>
                <v-list-item-subtitle>
                  Stores comments related to questions and answers, along with their scores and timestamps.
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title class="font-weight-bold">user</v-list-item-title>
                <v-list-item-subtitle>
                  Information about users, including their reputation, type, and profile links.
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title class="font-weight-bold">error_occurrence</v-list-item-title>
                <v-list-item-subtitle>
                  Logs occurrences of errors in questions or answers, categorized by type and severity.
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Data Insights Section -->

  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'DataCollection',
  data() {
    return {
      questionNum: 0,
      answerNum: 0,
      commentNum: 0,
      userNum: 0,
      tagNum: 0,
      errorNum: 0,
    };
  },
  mounted() {
    // 请求数据
    this.fetchData();
  },
  methods: {
    async fetchData() {
      try {
        const response = await axios.get('/api/v1/overview');
        const data = response.data[0];
        console.log(data);

        // 更新数据
        this.questionNum = data.questionNum || 0;
        this.answerNum = data.answerNum || 0;
        this.commentNum = data.commentNum || 0;
        this.userNum = data.userNum || 0;
        this.tagNum = data.tagNum || 0;
        this.errorNum = data.errorNum || 0;
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    },
  },
};

</script>

<style scoped>
.text-center {
  text-align: center;
}
</style>
