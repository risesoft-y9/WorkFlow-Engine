<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的Java快速开发框架</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-flowable/stargazers'><img src='https://gitee.com/risesoft-y9/y9-flowable/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.6-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
</p>

## 简介

工作流引擎对内提供单位/机关流程管理规则和内部业务流程的数字化落地实践；对外提供自动化地第三方业务驱动、接口接入和算法单元驱动能力。工作流引擎在提供底层驱动引擎的同时对全局透明监控、安全防御和国产化特色功能进行充分考虑，是内部流程管理和业务算法驱动的不二之选。

## 源码目录

```
y9-module-flowableui -- 工作流程模块
 ├── risenet-y9boot-webapp-flowableui-position -- 工作流程webapp
y9-module-itemadmin -- 事项管理模块
 ├── risenet-y9boot-api-feignclient-itemadmin -- 事项管理接口客户端封装
 ├── risenet-y9boot-api-interface-itemadmin -- 事项管理接口
 ├── risenet-y9boot-support-itemadmin-jpa-repository -- 事项管理持久层
 ├── risenet-y9boot-support-itemadmin-position -- 事项管理控制层、业务层
 ├── risenet-y9boot-webapp-itemadmin -- 事项管理webapp
y9-module-jodconverter -- 预览模块
 ├── risenet-y9boot-webapp-jodconverter -- 预览webapp
y9-module-processadmin -- 流程管理模块
 ├── risenet-y9boot-api-feignclient-processadmin -- 流程管理接口客户端封装
 ├── risenet-y9boot-api-interface-processadmin -- 流程管理接口
 ├── risenet-y9boot-support-processadmin -- 流程管理业务层
 ├── risenet-y9boot-webapp-processadmin -- 流程管理webapp
vue -- 前端工程
 ├── y9vue-itemAdmin  -- 事项管理前端工程
 ├── y9vue-flowableUI -- 工作流程前端工程
```

## 逻辑架构图

<div><img src="https://vue.youshengyun.com/files/img/architecture2.png"><div/>

## 功能架构图 ##

<div><img src="https://vue.youshengyun.com/files/img/function2.png"><div/>

## 部署架构图

<div><img src="https://vue.youshengyun.com/files/img/deploy2.png"><div/>

## 后端技术选型

| 序号 | 依赖            | 版本    | 官网                                                         |
| ---- | --------------- | ------- | ------------------------------------------------------------ |
| 1    | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a> |
| 2    | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a> |
| 3    | SpringDataRedis | 2.7.10  | <a href="https://spring.io/projects/spring-data-redis" target="_blank">官网</a> |
| 4    | SpringKafka     | 2.8.11  | <a href="https://spring.io/projects/spring-kafka" target="_blank">官网</a> |
| 5    | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a> |
| 6    | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a> |
| 7    | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a> |
| 8    | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a> |
| 9    | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a> |
| 10   | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> |
| 11   | flowable        | 6.8.0   | <a href="https://www.flowable.com/" target="_blank">官网</a> |

## 前端技术选型

| 序号 | 依赖         | 版本    | 官网                                                         |
| ---- | ------------ | ------- | ------------------------------------------------------------ |
| 1    | vue          | 3.3.2   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>     |
| 2    | vite2        | 2.9.13  | <a href="https://vitejs.cn/" target="_blank">官网</a>        |
| 3    | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a> |
| 4    | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a> |
| 5    | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a> |
| 6    | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a> |
| 7    | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8    | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a> |
| 9    | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>      |
| 10   | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>    |
| 11   | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>       |
| 12   | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a> |
| 13   | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a> |
| 14   | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>       |

## 中间件选型

| 序号 | 工具             | 版本 | 官网                                                         |
| ---- | ---------------- | ---- | ------------------------------------------------------------ |
| 1    | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>      |
| 2    | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a> |
| 3    | Kafka            | 2.6+ | <a href="https://kafka.apache.org/" target="_blank">官网</a> |
| 4    | filezilla server | 1.7+ | <a href="https://www.filezilla.cn/download/server" target="_blank">官网</a> |

## 数据库选型

| 序号 | 工具          | 版本       | 官网                                                         |
| ---- | ------------- | ---------- | ------------------------------------------------------------ |
| 1    | Mysql         | 5.7 / 8.0+ | <a href="https://www.mysql.com/cn/" target="_blank">官网</a> |
| 2    | Redis         | 6.2+       | <a href="https://redis.io/" target="_blank">官网</a>         |
| 3    | elasticsearch | 7.9+       | <a href="https://www.elastic.co/cn/elasticsearch/" target="_blank">官网</a> |

## 信创

| **序号** | 类型     | 对象                       |
| :------- | -------- | -------------------------- |
| 1        | 浏览器   | 奇安信、火狐、谷歌、360等  |
| 2        | 插件     | 金山、永中、数科、福昕等   |
| 3        | 中间件   | 东方通、金蝶、宝兰德等     |
| 4        | 数据库   | 人大金仓、达梦、高斯等     |
| 5        | 操作系统 | 统信、麒麟、中科方德等     |
| 6        | 芯片     | ARM体系、MIPS体系、X86体系 |

## 在线体验

待补充

## 文档专区

| 序号 | 名称                                                                                              |
|:---|-------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/单点登录对接文档.pdf" target="_blank">单点登录对接文档</a>           |
| 2  | <a href="https://vue.youshengyun.com/files/数字底座接口文档.pdf" target="_blank">数字底座接口文档</a>           |
| 3  | <a href="https://test.youshengyun.com/y9vue-code/digitalBase" target="_blank">安装部署文档</a>        |
| 4  | <a href="https://vue.youshengyun.com/files/操作使用文档（技术白皮书）.pdf" target="_blank">操作使用文档（技术白皮书）</a> |
| 5  | <a href="https://vue.youshengyun.com/files/数字底座数据库设计文档.pdf" target="_blank">数字底座数据库设计文档</a>     |
| 6  | <a href="https://vue.youshengyun.com/files/内部Java开发规范手册.pdf" target="_blank">内部Java开发规范手册</a>   |
| 7  | <a href="https://vue.youshengyun.com/files/日志组件使用文档.pdf" target="_blank">日志组件使用文档</a>           |
| 8  | <a href="https://vue.youshengyun.com/files/文件组件使用文档.pdf" target="_blank">文件组件使用文档</a>           |
| 9  | <a href="https://vue.youshengyun.com/files/代码生成器使用文档.pdf" target="_blank">代码生成器使用文档</a>         |
| 10 | <a href="https://vue.youshengyun.com/files/配置文件说明文档.pdf" target="_blank">配置文件说明文档</a>           |
| 11 | <a href="https://vue.youshengyun.com/files/常用工具类使用示例文档.pdf" target="_blank">常用工具类使用示例文档</a>     |
| 12 | <a href="https://vue.youshengyun.com/files/有生博大Vue开发手册v1.0.pdf" target="_blank">前端开发手册</a>      |
| 13 | <a href="https://vue.youshengyun.com/files/开发规范.pdf" target="_blank">前端开发规范</a>                 |
| 14 | <a href="https://vue.youshengyun.com/files/代码格式化.pdf" target="_blank">前端代码格式化</a>               |
| 15 | <a href="https://vue.youshengyun.com/files/系统组件.pdf" target="_blank">前端系统组件</a>                 |
| 16 | <a href="https://vue.youshengyun.com/files/通用方法.pdf" target="_blank">前端通用方法</a>                 |
| 17 | <a href="https://vue.youshengyun.com/files/国际化.pdf" target="_blank">前端国际化</a>                   |
| 18 | <a href="https://vue.youshengyun.com/files/Icon图标.pdf" target="_blank">前端Icon图标</a>             |
| 19 | <a href="https://vue.youshengyun.com/files/Oracle数据库适配文档.pdf" target="_blank">Oracle数据库适配文档</a>          |

## 工作流引擎截图

### 事项管理截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin1.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin2.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin3.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin4.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin5.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin6.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin7.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin8.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin9.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin10.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin11.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin12.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin13.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin14.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin15.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin16.png"></td>
    </tr>
</table>

### 工作流程截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI1.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI2.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI3.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI4.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI5.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI7.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI8.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI9.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI10.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI11.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI12.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI13.png"></td>
    </tr>
</table>

## 同构开源项目

| 序号 | 项目名称   | 项目介绍                                                     | 地址                                                         |
| :--- | ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1    | 数字底座   | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。 | <a href="https://gitee.com/risesoft-y9/y9-core" target="_blank">码云地址</a> |
| 2    | 数据流引擎 | 数据流引擎是一款面向数据集成、数据同步、数据交换、数据共享、任务配置、任务调度的底层数据驱动引擎。数据流引擎采用管执分离、多流层、插件库等体系应对大规模数据任务、数据高频上报、数据高频采集、异构数据兼容的实际数据问题。 | <a href="https://gitee.com/risesoft-y9/y9-dataflow" target="_blank">码云地址</a> |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源工作流引擎咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理-二维码2.png"><div/>
联系人：有生博大-咨询热线


座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>


