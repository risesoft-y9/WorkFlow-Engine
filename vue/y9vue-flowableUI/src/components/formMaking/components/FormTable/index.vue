<template>
  <div class="form-table" :class="{'is-disabled': disableddata}">
    <template v-if="(preview && platform != 'mobile') || (!preview && !mobileScreen)">
      <el-table
        :data="paging ? pagingData : tableData"
        border
        :class="{'form-table-pc': !preview}"
        v-if="!widget.options.virtualTable"
      >
        <el-table-column
          v-if="showControl"
          label="#"
          fixed
          width="50">
          <template #default="scope">
            <div class="scope-index">
              <span>{{(paging ? pageSize * (currentPage - 1) : 0) + scope.$index + 1}}</span>
            </div>
            <div class="scope-action">
              <el-popconfirm v-if="!printRead && isDelete" :teleported="true" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + scope.$index)">
                <template #reference>
                  <el-button :disabled="disableddata" type="danger" size="small" circle><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></el-button>
                </template>
              </el-popconfirm>
              <span v-if="printRead || !isDelete">{{(paging ? pageSize * (currentPage - 1) : 0) + scope.$index + 1}}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="columns.length==0"></el-table-column>
        <template v-else>
          <template v-for="column in columns" :key="column.model">
            <el-table-column
              v-if="columnDisplay(column.model)"
              :label="column.options.hideLabel ? '' : column.name"
              :width="column.options.width"
              :label-class-name="column.options.required ? 'required' : ''"
              :class-name="column.options?.customClass"
              :fixed="column.options.fixedColumn ? (column.options.fixedColumnPosition || 'left') : false"
              :column-key="column.key"
            >
              <template #default="scope" >

                <generate-form-item
                  :models="tableData[(paging ? pageSize * (currentPage - 1) : 0) + scope.$index]"
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
                  :row-index="(paging ? pageSize * (currentPage - 1) : 0) + scope.$index"
                  :table-name="name"
                  :is-dialog="isDialog"
                  :dialog-name="dialogName"
                  :is-group="false"
                  :group="group ? `${group}.${name}` : name"
                  :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + scope.$index}`"
                >
                  <template v-slot:[blank.name]="scope" v-for="blank in blanks">
                    <slot :name="blank.name" :model="scope.model"></slot>
                  </template>
                </generate-form-item>

              </template>
              
            </el-table-column>
          </template>
          
        </template>
      </el-table>

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
                <el-popconfirm v-if="!printRead && isDelete" :teleported="true" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + index)">
                  <template #reference>
                    <!-- Y9 -->
                    <el-button class="y9_button_delete" :disabled="disableddata" type="danger" size="small" circle><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></el-button>
                  </template>
                </el-popconfirm>
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
    

    <div class="form-table-mobile"
      v-if="(preview && platform === 'mobile') || (!preview && mobileScreen)"
      :style="{'display': (preview && platform === 'mobile') ? 'block' : ''}"
    >
      <div class="form-table-mobile-item"
        v-for="(t, index) in (paging ? pagingLength : tableData)"
        :key="(paging ? pageSize * (currentPage - 1) : 0) + index"
      >
        <div class="form-table-mobile-item__top" v-if="showControl">
          <span># {{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</span>
          <el-popconfirm v-if="!printRead && isDelete" :teleported="false" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + index)">
            <template #reference>
              <el-button :disabled="disableddata" type="danger" size="small" circle><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></el-button>
            </template>
          </el-popconfirm>
        </div>
        <div class="form-table-mobile-item__content">
          <template v-for="column in columns" 
            :key="column.key"
          >
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
        </div>
      </div>
    </div>

    <el-row>
      <el-col :span="(preview && platform != 'mobile') || !preview ? 12 : 24" :xs="24">
        <el-button class="y9_button_add" link type="primary" @click="handleAddRow" v-if="!disableddata && isAdd">
          <i class="fm-iconfont icon-plus" style="font-size: 12px; margin: 5px;"></i>{{$t('fm.actions.add')}}
        </el-button>
        
      </el-col>
      <el-col :span="((preview && platform != 'mobile') || !preview) && !disableddata ? 12 : 24" :xs="24">
        <el-pagination
          style="float: right;"
          layout="total, prev, pager, next"
          :page-size="pageSize"
          :current-page="currentPage"
          :total="tableData.length"
          :pager-count="5"
          @current-change="handlePageChange"
          v-if="paging && tableData.length"
        >
        </el-pagination>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'
import _ from 'lodash'
import VirtualTable from '../VirtualTable/index.vue'
import VirtualRow from '../VirtualTable/row.vue'
import VirtualCell from '../VirtualTable/cell.vue'
import { updateClassName } from '../../util/reuse-methods'
import { EventBus } from '../../util/event-bus'
export default {
  components: {
    VirtualTable,
    VirtualRow,
    VirtualCell,
    GenerateFormItem: defineAsyncComponent(() => import('../GenerateFormItem.vue')),
    GenerateElementItem: defineAsyncComponent(() => import('../GenerateElementItem.vue'))
  },
  props: ['columns', 'value', 'models', 'remote', 'blanks', 'disableddata', 'rules', 'name', 
    'remoteOption', 'preview', 'platform', 'dataSourceValue', 'eventFunction', 'widget', 
    'printRead', 'paging', 'pageSize', 'isDelete', 'isAdd', 'containerKey',
    'showControl', 'isDialog', 'dialogName', 'group', 'fieldNode','tableId','tableName'],
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

      if (this.widget && this.widget.events && this.widget.events.onRowAdd) {
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
      let msg = {
        guid:this.tableData[index].guid,
        tableId:this.tableId
      }
      EventBus.$emit('table_removeRow', msg);//Y9子表删除行事件，发送事件监听
      this.tableData.splice(index, 1)

      if (this.widget && this.widget.events && this.widget.events.onRowRemove) {
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
    hideChild (rowIndex, fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        this.onFormHide(`${this.fieldNode}.${rowIndex}.${item}`)
      })
    },
    displayChild (rowIndex, fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        this.onFormDisplay(`${this.fieldNode}.${rowIndex}.${item}`)
      })
    },
    hide (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }

      fields.forEach(item => {
        this.onFormHide(`${this.fieldNode}.${item}`)
      })
    },
    display (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        this.onFormDisplay(`${this.fieldNode}.${item}`)
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

      this.pagingData = this.tableData.slice(beginIndex, endIndex)

      this.pagingLength = this.pagingData.length
    },
  },
  beforeUnmount () {
    window.removeEventListener('resize', this.getMobileScreen)
  },
  watch: {
    value (val) {
      this.tableData = val

      let hideFields = []
      let disabledFields = []
      for (let i = 0; i < this.value.length; i++) {
        let row = this.value[i]
        let rowArray = Object.keys(row)
        let hideRow = {}
        let disabledRow = {}
        for (let f = 0; f < rowArray.length; f++) {
          hideRow[rowArray[f]] = this.tableHideFields?.[i]?.[rowArray[f]] ?? false
          disabledRow[rowArray[f]] = this.tableDisabledFields?.[i]?.[rowArray[f]] ?? false
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
.form-table{
  position: relative;

  .el-form-item{
    margin-top: 0px;//Y9
  }

  .el-table th.required>div::before{
    content: '*';
    color: #f56c6c;
    margin-right: 4px;
    background: transparent;
    vertical-align: top;
  }

  .el-table td{
    padding-bottom: 0;
  }

  .el-table__header-wrapper{
    background: var(--el-fill-color-light);

    div,th{
      background: var(--el-fill-color-light);
    }

    th.el-table-fixed-column--left{
      background: var(--el-fill-color-light);
    }

    th.el-table-fixed-column--right{
      background: var(--el-fill-color-light);
    }
  }

  .el-table__fixed-header-wrapper{
    div,th{
      background: var(--el-fill-color-light);
    }
  }

  .el-table__empty-block{
    // display: none;
    min-height: 50px;
  }

  .scope-action{
    display: none;
    margin-bottom: 5px;//Y9
    margin-left: auto;//Y9
    margin-right: auto;//Y9
  }

  .scope-index{
    display: block;
    margin: 2.5px auto;//Y9
  }

  .hover-row{
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

  .form-table-pc{
    display: block;
  }

  .form-table-mobile{
    display: none;

    .form-table-mobile-item{
      border: 1px solid var(--el-border-color-lighter);
      margin-bottom: 10px;

      .form-table-mobile-item__top{
        height: 36px;
        line-height: 36px;
        padding: 0 10px;
        background:  var(--el-fill-color-light);
        border-bottom: 1px solid var(--el-border-color-lighter);
        font-weight: 500;

        button{
          float: right;
          margin-top: 4px;
        }
      }

      .form-table-mobile-item__content{
        padding: 8px;
      }

    }
  }
}

@media screen and (max-width: 768px) {
  .form-table{
    .form-table-pc{
      display: none;
    }

    .form-table-mobile{
      display: block;
    }
  }
}

// Y9++
.y9_button_delete{
  box-shadow: none !important;
  min-width:30px !important;
}
.y9_button_add{
  box-shadow: none !important;
  min-width:2px !important;
}
.el-popover.el-popper{
  min-width:200px !important;
}
</style>
