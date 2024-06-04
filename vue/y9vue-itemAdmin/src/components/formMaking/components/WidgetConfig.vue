<template>
<el-scrollbar>
  <div>
    <div v-if="show" :key="data.key">
      <el-form label-position="top" size="default" :model="data">
        <el-form-item :label="$t('fm.config.widget.widgetType')">
          <el-tag effect="plain">{{$t(`fm.components.fields.${data.type}`)}}</el-tag>
        </el-form-item>
        <template v-if="isBind && bindFieldType.indexOf(data.type) > -1"><!-- 已绑定字段 fm.config.widget.model1-->
          <el-form-item v-if="data.type != 'td' && data.type != 'th' && data.type != 'col'" :label="$t('fm.config.widget.model1')">
            <el-input v-model="data.model" :readonly="true" @focus="selectTableAndField"></el-input>
          </el-form-item>
        </template>
        <template v-else><!-- 未绑定字段 fm.config.widget.model-->
          <el-form-item v-if="data.type != 'td' && data.type != 'th' && data.type != 'col'" :label="$t('fm.config.widget.model')">
            <!-- 数据库字段绑定 -->
            <el-input v-if="bindFieldType.indexOf(data.type) > -1 && (data.options.bindDatabase || data.options.bindDatabase == undefined)" v-model="data.model" :readonly="true" @focus="selectTableAndField"></el-input>

            <el-input v-else-if="bindFieldType.indexOf(data.type) > -1 && !data.options.bindDatabase" v-model="data.model"></el-input>

            <!-- 子表绑定 -->
            <el-input v-else-if="data.type == 'table'" v-model="data.model" :readonly="true" @focus="selectChildTable"></el-input>

            <!-- 意见框绑定 -->
            <el-input v-else-if="data.el == 'custom-opinion'" v-model="data.model" :readonly="true" @focus="selectOpinionFrame"></el-input>

            <!-- 编号按钮绑定 -->
            <el-input v-else-if="data.el == 'custom-numberButton'" v-model="data.model" :readonly="true" @focus="selectNumber"></el-input>

            <!-- 附件列表 -->
            <el-input v-else-if="data.el == 'custom-file'" v-model="data.model" clearable></el-input>

            <!-- 正文组件 -->
            <el-input v-else-if="data.el == 'custom-word'" v-model="data.model" clearable></el-input>

            <!-- 人员树 -->
            <el-input v-else-if="data.el == 'custom-perosonTree'" v-model="data.model" clearable></el-input>

            <!-- 图片显示 -->
            <el-input v-else-if="data.el == 'custom-picture'" v-model="data.model" clearable></el-input>

            <el-input v-else v-model="data.model" :disabled="data.type =='grid' || data.type == 'tabs' || data.type == 'report' || data.type == 'divider' || data.type == 'inline'" clearable></el-input>
          </el-form-item>
        </template>
        <template v-if="data.type == 'date' || data.type == 'select' || data.type == 'input'">
          <el-form-item label="绑定数据库字段">
            <el-switch v-model="data.options.bindDatabase"></el-switch>
          </el-form-item>
        </template>
        <template v-if="data.el == 'custom-personTree'"><!-- 人员树设置数据库关联字段 -->
          <el-form-item label="数据库字段">
            <el-input v-model="data.options.tableField" clearable></el-input>
          </el-form-item>
        </template>
        <template v-if="data.el == 'custom-picture'"><!-- 图片显示设置数据库关联字段 -->
          <el-form-item label="数据库字段">
            <el-input v-model="data.options.tableField" clearable></el-input>
          </el-form-item>
        </template>
        <template v-if="data.el == 'custom-numberButton'"><!-- 编号按钮设置数据库关联字段 -->
          <el-form-item label="数据库字段">
            <el-input v-model="data.options.tableField" clearable></el-input>
          </el-form-item>
        </template>
        <template v-if="data.el == 'custom-opinion'"><!-- 意见框设置最小高度 -->
          <el-form-item :label="$t('fm.config.widget.minHeight')">
            <el-input v-model="data.options.minHeight" clearable></el-input>
          </el-form-item>
        </template>
        <el-form-item :label="$t('fm.config.widget.name')" v-if="data.type!='grid' && data.type != 'tabs' && data.type != 'collapse' && data.type != 'report' && data.type != 'inline' && data.type != 'td' && data.type != 'th' && data.type != 'col' && data.type != 'alert' && data.type != 'dialog' && data.type != 'card'">
          <el-input clearable v-model="data.name"></el-input>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.buttonName')" v-if="Object.keys(data.options).indexOf('buttonName')>=0">
          <el-input clearable v-model="data.options.buttonName"></el-input>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.linkName')" v-if="Object.keys(data.options).indexOf('linkName')>=0">
          <el-input clearable v-model="data.options.linkName"></el-input>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.otitle')" v-if="Object.keys(data.options).indexOf('title') >= 0">
          <el-input clearable v-model="data.options.title" type="textarea" autosize></el-input>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.description')" v-if="Object.keys(data.options).indexOf('description') >= 0">
          <el-input clearable v-model="data.options.description" type="textarea" autosize></el-input>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.width')" v-if="Object.keys(data.options).indexOf('width')>=0 && data.type != 'td'">
          <el-input clearable v-model="data.options.width" ></el-input>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.height')" v-if="Object.keys(data.options).indexOf('height')>=0">
          <el-input clearable v-model="data.options.height"></el-input>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.size')" v-if="data.type == 'imgupload' && Object.keys(data.options).indexOf('size')>=0">
          {{$t('fm.config.widget.width')}} <el-input clearable style="width: 90px;" type="number" v-model.number="data.options.size.width"></el-input>
          {{$t('fm.config.widget.height')}} <el-input clearable style="width: 90px;" type="number" v-model.number="data.options.size.height"></el-input>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.labelWidth')" v-if="Object.keys(data.options).indexOf('labelWidth')>=0 ">
          <el-checkbox v-model="data.options.isLabelWidth" style="margin-right: 5px;">{{$t('fm.config.widget.custom')}}</el-checkbox>
          <el-input-number v-model="data.options.labelWidth" :disabled="!data.options.isLabelWidth" :min="0" :max="99999" :step="10"></el-input-number>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.labelWrap')" v-if="data.type !='grid' && data.type != 'tabs' && data.type != 'collapse' && data.type != 'report' && data.type != 'inline' && data.type != 'divider' && data.type != 'td' && data.type != 'th' && data.type != 'col' && data.type != 'alert' && data.type != 'dialog' && data.type != 'card'">
          <el-switch v-model="data.options.labelWrap"></el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.hideLabel')" v-if="data.type !='grid' && data.type != 'tabs' && data.type != 'collapse' && data.type != 'report' && data.type != 'inline' && data.type != 'divider' && data.type != 'td' && data.type != 'th' && data.type != 'col' && data.type != 'alert' && data.type != 'dialog' && data.type != 'card'">
          <el-switch v-model="data.options.hideLabel"></el-switch>
        </el-form-item>

        <!-- 开启查询 -->
        <el-form-item v-if="data.type =='textarea' || data.type =='select' || data.type == 'input' || data.type == 'date' || data.type == 'checkbox' || data.type == 'radio'" :label="$t('fm.config.widget.querySign')" title="">
          <el-switch v-model="data.options.querySign" @change="setQuerySign()"></el-switch>
        </el-form-item>
        
        <el-form-item :label="$t('fm.config.widget.placeholder')" v-if="Object.keys(data.options).indexOf('placeholder')>=0 && (data.type!='time' && data.type!='date')">
          <el-input v-model="data.options.placeholder" clearable></el-input>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.tip')" v-if="Object.keys(data.options).indexOf('tip')>=0">
          <el-input type="textarea" autosize clearable v-model="data.options.tip"></el-input>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.layout')" v-if="Object.keys(data.options).indexOf('inline')>=0">
          <el-radio-group v-model="data.options.inline">
            <el-radio-button :label="false" :value="false">{{$t('fm.config.widget.block')}}</el-radio-button>
            <el-radio-button :label="true" :value="true">{{$t('fm.config.widget.inline')}}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.effect')" v-if="Object.keys(data.options).indexOf('effect')>=0">
          <el-radio-group v-model="data.options.effect">
            <el-radio-button label="light" value="light">light</el-radio-button>
            <el-radio-button label="dark" value="dark">dark</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.contentPosition')" v-if="Object.keys(data.options).indexOf('contentPosition') >= 0">
          <el-radio-group v-model="data.options.contentPosition">
            <el-radio-button label="left" value="left">{{$t('fm.config.widget.left')}}</el-radio-button>
            <el-radio-button label="center" value="center">{{$t('fm.config.widget.center')}}</el-radio-button>
            <el-radio-button label="right" value="right">{{$t('fm.config.widget.right')}}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.showInput')" v-if="Object.keys(data.options).indexOf('showInput')>=0">
          <el-switch v-model="data.options.showInput" ></el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.min')" v-if="Object.keys(data.options).indexOf('min')>=0">
          <el-input-number v-model="data.options.min"  :step="1"></el-input-number>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.max')" v-if="Object.keys(data.options).indexOf('max')>=0">
          <el-input-number v-model="data.options.max"  :step="1"></el-input-number>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.step')" v-if="Object.keys(data.options).indexOf('step')>=0">
          <el-input-number v-model="data.options.step" :min="-99999" :max="99999" :step="1"></el-input-number>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.maxlength')" v-if="Object.keys(data.options).indexOf('maxlength')>=0">
          <el-input v-model="data.options.maxlength" type="number"></el-input>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.rows')" v-if="Object.keys(data.options).indexOf('rows')>=0">
          <el-input-number v-model="data.options.rows"></el-input-number>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.autosize')" v-if="Object.keys(data.options).indexOf('autosize')>=0">
          <el-switch v-model="data.options.autosize"></el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.precision')" v-if="Object.keys(data.options).indexOf('precision')>=0">
          <el-input-number v-model="data.options.precision" :min="0" :max="99999" :step="1"></el-input-number>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.controls')" v-if="Object.keys(data.options).indexOf('controls')>=0">
          <el-switch v-model="data.options.controls" ></el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.controlsPosition')" v-if="Object.keys(data.options).indexOf('controlsPosition') >= 0 && data.options.controls">
          <el-radio-group v-model="data.options.controlsPosition">
            <el-radio-button label="" value="">{{$t('fm.config.widget.default')}}</el-radio-button>
            <el-radio-button label="right" value="right">{{$t('fm.config.widget.right')}}</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.multiple')" v-if="data.type=='select' || data.type=='imgupload' || data.type == 'fileupload' || data.type == 'cascader' || data.type == 'treeselect'">
          <el-switch v-model="data.options.multiple" @change="handleSelectMuliple"></el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.filterable')" v-if="data.type=='select' || data.type == 'cascader' || data.type=='transfer' || data.type == 'treeselect'">
          <el-switch v-model="data.options.filterable"></el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.checkStrictly')" v-if="Object.keys(data.options).indexOf('checkStrictly')>=0">
          <el-switch v-model="data.options.checkStrictly"></el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.allowHalf')" v-if="Object.keys(data.options).indexOf('allowHalf')>=0">
          <el-switch
              v-model="data.options.allowHalf"
            >
            </el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.showAlpha')" v-if="Object.keys(data.options).indexOf('showAlpha')>=0">
          <el-switch
              v-model="data.options.showAlpha"
            >
            </el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.option')" v-if="Object.keys(data.options).indexOf('options')>=0">
          <el-radio-group v-model="data.options.remote" style="margin-bottom:10px;">
            <el-radio-button :label="false" :value="false">{{$t('fm.config.widget.staticData')}}</el-radio-button>
            <el-radio-button :label="true" :value="true">{{$t('fm.config.widget.remoteData')}}</el-radio-button>
          </el-radio-group>
          <template v-if="data.options.remote">
            <div>
              <el-radio-group v-model="data.options.remoteType" style="margin-bottom: 8px;">
                <el-radio label="datasource" value="datasource">{{$t('fm.datasource.name')}}</el-radio>
                <el-radio label="option" value="option">{{$t('fm.config.widget.remoteAssigned')}}</el-radio>
                <el-radio label="func" value="func">{{$t('fm.config.widget.remoteFunc')}}</el-radio>
              </el-radio-group>
              <el-input clearable v-if="data.options.remoteType == 'option'" v-model="data.options.remoteOption" style="margin-bottom: 5px;">
              </el-input>
              <el-input clearable  v-if="data.options.remoteType == 'func'" v-model="data.options.remoteFunc" style="margin-bottom: 5px;">
              </el-input>
              <el-select
                v-if="data.options.remoteType == 'datasource'"
                style="width: 100%;margin-bottom: 5px;"
                @change="handleDataSourceChange"
                v-model="data.options.remoteDataSource">
                <el-option
                  v-for="item in datasources"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
              <code-editor :key="data.options.remoteDataSource" v-if="data.options.remoteType == 'datasource' && data.options.remoteArgs" :mode="'json'" v-model="data.options.remoteArgs" :height="'120px'" style="width: 100%;margin-bottom: 5px;"></code-editor>
              <!-- Y9绑定数据字典 -->
              <el-input v-if="data.options.remoteType == 'func'" v-model="data.options.optionData" :readonly="true" placeholder="点击绑定" size="mini">
                <template #prepend>
                  <div slot="prepend" style="width: 48px;">数据字典</div>
                </template>
              </el-input>
              <el-input clearable  v-model="data.options.props.value">
                <template #prepend>
                  <div style="width: 48px;">{{$t('fm.config.widget.value')}}</div>
                </template>
              </el-input>
              <el-input clearable  v-model="data.options.props.label">
                <template #prepend>
                  <div style="width: 48px;">{{$t('fm.config.widget.label')}}</div>
                </template>
                
              </el-input>
              <el-input clearable v-model="data.options.props.children">
                <template #prepend>
                  <div style="width: 48px;">{{$t('fm.config.widget.childrenOption')}}</div>
                </template>
              </el-input>
            </div>
          </template>
          <template v-else>
            <div v-if="Object.keys(data.options).indexOf('showLabel')>=0">
              <el-checkbox  v-model="data.options.showLabel">{{$t('fm.config.widget.showLabel')}}</el-checkbox>
            </div>
            <template v-if="data.type=='radio' || (data.type=='select'&&!data.options.multiple)">
              <el-radio-group v-model="data.options.defaultValue" >
                <draggable tag="ul" v-model="data.options.options" 
                  v-bind="{group:{ name:'options'}, ghostClass: 'ghost',handle: '.drag-item'}"
                  handle=".drag-item"
                  item-key="index"
                >
                  <template #item="{element:item, index}">
                    <li :key="index" >
                      <el-radio
                        :label="item.value" 
                        :value="item.value" 
                        style="margin-right: 5px;"
                      >
                        <el-input clearable :style="{'width': data.options.showLabel? '90px': '180px' }" v-model="item.value"></el-input>
                        <el-input clearable style="width:90px;"  v-if="data.options.showLabel" v-model="item.label"></el-input>
                        
                      </el-radio>
                      <i class="drag-item" style="font-size: 16px;margin: 0 5px;cursor: move;"><i class="fm-iconfont icon-icon_bars"></i></i>

                      <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 0 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                      
                    </li>
                  </template>
                  
                </draggable>
                
              </el-radio-group>
            </template>

            <template v-if="data.type=='checkbox' || (data.type=='select' && data.options.multiple)">
              <el-checkbox-group v-model="data.options.defaultValue">

                <draggable tag="ul" :list="data.options.options" 
                  v-bind="{group:{ name:'options'}, ghostClass: 'ghost',handle: '.drag-item'}"
                  handle=".drag-item"
                  item-key="index"
                >
                  <template #item="{element: item, index}">
                    <li :key="index" >
                      <el-checkbox
                        :label="item.value"
                        style="margin-right: 5px; margin-bottom: 3px;"
                      >
                        <el-input clearable :style="{'width': data.options.showLabel? '90px': '180px' }" v-model="item.value"></el-input>
                        <el-input clearable style="width:90px;" v-if="data.options.showLabel" v-model="item.label"></el-input>
                      </el-checkbox>
                      <i class="drag-item" style="font-size: 16px;margin: 0 5px;cursor: move;"><i class="fm-iconfont icon-icon_bars"></i></i>
                      <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 0 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                      
                    </li>
                  </template>
                  
                </draggable>
              </el-checkbox-group>
            </template>
            <div style="margin-left: 22px;" v-if="data.type != 'cascader' && data.type != 'treeselect'">
              <el-button link type="primary" @click="handleAddOption" >{{$t('fm.actions.addOption')}}</el-button>
              <el-button link type="primary" @click="handleClearSelect" >{{$t('fm.actions.clearSelect')}}</el-button>
            </div>
            <template v-if="data.type == 'cascader'">
              <el-button style="width: 100%;" @click="handleSetCascader">{{$t('fm.config.widget.setting')}}</el-button>
            </template>
            <template v-if="data.type == 'treeselect'">
              <el-button style="width: 100%;" @click="handleSetTree">{{$t('fm.config.widget.setting')}}</el-button>
            </template>
          </template>
        </el-form-item>

        <template v-if="Object.keys(data.options).indexOf('steps')>=0">

          <el-form-item :label="$t('fm.config.widget.steps')">
            <el-radio-group v-model="data.options.remote" style="margin-bottom: 10px;">
              <el-radio-button :label="false" :value="false">{{$t('fm.config.widget.staticData')}}</el-radio-button>
              <el-radio-button :label="true" :value="true">{{$t('fm.datasource.name')}}</el-radio-button>
            </el-radio-group>
            <template v-if="data.options.remote">
              <el-select
                @change="handleDataSourceChange"
                style="width: 100%;margin-bottom: 5px;"
                v-model="data.options.remoteDataSource">
                <el-option
                  v-for="item in datasources"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
              <code-editor :key="data.options.remoteDataSource" v-if="data.options.remote && data.options.remoteArgs" :mode="'json'" v-model="data.options.remoteArgs" :height="'120px'" style="width: 100%;margin-bottom: 5px;"></code-editor>
              <el-input clearable  v-model="data.options.props.title">
                <template #prepend>
                  <div style="width: 30px;">Title</div>
                </template>
                
              </el-input>
              <el-input clearable v-model="data.options.props.description">
                
                <template #prepend>
                  <div style="width: 60px;">Description</div>
                </template>
              </el-input>
            </template>
            <template v-else>
              <el-radio-group v-model="data.options.defaultValue">
                <draggable tag="ul" :list="data.options.steps" 
                  v-bind="{group:{ name:'options'}, ghostClass: 'ghost',handle: '.drag-item'}"
                  handle=".drag-item"
                  item-key="index"
                >
                  <template #item="{element: item, index}">
                    <li :key="index" class="drag-item-bk" style="padding: 5px; margin-bottom: 5px; display: flex; align-items: center;">
                      <el-radio
                        :label="index" 
                        :value="index" 
                        style="margin-right: 5px; height: 100%;"
                      >
                        <div style="display: inline-block; vertical-align: middle;">
                          <el-input placeholder="Title" clearable  v-model="item.title"></el-input> <br>
                          <el-input placeholder="Description" clearable  v-model="item.description"></el-input>
                        </div>
                      </el-radio>
                      <i class="drag-item" style="font-size: 16px;margin: 0 5px;cursor: move;"><i class="fm-iconfont icon-icon_bars"></i></i>
                      
                      <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 0 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                      
                    </li>
                  </template>
                  
                </draggable>
                
              </el-radio-group>

              <div style="margin-left: 5px;">
                <el-button link type="primary" @click="handleAddStep">{{$t('fm.actions.addOption')}}</el-button>
              </div>
            </template>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.space')">
            <el-input-number v-model="data.options.space" :min="0" :step="10"></el-input-number>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.direction')">
            <el-radio-group v-model="data.options.direction">
              <el-radio-button label="horizontal" value="horizontal">Horizontal</el-radio-button>
              <el-radio-button label="vertical" value="vertical">Vertical</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.processStatus')">
            <el-select v-model="data.options.processStatus">
              <el-option value="wait" label="wait"></el-option>
              <el-option value="process" label="process"></el-option>
              <el-option value="finish" label="finish"></el-option>
              <el-option value="error" label="error"></el-option>
              <el-option value="success" label="success"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.finishStatus')">
            <el-select v-model="data.options.finishStatus">
              <el-option value="wait" label="wait"></el-option>
              <el-option value="process" label="process"></el-option>
              <el-option value="finish" label="finish"></el-option>
              <el-option value="error" label="error"></el-option>
              <el-option value="success" label="success"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.alignCenter')">
            <el-switch
              v-model="data.options.alignCenter"
            >
            </el-switch>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.simple')">
            <el-switch
              v-model="data.options.simple"
            >
            </el-switch>
          </el-form-item>

        </template>

        <template v-if="data.type == 'transfer'">

          <el-form-item :label="$t('fm.config.widget.option')">
            <el-radio-group v-model="data.options.remote" style="margin-bottom: 10px;">
              <el-radio-button :label="false" :value="false">{{$t('fm.config.widget.staticData')}}</el-radio-button>
              <el-radio-button :label="true" :value="true">{{$t('fm.datasource.name')}}</el-radio-button>
            </el-radio-group>
            <template v-if="data.options.remote">
              <el-select
                @change="handleDataSourceChange"
                style="width: 100%;margin-bottom: 5px;"
                v-model="data.options.remoteDataSource">
                <el-option
                  v-for="item in datasources"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
              <code-editor :key="data.options.remoteDataSource" v-if="data.options.remote && data.options.remoteArgs" :mode="'json'" v-model="data.options.remoteArgs" :height="'120px'" style="width: 100%;margin-bottom: 5px;"></code-editor>
              <el-input clearable  v-model="data.options.props.key">
                <template #prepend>
                  <div style="width: 30px;">Key</div>
                </template>
                
              </el-input>
              <el-input clearable v-model="data.options.props.label">
                
                <template #prepend>
                  <div style="width: 30px;">Label</div>
                </template>
              </el-input>
              <el-input clearable v-model="data.options.props.disabled">
                
                <template #prepend>
                  <div style="width: 50px;">Disabled</div>
                </template>
              </el-input>
            </template>
            <template v-else>
              <el-checkbox-group v-model="data.options.defaultValue">
                <draggable tag="ul" :list="data.options.data" 
                  v-bind="{group:{ name:'options'}, ghostClass: 'ghost',handle: '.drag-item'}"
                  handle=".drag-item"
                  item-key="index"
                >
                  <template #item="{element: item, index}">
                    <li :key="item.key" class="drag-item-bk" style="padding: 5px; margin-bottom: 5px; display: flex; align-items: center;">
                      <el-checkbox
                        :label="item.key" 
                        style="margin-right: 5px; height: 100%;"
                      >
                        <div style="display: inline-block; vertical-align: middle;">
                          <el-input placeholder="Key" clearable  v-model="item.key" style="margin-bottom: 3px;"></el-input> <br>
                          <el-input placeholder="Label" clearable  v-model="item.label"></el-input>
                        </div>
                      </el-checkbox>
                      <i class="drag-item" style="font-size: 16px;margin: 0 5px;cursor: move;"><i class="fm-iconfont icon-icon_bars"></i></i>
                      
                      <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 0 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                      
                    </li>
                  </template>
                  
                </draggable>
                
              </el-checkbox-group>

              <div style="margin-left: 5px;">
                <el-button link type="primary" @click="handleAddData">{{$t('fm.actions.addOption')}}</el-button>
                <el-button link type="primary" @click="handleClearSelect">{{$t('fm.actions.clearSelect')}}</el-button>
              </div>
            </template>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.titles')">
            <el-input placeholder="List 1" v-model="data.options.titles[0]" style="width: 130px;"></el-input> - 
            <el-input placeholder="List 2" v-model="data.options.titles[1]" style="width: 130px;"></el-input>
          </el-form-item>

        </template>

        <template v-if="data.type == 'alert'">
          <el-form-item :label="$t('fm.config.widget.type')">
            <el-select v-model="data.options.type">
              <el-option value="success"></el-option>
              <el-option value="warning"></el-option>
              <el-option value="info"></el-option>
              <el-option value="error"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.closable')">
            <el-switch v-model="data.options.closable"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.center')">
            <el-switch v-model="data.options.center"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.showIcon')">
            <el-switch v-model="data.options.showIcon"></el-switch>
          </el-form-item>
        </template>
        
        <template v-if="data.type == 'pagination'">
          <el-form-item :label="$t('fm.config.widget.pageSize')" v-if="Object.keys(data.options).indexOf('pageSize')>=0">
            <el-input-number
              v-model="data.options.pageSize"
              :step="5"
              :min="1"
              :max="100"
            ></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.pagerCount')" v-if="Object.keys(data.options).indexOf('pagerCount')>=0">
            <el-input-number
              v-model="data.options.pagerCount"
              :step="1"
              :min="5"
              :max="21"
            ></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.total')" v-if="Object.keys(data.options).indexOf('total')>=0">
            <el-input-number
              v-model="data.options.total"
              :step="1"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.background')" v-if="Object.keys(data.options).indexOf('background')>=0">
            <el-switch v-model="data.options.background"></el-switch>
          </el-form-item>
        </template>

        <el-form-item :label="$t('fm.config.widget.buttonSize')" v-if="Object.keys(data.options).indexOf('buttonSize')>=0">
          <el-radio-group v-model="data.options.buttonSize">
            <el-radio-button label="large" value="large">Large</el-radio-button>
            <el-radio-button label="default" value="default">Default</el-radio-button>
            <el-radio-button label="small" value="small">Small</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.buttonType')" v-if="Object.keys(data.options).indexOf('buttonType')>=0">
          <el-select v-model="data.options.buttonType" style="width: 100%;">
            <el-option value="" label="Default"></el-option>
            <el-option value="primary" label="Primary"></el-option>
            <el-option value="success" label="Success"></el-option>
            <el-option value="warning" label="Warning"></el-option>
            <el-option value="danger" label="Danger"></el-option>
            <el-option value="info" label="Info"></el-option>
            <el-option value="text" label="Text"></el-option>
            <el-option value="link" label="Link"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.linkType')" v-if="Object.keys(data.options).indexOf('linkType')>=0">
          <el-select v-model="data.options.linkType" style="width: 100%;">
            <el-option value="default" label="Default"></el-option>
            <el-option value="primary" label="Primary"></el-option>
            <el-option value="success" label="Success"></el-option>
            <el-option value="warning" label="Warning"></el-option>
            <el-option value="danger" label="Danger"></el-option>
            <el-option value="info" label="Info"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.buttonPlain')" v-if="Object.keys(data.options).indexOf('buttonPlain')>=0">
          <el-switch
              v-model="data.options.buttonPlain"
            >
            </el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.buttonRound')" v-if="Object.keys(data.options).indexOf('buttonRound')>=0">
          <el-switch
              v-model="data.options.buttonRound"
            >
            </el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.buttonCircle')" v-if="Object.keys(data.options).indexOf('buttonCircle')>=0">
          <el-switch
              v-model="data.options.buttonCircle"
            >
            </el-switch>
        </el-form-item>
        <el-form-item :label="'href'" v-if="Object.keys(data.options).indexOf('href')>=0">
          <el-input clearable v-model="data.options.href"></el-input>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.underline')" v-if="Object.keys(data.options).indexOf('underline')>=0">
          <el-switch
              v-model="data.options.underline"
            >
            </el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.isBlank')" v-if="Object.keys(data.options).indexOf('blank')>=0">
          <el-switch
              v-model="data.options.blank"
            >
            </el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.paging')" v-if="Object.keys(data.options).indexOf('paging')>=0">
          <el-switch
              v-model="data.options.paging"
            >
            </el-switch>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.pageSize')" v-if="data.options.paging && Object.keys(data.options).indexOf('pageSize')>=0">
          <el-input-number
            v-model="data.options.pageSize"
            :step="5"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item :label="$t('fm.config.widget.showControl')" v-if="Object.keys(data.options).indexOf('showControl')>=0">
          <el-switch
              v-model="data.options.showControl"
            >
            </el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.virtualTable')" v-if="Object.keys(data.options).indexOf('virtualTable')>=0">
          <el-switch
            v-model="data.options.virtualTable"
          >
          </el-switch>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.fixedColumn')" v-if="data.options.tableColumn">
          <el-switch
            v-model="data.options.fixedColumn"
          >
          </el-switch>
          <el-select v-if="data.options.fixedColumn" v-model="data.options.fixedColumnPosition" :placeholder="$t('fm.config.widget.fixedColumnSelect')" style="margin-left: 10px; width: 180px;">
            <el-option :label="$t('fm.config.widget.left')" value="left"></el-option>
            <el-option :label="$t('fm.config.widget.right')" value="right"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('fm.config.widget.defaultValue')" 
          v-if="Object.keys(data.options).indexOf('defaultValue')>=0 && data.type != 'custom' 
            && data.type != 'imgupload' 
            && data.type != 'fileupload' 
            && data.type != 'radio' 
            && data.type != 'checkbox' 
            && data.type != 'select'
            && data.type != 'date'
            && data.type != 'time'
            && data.type != 'steps'
            && data.type != 'transfer'
            && data.type != 'dialog'
            && data.type != 'card'
            && data.type != 'slider'
            && data.type != 'group'"
          >
          <!-- Y9自定义默认值 -->
          <el-checkbox v-model="data.options.customDefaultValue">{{ $t('fm.config.widget.customDefaultValue') }}</el-checkbox>
          <template v-if="data.options.customDefaultValue">
              <el-select v-model="data.options.defaultValue">
                  <el-option value="$_deptName">deptName(部门名称)</el-option>
                  <el-option value="$_deptLeader">deptLeader(岗位所在部门领导)</el-option>
                  <el-option value="$_userName">userName(用户名)</el-option>
                  <el-option value="$_positionName">positionName(岗位名称)</el-option>
                  <el-option value="$_duty">duty(职务)</el-option>
                  <el-option value="$_createDate">createDate(创建日期)</el-option>
                  <el-option value="$_mobile">mobile(手机号码)</el-option>
                  <el-option value="$_number">number(文件编号)</el-option>
                  <el-option value="$_zihao">zihao(字号)</el-option>
              </el-select>
          </template>
          <template v-else>
            <el-input clearable v-if="data.type=='textarea'" type="textarea" 
              :rows="data.options.rows" :maxlength="data.options.maxlength"
              v-model="data.options.defaultValue">
            </el-input>
            
            <template v-if="data.type=='input' || data.type == 'text'">
              <template v-if="data.options.dataType == 'number' || data.options.dataType == 'integer' || data.options.dataType == 'float'">
                <el-input clearable type="number" v-model.number="data.options.defaultValue"></el-input>
              </template>
              <template v-else>
                <el-input clearable v-model="data.options.defaultValue"></el-input>
              </template>
            </template>
            
            <el-rate v-if="data.type == 'rate'" style="display:inline-block;vertical-align: middle;" :max="data.options.max" :allow-half="data.options.allowHalf" v-model="data.options.defaultValue"></el-rate>
            <el-button link type="primary" v-if="data.type == 'rate'" style="display:inline-block;vertical-align: middle;margin-left: 10px;" @click="data.options.defaultValue=0">{{$t('fm.actions.clear')}}</el-button>
            <el-color-picker 
              v-if="data.type == 'color'"
              v-model="data.options.defaultValue"
              :show-alpha="data.options.showAlpha"
            ></el-color-picker>
            <el-switch v-if="data.type=='switch'" v-model="data.options.defaultValue"></el-switch>
            <el-input-number v-if="data.type=='number'"
              v-model="data.options.defaultValue"
              :step="data.options.step"
              :min="data.options.min"
              :max="data.options.max"
            ></el-input-number>

            <el-input-number v-if="data.type == 'pagination'"
              v-model="data.options.defaultValue"
              :step="1"
              :min="1"
            />

            <template v-if="data.type == 'html'">
              <code-editor :key="data.key" v-model="data.options.defaultValue" height="200px"></code-editor>
            </template>

            <template v-if="data.type == 'cascader'">
              <el-cascader
                v-model="data.options.defaultValue"
                clearable
                :options="data.options.remote ? [] : data.options.options"
                style="width: 100%"
                :props="{multiple: data.options.multiple, checkStrictly: data.options.checkStrictly}"
                :filterable="data.options.filterable"
                collapse-tags
              >
              </el-cascader>
            </template>

            <template v-if="data.type == 'treeselect'">
              <el-tree-select
                v-model="data.options.defaultValue"
                clearable
                :data="data.options.remote ? [] : data.options.options"
                style="width: 100%"
                :multiple="data.options.multiple"
                :check-strictly="data.options.checkStrictly"
                :filterable="data.options.filterable"
              >
              </el-tree-select>
            </template>

            <template v-if="data.type == 'editor'">
              <el-button style="width: 100%;" @click="handleSetEditorValue">{{$t('fm.config.widget.setting')}}</el-button>
            </template>

            <template v-if="data.type == 'table' || data.type == 'subform'">
              <el-button style="width: 100%;" @click="handleSetDefaultValue">{{$t('fm.config.widget.setting')}}</el-button>
            </template>
          </template>
        </el-form-item>

        <template v-if="data.type == 'time' || data.type == 'date'">
          <el-form-item :label="$t('fm.config.widget.showType')" v-if="data.type == 'date'">
            <el-select v-model="data.options.type">
              <el-option value="year"></el-option>
              <el-option value="month"></el-option>
              <el-option value="date"></el-option>
              <el-option value="week"></el-option>
              <el-option value="dates"></el-option>
              <el-option value="datetime"></el-option>
              <el-option value="datetimerange"></el-option>
              <el-option value="daterange"></el-option>
              <el-option value="monthrange"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.isRange')" v-if="data.type == 'time'">
            <el-switch
              v-model="data.options.isRange"
            >
            </el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.placeholder')" v-if="(!data.options.isRange && data.type == 'time') || (data.type != 'time' && data.options.type != 'datetimerange' && data.options.type != 'daterange')">
            <el-input clearable v-model="data.options.placeholder"></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.startPlaceholder')" v-if="(data.options.isRange) || data.options.type=='datetimerange' || data.options.type=='daterange'">
            <el-input clearable v-model="data.options.startPlaceholder"></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.endPlaceholder')" v-if="data.options.isRange || data.options.type=='datetimerange' || data.options.type=='daterange'">
            <el-input clearable v-model="data.options.endPlaceholder"></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.format')">
            <el-input clearable v-model.lazy="data.options.format"></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.defaultValue')" v-if="data.type=='time' && Object.keys(data.options).indexOf('isRange')>=0">
            <el-time-picker 
              key="1"
              style="width: 100%;"
              v-if="!data.options.isRange"
              v-model="data.options.defaultValue"
              :arrowControl="data.options.arrowControl"
              :value-format="data.options.format"
            >
            </el-time-picker>
            <el-time-picker 
              key="2"
              v-if="data.options.isRange"
              style="width: 100%;"
              v-model="data.options.defaultValue"
              is-range
              :arrowControl="data.options.arrowControl"
              :value-format="data.options.format"
            >
            </el-time-picker>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.defaultValue')" v-if="data.type=='date'">
            <el-date-picker
              key="1"
              v-model="data.options.defaultValue"
              :type="data.options.type"
              :clearable="true"
              :value-format="data.options.timestamp ? 'timestamp' : data.options.format"
              :format="data.options.format"
              style="width: 100%"
              v-if="data.options.type == 'datetimerange' || data.options.type == 'daterange'"
            >
            </el-date-picker>
            <el-date-picker
              key="2"
              v-model="data.options.defaultValue"
              :type="data.options.type"
              :clearable="true"
              :value-format="data.options.timestamp ? 'timestamp' : data.options.format"
              :format="data.options.format"
              style="width: 100%"
              v-else
            >
            </el-date-picker>
          </el-form-item>
        </template>

        <template v-if="data.type=='imgupload' || data.type=='fileupload'">
          
          <el-form-item :label="$t('fm.config.widget.limit')">
            <el-input clearable type="number" v-model.number="data.options.limit"></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.isQiniu')">
            <el-switch v-model="data.options.isQiniu"></el-switch>
          </el-form-item>
          <template v-if="data.options.isQiniu">
            <el-form-item label="Domain" :required="true" prop="options.domain">
              <el-input clearable v-model="data.options.domain"></el-input>
            </el-form-item>
            <el-form-item :label="$t('fm.config.widget.tokenFunc')" :required="true" prop="options.tokenType">
              
              <el-radio-group v-model="data.options.tokenType">
                <el-radio label="datasource" value="datasource">{{$t('fm.datasource.name')}}</el-radio>
                <el-radio label="func" value="func">{{$t('fm.config.widget.remoteFunc')}}</el-radio>
              </el-radio-group>
              <el-input clearable v-if="data.options.tokenType == 'func'" v-model="data.options.tokenFunc">
              </el-input>
              <el-select
                v-if="data.options.tokenType == 'datasource'"
                style="width: 100%;"
                @change="handleDataSourceChange"
                v-model="data.options.tokenDataSource">
                <el-option
                  v-for="item in datasources"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
              <code-editor :key="data.options.tokenDataSource" v-if="data.options.tokenType == 'datasource' && data.options.remoteArgs" :mode="'json'" v-model="data.options.remoteArgs" :height="'120px'" style="width: 100%;margin-bottom: 5px;"></code-editor>
            </el-form-item>
          </template>
          <template v-else>
            <el-form-item :label="$t('fm.config.widget.action')" :required="true" prop="options.action" trigger="change">
              <el-input clearable v-model="data.options.action"></el-input>
            </el-form-item>
            <el-form-item :label="$t('fm.config.widget.headers')">
              <ul>
                <li v-for="(item, index) in data.options.headers" :key="index" style="display: flex; align-items: center;">
                  <el-input type="textarea" clearable :rows="1" placeholder="KEY" size="small" style="width: 100px;margin-right:5px;" v-model="item.key"></el-input>

                  <el-input type="textarea" clearable :rows="1" placeholder="VALUE" size="small" style="width: 130px;" v-model="item.value"></el-input>
                  
                  <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                  
                </li>
              </ul>
            <div>
              <el-button link type="primary" @click="handleAddHeader">{{$t('fm.actions.add')}}</el-button>
            </div>
            </el-form-item>

            <el-form-item :label="$t('fm.config.widget.withCredentials')">
              <el-switch v-model="data.options.withCredentials"></el-switch>
            </el-form-item>
          </template>
        </template>

        <template v-if="data.type=='blank'">
          <el-form-item :label="$t('fm.config.widget.defaultType')">
            <el-select v-model="data.options.defaultType">
              <el-option value="String" :label="$t('fm.config.widget.string')"></el-option>
              <el-option value="Object" :label="$t('fm.config.widget.object')"></el-option>
              <el-option value="Array" :label="$t('fm.config.widget.array')"></el-option>
            </el-select>
          </el-form-item>
        </template>

        <template v-if="data.type == 'component'">
          <el-form-item :label="$t('fm.config.widget.customTemplates')">
            
            <el-button style="width: 100%;" @click="handleSetTemplate">{{$t('fm.config.widget.setting')}}</el-button>
          </el-form-item>
        </template>

        <template v-if="data.type == 'inline'">
          <el-form-item :label="$t('fm.config.widget.spaceSize')" >
            <el-input-number clearable :min="0" v-model="data.options.spaceSize"></el-input-number>
          </el-form-item>
        </template>

        <template v-if="data.type == 'grid'">
          <el-form-item :label="$t('fm.config.widget.gutter')" key="gutter">
            <el-input clearable type="number" v-model.number="data.options.gutter"></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.flex')" key="flex">
            <el-switch v-model="data.options.flex" disabled></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.justify')" v-if="data.options.flex" key="justify">
            <el-select v-model="data.options.justify">
              <el-option value="start" :label="$t('fm.config.widget.justifyStart')"></el-option>
              <el-option value="end" :label="$t('fm.config.widget.justifyEnd')"></el-option>
              <el-option value="center" :label="$t('fm.config.widget.justifyCenter')"></el-option>
              <el-option value="space-around" :label="$t('fm.config.widget.justifySpaceAround')"></el-option>
              <el-option value="space-between" :label="$t('fm.config.widget.justifySpaceBetween')"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.align')" v-if="data.options.flex" key="align">
            <el-select v-model="data.options.align">
              <el-option value="top" :label="$t('fm.config.widget.alignTop')"></el-option>
              <el-option value="middle" :label="$t('fm.config.widget.alignMiddle')"></el-option>
              <el-option value="bottom" :label="$t('fm.config.widget.alignBottom')"></el-option>
            </el-select>
          </el-form-item>
        </template>

        <template v-if="data.type == 'col'">
          <el-form-item :label="$t('fm.config.widget.span')">
            <el-input-number v-model="data.options.md"  :step="1" :min="1" :max="24" v-if="platform == 'pc'"></el-input-number>
            <el-input-number v-model="data.options.sm"  :step="1" :min="1" :max="24" v-if="platform == 'pad'"></el-input-number>
            <el-input-number v-model="data.options.xs"  :step="1" :min="1" :max="24" v-if="platform == 'mobile'"></el-input-number>
          </el-form-item>

          <el-form-item :label="$t('fm.config.widget.offset')">
            <el-input-number v-model="data.options.offset"  :step="1" :min="0" :max="24"></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.push')">
            <el-input-number v-model="data.options.push"  :step="1" :min="0" :max="24"></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.pull')">
            <el-input-number v-model="data.options.pull"  :step="1" :min="0" :max="24"></el-input-number>
          </el-form-item>
        </template>

        <template v-if="data.type == 'tabs'">
          <el-form-item :label="$t('fm.config.widget.type')">
            <el-radio-group v-model="data.options.type">
              <el-radio-button label="" value="">{{$t('fm.config.widget.default')}}</el-radio-button>
              <el-radio-button label="card" value="card">{{$t('fm.config.widget.card')}}</el-radio-button>
              <el-radio-button label="border-card" value="border-card">{{$t('fm.config.widget.borderCard')}}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.tabPosition')">
            <el-radio-group v-model="data.options.tabPosition">
              <el-radio-button label="top" value="top">{{$t('fm.config.widget.top')}}</el-radio-button>
              <el-radio-button label="left" value="left">{{$t('fm.config.widget.left')}}</el-radio-button>
              <el-radio-button label="right" value="right">{{$t('fm.config.widget.right')}}</el-radio-button>
              <el-radio-button label="bottom" value="bottom">{{$t('fm.config.widget.bottom')}}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.tabOption')">
            <draggable tag="ul" :list="data.tabs" 
              v-bind="{group:{ name:'options'}, ghostClass: 'ghost',handle: '.drag-item'}"
              handle=".drag-item"
              item-key="index"
            >
              <template #item="{element:item, index}">
                <li :key="index" >
                  <i class="drag-item" style="font-size: 16px;margin: 0 5px;cursor: move;"><i class="fm-iconfont icon-icon_bars"></i></i>
                  <el-input  clearable :placeholder="$t('fm.config.widget.tabName')"  style="width: 200px; margin-bottom: 5px;" v-model="item.label"></el-input>
                  
                  <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 0 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                  
                </li>
              </template>
              
            </draggable>
            <div style="margin-left: 22px;">
              <el-button link type="primary" @click="handleAddTab">{{$t('fm.actions.addTab')}}</el-button>
            </div>
          </el-form-item>
        </template>

        <template v-if="data.type == 'collapse'">
          <el-form-item :label="$t('fm.config.widget.collapseOptions')">
            <draggable tag="ul" :list="data.tabs" 
              v-bind="{group:{ name:'options'}, ghostClass: 'ghost',handle: '.drag-item'}"
              handle=".drag-item"
              item-key="index"
            >
              <template #item="{element:item, index}">
                <li :key="index" >
                  <i class="drag-item" style="font-size: 16px;margin: 0 5px;cursor: move;"><i class="fm-iconfont icon-icon_bars"></i></i>
                  <el-input  clearable :placeholder="$t('fm.config.widget.collapseTitle')"  style="width: 200px; margin-bottom: 5px;" v-model="item.title"></el-input>
                  
                  <i @click="handleOptionsRemove(index)" style="font-size: 16px;margin: 0 5px;cursor: pointer;"><i class="fm-iconfont icon-delete"></i></i>
                  
                </li>
              </template>
              
            </draggable>
            <div style="margin-left: 22px;">
              <el-button link type="primary" @click="handleAddCollapse">{{$t('fm.actions.addCollapse')}}</el-button>
            </div>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.accordion')">
            <el-switch v-model="data.options.accordion"></el-switch>
          </el-form-item>
        </template>

        <template v-if="data.type == 'report'">
          <el-form-item :label="$t('fm.config.widget.borderWidth')">
            <el-input-number v-model="data.options.borderWidth" :min="0" :step="1"></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.borderColor')">
            <el-color-picker 
              v-model="data.options.borderColor"
            ></el-color-picker>
          </el-form-item>
        </template>

        <template v-if="data.type == 'dialog'">
          <el-form-item :label="$t('fm.config.widget.showDialog')">
            <el-switch v-model="data.options.visible"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.center')">
            <el-switch v-model="data.options.center"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.showClose')">
            <el-switch v-model="data.options.showClose"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.showCancel')">
            <el-switch v-model="data.options.showCancel"></el-switch>
            <el-input v-model="data.options.cancelText" v-if="data.options.showCancel">
              <template #prepend>{{$t('fm.config.widget.cancelText')}}</template>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.showOk')">
            <el-switch v-model="data.options.showOk"></el-switch>
            <el-checkbox v-model="data.options.confirmLoading" :label="$t('fm.config.widget.confirmLoading')" v-if="data.options.showOk" style="margin-left: 18px; vertical-align: middle;"></el-checkbox>
            <el-input v-model="data.options.okText" v-if="data.options.showOk">
              <template #prepend>{{$t('fm.config.widget.okText')}}</template>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.marginTop')">
            <el-input v-model="data.options.top"></el-input>
          </el-form-item>
        </template>

        <template v-if="data.type == 'card'">
          <el-form-item :label="$t('fm.config.widget.padding')">
            <el-input v-model="data.options.padding" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.showHeader')">
            <el-switch v-model="data.options.showHeader"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.bordered')">
            <el-switch v-model="data.options.bordered"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('fm.config.widget.shadow')">
            <el-radio-group v-model="data.options.shadow">
              <el-radio-button label="always" value="always">always</el-radio-button>
              <el-radio-button label="hover" value="hover">hover</el-radio-button>
              <el-radio-button label="never" value="never">never</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </template>

        <el-form-item :label="$t('fm.config.widget.customClass')" v-if="Object.keys(data.options).includes('customClass')">
          
          <el-select
            style="width: 100%;"
            v-model="customClassArray"
            multiple
            filterable
            allow-create
            default-first-option>
            <el-option
              v-for="item in sheets"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item :label="$t('fm.config.widget.attribute')" key="attribute" v-if="data.type != 'td' && data.type != 'th'">
          <el-checkbox v-model="data.options.dataBind" v-if="Object.keys(data.options).indexOf('dataBind')>=0" >{{$t('fm.config.widget.dataBind')}}	</el-checkbox>
          <el-checkbox v-model="data.options.hidden" v-if="Object.keys(data.options).indexOf('hidden')>=0">{{$t('fm.config.widget.hidden')}}	</el-checkbox>
          <el-checkbox v-model="data.options.readonly" v-if="Object.keys(data.options).indexOf('readonly')>=0">{{$t('fm.config.widget.readonly')}} </el-checkbox>
          <el-checkbox v-model="data.options.disabled" v-if="Object.keys(data.options).indexOf('disabled')>=0">{{$t('fm.config.widget.disabled')}}	</el-checkbox>
          <el-checkbox v-model="data.options.editable" v-if="Object.keys(data.options).indexOf('editable')>=0">{{$t('fm.config.widget.editable')}} </el-checkbox>
          <el-checkbox v-model="data.options.clearable" v-if="Object.keys(data.options).indexOf('clearable')>=0">{{$t('fm.config.widget.clearable')}} </el-checkbox>
          <el-checkbox v-model="data.options.arrowControl" v-if="Object.keys(data.options).indexOf('arrowControl')>=0">{{$t('fm.config.widget.arrowControl')}} </el-checkbox>
          <el-checkbox v-model="data.options.isDelete" v-if="Object.keys(data.options).indexOf('isDelete')>=0">{{$t('fm.config.widget.isDelete')}} </el-checkbox>
          <el-checkbox v-model="data.options.isEdit" v-if="Object.keys(data.options).indexOf('isEdit')>=0">{{$t('fm.config.widget.isEdit')}} </el-checkbox>
          <el-checkbox v-model="data.options.isAdd" v-if="Object.keys(data.options).indexOf('isAdd')>=0">{{$t('fm.config.widget.isAdd')}} </el-checkbox>
          <el-checkbox v-model="data.options.showPassword" v-if="Object.keys(data.options).indexOf('showPassword')>=0">{{$t('fm.config.widget.showPassword')}} </el-checkbox>
          <el-checkbox v-model="data.options.showScore" v-if="Object.keys(data.options).indexOf('showScore')>=0">{{$t('fm.config.widget.showScore')}} </el-checkbox>
          <el-checkbox v-model="data.options.showWordLimit" v-if="Object.keys(data.options).indexOf('showWordLimit')>=0">{{$t('fm.config.widget.showWordLimit')}} </el-checkbox>
        </el-form-item>

        <!-- 自定义属性插槽 -->
        <slot name="widgetconfig" v-if="data.options.customProps" :type="data.type" :data="data" :customProps="data.options.customProps"></slot>

        <template v-if="data.type == 'custom'">
          <el-form-item :label="$t('fm.config.widget.extendPropsConfig')" v-if="Object.keys(data.options).includes('extendProps')">
            <el-button style="width: 100%;" @click="handleSetProps">{{$t('fm.config.widget.setting')}}</el-button>
          </el-form-item>
        </template>

        <template v-if="data.type != 'grid' && data.type != 'tabs' && data.type != 'collapse' && data.type != 'report' && data.type != 'inline' && data.type != 'divider' && data.type != 'td' && data.type != 'th' && data.type != 'col' && data.type != 'button' && data.type != 'link' && data.type != 'steps' && data.type != 'alert' && data.type != 'pagination' && data.type != 'dialog' && data.type != 'card'">
          
          <el-form-item :label="$t('fm.config.widget.validate')">
            <div class="validate-block" v-if="Object.keys(data.options).indexOf('required')>=0">
              <el-checkbox v-model="data.options.required">{{$t('fm.config.widget.required')}}</el-checkbox>

              <el-input class="message-input" clearable  v-model="data.options.requiredMessage" v-if="data.options.required"  :placeholder="$t('fm.message.errorTip')"></el-input>
            </div>
            <div class="validate-block" v-if="Object.keys(data.options).indexOf('dataType')>=0">
              <el-checkbox v-model="data.options.dataTypeCheck" style="margin-right: 10px;"></el-checkbox>
              <el-select :disabled="!data.options.dataTypeCheck" v-if="Object.keys(data.options).indexOf('dataType')>=0" v-model="data.options.dataType" style=" width: 239px;">
                <el-option value="string" :label="$t('fm.config.widget.string')"></el-option>
                <el-option value="url" :label="$t('fm.config.widget.url')"></el-option>
                <el-option value="email" :label="$t('fm.config.widget.email')"></el-option>
                <el-option value="hex" :label="$t('fm.config.widget.hex')"></el-option>
              </el-select>

              <el-input class="message-input" clearable  v-model="data.options.dataTypeMessage" v-if="data.options.dataTypeCheck"  :placeholder="$t('fm.message.errorTip')"></el-input>
            </div>
            
            <div class="validate-block" v-if="Object.keys(data.options).indexOf('pattern')>=0">
              <el-checkbox v-model="data.options.patternCheck" style="margin-right: 10px;"></el-checkbox>
              <el-input clearable :disabled="!data.options.patternCheck"  v-model.lazy="data.options.pattern"  style=" width: 239px;" :placeholder="$t('fm.config.widget.patternPlaceholder')"></el-input>
              <el-input class="message-input" clearable  v-model="data.options.patternMessage" v-if="data.options.patternCheck"  :placeholder="$t('fm.message.errorTip')"></el-input>
            </div>

            <div class="validate-block" v-if="Object.keys(data.options).indexOf('validator')>=0">
              <el-checkbox v-model="data.options.validatorCheck" style="margin-right: 10px;">{{$t('fm.config.widget.customValidation')}}</el-checkbox>

              <div v-if="data.options.validatorCheck">
                <div style="font-size: 14px;font-weight: 500;" class="code-line">(rule, value, callback) => {</div>
                <code-editor mode="javascript" :key="data.key" v-model="data.options.validator" height="150px"></code-editor>
                <div style="font-size: 14px;font-weight: 500;" class="code-line">}</div>
              </div>
              
            </div>
          </el-form-item>
        </template>

        <template v-if="data.events ">
          <el-form-item :label="$t('fm.eventscript.config.title')">
            <event-config :events="data.events" :eventscripts="eventscripts" @on-add="handleEventAdd" @on-edit="handleEventEdit" @on-remove="handleEventRemove"></event-config>
          </el-form-item>
        </template>
      </el-form>
    </div>
    <div v-else class="empty">
      {{$t('fm.description.configEmpty')}}
    </div>

    <code-dialog ref="codeDialog" mode="html" :title="$t('fm.config.widget.customTemplates')" help="https://www.yuque.com/ln7ccx/ntgo8q/zr53m4" @on-confirm="handleTemplateConfirm"></code-dialog>

    <code-dialog ref="cascaderDialog" width="800px" code-height="400px" mode="javascript" :title="$t('fm.config.widget.option')"  @on-confirm="handleCascaderConfirm"></code-dialog>

    <code-dialog ref="treeDialog" width="800px" code-height="400px" mode="javascript" :title="$t('fm.config.widget.option')"  @on-confirm="handleTreeConfirm"></code-dialog>

    <code-dialog ref="extendPropsDialog" width="800px" code-height="400px" mode="javascript" :title="$t('fm.config.widget.extendPropsConfig')"  @on-confirm="handlePropsConfirm"></code-dialog>

    <code-dialog ref="defaultValueDialog" width="800px" code-height="400px" mode="javascript" :title="$t('fm.config.widget.defaultValue')" @on-confirm="handleDefaultValueConfirm"></code-dialog>
    <!-- Y9 -->
    <selectTableAndField ref="selectTableAndField" :bindField="saveBindField"/>
    <selectChildTable ref="selectChildTable" :bindTable="saveBindTable"/>
    <selectField ref="selectField" :bindField="saveBindField"/>
    <selectOpinionFrame ref="selectOpinionFrame" :bindOpinionFrame="saveBindOpinionFrame"/>
    <selectNumber ref="selectNumber" :bindNumber="saveBindNumber"/>
    <!-- Y9 -->
    <cus-dialog 
      :visible="editorVisible"
      @on-close="editorVisible = false"
      :width="`calc(${data?.options?.width || '900px'} + 40px)`"
      form
      :title="$t('fm.config.widget.defaultValue')"
      @on-submit="handleEditorValueConfirm"
    >

      <Editor
        v-model="editorValue"
        :custom-style="{width: data.options.width}"
        :toolbar="data.options.customToolbar"
        class="fm-editor"
      ></Editor>
    </cus-dialog>
  </div>
</el-scrollbar>
</template>

<script>
import Draggable from 'vuedraggable/src/vuedraggable'
import CodeEditor from '../components/CodeEditor/index.vue'
import CodeDialog from './CodeDialog.vue'
import CusDialog from './CusDialog.vue'
import FmFormTable from './FormTable/index.vue'
import EventConfig from './EventPanel/config.vue'
import Editor from './Editor/index.vue'
import { EventBus } from '../util/event-bus.js'
import { ElMessage } from 'element-plus'
//Y9
import selectTableAndField from './SecondDev/selectTableAndField.vue'
import selectChildTable from './SecondDev/selectChildTable.vue'
import selectField from './SecondDev/selectField.vue'
import selectOpinionFrame from './SecondDev/selectOpinionFrame.vue'
import selectNumber from './SecondDev/selectNumber.vue'
export default {
  components: {
    Draggable,
    CodeEditor,
    CodeDialog,
    CusDialog,
    FmFormTable,
    EventConfig,
    Editor,
    selectTableAndField,
    selectChildTable,
    selectField,
    selectOpinionFrame,
    selectNumber
  },
  props: ['data', 'sheets', 'platform', 'datasources', 'eventscripts', 'formKey', 'fieldModels', 'systemName', 'fieldBind', 'formId'],
  emits: ['on-event-add', 'on-event-edit', 'on-event-remove'],
  inject: ['getModelNode'],
  data () {
    return {
      validator: {
        type: null,
        required: null,
        pattern: null,
        range: null,
        length: null,
        validator: null
      },
      bindFieldType: ['input', 'textarea', 'radio', 'select', 'checkbox', 'date', 'time', 'switch', 'number'],//Y9可绑定数据库字段类型
      isBind: false,//Y9
      editorVisible: false,
      editorValue: '',
      tableVisible: false,
      customClassArray: this.data && this.data.options && this.data.options.customClass ? this.data.options.customClass.split(' ').filter(item => item) : []
    }
  },
  computed: {
    show () {
      if (this.data && Object.keys(this.data).length > 0 && this.data.key && this.data.options) {
        return true
      }
      return false
    }
  },
  mounted () {
    this.validateRequired(this.data && this.data.options ? this.data.options.required : false)
    this.validateDataType(this.data && this.data.options ? this.data.options.dataType : '')
    this.valiatePattern(this.data && this.data.options ? this.data.options.pattern : '')
    this.validateCustom(this.data && this.data.options ? this.data.options.validator: '')
    if (this.data != null && this.data.options != undefined) {//Y9
      this.data.options.bindDatabase = (this.data.options.bindDatabase == undefined ? true : this.data.options.bindDatabase);
    }
    for (let item of this.fieldBind) {//Y9判断是否绑定数据库字段
      if (item.fieldName == this.data.model) {
        this.isBind = true;
        break;
      }
    }
  },
  methods: {
    reloadFieldBind() {//Y9重新获取字段绑定
      for (let item of this.fieldBind) {
        if (item.fieldName == this.data.model) {
          this.isBind = true;
          break;
        }
      }
    },
    handleOptionsRemove (index) {
      if (this.data.type === 'grid') {
        this.data.columns.splice(index, 1)
      } else if (this.data.type === 'tabs' || this.data.type === 'collapse') {
        this.data.tabs.splice(index, 1)
      } else if (this.data.type === 'imgupload' || this.data.type === 'fileupload') {
        this.data.options.headers.splice(index, 1)
      } else if (this.data.type === 'steps') {
        this.data.options.steps.splice(index, 1)
      } else if (this.data.type === 'transfer') {
        this.data.options.data.splice(index, 1)
      } else {
        if (!this.data.options.remote && this.data.options.options[index].value) {
          this.data.options.defaultValue = typeof this.data.options.defaultValue === 'string' ? '' : []
        }

        this.data.options.options.splice(index, 1)
      }

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleClearSelect () {
      if (this.data.type=='checkbox' || (this.data.type=='select' && this.data.options.multiple) || this.data.type == 'transfer') {
        this.data.options.defaultValue = []
      } else {
        this.data.options.defaultValue = ''
      }

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleAddData () {
      this.data.options.data.push({
        key: '',
        label: ''
      })

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleAddStep () {
      this.data.options.steps.push({
        title: 'New Step',
        description: ''
      })

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleAddOption () {
      if (this.data.options.showLabel) {
        this.data.options.options.push({
          value: this.$t('fm.config.widget.newOption'),
          label: this.$t('fm.config.widget.newOption')
        })
      } else {
        this.data.options.options.push({
          value: this.$t('fm.config.widget.newOption')
        })
      }
      
      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleAddTab () {
      let length = this.data.tabs.length

      this.data.tabs.push({
        label: this.$t('fm.config.widget.tab') + (length + 1),
        name: 'tab_' + Math.random().toString(36).slice(-8),
        list: []
      })

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleAddCollapse () {
      let length = this.data.tabs.length

      this.data.tabs.push({
        title: this.$t('fm.config.widget.collapse') + (length + 1),
        name: 'collapse_' + Math.random().toString(36).slice(-8),
        list: []
      })

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleAddHeader () {
      if ('headers' in this.data.options) {
        this.data.options.headers.push({
          key: '',
          value: ''
        })
      } else {

        this.data.options['headers'] = [{key: '', value: ''}]
      }
      
      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    generateRule () {
      if (this.data) {
        this.data.rules = []
        Object.keys(this.validator).forEach(key => {
          if (this.validator[key]) {
            this.data.rules.push(this.validator[key])
          }
        })
      }
    },
    handleSelectMuliple (value) {
      if (this.data.type == 'select' || this.data.type == 'treeselect') {
        if (value) {
          if (this.data.options.defaultValue) {
            this.data.options.defaultValue = [this.data.options.defaultValue]
          } else {
            this.data.options.defaultValue = []
          }
          
        } else {
          if (this.data.options.defaultValue.length>0){
            this.data.options.defaultValue = this.data.options.defaultValue[0]
          } else {
            this.data.options.defaultValue = ''
          }     
        }
      }
    },

    handleSetTemplate () {
      this.$refs.codeDialog.open(this.data.options.template)
    },

    handleTemplateConfirm (value) {
      try {
        this.data.options.template = value

        this.$refs.codeDialog.close()

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      } catch (e) {
        this.$refs.codeDialog.end()

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
    },

    handleSetEditorValue () {
      this.editorVisible = true

      this.editorValue = this.data.options.defaultValue
    },

    handleEditorValueConfirm (value) {
      try {
        this.data.options.defaultValue = this.editorValue

        this.editorVisible = false

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      } catch (e) {
        this.editorVisible = false

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
    },

    handleSetDefaultValue () {
      this.$refs.defaultValueDialog.open(this.data.options.defaultValue)
    },

    handleDefaultValueConfirm (value) {
      try {
        if (typeof value == 'string') {
          this.data.options.defaultValue =  Function('"use strict";return (' + value + ')')()
        } else {
          this.data.options.defaultValue =  value
        }

        this.$refs.defaultValueDialog.close()

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      } catch (e) {
        this.$refs.defaultValueDialog.end()

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
    },

    handleSetCascader () {
      this.$refs.cascaderDialog.open(this.data.options.options)
    },

    handleCascaderConfirm (value) {
      try {
        if (typeof value == 'string') {
          this.data.options.options =  Function('"use strict";return (' + value + ')')()
        } else {
          this.data.options.options =  value
        }

        this.$refs.cascaderDialog.close()

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      } catch (e) {
        this.$refs.cascaderDialog.end()

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
      
    },

    handleSetTree () {
      this.$refs.treeDialog.open(this.data.options.options)
    },

    handleTreeConfirm (value) {
      try {
        if (typeof value == 'string') {
          this.data.options.options =  Function('"use strict";return (' + value + ')')()
        } else {
          this.data.options.options =  value
        }

        this.$refs.treeDialog.close()

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      } catch (e) {
        this.$refs.treeDialog.end()

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
    },

    handleSetProps () {
      if (!this.data.options.extendProps) {
        this.data.options.extendProps = {}
      }

      this.$refs.extendPropsDialog.open(this.data.options.extendProps)
    },

    handlePropsConfirm (value) {
      try {
        if (typeof value == 'string') {
          this.data.options.extendProps =  Function('"use strict";return (' + value + ')')()
        } else {
          this.data.options.extendProps =  value
        }

        this.$refs.extendPropsDialog.close()

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      } catch (e) {
        this.$refs.extendPropsDialog.end()

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
    },

    validateRequired (val) {
      if (val) {
        this.validator.required = {required: true, message: this.data.options.requiredMessage ? this.data.options.requiredMessage : ``}
      } else {
        this.validator.required = null
      }

      this.$nextTick(() => {
        this.generateRule()
      })
    },

    validateDataType (val) {
      if (!this.show) {
        return false
      }
      
      if (val && (this.data.options.dataTypeCheck || !Object.keys(this.data.options).includes('dataTypeCheck'))) {
        this.validator.type = {type: val, message: this.data.options.dataTypeMessage ? this.data.options.dataTypeMessage : ''}
      } else {
        this.validator.type = null
      }

      this.generateRule()
    },
    valiatePattern (val) {
      if (!this.show) {
        return false
      }

      if (val && (this.data.options.patternCheck || !Object.keys(this.data.options).includes('patternCheck'))) {
        this.validator.pattern = {pattern: val, message: this.data.options.patternMessage ? this.data.options.patternMessage : ''}
      } else {
        this.validator.pattern = null
      }

      this.generateRule()
    },

    validateCustom (val) {
      if (!this.show) {
        return false
      }

      if (val && this.data.options.validatorCheck) {
        this.validator.validator = {func: val}
      } else {
        this.validator.validator = null
      }

      this.generateRule()
    },

    handleEventAdd (name) {
      this.$emit('on-event-add', name)
    },

    handleEventEdit ({eventName, functionKey}) {
      this.$emit('on-event-edit', {eventName, functionKey})
    },

    handleEventRemove (eventName) {
      this.$emit('on-event-remove', eventName)

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },

    setEvent (eventObj) {
      this.data.events[eventObj.type] = eventObj.key

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },

    handleDataSourceChange (value) {
      let args = this.datasources.find(item => item.value == value)?.args
      if (args) {
        this.data.options.remoteArgs = args
      }
    },
    selectTableAndField() {//Y9数据库字段绑定
      if (this.data.childTableInfo != undefined) {//childTableInfo为绑定子表信息：表名@表id
          if (this.data.childTableInfo.indexOf("@") == -1) {
              this.$message({type: 'error', message: "请先绑定子表"});
              return;
          }
          this.$refs.selectField.show(this.data.childTableInfo.split("@")[1]);
      } else {
          this.$refs.selectTableAndField.show(this.systemName);
      }
    },
    selectChildTable() {//子表绑定
      this.$refs.selectChildTable.show(this.systemName);
    },
    sethidden() {//Y9_设置隐藏字段
      let obj = {};
      obj.name = this.data.model;
      obj.hidden = this.data.options.hidden;
      this.$emit("sethidden", obj);
    },
    setQuerySign() {//设置开启查询
      let b = true;
      for (let item of this.fieldBind) {
          if (item.fieldName == this.data.model) {
              b = false;
              item.querySign = this.data.options.querySign ? "1" : "0";
              item.optionValue = '';
              if (this.data.type == 'radio' || this.data.type == 'checkbox' || this.data.type == 'select') {
                  if (this.data.options.remote) {//动态数据
                      item.optionValue = this.data.options.optionData;
                  } else {//静态数据
                      item.optionValue = JSON.stringify(this.data.options.options);
                  }
              }
              item.queryType = this.data.type;
              this.$emit("setFieldBind", item);
              break;
          }
      }
      if (b) {
          this.data.options.querySign = false;
          this.$message({type: 'error', message: "请先绑定数据库字段"});
      }
    },
    saveBindField(field) {//Y9_绑定表字段
      this.data.model = field.fieldName;
      this.data.bindTable = field.tableName;
      let formField = {};
      formField.fieldName = field.fieldName;
      formField.fieldCnName = field.fieldCnName;
      formField.tableName = field.tableName;
      formField.tableId = field.tableId;
      formField.fieldType = field.fieldType;
      if (this.data.options.hidden) {
          formField.fieldType = "hidden";
      }
      formField.querySign = this.data.options.querySign ? "1" : "0";
      formField.optionValue = '';
      formField.queryType = this.data.type;
      if (this.data.type == 'radio' || this.data.type == 'checkbox' || this.data.type == 'select') {
          if (this.data.options.remote) {//动态数据
              formField.optionValue = this.data.options.optionData;
          } else {//静态数据
              formField.optionValue = JSON.stringify(this.data.options.options);
          }
      }
      this.$emit("setFieldBind", formField);

      let ispush = true;
      for (let i = 0; i < this.fieldBind.length; i++) {
          let item = this.fieldBind[i];
          if (item.fieldName == formField.fieldName && item.tableName == formField.tableName) {
              ispush = false;
              this.fieldBind[i] = formField;//替换
              break;
          }
      }
      if (ispush) {
          this.isBind = true;
          this.fieldBind.push(formField);
      }
      this.reloadFieldBind();
    },
    saveBindTable(table) {//子表绑定
      this.data.model = "childTable@" + table.tableName;
      this.data.childTableInfo = table.tableName + "@" + table.id;
    },
    saveBindOption(option) {//绑定数据字典
      this.data.options.optionData = option.name + "(" + option.type + ")";
    },
    selectOpinionFrame() {//绑定意见框
      this.$refs.selectOpinionFrame.show();
    },
    saveBindOpinionFrame(opinionFrame) {//保存意见框绑定
      this.data.model = "custom_opinion@" + opinionFrame.mark;
      this.data.name = opinionFrame.name;
    },
    selectNumber() {//绑定编号
      this.$refs.selectNumber.show();
    },
    saveBindNumber(number) {//保存编号绑定
      this.data.model = "custom_numberButton@" + number.custom;
      this.data.name = number.name;
    },
  },
  watch: {
    'systemName': function (val) {//Y9
      this.systemName = val;
    },
    'formId': function (val) {//Y9
      this.formId = val;
    },
    'data.options.isRange': function(val) {
      if (typeof val !== 'undefined') {
        if (val) {
          this.data.options.defaultValue = null
        } else {
          if (Object.keys(this.data.options).indexOf('defaultValue')>=0) 
            this.data.options.defaultValue = ''
        }
      }
    },
    'data.options.type': function(val) {
      if (this.data.type == 'date') {
        if (val == 'daterange' || val == 'datetimerange' || val == 'dates') {
          this.data.options.defaultValue = []
        } else {
          this.data.options.defaultValue = ''
        }
      }
    },
    'data.options.required': function(val) {
      this.validateRequired(val)
    },
    'data.options.requiredMessage':function (val) {
      this.validateRequired(this.data && this.data.options ? this.data.options.required : false)
    },
    'data.options.dataType': function (val) {
      this.validateDataType(val)
    },
    'data.options.dataTypeCheck': function (val) {
      this.validateDataType(this.data && this.data.options ? this.data.options.dataType : '')
    },
    'data.options.dataTypeMessage': function (val) {
      this.validateDataType(this.data && this.data.options ? this.data.options.dataType : '')
    },
    'data.options.pattern': function (val) {
      this.valiatePattern(val)
    },
    'data.options.patternCheck': function (val) {
      this.valiatePattern(this.data && this.data.options ? this.data.options.pattern : '')
    },
    'data.options.patternMessage': function (val) {
      this.valiatePattern(this.data && this.data.options ? this.data.options.pattern : '')
    },
    'data.options.validator': function (val) {
      this.validateCustom(val)
    },
    'data.options.validatorCheck': function (val) {
      this.validateCustom(this.data && this.data.options ? this.data.options.validator : '')
    },
    'data.options.options': {
      deep: true,
      handler (val) {
        if (this.data.options && typeof this.data.options.defaultValue == 'object' && Array.isArray(this.data.options.defaultValue)) {
          this.data.options.defaultValue = this.data.options.defaultValue.filter(item => val.map(item => item.value).includes(item))
        }
        if (this.data.options && typeof this.data.options.defaultValue == 'string') {

          if (typeof val == 'object' && !val.map(item => item.value).includes(this.data.options.defaultValue)) {
            this.data.options.defaultValue = ''
          }
        }
      }
    },
    'data.options.customClass': function(val) {
      this.customClassArray = this.data && this.data.options && this.data.options.customClass ? this.data.options.customClass.split(' ').filter(item => item) : []
    },
    'data.options.format': function (val) {
      this.data.options.defaultValue = ''
    },
    customClassArray (val) {
      this.data.options.customClass = val.join(' ')
    }
  }
}
</script>
<!-- Y9++ -->
<style scoped>
.el-select {
    width: auto !important;
}
</style>
<!-- Y9++ -->