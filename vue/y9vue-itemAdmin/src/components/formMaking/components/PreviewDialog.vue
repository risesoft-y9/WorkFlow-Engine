<template>
  <cus-dialog
    :visible="previewVisible"
    @on-close="previewVisible = false"
    ref="widgetPreview"
    :showclose="false"
    form
    :title="$t('fm.actions.preview')"
    fullscreen
    custom-class="fm-generate-preview-container"
    :close-on-press-escape="false"
    class="PreviewDialog"
  >

    <el-radio-group v-model="platform" size="small" class="platform-controller">
      <el-radio-button label="pc" value="pc"><i class="fm-iconfont icon-pc"/></el-radio-button>
      <el-radio-button label="pad" value="pad"><i class="fm-iconfont icon-pad"/></el-radio-button>
      <el-radio-button label="mobile" value="mobile"><i class="fm-iconfont icon-mobile"/></el-radio-button>
    </el-radio-group>

    <div class="fm-generate-preview" :class="`${platform}`" v-loading="exportLoading">
      <el-scrollbar>
        <generate-form preview :print-read="printRead" :platform="platform" id="export-id" :edit="formEdit"  insite="true" v-if="previewVisible && (previewForm.config.ui == 'element' || !previewForm.config.ui)" :data="previewForm" :value="widgetModels" ref="generateForm">

          <template v-slot:blank>
          </template>
        </generate-form>

        <a-config-provider :theme="{algorithm: themeAlgorithm}" v-if="useAntdForm">
          <antd-generate-form preview :print-read="printRead" :platform="platform" :edit="formEdit"  insite="true" v-if="previewVisible && previewForm.config.ui == 'antd'" :data="previewForm" :value="widgetModels" ref="generateAntForm">
            <template v-slot:blank>
            </template>
          </antd-generate-form>
        </a-config-provider>
      </el-scrollbar>

      <div v-show="platform === 'mobile' && showQrcode" style=" position: absolute; top: 80px; left: 50%; margin-left: 220px;">
        <el-card shadow="never" :body-style="{
          padding: '15px'
        }">
          <div style="line-height: 20px; margin-bottom: 10px; ">{{$t('fm.description.qrcodePreview')}}</div>
          <div id="fmpreviewqrcode"></div>
        </el-card>
      </div>
    </div>

    <template #action>
      <el-button size="large" type="primary" @click="handleTest">{{$t('fm.actions.getData')}}</el-button>
      <el-button size="large" @click="handleReset">{{$t('fm.actions.reset')}}</el-button>
      <el-button size="large" @click="formEdit = false" v-if="formEdit" :disabled="printRead">{{$t('fm.actions.disabledEdit')}}</el-button>
      <el-button size="large" @click="formEdit = true" v-else :disabled="printRead">{{$t('fm.actions.enabledEdit')}}</el-button>
      <el-button size="large" @click="printRead = true" v-if="!printRead">{{$t('fm.actions.printReadMode')}}</el-button>
      <el-button size="large" @click="printRead = false" v-if="printRead">{{$t('fm.actions.editMode')}}</el-button>
      <el-button size="large" @click="handlePrint">{{$t('fm.actions.print')}}</el-button>
      <el-button size="large" @click="handleExportPDF" :loading="exportLoading">{{$t('fm.actions.exportPDF')}}</el-button>
      <el-button size="large" @click="previewVisible = false" type="info" plain>{{$t('fm.actions.close')}}</el-button>
    </template>
  </cus-dialog>

  <PreviewPdf ref="pdfPreview"></PreviewPdf>
</template>

<script>
import CusDialog from './CusDialog.vue'
import GenerateForm from './GenerateForm.vue'
import AntdGenerateForm from './AntdvGenerator/GenerateForm.vue'
import request from '../util/request.js'
import { consoleError } from '../util/index'
import { ElMessage } from 'element-plus'
import PreviewPdf from './PreviewPdf.vue'

export default {
  components: {
    CusDialog,
    GenerateForm,
    AntdGenerateForm,
    PreviewPdf
  },
  props: {
  },
  inject: {
    themeAlgorithm: {
      default: null
    },
    useAntdForm: {
      default: false
    }
  },
  emits: ['get-data-success'],
  data () {
    return {
      previewVisible: false,
      formEdit: true,
      previewForm: {},
      widgetModels: {},
      platform: 'pc',
      printRead: false,
      showQrcode: false,
      exportLoading: false,
    }
  },
  methods: {
    generateQrcode (url) {
      if (!window.QRCode) {
        consoleError('QRCode is not defined')

        return false
      }

      setTimeout(() => {
        this.showQrcode = true
        var qrcode = new QRCode(document.getElementById("fmpreviewqrcode"), {
          width : 120,
          height : 120
        });

        qrcode.makeCode(url)
      }, 500)
    },
    preview (data, platform) {
      this.previewForm = data
      this.formEdit = this.previewVisible = true
      this.printRead = false
      this.showQrcode = false

      this.platform = platform

      this.$nextTick(() => {
        window.document.getElementsByClassName('fm-generate-preview-container')?.[0].removeAttribute('tabindex')
      })
    },
    handleTest () {
      const $form = this.previewForm.config.ui == 'element' || !this.previewForm.config.ui ?
        this.$refs.generateForm : this.$refs.generateAntForm

      $form.getData().then(data => {
        this.$emit('get-data-success', data)
        this.$refs.widgetPreview.end()
      }).catch(e => {
        ElMessage({
          message: e,
          type: 'error'
        })
        this.$refs.widgetPreview.end()
      })
    },
    handleReset () {
      const $form = this.previewForm.config.ui == 'element' || !this.previewForm.config.ui ?
        this.$refs.generateForm : this.$refs.generateAntForm

      $form.reset()
    },
    handleExportWord () {
     var rules = "",
          ss = document.styleSheets;
      for (var i = 0; i < ss.length; ++i) {
          for (var x = 0; x < ss[i].cssRules.length; ++x) {
              rules += ss[i].cssRules[x].cssText;
          }
      }

      var header = `<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
      <head><meta charset='utf-8'><title>Export HTML to Word Document with JavaScript</title>
      <style>${rules}</style>
      </head><body>`;
 
      var footer = "</body></html>";
  
      var html = header+document.getElementById('export-id').innerHTML+footer;
  
      var blob = new Blob(['\ufeff', html], {
          type: 'application/msword'
      });
      
      // Specify link url
      var url = 'data:application/vnd.ms-word;charset=utf-8,' + encodeURIComponent(html);
      
      // Specify file name
      let filename = filename?filename+'.doc':'document.doc';
      
      // Create download link element
      var downloadLink = document.createElement("a");
  
      document.body.appendChild(downloadLink);
      
      if(navigator.msSaveOrOpenBlob ){
          navigator.msSaveOrOpenBlob(blob, filename);
      }else{
          // Create a link to the file
          downloadLink.href = url;
          
          // Setting the file name
          downloadLink.download = filename;
          
          //triggering the function
          downloadLink.click();
      }
      
      document.body.removeChild(downloadLink);
    },
    handlePrint () {
      this.printRead = true

      this.$nextTick(() => {
        window.print()
      })
    },
    handleExportPDF () {
      const tempPrint = this.printRead
      this.exportLoading = true
      setTimeout(() => {
        this.printRead = true

        this.$nextTick(() => {
          const $form = this.previewForm.config.ui == 'element' || !this.previewForm.config.ui ?
            this.$refs.generateForm : this.$refs.generateAntForm

          $form.exportPDF().then(data => {
            this.$refs.pdfPreview.open(data)        
          }).finally(() => {
            this.exportLoading = false
            this.printRead = tempPrint
          })
        })
      }, 200)
    }
  }
}
</script>

<style lang="scss">
//Y9+++
.PreviewDialog .el-dialog__footer{
    position: fixed;
    right: 0;
}
.PreviewDialog .el-dialog__title{
    float: left;
    height: 35px;
    line-height: 35px;
}
.PreviewDialog .el-button{
    height: 32px;
}

.PreviewDialog .el-dialog__header{
    height: 55px;
    border-bottom: solid 2px #eeeeee;
    background-color: #fff;
}

.PreviewDialog .el-dialog__body{
    height: calc(100% - 60px) !important;
}

.PreviewDialog .platform-controller{
    top:65px !important;
}

.PreviewDialog .fm-generate-preview{
    box-shadow:none !important;
}
//Y9+++
.fm-generate-preview-container{
  .platform-controller{
    position: absolute;
    width: 120px;
    top: 50px;
    z-index: 5000;
    left: 20px;
    // margin-left: -60px;
  }

  .fm-generate-preview{
    margin-top: 15px !important;
  }
}

@media print{
  .fm-design-container{
    display: none;
  }

  .fm-generate-preview-container{
    position: relative;

    .el-dialog{
      display: block;
    }

    .el-dialog__header{
      display: none;
    }

    .el-dialog__footer{
      display: none;
    }

    .el-dialog__body{
      padding: 0;
      display: block;

      .fm-generate-preview{
        box-shadow: none;
        border: 0;
      }
    }

    .platform-controller{
      display: none;
    }
  }
}
</style>