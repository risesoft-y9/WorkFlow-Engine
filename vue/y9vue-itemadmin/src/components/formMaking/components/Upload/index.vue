<template>
  <div class="fm-uplaod-container"
    :id="uploadId"
  >
    <draggable class="drag-img-list"
      v-model="fileList"
      v-bind="{group: uploadId, ghostClass: 'ghost', animation: 200}"
      :no-transition-on-drag="true"
      item-key="key"
    >
      <template #item="{element:item}">
        <div 
          :id="item.key"
          :style="{width: width+'px', height: height+'px'}"
          :class="{uploading: item.status=='uploading', 'is-success': item.status=='success', 'is-disabled': disabled}"
          class="upload-file" :key="item.key">
          <img :src="item.url" />

          <el-progress v-if="item.status=='uploading' && ui == 'element'" :width="miniWidth*0.9" class="upload-progress" type="circle" :percentage="item.percent"></el-progress>

          <a-progress v-if="item.status=='uploading' && ui == 'antd'" :width="miniWidth*0.9" class="upload-progress" type="circle" :percent="item.percent"></a-progress>

          <label class="item-status" v-if="item.status=='success'">
            <i class="fm-iconfont icon-check1" style="color: #fff; transform: rotate(-45deg); line-height: 12px; display: block;"></i>
          </label>

          <div class="uplaod-action" :style="{height: miniWidth / 4 + 'px'}">
            <i class="fm-iconfont icon-tupianyulan" :title="$t('fm.upload.preview')" @click="handlePreviewFile(item.key)" :style="{'font-size': miniWidth/8+'px'}"></i>
            <i v-if="isEdit && !disabled && !printRead" class="fm-iconfont icon-sync1" :title="$t('fm.upload.edit')" @click="handleEdit(item.key)" :style="{'font-size': miniWidth/8+'px'}"></i>
            <i v-if="isDelete && fileList.length > min && !disabled && !printRead" class="fm-iconfont icon-delete" :title="$t('fm.upload.delete')" @click="handleRemove(item.key)" :style="{'font-size': miniWidth/8+'px'}"></i>
          </div>
        </div>
      </template>
      
    </draggable>

    <div 
      :class="{'is-disabled': disabled, 'el-upload': ui == 'element', 'el-upload--picture-card': ui == 'element', 'ant-upload': ui == 'antd', 'ant-upload-select' : ui == 'antd', 'ant-upload-select-picture-card': ui == 'antd'}"
      v-show="(!isQiniu || (isQiniu && token)) && fileList.length < limit"
      :style="{width: width+'px', height: height+'px'}"
      @click.self="handleAdd"
      v-if="!readonly"
    >
      <i @click.self="handleAdd" class="fm-iconfont icon-plus" :style="{fontSize:miniWidth/4+'px', lineHeight: miniWidth/4+'px', marginTop: (-miniWidth/8)+'px', marginLeft: (-miniWidth/8)+'px', position: 'absolute', top: (height/2)+'px', left: (width/2)+'px'}"></i>
      <input accept="image/*" v-if="multiple"  multiple ref="uploadInput" @change="handleChange" type="file" :style="{width: 0, height: 0}" name="file" class=" upload-input">
      <input accept="image/*" v-else ref="uploadInput" @change="handleChange" type="file" :style="{width:0, height: 0}" name="file" class=" upload-input">
    </div>
  </div>
</template>

<script>
import Viewer from 'viewerjs'
import Draggable from 'vuedraggable/src/vuedraggable'
import { EventBus } from '../../util/event-bus.js'
import * as qiniu from 'qiniu-js'
import 'viewerjs/dist/viewer.css'
export default {
  components: {
    Draggable
  },
  props: {
    modelValue: {
      type: Array,
      default: () => []
    },
    width: {
      type: Number,
      default: 100
    },
    height: {
      type: Number,
      default: 100
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
    isDelete: {
      type: Boolean,
      default: false
    },
    min: {
      type: Number,
      default: 0
    },
    meitu: {
      type: Boolean,
      default: false
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    action: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
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
    }
  },
  emits: ['update:modelValue', 'on-upload-success', 'on-upload-error', 'on-upload-progress', 'on-upload-remove', 'on-meitu'],
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

      if (this.editIndex < 0 && this.fileList.length + files.length > this.limit) {
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
        const key = (new Date().getTime()) + '_' + Math.ceil(Math.random() * 99999)
        reader.readAsDataURL(file)
        reader.onload = () => {
          
          if (this.editIndex >= 0) {
            this.fileList[this.editIndex] = {
              key,
              url: reader.result,
              percent: 0,
              status: 'uploading'
            }

            this.editIndex = -1
          } else {
            this.fileList.push({
              key,
              url: reader.result,
              percent: 0,
              status: 'uploading'
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
        if (xhr.readyState === 4) {
          
          let resData = JSON.parse(xhr.response)
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
        fname: key,
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
      this.$emit('on-upload-remove', this.fileList[this.fileList.findIndex(item => item.key === key)])
      this.fileList.splice(this.fileList.findIndex(item => item.key === key), 1)
      this.$nextTick(() => {
        this.$emit('update:modelValue', this.fileList)
      })
    },
    handleEdit (key) {
      
      this.editIndex = this.fileList.findIndex(item => item.key === key)
      
      this.$refs.uploadInput.click()
    },
    handleMeitu (key) {

      this.$emit('on-meitu', this.fileList.findIndex(item => item.key === key))
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
.fm-uplaod-container{
  .is-disabled{
    position: relative;

    &::before{
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      // background: rgba(0,0,0,.1);
      content: '';
      display: block;
      cursor:not-allowed;
    }
  }

  .upload-file{
    margin: 0 10px 10px 0;
    display: inline-flex;
    justify-content: center;
    align-items: center;
    // background: #fff;
    overflow: hidden;
    background-color: #fff;
    border: 1px solid #c0ccda;
    border-radius: 6px;
    box-sizing: border-box;
    position: relative;
    vertical-align: top;
    &:hover{
      .uplaod-action{
        display: flex;
      }
    }
    .uplaod-action{
      position: absolute;
      // top: 0;
      // height: 30px;
      bottom: 0;
      left: 0;
      right: 0;
      background: rgba(0,0,0,0.6);
      display: none;
      justify-content: center;
      align-items: center;
      i{
        color: #fff;
        cursor: pointer;
        margin: 0 5px;
      }
    }
    &.is-success{
      .item-status{
        position: absolute;
        right: -15px;
        top: -6px;
        width: 40px;
        height: 24px;
        background: #13ce66;
        text-align: center;
        transform: rotate(45deg);
        box-shadow: 0 0 1pc 1px rgba(0,0,0,.2);
        &>i{
          font-size: 12px;
          margin-top: 11px;
          color: #fff;
          transform: rotate(-45deg);
        }
      }
    }
    &.uploading{
      &:before{
        display: block;
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0,0,0,0.3);
      }
    }
    .upload-progress{
      position: absolute;
      .el-progress__text{
        color: #fff;
        font-size: 16px !important;
      }
    }
    img{
      max-width: 100%;
      max-height: 100%;
      vertical-align: middle;
    }
  }
  .el-upload--picture-card{
    position: relative;
    overflow: hidden;
    .el-icon-plus{
      position: absolute;
      top: 50%;
      left: 50%;
    }
  }
  .upload-input{
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: block;
    opacity: 0;
    cursor: pointer;
  }

  .drag-img-list{
    display: inline;

    .ghost{
      position: relative;
      &::after {
        width: 100%;
        height: 100%;
        display: block;
        content: '';
        background: #fbfdff;
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        border: 1px dashed #3bb3c2;
      }
    }

    &>div{
      cursor: move;
    }
  }

  .ant-upload{
    display: inline-block;
    position: relative;

    &.ant-upload-select-picture-card{
      float: none;
    }
  }
}

.viewer-container{
  z-index: 9999 !important;
}
</style>
