<template>
  <div class="antd-form-table" :class="{'is-disabled': disableddata}">
    <template v-if="(preview && platform != 'mobile') || (!preview && !mobileScreen)">
      <a-table
        :dataSource="paging ? pagingData : tableData" 
        :pagination="false" bordered
        :class="{'antd-form-table-pc': !preview}"
        v-if="!widget.options.virtualTable"
        table-layout="auto"
        :scroll="{x: 'max-content'}"
        :size="tableSize"
      >
          <a-table-column
            v-if="showControl"
            :scopedSlots="{ customRender: '#' }"
            title="#"
            data-index="#"
            key="#"
            fixed="left"
            :width="80">
            <template #default="{index}">
              <div class="scope-index">
                <span>{{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</span>
              </div>
              <div class="scope-action">
                
                <a-popconfirm :disabled="disableddata" v-if="!printRead && isDelete" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + index)">
            
                  <a-button :disabled="disableddata" type="primary" danger shape="circle"  size="small"><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></a-button>
                  
                </a-popconfirm>
                <span v-if="printRead || !isDelete">{{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</span>
              </div>
            </template>
          </a-table-column>
          <a-table-column v-if="columns.length==0"></a-table-column>
          <template v-else>
            <template v-for="column in columns" >
              <a-table-column 
                :key="column.key"
                v-if="columnDisplay(column.model)"
                :data-index="column.key"
                :title="column.options.hideLabel ? '' : column.name"
                :width="column.options.width"
                :class="column.options.required ? 'required' : ''"
                :scopedSlots="{ customRender: column.key }"
                :fixed="column.options.fixedColumn ? (column.options.fixedColumnPosition || 'left') : false"
              >
                <template #default="{index}">
                  <generate-form-item
                    :models="tableData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
                    :rules="rules"
                    :widget="column"
                    :remote="remote"
                    :blanks="blanks"
                    :display="displayFields"
                    :sub-hide-fields="tableHideFields"
                    :sub-disabled-fields="tableDisabledFields"
                    :edit="!disableddata"
                    :remote-option="remoteOption"
                    :platform="platform"
                    :preview="preview"
                    :container-key="containerKey"
                    :data-source-value="dataSourceValue"
                    :event-function="eventFunction"
                    :print-read="printRead"
                    :is-table="true"
                    :is-mobile="false"
                    :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
                    :table-name="name"
                    :config="config"
                    :is-dialog="isDialog"
                    :dialog-name="dialogName"
                    :is-group="false"
                    :group="group ? `${group}.${name}` : name"
                    :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
                  >
                    <template v-slot:[blank.name]="scope" v-for="blank in blanks">
                      <slot :name="blank.name" :model="scope.model"></slot>
                    </template>
                  </generate-form-item>

                </template>
                
              </a-table-column>
            </template>
            
          </template>
        <!-- </a-table-column-group> -->
      </a-table>

      <VirtualTable
        v-if="widget.options.virtualTable"
        :columns="columns"
        :showControl="showControl"
        :paging="paging"
        :pageSize="pageSize"
        :currentPage="currentPage"
        :displayFields="displayFields"
        :group="group"
        :widget="widget"
      >
        <template v-slot="{rowWidthCalc, rowLeftWidthCalc, rowRightWidthCalc}">
          <VirtualRow :rowWidthCalc="rowWidthCalc"
            v-for="(t, index) in (paging ? pagingLength : tableData)"
            :key="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :row-index="index"
            class="main"
            :table-key="tableKey"
          >
            <VirtualCell width="50px" v-if="showControl"></VirtualCell>
            <VirtualCell :width="`calc(${rowLeftWidthCalc})`" v-if="rowLeftWidthCalc != '0px'"></VirtualCell>
            <template v-for="column in columns">
              <VirtualCell :key="column.key" v-if="columnDisplay(column.model) && !column.options.fixedColumn" :width="column.options.width">
                <generate-form-item
                  :models="tableData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
                  :rules="rules"
                  :widget="column"
                  :remote="remote"
                  :blanks="blanks"
                  :display="displayFields"
                  :sub-hide-fields="tableHideFields"
                  :sub-disabled-fields="tableDisabledFields"
                  :edit="!disableddata"
                  :remote-option="remoteOption"
                  :platform="platform"
                  :preview="preview"
                  :container-key="containerKey"
                  :data-source-value="dataSourceValue"
                  :event-function="eventFunction"
                  :print-read="printRead"
                  :is-table="true"
                  :is-mobile="false"
                  :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
                  :table-name="name"
                  :config="config"
                  :is-dialog="isDialog"
                  :dialog-name="dialogName"
                  :is-group="false"
                  :group="group ? `${group}.${name}` : name"
                  :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
                >
                  <template v-slot:[blank.name]="scope" v-for="blank in blanks">
                    <slot :name="blank.name" :model="scope.model"></slot>
                  </template>
                </generate-form-item>
              </VirtualCell>
            </template>
            <VirtualCell :width="`calc(${rowRightWidthCalc})`" v-if="rowRightWidthCalc != '0px'"></VirtualCell>
          </VirtualRow>
        </template>
        <template #left>
          <VirtualRow 
            v-for="(t, index) in (paging ? pagingLength : tableData)" :key="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :rowIndex="index"  
            class="left"
            :table-key="tableKey"
          >
            <VirtualCell width="50px" v-if="showControl">
              <div class="scope-index">
                <span>{{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</span>
              </div>
              <div class="scope-action">
                
                <a-popconfirm :disabled="disableddata"  v-if="!printRead && isDelete" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + index)">
            
                  <a-button :disabled="disableddata" type="primary" danger shape="circle"  size="small"><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></a-button>
                  
                </a-popconfirm>
                <span v-if="printRead || !isDelete">{{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</span>
              </div>
            </VirtualCell>
            <template v-for="column in columns">
              <VirtualCell :key="column.key" v-if="columnDisplay(column.model) && column.options.fixedColumn && column.options.fixedColumnPosition != 'right'" :width="column.options.width">
                <generate-form-item
                  :models="tableData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
                  :rules="rules"
                  :widget="column"
                  :remote="remote"
                  :blanks="blanks"
                  :display="displayFields"
                  :sub-hide-fields="tableHideFields"
                  :sub-disabled-fields="tableDisabledFields"
                  :edit="!disableddata"
                  :remote-option="remoteOption"
                  :platform="platform"
                  :preview="preview"
                  :container-key="containerKey"
                  :data-source-value="dataSourceValue"
                  :event-function="eventFunction"
                  :print-read="printRead"
                  :is-table="true"
                  :is-mobile="false"
                  :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
                  :table-name="name"
                  :config="config"
                  :is-dialog="isDialog"
                  :dialog-name="dialogName"
                  :is-group="false"
                  :group="group ? `${group}.${name}` : name"
                  :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
                >
                  <template v-slot:[blank.name]="scope" v-for="blank in blanks">
                    <slot :name="blank.name" :model="scope.model"></slot>
                  </template>
                </generate-form-item>
              </VirtualCell>
            </template>
          </VirtualRow>
        </template>
        <template #right>
          <VirtualRow 
            v-for="(t, index) in (paging ? pagingLength : tableData)" :key="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :rowIndex="index"
            class="right"
            :table-key="tableKey"
          >
            <template v-for="column in columns">
              <VirtualCell :key="column.key" v-if="columnDisplay(column.model) && column.options.fixedColumn && column.options.fixedColumnPosition == 'right'" :width="column.options.width">
                <generate-form-item
                  :models="tableData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
                  :rules="rules"
                  :widget="column"
                  :remote="remote"
                  :blanks="blanks"
                  :display="displayFields"
                  :sub-hide-fields="tableHideFields"
                  :sub-disabled-fields="tableDisabledFields"
                  :edit="!disableddata"
                  :remote-option="remoteOption"
                  :platform="platform"
                  :preview="preview"
                  :container-key="containerKey"
                  :data-source-value="dataSourceValue"
                  :event-function="eventFunction"
                  :print-read="printRead"
                  :is-table="true"
                  :is-mobile="false"
                  :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
                  :table-name="name"
                  :config="config"
                  :is-dialog="isDialog"
                  :dialog-name="dialogName"
                  :is-group="false"
                  :group="group ? `${group}.${name}` : name"
                  :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
                >
                  <template v-slot:[blank.name]="scope" v-for="blank in blanks">
                    <slot :name="blank.name" :model="scope.model"></slot>
                  </template>
                </generate-form-item>
              </VirtualCell>
            </template>
          </VirtualRow>
        </template>
      </VirtualTable>
    </template>

    <div class="antd-form-table-mobile"
      v-if="(preview && platform === 'mobile') || (!preview && mobileScreen)"
      :style="{'display': (preview && platform === 'mobile') ? 'block' : ''}"
    >
      <div class="antd-form-table-mobile-item"
        v-for="(t, index) in (paging ? pagingLength : tableData)"
        :key="(paging ? pageSize * (currentPage - 1) : 0) + index"
      >
        <div class="antd-form-table-mobile-item__top" v-if="showControl">
          <span># {{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</span>
          <a-popconfirm :disabled="disableddata"  v-if="!printRead && isDelete" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + index)">
          
            <a-button :disabled="disableddata" type="primary" danger shape="circle"  size="small"><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></a-button>
            
          </a-popconfirm>
        </div>
        <div class="antd-form-table-mobile-item__content">
          <a-space direction="vertical" style="width: 100%;">
            <template v-for="column in columns" :key="column.key" >
              <generate-form-item
                :models="tableData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
                :rules="rules"
                :widget="column"
                :remote="remote"
                :blanks="blanks"
                :display="displayFields"
                :sub-hide-fields="tableHideFields"
                :sub-disabled-fields="tableDisabledFields"
                :edit="!disableddata"
                :remote-option="remoteOption"
                :platform="platform"
                :preview="preview"
                :container-key="containerKey"
                :data-source-value="dataSourceValue"
                :event-function="eventFunction"
                :print-read="printRead"
                :is-table="true"
                :is-mobile="true"
                :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
                :table-name="name"
                :config="config"
                :is-dialog="isDialog"
                :dialog-name="dialogName"
                :is-group="false"
                :group="group ? `${group}.${name}` : name"
                :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
              >
                <template v-slot:[blank.name]="scope" v-for="blank in blanks">
                  <slot :name="blank.name" :model="scope.model"></slot>
                </template>
              </generate-form-item>
            </template>
          </a-space>
        </div>
      </div>
    </div>

    <a-row>
      <a-col :md="(preview && platform != 'mobile') || !preview ? 12 : 24" :xs="24" :sm="24">
        <a-button  type="link" @click="handleAddRow" v-if="!disableddata && isAdd">
          <i class="fm-iconfont icon-plus" style="font-size: 12px; margin: 5px;"></i>{{$t('fm.actions.add')}}
        </a-button>
        
      </a-col>
      <a-col :md="((preview && platform != 'mobile') || !preview) && !disableddata ? 12 : 24" :xs="24" :sm="24">
        <a-pagination
          style="float: right; padding-top: 5px;white-space: nowrap;"
          layout="total, prev, pager, next"
          :page-size="pageSize"
          v-model:current="currentPage"
          :total="tableData.length"
          :pager-count="5"
          @change="handlePageChange"
          v-if="paging && tableData.length"
          show-less-items
        >
        </a-pagination>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'
import _ from 'lodash'
import VirtualTable from '../VirtualTable/index.vue'
import VirtualRow from '../VirtualTable/row.vue'
import VirtualCell from '../VirtualTable/cell.vue'
import { updateClassName } from '../../util/reuse-methods'

export default {
  components: {
    VirtualTable,
    VirtualRow,
    VirtualCell,
    GenerateFormItem: defineAsyncComponent(() => import('./GenerateFormItem.vue')),
    GenerateElementItem: defineAsyncComponent(() => import('./GenerateElementItem.vue'))
  },
  props: ['config', 'columns', 'value', 'models', 'remote', 'blanks', 'disableddata', 'rules', 'name', 
    'remoteOption', 'preview', 'platform', 'dataSourceValue', 'eventFunction', 'widget', 
    'containerKey', 'printRead', 'paging', 'pageSize', 'isAdd', 'isDelete', 
    'showControl', 'isDialog', 'dialogName', 'group', 'fieldNode'],
  emits: ['update:value'],
  data () {
    return {
      tableData: this.value ?? [],
      displayFields: {},
      disabledFields: {},
      changeItem: {},
      pagingData: [],
      pagingLength: 0,
      currentPage: 1,
      tableHideFields: [],
      tableDisabledFields: [],
      mobileScreen: window.innerWidth < 768,
      tableKey: Math.random().toString(36).slice(-8)
    }
  },
  created () {
    for (let i = 0; i < this.columns.length; i++) {
      this.displayFields[this.columns[i].model] = !this.columns[i].options.hidden
      this.disabledFields[this.columns[i].model] = this.columns[i].options.disabled
    }

    this.tableHideFields = this.value ? this.value.map(v => Object.fromEntries(Object.keys(this.displayFields).map(field => [field, !this.displayFields[field]]))) : []

    this.tableDisabledFields = this.value ? this.value.map(v => Object.fromEntries(Object.keys(this.disabledFields).map(field => [field, this.disabledFields[field]]))) : []

    window.addEventListener('resize', this.getMobileScreen)

    this.loadPagingData()
  },
  computed: {
    tableSize () {
      switch (this.config.size) {
        case 'large': return 'default'; break;
        case 'default': return 'middle'; break;
        case 'small': return 'small'; break;
      }
    },
    // labelWidth () {
    //   return this.widget.options.hideLabel ? '0px' : (this.widget.options.isLabelWidth ? this.widget.options.labelWidth + 'px' : this.config.labelWidth + 'px')
    // }
  },
  provide () {
    return {
      'setTableData': this.setTableData
    }
  },
  inject: ['onFormDisabled', 'formHideFields', 'onFormHide', 'onFormDisplay'],
  methods: {
    columnDisplay (model) {
      if (this.formHideFields.includes(this.group ? `${this.group}.${this.widget.model}.${model}` : `${this.widget.model}.${model}`)
      ) {
        return false
      } else {
        return true
      }
    },
    getMobileScreen () {
      return _.debounce(() => {
        this.mobileScreen = window.innerWidth < 768
      }, 50)
    },
    setData (rowIndex, value) {
      if (typeof rowIndex !== 'number') {
        return new Promise((resolve) => {
          value = rowIndex

          this.tableData.forEach((_, index) => {
            Object.keys(value).forEach(item => {
              this.setTableData(value[item], index, item)
            })
          })

          resolve()
        })
      }
      return new Promise((resolve) => {
        this.$nextTick(() => {
          Object.keys(value).forEach(item => {
            this.setTableData(value[item], rowIndex, item)
          })
          resolve()
        })
      })
    },
    setTableData (value, rowIndex, field) {
      this.tableData[rowIndex][field] = value
    },
    handleAddRow () {
      let item = {}
      let hideItem = {}
      let disabledItem = {}
      for (let i = 0; i < this.columns.length; i++) {
        if (this.columns[i].type === 'blank') {
          item[this.columns[i].model] = this.columns[i].options.defaultType == 'String' ? '' : (this.columns[i].options.defaultType == 'Object' ? {} : [])
        } else if (this.columns[i].type === 'component' || this.columns[i].type === 'link' || this.columns[i].type === 'button') {
          item[this.columns[i].model] = undefined
        } else {
          item[this.columns[i].model] = _.cloneDeep(this.columns[i].options.defaultValue)
        }
        hideItem[this.columns[i].model] = !this.displayFields[this.columns[i].model]
        disabledItem[this.columns[i].model] = this.disabledFields[this.columns[i].model]
      }

      this.tableData.push(item)
      this.tableHideFields.push(hideItem)
      this.tableDisabledFields.push(disabledItem)

      if (this.widget.events && this.widget.events.onRowAdd) {
        let funcKey = this.widget.events.onRowAdd

        this.eventFunction[funcKey]({
          rowIndex: this.tableData.length - 1, 
          field: this.widget.model,
          currentRef: this,
          group: this.group,
          fieldNode: this.fieldNode
        })
      }

      this.changeItem = {}

      if (this.paging) {
        this.$nextTick(() => {
          if (this.tableData.length > this.currentPage * this.pageSize) {
            this.currentPage = parseInt((this.tableData.length - 1) / this.pageSize) + 1
          }

          this.loadPagingData()
        })
      }
    },

    handleRemove (index) {
      const removeData = {...this.tableData[index]}
      this.tableData.splice(index, 1)

      if (this.widget.events && this.widget.events.onRowRemove) {
        let funcKey = this.widget.events.onRowRemove

        this.eventFunction[funcKey]({
          removeIndex: index, 
          removeData: removeData,
          field: this.widget.model,
          currentRef: this,
          group: this.group,
          fieldNode: this.fieldNode
        })
      }

      this.changeItem = {}

      this.pagingData = []
      this.pagingLength = 0

      if (this.paging) {
        this.$nextTick(() => {
          if (this.tableData.length % this.pageSize == 0 && this.currentPage > parseInt(this.tableData.length / this.pageSize)) {
            this.currentPage = parseInt(this.tableData.length / this.pageSize)
          }

          this.loadPagingData()
        })
      }
    },
    hide (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.onFormHide(`${this.fieldNode}.${field}`)
      })
    },
    display (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.onFormDisplay(`${this.fieldNode}.${field}`)
      })
    },
    hideChild (rowIndex, fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.onFormHide(`${this.fieldNode}.${rowIndex}.${field}`)
      })
    },
    displayChild (rowIndex, fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.onFormDisplay(`${this.fieldNode}.${rowIndex}.${field}`)
      })
    },
    disabled (fields, disabled) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      for (let i = 0; i < this.columns.length; i++) {
        if (fields.indexOf(this.columns[i].model) >= 0) {
            
          this.columns[i].options.disabled = disabled

          this.disabledFields[this.columns[i].model] = disabled
        }
      }

      this.tableData.forEach((_, index) => {
        fields.forEach(field => {
          this.tableDisabledFields[index][field] = disabled
        })
      })
    },
    disabledChild (rowIndex, fields, disabled) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.tableDisabledFields[rowIndex][field] = disabled

        this.onFormDisabled(`${this.fieldNode}.${rowIndex}.${field}`, disabled)
      })
    },
    setOptions (fields, options) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      for (let i = 0; i < this.columns.length; i++) {
        if (fields.indexOf(this.columns[i].model) >= 0) {

          Object.keys(options).forEach(key => {
            this.columns[i].options[key] = options[key]
          })
        }
      } 
    },
    addClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.columns, item.split('.'), className, 'add')
      })
    },
    removeClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.columns, item.split('.'), className, 'remove')
      })
    },
    handleTableChange (value) {
      this.changeItem = value
    },

    handlePageChange (val) {
      this.currentPage = val

      this.pagingData = []
      this.pagingLength = 0

      this.$nextTick(() => {
        this.loadPagingData()
      })

      if (this.widget && this.widget.events && this.widget.events.onPageChange) {
        let funcKey = this.widget.events.onPageChange

        this.eventFunction[funcKey]({
          currentPage: val, 
          field: this.widget.model,
          currentRef: this,
          group: this.group,
          fieldNode: this.fieldNode
        })
      }
    },

    loadPagingData () {
      let beginIndex = (this.currentPage - 1) * this.pageSize

      let endIndex = beginIndex + this.pageSize

      this.pagingData = this.tableData?.slice(beginIndex, endIndex)

      this.pagingLength = this.pagingData.length
    }
  },
  beforeUnmount () {
    window.removeEventListener('resize', this.getMobileScreen)
  },
  watch: {
    value (val) {
      this.tableData = val

      let hideFields = []
      let disabledFields = []
      for (let i = 0; i < this.value?.length; i++) {
        let row = this.value[i]
        let rowArray = Object.keys(row)
        let hideRow = {}
        let disabledRow = {}
        for (let f = 0; f < rowArray.length; f++) {
          hideRow[rowArray[f]] = this.tableHideFields?.[i]?.[rowArray[f]] || false
          disabledRow[rowArray[f]] = this.tableDisabledFields?.[i]?.[rowArray[f]] || false
        }

        hideFields.push(hideRow)
        disabledFields.push(disabledRow)
      }

      this.tableHideFields = hideFields
      this.tableDisabledFields = disabledFields
    },
    'tableData': {
      deep: true,
      handler (val) {

        this.loadPagingData()

        this.$emit('update:value', val)
      }
    }
  }
}
</script>

<style lang="scss">
.fm-form .fm-form-item .antd-form-table{
  .ant-form-item{
    .ant-col{
      max-width: none;
    }

    .ant-form-item-label{
      flex: 0 0 auto;
    }

    .ant-form-item-control{
      flex: 1;
    }
  }

  .ant-form-item{
    .ant-form-item-label{
      width: var(--labelWidth)
    }
  }

  .no-label-left{
    .ant-form-item-control{
      margin-left: var(--labelWidth);
    }
  }
}

.antd-form-table{

  .fm-virtual-table{
    .scope-index{
      margin-bottom: 18px;
    }
    .scope-action{
      margin-bottom: 18px;
    }

    .fm-virtual-table-main{

      .fm-virtual-table__body{

        .fm-virtual-table__row{
          border-bottom: 1px solid #f0f0f0;

          &.is-hover{
            background-color: #fafafa;
          }
        }
      }

      .fm-virtual-table__header{

        .fm-virtual-table__header-content{
          border-bottom: 1px solid #f0f0f0;
          background: #fafafa;
        }

        .fm-virtual-table__header-row{

          .fm-virtual-table__header-cell{
            border-bottom: 1px solid #f0f0f0;
          }
        }
      }
    }
  }

  .ant-form-item{
    margin-bottom: 0;
  }

  .ant-form-explain{
    position: absolute;
    margin-top: -5px;
  }

  td{
    vertical-align: top;
  }

  .ant-table th.required div::before{
    content: '*';
    color: #f56c6c;
    margin-right: 4px;
    background: transparent;
    vertical-align: top;
  }

  .ant-table-body{
    overflow: auto;
  }

  .scope-action{
    display: none;
  }

  .scope-index{
    display: block;
  }

  .ant-table-row:hover{
    .scope-action{
      display: block;
      .el-button{
        padding: 3px;
      }
    }

    .scope-index{
      display: none;
    }
  }

  .ant-table-row-hover{
    .scope-action{
      display: block;
      .el-button{
        padding: 3px;
      }
    }

    .scope-index{
      display: none;
    }
  }

  .ant-table-pagination{
    display: none;
  }

  .ant-empty-normal{
    margin: 0;
  }

  .antd-form-table-pc{
    display: block;
  }

  .antd-form-table-mobile{
    display: none;

    .antd-form-table-mobile-item{
      border: 1px solid #e8e8e8;
      margin-bottom: 10px;

      .antd-form-table-mobile-item__top{
        height: 36px;
        line-height: 36px;
        padding: 0 10px;
        background:  #fafafa;
        border-bottom: 1px solid #e8e8e8;
        font-weight: 500;

        button{
          float: right;
          margin-top: 4px;
        }
      }

      .antd-form-table-mobile-item__content{
        padding: 8px;
      }

    }
  }
}

html.dark{

  .antd-form-table{

    .fm-virtual-table{

      .fm-virtual-table-main{

        .fm-virtual-table__body{

          .fm-virtual-table__row{
            border-bottom: 1px solid #303030;

            &.is-hover{
              background-color: #1d1d1d;
            }
          }
        }

        .fm-virtual-table__header{

          .fm-virtual-table__header-content{
            border-bottom: 1px solid #303030;
            background: #1d1d1d;
          }

          .fm-virtual-table__header-row{

            .fm-virtual-table__header-cell{
              border-bottom: 1px solid #303030;
            }
          }
        }
      }
    }

    .antd-form-table-mobile{

      .antd-form-table-mobile-item{
        border: 1px solid #303030;

        .antd-form-table-mobile-item__top{
          background:  #1d1d1d;
          border-bottom: 1px solid #303030;
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .antd-form-table{
    .antd-form-table-pc{
      display: none;
    }

    .antd-form-table-mobile{
      display: block;
    }
  }
}
</style>
