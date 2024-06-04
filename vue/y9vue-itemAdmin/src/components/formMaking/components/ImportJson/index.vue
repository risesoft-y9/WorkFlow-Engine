<template>
  <el-tabs v-model="activeName" class="import-json-container">
    <el-tab-pane :label="$t('fm.importjson.name')" name="library">

      <el-scrollbar height="560px" always class="import-json-library-container">
        <el-space wrap :size="20" v-if="props.libraryList?.length">
          <el-card class="import-json-card" v-for="item in props.libraryList" :key="item.title" shadow="hover" :body-style="{ padding: '0px', width: '218px' }">
            <el-image style="width: 218px; height: 218px" 
              :src="item.url" 
              fit="contain"
            >
              <template #error>
                <div class="image-slot">{{$t('fm.importjson.noimage')}}</div>
              </template>
            </el-image>
            <div style="padding: 10px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap; ">
              <span v-if="$i18n.locale == 'zh-cn'">{{item.title}}</span>
              <span v-else>{{item.enTitle}}</span>
            </div>  

            <div class="action-cover">
              <el-button type="primary" round @click="loadJson(item.json)">{{$t('fm.importjson.loadjson')}}</el-button>
            </div>
          </el-card>
        </el-space>
        <el-empty v-else :description="$t('fm.importjson.nojson')" />
      </el-scrollbar>
    </el-tab-pane>
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

const activeName = ref('library')
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