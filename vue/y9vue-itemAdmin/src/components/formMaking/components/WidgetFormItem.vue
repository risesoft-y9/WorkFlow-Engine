<template>
<div>
  <div class="widget-view"
    :class="{
      active: select.key == element.key, 
      'is_req': element.options.required, 
      'is_hidden': element.options.hidden,
    }"
    @click.stop="handleSelectWidget(index)"
    @mouseover.stop="handleMouseover"
    @mouseout="handleMouseout"
    v-if="element && element.key && element.type != 'divider' && element.type != 'alert'"
  >
    <el-form-item 
      :class="{
        [element.options && element.options.customClass]: element.options.customClass?true: false,
        'fm-label-wrap': element.options.labelWrap
      }"
      :label="element.options.hideLabel ? '' : element.name"
      :label-width="element.options.hideLabel ? '0px' : (element.options.isLabelWidth ? element.options.labelWidth + 'px' : '')"
    >
      <div v-if="element.options.tip" class="fm-item-tooltip" v-html="element.options.tip.replace(/\n/g, '<br/>')"></div>
      <widget-element-item :element="element" :is-table="false" :key="element.key"></widget-element-item>
    </el-form-item>

    <div class="widget-view-action" v-if="select.key == element.key">
      <!-- Y9字段权限 -->
      <i v-if="element.type == 'input' || element.type == 'textarea' || element.type == 'number' || element.type == 'radio' || element.type == 'checkbox'
        || element.type == 'select' || element.type == 'time' || element.type == 'date'" 
        class="fm-iconfont icon-biaogeshezhi" @click.stop="permissionConfig(index)" :title="$t('fm.tooltip.authority')"></i>
       <!-- Y9字段权限 -->
      <i class="fm-iconfont icon-icon_clone" @click.stop="handleWidgetClone(index)" :title="$t('fm.tooltip.clone')"></i>
      <i class="fm-iconfont icon-trash" @click.stop="handleWidgetDelete(index)" :title="$t('fm.tooltip.trash')"></i>
    </div>

    <div class="widget-view-drag" v-if="select.key == element.key">
      <i class="fm-iconfont icon-drag drag-widget"></i>
    </div>

    <div class="widget-view-model" :style="{'color': element.options.dataBind ? '' : '#666'}">
      <!-- Y9 -->
      <span v-if="isBind && !fieldPermission" style="color:blue">{{element.model}}</span>
      <span v-else-if="fieldPermission" style="color:red">{{element.model}}</span>
      <span v-else>{{element.model}}</span>
    </div>

    <div class="widget-view-type ">
      <span>{{element.type ? this.$t('fm.components.fields.' + element.type) : ''}}</span>
    </div>
  </div>
  <div class="widget-view no-put"
    v-if="element && element.key && (element.type == 'divider' || element.type == 'alert')" 
    :class="{active: select.key == element.key, 'is_hidden': element.options.hidden}"
    @click.stop="handleSelectWidget(index)"
    style="padding-bottom: 0;"
    @mouseover.stop="handleMouseover"
    @mouseout="handleMouseout"
  >
    <el-form-item label-width="0">
      <el-divider 
        :content-position="element.options.contentPosition"
        v-bind="element.options.customProps"
        v-if="element.type == 'divider'">
        {{element.name}}
      </el-divider>

      <el-alert v-if="element.type == 'alert'"
        :title="element.options.title"
        :type="element.options.type"
        :description="element.options.description"
        :closable="element.options.closable"
        :center="element.options.center"
        :show-icon="element.options.showIcon"
        :effect="element.options.effect"
        :style="{width: element.options.width}"
        v-bind="element.options.customProps"
      ></el-alert>
    </el-form-item>

    <div class="widget-view-action" v-if="select.key == element.key">
      <i class="fm-iconfont icon-icon_clone" @click.stop="handleWidgetClone(index)" :title="$t('fm.tooltip.clone')"></i>
      <i class="fm-iconfont icon-trash" @click.stop="handleWidgetDelete(index)" :title="$t('fm.tooltip.trash')"></i>
    </div>

    <div class="widget-view-drag" v-if="select.key == element.key">
      <i class="fm-iconfont icon-drag drag-widget"></i>
    </div>

    <div class="widget-view-model " :style="{'color': element.options.dataBind ? '' : '#666'}">
      <!-- Y9 -->
      <span v-if="isBind" style="color:blue">{{element.model}}</span>
      <span v-else>{{element.model}}</span>
    </div>

    <div class="widget-view-type ">
      <span>{{element.type ? this.$t('fm.components.fields.' + element.type) : ''}}</span>
    </div>
  </div>
  <permissionConfig ref="permissionConfig" @refresh="reloadPermission"/>
</div>
</template>

<script>
import WidgetElementItem from './WidgetElementItem.vue'
import _, { remove } from 'lodash'
import { EventBus } from '../util/event-bus.js'
import { addClass, removeClass } from '../util/index'
//Y9
import permissionConfig from './SecondDev/permissionConfig.vue'
import {getAllPerm} from "@/api/itemAdmin/y9form_fieldPerm";
export default {
  props: ['element', 'select', 'index', 'data', 'formKey','fieldBind','formId','fieldList'],
  components: {
    WidgetElementItem,
    permissionConfig,
  },
  emits: ['select-change', 'update:data', 'update:select'],
  data () {
    return {
      //Y9
      isBind:false,
      fieldPermission:false,
      y9fieldList:this.fieldList,
    }
  },
  mounted () {
    this.isBindField();//Y9
  },
  methods: {
    isFieldPerm(){//Y9判断是否已配置权限
      if (this.element.type == 'input' || this.element.type == 'textarea' || this.element.type == 'number' || this.element.type == 'radio' || this.element.type == 'checkbox'
        || this.element.type == 'select' || this.element.type == 'time' || this.element.type == 'date') {
        for (let field of this.y9fieldList) {
          if(field == this.element.model){
            this.fieldPermission = true;
            break;
          }
        }
      }
    },
    isBindField(){//Y9判断是否绑定数据库字段
      if(this.fieldBind != undefined){
        for(let item of this.fieldBind){
          if(item.fieldName == this.element.model){
            this.isBind = true;
            break;
          }
        }
      }
    },
    handleMouseover (e) {
      addClass(e.target, 'is-hover')
    },
    handleMouseout (e) {
      removeClass(e.target, 'is-hover')
    },
    handleSelectWidget (index) {
      this.$emit('update:select', this.data.list[index])
    },
    handleWidgetDelete (index) {
      if (this.data.list.length == 1) {
        this.$emit('select-change', -1)
      } else {
        if (this.data.list.length - 1 == index) {
          this.$emit('select-change', index - 1)
        } else {
          this.$emit('select-change', index)
        }
      }

      this.$nextTick(() => {
        this.data.list.splice(index, 1)

        setTimeout(() => {
          
          EventBus.$emit('on-history-add-' + this.formKey)
        }, 20)
      })
    },
    handleWidgetClone (index) {
      const key = Math.random().toString(36).slice(-8)
      let cloneData = {
        ..._.cloneDeep(this.data.list[index]),
        key,
        model: this.data.list[index].type + '_' + key,
      }
      
      this.data.list.splice(index + 1, 0, cloneData)

      this.$nextTick(() => {
        this.$emit('update:select', this.data.list[index + 1])

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      })
    },
    permissionConfig() {//Y9_权限配置
      if (!this.isBind) {
          this.$message({type: 'error', message: "请先绑定数据库字段"});
          return;
      } else {
          this.$refs.permissionConfig.show(this.formId, this.element.model);
      }
    },
    reloadPermission(){
      getAllPerm(this.formId).then(res => {//Y9获取权限配置字段
        this.y9fieldList = res.data;
        this.fieldPermission = false;
        for (let field of this.y9fieldList) {
          if(field == this.element.model){
            this.fieldPermission = true;
            break;
          }
        }
      });
    }
  },
  watch: {
    'fieldBind':{//Y9
      handler(val){
        this.isBindField();
      },
      deep:true
    },
    'fieldList':{//Y9
      handler(val){
        this.y9fieldList = val;
        this.isFieldPerm();
      },
      deep:true
    },
  }
}
</script>
