<template>
  <div class="my-process-designer">
    <div class="my-process-designer__header">
      <slot name="control-header"></slot>
      <template v-if="!$slots['control-header']">
        <el-button-group key="file-control">
          <el-button :type="headerButtonType" @click="$refs.refFile.click()"><i class="ri-folders-line" style="margin-right: 5px;"></i>打开文件</el-button>
          <el-button :type="headerButtonType" @click="downloadProcessAsXml()"><i class="ri-download-2-line" style="margin-right: 5px;"></i>下载文件</el-button>
          <el-button :type="headerButtonType" @click="previewProcessXML"><i class="ri-eye-line" style="margin-right: 5px;"></i>预览</el-button>
          <el-button :type="headerButtonType" @click="saveModel"><i class="ri-save-3-line" style="margin-right: 5px;"></i>保存</el-button>
          <!-- <el-button :type="headerButtonType" @click="deployModel"><i class="ri-database-2-line" style="margin-right: 5px;"></i>部署</el-button> -->
        </el-button-group>
        <el-button-group key="stack-control">
          <el-tooltip effect="light">
            <el-button :disabled="!revocable" :type="headerButtonType" @click="processUndo()"><i class="ri-arrow-go-back-line" style="margin-right: 5px;"></i>撤销</el-button>
          </el-tooltip>
          <el-tooltip effect="light">
            <el-button :disabled="!recoverable" :type="headerButtonType" @click="processRedo()" ><i class="ri-arrow-go-forward-line" style="margin-right: 5px;"></i>恢复</el-button>
          </el-tooltip>
        </el-button-group>
        <el-button-group key="right-control" style="float: right;right: 14px;position: relative;">
            <el-tooltip effect="light">
                <el-button class="global-btn-second" @click="processRestart"><i class="ri-repeat-fill" style="margin-right: 5px;"></i>清空</el-button>
            </el-tooltip>
            <el-tooltip effect="light">
                <el-button class="global-btn-second" @click="closeDialog"><i class="ri-close-line" style="margin-right: 5px;"></i>关闭</el-button>
            </el-tooltip>
        </el-button-group>
      </template>
      <!-- 用于打开本地文件-->
      <input type="file" id="files" ref="refFile" style="display: none" accept=".xml" @change="importLocalFile" />
    </div>
    <div class="my-process-designer__container">
      <div class="my-process-designer__canvas" ref="bpmn-canvas"></div>
    </div>
    <el-dialog :title="`预览${previewType}`" width="60%" v-model="previewModelVisible" append-to-body destroy-on-close>
        <Codemirror
          v-model:value="previewResult"
          :options="cmOptions"
          border
          :height="600"
        />
    </el-dialog>
  </div>
</template>

<script>
import BpmnModeler from "bpmn-js/lib/Modeler";
import DefaultEmptyXML from "./plugins/defaultEmpty";
// 翻译方法
import customTranslate from "./plugins/translate/customTranslate";
import translationsCN from "./plugins/translate/zh";
// 模拟流转流程
import tokenSimulation from "bpmn-js-token-simulation";
// 标签解析构建器
// import bpmnPropertiesProvider from "bpmn-js-properties-panel/lib/provider/bpmn";
// 标签解析 Moddle
import camundaModdleDescriptor from './plugins/descriptor/camundaDescriptor.json';
import activitiModdleDescriptor from './plugins/descriptor/activitiDescriptor.json';
import flowableModdleDescriptor from './plugins/descriptor/flowableDescriptor.json';
// 标签解析 Extension
import camundaModdleExtension from './plugins/extension-moddle/camunda';
import activitiModdleExtension from './plugins/extension-moddle/activiti';
import flowableModdleExtension from './plugins/extension-moddle/flowable';
// 引入json转换与高亮
// import X2JS from "x2js";

import Codemirror from 'codemirror-editor-vue3';
import 'codemirror/theme/monokai.css'
import 'codemirror/mode/javascript/javascript.js';
import 'codemirror/mode/xml/xml.js';
import y9_storage from "@/utils/storage";
import settings from '@/settings.ts';
import axios from 'axios';
export default {
  name: "MyProcessDesigner",
  componentName: "MyProcessDesigner",
  components: {
    Codemirror
  },
  emits: ['destroy', 'init-finished', 'commandStack-changed', 'update:modelValue', 'change', 'canvas-viewbox-changed', 'element-click','saveModelXml','closeDialog'],
  props: {
    modelValue: String, // xml 字符串
    processId: String,
    processName: String,
    translations: Object, // 自定义的翻译文件
    options: {
      type: Object,
      default: () => ({})
    }, // 自定义的翻译文件
    additionalModel: [Object, Array], // 自定义model
    moddleExtension: Object, // 自定义moddle
    onlyCustomizeAddi: {
      type: Boolean,
      default: false
    },
    onlyCustomizeModdle: {
      type: Boolean,
      default: false
    },
    simulation: {
      type: Boolean,
      default: true
    },
    keyboard: {
      type: Boolean,
      default: true
    },
    prefix: {
      type: String,
      default: "flowable"
    },
    events: {
      type: Array,
      default: () => ["element.click"]
    },
    headerButtonType: {
      type: String,
      default: "primary"
    }
  },
  data() {
    return {
      defaultZoom: 1,
      previewModelVisible: false,
      simulationStatus: false,
      previewResult: "",
      previewType: "xml",
      recoverable: false,
      revocable: false,
      cmOptions: {
        mode: 'xml', // 语言模式
        theme: 'monokai', // 主题
        lineNumbers: true, // 显示行号
        smartIndent: true, // 智能缩进
        readOnly: true,
        indentUnit: 2, // 智能缩进单位为4个空格长度
        foldGutter: true, // 启用行槽中的代码折叠
        styleActiveLine: true // 显示选中行的样式
      }
    };
  },
  computed: {
    additionalModules() {
      const Modules = [];
      // 仅保留用户自定义扩展模块
      if (this.onlyCustomizeAddi) {
        if (Object.prototype.toString.call(this.additionalModel) === "[object Array]") {
          return this.additionalModel || [];
        }
        return [this.additionalModel];
      }

      // 插入用户自定义扩展模块
      if (Object.prototype.toString.call(this.additionalModel) === "[object Array]") {
        Modules.push(...this.additionalModel);
      } else {
        this.additionalModel && Modules.push(this.additionalModel);
      }

      // 翻译模块
      const TranslateModule = {
        translate: ["value", customTranslate(this.translations || translationsCN)]
      };
      Modules.push(TranslateModule);

      // 模拟流转模块
      if (this.simulation) {
        Modules.push(tokenSimulation);
      }

      // 根据需要的流程类型设置扩展元素构建模块
      if (this.prefix === "flowable") {
        Modules.push(flowableModdleExtension);
      }
      if (this.prefix === "activiti") {
        Modules.push(activitiModdleExtension);
      }

      return Modules;
    },
    moddleExtensions() {
      const Extensions = {};
      // 仅使用用户自定义模块
      if (this.onlyCustomizeModdle) {
        return this.moddleExtension || null;
      }

      // 插入用户自定义模块
      if (this.moddleExtension) {
        for (let key in this.moddleExtension) {
          Extensions[key] = this.moddleExtension[key];
        }
      }

      // 根据需要的 "流程类型" 设置 对应的解析文件
      if (this.prefix === "activiti") {
        Extensions.activiti = activitiModdleDescriptor;
      }
      if (this.prefix === "flowable") {
        Extensions.flowable = flowableModdleDescriptor;
      }
      return Extensions;
    }
  },
  mounted() {
    this.initBpmnModeler();
    this.createNewDiagram(this.modelValue);
  },
  beforeUnmount() {
    if (this.bpmnModeler) this.bpmnModeler.destroy();
    this.$emit("destroy", this.bpmnModeler);
    this.bpmnModeler = null;
  },
  methods: {
    initBpmnModeler() {
      if (this.bpmnModeler) return;
      this.bpmnModeler = new BpmnModeler({
        container: this.$refs["bpmn-canvas"],
        keyboard: this.keyboard ? { bindTo: document } : null,
        additionalModules: this.additionalModules,
        moddleExtensions: this.moddleExtensions,
        ...this.options
      });
      this.$emit("init-finished", this.bpmnModeler);
      this.initModelListeners();
    },
    initModelListeners() {
      const EventBus = this.bpmnModeler.get("eventBus");
      const that = this;
      // 注册需要的监听事件, 将. 替换为 - , 避免解析异常
      this.events.forEach(event => {
        EventBus.on(event, function(eventObj) {
          let eventName = event.replace(/\./g, "-");
          let element = eventObj ? eventObj.element : null;
          that.$emit(eventName, element, eventObj);
        });
      });
      // 监听图形改变返回xml
      EventBus.on("commandStack.changed", async event => {
        try {
          this.recoverable = this.bpmnModeler.get("commandStack").canRedo();
          this.revocable = this.bpmnModeler.get("commandStack").canUndo();
          let { xml } = await this.bpmnModeler.saveXML({ format: true });
          this.$emit("commandStack-changed", event);
          this.$emit('update:modelValue', xml);
          this.$emit("change", xml);
        } catch (e) {
          console.error(`[Process Designer Warn]: ${e.message || e}`);
        }
      });
    },
    
    /* 创建新的流程图 */
    async createNewDiagram(xml) {
      // 将字符串转换成图显示出来
      let newId = this.processId || `Process_${new Date().getTime()}`;
      let newName = this.processName || `业务流程_${new Date().getTime()}`;
      let xmlString = xml || DefaultEmptyXML(newId, newName, this.prefix);
      try {
        let { warnings } = await this.bpmnModeler.importXML(xmlString);
        if (warnings && warnings.length) {
          warnings.forEach(warn => console.warn(warn));
        }
      } catch (e) {
        console.error(`[Process Designer Warn]: ${e?.message || e}`);
      }
    },

    // 下载流程图到本地
    /**
     * @param {string} type
     * @param {*} name
     */
    async downloadProcess(type, name) {
      try {
        const _this = this;
        // 按需要类型创建文件并下载
        if (type === "xml" || type === "bpmn") {
          const { err, xml } = await this.bpmnModeler.saveXML();
          // 读取异常时抛出异常
          if (err) {
            console.error(`[Process Designer Warn ]: ${err.message || err}`);
          }
          let { href, filename } = _this.setEncoded(type, name, xml);
          downloadFunc(href, filename);
        } else {
          const { err, svg } = await this.bpmnModeler.saveSVG();
          // 读取异常时抛出异常
          if (err) {
            return console.error(err);
          }
          let { href, filename } = _this.setEncoded("SVG", name, svg);
          downloadFunc(href, filename);
        }
      } catch (e) {
        console.error(`[Process Designer Warn ]: ${e.message || e}`);
      }
      // 文件下载方法
      function downloadFunc(href, filename) {
        if (href && filename) {
          let a = document.createElement("a");
          a.download = filename; //指定下载的文件名
          a.href = href; //  URL对象
          a.click(); // 模拟点击
          URL.revokeObjectURL(a.href); // 释放URL 对象
        }
      }
    },

    // 根据所需类型进行转码并返回下载地址
    setEncoded(type, filename = "diagram", data) {
      filename = this.bpmnModeler.getDefinitions().rootElements[0].id;
      const encodedData = encodeURIComponent(data);
      return {
        filename: `${filename}.${type}`,
        href: `data:application/${type === "svg" ? "text/xml" : "bpmn20-xml"};charset=UTF-8,${encodedData}`,
        data: data
      };
    },

    // 加载本地文件
    importLocalFile() {
      const that = this;
      const file = this.$refs.refFile.files[0];
      const reader = new FileReader();
      reader.readAsText(file);
      reader.onload = function() {
        let xmlStr = this.result;
        that.createNewDiagram(xmlStr);
      };
    },

    async saveModel(){
      const {xml} = await this.bpmnModeler.saveXML({format: true});
      let formData = new FormData();
      let file = new Blob([xml], { type: 'text/xml' },'activiti.xml');
      formData.append('file', file)
      let config = {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      };
      const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
      let url = import.meta.env.VUE_APP_PROCESS_CONTEXT + "vue/processModel/saveModelXml?access_token=" + y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
      axios.post(url,formData,config).then((res)=>{
          this.$emit("saveModelXml");
          loading.close();
          ElMessage({ type: res.data.success ? 'success' : 'error', message: res.data.msg, offset: 65 });
      }).catch((err)=>{
          loading.close();
          ElMessage({ type: 'error', message: "发生异常", offset: 65 });
      });
    },

    deployModel(){
      console.log(this.bpmnModeler.getDefinitions().rootElements[0]);
    },

    closeDialog(){
      this.$emit("closeDialog");
    },

    /* ------------------------------------------------ refs methods ------------------------------------------------------ */
    downloadProcessAsXml() {
      this.downloadProcess("xml");
    },
    downloadProcessAsBpmn() {
      this.downloadProcess("bpmn");
    },
    downloadProcessAsSvg() {
      this.downloadProcess("svg");
    },
    processRedo() {
      this.bpmnModeler.get("commandStack").redo();
    },
    processUndo() {
      this.bpmnModeler.get("commandStack").undo();
    },
    processRestart() {
      this.recoverable = false;
      this.revocable = false;
      this.createNewDiagram(null);
    },
    /*-----------------------------    方法结束     ---------------------------------*/
    previewProcessXML() {
      this.bpmnModeler.saveXML({ format: true }).then(({ xml }) => {
        this.previewResult = xml;
        this.previewType = 'xml';
        this.cmOptions.mode = 'xml'
        this.previewModelVisible = true;
      });
    },
  }
};
</script>
