export default {
	"list": [
		{
			"type": "inline",
			"icon": "icon-inlineview",
			"options": {
				"customClass": "",
				"hidden": false,
				"spaceSize": 20,
				"remoteFunc": "func_54uqa4ep",
				"remoteOption": "option_54uqa4ep"
			},
			"list": [
				{
					"type": "input",
					"icon": "icon-input",
					"options": {
						"width": "",
						"defaultValue": "",
						"required": false,
						"requiredMessage": "",
						"dataType": "",
						"dataTypeCheck": false,
						"dataTypeMessage": "",
						"pattern": "",
						"patternCheck": false,
						"patternMessage": "",
						"validatorCheck": false,
						"validator": "",
						"placeholder": "",
						"customClass": "",
						"disabled": false,
						"labelWidth": 50,
						"isLabelWidth": true,
						"hidden": false,
						"dataBind": true,
						"showPassword": false,
						"clearable": false,
						"remoteFunc": "func_xwvwfao4",
						"remoteOption": "option_xwvwfao4",
						"subform": false
					},
					"events": {
						"onChange": "",
						"onFocus": "",
						"onBlur": ""
					},
					"name": "Name",
					"key": "xwvwfao4",
					"model": "name",
					"rules": []
				},
				{
					"type": "input",
					"icon": "icon-input",
					"options": {
						"width": "",
						"defaultValue": "",
						"required": false,
						"requiredMessage": "",
						"dataType": "",
						"dataTypeCheck": false,
						"dataTypeMessage": "",
						"pattern": "",
						"patternCheck": false,
						"patternMessage": "",
						"validatorCheck": false,
						"validator": "",
						"placeholder": "",
						"customClass": "",
						"disabled": false,
						"labelWidth": 80,
						"isLabelWidth": true,
						"hidden": false,
						"dataBind": true,
						"showPassword": false,
						"clearable": false,
						"remoteFunc": "func_xwvwfao4",
						"remoteOption": "option_xwvwfao4",
						"subform": false
					},
					"events": {
						"onChange": "",
						"onFocus": "",
						"onBlur": ""
					},
					"name": "Address",
					"key": "w1ma2t9o",
					"model": "address",
					"rules": []
				},
				{
					"type": "button",
					"icon": "icon-button",
					"options": {
						"customClass": "",
						"disabled": false,
						"labelWidth": 100,
						"isLabelWidth": false,
						"hideLabel": true,
						"hidden": false,
						"buttonSize": "default",
						"buttonType": "primary",
						"buttonPlain": false,
						"buttonRound": false,
						"buttonCircle": false,
						"buttonName": "查 询",
						"width": "",
						"remoteFunc": "func_kwrk2vfo",
						"remoteOption": "option_kwrk2vfo",
						"subform": false
					},
					"events": {
						"onClick": "9urxn55l"
					},
					"name": "Button",
					"key": "kwrk2vfo",
					"model": "button_kwrk2vfo",
					"rules": []
				}
			],
			"name": "Inline",
			"key": "54uqa4ep",
			"model": "inline_54uqa4ep",
			"rules": []
		},
		{
			"type": "table",
			"icon": "icon-table",
			"options": {
				"defaultValue": [],
				"customClass": "",
				"labelWidth": 100,
				"isLabelWidth": false,
				"hidden": false,
				"dataBind": true,
				"disabled": false,
				"required": false,
				"validatorCheck": false,
				"validator": "",
				"paging": false,
				"pageSize": 5,
				"isAdd": false,
				"isDelete": false,
				"showControl": false,
				"remoteFunc": "func_9wmtwlft",
				"remoteOption": "option_9wmtwlft",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": "",
				"onRowAdd": "",
				"onRowRemove": "",
				"onPageChange": ""
			},
			"tableColumns": [
				{
					"type": "text",
					"icon": "icon-wenzishezhi-",
					"options": {
						"defaultValue": "",
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"required": false,
						"width": "100px",
						"remoteFunc": "func_gt3uhdyl",
						"remoteOption": "option_gt3uhdyl",
						"tableColumn": true,
						"fixedColumn": true,
						"fixedColumnPosition": "left"
					},
					"events": {
						"onChange": ""
					},
					"name": "ID",
					"novalid": {},
					"key": "gt3uhdyl",
					"model": "id",
					"rules": []
				},
				{
					"type": "text",
					"icon": "icon-wenzishezhi-",
					"options": {
						"defaultValue": "",
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"required": false,
						"width": "200px",
						"remoteFunc": "func_gt3uhdyl",
						"remoteOption": "option_gt3uhdyl",
						"tableColumn": true
					},
					"events": {
						"onChange": ""
					},
					"name": "Name",
					"novalid": {},
					"key": "z970luym",
					"model": "name",
					"rules": []
				},
				{
					"type": "text",
					"icon": "icon-wenzishezhi-",
					"options": {
						"defaultValue": "",
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"required": false,
						"width": "200px",
						"remoteFunc": "func_gt3uhdyl",
						"remoteOption": "option_gt3uhdyl",
						"tableColumn": true
					},
					"events": {
						"onChange": ""
					},
					"name": "State",
					"novalid": {},
					"key": "8mtpr4lb",
					"model": "state",
					"rules": []
				},
				{
					"type": "text",
					"icon": "icon-wenzishezhi-",
					"options": {
						"defaultValue": "",
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"required": false,
						"width": "300px",
						"remoteFunc": "func_gt3uhdyl",
						"remoteOption": "option_gt3uhdyl",
						"tableColumn": true
					},
					"events": {
						"onChange": ""
					},
					"name": "City",
					"novalid": {},
					"key": "zjje0rnn",
					"model": "city",
					"rules": []
				},
				{
					"type": "text",
					"icon": "icon-wenzishezhi-",
					"options": {
						"defaultValue": "",
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"required": false,
						"width": "400px",
						"remoteFunc": "func_gt3uhdyl",
						"remoteOption": "option_gt3uhdyl",
						"tableColumn": true
					},
					"events": {
						"onChange": ""
					},
					"name": "Address",
					"novalid": {},
					"key": "mgo2ow9v",
					"model": "address",
					"rules": []
				},
				{
					"type": "text",
					"icon": "icon-wenzishezhi-",
					"options": {
						"defaultValue": "",
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"required": false,
						"width": "200px",
						"remoteFunc": "func_gt3uhdyl",
						"remoteOption": "option_gt3uhdyl",
						"tableColumn": true
					},
					"events": {
						"onChange": ""
					},
					"name": "Zip",
					"novalid": {},
					"key": "ad8miomh",
					"model": "zip",
					"rules": []
				},
				{
					"type": "component",
					"icon": "icon-component",
					"options": {
						"customClass": "",
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"template": "<div>\n  <el-button type=\"primary\" link @click=\"triggerEvent('edit')\">Edit</el-button>\n  \n  <el-button type=\"danger\" link @click=\"triggerEvent('delete')\">Delete</el-button>\n</div>",
						"required": false,
						"pattern": "",
						"validator": "",
						"width": "150px",
						"remoteFunc": "func_4fyxuiz7",
						"remoteOption": "option_4fyxuiz7",
						"tableColumn": true,
						"fixedColumn": true,
						"fixedColumnPosition": "right"
					},
					"events": {
						"onChange": ""
					},
					"name": "Operations",
					"novalid": {},
					"key": "4fyxuiz7",
					"model": "component_4fyxuiz7",
					"rules": []
				}
			],
			"name": "Sub-Form",
			"key": "9wmtwlft",
			"model": "list",
			"rules": []
		},
		{
			"type": "pagination",
			"icon": "icon-pagination",
			"options": {
				"defaultValue": 1,
				"customClass": "",
				"disabled": false,
				"labelWidth": 100,
				"isLabelWidth": false,
				"hideLabel": true,
				"hidden": false,
				"dataBind": true,
				"background": true,
				"pageSize": 10,
				"pagerCount": 7,
				"total": 0,
				"remoteFunc": "func_9totvb6b",
				"remoteOption": "option_9totvb6b",
				"tableColumn": false
			},
			"events": {
				"onChange": "3td35yzi"
			},
			"name": "Pagination",
			"key": "9totvb6b",
			"model": "currentPage",
			"rules": []
		}
	],
	"config": {
		"labelWidth": 100,
		"labelPosition": "right",
		"size": "default",
		"customClass": "",
		"ui": "element",
		"layout": "horizontal",
		"width": "100%",
		"hideLabel": false,
		"hideErrorMessage": false,
		"eventScript": [
			{
				"key": "mounted",
				"name": "mounted",
				"func": ""
			},
			{
				"key": "refresh",
				"name": "refresh",
				"func": ""
			},
			{
				"key": "z9buqwp4",
				"name": "loadDataList",
				"func": "const {currentPage} = arguments[0]\n\n// 模拟数据\nlet listdata = Array.from({length: 10}).map((_, index) => {\n  let row = (currentPage - 1) * 10 + index + 1\n  \n  return {\n    id: row,\n    name: 'Tom' + row,\n    state: 'California' + row,\n    city: 'Los Angeles',\n    address: 'No. 189, Grove St, Los Angeles',\n    zip: 'CA 90036'\n  }\n})\n\n// 给列表赋值\nthis.setData({\n  list: listdata\n})\n\n// 修改分页组件 total\nthis.setOptions(['currentPage'], {\n  total: 100\n})"
			},
			{
				"key": "9urxn55l",
				"name": "onClick_9urxn55l",
				"func": "const currentPage = 1\n\nthis.setData({\n  currentPage: 1\n})\n\nthis.triggerEvent('loadDataList', {\n  currentPage\n})"
			},
			{
				"key": "3td35yzi",
				"name": "onChange_3td35yzi",
				"func": "const {value} = arguments[0]\n\nthis.triggerEvent('loadDataList', {\n  currentPage: value\n})"
			},
			{
				"key": "trfw3pj8",
				"name": "edit",
				"func": "const {rowIndex} = arguments[0]\n\nconst list = this.getValue('list')\n\nthis.$message('您点击了编辑操作：ID='+list[rowIndex].id )"
			},
			{
				"key": "23slyh16",
				"name": "delete",
				"func": "const {rowIndex} = arguments[0]\n\nconst list = this.getValue('list')\n\nthis.$message.error('您点击了删除操作：ID='+list[rowIndex].id )"
			}
		],
		"platform": "pad",
		"dataSource": []
	}
}