<template>
  <el-tabs v-model="activeName" class="import-json-container">
    <el-tab-pane label="JSON" name="json">
      <div class="import-json-code">
        <el-alert type="info" :title="$t('fm.description.uploadJsonInfo')"></el-alert>
        <code-editor height="450px" mode="json" v-model="jsonEg" ref="jsonCodeEditor" wrap></code-editor>
        <div style="margin-top: 30px; text-align: center;">
          <el-button type="primary" @click="uploadJsonFile" :loading="uploadLoading">{{$t('fm.actions.importFile')}}</el-button>

          <el-button type="primary" @click="submit">{{$t('fm.actions.confirm')}}</el-button>
        </div>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import {ref} from 'vue'
import { ElMessage } from 'element-plus'
import CodeEditor from '../CodeEditor/index.vue'

const activeName = ref('json')
const jsonEg = ref(`{
  "list": [],
  "config": {
    "labelWidth": 100,
    "labelPosition": "right",
    "size": "small",
    "customClass": "",
    "ui": "element",
    "layout": "horizontal",
    "width": "100%",
    "hideLabel": false,
    "hideErrorMessage": false
  }
}`)
const uploadLoading = ref(false)
const jsonCodeEditor = ref(null)

const props = defineProps({
  libraryList: {
    type: Array,
    defalut: () => []
  }
})

const emit = defineEmits(['load-json'])

const submit = () => {
  emit('load-json', jsonEg.value)
}

const loadJson = (json) => {
  emit('load-json', json)
}

const uploadJsonFile = () => {
  let input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  input.onchange = event => {
    uploadLoading.value = true
    let file = event.target.files[0]

    if (file.type === 'application/json') {
      let fileReader = new FileReader()

      fileReader.onload = () => {
        let fc = fileReader.result

        jsonEg.value = fc

        jsonCodeEditor.value.setValue(fc)

        uploadLoading.value = false
      }
      fileReader.readAsText(file, 'UTF-8')
    } else {
      uploadLoading.value = false
      ElMessage.error('File Type Error!')
    }
  }
  input.click()
}
</script>

<style lang="scss">
.import-json-container{
  .import-json-library-container{
    .import-json-card{
      .action-cover{
        display: none;
        position: absolute;
        top: 0;
        left: 0;
        bottom: 0;
        right: 0;
        background: rgba(0,0,0,0.6);
      }
      // cursor: pointer;
      position: relative;

      &:hover{
        .action-cover{
          display: flex;
          justify-content: center;
          align-items: center;
        }
      }

      .image-slot {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        background: var(--el-fill-color-light);
        color: var(--el-text-color-secondary);
        font-size: 30px;
      }
    }
  }

  .import-json-code{
    height: 560px;
  }
}
</style>