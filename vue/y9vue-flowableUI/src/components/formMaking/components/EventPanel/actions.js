export const formActions = [
  {
    action: 'hide',
    options: {
      fields: [],
      condition: '',
      isCondition: false,
    }
  },
  {
    action: 'display',
    options: {
      fields: [],
      condition: '',
      isCondition: false,
    }
  },
  {
    action: 'disabled',
    options: {
      fields: [],
      condition: '',
      isCondition: false,
      disabled: false
    }
  },
  {
    action: 'refresh',
    options: {
      condition: '',
      isCondition: false,
    }
  },
  {
    action: 'reset',
    options: {
      condition: '',
      isCondition: false,
    }
  },
  {
    action: 'setData',
    options: {
      condition: '',
      isCondition: false,
      fields: [],
      values: {},
      valueTypes: {}
    }
  },
  {
    action: 'validate',
    options: {
      condition: '',
      isCondition: false,
      fields: [],
      failSuspend: false
    }
  }
]

export const requestActions = [
  {
    action: 'sendRequest',
    options: {
      condition: '',
      isCondition: false,
      dataSource: '',
      valueTypes: {},
      responseVariable: ''
    }
  },
  {
    action: 'refreshFieldDataSource',
    options: {
      condition: '',
      isCondition: false,
      field: '',
      valueTypes: {},
    }
  },
  {
    action: 'getFieldDataSource',
    options: {
      condition: '',
      isCondition: false,
      field: '',
      localVariable: ''
    }
  }
]

export const dialogActions = [
  {
    action: 'openDialog',
    options: {
      condition: '',
      isCondition: false,
      field: '',
    }
  },
  {
    action: 'closeDialog',
    options: {
      condition: '',
      isCondition: false,
      field: '',
    }
  }
]

export const otherActions = [
  {
    action: 'triggerEvent',
    options: {
      condition: '',
      isCondition: false,
      functionName: '',
      functionParams: ''
    }
  },
  {
    action: 'js',
    options: {
      func: ''
    }
  }
]