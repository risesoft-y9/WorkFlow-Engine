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
      remoteType: 'datasource',
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
      remoteType: 'datasource',
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
      remoteType: 'datasource',
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
    type: 'html',
    icon: 'icon-html',
    options: {
      defaultValue: '<b style="color: red;">\n\tThis is a HTML5\n</b>',
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
    type: 'button',
    icon: 'icon-button',
    options: {
      customClass: '',
      disabled: false,
      labelWidth: 100,
      isLabelWidth: false,
      hideLabel: true,
      hidden: false,
      buttonSize: 'default',
      buttonType: '',
      buttonPlain: false,
      buttonRound: false,
      buttonCircle: false,
      buttonName: 'Button',
      width: '',
      customProps: {},
      tip: ''
    },
    events: {
      onClick: ''
    }
  },
  {
    type: 'link',
    icon: 'icon-lianjie',
    options: {
      customClass: '',
      disabled: false,
      labelWidth: 100,
      isLabelWidth: false,
      hideLabel: false,
      hidden: false,
      linkType: 'default',
      linkName: 'Link',
      underline: true,
      blank: true,
      href: '',
      customProps: {},
      tip: ''
    },
    events: {
      onClick: ''
    }
  },
  {
    type: 'cascader',
    icon: 'icon-jilianxuanze',
    options: {
      defaultValue: [],
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
      remote: false,
      remoteType: 'datasource',
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
  {
    type: 'treeselect',
    icon: 'icon-shuxuanzeqi',
    options: {
      defaultValue: [],
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
      remote: false,
      remoteType: 'datasource',
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
  {
    type: 'steps',
    icon: 'icon-m-buzhou',
    options: {
      defaultValue: 0,
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hideLabel: true,
      hidden: false,
      dataBind: true,
      steps: [
        {
          title: 'Step 1'
        },
        {
          title: 'Step 2'
        },
        {
          title: 'Step 3'
        }
      ],
      props: {
        title: 'title',
        description: 'description'
      },
      remote: false,
      width: '',
      direction: 'horizontal',
      processStatus: 'process',
      finishStatus: 'finish',
      alignCenter: false,
      simple: false,
      remoteType: 'datasource',
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'transfer',
    icon: 'icon-m-chuansuokuang',
    options: {
      defaultValue: [],
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hideLabel: false,
      hidden: false,
      dataBind: true,
      data: [
        {key: '1', label: 'Option 1'},
        {key: '2', label: 'Option 2'},
        {key: '3', label: 'Option 3'},
      ],
      props: {
        key: 'key',
        label: 'label',
        disabled: 'disabled'
      },
      filterable: false,
      required: false,
      validatorCheck: false,
      validator: '',
      width: '',
      remote: false,
      titles: ['Source', 'Target'],
      disabled: false,
      remoteType: 'datasource',
      customProps: {},
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'pagination',
    icon: 'icon-pagination',
    options: {
      defaultValue: 1,
      customClass: '',
      disabled: false,
      labelWidth: 100,
      isLabelWidth: false,
      hideLabel: false,
      hidden: false,
      dataBind: true,
      background: true,
      pageSize: 10,
      pagerCount: 7,
      total: 100,
      customProps: {}
    },
    events: {
      onChange: '',
      
    }
  }
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
    type: 'component',
    icon: 'icon-component',
    options: {
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      template: '<div>自定义内容</div>',
      required: false,
      pattern: '',
      validator: '',
      width: '',
      tip: ''
    },
    events: {
      onChange: ''
    }
  },
  {
    type: 'fileupload',
    icon: 'icon-wenjianshangchuan',
    options: {
      defaultValue: [],
      width: '',
      tokenFunc: 'funcGetToken',
      token: '',
      tokenType: 'datasource',
      domain: '',
      disabled: false,
      tip: '',
      action: '',
      customClass: '',
      limit: 9,
      multiple: false,
      isQiniu: false,
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      headers: [],
      required: false,
      validatorCheck: false,
      validator: '',
      withCredentials: false
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
      defaultValue: '',
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
      validatorCheck: false,
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
  },
  {
    type: 'report',
    icon: 'icon-table1',
    options: {
      customClass: '',
      hidden: false,
      borderWidth: 1,
      borderColor: '#999',
      width: '100%'
    },
    headerRow: [
      {
        type: 'th',
        options: {
          width: ''
        }
      }
    ],
    rows: [
      {
        columns: [
          {
            type: 'td',
            options: {
              customClass: '',
              colspan: 1,
              rowspan: 1,
              align: 'left',
              valign: 'top',
              width: '',
              height: ''
            },
            list: []
          }
        ]
      }
    ]
  },
  {
    type: 'tabs',
    icon: 'icon-tabs',
    tabs: [
      {
        label: 'Tab 1',
        name: 'tab_1',
        list: []
      }
    ],
    options: {
      type: '',
      tabPosition: 'top',
      customClass: '',
      hidden: false,
    }
  },
  {
    type: 'collapse',
    icon: 'icon-zhediemianban',
    tabs: [
      {
        title: 'Collapse 1',
        name: 'collapse_1',
        list: []
      }
    ],
    options: {
      type: '',
      accordion: false,
      customClass: '',
      hidden: false,
    }
  },
  {
    type: 'inline',
    icon: 'icon-inlineview',
    options: {
      customClass: '',
      hidden: false,
      spaceSize: 10
    },
    list: []
  },
  {
    type: 'card',
    icon: 'icon-kapian',
    list: [],
    options: {
      title: 'Card name',
      showHeader: true,
      bordered: true,
      customClass: '',
      shadow: 'always',
      width: '',
      padding: '10px',
      hidden: false
    }
  },
  {
    type: 'divider',
    icon: 'icon-fengexian',
    options: {
      hidden: false,
      contentPosition: 'left',
      customProps: {}
    }
  },
  {
    type: 'alert',
    icon: 'icon-jinggaotishi',
    options: {
      hidden: false,
      title: 'Info alert',
      type: 'info',
      description: '',
      closable: true,
      center: false,
      showIcon: false,
      effect: 'light',
      width: '',
      customProps: {}
    }
  }
]

export const collectionComponents = [
  {
    type: 'table',
    icon: 'icon-table',
    options: {
      defaultValue: [],
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      disabled: false,
      required: false,
      validatorCheck: false,
      validator: '',
      paging: false,
      pageSize: 5,
      isAdd: true,
      isDelete: true,
      showControl: true,
      virtualTable: true,
      tip: ''
    },
    events: {
      onChange: '',
      onRowAdd: '',
      onRowRemove: '',
      onPageChange: ''
    },
    tableColumns: []
  },
  {
    type: 'subform',
    icon: 'icon-a-ziyuan25',
    options: {
      defaultValue: [],
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      disabled: false,
      required: false,
      validatorCheck: false,
      validator: '',
      paging: false,
      pageSize: 5,
      showControl: true,
      isAdd: true,
      isDelete: true,
      tip: ''
    },
    events: {
      onChange: '',
      onRowAdd: '',
      onRowRemove: '',
      onPageChange: ''
    },
    list: []
  },
  {
    type: 'dialog',
    icon: 'icon-Dialog',
    options: {
      defaultValue: {},
      visible: false,
      customClass: '',
      title: 'Dialog Title',
      width: '',
      top: '15vh',
      center: false,
      cancelText: 'Cancel',
      showClose: true,
      okText: 'Confirm',
      showCancel: true,
      showOk: true,
      confirmLoading: false,
      dataBind: true,
    },
    list: [],
    events: {
      onCancel: '',
      onConfirm: ''
    }
  },
  {
    type: 'group',
    icon: 'icon-fenzu',
    options: {
      defaultValue: {},
      customClass: '',
      labelWidth: 100,
      isLabelWidth: false,
      hidden: false,
      dataBind: true,
      width: '',
      validatorCheck: false,
      validator: '',
      tip: ''
    },
    list: [],
  }
]
