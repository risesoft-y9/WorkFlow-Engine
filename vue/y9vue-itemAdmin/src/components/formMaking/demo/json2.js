export default 
{
	"list": [
		{
			"type": "text",
			"icon": "icon-wenzishezhi-",
			"options": {
				"defaultValue": "报销单",
				"customClass": "formheader",
				"labelWidth": 100,
				"isLabelWidth": false,
				"hidden": false,
				"dataBind": true,
				"remoteFunc": "func_1622622703373",
				"remoteOption": "option_1622622703373",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": ""
			},
			"name": "",
			"key": "1622622703373",
			"model": "biaodantouName",
			"rules": []
		},
		{
			"type": "input",
			"icon": "icon-input",
			"options": {
				"width": "100%",
				"defaultValue": "",
				"required": false,
				"requiredMessage": "",
				"dataType": "",
				"dataTypeCheck": false,
				"dataTypeMessage": "",
				"pattern": "",
				"patternCheck": false,
				"patternMessage": "",
				"placeholder": "",
				"customClass": "",
				"readonly": false,
				"disabled": false,
				"labelWidth": 100,
				"isLabelWidth": false,
				"hidden": true,
				"dataBind": true,
				"showPassword": false,
				"remoteFunc": "func_1623233157014",
				"remoteOption": "option_1623233157014",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": ""
			},
			"name": "单行文本",
			"key": "1623233157014",
			"model": "guid",
			"rules": [],
			"bindTable": "y9_form_baoxiao"
		},
		{
			"type": "input",
			"icon": "icon-input",
			"options": {
				"width": "100%",
				"defaultValue": "",
				"required": false,
				"requiredMessage": "",
				"dataType": "",
				"dataTypeCheck": false,
				"dataTypeMessage": "",
				"pattern": "",
				"patternCheck": false,
				"patternMessage": "",
				"placeholder": "",
				"customClass": "",
				"readonly": false,
				"disabled": false,
				"labelWidth": 100,
				"isLabelWidth": false,
				"hidden": true,
				"dataBind": true,
				"showPassword": false,
				"remoteFunc": "func_1623233159197",
				"remoteOption": "option_1623233159197",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": ""
			},
			"name": "单行文本",
			"key": "1623233159197",
			"model": "processInstanceId",
			"rules": [],
			"bindTable": "y9_form_baoxiao"
		},
		{
			"type": "report",
			"icon": "icon-table1",
			"options": {
				"customClass": "table",
				"hidden": false,
				"borderWidth": 2,
				"borderColor": "#777777",
				"width": "690px",
				"remoteFunc": "func_1622518382735",
				"remoteOption": "option_1622518382735",
				"tableColumn": false
			},
			"rows": [
				{
					"columns": [
						{
							"type": "td",
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "80px",
								"height": ""
							},
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">文件编号</div>",
										"required": false,
										"remoteFunc": "func_1622518399213",
										"remoteOption": "option_1622518399213",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "文件编号",
									"key": "1622518399213",
									"model": "",
									"rules": []
								}
							],
							"key": "t3op0byh",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "$_number",
										"fieldPermission": "",
										"readonly": true,
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
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_iy0jellb",
										"remoteOption": "option_iy0jellb",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": "",
										"onFocus": "",
										"onBlur": ""
									},
									"name": "单行文本",
									"key": "iy0jellb",
									"model": "number",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 3,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0,
								"invisible": false
							},
							"key": "rhnhg09l",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "gll2o2rj",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "80px",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "uwu2k3ja",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">创建日期</div>",
										"required": false,
										"remoteFunc": "func_1622518545123",
										"remoteOption": "option_1622518545123",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "创建日期",
									"key": "1622518545123",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "80px",
								"height": ""
							},
							"key": "pv8s5nee",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "$_createDate",
										"required": false,
										"requiredMessage": "",
										"dataType": "",
										"dataTypeCheck": false,
										"dataTypeMessage": "",
										"pattern": "",
										"patternCheck": false,
										"patternMessage": "",
										"placeholder": "",
										"customClass": "",
										"readonly": true,
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1623232898734",
										"remoteOption": "option_1623232898734",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623232898734",
									"model": "riqi",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 3,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0,
								"invisible": false
							},
							"key": "x1420xkf",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 2,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "210px",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "6mqjsfvc",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "wiqoy1go",
							"rules": []
						}
					]
				},
				{
					"columns": [
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">发起人</div>",
										"required": false,
										"remoteFunc": "func_1622518591113",
										"remoteOption": "option_1622518591113",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "发起人",
									"key": "1622518591113",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "vxrmj5zb",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "$_userName",
										"required": false,
										"requiredMessage": "",
										"dataType": "",
										"dataTypeCheck": false,
										"dataTypeMessage": "",
										"pattern": "",
										"patternCheck": false,
										"patternMessage": "",
										"placeholder": "",
										"customClass": "",
										"readonly": true,
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1623232912780",
										"remoteOption": "option_1623232912780",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623232912780",
									"model": "baoxiaoren",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 3,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "cqwjdr37",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "nizqs2un"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "z284aldx"
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">部门名称</div>",
										"required": false,
										"remoteFunc": "func_1622518601867",
										"remoteOption": "option_1622518601867",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "部门名称",
									"key": "1622518601867",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "lzbmolxr",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "$_deptName",
										"required": false,
										"requiredMessage": "",
										"dataType": "",
										"dataTypeCheck": false,
										"dataTypeMessage": "",
										"pattern": "",
										"patternCheck": false,
										"patternMessage": "",
										"placeholder": "",
										"customClass": "",
										"readonly": true,
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1623232929960",
										"remoteOption": "option_1623232929960",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623232929960",
									"model": "bumenmingcheng",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 3,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "79c002x5",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "qlafytm9"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "cllve2uu"
						}
					]
				},
				{
					"columns": [
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">文件标题</div>",
										"required": false,
										"remoteFunc": "func_1622518616919",
										"remoteOption": "option_1622518616919",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "文件标题",
									"key": "1622518616919",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "gkwmyweu",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "",
										"required": true,
										"requiredMessage": "文件标题不能为空",
										"dataType": "",
										"dataTypeCheck": false,
										"dataTypeMessage": "",
										"pattern": "",
										"patternCheck": false,
										"patternMessage": "",
										"placeholder": "",
										"customClass": "",
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1622601247343",
										"remoteOption": "option_1622601247343",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1622601247343",
									"model": "title",
									"rules": [
										{
											"required": true,
											"message": "文件标题不能为空"
										}
									],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 7,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "3au18nns",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "af7vu9uk"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "ycovh2ma"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 3,
								"markRow": 0
							},
							"key": "5h4aq9io"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 4,
								"markRow": 0
							},
							"key": "v9o626w8"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 5,
								"markRow": 0
							},
							"key": "cedoyzx9"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 6,
								"markRow": 0
							},
							"key": "d4eedh85"
						}
					]
				},
				{
					"columns": [
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">类别</div>",
										"required": false,
										"remoteFunc": "func_1622518648332",
										"remoteOption": "option_1622518648332",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "类别",
									"key": "1622518648332",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "whkzk38r",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "radio",
									"icon": "icon-radio-active",
									"options": {
										"inline": true,
										"defaultValue": "",
										"fieldPermission": "",
										"showLabel": false,
										"options": [
											{
												"value": "报销",
												"label": "Option 1"
											},
											{
												"value": "核销",
												"label": "Option 2"
											},
											{
												"value": "借款",
												"label": "Option 3"
											},
											{
												"value": "其他"
											}
										],
										"required": true,
										"requiredMessage": "请选择类别",
										"validatorCheck": false,
										"validator": "",
										"width": "",
										"remote": false,
										"remoteType": "func",
										"remoteOption": "option_jqotzq4c",
										"remoteOptions": [],
										"props": {
											"value": "value",
											"label": "label"
										},
										"remoteFunc": "getOptionData",
										"customClass": "",
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"disabled": false,
										"tableColumn": false,
										"hideLabel": true,
										"optionData": "报销类别(baoxiaoleibie)"
									},
									"events": {
										"onChange": "qvxd9eeq"
									},
									"name": "单选框组",
									"key": "jqotzq4c",
									"model": "baoxiaoleibie",
									"rules": [
										{
											"required": true,
											"message": "请选择类别"
										}
									],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 7,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0,
								"invisible": false
							},
							"key": "1hd509k8",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 1,
								"markRow": 0,
								"invisible": true
							},
							"key": "jwbyu94j"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "5t3xcppl"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 3,
								"markRow": 0
							},
							"key": "rdsngiji"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 4,
								"markRow": 0
							},
							"key": "1yxhx9n8"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 5,
								"markRow": 0
							},
							"key": "7yrs0g73"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 6,
								"markRow": 0
							},
							"key": "swr23dke"
						}
					]
				},
				{
					"columns": [
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">单据张数</div>",
										"required": false,
										"remoteFunc": "func_1622518462385",
										"remoteOption": "option_1622518462385",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单据张数",
									"key": "1622518462385",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "9qqbzxnp",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "",
										"defaultValue": "",
										"fieldPermission": "",
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
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_34h8l387",
										"remoteOption": "option_34h8l387",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": "",
										"onFocus": "",
										"onBlur": ""
									},
									"name": "单行文本",
									"key": "34h8l387",
									"model": "danjuzhangshu",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 2,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": false,
								"markCol": 0,
								"markRow": 0
							},
							"key": "debvsswi",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "100px",
								"height": "",
								"markCol": 1,
								"markRow": 0,
								"invisible": true
							},
							"key": "p1yc1nuu",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">总额</div>",
										"required": false,
										"remoteFunc": "func_1622622485204",
										"remoteOption": "option_1622622485204",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "总额",
									"key": "1622622485204",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": false,
								"markCol": 0,
								"markRow": 0
							},
							"key": "w5epaubl",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "",
										"required": true,
										"requiredMessage": "总额不能为空",
										"dataType": "number",
										"dataTypeCheck": false,
										"dataTypeMessage": "总额必须为数值",
										"pattern": "",
										"patternCheck": false,
										"patternMessage": "",
										"placeholder": "",
										"customClass": "",
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1622518488297",
										"remoteOption": "option_1622518488297",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": "g92xosh3"
									},
									"name": "单行文本",
									"key": "1622518488297",
									"model": "baoxiaozonge",
									"rules": [
										{
											"required": true,
											"message": "总额不能为空"
										}
									],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "xybg4l94",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">大写金额</div>",
										"required": false,
										"remoteFunc": "func_1622518495144",
										"remoteOption": "option_1622518495144",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "大写金额",
									"key": "1622518495144",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "daxiejine",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "kjr0eaz7",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "input",
									"icon": "icon-input",
									"options": {
										"width": "100%",
										"defaultValue": "",
										"fieldPermission": "",
										"required": false,
										"requiredMessage": "",
										"dataType": "",
										"dataTypeCheck": false,
										"dataTypeMessage": "",
										"pattern": "",
										"patternCheck": false,
										"patternMessage": "",
										"placeholder": "",
										"customClass": "",
										"readonly": false,
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1626320193452",
										"remoteOption": "option_1626320193452",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1626320193452",
									"model": "daxiejine",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 2,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "ewgt6p2c",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "7dbkzlje"
						}
					]
				},
				{
					"columns": [
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">摘要</div>",
										"required": false,
										"remoteFunc": "func_1622518747630",
										"remoteOption": "option_1622518747630",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "摘要",
									"key": "1622518747630",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "w4gwxmjc",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "textarea",
									"icon": "icon-diy-com-textarea",
									"options": {
										"width": "100%",
										"defaultValue": "",
										"fieldPermission": "",
										"readonly": false,
										"autoSize": false,
										"required": false,
										"requiredMessage": "",
										"disabled": false,
										"pattern": "",
										"patternMessage": "",
										"validatorCheck": false,
										"validator": "",
										"placeholder": "",
										"customClass": "",
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"clearable": true,
										"maxlength": "500",
										"showWordLimit": false,
										"rows": 7,
										"autosize": true,
										"customProps": {},
										"remoteFunc": "func_3dk8th5a",
										"remoteOption": "option_3dk8th5a",
										"tableColumn": false,
										"hideLabel": true,
										"subform": false
									},
									"events": {
										"onChange": "",
										"onFocus": "",
										"onBlur": ""
									},
									"name": "多行文本",
									"key": "3dk8th5a",
									"model": "baoxiaozhaiyao",
									"rules": [],
									"bindTable": "y9_form_baoxiao"
								}
							],
							"options": {
								"customClass": "",
								"colspan": 7,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "m02pov8b",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "j6l0143b"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "tz7ubgjm"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 3,
								"markRow": 0
							},
							"key": "yn1q0gxb"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 4,
								"markRow": 0
							},
							"key": "xxgfdlr3",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 5,
								"markRow": 0
							},
							"key": "tdf8j37g"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 6,
								"markRow": 0
							},
							"key": "59z91c33"
						}
					]
				},
				{
					"columns": [
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "labelname",
										"labelWidth": 80,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">意见指示</div>",
										"required": false,
										"remoteFunc": "func_1622602442926",
										"remoteOption": "option_1622602442926",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "意见指示",
									"key": "1622602442926",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "3qfxn08o",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"name": "个人意见",
									"el": "custom-opinion",
									"options": {
										"defaultValue": {},
										"customClass": "",
										"labelWidth": 0,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"required": false,
										"pattern": "",
										"remoteFunc": "func_1623232596850",
										"remoteOption": "option_1623232596850",
										"tableColumn": false,
										"hideLabel": true
									},
									"type": "custom",
									"icon": "icon-extend",
									"key": "1623232596850",
									"model": "custom_opinion@personalComment",
									"rules": []
								}
							],
							"options": {
								"customClass": "",
								"colspan": 7,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "vyp91xlq",
							"rules": []
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 1,
								"markRow": 0
							},
							"key": "pmuquuh6"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "iq80xk5l"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 3,
								"markRow": 0
							},
							"key": "x8neojtr"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 4,
								"markRow": 0
							},
							"key": "me4c2fth"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 5,
								"markRow": 0
							},
							"key": "h3s6cgvv"
						},
						{
							"type": "td",
							"list": [],
							"options": {
								"customClass": "",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 6,
								"markRow": 0
							},
							"key": "7kxoht2r"
						}
					]
				}
			],
			"name": "表格布局",
			"key": "1622518382735",
			"model": "report_1622518382735",
			"rules": []
		},
		{
			"name": "附件列表",
			"el": "custom-file",
			"icon": "el-icon-document",
			"options": {
				"defaultValue": {},
				"customClass": "",
				"labelWidth": 0,
				"isLabelWidth": false,
				"hidden": false,
				"dataBind": true,
				"required": false,
				"pattern": "",
				"remoteFunc": "func_vv8n4tbi",
				"remoteOption": "option_vv8n4tbi",
				"tableColumn": false,
				"hideLabel": true
			},
			"type": "custom",
			"key": "vv8n4tbi",
			"model": "custom_file",
			"rules": []
		}
	],
	"config": {
		"ui": "element",
		"size": "small",
		"width": "750px",
		"layout": "horizontal",
		"labelCol": 3,
		"hideLabel": false,
		"labelWidth": 100,
		"customClass": "form",
		"labelPosition": "right",
		"hideErrorMessage": false,
		"styleSheets": ".form{\n  background-color:#fff;\n  margin: auto;\n  box-shadow: 0px 2px 14px 0px rgba(0,0,0,0.15);\n  padding-bottom: 30px;\n}\n.formheader .el-form-item__content{\n  text-align: center;\n  font-size: 38px;\n  margin: 50px 0 30px 0;\n  letter-spacing: 5px;\n} \n\n.form .el-form-item__content{\n  line-height: 40px;\n}\n\n.fm-report-table__table .fm-report-table__td{\n  padding:5px;\n}\n\n.labelname .el-form-item__content{\n  text-align: center;\n  padding:0;\n  color:#25992e;\n  font-weight: bold;\n  width:100%;\n}\n\n.table{\n  margin: auto;\n}\n.form .el-date-editor.el-input, .el-date-editor.el-input__inner{\n  width:100%;\n  height: 35px;\n  line-height: 35px;\n}\n.form .el-row .el-row--flex{\n  line-height: 40px;\n  height: 40px;\n}\n.form .el-textarea__inner{\n  resize: none;\n  border: 0px solid #DCDFE6;\n  padding:0px 7px;\n  color: #000;\n  box-shadow: none;\n  min-height: 40px !important;\n}\n.form .el-input__inner{\n  border: 0px solid #DCDFE6;\n  padding:0;\n  color: #000;\n  box-shadow: none;\n}\n.form .el-input--small .el-input__inner {\n  height: 35px;\n  line-height: 35px;\n}\n.form .el-input__wrapper{\n  border:none;\n  box-shadow: none !important;\n  background-color: #fff;\n}\n.form .el-input.is-disabled .el-input__wrapper{\n  background-color: #fff !important;\n}\n.form .el-radio__inner::after {\n    background-color: #1890ff;\n   width: 6px;\n    height: 6px;\n}\n.form .el-radio__input.is-checked .el-radio__inner {\n    background: #fff;\n}\n.form .el-input.is-disabled .el-input__inner {\n  background-color:#fff;\n  color: #000;\n   -webkit-text-fill-color: #000;\n}\n\n.form .el-textarea.is-disabled .el-textarea__inner{\n   background-color:#fff;\n   color: #000;\n}\n.form .el-radio__input.is-disabled + span.el-radio__label{\n  color: #000;\n}\n.form .el-radio.el-radio--small .el-radio__inner{\n  width: 15px;\n  height: 15px;\n}\n\n.form .el-form-item--small .el-checkbox{\n  line-height: 40px !important;\n}\n\n.form .el-radio__input.is-disabled .el-radio__inner, .el-radio__input.is-disabled.is-checked .el-radio__inner {\n    background-color: rgb(245, 247, 250);\n    border-color: #999;\n}\n\n.form .el-radio__input.is-disabled.is-checked .el-radio__inner::after {\n    background-color: #999;\n}\n\n.form .el-divider--horizontal {\n    display: block;\n    height: 1px;\n    width: 690px;\n    margin: 30px auto 20px;\n}\n\n.form .el-divider__text{\n    font-weight: 500;\n    font-size: 20px;\n    line-height: 20px;\n}\n.form .el-form-item--small.el-form-item{\n  margin-bottom: 0px;\n}\n\n.form .el-form-item--small .el-form-item__error {\n  padding-top: 0px;\n  margin-top: -8px;\n}\n\n",
		"dataSource": [
			{
				"key": "m00z3fyk",
				"name": "初始化数据",
				"url": "https://vue.youshengyun.com:8443/flowableUI/vue/y9form/getInitData",
				"method": "GET",
				"auto": false,
				"params": {},
				"headers": {},
				"responseFunc": "let initdata = res.data;\nlet data = {};\ndata.number = initdata.number;\ndata.bumenmingcheng = initdata.deptName;\ndata.baoxiaoren = initdata.userName;\ndata.riqi = initdata.createDate;\nthis.setData(data);\nreturn res;",
				"requestFunc": "return config;"
			}
		],
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
				"key": "qvxd9eeq",
				"name": "onChangeleibie",
				"func": "let baoxiaoleibie = this.getValue('baoxiaoleibie');\nif(baoxiaoleibie == \"其他\"){\n  baoxiaoleibie = \"报销单\";\n}else{\n  baoxiaoleibie = baoxiaoleibie + \"单\";\n}\nthis.setData({\"biaodantouName\": baoxiaoleibie});"
			},
			{
				"key": "g92xosh3",
				"name": "onChangeZonge",
				"func": "let Num = this.getValue('baoxiaozonge');\n\nfor(i=Num.length-1;i>=0;i--){\n    Num = Num.replace(\",\",\"\")//替换tomoney()中的“,”\n    Num = Num.replace(\" \",\"\")//替换tomoney()中的空格\n}\nNum = Num.replace(\"￥\",\"\")//替换掉可能出现的￥字符\nif(isNaN(Num)) { //验证输入的字符是否为数字\n  alert(\"请检查小写金额是否正确\");\n  return;\n}\n//字符处理完毕后开始转换，采用前后两部分分别转换\nlet part = String(Num).split(\".\");\nlet newchar = \"\"; \n//小数点前进行转化\nfor(i=part[0].length-1;i>=0;i--) {\n  if(part[0].length > 10){\n\t   alert(\"位数过大，无法计算\");\n\t   return \"\";\n  }//若数量超过拾亿单位，提示\n  tmpnewchar = \"\"\n  perchar = part[0].charAt(i);\n  switch(perchar) {\n\t   case \"0\": tmpnewchar=\"零\" + tmpnewchar ;break;\n\t   case \"1\": tmpnewchar=\"壹\" + tmpnewchar ;break;\n\t   case \"2\": tmpnewchar=\"贰\" + tmpnewchar ;break;\n\t   case \"3\": tmpnewchar=\"叁\" + tmpnewchar ;break;\n\t   case \"4\": tmpnewchar=\"肆\" + tmpnewchar ;break;\n\t   case \"5\": tmpnewchar=\"伍\" + tmpnewchar ;break;\n\t   case \"6\": tmpnewchar=\"陆\" + tmpnewchar ;break;\n\t   case \"7\": tmpnewchar=\"柒\" + tmpnewchar ;break;\n\t   case \"8\": tmpnewchar=\"捌\" + tmpnewchar ;break;\n\t   case \"9\": tmpnewchar=\"玖\" + tmpnewchar ;break;\n  }\n  switch(part[0].length-i-1) {\n\t   case 0: tmpnewchar = tmpnewchar +\"元\" ;break;\n\t   case 1: if(perchar!=0)tmpnewchar= tmpnewchar +\"拾\" ;break;\n\t   case 2: if(perchar!=0)tmpnewchar= tmpnewchar +\"佰\" ;break;\n\t   case 3: if(perchar!=0)tmpnewchar= tmpnewchar +\"仟\" ;break;\n\t   case 4: tmpnewchar= tmpnewchar +\"万\" ;break;\n\t   case 5: if(perchar!=0)tmpnewchar= tmpnewchar +\"拾\" ;break;\n\t   case 6: if(perchar!=0)tmpnewchar= tmpnewchar +\"佰\" ;break;\n\t   case 7: if(perchar!=0)tmpnewchar= tmpnewchar +\"仟\" ;break;\n\t   case 8: tmpnewchar= tmpnewchar +\"亿\" ;break;\n\t   case 9: tmpnewchar= tmpnewchar +\"拾\" ;break;\n  }\n  newchar = tmpnewchar + newchar;\n}\n//小数点之后进行转化\nif(Num.indexOf(\".\")!=-1){\n  if(part[1].length > 2)  {\n  // alert(\"小数点之后只能保留两位,系统将自动截断\");\n   part[1] = part[1].substr(0,2)\n  }\n  for(i=0;i<part[1].length;i++){\n\t   tmpnewchar = \"\"\n\t   perchar = part[1].charAt(i)\n\t   switch(perchar) {\n\t\t    case \"0\": tmpnewchar=\"零\" + tmpnewchar ;break;\n\t\t    case \"1\": tmpnewchar=\"壹\" + tmpnewchar ;break;\n\t\t    case \"2\": tmpnewchar=\"贰\" + tmpnewchar ;break;\n\t\t    case \"3\": tmpnewchar=\"叁\" + tmpnewchar ;break;\n\t\t    case \"4\": tmpnewchar=\"肆\" + tmpnewchar ;break;\n\t\t    case \"5\": tmpnewchar=\"伍\" + tmpnewchar ;break;\n\t\t    case \"6\": tmpnewchar=\"陆\" + tmpnewchar ;break;\n\t\t    case \"7\": tmpnewchar=\"柒\" + tmpnewchar ;break;\n\t\t    case \"8\": tmpnewchar=\"捌\" + tmpnewchar ;break;\n\t\t    case \"9\": tmpnewchar=\"玖\" + tmpnewchar ;break;\n\t   }\n\t   if(i==0)tmpnewchar =tmpnewchar + \"角\";\n\t   if(i==1)tmpnewchar = tmpnewchar + \"分\";\n\t   newchar = newchar + tmpnewchar;\n  }\n}\n//替换所有无用汉字\nwhile(newchar.search(\"零零\") != -1)\n newchar = newchar.replace(\"零零\", \"零\");\n newchar = newchar.replace(\"零亿\", \"亿\");\n newchar = newchar.replace(\"亿万\", \"亿\");\n newchar = newchar.replace(\"零万\", \"万\");\n newchar = newchar.replace(\"零元\", \"元\");\n newchar = newchar.replace(\"零角\", \"\");\n newchar = newchar.replace(\"零分\", \"\");\nif (newchar.charAt(newchar.length-1) == \"元\" || newchar.charAt(newchar.length-1) == \"角\"){\n  newchar = newchar+\"整\";\n}\nthis.setData({\"daxiejine\": newchar});"
			}
		]
	}
}