<template>
  <v-container>
    <v-row>
      <!-- Topic Query -->
      <v-col cols="6">
        <v-card>
          <v-card-title>Topic Frequency Query</v-card-title>
          <v-card-text>
            <br>
            <br>
            <v-row>
              <!-- 输入框：话题名称 -->
              <v-col>
                <v-text-field
                  label="Enter Topic Name"
                  v-model="topicQuery"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col>
                <!-- 查询按钮：查询指定话题 -->
                <v-btn color="primary" class="mt-2" @click="queryTopicFrequency">
                  Query Topic Frequency
                </v-btn>
              </v-col>
            </v-row>
            <v-row>
              <!-- 输入框：数量 N -->
              <v-col>
                <v-text-field
                  label="Enter Top N Topics"
                  v-model="topicTopN"
                  type="number"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col>
                <!-- 查询按钮：查询前 N 个话题 -->
                <v-btn color="primary" class="mt-2" @click="queryTopTopics">
                  Query Top N Topics
                </v-btn>
              </v-col>
            </v-row>
            <br>
            <br>
            <br>

          </v-card-text>
          <!-- 结果展示框 -->
          <v-card-subtitle>Result:</v-card-subtitle>
          <v-card-text>
            <v-textarea
              v-model="topicResult"
              outlined
              readonly
              rows="15"
            ></v-textarea>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Error Query -->
      <v-col cols="6">
        <v-card>
          <v-card-title>Error/Exception Frequency Query</v-card-title>
          <v-card-text>
            <v-row>
              <!-- 输入框：错误名称 -->
              <v-col>
                <v-text-field
                  label="Enter Error/Exception Name"
                  v-model="errorQuery"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col>
                <!-- 查询按钮：查询指定错误 -->
                <v-btn color="primary" class="mt-2" @click="queryErrorFrequency">
                  Query Frequency
                </v-btn>
              </v-col>
            </v-row>
            <v-row>
              <!-- 输入框：数量 N -->
              <v-col>
                <v-text-field
                  label="Enter Top N Errors"
                  v-model="errorTopN"
                  type="number"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col>
                <!-- 查询按钮：查询前 N 个错误 -->
                <v-btn color="primary" class="mt-2" @click="queryTopErrors">
                  Query Top N Errors
                </v-btn>
              </v-col>
            </v-row>
            <v-row>
              <!-- 输入框：数量 N -->
              <v-col>
                <v-text-field
                  label="Enter Top N Exceptions"
                  v-model="exceptionTopN"
                  type="number"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col>
                <!-- 查询按钮：查询前 N 个异常 -->
                <v-btn color="primary" class="mt-2" @click="queryTopExceptions">
                  Query Top N Exceptions
                </v-btn>
              </v-col>
            </v-row>

          </v-card-text>
          <!-- 结果展示框 -->
          <v-card-subtitle>Result:</v-card-subtitle>
          <v-card-text>
            <v-textarea
              v-model="errorResult"
              outlined
              readonly
              rows="15"
            ></v-textarea>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "RESTfulService",
  data() {
    return {
      topicQuery: "",
      topicTopN: 5,
      errorQuery: "",
      errorTopN: 5,
      exceptionTopN: 5, // 新增变量
      topicResult: "",
      errorResult: "",
    };
  },
  methods: {
    // 查询指定话题频率
    async queryTopicFrequency() {
      try {
        const response = await axios.get(`/api/v1/topic`, {
          params: {
            weight: 1,
            n: 1,
            filter: this.topicQuery,
          },
        });
        this.topicResult = JSON.stringify(response.data, null, 2);
      } catch (error) {
        console.error("Error querying topic frequency:", error);
        this.topicResult = JSON.stringify({ error: "Error fetching data." });
      }
    },
    // 查询前 N 个话题
    async queryTopTopics() {
      try {
        const response = await axios.get(`/api/v1/topic`, {
          params: {
            weight: 1,
            n: this.topicTopN,
            filter: null,
          },
        });
        this.topicResult = JSON.stringify(response.data, null, 2);
      } catch (error) {
        console.error("Error querying top topics:", error);
        this.topicResult = JSON.stringify({ error: "Error fetching data." });
      }
    },
    // 查询指定错误频率
    async queryErrorFrequency() {
      try {
        const response = await axios.get(`/api/v1/error/specific`, {
          params: {
            weight: 1,
            n: 1,
            filter: this.errorQuery,
          },
        });
        this.errorResult = JSON.stringify(response.data, null, 2);
      } catch (error) {
        console.error("Error querying error frequency:", error);
        this.errorResult = JSON.stringify({ error: "Error fetching data." });
      }
    },
    // 查询前 N 个错误
    async queryTopErrors() {
      try {
        const response = await axios.get(`/api/v1/error`, {
          params: {
            weight: 1,
            n: this.errorTopN,
            filter: "error",
          },
        });
        this.errorResult = JSON.stringify(response.data, null, 2);
      } catch (error) {
        console.error("Error querying top errors:", error);
        this.errorResult = JSON.stringify({ error: "Error fetching data." });
      }
    },
    async queryTopExceptions() {
      try {
        const response = await axios.get(`/api/v1/error`, {
          params: {
            weight: 1,
            n: this.exceptionTopN,
            filter: "exception", // 过滤条件：异常
          },
        });
        this.errorResult = JSON.stringify(response.data, null, 2);
      } catch (error) {
        console.error("Error querying top exceptions:", error);
        this.errorResult = JSON.stringify({ error: "Error fetching data." });
      }
    },
  },
};
</script>

<style scoped>
.v-card {
  margin: 20px 0;
}
.v-card-title {
  font-weight: bold;
  font-size: 20px;
}
</style>
