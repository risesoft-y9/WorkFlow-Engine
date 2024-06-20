<template>
  <div class="login">
      <div class="form">
        <h1 class="title">
          404 Error
        </h1>
        <p>抱歉，你访问的页面不存在</p>
        <el-button  type="primary" size="mini" @click="$router.push('/')">返回首页</el-button>
      </div>
  </div>
</template>
<script>
export default {
  name: 'Login',
    data() {
      return {
        loading: false
      };
    },
    methods: {
    }
};
</script>
<style lang="scss" scoped>
.login {
  display: flex;
  width: 100%;
  height: 100vh;
  min-height: 300px;
  overflow: auto;
  /* background-image: url("../../assets/images/bg.jpg"); */
  background-color: #f5f7f9;
  background-repeat: no-repeat;
  background-position: center center;
  background-attachment: fixed;
  background-size: cover;
  align-items: center;
  .form{
      flex: none;
      width: 320px;
      padding: 60px;
      margin: 0 auto;
      border-radius: 4px;
      background-color: rgba($color: #000000, $alpha: 0.1);
      text-align: center;
      color: rgba($color: #000000, $alpha: 0.5);
      .title{
        font-weight: 300;
      }
  }
}
</style>
