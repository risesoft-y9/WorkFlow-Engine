<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的Java国产信创工作流引擎</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-flowable/stargazers'><img src='https://gitee.com/risesoft-y9/y9-flowable/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.9-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE">
<img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
<img src="https://img.shields.io/badge/total%20lines-301k-blue.svg">
</p>

## 简介

<!--工作流引擎对内提供单位/机关流程管理规则和内部业务流程的数字化落地实践；对外提供自动化地第三方业务驱动、接口接入和算法单元驱动能力。工作流引擎在提供底层驱动引擎的同时对全局透明监控、安全防御和国产化特色功能进行充分考虑，是内部流程管理和业务算法驱动的最佳实践。-->
工作流引擎为业务流程自动化提供底层支撑，通过表单设计器、流程设计器与事项管理模块的协同，实现流程从设计、运行到监控的全生命周期管理。支持表单可视化配置、流程图形化设计、待办事项集中处理及跨系统集成，结合模板管理与智能提醒功能，提升流程流转效率与规范化水平，满足复杂业务场景的自动化需求。[系统在线体验----->>>>>](#在线体验)

（一）表单设计器

表单设计器提供可视化的表单创建与配置工具，支持通过拖拽操作快速设计表单结构，灵活设置数据源关联、字段绑定规则及校验逻辑。可自定义表单样式与布局，适配不同终端展示需求，支持数组、表格等复杂数据结构的录入与展示。通过配置表单动作触发规则，实现与后端数据的联动交互，同时支持导入标准化模板，简化常用表单的重复设计工作，提升表单制作效率与标准化程度，为流程流转提供结构化数据载体。

（二）流程设计器

流程设计器支持图形化流程建模与全生命周期管理，核心功能包括流程可视化设计、实例运行监控及版本迭代支持。通过图形界面拖拽节点元素（如开始事件、审批节点、结束事件），配置节点权限、流转条件及处理规则，构建可视化流程模型；可实时监控流程实例状态，对异常流程执行删除、挂起、激活等操作，查看审批历程与变量信息；支持低版本流程实例向高版本无缝迁移，保障流程迭代过程中业务连续性，同时支持流程模拟，提前发现设计缺陷，优化流程路径。

（三）事项管理

事项管理模块实现流程运行的精细化管控，核心功能涵盖事项配置、待办集中处理、流程协同及模板管理。支持统一配置流程节点的表单样式、审批规则、意见框管理、附件配置及编号规则，通过接口配置与链接配置实现第三方系统集成；汇总所有待办事项于统一界面，支持按岗位筛选不同状态的流程实例，通过消息提醒功能推送审批通知与办理结果。提供正文模板、套红模板、打印模板的导入与管理功能，支持流程实例关联、日历规则配置，结合动态视图自定义展示方式，全面提升流程处理效率与用户体验。

## 开源地址

源码地址：<https://github.com/risesoft-y9/WorkFlow-Engine>

## 源码目录

```
y9-module-flowableui -- 工作流程模块
 ├── risenet-y9boot-webapp-flowableui -- 工作流程webapp
y9-module-itemadmin -- 事项管理模块
 ├── risenet-y9boot-api-feignclient-itemadmin -- 事项管理接口客户端封装
 ├── risenet-y9boot-api-interface-itemadmin -- 事项管理接口
 ├── risenet-y9boot-support-itemadmin-jpa-repository -- 事项管理持久层
 ├── risenet-y9boot-support-itemadmin -- 事项管理控制层、业务层
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

1. 工作流引擎主要分为事项配置和工作台，事项配置面向运维人员对工作流进行配置，工作台面向用户操作使用


2. 工作流引擎需要数字底座中对于岗位角色的配置，否则无法运转


3. 工作流引擎不仅仅支持传统OA，更面向外部业务、算法、接口、代理的接入和控制

## 功能架构图

<div><img src="https://vue.youshengyun.com/files/img/flowableUI_framework2.png"><div/>

1.工作台的功能和界面都由事项管理进行配置后形成

2.事项管理的功能分为流程类、表单类和配置项类

3.表单管理负责映射数据库的表结构设计，由拖拉拽的表单引擎支撑

4.流程管理包含流程的设计、部署和监控，由拖拉拽的流程引擎支撑

5.接口管理支持在流程节点和流程路径中接入第三方的接口

6.链接管理支持通过岗位绑定第三方应用的入口

7.正文模板和套红模板均需要接入第三方插件才可以使用

## 部署架构图

<div><img src="https://vue.youshengyun.com/files/img/deploy2.png"><div/>
1.工作流引擎支持以微服务、分布式、容器化的方式进行部署和扩展

2.工作流引擎需要额外部署文件预览相关的服务器

3.工作流引擎依赖数字底座和其单点登录服务

4.工作流引擎单体在信创环境中，4核8GB内存的虚拟机可以轻松应对500用户（历史数据需按年度结转）

## 后端技术选型

| 序号 | 依赖              | 版本      | 官网                                                                                                                 |
|----|-----------------|---------|--------------------------------------------------------------------------------------------------------------------|
| 1  | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a>                                            |
| 2  | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a>                                        |
| 3  | SpringDataRedis | 2.7.10  | <a href="https://spring.io/projects/spring-data-redis" target="_blank">官网</a>                                      |
| 4  | SpringKafka     | 2.8.11  | <a href="https://spring.io/projects/spring-kafka" target="_blank">官网</a>                                           |
| 5  | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a>                        |
| 6  | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a>                          |
| 7  | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a>                                         |
| 8  | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a>                                                  |
| 9  | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a>                                                        |
| 10 | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> |
| 11 | flowable        | 6.8.0   | <a href="https://www.flowable.com/" target="_blank">官网</a>                                                         |

## 前端技术选型

| 序号 | 依赖           | 版本      | 官网                                                                     |
|----|--------------|---------|------------------------------------------------------------------------|
| 1  | vue          | 3.3.2   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>                 |
| 2  | vite2        | 2.9.13  | <a href="https://vitejs.cn/" target="_blank">官网</a>                    |
| 3  | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a>          |
| 4  | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a>           |
| 5  | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a>            |
| 6  | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a>       |
| 7  | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8  | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a>       |
| 9  | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>                  |
| 10 | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>                |
| 11 | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>                   |
| 12 | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a>        |
| 13 | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a>           |
| 14 | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>                   |

## 中间件选型

| 序号 | 工具               | 版本   | 官网                                                                        |
|----|------------------|------|---------------------------------------------------------------------------|
| 1  | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>                     |
| 2  | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a>               |
| 3  | Kafka            | 2.6+ | <a href="https://kafka.apache.org/" target="_blank">官网</a>                |
| 4  | filezilla server | 1.7+ | <a href="https://www.filezilla.cn/download/server" target="_blank">官网</a> |

## 数据库选型

| 序号 | 工具            | 版本         | 官网                                                                        |
|----|---------------|------------|---------------------------------------------------------------------------|
| 1  | Mysql         | 5.7 / 8.0+ | <a href="https://www.mysql.com/cn/" target="_blank">官网</a>                |
| 2  | Redis         | 6.2+       | <a href="https://redis.io/" target="_blank">官网</a>                        |
| 3  | elasticsearch | 7.9+       | <a href="https://www.elastic.co/cn/elasticsearch/" target="_blank">官网</a> |

## 工作流引擎专利

| 序&nbsp;号 | 专利号              | 专利名称                           |
|----------|------------------|--------------------------------|
| 1        | ZL202210221266.3 | 《基于全域状态更新的工作流同步更新方法及工作流同步更新系统》 |
| 2        | ZL202210222170.X | 《一种工作流数据恢复方法及数据恢复系统》           |
| 3        | ZL202210803411.9 | 《一种包含多起点多分支流程的工作流引擎的实现方法》      |

## 信创兼容适配

| 序号 | 类型   | 对象                 |
|:---|------|--------------------|
| 1  | 浏览器  | 奇安信、火狐、谷歌、360等     |
| 2  | 插件   | 金山、永中、数科、福昕等       |
| 3  | 中间件  | 东方通、金蝶、宝兰德等        |
| 4  | 数据库  | 人大金仓、达梦、高斯等        |
| 5  | 操作系统 | 统信、麒麟、中科方德等        |
| 6  | 芯片   | ARM体系、MIPS体系、X86体系 |

## 引擎国产化特点

| 序&nbsp;号 | 特&nbsp;点&nbsp;名&nbsp;称 | 特点描述                                                                   |
|----------|------------------------|------------------------------------------------------------------------|
| 1        | 串行办理                   | 一个流程节点，多人按照顺序依次收到待办进行办理，全部办理完成后，自动进入下一个流程节点                            |
| 2        | 并行办理                   | 一个流程节点，多人同时收到待办进行办理，全部办理完成后，自动进入下一个流程节点                                |
| 3        | 或行办理                   | 一个流程节点，无论串行或并行办理，任意一个人办理完成后，自动进入下一个流程节点（需避免使用排他网关）                     |
| 4        | 票行办理                   | 一个流程节点，无论串行或并行办理，多个人分配不同的权重进行投票，投票权重大于X%(例如60%），自动进入下一个流程节点（需避免使用排他网关） |
| 5        | 单人签收                   | 一个流程节点，支持多人同时接到办理任务，一个人抢占签收进行办理（签收后，其他人无办理任务），可拒签，可撤销签收                |
| 6        | 主协办                    | 一个并行办理的流程节点，支持定义主办和协办，只有主办可以办理完成后操作进入一个流程节点                            |
| 7        | 子流程                    | 主流程设置子流程，支持主、子流程独立办理                                                   |
| 8        | 自动执行                   | 支持在流程路径和流程节点自动触发或者跳过，并进入下一个流程节点                                        |
| 9        | 加减签                    | 支持在本流程节点增加或者减少办理的岗位                                                    |
| 10       | 抄送                     | 任何流程状态下，所有参与人都可以将本流程实例通知至其他岗位，允许重复抄送                                   |
| 11       | 重定位                    | 支持将当前流程实例跳转到任意节点                                                       |
| 12       | 委托                     | 将本岗位的所有事项和流程实例交由另一个岗位进行办理，支持设置委托的日期范围                                  |
| 13       | 催办                     | 支持在在办列表中对于办理岗位进行催促通知                                                   |
| 14       | 退回                     | 支持将本流程实例退回给申请人（起草人),支持退回给流程节点的发送人                                      |
| 15       | 收回                     | 支持发送人在当前办理人未流转前，撤回并重新办理                                                |
| 16       | 特殊办结                   | 无论在任何流程节点，支持直接对流程实例进行办结                                                |
| 17       | 过期任务                   | 支持流程实例在每个流程节点配置过期时间，过期后进行状态改变                                          |
| 18       | 恢复待办                   | 支持将已办结的流程实例重新进行流转                                                      |

## 引擎国产化功能

| 序&nbsp;号 | 功&nbsp;能&nbsp;名&nbsp;称 | 功能描述                                     |
|----------|------------------------|------------------------------------------|
| 1        | 事项配置                   | 针对所有工作流引擎中的配置项进行统一的绑定、管理操作               |
| 2        | 表单配置                   | 支持表单的动作设置、数据源、字段绑定、字段校验、样式加载等诸多的灵活配置项    |
| 3        | 统一待办                   | 汇总所有的待办消息于统一界面进行通知和办理                    |
| 4        | 接口配置                   | 支持第三方系统的接口配置、测试和调用，便于在流程路径和流程节点中进行接入     |
| 5        | 链接配置                   | 支持第三方系统的功能模块地址的绑定和配置                     |
| 6        | 关联流程                   | 支持手动将参与办理的流程实例与当前流程实例进行关联，关联后可以联动打开      |
| 7        | 消息提醒                   | 支持对在办件的流程节点的意见批复和办理情况进行消息通知配置            |
| 8        | 实例迁移                   | 支持将低版本的流程的实例无缝迁移至高版本中                    |
| 9        | 流程监控                   | 支持对流程实例的删除、挂起、激活、历程图、变量进行实时查看和操作         |
| 10       | 综合检索                   | 针对当前登录人的岗位下所有事项中的所有状态的流程实例检索             |
| 11       | 正文模板                   | 支持外部导入的流式文件模板，便于正文起草的模板使用（需要正文插件）        |
| 12       | 套红模板                   | 支持外部导入的流式红头文件模板，便于正文起草后进行红头模板的添加（需要正文插件） |
| 13       | 沟通交流                   | 在流程实例的全过程中，支持所有办理和抄送相关岗位的人员进行留言沟通        |
| 14       | 打印模板                   | 支持表单信息和word模板的配置，便于流程流转过程中的预览和打印         |
| 15       | 日历配置                   | 针对法定节假日、国家调休计算工作日和自然日，便于流程的日期规则计算        |
| 16       | 意见配置                   | 在事项的不同的流程节点中，支持配置不同类型的意见框，从而明确不同岗位的意见分栏  |
| 17       | 动态视图                   | 支持灵活配置草稿箱、待办列表、在办列表、办结列表的展示方式            |
| 18       | 编号配置                   | 在事项的不同的流程节点中，支持授权岗位对字段进行编号的定义            |

## 在线体验

### 事项管理

演示地址 ：<https://demo.youshengyun.com/itemAdmin/>

> 演示账号：systemManager 密码：Risesoft@2024
>

### 工作台

演示地址 ：<https://demo.youshengyun.com/flowableUI/workIndex/>

> 演示账号：
>
> 业务用户：user 密码：Risesoft@2024
>

## 文档专区

开发文档：https://docs.youshengyun.com/

| 序号 | 名称                                                                                                               |
|----|------------------------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/内部Java开发规范手册.pdf" target="_blank">内部Java开发规范手册</a>                    |
| 2  | <a href="https://vue.youshengyun.com/files/日志组件使用文档.pdf" target="_blank">日志组件使用文档</a>                            |
| 3  | <a href="https://vue.youshengyun.com/files/文件组件使用文档.pdf" target="_blank">文件组件使用文档</a>                            |
| 4  | <a href="https://vue.youshengyun.com/files/代码生成器使用文档.pdf" target="_blank">代码生成器使用文档</a>                          |
| 5  | <a href="https://vue.youshengyun.com/files/配置文件说明文档.pdf" target="_blank">配置文件说明文档</a>                            |
| 6  | <a href="https://vue.youshengyun.com/files/常用工具类使用示例文档.pdf" target="_blank">常用工具类使用示例文档</a>                      |
| 7  | <a href="https://vue.youshengyun.com/files/有生博大Vue开发手册v1.0.pdf" target="_blank">前端开发手册</a>                       |
| 8  | <a href="https://vue.youshengyun.com/files/开发规范.pdf" target="_blank">前端开发规范</a>                                  |
| 9  | <a href="https://vue.youshengyun.com/files/代码格式化.pdf" target="_blank">前端代码格式化</a>                                |
| 10 | <a href="https://vue.youshengyun.com/files/系统组件.pdf" target="_blank">前端系统组件</a>                                  |
| 11 | <a href="https://vue.youshengyun.com/files/通用方法.pdf" target="_blank">前端通用方法</a>                                  |
| 12 | <a href="https://vue.youshengyun.com/files/国际化.pdf" target="_blank">前端国际化</a>                                    |
| 13 | <a href="https://vue.youshengyun.com/files/Icon图标.pdf" target="_blank">前端Icon图标</a>                              |
| 14 | <a href="https://vue.youshengyun.com/files/单点登录对接文档.pdf" target="_blank">单点登录对接文档</a>                            |
| 15 | <a href="https://vue.youshengyun.com/files/工作流源码部署文档.pdf" target="_blank">工作流源码部署文档</a>                          |
| 16 | <a href="https://vue.youshengyun.com/files/工作流程操作文档.pdf" target="_blank">工作流程操作文档</a>                            |
| 17 | <a href="https://vue.youshengyun.com/files/workFlow/流程管理后端接口文档.pdf" target="_blank">流程管理接口文档</a>                 |
| 18 | <a href="https://vue.youshengyun.com/files/workFlow/事项管理后端接口文档.pdf" target="_blank">事项管理接口文档</a>                 |
| 19 | <a href="https://vue.youshengyun.com/files/workFlow/工作办件后端接口文档.pdf" target="_blank">工作办件接口文档</a>                 |
| 20 | <a href="https://vue.youshengyun.com/files/workFlow/Oracle数据库适配文档.pdf" target="_blank">Oracle数据库适配文档</a>         |
| 21 | <a href="https://vue.youshengyun.com/files/workFlow/Dameng数据库适配文档.pdf" target="_blank">Dameng数据库适配文档</a>         |
| 22 | <a href="https://vue.youshengyun.com/files/workFlow/PostgreSQL数据库适配文档.pdf" target="_blank">PostgreSQL数据库适配文档</a> |
| 23 | <a href="https://vue.youshengyun.com/files/workFlow/KingBase数据库适配文档.pdf" target="_blank">Kingbase数据库适配文档</a>     |
| 24 | <a href="https://vue.youshengyun.com/files/workFlow/Mariadb数据库适配文档.pdf" target="_blank">Mariadb数据库适配文档</a>       |
| 25 | <a href="https://vue.youshengyun.com/files/workFlow/OceanBase数据库适配文档.pdf" target="_blank">OceanBase数据库适配文档</a>   |
| 26 | <a href="https://vue.youshengyun.com/files/workFlow/MySQL数据库适配文档.pdf" target="_blank">MySQL数据库适配文档</a>           
| 27 | <a href="https://docs.youshengyun.com/digitalbase/backend/hole/holeInfo" target="_blank">安全扫描漏洞问题及解决方案文档</a>     | 

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
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin55.png"></td>
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
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin6.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/itemAdmin17.png"></td>
    </tr>
</table>

### 工作台截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI1.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI7.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI2.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/flowableUI3.png"></td>
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

## 依赖开源项目

| 序&nbsp;号 | 项&nbsp;目&nbsp;&nbsp;名&nbsp;称 | 项目介绍                                                                                                                                         | 地&nbsp;址                                                                                                                                                          |
|----------|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1        | 数字底座                         | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用 | <a href="https://gitee.com/risesoft-y9/y9-core" target="_blank">码云</a> <a href="https://github.com/risesoft-y9/Digital-Infrastructure" target="_blank">GitHub</a> |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

### 中国城市发展研究会

官网：<a href="https://www.china-cfh.com/" target="_blank">https://www.china-cfh.com/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理统一二维码咨询.png"><div/>
联系人：有生博大-咨询热线


座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>


