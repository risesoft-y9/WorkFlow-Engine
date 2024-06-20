/*
 * @author igdianov
 * address https://github.com/igdianov/activiti-bpmn-moddle
 * */

import FlowableModdleExtension from './flowableExtension.js'
export default {
  __init__: ['FlowableModdleExtension'],
  FlowableModdleExtension: ['type', FlowableModdleExtension]
};
