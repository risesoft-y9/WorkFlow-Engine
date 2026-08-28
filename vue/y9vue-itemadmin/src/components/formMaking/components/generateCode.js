function findRemoteFunc (list, funcList, tokenFuncList, blankList, optionList) {
  for (let i = 0; i < list.length; i++) {
    if (list[i].type == 'grid') {
      list[i].columns.forEach(item => {
        findRemoteFunc(item.list, funcList, tokenFuncList, blankList, optionList)
      })
    } else if (list[i].type == 'table') {
      findRemoteFunc(list[i].tableColumns, funcList, tokenFuncList, blankList, optionList)
    } else if (list[i].type == 'subform') {
      findRemoteFunc(list[i].list, funcList, tokenFuncList, blankList, optionList)
    } else if (list[i].type == 'tabs') {
      list[i].tabs.forEach(item => {
        findRemoteFunc(item.list, funcList, tokenFuncList, blankList, optionList)
      })
    } else if (list[i].type == 'collapse') {
      list[i].tabs.forEach(item => {
        findRemoteFunc(item.list, funcList, tokenFuncList, blankList, optionList)
      })
    } else if (list[i].type == 'report') {
      list[i].rows.forEach(row => {
        row.columns.forEach(col => {
          findRemoteFunc(col.list, funcList, tokenFuncList, blankList, optionList)
        })
      })
    } else if (list[i].type == 'inline') {
      findRemoteFunc(list[i].list, funcList, tokenFuncList, blankList, optionList)
    } else if (list[i].type == 'dialog') {
      findRemoteFunc(list[i].list, funcList, tokenFuncList, blankList, optionList)
    } else if (list[i].type == 'card') {
      findRemoteFunc(list[i].list, funcList, tokenFuncList, blankList, optionList)
    } else if (list[i].type == 'group') {
      findRemoteFunc(list[i].list, funcList, tokenFuncList, blankList, optionList)
    } else {
      if (list[i].type == 'blank') {
        if (list[i].model && blankList.findIndex(item => item.name == list[i].model) < 0) {
          blankList.push({
            name: list[i].model,
            label: list[i].name,
            dataBind: list[i].options.dataBind
          })
        }
      } else if (list[i].type == 'imgupload') {
        if (list[i].options.tokenType == 'func' && list[i].options.tokenFunc) {
          tokenFuncList.push({
            func: list[i].options.tokenFunc,
            label: list[i].name,
            model: list[i].model
          })
        }
      } else {
        if (list[i].options.remote && list[i].options.remoteType == 'func' && list[i].options.remoteFunc) {
          funcList.push({
            func: list[i].options.remoteFunc,
            label: list[i].name,
            model: list[i].model
          })
        }

        if (list[i].options.remote && list[i].options.remoteType == 'option' && list[i].options.remoteOption) {
          optionList.push({
            option: list[i].options.remoteOption,
            label: list[i].name,
            model: list[i].model
          })
        }
      }
    }
  }
}

export default function (data, type = 'html', ui = 'element') {

  const isDark = document.querySelector('html').className.indexOf('dark')>-1

  const funcList = []

  const tokenFuncList = []

  const blankList = []

  const optionList = []

  const templateName = ui == 'element' ? 'fm-generate-form' : 'fm-generate-antd-form'

  const buttonName = ui == 'element' ? 'el-button' : 'a-button'

  const cdnjs = ui == 'element' ? 'https://unpkg.com/element-plus' : 'https://unpkg.com/ant-design-vue/dist/antd.min.js'

  const dayjs = `<script src="https://unpkg.com/dayjs/dayjs.min.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/customParseFormat.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/weekday.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/localeData.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/weekOfYear.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/weekYear.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/advancedFormat.js"></script>
  <script src="https://unpkg.com/dayjs/plugin/quarterOfYear.js"></script>`

  let cdncss = ''
  if (ui == 'element') {
    if (isDark) {
      cdncss = `<link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
      <link rel="stylesheet" href="https://unpkg.com/element-plus/theme-chalk/dark/css-vars.css">`
    } else {
      cdncss = `<link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">`
    }
  } else {
    cdncss = `<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ant-design-vue/dist/reset.min.css">`
  }

  findRemoteFunc(JSON.parse(data).list, funcList, tokenFuncList, blankList, optionList)

  let funcTemplate = ''

  let blankTemplate = ''

  let optionTemplate = ''

  for (let i = 0; i < optionList.length; i++) {
    optionTemplate += `
          ${optionList[i].option} : [], // ${optionList[i].label} option data
    `
  }

  for(let i = 0; i < funcList.length; i++) {
    funcTemplate += `
            ${funcList[i].func} (resolve) {
              // ${funcList[i].label} ${funcList[i].model}
              // resolve(data)
            },
    `
  }

  for(let i = 0; i < tokenFuncList.length; i++) {
    funcTemplate += `
            ${tokenFuncList[i].func} (resolve) {
              // ${tokenFuncList[i].label} ${tokenFuncList[i].model}
              // resolve(token)
            },
    `
  }

  for (let i = 0; i < blankList.length; i++) {
    if (blankList[i].dataBind) {
      blankTemplate += `
        <template v-slot:${blankList[i].name}="scope">
          <!-- ${blankList[i].label || blankList[i].name} -->
          <!-- use v-model="scope.model.${blankList[i].name}" to bind data -->
        </template>
      `
    } else {
      blankTemplate += `
        <template v-slot:${blankList[i].name}>
          <!-- ${blankList[i].label || blankList[i].name} -->
        </template>
      `
    }
    
  }

  if (type == 'html') {
    return `<!DOCTYPE html>
<html class="${isDark ? 'dark' : ''}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no" />
  ${cdncss}
  <link rel="stylesheet" href="https://form.making.link/public/lib/form-making-v3/dist/index.css">
</head>
<body>
  <div id="app">
    <${templateName}
      :data="jsonData" 
      :remote="remoteFuncs"
      :remote-option="dynamicData"
      ref="generateForm"
    >${blankTemplate}
    </${templateName}>
  </div>
  <script src="https://unpkg.com/vue/dist/vue.global.prod.js"></script>${ui == 'antd' ? `\n\t${dayjs}` : ''}
  <script src="${cdnjs}"></script>
  <script src="https://form.making.link/public/lib/form-making-v3/dist/form-making-v3.umd.js"></script>
  <script>
    Vue.createApp({
      data () {
        return {
          jsonData: ${data},
          remoteFuncs: {
            ${funcTemplate}
          },
          dynamicData: {
            ${optionTemplate}
          }
        }
      }
    }).use(${ui == 'element' ? 'ElementPlus' : 'antd'}).use(FormMakingV3)${ui == 'element' ? '' : '.provide("useInjectFormItemContext", antd.Form.useInjectFormItemContext)'}.mount('#app')
  </script>
</body>
</html>`
  } else {
    return `<template>
  <div>
    <${templateName} 
      :data="jsonData" 
      :remote="remoteFuncs" 
      :remote-option="dynamicData"
      ref="generateForm"
    >${blankTemplate}
    </${templateName}>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const generateForm = ref()

const jsonData = reactive(${data})

const remoteFuncs = reactive({
  ${funcTemplate}
})

const dynamicData = reactive({
  ${optionTemplate}
})
</script>`
  }
}