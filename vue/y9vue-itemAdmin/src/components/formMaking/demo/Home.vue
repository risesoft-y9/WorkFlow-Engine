<template>
  <button @click="toggleDark()">
    <i inline-block align-middle i="dark:carbon-moon carbon-sun" />

    <span class="ml-2">{{ isDark ? 'Dark' : 'Light' }}</span>
  </button>

  <fm-making-form ref="makingForm" clearable upload preview generate-code generate-json
    :custom-fields="customFields"
    :global-config="globalConfig"
    :field-config="fieldConfig"
    name="v3sampledev"
    :cache="true"
    :json-templates="jsonTemplates"
    :init-from-template="false"
    :field-models="fieldModels"
    style="height: 700px;"
    use-antd-form
  >

    <template #widgetconfig="{type, data, customProps}">
      <el-form-item v-if="type === 'button'" label="Loading">
        <el-switch v-model="customProps.loading"></el-switch>
      </el-form-item>

      <el-form-item v-if="type === 'custom' && data.el=== 'custom-width-height'" label="Tip">
        <el-input v-model="customProps.tip"></el-input>
      </el-form-item>
    </template>
  </fm-making-form>
</template>

<script>
import { useDark, useToggle } from '@vueuse/core'
import json0 from './json0.js'
import json1 from './json1.js'
import json2 from './json2.js'
import json3 from './json3.js'
import json4 from './json4.js'
import json5 from './json5.js'
import json6 from './json6.js'
import json7 from './json7.js'
import json8 from './json8.js'
import json9 from './json9.js'
import json10 from './json10.js'
import json11 from './json11.js'
import { theme } from 'ant-design-vue'
import { provide, ref, nextTick } from 'vue'

export default {
  setup () {
    const isDark = useDark()

    const themeAlgorithm = ref(theme.defaultAlgorithm)

    provide('themeAlgorithm', themeAlgorithm)

    nextTick(() => {
      if (isDark.value) {
        themeAlgorithm.value = theme.darkAlgorithm
      } else {
        themeAlgorithm.value = theme.defaultAlgorithm
      }
    })

    const toggleDark = () => {
      useToggle(isDark)()

      if (isDark.value) {
        themeAlgorithm.value = theme.darkAlgorithm
      } else {
        themeAlgorithm.value = theme.defaultAlgorithm
      }
    }

    return {
      isDark,
      toggleDark,
      theme
    }
  },
  data() {
    return {
      customFields: [
        {
          name: '自定义组件',
          el: 'custom-width-height',
          icon: '<svg fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M13.5 16.875h3.375m0 0h3.375m-3.375 0V13.5m0 3.375v3.375M6 10.5h2.25a2.25 2.25 0 002.25-2.25V6a2.25 2.25 0 00-2.25-2.25H6A2.25 2.25 0 003.75 6v2.25A2.25 2.25 0 006 10.5zm0 9.75h2.25A2.25 2.25 0 0010.5 18v-2.25a2.25 2.25 0 00-2.25-2.25H6a2.25 2.25 0 00-2.25 2.25V18A2.25 2.25 0 006 20.25zm9.75-9.75H18a2.25 2.25 0 002.25-2.25V6A2.25 2.25 0 0018 3.75h-2.25A2.25 2.25 0 0013.5 6v2.25a2.25 2.25 0 002.25 2.25z"></path></svg>',
          options: {
            defaultValue: {},
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            validator: '',
            extendProps: {},
            customProps: {}
          },
          events: {
            onChange: '',
            onTest: '',
          }
        },{
          name: '图表示例',
          el: 'custom-chart',
          options: {
            defaultValue: [
              { year: '1951 年', sales: 38 },
              { year: '1952 年', sales: 52 },
              { year: '1956 年', sales: 61 },
              { year: '1957 年', sales: 145 },
              { year: '1958 年', sales: 48 },
              { year: '1959 年', sales: 38 },
              { year: '1960 年', sales: 38 },
              { year: '1962 年', sales: 38 },
            ],
            customClass: '',
            labelWidth: 100,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            validator: '',
            customProps: {}
          }
        },{
          name: 'VantField',
          el: 'custom-vant-field',
          options: {
            defaultValue: '',
            hideLabel: true,
            hidden: false,
            dataBind: true,
            required: false,
            validator: '',
            placeholder: '请输入文本',
            disabled: false,
            extendProps: {
              label: '文本',
            },
          }
        },{
          name: 'VantCalendar',
          el: 'custom-vant-calendar',
          options: {
            defaultValue: '',
            hideLabel: true,
            hidden: false,
            dataBind: true,
            disabled: false,
            required: false,
            validator: '',
            extendProps: {
            },
          }
        },{
          name: '分页数据列表',
          el: 'custom-pagination-table',
          options: {
            defaultValue: [],
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            validator: '',
            extendProps: {
              columns: []
            }
          },
          events: {
            onChange: '',
            onLoad: ''
          }
        }
      ],
      globalConfig: {
        // styleSheets: '.a .el-form-item__label{color: red;}',
        dataSource: [
          {
            key: 'upload',
            name: 'Get Upload Token',
            url: 'https://tools-server.making.link/api/uptoken',
            method: "GET",
            auto: true,
            responseFunc: 'return res.uptoken;',
          },
          {
            key: 'getoptions',
            name: 'Get Options',
            url: 'https://tools-server.making.link/api/new/options',
            method: 'GET',
            auto: true,
            responseFunc: 'return res.data;',
          }
        ]
      },
      fieldConfig: [
        {
          type: 'fileupload',
          options: {
            domain: 'https://tcdn.form.making.link/',
            action: 'https://tools-server.making.link/api/transfer',
          }
        },
        {
          type: 'imgupload',
          options: {
            domain: 'https://tcdn.form.making.link/',
            action: 'https://tools-server.making.link/api/transfer',
          }
        },
        {
          type: 'select',
          options: {
            options: [
              {value: '1111'},
              {value: '2222'},
              {value: '3333'}
            ]
          }
        }
      ],
      jsonTemplates: [
        {
          title: '空白表单',
          enTitle: 'Empty form',
          json: json0,
          url: '/images/json00.png'
        },
        {
          title: '典型表单',
          enTitle: 'Typical form',
          json: json1,
          url: '/images/json1.png'
        },
        {
          title: '快速注册表单',
          enTitle: 'Quick Sign-up Form',
          json: json2,
          url: '/images/json2.png'
        },
        {
          title: '复杂表格 - 人员履历表',
          enTitle: 'Complex forms',
          json: json3,
          url: '/images/json3.png'
        },
        {
          title: '步骤表单',
          enTitle: 'Step form',
          json: json4,
          url: '/images/json4.png'
        },
        {
          title: '数据源应用 - 主子表省市区联动',
          enTitle: 'Data source application',
          json: json5,
          url: '/images/json5.png'
        },
        {
          title: '事件应用 - 数据统计',
          enTitle: 'Event application',
          json: json6,
          url: '/images/json6.png'
        },
        {
          title: '响应式表单',
          enTitle: 'Reactive form',
          json: json7,
          url: '/images/json7.png'
        },
        {
          title: '复杂表格 - 动态增减表单项',
          enTitle: 'Complex forms',
          json: json8,
          url: '/images/json8.png'
        },
        {
          title: '可查询的分页数据列表',
          enTitle: 'Searchable paging data list',
          json: json9,
          url: '/images/json9.png'
        },
        {
          title: '中后台数据增删改查',
          enTitle: 'CRUD',
          json: json10,
          url: '/images/json10.png'
        },
        {
          title: '用户弹框选择',
          enTitle: 'The user select from the box',
          json: json11,
          url: '/images/json11.png'
        }
      ],
      fieldModels: [],
    }
  },
  mounted () {
  }
}
</script>