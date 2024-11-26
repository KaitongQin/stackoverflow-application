<template>
  <div id="app">
    <!-- 顶部区域 -->
    <header class="header">
      <img class="logo" src="./assets/logo.png" alt="Logo" />
    </header>

    <!-- 主体区域 -->
    <main class="main-content">
      <div class="left-content">
        <h1>欢迎访问我们的首页</h1>
        <p>
          这是一个示例页面，左侧展示文字内容，右侧是一张图片。你可以根据需要调整或扩展内容。
        </p>
      </div>
      <div class="right-content">
        <img src="./assets/illo-home-hero.png" alt="示例图片" />
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style scoped>
/* 顶部区域样式 */
.header {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #ffffff;
  border-bottom: 1px solid #ddd;
}

.logo {
  height: 50px;
}

/* 主体区域样式 */
.main-content {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.left-content {
  flex: 1;
  padding-right: 20px;
}

.right-content img {
  width: 500px;
  height: auto;
  border-radius: 10px;
}
</style>
