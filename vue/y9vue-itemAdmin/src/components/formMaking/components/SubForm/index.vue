<template>
  <div class="form-subform">
    <div class="form-subform-item" 
      v-for="(t, index) in (paging ? pagingLength : subformData)"
      :key="(paging ? pageSize * (currentPage - 1) : 0) + index"
      @mouseover.stop="handleMouseover((paging ? pageSize * (currentPage - 1) : 0) + index)"
      @mouseout="handleMouseout((paging ? pageSize * (currentPage - 1) : 0) + index)"
      :ref="`formSubformItem_${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
    >
      <div class="form-subform-index" v-if="showControl">
        <el-tag round>#{{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</el-tag>
      </div>
      <div class="form-subform-remove" v-if="showControl">
        <el-popconfirm v-if="!printRead && isDelete" :teleported="false" :title="$t('fm.description.deleteConfirm')" @confirm="handleRemove((paging ? pageSize * (currentPage - 1) : 0) + index)">
          <template #reference>
            <el-button :disabled="disableddata" type="danger" size="small" circle><i class="fm-iconfont icon-trash" style="font-size: 12px; margin: 5px;"></i></el-button>
          </template>
        </el-popconfirm>
        <el-tag round v-if="printRead || !isDelete">#{{(paging ? pageSize * (currentPage - 1) : 0) + index + 1}}</el-tag>
      </div>
      <div class="form-subform-content">
        <template v-for="item in list">
          <generate-col-item
            v-if="item.type == 'grid'"
            :key="item.key"
            :model="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :element="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="false"
            :group="group ? `${group}.${name}` : name"
            :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-col-item>

          <generate-tab-item
            v-else-if="item.type == 'tabs'"
            :key="item.key"
            :model="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :element="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="false"
            :group="group ? `${group}.${name}` : name"
            :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-tab-item>

          <generate-collapse
            v-else-if="item.type == 'collapse'"
            :key="item.key"
            :model="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :element="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="false"
            :group="group ? `${group}.${name}` : name"
            :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-collapse>

          <generate-report
            v-else-if="item.type == 'report'"
            :key="item.key"
            :model="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :element="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="false"
            :group="group ? `${group}.${name}` : name"
            :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-report>

          <generate-card
            v-else-if="item.type == 'card'"
            :key="item.key"
            :model="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :element="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="false"
            :group="group ? `${group}.${name}` : name"
            :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-card>

          <generate-inline
            v-else-if="item.type == 'inline'"
            :key="item.key"
            :model="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :element="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="false"
            :group="group ? `${group}.${name}` : name"
            :field-node="`${fieldNode}.${(paging ? pageSize * (currentPage - 1) : 0) + index}`"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-inline>

          <generate-form-item
            v-else
            :key="item.key"
            :models="subformData[(paging ? pageSize * (currentPage - 1) : 0) + index]"
            :rules="rules"
            :widget="item"
            :remote="remote"
            :blanks="blanks"
            :display="displayFields"
            :sub-hide-fields="subHideFields"
            :sub-disabled-fields="subDisabledFields"
            :edit="!disableddata"
            :remote-option="remoteOption"
            :platform="platform"
            :preview="preview"
            :container-key="containerKey"
            :data-source-value="dataSourceValue"
            :event-function="eventFunction"
            :print-read="printRead"
            :is-subform="true"
            :row-index="(paging ? pageSize * (currentPage - 1) : 0) + index"
            :sub-name="name"
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
    <el-row class="form-subform-action">
      <el-col :span="(preview && platform != 'mobile') || !preview ? 12 : 24" :xs="24">
        <el-button link type="primary" @click="handleAddRow" v-if="!disableddata && isAdd">
          <i class="fm-iconfont icon-plus" style="font-size: 12px; margin: 5px;"></i>{{$t('fm.actions.add')}}
        </el-button>
        
      </el-col>
      <el-col :span="((preview && platform != 'mobile') || !preview) && !disableddata ? 12 : 24" :xs="24">
        <el-pagination
          style="float: right;"
          layout="total, prev, pager, next"
          :page-size="pageSize"
          :current-page="currentPage"
          :total="subformData.length"
          :pager-count="5"
          @current-change="handlePageChange"
          v-if="paging && subformData.length"
        >
        </el-pagination>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'
import _ from 'lodash'
import { addClass, removeClass } from '../../util'
import { updateClassName } from '../../util/reuse-methods'

export default {
  name: 'fm-sub-form',
  components: {
    GenerateColItem: defineAsyncComponent(() => import('../GenerateColItem.vue')),
    GenerateFormItem: defineAsyncComponent(() => import('../GenerateFormItem.vue')),
    GenerateInline: defineAsyncComponent(() => import('../GenerateInline.vue')),
    GenerateReport: defineAsyncComponent(() => import('../GenerateReport.vue')),
    GenerateTabItem: defineAsyncComponent(() => import('../GenerateTabItem.vue')),
    GenerateCollapse: defineAsyncComponent(() => import('../GenerateCollapse.vue')),
    GenerateCard: defineAsyncComponent(() => import('../GenerateCard.vue'))
  },
  props: ['showControl', 'list', 'value', 'models', 'remote', 'blanks', 'disableddata', 
    'rules', 'name', 'remoteOption', 'preview', 'platform', 'dataSourceValue', 'eventFunction', 
    'widget', 'printRead', 'paging', 'pageSize', 'containerKey', 
    'isAdd', 'isDelete', 'isDialog', 'dialogName', 'group', 'fieldNode'],
  emits: ['update:value'],
  data () {
    return {
      subformData: this.value ?? [],
      displayFields: {},
      changeItem: {},
      pagingData: [],
      pagingLength: 0,
      currentPage: 1,

      itemModel: {},
      subHideFields: [],
      subDisabledFields: [],
      itemDisabled: {}
    }
  },
  created () {
    this._generateModel(this.list)

    this.loadPagingData()

    this.subHideFields = this.subformData.map(v => Object.fromEntries(Object.keys(this.displayFields).map(field => [field, !this.displayFields[field]])))

    this.subDisabledFields = this.subformData.map(v => Object.fromEntries(Object.keys(this.itemDisabled).map(field => [field, !this.itemDisabled[field]])))
  },
  provide () {
    return {
      'setSubformData': this.setSubformData
    }
  },
  inject: ['onFormDisabled', 'onFormHide', 'onFormDisplay'],
  methods: {
    handleMouseover (key) {
      addClass(this.$refs[`formSubformItem_${key}`][0], 'is-hover')
    },
    handleMouseout (key) {
      removeClass(this.$refs[`formSubformItem_${key}`][0], 'is-hover')
    },
    setSubformData (value, rowIndex, field) {
      this.subformData[rowIndex][field] = value
    },
    setData (rowIndex, value) {
      if (typeof rowIndex !== 'number') {
        return new Promise((resolve) => {
          value = rowIndex

          this.subformData.forEach((_, index) => {
            Object.keys(value).forEach(item => {
              this.setSubformData(value[item], index, item)
            })
          })

          resolve()
        })
      }
      return new Promise((resolve) => {
        this.$nextTick(() => {
          Object.keys(value).forEach(item => {
            this.setSubformData(value[item], rowIndex, item)
          })
          resolve()
        })
      })
    },
    handleAddRow () {
      this.subformData.push(_.cloneDeep(this.itemModel))

      this.subHideFields.push(
        Object.fromEntries(Object.keys(this.displayFields).map(field => [field, !this.displayFields[field]]))
      )

      this.subDisabledFields.push(
        Object.fromEntries(Object.keys(this.itemDisabled).map(field => [field, !this.itemDisabled[field]]))
      )

      if (this.widget && this.widget.events && this.widget.events.onRowAdd) {
        let funcKey = this.widget.events.onRowAdd

        this.eventFunction[funcKey]({
          rowIndex: this.subformData.length - 1, 
          field: this.widget.model,
          currentRef: this,
          group: this.group,
          fieldNode: this.fieldNode
        })
      }

      if (this.paging) {
        this.$nextTick(() => {
          if (this.subformData.length > this.currentPage * this.pageSize) {
            this.currentPage = parseInt((this.subformData.length - 1) / this.pageSize) + 1
          }

          this.loadPagingData()
        })
      }
    },

    handleRemove (index) {
      const removeData = {...this.subformData[index]}

      this.subformData.splice(index, 1)

      this.subHideFields.splice(index, 1)

      this.subDisabledFields.splice(index, 1)

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

      this.pagingData = []
      this.pagingLength = 0

      if (this.paging) {
        this.$nextTick(() => {
          if (this.subformData.length % this.pageSize == 0 && this.currentPage > parseInt(this.subformData.length / this.pageSize)) {
            this.currentPage = parseInt(this.subformData.length / this.pageSize)
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
    disabled (fields, disabled) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      this.subformData.forEach((_, index) => {
        fields.forEach(field => {
          this.subDisabledFields[index][field] = disabled
        })
      })
      this._setDisabled(this.list, fields, disabled)
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
    disabledChild (rowIndex, fields, disabled) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.subDisabledFields[rowIndex][field] = disabled

        this.onFormDisabled(`${this.fieldNode}.${rowIndex}.${field}`, disabled)
      })
    },
    setOptions (fields, options) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      this._setOptions(this.list, fields, options)
    },

    addClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.list, item.split('.'), className, 'add')
      })
    },
    removeClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.list, item.split('.'), className, 'remove')
      })
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

      this.pagingData = this.subformData.slice(beginIndex, endIndex)

      this.pagingLength = this.pagingData.length
    },

    _generateModel (genList) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type == 'grid') {
          this.displayFields[genList[i].model] = !genList[i].options.hidden

          genList[i].columns.forEach(item => {
            this._generateModel(item.list)
          })
        } else if (genList[i].type === 'tabs') {
          genList[i].tabs.forEach(item => {
            this._generateModel(item.list)
          })

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'collapse') {
          genList[i].tabs.forEach(item => {
            this._generateModel(item.list)
          })

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'report') {
          genList[i].rows.forEach(row => {
            row.columns.forEach(column => {
              this._generateModel(column.list)
            })
          })

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'inline') {
          this._generateModel(genList[i].list)

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'card') {
          this._generateModel(genList[i].list)

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else {
          if (genList[i].type == 'blank') {
            this.itemModel[genList[i].model] = genList[i].options.defaultType === 'String' ? '' : (genList[i].options.defaultType === 'Object' ? {} : [])
          } else {
            this.itemModel[genList[i].model] = _.cloneDeep(genList[i].options.defaultValue)
          }

          this.displayFields[genList[i].model] = !genList[i].options.hidden
          this.itemDisabled[genList[i].model] = !genList[i].options.disabled
        }
      }
    },
    _setDisabled (genList, fields, disabled) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._setDisabled(item.list, fields, disabled)
          })
        } else if (genList[i].type === 'tabs') {
          genList[i].tabs.forEach(item => {
            this._setDisabled(item.list, fields, disabled)
          })
        } else if (genList[i].type === 'collapse') {
          genList[i].tabs.forEach(item => {
            this._setDisabled(item.list, fields, disabled)
          })
        } else if (genList[i].type === 'report') {
          genList[i].rows.forEach(row => {
            row.columns.forEach(column => {
              this._setDisabled(column.list, fields, disabled)
            })
          })
        } else if (genList[i].type === 'inline') {
          this._setDisabled(genList[i].list, fields, disabled)
        } else if (genList[i].type === 'card') {
          this._setDisabled(genList[i].list, fields, disabled)
        } else {
          if (fields.indexOf(genList[i].model) >= 0) {

            genList[i].options.disabled = disabled

            this.itemDisabled[genList[i].model] = !disabled
          }
        }
      }
    },
    _setOptions (genList, fields, opts) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._setOptions(item.list, fields, opts)
          })
        } else if (genList[i].type === 'tabs') {
          genList[i].tabs.forEach(item => {
            this._setOptions(item.list, fields, opts)
          })
        } else if (genList[i].type === 'collapse') {
          genList[i].tabs.forEach(item => {
            this._setOptions(item.list, fields, opts)
          })
        } else if (genList[i].type === 'report') {
          genList[i].rows.forEach(row => {
            row.columns.forEach(column => {
              this._setOptions(column.list, fields, opts)
            })
          })
        } else if (genList[i].type === 'inline') {
          this._setOptions(genList[i].list, fields, opts)
        } else if (genList[i].type === 'card') {
          this._setOptions(genList[i].list, fields, opts)
        }
        if (fields.indexOf(genList[i].model) >= 0) {

          Object.keys(opts).forEach(key => {
            genList[i].options[key] = opts[key]
          })
        }
      }
    },
  },
  watch: {
    value (val) {
      this.subformData = val

      let hideFields = []
      let disabledFields = []
      for (let i = 0; i < this.value.length; i++) {
        let rowArray = Object.keys(this.displayFields)
        let hideRow = {}
        for (let f = 0; f < rowArray.length; f++) {
          hideRow[rowArray[f]] = this.subHideFields?.[i]?.[rowArray[f]] ?? !this.displayFields[rowArray[f]]
        }

        hideFields.push(hideRow)

        let disabledArray = Object.keys(this.itemDisabled)
        let disabledRow = {}
        for (let d = 0; d < disabledArray.length; d++) {
          disabledRow[disabledArray[d]] = this.subDisabledFields?.[i]?.[disabledArray[d]] ?? !this.itemDisabled[disabledArray[d]]
        }
        disabledFields.push(disabledRow)
      }

      this.subHideFields = hideFields
      this.subDisabledFields = disabledFields
    },
    subformData: {
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
.form-subform{
  .form-subform-item{
    display: flex;
    padding: 6px;

    &.is-hover{
      background-color: var(--el-fill-color-light);

      >.form-subform-index{
        display: none;
      }
      >.form-subform-remove{
        display: block;
      }
    }

    .form-subform-index{
      width: 50px;
      text-align: center;
    }

    .form-subform-content{
      flex: 1;
    }

    .form-subform-remove{
      width: 50px;
      text-align: center;
      display: none;
    }
  }
}
</style>
