export default {
	"list": [
		{
			"type": "inline",
			"icon": "icon-inlineview",
			"options": {
				"customClass": "",
				"hidden": false,
				"spaceSize": 0,
				"remoteFunc": "func_pxpb5htu",
				"remoteOption": "option_pxpb5htu",
				"tableColumn": false
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
						"disabled": true,
						"labelWidth": 100,
						"isLabelWidth": false,
						"hidden": false,
						"dataBind": true,
						"showPassword": false,
						"clearable": false,
						"remoteFunc": "func_us2fkmk9",
						"remoteOption": "option_us2fkmk9",
						"subform": false
					},
					"events": {
						"onChange": "",
						"onFocus": "",
						"onBlur": ""
					},
					"name": "用户选择",
					"key": "us2fkmk9",
					"model": "user",
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
						"buttonType": "",
						"buttonPlain": false,
						"buttonRound": false,
						"buttonCircle": false,
						"buttonName": "选择",
						"width": "",
						"remoteFunc": "func_kuwqw3gv",
						"remoteOption": "option_kuwqw3gv",
						"subform": false
					},
					"events": {
						"onClick": "kgwmsntc"
					},
					"name": "Button",
					"key": "kuwqw3gv",
					"model": "button_kuwqw3gv",
					"rules": []
				}
			],
			"name": "Inline",
			"key": "pxpb5htu",
			"model": "inline_pxpb5htu",
			"rules": []
		},
		{
			"type": "dialog",
			"icon": "icon-Dialog",
			"options": {
				"defaultValue": {},
				"visible": false,
				"customClass": "",
				"title": "用户选择",
				"width": "1000px",
				"top": "10vh",
				"center": false,
				"cancelText": "Cancel",
				"showClose": true,
				"okText": "Confirm",
				"showCancel": false,
				"showOk": false,
				"confirmLoading": false,
				"dataBind": false,
				"remoteFunc": "func_3zn3pq87",
				"remoteOption": "option_3zn3pq87",
				"tableColumn": false
			},
			"list": [
				{
					"type": "inline",
					"icon": "icon-inlineview",
					"options": {
						"customClass": "",
						"hidden": false,
						"spaceSize": 20,
						"remoteFunc": "func_aej0q1ei",
						"remoteOption": "option_aej0q1ei",
						"tableColumn": false,
						"subform": true
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
								"labelWidth": 40,
								"isLabelWidth": true,
								"hidden": false,
								"dataBind": true,
								"showPassword": false,
								"clearable": false,
								"remoteFunc": "func_bashcrkr",
								"remoteOption": "option_bashcrkr",
								"subform": false
							},
							"events": {
								"onChange": "",
								"onFocus": "",
								"onBlur": ""
							},
							"name": "账号",
							"key": "bashcrkr",
							"model": "name",
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
								"buttonName": "查询",
								"width": "",
								"remoteFunc": "func_46j5xrvv",
								"remoteOption": "option_46j5xrvv",
								"subform": false
							},
							"events": {
								"onClick": "3kbo2wmb"
							},
							"name": "Button",
							"key": "46j5xrvv",
							"model": "button_46j5xrvv",
							"rules": []
						}
					],
					"name": "Inline",
					"key": "aej0q1ei",
					"model": "inline_aej0q1ei",
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
						"remoteFunc": "func_w8rl4u1u",
						"remoteOption": "option_w8rl4u1u",
						"tableColumn": false,
						"subform": true,
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
								"width": "",
								"remoteFunc": "func_pg08nf6n",
								"remoteOption": "option_pg08nf6n",
								"tableColumn": true
							},
							"events": {
								"onChange": ""
							},
							"name": "ID",
							"novalid": {},
							"key": "pg08nf6n",
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
								"width": "",
								"remoteFunc": "func_pg08nf6n",
								"remoteOption": "option_pg08nf6n",
								"tableColumn": true
							},
							"events": {
								"onChange": ""
							},
							"name": "账号",
							"novalid": {},
							"key": "bqzsgdpu",
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
								"width": "",
								"remoteFunc": "func_pg08nf6n",
								"remoteOption": "option_pg08nf6n",
								"tableColumn": true
							},
							"events": {
								"onChange": ""
							},
							"name": "邮箱",
							"novalid": {},
							"key": "htpi04id",
							"model": "email",
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
								"width": "",
								"remoteFunc": "func_pg08nf6n",
								"remoteOption": "option_pg08nf6n",
								"tableColumn": true
							},
							"events": {
								"onChange": ""
							},
							"name": "电话",
							"novalid": {},
							"key": "hrcxm2qf",
							"model": "phone",
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
								"width": "",
								"remoteFunc": "func_pg08nf6n",
								"remoteOption": "option_pg08nf6n",
								"tableColumn": true
							},
							"events": {
								"onChange": ""
							},
							"name": "角色",
							"novalid": {},
							"key": "qe2xxku6",
							"model": "role",
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
								"template": "<div>\n  <el-button size=\"small\" type=\"primary\" @click=\"triggerEvent('select')\">选择</el-button>\n</div>",
								"required": false,
								"pattern": "",
								"validator": "",
								"width": "",
								"remoteFunc": "func_2he43rs9",
								"remoteOption": "option_2he43rs9",
								"tableColumn": true
							},
							"events": {
								"onChange": ""
							},
							"name": "",
							"novalid": {},
							"key": "2he43rs9",
							"model": "component_2he43rs9",
							"rules": []
						}
					],
					"name": "Sub-Form",
					"key": "w8rl4u1u",
					"model": "userList",
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
						"remoteFunc": "func_5acp377j",
						"remoteOption": "option_5acp377j",
						"tableColumn": false,
						"subform": true
					},
					"events": {
						"onChange": "mdhthplo"
					},
					"name": "Pagination",
					"key": "5acp377j",
					"model": "currentPage",
					"rules": []
				}
			],
			"events": {
				"onCancel": "",
				"onConfirm": ""
			},
			"name": "Dialog",
			"key": "3zn3pq87",
			"model": "userDialog",
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
				"func": "this.triggerEvent('loadUserList', {\n  currentPage: 1\n})"
			},
			{
				"key": "kgwmsntc",
				"name": "onFocus_kgwmsntc",
				"func": "const userDialog = this.getComponent('userDialog')\n\nuserDialog.open()"
			},
			{
				"key": "qxn4ru2f",
				"name": "loadUserList",
				"func": "const {currentPage, query} = arguments[0]\n\nlet listData = localStorage.getItem('fmUserList') ?\n  JSON.parse(localStorage.getItem('fmUserList')) : []\n  \nif (query) {\n  listData = listData.filter(item => \n    item.name.includes(query.name || ''))\n}\n  \nthis.setData({\n  userDialog: {\n    name: query && query.name,\n    userList: listData.slice((currentPage - 1) * 10, (currentPage - 1) * 10 + 10),\n    currentPage\n  }\n})\n\nthis.getComponent('userDialog').setOptions(['currentPage'], {\n  total: listData.length\n})"
			},
			{
				"key": "3kbo2wmb",
				"name": "onClick_3kbo2wmb",
				"func": "const userDialogValues = this.getValue('userDialog')\n\nconsole.log(userDialogValues)\n\nthis.triggerEvent('loadUserList', {\n  currentPage: 1,\n  query: {\n    name: userDialogValues.name\n  }\n})"
			},
			{
				"key": "uc0aw1f8",
				"name": "select",
				"func": "const {rowIndex} = arguments[0]\n\nconst userDialogValue = this.getValue('userDialog')\n\nconst selectUser = userDialogValue.userList[rowIndex]\n\nthis.setData({\n  user: selectUser.name\n})\n\nthis.getComponent('userDialog').close()"
			},
			{
				"key": "mdhthplo",
				"name": "onChange_mdhthplo",
				"func": "const {value} = arguments[0]\n\nthis.triggerEvent('loadUserList', {\n  currentPage: value\n})"
			}
		]
	}
}