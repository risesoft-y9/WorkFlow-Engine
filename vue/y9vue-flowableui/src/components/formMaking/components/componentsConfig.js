export const basicComponents = [
  {
    type: 'input',
    icon: 'icon-input',
    options: {
      width: '',
      defaultValue: '',
      fieldPermission:'',//字段权限配置属性。
      bindDatabase:true,//是否需要绑定数据库字段
      readonly:false,//完全只读
      querySign:false,//开启查询条件
      required: false,
      requiredMessage: '',
      dataType: '',
      dataTypeCheck: false,
      dataTypeMessage: '',
      pattern: '',
      patternCheck: false,
      patternMessage: '',
      validatorCheck: false,
      validator: '',
      placeholder: '',
      customClass: '',
      disabled: false,
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      showPassword: false,
      clearable: false,
      maxlength: '',
      showWordLimit: false,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
  {
    type: 'textarea',
    icon: 'icon-diy-com-textarea',
    options: {
      width: '',
      defaultValue: '',
      fieldPermission:'',//字段权限配置属性。
      readonly:false,//完全只读
      querySign:false,//开启查询条件
      required: false,
      requiredMessage: '',
      disabled: false,
      pattern: '',
      patternMessage: '',
      validatorCheck: false,
      validator: '',
      placeholder: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      clearable: false,
      maxlength: '',
      showWordLimit: false,
      rows: 2,
      autosize: false,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
  {
    type: 'number',
    icon: 'icon-number',
    options: {
      width: '',
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      defaultValue: 0,
      fieldPermission:'',//字段权限配置属性。
      min: 0,
      max: 9,
      step: 1,
      disabled: false,
      controls: true,
      controlsPosition: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      precision: 0,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
  {
    type: 'radio',
    icon: 'icon-radio-active',
    options: {
      inline: false,
      defaultValue: '',
      fieldPermission:'',//字段权限配置属性。
      optionData:'',//数据字典配置
      querySign:false,//开启查询条件
      showLabel: false,
      options: [
        {
          value: 'Option 1',
          label: 'Option 1'
        },
        {
          value: 'Option 2',
          label: 'Option 2'
        },
        {
          value: 'Option 3',
          label: 'Option 3'
        }
      ],
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      width: '',
      remote: false,
      remoteType: 'func',
      remoteOption: '',
      remoteOptions: [],
      props: {
        value: 'value',
        label: 'label'
      },
      remoteFunc: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      disabled: false,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'checkbox',
    icon: 'icon-check-box',
    options: {
      inline: false,
      defaultValue: [],
      fieldPermission:'',//字段权限配置属性。
      optionData:'',//数据字典配置
      querySign:false,//开启查询条件
      showLabel: false,
      options: [
        {
          value: 'Option 1'
        },
        {
          value: 'Option 2'
        },
        {
          value: 'Option 3'
        }
      ],
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      width: '',
      remote: false,
      remoteType: 'func',
      remoteOption: '',
      remoteOptions: [],
      props: {
        value: 'value',
        label: 'label'
      },
      remoteFunc: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      disabled: false,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'select',
    icon: 'icon-select',
    options: {
      defaultValue: '',
      fieldPermission:'',//字段权限配置属性。
      bindDatabase:true,//是否需要绑定数据库字段
      optionData:'',//数据字典配置
      querySign:false,//开启查询条件
      multiple: false,
      disabled: false,
      clearable: false,
      placeholder: '',
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      showLabel: false,
      width: '',
      options: [
        {
          value: 'Option 1'
        },
        {
          value: 'Option 2'
        },{
          value: 'Option 3'
        }
      ],
      remote: false,
      remoteType: 'func',
      remoteOption: '',
      filterable: false,
      remoteOptions: [],
      props: {
        value: 'value',
        label: 'label'
      },
      remoteFunc: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
  {
    type: 'time',
    icon: 'icon-time',
    options: {
      defaultValue: '',
      fieldPermission:'',//字段权限配置属性。
      bindDatabase: true,//是否需要绑定数据库字段
      readonly: false,
      disabled: false,
      editable: false,
      clearable: true,
      placeholder: '',
      startPlaceholder: '',
      endPlaceholder: '',
      isRange: false,
      arrowControl: false,
      format: 'HH:mm:ss',
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      width: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
  {
    type: 'date',
    icon: 'icon-date',
    options: {
      defaultValue: '',
      fieldPermission:'',//字段权限配置属性。
      bindDatabase: true,//是否需要绑定数据库字段
      querySign:false,//开启查询条件
      readonly: false,
      disabled: false,
      editable: false,
      clearable: true,
      placeholder: '',
      startPlaceholder: '',
      endPlaceholder: '',
      type: 'date',
      format: 'YYYY-MM-DD',
      timestamp: false,
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      width: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
  {
    type: 'rate',
    icon: 'icon-pingfen1',
    options: {
      defaultValue: null,
      max: 5,
      disabled: false,
      allowHalf: false,
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      showScore: false,
      width: '',
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'color',
    icon: 'icon-color',
    options: {
      defaultValue: '',
      disabled: false,
      showAlpha: false,
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      width: '',
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'switch',
    icon: 'icon-switch',
    options: {
      defaultValue: false,
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      disabled: false,
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      width: '',
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'slider',
    icon: 'icon-slider',
    options: {
      defaultValue: 0,
      disabled: false,
      required: false,
      requiredMessage: '',
      validatorCheck: false,
      validator: '',
      min: 0,
      max: 100,
      step: 1,
      showInput: false,
      range: false,
      width: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'text',
    icon: 'icon-wenzishezhi-',
    options: {
      defaultValue: 'This is a text',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      required: false,
      width: '',
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'cascader',
    icon: 'icon-jilianxuanze',
    options: {
      width: '',
      placeholder: '',
      disabled: false,
      clearable: false,
      options: [
        {
          value: 'Option 1',
          label: 'Option 1',
          children: [
            {value: 'Option 1 - children', label: 'Option 1 - children'}
          ]
        },
        {
          value: 'Option 2',
          label: 'Option 2',
          children: [
            {value: 'Option 2 - children', label: 'Option 2 - children'}
          ]
        },{
          value: 'Option 3',
          label: 'Option 3'
        }
      ],
      remote: true,
      remoteType: 'func',
      remoteOption: '',
      remoteOptions: [],
      props: {
        value: 'value',
        label: 'label',
        children: 'children'
      },
      remoteFunc: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      required: false,
      validatorCheck: false,
      validator: '',
      multiple: false,
      filterable: false,
      checkStrictly: false,
      customProps: {},
      tip: ''
    },
    events: {
      onChange: '',
      onFocus: '',
      onBlur: ''
    }
  },
]

export const advanceComponents = [
  {
    type: 'blank',
    icon: 'icon-zidingyishuju',
    options: {
      defaultType: 'String',
      customClass: '',
      width: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      required: false,
      pattern: '',
      validator: '',
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'imgupload',
    icon: 'icon-tupian',
    options: {
      defaultValue: [],
      size: {
        width: 100,
        height: 100,
      },
      width: '',
      tokenFunc: 'funcGetToken',
      token: '',
      tokenType: 'datasource',
      domain: '',
      disabled: false,
      readonly: false,
      limit: 8,
      multiple: false,
      isQiniu: false,
      isDelete: true,
      min: 0,
      isEdit: true,
      action: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      headers: [],
      required: false,
      validatorCheck: false,
      validator: '',
      withCredentials: false,
      tip: ''
    },
    events: {
      onChange: '',
      onSelect: '',
      onUploadSuccess: '',
      onUploadError: '',
      onRemove: ''
    }
  },
  {
    type: 'editor',
    icon: 'icon-fuwenbenkuang',
    options: {
      defaultValue:'',
      width: '',
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      customToolbar: [
        [ 'bold', 'italic', 'underline', 'strike', 
          { 'color': [] }, 
          { 'background': [] }, 
          { 'align': [] },
          { 'list': 'ordered'}, 
          { 'list': 'bullet' },
          { 'indent': '-1'}, 
          { 'indent': '+1' }
        ], 
        [{ 'font': [] },{ 'header': [1, 2, 3, 4, 5, 6, false] }],               
        [{ 'script': 'sub'}, { 'script': 'super' }],  
        ['link', 'image','blockquote', 'code-block'],
        [{ 'direction': 'rtl' }], 
        ['clean'] 
      ],
      disabled: false,
      required: false,
      validator: '',
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
]

export const layoutComponents = [
  {
    type: 'grid',
    icon: 'icon-RectangleCopy',
    columns: [
      {
        type: 'col',
        options: {
          span: 12,
          offset: 0,
          push: 0,
          pull: 0,
          xs: 24,
          sm: 12,
          md: 12,
          lg: 12,
          xl: 12,
          customClass: ''
        },
        list: []
      },
    ],
    options: {
      gutter: 0,
      justify: 'start',
      align: 'top',
      customClass: '',
      hidden: false,
      flex: true,
      responsive: true
    }
  }
]

