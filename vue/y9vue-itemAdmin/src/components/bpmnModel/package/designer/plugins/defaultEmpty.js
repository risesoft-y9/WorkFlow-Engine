/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2023-08-03 09:24:41
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-08-04 15:26:57
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\components\bpmnModel\package\designer\plugins\defaultEmpty.js
 */
export default (key, name, type) => {
  if (!type) type = 'flowable';
  const TYPE_TARGET = {
    activiti: 'http://activiti.org/bpmn',
    camunda: 'http://bpmn.io/schema/bpmn',
    flowable: 'http://flowable.org/bpmn'
  };
  return `<?xml version="1.0" encoding="UTF-8"?>
<bpmn2:definitions 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
  id="diagram_${key}"
  targetNamespace="${TYPE_TARGET[type]}">
  <bpmn2:process id="${key}" name="${name}" isExecutable="true">
  </bpmn2:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="${key}">
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn2:definitions>`;
};
