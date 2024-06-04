<template>
  <div class="event-panel-config">

    <el-collapse :modelValue="eventArray.map(item => item.eventName)" v-if="eventArray.length">
      <el-collapse-item :key="item.eventName" :name="item.eventName" :title="$i18n.locale == 'zh-cn' ? eventEnum[item.eventName] ?? item.eventName : item.eventName" v-for="(item, index) in eventArray">
        <div class="event-panel-item">
          <el-select
            size="default"
            style="width: 100%;margin-bottom: 5px;"
            v-model="item.functionKey">
            <el-option
              v-for="item in eventscripts"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
          
          <i class="fm-iconfont icon-code" @click="handleCode(item)"  :title="$t('fm.eventscript.config.code')"></i>
          <i class="fm-iconfont icon-trash" @click="handleRemove(item, index)"  :title="$t('fm.tooltip.trash')"></i>
        </div>
      </el-collapse-item>
    </el-collapse>

    <el-dropdown style="width: 100%; margin-top: 5px; display:block;" trigger="click" @command="handleCommand" szie="small">
      <el-button type="primary" plain style="width: 100%;" size="default"> 
        {{$t('fm.eventscript.config.create')}}<i class="fm-iconfont icon-plus" style="font-size: 12px; margin: 5px;"></i>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu style="width: 280px;">
          <template v-for="e in Object.keys(events)" :key="e">
            <el-dropdown-item v-if="$i18n.locale == 'zh-cn'" :disabled="eventArray.filter(item => item.eventName == e).length != 0" :command="e">
              {{eventEnum[e] ?? e}}
            </el-dropdown-item>
            <el-dropdown-item v-else :disabled="eventArray.filter(item => item.eventName == e).length != 0" :command="e">
              {{e}}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </template>
      
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: 'event-config',
  props: ['events', 'eventscripts'],
  emits: ['on-add', 'on-remove', 'on-edit', 'update:events'],
  data () {
    return {
      eventsModel: this.events,
      eventArray: Object.keys(this.events).map(item => ({
        eventName: item,
        functionKey: this.events[item]
      })).filter(item => item.functionKey),
      eventEnum: {
        onChange: 'onChange 值发生变化',
        onClick: 'onClick 单击',
        onFocus: 'onFocus 获取焦点',
        onBlur: 'onBlur 失去焦点',
        onRowAdd: 'onRowAdd 子表单添加行',
        onRowRemove: 'onRowRemove 子表单删除行',
        onUploadSuccess: 'onUploadSuccess 上传成功',
        onUploadError: 'onUploadError 上传失败',
        onRemove: 'onRemove 移除',
        onUploadProgress: 'onUploadProgress 上传中',
        onSelect: 'onSelect 文件选择',
        onPageChange: 'onPageChange 当前页改变',
        onCancel: 'onCancel 点击取消按钮',
        onConfirm: 'onConfirm 点击确定按钮'
      }
    }
  },
  methods: {
    handleCommand (command) {
      this.$emit('on-add', command)
    },

    handleRemove (item, index) {
      this.$emit('on-remove', item.eventName)
    },

    handleCode (item) {
      this.$emit('on-edit', item)
    }
  },
  watch: {
    events: {
      deep: true,
      handler (val) {
        this.eventsModel = val
        this.eventArray = Object.keys(this.events).map(item => ({
          eventName: item,
          functionKey: this.events[item]
        })).filter(item => item.functionKey)
      }
    },
    eventsModel: {
      deep: true,
      handler (val) {
        this.$emit('update:events', val)
      }
    },
    eventArray: {
      deep: true,
      handler (val) {
        for(let i = 0; i < val.length; i++) {
          this.eventsModel[val[i].eventName] = val[i].functionKey
        }
      }
    }
  }
}
</script>

<style lang="scss">
.event-panel-config{
  .el-collapse-item{
    border: 1px solid var(--el-border-color-lighter);
    border-bottom-color: var(--el-fill-color-darker);
  }

  .el-collapse-item__header{
    background: var(--el-border-color-lighter);
    height: 30px;
    line-height: 30px;
    padding: 5px;
    font-size: 12px;
  }

  .el-collapse-item__wrap{
    border: 0;

    .el-collapse-item__content{
      padding: 5px;
    }
  }

  .event-panel-item{
    vertical-align: top;
    display: flex;

    > i {
      margin-left: 5px;
      cursor: pointer;
    }
  }
}
</style>