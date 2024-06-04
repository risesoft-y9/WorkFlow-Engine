export default {
	"list": [
		{
			"type": "component",
			"icon": "icon-component",
			"options": {
				"customClass": "formheader",
				"isLabelWidth": false,
				"hidden": false,
				"dataBind": true,
				"template": "<div style=\"text-align: center;\">办件信息</div>",
				"required": false,
				"remoteFunc": "func_1622619081352",
				"remoteOption": "option_1622619081352",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": ""
			},
			"name": "办件信息",
			"key": "1622619081352",
			"model": "",
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
				"disabled": false,
				"labelWidth": 100,
				"isLabelWidth": false,
				"hidden": true,
				"dataBind": true,
				"showPassword": false,
				"remoteFunc": "func_1623031603653",
				"remoteOption": "option_1623031603653",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": ""
			},
			"name": "",
			"key": "1623031603653",
			"model": "guid",
			"rules": [],
			"bindTable": "y9_form_ziyoubanjian"
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
				"disabled": false,
				"labelWidth": 100,
				"isLabelWidth": false,
				"hidden": true,
				"dataBind": true,
				"showPassword": false,
				"remoteFunc": "func_1623031607288",
				"remoteOption": "option_1623031607288",
				"tableColumn": false,
				"hideLabel": true
			},
			"events": {
				"onChange": ""
			},
			"name": "单行文本",
			"key": "1623031607288",
			"model": "processInstanceId",
			"rules": []
		},
		{
			"type": "report",
			"icon": "icon-table1",
			"options": {
				"customClass": "table",
				"hidden": false,
				"borderWidth": 2,
				"borderColor": "#F70434",
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
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 0,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">文件编号</div>",
										"required": false,
										"remoteFunc": "func_1622619484999",
										"remoteOption": "option_1622619484999",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "",
									"key": "1622619484999",
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
							"key": "kwj6q28x",
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
										"remoteFunc": "func_1623058442021",
										"remoteOption": "option_1623058442021",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623058442021",
									"model": "number",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
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
							"key": "laz0ay9t",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">类型</div>",
										"required": false,
										"remoteFunc": "func_1622619560645",
										"remoteOption": "option_1622619560645",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "类型",
									"key": "1622619560645",
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
							"key": "erum9hdu",
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
										"defaultValue": "自由办件",
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
										"remoteFunc": "func_1623049264602",
										"remoteOption": "option_1623049264602",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623049264602",
									"model": "type",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
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
							"key": "f74fih4i",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">字号</div>",
										"required": false,
										"remoteFunc": "func_1622620205114",
										"remoteOption": "option_1622620205114",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "",
									"key": "1622620205114",
									"model": "",
									"rules": []
								}
							],
							"options": {
								"customClass": "td_12",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "80px",
								"height": ""
							},
							"key": "io2ijobw",
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
										"defaultValue": "$_zihao",
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
										"remoteFunc": "func_1623058458466",
										"remoteOption": "option_1623058458466",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623058458466",
									"model": "wordSize",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "150px",
								"height": ""
							},
							"key": "i4jrow3s",
							"rules": []
						}
					]
				},
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
								"width": "",
								"height": ""
							},
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">文件标题</div>",
										"required": false,
										"remoteFunc": "func_1622620587996",
										"remoteOption": "option_1622620587996",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "",
									"key": "1622620587996",
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
										"defaultValue": "",
										"required": true,
										"requiredMessage": "请输入文件标题。",
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
										"remoteFunc": "func_1622518410686",
										"remoteOption": "option_1622518410686",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1622518410686",
									"model": "title",
									"rules": [
										{
											"required": true,
											"message": "请输入文件标题。"
										}
									],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 5,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
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
								"width": "",
								"height": "",
								"invisible": true,
								"markCol": 2,
								"markRow": 0
							},
							"key": "pv8s5nee",
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
								"width": "",
								"height": "",
								"markCol": 3,
								"markRow": 0,
								"invisible": true
							},
							"key": "x1420xkf",
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
								"markCol": 4,
								"markRow": 0
							},
							"key": "6mqjsfvc"
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">创建部门</div>",
										"required": false,
										"remoteFunc": "func_1622620622928",
										"remoteOption": "option_1622620622928",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "创建部门",
									"key": "1622620622928",
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
										"remoteFunc": "func_1623058477924",
										"remoteOption": "option_1623058477924",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623058477924",
									"model": "department",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
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
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">创建人</div>",
										"required": false,
										"remoteFunc": "func_1622620627563",
										"remoteOption": "option_1622620627563",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "",
									"key": "1622620627563",
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
										"remoteFunc": "func_1623058505798",
										"remoteOption": "option_1623058505798",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623058505798",
									"model": "creater",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 2,
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">创建日期</div>",
										"required": false,
										"remoteFunc": "func_1622620629399",
										"remoteOption": "option_1622620629399",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "创建日期",
									"key": "1622620629399",
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
										"remoteFunc": "func_1623058529522",
										"remoteOption": "option_1623058529522",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1623058529522",
									"model": "createDate",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
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
								"markRow": 0,
								"invisible": false
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
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">联系方式</div>",
										"required": false,
										"remoteFunc": "func_1623119814666",
										"remoteOption": "option_1623119814666",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "",
									"key": "1623119814666",
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
							"key": "5h4aq9io",
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
										"defaultValue": "$_mobile",
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
										"disabled": false,
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"showPassword": false,
										"remoteFunc": "func_1622604893427",
										"remoteOption": "option_1622604893427",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1622604893427",
									"model": "contact",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
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
							"key": "v9o626w8",
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
							"key": "cedoyzx9"
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">文件概要</div>",
										"required": false,
										"remoteFunc": "func_1623119829222",
										"remoteOption": "option_1623119829222",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "文件概要",
									"key": "1623119829222",
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
							"key": "c6zapnmm",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "textarea",
									"icon": "icon-diy-com-textarea",
									"options": {
										"width": "",
										"defaultValue": "",
										"fieldPermission": "",
										"readonly": false,
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
										"clearable": false,
										"maxlength": "500",
										"showWordLimit": false,
										"rows": 5,
										"autosize": true,
										"customProps": {},
										"remoteFunc": "func_55qfuh0o",
										"remoteOption": "option_55qfuh0o",
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
									"key": "55qfuh0o",
									"model": "outline",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 5,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "b4sh7qp4",
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
							"key": "ln1rgosr"
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
							"key": "upzrwt8z"
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
							"key": "kty20t84"
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
							"key": "me37fxgi"
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">发送对象</div>",
										"required": false,
										"remoteFunc": "func_1622620634986",
										"remoteOption": "option_1622620634986",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "发送对象",
									"key": "1622620634986",
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
							"key": "8tv1zu1n"
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
										"readonly": false,
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
										"remoteFunc": "func_em58mw4k",
										"remoteOption": "option_em58mw4k",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": "",
										"onFocus": "",
										"onBlur": ""
									},
									"name": "单行文本",
									"key": "em58mw4k",
									"model": "send",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 5,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": "",
								"markCol": 0,
								"markRow": 0
							},
							"key": "vh76l5jo",
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
							"key": "vt8dw177"
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
							"key": "ujqdwxnj"
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
							"key": "b1moimfz"
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
							"key": "eekc7l0o"
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">紧急程度</div>",
										"required": false,
										"remoteFunc": "func_1622620637117",
										"remoteOption": "option_1622620637117",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "紧急程度",
									"key": "1622620637117",
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
								"width": "100px",
								"height": ""
							},
							"key": "9qqbzxnp",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "select",
									"icon": "icon-select",
									"options": {
										"defaultValue": "一般",
										"fieldPermission": "",
										"optionData": "报销类别(baoxiaoleibie)",
										"multiple": false,
										"disabled": false,
										"clearable": false,
										"placeholder": "",
										"required": false,
										"requiredMessage": "",
										"validatorCheck": false,
										"validator": "",
										"showLabel": false,
										"width": "",
										"options": [
											{
												"value": "一般"
											},
											{
												"value": "紧急"
											},
											{
												"value": "重要"
											}
										],
										"remote": false,
										"remoteType": "func",
										"remoteOption": "option_ifro72rr",
										"filterable": false,
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
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": "",
										"onFocus": "",
										"onBlur": ""
									},
									"name": "下拉选择框",
									"key": "ifro72rr",
									"model": "level",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
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
								"markRow": 1
							},
							"key": "debvsswi",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "component",
									"icon": "icon-component",
									"options": {
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">签发人</div>",
										"required": false,
										"remoteFunc": "func_1622620316591",
										"remoteOption": "option_1622620316591",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "签发人",
									"key": "1622620316591",
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
							"key": "p1yc1nuu",
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
										"required": false,
										"requiredMessage": "",
										"dataType": "number",
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
										"remoteFunc": "func_1622518488297",
										"remoteOption": "option_1622518488297",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "单行文本",
									"key": "1622518488297",
									"model": "signAndIssue",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">签发日期</div>",
										"required": false,
										"remoteFunc": "func_1622620389681",
										"remoteOption": "option_1622620389681",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "签发日期",
									"key": "1622620389681",
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
							"key": "kjr0eaz7",
							"rules": []
						},
						{
							"type": "td",
							"list": [
								{
									"type": "date",
									"icon": "icon-date",
									"options": {
										"defaultValue": "",
										"fieldPermission": "",
										"bindDatabase": true,
										"readonly": false,
										"disabled": false,
										"editable": true,
										"clearable": true,
										"placeholder": "",
										"startPlaceholder": "",
										"endPlaceholder": "",
										"type": "date",
										"format": "YYYY-MM-DD",
										"timestamp": false,
										"required": false,
										"requiredMessage": "",
										"validatorCheck": false,
										"validator": "",
										"width": "",
										"customClass": "",
										"labelWidth": 100,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"remoteFunc": "func_za06mp2r",
										"remoteOption": "option_za06mp2r",
										"tableColumn": false,
										"hideLabel": true,
										"subform": false
									},
									"events": {
										"onChange": "",
										"onFocus": "",
										"onBlur": ""
									},
									"name": "日期选择器",
									"key": "za06mp2r",
									"model": "dateOfIssue",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 1,
								"rowspan": 1,
								"align": "left",
								"valign": "top",
								"width": "",
								"height": ""
							},
							"key": "ewgt6p2c",
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">备注</div>",
										"required": false,
										"remoteFunc": "func_1622620639888",
										"remoteOption": "option_1622620639888",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "备注",
									"key": "1622620639888",
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
										"width": "",
										"defaultValue": "",
										"fieldPermission": "",
										"readonly": false,
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
										"clearable": false,
										"maxlength": "500",
										"showWordLimit": false,
										"rows": 5,
										"autosize": true,
										"customProps": {},
										"remoteFunc": "func_ee3f6vyf",
										"remoteOption": "option_ee3f6vyf",
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
									"key": "ee3f6vyf",
									"model": "remarks",
									"rules": [],
									"bindTable": "y9_form_ziyoubanjian"
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 5,
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
								"markCol": 3,
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
								"markCol": 4,
								"markRow": 0
							},
							"key": "tdf8j37g"
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
										"customClass": "label_name",
										"labelWidth": 70,
										"isLabelWidth": false,
										"hidden": false,
										"dataBind": true,
										"template": "<div style=\"text-align: center;\">意见指示</div>",
										"required": false,
										"remoteFunc": "func_1622620641650",
										"remoteOption": "option_1622620641650",
										"tableColumn": false,
										"hideLabel": true
									},
									"events": {
										"onChange": ""
									},
									"name": "意见指示",
									"key": "1622620641650",
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
										"minHeight": "",
										"pattern": "",
										"remoteFunc": "func_ndx7o69w",
										"remoteOption": "option_ndx7o69w",
										"tableColumn": false,
										"hideLabel": true,
										"subform": false
									},
									"type": "custom",
									"icon": "icon-extend",
									"key": "ndx7o69w",
									"model": "custom_opinion@personalComment",
									"rules": []
								}
							],
							"options": {
								"customClass": "td_right_border",
								"colspan": 5,
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
								"markCol": 3,
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
								"markCol": 4,
								"markRow": 0
							},
							"key": "h3s6cgvv"
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
				"remoteFunc": "func_1623396951612",
				"remoteOption": "option_1623396951612",
				"tableColumn": false,
				"hideLabel": true
			},
			"type": "custom",
			"key": "1623396951612",
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
		"styleSheets": "\n.formheader{\n  width:750px;\n  margin: auto;\n}\n.formheader .el-form-item__content{\n  text-align: center;\n  font-size: 38px;\n  margin: 50px 0 30px 0;\n  letter-spacing: 5px;\n  color:red;\n} \n\n.form .el-form-item__content{\n  line-height: 40px;\n}\n.form{\n  background-color:#fff;\n  margin: auto;\n  box-shadow: 0px 2px 14px 0px rgba(0,0,0,0.15);\n  padding-bottom: 30px;\n}\n.form .el-textarea__inner{\n  resize: none;\n  border:none;\n  box-shadow: none;\n  padding:0px 7px;\n  color: #000;\n  min-height: 40px !important;\n}\n.form .el-date-editor.el-input, .el-date-editor.el-input__inner{\n  width:100%;\n  height: 35px;\n  line-height: 35px;\n}\n.form .el-input__wrapper{\n  border:none;\n  box-shadow: none !important;\n}\n\n.form .el-input__inner{\n  padding:0;\n  width:100%;\n  color: #000;\n}\n.form .el-input--prefix .el-input__inner{\n  padding-left: 0px;\n}\n.form .el-input--suffix .el-input__inner{\n  padding-right:0;\n}\n.form .el-input--prefix .el-input__inner{\n  padding-left: 30px;\n}\n.form .el-input--small .el-input__inner {\n  height: 35px;\n  line-height: 35px;\n}\n.label_name .el-form-item__content{\n  text-align: center;\n  padding:0;\n  color:red;\n  font-weight: bold;\n  line-height: 40px;\n  width:100%;\n  margin-left: 0;\n}\n.table{\n  margin: auto;\n  border-left: 0px;\n}\n.table .td_right_border{\n  border-right: 0px;\n}\n.td_right_border{\n  border-right: 0px;\n}\n.form .el-input.is-disabled .el-input__inner {\n  background-color:#fff;\n  color: #000;\n  -webkit-text-fill-color: #000;\n}\n.form .el-select{\n  --el-select-disabled-border: none;\n}\n\n\n.form .el-input.is-disabled .el-input__wrapper {\n  background-color: #fff;\n  box-shadow: none;\n}\n\n.form .el-textarea.is-disabled .el-textarea__inner{\n   background-color:#fff;\n   color: #000;\n}\n\n\n.form .el-divider--horizontal {\n    display: block;\n    height: 1px;\n    width: 690px;\n    margin: 30px auto 20px;\n}\n.form .el-divider__text{\n    font-weight: 500;\n    font-size: 20px;\n    line-height: 20px;\n}\n.form .el-form-item--small.el-form-item{\n  margin-bottom: 0px;\n}\n.form .el-form-item--small .el-form-item__error {\n  padding-top: 0px;\n  margin-top: -8px;\n}\n",
		"dataSource": [
			{
				"key": "yow40f7g",
				"name": "初始化数据",
				"url": "https://vue.youshengyun.com:8443/flowableUI/vue/y9form/getInitData",
				"method": "GET",
				"auto": false,
				"params": {},
				"headers": {},
				"responseFunc": "let initdata = res.data;\nlet data = {};\ndata.number = initdata.number;\ndata.wordSize = initdata.zihao;\ndata.department = initdata.deptName;\ndata.creater = initdata.userName;\ndata.createDate = initdata.createDate;\ndata.contact = initdata.mobile;\nthis.setData(data);\nreturn res;",
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
			}
		],
		"initDataUrl": ""
	}
}