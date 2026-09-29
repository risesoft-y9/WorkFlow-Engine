<template>
  <div class="fm-upload-file"
    :id="uploadId"
  >
    <div class="file-button"
      v-show="(!isQiniu || (isQiniu && token))"
      v-if="!printRead"
    >
      <el-button v-if="ui == 'element'" type="primary" :disabled="disabled || fileList.length == limit" @click="handleAdd">{{$t('fm.actions.upload')}}</el-button>
      <a-button :size="size" v-if="ui == 'antd'" :disabled="disabled || fileList.length == limit" @click="handleAdd">{{$t('fm.actions.upload')}}</a-button>
      <input v-if="multiple"  multiple ref="uploadInput" @change="handleChange" type="file"  name="file" class=" upload_input">
      <input v-else ref="uploadInput" @change="handleChange" type="file"  name="file" class=" upload_input">
    </div>

    <div class="upload_tip">
      {{tip}}
    </div>

    <ul class="upload-list">
      <li class="list_item" 
        :class="{uploading: item.status=='uploading', 'is-success': item.status=='success', 'is-disabled': disabled}"
        v-for="(item) in fileList" :key="item.key"
      >
        <a class="list_item-name" :href="item.url" target="_blank">
          <i class="fm-iconfont icon-file"></i> 
          {{item.name}}
        </a>

        <template v-if="!printRead">
          <label class="list_item-status-label">
            <i v-if="item.status === 'success'" class="fm-iconfont icon-check" style="color: #67c23a;"></i>
          </label>
          <i class="fm-iconfont icon-close" @click="handleRemove(item.key)" style="font-size: 12px;z-index:1;"></i>

          <el-progress v-if="item.status == 'uploading' && ui == 'element'" :stroke-width="2" :percentage="item.percent"></el-progress>
          <a-progress v-if="item.status == 'uploading' && ui == 'antd'" :stroke-width="2" :percent="item.percent" />
        </template>
      </li>
    </ul>
  </div>
</template>

<script>
import { EventBus } from '../../util/event-bus.js'
import * as qiniu from 'qiniu-js'
import 'viewerjs/dist/viewer.css'
export default {
  components: {
  },
  props: {
    modelValue: {
      type: Array,
      default: () => []
    },
    token: {
      type: String,
      default: ''
    },
    domain: {
      type: String,
      default: ''
    },
    multiple: {
      type: Boolean,
      default: false
    },
    limit: {
      type: Number,
      default: 9
    },
    isQiniu: {
      type: Boolean,
      default: false
    },
    min: {
      type: Number,
      default: 0
    },
    action: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    tip: {
      type: String,
      default: ''
    },
    headers: {
      type: Array,
      default: () => []
    },
    ui: {
      type: String,
      default: 'element'
    },
    containerKey: {
      type: String,
      default: ''
    },
    withCredentials: {
      type: Boolean,
      default: false
    },
    printRead: {
      type: Boolean,
      default: false
    },
    onSelect: {
      type: Function,
      default: undefined
    },
    size: {
      type: String,
      default: 'default'
    }
  },
  emits: ['update:modelValue', 'on-upload-success', 'on-upload-error', 'on-upload-progress', 'on-upload-remove'],
  data () {
    return {
      fileList: this.modelValue.map(item => {
        return {
          ...item,
          key: item.key ? item.key : (new Date().getTime()) + '_' + Math.ceil(Math.random() * 99999),
        }
      }),
      viewer: null,
      uploadId: 'upload_' + new Date().getTime(),
      editIndex: -1,
      meituIndex: -1,
    }
  },
  computed: {
    miniWidth () {
      if (this.width > this.height) {
        return this.height
      } else {
        return this.width
      }
    }
  },
  methods: {
    handleChange () {
      const files = this.$refs.uploadInput.files

      if (this.fileList.length + files.length > this.limit) {
        this.$refs.uploadInput.value = []
        return false
      }
      
      for (let i = 0; i < files.length; i++) {
        let file = files[i]

        let rFile = this.onSelect(file)

        if (rFile === false) {
          return false
        }
        if (rFile instanceof File) {
          file = rFile
        }

        const reader = new FileReader()
        let key = new Date().getTime()
        reader.readAsDataURL(file)
        reader.onload = () => {

          key = key + '_' + file.name
          
          if (this.editIndex >= 0) {

            this.fileList[this.editIndex] = {
              key,
              url: reader.result,
              percent: 0,
              status: 'uploading',
              name: file.name
            }

            this.editIndex = -1
          } else {
            this.fileList.push({
              key,
              url: reader.result,
              percent: 0,
              status: 'uploading',
              name: file.name
            })
          }

          this.$nextTick(() => {
            if (this.isQiniu) {
              this.uploadAction2(reader.result, file, key)
            } else {
              this.uploadAction(reader.result, file, key)
            }
          })
        }
      }
      this.$refs.uploadInput.value = []
    }, 
    uploadAction (res, file, key) {
      let changeIndex = this.fileList.findIndex(item => item.key === key)
      const xhr = new XMLHttpRequest()
      
      const url = this.action
      xhr.open('POST', url, true)
      // xhr.setRequestHeader('Content-Type', 'multipart/form-data')
      this.headers.map(item => {
        item.key && xhr.setRequestHeader(item.key, item.value)
      })

      let formData = new FormData()
      formData.append('file', file)
      formData.append('fname', file.name)
      formData.append('key', key)

      xhr.withCredentials = this.withCredentials
      
      xhr.onreadystatechange = () => {
        if (xhr && xhr.readyState === 4) {
          
          let resData = xhr.response ? JSON.parse(xhr.response) : {}
          if (resData && resData.url) {
            this.fileList[this.fileList.findIndex(item => item.key === key)] = {
              ...this.fileList[this.fileList.findIndex(item => item.key === key)],
              url: resData.url,
              percent: 100,
              ...resData
            }
            setTimeout(() => {
              this.fileList[this.fileList.findIndex(item => item.key === key)] = {
                ...this.fileList[this.fileList.findIndex(item => item.key === key)],
                status: 'success'
              }
              this.$emit('on-upload-success', {
                ...this.fileList[this.fileList.findIndex(item => item.key === key)],
                status: 'success'
              })
              this.$emit('update:modelValue', this.fileList)
            }, 200)
          } else {
            this.fileList[this.fileList.findIndex(item => item.key === key)] = {
              ...this.fileList[this.fileList.findIndex(item => item.key === key)],
              status: 'error'
            }
            this.$emit('on-upload-error', {
              ...this.fileList[this.fileList.findIndex(item => item.key === key)],
              status: 'error'
            })
            this.fileList.splice(this.fileList.findIndex(item => item.key === key), 1)
          }
        }
      }
      xhr.upload.onprogress = (res) => {
        if (res.total && res.loaded) {
          this.fileList[this.fileList.findIndex(item => item.key === key)].percent = (res.loaded == res.total ? 99 : parseInt(res.loaded/res.total*100))

          this.$emit('on-upload-progress', {
            ...this.fileList[this.fileList.findIndex(item => item.key === key)],
            status: 'uploading',
            percent: (res.loaded == res.total ? 99 : parseInt(res.loaded/res.total*100))
          })
        }
      }

      xhr.send(formData)
    },
    uploadAction2 (res, file, key) {
      const _this = this
      const observable = qiniu.upload(file, key, this.token, {
        fname: file.name,
        mimeType: []
      }, {
        useCdnDomain: true,
        // region: qiniu.region.z2
      })
      observable.subscribe({
        next (res) {
          _this.fileList[_this.fileList.findIndex(item => item.key === key)].percent = parseInt(res.total.percent)
          
          _this.$emit('on-upload-progress', {
            ..._this.fileList[_this.fileList.findIndex(item => item.key === key)],
            status: 'uploading',
            percent: parseInt(res.total.percent)
          })
        },
        error (err) {
          _this.$message.error(err.message)
          _this.fileList[_this.fileList.findIndex(item => item.key === key)] = {
            ..._this.fileList[_this.fileList.findIndex(item => item.key === key)],
            status: 'error'
          }
          _this.$emit('on-upload-error', {
            ..._this.fileList[_this.fileList.findIndex(item => item.key === key)],
            status: 'error'
          })
          _this.fileList.splice(_this.fileList.findIndex(item => item.key === key), 1)
        },
        complete (res) {
          _this.fileList[_this.fileList.findIndex(item => item.key === key)] = {
            ..._this.fileList[_this.fileList.findIndex(item => item.key === key)],
            url: _this.domain + res.key,
            percent: 100,
            ...res,
          }
          setTimeout(() => {
            _this.fileList[_this.fileList.findIndex(item => item.key === key)] = {
              ..._this.fileList[_this.fileList.findIndex(item => item.key === key)],
              status: 'success'
            }
            _this.$emit('on-upload-success', {
              ..._this.fileList[_this.fileList.findIndex(item => item.key === key)],
              status: 'success'
            })
            
            _this.$emit('update:modelValue', _this.fileList)
          }, 200)
        }
      })
    },
    handleRemove (key) {
      if (!this.disabled) {
        this.$emit('on-upload-remove', this.fileList[this.fileList.findIndex(item => item.key === key)])
        this.fileList.splice(this.fileList.findIndex(item => item.key === key), 1)
        this.$nextTick(() => {
          this.$emit('update:modelValue', this.fileList)
        })
      }
    },
    handleAdd () {
      if (!this.disabled) {
        this.editIndex = -1
        this.$refs.uploadInput.click()
      }
    },
    handlePreviewFile (key) {
      this.viewer && this.viewer.destroy()
      this.uploadId = 'upload_' + new Date().getTime()
      
      this.$nextTick(() => {
        this.viewer = new Viewer(document.getElementById(this.uploadId))
        this.viewer.view(this.fileList.findIndex(item => item.key === key))
      })
    }
  },
  watch: {
    modelValue (val) {
      this.fileList = this.modelValue.map(item => {
        return {
          ...item,
          key: item.key ? item.key : (new Date().getTime()) + '_' + Math.ceil(Math.random() * 99999),
        }
      })
    }
  }
}
</script>

<style lang="scss">
.fm-upload-file{
  .upload_input{
    display: none !important;
  }

  .upload_tip{
    font-size: 12px;
    color: #606266;
    margin-top: 7px;
  }

  .upload-list{
    margin: 0;
    padding: 0;
    list-style: none;

    .list_item{
      font-size: 14px;
      color: #606266;
      line-height: 1.8;
      margin-top: 5px;
      position: relative;
      box-sizing: border-box;
      border-radius: 4px;
      width: 100%;

      &.is-disabled{
        // &::before{
        //   position: absolute;
        //   top:0;
        //   left: 0;
        //   bottom: 0;
        //   right: 0;
        //   z-index: 99;
        //   display: block;
        //   content: '';
        //   cursor: not-allowed;
        // }
        .icon-close{
          cursor: not-allowed;
        }
      }



      .el-progress,.ant-progress{
        position: absolute;
        top: 20px;
        width: 100%;
      }

      .el-progress__text,.ant-progress-text{
        position: absolute;
        right: 30px;
        top: -13px;
      }

      .el-progress-bar,.ant-progress-outer{
        margin-right: 0;
        padding-right: 0;
      }

      &.is-success{
        .list_item-status-label{
          .icon-upload-success{
            display: inline-block;
            line-height: inherit;
          }
        }
      }

      .icon-close{
        display: none;
        position: absolute;
        top: 5px;
        right: 5px;
        cursor: pointer;
        color: #606266;
        font-weight: 400;
      }

      &:hover{
        background-color: #f5f7fa;

        .icon-close{
          display: inline-block;
        }

        .list_item-status-label{
          display: none;
        }

        .el-progress__text{
          display: none;
        }
      }

      .list_item-name{
        text-decoration: none;
        color: #606266;
        display: block;
        margin-right: 40px;
        overflow: hidden;
        padding-left: 4px;
        text-overflow: ellipsis;
        transition: color .3s;
        white-space: nowrap;

        &:hover{
          color: #409eff;
          cursor: pointer;
        }

        i{
          margin-right: 7px;
          color: #909399;
          height: 100%;
          line-height: inherit;
        }
      }

      .list_item-status-label{
        display: block;
        position: absolute;
        right: 5px;
        top: 0;
        line-height: inherit;

        .icon-upload-success{
          color: #67c23a;
          display: none;
        }
      }
    }
  }
}
</style>
